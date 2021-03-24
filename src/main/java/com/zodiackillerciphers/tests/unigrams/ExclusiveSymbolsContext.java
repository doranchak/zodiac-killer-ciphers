package com.zodiackillerciphers.tests.unigrams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;

public class ExclusiveSymbolsContext {
	/** cipher to examine */
	public String cipher;
	/** grid width of cipher */
	public int width;
	/** number of rows to select */
	public int n;
	/** current selection of rows */
	public int[] rows;
	/** set of symbols exclusive to these rows */
	public String symbols;
	/** count of positions occupied by these symbols */
	public int positions;
	/** symbol count map used to calculate position count */
	public Map<Character, Integer> countMap;
	/** stats for randomizations for this many rows */
	public StatsWrapper statsSymbols;
	public StatsWrapper statsPositions;
	
	public ExclusiveSymbolsContext(String cipher, int width, int n) {
		this.cipher = cipher;
		this.width = width;
		this.n = n;
		rows = new int[n];
		for (int i=0; i<rows.length; i++) rows[i] = i;
		countMap = Ciphers.countMap(cipher);
	}
	
	public int rows() {
		return cipher.length() / width;
	}
	
	public int patternScore() {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int bestScore = 0;
		for (int i=1; i<rows.length; i++) {
			int diff = rows[i]-rows[i-1];
			Integer val = map.get(diff);
			if (val == null) val = 0;
			val++;
			map.put(diff,  val);
			bestScore = Math.max(bestScore,  val);
		}
		return bestScore;
	}
	public double sigmaSymbols() {
		if (statsSymbols.stats.getStandardDeviation() == 0) return 0;
		return (symbols.length()-statsSymbols.stats.getMean())/statsSymbols.stats.getStandardDeviation();
	}
	public double sigmaPositions() {
		if (statsPositions.stats.getStandardDeviation() == 0) return 0;
		return (positions-statsPositions.stats.getMean())/statsPositions.stats.getStandardDeviation();
	}

	public static String heading() {
		return "n	rows	symbols	count	mean	sigma	positions	mean	sigma	patternscore";	
	}
	public void processResults() {
		String result = "";
		result += n + "	";
		result += Arrays.toString(rows) + "	";
		symbols = UnigramTests.exclusiveSymbolsIn(cipher, width, rows);
		result += "=(\"" + symbols + "\")	";
		result += symbols.length() + "	";
		result += statsSymbols.stats.getMean() + "	";
		result += sigmaSymbols() + "	";
		positions = UnigramTests.exclusiveSymbolsIn(cipher, width, rows, countMap);
		result += positions + "	";
		result += statsPositions.stats.getMean() + "	";
		result += sigmaPositions() + "	";
		result += patternScore();
		System.out.println(result);
	}
}
