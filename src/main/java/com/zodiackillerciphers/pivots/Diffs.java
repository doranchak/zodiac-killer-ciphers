package com.zodiackillerciphers.pivots;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ngrams.NGramsBean;

public class Diffs {
	/** maps bigram to the change to its count */
	public Map<String, Integer> map;
	public Diffs() {
		map = new HashMap<String, Integer>();
	}
	
	public void add(String bigram) {
		if (bigram == null) return;
		Integer val = map.get(bigram);
		if (val == null) val = 0;
		val++;
		map.put(bigram, val);
	}
	public void remove(String bigram) {
		if (bigram == null) return;
		Integer val = map.get(bigram);
		if (val == null) val = 0;
		val--;
		map.put(bigram, val);
	}
}
