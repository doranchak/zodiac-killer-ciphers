package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;

public class ReAppear {
	
	/** re-appearance analysis.  for each position of the cipher, determine how far away the next repetition of that symbol is located.*/
	public static void measure(String cipher) {
		List<Integer> distances = new ArrayList<Integer>();
		for (int i=0; i<cipher.length(); i++) {
			char ch1 = cipher.charAt(i);
			boolean found = false;
			for (int j=i+1; j<cipher.length(); j++) {
				char ch2 = cipher.charAt(j);
				if (ch1 == ch2) {
					System.out.println(i+", "+j+", "+ch1 + ", " + (j-i) + ", " + ((float)(j-i))/(cipher.length()-i));
					distances.add(j-i);
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println(i+", N/A, "+ch1 + ", 0"); // "0" means N/A
				distances.add(null);
			}
			// note: how to highlight in cryptoscope:  hrange(49, 300); halphargb("H",150,200,150);

		}
		
		for (int i=0; i<distances.size()-2; i++) {
			
			int sum = 0;
			for (int j=0; j<3; j++) {
				if (distances.get(i+j) == null) {
					sum = Integer.MAX_VALUE;
					break;
				}
				else sum += distances.get(i+j);
			}
			System.out.println(i+", " + cipher.substring(i, i+3) + ", " + sum);
		}
	}
	
	public static float scoreUNUSED(String sub) {
		Map<Character, ReAppearBean> map = new HashMap<Character, ReAppearBean>();
		
		for (int i=0; i<sub.length(); i++) {
			Character key = sub.charAt(i);
			ReAppearBean val = map.get(key);
			if (val == null) val = new ReAppearBean();
			
			if (val.positionFirst == null) val.positionFirst = i;
			else if (val.positionSecond == null) val.positionSecond = i;
			
			map.put(key, val);
		}

		float score = 0;
		for (Character key : map.keySet()) {
			ReAppearBean val = map.get(key);
			//System.out.println("key " + key + " val " + val);
			if (val.positionSecond == null) val.positionSecond = sub.length()-1; 
			score += ((float)(val.positionSecond-val.positionFirst))/(Math.pow(2.0f, val.positionFirst));
		}
		return score;
	}
	
	public static void main(String[] args) {
		measure(Ciphers.cipher[Integer.valueOf(args[0])].cipher);
	}
}
