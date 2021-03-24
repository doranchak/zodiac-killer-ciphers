package com.zodiackillerciphers.ciphers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;

// http://www.reddit.com/r/codes/comments/1l4wfg/i_need_help_deciphering_this/
public class ArrowSquiggleCipher {
	
	static int[][] words = new int[][] {
			{1,2},
			{13,6,10,3,7,11},
			{5,3},
			{10,8,18,7,11},
			{5,4},
			{2,1},
			{12,9,3,4,7,8,4,8},
			{11,9,8,9,13,14,8,7,3,14,8,4},
			{4,10,12,15},
			{7,19},
			{13,14,8,7,4,16,6},
			{4,16,4},
			{1},
			{2,7,4},
			{3,5,4},
			{17,10,1},
			{4,1,2}	
		};
	static String[] tokens;
	static {
		tokens = new String[words.length];
		
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int a = 0; int b = 0;
		Map<Integer, Character> map = new HashMap<Integer, Character>();
		for (int[] word : words) {
			String token = "";
			for (int key : word) {
				Character val = map.get(key);
				if (val == null) val = alpha.charAt(a++);
				token += val;
				map.put(key,val);
			}
			tokens[b++] = token;
			System.out.println(token);
		}

	}
	
	public static void search() {
		WordFrequencies.init();
		// 11,9,8,9,13,14,8,7,3,14,8,4
		/*
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() < 12) continue;
			char ch1 = word.charAt(1); 
			char ch2 = word.charAt(3);
			
			char ch3 = word.charAt(2);
			char ch4 = word.charAt(6);
			char ch5 = word.charAt(10);
			
			char ch6 = word.charAt(5);
			char ch7 = word.charAt(9);
			
			if (ch1==ch2 && ch3==ch4 && ch4==ch5 && ch6==ch7) {
				System.out.println(WordFrequencies.freq(word) + ", " + word);
				
				
				
			}
			
		}*/

		/*
		//13,6,10,3,7,11
		//10,8,18,7,11
		// common letters: 7,10,11
		
		List<String> words1 = WordFrequencies.byLength.get(6);
		for (String word1 : words1) {
			List<String> words2 = WordFrequencies.byLength.get(5);
			for (String word2 : words2) {
				if (word1.charAt(4) != word2.charAt(3)) continue;
				if (word1.charAt(2) != word2.charAt(0)) continue;
				if (word1.charAt(5) != word2.charAt(4)) continue;
				
				//13,14,8,7,4,16,6
				List<String> words3 = WordFrequencies.byLength.get(7);
				for (String word3 : words3) {
					if (word1.charAt(0) != word3.charAt(0)) continue;
					if (word2.charAt(1) != word3.charAt(2)) continue;
					if (word1.charAt(4) != word3.charAt(3)) continue;
					if (word1.charAt(1) != word3.charAt(6)) continue;
				
					long f1 = WordFrequencies.freq(word1);
					long f2 = WordFrequencies.freq(word2);
					long f3 = WordFrequencies.freq(word3);
					
					System.out.println(f1*f2*f3 + ", " + word1 + ", " +word2 + ", " + word3);
				}
			}
			
			*/
		

		//12,9,3,4,7,8,4,8
		//0  1 2 3 4 5 6 7
		List<String> words1 = WordFrequencies.byLength.get(8);
		for (String word1 : words1) {
			if (word1.charAt(7-3) != word1.charAt(7-6)) continue;
			if (word1.charAt(7-5) != word1.charAt(7-7)) continue;
			
			//13,14,8,7,4,16,6   3 in common
			//0  1  2 3 4 5  6
			List<String> words2 = WordFrequencies.byLength.get(7);
			for (String word2 : words2) {
				if (word2.charAt(6-2) != word1.charAt(7-5)) continue;
				if (word2.charAt(6-3) != word1.charAt(7-4)) continue;
				if (word2.charAt(6-4) != word1.charAt(7-3)) continue;
				
				
				// we now have decoded:  3,4,6,7,8,9,12,13,14,16
				
				//13,6,10,3,7,11: 4
				//0  1 2  3 4 5
				List<String> words3 = WordFrequencies.byLength.get(6);
				for (String word3 : words3) {
					if (word3.charAt(5-0) != word2.charAt(6-0)) continue;
					if (word3.charAt(5-1) != word2.charAt(6-6)) continue;
					if (word3.charAt(5-3) != word1.charAt(7-2)) continue;
					if (word3.charAt(5-4) != word2.charAt(6-3)) continue;
					
					long f1 = WordFrequencies.freq(word1);
					long f2 = WordFrequencies.freq(word2);
					long f3 = WordFrequencies.freq(word3);
					long score = f1*f2*f3;
					System.out.println(score+", " + word1 + ", " + word2 + ", " + word3);
				}				
				
				
			}
			
		}
	
	}
		
		
		
	public static void main(String[] args) {
		search();
	}
}
