package com.zodiackillerciphers.ngrams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PeriodBean {
	
	/** the period */
	public int period;
	/** ngram length */
	public int n;
	/** counts of all ngrams with the given period */
	public Map<String, Integer> counts;
	
	/** set of all positions that are involved in repeated ngrams of the given period */
	public Set<Integer> positions;
	
	/** rewritten cipher text */
	public String rewritten;
	
	public PeriodBean() {
		counts = new HashMap<String, Integer>();
		positions = new HashSet<Integer>();
	}
	public String toString(boolean javascript) {
		int repeats = 0;
		String str = "";
		for (String key : counts.keySet()) {
			Integer val = counts.get(key); 
			if (val > 1) {
				str += key + "(" + val + ") ";
				repeats += val - 1;
			}
		}
		return repeats + "	" + (period+1) + "	" + str + " " + (javascript ? js() : positions);
		
	}
	
	public int repeats() {
		int repeats = 0;
		for (String key : counts.keySet()) {
			Integer val = counts.get(key); 
			if (val > 1) {
				repeats += val - 1;
			}
		}
		return repeats;
	}
	
	public String js() {
		String js = "";
		for (Integer pos : positions) {
			int row = pos/17;
			int col = pos%17;
			js += "darkenrc(" + row + ","+col+"); ";
		}
		return js;
	}
	
	public String toString() {
		return toString(false);
	}
	
}
