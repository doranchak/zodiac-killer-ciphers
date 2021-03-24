package com.zodiackillerciphers.annealing.vigsub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;

/** attempt to evolve a cipher text that can simultaneously decode to one plaintext under substitution
 * and another under vigenere
 */
public class VigSubSolution extends Solution {
	
	public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static Random rand = new Random();

	/** the plaintext (as a sequence of words) under substitution */
	public List<String> plaintextSub;
	public StringBuffer plaintextSubStr;
	/** the plaintext (as a sequence of words) under vigenere */
	public List<String> plaintextVig;
	public StringBuffer plaintextVigStr;
	/** the vigenere key (as an array of shift values) */
	public int vigKeyLength;
	public int[] vigKey;

	// for reversals
	public int whichMutation;
	public int vigKeyIndex;
	public int vigKeyVal;
	public int swapBetweenIndex;
	public int swapWithinWhich;
	public int swapWithinIndex1;
	public int swapWithinIndex2;
	
	public Random random = new Random();
	public double energyCached;
	
	/** computed ciphertext, derived from vig key */
	public StringBuffer ciphertext;

	public VigSubSolution(int vigKeyLength) {
		this.vigKeyLength = vigKeyLength;
	}
	
	/** add the pair of words to the sub and vig plaintexts */
	public void add(String wordSub, String wordVig) {
		plaintextSub.add(wordSub.toUpperCase());
		plaintextVig.add(wordVig.toUpperCase());
	}
	
	/** returns true if the current selection of plaintexts and vig key are compatible */
	public boolean isCompatible() {
		encode();
		StringBuffer ptSub = new StringBuffer();
		for (String word : plaintextSub)
			ptSub.append(word);
		String c1 = Arrays.toString(Ciphers.toNumeric(ciphertext.toString(), false));
		String c2 = Arrays.toString(Ciphers.toNumeric(ptSub.toString(), false));
//		System.out.println("compat " + ptSub + ", " + plaintextVig + ", " + ciphertext + ", " + c1 + ", " + c2);
		// return true only if the isomorphisms are equal
		return (c1.equals(c2));
	}
	
	@Override
	public boolean mutate() {
		whichMutation = -1;
		WordFrequencies.init();
		int which = rand.nextInt(4);
		if (which == 0) {
			// 1) tweak the vig key
//			String str = "before: " + Arrays.toString(vigKey);
			int index = rand.nextInt(vigKey.length);
			int val = 1 + rand.nextInt(25);
			vigKeyVal = vigKey[index];
			vigKeyIndex = index;
			vigKey[index] = val;
//			str += " after: " + Arrays.toString(vigKey);
//			System.out.println("mutate vigkey: " + str);
		} else if (which == 1) {
			// 2) add random pair of words
			while (true) {
				String wordSub = WordFrequencies.randomWord(99);
				String wordVig = WordFrequencies.randomWord(wordSub.length(), 99);
				if (wordSub.length() < 4) continue;
//				System.out.println(wordSub + ", " + wordVig);
				if (wordSub.equals(wordVig)) continue;
				plaintextSub.add(wordSub);
				plaintextVig.add(wordVig);
//				System.out.println("mutate add pair " + wordSub + ", " + wordVig);
				break;
			}
			
		} else if (which == 2) {
			// 3) swap words between lists
			if (plaintextSub.size() > 0 && plaintextVig.size() > 0) {
//				String str = "before: " + plaintextSub + ", " + plaintextVig;
				int index = rand.nextInt(plaintextSub.size());
				String ws = plaintextSub.get(index);
				String wv = plaintextVig.get(index);
				plaintextVig.set(index, ws);
				plaintextSub.set(index, wv);
				swapBetweenIndex = index;
//				str += " after: " + plaintextSub + ", " + plaintextVig;
//				System.out.println("mutate swap between " + str);
			} else return false;
		} else if (which == 3) {
			// 4) swap words within a list
			if (plaintextSub.size() > 1 && plaintextVig.size() > 1) {
				int whichList = rand.nextInt(2);
				List<String> list = whichList == 0 ? plaintextSub : plaintextVig;
//				String str = "mutate swap within " + whichList + ", before: " + list; 
				int index1 = rand.nextInt(list.size());
				int index2 = index1;
				while (index1 == index2) {
					index2 = rand.nextInt(list.size());
				}
				String w1 = list.get(index1);
				String w2 = list.get(index2);
				list.set(index1, w2);
				list.set(index2, w1);
				swapWithinWhich = whichList;
				swapWithinIndex1 = index1;
				swapWithinIndex2 = index2;
//				str += " after: " + list;
//				System.out.println(str);
			} else return false;
		}
		whichMutation = which;
		return true;
	}
	
	@Override
	public void mutateReverse() {
		if (whichMutation == 0) {
			vigKey[vigKeyIndex] = vigKeyVal;
		} else if (whichMutation == 1) {
			// remove most recently added pair
			plaintextSub.remove(plaintextSub.size()-1);
			plaintextVig.remove(plaintextVig.size()-1);
//			System.out.println("reverse 1 " + plaintextSub + ", " + plaintextVig);
		} else if (whichMutation == 2) {
			// undo swap
			String ws = plaintextSub.get(swapBetweenIndex);
			String wv = plaintextVig.get(swapBetweenIndex);
			plaintextVig.set(swapBetweenIndex, ws);
			plaintextSub.set(swapBetweenIndex, wv);
//			System.out.println("reverse 2 " + plaintextSub + ", " + plaintextVig);
		} else if (whichMutation == 3) {
			// undo swap
			List<String> list = swapWithinWhich == 0 ? plaintextSub : plaintextVig;
			String w1 = list.get(swapWithinIndex1);
			String w2 = list.get(swapWithinIndex2);
			list.set(swapWithinIndex1, w2);
			list.set(swapWithinIndex2, w1);
//			System.out.println("reverse 3 " + plaintextSub + ", " + plaintextVig);
		}
	}
	
	@Override
	public void mutateReverseClear() {
		whichMutation = -1;
		vigKeyIndex = -1;
		vigKeyVal = -1;
		swapBetweenIndex = -1;
		swapWithinWhich = -1;
		swapWithinIndex1 = -1;
		swapWithinIndex2 = -1;
	}
	
	@Override
	public String representation() {
		StringBuffer rep = new StringBuffer();
		rep.append("sub: ");
		rep.append(plaintextSub);
		rep.append(" vig: ");
		rep.append(plaintextVig);
		rep.append(" key: ");
		rep.append(Arrays.toString(vigKey));
		if (ciphertext != null && ciphertext.length() > 0) {
			rep.append(" cipher: " + ciphertext);
		}
		rep.append(" energy: " + energyCached);
		return rep.toString();
	}
	
	@Override
	public double energy() {
		// smaller is better
		if (isCompatible()) {
			energyCached = -ciphertext.length();
		} else 
			energyCached = Double.MAX_VALUE;
		return energyCached;
	}
	
	@Override
	public double energyCached() {
		return energyCached;
	}

	@Override
	public void initialize() {
		plaintextSub = new ArrayList<String>();
		plaintextVig = new ArrayList<String>();
		vigKey = new int[vigKeyLength];
		for (int i=0; i<vigKey.length; i++)
			vigKey[i] = 1 + rand.nextInt(25);
	}
	
	@Override
	public Solution clone() {
		
		VigSubSolution newSol = (VigSubSolution) this.clone();
		newSol.initialize();
		newSol.plaintextVig.addAll(this.plaintextVig);
		newSol.plaintextSub.addAll(this.plaintextSub);
		newSol.ciphertext = new StringBuffer(this.ciphertext);
		
		return newSol;

	}

	/** encode the cipher with the current key */
	public StringBuffer encode() {
		ciphertext = new StringBuffer();
		plaintextVigStr = new StringBuffer();
		for (String word : plaintextVig) {
			plaintextVigStr.append(word);
		}
		for (int i=0; i<plaintextVigStr.length(); i++) {
			char p = plaintextVigStr.charAt(i);
			int k = alphabet.indexOf(p);
			k += vigKey[i % vigKey.length];
			if (k < 0) k += 26;
			else k %= 26;
			ciphertext.append(alphabet.charAt(k));
		}
		return ciphertext;
	}

	public static void test() {
		VigSubSolution v = new VigSubSolution(5);
		v.initialize();

		System.out.println("Init: " + v.representation());
		double bestEnergy = 0;
		while (true) {
			v.mutate();
			double currentEnergy = v.energy();
			if (currentEnergy < bestEnergy) {
				bestEnergy = currentEnergy;
				System.out.println("New best: " + v.representation());
				v.mutateReverseClear();
			} else v.mutateReverse();
		}
	}
	public static void main(String[] args) {
		test();
	}

}
