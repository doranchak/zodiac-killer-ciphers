package com.zodiackillerciphers.corpus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.names.Name;
import com.zodiackillerciphers.old.MyNameIs;
import com.zodiackillerciphers.suffixtree.LRS;
import com.zodiackillerciphers.tests.unicity.PlaintextBean;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** Look for length 97 plaintexts where we can also pop in the known K4 cribs */
public class K4CorpusScanner extends CorpusBase {
	public static void search() {
		WordFrequencies.init();
		CorpusBase.REDDIT_ONLY = false;
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long hits = 0;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngrams = ngrams(97);
			for (List<String> plaintext : ngrams) {
				if (applyCribs(plaintext)) hits++;
				samples++;
			}
			if (hits >= 1000000) {
				System.out.println("A million hits out of " + samples + " samples taken from " + sources + " sources!");
				return;
			}
		}
	}
	
	public static boolean applyCribs(List<String> plaintext) {
		// Counting starts at position 0.
		// Cribs are: 
		// - EASTNORTHEAST at position 21
		// - BERLINCLOCK at position 63
		// We can replace ngrams with EASTNORTHEAST if at position 21 we have a number of ngrams with length sum equaling 13.   
		// We can replace ngrams with BERLINCLOCK if at position 63 we have a number of ngrams with length sum equaling 11.
		
		int index = 0;
		int length = 0;
		
		int index1 = 0;
		int count1 = 0;

		int index2 = 0;
		int count2 = 0;

		while (index < plaintext.size()) {
			length += plaintext.get(index).length();
//			System.out.println("1: " + plaintext.get(index));
			index++;
			if (length >= 21) break;
		}
		if (length != 21) return false;
		index1 = index;
		
		length = 0;
		while (index < plaintext.size()) {
			length += plaintext.get(index).length();
//			System.out.println("2: " + plaintext.get(index));
			count1++;
			index++;
			if (length >= 13) break;
		}
		if (length != 13) return false;

		length = 21 + 13;
		while (index < plaintext.size()) {
			length += plaintext.get(index).length();
//			System.out.println("3: " + plaintext.get(index));
			index++;
			if (length >= 63) break;
		}
		if (length != 63) return false;
		index2 = index;

		length = 0;
		while (index < plaintext.size()) {
			length += plaintext.get(index).length();
//			System.out.println("4: " + plaintext.get(index));
			count2++;
			index++;
			if (length >= 11) break;
		}
		if (length == 11) {
			
			// reject if most words are too short
			if (PlaintextBean.badAverageTokenLength(plaintext))
				return false;
			// reject if string is too repetitive
			if (PlaintextBean.tooRepetitive(flatten(plaintext, true).toString()))
				return false;
			
//			System.out.println(index1 + " " + count1 + " " + index2 + " " + count2);			
			StringBuffer p1 = flatten(plaintext, true);
			for (int i=0; i<count2; i++) {
				plaintext.remove(index2);
			}
			plaintext.add(index2, "CLOCK");
			plaintext.add(index2, "BERLIN");
			for (int i=0; i<count1; i++) {
				plaintext.remove(index1);
			}
			plaintext.add(index1, "EAST");
			plaintext.add(index1, "NORTH");
			plaintext.add(index1, "EAST");
			StringBuffer p2 = flatten(plaintext, true);
			float score = WordFrequencies.scoreLog(p2.toString());
			System.out.println(score + "," + p1 + "," + p2);
			return true;
		}
		return false;		
	}

	public static void main(String[] args) {
		search();
	}
}
