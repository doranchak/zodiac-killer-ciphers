package com.zodiackillerciphers.tests.samblake;

import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

public class CorpusScanner extends CorpusBase {
	/** pick random sample from corpus.  then see if any long strings in candidate plaintexts can be found in it. */
	public static void scan() {

		SubstitutionMutualEvolve.initSources();
		StringBuffer plaintexts = TransformedCiphers
				.getPlaintexts("/Users/doranchak/projects/zodiac/sam-blake-combined-plaintexts-zk-and-az.txt");
		Map<String, Set<Integer>> index = TransformedCiphers.ngramMapFor(6, plaintexts);
		
		CorpusBase.SHOW_INFO = false;
		long hits = 0;

		while (true) {
			SubstitutionMutualEvolve.randomSource();
			StringBuffer corpus = flatten(tokens, false);
			TransformedCiphers.findLargeMatchingSubstrings(6, plaintexts, corpus.toString(), index, file);

		}
	}
	
	
	public static void main(String[] args) {
		scan();
	}
	
}
