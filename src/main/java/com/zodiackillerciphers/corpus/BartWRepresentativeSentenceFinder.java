package com.zodiackillerciphers.corpus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.old.MyNameIs;
import com.zodiackillerciphers.tests.unicity.PlaintextBean;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

public class BartWRepresentativeSentenceFinder extends CorpusBase {
	// https://www.facebook.com/groups/1903970982969730/permalink/3380924855274328/
	// Find sample sentences that have letter frequencies very close to those of english  
	public static void search(int maxLength) {
		WordFrequencies.init();
		CorpusBase.REDDIT_ONLY = false;
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		boolean go = true;
		Map<Integer, Float> minDistancesByLength = new HashMap<Integer, Float>(); 
		String tab = "	";
		System.out.println(com.zodiackillerciphers.tests.LetterFrequencies.frequencyMapEnglish);
		while (go) {
			go = !SubstitutionMutualEvolve.randomSource();
			for (int i=0; i<tokens.length; i++) {
				String withSpaces = "";
				String withoutSpaces = "";
				for (int j=i+1; j<tokens.length; j++) {
					if (withSpaces.length() > 0) withSpaces += " ";
					withSpaces += tokens[j];
					withoutSpaces += tokens[j];
					if (withoutSpaces.length() > maxLength) break;
					Map<Character, Float> freq = com.zodiackillerciphers.tests.LetterFrequencies.frequenciesFor(withoutSpaces);
					float distance = com.zodiackillerciphers.tests.LetterFrequencies
							.distance(com.zodiackillerciphers.tests.LetterFrequencies.frequencyMapEnglish, freq);
					Float val = minDistancesByLength.get(withoutSpaces.length());
					if (val == null) val = 10f;
					if (distance < val) {
						minDistancesByLength.put(withoutSpaces.length(), distance);
						System.out.println(distance + tab + withoutSpaces.length() + tab + withSpaces + tab + freq);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		search(300);
	}

}
