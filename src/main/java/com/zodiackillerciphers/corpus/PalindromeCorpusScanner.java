package com.zodiackillerciphers.corpus;

import java.util.Arrays;

import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

public class PalindromeCorpusScanner extends CorpusBase {
	// search corpus for plaintext that fits Z13.
	public static void search() {
//		WordFrequencies.init();
//		CorpusBase.REDDIT_ONLY = true;
		String tab = "	";
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = true;
		boolean go = true;
		while (go) {
			go = !SubstitutionMutualEvolve.randomSource();
			StringBuilder flattened = new StringBuilder(flatten(Arrays.asList(tokens), true));
			for (int i=0; i<flattened.length(); i++) {
				StringBuilder pal;  String palNS; StringBuilder palWS;
				double ioc;
				boolean perfect;
				pal = new StringBuilder();
				palWS = new StringBuilder();
				perfect = palindrome(flattened, i, false, pal, palWS);
				palNS = pal.toString().replaceAll(" ", "");
				if (palNS.length() > 6) {
					ioc = Stats.ioc(palNS);
					if (ioc < 0.3) {
						System.out.println(palNS.length() + tab + ioc + tab + perfect + tab + file + tab + i + tab + palWS + tab + pal + tab + context(flattened, pal, i, 20));
					}
				}
				pal = new StringBuilder();
				palWS = new StringBuilder();
				perfect = palindrome(flattened, i, true, pal, palWS);
				palNS = pal.toString().replaceAll(" ", "");
				if (palNS.length() > 6) {
					ioc = Stats.ioc(palNS);
					if (ioc < 0.3) {
						System.out.println(palNS.length() + tab + ioc + tab + perfect + tab + file + tab + i + tab + palWS + tab + pal + tab + context(flattened, pal, i, 20));
					}
				}
			}
		}
	}
	
	public static String context(StringBuilder input, StringBuilder match, int index, int pad) {
		int start = index-match.length()/2 - pad;
		if (start < 0) start = 0;
		int end = index+match.length()/2 + pad;
		if (end >= input.length()) end = input.length()-1;
		return input.substring(start, end+1);
	}

	public static boolean palindrome(StringBuilder input, int start, boolean even, StringBuilder palindrome, StringBuilder palindromeWS) {
		int end = start;
		if (even) end++;
		boolean go = true;
		boolean perfect = false;
		while (go) {
//			System.out.println(start + " " + end);
			if (start < 0 || start >= input.length()) break;
			if (end < 0 || end >= input.length()) break;
			char c1 = input.charAt(start);
			char c2 = input.charAt(end);
			if (c1 == ' ') {
				start--;
				palindromeWS.insert(0, ' ');
				continue;
			}
			if (c2 == ' ') {
				end++;
				palindromeWS.append(' ');
				continue;
			}
			if (c1 != c2) break;
			palindrome.insert(0, c1);
			palindromeWS.insert(0, c1);

			if (start != end) {
				palindrome.append(c2);
				palindromeWS.append(c2);
				if (start == 0 || input.charAt(start-1) == ' ') {
					if (end == input.length()-1 || input.charAt(end+1) == ' ') {
						perfect = true;
					}
				} else perfect = false;
			}
			start--;
			end++;
		}
		return perfect;
	}
	public static void main(String[] args) {
		search();
//		process();
//		for (int i=0; i<100; i++) {
//			StringBuilder pal = palindrome(new StringBuilder("THIS IS A TEST OF MADAM RACECAR I THINK GARRAG IS FINE"), i, false);
//			if (pal.length() > 0) {
//				System.out.println(i + " " + pal);
//			}
//			pal = palindrome(new StringBuilder("THIS IS A TEST OF MADAM RACECAR I THINK GARRAG IS FINE"), i, true);
//			if (pal.length() > 0) {
//				System.out.println(i + " " + pal);
//			}
//		}
	}
}
