package com.jcwx.game.match;

public class Range {
	private Range next;
	private int value;
	public Range(int value){
		this.value=value;
	}
	public Range getNext() {
		return next;
	}
	public void setNext(Range next) {
		this.next = next;
	}

	public int getValue() {
		return value;
	}
}
