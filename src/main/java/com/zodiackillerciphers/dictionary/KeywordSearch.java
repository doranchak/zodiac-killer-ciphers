package com.zodiackillerciphers.dictionary;

import java.util.LinkedHashSet;

public class KeywordSearch {
	
	public static String removeDuplicates(String input) {
        LinkedHashSet<Character> uniqueChars = new LinkedHashSet<>();
        
        for (char c : input.toCharArray()) {
            uniqueChars.add(c);
        }
        
        StringBuilder result = new StringBuilder();
        for (char c : uniqueChars) {
            result.append(c);
        }
        
        return result.toString();
    }
	
	public static void search(String keyword) {
		WordFrequencies.init();
		keyword = removeDuplicates(keyword.toUpperCase());
		for (String word : WordFrequencies.map.keySet()) {
			String uniq = removeDuplicates(word);
			if (uniq.equals(keyword)) {
				System.out.println(word + " = " + keyword);
			}
		}
	}
	
	public static void main(String[] args) {
		search(args[0]);
	}
}
