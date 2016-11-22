package com.zodiackillerciphers.old;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ngrams.NGrams;


public class CollapseHomophones {
	
	/** return given cipher with combined symbols, as determined by the given mapping */
	public static String collapsed(String cipher, Map<Character, Character> map) {
		StringBuffer result = new StringBuffer(cipher.length());
		for (int i=0; i<cipher.length(); i++) {
			Character c1 = cipher.charAt(i);
			Character c2 = map.get(c1);
			if (c2 == null) result.append(c1); else result.append(c2);
		}
		return result.toString();
	}
	public static void go(int which) {
		Set<Character> alphabet = new HashSet<Character>();
		
		String[] ciphers;
		if (which == 0) ciphers = Zodiac.cipher340;
		else ciphers = Zodiac.cipher408;
		
		for (int i=0; i<ciphers[0].length(); i++) alphabet.add(ciphers[0].charAt(i));
		String line = "";
		for (Character c : alphabet) line += c;
		System.out.println(line);
		
		Map<Character, Character> map = new HashMap<Character, Character>();
		
		// select every possible triplet of symbols and combine them.
		int[] totals = new int[2];
		int[] totalsOriginal = new int[2];
		NGrams.countNgrams(ciphers[0], 2, totalsOriginal);
		
		int[] max = new int[2];
		
		/** map n-gram count to 1) number of occurrences, 2) number of correct occurrences */
		Map<Integer, Integer[]> countsC = new HashMap<Integer, Integer[]>(); 
		Map<Integer, Integer[]> countsU = new HashMap<Integer, Integer[]>(); 
		
		int countTotal = 0;
		int countBetterCTotal = 0;
		int countBetterUTotal = 0;
		int countBetterCCorrect = 0;
		int countBetterUCorrect = 0;
		
		boolean same = false;
		
		for (Character c1 : alphabet) {
			for (Character c2 : alphabet) {
				for (Character c3 : alphabet) {
					countTotal++;
					
					map.clear();
					map.put(c2, c1);
					map.put(c3, c1);
					
					
					if (Zodiac.solutionKey.get(c1) == Zodiac.solutionKey.get(c2) && Zodiac.solutionKey.get(c2) == Zodiac.solutionKey.get(c3)) same = true;
					else same = false;
					NGrams.countNgrams(collapsed(ciphers[0], map), 2, totals);
					
					if (totals[0] >= totalsOriginal[0]) {
						countBetterCTotal++;
						if (same) countBetterCCorrect++;
					}
					if (totals[1] >= totalsOriginal[1]) {
						countBetterUTotal++;
						if (same) countBetterUCorrect++;
					}
					
					max[0] = Math.max(max[0], totals[0]);
					max[1] = Math.max(max[1], totals[1]);
					
					Integer[] val;
					
					val = countsC.get(totals[0]);
					if (val == null) val = new Integer[] {0,0};
					val[0]++;
					if (same) val[1]++;
					countsC.put(totals[0], val);

					val = countsU.get(totals[1]);
					if (val == null) val = new Integer[] {0,0};
					val[0]++;
					if (same) val[1]++;
					countsU.put(totals[1], val);
					
				}
			}
		}
		System.out.println("countTotal: " + countTotal + 
				" countBetterCTotal: " + countBetterCTotal + 
				" countBetterUTotal: " + countBetterUTotal + 
				" countBetterCCorrect: " + countBetterCCorrect + 
				" countBetterUCorrect: " + countBetterUCorrect); 

		for (Integer i : countsC.keySet())
			System.out.println("C: " + i + " " + countsC.get(i)[0] + " " + countsC.get(i)[1] + " " + (float) 100*countsC.get(i)[1]/countsC.get(i)[0]);
		for (Integer i : countsU.keySet())
			System.out.println("U: " + i + " " + countsU.get(i)[0] + " " + countsU.get(i)[1] + " " + (float) 100*countsU.get(i)[1]/countsU.get(i)[0]);
		
	}
	
	static void testCollapse() {
		Map<Character, Character> map = new HashMap<Character, Character>();
		map.put('A', 'Z');
		map.put('B', 'Z');
		map.put('C', 'Z');
		map.put('D', 'Z');
		System.out.println(Zodiac.cipher[0]);
		System.out.println(collapsed(Zodiac.cipher[0], map));
	}
	
	public static void main(String[] args) {
		go(1);
		//for (Character c : Zodiac.solutionKey.keySet())
//			System.out.println(c+": " + Zodiac.solutionKey.get(c));
		//testCollapse();
		//int[] totals = new int[2];
		//NGrams.countNgrams(Zodiac.cipher[1], 2, totals);
		//System.out.println(totals[0]+","+totals[1]);
	}
}
