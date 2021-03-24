package com.zodiackillerciphers.annealing.homophonic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;

public class NGramPool {
	
	public static Map<Integer, List<String>> ngramsList;
	public static Map<Integer, Set<String>> ngramsSet;
	static { init(); }
	
	static void init() {
		ngramsList = new HashMap<Integer, List<String>>();
		ngramsSet = new HashMap<Integer, Set<String>>();
		
		Map<Integer, Integer> limits = new HashMap<Integer, Integer>();
		limits.put(2, 100);
		limits.put(3, 1000);
		limits.put(4, 3250);
		limits.put(5, 20000);

		for (int n = 2; n <= 5; n++) {
			ngramsList.put(n, FileUtil.loadFrom(
					"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools2/zkdecrypto-1.2-newest/ZKDV12/language/eng/"
							+ n + "grams-sorted",
					limits.get(n)));
			ngramsSet.put(n, new HashSet<String>(ngramsList.get(n)));
		}
	}
	
	public static void testNgrams() {
		for (int n = 2; n <= 5; n++) {
			int hits = 0;
			int total = 0;
			String matches = "";
			for (int i=0; i<Ciphers.Z408_SOLUTION.length()-n+1; i++) {
				String sub = Ciphers.Z408_SOLUTION.substring(i,i+n).toUpperCase();
				if (ngramsSet.get(n).contains(sub)) {
					matches += sub + " ";
					hits++;
				}
				total++; 
			}
			System.out.println(matches);
			System.out.println(hits + " out of " + total);
			
		}
	}
	
	public static void main(String[] args) {
		testNgrams();
	}
	
}
