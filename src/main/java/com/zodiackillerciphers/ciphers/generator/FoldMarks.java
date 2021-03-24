package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * represents the marks that appear on the left and right sides of the cipher,
 * suggestive of fold markings
 */
public class FoldMarks {
	
	public int positionLeft;
	public int positionRight;
	
	public char symbol;

	/** allowed positions for pairs of fold marks */
	public static int[] positions1 = new int[] {153, 169};
	public static int[] positions2 = new int[] {170, 186};
	
	public FoldMarks(int positionLeft, int positionRight, char symbol) {
		super();
		this.positionLeft = positionLeft;
		this.positionRight = positionRight;
		this.symbol = symbol;
	}
	
	/** returns list of candidate fold marks.  if none are found, empty list is returned. */ 
	public static List<FoldMarks> foldMarksFrom(String text) {
		List<FoldMarks> list = new ArrayList<FoldMarks>();
		if (text == null) return list;
		
		if (text.charAt(positions1[0]) == text.charAt(positions1[1]))
			list.add(new FoldMarks(positions1[0], positions1[1], text.charAt(positions1[0])));
		if (text.charAt(positions2[0]) == text.charAt(positions2[1]))
			list.add(new FoldMarks(positions2[0], positions2[1], text.charAt(positions2[0])));
		
		return list;
	}
	
	public boolean correctEncodings() {
		return symbol == '-';
	}
	
	public String toString() {
		return positionLeft + "," + positionRight + ", " + symbol;
	}
	
	public String highlight() {
		String js = "fold marks ";
		js += "darkenpos(" + positionLeft + "); ";
		js += "darkenpos(" + positionRight + "); ";
		return js;
	}
	
}
