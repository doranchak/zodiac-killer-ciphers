package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotPair;

/** convenience methods for finding and highlighting patterns and features */
public class PatternsAndFeatures {
	/** return all positions covered by pivots */
	public static List<Integer> posPivots(String cipher) {
		List<Integer> list = new ArrayList<Integer>();
		CandidatePlaintext cp = new CandidatePlaintext();
		cp.plaintext = cipher;
		cp.criteriaHasPivots(); // make pivot pairs
		for (PivotPair pair : cp.pivotPairs) {
			for (Pivot pivot : pair.pivots) {
				if (pivot.positions != null)
					list.addAll(pivot.positions);
			}
		}
		return list;
	}

	/** return all positions covered by box corner pairs */
	public static List<Integer> posBoxCorners(String cipher) {
		List<Integer> pos = new ArrayList<Integer>();
		List<BoxCornerPair> list = CandidateConstraints.boxCornerPairs(cipher,
				false);
		for (BoxCornerPair pair : list) {
			if (pair.positions() != null)
				pos.addAll(pair.positions());
		}
		return pos;
	}
	
	/** return all positions covered by pseudo-words */
	public static List<Integer> posWords(String cipher) {
		Map<Integer, String> wordsMap = new HashMap<Integer, String>();
		CandidateKey.hasWords(cipher, wordsMap);
		return CandidateKey.wordPositions(wordsMap);
	}
	
	/** format the argument for hsl_random javascript function for the given lists of positions */
	public static String argsFor(List<Integer>... pos) {
		if (pos == null) return null;
		String result = "[";
		for (int i=0; i<pos.length; i++) {
			List<Integer> list = pos[i];			
			result += list;
			if (i < pos.length-1) result += ", ";
		}
		result += "]";
		return result;
	}
	public static String argsFor(List<List<Integer>> list) {
		if (list == null) return null;
		String result = "[";
		for (int i=0; i<list.size(); i++) {
			List<Integer> pos = list.get(i);
			result += pos;
			if (i<list.size()-1) result += ", "; 
		}
		result += "]";
		return result;
	}
	
	/** return lists of repeating ngram positions */
	public static List<List<Integer>> ngrams(String cipher, int n) {
		List<List<Integer>> list = new ArrayList<List<Integer>>();
		NGramsBean bean = new NGramsBean(n, cipher);
		for (String ngram : bean.repeats) {
			List<Integer> pos = new ArrayList<Integer>(); 
			for (Integer start : bean.positions.get(ngram)) {
				for (int i=0; i<n; i++) pos.add(start+i);
			}
			list.add(pos);
		}
		return list;
	}
	
	/** return lists of repeating ngrams at the given period */
	public static List<List<Integer>> periodNgrams(String cipher, int n, int period) {
		List<List<Integer>> list = new ArrayList<List<Integer>>();
		
		String re = Periods.rewrite3(cipher, period);
		Map<Integer, Integer> map = Periods.rewrite3Map(cipher, period);
		NGramsBean bean = new NGramsBean(n, re);
		for (String ngram : bean.repeats) {
			List<Integer> pos = new ArrayList<Integer>(); 
			for (Integer start : bean.positions.get(ngram)) {
				for (int i=0; i<n; i++) pos.add(map.get(start+i));
			}
			list.add(pos);
		}
		return list;
	}
	
	/** convert to mirrored positions (read right to left instead of left to right) */
	public static List<List<Integer>> mirror(List<List<Integer>> list) {
		List<List<Integer>> newList = new ArrayList<List<Integer>>();
		for (List<Integer> positions : list) {
			List<Integer> updated = new ArrayList<Integer>();
			for (Integer pos : positions) {
				int row = pos/17;
				int col = 17 - (pos % 17);
				updated.add(row*17 + col);
			}
			newList.add(updated);
		}
		return newList;
	}
	
	/** return positions covered by fold marks */
	public static List<Integer> posFoldMarks(String cipher) {
		List<Integer> list = new ArrayList<Integer>();
		if (cipher.charAt(153) == '-' && cipher.charAt(153) == cipher.charAt(169)) {
			list.add(153); list.add(169); return list;
		}
		if (cipher.charAt(170) == '-' && cipher.charAt(170) == cipher.charAt(186)) {
			list.add(170); list.add(186); return list;
		}
		return list;
	}
	
	

}
