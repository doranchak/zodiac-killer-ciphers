package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.Z340Solution;
import com.zodiackillerciphers.lucene.NGramsCSRA;

public class Z13Z32UsingZ340Key {
	
	// note: N decodes to same letter in each. (E)
	// D decodes to same letter in each. (N) 
	// z decodes to same letter in each (E)
	//static Set<String> seen;
	static Map<Character, Character> z408Key = Ciphers.decoderMapFor(Ciphers.Z408, Ciphers.Z408_SOLUTION.toUpperCase());
	static Map<Character, Character> z340Key = Z340Solution.z340SolutionKey();
	static String tab = "	";
	static List<Map<Character, Character>> keys = new ArrayList<Map<Character, Character>>();
	static {
		keys.add(z408Key);
		keys.add(z340Key);
	}
	/** decode z13 with z408 and z340 keys */
	public static void decodeZ13() {
		String z = Ciphers.Z13;
		for (int i=0; i<2; i++)
			System.out.println(Ciphers.decode(z, keys.get(i)));
	}
	/** decode given cipher with every possible mix of z408 and z340 keys */
	public static void decodeMix(String cipher) {
		//seen = new HashSet<String>();
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		
		Map<Character, Integer> selections = new HashMap<Character, Integer>();
		String alpha = Ciphers.alphabet(cipher);
		for (int i=0; i<alpha.length(); i++) {
			selections.put(alpha.charAt(i), 0);
		}
//		System.out.println(cipher);
//		System.out.println(alpha);
		decodeMix(cipher, alpha, selections, 0);
	}
	public static void decodeMix(String cipher, String alphabet, Map<Character, Integer> selections, int alphabetIndex) {
		//System.out.println("decodeMix " + alphabetIndex + " " + selections + " " + alphabetIndex);
		if (alphabetIndex == alphabet.length()) {
			StringBuffer pt = new StringBuffer();
			for (int i=0; i<cipher.length(); i++) {
				char c = cipher.charAt(i);
				Character p = keys.get(selections.get(c)).get(c);
				if (p == null) p = ' ';
				pt.append(p);
			}
			//if (seen.contains(pt.toString())) return;
			//seen.add(pt.toString());
			float zk1 = NGramsCSRA.zkscore(new StringBuffer(pt.toString().replaceAll(" ", "")), false, false, "EN", false);
			float zk2 = NGramsCSRA.zkscore(pt, false, false, "EN", true);
			System.out.println(zk1 + tab + zk2 + tab + pt);
			return;
		}
		char a = alphabet.charAt(alphabetIndex);
		
		selections.put(a, 0);
		decodeMix(cipher, alphabet, selections, alphabetIndex+1);
		if (a == 'D' || a == 'N' || a == 'z') return; // these are the same for both keys.
		selections.put(a, 1);
		decodeMix(cipher, alphabet, selections, alphabetIndex+1);
	}
	
	
	/** decode z32 with z408 and z340 keys */
	public static void decodeZ32() {
		String z = Ciphers.Z32;
		for (int i=0; i<2; i++)
			System.out.println(Ciphers.decode(z, keys.get(i)));
	}
	/** decode z32 with z408 and z340 keys */
	public static void decodeZ408() {
		String z = Ciphers.Z408;
		for (int i=0; i<2; i++)
			System.out.println(Ciphers.decode(z, keys.get(i)));
	}
	public static void main(String[] args) {
//		decodeZ13();
//		decodeZ32();
//		decodeZ408();
		//decodeMix(Ciphers.Z13);
		decodeMix(Ciphers.Z32);
	}
}
