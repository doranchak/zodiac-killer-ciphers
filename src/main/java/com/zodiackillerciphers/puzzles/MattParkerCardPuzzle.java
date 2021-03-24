package com.zodiackillerciphers.puzzles;

import java.util.HashMap;
import java.util.Map;

//https://www.youtube.com/watch?v=8I1OCqX93XI
public class MattParkerCardPuzzle {
	/**
	 * first question: how many permutations have no three or four card
	 * ascending/descending subsets?
	 * 
	 * counts by subset length:  4 of length 2, 18 of length 3, 2 of length 4.
	 * thus the answer is 4.  they are: 2413 2143 3142 and 3412
	 */
	/**
	 * second: what's the largest size of ascending/descending subset that all
	 * permutations of 10 cards must contain?
	 * 
	 * 
	 * distributions for all N cards up to 10:
	 * 

		1 cards:
		{1=1}
		2 cards:
		{2=2}
		3 cards:
		{2=4, 3=2}
		4 cards:
		{2=4, 3=18, 4=2}
		5 cards:
		{3=86, 4=32, 5=2}
		6 cards:
		{3=306, 4=362, 5=50, 6=2}
		7 cards:
		{3=882, 4=3242, 5=842, 6=72, 7=2}
		8 cards:
		{3=1764, 4=24564, 5=12210, 6=1682, 7=98, 8=2}
		9 cards:
		{3=1764, 4=163872, 5=161158, 6=32930, 7=3026, 8=128, 9=2}
		10 cards:
		{4=985032, 5=1969348, 6=592652, 7=76562, 8=5042, 9=162, 10=2}
		
		f(n) = 1 for n=[1]
		f(n) = 2 for n=[2,4]
		f(n) = 3 for n=[5,9]
		f(n) = 4 for n=[10,?] [best guess: 16]

	So, the answer is 4.



	 *  
	 * 
	 */

	static Map<Integer, Integer> counts;
	static boolean DUMP = false; 
	
	public static <T> void printAllRecursive(int n, int[] elements, char delimiter) {

		if (n == 1) {
			printArray(elements, delimiter);
		} else {
			for (int i = 0; i < n - 1; i++) {
				printAllRecursive(n - 1, elements, delimiter);
				if (n % 2 == 0) {
					swap(elements, i, n - 1);
				} else {
					swap(elements, 0, n - 1);
				}
			}
			printAllRecursive(n - 1, elements, delimiter);
		}
	}
	static void out(String str) {
		if (DUMP) System.out.print(str);
	}
	
	private static <T> void printArray(int[] input, char delimiter) {
		out("\n");
		
		int lis = lis(input);
		int lds = lds(input);
		int max = Math.max(lis, lds);
		Integer count = counts.get(max);
		if (count == null) count = 0;
		count++;
		counts.put(max, count);
		out(max + " ");
		for (int i = 0; i < input.length; i++) {
			out(""+input[i]);
		}
	}
	
	public static int lis(int[] arr) {
		return LIS.lis(arr, arr.length);
	}
	public static int lds(int[] arr) {
		int[] arr2 = new int[arr.length];
		for (int i=0; i<arr.length; i++) {
			arr2[i] = arr[arr.length-1-i];
		}
		return LIS.lis(arr2, arr2.length);
	}


	private static <T> void swap(int[] input, int a, int b) {
		int tmp = input[a];
		input[a] = input[b];
		input[b] = tmp;
	}

	/**
	 * generalized permutation search for longest increasing (decreasing) sequences
	 */
	public static void search(int numCards) {
		counts = new HashMap<Integer, Integer>();
		int[] arr = new int[numCards];
		for (int i = 0; i < numCards; i++)
			arr[i] = i + 1;
		printAllRecursive(numCards, arr, ' ');
		System.out.println(counts);
	}

	public static void main(String[] args) {
		for (int i=1; i<11; i++) {
			System.out.println(i+ " cards:");
			search(i);
		}
	}
}
