package com.zodiackillerciphers.tests.kasiski;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.stats.StatsWrapper;

public class KasiskiResult {
	/** the repeating ngrams */
	List<NGramsBean> ngrams;
	/** factor counts by ngram length */
	Map<Integer, Map<Integer, Integer>> factorCountsByLength;
	/** factor counts by for any length > 0 */
	Map<Integer, Integer> factorCountsAll;
	/** factor counts by for any length > 1 */
	Map<Integer, Integer> factorCountsWithoutUnigrams;

	public void dump() {
		System.out.println("--- factorCountsByLength: ---");
		for (Integer n : factorCountsByLength.keySet()) {
			System.out.println(n);
			for (Integer factor : factorCountsByLength.get(n).keySet()) {
				System.out.println("	" + factor + "	" + factorCountsByLength.get(n).get(factor));
			}
		}
		System.out.println("--- factorCountsAll: ---");
		for (Integer factor : factorCountsAll.keySet()) {
			System.out.println(factor + "	" + factorCountsAll.get(factor));
		}
		System.out.println("--- factorCountsWithoutUnigrams: ---");
		for (Integer factor : factorCountsWithoutUnigrams.keySet()) {
			System.out.println(factor + "	" + factorCountsWithoutUnigrams.get(factor));
		}
	}
	
	public void addBean(NGramsBean bean) {
		if (ngrams == null) {
			ngrams = new ArrayList<NGramsBean>();
		}
		ngrams.add(bean);
	}
	
	public void incrementFactor(int factor, int n) {
		if (factorCountsByLength == null) 
			factorCountsByLength = new HashMap<Integer, Map<Integer, Integer>>();
		if (factorCountsAll == null) 
			factorCountsAll = new HashMap<Integer, Integer>();
		if (factorCountsWithoutUnigrams == null) 
			factorCountsWithoutUnigrams = new HashMap<Integer, Integer>();
		Map<Integer, Integer> map = factorCountsByLength.get(n);
		if (map == null) map = new HashMap<Integer, Integer>();
		factorCountsByLength.put(n,  map);
		inc(map, factor);
		inc(factorCountsAll, factor);
		if (n > 1) inc(factorCountsWithoutUnigrams, factor);
	}
	
	void inc(Map<Integer, Integer> map, int key) {
		Integer val = map.get(key);
		if (val == null) val = 0;
		val++;
		map.put(key, val);
	}

	@Override
	public String toString() {
		return "KasiskiResult [ngrams=" + ngrams + ", factorCountsByLength=" + factorCountsByLength
				+ ", factorCountsAll=" + factorCountsAll + ", factorCountsWithoutUnigrams="
				+ factorCountsWithoutUnigrams + "]";
	}
	
}
