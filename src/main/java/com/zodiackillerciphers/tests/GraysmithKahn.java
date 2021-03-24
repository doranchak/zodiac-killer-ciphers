package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.ZodiacSymbols;

/** testing Graysmith's claim that Zodiac used a sample cipher alphabet developed by David Kahn */ 
public class GraysmithKahn {
	
	// Kahn's mappings
	public static Map<Character, Character> p2c = new HashMap<Character, Character>();
	public static Map<Character, Character> c2p = new HashMap<Character, Character>();
	public static String plain = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String cipher = "LBQACSRDTOFVMHWIJXGKYUNZEP";
	static {
		for (int i=0; i<plain.length(); i++) {
			p2c.put(plain.charAt(i), cipher.charAt(i));
			c2p.put(cipher.charAt(i), plain.charAt(i));
		}
	}
	
	public static void test(int trials) {
		
		// number of symbols to assign to each plain text letter [A,Z].
		int[] totals = new int[] {
			4,1,1,2,7,2,1,2,4,0,1,3,1,4,4,1,0,3,4,4,1,1,1,1,1,0	
		};
		
		Random rand = new Random();
		// alphabet of the 408
		String alphabet = Ciphers.alphabet[1];
		
		int total = 0; int total2 = 0;
		for (int i=0; i<trials; i++) {
			int hits1 = 0; int hits2 = 0;
			List<Character> list = new ArrayList<Character>();
			for (char ch : alphabet.toCharArray()) list.add(ch);
			
			String[] assignments = new String[totals.length];
			for (int j=0; j<assignments.length; j++) assignments[j] = "";
			
			int j=0;
			while (!list.isEmpty()) {
				while (assignments[j].length() >= totals[j]) j++;
				
				int r = rand.nextInt(list.size());
				assignments[j] += list.get(r);
				
				list.remove(r);
			}
			for (int k=0; k<totals.length; k++) {
				char letter = (char)(65+k);
				
				String line = letter + " : " + assignments[k];
				
				String interpretations = ZodiacSymbols.interpret(assignments[k]);
				
				line += ", " + interpretations;
				
				if (interpretations.contains(""+p2c.get(letter))) {
					line += ", HIT1";
					hits1++;
				}
				if (interpretations.contains(""+c2p.get(letter))) {
					line += ", HIT2";
					hits2++;
					
				}
				System.out.println(line);
			}
			System.out.println(hits1 + ", " + hits2 + ", " + (hits1+hits2) + ", trial " + i);
			total += hits1 + hits2;
			if (hits1 >= 3) total2++;
			
		}
		System.out.println("Trials: " + trials + ", total: " + total + ", total2: " + total2 + ", average: " + (((float)total))/trials);
	}
	
	public static void main(String[] args) {
		test(10000);
	}
}
