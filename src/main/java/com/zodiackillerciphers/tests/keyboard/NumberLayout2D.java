package com.zodiackillerciphers.tests.keyboard;

// 2d number pad  
public class NumberLayout2D implements NumberLayout {

	// index = digit
	// value = [row,col] positions
	static int[][] positions = new int[][] { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 1, 2 }, { 2, 0 }, { 2, 1 }, { 2, 2 },
			{ 3, 0 }, { 3, 1 }, { 3, 2 } };
	
	@Override
	public double distance(int a, int b) {
		int[] a_pos = positions[a];
		int[] b_pos = positions[b];
		
		if (a == 0) {// use closest x distance to the 0 key which spans 2 horizontal positions.
			a_pos[1] = Math.min(b_pos[1], 1);
		} else if (b == 0) {
			b_pos[1] = Math.min(a_pos[1], 1);
		}
		
		double distance = (a_pos[0] - b_pos[0]) * (a_pos[0] - b_pos[0]);
		distance += (a_pos[1] - b_pos[1]) * (a_pos[1] - b_pos[1]);
		distance = Math.sqrt(distance);
		return distance;

	}

	@Override
	public double distance(String a, String b) {
		// TODO Auto-generated method stub
		return distance(Integer.valueOf(a), Integer.valueOf(b));
	}

}
