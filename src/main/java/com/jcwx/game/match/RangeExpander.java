package com.jcwx.game.match;
/**
 * 判断以及变更匹配范围的接口类，用来判断匹配区间是否难以匹配以及扩大匹配区间
 * @author Chenlong
 * */
public interface RangeExpander {
	/**
	 *  自己根据想定义的条件判断是否为难以匹配到，
	 *  比如大于指定时间未匹配到或者大于指定全局扫描次数
	 * @return 是否难以匹配到
	 * */
	boolean isDifficutMatch(MatchTask task);
	/**
	 * 扩大匹配范围，可以自定义实现扩大机制，建议采用
	 * 递增时的扩大范围方式
	 * @return 扩大匹配范围后的匹配范围值
	 * */
	int expandMatchRange(MatchTask task);
}
