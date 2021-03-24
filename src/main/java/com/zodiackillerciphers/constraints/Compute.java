package com.zodiackillerciphers.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.tests.LetterFrequencies;

public class Compute {
	/** Obtain all substrings of the given cipher that meet the following conditions:
	 * 1) The first symbol repeats somewhere else in the substring
	 * 2) The last symbol repeats somewhere else in the substring
	 *
	 * Calculate an estimated probability for a sample of plaintext to fit into the substring without violating the constraint
	 * imposed by the repeated symbols.
	 * 
	 * Exclude any constraints whose estimated probability exceeds the given maximum
	 * 
	 * Display the substring and the calculation.
	 * 
	 * Return a map linking constraint length to list of all constraints of that length.
	 *  
	 *  
	 */
	
	public static Map<Integer, List<Info>> constraints(String cipher, String solution, int maxLength, float maxProbability) {
		
		Map<Integer, List<Info>> map = new HashMap<Integer, List<Info>>();
		int total = 0;
		
		for (int i=0; i<cipher.length()-1; i++) {
			for (int j=i+1; j<cipher.length() && (j-i+1) <= maxLength; j++) {
				String sub = cipher.substring(i,j+1);
				Info info = new Info(sub, i, true);
				if (solution != null) info.plaintext = solution.substring(i,j+1); // the actual solution
				Integer val = info.counts.get(sub.charAt(0));
				if (val < 2) continue;
				val = info.counts.get(sub.charAt(sub.length()-1));
				if (val < 2) continue;
				
				//float score = score(sub, counts);
				if (info.substring.length() > maxLength) continue;
				if (info.probability > maxProbability) continue;
				
				
				System.out.println("Constraint: " + info);
				
				int key = info.substring.length();
				List<Info> list = map.get(key);
				if (list == null) list = new ArrayList<Info>();
				list.add(info);
				map.put(key, list);
				total++;
			}
		}
		System.out.println("Map contains " + total + " candidate snippets.");
		return map;
	}
	
	
	public static void main(String[] args) {
		Cipher c = Ciphers.cipher[Integer.valueOf(args[0])];
		constraints(c.cipher, c.solution, Integer.valueOf(args[1]), Float.valueOf(args[2]));
	}
	
}
