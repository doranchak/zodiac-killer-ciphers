package com.zodiackillerciphers.annealing.namestuffer;

import com.zodiackillerciphers.ciphers.Ciphers;

public class Solver {
	public static Thread[] threads;
	public static void solve(String ciphertext, String plaintext, int numThreads, String[] words) {
		threads = new NamestufferThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new NamestufferThread(ciphertext, plaintext, 10000, 100000, i, words);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		String cipher = Ciphers.Z340; 
		System.out.println("Cipher: " + cipher);
		String[] words = new String[] {"ROBERT", "WILLIAMS"};
		NamestufferSolution.MAX_DIFFERENCE = 1;
		solve(cipher, null, 8, words);
	}
}
