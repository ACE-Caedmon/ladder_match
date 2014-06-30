package com.jcwx.game.match;

import com.jcwx.game.match.config.TRExpandConfig;

/**
 * 合并两个MatchTask作为一个MatchTask的范围扩张处理类
 * 根据匹配时间来扩大匹配范围
 * */
public class CombineTRExpander implements RangeExpander{
	private TRExpandConfig config;
	public CombineTRExpander(TRExpandConfig config){
		this.config=config;
	}
	@Override
	public boolean isDifficutMatch(MatchTask task) {
		long now=System.currentTimeMillis();
		return now-task.getLastExpandRangeTime()>config.getPeriod();
	}

	@Override
	public int expandMatchRange(MatchTask task) {
		int oldCombineRange=task.getCombineRange();
		int increment=config.getIncrementValue();
		if(oldCombineRange+increment>=config.getMaxValue()){
			return config.getMaxValue();
		}
		task.updateCombineRange(oldCombineRange+increment);
		task.setLastExpandRangeTime(System.currentTimeMillis());
		return task.getCombineRange();
	}

}
