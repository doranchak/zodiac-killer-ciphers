package com.zodiackillerciphers.corpus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** how far apart are letters on average?   http://www.zodiackillersite.com/viewtopic.php?t=4809&p=79071 */
public class LetterDistances extends CorpusBase {
	public static void compute() {
		System.out.println("Done word init.");
		SubstitutionMutualEvolve.initSources();
		System.out.println("Done source init.");
		CorpusBase.SHOW_INFO = false;
		long sources = 0;
		long textlength = 0;
		Map<Character, StatsWrapper> stats = new HashMap<Character, StatsWrapper>(); 
		Map<Character, Integer> positions = new HashMap<Character, Integer>();  
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			positions.clear();
			StringBuffer sb = flatten(tokens, false);
			for (int i=0; i<sb.length();i++) {
				char key = sb.charAt(i);
				StatsWrapper stat = stats.get(key);
				if (stat == null) {
					stat = new StatsWrapper();
					stats.put(key, stat);
				}
				Integer lastPos = positions.get(key);
				if (lastPos == null) {
				} else {
					int diff = i-lastPos;
					stat.addValue(diff);
				}
				positions.put(key, i);
			}
			textlength += sb.length();
			System.out.println(textlength);
			if (textlength > 100000000) go = false;
		}
		for (Character key : stats.keySet()) {
			System.out.println(key);
			stats.get(key).output();
		}
	}
	
	public static void main(String[] args) {
		compute();
	}

}
