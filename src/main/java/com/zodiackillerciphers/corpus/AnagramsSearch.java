package com.zodiackillerciphers.corpus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.AZDecrypt;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** search corpus for strings that can anagram to the given string */
public class AnagramsSearch extends CorpusBase {
	public static void search(String str) {
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
//			System.out.println("File: " + CorpusBase.file);
			List<List<String>> ngrams = ngrams(str.length());
			for (List<String> plaintext : ngrams) {
				StringBuffer sbWithSpaces = flatten(plaintext, true);
				StringBuffer sbWithoutSpaces = flatten(plaintext, false);
				if (found.contains(sbWithSpaces.toString())) continue;
				if (Anagrams.anagram(str, sbWithoutSpaces.toString(), true)) {
					System.out.println(
							WordFrequencies.scoreLog(sbWithSpaces.toString()) + " " + sbWithSpaces + " " + CorpusBase.file);
					found.add(sbWithSpaces.toString());
				}
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
	}

	public static void searchLongestAnagrams(String str, int minLength, int maxSources, boolean includeIncompleteEndings) {
//		WordFrequencies.init();
//		System.out.println("Done word init.");
		SubstitutionMutualEvolve.initSources();
		System.out.println("Done source init.");
		CorpusBase.SHOW_INFO = false;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			System.out.println("File: " + CorpusBase.file);
			//System.out.println(Arrays.toString(tokens));
			search(tokens, str, minLength, includeIncompleteEndings);
			if (sources >= maxSources) break;
		}
	}
	
	/**
	 * for each starting position in the given sequence of ngrams, output the
	 * longest subsequence starting at that position that can be anagrammed out of
	 * the given message.
	 */
	public static void search(List<String> ngrams, String message, int minLength, boolean includeIncompleteEndings) {
		message = FileUtil.convert(message).toString(); // force uppercase and keep only alphabetic symbols
		boolean doShowCounts = true;
		for (int i=0; i<ngrams.size(); i++) {
			Map<Character, Integer> counts = Ciphers.countMap(message);
			if (doShowCounts) {
				System.out.println(counts);
				doShowCounts = false;
			}
			
			StringBuffer sb = new StringBuffer();
			for (int j=i; j<ngrams.size(); j++) {
				boolean go = true;
				String ngram = FileUtil.convert(ngrams.get(j)).toString();
				
				int end = sb.length();
				boolean complete = true;
				String fragment = "";
				for (int k=0; k<ngram.length(); k++) {
					char c = ngram.charAt(k);
					Integer count = counts.get(c);
					if (count == null || count == 0) {
						go = false;
						complete = false;
						break;
					}
					count--;
					counts.put(c, count);
					sb.append(c);
					fragment += c;
				}
				if (!includeIncompleteEndings && !complete) { // don't leave incomplete endings   
					sb = new StringBuffer(sb.substring(0, end));
					for (int k=0; k<fragment.length(); k++) { // restore leftover counts
						char ch = fragment.charAt(k);
						Integer val = counts.get(ch);
						val++;
						counts.put(ch, val);
					}
				}
				if (!go) break;
				sb.append(' ');
			}
			int len = len(sb);
			if (len >= minLength) System.out.println(len + "	" + AZDecrypt.score(sb.toString()) + "	" + sb + "	Leftovers: " + leftover(counts));
		}
		
	}
	
	/** length without spaces */
	static int len(StringBuffer sb) {
		return sb.toString().replaceAll(" ", "").length();
	}
	
	public static String leftover(Map<Character, Integer> letterPool) {
		StringBuffer leftover = new StringBuffer();
		for (Character key : letterPool.keySet()) {
			Integer val = letterPool.get(key);
			if (key == 0) continue;
			for (int i=0; i<val; i++) leftover.append(key);
		}
		return leftover.toString();
		
	}
	public static void search(String[] ngrams, String message, int minLength, boolean includeIncompleteEndings) {
		List<String> list = new ArrayList<String>();
		for (String ngram : ngrams) list.add(ngram);
		search(list, message, minLength, includeIncompleteEndings);
	}
	
	/** for let's crack zodiac episode 10 (about anagrams) */
	public static void searchZodiacLetter() {
		//String message = "This is the Zodiac speaking. I am rather unhappy because you people will not wear some nice  buttons. So I now have a little list, starting with the woeman + her baby that I gave a rather intersting ride for a couple howers one evening a few months back that ended in my burning her car where I found them.";
		String message = "San Francisco Chronicle S.F. Mon. Oct 5, 1970 Dear Editor, You'll hate me, but I've got to tell you. The pace isn't any slower! In fact it's just one big thirteenth 13 'Some of them fought it was horrible' P.S. THERE ARE REPORTS city police pig cops are closeing in on me. Fk I'm  crackproof, What is the price tag now? Zodiac";
		
		// try to anagram into portions of the Unabomber manifesto
		StringBuffer corpus = FileUtil.loadSBFrom(
				"/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/corpus/unabomber-manifesto.txt");
		
		String[] tokens = FileUtil.tokenizeAndConvert(corpus.toString());
		search(tokens, message, 50, true);
	}
	public static void searchZodiacLetterLeftovers1() {
		String message = "BCCCCCEFGGGGGIIIIIIIJKOOPRRRRTTUWWZ";
		// try to anagram into portions of the Unabomber manifesto
		StringBuffer corpus = FileUtil.loadSBFrom(
				"/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/corpus/unabomber-manifesto.txt");
		String[] tokens = FileUtil.tokenizeAndConvert(corpus.toString());
		search(tokens, message, 1, true);
	}
	public static void searchZodiacLetterLeftovers2() {
		String message = "AABBDDEEEEHHHHHHHIIIKMPUUVWWWWWZ";
		// try to anagram into portions of the Unabomber manifesto
		StringBuffer corpus = FileUtil.loadSBFrom(
				"/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/corpus/unabomber-manifesto.txt");
		String[] tokens = FileUtil.tokenizeAndConvert(corpus.toString());
		search(tokens, message, 1, true);
	}
	public static void searchFaycal32_A() {
		String message = "YNSETAIDOBLAORTRDFWN";
		// try to anagram into portions of the Unabomber manifesto
		StringBuffer corpus = FileUtil.loadSBFrom(
				"/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/corpus/unabomber-manifesto.txt");
		String[] tokens = FileUtil.tokenizeAndConvert(corpus.toString());
		search(tokens, message, 10, false);
	}
	public static void searchFaycal32_B() {
		String message = "YNSETAIDOBLAORTRDFWN";
		searchLongestAnagrams(message, 10, 25000, false);
	}
	public static void searchFaycal32_C() {
		String message = "YNAIDOBLARDF";
		searchLongestAnagrams(message, 4, 25000, false);
	}
	public static void searchDaleHalloween(int minLength) {
		String message = "BYFIREBYGUNBYKNIFEBYROPEPARADICESLAVES";
		searchLongestAnagrams(message, minLength, Integer.MAX_VALUE, false);
	}
	
	public static void main(String[] args) {
		// search("EMRNPAOLEMSGPCETTO");
		// searchZodiacLetter();
		// searchZodiacLetterLeftovers1();
		// searchZodiacLetterLeftovers2();
		// searchFaycal32_A();
		// searchFaycal32_B();
		// searchFaycal32_C();
		search(args[0]);
	}

}
