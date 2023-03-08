package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.tests.unicity.PlaintextBean;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** the challenge cipher D.C.B. Marsh made which was in the SF Examiner, 1969-10-23 */
public class MessageToZodiac extends CorpusBase {
	public static String cipher = "@Xf9lee8%B#PVtSQ!YrHM\\Zp5)t+6T^EXMFZc+O@Wc6^";
	/** pairs of matching symbols */
	public static List<int[]> pairs;
	static {
		pairs = new ArrayList<int[]>();
		for (int i=0; i<cipher.length()-1; i++) {
			for (int j=i+1; j<cipher.length(); j++) {
				if (cipher.charAt(i) == cipher.charAt(j)) {
					pairs.add(new int[] {i,j});
				}
			}
		}
	}

	public static void search(int maxErrors) {
		WordFrequencies.init();
		//CorpusBase.REDDIT_ONLY = true;
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long hits = 0;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngrams = ngrams(cipher.length());
			for (List<String> plaintext : ngrams) {
				StringBuffer sbWithSpaces = flatten(plaintext, true);
				StringBuffer sbWithoutSpaces = flatten(plaintext, false);
				//System.out.println("pt " + sbWithSpaces);
				samples++;
				int errors = errors(sbWithoutSpaces);
				
				if (errors <= maxErrors) {
					// reject if most words are too short
					if (PlaintextBean.badAverageTokenLength(plaintext))
						continue;
					// reject if string is too repetitive
					if (PlaintextBean.tooRepetitive(sbWithoutSpaces.toString()))
						continue;
					hits++;
					System.out.println(errors + "	" + WordFrequencies.scoreLog(sbWithSpaces.toString()) + "	" + sbWithSpaces);
				}
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
	}
	
	public static int errors(StringBuffer sb) {
		int errors = 0;
		for (int[] pair : pairs) {
			if (sb.charAt(pair[0]) != sb.charAt(pair[1])) errors++;
		}
		return errors;
	}
	
	public static void test1() {
		for (int[] pair : pairs) 
			System.out.println(Arrays.toString(pair));
	}
	public static void test2() {
		search(3);
	}
	public static void main(String[] args) {
		test2();
	}
}
