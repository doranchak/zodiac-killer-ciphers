package com.zodiackillerciphers.annealing.advent;

import com.zodiackillerciphers.ciphers.Ciphers;

public class AdventTest {
	public static Thread[] threads;
	public static void run(int numThreads) {
		threads = new AdventThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new AdventThread(i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		run(10);
	}
}
