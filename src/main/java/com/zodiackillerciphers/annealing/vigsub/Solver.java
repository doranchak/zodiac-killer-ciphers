package com.zodiackillerciphers.annealing.vigsub;

public class Solver {
	public static Thread[] threads;
	public static void solve(int vigKeyLength, int numThreads) {
		threads = new VigSubThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new VigSubThread(vigKeyLength, 10000, 100000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		solve(5, 1);
	}
}
