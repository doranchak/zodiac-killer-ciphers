package com.zodiackillerciphers.pivots.symmetries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** https://twitter.com/jamestanton/status/1290269147658457090 */
public class PermutationPathPuzzle {
	static boolean[] sideLeft; // path unblocked on left side?
	static boolean[] sideRight; // path unblocked on right side?
	
//	static int[] numbers;
	static boolean[] used;
	static List<Integer> current;
	
	static int n;
	
	static int numTotal;
	static int numValid;
	static int numInvalid;
	
	/** brute force search for feasible side-alternating paths through n points along a line */
	public static void search(int n) {
		PermutationPathPuzzle.n = n;
		numTotal = 0;
		numValid = 0;
		numInvalid = 0;
//		numbers = new int[n];
		//for (int i=0; i<n; i++) numbers[i] = i;
		current = new ArrayList<Integer>();
		used = new boolean[n];
		
		//sideLeft = new boolean[n];
		//for (int i=0; i<n; i++) sideLeft[i] = true;
		//sideRight = new boolean[n];
		//for (int i=0; i<n; i++) sideRight[i] = true;
		
		permute();
		System.out.println("== N: " + n);
		System.out.println("Total: " + numTotal);
		System.out.println("Valid: " + numValid);
		System.out.println("Invalid: " + numInvalid);
	}
	public static void permute() {
		if (current.size() == n) {
			numTotal++;
			boolean unblocked = checkPath();
			if (unblocked) numValid++;
			else numInvalid++;
			System.out.println(out() + ": " + unblocked);
			return;
		}
		for (int i=0; i<n; i++) {
			if (!used[i]) {
				current.add(i);
				used[i] = true;
				permute();
				current.remove(current.size()-1);
				used[i] = false;
			}
		}
	}
	
	public static String out() {
		String s = "";
		for (int i=0; i<current.size(); i++) {
			if (s.length() > 0) s += ", ";
			s += current.get(i) + 1;
		}
		return s;
	}
	
	// returns true if path is possible given the initial direction.
	// (true = right, false = left)
	public static boolean checkPath() {
		int direction = 0;
		
		int[][] connections = new int[2][n]; // 
		for (int i=0; i<2; i++)
			for (int j=0; j<n; j++)
				connections[i][j] = -1;
		
		for (int i=1; i<current.size(); i++) {
			int num = current.get(i);
			int prev = current.get(i-1);
			
			// check for blocked path:
			// on the same side,
			// path from a -> b is blocked
			// if there is some c, where a < c < b, that connects to d 
			// where d > b or d < a
			
			int a, b;
			if (num < prev) {
				a = num;
				b = prev;
			} else {
				a = prev;
				b = num;
			}
			for (int j=a+1; j<b; j++) {
				int connection = connections[direction][j];
				if (connection < 0) continue; // unconnected
				if (connection < a || connection > b) return false; 
			}
			connections[direction][num] = prev;
			connections[direction][prev] = num;
			direction = 1-direction;
		}
		
//		String result = Arrays
//		        .stream(connections)
//		        .map(Arrays::toString) 
//		        .collect(Collectors.joining(""));
//		System.out.println(current + ", " + result);
		return true;
	}
	public static void main(String[] args) {
		for (int i=2; i<30; i++) 
			search(i);
	}
}
