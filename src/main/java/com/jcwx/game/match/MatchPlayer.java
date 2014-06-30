package com.jcwx.game.match;

public interface MatchPlayer extends Matcher{
	/**
	 * 匹配玩家的唯一标识ID
	 * */
	long getId();
	/**
	 * 设置匹配玩家的唯一标识ID
	 * */
	void setId(long id);
	/**
	 * 玩家取消匹配
	 * */
	void cancelMatch();
	/**
	 * 设置玩家匹配任务
	 * */
	void setMatchTask(MatchTask task);
	/**
	 * @return 玩家匹配任务
	 * */
	MatchTask getMatchTask();
}
