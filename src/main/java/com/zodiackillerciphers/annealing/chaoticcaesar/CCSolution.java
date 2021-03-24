package com.zodiackillerciphers.annealing.chaoticcaesar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.keyreconstruction.KeyReconstruction;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.tests.ChaoticCaesar;
import com.zodiackillerciphers.tests.LetterFrequencies;

/** http://scienceblogs.de/klausis-krypto-kolumne/2020/05/10/christophs-chaotic-caesar-challenge */
public class CCSolution extends Solution {

	public String key; // key derived from the word list
	public List<Character> letters; // letters that form the key
	public String bestPlaintext; // the best plaintext for the given key across all n in [1,25]
	
	public Random random = new Random();
	
	List<Character> reverse;
	
	public double energyCached;
	
	static String correctPlaintext = ChaoticCaesar.decode(ChaoticCaesar.challengeCipher,
			KeyReconstruction.keyFrom("VICTORFANSHE"), 18);	
	
	public void decode() {
		
	}
	
	@Override
	public void mutateReverse() {
		letters = reverse;
		mutateReverseClear();
	}
	@Override
	public void mutateReverseClear() {
		reverse = null;
	}

	@Override
	public String representation() {
		return energyCached + "	" + key + "	" + letters + "	" + bestPlaintext; 
	}

	@Override
	public double energyCached() {
		// TODO Auto-generated method stub
		return energyCached;
	}

	@Override
	public void initialize() {
		letters = new ArrayList<Character>();
		mutateAddLetter();
		mutateReverseClear();
	}
	public CCSolution() {
	}
	
	@Override
	public boolean mutate() {
		
		int which = random.nextInt(3);
		if (which == 0) 
			return mutateAddLetter();
		if (which == 1) 
			return mutateRemoveLetter();
		if (which == 2) 
			return mutateSwapLetters();
		
		return true;
	}
	
	public void makeReverse() {
		reverse = new ArrayList<Character>();
		reverse.addAll(letters);
	}
	
	public boolean mutateAddLetter() {
		if (letters.size() == 26) return true; // already used all letters
		makeReverse();
		List<Character> available = new ArrayList<Character>();
		for (int i=0; i<ChaoticCaesar.alphabet.length(); i++) {
			char ch = ChaoticCaesar.alphabet.charAt(i);
			if (!letters.contains(ch)) available.add(ch);
		}
		if (available.size() < 1) return true; 
		
		int where = random.nextInt(letters.size()+1);
		letters.add(where, available.get(random.nextInt(available.size())));
		return true;
		
	}
	public boolean mutateRemoveLetter() {
		if (letters.size() == 0) return true; // ignore if already empty
		makeReverse();
		letters.remove(random.nextInt(letters.size()));
		return true;
	}
	public boolean mutateSwapLetters() {
		if (letters.size() < 2) return true; // ignore if not enough to swap
		makeReverse();
		int a = random.nextInt(letters.size());
		int b = a;
		while (a==b) b = random.nextInt(letters.size());
		Character aLetter = letters.get(a);
		Character bLetter = letters.get(b);
		letters.set(a, bLetter);
		letters.set(b, aLetter);
		return true;
	}
	
	@Override
	public double energy() {
		energyCached = 0;
		makeKey();
		int n = 18;
		bestPlaintext = ChaoticCaesar.decode(ChaoticCaesar.challengeCipher, key, n);
		energyCached = -ChaoticCaesar.match(bestPlaintext, correctPlaintext);
		return energyCached;
	}
	public double energy408() {
		energyCached = 0;
		makeKey();
		double best = 0; double ng = 0;
		String plaintext;
		for (int n=1; n<3; n++) {
			plaintext = ChaoticCaesar.decode(ChaoticCaesar.z408Cipher, key, n);
			ng = NGramsCSRA.ngramSum(plaintext.substring(0,50), "EN", false, 3);
			if (ng > best) {
				best = ng;
				bestPlaintext = plaintext;
			}
		}
		energyCached = -best;
		//System.out.println("energy " + energyCached);
		return energyCached;
	}
	
	public void makeKey() {
		StringBuffer sb = new StringBuffer();
		for (Character letter : letters) sb.append(letter);
		key = KeyReconstruction.keyFrom(sb.toString());
	}
	
	@Override
	public Solution clone() {
		CCSolution newSol = new CCSolution();
		newSol.bestPlaintext = this.bestPlaintext;
		newSol.key = this.key;
		newSol.letters = new ArrayList<Character>();
		newSol.letters.addAll(this.letters);
		return newSol;
	}
	
	public static void main(String[] args) {
	}

}
