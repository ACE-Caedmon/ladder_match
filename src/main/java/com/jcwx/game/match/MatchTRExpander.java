package com.jcwx.game.match;

import com.jcwx.game.match.config.TRExpandConfig;



public class MatchTRExpander implements RangeExpander{
	private TRExpandConfig config;
	public MatchTRExpander(TRExpandConfig config){
		this.config=config;
	}
	@Override
	public boolean isDifficutMatch(MatchTask task) {
		long now=System.currentTimeMillis();
		long diff=now-task.getLastExpandRangeTime();
		return diff>config.getPeriod();
	}

	@Override
	public int expandMatchRange(MatchTask task) {
		int oldMatchRange=task.getMatchRange();
		int increment=config.getIncrementValue();
		if(oldMatchRange+increment>=config.getMaxValue()){
			return config.getMaxValue();
		}
		task.updateMatchRange(oldMatchRange+increment);
		task.setLastExpandRangeTime(System.currentTimeMillis());
		return task.getMatchRange();
	}

}
