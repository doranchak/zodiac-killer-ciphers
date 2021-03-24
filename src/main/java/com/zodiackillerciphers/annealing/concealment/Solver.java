package com.zodiackillerciphers.annealing.concealment;

import com.zodiackillerciphers.dictionary.InsertWordBreaks;

public class Solver {
	public static Thread[] threads;
	public static void solve(StringBuffer cipher, int numThreads) {
		InsertWordBreaks.init("EN", true);
		threads = new ConcealmentCipherThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new ConcealmentCipherThread(cipher, 10000, 100000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] main) {
//		solve(new StringBuffer("THXISXISXATXESXTOFXTHECOXNXCXEXALMEXNTXCXIPXHXEXR"), 5);
		solve(new StringBuffer("GMTIPDTTOFYANRSITLOACYANWYAJAHCTDSIHNDONIPEICNCOACOHMOOOTYPMDISWTOKIEYWOPRAAOTWFFFYOTYOMIHLYDWICSTOTIHHNCFBYHNLAOWALYCWOWTDSOLIOMWWWYNTLTYMYFYLNAWNMWSSINDIDCWGOOBOYAYOFWGYKODLIAALAC"), 5);
	}
}
