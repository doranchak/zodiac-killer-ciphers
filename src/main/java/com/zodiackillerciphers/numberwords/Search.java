package com.zodiackillerciphers.numberwords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.dictionary.WordFrequencies;

/** investigating numbers that generate words.  example: pi => 3 1 4 15 9 26 => CADOIZ => ZODIAC */ 
public class Search {

	
	static Map<String, List<String>> anagramMap;
	static {
		WordFrequencies.init();
		anagramMap = Anagrams.anagramMap(WordFrequencies.map.keySet());
		int max = 0;
		for (String key : anagramMap.keySet()) {
			max = Math.max(max, anagramMap.get(key).size());
			//if (anagramMap.get(key).size() > 7) {
			//	System.out.println(key  + ", " + Anagrams.toString(anagramMap.get(key)));
			//}
		}
		//Anagrams.dump(anagramMap);
		//System.out.println(max);
	}
	
	public static char charFor(int number) {
		return (char) (96 + number);
	}
	
	public static int numFor(char ch) {
		return ((int) ch) - 96;
	}
	public static int search(String numbers) {
		System.out.println("-- Searching for words in " + numbers + " --");
		List<Character> string = new ArrayList<Character>();
		return search(numbers, string);
	}
	
	public static void dump(List<Character> string) {
		String s = "";
		String n = "";
		for (Character c : string) {
			s += c;
			n += numFor(c) + " ";
		}
		String key = s.toUpperCase();
		char[] keyarray = key.toCharArray();
		Arrays.sort(keyarray);
		String sorted = new String(keyarray);
		List<String> anagrams = anagramMap.get(sorted);
		String a;
		if (anagrams == null || anagrams.size() == 0) {
			System.out.println(s + ", " + n + ", N/A");
			return; //a = "No anagrams found";
		}
		//a = Anagrams.toString(anagrams);
		//System.out.println(s + ", " + n + ", " + a);
		for (String anagram : anagrams) {
			System.out.println(s + ", " + n + ", " + anagram + ", " + WordFrequencies.freq(anagram));
		}
	}
	
	public static int search(String numbers,List<Character> string) {
		
		if (numbers == null || numbers.length() == 0) {
			dump(string);
			return 1;
		}
		
		String chunk = "";
		int total = 0;
		for (int i=0; i<numbers.length(); i++) {
			chunk += numbers.charAt(i);
			int num = Integer.valueOf(chunk);
			if (num > 26) break;
			if (num == 0) continue; // ignore 0's for now.  TODO: start alphabet at 0 instead of 1.
			//System.out.println(chunk + "," + num + "," + charFor(num));
			string.add(charFor(num));
			total += search(numbers.substring(i+1), string);
			string.remove(string.size()-1);
		}
		return total;
	}
	
	
	
	public static void main(String[] args) {
		search("314159260");
		// TODO: find candidate words within string
	}
}
