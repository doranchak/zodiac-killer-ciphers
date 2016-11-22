package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;

public class ShuffleTests {
	/**
	 * Maybe I should repeat the random shuffle experiment, but keep only the
	 * shuffles that have the same number of repeated bigrams/fragments, and
	 * that have similar stretches of no repeating characters. Then we can see if
	 * that reduces the statistical significance of Z340's score
	 * 
	 * To accomplish that, shuffle by row, to preserve per-row symbol repeat stats.  But
	 * keep only the shuffles that have 25 repeated bigrams.
	 */
	
	public static void z340RowShuffle() {
		NGramsBean bean = new NGramsBean(2, Ciphers.cipher[0].cipher);
		System.out.println(bean);
		
		for (int i=0; i<1000; i++) {
			boolean go = true;
			List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
			String cipher = null;
			while (go) {
				cipher = "";
				for (StringBuffer line : list) {
					cipher += CipherTransformations.shuffle(line.toString());
				}
				bean = new NGramsBean(2, cipher);
				if (bean.numRepeats() == 25 && bean.count() == 46) {
					go = false;
				}
			}
			list = TransformationBase.toList(cipher, 17);
			System.out.println("cipher_information=z340_" + i + "_shuffle_by_row");
			for (StringBuffer sb : list) System.out.println(sb);
			System.out.println();
			
		}
	}

	public static void z340FullShuffle() {
		
		for (int i=0; i<1000; i++) {
			String cipher = CipherTransformations.shuffle(Ciphers.cipher[0].cipher);
			List<StringBuffer> list = TransformationBase.toList(cipher, 17);
			System.out.println("cipher_information=z340_" + i + "_shuffle");
			for (StringBuffer sb : list) System.out.println(sb);
			System.out.println();
			
		}
	}
	
	public static void main(String[] args) {
		z340FullShuffle();
	}
}
