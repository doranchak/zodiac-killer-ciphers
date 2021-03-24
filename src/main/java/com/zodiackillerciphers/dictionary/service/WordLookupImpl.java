package com.zodiackillerciphers.dictionary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.Solver;
import com.zodiackillerciphers.dictionary.WordFrequencies;

public class WordLookupImpl implements WordLookup {

	@Override
	public Results find(String cipherword, int limit) {
		return null;
	}

	@Override
	public Results find(String cipherword, String plaintext, int limit) {
		WordFrequencies.init();
		Results results = new Results();
		
		Map<Character, Character> map = new HashMap<Character, Character>();
		if (plaintext != null)
			for (int i = 0; i < cipherword.length() && i < plaintext.length(); i++) {
				char p = plaintext.charAt(i);
				if (p < 65 || p > 90)
					continue; // only considering A-Z
				map.put(cipherword.charAt(i), p);
			}

		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != cipherword.length()) continue;
			Map<Character, Character> newMap = Ciphers.decoderMapFor(map, cipherword, word);
			if (newMap == null) continue; // new map will be null if an assignment violation is found
			results.countTotal++;
			if (results.words.size() == limit) continue;
			results.addWord(word, WordFrequencies.freq(word), WordFrequencies.percentile(word));
			results.count++;
			
		}
		return results;

	}

	public static void wordPatterns() {
		WordFrequencies.init();
		Map<String, Integer> counts = new HashMap<String, Integer>(); 
		for (String word : WordFrequencies.map.keySet()) {
			String pattern = Ciphers.fromNumeric(Ciphers.toNumeric(word, false), false);
			Integer val = counts.get(pattern);
			if (val == null) val = 0;
			val++;
			counts.put(pattern,  val);
		}
		for (String pattern : counts.keySet()) {
			System.out.println(pattern.length() + "	" + pattern + "	" + counts.get(pattern));
		}
	}
	
	public static void test() {
		WordLookupImpl w = new WordLookupImpl();
		w.find("FNTJFRJKSB", "R", 100).dump();
		//System.out.println(w.find("ABCCBDE", "", 20).json());
	}
	public static void main(String[] args) {
		test();
		//wordPatterns();
	}
}
