package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;

import com.zodiackillerciphers.util.PermutationState;
import com.zodiackillerciphers.util.Permutations;

public class StateRowPermutations implements PermutationState {

	/** current ordering of rows */
	private int[] rows;
	private StringBuilder[] cipher;
	public StringBuilder[][] permutations;
	
	private int counter;
	
	public StateRowPermutations(StringBuilder[] cipher) {
		int N = cipher.length;
		this.rows = new int[N];
		for (int i=0; i<N; i++) rows[i] = i;
		this.cipher = cipher;
		this.permutations = new StringBuilder[(int)GridPermutations.factorial(N)][N];
		counter = 0;
	}

	@Override
	public void action() {
		for (int i=0; i<rows.length; i++) {
			permutations[counter][i] = new StringBuilder(cipher[rows[i]]);
		}
		System.out.println(counter);
		System.out.println(Arrays.toString(rows));
		System.out.println(Arrays.toString(permutations[counter]));
		System.out.println();
		counter++;
	}

	@Override
	public void swap(int pos1, int pos2) { // recursion is on a 1D array so convert it to 2D
		int tmp = rows[pos1];
		rows[pos1] = rows[pos2];
		rows[pos2] = tmp;
	}

	@Override
	public int numElements() {
		// TODO Auto-generated method stub
		return rows.length;
	}
	
	public static void test() {
		StateRowPermutations s = new StateRowPermutations(StringUtils.toStringBuilder(W168.W168));
		Permutations.recurse(s);
	}
	
	public static void main(String[] args) {
		test();
	}

}
