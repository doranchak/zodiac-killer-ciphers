package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.NGramsBean;

/** find smalls trings that permit large variety of anagrammed words */
public class SonjaC {
	
	public static Set<String> words;
	static {
		words = new HashSet<String>();
		WordFrequencies.init();
		
		for (String word : WordFrequencies.map.keySet()) {
			String converted = FileUtil.convert(word).toString();
			if (converted.length() < 3 || converted.length() > 5) continue;
			words.add(converted);
		}
		System.out.println("Words: " + words.size());
	}
	
	public static void permute() {
		char[] array = new char[4];
		permute(array, 0);
	}
	public static void permute(char[] array, int position) {
		if (position == array.length) {
			process(new String(array));
			return;
		}
		for (int i=0; i<26; i++) {
			array[position] = (char) (65+i);
			permute(array, position+1);
		}
	}
	
	public static void process(String permutation) {
		NGramsBean bean1 = new NGramsBean(1, permutation);
		int count = 0;
		int freqsum = 0;

		/** remove a letter */
		int count1 = 0;
		int freqsum1 = 0;
		
		/** add a letter */
		int count2 = 0;
		int freqsum2 = 0;
		
		/** remove and add */
		int count3 = 0;
		int freqsum3 = 0;
		
		/** perfect anagram */
		int count4 = 0;
		int freqsum4 = 0;
		
		
		for (String word : words) {
			NGramsBean bean2 = new NGramsBean(1, word);
			int diff1 = NGramsBean.diff(bean1, bean2);
			int diff2 = NGramsBean.diff(bean2, bean1);
			if (diff1 < 2 && diff2 < 2) {
				//System.out.println(permutation + ", " + word + ", " + diff1 + ", " + diff2);
				count++;
				int freq = WordFrequencies.freq(word);
				freqsum+=freq;
				
				if (diff1 == 0 && diff2 == 1) {
					count2++;
					freqsum2+=freq;
				}
				else if (diff1 == 1 && diff2 == 0) {
					count1++;
					freqsum1+=freq;
				}
				else if (diff1 == 1 && diff2 == 1) {
					count3++;
					freqsum3+=freq;
				}
				else if (diff1 == 0 && diff2 == 0) {
					count4++;
					freqsum4+=freq;
				}
			}
			/* possibilities:
			 * keep permutation the same. (perfect anagram)
			 * remove a letter from the permutation. (permutation has error of +1)
			 * add a letter to the permutation. (permutation has error of -1)
			 * remove a letter from permutation, add a letter to permutation.
			 */
		}
		System.out.println(permutation + ", " + count + ", " + freqsum + ", " + (count*freqsum)
				 + ", " + count1 + ", " + freqsum1 + ", " + (count1*freqsum1)
				 + ", " + count2 + ", " + freqsum2 + ", " + (count2*freqsum2)
				 + ", " + count3 + ", " + freqsum3 + ", " + (count3*freqsum3)
				 + ", " + count4 + ", " + freqsum4 + ", " + (count4*freqsum4));
	}
	public static void process2(String permutation) {
		NGramsBean bean1 = new NGramsBean(1, permutation);
		int count = 0;
		int freqsum = 0;
		/** remove a letter */
		int count1 = 0;
		int freqsum1 = 0;
		
		/** add a letter */
		int count2 = 0;
		int freqsum2 = 0;
		
		/** remove and add */
		int count3 = 0;
		int freqsum3 = 0;
		
		/** perfect anagram */
		int count4 = 0;
		int freqsum4 = 0;
		for (String word : words) {
			NGramsBean bean2 = new NGramsBean(1, word);
			int diff1 = NGramsBean.diff(bean1, bean2);
			int diff2 = NGramsBean.diff(bean2, bean1);
			if (diff1 < 2 && diff2 < 2) {
				System.out.println(permutation + ", " + word + ", " + diff1 + ", " + diff2 + ", " + WordFrequencies.freq(word));
				count++;
				int freq = WordFrequencies.freq(word);
				freqsum+=freq;
				
				if (diff1 == 0 && diff2 == 1) {
					count2++;
					freqsum2+=freq;
				}
				else if (diff1 == 1 && diff2 == 0) {
					count1++;
					freqsum1+=freq;
				}
				else if (diff1 == 1 && diff2 == 1) {
					count3++;
					freqsum3+=freq;
				}
				else if (diff1 == 0 && diff2 == 0) {
					count4++;
					freqsum4+=freq;
				}
			}
			/* possibilities:
			 * keep permutation the same. (perfect anagram)
			 * remove a letter from the permutation. (permutation has error of +1)
			 * add a letter to the permutation. (permutation has error of -1)
			 * remove a letter from permutation, add a letter to permutation.
			 */
		}
		System.out.println(permutation + ", " + count + ", " + freqsum + ", " + (count*freqsum)
				 + ", " + count1 + ", " + freqsum1 + ", " + (count1*freqsum1)
				 + ", " + count2 + ", " + freqsum2 + ", " + (count2*freqsum2)
				 + ", " + count3 + ", " + freqsum3 + ", " + (count3*freqsum3)
				 + ", " + count4 + ", " + freqsum4 + ", " + (count4*freqsum4));
	}
	
	public static void search() {
		permute();
	}
	
	/** last 18: look for words that match the same partial pattern */
	
	public static void search2() {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != 8) continue;
			int[] diffs = Anagrams.differences(word, "HOPIT");
			if (diffs[0] == 3 && diffs[1] == 0) System.out.println(word + ", " + WordFrequencies.freq(word));
		}
	}
	
	public static void main(String[] args) {
		search2();
		//process2("SREA");
	}
}
