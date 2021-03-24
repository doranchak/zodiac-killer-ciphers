package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.old.CombinationGenerator;

/** tests related to Nathaniel Thornburg's primer ideas */
public class Primer {
	public static Map<Character, Integer> lastColumns(String cipher, int width, String commonCipher, boolean first) {
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		
		Set<Character> set = new HashSet<Character>();
		if (commonCipher != null) 
			for (int i=0; i<commonCipher.length(); i++) set.add(commonCipher.charAt(i));
		
		for (int i=0; i<cipher.length(); i++) {
			int col = i % width;
			Character key = cipher.charAt(first ? cipher.length()-i-1: i);
			if (commonCipher == null || set.contains(key)) map.put(key, col);
		}
		return map;
	}
	
	public static void testLast() {
		Map<Character, Integer> map = lastColumns(Ciphers.cipher[0].cipher, 17, Ciphers.cipher[1].cipher, true);
		Map<Integer, List<Character>> chars = new HashMap<Integer, List<Character>>();

		for (Character key : map.keySet()) {
			System.out.println(key + ": " + map.get(key));
			Integer col = map.get(key);
			List<Character> list = chars.get(col);
			if (list==null) list = new ArrayList<Character>();
			list.add(key);
			chars.put(col, list);
		}		
		
		for (Integer i : chars.keySet()) {
			List<Character> ch = chars.get(i);
			String s = i + ": ";
			for (Character c : ch) s += c;
			System.out.println(s);
		}
		
		//if (1==1) return;
		
		int[] elements = new int[17];
		for (int i=0; i<17; i++) elements[i] = i;
		
		int[] indices;
		int len = 12;
		CombinationGenerator x = new CombinationGenerator (elements.length, len);
		StringBuffer combination;
		int count = 0;
		long combinations = 0;
		while (x.hasMore ()) {
		  int product = 1;	
		  combination = new StringBuffer ();
		  indices = x.getNext ();
		  for (int i = 0; i < indices.length; i++) {
			int size = 0;
			if (chars.get(elements[indices[i]]) != null) size = chars.get(elements[indices[i]]).size();
		    combination.append (elements[indices[i]]).append(" [").append(size).append("] ");
		    if (size > 0) product*=size;
		  }
		  System.out.println (combination.toString () + ", " + product);
		  count++;
		  combinations += product;
		}	
		System.out.println(count + " column combinations, total symbol combinations: " + combinations);
		
	}
	
	
	public static void main(String[] args) {
		testLast();
	}
}
