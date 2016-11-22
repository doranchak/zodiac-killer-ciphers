package com.zodiackillerciphers.dictionary;

import java.util.Comparator;

public class WordFrequencyComparator implements Comparator<String> {
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		return Integer.compare(WordFrequencies.freq(o2),
				WordFrequencies.freq(o1));
	}

}
