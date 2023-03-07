package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.algorithms.columnar.State;
import com.zodiackillerciphers.ciphers.algorithms.columnar.Variant;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.util.PermutationState;

public class StateColumnarBrute implements PermutationState {

	private boolean encode;
	private int[] elements;
	private StringBuilder ciphertext;
	private StringBuilder plaintext;
	private Variant variant;
	private static String TAB = "	";
	private static int N = 4;
	
	/** use TreeSet to implement min/max heap of fixed number of elements. */
	TreeSet<Double> treeSet;
	/** max size of heap */
	int maxHeapSize = 10;
	

	public StateColumnarBrute(Variant variant, int[] elements, StringBuilder ciphertext, StringBuilder plaintext, boolean encode) {
		this.variant = variant;
		this.encode = encode;
		this.elements = elements;
		this.ciphertext = ciphertext;
		this.plaintext = plaintext;
		
		treeSet = new TreeSet<Double>();
		
	}

	public StringBuilder decode() {
		StringBuilder pt = com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTransposition
				.decode(new State(variant, null, ciphertext, elements, false));
		return pt;
	}

	public StringBuilder encode() {
		StringBuilder ct = com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTransposition
				.encode(new State(variant, plaintext, null, elements));
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
		StringBuilder text = encode ? encode() : decode();
		Double scorePt = ngramScore(text, N);
		
		boolean print = false;
		// if heap not full, just add it
		if (treeSet.size() < maxHeapSize) {
			treeSet.add(scorePt);
			print = true;
		} else {
			// is this score better than the worst score? 
			Double worst = treeSet.first();
			if (scorePt > worst) { // it's better
				print = true;
				treeSet.add(scorePt); // so add to heap
				treeSet.remove(worst); // and remove the worst score
			}
		}
		
		//if (print) System.out.println(scorePt + TAB + treeSet + TAB + Arrays.toString(elements) + TAB + pt);
		if (print) System.out.println(scorePt + TAB + variant + TAB + Arrays.toString(elements) + TAB + text);
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
