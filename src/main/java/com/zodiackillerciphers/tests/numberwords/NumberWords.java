package com.zodiackillerciphers.tests.numberwords;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.MapUtil;
import com.zodiackillerciphers.tests.mapcode.EnglishNumberToWords;

/**
 * test this encoding idea: 1) plaintext is converted to numbers 2) new
 * plaintext formed by writing out numbers as words 3) symbol substitutions
 * applied
 */
public class NumberWords {

	/** ngram size */
	static int n;
	/** stop when an ngram reaches this count */
	static int max;
	/** stream of ngrams */
	static StringBuffer sb;
	static Random rand = new Random();

	/** ngram counts */
	static Map<String, Integer> counts;

	/**
	 * generate ngram statistics for a stream of randomly generated
	 * number-words.
	 * 
	 * @param n
	 *            the ngram size
	 * @param max
	 *            the number of occurrences of an ngram that causes the stats
	 *            collection to stop.
	 */
	public static void generateNgrams(int n, int max, boolean digits) {
		NumberWords.n = n;
		NumberWords.max = max;
		NumberWords.sb = new StringBuffer();
		counts = new HashMap<String, Integer>();

		boolean go = true;
		while (go) {
			makeNgrams(digits);
			go = processNgrams();
		}
		dumpCounts();
	}

	/**
	 * if digits is true, spell out digits individually (22 = TWO TWO instead of
	 * TWENTY TWO)
	 */
	public static void makeNgrams(boolean digits) {
		while (sb.length() < n) {
			int num = rand.nextInt(26) + 1;
			String word;
			if (digits)
				word = EnglishNumberToWords.convertUpperDigits(num).replaceAll(
						" ", "");
			else
				word = EnglishNumberToWords.convertUpper(num).replaceAll(" ",
						"");
			// System.out.println(word);
			sb.append(word);
		}
	}

	/** returns false if it's time to stop */
	public static boolean processNgrams() {
		// System.out.println("sb " + sb);
		int i;
		for (i = 0; i < sb.length() - n + 1; i++) {
			String key = sb.substring(i, i + n);
			Integer val = counts.get(key);
			if (val == null)
				val = 0;
			val++;
			counts.put(key, val);
			// System.out.println("key " + key + " val " + val);
			if (val == max)
				return false;
		}
		sb = new StringBuffer(sb.substring(i, i + n - 1));
		return true;
	}

	public static void dumpCounts() {
		counts = MapUtil.sortByValue(counts, true);
		
		int lineLength = 50000;
		
		int count = 0;
		
		System.out.println("# of Ngrams: " + counts.size());

		for (String key : counts.keySet()) {
			String print = key;
			Integer val = counts.get(key);
			String pad = "";
			if (val < 1000) pad += " ";
			if (val < 100) pad += " ";
			if (val < 10) pad += " ";
			
			System.out.print(print + pad + val);
			count += print.length();
			if (count >= lineLength) {
				System.out.println();
				count = 0;
			}
		}
	}

	public static void test() {
		generateNgrams(6, 9999, false);
	}

	public static void main(String[] args) {
		test();
	}
}
