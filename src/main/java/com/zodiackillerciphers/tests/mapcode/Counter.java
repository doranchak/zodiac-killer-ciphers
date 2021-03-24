package com.zodiackillerciphers.tests.mapcode;

public class Counter {
	long count;
	public Counter() {
		count = 0;
	}
	public void inc() {
		count++;
		if (count % 10000000 == 0) {
			System.out.println("Count: " + count);
		}
	}
	public String toString() {
		return ""+count;
	}
}
