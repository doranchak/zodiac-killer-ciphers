package com.zodiackillerciphers.tests;

import java.util.Random;

public class DaliDays {
	/**
	 * what are the odds of at least n dates out of m dates falling on a Dali
	 * day?
	 */
	public static void test(int n, int m, int numTests) {
		
		Random rand = new Random();
		
		int total = 0;
		int hits = 0;
		
		for (int i=0; i<numTests; i++) {
			int num = 0;
			for (int j=0; j<m; j++) {
				if (rand.nextFloat() < ((float)1)/7) {
					num++;
				}
			}
			if (num >= n) hits++;
			total++;
		}
		float ratio = ((float)hits)/total;
		System.out.println(hits + ", " + total + ", ratio " + ratio + ", 1/ratio " + 1/ratio);
	}
	public static void main(String[] args) {
		test(5, 7, 10000000);
		//9624, 10000000, ratio 0.0009624

	}
}
