package com.zodiackillerciphers.pivots.symmetries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** a pair of "legs" containing symmetrical symbols */
public class Pair {
	
	/** origin of this pair of legs */
	public int[] origin;
	/** symbols that match in corresponding positions of each leg */
	public List<Match> matches = new ArrayList<Match>();
	/** the two directions involved in this pair of matches */
	public int[][] directions;
	
	/** calculate a score we can use to compare matches.
	 * countMap is use to compute relative probabilities
	 * (for example, it's harder to make symmetries with 
	 * infrequently occurring symbols)
	 **/
	public Score score(Map<Character, Integer> countMap, int L) {
		Score score = new Score();
		score.probability = 1;
		score.minTotalDistance = 0;
		for (Match match : matches) {
			score.probability *= ((double)countMap.get(match.symbol))/L;
		}
		
		for (int i=0; i<matches.size()-1; i++) {
			Match m1 = matches.get(i);
			int min = Integer.MAX_VALUE;
			for (int j=i+1; j<matches.size(); j++) {
				Match m2 = matches.get(j);
				min = Math.min(min, Match.distance(m1, m2));
			}
			score.minTotalDistance += min;
		}
		return score;
	}
	public String d() {
		String p = "";
		for (int[] d : directions) p += Arrays.toString(d) + " ";
		return p;
	}
	
	public void dump(Map<Character, Integer> countMap, int L, int period) {
		if (matches.size() < 3) return;
		Score sc = score(countMap, L);
		if (matches.size() - sc.minTotalDistance < 0) return;
		String ngram = "";
		for (Match match : matches) ngram += match.symbol;
		System.out.println(sc.probability + " " + sc.minTotalDistance + " " + ngram + " period " + period + " size " + matches.size() + " origin " + Arrays.toString(origin) + " directions " + d() + " js " + javascript()); 
		for (Match match : matches) System.out.println(match);
	}
	
	public String darken(int[] point) {
		return "darkenrc(" + point[0] + "," + point[1] + "); ";
	}
	
	public String javascript() {
		String result = darken(origin);
		for (Match m : matches) {
			for (int[] point : m.positions) {
				result += darken(point);
			}
		}
		return result;
	}
	
}
