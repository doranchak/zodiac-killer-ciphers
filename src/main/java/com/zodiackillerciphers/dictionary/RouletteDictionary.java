package com.zodiackillerciphers.dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zodiackillerciphers.io.FileUtil;

/**
 * randomly select words from a dictionary. common words are returned more often
 * than infrequently use words.
 */
public class RouletteDictionary {

	public static String[] words;
	public static long[] freqs;
	
	static Random rand = new Random();

	public static void init() {
		init(WordFrequencies.WORDS1);
	}

	public static void init(String wordFile) {
		List<String> lines = scrub(FileUtil.loadFrom(wordFile));
		int count = 0;

		words = new String[lines.size()];
		freqs = new long[lines.size()];

		for (String line : lines) {
			String[] split = line.split(" ");
			String word = split[1];
			word = WordFrequencies.scrub(word.toUpperCase());
			words[count] = word;
			long freq = Long.valueOf(split[0]);
			if (count > 0)
				freq += freqs[count - 1];
			freqs[count] = freq;
			count++;
		}
	}

	public static List<String> scrub(List<String> lines) {
		List<String> result = new ArrayList<String>();
		for (String line : lines) {
			if (line.contains("WHOLE_CORPUS"))
				continue;
			String[] split = line.split(" ");

			String word = split[1];
			if (WordFrequencies.ignore(word)) {
				continue;
			}
			result.add(line);
		}
		return result;
	}

	
	/** pick random word.  common words will be returned more often. */
	public static String randomWord() {
		int r = rand.nextInt((int) freqs[freqs.length-1]);
		return words[find(r)];
	}
	
	public static void test() {
		init();
//		words = new String[] { "a", "b", "c","d" };
//		freqs = new long[] { 3, 5, 6, 20 };
//		for (long i = 0; i < 20; i++)
//			System.out.println("find " + i + " " + find(i));
		// System.out.println(Arrays.toString(words));
		// System.out.println(Arrays.toString(freqs));
//		System.out.println(find(97762564));
		for (int i=0; i<10000; i++) System.out.print(randomWord() + " ");
	}
	public static void testZodiacWordsWithCounts() {
		init(WordFrequencies.ZODIAC_WORDS_WITH_COUNTS);
		for (int i=0; i<10000; i++) System.out.print(randomWord() + " ");
	}

	/** perform binary search for the given freq value T */
	public static int find(long T) {
		return find(T, 0, words.length-1);
	}

	public static int find(long T, int L, int R) {
		int m = (int) Math.floor((L + R) / 2);
//		System.out.println("T " + T + " L " + L + " R " + R + " m " + m);
		if (L > R) {
			if (freqs[L] > T) return L;
			return -1;
		}
		long A = freqs[m];
//		System.out.println("A " + A);
		if (A < T)
			return find(T, m + 1, R);
		else if (A > T)
			return find(T, L, m - 1);
		return m+1;
	}

	public static void main(String[] args) {
		//test();
		testZodiacWordsWithCounts();
	}
}
