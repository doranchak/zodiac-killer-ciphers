package com.zodiackillerciphers.tests.keyboard;

// just the raw absolute difference between digits  
public class NumberLayoutBasic implements NumberLayout {

	@Override
	public double distance(int a, int b) {
		return Math.abs(a-b);
	}

	@Override
	public double distance(String a, String b) {
		// TODO Auto-generated method stub
		return distance(Integer.valueOf(a), Integer.valueOf(b));
	}

}
