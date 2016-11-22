package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RepeatedSubstringSearchResult {
	/** the pattern that repeats */
	public String sequence;
	/** the positions it appears in */
	public List<Integer> positions;
	/** probability score */
	public float score;
	/** if searching via combinations of letters from the cipher alphabet, this is the combination for this result */
	public String combination;

	public String dumpText;
	public String dumpBBCode;
	
	public RepeatedSubstringSearchResult() {
		dumpText = "";
		dumpBBCode = "";
	}
	
	/** compute the probability score using the given map of counts */
	public void computeScore(Map<Character, Integer> counts) {
		score = 1;
		
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<sequence.length(); i++) {
			char key = sequence.charAt(i);
			set.add(key);
			score *= counts.get(key);
		}

		//score = 1/(float) Math.pow(score, set.size());
		score = 1/score;
	}
	
	public void addPosition(int pos) {
		if (positions == null) positions = new ArrayList<Integer>();
		positions.add(pos);
	}
	
	public String toString(boolean bbcode) {
		if (bbcode) return dumpBBCode;
		return dumpText;
	}
	/** returns true if the number of distinct symbols within the sequence is equal to at least 2/3
	 * of the sequence length  
	 */
	public boolean isGood() {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<sequence.length(); i++) {
			set.add(sequence.charAt(i));
		}
		int min = 2*sequence.length()/3;
		if (min == 1) min = 2;
		
		return set.size() >= min;
	}
	
}
