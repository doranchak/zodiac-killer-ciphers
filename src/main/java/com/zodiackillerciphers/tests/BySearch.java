package com.zodiackillerciphers.tests;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/** Find all occurrences of "By" */
public class BySearch {
	public static void search(String ciphertext, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "How many times do variants of \"BY\" and \"YB\" appear horizontally?";
		stats.actual = 4;
		stats.histogram = true;
		for (int i = 0; i < shuffles; i++) {
			if (i % 100000 == 0)
				System.out.println(i + "...");
			String shuffle = CipherTransformations.shuffle(ciphertext);
			String original = shuffle;
			shuffle = shuffle.replaceAll("b", "B");
			shuffle = shuffle.replaceAll("y", "Y");
			int count = 0;
			for (String word : new String[] { "BY", "YB" }) {
				int pos = shuffle.indexOf(word);
				while (pos > -1) {
					count++;
					pos = shuffle.indexOf(word, pos + 1);
				}
			}
			if (count > 7) 
				System.out.println(original);
			stats.addValue(count);
		}
		stats.output();
	}

	public static void main(String[] args) {
		search(Ciphers.Z340, 10000000);
	}
}
