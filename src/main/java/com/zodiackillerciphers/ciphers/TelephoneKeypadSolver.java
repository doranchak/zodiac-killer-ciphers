package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;

public class TelephoneKeypadSolver {
	
	public static Map<Character, Integer> encoder;
	
	static {
		encoder = new HashMap<Character, Integer>();
		encoder.put('A', 2);
		encoder.put('B', 2);
		encoder.put('C', 2);
		encoder.put('D', 3);
		encoder.put('E', 3);
		encoder.put('F', 3);
		encoder.put('G', 4);
		encoder.put('H', 4);
		encoder.put('I', 4);
		encoder.put('J', 5);
		encoder.put('K', 5);
		encoder.put('L', 5);
		encoder.put('M', 6);
		encoder.put('N', 6);
		encoder.put('O', 6);
		encoder.put('P', 7);
		encoder.put('Q', 7);
		encoder.put('R', 7);
		encoder.put('S', 7);
		encoder.put('T', 8);
		encoder.put('U', 8);
		encoder.put('V', 8);
		encoder.put('W', 9);
		encoder.put('X', 9);
		encoder.put('Y', 9);
		encoder.put('Z', 9);		
	}
	
	static class ResultBean {
		public String word;
		public int frequency;
	};
	
	public static void wordsFor(String digits) {
		List<ResultBean> results = new ArrayList<ResultBean>(); 
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != digits.length()) continue;
			if (encode(word).equals(digits)) {
				ResultBean bean = new TelephoneKeypadSolver.ResultBean();
				bean.word = word;
				bean.frequency = WordFrequencies.freq(word);
				results.add(bean);
			}
		}
		Collections.sort(results, new Comparator<ResultBean>() {
			@Override
			public int compare(ResultBean o1, ResultBean o2) {
				// TODO Auto-generated method stub
				return Integer.compare(o2.frequency, o1.frequency);
			}
		});
		
		for (ResultBean bean : results) {
			System.out.println(bean.frequency + " " + bean.word);
		}
	}
	
	public static String encode(String word) {
		String result = "";
		for (int i=0; i<word.length(); i++) result += encoder.get(word.charAt(i));
		return result;
	}
	
	public static int[] convertStringToIntArray(String digits) {
	    int[] intArray = new int[digits.length()];
	    for (int i = 0; i < digits.length(); i++) {
	        intArray[i] = Character.getNumericValue(digits.charAt(i));
	    }
	    return intArray;
	}

	public static void process(String digits) {
		digits = digits.replaceAll("[^0-9]", "");		
		System.out.println("===============================");
		System.out.println("==== INPUT: " + digits + " ====");
		System.out.println("===============================");
		for (int i=digits.length(); i>0; i--) {
			System.out.println("==== LENGTH: " + i + " ====");
			for (int j=0; j<digits.length()-i+1; j++) {
				String sub = digits.substring(j, j+i);
				System.out.println("[DIGITS: " + sub + "]");
				System.out.println();
				wordsFor(sub);
				System.out.println();
			}
		}
	}
	public static void main(String[] args) {
		WordFrequencies.init();
		process(args[0]);
	}
}
