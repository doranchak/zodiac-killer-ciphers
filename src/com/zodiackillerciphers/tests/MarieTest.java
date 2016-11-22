package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

/* http://www.zodiackillersite.com/viewtopic.php?f=81&t=2751 */
public class MarieTest {
	public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static void test() {
		String cipher = Ciphers.cipher[1].cipher;
		String plain = Ciphers.cipher[1].solution.toUpperCase();
		
		//System.out.println(cipher);
		//System.out.println(plain);
		
		Map<Character, Character> decoder = Ciphers.decoderMapFor(cipher, plain);
		System.out.println(decoder);
		
		String alphacipher = Ciphers.alphabet(cipher);
		String alphaplain = "";
		for (int i=0; i<alphacipher.length(); i++) alphaplain += decoder.get(alphacipher.charAt(i));
		measure(alphacipher, alphaplain);
		for (int i=0; i<100; i++) measure(CipherTransformations.shuffle(alphacipher), alphaplain);
	}
	
	public static void measure(String cipher, String plain) {
		System.out.println();
		System.out.println("cipher:" + cipher);
		System.out.println(" plain:" + plain);
		Map<Character, Integer> counts = new HashMap<Character, Integer>();
		
		int max = -1;
		char maxSymbol = 0;
		
		Map<Character, Character> decoder = Ciphers.decoderMapFor(cipher, plain);
		
		String sequences = "";
		for (Character c : decoder.keySet()) {
			Set<Character> seen = new HashSet<Character>();
			seen.add(c);
			Character p = null;
			String sequence = "" + c;
			while (alphabet.contains(""+c)) {
				if (!alphabet.contains("" + c))
					break;
				p = decoder.get(c);
				if (p == null)
					break;
				if (seen.contains(p))
					break;
				seen.add(p);
				sequence += p;
				c = p;
				//System.out.println(sequence);
			}
			if (alphabet.contains(""+sequence.charAt(0))) sequences += sequence + " ";
			if (p != null) {
				Integer count = counts.get(p);
				if (count == null) count = 0;
				count++;
				counts.put(p,  count);
				
				if (count > max) {
					max = count;
					maxSymbol = p;
				}
			}
		}
		System.out.println("sequences: " + sequences);
		System.out.println("totals for each terminating letter: " + counts);
	}
	
	public static void main(String[] args) {
		test();
	}
}
