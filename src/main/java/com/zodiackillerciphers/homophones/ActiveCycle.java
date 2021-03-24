package com.zodiackillerciphers.homophones;

import java.util.Map;

public class ActiveCycle {
	
	/** the pattern that repeats */
	public String cycle;
	
	/** the unbroken cycle sequence we've read so far */
	public StringBuffer sequence;

	/** reference to counts */
	public Map<Character, Integer> counts;
	
	public ActiveCycle(char c1, char c2, Map<Character, Integer> counts) {
		cycle = "" + c1 + c2;
		sequence = new StringBuffer();
		this.counts = counts;
	}
	
	/** what is symbol that follows the given symbol? */
	public char nextSymbol(char c) {
		int index = (cycle.indexOf(c)+1) % cycle.length();
		//System.out.println(cycle + " next for " + c + " is " + cycle.charAt(index));
		return cycle.charAt(index);
	}
	
	/** returns true if the given symbol is the next expected symbol in the sequence */
	public boolean isNextSymbol(char c) {
		if (sequence.length() == 0) return true;
		//System.out.println(cycle + " isNextSymbol " + c);
		return c == nextSymbol(sequence.charAt(sequence.length()-1));
	}
	
	public void add(char c) {
		sequence.append(c);
	}
	
	public int reps() {
		return sequence.length() / cycle.length();
	}
	
	/** the score: number of repetitions times the ratio of reps to total symbols */
	public float score() {
		int total = 0;
		for (int i=0; i<cycle.length(); i++) {
			if (counts.get(cycle.charAt(i)) == null) {
				System.err.println(cycle + " why is counts null for " + cycle.charAt(i));
			}
			total += counts.get(cycle.charAt(i));
		}
		return ((float)sequence.length()) * reps() / total;
	}
	
	public String toString() {
		return score() + " len " + sequence.length() + " reps " + reps() + " cycle " + cycle + " sequence " + sequence; 
	}
	
}
