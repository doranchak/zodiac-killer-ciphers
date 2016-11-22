package com.zodiackillerciphers.constraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;

public class CipherSearch {
	/** find largest sections of cipher text that contain no more than the given number of unique symbols */
	public static void searchSections(String cipher, String solution, int numUniques) {
		List<CipherSearchResult> list = new ArrayList<CipherSearchResult>(); 
		for (int L=1; L<cipher.length(); L++) {
			for (int i=0; i<cipher.length()-L+1; i++) {
				String sub = cipher.substring(i, i+L);
				if (satisfied(sub, numUniques)) {
					CipherSearchResult c = new CipherSearchResult();
					c.substring = sub;
					if (solution != null) c.solution = solution.substring(i, i+L);
					c.pos = i;
					list.add(c);
				} else break;
			}
		}
		Collections.sort(list);
		int total = list.size();
		System.out.println("Top 100 results:");
		
		for (CipherSearchResult c : list) {
			total--;
			if (total >= 100) continue;
			if (numUniques < 27)
				System.out.println(c + ", " + alpha(c.substring));
			else
				System.out.println(c);
		}
	}
	
	public static boolean satisfied(String sub, int numUniques) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<sub.length(); i++) {
			char ch = sub.charAt(i);
			if (ch == ' ') continue; // ignore spaces
			set.add(ch);
			if (set.size() > numUniques) return false;
		}
		return true;
	}
	
	/** if the substring has no more than 26 unique symbols, convert it so it only uses symbols A through Z */
	public static String alpha(String sub) {
		Map<Character, Character> map = new HashMap<Character, Character>();
		String result = "";
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int pointer = 0;
		for (int i=0; i<sub.length(); i++) {
			Character key = sub.charAt(i);
			if (key == ' ') {
				result += " ";
				continue; // ignore spaces
			}
			Character val = map.get(key);
			if (val == null) {
				val = alphabet.charAt(pointer++);
			}
			result += val;
			map.put(key, val);
		}
		return result;
	}
	
	public static void main(String[] args) {
		searchSections(Ciphers.cipher[1].cipher, Ciphers.cipher[1].solution, 26);
		searchSections(Ciphers.cipher[0].cipher, null, 26);
		searchSections(Ciphers.cipherByDescription("Celebrity Cypher").cipher, null, 26);
	}
}


