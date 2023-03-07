package com.zodiackillerciphers.ciphers.algorithms.columnar;

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
	
	public static Variant[] variants = new Variant[] { Variant.TOP_TO_BOTTOM, Variant.BOTTOM_TO_TOP, Variant.ALTERNATING_1,
			Variant.ALTERNATING_2 };
	/** perfect columnar:
	 * 
	 * key k, width w
	 * plaintext p with length L
	 * 
	 * L is multiple of w
	 * 
	 * Total rows = L/w
	 * 
	 * ciphertext c with length L.
	 * for i in [0,L), 
	 * we need ciphertext letter taken from some position in p
	 * let f(i) return that position from p.
	 * 
	 * column selection as function of i:
	 * 		we seek the nth element of key k
	 * 		n = floor(i/rows)
	 * 
	 * 		we seek the jth element of plaintext p			
	 * 		it comes from column k[n]
	 * 		and the current row is: i % rows
	 * 		so p(j) = the char at (row,col) = (i%rows, k[n])
	 * 		translate to one dimension: p(j) = (i%rows)*w + k[n] 
	 * 
	 * therefore, f(i) = p((i%rows)*w + k[n])
	 */
	public static StringBuilder encode(State state) {
		state.encode();
		return state.ciphertext;
	}
	
//	public static StringBuilder decode(StringBuilder ciphertext, int[] key) {
//		// if this is irregular, then put padding back in until it is regular.
//		// but padding has to be in the expected locations.
//		int[] pads = padPositionsFor(ciphertext, key);
//		
//	}
	
	/** assuming we are encrypting the given text with the given key,
	 * this function returns a list of positions in the resulting cipher text 
	 * where the pad characters appear.
	 * that way we can stick them back in when faced with an irregular cipher text, 
	 * simplifying to decryption calculations.
	 */
	public static int[] pads(StringBuilder text, int[] key) {
		int num = key.length - (text.length() % key.length);
		if (num == key.length) return null; // no pads needed because this is regular cipher.
		//text =
		return null;
	}
	
	
	
	public static StringBuilder decode(State state) {
		state.decode();
		return state.plaintext;
	}

	public static void removeSpaces(StringBuilder sb) {
		if (sb == null) return;
		int pos = sb.indexOf(" ");
		while (pos > -1) {
			sb.deleteCharAt(pos);
			pos = sb.indexOf(" ");
		}
	}

	/** encode the given plaintext with the given key, then decode it back.
	 * if the decoding doesn't match, then raise an error because our 
	 * algorithm is probably broken!
	 */
	public static void testEncodeDecode(Variant variant, StringBuilder pt, int[] key) {
		State state = new State(variant, pt, null, key);
		StringBuilder ct = encode(state);
		System.out.println("Key: " + Arrays.toString(key));
		System.out.println("Key length: " + key.length);
		System.out.println("State: " + state);
		System.out.println("Encoded: [" + ct + "]");
		StringBuilder decoded = decode(new State(variant, null, ct, key, false));
		System.out.println("Decoded: [" + decoded + "]");
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
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ. ";
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
			StringBuilder decoded = decode(new State(Variant.TOP_TO_BOTTOM, null, cipher, key, removeSpaces));
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
		Random rand = new Random();
		while (true) {
			StringBuilder pt = randomPlaintext(200);
			int[] key = randomKey(Math.max(pt.length()/2, 2));
			if (key.length > pt.length()) continue;
			System.out.println("Input plaintext: [" + pt + "]");
			System.out.println("Length: " + pt.length());
			System.out.println("Key: " + Arrays.toString(key));
			System.out.println("Key Length: " + key.length);
			System.out.println("Regular? " + (pt.length() % key.length == 0));
			
			testEncodeDecode(variants[rand.nextInt(variants.length)], pt, key);
			System.out.println();
		}
	}

	public static void testUntransposedIocs() {
		StringBuilder z408 = new StringBuilder(Ciphers.Z408);
		System.out.println(Stats.iocNgram(z408, 2));
		System.out.println(Stats.iocNgram(z408, 3));
		System.out.println(Stats.iocNgram(z408, 4));
		StringBuilder encoded = encode(new State(Variant.TOP_TO_BOTTOM, z408, null, new int[] {3,6,0,4,5,1,2}));
		double[][] iocs = untransposedIocs(encoded, 7, false);
		for (double[] d : iocs)
		System.out.println(Arrays.toString(d));
	}
	public static void testKeyFor() {
		int[] key = keyFor("GERMAN");
		StringBuilder msg = new StringBuilder("DEFENDTHEEASTWALLOFTHECASTLE");
		StringBuilder ct = encode(new State(Variant.TOP_TO_BOTTOM, msg, null, key));
		StringBuilder pt = decode(new State(Variant.TOP_TO_BOTTOM, null, ct, key, false));
		System.out.println(Arrays.toString(key));
		System.out.println(ct);
		System.out.println(pt);
		
	}
	
	public static void makeForJarl() {
		//String pt = "AZDECRYPT IS A ZKDECRYPTO INSPIRED LETTER NGRAM BASED HOMOPHONIC SUBSTITUTION SOLVER SINCE LATE TWO THOUSAND FOURTEEN. IT IS A FREEBASIC PROGRAM THAT IS REALLY AWESOME.";
		String pt = "DURING THE FIFTH CENTURY, THE AREA CAME UNDER THE RULE OF THE FRANKISH MEROVINGIAN KINGS, WHO WERE PROBABLY FIRST ESTABLISHED IN WHAT IS NORTHERN FRANCE, FULL OF FRENCH";
		int[] key = randomKeyWithLength(56);
		System.out.println(Arrays.toString(key));
		testEncodeDecode(Variant.TOP_TO_BOTTOM, new StringBuilder(pt), key);
	}
	
	public static void testSimple() {
		String[] pts = new String[] {
				"DEFENDTHEEASTWALLOFTHECASTLEXX",
				"DEFENDTHEEASTWALLOFTHECASTLE",
				"COLUMNS",
				"COLUMNSXX"
		};
		int[][] keys = new int[][] { { 4, 1, 0, 3, 5, 2 }, { 4, 1, 0, 3, 5, 2 }, { 0, 2, 1 }, { 0, 2, 1 } };
		
		
		for (int i=0; i<pts.length; i++) {
			for (Variant variant : variants) {
				testEncodeDecode(variant, new StringBuilder(pts[i]), keys[i]);
			}			
		}
	}
	
	public static void main(String[] args) {
//		testSimple();
//		testRandomCiphers();
//		testUntransposedIocs();
//		keyFor("SMEGGYWEGGY");
//		testKeyFor();
		//for (int i=0; i<10; i++) 
//		makeForJarl();
	}
}
