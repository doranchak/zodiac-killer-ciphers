package com.zodiackillerciphers.ciphers.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.Stats;

public class ColumnarTransposition {
	public static StringBuilder encode(StringBuilder plaintext, int[] key) {
		if (key == null || plaintext == null)
			return plaintext;
		for (int i : key) if (i >= key.length) {
			throw new RuntimeException("Invalid key entry " + i);
		}
		StringBuilder encoded = new StringBuilder();
		int i = 0;
		int pos = key[i];
		int width = key.length;
		while (encoded.length() < plaintext.length()) {
			if (pos >= plaintext.length()) {
				i++;
				if (i == key.length)
					break;
				pos = key[i];
			}
			encoded.append(plaintext.charAt(pos));
			pos += width;
		}
		return encoded;
	}

	public static StringBuilder decode(StringBuilder ciphertext, int[] key, boolean removeSpaces) {
		if (key == null || ciphertext == null)
			return ciphertext;
		for (int i : key) if (i >= key.length) {
			throw new RuntimeException("Invalid key entry " + i);
		}
		
		int len = ciphertext.length();
		boolean irregular = len % key.length != 0;
		
		int width = key.length;
		int height = len/width;
		if (irregular) height++;

		int padding = irregular ? width - (len % width) : 0;
		int irregularMissingColumnStart = width - padding; 
		
		StringBuilder decoded = new StringBuilder(len + padding);
		for (int i=0; i<len+padding; i++) decoded.append(' ');
//		System.out.println("len " + ciphertext.length() + " width " + width + " height " + height
//				+ " irregularMissingColumnStart + " + irregularMissingColumnStart + " padding " + padding);
		
		int which = 0;
		int col = key[which];
		int row = 0;
		int lastRow = height-1;
		for (int i=0; i<len; i++) {
//			System.out.println(" i " + i+ " row " + row + " (i/width) " + (i/width) + " col " + col + " ch " + ciphertext.charAt(i) + " (row*width+col) " + (row*width+col));
			decoded.setCharAt(row*width+col, ciphertext.charAt(i));
//			System.out.println(decoded + ", " + row + " " + col + ", " + which);
//			System.out.println("DEFENDTHEEASTWALLOFTHECASTLE");
			
			row++;
			if (row > lastRow || (row == lastRow && col >= irregularMissingColumnStart)) {
				which++;
				if (which == key.length) {
					if (removeSpaces) removeSpaces(decoded);
					return decoded;
				}
				row = 0;
				col = key[which];
			}
		}
		return decoded;
	}
	
	public static void removeSpaces(StringBuilder sb) {
		if (sb == null) return;
		int pos = sb.indexOf(" ");
		while (pos > -1) {
			sb.deleteCharAt(pos);
			pos = sb.indexOf(" ");
		}
	}

	public static void testEncodeDecode(StringBuilder pt, int[] key) {
		StringBuilder ct = encode(pt, key);
		System.out.println("Encoded: " + ct);
		StringBuilder decoded = decode(ct, key, false);
		System.out.println("Decoded: " + decoded);
		if (!pt.toString().equals(decoded.toString())) {
			System.out.println("ERROR: NO MATCH!");
			System.out.println("[" + pt + "]");
			System.out.println("[" + decoded + "]");
			System.exit(-1);
		}
	}
	
	/** scramble array in place using fisher yates shuffle */
	public static void shuffle(int[] array) {
		Random rand = new Random();
		if (array == null) return;;
		for (int i=array.length-1; i>=1; i--) {
			int j = rand.nextInt(i+1);
			int tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}
	
	public static StringBuilder randomPlaintext(int max) {
		Random rand = new Random();
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int length = rand.nextInt(max)+1;
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<length; i++) {
			sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
		}
		return sb;
	}

	public static int[] randomKey(int max) {
		Random rand = new Random();
		int length = rand.nextInt(max-1)+2; // [2, max]
		return randomKeyWithLength(length);
	}
	public static int[] randomKeyWithLength(int length) {
		int[] key = new int[length];
		for (int i=0; i<length; i++) key[i] = i;
		shuffle(key);
		return key;
	}
	
	/** try out all possible keys for the given key length, and return the mean and max ngraphic iocs of every untransposed cipher */
	public static double[][] untransposedIocs(StringBuilder cipher, int keyLength, boolean removeSpaces) {
		double[][] iocs = new double[3][2]; // 3 rows (one per ngraph size), 2 columns (mean and max ioc)
		for (int i=0; i<3; i++) iocs[i] = new double[2];
		int[] key = new int[keyLength];
		Set<Integer> usedDigits = new HashSet<Integer>();
		int[] count = new int[1];
		untransposedIocs(cipher, keyLength, iocs, key, usedDigits, 0, count, removeSpaces);
//		System.out.println("Keys: " + count[0]);
		for (int i=0; i<3; i++) {
			iocs[i][0] /= count[0];
		}
//		for (double[] d : iocs)
//			System.out.println(Arrays.toString(d));
		return iocs;
	}
	public static void untransposedIocs(StringBuilder cipher, int keyLength, double[][] iocs, int[] key, Set<Integer> usedDigits, int column, int[] count, boolean removeSpaces) {
		if (column == keyLength) {
//			System.out.println(Arrays.toString(key));
			StringBuilder decoded = decode(cipher, key, removeSpaces);
//			if (decoded.equals(cipher)) {
//				System.out.println("EQUAL! " + Arrays.toString(key) + " " + cipher);
//				System.exit(-1);
//			}
//			String line = "";
			for (int i=0; i<3; i++) {
				double ng = Stats.iocNgram(decoded, i+2);
				iocs[i][0] += ng;
				iocs[i][1] = Math.max(ng, iocs[i][1]);
//				line += ng + " ";
			}
			count[0]++;
//			System.out.println(line + " " + Arrays.toString(key) + " " + decoded);
			return;
		}
		for (int i=0; i<keyLength; i++) {
			if (usedDigits.contains(i)) continue;
			usedDigits.add(i);
			key[column] = i;
			untransposedIocs(cipher, keyLength, iocs, key, usedDigits, column+1, count, removeSpaces);
			usedDigits.remove(i);
		}
	}
	
	/** convert the given key phrase into a key consisting of non repeating integers */
	public static int[] keyFor(String keyPhrase) {
		TreeSet<Character> letters = new TreeSet<Character>();
		StringBuffer keyPhraseUpdated = new StringBuffer();
		for (int i=0; i<keyPhrase.length(); i++) {
			char ch = keyPhrase.charAt(i);
			if (!letters.contains(ch)) {
				letters.add(keyPhrase.charAt(i));
				keyPhraseUpdated.append(ch);
			}
		}
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<keyPhraseUpdated.length(); i++) {
			char key = keyPhraseUpdated.charAt(i);
			map.put(key, i);
		}
		int val = 0;
		int[] key = new int[keyPhraseUpdated.length()];
		for (Character letter : letters) {
			key[val++] = map.get(letter);
		}
		//System.out.println(letters + " " + keyPhraseUpdated + " " + Arrays.toString(key));
		return key;
	}
	
	public static void testRandomCiphers() {
		while (true) {
			StringBuilder pt = randomPlaintext(50);
			int[] key = randomKey(Math.max(pt.length()/2, 2));
			if (key.length > pt.length()) continue;
			System.out.println(pt);
			System.out.println(Arrays.toString(key));
			
			testEncodeDecode(pt, key);
			
		}
	}

	public static void testUntransposedIocs() {
		StringBuilder z408 = new StringBuilder(Ciphers.Z408);
		System.out.println(Stats.iocNgram(z408, 2));
		System.out.println(Stats.iocNgram(z408, 3));
		System.out.println(Stats.iocNgram(z408, 4));
		StringBuilder encoded = encode(z408, new int[] {3,6,0,4,5,1,2});
		double[][] iocs = untransposedIocs(encoded, 7, false);
		for (double[] d : iocs)
		System.out.println(Arrays.toString(d));
	}
	public static void testKeyFor() {
		int[] key = keyFor("GERMAN");
		StringBuilder msg = new StringBuilder("DEFENDTHEEASTWALLOFTHECASTLE");
		StringBuilder ct = encode(msg, key);
		StringBuilder pt = decode(ct, key, false);
		System.out.println(Arrays.toString(key));
		System.out.println(ct);
		System.out.println(pt);
		
	}
	public static void main(String[] args) {
//		testEncodeDecode(new StringBuilder("DEFENDTHEEASTWALLOFTHECASTLEXX"), new int[] { 4, 1, 0, 3, 5, 2 }); // len 30
//		testEncodeDecode(new StringBuilder("DEFENDTHEEASTWALLOFTHECASTLE"), new int[] { 4, 1, 0, 3, 5, 2 }); // len 28
//		testRandomCiphers();
//		testUntransposedIocs();
//		keyFor("SMEGGYWEGGY");
		testKeyFor();
	}
}
