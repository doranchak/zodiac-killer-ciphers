package com.zodiackillerciphers.tests;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;

public class BoxCorners {
	/** returns number of "box corner" patterns found in the given cipher.
	 * 
	 * Loosest definition:  
	 * - Let L = { /, -, | }, the set of "line" symbols.
	 * - Count number of cipher positions, for which at least 2 of the symbols in positions to the immediate N, E, 
	 *   S, or W are in L. 
	 */
	public static String L = "/-|";
	static int W = 17;

	/** find one pair of the exact same box corner patterns that Z340 has */
	public static boolean find1(String cipher) {
		boolean foundNW = false;
		boolean foundES = false;
		List<StringBuffer> list = TransformationBase.toList(
				cipher, W);
		for (int row=0; row<cipher.length()/W; row++) {
			for (int col=0; col<W; col++) {
				String boxNW = rc(list, row, col) + rc(list, row-1, col) + rc(list, row, col-1);
				if ("*|-".equals(boxNW)) foundNW = true;
				String boxES = rc(list, row, col) + rc(list, row, col+1) + rc(list, row+1, col);
				if ("*-|".equals(boxES)) foundES = true;
			}
		}
		
//		if (foundNW) {
//			System.out.println("foundNW! " + cipher);
//		}
//		if (foundES) {
//			System.out.println("foundES! " + cipher);
//		}
//		1.825 s per 10,000
		return foundNW & foundES;
	}
	
	/** find one pair of the exact same box corner patterns that Z340 has, but with any matching base
	 * symbol (instead of "*") */
	public static boolean find2(String cipher) {
		boolean foundES = false;
		boolean foundNW = false;
		Map<Character, Set<String>> map = new HashMap<Character, Set<String>>(); 
		List<StringBuffer> list = TransformationBase.toList(
				cipher, W);
		for (int row=0; row<cipher.length()/W; row++) {
			for (int col=0; col<W; col++) {
				String boxNW = rc(list, row, col) + rc(list, row-1, col) + rc(list, row, col-1);
				if (boxNW.endsWith("|-")) {
					Character key = boxNW.charAt(0);
					
				}
				String boxES = rc(list, row, col) + rc(list, row, col+1) + rc(list, row+1, col);
				if ("*-|".equals(boxES)) foundES = true;
			}
		}
		
//		if (foundNW) {
//			System.out.println("foundNW! " + cipher);
//		}
//		if (foundES) {
//			System.out.println("foundES! " + cipher);
//		}
//		1.825 s per 10,000
		return foundNW & foundES;
	}
	
	/** find one pair of the box corner patterns, with same base symbol (*), but each with any orientation of two lines */
//	public static boolean find2(String cipher) {
//		int found = 0;
//		List<StringBuffer> list = TransformationBase.toList(
//				cipher, W);
//		for (int row=0; row<cipher.length()/W; row++) {
//			for (int col=0; col<W; col++) {
//				
//				char base1 = rc(list, row, col).charAt(0);
//				if (base1 != '*') continue;
//				String adjacent = rc(list, row-1, col) + rc(list, row, col+1) + rc(list, row+1, col) + rc(list, row, col-1);
//				int numLines = 0;
//				for (int i=0; i<adjacent.length(); i++) {
//					if (isLine(adjacent.charAt(i))) {
//						numLines++;
//						if (numLines == 2) {
//							found++;
//							if (found == 2) {
//								System.out.println("hit r" + row + "c" + col + ": " + cipher);
//								return true;
//							}
//						}
//					}
//				}
//			}
//		}
//		return false;
//	}
	
	public static String rc(List<StringBuffer> list, int row, int col) {
		if (row < 0) return "";
		if (row >= list.size()) return "";
		if (col < 0) return "";
		if (col >= list.get(row).length()) return "";
		return "" + list.get(row).charAt(col);
	}
	
	public static boolean isLine(char ch) {
		return L.contains(""+ch);
	}
	
	public static void shuffleFind1(String cipher, int shuffles) {
		System.out.println(new Date().getTime());
		StatsWrapper stats = new StatsWrapper();
		stats.actual = 1;
		for (int i=0; i<shuffles; i++) {
			String shuf = CipherTransformations.shuffle(cipher);
			int add = find1(shuf) ? 1 : 0;
			stats.addValue(add);
		}
		stats.output();
		System.out.println(new Date().getTime());
	}
	
	public static void shuffleFind2(String cipher, int shuffles) {
		System.out.println(new Date().getTime());
		StatsWrapper stats = new StatsWrapper();
		stats.actual = 1;
		for (int i=0; i<shuffles; i++) {
			String shuf = CipherTransformations.shuffle(cipher);
			int add = find2(shuf) ? 1 : 0;
			stats.addValue(add);
		}
		stats.output();
		System.out.println(new Date().getTime());
	}
	public static void main(String[] args) {
//		System.out.println(find2(Ciphers.Z340));
		shuffleFind2(Ciphers.Z340, 10000);
	}
}
