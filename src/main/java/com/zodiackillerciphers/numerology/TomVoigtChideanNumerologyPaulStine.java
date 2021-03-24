package com.zodiackillerciphers.numerology;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.dictionary.WordFrequencies;

/** https://www.reddit.com/r/TheZodiac/comments/b3ynfp/zodiac_death_machine/ */
public class TomVoigtChideanNumerologyPaulStine {

	// http://www.professionalnumerology.com/chaldeansystem.html
	static Map<Integer, String> numberToLetters;
	static {
		numberToLetters = new HashMap<Integer, String>();
		numberToLetters.put(1, "AIJQY");
		numberToLetters.put(2, "BKR");
		numberToLetters.put(3, "CGLS");
		numberToLetters.put(4, "DMT");
		numberToLetters.put(5, "EHNX");
		numberToLetters.put(6, "UVW");
		numberToLetters.put(7, "OZ");
		numberToLetters.put(8, "FP");
	}
	/** 
	 * generate all words that can be produced from the given sequence of digits, using the Chidean number table.
	 * 
	 */
	public static void search(int[] digits) {
		WordFrequencies.init();
		String[] letters = new String[digits.length];
		for (int i=0; i<digits.length; i++) {
			letters[i] = numberToLetters.get(digits[i]);
		}
		//System.out.println(Arrays.toString(letters));
		search(digits, letters, 0, "");
	}
	
	public static void search(int[] digits, String[] letters, int index, String word) {
		if (index == digits.length) {
			anagram(word);
			return;
		}
		for (int i=0; i<letters[index].length(); i++) {
			char letter = letters[index].charAt(i);
			search(digits, letters, index+1, word + letter);
		}
	}
	
	public static void anagram(String str) {
		for (String word : WordFrequencies.byLength.get(str.length())) {
			if (Anagrams.anagram(word, str, true)) {
				System.out.println(WordFrequencies.freq(word) + " " + word + " " + str);
			}
		}
	}
	
	/** find all 5 digit license plate numbers that anagram to ZODIAC using these rules
	 * i.e., the plate has two 1s (each representing I and A) and a single 7 (allowed to represent both Z and O).
	 * or, the plate has a single 1 (allowed to represent both I and A) and two 7s (each representing Z and O) 
	 **/
	public static void plates() {
		for (int i=11111; i<=77777; i++) {
			String digits = "" + i;
			if (digits.contains("0")) continue;
			if (digits.contains("9")) continue;

			int[] counts = new int[] {
					count(digits, '0'),
					count(digits, '1'),
					count(digits, '2'),
					count(digits, '3'),
					count(digits, '4'),
					count(digits, '5'),
					count(digits, '6'),
					count(digits, '7'),
					count(digits, '8'),
					count(digits, '9')
			};
			
			if (counts[0] > 0 || counts[2] > 0 || counts[5] > 0 || counts[6] > 0 || counts[8] > 0 || counts[9] > 0) 
				continue;
			if (counts[3] > 1 || counts[4] > 1) continue;
			if ((counts[1] == 1 && counts[7] == 2) || (counts[1] == 2 && counts[7] == 1)) {
				System.out.println(digits + ", " + Arrays.toString(counts));
			}
		}
	}
	
	static int count(String digits, char digit) {
		int result = 0;
		for (int i=0; i<digits.length(); i++) {
			if (digits.charAt(i) == digit) result++;
		}
		return result;
	}
	
	public static void main(String[] args) {
		//search(new int[] {1, 7, 4, 1, 3});
//		search(new int[] {1, 1, 7, 4, 1, 3});
//		search(new int[] {7, 1, 7, 4, 1, 3});
//		search(new int[] {4, 1, 7, 4, 1, 3});
//		search(new int[] {3, 1, 7, 4, 1, 3});
		plates();
	}
}
