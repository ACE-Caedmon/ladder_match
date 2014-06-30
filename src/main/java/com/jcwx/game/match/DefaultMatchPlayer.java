package com.jcwx.game.match;

public class DefaultMatchPlayer implements MatchPlayer{
	private long id;
	private int fraction;
	private int camp;
	private MatchTask task;
	/**
	 * 构造带阵营的MatchPlayer
	 * @param id PlayerID
	 * @param fraction 分数
	 * */
	public DefaultMatchPlayer(int id,int fraction){
		this.id=id;
		this.fraction=fraction;
	}
	@Override
	public int getFraction() {
		return fraction;
	}

	@Override
	public void cancelMatch() {
		task.removePlayer(this);
	}

	@Override
	public MatchTask getMatchTask() {
		// TODO Auto-generated method stub
		return task;
	}

	@Override
	public int getCamp() {
		// TODO Auto-generated method stub
		return camp;
	}

	@Override
	public void setMatchTask(MatchTask task) {
		this.task=task;
		
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
}
