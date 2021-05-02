package com.zodiackillerciphers.tests;

import java.util.Random;

public class Exceedance {
	/** for an event that has a 1 in N chance of occuring, what is the chance of it occurring
	 * when M trials are repeatedly conducted?
	 * 
	 * for example, when rolling dice there's a 1 in 6 chance of coming up with a 3,
	 * so the expected chance of coming up with a 3 during 6 trials is 1.
	 * what is it in practice, and how many repeats of the trials are needed to guarantee that it happens?  
	 * 
	 * this might be related to "exceedance probabilities"
	 * 
	 */
	public static void test(int N, int M, int repeats) {
		Random random = new Random();
		
		int hits = 0;
		for (int i=0; i<repeats; i++) {
			for (int j=0; j<M; j++) {
				if (random.nextInt(N) == 0) {
					hits++;
					break;
				}
			}
		}
		double prob = hits;
		prob /= repeats;
		System.out.println(prob);
		
	}
	public static void main(String[] args) {
		test(1000000, 10000000, 1000);
	}
}
