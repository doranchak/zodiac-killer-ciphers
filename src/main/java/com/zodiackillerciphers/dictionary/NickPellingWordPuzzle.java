package com.zodiackillerciphers.dictionary;
/** http://ciphermysteries.com/2018/06/30/a-quicky-word-puzzle-for-you */
public class NickPellingWordPuzzle {
	public static String row1 = "qwertyuiop";
	public static String row2 = "asdfghjkl";
	public static String row3 = "zxcvbnm";
	public static void search() {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			int perc = WordFrequencies.percentile(word);
			if (match(word, row1))
				System.out.println(word.length() + "	1	" + perc + "	" + word);  
			else if (match(word, row2))
				System.out.println(word.length() + "	2	" + perc + "	" + word);  
			else if (match(word, row3))
				System.out.println(word.length() + "	3	" + perc + "	" + word);  
			
		}
	}
	public static boolean match(String word, String row) {
		word = word.toLowerCase();
		for (int i=0; i<word.length(); i++) {
			if (!row.contains(""+word.charAt(i))) return false; 
		}
		return true;
	}
	public static void main(String[] args) {
		search();
	}
}
