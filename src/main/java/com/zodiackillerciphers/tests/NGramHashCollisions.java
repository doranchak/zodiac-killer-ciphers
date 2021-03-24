package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.Map;

/** if we hash all possible 6-grams, how many of them will have conflicting hash codes, requring direct comparison
 * within buckets?
 */
public class NGramHashCollisions {
	static void test() {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>(); 
		for (int a=0; a<26; a++) {
			for (int b=0; b<26; b++) {
				for (int c=0; c<26; c++) {
					for (int d=0; d<26; d++) {
						for (int e=0; e<26; e++) {
							for (int f=0; f<26; f++) {
								String ngram = "" + (char)(a+97) + (char)(b+97) + (char)(c+97) + (char)(d+97) + (char)(e+97) + (char)(f+97);
								//System.out.println(ngram);
								Integer key = ngram.hashCode();
								Integer val = counts.get(key);
								if (val == null) val = 0;
								val++;
								counts.put(key, val);
								if (val > 1) {
									System.out.println(ngram + " " + key + " " + val);
								}
							}
						}
					}
				}
			}
		}
		System.out.println(counts.size());
		for (Integer key : counts.keySet()) {
			Integer val = counts.get(key);
			if (val < 2) continue;
			System.out.println(key + ": " + val);
		}
	}
	public static void main(String[] args) {
		test();
	}
}
