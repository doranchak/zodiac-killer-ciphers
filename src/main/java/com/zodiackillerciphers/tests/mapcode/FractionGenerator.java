package com.zodiackillerciphers.tests.mapcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FractionGenerator {

//	public static String[] conjunctions = new String[] { "AND", "&", "PLUS", "+" };
	public static String[] conjunctions = new String[] { "AND", "&"};

	/**
	 * Generate all pronunciations of fractions from min to max (inclusive), up
	 * to 1/32nd resolution
	 */
//	public static List<String> generate(int min, int max) {
//		List<String> list = new ArrayList<String>();
//		for (int n = min; n <= max; n++) {
//			for (String prefix : prefixes(n)) {
//				list.add(prefix);
//			}
//		}
//		return list;
//	}

	public static List<Fraction> generate(int whole, int numerator,
			int denominator, boolean spaces) {
		List<Fraction> list = new ArrayList<Fraction>();
		Set<String> wholes = new HashSet<String>();
		Set<String> nums = new HashSet<String>();
		Set<String> dens = new HashSet<String>();

		if (whole == 0)
			wholes.add("");
		else {
			wholes.add(EnglishNumberToWords.convertUpper(whole));
//			if (whole > 9)
//				wholes.add(EnglishNumberToWords.convertUpperDigits(whole));
		}

		if (numerator > 0) {
			nums.add(EnglishNumberToWords.convertUpper(numerator));
//			if (numerator > 9)
//				nums.add(EnglishNumberToWords.convertUpperDigits(numerator));
			if (numerator == 1) {
				nums.add("A");
			}
		}

		String plural = "";
		if (numerator > 1) plural = "S";
		switch (denominator) {
		case 2:
			dens.add("HALF" + plural);
			break;
		case 3:
			dens.add("THIRD" + plural);
			break;
		case 4:
			dens.add("FOURTH" + plural);
			break;
		case 5:
			dens.add("FIFTH" + plural);
			break;
		case 8:
			dens.add("EIGHTH" + plural);
			break;
		case 10:
			dens.add("TENTH" + plural);
			break;
		case 16:
			dens.add("SIXTEENTH" + plural);
			break;
		case 32:
			dens.add("THIRTYSECOND" + plural);
			dens.add("THIRTY-SECOND" + plural);
			if (spaces) dens.add("THIRTY SECOND" + plural);
			break;
		}
		
		String[] conjs = conjunctions;
		if (whole == 0) conjs = new String[] {""};
		String delim = "";
		if (spaces) delim = " ";
		for (String w : wholes) {
			if (numerator > 0) {
				for (String n : nums) {
					for (String d : dens) {
						for (String c : conjs) {
							Fraction frac = new Fraction(w, n, d, c, whole, numerator, denominator);
							list.add(frac);
						}
					}
				}
			} else list.add(new Fraction(w, whole));
		}

		return list;
	}

//	public static List<String> prefixes(int n) {
//		List<String> list = new ArrayList<String>();
//		if (n == 0) {
//			list.add("");
//		}
//		for (String conj : conjunctions) {
//			list.add(EnglishNumberToWords.convertUpper(n) + conj);
//		}
//		return list;
//	}

	public static void test() {
		//System.out.println(generate(0, 10));
		for (int whole = 0; whole < 10; whole++) {
			for (int d : new int[] {2, 3, 4, 5, 8, 10, 16, 32}) {
				for (int n = 0; n<d; n++) {
					if (n == 0 && whole == 0) continue;
					System.out.println(generate(whole,n,d,true));
					
				}
			}
			
			
		}
	}

	public static void main(String[] args) {
		test();
	}
}
