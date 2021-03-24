package com.zodiackillerciphers.puzzles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** https://www.quora.com/What-are-some-funny-nice-intelligent-and-mind-boggling-tricky-math-questions-to-ask */

/* You want nice math question? Here we come?
Suppose I have a 10-digits lock. The lock has the following rule:

The first digit of the code is the number of appearance of the digit '0' in the password

The second digit of the code is the number of appearance of the digit '1' in the password

The third digit of the code is the number of appearance of the digit '2' in the password

and so on until...

The 10th digit of the password is the number of occurrence of the digit '9' in the password.

Your task, find my password?

9000000000 is wrong that despite they're 9 zeroes (and the first digit is 9 which is correct) but the last number is zero which is wrong (because there is 1 nine in the password instead of 0)

A good programmer solves this in 3 minutes. This kind of math question tests 2 skill set: 1. Reading comprehension skill (if you don't read this properly you will run in circle) 2. Analyzing skill
*/

/* result:  Solution: [6, 2, 1, 0, 0, 0, 1, 0, 0, 0] */
public class LockPuzzle {
	public static void solve() {
		List<Integer> code = new ArrayList<Integer>();
		solve(code);
	}
	public static void solve(List<Integer> code) {
		if (code.size() < 10) {
			for (int i=0; i<10; i++) {
				code.add(i);
				solve(code);
				code.remove(code.size()-1);
			}
		} else {
			if (!valid(code)) return;
			System.out.println("Solution: " + code);
		}
	}
	
	public static boolean valid(List<Integer> code) {
		if (code == null) return true;
		int[] counts = new int[10];
		for (int i=0; i<code.size(); i++) {
			counts[code.get(i)]++;
		}
		//System.out.println(code + " " + Arrays.toString(counts));
		for (int i=0; i<code.size(); i++) {
			if (code.get(i) != counts[i]) return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		solve();
	}
}
