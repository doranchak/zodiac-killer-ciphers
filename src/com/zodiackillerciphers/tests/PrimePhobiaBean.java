package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.List;

public class PrimePhobiaBean {
	public int numPlus;
	public int numPlusPrime;
	public int numB;
	public int numBPrime;
	
	/** prime positions in which + or B appears */ 
	public List<Integer> positions;
	
	public int errors;
	
	public PrimePhobiaBean() {
		positions = new ArrayList<Integer>();
	}

	public String toString() {
		return "numPlus " + numPlus + " numPlusPrime " + numPlusPrime + " numB " + numB + " numBPrime " + numBPrime + " errors " + errors + " positions " + positions;
	}
}
