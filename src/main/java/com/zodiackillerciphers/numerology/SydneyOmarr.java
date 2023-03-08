package com.zodiackillerciphers.numerology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;

public class SydneyOmarr {

	public static String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	// omarr equivalents for a digit 
	public static Map<Integer, String> equivalents;
	static {
		equivalents = new HashMap<Integer, String>();
		for (int i=0; i<alpha.length(); i++) {
			int key = (i % 9) + 1;
			String val = equivalents.get(key);
			if (val == null) val = "";
			val += alpha.charAt(i);
			equivalents.put(key, val);
		}
	}
	
	/** return all sydney omarr style decipherments for the given numerical sequence */  
	public static List<String> omarr(int[] sequence) {
		List<String> list = new ArrayList<String>();
		omarr(sequence, list, 0, new StringBuffer());
		return list;
	}
	
	static void omarr(int[] sequence, List<String> list, int index, StringBuffer currentWord) {
		if (index == sequence.length) {
			list.add(currentWord.toString());
			return;
		}
		String equivs = equivalents.get(sequence[index]);
		for (int i=0; i<equivs.length(); i++) {
			currentWord.append(equivs.charAt(i));
			omarr(sequence, list, index+1, currentWord);
			currentWord.deleteCharAt(currentWord.length()-1);
		}
		
	}
	
	public static void process(int[] sequence) {
		System.out.println("=========== sequence " + Arrays.toString(sequence));
		List<String> list = omarr(sequence);
		for (String word : list) {
			System.out.println(WordFrequencies.percentile(word) + "	" + word);
		}
	}

	public static void test() {
		WordFrequencies.init();
		System.out.println(equivalents);
		process(new int[] {7,9,4,5});
		process(new int[] {1,5,3,6});
		process(new int[] {3,7});
		process(new int[] {2,6});
		process(new int[] {2,5,3,3,9});
		process(new int[] {4,1,4,5});
	}
	public static void main(String[] args) {
		test();
	}
}
