package com.jcwx.game.match;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcwx.game.match.config.MatchConfig;

/**
 * 匹配处理器抽象类
 * 
 * @author Chenlong
 * */
public class MatchProcessor implements Runnable {
	private LinkedBlockingQueue<MatchTask> taskQueue = new LinkedBlockingQueue<MatchTask>();
	private LinkedBlockingQueue<MatchTask> waitAddTaskQueue = new LinkedBlockingQueue<MatchTask>();
	private MatchConfig config;
	private ScheduledExecutorService scheduledExecutor = Executors
			.newSingleThreadScheduledExecutor();
	private RangeExpander matchRangeExpander;
	private MatchProcessorNotifier notifier;
	private static Logger logger = LoggerFactory
			.getLogger(MatchProcessor.class);
	private CombineTRExpander combineRangeExpander;
	private Thread workThread = null;

	public MatchProcessor(MatchConfig config) {
		this.config = config;
		if (config.getMatchExpandConfig() != null) {
			matchRangeExpander = new MatchTRExpander(
					config.getMatchExpandConfig());
		}
		if (config.getCombineExpandConfig() != null) {
			combineRangeExpander = new CombineTRExpander(
					config.getCombineExpandConfig());
		}
	}

	public MatchConfig getMatchConfig() {
		return config;
	}

	/**
	 * 提交匹配任务 在方法中会设置匹配开始时间，在task提交进来之前必须设置好匹配范围
	 * */
	public void submitMatch(MatchTask task) {
		try {
			task.setMaxUser(config.getMaxUser());
			task.setStartTime(System.currentTimeMillis());
			waitAddTaskQueue.put(task);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 取消匹配任务，从匹配队列中移除
	 * */
	public void cancelMatch(MatchTask task) {
		waitAddTaskQueue.remove(task);
		taskQueue.remove(task);
	}

	public LinkedBlockingQueue<MatchTask> getTaskQueue() {
		return taskQueue;
	}

	public LinkedBlockingQueue<MatchTask> getWaitAddTaskQueue() {
		return waitAddTaskQueue;
	}

	/**
	 * 将待处理的匹配任务填充到taskQueue中,下次执行匹配遍历时就会计算这些任务
	 * */
	protected void fillTaskQueue() {
		if (!getWaitAddTaskQueue().isEmpty()) {
			Iterator<MatchTask> iterator = getWaitAddTaskQueue().iterator();
			while (iterator.hasNext()) {
				getTaskQueue().add(iterator.next());
				iterator.remove();
			}
		}
	}

	protected void headToTail() throws InterruptedException {
		MatchTask t = taskQueue.remove();
		taskQueue.put(t);
	}

	protected MatchTask matchTask(MatchTask task) {
		task.incrementMatchCount();
		expandMatchRange(task);
		List<Integer> matchRangeRecords = task.getMatchRangeRecords();
		boolean campActive = config.isCampActive();// 是否必须对立阵营
		for (int record : matchRangeRecords) {
			Iterator<MatchTask> iterator = getTaskQueue().iterator();
			while (iterator.hasNext()) {
				MatchTask position = iterator.next();
				if(!position.isActive()){
					iterator.remove();
					continue;
				}
				boolean baseCondition = (position != task && task
						.playersSizeEquals(position));
				if (campActive) {// 如果必须是对立阵营才能成为对手，则判断
					baseCondition = baseCondition && !task.isSameCamp(position);
				}
				if (baseCondition) {
					int cpr = position.compareTo(task);
					if (Math.abs(cpr) <= record) {// 找到合适的队伍
						task.completed();
						notifier.completed(task, position);
						getTaskQueue().remove();// 匹配成功则移除头并且移除position
						iterator.remove();
						return position;
					}
				}
			}
		}
		return null;
	}

	private void expandMatchRange(MatchTask task) {
		if (matchRangeExpander != null) {
			if (matchRangeExpander.isDifficutMatch(task)) {
				matchRangeExpander.expandMatchRange(task);
			}
		}
	}

	public void start() {
		scheduledExecutor.scheduleWithFixedDelay(this, 0,
				config.getMatchExecutePeriod(), TimeUnit.MILLISECONDS);
	}

	public void setNotifier(MatchProcessorNotifier notifier) {
		this.notifier = notifier;
	}

	protected MatchProcessorNotifier getNotifier() {
		return notifier;
	}

	@Override
	public void run() {
		try {
			this.workThread = Thread.currentThread();
			fillTaskQueue();
			MatchTask task = getTaskQueue().peek();
			if(task==null){
				return;
			}
			if(!task.isActive()){// 判断task是否失效
				getTaskQueue().remove();
				return;
			}
			if (!task.isFull()) {// 组队未满人，先寻找平均值范围内的组成完整队伍
				if (combineRangeExpander != null) {
					if (combineRangeExpander.isDifficutMatch(task)) {
						combineRangeExpander.expandMatchRange(task);
					}
				}
				Iterator<MatchTask> iterator = null;
				// 循环遍历比较匹配范围集合,从小的范围到大的范围开始比较
				List<Integer> combineRangeRecords = task.getCombineRangeRecords();
				recordsLoop:for (int record : combineRangeRecords) {//范围扩张记录遍历，由小到大取最优
					iterator = getTaskQueue().iterator();
					while (iterator.hasNext()) {//剩余所有Task遍历，用于组合完整队伍
						MatchTask position = iterator.next();
						if(!position.isActive()){//判断position是否失效
							iterator.remove();
							continue;
						}
						boolean campActive = getMatchConfig().isCampActive();//是否需要阵营判断
						if (position != task&& task.canCombine(position,campActive)) {// 判断是否能够合并两个组
							int cpr = position.compareTo(task);
							// 符合匹配要求
							if (Math.abs(cpr) <= record) {
								task.combine(position, campActive);// 合并
								iterator.remove();// 移除
							}
							if (task.isFull()) {
								break recordsLoop;
							}

						}
					}
				}
			}
			
			// 遍历所有组合仍未能组成完整队伍，则将匹配请求放入尾部
			if (!task.isFull()) {// 在此时出现线程安全问题，如玩家取消匹配
				// 一次全局遍历无法组成完整队伍，则分离队伍
				List<MatchTask> externalPlayerTasks = task
						.separate();
				for (MatchTask externalTask : externalPlayerTasks) {
					getTaskQueue().put(externalTask);
				}
				headToTail();
				return;
			}
			MatchTask result = matchTask(task);
			if (result == null) {
				// 未匹配成功，则将头移到尾部
				headToTail();
			} else {
				int remain = 0;
				for (MatchTask t : getTaskQueue()) {
					remain = remain + t.size();
				}
				logger.debug("Complete a team match and remain players "+remain);
			}

		} catch (Exception e) {
			logger.error("匹配异常", e);
		}

	}

	public Thread workThread() {
		return workThread;
	}
}
