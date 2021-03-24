package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.util.PermutationState;

public class StateColumnarVerify implements PermutationState {

	private int[] elements;
	private StringBuilder ciphertext;
	private StringBuilder plaintext;
	private static String TAB = "	";
	private static int N = 4;

	public StateColumnarVerify(int[] elements, StringBuilder ciphertext, StringBuilder plaintext) {
		this.elements = elements;
		this.ciphertext = ciphertext;
		this.plaintext = plaintext;
	}

	public StringBuilder decode() {
		StringBuilder pt = com.zodiackillerciphers.ciphers.algorithms.ColumnarTransposition.decode(ciphertext, elements,
				false);
		return pt;
	}

	public StringBuilder encode() {
		StringBuilder ct = com.zodiackillerciphers.ciphers.algorithms.ColumnarTransposition.encode(plaintext, elements);
		return ct;
	}

	public static double ngramScore(StringBuilder sb, int n) {
		double val = 0;
		for (int i=0; i<sb.length()-n+1; i++) {
			String sub = sb.substring(i, i+n);
			val += NGramsCSRA.valueFor(sub, "EN", true);
		}
		return val;
	}
	
	@Override
	public void action() {
		StringBuilder ct = encode();
		this.ciphertext = ct;
		StringBuilder pt = decode();

		double scorePt = ngramScore(pt, N);
		double scoreCt = ngramScore(ct, N);
		System.out.println(scoreCt + TAB + scorePt + TAB + Arrays.toString(elements));
		System.out.println(ct);
		System.out.println(pt);
		if (!plaintext.toString().trim().equals(pt.toString().trim())) {
			throw new RuntimeException("MISMATCHED PLAINTEXTS");
		}
	}

	@Override
	public void swap(int a, int b) {
		int tmp = elements[a];
		elements[a] = elements[b];
		elements[b] = tmp;
	}

	@Override
	public int numElements() {
		// TODO Auto-generated method stub
		return elements.length;
	}

}
