package com.zodiackillerciphers.tests.keyboard;

// standard keyboard, 1D row of numbers above letters  
public class NumberLayout1D implements NumberLayout {

	// index = digit
	// value = position on keyboard
	static int[] positions = new int[] { 9, 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	
	@Override
	public double distance(int a, int b) {
		return Math.abs(positions[a]-positions[b]);
	}

	@Override
	public double distance(String a, String b) {
		// TODO Auto-generated method stub
		return distance(Integer.valueOf(a), Integer.valueOf(b));
	}

}
