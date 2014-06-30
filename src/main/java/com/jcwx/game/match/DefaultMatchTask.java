package com.jcwx.game.match;

import java.util.LinkedList;
import java.util.List;

public class DefaultMatchTask implements MatchTask{
	private long lastExpandCombineRangeTime;
	private int camp=-1;
	private List<MatchPlayer> friendPlayers=new LinkedList<MatchPlayer>();//队伍中不会被分离的玩家，好友邀请的
	private List<MatchTask> externalPlayerTasks=new LinkedList<MatchTask>();//队伍中可能会被分离的玩家队伍，系统加入的
	private List<Integer> combineRangeRecords=new LinkedList<Integer>();
	private int matchCount;
	private boolean complete;
	private long matchTime=-1;
	private long startTime=System.currentTimeMillis();
	private long lastExpandRangeTime=startTime;
	private List<Integer> rangeRecords=new LinkedList<Integer>();
	private MatchPlayer createPlayer;
	private long timeout;
	private int maxUser;
	/***
	 * 单人匹配的构造方法
	 * @param matchRange 寻找对手的初始匹配范围
	 * @param createPlayer 队伍创建者
	 * */
	public DefaultMatchTask(int matchRange, MatchPlayer createPlayer){
		this(matchRange, 0, createPlayer);
	}
	/**
	 * 单人匹配的构造方法
	 * @param matchRange 寻找对手的初始匹配范围
	 * @param createPlayer 创建者
	 * @param timeout 超时时间
	 * */
	public DefaultMatchTask(int matchRange,MatchPlayer createPlayer,long timeout){
		this(matchRange, createPlayer);
		this.timeout=timeout;
	}
	/**
	 * 多人匹配的构造方法
	 * @param matchRange 寻找对手的初始匹配范围
	 * @param combineRange 寻找队友的初始匹配范围
	 * @param createPlayer 队伍创建者
	 * */
	public DefaultMatchTask(int matchRange,int combineRange,MatchPlayer createPlayer){
		this.rangeRecords.add(matchRange);
		this.createPlayer=createPlayer;
		combineRangeRecords.add(combineRange);
		this.friendPlayers.add(createPlayer);
		this.camp=createPlayer.getCamp();
		createPlayer.setMatchTask(this);
	}
	/**
	 * 分数比较，主要用于比较两个匹配队伍的分数差的绝对值是否在一定范围内
	 * 值越小，说明两个队伍实力越相近
	 * */
	@Override
	public int compareTo(MatchTask o) {
		// TODO Auto-generated method stub
		return o.getFraction()-getFraction();
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return complete;
	}
	@Override
	public int matchCount() {
		// TODO Auto-generated method stub
		return matchCount;
	}

	@Override
	public void incrementMatchCount() {
		matchCount++;
		
	}

	public void completed() {
		this.complete = true;
		this.matchTime=System.currentTimeMillis()-startTime;
	}

	public long getMatchTime() {
		return matchTime;
	}
	@Override
	public long getStartTime() {
		return startTime;
	}
	@Override
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public int getMatchRange() {
		// TODO Auto-generated method stub
		return this.rangeRecords.get(rangeRecords.size()-1);
	}

	@Override
	public void updateMatchRange(int newRange) {
		if(!rangeRecords.contains(newRange)){
			rangeRecords.add(newRange);
		}
		
	}

	public long getLastExpandRangeTime() {
		return lastExpandRangeTime;
	}

	public void setLastExpandRangeTime(long lastExpandRangeTime) {
		this.lastExpandRangeTime = lastExpandRangeTime;
	}
	@Override
	public boolean isSameCamp(Matcher matcher){
		return matcher.getCamp()==getCamp();
	}

	@Override
	public List<Integer> getMatchRangeRecords() {
		// TODO Auto-generated method stub
		return rangeRecords;
	}

	public boolean isFull(){
		return size()==maxUser;
	}
	@Override
	public int getFraction() {
		int totalFraction=0;
		List<MatchPlayer> totalPlayers=getPlayers();
		for(MatchPlayer player:totalPlayers){
			totalFraction=totalFraction+player.getFraction();
		}
		return totalFraction/totalPlayers.size();
	}
	public void addFriendPlayer(MatchPlayer p){
		if(isFull()){
			throw new IndexOutOfBoundsException("最大不能超过"+maxUser+"个玩家一组");
		}
//		if(isSameCamp(p)){
			friendPlayers.add(p);
//		}else{
//			throw new IllegalArgumentException("非同一阵营的不能组成队伍");
//		}
		
	}
	public int size(){
		int size=friendPlayers.size();
		for(MatchTask task:externalPlayerTasks){
			size=size+task.size();
		}
		return size;
	}
	public List<MatchPlayer> getPlayers(){
		List<MatchPlayer> players=new LinkedList<MatchPlayer>();
		players.addAll(friendPlayers);
		for(MatchTask task:externalPlayerTasks){
			players.addAll(task.getPlayers());
		}
		return players;
	}
	public boolean canCombine(MatchTask task,boolean campActive){
		boolean sizeOk=this.size()+task.size()<=maxUser;
		if(campActive){
			return sizeOk&&isSameCamp(task);
		}
		return sizeOk;
	}
	public void combine(MatchTask task,boolean campActive){
		if(canCombine(task,campActive)){
			externalPlayerTasks.add(task);
		}else{
			throw new IllegalArgumentException("两个MatchTask所包含的玩家总数超出限制");
		}
		
	}
	/**
	 * 分离队伍，除了队伍最开始的人之外
	 * */
	public List<MatchTask> separate(){
		List<MatchTask> result=new LinkedList<MatchTask>();
		for(MatchTask task:externalPlayerTasks){
			result.add(task);
		}
		externalPlayerTasks.clear();
		return result;
	}
	public List<MatchTask> getExternalPlayerTasks(){
		return externalPlayerTasks;
	}
	/**
	 * 组合完整队伍的范围差值
	 * */
	public int getCombineRange() {
		return combineRangeRecords.get(combineRangeRecords.size()-1);
	}
	public void updateCombineRange(int newCombineRange) {
		if(!combineRangeRecords.contains(newCombineRange)){
			combineRangeRecords.add(newCombineRange);
		}
	}
	@Override
	public boolean removePlayer(MatchPlayer player) {
		if(!friendPlayers.remove(player)){
			for(MatchTask task:externalPlayerTasks){
				if(task.removePlayer(player)){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 人数>1并且如果有设置超时时间的话，必须是未超时状态
	 * */
	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		boolean active=size()>0;
		if(!isNeverTimeout()){
			active=active&&getTimeout()>=System.currentTimeMillis();
		}
		return active;
	}
	public long getLastExpandCombineRangeTime() {
		return lastExpandCombineRangeTime;
	}
	public void setLastExpandCombineRangeTime(long lastExpandCombineRangeTime) {
		this.lastExpandCombineRangeTime = lastExpandCombineRangeTime;
	}

	@Override
	public boolean playersSizeEquals(MatchTask task) {
		return task.size()==size();
	}
	public int getCamp() {
		return camp;
	}
	public List<Integer> getCombineRangeRecords(){
		return combineRangeRecords;
	}

	@Override
	public MatchPlayer getCreatePlayer() {
		// TODO Auto-generated method stub
		return createPlayer;
	}
	@Override
	public boolean isMultiMatch() {
		// TODO Auto-generated method stub
		return maxUser>1;
	}
	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return timeout;
	}
	@Override
	public boolean isNeverTimeout() {
		// TODO Auto-generated method stub
		return timeout==0;
	}
	@Override
	public int getMaxUser(){
		return maxUser;
	}
	@Override
	public void setMaxUser(int maxUser){
		this.maxUser=maxUser;
	}


}
