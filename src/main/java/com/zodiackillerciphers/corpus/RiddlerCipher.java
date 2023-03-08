package com.zodiackillerciphers.corpus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** look for corpora that fit riddler's cipher from the new batman movie trailer */
public class RiddlerCipher extends CorpusBase {
	
	static String cipher = "ABCDBEEFDCC";
	static String tab = "	";
	
	/** find word combinations, sampled from a corpus, that fit the riddler's cipher */
	public static void search() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		Set<String> found = new HashSet<String>();
		WordFrequencies.init();
		System.out.println("Done word init.");
		SubstitutionMutualEvolve.initSources();
		System.out.println("Done source init.");
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long hits = 0;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			//System.out.println("File: " + CorpusBase.file);
			List<List<String>> ngrams = ngrams(cipher.length());
			for (List<String> plaintext : ngrams) {
				samples++;
				StringBuffer sbWithSpaces = flatten(plaintext, true);
				StringBuffer sbWithoutSpaces = flatten(plaintext, false);
				
				if (found.contains(sbWithSpaces.toString()))
					continue;

				Map<Character, Character> decoderMap = new HashMap<Character, Character>();
				decoderMap = Ciphers.decoderMapFor(decoderMap, cipher, sbWithoutSpaces.toString());
				if (decoderMap == null)
					continue; // this plaintext does not fit cipher
				
				hits++;
				

				
				boolean homophonic = Ciphers.isHomophonic(decoderMap);
				boolean proper = isProper(sbWithSpaces.toString());
				double ngramsScore = NGramsCSRA.zkscore(sbWithSpaces, "EN", true);
				
				System.out.println(ngramsScore + tab + WordFrequencies.scoreLog(sbWithSpaces.toString()) + tab + proper + tab + homophonic
						+ tab + sources + tab + samples + tab + hits + tab + sbWithSpaces + tab + CorpusBase.file);
				found.add(sbWithSpaces.toString());
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
	}
	
	/** UNFINISHED brute force search for all combinations of N words (sampled from a dictionary) that fit the cipher */   
	static void searchBrute(int numWords) {
		searchBrute(numWords, new HashMap<Character, Character>(), new ArrayList<String>(), 0, 0);
	}
	
	/** UNFINISHED */
	static void searchBrute(int numWords, Map<Character, Character> key, List<String> words, int wordsLength, int pos) {
		if (pos == cipher.length()) {
			if (wordsLength == cipher.length()) {
				// success condition
				for (String word : words)
					System.out.print(word + " ");
				return;
			}
			// failure
			return;
		}
		
		
	}	

	/** length without spaces */
	static int len(StringBuffer sb) {
		return sb.toString().replaceAll(" ", "").length();
	}
	
	/** process results */
	public static void process(String file) {
		WordFrequencies.init();
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		
		List<RiddlerResultBean> results = new ArrayList<RiddlerResultBean>(); 

		List<String> list = FileUtil.loadFrom(file);
		for (String line : list) {
			if (reject(line)) continue;
			RiddlerResultBean bean = new RiddlerResultBean();
			bean.plaintext = line;
			bean.plaintextNoSpaces = line.replaceAll(" ", "");
//			System.out.println(bean.plaintext);
//			System.out.println(bean.plaintextNoSpaces);
			bean.homophonic = Ciphers.isHomophonic(Ciphers.decoderMapFor(cipher, bean.plaintextNoSpaces));
			bean.proper = isProper(bean.plaintext);
			
			String[] words = line.split(" ");

			// number of words
			bean.numWords = words.length;
			
			// average word length, average word percentile, sum of word percentiles
			bean.averageWordLength = 0;
			bean.sumWordPercentiles = 0;
			for (String word : words) {
				bean.averageWordLength += word.length();
				bean.sumWordPercentiles += WordFrequencies.percentile(word);
			}
			bean.averageWordLength /= bean.numWords;
			bean.averageWordPercentile = bean.sumWordPercentiles/bean.numWords;
			
			// ngram score with spaces (zk)
			bean.ngramZk = NGramsCSRA.zkscore(new StringBuffer(line), "EN", true);

			// ngram score with spaces (5-grams only)
			bean.ngram5 = NGramsCSRA.ngramSum(line, "EN", true, 5);
			
			// ngram score with spaces (6-grams only)
			bean.ngram6 = NGramsCSRA.ngramSum(line, "EN", true, 6);

			// ioc (ignore spaces)
			bean.ioc = Stats.iocAsFloat(bean.plaintextNoSpaces);
			results.add(bean);
		}
		Collections.sort(results);
		for (RiddlerResultBean bean : results)
			System.out.println(bean);
	}
	
	/** reject candidates based on certain criteria */
	static boolean reject(String str) {
		/** reject if not enough letter variety */
		int minDistinct = 4;
		
		Set<Character> alphabet = new HashSet<Character>();
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == ' ') continue;
			alphabet.add(ch);
		}
		
		if (alphabet.size() < minDistinct) return true;
		return false;
	}
	
	/** a solution is "proper" if it has a word break where the line break appears in the original cipher.
	 * i.e., after the first 6 non-space characters, a space should appear.
	 **/
	static boolean isProper(String str) {
		int count = 0; // count non-space chars
		int pos = 0;
		char ch;
		while (count<6) {
			ch = str.charAt(pos++);
			if (ch == ' ') continue;
			count++;
		}
		ch = str.charAt(pos);
		return ch == ' ';
	}
	
	public static void test() {
		WordFrequencies.init();
		String[] lines = new String[] {
		"IA SEAT THESS",
		"HE LIES STILL",
		"NOT COLLECT T",
		"DALE ALL TELL",
		"LN SENT THESS",
		"TALE ALL WELL",
		"HN SENT THESS",
		"NN SENT THESS",
		"MN SENT THESS",
		"BELIEF FTILL",
		"POIROT TORII"};
		for (String line : lines) {
			System.out.println(line);
			float sum = 0;
			for (String word : line.split(" ")) {
				int freq = WordFrequencies.percentile(word);
				System.out.println(" - " + word + ": " + freq);
				sum += freq;
			}
			System.out.println("  SUM: " + sum);
		}
	}

	public static void main(String[] args) {
		//search();
		process("/Users/doranchak/Downloads/riddler-cipher/riddler-cipher-results-all.txt");
//		test();
		
	}

}
