package com.zodiackillerciphers.ciphers.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** different way to handle vigenere operations */ 
public class Vigenere2 {
	
	public static String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String ALPHA_BERENGER = "ABCDEFGHIJKLMNOPQRSTUVXYZ";
	
	// alphabet for this vigenere
	public String alphabet;
	// shift value (normal is 1)
	public int shift = 1;
	// tabula recta generated from the given alphabet
	public String[] tabulaRecta;
	// map chracters to integers 
	public Map<Character, Integer> charToInt;
	// map letters to all letter-pairs that can produce them via the tabula recta
	public Map<Character, Set<String>> pairs;
	public String name;

	public Vigenere2(String alphabet, int shift, String name) {
		if (shift != 1 && shift != -1)
			throw new IllegalArgumentException("Only supports shift of 1 or -1");
		this.alphabet = alphabet.toUpperCase();
		this.shift = shift;
		this.name = name;
		
		makeTabulaRecta();
		makePairs();
	}
	
	void makeTabulaRecta() {
		tabulaRecta = new String[alphabet.length()];
		charToInt = new HashMap<Character, Integer>();
		for (int i=0; i<alphabet.length(); i++) {
			tabulaRecta[i] = shift(alphabet, i);
			charToInt.put(alphabet.charAt(i), i);
		}
	}
	
	void makePairs() {
		pairs = new HashMap<Character, Set<String>>();
		for (int a=0; a<alphabet.length(); a++) {
			for (int b=0; b<alphabet.length(); b++) {
				char c1 = alphabet.charAt(a);
				char c2 = alphabet.charAt(b);
				char key = decode(c1, c2);
				Set<String> val = pairs.get(key);
				if (val == null) val= new HashSet<String>();
				val.add(""+c1+""+c2);
				pairs.put(key, val);
			}
			
		}
		//System.out.println(pairs);
	}
	
	public Character decode(char c1, char c2) {
		Integer row = charToInt.get(c1);
		Integer col = charToInt.get(c2);
		if (row == null || col == null) return null;
		return tabulaRecta[row].charAt(col);
	}
	
	public void dump() {
		System.out.println("name: " + name);
		System.out.println("alphabet: " + alphabet);
		for (String s : tabulaRecta) System.out.println("tabula recta " + s);
		System.out.println("charToInt: " + charToInt);
		System.out.println("pairs: " + pairs);
	}
	
	public String shift(String str, int amount) {
		String result = "";
		for (int i=0; i<str.length(); i++) {
			result += (str.charAt((i+amount*shift+str.length())%str.length()));
		}
		return result;
	}
	
	public static void main(String[] args) {
		Vigenere2 v = new Vigenere2(ALPHA_BERENGER, 1, "blah");
		v.dump();
	}
	

}
