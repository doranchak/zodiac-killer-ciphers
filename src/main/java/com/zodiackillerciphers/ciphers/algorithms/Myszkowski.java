package com.zodiackillerciphers.ciphers.algorithms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Myszkowski extends Base {

	/**
	 * Build alphabet of key
	 */
	public static int[] makeAlphabet(int[] key) {
		Set<Integer> set = new HashSet<Integer>();
		for (int i : key)
			set.add(i);
		Integer[] sorted = set.toArray(new Integer[0]);
		Arrays.sort(sorted);
		int[] sorted2 = new int[sorted.length];
		for (int i = 0; i < sorted.length; i++)
			sorted2[i] = sorted[i];
		return sorted2;
	}

	public static String encode(String plaintext, int[] key) {
		plaintext = cleanup(plaintext);
		int[] alphabet = makeAlphabet(key);
		String ct = "";

		for (int i = 0; i < alphabet.length; i++) {
			for (int row = 0; row * key.length < plaintext.length(); row++) {
				for (int col = 0; col < key.length; col++) {
					if (row * key.length + col >= plaintext.length())
						continue;

					if (key[col] == alphabet[i]) {
						ct += plaintext.charAt(row * key.length + col);
					}
				}
			}
		}
		return ct;
	}

	public static String decode(String ciphertext, int[] key) {
		ciphertext = cleanup(ciphertext);
		int[] alphabet = makeAlphabet(key);
		StringBuffer pt = new StringBuffer();
		int count = 0;

		for (int i = 0; i < ciphertext.length(); i++)
			pt.append(" ");

		for (int i = 0; i < alphabet.length; i++) {
			for (int row = 0; row * key.length < ciphertext.length(); row++) {
				for (int col = 0; col < key.length; col++) {
					if (row * key.length + col >= ciphertext.length())
						continue;

					if (key[col] == alphabet[i]) {
						pt.setCharAt(row * key.length + col,
								ciphertext.charAt(count));
						count++;
					}
				}
			}
		}
		return pt.toString();
	}

	public static void test() {
		String pt = "The trouble with doing something right the first time is that nobody appreciates how difficult it was";
		pt = cleanup(pt);
		System.out
				.println(encode(
						pt,
						new int[] { 3, 1, 3, 3, 1, 2 }));
		System.out
				.println(decode(
						"HRBWHISEIRHHITMSAODPEASWFCTWOINTIETTBPTDUATETULETDOGOMHNGGTTFRSIEIHTNOYARCIEHOIFILITS",
						new int[] { 3, 1, 3, 3, 1, 2 }));
		
		
		Random r = new Random();
		for (int i=0; i<1000; i++) {
			int len = 3 + r.nextInt(10);
			int[] key = new int[len];
			for (int j=0; j<key.length; j++) {
				key[j] = r.nextInt(len);
			}
			String en = encode(pt, key);
			String de = decode(en, key);
			System.out.println(Arrays.toString(key) + " " + en + ", " + de);
			if (!de.equals(pt)) {
				System.out.println("FAIL");
				break;
			}
		}
	}

	public static void main(String[] args) {
		test();
	}

}
