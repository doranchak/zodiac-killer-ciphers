package com.zodiackillerciphers.annealing.anagrams_shuffler;

import com.zodiackillerciphers.ngrams.AZDecrypt;

public class Solver {
	public static Thread[] threads;
	public static void solve(int numThreads, String inputString) {
		AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/Spaces/English/5-grams_english+spaces_jarlve_reddit_v1912.gz", 5, true);		
		threads = new ShufflerThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new ShufflerThread(inputString, 100, 100000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] args) {
		if (args.length != 1) throw new IllegalArgumentException("Please specify the input string (in all caps)");
		solve(1, args[0]);
	}
}
