package com.zodiackillerciphers.annealing.last18;

import com.zodiackillerciphers.dictionary.InsertWordBreaks;

public class Solver {
	public static Thread[] threads;
	public static void solve(int numThreads) {
		InsertWordBreaks.init("EN", true);
		threads = new Last18Thread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new Last18Thread(10000, 100000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		solve(8);
	}
}
