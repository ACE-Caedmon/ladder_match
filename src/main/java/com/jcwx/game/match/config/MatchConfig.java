package com.jcwx.game.match.config;
/**
 * 匹配配置信息
 * @author Chenlong
 * */
public class MatchConfig {
	private int maxUser;
	private boolean campActive;
	private long matchExecutePeriod;
	private TRExpandConfig matchExpandConfig;
	private TRExpandConfig combineExpandConfig;
	/**
	 * 是否考虑阵营
	 * @return true 多人匹配下必须是同一阵营才能成为队友，不同阵营的才能成为对手
	 * */
	public boolean isCampActive() {
		return campActive;
	}
	public void setCampActive(boolean campActive) {
		this.campActive = campActive;
	}
	/**
	 * @return 匹配对手的范围自增配置
	 * */
	public TRExpandConfig getMatchExpandConfig() {
		return matchExpandConfig;
	}
	public void setMatchExpandConfig(TRExpandConfig expandConfig) {
		this.matchExpandConfig = expandConfig;
	}
	/**
	 * @return 循环执行遍历匹配时间周期，单位毫秒
	 * */
	public long getMatchExecutePeriod() {
		return matchExecutePeriod;
	}
	public void setMatchExecutePeriod(long matchExecutePeriod) {
		this.matchExecutePeriod = matchExecutePeriod;
	}
	public TRExpandConfig getCombineExpandConfig() {
		return combineExpandConfig;
	}
	public void setCombineExpandConfig(TRExpandConfig combineExpandConfig) {
		this.combineExpandConfig = combineExpandConfig;
	}
	public int getMaxUser() {
		return maxUser;
	}
	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}
	
}