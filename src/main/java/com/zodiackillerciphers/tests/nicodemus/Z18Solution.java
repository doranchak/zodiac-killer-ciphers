package com.zodiackillerciphers.tests.nicodemus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.transform.CipherTransformations;

/** https://www.tapatalk.com/groups/zodiackillerfr/zodiac-ciphers-seminar-at-forensic-sciences-confer-t10414-s10.html#p213186 */
public class Z18Solution {
	static String tab = " ";
	public static void shuffleTest(int shuffles) {
		String Z18 = "EBEORIETEMETHHPITI";
		process(Z18);
		for (int i=0; i<shuffles; i++) {
			Z18 = CipherTransformations.shuffle(Z18);
			process(Z18);
		}
			
	}
	
	public static void process(String str) {
		/** compute distinct non-overlapping pairs */
		Set<String> pairs = new HashSet<String>();
		/** track counts of start and end letters */
		Map<String, Integer> countStart = new HashMap<String, Integer>(); 
		Map<String, Integer> countEnd = new HashMap<String, Integer>(); 

		for (int i=0; i<str.length(); i+=2) {
			String pair = str.substring(i,i+2);
			if (!pairs.contains(pair)) {
				add(pair.charAt(0), countStart);
				add(pair.charAt(1), countEnd);
			}
			pairs.add(pair);
		}
		
		String maxStart = max(countStart);
		String maxEnd = max(countEnd);
		int maxStartCount = countStart.get(maxStart);
		int maxEndCount = countEnd.get(maxEnd);
		int leftover = pairs.size() - maxStartCount - maxEndCount;
		
		System.out.println(pairs.size() + tab + maxStart + tab + maxStartCount + tab + maxEnd + tab + maxEndCount + tab + leftover + tab + str);
		
	}
	
	static void add(char ch, Map<String, Integer> counts) {
		String key = "" + ch;
		Integer val = counts.get(key);
		if (val == null) val = 0;
		val++;
		counts.put(key, val);
	}
	
	static String max(Map<String, Integer> counts) {
		int max = Integer.MIN_VALUE;
		String maxKey = null;
		for (String key : counts.keySet()) {
			int val = counts.get(key);
			if (val > max) {
				maxKey = key;
				max = val;
			}
		}
		return maxKey;
	}
	public static void test() {
		process("EBEORIETEMETHHPITI");
	}
	
	public static void main(String[] args) {
		test();
//		shuffleTest(10000);
	}
}
