package com.zodiackillerciphers.ciphers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/** from http://www.schneier.com/blog/archives/2006/01/handwritten_rea.html */
public class SchneierHandwrittenCryptogram {
	public static String[] cipher = new String[] {
			"d%K4q@h*",
			"y&#7V$3?",
			"WjuPD",
			"qXoRwis",
			"Mmg",
			"HkcFBfe",
			"XnLoyuI",
			"wAzQY",
			"bUktPsq",
			"AcZfiYzD",
			"CRVhXqz",
			"EfbdqarO"
	};
	
	
	public static void wordsWithCommonLetters() {
		for (int a=0; a<cipher.length; a++) {
			for (int b=0; b<cipher.length; b++) {
				for (int c=0; c<cipher.length; c++) {
					for (int d=0; d<cipher.length; d++) {
						if (a==b) continue;
						if (b==c) continue;
						if (c==d) continue;
						if (a==c) continue;
						if (a==d) continue;
						if (b==d) continue;
						String word = cipher[a] + " " + cipher[b] + " " + cipher[c] + " " + cipher[d];
						int rep = repeats(word);
						if (rep > 0) System.out.println(rep + ", " + word);
					}
				}
			}
		}
	}
	
	// These two words share 3 letters: wAzQY AcZfiYzD
	// Find all matches from the dictionary.
	//
	/* triplets that share 5 letters:
		5, CRVhXqz qXoRwis wAzQY
		5, CRVhXqz wAzQY qXoRwis
		5, qXoRwis CRVhXqz wAzQY
		5, qXoRwis wAzQY CRVhXqz
		5, wAzQY CRVhXqz qXoRwis
		5, wAzQY qXoRwis CRVhXqz
		
		5, qXoRwis d%K4q@h* CRVhXqz
		5, qXoRwis CRVhXqz d%K4q@h*
		5, qXoRwis CRVhXqz AcZfiYzD
		5, qXoRwis AcZfiYzD CRVhXqz
		5, d%K4q@h* qXoRwis CRVhXqz
		5, d%K4q@h* CRVhXqz qXoRwis
		
	 */
	public static void search() {
		WordFrequencies.init();
		List<String> list5 = WordFrequencies.byLength.get(5);
		List<String> list7 = WordFrequencies.byLength.get(7);
		List<String> list8 = WordFrequencies.byLength.get(8);

		for (String word1 : list7) {
			for (String word2 : list8) {
				for (String word3 : list7) {
					
					// 5, qXoRwis d%K4q@h* CRVhXqz
					if (!match(word1, 0, word2, 4)) continue;
					if (!match(word1, 1, word3, 4)) continue;
					if (!match(word1, 3, word3, 1)) continue;
					if (!match(word2, 4, word3, 5)) continue;
					if (!match(word2, 6, word3, 3)) continue;
					

					//5, CRVhXqz qXoRwis wAzQY
					//if (!match(word1, 1, word2, 3)) continue;
					//if (!match(word1, 4, word2, 1)) continue;
					//if (!match(word1, 5, word2, 0)) continue;
					//if (!match(word1, 6, word3, 2)) continue;
					//if (!match(word2, 4, word3, 0)) continue;
					
					int score = WordFrequencies.freq(word1) + WordFrequencies.freq(word2) + WordFrequencies.freq(word3);
					System.out.println(score + ", " + word1 + ", " + word2 + ", " + word3);
					
				}
			}
		}
		/*
		List<String> list5 = WordFrequencies.byLength.get(5); // first word has 5 letters
		List<String> list8 = WordFrequencies.byLength.get(8); // second word has 8 letters
		
		for (String word1 : list5) {
			for (String word2 : list8) {
				if (!match(word1, 1, word2, 0)) continue;
				if (!match(word1, 2, word2, 6)) continue;
				if (!match(word1, 4, word2, 5)) continue;
				int score = WordFrequencies.freq(word1) + WordFrequencies.freq(word2);
				System.out.println(score + ", " + word1 + ", " + word2);
			}
		}
		*/
		
		
		
	}
	/** which word has the fewest leftovers when the given symbols are decoded? */
	public static void leftovers(String symbols) {
		
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<symbols.length(); i++) {
			set.add(symbols.charAt(i));
		}
		
		for (String s : cipher) {
			int leftovers = s.length();
			for (int i=0; i<s.length(); i++) {
				if (set.contains(s.charAt(i))) leftovers--;
			}
			System.out.println(leftovers + ", " + s);
		}
	}
	
	public static boolean match(String word1, int pos1, String word2, int pos2) {
		return word1.charAt(pos1) == word2.charAt(pos2);
	}
	
	public static int repeats(String text) {
		Set<Character> seen = new HashSet<Character>();
		int repeats = 0;
		for (int i=0; i<text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == ' ') continue;
			if (seen.contains(ch)) repeats++;
			else seen.add(ch);
	
		}
		return repeats;
	}
	
	public static void main(String[] args) {
		//wordsWithCommonLetters();
		//search();
		leftovers("qXoRwis d%K4q@h* CRVhXqz");
	}
}
