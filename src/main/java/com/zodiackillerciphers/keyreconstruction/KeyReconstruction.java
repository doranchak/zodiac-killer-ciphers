package com.zodiackillerciphers.keyreconstruction;

import java.util.HashSet;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.tests.ChaoticCaesar;

/** try to find keywords that may be seeds for substitution keywords */
public class KeyReconstruction {
	/** search dictionary for matches to the given key that had its duplicate letters removed */
	public static void searchDictionary(String key) {
		WordFrequencies.init();
		key = key.toUpperCase();
		for (String word : WordFrequencies.map.keySet()) {
			String wordAsKey = keyFrom(word, false);
			if (key.length() > wordAsKey.length()) {
				if (key.startsWith(wordAsKey)) {
					System.out.println(word + " " + WordFrequencies.percentile(word) + " (partial match " + wordAsKey + ")");
				} 
			} else if (key.length() < wordAsKey.length()) {
				if (wordAsKey.startsWith(key)) {
					System.out.println(word + " " + WordFrequencies.percentile(word) + " (partial match " + wordAsKey + ")");
				} 
			} else {
				if (wordAsKey.equals(key)) {
					System.out.println(word + " " + WordFrequencies.percentile(word) + " (EXACT MATCH " + wordAsKey + ")");
				} 
			}
		}
				
	}

	/** search dictionary for matches to the given key that had its duplicate letters removed.
	 * allows wildcards in the key ("?" characters) */
	public static void searchDictionaryWildcard(String key) {
		WordFrequencies.init();
		key = key.toUpperCase();
		for (String word : WordFrequencies.map.keySet()) {
			String wordAsKey = keyFrom(word, false);
			int matches = 0;
			boolean hit = true;
			for (int i=0; i<key.length() && i<wordAsKey.length(); i++) {
				char c1 = key.charAt(i);
				char c2 = wordAsKey.charAt(i);
				if (c1 == c2 || c1 == '?') {
					matches++;
				} else {
					hit = false;
					break;
				}
			}
			if (hit) {
				String m = matches == key.length() ? "EXACT MATCH" : "partial match";
				System.out.println(m + " " + word + " " + WordFrequencies.percentile(word) + " " + wordAsKey); 
			}
			
		}
				
	}
	
	public static String keyFrom(String inp) {
		return keyFrom(inp, true);
	}
	public static String keyFrom(String inp, boolean appendLeftovers) {
		Set<Character> seen = new HashSet<Character>();
		StringBuffer sb = new StringBuffer();
		if (appendLeftovers) inp += ChaoticCaesar.alphabet;
		for (int i=0; i<inp.length(); i++) {
			char c = inp.charAt(i);
			if (seen.contains(c)) continue;
			seen.add(c);
			sb.append(c);
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
//		searchDictionary("LETRS");
		searchDictionaryWildcard("A?REDY");
	}
}
