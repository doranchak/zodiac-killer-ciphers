package com.zodiackillerciphers.annealing.w168stencil;

import com.zodiackillerciphers.lucene.NGramsCSRA;

public class Solver {
	public static Thread[] threads;
	public static void solve(int numThreads) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		//WordFrequencies.init();
		threads = new W168StencilThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new W168StencilThread(100, 100000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		solve(8);
	}
}
