package com.zodiackillerciphers.ngrams;

import java.util.HashSet;
import java.util.Set;

public class Positions {

	/** the ngram */
	public String ngram;
	
	/** the start positions of each occurrence of the ngram */
	public Set<Integer> startPositions;
	
	/** every position covered by each non-wildcard symbol of all occurences of the ngrams */
	public Set<Integer> coveredPositions;
	
	public Positions(String ngram) {
		this.ngram = ngram;
		startPositions = new HashSet<Integer>();
		coveredPositions = new HashSet<Integer>();
	}
	
	// add an occurrence of an ngram.  ignore it if it overlaps an existing occurrence.
	public void add(int startPosition) {
		if (startPositions.contains(startPosition)) {
			//System.out.println(ngram + " already had start at " + startPosition);
			return; // ignore duplicates
		}
		
		// does this new occurrence overlap an existing occurrence?
		Set<Integer> newCoveredPositions = new HashSet<Integer>(); 
		for (int i=0; i<ngram.length(); i++) {
			if (ngram.charAt(i) == '?') continue;
			newCoveredPositions.add(startPosition+i);
			if (coveredPositions.contains(startPosition+i)) {
				//System.out.println(ngram + " at pos " + (startPosition + i)
				//		+ " already covered " + toString());
				return;
			}
		}
		
		// no overlap, so add the new position, and its covered positions;
		//System.out.println(ngram + " adding new start " + startPosition + " covered " + newCoveredPositions);
		startPositions.add(startPosition);
		coveredPositions.addAll(newCoveredPositions);
	}
	
	// number of occurrences
	public int count() {
		return startPositions.size();
	}
	
	// return the ratio of unseen positions to seen positions
	public double unseenRatio(Set<Integer> seen) {
		double result = 0;
		for (Integer pos : coveredPositions) {
			if (seen.contains(pos)) continue;
			result++;
		}
		result /= coveredPositions.size();
		return result;
	}
	
	public String toString() {
		String result = "ngram " + ngram + " count " + count() + " starts " + startPositions + " covered " + coveredPositions;
		return result;
		
	}
	
}
