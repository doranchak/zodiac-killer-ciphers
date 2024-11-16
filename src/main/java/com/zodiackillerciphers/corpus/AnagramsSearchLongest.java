package com.zodiackillerciphers.corpus;

import com.zodiackillerciphers.ngrams.AZDecrypt;

/** search corpus for longest word n-gram sequences that can be found as anagrams in the given string. */
public class AnagramsSearchLongest {
	public static void main(String[] args) {
		if (args.length !=2)
			throw new IllegalArgumentException("Please provide input string and min length.");
		String inputString = args[0];
		int minLength = Integer.valueOf(args[1]);
		AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/Spaces/English/5-grams_english+spaces_jarlve_reddit_v1912.gz", 5, true);
		AnagramsSearch.searchLongestAnagrams(inputString, minLength, Integer.MAX_VALUE, false);
	}
}
