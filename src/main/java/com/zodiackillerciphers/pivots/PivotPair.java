package com.zodiackillerciphers.pivots;

import java.util.Map;

/** represents a pair of pivots pointing in the same directions */
public class PivotPair {
	/** the pair of pivots */
	public Pivot[] pivots;
	
	public PivotPair(Pivot[] pivots) {
		super();
		this.pivots = pivots;
	}

	/** return the directions */
	public Direction[] directions() {
		if (pivots == null || pivots.length == 0) return null;
		return pivots[0].directions;
	}
	
	public long score(Map<Character, Integer> countMap) {
		long score = 1;
		if (pivots == null) return score;
		for (Pivot p : pivots) score *= p.score(countMap);
		return score;
	}
	
	public String toString() {
		if (pivots == null) return null;
		String s = "";
		for (Pivot p : pivots) s += p + " ";
		return s;
	}
	
}
