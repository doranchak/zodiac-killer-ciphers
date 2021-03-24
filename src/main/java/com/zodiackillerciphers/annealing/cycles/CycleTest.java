package com.zodiackillerciphers.annealing.cycles;

import com.zodiackillerciphers.ciphers.Ciphers;

public class CycleTest {
	public static Thread[] threads;
	public static void run(String ciphertext, int numThreads) {
		threads = new CycleThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new CycleThread(ciphertext, 2, 5000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void greedy() {
		CycleSolution cs = new CycleSolution(Ciphers.Z340);
		cs.initialize();
		double bestEnergy = Double.MAX_VALUE;
		while (true) {
			cs.mutate();
			double currentEnergy = cs.energy();
			if (currentEnergy < bestEnergy) {
				bestEnergy = currentEnergy;
				System.out.println("NEW BEST " + cs);
				cs.mutateReverseClear();
			} else {
				cs.mutateReverse();
			}
		}
	}
	public static void main(String[] main) {
		run(Ciphers.Z340, 8);
		//greedy();
	}
}
