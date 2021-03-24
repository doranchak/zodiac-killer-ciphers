package com.zodiackillerciphers.tests;

import java.util.Arrays;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;

/** based on idea in his book that "CALIFORNIA" matches beginning of 32-character cipher
 * due to literal matches on letters.
 */
public class CraigBauerWordSearch {
	
	public static int[] match(String word, String cipher) {
		int exact = 0;
		int ambiguous = 0;
		for (int i=0; i<word.length()&&i<cipher.length(); i++) {
			char c1 = word.charAt(i);
			char c2 = cipher.charAt(i);
			if (c1 == c2) exact++;
			else {
				String interpretations = WordSearch.translations.get(c2);
				if (interpretations != null && interpretations.contains(""+c1)) {
					ambiguous++;
				}
			}
		}
		return new int[] {exact, ambiguous};
	}
	
	public static void match(String key, Cipher cipher, int pos, TreeSet<CraigBauerWordSearchBean> minmax) {
		if (key.length() < 2) return;
		
		int[] matches = match(key, cipher.cipher.substring(pos));
		// score:  maximize word length, exact counts, and (to a lesser extent) ambiguous counts.
		CraigBauerWordSearchBean bean = new CraigBauerWordSearchBean(matches[0],
				matches[1], key.length(), WordFrequencies.freq(key), pos, key, cipher.description.substring(0,
							cipher.description.indexOf(":")));
		minmax.add(bean);
//		System.out.println("minmax size " + minmax.size());
//		System.out.println("Added " + bean);
		
		if (minmax.size() > 100) {
			//System.out.println("Removing " + minmax.last());
			minmax.remove(minmax.first());
		}
		
	}
	public static void search() {
		
		Cipher[] ciphers = new Cipher[] {
				Ciphers.cipher[0], // z340
				Ciphers.cipher[1], // z408
				Ciphers.cipher[3], // z13
				Ciphers.cipher[4]
		};
		
		WordFrequencies.init();
		int total = WordFrequencies.map.size();
		for (Cipher cipher : ciphers) {
			System.out.println(cipher.description);
			int count = 0;
			for (int i = 0; i < cipher.cipher.length(); i++) {
				TreeSet<CraigBauerWordSearchBean> heap = new TreeSet<CraigBauerWordSearchBean>();
				for (String key : WordFrequencies.map.keySet()) {
					match(key, cipher, i, heap);
					count++;
					if (count % 1000 == 0) {
//						System.out.println(100 * ((float) count) / total
//								+ "%...");
						// for (CraigBauerWordSearchBean bean : heap)
						// System.out.println(bean);
						// System.out.print(heap.size());
					}
				}
				System.out.println("Dumping max heap for position " + i + ":");
				for (CraigBauerWordSearchBean bean : heap) {
					System.out.println(bean);
					System.out.println(bean.js1());
				}
			}
			
		}
	}
	
	public static void test1() {
		System.out.println(Arrays.toString(match("CALIFORNIA","C9J|#OktAMf8oORTGX6FDVj%HCELzPW9")));
		System.out.println(Arrays.toString(match("GOD","G2d")));
	}
	public static void main(String[] args) {
		search();
		//test1();
	}
}
