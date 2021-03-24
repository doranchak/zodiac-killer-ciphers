package com.zodiackillerciphers.annealing.homophonicgenerator;

public class Solver {
	public static Thread[] threads;
	public static void solve(int numThreads) {
		threads = new GeneratorThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new GeneratorThread(10000, 1000000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		solve(8);
	}
}
