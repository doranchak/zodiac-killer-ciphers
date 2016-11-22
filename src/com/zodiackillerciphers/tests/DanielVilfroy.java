package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.transform.CipherTransformations;

/** Dan Vilfroy's grille observations */
public class DanielVilfroy {
	public static void test1() {
		/** 22 +'s, 315 other */
		String test = "++++++++++++++++++++++                                                                                                                                                                                                                                                                                                                           ";
		
		Map<Integer, Integer> overallCounts = new HashMap<Integer, Integer>();
		Map<Integer, Integer> matchingCounts = new HashMap<Integer, Integer>();
		for (int i=0; i<1000000; i++) {
			String shuffle;
			
			shuffle = CipherTransformations.shuffle(test);
			int count1 = count(shuffle);
			
			shuffle = CipherTransformations.shuffle(test);
			int count2 = count(shuffle);
			
			Integer val = overallCounts.get(count1);
			if (val == null) val = 0;
			val++;
			overallCounts.put(count1, val);
			
			val = overallCounts.get(count2);
			if (val == null) val = 0;
			val++;
			overallCounts.put(count2, val);
			
			if (count1 == count2) {
				val = matchingCounts.get(count1);
				if (val == null) val = 0;
				val++;
				matchingCounts.put(count1, val);
			}
			
		}
		
		System.out.println("Overall:");
		System.out.println(overallCounts);
		System.out.println("Matching:");
		System.out.println(matchingCounts);
		
	}
	public static int count(String cipher) {
		int count = 0;
		for (int i=0; i<22; i++) 
			if (cipher.charAt(i) == '+') count++;
		return count;
	}
	
	public static void main(String[] args) {
		test1();
	}
}
