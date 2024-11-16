package com.zodiackillerciphers.annealing.anagrams_shuffler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.ngrams.AZDecrypt;

/*
 * 
 *  Strategy:
 *  - Shuffle the input string, and randomly insert spaces, to maximize the azdecrypt score
 * 
*/

public class ShufflerSolution extends Solution {

	// the input string we want to rearrange
	StringBuffer inputString;

	StringBuffer mutateInputString; // the original input string before adding/removing word.

	public Random random = new Random();
	public double energyCached;
	
	@Override
	public void mutateReverse() {
		inputString = new StringBuffer(mutateInputString);
	}
	@Override
	public void mutateReverseClear() {
		mutateInputString = null;
	}

	@Override
	public String representation() {
		return energyCached + " " + inputString;
	}

	@Override
	public double energyCached() {
		return energyCached;
	}

	@Override
	public void initialize() {
	}
	public ShufflerSolution(String inputString) {
		this.inputString = new StringBuffer(inputString);
	}
	public ShufflerSolution(StringBuffer inputString) {
		this.inputString = new StringBuffer(inputString);
	}
	
	// * swap two characters (letter or space)
	// * insert a space
	// * remove a space
	@Override
	public boolean mutate() {
		mutateInputString = new StringBuffer(inputString);
		int mutateWhich = random.nextInt(100);
		if (mutateWhich < 80) { // [0, 79], 80% chance
			int a = random.nextInt(inputString.length());
			int b = a;
			while (a==b) b = random.nextInt(inputString.length());
			char tmp = inputString.charAt(a);
			inputString.setCharAt(a, inputString.charAt(b));
			inputString.setCharAt(b, tmp);
		} else if (mutateWhich < 90) { // [80, 89], 10% chance
			// [0, len]
			int pos = random.nextInt(inputString.length()+1);
			boolean insert = true;
			// avoid inserting double spaces
			if (pos > 0 && inputString.charAt(pos-1) == ' ') insert = false;
			if (pos < inputString.length() && inputString.charAt(pos) == ' ') insert = false;
			if (insert) inputString.insert(pos, ' ');
		} else { // [90, 99], 10% chance
			List<Integer> spaceIndices = new ArrayList<>();
			for (int i = 0; i < inputString.length(); i++) {
				if (inputString.charAt(i) == ' ') {
					spaceIndices.add(i);
				}
			}
			if (spaceIndices.size() > 0) {
				int pos = random.nextInt(spaceIndices.size());
				inputString.deleteCharAt(spaceIndices.get(pos));
			}
		}
		return true;
	}

	@Override
	public double energy() {
		energyCached = -score();
		return energyCached;
	}
	@Override
	public Solution clone() {
		ShufflerSolution sol = new ShufflerSolution(inputString);
		return sol;
	}
	
	public double score() {
		return AZDecrypt.score(inputString);
	}
	
}
