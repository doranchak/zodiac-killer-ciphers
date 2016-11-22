package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;

public class RepeatingFragmentsBean {
	/** key: fragment pattern.  value: all distinct positions where the patterns occur. */
	public Map<String, Set<Integer>> counts;
	/** list of results */
	public List<RepeatingFragmentsBeanEntry> beans;
	/** cipher length */
	int cipherLength;
	
	/** cipher symbol frequencies */
	Map<Character, Integer> cipherSymbolFrequencies;
	
	public RepeatingFragmentsBean(String cipher) {
		counts = new HashMap<String, Set<Integer>>();
		cipherSymbolFrequencies = Ciphers.countMap(cipher);
		beans = new ArrayList<RepeatingFragmentsBeanEntry>();
		cipherLength = cipher.length();
	}
	
	/** compute probability of the given pattern */
	public double probability(String key) {
		//int q = 1;
		//for (int i=0; i<key.length(); i++) if (key.charAt(i) == '?') q++;
		
		if (counts.containsKey(key)) {
			double result = 1;
			for (int i=0; i<key.length(); i++) {
				char ch = key.charAt(i);
				if (ch == '?') continue; // wildcard
				result *= ((double)cipherSymbolFrequencies.get(ch)/cipherLength);
			}
			return Math.pow(result,  counts.get(key).size()); // raise to the power of the number of repeats 
		} else return 0;
	}
	
	/** return pattern with the lowest probability */
	public String rarest() {
		Double min = Double.MAX_VALUE;
		String rarest = null;
		for (String key : counts.keySet()) {
			double prob = probability(key);
			if (prob < min) {
				min = prob;
				rarest = key;
			}
		}
		return rarest;
	}
	
	public void map(String key, int pos) {
		Set<Integer> positions = counts.get(key);
		if (positions == null) positions = new HashSet<Integer>();
		positions.add(pos);
		counts.put(key,  positions);
		//System.out.println(key + " now has count " + count);
	}
	
	public void dump() {
		//for (String key : counts.keySet())
		//	System.out.println(key+" "+counts.get(key)+" "+probability(key));
		//System.out.println("Rarest: " + rarestString());
		for (RepeatingFragmentsBeanEntry bean : beans) {
			System.out.println(bean);
		}
	}
	
	public String info(String prefix) {
		String result = prefix + "	";
		result += rarestString() + "	" + counts.size() + "	" + average();
		return result;
	}
	
	/** return average probability */
	public double average() {
		double sum = 0;
		for (String key : counts.keySet()) 
			sum += probability(key);
		if (counts.size() == 0) return 0;
		return sum / counts.size();
	}

	
	
	public void makeBeans() {
		for (String key : counts.keySet()) {
			RepeatingFragmentsBeanEntry bean = new RepeatingFragmentsBeanEntry();
			bean.key = key;
			bean.count = counts.get(key).size();
			bean.probability = probability(key);
			Object[] a = counts.get(key).toArray();
			Arrays.sort(a);
			bean.positions = Arrays.toString(a);
			bean.positionsInt.addAll(counts.get(key));
			beans.add(bean);
		}
		//Collections.sort(beans);
		
	}
	
	public String rarestString() {
		String r = rarest();
		return probability(r) + "	" + r + "	" + counts.get(r);
		
	}
}
