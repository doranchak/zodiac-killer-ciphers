package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Jelberg {
	/** http://zodiackillersite.com/viewtopic.php?f=81&t=4022
	 * 
	 * shuffle cipher, then select the first n symbols
	 * calculate multiplicity
	 * generate histogram of results
	 *   
	 **/
	public static void shuffleMultiplicity(String cipher, int n, int shuffles) {
		Map<Float, Integer> counts = new HashMap<Float, Integer>();
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			String sub = cipher.substring(0, n);
			float m = Stats.multiplicity(sub);
			Integer val = counts.get(m);
			if (val == null) val = 0;
			val++;
			counts.put(m,  val);
		}
		System.out.println(counts);
	}
	
	public static void main(String[] args) {
		shuffleMultiplicity(Ciphers.Z340, 17, 1000000);
	}
}
