package com.zodiackillerciphers.puzzles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.tests.LetterFrequencies;

/* find the best 3-word opening.
 *    - They need to have unique letters (15 unique letters total)
 *    - They need to have the highest letter frequency sum  
 */
public class Wordle {

	static int topYellowScore = 0;
	static int topGreenScore = 0;
	static int topTotal = 0;
	
	// for each character, and for each position, how often did it not appear there but in other positions?
	static Map<Character, Map<Integer, Integer>> yellowDatabase;
	// for each character, and for each position, how often did it appear there?
	static Map<Character, Map<Integer, Integer>> greenDatabase;
	
	static String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	public static void search3() {
		List<String> wordList = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/dictionaries/wordle-5-letter-words.txt");
		float max = 0;
		for (int a = 0; a < wordList.size(); a++) {
			Set<Character> letters = new HashSet<Character>();
			if (seen(letters, wordList.get(a))) continue;
			for (int b = a + 1; b < wordList.size(); b++) {
				if (seen(letters, wordList.get(b))) continue;
				for (int c = b + 1; c < wordList.size(); c++) {
					if (seen(letters, wordList.get(c))) continue;
					
					String text = wordList.get(a) + wordList.get(b) + wordList.get(c);
					String textSpaces = wordList.get(a) + " " + wordList.get(b) + " " + wordList.get(c);
					
					float freq = LetterFrequencies.frequencySum(text);
					if (freq > max) {
						max = freq;
						System.out.println("BEST " + freq + " " + textSpaces);
					} //else
						//System.out.println(freq + " " + textSpaces);
					
				}

			}

		}
	}
	public static void search4() {
		List<String> wordList = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/dictionaries/wordle-5-letter-words.txt");
		float max = 0;
		for (int a = 0; a < wordList.size(); a++) {
			Set<Character> letters = new HashSet<Character>();
			if (seen(letters, wordList.get(a))) continue;
			for (int b = a + 1; b < wordList.size(); b++) {
				if (seen(letters, wordList.get(b))) continue;
				for (int c = b + 1; c < wordList.size(); c++) {
					if (seen(letters, wordList.get(c))) continue;
					for (int d = c + 1; d < wordList.size(); d++) {
						if (seen(letters, wordList.get(d))) continue;
						
						String text = wordList.get(a) + wordList.get(b) + wordList.get(c) + wordList.get(d);
						String textSpaces = wordList.get(a) + " " + wordList.get(b) + " " + wordList.get(c) + " " + wordList.get(d);
						
						float freq = LetterFrequencies.frequencySum(text);
						if (freq > max) {
							max = freq;
							System.out.println("BEST " + freq + " " + textSpaces);
						} //else
							//System.out.println(freq + " " + textSpaces);
						
						
					}
				}

			}

		}
	}
	/** for all selections of N unique words, rank them by these measurements across a selection of top-percentile 5-letter words:
	 * - number of distinct yellow hits
	 * - number of distinct green hits
	 */
	static void search5(int N) {
		makeDatabase();
		int[] selections = new int[N];
		List<String> wordList = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/dictionaries/wordle-5-letter-words.txt");
		search5(selections, 0, wordList);
	}
	static void search5(int[] selections, int index, List<String> wordList) {
		if (index == selections.length) {
			//System.out.println(Arrays.toString(selections));
			search5Measure(selections, wordList);
			return;
		}
		int start = 0;
		if (index > 0)
			start = selections[index-1]+1;
		for (int i=start; i<wordList.size(); i++) {
			if (index == 0) System.out.println(" - " + i + "...");
			selections[index] = i;
			search5(selections, index+1, wordList);
		}
	}
	
	static void search5Measure(int[] selections, List<String> wordList) {
		
		// PROBLEM: this measurement doesn't accont for yellow hits being counted for positions that already hit green.
		
		int greenScore = 0;
		int yellowScore = 0;
		int totalScore = 0;
		
		Set<String> seen = new HashSet<String>(); // make sure not to double count the same char+position pair.


		for (int i = 0; i < selections.length; i++) {
			String word = wordList.get(selections[i]);
			for (int k = 0; k < word.length(); k++) {
				char c = word.charAt(k);
				String key = c + " " + k;
				if (seen.contains(key)) continue;
				seen.add(key);
				Map<Integer, Integer> map = yellowDatabase.get(c);
				if (map != null) {
					Integer val = map.get(k);
					if (val != null) {
						yellowScore += val;
					}
				}
				map = greenDatabase.get(c);
				if (map != null) {
					Integer val = map.get(k);
					if (val != null) {
						greenScore += val;
					}
				}
			}
		}
		totalScore = greenScore + yellowScore;
		if (greenScore > topGreenScore || yellowScore > topYellowScore || totalScore > topTotal) {
			String words = "";
			for (int i=0; i<selections.length; i++) {
				String word = wordList.get(selections[i]);
				words += word + " ";
			}
			if (greenScore > topGreenScore) topGreenScore = greenScore; 
			if (yellowScore > topYellowScore) topYellowScore = yellowScore;
			if (totalScore > topTotal) topTotal = totalScore;
			System.out.println(totalScore + " " + greenScore + " " + yellowScore + " " + words);
		}
	}
	/** precompute how often letters appear in various positions in the top words */
	static void makeDatabase() {
		WordFrequencies.init();
		List<String> topWords = new ArrayList<String>();
		// collect the top 5000 words
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != 5) continue;
			topWords.add(word.toLowerCase());
			if (topWords.size() == 5000) break;
		}
		
		// when we guess a letter at a position, we need to know:
		// 1) how often it appeared in this position in all of the top words
		// 2) how often it did not appear in this position but appeared in another
		
		for (String word : topWords) {
			for (int i=0; i<word.length(); i++) {
				mapGreen(word.charAt(i), i);
			}
			mapYellow(word);
		}
		
		System.out.println(topWords);
		System.out.println(greenDatabase);
		System.out.println(yellowDatabase);
	}
	
	static void mapGreen(char ch, int position) {
		if (greenDatabase == null)
			greenDatabase = new HashMap<Character, Map<Integer, Integer>>();
		Map<Integer, Integer> map = greenDatabase.get(ch);
		if (map == null ) {
			map = new HashMap<Integer, Integer>();
			greenDatabase.put(ch, map);
		}
		
		Integer val = map.get(position);
		if (val == null) val = 0;
		val++;
		map.put(position, val);
	}
	static void mapYellow(String word) {
		if (yellowDatabase == null)
			yellowDatabase = new HashMap<Character, Map<Integer, Integer>>();
		
		Map<Character, Integer> counts = new HashMap<Character, Integer>(); 
		for (int a=0; a<word.length()-1; a++) {
			char c = word.charAt(a);
			Integer val = counts.get(c);
			if (val == null) val = 0;
			val++;
			counts.put(c, val);
		}
		
		for (Character key : counts.keySet()) {
			for (int pos=0; pos<word.length(); pos++) {
				if (key.equals(word.charAt(pos))) continue;
				Map<Integer, Integer> map = yellowDatabase.get(key);
				if (map == null ) {
					map = new HashMap<Integer, Integer>();
					yellowDatabase.put(key, map);
				}
				Integer val = map.get(pos);
				if (val == null) val = 0;
				val++;
				map.put(pos, val);
			}
		}
		
	}

	static boolean seen(Set<Character> letters, String word) {
		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			if (letters.contains(ch)) {
				for (int j = 0; j < i; j++)
					letters.remove(word.charAt(j)); // clean up
				return true;
			} else {
				letters.add(ch);
			}
		}
		return false;
	}
	public static void main(String[] args) {
//		search3();
//		search4();
		search5(3);
//		makeDatabase();
	}
}
