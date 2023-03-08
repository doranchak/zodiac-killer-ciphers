package com.zodiackillerciphers.ciphers.w168.ngrams.attic;

import com.zodiackillerciphers.ciphers.w168.StringUtils;
import com.zodiackillerciphers.ciphers.w168.W168;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/** place to put junk */
public class ShuffleNgramsTestAttic {

	public static void shuffleTest(StringBuilder[] cipher, int n, int shuffles, boolean reverse, int period) {
		System.out.println("n=" + n + ", shuffles=" + shuffles + ", reverse=" + reverse + ", period=" + period);
		StatsWrapper stats = new StatsWrapper();
		String cipherStr = toString(cipher, reverse);
		
		double score = score(cipherStr, n, period);
		stats.actual = score;
		for (int i=0; i<shuffles; i++) {
			cipherStr = CipherTransformations.shuffle(cipherStr);
			score = score(cipherStr, n, period);
			stats.addValue(score);
		}
		stats.output();
	}

	/** this version merges the ngram scores by using zkscore */
	public static void shuffleTestCombined(StringBuilder[] cipher, int shuffles, boolean reverse, int period) {
		StatsWrapper stats = new StatsWrapper();
		String cipherStr = toString(cipher, reverse);
		
		double score = scoreZK(cipherStr, period);
		stats.actual = score;
		for (int i=0; i<shuffles; i++) {
			cipherStr = CipherTransformations.shuffle(cipherStr);
			score = scoreZK(cipherStr, period);
			stats.addValue(score);
		}
		System.out.println(reverse + "	" + period + "	" + stats.outputStr());
	}
	// without period
	public static double score(StringBuilder str, int n) {
		double score = 0;
		for (int i=0; i<str.length()-n+1; i++) {
			String ngram = str.substring(i,i+n);
			score += NGramsCSRA.valueFor(ngram, "EN", true);
		}
		return score;
	}
	// with period
	static double score(String str, int n, int period) {
		double score = 0;
		boolean go = true;
		int i=0;
		while (go) {
			String ngram = "";
			for (int j=0; j<period*n; j+=period) {
				if (i+j >= str.length()) {
					go = false; 
					break;
				}
				ngram += str.charAt(i+j);
			}
			i++;
			if (go) {
				//System.out.println("[" + ngram + "]");
				score += NGramsCSRA.valueFor(ngram, "EN", true);
			}
		}
		return score;
	}
	static double scoreZK(String str, int period) {
		double score = 0;
		boolean go = true;
		int i=0;
		int n =6;  // zkscore the entire 6-gram
		while (go) {
			String ngram = "";
			for (int j=0; j<period*n; j+=period) {
				if (i+j >= str.length()) {
					go = false; 
					break;
				}
				ngram += str.charAt(i+j);
			}
			i++;
			if (go) {
				//System.out.println("[" + ngram + "]");
				score += NGramsCSRA.zkscore(new StringBuffer(ngram), "EN", true);
			}
		}
		return score;
	}
	public static String toString(StringBuilder[] arr, boolean reverse) {
		String result = "";
		for (StringBuilder sb : arr) result += sb;
		if (reverse) result = new StringBuffer(result).reverse().toString();
		return result;
	}
	public static void go() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");		
		int shuffles = 10000;
		
		for (int n=2; n<=6; n++) {
			for (boolean reverse : new boolean[] {false, true}) {
				shuffleTest(StringUtils.toStringBuilder(W168.Z408_1), n, shuffles, reverse, 1);
			}
		}
	}
	public static void go2() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");		
		int shuffles = 10000;
		
		for (int n=2; n<=6; n++) {
			for (boolean reverse : new boolean[] {false, true}) {
				shuffleTest(StringUtils.toStringBuilder(W168.Z408_PT), n, shuffles, reverse, 1);
			}
		}
	}

	public static void go3() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		int shuffles = 100000;

		for (int n = 2; n <= 6; n++) {
			for (boolean reverse : new boolean[] { false, true }) {
				for (int period = 1; period <= 84; period++) {
					shuffleTest(StringUtils.toStringBuilder(W168.Z408_1), n, shuffles, reverse, period);
				}
			}
		}
	}
	public static void go4() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		int shuffles = 100000;

		for (int period = 1; period <= 84; period++) {
			for (boolean reverse : new boolean[] { false, true }) {
				shuffleTestCombined(StringUtils.toStringBuilder(W168.Z408_1), shuffles, reverse, period);
			}
		}
	}
}
