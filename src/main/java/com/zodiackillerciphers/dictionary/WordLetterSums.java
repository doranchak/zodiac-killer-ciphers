package com.zodiackillerciphers.dictionary;

public class WordLetterSums {
	/** find all words that have the given letter value sum */
	public static void findWordsWithSum(int sum) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (sumFor(word) == sum) {
				System.out.println(word.length() + " " + WordFrequencies.freq(word) + " " + word + " " + dump(word));
			}
		}
	}
	
	public static int sumFor(String word) {
		int sum = 0;
		if (word == null) return sum;
		for (int i=0; i<word.length(); i++) {
			char ch = word.charAt(i);
			sum += ch-64;
		}
		return sum;
	}
	
	/** return string show how sum is constructed from letter values */
	public static String dump(String word) {
		if (word == null) return null;
		String result = "";
		
		for (int i=0; i<word.length(); i++) {
			char ch = word.charAt(i);
			int val = ch-64;
			
			if (!result.isEmpty()) result += "+";
			result += val;
		}
		return result;
		
	}
	
	public static void main(String[] args) {
		findWordsWithSum(100);
	}
}
