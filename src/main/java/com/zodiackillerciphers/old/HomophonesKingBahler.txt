package com.zodiackillerciphers.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.zodiackillerciphers.ciphers.Ciphers;

public class HomophonesKingBahler {

	/** in reduceString2, remove any symbol that occurs more than n times in the string */
	public static int REDUCE_STRING2_N = 1;
	
	/** which special count to use */
	public static int WHICH_SPECIAL_COUNT = 1;
	
	/** ignore sequences exceeding this length */
	static int MAX_LENGTH = 9;
	
	/** we stop counting scores if we exceed this many of them for a given homophone candidate length */
	static int NUM_DISJOINT = 10000; 
	
	/** if this is non-zero, then limit the search to substrings that would not produce a number of combinations exceeding this value. */
	long limit;
	
	/** original cipher text */
	String cipher;
	
	/** cipher translated to ordinal values of symbols */
	int[] translated;
	
	/** map of cipher symbol to ordinal value */
	Map<Character, Integer> charmap;
	
	/** Map ordinal values back to symbols */
	Map<Integer, Character> symbols;
	
	/** maps ordinal symbols to each occurrence in the translated cipher */
	Map<Integer, List<Integer>> positionMap;
	
	/** number of distinct symbols in alphabet */
	int maxSymbol = 0;
	
	/** all substrings of the cipher beginning with first occurrence of the [i]th symbol
	 *  and ending one position before the 2nd occurrence of the [i]th symbol.  
	 */
	List<List<List<Integer>>> strings; // list of symbols, each with list of strings, each with a list of symbols

	List<List<List<Integer>>> stringsCopy; // a copy of the original, so we can compare it to the reduced strings

	/** for each symbol [S] there is a set of strings [H] which start with [S].
	 * for each symbol [h] in all strings of [H], determine number of strings in [H]
	 * in which [h] appears.  we seek all [h] that appear in all strings of [H]. 
	 */
	Map<Integer, Map<Integer, Integer>> symbolCounts;
	
	/** score for strings */
	List<Object[]> scores; // [repeated string, count of strings, count of appearances, ratio of appearances to total strings, F (string length * appearance count * ratio * unseenRatio), full sequences] 

	/** top n disjoint homophone sets */
	List<Object[]> top;
	
	/** Map each symbol to its best homophone candidate.  Object[] is { homophone string, num repeats, ratio }.  tracked by homophone length. */
	Map<Integer, Map<Character, Object[]>> bestBySymbol;
	int bestSum; // sum of best coverages for all hom lengths
	
	/** heuristic scores for the top n sets.  sum, average, cardinality=3 count, cardinality>3 count */
	float[] topScores;
	
	/** number of combinations tested */
	int combinations = 0;
	
	static int[] example = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 5, 13, 14, 15, 16, 17, 18, 19, 4, 5, 20, 21, 22, 2, 3, 8, 1, 10, 23, 24, 9, 4, 5, 25, 8, 7, 3, 21, 24, 26, 1, 22, 4, 27, 24, 2, 28, 23, 24, 29, 5, 6, 7, 11, 26, 30, 16, 13, 28, 27, 19, 11, 15, 31, 16, 20, 23, 29, 22, 28, 6, 2, 11, 6, 18, 30, 14, 9, 12, 29, 16, 32, 30, 25, 13, 8, 26, 17, 10, 28, 12, 12, 15, 12, 29, 6, 19, 27, 18, 12, 23, 7, 26, 24, 9, 33, 4, 22, 33, 2, 5, 30, 27, 29, 7, 11, 23, 24, 20, 26, 10, 32, 32, 34, 13, 16, 19, 8, 15, 6, 4, 18, 20, 27, 9, 12, 13, 35, 3, 10, 32, 28, 22, 31, 31, 15, 19, 8, 18, 9, 11, 21, 20, 21, 13, 12, 30, 16, 10, 21, 4, 15, 19, 23, 6, 29, 14, 30, 32, 31, 20, 8, 26, 18, 5, 27, 29, 28, 21, 31, 24, 9, 23, 24, 13, 4, 26, 24, 10, 27, 11, 19, 23, 30, 2, 16, 7, 8};

	static String[] testCiphers = {
		Ciphers.cipher[0].cipher,
		Ciphers.cipher[1].cipher,
		"|FkdW<7tB_YOB*-Cc2GqMf.^pO(KBz3.c|-zlUV+^J+Op7<FBy-|Lz.VGXcU2;%qK+5#R8;(cBF5|N+#yS96z(D2>FkCd*-OlF^R8p++t4Vc.b425f^NFGlBy:cM+UZGW()L#zHJ/k4&+PF5|djtz+M9_Np+B(#O%DWY.<*Kf)HER>pl^VPk|1LTG2dU+R/5tE|DYBpbTMKOyBX1*:49CE>VUZ5-+(G2Jfj#O+_NYz+@L9RcT+L16C<+FlWB|)L>MDHNpkSzZO8A|K;+2<clRJ|*5T4M.+&BF++)WCzWcPOSHT/()pd<M+b+ZR2FBcyA64KSpp7^l8*V3pO++RK2",
		"RcT+L16C<+FlWB|)LSpp7^l8*V3pO++RK2++)WCzWcPOSHT/()pd2GTL1|kPV^lp>REH|Lz.VGXcU2;%qK+5#JHz#L)(WGZU+Mc:yB-yBF<7pO+J^+VUlz-d<M+b+ZR2FBcyA64K/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|c.3zBK(Op^.fMqG22<clRJ|*5T4M.+&BF)fK*<.YWD%O#(B+pNlGFN^f524b.cV4t+++;K|A8OZzSkpNHDM>R8;(cBF5|N+#yS96zOKMTbpBYD|Et5/R+U(G2Jfj#O+_NYz+@L9yBX1*:49CE>VUZ5-+cC-*BOY_Bt7<WdkF|",
		"Spp7^l8*V3pO++RK2#5+Kq%;2UcXGV.zL|Np+B(#O%DWY.<*Kf)d2GTL1|kPV^lp>REHBy:cM+UZGW()L#zHJ(D2>FkCd*-OlF^R8p|c.3zBK(Op^.fMqG2R8;(cBF5|N+#yS96zlGFN^f524b.cV4t++OKMTbpBYD|Et5/R+U-zlUV+^J+Op7<FBy-2<clRJ|*5T4M.+&BF9L@+zYN_+O#jfJ2G(L)|BWlF+<C61L+TcR>MDHNpkSzZO8A|K;+yBX1*:49CE>VUZ5-+K46AycBF2RZ+b+M<dp)(/THSOPcWzCW)++_9M+ztjd|5FP+&4k/|FkdW<7tB_YOB*-Cc",
		"p8R^FlO-*dCkF>2D(++t4Vc.b425f^NFGlJHz#L)(WGZU+Mc:yB/k4&+PF5|djtz+M9_Spp7^l8*V3pO++RK2L)|BWlF+<C61L+TcR#5+Kq%;2UcXGV.zL|>MDHNpkSzZO8A|K;+-zlUV+^J+Op7<FBy-OKMTbpBYD|Et5/R+U9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<dFB&+.M4T5*|JRlc<2++)WCzWcPOSHT/()pyBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2|FkdW<7tB_YOB*-CcR8;(cBF5|N+#yS96zHER>pl^VPk|1LTG2d)fK*<.YWD%O#(B+pN",
		"z69Sy#+N|5FBc(;8RSpp7^l8*V3pO++RK2/k4&+PF5|djtz+M9_-yBF<7pO+J^+VUlz-By:cM+UZGW()L#zHJp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|d<M+b+ZR2FBcyA64KHER>pl^VPk|1LTG2d)fK*<.YWD%O#(B+pN9L@+zYN_+O#jfJ2G(U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BF>MDHNpkSzZO8A|K;+yBX1*:49CE>VUZ5-+2GqMf.^pO(KBz3.c|p)(/THSOPcWzCW)++|FkdW<7tB_YOB*-Cc++t4Vc.b425f^NFGlRcT+L16C<+FlWB|)L",
		"cC-*BOY_Bt7<WdkF|Spp7^l8*V3pO++RK2-yBF<7pO+J^+VUlz-_9M+ztjd|5FP+&4k/By:cM+UZGW()L#zHJp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|d<M+b+ZR2FBcyA64KHER>pl^VPk|1LTG2d)fK*<.YWD%O#(B+pN9L@+zYN_+O#jfJ2G(U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BF>MDHNpkSzZO8A|K;+RcT+L16C<+FlWB|)L2GqMf.^pO(KBz3.c|p)(/THSOPcWzCW)++yBX1*:49CE>VUZ5-+lGFN^f524b.cV4t++z69Sy#+N|5FBc(;8R",
		"VB#PAfZ%%9/EcS)kNcG%FQXROP5e6##de_Ld/P#B@XqEHMU^RRkB#SQTBGq8OlEYItNRBPDR+j=6\\N(eEUHkF9%P/Z/UB%kOR=pX=BeYq!K@PH96FYGe+VWPOtTV+\\WVB#9AkpUzP5M8RUt%L)NVEKH=GH!FBX9zXADd\\7L!=qrI!Jk598LMlNA)Z(PkIU=)MHEq6IW9rXEV)5DYQNtTq7kIU^YJMcZKqpI)Wq!85LMr9#_tL#%9AYT_EKYGeZVS(/9#BPORAU%fRlqEAQJX@/ed\\rYT_RD9IpeXqWq_F#8c+@9A9B6WQPBr+IJ8_qtTJ@ZZcpOVWI5+tL)l^R6HWqS^_qYd_+cUR5TO%(Sz58Xq6)IK9DGq+@^=SrlfUe67DzG%%IMk^LMZJdr\\pFHVWe8Y",
		"peXqWq_F#8c+@9A9B9%P/Z/UB%kOR=pX=Bk^LMZJdr\\pFHVWe8YEqlRf%UAROPB#9/(S_ed##6e5PORXQF%GcWqS^_qYd_+cUR5TO%VEXr9WI6qEHM)=UIkH!FBX9zXADd\\7L!=qP5M8RUt%L)NVEKH=GI9DR_TYr\\de/@XJQA)5DYQNtTq7kIU^YJMWV+eGYF69HP@K!qYeBPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HPOtTV+\\WVB#9AkpUz(Sz58Xq6)IK9DGq+@MI%%GzD76eUflrS=^VZeGYKE_TYA9%#Lt_Z@JTtq_8JI+rBPQW6Nk)ScE/9%%ZfAP#BVkRR^UMHEqX@B#P/dLrI!Jk598LMlNA)Z(PB#SQTBGq8OlEYItNR#9rML58!qW)IpqKZc",
		"peXqWq_F#8c+@9A9BWqS^_qYd_+cUR5TO%VEXr9WI6qEHM)=UIkk^LMZJdr\\pFHVWe8YVB#PAfZ%%9/EcS)kNkRR^UMHEqX@B#P/dLMI%%GzD76eUflrS=^#9rML58!qW)IpqKZcBPDR+j=6\\N(eEUHkFAQJX@/ed\\rYT_RD9IG=HKEVN)L%tUR8M5PH!FBX9zXADd\\7L!=qZcpOVWI5+tL)l^R6HEqlRf%UAROPB#9/(S6WQPBr+IJ8_qtTJ@ZVZeGYKE_TYA9%#Lt_MJY^UIk7qTtNQYD5)POtTV+\\WVB#9AkpUzcG%FQXROP5e6##de_rI!Jk598LMlNA)Z(PB#SQTBGq8OlEYItNR@+qGD9KI)6qX85zS(B=Xp=ROk%BU/Z/P%9eYq!K@PH96FYGe+VW",
		"6WQPBr+IJ8_qtTJ@Zk^LMZJdr\\pFHVWe8Y^=SrlfUe67DzG%%IM@+qGD9KI)6qX85zS(9%P/Z/UB%kOR=pX=BZcpOVWI5+tL)l^R6HVB#PAfZ%%9/EcS)kN#9rML58!qW)IpqKZcBPDR+j=6\\N(eEUHkFAQJX@/ed\\rYT_RD9IP5M8RUt%L)NVEKH=GLd/P#B@XqEHMU^RRkWqS^_qYd_+cUR5TO%POtTV+\\WVB#9AkpUzB9A9@+c8#F_qWqXep)5DYQNtTq7kIU^YJMq=!L7\\dDAXz9XBF!H_tL#%9AYT_EKYGeZVRNtIYElO8qGBTQS#BkIU=)MHEq6IW9rXEV_ed##6e5PORXQF%GcS(/9#BPORAU%fRlqErI!Jk598LMlNA)Z(PWV+eGYF69HP@K!qYe"
	};
	
	/** maps used to compare algorithmic homophone scores to direct comparisons */
	static Map<String, Integer> scoreMap1;
	static Map<String, Integer> scoreMap2;

	static Map<String, Integer> refMap; // best repeat counts found for a reference cipher (the 408 or 340)
	static String refCipher;
	static { // init the repeat counts so we can use for many comparisons to transformed ciphers.
		
		/*
		System.out.println("init...");
		HomophonesKingBahler h = new HomophonesKingBahler(Ciphers.cipher[HomophonesProblem.which], 0);
		h.translateCipher();
		h.findStrings();
		//h.dumpStrings();
		h.reduceStrings();
		//h.dumpStrings();
		h.scoreStrings(WHICH_SPECIAL_COUNT);
		refMap = h.adjustedScoreMap();
		refCipher = h.cipher;*/
	}
	
	// adjusted score relative to a reference cipher text (see scoreAdjust())
	int adjustedScore = 0;
	
	public HomophonesKingBahler(String cipher) {
		this(cipher, 0);
	}
	
	public HomophonesKingBahler(String cipher, long limit) {
		this.cipher = cipher;
		this.limit = limit;
	}

	/** converts cipher text by replacing symbols with ordinal value of their first occurrence */
	public void translateCipher() {
		positionMap = new HashMap<Integer, List<Integer>>();
		int num=1; translated = new int[cipher.length()]; 
		charmap = new HashMap<Character, Integer>();
		symbols = new HashMap<Integer, Character>();
		char ch;
		for (int i=0; i<cipher.length(); i++) {
			ch = cipher.charAt(i);
			if (charmap.keySet().contains(ch)) {
				;
			} else {
				maxSymbol = num;
				
				symbols.put(num, ch);
				charmap.put(ch, num++);
			}
			translated[i] = charmap.get(ch);
			List<Integer> list = positionMap.get(charmap.get(ch));
			if (list == null) list = new ArrayList<Integer>();
			list.add(i); positionMap.put(charmap.get(ch), list);
		}
	}
	static void testTranslateCipher(String c) {
		HomophonesKingBahler h = new HomophonesKingBahler(c);
		h.translateCipher();
		String s = "";
		for (int i=0; i<h.translated.length; i++) {
			s += h.translated[i] + ", ";
		}
		System.out.println(s);
		
		for (Integer i : h.positionMap.keySet()) {
			s = "positions for " + i + ": ";
			for (Integer j : h.positionMap.get(i)) s += j + ", ";
			System.out.println(s);
		}
		
	}
	
	/** find strings */
	public void findStrings() {
		strings = new ArrayList<List<List<Integer>>>();
		strings.add(null); // unused 0th position

		stringsCopy = new ArrayList<List<List<Integer>>>();
		stringsCopy.add(null); // unused 0th position
		
		// for each symbol
		for (int i=1; i<=maxSymbol; i++) {
			List<List<Integer>> list = new ArrayList<List<Integer>>();
			List<List<Integer>> listCopy = new ArrayList<List<Integer>>();
			List<Integer> positions = positionMap.get(i);
			if (positions.size() > 1) { // if symbol only occurs once, ignore it, since it cannot be in a repeated homophone sequence
				for (int j=0; j<positions.size(); j++) {
					int a = positions.get(j); // start of string ([j]th occurrence of [i]th symbol
					int b = (j==positions.size()-1 ? translated.length : positions.get(j+1)); // end of string ([j+1]th occurrence of [i]th symbol, or end of cipher text)
					List<Integer> string = new ArrayList<Integer>();
					List<Integer> stringCopy = new ArrayList<Integer>();
					for (int k=a; k<b; k++) {
						string.add(translated[k]);
						stringCopy.add(translated[k]);
					}
					list.add(string);
					listCopy.add(stringCopy);
					//System.out.println(i+":"+symbolsFromOrdinals(string));
				}
			}
			strings.add(list);
			stringsCopy.add(listCopy);
		}
	}

	String symbolsFromOrdinals(List<Integer> sequence) {
		//if (sequence.size() > 30) System.out.println("sfo size " + sequence.size());
		StringBuffer sb = new StringBuffer(sequence.size());
		for (Integer i : sequence) sb.append(symbols.get(i));
		return sb.toString();
	}
	
	/** score strings 
	 * whichSpecialCount:  1 for obsoleted special count adjustment, 2 for adjustment based on direct comparison
	 * */ 
	public void scoreStrings(int whichSpecialCount) {
		scores = new ArrayList<Object[]>(); // [string
		int[] indices;

		
		
		int count = 0;
		int num = 1;
		//List<List<List<Integer>>> strings;
		for (List<List<Integer>> stringList : strings) { // for each symbol's set of strings
			//int max = 0; // compute length of longest string in the symbol's set of strings
			//for (List<Integer> list : stringList) max = Math.max(max, list.size());
			//System.out.println("wha... " + count);
			Map<String, Object[]> counts = new HashMap<String, Object[]>(); // map subsequence to its [total, List of strings in which it is found]
			if (stringList == null) {count++; continue;}
			int totalLength = 0;
			for (int k=0; k<stringList.size(); k++) { // for each string
				List<Integer> string = stringList.get(k); totalLength += string.size();
				
				//System.out.println("k " + k + ", symbol " + (string.size() == 0 ? "null" : string.get(0)) + ", num strings " + stringList.size() + ", string " + symbolsFromOrdinals(string));
				//int c1 = combinations;
				for (int len = 2; len <= string.size() && len <= MAX_LENGTH; len++) { 

					// we are testing (n choose k) = (string.size() choose len) combinations.
					// let's impose the limit to avoid searching too many combiantions
					
					
					//System.out.println(num+": len " + len + ": " + binomial(string.size(), len)); 
					
					if (limit > 0) {
						// calculate binomial coefficient
						long combos = binomial(string.size(), len);
						if (combos > limit) continue;
						if (combos < 0) continue; // sometimes the number is so large, the result flips sign.
					}
					
					//System.out.println("len " + len + " size " + string.size());
					//int combo = 0;
					CombinationGenerator x = new CombinationGenerator (string.size(), len);
					List<Integer> combination = new ArrayList<Integer>();
					while (x.hasMore ()) {
					  //combo++;
					  combinations++;
					  //if (combinations % 1000 == 0) {
					//	  System.out.println(combinations + " combinations...");
					  //}
					  indices = x.getNext ();
					  for (int i = 0; i < indices.length; i++) {
						//if (i>0 && i % 10000 == 0) System.out.println("ind " + i);  
					    combination.add(string.get(indices[i]));
					  }
					  String str = symbolsFromOrdinals(combination);
					  Object[] o = counts.get(str); if (o == null) o = new Object[2];
					  Integer val = (Integer) o[0];
					  List<Integer> whichStrings = (List<Integer>) o[1]; 
					  if (val == null) val = 1; else val++;
					  if (whichStrings == null) whichStrings = new ArrayList<Integer>();
					  whichStrings.add(k);
					  o[0] = val; o[1] = whichStrings;
					  /*
					  if (str.equals("DK6")) {
						  System.out.println("dealing with DK6");
						  for (List<Integer> list : stringList) System.out.println("found DK6.  " + symbolsFromOrdinals(list));
					  }*/
					  counts.put(str, o);
					  //System.out.println("Symbol " + count + ": " + str + ", " + o[0]);
					  combination.clear();
					}
					//System.out.println("combos: " + combo);
				}
				num++;
				//int c2 = combinations;
				//System.out.println("Combos " + (c2-c1));

			}
			for (String str : counts.keySet()) {
				if (str.length() > MAX_LENGTH) continue;
				//List<Object[]> scores; // [repeated string, count of strings, count of appearances, ratio of appearances to total strings, F (string length * appearance count)]

				Object[] o = counts.get(str);
				Integer c = (Integer) o[0]; // raw count of appearances
				//System.out.println(c + " occurrences of " + str);
				List<Integer> whichStrings = (List<Integer>) o[1]; // list of appearances
				
				// bias the search towards contiguous appearances that begin with the first string.
				// also, take the total string length into account, since a single non-match
				// might occur on a very long substring.
				boolean found = true;
				int adjustedCount = 0;
				for (Integer i : whichStrings) {
					if (i == adjustedCount) adjustedCount++;
					else break;
				}
				
				// bah, let's just count the repeats
				adjustedCount = c;
				
				// then, add the special cases (removal of a repeated symbol caused at least one repeated sequence to be ignored)
				
				int specialCount = whichSpecialCount == 1 ? specialCount(str) : specialCount2(str);
				//System.out.println(str + ": adjusted " + adjustedCount + " special " + specialCount);
				if (specialCount != adjustedCount) {
					//System.out.println("it was different: " + (specialCount > adjustedCount ? "higher" : "lower") + ".  diff from other special count: " + (specialCount-specialCount(str)));
					if (specialCount > adjustedCount) adjustedCount = specialCount;
				}
				
				// compute ratio of length of contiguous matches to total length of all substrings
				int n = stringList.size(); // # of strings
				
				float ratio = (float) adjustedCount / n;
				/*
				if (totalLength == 0) ratio = 0;
				else {
					ratio = (float) str.length() * adjustedCount / totalLength;
				}*/
				
				//if (adjustedCount == 1) 
				//	System.out.println("SMEG: str " + str + " adjustedCount " + adjustedCount + ", " + totalLength + ", " + str.length() + ", " + str.length() * adjustedCount * ratio);
				
				/*
				if (str.equals("9PUk")) {
					System.out.println("SMEG: " + adjustedCount + ", " + totalLength + ", " + str.length() + ", " + str.length() * adjustedCount * ratio);
				}*/
				
				/*
				if (str.equals("W6")) {
					System.out.println("SMEG: " + stringList.size());
					
					
					System.out.println(" - symbolic form:");
					for (int j=0; j<stringList.size(); j++) {
						String s = "";
						for (int k=0; k<stringList.get(j).size(); k++) {
							s += symbols.get(stringList.get(j).get(k));
						}
						System.out.println(s);
					}
					System.out.println(str + "," + stringList.size() + "," + adjustedCount + "," + ratio + "," + (str.length() * adjustedCount * ratio));
				}*/
				
				Object[] score = new Object[] {
						str, stringList.size(), adjustedCount, ratio, (float) str.length() * adjustedCount * adjustedCount / n, sequenceFor(cipher, str)  
				};
				//System.out.println(str + "," + stringList.size() + "," + adjustedCount + "," + ratio + "," + (str.length() * adjustedCount * ratio));
				scores.add(score);
				
			}
			count++;
		}
		sortScores();
	}
	
	/** remove all cipher symbols except the ones in the input */
	public static String sequenceFor(String cipher, String inp) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<inp.length(); i++) set.add(inp.charAt(i));
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<cipher.length(); i++) {
			if (set.contains(cipher.charAt(i))) sb.append(cipher.charAt(i));
		}
		return sb.toString();
	}

	/** score strings - 2nd part done after running scoreStrings():
	 *  for each symbol, find the highest number of repeated homophones to which it belongs.
	 *  the symbol's new score = # of repeats divided by # of times the symbol is in the cipher text
	 *  total score is sum of ratios for all symbols.  
	 * */ 
	public void scoreStringsBestBySymbol(boolean ignoreNonRepeats) {
		//List<Object[]> scores; // [repeated string, count of strings, count of appearances, ratio of appearances to total strings, F (string length * appearance count * ratio * unseenRatio)]
		bestBySymbol = new HashMap<Integer, Map<Character, Object[]>>();		 
		for (Object[] o : scores) {
			String str = (String) o[0];
			Integer len = str.length();
			Integer count = (Integer) o[2];
			
			if (count == null) continue;
			
			if (ignoreNonRepeats) 
				if (count == 1) continue; // ignore anything that doesn't repeat
			
			//System.out.println("str " + str + " len " + len + " count " + count);
			
			Map<Character, Object[]> best = bestBySymbol.get(len);
			if (best == null) {
				best = new HashMap<Character, Object[]>();
				bestBySymbol.put(len, best);
			}
			
			for (int i=0; i<str.length(); i++) { // for each symbol in the homophone candidate
				char key = str.charAt(i);
				int ord = charmap.get(key); // ordinal (translated) value of this character
				int n = positionMap.get(ord).size(); // number of times this symbol appears in the cipher text
				Object[] val = best.get(key);
				if (val == null) { // Object[] is { homophone string, num repeats, ratio }
					val = new Object[] { str, count, (float)count/n };
					//System.out.println("best for " + key + " is " + str);
				}
				else {
					Integer val1 = (Integer) val[1];
					Float val2 = (Float) val[2];
					if (count > val1) { 
						val[0] = str;
						val[1] = count;
						val[2] = (float) count/n;
						
						//System.out.println("new best for " + key + " is " + str);
					}
				}
				best.put(key, val);
			}
		}
		
		
		int count = 0;
		for (Integer len : bestBySymbol.keySet()) {
			Map<Character, Object[]> best = bestBySymbol.get(len);
			for (Character ch : best.keySet()) {
				Object[] o = best.get(ch);
				//System.out.println(len + ", " + ch + ": [" + o[0] + ", " + o[1] + ", " + o[2] + "]" );
				count += (Integer) o[1];
			}
		}
		bestSum = count;
		//System.out.println("bestSum " + bestSum);
	}
	public void dumpScoresBestBySymbol() {
		int total = 0;
		System.out.println("Best By Symbol:");
		for (Integer len : bestBySymbol.keySet()) {
			Map<Character, Object[]> best = bestBySymbol.get(len);
			int count = 0;
			for (Character ch : best.keySet()) {
				Object[] o = best.get(ch);
				System.out.println(len + ", " + ch + ": [" + o[0] + ", " + o[1] + ", " + o[2] + "]" );
				count += (Integer) o[1];
			}
			System.out.println("Length " + len + ", Coverage: " + count + ", ratio: " + ((float) count / cipher.length()));
			total += count;
		}
		System.out.println("Coverage sum: " + total);
	}
	public void dumpScoresBestBySymbol2() {
		int total = 0;
		//System.out.println("Best By Symbol:");
		for (Integer len : bestBySymbol.keySet()) {
			Map<Character, Object[]> best = bestBySymbol.get(len);
			int count = 0;
			for (Character ch : best.keySet()) {
				Object[] o = best.get(ch);
				//System.out.println(len + ", " + ch + ": [" + o[0] + ", " + o[1] + ", " + o[2] + "]" );
				count += (Integer) o[1];
			}
			//System.out.println("Length " + len + ", Coverage: " + count + ", ratio: " + ((float) count / cipher.length()));
			total += count;
		}
		System.out.println("Coverage sum: " + total);
	}

	/** compute score based on best by symbol */
	public int bestBySymbol() {
		int total = 0;
		//System.out.println("Best By Symbol:");
		for (Integer len : bestBySymbol.keySet()) {
			Map<Character, Object[]> best = bestBySymbol.get(len);
			int count = 0;
			for (Character ch : best.keySet()) {
				Object[] o = best.get(ch);
				//System.out.println(len + ", " + ch + ": [" + o[0] + ", " + o[1] + ", " + o[2] + "]" );
				count += (Integer) o[1];
			}
			//System.out.println("Length " + len + ", Coverage: " + count + ", ratio: " + ((float) count / cipher.length()));
			total += count;
		}
		return total;
	}

	
	/** adjust scores based on direct comparisons to repeated sequences in the original reference cipher text */
	void scoreAdjust(String referenceCipher) {
		adjustedScore = 0;

		Map<String, Integer> newMap = adjustedScoreMap();
		
		/* if a repeated string in the reference cipher does not repeat in the new cipher, then punish the score. */
		for (String str : refMap.keySet()) {
			if (!newMap.keySet().contains(str)) {
				Integer refVal = refMap.get(str);
				// does it appear but we just didn't catch it via remove_homophones?
				Integer newVal = actualDifference(cipher, str);
				//System.out.println("str " + str + " was in ref but not new.  newVal " + newVal + " refVal " + refVal + " diff " + (newVal - refVal));
				adjustedScore += (newVal - refVal); // add or subtract the differences 
			}
		}
		
		for (String str : newMap.keySet()) {
			Integer newVal = newMap.get(str);
			Integer refVal = refMap.get(str);
			if (refVal == null) {
				// does it appear but we just didn't catch it via remove_homophones?
				refVal = actualDifference(refCipher, str);
				//System.out.println("str " + str + " was in new but not ref.  newVal " + newVal + " refVal " + refVal + " diff " + (newVal - refVal));
			}
			else {
				//System.out.println("str " + str + " was in ref and new.  newVal " + newVal + " refVal " + refVal + " diff " + (newVal - refVal));
				adjustedScore += (newVal - refVal); // add or subtract the differences 
			}
		}
		//System.out.println("adjustedScore: " + adjustedScore);
	}
	
	
	
	void sortScores() {
		Collections.sort(scores, new ScoreComparator());
	}

	void dumpScores() {
		dumpScores(scores);
	}
	
	
	void dumpTop() {
		dumpScores(top);
	}
	
	/** ordinal version of given string, in displayable format */
	String toOrd(String str) {
		if (str == null || str.length() == 0) return null;
		StringBuffer result = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			int c = charmap.get(str.charAt(i));
			if (c<10) result.append("0");
			result.append(c);
			result.append(" ");
		}
		return result.toString();
	}
	
	
	void dumpScores(List<Object[]> list) {
		System.out.println("dumping scores");
		float sum = 0; 
		for (Object[] o : list) {
			//if ((Integer)o[2] < 2) continue;
			String s = "";
			for (int i=0; i<o.length; i++) {
				if (i==0) {
					s += ((String) o[i]).length() + ",";
					s += toOrd((String) o[i]) + ",";
				}
				s+= o[i] + ",";
			}
			String str = (String) o[0];
			float score = scoreReal(str);
			sum += score;
			s += "" + score;
			
			System.out.println(s);
		}
		System.out.println("scoreReal sum: " + sum + ", avg: " + (sum/list.size()));
	}
	
	/* return "how much of this matches a real homophone set of the 408" score */
	static float scoreReal(String str) {
		int count = 0; 
		Set<Character> phones = new HashSet<Character>();
		for (int i=0; i<str.length(); i++) {
			char c1 = str.charAt(i);
			int j = Ciphers.alphabet[1].indexOf(c1);
			if (j<0) return -1;
			char c2 = Ciphers.solutions[1].charAt(j);
			phones.add(c2);
		}
		
		// phones size 1: score is 1
		// phones size str.length(); score is 0
		
		return (float) (str.length() - phones.size())/(str.length()-1);
	}
	
	
	/* greedily select the top scoring n disjoint sets of homophone candidates */
	/* homophone sets with cardinality not equal to [m] are ignored.
	 * if disjoint is false, then include non-disjoint sets.
	 **/
	List<Object[]> topDisjoint(int n, int m, boolean disjoint) {
		Map<Character, Integer> seen = new HashMap<Character, Integer>();
		
		List<Object[]> results = new ArrayList<Object[]>();
	
		int i=0;
		while (i<scores.size() && results.size() < n) {
			Object[] o = scores.get(i);
			String str = (String)o[0];
			
			if (str.length() != m) { i++; continue;} // ignore this string if it doesn't match the length we want
			
			
			boolean add = true;
			for (int j=0; j<str.length(); j++) {
				if (disjoint && seen.keySet().contains(str.charAt(j))) {
					add = false;
					continue;
				}
			}
			//System.out.println("str " + str + " add " + add);
			
			if (add) { 
				int numUnseen = 0;
				int numSeenTotal = 0;
				for (int j=0; j<str.length(); j++) {
					char ch = str.charAt(j);
					if (!seen.keySet().contains(ch)) numUnseen++;
					Integer numSeen = seen.get(ch);
					if (numSeen == null) numSeen = 1;
					else numSeen++;
					seen.put(ch, numSeen);
					numSeenTotal += numSeen;
				}
				float factor = numSeenTotal == 0 ? 0 : ((float) numUnseen) / numSeenTotal;
				//System.out.println("str " + str + " score " + o[4] + " numUnseen " + numUnseen + " numSeenTotal " + numSeenTotal + " factor " + factor);
				if (HomophonesProblem.SEEN_FACTOR) o[4] = ((Float) o[4]) * factor;
				if ((Float)o[4] > 0 && (Integer)o[2] > 1) // exclude any result that contain no repeats, and any score that scores zero (due to being non-disjoint to already seen homophone candidates) 
					results.add(o);
			}

			i++;
		}
		top = results;
		return results;
	}
	
	void dumpStrings() {
		for (int i=1; i<strings.size(); i++) {
			System.out.println("symbol " + i + " [" + symbols.get(i) + "]");
			System.out.println(" - numeric form:");
			for (int j=0; j<strings.get(i).size(); j++) {
				String s = "";
				for (int k=0; k<strings.get(i).get(j).size(); k++) {
					if (strings.get(i).get(j).get(k) < 10) s += "0";
					s += strings.get(i).get(j).get(k) + " ";
				}
				System.out.println(s);
			}
			System.out.println(" - symbolic form:");
			for (int j=0; j<strings.get(i).size(); j++) {
				String s = "";
				for (int k=0; k<strings.get(i).get(j).size(); k++) {
					s += symbols.get(strings.get(i).get(j).get(k));
				}
				System.out.println(s);
			}
		}
	}
	
	public static void testFindStrings(String c) {
		Date d1 = new Date();
		HomophonesKingBahler h = new HomophonesKingBahler(c, 0);
		h.translateCipher();
		h.findStrings();
		h.reduceStrings();
		h.dumpStrings();
		h.scoreStrings(WHICH_SPECIAL_COUNT);
		//h.scoreAdjust(Ciphers.cipher[HomophonesProblem.which]);
		h.scoreStringsBestBySymbol(true);
		h.dumpScores();
		System.out.println("Combinations tested: " + h.combinations);

		/*
		System.out.println("Dumping top scores only:");
		float[] scores = new float[8];
		for (int len=2; len<10; len++) {
			h.topDisjoint(NUM_DISJOINT, len, false);
			System.out.println("Top " + NUM_DISJOINT);
			System.out.println("Homophones,number of strings,number of repeats,proportion of repeats to sum of string lengths,heuristic score (num strings * contig count * proportion)");
			h.dumpTop();
			h.scoreTop();
			System.out.println("Score [" + h.topScores[0] + "," + h.topScores[1] + "," + h.topScores[2] + "," + h.topScores[3] + "]");
			scores[len-2] = h.topScores[0];
			System.out.println("Max coverage: [" + h.maxCoverage() + "]");
		}
		int nonZero = 0;
		for (int i=0; i<scores.length; i++) if (scores[i]>0) nonZero++;
		System.out.println("Scores: [" + scores[0] + "," + scores[1] + "," + scores[2] + "," + scores[3] + "," + scores[4] + "," + scores[5] + "," + scores[6] + "," + scores[7] + "]. Num non-zero: " + nonZero);
		System.out.println("Sum of scores: " + (scores[0] + scores[1] + scores[2] + scores[3] + scores[4] + scores[5] + scores[6] + scores[7]));
		Date d2 = new Date();
		System.out.println(d2.getTime() - d1.getTime() + " ms");
		System.out.println("Dumping all scores:");
		h.dumpScores();*/
		h.dumpScoresBestBySymbol();
		System.out.println("adjustedScore: " + h.adjustedScore);
	}
	
	public static void testFindStrings2(String c, String msg) {
		Date d1 = new Date();
		HomophonesKingBahler h = new HomophonesKingBahler(c, 0);
		h.translateCipher();
		h.findStrings();
		h.reduceStrings();
		h.dumpStrings();
		h.scoreStrings(WHICH_SPECIAL_COUNT);
		//h.scoreAdjust(Ciphers.cipher[HomophonesProblem.which]);
		h.scoreStringsBestBySymbol(true);
		h.dumpScores();
		System.out.println("Combinations tested: " + h.combinations);

		System.out.println(h.bestBySymbol() + "," + msg);
	}
	
	/** perform reductions of each string */
	void reduceStrings() {
		for (int i=1; i<strings.size(); i++) {
			//boolean permitOne = true;
			for (int j=0; j<strings.get(i).size(); j++) {
				List<Integer> string = strings.get(i).get(j);
				reduceString1(string);
				//permitOne = reduceString2(string, permitOne);
				reduceString2(string, REDUCE_STRING2_N);
			}
		}
		//System.out.println("After");
		//dumpStrings();
		for (int i=1; i<strings.size(); i++) {

			//if (i==11) dumpStrings();
			//for (int j=0; j<strings.get(i).size(); j++) {
			reduceString3(strings.get(i), HomophonesProblem.F);
			//if (i==11) dumpStrings();
			//}
		}
		
	}
	
	/** remove any symbol that is less than the first symbol in the string */
	static void reduceString1(List<Integer> string) {
		if (string.size() < 2) return;
		int val = string.get(0);
		for (int i=string.size()-1; i>0; i--) {
			if (string.get(i) < val) string.remove(i);
		}
	}

	/** remove any symbol that occurs more than n times in the string */
	static void reduceString2(List<Integer> string, int n) {
		int b = string.size();
		if (string == null) return;
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		for (Integer key : string) {
			Integer val = counts.get(key);
			if (val == null) val = 0;
			val++;
			counts.put(key, val);
		}
		
		List<Integer> newList = new ArrayList<Integer>();
		for (int i=0; i<string.size(); i++) {
			if (counts.get(string.get(i)) <= n) {
				newList.add(string.get(i));
			}
		}
		string.clear();
		string.addAll(newList);
		//System.out.println("before " + b + " after " + string.size());
		
		/*
		
		int b = string.size();
		//boolean forgive = permitOne;
		if (string.size() < 2) return;
		// map symbols to first occurrence 
		Map<Integer, Integer> map1 = new HashMap<Integer, Integer>();
		// map symbols to second occurrence 
		Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
		//Set<Integer> seen = new HashSet<Integer>(); // queue of symbols to delete
		
		for (int i=string.size()-1; i>0; i--) {
			Integer j = string.get(i); // symbol
			Integer first = map1.get(j); // first position for symbol [j]
			Integer second = map2.get(j); // second position 
			//System.out.println("i " + i+" j "+j+" first "+first+" size "+string.size());
			if (first == null) first = i;
			else if (second == null) second = i;
			else {
					// seen this symbol before.
						// delete first occurrence, and second occurrence, and delete current occurrence.
					if (first < string.size()) string.set(first, -1);
					if (second < string.size()) string.set(second, -1);
					string.set(i, -1);
				}
			map1.put(j, first);
			map2.put(j, second);
			//System.out.println(dumpArray(string));
		}
		List<Integer> newList = new ArrayList<Integer>();
		for (int i=0; i<string.size(); i++) if (string.get(i) > -1)newList.add(string.get(i));
		string.clear();
		string.addAll(newList);
		System.out.println("before " + b + " after " + string.size());
		*/
	}
	
	/** there are n strings.  remove any symbols that do not appear in at least the first F strings. */
	static void reduceString3(List<List<Integer>> strings, int F) {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		int num = 0;
		for (List<Integer> list : strings) {
			for (Integer i : list) {
				Integer count = counts.get(i);
				if (count == null) count = 1;
				else count++;
				counts.put(i, count);
			}
			num++;
			if (num == F) break;
		}
		
		//int min = strings.size() < 4 ? strings.size() : strings.size() - 1;
		int min = Math.min(F, strings.size());
		for (List<Integer> list : strings) {
			for (int i=list.size()-1; i>=0; i--) {
				Integer count = counts.get(list.get(i)); 
				if (count == null || count < min)
					list.remove(i);
			}
		}

		
	}
	
	/** there are n strings.  remove any symbols that do not appear in at least F strings. */
	static void reduceString4(List<List<Integer>> strings, int F) {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		int num = 0;
		for (List<Integer> list : strings) {
			for (Integer i : list) {
				Integer count = counts.get(i);
				if (count == null) count = 1;
				else count++;
				counts.put(i, count);
			}
			num++;
			//if (num == F) break;
		}

		/*boolean go = false;
		if (strings.size() > 0 && strings.get(0).size() > 0 && strings.get(0).get(0) == 11) {
			go = true;
			System.out.println("Frack");
			for (Integer key : counts.keySet()) System.out.println("smeg " + key + ": " + counts.get(key));
		}*/
		
		int min = strings.size()/2 + 2; // a majority of strings
		min = Math.max(F, min); // enforce F as a minimum.
		if (min > strings.size()) min = strings.size(); // fewer than F strings in the set, so all must match
		//int min = (int) Math.max(F, strings.size()*HomophonesProblem.R);
		//min = Math.min(min, strings.size());
		//if (go) System.out.println("min " + min);
		
		//int c = 0;
		for (List<Integer> list : strings) {
			for (int i=list.size()-1; i>=0; i--) {
				Integer count = counts.get(list.get(i)); 
				if (count == null || count < min) {
					//if (go) System.out.println("removing " + c + " " + i + ", count " + count);
					list.remove(i);
				}
			}
			//c++;
		}

		
	}

	/** there are n strings.  remove any symbols that do not appear in at least F *contiguous* strings. */
	static void reduceString5(List<List<Integer>> strings, int F) {
		
		if (strings == null) return;
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>(); // map symbol to continguous counts
		Map<Integer, Integer> max = new HashMap<Integer, Integer>(); // max contiguous counts
		Set<Integer> current; // current string's set of symbols
		Set<Integer> previous = null; // the previous string's set of symbols
		for (int i=0; i<strings.size(); i++) {
			if (strings.get(i) == null) continue;
			current = new HashSet<Integer>();
			for (int j=1; j<strings.get(i).size(); j++) {
				int k=strings.get(i).get(j);
				current.add(k);
				if (i==0) {// first string
					counts.put(k, 1);
					max.put(k, 1);
				}
				else if (previous.contains(k)) { // this symbol has appeared immediately before, so increment the count 
					int val = counts.get(k);
					val++; 
					counts.put(k, val);
					if (max.get(k) == null) max.put(k, val);// update best found
					else max.put(k, Math.max(max.get(k),val)); 
				} else { // symbol did not appear immediately before, so reset the count
					counts.put(k, 1);
					if (max.get(k) == null) max.put(k, 1);// update best found
					else max.put(k, Math.max(max.get(k),1)); 
				}
			}
			// did symbols from the previous string NOT appear in the current string?  if so, reset counts
			if (i>0) {
				for (Integer k : previous) {
					if (!current.contains(k)) {
						counts.put(k, 0);
					}
				}
			}
			previous = new HashSet<Integer>();
			previous.addAll(current);
			
		}

		/*
		if (strings.size() > 0 && strings.get(0) != null) {
			System.out.println("frack " + strings.get(0).get(0));
			for (int k : max.keySet()) {
				System.out.println("smeg " + k+","+max.get(k));
			}
			
		}*/
		for (List<Integer> list : strings) {
			for (int i=list.size()-1; i>0; i--) {
				Integer count = max.get(list.get(i)); 
				if (count == null || count < F) {
					list.remove(i);
				}
			}
		}

		
	}
	
	
	static String dumpArray(List a) {
		String s = "";
		for (int i=0; i<a.size(); i++) s+= a.get(i) + ",";
		return s;
	}
	
	
	static void testExample() {
		String s = "";
		String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ012345678";
		for (int i=0; i<example.length; i++) s += symbols.charAt(example[i]-1);
		System.out.println(s);
		testTranslateCipher(s);
		testFindStrings(s);
	}
	
	static void testReduceString2() {
		Integer[] s = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 5, 13, 14, 15, 16, 17, 18, 19, 4, 5, 20, 21, 22, 2, 3, 8};
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(s));
		reduceString2(list, REDUCE_STRING2_N);
		String str = "";
		for (int i=0; i<list.size(); i++) str += list.get(i) + ", ";
		System.out.println(str);
	}
	
	static void testCipher() {
		String s = Ciphers.cipher[0].cipher;
			//"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-"; // this subset of the 340 causes many substring combinations
		System.out.println(s);
		testTranslateCipher(s);
		testFindStrings(s);
	}

	/** evaluate the given cipher by returning the top 100 disjoint candidate homophone sets, and the number of combinations tested */
	public static float[] evaluateCipher(String cipher) {
		HomophonesKingBahler h = new HomophonesKingBahler(cipher);
		h.translateCipher();
		h.findStrings();
		//h.dumpStrings();
		h.reduceStrings();
		//h.dumpStrings();
		h.scoreStrings(WHICH_SPECIAL_COUNT);
		//h.dumpScores();
		h.topDisjoint(NUM_DISJOINT, 3, false);
		h.scoreTop();
		
		float[] result = {h.topScores[0], h.topScores[1], h.topScores[2], h.topScores[3], h.combinations};
		//System.out.println("Top 100: [" + scores[0] + "," + scores[1] + "]");
		//h.dumpTop();
		//System.out.println("Combinations tested: " + h.combinations);
		return result;
	}

	/** pull heuristic sums for individual set cardinalities for use by heuristic search */
	public static float[][] evaluateCipher2(String cipher) {
		HomophonesKingBahler h = new HomophonesKingBahler(cipher);
		h.translateCipher();
		h.findStrings();
		//h.dumpStrings();
		h.reduceStrings();
		//h.dumpStrings();
		h.scoreStrings(WHICH_SPECIAL_COUNT);
		//h.dumpScores();

		float[][] scores = new float[8][2];
		for (int len=2; len<10; len++) {
			scores[len-2] = new float[2];
			h.topDisjoint(NUM_DISJOINT, len, false);
			//System.out.println("Top 100");
			//System.out.println("Homophones,number of strings,number of repeats,proportion of repeats to sum of string lengths,heuristic score (num strings * contig count * proportion)");
			//h.dumpTop();
			h.scoreTop();
			//System.out.println("Score [" + h.topScores[0] + "," + h.topScores[1] + "," + h.topScores[2] + "," + h.topScores[3] + "]");
			scores[len-2][0] = h.topScores[0];
			scores[len-2][1] = h.scoreRealTop();
		}
		
		return scores;
		
		
	}
	
	
	public static void testEvaluateCipher(String cipher) {
		float[] scores = evaluateCipher(cipher);
		System.out.println(cipher);
		System.out.println("[" + scores[0] + "," + scores[1] + "," + scores[2] + "," + scores[3] + "," + scores[4] + "] ");
	}
	
	/** return sum and average of the top candidates */
	void scoreTop() {
		float[] result = {0,0,0,0};
		for (Object[] o : top) {
			Float f = (Float) o[4];
			String s = (String) o[0];
			result[0] += f;
			result[1] ++;
			if (s.length() == 3) result[2]++;
			else if (s.length() > 3) result[3]++;
		}
		if (result[1] > 0) result[1] = result[0]/result[1];
		topScores = result;
	}
	
	/** determine max coverage per symbol.  expressed as ratio to total cipher length */
	float maxCoverage() {
		Map<Character, Integer> bestCover = new HashMap<Character, Integer>(); // for each symbol, track its max # of repetitions
		 
		for (Object[] o : top) {
			String str = (String) o[0];
			Integer count = (Integer) o[2];
			for (int i=0; i<str.length(); i++) {
				Character ch = str.charAt(i);
				Integer val = bestCover.get(ch);
				if (val == null) val = 0;
				val = Math.max(val, count);
				bestCover.put(ch, val);
			}
		}
		
		int total = 0;
		for (Character key : bestCover.keySet()) total += bestCover.get(key);
		return (float) total / cipher.length();
		
	}
	
	float scoreRealTop() {
		float sum = 0; 
		for (Object[] o : top) {
			String s = "";
			String str = (String) o[0];
			float score = scoreReal(str);
			sum += score;
		}
		return sum;
	}
	
	// Evaluate n!
    public static long factorial( long n )
    {
        if( n <= 1 )     // base case
            return 1;
        else
            return n * factorial( n - 1 );
    }
    
    // compute binomial coefficient
    public static long binomial(int n, int k) { // n choose k
    	return factorial(n) / (factorial(n - k) * factorial(k));    	
    }
    
    /** obsolete method of adjusting, due to quirks of remove_homophones, the count of repeated sequences */
    public int specialCount(String str) {
    	if (str == null) return 0;
    	int count = 0;
    	
    	Set<Integer> symbols = new HashSet<Integer>();
    	for (int i=0; i<str.length(); i++) symbols.add(charmap.get(str.charAt(i)));
    	
    	
    	List<List<Integer>> set = strings.get(charmap.get(str.charAt(0))); // get set of strings starting with this symbol
    	for (List<Integer> string : set) {
    		//String s1 = "";
    		//String s2 = "";
    		List<Integer> newString = new ArrayList<Integer>();
    		for (Integer i : string) {
    			//s1 += this.symbols.get(i);
    			if (symbols.contains(i)) {
    				newString.add(i);
    				//s2 += this.symbols.get(i);
    			}
    		}
    		//System.out.println("s1 " + s1 + " s2 " + s2);
    		
    		if (newString.size() >= str.length()) {
        		boolean found = true;
        		for (int i=0; i<newString.size(); i++) {
        			if (i>=str.length()) { found = false; break; }
        			if (newString.get(i) != charmap.get(str.charAt(i))) { found = false; break; }
        		}
        		if (found) count++;
    		}
    	}
    	//System.out.println("special count for " + str + " is " + count);
    	return count;
    }
    
    /** count the repeated sequences directly when remove_homophones finds homophone candidates.  this is because
     * remove_homophones may have ignored some repeated sequences due to its string reduction rules.
     */
    public int specialCount2(String str) {
    	return matches(sequenceFor(cipher, str), str);
    }
    
	class ScoreComparator implements Comparator {
		public int compare(Object o1, Object o2) {
//			List<Object[]> scores; // [repeated string, count of strings, count of appearances, ratio of appearances to total strings, F (string length * appearance count)] 
			Float f1 = (Float) (((Object[]) o1)[4]);
			Float f2 = (Float) (((Object[]) o2)[4]);
			if (f1 < f2) return 1;
			if (f2 < f1) return -1;
			return 0;
		}
	}

	/** given the two strings, find all the common substrings, and return a score which is a sum of the lengths of all such substrings.
	 * this gives an estimate of how similar the two strings are to each other. */
	public static int lcsScore(String s1, String s2) {
		System.out.println("s1 [" + s1 + "]");
		System.out.println("s2 [" + s2 + "]");
		Object[] lcs1;
		Object[] lcs2;
		
		int total = 0;
		if (s1.length() == 0 || s2.length() == 0) return total; // stopping condition
		
		// s1 shares a substring with s2, and/or its reverse.  choose the one that produces the longest matched substring. */
		lcs1 = Algorithms.longestCommonSubstring(s1, s2);
		lcs2 = Algorithms.longestCommonSubstring(s1, Algorithms.reverseIt(s2));
		String sub1 = (String) lcs1[0];
		String sub2 = (String) lcs2[0];
		
		System.out.println("sub1 [" + sub1 + "] pos1 [" + lcs1[1] + "] pos2 [" + lcs1[2] + "]");
		System.out.println("sub2 [" + sub2 + "] pos1 [" + lcs2[1] + "] pos2 [" + lcs2[2] + "]");

		if (sub1.length() < 10 && sub2.length() < 10) return total; // one must produce non-zero substring match 
		
		StringBuffer sb1, sb2;
		if (sub1.length() >= sub2.length()) {
			total = sub1.length();
			sb1 = new StringBuffer(s1);
			sb2 = new StringBuffer(s2);
			int pos1 = (Integer) lcs1[1];
			int pos2 = (Integer) lcs1[2];
			sb1.delete(pos1, pos1+sub1.length());
			sb2.delete(pos2, pos2+sub1.length());
		} else {
			System.out.println(" - Reverse...");
			total = sub2.length();
			sb1 = new StringBuffer(s1);
			sb2 = new StringBuffer(Algorithms.reverseIt(s2));
			int pos1 = (Integer) lcs2[1];
			int pos2 = (Integer) lcs2[2];
			sb1.delete(pos1, pos1+sub2.length());
			sb2.delete(pos2, pos2+sub2.length());
		}
		return total + lcsScore(sb1.toString(), sb2.toString());
    }
	
	/** homophone detection often doesn't find legitimate repeats.  For example:
	 * the sequence GSGSGSGSGSSGG has 5 repeats of GS but only 4 are counted because of the appearance of SS (rule 2 removes these).
	 * so this method computes the real difference between the number of repeats found in two cipher texts (one being a transformation of the other) 
	 * @param s1 first cipher text (reference/original)
	 * @param s2 second cipher text (experiment/transformed)
	 * @param seq homophone candidate being tested
	 */
	public static int[] actualDifference(String s1, String s2, String seq, int thresholdCount, float thresholdRatio) {
		
		/** generate all rearrangements of characters in seq */
		List<String> combos = new ArrayList<String>();
		Algorithms.permuteString(combos, "", seq);
		//for (String s : combos) System.out.println("permutation " + s);

		int maxc1 = 0;
		int maxc2 = 0;
		String maxd1 = null;
		String maxd2 = null;
		String maxs1 = null;
		String maxs2 = null;
		
		for (String s : combos) {
			String d1 = sequenceFor(s1, s);
			String d2 = sequenceFor(s2, s);
			int c1 = matches(d1, s);
			int c2 = matches(d2, s);
			if (c1 > maxc1) {
				maxc1 = c1;
				maxd1 = d1;
				maxs1 = s;
			}
			if (c2 > maxc2) {
				maxc2 = c2;
				maxd2 = d2;
				maxs2 = s;
			}
		}
		float cov1 = (float) seq.length()*maxc1 / maxd1.length(); 
		float cov2 = (float) seq.length()*maxc2 / maxd2.length(); 
		if (maxc1 < thresholdCount || cov1 < thresholdRatio) { maxc1 = 0; maxd1 = null; maxs1 = null; }
		if (maxc2 < thresholdCount || cov2 < thresholdRatio) { maxc2 = 0; maxd2 = null; maxs2 = null; }
		if (maxc1 == 0 && maxc2 == 0) return new int[] {0,0};
		System.out.println("seq " + seq + " len " + seq.length() + ", " + maxs1 + ", "+maxc1+", "+maxd1+", "+maxs2 + ", " + maxc2+", "+maxd2+": "+(maxc2-maxc1) + ". Score1 " + scoreMap1.get(maxs1) + ", Score2 " + scoreMap2.get(maxs2));
		return new int[] {maxc1,maxc2};
	}
	/** one-cipher version. returns repeat count.  */
	public static int actualDifference(String s1, String seq) {
		
		/** generate all rearrangements of characters in seq */
		List<String> combos = new ArrayList<String>();
		Algorithms.permuteString(combos, "", seq);
		//for (String s : combos) System.out.println("permutation " + s);

		int maxc1 = 0;
		String maxd1 = null;
		String maxs1 = null;
		
		for (String s : combos) {
			String d1 = sequenceFor(s1, s);
			int c1 = matches(d1, s);
			if (c1 > maxc1) {
				maxc1 = c1;
				maxd1 = d1;
				maxs1 = s;
			}
		}
		return maxc1;
	}
	
	/** count the number of times the given search string appears in the given string */
	public static int matches(String str, String findStr) {
		int lastIndex = 0;
		int count =0;

		while(lastIndex != -1){

		       lastIndex = str.indexOf(findStr,lastIndex);

		       if( lastIndex != -1){
		             count ++;
		             lastIndex++;
		      }
		}	
		return count;
	}

	/** from the two cipher texts, determine the list of sequences for which to compare actual differences */
	public static List<String> sequencesToTest(String s1, String s2, int thresholdCount, float thresholdRatio) {
		
		scoreMap1 = new HashMap<String, Integer>();
		scoreMap2 = new HashMap<String, Integer>();
		
		HomophonesKingBahler h1 = new HomophonesKingBahler(s1, 0);
		h1.translateCipher();
		h1.findStrings();
		h1.reduceStrings();
		h1.scoreStrings(WHICH_SPECIAL_COUNT);
		h1.dumpScores();
		HomophonesKingBahler h2 = new HomophonesKingBahler(s2, 0);
		h2.translateCipher();
		h2.findStrings();
		h2.reduceStrings();
		h2.scoreStrings(WHICH_SPECIAL_COUNT);
		h2.dumpScores();
		
		List<String> results = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		
		for (Object[] o : h1.scores) {
			String str = (String) o[0];
			int count = (Integer) o[2];
			scoreMap1.put(str, count);
			if (count < thresholdCount) continue;
			float ratio = (float) count*str.length() / ((String)o[5]).length();
			if (ratio < thresholdRatio) continue;
			String sorted = Algorithms.sortInternal(str);
			if (!set.contains(sorted)) {
				results.add(str);
				set.add(sorted);
			} 
		}
		for (Object[] o : h2.scores) {
			String str = (String) o[0];
			int count = (Integer) o[2];
			scoreMap2.put(str, count);
			if (count < thresholdCount) continue;
			float ratio = (float) count*str.length() / ((String)o[5]).length();
			if (ratio < thresholdRatio) continue;
			String sorted = Algorithms.sortInternal(str);
			if (!set.contains(sorted)) {
				results.add(str);
				set.add(sorted);
			}
		}
		return results;
	}
	
	/** produce map of adjusted scores.  an adjusted score is the maximum number of repeats, directly determined via direct inspection,
	 * found for any permutation of symbols in a sequence found by remove_homophones.
	 */
	Map<String, Integer> adjustedScoreMap() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (Object[] o : scores) {
			String str = (String) o[0];
			String key = Algorithms.sortInternal(str);
			Integer count = (Integer) o[2];
			if (count < 2) continue; // ignore non-repeats
			Integer val = map.get(key);
			if (val == null) val = 0;
			val = Math.max(val, actualDifference(cipher, str));
			map.put(key, val);
		}
		//System.out.println("=====");
		//for (String key: map.keySet()) System.out.println("adjusted map " + key + ": " + map.get(key));
		return map;
	}
	
	public static void testLCS() {
		System.out.println(" -- 408s:");
		String s1 = Ciphers.cipher[1].cipher;
		//String s2 = "9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVBX9zXADd\\7L!=qpeXqWq_F#8c+@9A9B##6e5PORXQF%GcVZ_H%OT5RUc+_dYq_^SqWTtq_8JI+rBPQW6E@e!VZeGYKE_TYA9%#Lt_r9WI6qEHM)=UIkXJdF";
		String s2 = "VB#PAfZ%%9/EcS)kNMI%%GzD76eUflrS=^POtTV+\\WVB#9AkpUzP(Z)ANlML895kJ!IrG=HKEVN)L%tUR8M5PAQJX@/ed\\rYT_RD9IH6R^l)Lt+5IWVOpcZFkHUEe(N\\6=j+RDPB#9rML58!qW)IpqKZckRR^UMHEqX@B#P/dLB#SQTBGq8OlEYItNR(Sz58Xq6)IK9DGq+@Y8eWVHFp\\rdJZML^kEqlRf%UAROPB#9/(S)5DYQNtTq7kIU^YJMeYq!K@PH96FYGe+VWB=Xp=ROk%BU/Z/P%9kIU=)MHEIODYd86WQPBr+qJPAT_#cG%FQXR685X_+Fq=!L7\\dI_ezEc__tL#%9AWq69KUqWqS^_qY9t#XYRWB9A9@+crT#BG5qXJdFeTXE@e!ZOeVZ_HV%p";
		String lcs = (String) Algorithms.longestCommonSubstring(s1, s2)[0];
		System.out.println(lcs.length() + ": " + lcs);
		System.out.println(lcsScore(s1,s2));
		
		System.out.println(" -- 340s:");
		s1 = Ciphers.cipher[0].cipher;
		s2 = "d2GTL1|HER>pl^VP)fK*<.YNp+B(#O%DJHz#L)(By:cM+UZG2KR++OpSpp7^l8*V/k4&+PF_9M+ztjd|(D2>FkCp8R^FlO-*|Lz.VGX#5+Kq%;2U9L@+zYN(G2Jfj#O+K46AycBd<M+b+ZR2-yBF<7p-zlUV+^J+OKMTbpBU+R/5tE|DFB&+.M42<clRJ|*5R8;(cBFz69Sy#+N|++t4Vc.lGFN^f524+-5ZUV>yBX1*:49C|FkdW<7tB2GqMf.^+;K|A8O|c.3zBK(O>MDHNpkSzL)|BWlFcC-*BOYRcT+L16C<"; 
		lcs = (String) Algorithms.longestCommonSubstring(s1, s2)[0];
		System.out.println(lcs.length() + ": " + lcs);
		System.out.println(lcsScore(s1,s2));
		
		
	}
	
	public static void testSequenceFor() {
		String s1 = Ciphers.cipher[1].cipher;
		String[] s = new String[] {"!d"};
		for (int i=0; i<s.length; i++) System.out.println(sequenceFor("VB#PAfZ%%9/EcS)kNMI%%GzD76eUflrS=^POtTV+\\WVB#9AkpUzP(Z)ANlML895kJ!IrG=HKEVN)L%tUR8M5PAQJX@/ed\\rYT_RD9IH6R^l)Lt+5IWVOpcZFkHUEe(N\\6=j+RDPB#9rML58!qW)IpqKZckRR^UMHEqX@B#P/dLB#SQTBGq8OlEYItNR(Sz58Xq6)IK9DGq+@Y8eWVHFp\\rdJZML^kEqlRf%UAROPB#9/(S)5DYQNtTq7kIU^YJMeYq!K@PH96FYGe+VWB=Xp=ROk%BU/Z/P%9kIU=)MHEIODYd86WQPBr+qJPAT_#cG%FQXR685X_+Fq=!L7\\dI_ezEc__tL#%9AWq69KUqWqS^_qY9t#XYRWB9A9@+crT#BG5qXJdFeTXE@e!ZOeVZ_HV%p", s[i]));
	}
	
	public static void testSequencesToTest() {
		String s1 = Ciphers.cipher[1].cipher;
		String s2 = "VB#PAfZ%%9/EcS)kNMI%%GzD76eUflrS=^POtTV+\\WVB#9AkpUzP(Z)ANlML895kJ!IrG=HKEVN)L%tUR8M5PAQJX@/ed\\rYT_RD9IH6R^l)Lt+5IWVOpcZFkHUEe(N\\6=j+RDPB#9rML58!qW)IpqKZckRR^UMHEqX@B#P/dLB#SQTBGq8OlEYItNR(Sz58Xq6)IK9DGq+@Y8eWVHFp\\rdJZML^kEqlRf%UAROPB#9/(S)5DYQNtTq7kIU^YJMeYq!K@PH96FYGe+VWB=Xp=ROk%BU/Z/P%9kIU=)MHEIODYd86WQPBr+qJPAT_#cG%FQXR685X_+Fq=!L7\\dI_ezEc__tL#%9AWq69KUqWqS^_qY9t#XYRWB9A9@+crT#BG5qXJdFeTXE@e!ZOeVZ_HV%p";
		List<String> s = sequencesToTest(s1, s2, 5, 0.75f);
		for (String str : s) System.out.println(str);
	}
	public static void testActualDifference(int thresholdCount, float thresholdRatio) {
		String s1 = Ciphers.cipher[0].cipher;
		String s2 = "*V3pO++RK28l^7ppSZGW()L#zHJU+Mc:yB%DWY.<*Kf)O#(B+pNVPk|1LTG2d^lp>REH+;K|A8OZzSkpNHDM>cC-*BOY_Bt7<WdkF|p)(/THSOPcWzCW)++L)|BWlF+<C61L+TcR2GqMf.^pO(KBz3.c|+-5ZUV>EC94:*1XBy++t4Vc.b425f^NFGlR8;(cBF5|N+#yS96zFB&+.M4T5*|JRlc<2OKMTbpBYD|Et5/R+U-yBF<7pO+J^+VUlz-K46AycBF2RZ+b+M<d9L@+zYN_+O#jfJ2G(|Lz.VGXcU2;%qK+5#(D2>FkCd*-OlF^R8p/k4&+PF5|djtz+M9_";
		//String s1 = Ciphers.cipher[1];
		//String s2 = "VB#PAfZ%%9/EcS)kNMI%%GzD76eUflrS=^POtTV+\\WVB#9AkpUzP(Z)ANlML895kJ!IrG=HKEVN)L%tUR8M5PAQJX@/ed\\rYT_RD9IH6R^l)Lt+5IWVOpcZFkHUEe(N\\6=j+RDPB#9rML58!qW)IpqKZckRR^UMHEqX@B#P/dLB#SQTBGq8OlEYItNR(Sz58Xq6)IK9DGq+@Y8eWVHFp\\rdJZML^kEqlRf%UAROPB#9/(S)5DYQNtTq7kIU^YJMeYq!K@PH96FYGe+VWB=Xp=ROk%BU/Z/P%9kIU=)MHEIODYd86WQPBr+qJPAT_#cG%FQXR685X_+Fq=!L7\\dI_ezEc__tL#%9AWq69KUqWqS^_qY9t#XYRWB9A9@+crT#BG5qXJdFeTXE@e!ZOeVZ_HV%p";
		
		List<String> strings = sequencesToTest(s1, s2, thresholdCount, thresholdRatio);
		int sum1=0, sum2=0;
		int[] sums;
		for (String seq : strings) {
			sums = actualDifference(s1, s2, seq, thresholdCount, thresholdRatio);
			sum1 += sums[0];
			sum2 += sums[1];
		}
		System.out.println("Sum1: " + sum1 + " Sum2: " + sum2);
	}

	public static void testChunks(String cipher) {
		for (int i=0; i<cipher.length(); i++) {
			for (int j=96; j<cipher.length(); j++) {
				String sub = cipher.substring(i,j);

				if (scoreMap1 != null) scoreMap1.clear();
				if (scoreMap2 != null) scoreMap2.clear();
				if (refMap != null) refMap.clear();
				
				testFindStrings2(sub,i+","+j);
				
			}
		}
		
	}
	public static void main(String[] args) {
		//testTranslateCipher(Ciphers.cipher[1]);
		testFindStrings2(Ciphers.cipher[1].cipher,"408");
		//testFindStrings2("HER>pl^VPk|1LTGNp+B(#O%DWY.<*KBy:cM+UZGW()L#zSpp7^l8*V3pO++R_9M+ztjd|5FP+&4p8R^FlO-*dCkF>2#5+Kq%;2UcXGV.z(G2Jfj#O+_NYz+2df)HJK2k/D(L|@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WczWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+","jurgen");
		/*
		testFindStrings2("HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-","top half");
		testFindStrings2("U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+","bottom half");
		testFindStrings2(Ciphers.cipher[1],"408");
		testFindStrings2("9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQA","top half");
		testFindStrings2("P5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk","bottom half");
*/
		//testFindStrings2("U%P/Z/9%BkOR=pX=B6V+eGYFWPH9@K!qYe)QY^U5k7qTtNJYDIMlO/P#%9(RAUBfRSqEk^HMZJdr\\WFLVpe8YK+qGD9@I)6qXS5z8(REtIYNlOSqG#TQ8BBLX/P#B@dqEHMU^RRkcWKqpL)Zq!8I5Mr9#BkDR6j=+\\E(eNUHPFWcpOV+5I6tH)l^RZLI9DR_TYr\\Xe/@dQJAPLM8RUt%H)EVNK5=Gr5!JkI98L)lNAMZ(PzPWUA9B#V+tpVT\\Ok^=GrSzUe67Dfl%%IMNP)ScE/kB#ZfA9%%V+eXqpq_FB8cW@9A9#%^T5RUc+_dYq_OSqWVEeGYKZ_TYA9%#Lt_","");
		
		
		//testChunks(Ciphers.cipher[1]);
		//testFindStrings(Ciphers.cipher[1]);
		//testFindStrings("9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_");
		
		/*String[] s = new String[] {
				"dpA6%fG+pSkSLXVe9/kU\\%Ur@I(7q)JWG%PA%NZeI9)RqWlQeYP#9f(f6!AWNTV^A8F/B#ReA7J9qttZRPY6Z@BlEPDkB!INe65@9/XVqU#z5%8YQGHM+HUqWEHBG9O5EYYI8qPBE\\kkV%8TLlDK9RG@%H+^Fp%L5MO5EDUDKkMVLZeIMRr8)_Rt9!OUTMcXMlU9qST_%KqR^tZpqNNc#G(YTLIY=ROJOWkA+BB/AY))epRPdVq))_PT99rN6MXk^rW_SZdDQ#%\\VqJ=c=\\IFc(YRSB#dEXYBZSp5#EPq+#PLeK8^WKrF+8/z_jBOt/H5UVqlHtc9U^=LR_@=zI+",
				"S96+%pXedGLpVfSkAq%\\@%IJG/r)kWU(7UWPN9Z)QYPIlAeeRq%V/(AfWAF#!^986NTfZZe9AqP6BJR#Y7ttRe/EBP!59@k6B@DINlGUU%#8MHX5HV+zYQqYBHOB58Pq9IWqGEYEK%kTVLR@E89\\G%lDkEkF5pMUKHLD+D%O5^_OZRert!MMRV9I8)LTRcUX9%qUl_TKMqSMY=pcq#LY^NTtING(ZApO+WB)eRAYO)kB/J9XV_qPNMR)rP6)T9d%=Wd_DVJkZ\\^qSQ#r#BIYFREYc(d=XcSB\\LW5q#+K^ZPeS8E#PptV+_8jHUKz/r5/BOF_+t^c==IqU@lz9LRH",
				"pXAeSVkdG+SL9p6f%kJUG(W7/r@q)%I\\U%AQ%YReqPI9WlP)NeZ9AfFN8T#!AV^/W(6f#PR6tYtBJ9ZRZqe7AB5l9I@N@kBe6/!EDPVMqHY+QX5%GHU8Uz#W8EPEqYq9OYIB5HGB\\Rk@lGDE8TK9%Lk%V+U^KOD5HL5EDkMF%pVtL!89)MMR_ROrZIeT%MqqKSUlUT_R9cMXtLZYGI(^NcYT=#pNqO)JeB)/RA+AYpBOkWPNdMT69R)_9rXPV)q^VrJQq#kZd%\\=DWS_=E\\YSXBc(Y#dBRIcFSKp^#8PZPqLeW+5E#rHFUB5OKz_t/Vj+/8l=HILzRqU^_@+=t9c",
				"Sk%dS+V6e9GAXLfpp(7%/q@W\\G%rUJ)UIkRqZPW9eNYPI%Qle)ANTf#VA8(F/!fA^6W9ttABZ9Ye6ZJRPR7q#INP@eB@E9/kl56D!BYQ#XG%+UHU5qMHz8VEYBqYOqHPB9E8IG5WlDVEKTGk@%8kR9%L\\O5pHE5DFKkL^UD%M+8)eM_R9Z!OMLtRIrVqSXUTUKcqRlM%_M9TG(q^YcIpY=NZLTN#tB/WRA+)OepAJ)YkBOT9qR9_6VMX)dNr)PPQ#_k%dqWJ=ZrV\\SD^SBFc#YXIYB(\\EdcR=#P#ZLq85^WPpKeE+SBO8Kt_5+UVzFH//jrLRcq_^ztI+UH=@9=l",
				"Vdp9Ap+feX6GkL%SSW/k%UI@UGJ\\r7)%q(ePAP%)9eYQNIqlZWR8#9/fWA6FA(!T^fVNYB#ZRq976PeJtRAZt@@B/l!BD95EkN6PeI+XVUq8%zHMU5QH#GYqqWBE5OGP8H9YIBYEGE\\%kLT%@Rk8D9VKlDH+k^M5%KUFL5DpEO9MVOLrRI!tZM)Re_8KUTRM9UMq%clS_XTqI^t=Z#cNYLpN(TqYG)ROpJB+ke)OA/YWAB6RPXdP_)MNV)9rq9Tqk^=rDdSJVWZ#\\_%QXc=B\\RYcYEI(BdF#S8ZSWp+qE^K5PPe#L#5KrVFj_/UH+zO/8tBzql+H=^9I=tUR@c_L",
				"fA%Gdpp9+eSk6LSXVUU%r/kI%@G(7\\)qJWe%ZIPA)P9YRqNlWQe6ff!#9W/AFNT(^VA87RAJB#qZ96tteRZPYDlPk@B!/B9INE6e5@zq#5XV8U%HYQUHGM+GEB9qW5BOPEYHIY8q%kV8E\\L%T@lDk9KRG%^pLH+Mk5KO5FDEUDILeMMVrOR!8)ZR_t9MMXlUT9RUqqSc_T%KNZqN^t#=cYG(pTYLIkJWAROBp+eB/OYA)))dq)RPPX_MT9Vr9N6Sr_Zk^D=dJQ#W\\%Vqc\\F(c=RBYYSBId#EXEp#PZS+Wq^#P5eLK8/F8zKrjV_UBO+/tH59HcUql=+^ILRt@_=z",
				"S6Sdfe9%VLXpkG+pAq\\(/UG%%W)JI7r@kUWNRPeYPZelQ)qI9A%V(N#6F/f8^AWT!A9fZetB76ZAYRPqtJ9#ReEI@D9/P@65!NkBBlGUYXzHU#+HM8Q5%VqYHEqGPBBqI85Y9OWEKklE%@%VG9RLD8T\\kEFOH%KkpDDUM5L5+^_Z8MI!Oe9Rtr)MRVLTcqUMqRXK_%9SlUTMYpG^NY=qITL#(NctZAOBRkepW)Y)B/A+OJ9VTR)MXq6rNP9)_Pd%WQkSJ=_q\\VD#Zd^r#ISccYBFXdERB(Y=\\L5#ZE^W#8eK+PPqSpt+BK/UV85/HjOz_rF_tLq9I+cz@==RU^lH",
				"9GVSpL+X6ekf%SdpA%rW(k)@J\\G7U%q/IUPIeRAl9QNYqeZWP)%/!8N9^AA(FT6fV#WfZJYt#R9Pe6t7AZBqR/k@IB6B5E9NDPe@!lU5+YVH%MUHQz#GX8qB9qEWIO8HPYGBYq5E%8Gl\\9TRk@D%VKELkkLDO+D5UFK5%pEHM^OM98VRRtZ!)Ie_MrLRlKqT_U%cqSMXTU9M=NIGtTcLpY(NqY^#ZpA)BOY+)Oe/kWARBJX)6TPr_NVM9)q9RPd=ZqQ^\\dVWJ#S_%kDrB(XS=dYEIYBcF#cR\\WP8#SeqK5^PE#LZ+pVz5Br/_H+UO/8tKjF+UzLl@^=tIR9c_q=H",
				"eVLdfkA6SpG+9SX%pGW)/U7U\\(kr@%qJ%IYelPeq%NRAI9PWQZ)F8^#6Tf(N9!A/VAfW6YRB7tRet#J9ZZPAq9@6@DNlEIBkB/e5P!H+HXzQqUYV5%UGM#8PqIqGYEHEW9OBY8B5@G9E%Dkkl\\8T%KRVLKDDH%5^FO+L5kEUpM!9RMI)LZ8VMRO_terqK_UMSMcqTlURT%X9YIT^N(ZpGtNc=YLq#e)YRk/JOBOA+pA)WBM6rR)9dVTP)_X9NqPJq\\kS#rWQ^Zd=%V_DYXdccB\\IS=(YB#EFR^8eZEPp5#SPqWLK#+U5/K/OF+Brz_VtH8jIz@q9RHtLlU^+_=c=",
				"AVG+6Lp%f9pSeSdkXUWr@\\)I%U%kqG(/7J%eI9Nl)ZePAWYRPqQf8!A(^Wf6/9VFN#TARYJ9eRqA7Z#Z6tBtPl@kBE6!PD/Be9I@N5q+5%UH8#zUVGHYXQMEq9OHI5BGBWYPEqY8kG8Tk9LV%%\\K@lEDR^DL5FDMp%k+EKOH5UL9MRZRreIOV_!8M)tMKlUc_9XMRTTqqUS%ZINcpT#qN=tYYG^(LJ)A+OYBWkpOAeBR/)d6)_VrPq)XP9MTR9NrqZdW\\D_S=^%JQk#V\\X(YIdRFcB=#YScBEp8Pq5e+#EWSL^#ZPKF5z_+/j8/VrtUBKOHHzU^t@=c9+l_ILqR=",
				"fpe+LVkSSXdA9%p6GUkG@)W7(qJ/U%%I\\reAY9leqRWQP%PZ)NI69FA^8TNVA#f/fW(!7#69RYttZPBRZAqeJDB9B6@NIe5@l/P!EkzVH%H+QYGMXqU#8U5GWPOIqYEY8qEBB5H9%\\@T9GDlKREk%VLk8%+K5DD5OEUH^kpMFLIV!RR9)8_tMLOerZMMTqU_KSqT%UMRX9clNtYcTI(GYL^Z=q#pNkOe+Y)/BA)RJpWBOA)PM_r69T9NRdXqPV)S^Jd\\q#Q%Vkr=_DWZc=YYdXBS#Ec\\BFRI(ES^qe8P#LKZpW#+5P/rU_/5OBtHKFV8j+z9lI^@zRL_=qH+c=tU",
				"%dp+SpGLk6f9SXVAe%/k@qIr)7\\U%(JWUGZPA9W)IlqNePRQe%Yf#9AVW!^T(6/NA8fFAB#9ZqJRte7ZtPYR6P@BBe!k6NED/I5@l9#XV%G85HQUzUYM+qHBqWOY59IYHGBE8qEPVE\\TKL89Dk%%lRGk@pH+5EMLD5F%kOUD^KeMVR_rMR)ZIO8t9L!XUTUT9l_ScMRq%KMqq^tcY#NT(pN=GLIZYWRO+ABAY/OkpB))JeqRP_9P)r9V)XTN6dM_k^d%DZ\\#WS=QVqrJFc=Y#R(dBIcBSEX\\Y#ZSqL+PeP5EW#K8p^8Kr_tjz/O+/VBH5FUcql^_=U@Rt9+L=zHI",
				"VA6d+pkpL9S%feXGSWU\\/@k7I)%q%UGJr(e%NP9Aq)lPWZeYQIR8f(#A9TW^/Vf6FA!NYReB9#tqRZZA76PJt@lE@BBN!6/ePD95kI+qUX%VQ8HUG#zHM5YqEHqOWY5IBYBGP89EGkkET\\DL9%KV%@R8lD^FH5+5MDkEp%KULO9LZMRV)rRO_eI!tM8KMcUUTS9_RTXMq%lqIZp^ct(#T=YqNYLNG)JOR+O/BYpAWke)AB6dVR_P9PrX9q)MN)TqrWkd^#D\\=%_SJVZQX\\IcY=BRdB#FcYE(S8p5ZqSP+eWL#E^KP#5F+K_rOj/Vt8/UHzBzHtq^lR=@+_c9I=UL",
				"+L6eVS%dpG9SAkpfX@)\\GW(%/kr%qU7IUJ9lNYeRZPAIPW%q)eQA^(F8Nf#9!/VfTW6A9Re6YtAB#JZZRtq7PB6E9@IP@Bk/elN!D5%HUH+Y#XV5UGqQ8zMOIHPqEBqW9BYEY5G8T9k@GlVE\\8%KkDL%R5DFKDOpH+LkE^5M%URRZ!98eMVMO_L)rItU_cqKqXUTlRTMS9M%cTpYIGq^tN=YZ(#NL+YOe)BWROApAJ/Bk)_rVM6TqRP)X9d9P)Nd\\WJqQ_k^Z=%r#DSVYdIYXSFc=(B#\\BRcEqe5^8##ZSPWLpP+EK_/+U5B8KrzVtFOj/H^@tIzLcqlU+_HR=9=",
				"Sd6VLpf%9AGeXSkp+q/\\W)IU%%UrGJ(7k@WPNel)eZP%IYQRqA9V#(8^W6f/f!FANT9AZBeYRq7AZRJ6Ptt#9e@E@6!DP/lk95INBBGXU+H8z#Uq5HMYQV%YqHqI5GBBE9P8EYWOKEkG9L%V%k8@RlD\\TEHFDDM%pk^LKUO5+5_MZ9RrIeOLM!t8)VRTUcK_9MXRMlq%qSTUY^pIT#Nq=ZNYLG(tcARO)YBkWpJAe)B/O+9RV6rP)qXd)MNT9P_%kWq\\DS_=rZJVQ#^d#cIXdRcFB\\(YESB=YLZ58e+E#WpP^K#PSqtK+5/j/8VFzUHBOr__qtz@=9c+HUI=LRl^",
				"Sp+X9%Lf6SVdGApke(k@J%%)U\\qW/rUI7GRA9QPZleNWePI%)qYN9AA/f^6(V8#!fWTFt#9PZAR7eZYBJRqt6IBB5/P6DEe@@kl!N9YV%MU#HzUG+X5q8QHEWO8BBIGHYqq9E5YPl\\TR%V9%kKGE8kLD@O+5UkpD%FEDHL^M5K8VRtOeRIZ_9MMLr)!qTU%RX_McTKUlM9SqGtcL=qTNpYI^NZ#(YBO+)pWYkOA)RAJB/eTP_NXqr)V96R)dP9MQ^dV=_\\SW%qkZrD#JS=YEBFdcI#Xc(\\RBY#SqKW#eE5L8ZPp+P^Br_HV8//+t5KzFjOULl^=+c@9t_zqUH=RI",
				"9fdpAeVSL+XGk%p6S%U/kUGW()@Jr7%I\\qPePA%YeRl9QIqZ)NW/6#9fF8N^AA!TfW(VZ7B#R6YtR9PJtAqeZ/D@Bl9@I6B5kNP!EeUzXVqH+YH%M5Q#8UGBGqWEPqEIO89YB5HY%%E\\k@Gl9TR8DVLkKk%H+^KDOD5UL5pMFEOIMVL!98RRtM)erZ_RMUTMqKq_U%lSX9cT=N^tZYIGTcLN(q#pYpkROJe)BY+)A/WBOAX)RPdM6Tr_N)9qPV9=Sk^rJqQ\\dVZ#_DW%Bcc=\\YXSdYE(BFRI#WEZSp^8#eqKPP#+5LV/KrFU5B/_HzO8j+t+9qlHIzL@^=URc=t_",
				"%eASVGpSXd+Lfp6k9%GUqWrI(J/@)Uk\\7%ZY%WeI)RQP9leANqPfFfV8!WNA#A^69(T/A6RZYJqtPB9R7#etZP9le@k!I5@B6DBEN/#HqG+58YMX%HzVUQUBPEYq95E8qOIGWHYBV@kKG8LlRET9%\\kD%pK^EDLMOUH5D%+F5ke!L_9Mr8tMRRIVZ)OXqMTKl9q%UU_MTcSRqYZYIN#GL^cTNtp(=WeJA)ABB)R+YkOO/pqMd96)PTNR_r)PV9X_Jr%qZDQVkd\\S^W#=FY\\#X(RSEcYdc=IBB#^pL8P+#KZqeES5PW8UFt5zjBHK_//r+OVcIH_zU=L=q^@9ltR+",
				"efdSVGppk+69LAXS%GU/(WrIk7@\\%)UJq%YePReI)Aq9NPl%QWZF6#N8!W9TA(/^fAVf67BtYJq#t9eZRRPZA9D@I@k!BNBE/6l5ePHzXY+58VQ%UUHqMG#PGqEq95WYOHBIE8YB@%ElG8L\\DTk%9kRKVK%HODLM+55FkD^UEp!IM89MrV)RZORLt_eqMUqKl9TSUcR_M%TXYN^GIN#t(cp=TZLYqekRB)ABO/+OpYJ)AWM)RT6)PP9_VXrdN9qJSkQqZD^#dW=\\rV%_YccSX(R=BYIBd\\E#F^EZ#8P+SPq5WepKL#U/KB5zjrO_+V/FHt8I9qLzU=lR^t+@H=_c",
				"9Lk+AVX%6fGpdSpSe%)7@UWJ%\\Urk/(IqGPlq9%eQZNeIAPR)WY/^TAf8Af(6!9#NWVFZRt9RYPAe7J#BtqZ6/6NBl@5PEDkB@I!e9UHQ%q+M#Uz5VXY8GHBIYOEq8BHG9WqE5YP%9DTkGRVk%8\\ElLK@kD55^DUpF%L+HOMEKOR)RL9teZIMVM8r_!R_SUMK%XcMlTUq9Tq=T(cZILqpNNt^G#YYpY/+J))WOkAORBBAeXr9_d6NqV))PRTP9M=\\#drqV_WSZ^kQD%JBdBY\\XEFIc(=cSR#YWePqp8K#5EPSZ#+L^V/O_F5H8+/zrKBjtU+@R^Hz=ct9UlqL=_I",
				"%p6Sd9G+ekAXLSfVp%I\\q/%r@G7UJ)(UWkZ)NWPPI9Yq%QlReeAfW(V#/!AFTfA^N689AqeZBZJ96tRPRt7Y#P!Ee@/kB9Nl56ID@B#8UGXU5%HQqMHYz+VB5HYqB9OPYE8IEGqWVLkKE%8T@DkR9l%G\\pMFEHkL5K5^UDO%D+erZ_MOMR!)LtR8I9VX9cTURlUqSM%_qMKTq#pY^=NcY(ZLTGNItWBOARpA+e/J)YBk)OqPV9RX)_M9dNrT)6P_DW%k=ZdJ#rV\\QSq^FRI#cB(YYB\\EdScX=#+5LZWPq^PpKe#E8S8j+tKVz_UOFH/B/5rc=t_q+U^IRH=@L9zl",
				"G%SVeLX9fdpS6+pkAr%qWG)J%U/k(\\@I7UIZWeYlQPePARN9)q%!fV8F^A/6#9N(AWTfJAZY6RPZ7B#te9qtRkPe@965/D@BIEB!Nl5#G+HHMUzXVYU%8Qq9BYqPI8BGqWEHO5YE8VKG@9R%%E\\lkTLDkLpEDKDUk%H+OF5M5^Me_9!RtOIMV8ZRr)LlXTKq_%RMUTqcU9SMNqYIYTL=N^tGpc#(ZAWA)eY)pkROBO+B/J)q96MrNX)RPTV_P9dZ_%qJ\\V=Sk^QWdD#r(F#XYdEBcc=SIYRB\\P#L8^eKWEZS#5q+Ppz8t5U/HV/KrB+_jOFUc_zI@=+9qlLt^=RH",
				"kALp+XS9%efGpdVS67U)k@J(%%GUrI/Wq\\q%lA9QRPZYeI)PeWNTf^9AAN/fF6!W#8V(tRR#9PtZA67JqBYZeNl6BB5I/P9Dk!@@eEQqHV%MYU#Hz58X+GUYEIWO8EBBPG95qqYHDk9\\TRl%V@%8LEGKk5^D+5UOkpK%LMHDEF)LRVRt8Oe!IMrM9_ZSM_TU%qRXqMl9UKTc(ZTtcLG=qYNN#^IYp/JYO+)BpWekABR)AO9drP_NTXqM))PR69V#r\\^dVQ=_JSZDkq%WB\\d=YESBFYc(RcX#IPpeSqK#W#^EP+Z8L5OF/r_HBV8U/zjK5t+RH@l^=L+cI9U=qz_t",
				"L9AXpS6dp%SGk+Vef)%UJI(\\/k%qr7@WGUlP%Q)RNPAZWIq9eYe^/fAWN(#9fV!TA8F6RZRPqteB#AZJt9Y676/l5!IE@BPekNB@9DHUqM8YUXV#G5Q%+HzIBE85EHqWBY9YOqPG9%kRLlkE\\VK8DTG@%Dk^UMOFH+pEL55DK%ROLtr8ZMVe_M)R9!I_RM%9qcUTXTlSUKqMT=ZL#Gp^tqYN(cIYNYpJ)BBOROWAA/+)ekrXdNPTVRPq9)9_6M)\\=rVDQWk^_%Z#dqJSdB\\ERSIc=F#(BYXYceWpK+#5ZS#LPPq8^E/VFHjB+Kr8tzO_5U/@+H==Ltqlc_UR^zI9",
				"p6+p9GL%fdSkVeXASI\\@k%r)%U/(7WGJUq)N9APIlZePRqeYQ%WW(A9/!^f6#NT8FAfVqe9#ZJRA7BttY6PRZ!EBB/k6PD@IN@95le8U%VU5H#zXYQ+HMqG5HOWB9IBGqEYqP8EYLkT\\%89V%ElDG@RkKMF5+kLDp%HO5DKU^ErZRVOMReIM8)9!tL_9cUTRl_XMUqSKq%MT#pct=NTqN^G(IYLZYBO+OpAYWkRB/)e)JAPV_PX)rq)RT96MNd9DWd^=Z\\_SkQ#qJVr%RIY=B(dFccSBXYE\\#+5qSWPe#EZ#P8^KpLj+_rVz/8/KBO5UHFt=t^l+U@c9qLRzI=H_",
				"GVS%dkfAp6pSe9LX+rW(%/7UUk\\IqG%)J@IeRZPqe%AN)WYPlQ9!8Nf#T6f9(WVF/^AAJYtABt7R#eqZ6ZRP9k@IP@NDlBE!e9/65B5+Y#XQzqVU8GHUHM%9qEBqYGEWH5YPBI8O8GlVED%k\\kLK@%9RTLDOpH5%^+FMEKkDU5M98eM)ILVZr_!ORtRlKqXUSMMTc9TqR_%UNIGq^(NZtp#YY=TLcA)BWR/kJOOBAepY)+)6TqR9)dPVP9MXrN_ZqQ_k#Sr^WD%J=\\Vd(XSFcBc\\=IR#YBdEYP8##ZPEpS5+L^WeKqz5B8KO/Fr+jtUV/H_UzLcqR9Hlt=_I+@=^",
				"VekApSdS9XLpfG%6+WG7Uk(/q%J)IUr%\\@eYq%ARPWPQl)eIZN98FTf9N#V/A^W6!f(AY6tR#tBZZPRq7JAe9@9NlBI@e/56!DkPEB+HQqVYXGUMH8z5#U%qPYEWEqYB8I5G9BHOG@Dk\\lEK%R9L%8VkTDK5^+OHEkUDM%LpF59!)LV8M_OtRrIMeZRKqSMTqUTR%_9MlXcUIY(ZtG^Y=LT#NNqpc)e/JOBRAp)YBkAWO+6M9dPTR9XNrP))qV_qJ#r^Qk%=V\\DSZ_WdXYB\\=Sc#BEdRc(FIY8^PpS#ZLWKe+EP#5q5UOFrBKtVH/j/z8+_zIRHlLq_+=@=9Uct^",
				"VGdpe6L%kAfXpSS+9Wr/IG\\)%7UUJk(q@%eIP)YNlZq%eQARW9P8!#WF(^fTf6A9NVA/YJBq6eRAtR7P#tZ9Z@k@!9E6PNlD5BIeB/+5X8HUH#QqzMVYG%Uq9q5PHIBYEG8WEYOBG8EL@k9VDk%R\\lKT%DLHMKFDp5^%U+OE5k9MMr!ZRe)LItV8_ROKlU9qc_XSMM%TqTURIN^#YpTq(ZNLtGYc=)ARBeOYW/Jk)OBA+p6)RPMVrq9d)NPT9_XqZkDJW\\_#rSV^Q%d=X(cRYIdFB\\cE=S#YB8PZ+^5e#PpEKS#LqW5zKjU+/8OF/HrBt_VzUq=It@cRH9=lL_^+",
				"G+Se9d%ALfpSVpkX6r@qG%/%U)UI(Wk7J\\I9WYPPZ%le)ReAqQN!AVF/#ff^6WN89TA(J9Z6ZBARR7qtY#tPekBe9/@Pl6D!I@BN5E5%GHUX#qHz8Y+VQMU9OYPBqBEIG5EqWY8H8TK@%EVk9%LlG\\DRkL5EKkHp^D%MOD+5UFMR_!OMeLRIr89V)tZlUTqRUXM_M9qKTS%cNcYY=^qZTN#GIt(LpA+AepRWJYkBB)O/)O)_9MXRqdr)PT6P9NVZd%J=k_r\\SDQq^#VW(Y#YBcF\\dcRSX=BEIPqL^WZ#peE+#8SPK5z_tUVK8F//jB5rOH+U^_I+qcH@9=LzlR=t",
				"GVA9SSppke6LXdf+%rWU%(qkI7G\\)J/U@%Ie%PRWA)qYNlQPe9Z!8f/NV9WTF(^A#6AfJYRZtZ#qt6eRPB79Ak@l/IeB!N9E65@DBP5+qUYGV8QHUHMXz%#9qEBEYW5YPHI8qGOB8Gk%lK\\LD@k9RE%TVLD^kOE+M5KFDUH%5pM9LO8_Vr)!ZRtMIRelKMRqTT9Sqc_%UMUXNIZ=GYt#(YpTL^NcqA)JpBAOB/eOY)Rk+W)6dXT9PP9MVrNR)_qZqr=Q%^D#JW\\VkSd_(X\\BS#=RBYIdEccYFP8pW#LS+P^5eKZEq#z5FVBtrjOU+/HK/_8UzH+L_l=RIt@=q9^c",
				"LGX9pfkAd6+SpSVe%)rJ%IU7U/\\@(kqWG%lIQP)eq%PN9RAWeYZ^!A/W6Tf#(AN9V8FfRJPZq7tRBe9t#ZY6A6k5/!DNl@EBIBe@9PH5MU8zQqXU%YVG+H#I98B5GYEqHOEWYqPB98R%L%DkEkTl\\KG@VDLUkM%5^HF5O+EDKpRMtOrI)LMZR8V_9!e_l%R9MSMUcUqTTKqXTNL=#N(Z^pcGtYIYqYA)pBk/JRO+BOA)eWr)NXP)9dRV_TP96Mq\\ZV=DS#rkWdQ^%qJ_d(EBRcB\\cIYS=#XYFePKW+EPpZ5q#SL8^#/zHVj/OFK+_Brt5U8@U=+=9RHqt^Ll_zIc",
				"f%Sdp9V+kSpXeAL6GU%(/I%W@7qkJGU)\\reZRP)Pe9qWAQY%lNI6fN#W/8ATV9AFf^(!7AtBqZY9tZ#P6RReJDPI@!/@BNeB59l6Ekz#YX8U+%QGVMHqHU5GBEq5BqOYYW8PEIH9%VlEL%GTDK\\R@k9k8%pOHMkD55E+UK^DFLIe8MrO9R)_Vt!LRZMMXqU9RKUSTT%qM_clNqG^#=Ic(YtLYZTpNkWBRBp)+/AO)eJYOA)qTRPX6_99PNMdrV)S_QkD=qd#%^VJr\\WZcFScRBXYB#=EY\\dI(E##Z+W8qPLSK^pe5P/8BKjV5_OtrHUF/+z9cLq=+z^R_l=IH@tU",
				"f6de+%GpASL9kXSpVU\\/G@%rIUq)%7J(kWeNPY9ZI)%WlPqQRAe6(#FAf!WfV^/TAN987eB69AJqRZRZtPt#YDE@9BPk!le6/N5IB@zUXH%#58qGHUQMYV+GHqPOB95EYIBY8EWq%kE@TV8LkK9%DRl\\G%FHK5pLM^EDk5UO+DIZM!ReMrL_RO)t8V9McUqUXl9MT_RS%qTKNp^YcqN#ZYT=(LGtIkORe+WABJAYp/)BO))VRM_q)Pd9rX9NTP6SWkJd_ZDr%\\=#VQ^qcIcYYF(R\\#dBBES=XE5Z^q#P+pLeWPK#S8/+KU_8zjFt/VOHBr59tqI^cU=H_@+R=Llz",
				"%pG6LS9VkSApefdX+%Ir\\)(%W7qUkGU/J@Z)INlRPeqW%AYePQ9fW!(^N/8TVf9F6#AAAqJeRtZYtZR#67BP9P!kE6I/@NelB9D@5B#85UHYU+QGqVHzXM%B59HIEBqYYEWPGq8OVL8k9l%GDKk\\@%ERTpMLFDOkD5E^+K%HU5erMZR8O9)_LV!IMtRX9lc_qRKSTMTqMU%Uq#NpTG=I(YZtYN^LcWBAOYBp)/AJOekR)+qP)VrTX699dPM)RN__DZW\\Q=q#%r^JSkVdFR(IdSBXB#\\=YccEY#+P5e#W8PLpS^EZKq8jz+/BV5OtFrU/KH_c=Ut@L+zR_HlI9q=^",
				"VS9p6SeA%+XfdkpLGW(%I\\qGU%@JU/7k)reRP)NWY%Z9QePqAlI8N/W(VFffAA6#T9^!YtZqeZ6RA9P7Bt#RJ@I/!Ee9lPB5D@NB6k+YU8UGHq#%MzXQVH5qEB5HYPEBO8GqYWI9Gl%LkK@kVTR%ED\\98DOkMFEK^p5U%H5+DL98OrZ_!LeRtIM)VRMKqR9cTqMXU%MUST_lIG=#pYYZqcLN^(tTN)BpBOAeJW+)kR/OYA6TXPV9Mdq_N)R9Pr)qQ=DW%Jr_dVSk#^\\ZXSBRI#Y\\FYEccB=d(8#W+5L^p#qKEZPSeP5BVj+tUF8_H/KOr/zzL+=t_IHc^=9qRl@U",
				"+SL9VXepfpAkd%S6G@q)%WJGkUIU7/%(\\r9WlPeQYAe)%qPZRNIAV^/8AF96WfT#fN(!9ZRZYP6#7qRtBAteJBe6/@59BD!lN@PIEk%GHU+MHVz8qQX#YU5OYIBq8PWG5EYqBEH9TK9%GR@\\%LkDEVlk85EDkDUK+%M^5HpOFLR_RO9t!VIrL)Me8ZMUT_RK%qTM9MSUXqclcYT=ILYtN#Z(^qGpN+AYp))eOkBJ/RWBOA_9rX6NMP)Pd9RqTV)d%\\=qVJ^SDr#k_QWZY#dBXEY=cR\\BcFSI(qLeW8K^SE+pPZ##5P_t/V5HUr/jFOK8B+z^_@+z=Il9=HRqcLtU",
				"G+keLX96SpAdpf%SVr@7G)J%\\(kU/IU%qWI9qYlQPNRA%P)eZWe!ATF^A/(N9f#W6fV8J9t6RPZet#RBq7AZYkBN965/EIBl@!DPe@5%QHHMUUYVqX8z#G+9OYPI8BHEWEq5GBYq8TD@9R%kl\\kEL%VKGL55KDUkFO+^HM%pEDMR)!RtOZ8VLMrIe_9lUSq_%RcqTMU9MXTKNc(YTL=pGtZ^#NqYIA+/eY)pOBOJRBkWA))_9MrNXVTPdRP)q96Zd#J\\V=WQ^rkDS_%q(YBYdEBIS=\\cRcF#XPqP^eKW5#SpZ+E#L8z_OU/HV+BrFKj/8t5U^RI@=+tLlHq=9c_z",
				"pekdLG6A+X9V%pSfSIG7/)r\\U@J%W%kqU()YqPlIN%9QPeZAWeRWFT#^!(fAA/8f9V6Nq6tBRJeR9PZYA#Z7t!9N@6kElB5/@PBeDI8HQXH5Uq%MU+#VGzY5PYqI9HEO8BqBWYGEL@DE98kkTR%GV\\K%lMK5HDLF^5UkDp+E%Or!)MRMZLRtO9eV_I89qSU_lcMU%RKXTTMq#Y(^TNpZcL=IqtYNGBe/RYAOJ+)p)WOAkBPM9Rr)Vd_NX6qP9)TDJ#k\\ZWrdV=q_^%SQRYBcd(I\\YEBXF=#cS+^PZeP5pqKW8#SLE#jUOK/z+F_HV58rt/B=IRq@UtH^=+zcl_9L",
				"e+fp9XSpASV%Lkd6GG@UI%J(kUqW%)7/\\rY9e)PQRA%WeZlqPNIFA6W/AN9fV8f^T#(!697qZPt#RZYARtBeJ9BD!/5IBle@P6N@EkH%z8UMYVqG+#HQXU5POG5B8EWEYqBIYqH9@T%L%Rl\\kKGV9DEk8K5%MkUO+^EDpD5HFL!RIrOt8VL_9eR)MZMqUM9R%qTMTKX_SUclYcN#=LGtZYIqT(^pNe+kBp)BOJA)WY/ROAM_)PXNTPd96qr9RV)JdSD=VQ^r%q_\\#kWZYYcRBES=\\#XFdBcI(^qE+WK#SpL8#ePZ5PU_/jVHBrFt58/OK+zI^9=+=LlH_zc@RqtU",
				"SpV+Gd9pkXL%fe6SA(kW@r/%I7J)%UG\\qURAe9IPP)qQlZeYNW%N98A!#/WTA^f6F(Vft#Y9JBZqtPRA76eZRIB@Bk@/!N56PD9EelYV+%5XU8QMH#zHUGqEWqO9qB5Y8IBGPHYEl\\GT8E%LDR9V%@kKkO+D5LHkM5UDp%KFE^8V9RMMOr)tReI!Z_LqTKUlUR9S%_XMqcTMGtIcN^=#(LTqNYpYZBO)+ARpB/)YWkeOAJTP6_)RXP9Nrq)MV9dQ^qdZk=D#V\\_SJW%rS=XY(cBRBEdFcYI#\\#S8qPZW+PKe#E^5LpBr5_zKVjOH/8/U+tFLlz^Uq+=R=@c9It_H",
				"VfGpSdLS96Ape+%kXWUrI(/)q%\\UkG@%7JeeI)RPlWPN%AY9ZqQ86!WN#^V/(f9FAfTAY7JqtBRZZeR#69AtP@Dk!I@6e/ElB9BPN5+z58YXHGUUqVH%#QMqG95EqIYBHEWPOBY8G%8LlE9K%kk\\@TVDRD%LMOHDEkF^+K5p5U9IMr8MR_OZLV!Re)tKMl9qU_TRcMTqUXS%INN#G^TY=pZtYcq(L)kABBRYApOJOe+W/)6))PTRr9XVdPM_q9NqSZDQk\\%=Wr^Jd_#VXc(RScd#BI\\=YYFBE8EP+#ZeLW5pS^q#PK5/zjBK/tV+FrU_8OHz9U=Lq@_+tHlI^cR=",
				"AXk+9VS6SefGpLd%pUJ7@%W(\\qGUrI)/%k%Qq9PeRNWYeI)lPZAfATA/8N(VF6!W^#f9RPt9ZYteZ67JqRBA#l5NB/@IEe9Dk!6@PBqMQ%U+YUGHz58HX#VE8YOBqEHYPG95IqBWkRDT%GlkK@%8L9EV\\^U55kDOFEK%LMDHp+Lt)RO98Z_!IMrRMeVM%SURKqcTqMl9_UXTZL(c=IGpYYNN#T^qtJ)/+p)BOAekABYRWOdN9_X6TV9M))PrRqPrV#d=qQW%JSZD\\k_^\\EBYBXSI#Yc(RdcF=pKPqW8#5L^EP+eZ#SFHO_V5B+tU/zj/K8rH=R^+zLt_I9U=@qcl",
				"LSGSAp9%6+dkeXpfV)(rqUI%%\\@/7GJkUWlRIW%)PZN9PqYQAee^N!VfW/f(A#TFA968RtJZRqZAe9Bt6P#7Y6Ikel!/PEB@N95BD@HY5Gq8U#U%XQHMVz+IE9YE5BBHOqYP8WGq9l8KkL%VkTED@R\\%GDOLE^MkpF5H5KU+%DR8M_LrOeZRM)!tVI9_qlTM9RXcUUSq%TMKTGNYZ#=qpc^(YLtNIYBAAJBpWO+R/e)Ok)rT)9dPXqV_R9MNP)6\\QZ%rD=_Wdk#JV^SqdS(#\\RBFIYcBYE=cXe#PLp+W#5qZP^KSE8/BztFjV8+_KOUHr/5@LU_H=+ct^qRI=l9z",
				"X69%+GdpeSVpkLSfAJ\\%%@r/kG(WI7)qUUQNPZ9IPAYRe)qlWe%A(/fA!#9FN8WT^V6fPeZA9JB#6tYqtRZ7R5E/PBk@B9I@!N6eDlMUU#%5XVHY+8QHGzq8HBBO9qWPEq5YIYGERk%VT8E\\@lGLD9K%kUFkp5LH+KODM5DE%^tZOeRMMV!89r)R_IL%cRXUlUTqqK9S_TMMLp=qcN^tYGI#(TYNZ)OpW+AROeB)B/YAkJNVXq_)RPMT6P9r9)dVW=_dZk^JQqD#\\%SrEIBFY(c=YSXRBd#c\\K5W#qPZS^#8+PeLEpH+V8_zKrUB5jO/t/F=t+c^UqlILz=R@_9H",
				"LS9VXpdSk+G6p%Aef)q%WJI/(7@r\\k%UGUlWPeQ)PRq9INAZ%Ye^V/8AW#NTA!(9ffF6RZZYPqBtt9Je#AR676e/@5!@INBkEBPl9DHGU+M8XYQ%5UV#qHzIYBq85qEYO9HWBEPG9K%GRLElDT8k\\Vk@%DEkDUMHO55LF+p^K%R_O9trM8)RMZVeL!I_TRK%9UqSUlcTXMqMTY=IL#^G(cNptqZYNYAp))BRB/+AOOWJekr9X6NPRT9_)VPqdM)\\%=qVDkQ#dZW^_rJSd#BXERcSBY(I=F\\YceLW8K+Z#PqP5S#p^E/tV5HjKBO_z+r8FU/@_+z==qLR^UtlcHI9",
				"pSGpeAkSdL6f+%V9Xk(rIGU7q/)\\U@%W%JARI)Y%qWPlNe9ZePQ9N!WFfTV#^(6Af8/A#tJq6RtZBRe79AYZPBIk!9lNe@6EDBP@/5VY58HqQGXHUz%#+UMWE95PEYYqIHGOBqB8\\l8L@kDKE9k%TVG%R+OLMK^5EHDF%5pDkUV8Mr!L)_MRZIRe9OtTql9qMSTU_cMUXKR%tGN#YZ(Y^TpNcqI=LOBABeJ/ARYOk+W)p)PT)PMd99RrV)_q6XN^QZDJr#%k\\WSd_q=V=S(RY\\B#cdIcYFXBES#P+^pPLZe5Eq#8WKrBzjUFOtK/+/_85VHlLU=IHR_q@t9^cz+=",
				"kAe+96XLSpS%dGfVp7UG@%\\J)qk(%/rUWIq%Y9PNQlWARZPIee)TfFA/(A^V9Nf#!68WtR69ZePRZ#tABJ7YqNl9B/E56eBIP@kD@!QqH%UUMHGVY#X5z+8YEPOBH8IYWEBq9Gq5Dk@T%kR9K\\lVE8%GL5^K5kFUDE+OpHL%DM)L!ROZtR_V8eMMI9rSMqURc%_TTqXUlMK9(ZYc=pLTYtGq^NNI#/Je+pO)YAOBWRAk)B9dM_XVNr9PTqR))6P#rJd=WV\\%^Q_kZSqDB\\YYBIEd#=SFc(cXRPp^qW5KeLS##ZPE8+OFU_V+H/trB8Kz/5jRHI^+t=@_lLcqU9z=",
				"LfX+kGSS9dA6epV%p)UJ@7r(q%/U\\GkW%IleQ9qIRWPP%NYAeZ)^6AAT!NV/#f(F98fWR7P9tJtZZBRe6#YAq6D5BNkIe/@lE9B@P!HzM%Q5YGUXqUHV+#8IG8OY9EYBqEHPWqB59%RTD8lK%Ekk@\\GVLD%U55LOEkH^FK+DpMRItR)M8_OMLZ!V9er_M%USlqTRUMcqTKX9TNLc(NGY=^ZpYtIq#Yk)+/ABApRJOeO)WBr)N_9)T9XRdVMP6qP\\SVd#ZQ%=krWJ^q_DdcEYB(S#Bc\\IY=XFReEKqPP#LWZp5^S8#+//H_OzBtVKF+Ur58j@9=^RUL_+qHtIlzc=",
				"%Ve9pLfS6XAdSpk+G%WG%k)Uq\\JU/(I7@rZeYPAleWNQ%PR)q9If8F/9^6V(Af#NWTA!AY6Z#R7ZePRBtqt9JP@9/B6DeE5l@I!NBk#+HUVHzGUMqXY8Q%5BqPBWIGYH8EqE5YO9VG@%\\9%KkRkElLDT8pDKk+D%EFU^HOM55Le9!OVRI_ZtLM8r)RMXKqRT_MTc%MUq9SUlqIY=tTNYpLZ^G#(cNW)epOYkAO)JRBB/+Aq6MXPr)9VNdRTP9_)_qJ=^\\S%WVrkQD#dZFXYB=dc#IE\\cSRBY(#8^WSeEL5KpZ#+PqP85UVr//t+HFKBjO_zczI+l@9_t=HqL=R^U",
				"SdX%9pe6k+ASpfVGL(/J%%IG\\7@UqkUWr)RPQZP)YNq9%WAeeIlN#Af/WF(TAfV968!^tBPAZq6et9RZ#7YJRI@5P/!9ENBleBD@k6YXM#U8HUQ%qGVz+5HEq8BB5PHYOEYWGq9IlERV%L@kDTkK\\%G89OHUpkMKF55^E+%DLD8MteOr!Z)RL_VI9MRqU%XR9qcSUMTTMKl_G^Lq=#Yp(cZYtNINTBR)WpBeO/+JAOk)AYTRNqXPMV9_d9P)6)rQkV_=DJW#dr%^SqZ\\ScEFBRYIBY\\#=cX(d#ZK#W+^5PqpLSE8PeBKH8VjU+O_Ftr/5z/Lq=c+=ItR^H_l9zU@",
				"kVefp6d9%GSSAX+Lp7WGUk\\/%%r(qUJ@)IqeYeANPPZIRW%Q9l)T8F69(#/f!NVfAA^WtY67#eBZAJtZRP9RqN@9DBE@/PkIel5B6!Q+HzVUXU#5YGqM%H8YqPGWHqBB9EYE8OI5DG@%\\kE%V8lKkRT9L5DK%+FHkpLOE^U5DM)9!IVZMOeM8_LtRRrSKqMTcURXlqTM%U_9(IYNtp^=qNGYZLcT#/)ekOORpWABAJ)+YB96M)PVRXq)T9dN_rP#qJS^Wk=_ZQ%rVd\\DBXYc=IcBF(S#\\EYdRP8^ES5ZW#P#LpKqe+O5U/r+KV8zBtFH_/jRzI9ltq+cUL_H=^@=",
				"k9fSVGLpAX6de+%Sp7%UqWr)kUJ\\/G@%(IqPeWeIlA%QNPY9ZR)T/6V8!^9fA(#FAfNWtZ7ZYJR#RPeB69AtqN/De@k6Bl5E@9BPI!QUzG+5HVqMUXH%#Y8YBGYq9IWE8HqPOBE5D%%KG89\\kRkE@TVlL5k%EDLD+^UFHK5pOM)OI_9MRVLtZM!Re8rSRMTKl_TM%cUqUXq9(=NYINTtZLp^YcqG#/pkA)AYOJ)ORe+WBB9X)96)rPdNVRM_qTP#=S%qZ\\^rVWkJd_QDBBc#X(d=\\EIcYYFSRPWEL8PeSpK5Z^q##+OV/t5z/rFH+KU_8BjR+9_zU@lH=tqI^cL=",
				"ppkVd+eXS%6AfLG9SkI7W/@GJq%\\UU)r%(A)qeP9YQWZN%elIPR9WT8#AFAVf(f6^!/N#qtYB96PZAeR7RJZtB!N@@B95ePElD6k/IV8Q+X%HMG#UqzH5UYW5YqqOP8YBHEGI9BE\\LDGET@RKVkk%98%l+M5DH5KUEpF^%DLkOVr)9MR!t_eZLIRMO8T9SKUUq%TXcMM_lRqt#(I^cYLYqpZNTN=GOB/)R+e)AWOJkYApBPP96R_MN9qVd)r)XT^D#qkdJV%_WrS\\Z=Q=RBXcYYE#FI\\cd(BSS+P8Zq^KL#5pEePW#rjO5K_UHt8+F//zVBl=Rzq^I=_ctH9@U+L",
				"%dSA6+epXfVk9pLSG%/(U\\@GkJUW7%I)qrZPR%N9YAQeeqP)lWIf#Nf(AF9A68T/W^V!ABtRe96#P7YtZqRZJP@IlEB9B5D@N/!6ek#XYqU%HVMz+QU8HG5BqEEHOPW8GqYB5IY9VElkkT@\\R%GD%L9K8pHO^F5K+U%D5kMDELeM8LZR!VtI9)OrR_MXUqMcUqT%MKSR9_Tlq^GZpcYtLNI(=#TYNWRBJO+eO)k)/pBYAAqRTdV_MPN)69XPr9)_kQrWdJ^VSq#=D\\%ZFcS\\IYY=EcXBBRd#(#Z#p5q^SKE8PW+eLP8KBF+_UrH/5OVj/tzcqLHt^Il=9zR+=@_U",
				"pVGSed%A+p9kSfLX6IWr(G/%U@k%7qU)J\\)eIRYPZ%9APqWelQNW8!NF#ffA9/TV6^A(qYJt6BAR9#ZtZ7RPe!@kI9@PlBB/NeD65E8+5YHX#q%VUQGzHMU5q9EPqBEOWBYYGI8HLG8l@EVkT\\%DK%9RkMDLOKHp^5+k5E%DUFr9M8!MeLRVO)_IRtZ9KlqqUXMUTRSTM_%c#INGY^qZct=(YNTLpB)ABeRWJ+Op/AkY)OP6)TMRqd_PX99)rNVDqZQJk_rd^=#%S\\VWRX(SYcF\\Y=BB#cdEI+8P#^Z#pqSWPLEeK5j5zBUK8F_rVOt//H+=zULIqcH^l+R_9@=t",
				"dpXpSGLSe+V6A9fk%/kJI(r)qG@W\\U%U7%PAQ)RIlWY9eN%PeqZ#9AWN!^VFA8(f/6TfB#PqtJRZ69YeRZ7tA@B5!Ik6e9B@El/DNPXVM8Y5HGH%+UqUzQ#qW85E9IYPOqHEBGYBE\\RLl89K@TGkk%%DVH+UMOLDEK5DF^k%5pMVtr8MR_!R9ZLOI)eUT%9ql_TqUKcMRMSX^tL#GNTYYcIpZ=N(qRO)BBAYAe+)OJpk/WRPNPT)r9M_6VdX)9qk^VDQZ\\%JdqWr=S#_c=ERS(d#YYXI\\BcBFZSK+#PeL^q85pWEP#KrHjBz/tU_5+FV/O8ql==LU@_I^ztH+9Rc",
				"6SLeS+dGfpk9pV%XA\\q)G(@/rUk7%IW%JUNWlYR9PIeAqP)eZQ%(V^FNA#!69T/W8fAfeZR6t9BJ7#tZqYAPREe69IB@kDBN/!@P5lUGHHY%X5zVQU8+#MqHYIPEOq9GWYB5qB8EkK9@lTE8%\\D%LGVRkFEDKO5HL%+5kMDpU^Z_R!8RMMIV)Or9etLcT_qqUUlMTSR9KX%MpYTYGc^NNt(=#IqLZOAYeB+RAkO/pB)W)JV9rMT_R))P9XP6qNdW%\\JQdkZS^#=Dq_VrI#dYSYc(c=BBRXFE\\5Le^#qZPESPW+8#Kp+t/UB_Kz/rOVj58HFt_@IL^qU9lR+=zc=H",
				"Skp9VdGA%e6X+fpLS(7I%W/rU%G\\J@Uk)qRq)PePI%ZYNQ9eAlWNTW/8#!ffF(AA69^VttqZYBJRA6eP97#RZIN!/@@klP9E5BDB6eYQ8U+X5q#HUM%zVHGEY5Bqq9EBPH8OGWIYlDL%GE8kV@kRT%\\9KO5MkDHL^pKFU5%+DE8)rO9MMLe!ZtRIVR_qS9RKUlMXqc%UMT_TG(#=I^NZqYpLcNtTYB/Bp)RAJWeO)+kOYAT9PX6R)dqMVN_)Pr9Q#D=qkZr_JWVdS^\\%SBRBXc(\\FYIEYc=d##P+W8ZPp#^5KqESeLBOjV5KzF8U+H_/r/tLR=+zqUHcIt=^9l@_",
				"GLX+SkS9p%pVef6dAr)J@(7q%k%IWGU\\/UIlQ9RqWPAZ)eYeNP%!^AANTV/9fW8F6(#fJRP9ttZZ#AqY67eBRk65BINe/BP!@9DE@l5HM%YQGUV#8+HzUXq9I8OEYYBWB5qPGHqE89RTlDK%\\VLG@%kEkLDU5O5Ek+pMDK%FH^MRtR8)_OVer9!IZMLl_%UqSTRTX9KqMcUMNTLcG(Y=tq#IYNp^ZAY)+B/ApOWB)ekORJ)rN_T99XPqP6M)VRdZ\\VdQ#%=^_DqJSWkr(dEYSB#B=FRXYcIc\\PeKq#PLWS#+8^E5Zpz/H_BOtVr8j5U/+KFU@=^LR_+lc=zI9tqH",
				"+ApdeS6LkV%SX9Gpf@Uk/Gq\\)7W%(J%rIU9%APYWNlqeZRQPI)eAf9#FV(^T8fNA/!W69R#B6ZeRtYAtPZJq7BlB@9eE6N@PI5/k!D%qVXHGUHQ+#YMU58zOEWqPYHIYqBE8B95GTk\\E@Kk9DGVlR%8L%5^+HKEFD5DpOUkLM%RLVM!_ZR)9e8tOMrIUMTUqTc_SKXq%Rl9McZt^YYpT(IqGL=N#N+JOReAOY/)WB)pABk_dPRM9Vr96qTNX)P)dr^kJ%W\\#q_QV=ZDSY\\=cY#IdBXFSEB(RcqpSZ^L5eP8##KWP+E_FrKUt+/O58BHVzj/^HlqI_t@RzcL=+U=9",
				"%defX6p+VpSASGk9L%/GUJ\\I@WkqU(r7%)ZPYeQN)9eAW%RIqPlf#F6A(WA89VfN!T/^AB67Peq9Y#ZRtJtZRP@9D5E!B@BelIkN/6#XHzMU8%+VGqY5QUHBqPG8H5OqWYEE9YBIVE@%RkLTG\\Kkl8D%9pHK%UFM5D+E^OL5kDeM!ItZrR9V_L8M)ORXUqM%c9UKTTMqlSR_q^YNLp#cItYZGN(=TWRek)OB+)OAJBA/pYqRM)NVP_6P9dT)9Xr_kJSVWDdq^%rQZ#=\\FcYcEIRYX=#\\S(BBd#Z^EK5+q8SLp#PPWe8KU/H+j_5rtFBzOV/cqI9=t=^zl_HLUR+@",
				"pGVL+peSkd%f6SA9XkrW)@IG(7/%U\\qU%JAIel9)YRqPZeNW%PQ9!8^AWFNT#f6(Vf/A#JYR9q6ttBA7eZRZPBk@6B!9IN@PDEel/5V5+H%8HYQX#zUGqUMW9qIO5PEYqBGHYEB8\\8G9TL@lDEV%kKk%R+LDD5MKO5Hp%FE^kUVM9RRr!8)MeIZ_LOtTlK_U9qqSUXMcTMR%tNITc#YG(^qNpYZ=LOA)Y+BeB/RWkOAJp)P)6r_PMT9Rq)V9dXN^Zq\\dDJQ#k_SW%r=V=(XdYRYSBcFcI#\\BESP8eq+^#PZ#E5LpWKrz5/_jUBOK8/+tFVHlUz@^=ILRqc9t_H+=",
				"%A9dXSfpS+6kpLVeG%U%/JqUk(@\\7I)WGrZ%PPQWeAR9Nq)leYIff/#AV69NA(TW^8F!ARZBPZ7#t9etqRY6JPl/@5eDBIBEN!6@9k#qUXMGzVY%UQ8H+H5BEBq8YGWEOHY5IqP9Vk%ERK%\\lTkDL9G@8p^kHUE%+O5F5MDDKLeLOMt_IV8RZ)rR9!MXMRU%TMTqUcS9_KqlqZ=^LYNtGcp(#TIYNWJpR)AkOB+O/BY)eAqdXRN9)PT_V9Pr6M)_r=kV%S^QdW#D\\qJZF\\BcE#c=SYIBRdXY(#pWZKLES#q5P+e8^P8FVKHt/rB_+Oj/5UzcH+q=_9lL^tR=@zIU",
				"GVS69pAedfXSL%+pkrWq\\%kUG/UJ()%@I7IeWNPA%YPeQRlZ9)q!8V(/9fF#6AN^fAWTJYZeZ#R6B7PtRA9qtk@eE/Bl9@D5I6PB!N5+GUUVqHXzMYH#%8Q9qYHBWEPqG8EIBO5Y8GKk%\\k@E%Rl9VTLDLDEFk+^KH%UODp5M5M9_ZOVL!MIt8ReRr)lKTcRTMqUM%q_XU9SNIYp=tZY^NLGTqc#(A)AOpOJeRk)BYW+B/)69VXPdMR)NTrq_P9Zq%W=^rJkSVQ\\_dD#(X#IB=\\YccESdFYRBP8L5WSp^ZEK#e#q+Pz5t+VrFUK/HB/8_jOUz_t+lHIq9=L@c^=R",
				"pdVeLA9SG6SX%pkf+k/WG)U%(r\\qJ%I7U@APeYl%PRINWQZ)qe99#8F^f/N!(VAfWT6A#BY6RRZtJeZPAqt79B@@96l/IkEe5P!NDBVX+HHqUY5UGM#8Qz%WqqPIEBE9HY8B5YGO\\EG@9k%l8kKRVLD%T+HDKD^kOLFEUpM5%5VM9!RLO8MZ_ter)IRTUKq_MRqlcT%X9SMUt^IYTZ=GNpYLq#(NcOR)eYJpBAOA)WB/k+PR6MrdXT)V9NqP9)_^kqJ\\r=QZW%V_D#Sd=cXYd\\BS(I#EFRBcYSZ8^epW#P5LK#+PEqrK5U/FVBz+tH8jO/_lqzI@H+LUt_=c=R9^",
				"%96kSLVpGX+ApedSf%%\\7()WIrJ@UkG/qUZPNqRle)IQ9%AYPWef/(TN^8W!AAf9F#V6AZettRYqJP9R#6BZ7P/ENI6@!k5BlB9@eD#UUQYH+85M%qVHXGzBBHYEIq598OEWPqYGV%kDl9GL8RTk\\@EK%pkF5ODDMLU5^+KHE%eOZ)8R9rMtRLV!M_IXRcSq_K9l%UMTqUTMq=p(GTI#NLcZtY^YNWpO/BY)BA)+JOeRAkqXV9Tr6P)N_dPMR9)_=W#Q\\qDZVdr^Jk%SFBIBSdXR(EY\\=Yc#c#W5P#e8+PKqpS^ZLE8V+OB/5jzH_FrUKt/c+tRL@z=U=^HlIq_9",
				"GSp6dLSfXep%AV9k+rqk\\/)(UJGI%UW%7@IWANPlReQY)Z%ePq9!V9(#^N6AFWff8/TAJZ#eBRt7P6qARYZt9keBE@6ID59!Pl@/NB5GVUXHYzMH8#q+UQ%9YWHqIEG8P5BEqBYO8K\\kE9l%R@LVkG%DTLE+FHDO%UKMp^Dk55M_VZMR8It!reL9O)RlTTcU_qM%q9XMKRSUNYtp^TGNLY#qZI=(cAAOORYBk)eBWJ)p/+)9PVRrT)NMPqd6X9_Z%^Wk\\QSVJD_rq=#d(#=IcdScEYRF\\XBBYPLS5Ze#EK^+#p8WPqztr+K/B/HUj8F5VO_U_ltq@L9=I=cHz+R^",
				"AVXGpd%Sk96fpL+SeUWJrk/%q7%\\UI)@(G%eQIAPZWqPNe)l9RYf8A!9#fVT/(6W^ANFRYPJ#BAZtZe7qR9t6l@5kB@PeN/ED!6BI9q+M5VX#GQUUz8H%YHEq89WqBYYBHG5IOEPkGR8\\EVKD%k%L9Tl@^DUL+HpE5kF%MD5OKL9tMVMe_)OZIrRR8!MK%lTUXTSRcM9_UqqZILNt^qY(=pN#TcGYJ))AORWA/pOkBY+Bed6N)PRq99XV)Pr_TMrqVZ^k_%#=WSD\\dQJ\\XE(=cF#BBIcRdYSYp8KPSZ#LPW5E+eq#^F5HzrK8tOV+/j/_BUHz=Ulqc_R+t9=@^LI",
				"S%LXkfGpSd+VAp6e9(%)J7Urkq/@WUI\\G%RZlQqeIAWP9e%)NYPNf^AT6!9V#A8fW(F/tARPt7J#ZB9YRqe6ZIP65NDkBe@B@l!E9/Y#HMQz5VGX%+q8UHUEBI8YG9WYqOqE5HPBlV9RD%8\\KETGkLk@%OpDU5%L+EH5D^MFKk8eRt)IMV_MR9LrZ!OqX_%SMlTTUUKM9cqRGqTL(NNtY^cIZ#pY=BWY)/kAOAR+)JBOepTqrN9))P9R_6dPVMXQ_\\V#SZ^%kdqrDWJ=SFdEBc(=#cYX\\RIYB##eKPEPSLZq8p+5^WB8/HO/zrtK_5Fj+UVLc@=R9Ul_q^zH=tI+",
				"dSkeXS%p9fAGp+VL6/(7GJq%I%UUrk@W)\\PRqYQWZ)Pe%IA9elN#NTFAVfW/6f!9A8^(Btt6PZAqZ7RJ#9YRe@IN95eP!/DlkBB@6EXYQHMG#8Uzq5V%+HUqEYP8YB5BGE9WOqIHElD@RKVL%%k8\\TG9kHO5KUEpMk%^L+5DDFM8)!t_erOILMVR9RZUqSq%TX9RMMlTUK_c^G(YLYq#=NZNtcITpRB/e)AWBpkJAO+)YORT9MN9qPX)d)P_6rVkQ#JV%_D=SrZ^dq\\WcSBYE#FRBc\\(=YXdIZ#P^KL#+WEpPSq8e5KBOUHt8jV/Fzr_5/+qLRI=_c=+9HUl^z@t",
				"96kSpdV+ASGXL%fep%\\7qk/W@U(rJ)%UGIPNqWAPe9%RIQlZeY)/(TV9#8AfN!A^f6FWZetZ#BY9RtJPRA76q/ENeB@@BlIk56PD9!UUQGVX+%qY5MH#zH8BHYYWqqOEE98IBGP5%kDK\\EGTkl8R9V%@LkF5E+HD5^OLUDp%KMOZ)_VM9RL8MtReI!rRcSTTUKUMql%_XMq9=p(Yt^IcZGNLTqNY#pO/AOR)+JBA)YWkeBXV99PR6_dT)Nrq)MP=W#%^kqdrQZV\\_SJDBIB#=cXY\\S(EdFcYRW5PLSZ8qp#PKe#E^+V+OtrK5_FBzH/8/Uj+tR_lqz^HLU=@c9I=",
				"L6dAp9%S+epSVXGfk)\\/UI%%q@Gk(WJrU7lNP%)PZW9YAReQIeq^(#fW/fVAF9N8A!6TReBRqZAZ96#tYPJ7t6E@l!/PeB9BI@5kDNHUXq8U#G%HVY+M5zQIHqE5BBYOPWEq89GY9kEkL%VKT@\\lGR8%DDFH^MkpE5K+ODUL%5RZMLrOe_R!V89tMI)_cUM9RXTUqTqK%lMSTp^Z#=qYcYtGILNN(YORJBpWA+eOB))Ak/rVRdPXq9_MPT6N))9\\WkrD=_%dJ^QqVZS#dIc\\RBF#YY=SXE(cBe5Zp+W#Lq^S#8KPEP/+KFjV8t_UrB5Hz/O@tqH=+c_^IlLz=U9R",
				"%Vepdpfk9+6ASXGLS%WGk/IU7%@\\U(Jr)qZeYAP)eqP9N%RQIlWf8F9#W6T/A(fNA!^VAY6#Bq7tZ9eRtPJRZP@9B@!DN/BElI5k6e#+HVX8zQU%UqYM5HGBqPWq5GYBOHEE89IYVG@\\EL%D%TkklR89KpDK+HM%5k5F^OULDEe9!VMrI)ORZL8tMR_XKqTU9MSRUcMq%l_TqIYt^#N(=cpZGLNTYW)eORBk/p+OJB)AYAq6MPRP)9X_VdTN)r9_qJ^kDS#=dWrQVZ\\%FXY=cRcBBYI\\SE(d##8^SZ+EPWq5p#KPeL85UrKj/OV_+FBHz/tczIlq=9R+^tHL=U@_",
				"LfAe9Gkp+pV6S%SdX)UUG%r7k@IW\\q%(/Jle%YPIqA9)eNWZRPQ^6fF/!T9AW8(VfN#AR7R6ZJt#9qYeZAtBP6Dl9/kNBB!@EePI@5HzqHU5QV%8+UG#YXMIGEPB9YWO5qHYBEq89%k@%8D\\TLGkKVlERD%^KkL5+5MDFEpOHURIL!OM)VRr9Z_e8Mt_MMqRlSTU9KcTXqU%TNZY=N(tc#IpYqG^LYkJepA/O+B)OAWBR)r)dMX)9P_P6V9qTRN\\SrJ=Z#^dDqW%_QkVdc\\YB(B=YRXI#FScEeEp^WPPSq+85L##ZK//FUVzOr_j5+t8BKH@9HI+URl^=zt_cLq=",
				"e%fkdSV9L+ApSXGp6G%U7/(W%)@UkqJrI\\YZeqPRePl9%AWQI)NFf6T#N8/^Af9VA!W(6A7tBtYZR9R#ZPJqe9PDN@I@/6BlBe5k!EH#zQXY+UH%qVGM58UPBGYqEqBIOEWY895H@V%DElG%9Tk\\KR8LkKp%5HODkD5^+EULMF!eI)M89ORRLV_tMrZqXMSUqKR_UMTT%l9cYqN(^GI=TcZtYLN#peWk/RB)pY+JOA)ABOMq)9RT6Xr_dP9N)PVJ_S#kQq=\\dr^%VZDWYFcBcSXBdY\\=#E(RI^#EPZ#8WeqpSLKP+5U8/OKB5V/_FrtHzj+Ic9RqLz+@^Hl_=U=t",
				"6Gkd%pLXAVSpfS+9e\\r7/%I)JUWqkU(@%GNIqPZ)lQ%eWAeR9PY(!T#fW^Af8V96NA/FeJtBAqRPRYZ#7t9Z6EkN@P!65l@eBDIB/9U5QX#8HMq+GVzY%UHH9YqB5I8EqYWGEOBPk8DEVL9RkGK\\%lT%@FL5HpMDU^DE+%O5kKZM)MerRtL9_VI8RO!clSUX9_%MKTTMqURqpN(^q#TLZIYtNGc=YOA/RWBY)J)AOkB+peV)9RqPrNd69P)T_XMWZ#k_D\\Vrq%^SQd=JI(BcFRdE\\X#=cSYBY5PPZ#+eKp8LSE#qW^+zOK8j/HF5tr/B_VUtURqc=@=Hz_l9L^+I",
				"e69d%fpSS+GVLApkXG\\%/%UI(q@rW)Uk7JYNPPZe)RW9Iel%AqQF(/#f6WNVA!8^f9TA6eZBA7qtZ9JYRR#tP9E/@PD!IeBk@6lBN5HUUX#z8YG%5+HqVQMPHBqBG5EYO9qIEWY8@k%EV%LlKT8G9k\\DRKFkHp%MOE5LDD^+5U!ZOMeIr8_RM9RLV)tqcRUXM9qTUlK_MTS%Yp=^qN#GYcNITZt(LeOpRWkBBA+A)YJO/)MVXRq)PT9_)6rdP9NJW=k_SDQ%dZq\\r^#VYIBcFcRS#Y(Xd\\=BE^5WZ#E+#LqP8epSPKU+VK8/jBt_z5/FrOHIt+qc9=L_^Uz@HlR=",
				"ALSSfdX%ke6VpGp9+U)q(U/J%7G\\WIrk%@%lWRePQZqYNe)IAP9f^VN6#AfTF(8W!9/ARRZt7BPAt6eYqJ#Z9l6eID@5PN9E@!kB/BqHGYzXM#QHU+85VU%EIYEGq8BYPHq59WBOk9Kl%ERVD@kGL8\\%T^DEO%HUp5KFDML+k5LR_8IMte)!Z9rMVORM_TqMU%XSqcK9lTRUZTYGN^Lq(YpI#Nt=cJYABkR)W/eO)BAOp+dr9T)RNq9MV6P)PX_r\\%QSkV_#JWqDZ^=d\\d#SccEFBYIXR(=BYpeL#EZK#P^58+PSWqF/tB/KH8OU+5jzrV_H@_L9q=cRItz=Ul+^",
				"efkSpGp6XS9VdL%+AGU7qIrk\\J(%W/)%@UYeqW)IANQRPePlZ9%F6TVW!9(AN/8#^fAf67tZqJ#ePtZYBRA9R9DNe!kBE5I/@@6PBlHzQG85VUMYU+XH#%qPGYY59WH8EBqqIBOE@%DKL8\\kRl%GE9VTkK%5EML+FUOkDHDp5^!I)_rMVZt8O9MReRLqMST9lTc%qRKU_XUMYN(Y#NtpLG=I^TqcZek/ABAOO)Bp)RYW+JM)99P)PVNTX6Rrq_dJS#%DZ^WVQ=qk\\_drYcB#R(=IESBXcdFY\\^EPL+PS5K#W8Ze#qpU/Otjzr+HBV5K/8_FI9R_=Ult=L+zq@c^H",
				"XA6SVG%9LppdS+kfeJU\\qWr%%)kI/(@7UGQ%NWeIZPlA)PR9qeYAf(V8!f/^9W#NAT6FPReZYJAZR#qBt9t765lEe@kP/6B!@IBND9MqUG+5#UHV8XY%QzH8EHYq9BBIW5qEOYGPRkkKG8V%9\\LElTD%@U^FEDLpkD+MHO55%KtLZ_9MeORVrM8R)I!%McTKlXR_T9UqUSMqLZpYINq=Tt#^Gc(NY)JOA)AWpYOBRB+/keNdV96)qXrPPRT_9)MVrW%qZ_=\\^DkQd#SJE\\I#X(FBd=RcSYBcYKp5L8P#WeS+Z#qPE^HF+t5z8V/rjKB_O/U=Ht_zUc+@l=qL^R9I",
				"XAe9GS6pLdp+f%SkVJUG%r(\\I)/k@U%q7WQ%YPIRN)lPA9eZWqeAfF/!N(W^#9A6fVT8PR6ZJteqRB#97AZtY5l9/kIE!6@BBDPeN@MqHU5YU8HXV%z#GQ+8EPB9EH5IqWOGBYYqRk@%8lkL9E\\T%VKDGU^KkLOFMDH+5%pE5DtL!OM8ZrRMVRIe_)9%MqRlqc9_UTUMXTSKLZY=NGp#T^tcNqY(I)JepABOBYRO+kWA/)NdMX)TVPrRP_)q996VrJ=ZQWD\\k^dS_%#qE\\YB(SIRdc=YcF#BXKp^WP#5+eZSqE#LP8HFUVzB+j/Kr_/8tO5=HI+ULt=@ql^9c_Rz",
				"pSdpLeSGk%A69X+fVk(/I)Gqr7%U\\%J@UWARP)lYWIqZ%NPQ9ee9N#W^FV!Tff(/AA68#tBqR6ZJtAReZP97YBI@!69ekNPlE/5BD@VYX8HHG5Q#qUUM%z+WEq5IPY9YBEHB8OGq\\lEL9@K8DVkk%RT%G+OHMDKEL5p^FkU5%DV8MrR!_M)eLZOtRI9TqU9_qTlSXMcR%UMKtG^#TYYN(qZp=LcNIOBRBYeAA/WJOp)+k)PTRPrM9)9qdVXN_)6^QkD\\J%Z#_rW=VdSq=ScRdY#(BF\\IBEYcXS#Z+e^LPP#p5WKqE8rBKj/UtzO8F+VH_/5lLq=@I_URcHt+=^9z",
				"%AVkG69fSdpL+pSXe%UW7r\\%Uq/k)@I(JGZ%eqINPeWPAl9)RQYff8T!(/6V#9^AWNAFARYtJeZ7ZB#R9qtP6Pl@NkE/De@B6B!I59#q+Q5UUzGXVH%8YMHBEqY9HBGYqWIO5E8PVkGD8k%%KE\\9TLlR@p^D5LFk%EH+D5MOUKeL9)MZOI_MVRRr8t!XMKSlcRMTUT_U9q%qqZI(Np=NY^tTc#GLYWJ)/AOpkAROY+BB)eqd69)VX)9RPr_PTNM_rq#ZW=S%k^\\dDQVJF\\XB(IBc#c=dYRSEY#p8PP5WELZSeq+#K^8F5Oz+V/tKr/_jBHUcHzRUt+9_ql@^=L=I",
				"%SXpp+Ld6GSAVe9fk%(JkI@)/\\rqUWG%U7ZRQA)9lPNIW%eYPeqfNA9WA^#(!Vf8F/6TAtP#q9RBeJZRY6Z7tPI5B!B6@Ekel@9/DN#YMV8%HXU5Gq+HUzQBE8W5OIqH9YEqPBGYVlR\\LT9Ek8KkG@%%DpOU+M5DHFLE^DKk%5e8tVrRRMZM_L9!OI)Xq%T9U_UclTMKqRMSqGLt#cT^pNYZIY=N(WB)OB+YROAAJ)epk/qTNPP_rRV)9d6MX)9_QV^Dd\\kWZ%rqJ=S#FSE=RYdcI(#\\XYBcB##KS+qeZ5PLp8^WEP8BHrj_/K+ztF5UV/OcL=l=^@qtU_HzI+9R",
				"eA6+SL%dGkfpSpV9XGU\\@q)%/r7UI(kW%JY%N9WlZPIqe)RAePQFf(AV^f#!T6WN98/A6Re9ZRABJt7qt#YZP9lEBe6P@kND!IB@/5HqU%GH#X5Qz8YV+UMPEHOYIBq9YG5EWqB8@kkTK9VE8D%Ll\\G%RK^F5EDpHL5%MO+DkU!LZR_ReMM)Ir8V9OtqMcUT_XUlSM9qTKR%YZpcYTq^N(N#GtI=LeJO+AYWRA/kBBO)p)MdV_9rqR)9)PTP6XNJrWd%\\_kZ#SDQ^q=VY\\IY#dFc(BcRS=XBE^p5qLe#ZPPE+#S8WKUF+_t/8KzO/jBr5VHIHt^_@cqUR9=Llz+=",
				"6pAd+XSLpkS9Gf%eV\\IU/@J()k7q%rU%GWN)%P9QRlAqWPIeZYe(Wf#AAN^9TV/!6fF8eqRB9PtR#tZZJ7A6YE!l@B5I6BNe/kDP9@U8qX%MYHVQGU5z#H+H5EqO8EIWYYB9GBPqkLkETRl9\\DK%8%V@GFM^H5UOD+5EkL%pKDZrLMRt8RV)_OMIe!9c9MUU%q_TSTRlMXqKp#Z^cLGTt(Y=NNqYIOBJR+)BYO/ApAkWe)VPdR_NTrP99X))qM6WDrkdVQ\\^#%=ZS_JqIR\\cYESd=B#B(cFYX5+pZqK#eSPLWPE#^8+jFK_HB/rOtVz/8U5t=Hq^=L@lR_+U9cIz",
				"fX9%k+SeLApd6pVSGUJ%%7@(G)UI/\\kWqreQPZq9RYl%)PNAeWI6A/fTANF^fW#(98V!7PZAt9t6RRqBe#YZJD5/PNBI96l!@EB@ekzMU#Q%YHHq8XUV+G5G8BBYOEPIE5qHWqY9%R%VDTl@9kLEk\\GK8%Ukp55OKD^MHF+DELItOe)R8!RLrMZV9_MM%RXSUqq_M9UcTKTlNL=q(cGYTZ#^ptIYNk)pW/+BeYJBROO)AA)NXq9_TMrdPRVP69)SV=_#dQJ\\rDkW^q%ZcEBFBYSYd\\RcI=X#(EKW#Pq#^ep+Z5S8LP/HV8O_BU/FjK+r5tz9=+cR^LI@H=qtlz_U",
				"6+GSS%ALeXdpp9kVf\\@r(q%U)GJ/kI%7WUN9IRWZ%lYQPA)Pqee(A!NVff^FA#9W/T86e9JtZARR6PB#qZtY7EBkIePl695@B!/N@DU%5YG#qHHMXV8UQ+zHO9EYBEIP8qW5BYqGkT8lKVk9@RE\\L%DG%F5LOEp^DKUH+Mk5D%ZRM8_eLR!tMVrO)9IcUlqTXM_q%UT9RSKMpcNGYqZTYL^t#=(INO+ABAWJYe)ROBp/)kV_)T9qdrMNRPPX96)WdZQ%_r\\JVk^D=#qSIY(S#F\\dYEc=RBBXc5qP#L#pe^KZS+WP8E+_zBt8F/UHKrjVO5/t^UL_cH@I=ql=+Rz9",
				"XS9AepS+Lk6fVpd%GJq%UGk(@)7\\UWI/%rQWP%YAR9lqNee)PZIAV/fF9NA^T(68W#f!PZZR6#t9Rte7YqBAJ5e/l9BIB6NED@!@PkMGUqHVY%HQUz+8X#58YBEPWEOIYHGq5qB9RK%k@\\lT9Dk%GLEV8UEk^K+O5D5F%DMHpLt_OL!V8RR)ZI9rMeM%TRMqTqU_ScMK9UXlLY=ZYtGcT(pNI#^qN)ApJeOB+Y/Ok)BRWAN9XdMPT_r9V)6PRq)V%=rJ^Qd\\#WSqDk_ZE#B\\Y=SYdBIcXRcF(KLWp^S#qeP5E8+Z#PHtVFUrB_/O+/5jK8z=_+HIlL^@Rt9z=qcU",
				"6GV9fSdeSk%pLX+pA\\rW%U(/Gq7%I)J@kUNIePeRPYWqZ)lQ9A%(!8/6N#FVTfW^AA9feJYZ7tB6ZtAqRP9#REk@/DI@9eNP!65BBlU5+UzYXHGQ#8HM%VqH9qBGEqPYYB5I8OWEk8G%%lE@KDVL9RT\\kFLDk%OHKE5pMDU5+^ZM9OI8M!_)erRtRVLclKRMqUqTSX9_%UTMpNI=NG^YY(q#TLctZOA)pkBReA/WBY)+OJV)6X)TRM99qPrN_PdWZq=SQkJ%#_D\\Vd^rI(XBcScY#BFRdEY=\\5P8WE#Z^LP#+eKqSp+z5V/BKUtO8j/H_rFtUz+9LqI_Rc=@=^lH",
				"pVASGkX%9ep+SLd6fkWUqr7J%%GI@()/\\UAe%WIqQZPY)9RlPNe98fV!TAf/FWAN^#(6#YRZJtPAZ6q9tRBe7B@lekN5P/9!BI6@EDV+qG5QM#UH8%YHXUzWqEY9Y8BBP5OEIqHG\\GkK8DRV%@LTl9Ek%+D^EL5UpkKM5ODHF%V9L_M)teO!rR8RMZITKMTlS%XRq9Uq_UcMtIZYN(Lq=Y#cGT^pNO)JAA/)WpeB+BYROkP6d9)9NqXMP_TrRV)^qr%Z#V_=JDdQ\\kWS=X\\#(BEFBYRYSdcIcS8pLPPK#W^+q#eZ5Er5FtzOH8VUj_B/K+/lzH_UR=c+I=^L@qt9",
				"k%pe+XfASVSpG69dL7%IG@JUU(Wqkr\\%/)qZ)Y9Qe%ReWAINPPlTfWFAA6fN8V9!(/#^tAq69P7RtYZ#JeZBRNP!9B5DlI@eBkE/@6Q#8H%MzqY+GV5UUXHYB5PO8GEEqYW9HBqIDVL@TR%klGK\\8k%E95pMK5U%^ODE+LFkHD)er!RtIL89_VMZOMRSX9qU%MMqKTTlcRU_(q#YcLNZGIYtNp=^T/WBe+)kJB)AOAOpRY9qPM_N)dT69P)VXRr#_DJdVSrQq%^ZW=k\\BFRYYEc\\SX#=(IBcdP#+^qKEp#8LSP5WZeO8jU_H/FB5trz+VK/Rc=I^=9HLz_lUt+q@",
				"pfpAe6%GVdSXS+9kLkUIUG\\%rW/(Jq@%7)Ae)%YNZIePRQW9Pql96WfF(f!8#NAVA/T^#7qR6eAJYBtPZ9ZtRBD!l9EPk@@I5eB/N6Vz8qHU#5+XYMG%UQHWG5EPHB9qqE8YOBYI\\%Lk@kV8GElRKT%D9+%M^KFpLDHOUE5k5DVIrL!ZeM9M8t_RO)RTM9MqcXlKUq%TURS_tN#ZYpqNI^GLYc=(TOkBJeOWA)RB)A+p/YP)PdMVq)6RTN9_X9r^SDrJW_ZqkQV%d=#\\=cR\\YIF(XcSE#YBBdSE+p^5#P8Z#KLqWPer/jFU+8z5KBHt_VO/l9=HItcUzqL=_^+R@",
				"Gp9+SdXeLfp6ASkV%rk%@(/JG)UI\\Uq7W%IAP9RPQYle)N%WqeZ!9/AN#AF^6W(fVT8fJ#Z9tBP6R7qeRZtYAkB/BI@596D!EleN@P5VU%YXMHHz8UqGQ+#9WBOEq8PIG5HEYYqB8\\%TlER@9%LkkKDGVL+k5OHUKD%MF^E5DpMVOR8Mt!RIrZL_)9elTRUqU%q_M9cMTSKXNt=cG^LYTN#pZY(IqAOp+BR)eYkBOJA/)W)PX_TRNMr)PVd996qZ^=dQkVJ\\SDWr%#q_(=BYScEYdcRI\\#BXFPSWq#ZK^eE+5pLP8#zrV_BKHU//j+FtO58Ul+^Lq=I@9=tH_Rzc",
				"9peSfk6d%LpG+VAXS%IG(U7\\/%)kr@WUJqP)YReqNPZlAI9e%QW/WFN6T(#f^9!A8fAVZq6t7teBAR#J9YRPZ/!9IDNE@P6BkB@l5eU8HYzQUX#HV5%+qMGB5PEGYHqBIW9OqE8Y%L@l%DkEV9\\8TGkRKkMKO%5FHpD+L5D^UEOr!8I)ZMeRVMR9Lt_R9qqMScUX_TlUKM%T=#YGN(p^qTtNcIZLYpBeBk/ORWYOA+)J)AXPMT)9VRqrP)_6dN9=DJQS#Wk_\\^ZdqrV%BRYScBIcFd=(YX\\E#W+^#EP5Z#eSPq8pKLVjUB/O+K8/rz_5FHt+=IL9Rtqc@lU^zH=_",
				"SGV9d6pL%e+fXpSAk(rW%/\\k)%G@UJIqU7RIePPNAlZY9eQ)W%qN!8/#(9^fFA6AWVfTtJYZBe#RA697PqZRtIk@/@EB6P9BD5!elNY5+UXUVH#H%zM8GqQE9qBqHWIBPOG85YEYl8G%Ek\\9V@T%RLKkDOLDkHF+DpK5%UME^58M9OMZVRe!RItr_L)qlKRUcT_XqUM%9TMSGNI=^ptTqYcNL#YZ(BA)pROOYWe+k)BAJ/T)6XRVPrqM_)NP9d9QZq=kW^\\_JdSVD%r#S(XBcI=dFYYcER#\\B#P8WZ5Se#^qEK+LpPBz5VK+r/8U_/HjtFOLUz+qtl@cI^9==_HR",
				"k%p+eA6SVdSL9pGXf7%I@GU\\qW/()%krJUqZ)9Y%NWePRlPAIQeTfWAFf(V8#N^/9!A6tAq96ReZYBtRZ#JP7NP!B9lEe@@I6/Bk5DQ#8%HqUG+XYHUV5MzYB5OPEHYqqEIBW98GDVLT@kkKGEl9%\\8R%5pM5K^FEDHODk+LU%)erR!LZ_9M8ROVMtISX9UqMcTKUq_RTl%M(q#cYZpYI^GT=tNLN/WB+eJOA)RBYpOA)k9qP_MdV96RTrXP)N)#_DdJrW%qkQ\\=^ZVSBFRYY\\I#XcSdB=(EcP#+q^p5L8Z#eWSPKEO8j_UF+t5KB/VrzH/Rc=^IHt_zqL@+lU=9",
				"L%e+6VkAXfpS9SGdp)%G@\\W7UJUIq%(r/klZY9Neq%Qe)WPRIPA^fFA(8TfA6WV/N!#9RA69eYtRP7qZZtJB#6P9BE@Nl5D!e/Ik@BH#H%U+QqMz8GUY5XVIBPOHqYE8G5YBE9qW9V@TkGDkR%LK%l8E\\DpK5FD5^U%MEkOLH+Re!RZ9)LtIr_O8MMV_XqUcKSM%M9TRqlUTTqYcpI(ZLN#Y=GN^tYWe+O)/J)kBApBAROrqM_V69dN)P9XT)RP\\_JdWq#rVSD%=QZk^dFYYIXB\\EcR#BS(c=e#^q58PpKE+LW#PZS/8U_+5OFH/jtVBzKr@cI^tzRH=9=_+LUql",
				"SXk9dpApeGSf6+VL%qJ7%/kUIGr(U\\@W)%WQqPPA%)YIReN9elZVAT/#9fWF!N6(A8^fZPtZB#Rq6Jt7e9YRAe5N/@Bl!9kIDEB@6PGMQUXVq8H5YzU%+H#Y8YBqWE5P9EGHOqIBKRD%E\\kL@8l%kTG9VEU5kH+^MKLO%F5DDp_t)OMVLr!M8IZR9ReT%SRUTM9qlqMcUK_XYL(=^tZ#YNGNpcITqA)/pROJBeABkO+)YW9N9XRPdPM)T)V_6rq%V#=k^rDJZQSWdq\\_#EBBc=\\RY(ScIYXdFLKPWZSp+^P#E5q8e#tHOVKrFjUzB/+_5/8_=R+qlH=IUL9t^z@c",
				"XAe6k%V9SSGLpf+pdJUG\\7%W%(qr)IU@k/Q%YNqZePRWIl)e9APAfF(Tf8/NV!^W6A9#PR6etAYZtZJRq79#B5l9ENP@/Iek6!DBB@MqHUQ#+UYG5H8z%VX8EPHYBqBEY9I5GOWqRk@kDVG%lK89L%T\\EU^KF5pDkOELDM%5+HtL!Z)e9O8_MRrIRVM%MqcSXKRqTl_9MUTULZYp(qI=GYNT#Nct^)JeO/W)pBAAYBk+ORNdMV9q6XT9)rP)_PRVrJW#_q=Q%Z\\DSd^kE\\YIBFXBS#(dRcY=cKp^5P#8W#LPe+EqSZHFU+O85VBtz/j/_rK=HItRcz+L_U@=9^lq"		};
		for (int i=0; i<s.length; i++) testFindStrings2(s[i]);*/
		
		//testFindStrings(Quadrants.makeCipher(1, 17,10,0,7,180,180,180,270,0,0,0,1,1)); // best 408 #5 (score=719)
		//testFindStrings(Quadrants.makeCipher(0,4,7,0,7,180,180,180,180,1,0,0,0,1)); // best 340 (score=420)
		//testSequencesToTest();
		//for (int i=1; i<10; i++) 
		//	testActualDifference(i, 0f);
		//testLCS();
		//testSequenceFor();
		//testFindStrings("9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVBX9zXADd\\7L!=qpeXqWq_F#8c+@9A9B##6e5PORXQF%GcVZ_H%OT5RUc+_dYq_^SqWTtq_8JI+rBPQW6E@e!VZeGYKE_TYA9%#Lt_r9WI6qEHM)=UIkXJdF");
		//testExample();
		//testReduceString2();
		//testCipher();
		/*for (int i=0; i<testCiphers.length; i++) 
			testEvaluateCipher(testCiphers[i]);*/
	}
}
