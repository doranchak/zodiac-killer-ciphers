package com.zodiackillerciphers.tests.unicity;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/** not used.  see VigSubSolution instead. */
public class SubVigFinder {
	/** try to generate ciphers that can be simultaneously decoded to two different plausible plaintexts -- one via
	 * substitution, and one via vigenere. 
	 */
	
	/** list of words in the first plaintext (substitution) */
	List<String> plaintextWordsSub;
	String plaintextSub; // stream version
	
	/** list of words in the second plaintext (vigenere) */
	List<String> plaintextWordsVig;
	String plaintextVig; // stream version
	
	/** substitution cipher*/
	String cipherSub;
	
	/** vigenere cipher */
	String cipherVig;
	
	/** size of the vigenere key */
	int vigKeySize = 5;

	public void init() {
		plaintextWordsSub = new ArrayList<String>();
		plaintextWordsVig = new ArrayList<String>();
		plaintextSub = "";
		plaintextVig = "";
		cipherSub = "";
		cipherVig = "";
	}
	public void search() {
		WordFrequencies.init();
//		for (int i=0; i<100; i++) {
//			System.out.println(WordFrequencies.randomWord(90));
//		}
		
		int[] vigKey = new int[vigKeySize];
		
		while (true) {
			String wordSub = WordFrequencies.randomWord(90);
			String wordVig = WordFrequencies.randomWord(wordSub.length(), 90);
			if (wordSub.equals(wordVig)) continue;
			System.out.println(wordSub + ", " + wordVig);
		
			// appending the words gives us a candidate plaintext for sub,
			// and one for vig.
			// we want to append the enciphered vig word to the vig cipher,
			// but the result needs to remain isomorphic with the sub plaintext.
			String plaintextSubCandidate = plaintextSub + wordSub;
			String plaintextVigCandidate = plaintextVig + wordVig;
			
		}
		
		
	}
	public static void main(String[] args) {
		new SubVigFinder().search();
	}
	
}
