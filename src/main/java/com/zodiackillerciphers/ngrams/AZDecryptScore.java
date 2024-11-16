package com.zodiackillerciphers.ngrams;

public class AZDecryptScore {	
	public static void main(String[] args) {
		if (args.length != 1) throw new IllegalArgumentException("Please provide an input string");
		String input = args[0];
		if (input.indexOf(" ") > -1)
			AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/Spaces/English/5-grams_english+spaces_jarlve_reddit_v1912.gz", 5, true);
		else
			AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/5-grams_english_jarlve_reddit_v1912.gz", 5, false);
		System.out.println("Input: " + input);
		System.out.println("Spaces? " + (input.indexOf(" ") > -1 ? "Yes" : "No"));
		float score = AZDecrypt.score(input);
		System.out.println("Score: " + score);
		input = input.replaceAll(" ", "");
		System.out.println("Score (divided by length without spaces): " + score/input.length());
	}

}
