package com.zodiackillerciphers.annealing;

import java.util.HashSet;
import java.util.Set;

import com.zodiackillerciphers.tests.mapcode.EnglishNumberToWords;

public class NumberWord {
	int number; // [1,26]
	int pos; // [0,cipher.length)

	public NumberWord(int number, int pos) {
		super();
		this.number = number;
		this.pos = pos;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(int number) {
		if (number < 1 || number > 26)
			throw new RuntimeException(number + " is beyond acceptable range.");
		this.number = number;
	}

	/**
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @param pos
	 *            the pos to set
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}

	public String toString() {
		return "{" + number + "," + pos + "}";
	}

	public String word() {
		return EnglishNumberToWords.convertUpper(getNumber()).replaceAll(" ",
				"");
	}
	
	/** returns number of overlapping positions */
	public int overlaps(NumberWord nw2) {
		NumberWord nw1 = this;
		int overlaps = 0;
		
		Set<Integer> set = new HashSet<Integer>();
		for (int i=0;i<nw1.word().length(); i++) {
			set.add(nw1.pos+i);
		}
		for (int i=0;i<nw2.word().length(); i++) {
			if (set.contains(nw2.pos+i)) overlaps++;
		}
		
//		System.out.println("overlap " + nw1 + " " + nw2 + " " + overlaps);
		return overlaps;
	}
	
}
