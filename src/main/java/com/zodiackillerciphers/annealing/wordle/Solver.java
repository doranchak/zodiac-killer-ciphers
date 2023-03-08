package com.zodiackillerciphers.annealing.wordle;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.lucene.NGramsCSRA;

public class Solver {
	public static Thread[] threads;
	public static void solve(int numThreads) {
		WordFrequencies.init();
		threads = new WordleThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new WordleThread(100, 100000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		solve(1);
	}
}
