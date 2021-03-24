package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.keyreconstruction.KeyReconstruction;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** which words, when you swap the two given letters, produce other valid words? */
//https://www.reddit.com/r/funny/comments/dmv7s8/i_came_in_to_the_office_early_and_switched_as/
public class SwapLetters extends CorpusBase {
	public static void search(char a, char b) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.indexOf(a) == -1 && word.indexOf(b) == -1) continue;
			StringBuffer sb = swap(word, a, b);
			double p1 = WordFrequencies.freq(sb.toString());
			if (p1 == 0) continue;
			p1 = p1 == 0 ? 0 : Math.log(p1);
			double p2 = WordFrequencies.freq(word);
			if (p2 == 0) continue;
			p2 = p2 == 0 ? 0 : Math.log(p2);
			System.out.println(word.length() + " " + (p1+p2) + " " + sb + " " + word);
		}
	}
	
	public static StringBuffer swap(String inp, char a, char b) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<inp.length(); i++) {
			char c = inp.charAt(i);
			if (c == a) sb.append(b);
			else if (c == b) sb.append(a);
			else sb.append(c);
		}
		return sb;
	}
	
	public static void scanCorpus(int max) {
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		Map<String, Integer> ngrams = new HashMap<String, Integer>();
		Set<String> ngramsNoSpace = new HashSet<String>();
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			for (int i=0; i<tokens.length-1; i++) {
				StringBuffer sb = new StringBuffer();
				sb.append(tokens[i]);
				sb.append(" ");
				sb.append(tokens[i+1]);
				//System.out.println(i + " " + sb);
				if (count(sb) < 3) continue;
				Integer val = ngrams.get(sb.toString());
				if (val == null) val = 0;
				val++;
				ngrams.put(sb.toString(), val);
				ngramsNoSpace.add(sb.toString().replaceAll(" ", ""));
				if (ngrams.size() == max) break;
				if (ngrams.size() % 1000 == 0) 
					System.out.println(ngrams.size());
			}
			if (ngrams.size() == max) break;
		}
		for (String key1 : ngrams.keySet()) {
			String key2 = swap(key1, 'M', 'N').toString().replaceAll(" ", "");
			if (ngramsNoSpace.contains(key2)) {
				System.out.println(key1 + " " + ngrams.get(key1) + " " + key2);
			}
		}
	}
	
	static int count(StringBuffer sb) {
		int count = 0;
		for (int i=0; i<sb.length(); i++) {
			if (sb.charAt(i) == 'M' || sb.charAt(i) == 'N') count++;
		}
		return count;
	}
	
	public static void main(String[] args) {
//		search('M','N');
		scanCorpus(1000000);
	}
}
