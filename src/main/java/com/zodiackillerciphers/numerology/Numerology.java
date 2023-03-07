package com.zodiackillerciphers.numerology;

import com.zodiackillerciphers.dictionary.WordFrequencies;

public class Numerology {
	
	/** compute gematria (sum of numbers-for-letters) of the given word */
	public static int gematriaSum(String word) {
		return gematriaSum(word, false);
	}
	public static int gematriaSum(String word, boolean showSteps) {
		String steps = "";
		String steps2 = word + " = ";
		word = word.toUpperCase();
		int sum = 0;
		for (int i=0; i<word.length(); i++) {
			char c = word.charAt(i);
			int val = (int) c;
			val -= 64;
			if (val < 1 || val > 26) continue;
			sum += val;
			if (showSteps) {
				steps += c + "=" + val + " ";
				steps2 += val + " + ";
			}
		}
		if (showSteps) {
			System.out.println(steps);
			System.out.println(steps2 + "= " + sum);
		}
		return sum;
	}
	
	/** show all words that have the given sum */
	public static void wordsForSum(int sum) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (gematriaSum(word) == sum) System.out.println(WordFrequencies.percentile(word) + " " + WordFrequencies.freq(word) + " " + word.length() + " " + word);
		}
	}
	
	public static void main(String[] args) {
//		System.out.println(gematriaSum("GAMEOFTHRONES", true));
//		System.out.println(gematriaSum("HARRYPOTTER", true));
//		System.out.println(gematriaSum("STARWARS", true));
//		System.out.println(gematriaSum("STARTREK", true));
//		System.out.println(gematriaSum("KNOWLEDGE", true));
//		System.out.println(gematriaSum("HARDWORK", true));
//		System.out.println(gematriaSum("ATTITUDE", true));
//		
		WordFrequencies.init();
//		for (String word : WordFrequencies.map.keySet()) {
//			if (gematriaSum(word, false) == 100) {
//				System.out.println(gematriaSum(word, true));
//			}
//		}
		
		wordsForSum(58);
//		wordsForSum(110);
	}
}
