package com.zodiackillerciphers.pivots;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** a pivot (a pair of ngrams that intersect at the same position) */
public class Pivot {
	public static int W = 17;
	/** the ngram that repeats and intersects */
	public String ngram;
	/** the directions of each ngram */
	public Direction[] directions;
	/** the list of positions covered by both */
	public List<Integer> positions;

	public String toString() {
		String result = ngram.length() + ", " + ngram + ", " + dumpDirections() + ", "
				+ dumpPos() + ", " + js();
		return result;
	}
	
	/** return a score based on the given count map and cipher length.
	 * the idea is that pivots involving rare symbols are
	 * "more valuable" than ones involving common symbols.
	 */
	public long score(Map<Character, Integer> countMap) {
		long score = 1;
		//System.out.println("score for pivot " + ngram);
		for (int i=0; i<ngram.length() && i<4; i++) { // limit ngram length to 4, for fair comparisons against the original Z340
			char key = ngram.charAt(i);
			Integer val = countMap.get(key);
			//System.out.println("key " + key + " val " + val);
			if (val == null) {
				throw new IllegalArgumentException("No count for [" + key + "]");
			}
			score *= val;
		}
		return score;
	}
	
	public String js() {
		if (1==1) return "";
		String result = "";
		for (Integer pos : positions) {
			int row = pos/W;
			int col = pos%W;
			result += "darkenrc(" + row + "," + col + "); "; 
		}
		return result;
	}

	public String dumpDirections() {
		if (directions == null)
			return null;
		String result = "directions: ";
		for (Direction d : directions)
			result += d + " ";
		return result;
	}

	public String dumpPos() {
		if (positions == null)
			return null;
		String result = "positions: ";
		for (Integer i : positions)
			result += i + " ";
		return result;
	}
	
	/** returns true if the given pivot shares any positions with this pivot */
	public boolean overlaps(Pivot pivot) {
		if (pivot == null) return false;
		if (pivot.positions == null) return false;
		for (Integer i : pivot.positions) if (this.positions.contains(i)) return true;
		return false;
	}
	
	/** returns true if this pivot is a substring of the given pivot */
	public boolean substringOf(Pivot pivot) {
		if (!pivot.ngram.startsWith(ngram)) return false;
		for (Integer pos : positions)
			if (!pivot.positions.contains(pos)) return false;
		
		return true;
	}
	
	public String highlight() {
		String js = "pivot ";
		for (int pos : positions) {
			js += "darkenpos(" + pos + "); ";
		}
		return js;
	}
}
