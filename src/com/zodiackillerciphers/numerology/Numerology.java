package com.zodiackillerciphers.numerology;

import com.zodiackillerciphers.dictionary.WordFrequencies;

public class Numerology {
	
	/** compute gematria (sum of numbers-for-letters) of the given word */
	public static int gematriaSum(String word) {
		return gematriaSum(word, false);
	}
	public static int gematriaSum(String word, boolean showSteps) {
		String steps = "";
		word = word.toUpperCase();
		int sum = 0;
		for (int i=0; i<word.length(); i++) {
			char c = word.charAt(i);
			int val = (int) c;
			val -= 64;
			if (val < 1 || val > 26) continue;
			sum += val;
			if (showSteps) steps += c + "=" + val + " "; 
		}
		if (showSteps) System.out.println(steps);
		return sum;
	}
	
	/** show all words that have the given sum */
	public static void wordsForSum(int sum) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (gematriaSum(word) == sum) System.out.println(word.length() + " " + WordFrequencies.freq(word) + " " + word);
		}
	}
	
	public static void main(String[] args) {
		//System.out.println(gematriaSum("ZODIAC", true));
		
		wordsForSum(58);
	}
}
