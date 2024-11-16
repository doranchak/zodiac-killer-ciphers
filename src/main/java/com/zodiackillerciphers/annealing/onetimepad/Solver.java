package com.zodiackillerciphers.annealing.onetimepad;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.ngrams.AZDecrypt;

public class Solver {
	public static Thread[] threads;
	public static void solve(int numThreads) {
		AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/5-grams_english_jarlve_reddit_v1912.gz", 5);
		
		threads = new OTPThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new OTPThread(Tests.ciphers_unknown_2, 19, false, 100, 100000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		solve(1);
	}
}
