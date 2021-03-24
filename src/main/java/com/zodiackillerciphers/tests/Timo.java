package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/** timo pakkanen's claims */
public class Timo {
	public static String[] letters = new String[] {
		"zobefcdmk",
		"kmotievzn",
		"nmclvdbqfvw",
		"wfcbgotbe",
		"bdocintvgs"
	};
	
	public static void search() {
		WordFrequencies.init();
		List<String> words = WordFrequencies.byLength.get(5);
		for (String word : words) {
			boolean match = true;
			for (int i=0; i<word.length(); i++) {
				char ch = word.toLowerCase().charAt(i);
				if (!letters[i].contains(""+ch)) {
					match = false;
					break;
				}
			}
			if (match) {
				System.out.println(WordFrequencies.percentile(word)+"," +word);
			}
		}
		
	}
	public static void main(String[] args) {
		search();
	}
}
