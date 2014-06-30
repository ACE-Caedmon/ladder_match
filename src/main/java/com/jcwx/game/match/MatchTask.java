package com.jcwx.game.match;

import java.util.Collection;
import java.util.List;


/**
 * 匹配任务封装接口
 * @author Chenlong
 * */
public interface MatchTask extends Matcher,Comparable<MatchTask>{
	/**
	 * 是否匹配完成
	 * */
	boolean isComplete();
	/**
	 * 全局遍历匹配次数
	 * */
	int matchCount();
	/**
	 * 递增全局遍历匹配次数
	 * */
	void incrementMatchCount();
	/**
	 * 匹配完成动作
	 * */
	void completed();
	/**
	 * 从任务进入队列到匹配完成耗时总时长，单位毫秒
	 * 如果返回负数，则代表任务还未匹配完成
	 * */
	long getMatchTime();
	/**
	 * 设置开始匹配时间
	 * */
	void setStartTime(long startTime);
	/**
	 * 获取开始匹配时间
	 * */
	long getStartTime();

	/**
	 * 移除某一个玩家
	 * @return true 包含这个玩家，并移除成功 false 不存在这个玩家
	 * */
	boolean removePlayer(MatchPlayer player);
	/**
	 * 任务是否有效,如果失效，会从匹配队列中移除
	 * */
	boolean isActive();
	/**
	 * 是否同一阵营
	 * */
	boolean isSameCamp(Matcher matcher);
	
	/**
	 * 获取当前匹配分数差值范围
	 * */
	int getMatchRange();
	
	/**
	 * 扩大当前匹配分数差值范围
	 * */
	public void updateMatchRange(int newMatchRange);
	/**
	 * 获取最后扩大匹配范围的时间，毫秒单位
	 * */
	long getLastExpandRangeTime();
	/**
	 * 设置最后扩大匹配范围的时间，毫秒单位
	 * */
	void setLastExpandRangeTime(long lastExpandRangeTime);
	/**
	 * 人数是否对等
	 * */
	boolean playersSizeEquals(MatchTask task);
	
	List<Integer> getMatchRangeRecords();
	/**
	 * @return 是否满人,达到最大人数限制
	 * */
	boolean isFull();
	/**
	 * 两个Task是否能合并成一个队伍
	 * @param position 需要判断的Task
	 * @param campActive 是否需要相同阵营才能成为队友
	 * */
	boolean canCombine(MatchTask position, boolean campActive);
	/**
	 * @return 获取寻找队友的匹配范围扩张记录
	 * */
	List<Integer> getCombineRangeRecords();
	/**
	 * 合并一个匹配队伍到自己队伍中
	 * @param position 被合并的队伍
	 * @param campActive true必须阵营相同才能合并
	 * */
	void combine(MatchTask position, boolean campActive);
	/**
	 * @return 队伍中所有人数
	 * */
	int size();
	/**
	 * 分离队伍,会将暂时分配到一起的人分离，但是不会分离一开始就一起匹配的好友
	 * */
	List<MatchTask> separate();
	/**
	 * 获取队伍中所有玩家
	 * */
	Collection<? extends MatchPlayer> getPlayers();
	/**
	 * 获取队伍的创建者，构造方法中传入的createPlayer
	 * */
	MatchPlayer getCreatePlayer();
	/**
	 * 更新合并队伍的匹配范围
	 * @param range 新的匹配范围
	 * */
	void updateCombineRange(int range);
	/**
	 * @return 合并队伍的匹配范围
	 * */
	int getCombineRange();
	/**
	 * @return 是否为多人匹配
	 * */
	boolean isMultiMatch();
	/**
	 * 获取匹配超时时间,如果为0,则是永不超时
	 * */
	long getTimeout();
	/**
	 * 是否永不超时
	 * */
	boolean isNeverTimeout();
	/**
	 * @return 多人匹配的人数
	 * */
	int getMaxUser();
	/**
	 *设置 多人匹配的人数
	 * */
	 void setMaxUser(int maxUser);
}
