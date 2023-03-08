package com.zodiackillerciphers.ciphers.w168.ngrams;

import java.util.Arrays;

import com.zodiackillerciphers.ciphers.w168.W168;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.util.PermutationState;
import com.zodiackillerciphers.util.Permutations;

/** which column permutations of the grid produce the highest n-gram scores? */
public class ColumnPermutationNgrams implements PermutationState {

	public int[] elements;
	public StringBuilder[] cipher;
	
	public ColumnPermutationNgrams(int permutationSize, StringBuilder[] cipher) {
		elements = new int[permutationSize];
		this.cipher = cipher;
		for (int i=0; i<permutationSize; i++) {
			elements[i] = i;
		}
	}
	@Override
	public void action() {
		//System.out.println(Arrays.toString(elements));
		
		StringBuilder text = new StringBuilder();
		StringBuilder scores = new StringBuilder();
		for (var row=0; row<cipher.length; row++) {
			for (var col=0; col<cipher[0].length(); col+=elements.length) {
				for (var element : elements) {
					var colNew = col + element;
					if (colNew >= cipher[0].length()) continue;
					text.append(cipher[row].charAt(colNew));
				}
			}
		}
		if (text.length() != 168) throw new RuntimeException("Invalid length: " + text.length());
		String tab = "	";
		
		for (int n=2; n<=6; n++) {
			double val = 0;
			for (int i=0; i<text.length()-n+1; i++) {
				String ngram = text.substring(i, i+n);
				val += NGramsCSRA.valueFor(ngram, "EN", true);
			}
			System.out.println(elements.length + tab + n + tab + val + tab + Arrays.toString(elements));  
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
		return elements.length;
	}
	
	public static void test(StringBuilder[] cipher) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		for (int p=2; p<11; p++) {
			ColumnPermutationNgrams cpn = new ColumnPermutationNgrams(p, cipher);
			Permutations.recurse(cpn);
		}
		
	}
	public static void main(String[] args) {
		test(W168.cipherBuilder);
	}
	
	

}
