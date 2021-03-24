package com.zodiackillerciphers.annealing.chaoticcaesar;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.lucene.NGramsCSRA;

public class Solver {
	public static Thread[] threads;
	public static void solve(int numThreads) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		WordFrequencies.init();

		
		System.out.println("Reference score: " + NGramsCSRA.ngramSum(Ciphers.Z408_SOLUTION.substring(0,50).toUpperCase(), "EN", false, 3));
		
		threads = new CCThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new CCThread(100, 100000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
		solve(1);
	}
}
