package com.jcwx.game.match.config;
/**
 * 根据时间自动扩大匹配范围的配置信息类
 * 时间以毫秒为单位
 * @author Chenlong
 * */
public class TRExpandConfig {
	private long period;
	private int maxValue=Integer.MAX_VALUE;
	private int incrementValue;
	/**
	 * @return 扩大匹配范围的周期，可以理解为每隔一段时间未匹配到合适人选的话就扩大匹配范围
	 * */
	public long getPeriod() {
		return period;
	}
	public void setPeriod(long period) {
		this.period = period;
	}
	/**
	 * @return 匹配范围的最大值，达到最大值后，将不会再扩大匹配范围
	 * 默认Integer.MAX_VALUE
	 * */
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	/**
	 * @return 每个时间周期内扩大的匹配范围值
	 * */
	public int getIncrementValue() {
		return incrementValue;
	}
	public void setIncrementValue(int incrementValue) {
		this.incrementValue = incrementValue;
	}

}