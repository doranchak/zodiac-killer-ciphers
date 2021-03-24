package com.zodiackillerciphers.puzzles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MultiplicationCards {
	/**
	 * https://fivethirtyeight.com/features/can-you-outsmart-our-elementary-
	 * school-math-problems/
	 * 
	 * Riddler Classic
	 * 
	 * From Josh and Laura Pasek, another elementary school (but not elementary
	 * in difficulty) math problem:
	 * 
	 * Consider the following game. In front of you is a stack of 10 cards
	 * printed with the numbers 0 through 9, one per card. The stack is shuffled
	 * and, sight unseen, you draw a number from the top. You look at the number
	 * and place it somewhere in the multiplication equation below. You then
	 * draw another number, look at it, and place it somewhere else in the
	 * equation. You do that two more times, until all four slots are filled.
	 * Once a digit is placed, it can’t be moved, and it can’t be drawn again
	 * because it’s no longer in the stack.
	 * 
	 * Your goal is to build a multiplication equation with the lowest possible
	 * product. What is the optimal strategy? And how much of this game is luck
	 * and how much is skill? In other words, how much does the expected product
	 * under the optimal strategy differ from simply placing the cards randomly?
	 */

	/**
	 * my code here is just doing brute force on the 4 cards, but doesn't really
	 * help since you don't yet know what the next cards are.
	 */
	
	static int[][] permutations = new int[][] { { 0, 1, 2, 3 }, { 0, 1, 3, 2 },
			{ 0, 2, 1, 3 }, { 0, 2, 3, 1 }, { 0, 3, 1, 2 }, { 0, 3, 2, 1 },
			{ 1, 0, 2, 3 }, { 1, 0, 3, 2 }, { 1, 2, 0, 3 }, { 1, 2, 3, 0 },
			{ 1, 3, 0, 2 }, { 1, 3, 2, 0 }, { 2, 0, 1, 3 }, { 2, 0, 3, 1 },
			{ 2, 1, 0, 3 }, { 2, 1, 3, 0 }, { 2, 3, 0, 1 }, { 2, 3, 1, 0 },
			{ 3, 0, 1, 2 }, { 3, 0, 2, 1 }, { 3, 1, 0, 2 }, { 3, 1, 2, 0 },
			{ 3, 2, 0, 1 }, { 3, 2, 1, 0 } };

	static int product(int[] digits, int permutation) {
		int[] selection = permutations[permutation];

		int a = digits[selection[0]];
		int b = digits[selection[1]];
		int c = digits[selection[2]];
		int d = digits[selection[3]];

		int product = (a * 10 + b) * (c * 10 + d);
		return product;
	}

	static void bestFor(int[] digits) {
		int best = 0;
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < permutations.length; i++) {
			int product = product(digits, i);
			if (product < min) {
				best = i;
				min = product;
			}
		}

		System.out.println("Best for " + Arrays.toString(digits) + " is "
				+ dump(digits, permutations[best]) + " = " + min);
	}

	static String dump(int[] digits, int[] permutation) {
		String result = digits[permutation[0]] + "" + digits[permutation[1]]
				+ " X " + digits[permutation[2]] + "" + digits[permutation[3]];
		return result;

	}

	static void bruteSearch() {
		for (int a = 0; a < 10; a++) {
			for (int b = 0; b < 10; b++) {
				for (int c = 0; c < 10; c++) {
					for (int d = 0; d < 10; d++) {
						Set<Integer> set = new HashSet<Integer>();
						set.add(a);
						set.add(b);
						set.add(c);
						set.add(d);
						if (set.size() != 4)
							continue;
						bestFor(new int[] { a, b, c, d });
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		// bestFor(new int[] {1,2,3,4});
		bruteSearch();
	}

}
