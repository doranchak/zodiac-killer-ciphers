package com.zodiackillerciphers.puzzles;

import java.util.Random;

// https://fivethirtyeight.com/features/can-you-reach-the-summit-first/
public class BasketballPuzzle {
	
	// solve using brute force
	public static void search(int N) {
		
		int hits = 0;
		int total = 0;
		for (int a=1; a<=N; a++) {
			for (int b=1; b<=N; b++) {
				for (int c=1; c<=N; c++) {
					total++;
					
					int sum = a+b+c;
					boolean equalTeams = sum % 2 == 0;
					// What is the probability that, on any given week, it’s possible to form two
					// equal teams with everyone playing, where two towns are pitted against the
					// third?
					
					// implications:
					// 1) two equal teams:  sum must be even
					// 2) everyone playing: sum = total number of players who showed up
					// 3) two towns pitted against the third:  one town brought sum/2 players.
					
					boolean hit = false;
					if (equalTeams && (a==sum/2 || b==sum/2 || c==sum/2))
						hit = true;
//					System.out.println(a + " " + b + " " + c + " " + sum + " " + equalTeams + " " + hit);
					if (hit) hits++;
				}
			}
		}
		float prob = hits;
		prob /= total;
		System.out.println(N + " " + hits + " " + total + " " + prob);
		
		// Extra credit:
		// Suppose that, instead of anywhere from one to five individuals per town,
		// anywhere from one to N individuals show up per town. Now what’s the
		// probability that there will be two equal teams?
		
		// Let m be total people who show up
		// m = a + b + c (number of people from each town)
		// a, b, and c can take any value from: [1,N]
		// therefore m can be: [3, 3N]
		// ???
		
		// brute for for various values of N yields numerator sequence:  3 9 18 30 45 63 84 108 135 165 198 234
		// demoninator is always N^3
		// numerator sequence matches https://oeis.org/A045943:  3*n*(n+1)/2
		// thus, i suspect probability is:  [3*n*(n+1)/2] / n^3
		// simplifies to 3(n+1)/(2n^2)
		
		// answer on https://fivethirtyeight.com/features/can-you-break-a-very-expensive-centrifuge/
		// actual answer was:  3(n−1)/(2n^2)  (numerator: 3(n-1)*n/2)
		// because the sequence was shifted   
		
	}
	
	static Random rand = new Random();
	// returns true if we pick random number of between 1 and N people from each town, 
	// and we can form equal teams 
	public static boolean sample(int N) {
		int a = rand.nextInt(N) + 1;
		int b = rand.nextInt(N) + 1;
		int c = rand.nextInt(N) + 1;
		int sum = a+b+c;
		
		boolean result = false;
		
		if (sum % 2 == 0) {
			int half = sum/2;
			result = a==half || b==half || c==half;
		}
//		System.out.println(a + " " + b + " " + c + " " + sum + " " + result);
		return result;
	}
	
	public static void simulate(int steps, int N) {
		int hits = 0;
		int total = 0;
		for (int i=0; i<steps; i++) {
			if (sample(N)) hits++;
			total++;
		}
		float prob = hits;
		prob /= total;
		System.out.println(hits + " " + total + " " + prob);
	}
	
	public static void main(String[] args) {
//		for (int n=1; n<1000; n++) 
//			search(n);
		// N=2: 3/8
		// N=3: 9/27 
		// N=4: 18/64
		// N=5: 30/125
		// N=6: 45/216
		// N=7: 63/343
		// N=8: 84/512
		// N=9: 108/729
		// N=10: 135/1000
		// N=11: 165/1331
		// N=12: 198/1728
		// N=13: 234/2197
//		simulate(10000000, 2);
		
		int i = 3;
		long val = (long) i;
	}
}

