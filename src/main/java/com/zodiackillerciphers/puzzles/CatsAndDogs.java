package com.zodiackillerciphers.puzzles;

import java.util.Arrays;

/** https://www.youtube.com/watch?v=8gppjTZ1vCE */
public class CatsAndDogs {
	
	static int hits;
	static boolean SAY = false;
	static String tab = "	";
	
	// in N kennels, how many allocations of dogs + cats are possible where there
	// are no two cats in adjacent kennels?
	public static void search(int N) {
		hits = 0;
		search(N, new boolean[N], 0);
		System.out.println(N + tab + hits + tab + (long)Math.pow(2, N));
	}
	public static void search(int N, boolean[] kennels, int index) {
		if (index == kennels.length) {
			hits++;
			say(Arrays.toString(kennels));
			return;
		}
		kennels[index] = false;
		search(N, kennels, index+1);
		if (index == 0 || !kennels[index-1]) {
			kennels[index] = true;
			search(N, kennels, index+1);
			kennels[index] = false;
		}
	}
	
	public static void say(String msg) {
		if (SAY) System.out.println(msg);
	}
	
	public static void main(String[] args) {
		for (int N=1; N<100; N++)
			search(N);
	}
}
