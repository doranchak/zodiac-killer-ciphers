package com.zodiackillerciphers.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zodiackillerciphers.names.Census;

/** another recursive search of words/phrases that fit in Z13 */
public class Z13WordSearch {
	public static int MIN_WORD_LENGTH = 3;
	public static int CIPHER_LENGTH = 13;
	/** does the given string fit Z13?  allows for partial prefixes to fit too */ 
	public static boolean fits(StringBuffer sb) {
		
		/* Z13:
		if (sb.length() > 11 && sb.charAt(0) != sb.charAt(11)) return false;
		if (sb.length() > 10 && sb.charAt(2) != sb.charAt(10)) return false;
		if (sb.length() > 6 && sb.charAt(4) != sb.charAt(6)) return false;
		if (sb.length() > 8 && sb.charAt(6) != sb.charAt(8)) return false;
		if (sb.length() > 12 && sb.charAt(7) != sb.charAt(12)) return false;
		return true;
		*/
		
		/* glurk's cipher: 8434567889788 */
		
		//8 4 3 4 5 6 7 8 8 9 7  8  8
		//0 1 2 3 4 5 6 7 8 9 10 11 12
		if (sb.length() > 7 && sb.charAt(0) != sb.charAt(7)) return false;
		if (sb.length() > 8 && sb.charAt(0) != sb.charAt(8)) return false;
		if (sb.length() > 11 && sb.charAt(0) != sb.charAt(11)) return false;
		if (sb.length() > 12 && sb.charAt(0) != sb.charAt(12)) return false;
		
		if (sb.length() > 3 && sb.charAt(1) != sb.charAt(3)) return false;
		if (sb.length() > 10 && sb.charAt(6) != sb.charAt(10)) return false;
		
		return true;
		
	}
	
	public static boolean ignore(String word) {
		if (word.length() == 1) {
			if (word.equals("A")) return false;
			if (word.equals("I")) return false;
			return true;
		}
		return false;
	}
	public static void search(int maxWords) {
		Census.init();
		WordFrequencies.init();
		search(new ArrayList<String>(), new StringBuffer(), maxWords);
	}
	public static void search(List<String> currentWords, StringBuffer currentPlaintext, int maxWords) {
		int remaining = CIPHER_LENGTH-currentPlaintext.length();
		if (remaining == 0) { // we filled up Z13 without violating any constraints
			dump(currentWords);
			return;
		}
		if (remaining < 0) throw new RuntimeException("" + remaining);
		if (currentWords.size() >= maxWords) return; // too many words 
		// recurse
		for (int L=remaining; L>=MIN_WORD_LENGTH; L--) {
			List<String> words = WordFrequencies.byLength.get(L);
			Collections.sort(words);
			if (words == null || words.size() == 0) continue;
			for (String word : words) {
				if (ignore(word)) continue;
				StringBuffer sbNew = new StringBuffer(currentPlaintext).append(word);
				if (fits(sbNew)) {
					currentWords.add(word);
					search(currentWords, sbNew, maxWords);
					currentWords.remove(currentWords.size()-1);
				}
			}
		}
	}
	public static void dump(List<String> words) {
		float score = 1.0f;
		float scoreCensus = 1.0f;
		for (String word : words) {
			score *= (WordFrequencies.percentile(word) + 1) * word.length();
			scoreCensus *= (Census.score(word, null) + 1);
		}
		String line = "" + score + ", " + scoreCensus + ", " + words.size() + ", ";
		for (String word : words) line += word + " ";
		for (String word : words) line += WordFrequencies.percentile(word) + " ";
		System.out.println(line);
	}
	
	public static void main(String[] args) {
		//search(Integer.valueOf(args[0]));
		search(2);
	}
}
