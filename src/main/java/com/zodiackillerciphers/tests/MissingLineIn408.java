package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.keyreconstruction.KeyReconstruction;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;
import com.zodiackillerciphers.transform.CipherTransformations;

/**
 * Search corpus for sequences of words that can fit between parts 2 and 3 of the 408 cipher,
 * while maximizing the number of homophone cycles that are "repaired" and minimizing the number
 * that are broken. 
 * 
 */
public class MissingLineIn408 extends CorpusBase {
	public static String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String tab = "	";
	// We are looking for a plaintext string in this form: TH <x> E, where <x> is a
	// string of length L, with 0 < L < 18.
	//
	// To preserve or fix cycles, the allowable frequencies of letters comprising
	// <x> are:
	//
	// (note: n is assumed to be zero or greater, and can be selected for each
	// letter independently)
	// A: n (sequence already broken before part 3)
	// B: n
	// C: n
	// D: n (sequence already broken by part 2)
	// E: 7n
	// F: 2n
	// G: n
	// H: 2n
	// I: 4n
	// J: n
	// K: n
	// L: n
	// M: n
	// N: 4n+1
	// O: 4n+1
	// P: n
	// Q: n
	// R: 3n+1
	// S: 4n
	// T: 4n
	// U: n
	// V: n
	// W: n
	// X: n
	// Y: n
	// Z: n
	
	public static void scanCorpus() {
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			for (int i=0; i<tokens.length; i++) {
				String firstToken = tokens[i];
				if (!firstToken.startsWith("TH")) continue; // first token must start with TH
				StringBuffer withoutSpaces = new StringBuffer();
				StringBuffer withSpaces = new StringBuffer();
				for (int j=i; j<tokens.length; j++) {
					String currentToken = tokens[j];
					withoutSpaces.append(currentToken);
					if (withoutSpaces.length()-3 > 17) break;
					if (withSpaces.length() > 0) withSpaces.append(" ");
					withSpaces.append(currentToken);
					if (!currentToken.equals("THE") && currentToken.endsWith("E") && withoutSpaces.length() > 3) {
						int score = score(withoutSpaces);
						if (score < 2) {
							System.out.println(withoutSpaces.length()-3 + tab + score + tab + "and all " + withSpaces + " i have killed");
						}
					}
				}
			}
		}
	}
	public static void scanDictionary() {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (!word.endsWith("E")) continue;
			StringBuffer full = new StringBuffer("THE" + word);
			StringBuffer fullSpaces = new StringBuffer("THE " + word);
			if (full.length()-3 > 17) continue;
			int score = score(full);
			if (score < 2) {
				System.out.println(full.length()-3 + tab + score + tab + "and all " + fullSpaces + " i have killed");
			}
		}
	}

	
	public static int score(StringBuffer sb) {
		Map<Character, Integer> counts = new HashMap<Character, Integer>();
		for (int i=2; i<sb.length()-1; i++) { // ignore "TH" at beginning and "E" at end
			char key = sb.charAt(i);
			Integer val = counts.get(key);
			if (val == null) {
				val = 0;
			}
			val++;
			counts.put(key, val);
		}
		
		int diffs = 0;
		for (int i=0; i<alpha.length(); i++) {
			char ch = alpha.charAt(i);
			int diff = diff(ch, counts.get(ch));
			//System.out.println(" - " + ch + " " + counts.get(ch) + " " + diff);
			diffs += diff;
		}
		return diffs;
	}
	
	public static int diff(char ch, Integer count) {
		if (count == null) count = 0;
//		if (ch == 'A' || ch == 'B' || ch == 'C' || ch == 'D' || ch == 'G' || ch == 'J' || ch == 'K' || ch == 'L'
//				|| ch == 'M' || ch == 'P' || ch == 'Q' || ch == 'U' || ch == 'V' || ch == 'W' || ch == 'X' || ch == 'Y'
//				|| ch == 'Z')
//			return 0;
		if (ch == 'F' || ch == 'H') {
			return count % 2 == 0 ? 0 : 1;
		}
		if (ch == 'I' || ch == 'S' || ch == 'T') {
			return count % 4 == 0 ? 0 : 1;
		}
		if (ch == 'E') {
			return count % 7 == 0 ? 0 : 1;
		}
		if (ch == 'R') {
			return (count + 2) % 3 == 0 ? 0 : 1;
		}
		if (ch == 'N' || ch == 'O') {
			return (count + 3) % 4 == 0 ? 0 : 1;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		scanCorpus();
//		scanDictionary();
//		for (int c = 0; c<20; c++) {
//			System.out.println(c + ": " + (c+2) % 3);
//		}
//		System.out.println(1 % 2);
	}
}
