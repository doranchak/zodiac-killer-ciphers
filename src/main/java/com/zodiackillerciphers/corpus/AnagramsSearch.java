package com.zodiackillerciphers.corpus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** search to corpus for strings that can anagram to the given string */
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
			List<List<String>> ngrams = ngrams(str.length());
			for (List<String> plaintext : ngrams) {
				StringBuffer sbWithSpaces = flatten(plaintext, true);
				StringBuffer sbWithoutSpaces = flatten(plaintext, false);
				if (found.contains(sbWithSpaces.toString())) continue;
				if (Anagrams.anagram(str, sbWithoutSpaces.toString(), true)) {
					System.out.println(
							WordFrequencies.scoreLog(sbWithSpaces.toString()) + " " + sbWithSpaces + " " + str);
					found.add(sbWithSpaces.toString());
				}
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
	}
	
	public static void main(String[] args) {
		search("EMRNPAOLEMSGPCETTO");
	}

}
