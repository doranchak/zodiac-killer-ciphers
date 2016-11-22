package com.zodiackillerciphers.old;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;

public class Homophones {

	/* count longest runs for all symbol pairs in the given cipher 


	possibilities for each letter [a] we encounter:
	
	- hasn't been seen before
	- is expected by a subset [e1] of in-progress sequences set [P]
	- breaks some subset [e2] of in-progress sequences set [P]
	
	Let set [S] contain all distinct symbols we've seen so far
	
	if letter [a] hasn't been seen before:
		- for each letter [b] in [S], form new key [cd] where [c] is the lesser of [a] and [b], and [d] is the greater.
		   [cd] now "expects" either symbol [c] or symbol [d].  if [c] is seen, then expect [d].  if [d] is seen, then expect [c].
		- add [a] to [S]

	otherwise:
		- let [K] be the set of all keys that contain [a].
		- for each key [k1k2] in [K]:  (k1 is the first letter, k2 is the second letter)
		  	- the expected value is [k1] but [a] = [k2]:  reset running count to zero.  
		  		expected value is now [k1].
		  	- the expected value is [k1] and [a] = [k1]: increment running count.  update maximum for this key.
		  		expected value is now [k2].

	 */
	
	/** cache for factorials */
	static Map<Integer, BigDecimal> factorials = new HashMap<Integer, BigDecimal>();
	/** cache for binomials */
	static Map<String, BigDecimal> binomials = new HashMap<String, BigDecimal>();
	

	public static Map<Integer, Integer> longestRuns(String cipher, int[] hitMiss, int[] highRanks) {

		Set<Character> seen = new HashSet<Character>();

		// maps symbol combinations to the next symbol that is expected in the repeated sequence
		Map<String, Character> expected = new HashMap<String, Character>();
		// tracks lengths of discovered continuous repeated sequences
		Map<String, Integer> counts = new HashMap<String, Integer>();
		// tracks lengths of entire sequences
		Map<String, Integer> lengths = new HashMap<String, Integer>();
		// tracks maximum lengths of continuous repeated sequences per key
		Map<String, Integer> maxes = new HashMap<String, Integer>();
		// tracks number of times we'e encountered each length result
		Map<Integer, Integer> scores = new HashMap<Integer, Integer>();
		// map each symbol to the list of keys in which it is involved
		Map<Character, Set<String>> keys = new HashMap<Character, Set<String>>(); 
		
		
		// track every symbol pair that has never repeated 
		Set<String> nonrepeats = new HashSet<String>();
		
		for (int i=0; i<cipher.length(); i++) {
			char ch1 = cipher.charAt(i);
			if (!seen.contains(ch1)) { // new symbol.  add to set
				//debug("New symbol " + ch1);
				for (Character ch2 : seen) {
					String key = order(""+ch1+ch2);
					//debug("Forming new key " + key);
					expected.put(key, ch2);
					keysAdd(keys, key);
					nonrepeats.add(key);
					lengths.put(key, 2);
					maxes.put(key, 0);
				}
				seen.add(ch1);
			} else { // symbol is already associated with multiple keys.
				// possibilities for each key's symbol combinations:
				// - there is no expected value.  thus, the opposite of the current symbol becomes the expected value.
				// - there is an expected value.  if the symbol matches, increment the count and update the expected value.
				// 		if the symbol doesn't match, reset the count.  expected value remains the same. 
				Set<String> k = keys.get(ch1); // all keys this letter is involved with
				if (k != null) //
					for (String key : k) {
						int len = lengths.get(key);
						len++; lengths.put(key, len);
						Character ch2 = expected.get(key);
						//debug("Seen " + ch1 + " before.  Its key " + key + " has " + (ch2 == null ? "no expected value" : "expected value " + ch2));
						if (ch2 == null) { // no expectation exists.  
							if (key.charAt(0) == ch1) expected.put(key, key.charAt(1));
							else expected.put(key, key.charAt(0));
							//debug("New expected value is " + expected.get(key));
						} else { // there is an expectation
							if (ch1 == ch2) { // current letter meets the expectation
								//debug("We met the expectation");
								hitMiss[0]++;
								updateCounts(counts, scores, key);
								updateExpected(expected, key, ch1);
								nonrepeats.remove(key);
								
								if (counts.get(key) != null) { 
									int max = maxes.get(key);
									max = Math.max(max, counts.get(key));
									maxes.put(key, max);
								}
							} else { // current letter did not meet the expectation
								//debug("We did not meet the expectation.  Resetting count.");
								hitMiss[1]++;
								counts.put(key, 0);
							}
						}
					}
			}
		}
		scores.put(0, nonrepeats.size());
		//System.out.println("hits " + hits + " misses " + misses+ " ratio " + ((float)hits)/misses);
		//for (String key : lengths.keySet()) System.out.println(key+":" + lengths.get(key));
		for (String key : maxes.keySet()) {
//			System.out.println(key+","+maxes.get(key)+","+lengths.get(key));
			float rank = (2+maxes.get(key))/lengths.get(key);
			if (rank >= 0.9) highRanks[lengths.get(key)]++;
		}
		return scores;
	}
	
	public static void updateExpected(Map<String, Character> map, String key, Character ch1) {
		if (ch1 == key.charAt(0)) map.put(key, key.charAt(1)); else map.put(key, key.charAt(0));
		//debug("New expected value is " + map.get(key));
	}
	
	public static void updateCounts(Map<String, Integer> counts, Map<Integer, Integer> scores, String key) {
		Integer val = counts.get(key);
		if (val == null) val = 1;
		else val++;
		counts.put(key, val);
		//debug("New count: " + val);

		
		Integer val2 = scores.get(val);
		if (val2 == null) val2 = 1;
		else val2++;
		scores.put(val, val2);
		//debug("New score for this count: " + val2);
	}
	
	public static void keysAdd(Map<Character, Set<String>> map, String key) {
		
		char ch1 = key.charAt(0); char ch2 = key.charAt(1);
		
		Set<String> val;
		if (map.get(ch1) == null) val = new HashSet<String>();
		else val = map.get(ch1);
		val.add(key);
		map.put(ch1, val);
		
		if (map.get(ch2) == null) val = new HashSet<String>();
		else val = map.get(ch2);
		val.add(key);
		map.put(ch2, val);
		
	}

	static void debug(String msg) {
		System.out.println(msg);
	}
	
	public static String order(String symbols) {
		if (symbols.charAt(0) < symbols.charAt(1)) return symbols;
		else return "" + symbols.charAt(1) + symbols.charAt(0);
	}
	
	public static void dump(Map<Integer, Integer> counts, int[] hitMiss, int[] highRanks) {
		float total = 0;

		int max = 17;
		for (Integer key : counts.keySet()) {
			System.out.println("Length " + key + " Count " + counts.get(key));
			if (key > 0) {
				int d = Math.max(max-key,0); 
				total += ((float)counts.get(key)/Math.pow(d+1,3));
			}
		}
		System.out.println("Total: " + total);
		System.out.println("hits " + hitMiss[0] + " misses " + hitMiss[1] + " ratio " + ((float)hitMiss[0])/hitMiss[1]);
		String s = "";
		for (int i=0; i<highRanks.length; i++) {
			s += highRanks[i] + ", ";
		}
		System.out.println(s);
	}
	
	public static void testLongestRuns() {
		int[] hitMiss; int[] highRanks;
		Map<Integer, Integer> scores;
		
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns(Ciphers.cipher[0].cipher, hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns(Ciphers.cipher[1].cipher, hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("|FkdW<7tB_YOB*-Cc2GqMf.^pO(KBz3.c|-zlUV+^J+Op7<FBy-|Lz.VGXcU2;%qK+5#R8;(cBF5|N+#yS96z(D2>FkCd*-OlF^R8p++t4Vc.b425f^NFGlBy:cM+UZGW()L#zHJ/k4&+PF5|djtz+M9_Np+B(#O%DWY.<*Kf)HER>pl^VPk|1LTG2dU+R/5tE|DYBpbTMKOyBX1*:49CE>VUZ5-+(G2Jfj#O+_NYz+@L9RcT+L16C<+FlWB|)L>MDHNpkSzZO8A|K;+2<clRJ|*5T4M.+&BF++)WCzWcPOSHT/()pd<M+b+ZR2FBcyA64KSpp7^l8*V3pO++RK2", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("RcT+L16C<+FlWB|)LSpp7^l8*V3pO++RK2++)WCzWcPOSHT/()pd2GTL1|kPV^lp>REH|Lz.VGXcU2;%qK+5#JHz#L)(WGZU+Mc:yB-yBF<7pO+J^+VUlz-d<M+b+ZR2FBcyA64K/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|c.3zBK(Op^.fMqG22<clRJ|*5T4M.+&BF)fK*<.YWD%O#(B+pNlGFN^f524b.cV4t+++;K|A8OZzSkpNHDM>R8;(cBF5|N+#yS96zOKMTbpBYD|Et5/R+U(G2Jfj#O+_NYz+@L9yBX1*:49CE>VUZ5-+cC-*BOY_Bt7<WdkF|", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("Spp7^l8*V3pO++RK2#5+Kq%;2UcXGV.zL|Np+B(#O%DWY.<*Kf)d2GTL1|kPV^lp>REHBy:cM+UZGW()L#zHJ(D2>FkCd*-OlF^R8p|c.3zBK(Op^.fMqG2R8;(cBF5|N+#yS96zlGFN^f524b.cV4t++OKMTbpBYD|Et5/R+U-zlUV+^J+Op7<FBy-2<clRJ|*5T4M.+&BF9L@+zYN_+O#jfJ2G(L)|BWlF+<C61L+TcR>MDHNpkSzZO8A|K;+yBX1*:49CE>VUZ5-+K46AycBF2RZ+b+M<dp)(/THSOPcWzCW)++_9M+ztjd|5FP+&4k/|FkdW<7tB_YOB*-Cc", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("p8R^FlO-*dCkF>2D(++t4Vc.b425f^NFGlJHz#L)(WGZU+Mc:yB/k4&+PF5|djtz+M9_Spp7^l8*V3pO++RK2L)|BWlF+<C61L+TcR#5+Kq%;2UcXGV.zL|>MDHNpkSzZO8A|K;+-zlUV+^J+Op7<FBy-OKMTbpBYD|Et5/R+U9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<dFB&+.M4T5*|JRlc<2++)WCzWcPOSHT/()pyBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2|FkdW<7tB_YOB*-CcR8;(cBF5|N+#yS96zHER>pl^VPk|1LTG2d)fK*<.YWD%O#(B+pN", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("z69Sy#+N|5FBc(;8RSpp7^l8*V3pO++RK2/k4&+PF5|djtz+M9_-yBF<7pO+J^+VUlz-By:cM+UZGW()L#zHJp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|d<M+b+ZR2FBcyA64KHER>pl^VPk|1LTG2d)fK*<.YWD%O#(B+pN9L@+zYN_+O#jfJ2G(U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BF>MDHNpkSzZO8A|K;+yBX1*:49CE>VUZ5-+2GqMf.^pO(KBz3.c|p)(/THSOPcWzCW)++|FkdW<7tB_YOB*-Cc++t4Vc.b425f^NFGlRcT+L16C<+FlWB|)L", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("cC-*BOY_Bt7<WdkF|Spp7^l8*V3pO++RK2-yBF<7pO+J^+VUlz-_9M+ztjd|5FP+&4k/By:cM+UZGW()L#zHJp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|d<M+b+ZR2FBcyA64KHER>pl^VPk|1LTG2d)fK*<.YWD%O#(B+pN9L@+zYN_+O#jfJ2G(U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BF>MDHNpkSzZO8A|K;+RcT+L16C<+FlWB|)L2GqMf.^pO(KBz3.c|p)(/THSOPcWzCW)++yBX1*:49CE>VUZ5-+lGFN^f524b.cV4t++z69Sy#+N|5FBc(;8R", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("VB#PAfZ%%9/EcS)kNcG%FQXROP5e6##de_Ld/P#B@XqEHMU^RRkB#SQTBGq8OlEYItNRBPDR+j=6\\N(eEUHkF9%P/Z/UB%kOR=pX=BeYq!K@PH96FYGe+VWPOtTV+\\WVB#9AkpUzP5M8RUt%L)NVEKH=GH!FBX9zXADd\\7L!=qrI!Jk598LMlNA)Z(PkIU=)MHEq6IW9rXEV)5DYQNtTq7kIU^YJMcZKqpI)Wq!85LMr9#_tL#%9AYT_EKYGeZVS(/9#BPORAU%fRlqEAQJX@/ed\\rYT_RD9IpeXqWq_F#8c+@9A9B6WQPBr+IJ8_qtTJ@ZZcpOVWI5+tL)l^R6HWqS^_qYd_+cUR5TO%(Sz58Xq6)IK9DGq+@^=SrlfUe67DzG%%IMk^LMZJdr\\pFHVWe8Y", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("peXqWq_F#8c+@9A9B9%P/Z/UB%kOR=pX=Bk^LMZJdr\\pFHVWe8YEqlRf%UAROPB#9/(S_ed##6e5PORXQF%GcWqS^_qYd_+cUR5TO%VEXr9WI6qEHM)=UIkH!FBX9zXADd\\7L!=qP5M8RUt%L)NVEKH=GI9DR_TYr\\de/@XJQA)5DYQNtTq7kIU^YJMWV+eGYF69HP@K!qYeBPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HPOtTV+\\WVB#9AkpUz(Sz58Xq6)IK9DGq+@MI%%GzD76eUflrS=^VZeGYKE_TYA9%#Lt_Z@JTtq_8JI+rBPQW6Nk)ScE/9%%ZfAP#BVkRR^UMHEqX@B#P/dLrI!Jk598LMlNA)Z(PB#SQTBGq8OlEYItNR#9rML58!qW)IpqKZc", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		hitMiss = new int[] {0,0}; highRanks = new int[40]; scores=longestRuns("peXqWq_F#8c+@9A9BWqS^_qYd_+cUR5TO%VEXr9WI6qEHM)=UIkk^LMZJdr\\pFHVWe8YVB#PAfZ%%9/EcS)kNkRR^UMHEqX@B#P/dLMI%%GzD76eUflrS=^#9rML58!qW)IpqKZcBPDR+j=6\\N(eEUHkFAQJX@/ed\\rYT_RD9IG=HKEVN)L%tUR8M5PH!FBX9zXADd\\7L!=qZcpOVWI5+tL)l^R6HEqlRf%UAROPB#9/(S6WQPBr+IJ8_qtTJ@ZVZeGYKE_TYA9%#Lt_MJY^UIk7qTtNQYD5)POtTV+\\WVB#9AkpUzcG%FQXROP5e6##de_rI!Jk598LMlNA)Z(PB#SQTBGq8OlEYItNR@+qGD9KI)6qX85zS(B=Xp=ROk%BU/Z/P%9eYq!K@PH96FYGe+VW", hitMiss, highRanks); dump(scores, hitMiss, highRanks);
		

	}

	
    // return the longest common prefix of s and t
    public static String lcp(String s, String t) {
        int n = Math.min(s.length(), t.length());
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) != t.charAt(i))
                return s.substring(0, i);
        }
        return s.substring(0, n);
    }
	
	  // return the longest repeated string in s
    public static String lrs(String s) {

        // form the N suffixes
        int N  = s.length();
        String[] suffixes = new String[N];
        for (int i = 0; i < N; i++) {
            suffixes[i] = s.substring(i, N);
        }

        // sort them
        Arrays.sort(suffixes);

        // find longest repeated substring by comparing adjacent sorted suffixes
        String lrs = "";
        for (int i = 0; i < N - 1; i++) {
            String x = lcp(suffixes[i], suffixes[i+1]);
            if (x.length() > lrs.length())
                lrs = x;
        }
        return lrs;
    }	
	
	public static float weightedAvg(Map<Integer, Integer> runs) {
		// h in [1,7]: 25%
		// h in [8,inf]: 75%
		
		int[] totals = new int[] {0,0};
		
		for (int h : runs.keySet()) {
			if (h<8) totals[0]+=runs.get(h); else totals[1]+=runs.get(h);
		}
		//System.out.println(totals[0] + " - " + totals[1]);
		return .1f*totals[0] + .9f*totals[1];
	
	}

	// brute force homophone search using all combinations of n symbols on the given alphabet.
	// each returned row is an array: [sequence length, longest run count, subsequence in longest run, sequence]
	// if contig is true, then only count contiguous repeats
	// 
	public static List<Object[]> homophoneSearch(int n, String[] alphabet, String cipher, boolean contig) {
		// maps n-symbol combination to its corresponding ciphertext sequence 
		Map<String, StringBuffer> sequences = new HashMap<String, StringBuffer>();
		// maps each symbol to each key in which it appears as a substring
		Map<Character, Set<String>> keysBySymbol = new HashMap<Character, Set<String>>(); 
		
		// compute all combinations of n symbols selected from the given alphabet. 
		int[] indices;
		CombinationGenerator x = new CombinationGenerator (alphabet.length, n);
		StringBuffer combination;
		while (x.hasMore ()) {
		  combination = new StringBuffer ();
		  indices = x.getNext ();
		  for (int i = 0; i < indices.length; i++) {
		    combination.append (alphabet[indices[i]]);
		  }
		  String key = combination.toString ();
		  sequences.put(key, new StringBuffer()); // init sequences
		  
		  Set<String> set;
		  for (int i=0; i<key.length(); i++) { // track symbol combinations by symbol
			  char ch = key.charAt(i);
			  set = keysBySymbol.get(ch);
			  if (set == null) set = new HashSet<String>();
			  set.add(key);
			  keysBySymbol.put(ch, set);
			  
		  }
		  //System.out.println (key);
		  
		}		
		
		// generate sequences
		StringBuffer sb;
		for (int i=0; i<cipher.length(); i++) {
			for (String key : keysBySymbol.get(cipher.charAt(i))) {
				sequences.get(key).append(cipher.charAt(i));
			}
		}
		
		List<Object[]> results = new ArrayList<Object[]>();
		for (String key : sequences.keySet()) {
			String s = sequences.get(key).toString();
			Object[] r = repeatScore(s, n);
			int matches = 0;
			if (contig) matches = contig(s, (String) r[1]);
			else {
				matches = (Integer) r[0];
			}
			Object[] result = new Object[] {s.length(), matches, r[1], s};
			//System.out.println(key+": " + s+ ": " + lrs(s) + ": [" + r[0] + ", " + r[1] + "] "+ contig(s, (String) r[1])); 
			results.add(result);
		}
		
		return results;
	}

	// a more direct brute force homophone search using all combinations of n symbols on the given alphabet.
	// returns sum of rarest odds for each distinct symbol
	//
	// if contig is true, then track max # of contiguous sequences instead of simply the # of repetitions
	public static double[] homophoneSearch(int n, char[] alphabet, String cipher, boolean similarity, boolean print, boolean progress, boolean bestRepsOnly, boolean contig, Map<Character, Integer> countsMap, Map<Character, Double> best) {
		Map<Character, String> bestOut = new HashMap<Character, String>();
		// compute all combinations of n symbols selected from the given alphabet.
		int[] indices;
		CombinationGenerator x = new CombinationGenerator (alphabet.length, n);
		StringBuffer combination;
		Set<String> seen = new HashSet<String>(); // set to avoid printing dupes
		
		BigDecimal numerator = factorial(alphabet.length);
		BigDecimal denominator = factorial(n).multiply(factorial(alphabet.length-n));
		BigDecimal total = numerator.divide(denominator);
			
		//System.out.println("Total combinations: " + total);
		int counter = 0; int prev = 0;
		
		//boolean resume = true;
		while (x.hasMore ()) {
		  if (progress) {
			  counter++;
			  int current = Math.round(100*((float)counter)/total.floatValue());
			  if (current != prev) 
				  System.out.println("Progress: [" + current + "%]");
			  prev = current;
		  }
		  combination = new StringBuffer ();
		  indices = x.getNext ();
		  for (int i = 0; i < indices.length; i++) {
		    combination.append (alphabet[indices[i]]);
		  }
		  String key = combination.toString ();
		  /*if (resume && !"6RVXfl|".equals(key)) { continue; } // resume from this key
		  if ("6RVXfl|".equals(key)) {
			  resume = false;
		  }*/
		  
		  //System.out.println (key);
		  String seq = dumpSequenceFor(cipher, key);
		  Map<String, Integer> counts = new HashMap<String, Integer>();
		  for (int i=0; i<seq.length()-n+1; i++) {
			  String sub = seq.substring(i, i+n);
			  //Integer val = counts.get(sub);
			  //if (val == null) val = 0;
			  //val++;
			  //counts.put(sub, val);
			  counts.put(sub, count(seq, sub, contig));
		  }
		  int max = 0; String maxSub = null;
		  for (String sub : counts.keySet()) {

			    // don't bother calculating odds, cosine distances, etc. just track the best # of repetitions per symbol. 
			  	if (bestRepsOnly) {
			  		if (distinct(sub) != sub.length()) continue; // ignore anything with repeated symbols, to help avoid spurious results
			  		int reps = counts.get(sub);
					  // track best (rarest) score per symbol
					  for (int i=0; i<sub.length(); i++) {
						  char ch = sub.charAt(i);
						  Double d = best.get(ch);
						  if (d == null) d = 0d;
						  if (reps > d) {
							  d = (double) reps;
							  best.put(ch, d);
							  bestOut.put(ch, dumpSequenceFor(cipher, sub));
						  }
					  }
			  		continue;
			  	}
			  	
			  	// otherwise, continue with more odds-oriented computations and information
				  maxSub = sub;
				  String seq2 = filter(seq, maxSub);// remove any symbols that aren't in maxSub
				  
				  Object[] non = nonRepeats(seq2, maxSub);
				  int matches = (Integer)non[2];
				  max = matches;
				  
				  //if (max != counts.get(maxSub))throw new RuntimeException(max+" != " + counts.get(maxSub));
				  //System.out.println(maxSub + " " + max + " " + counts.get(maxSub));
				  
				  int cn = counts(maxSub, countsMap);
				  float freq = cn/(float)cipher.length();
				  String z408 = "";
				  if (HomophonesProblem.which == 1) {
					  z408 += "	";
					  for (int i=0; i<maxSub.length(); i++) z408 += Ciphers.decoderMap.get(maxSub.charAt(i));
					  z408 += "	"+(Ciphers.realHomophone(maxSub) ? "Yes" : "No");
				  } else
					  z408 = "";
				  
				  float sim = 0f;
				  if (similarity) sim = similarity(maxSub);
				  
				  
				  double odds = odds(maxSub, max, seq2, (String)non[0]);
				  double score = similarity ? (1+sim)*odds : odds;
				  
				  String output = key+"	"+maxSub+"	"+odds+"	"+matches+"	"+cn+"	"+(Math.round(freq*10000)/100f)+"%	"+sim+"	"+(score)+"	"+non[1]+z408; 
				  if (print && (odds >= Math.pow(10, n-1) || seq2.startsWith(maxSub+maxSub))) {
					  if (!seen.contains(maxSub)) { // don't print anything we've seen already
						  System.out.println(output);
						  seen.add(maxSub);
					  }
				  }
				  
				  // track best (rarest) score per symbol
				  for (int i=0; i<maxSub.length(); i++) {
					  char ch = maxSub.charAt(i);
					  Double d = best.get(ch);
					  if (d == null) d = 0d;
					  if (score > d) {
						  d = score;
						  best.put(ch, d);
						  bestOut.put(ch, output);
					  }
				  }
		  }
		}		
		double sum = 0; int c = 0;
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		for (Character ch : best.keySet()) {
			sum += best.get(ch);
			if (print) System.out.println("Best for " + ch+"	"+best.get(ch)+"	"+bestOut.get(ch));
			c++;
			min = Math.min(min, best.get(ch));
			max = Math.max(max, best.get(ch));
		}
		//return results;
		return new double[] {sum, min, max, sum/c};
	}
	
	/** return only the letters in str that occur in symbols */
	public static String filter(String str, String symbols) {
		StringBuffer result = new StringBuffer();
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<symbols.length(); i++) set.add(symbols.charAt(i));
		for (int i=0; i<str.length(); i++) if (set.contains(str.charAt(i))) result.append(str.charAt(i));
		return result.toString();
	}
	
	/** produce sum of cosine similarities among the given symbols */
	public static float similarity(String str) {
		/*Set<Character> distinct = symbols(str);
		if (distinct.size() != str.length()) {
			str = "";
			for (Character ch : distinct) str += ch;
		}*/
		Map<Character, Float> bySymbol = new HashMap<Character, Float>(); 
		float sum = 0;
		for (int i=0; i<str.length()-1; i++) {
			for (int j=i+1; j<str.length(); j++) {
				char ch1 = str.charAt(i);
				char ch2 = str.charAt(j);
				String bigram = ""+ch1+ch2;
				Float val = Ciphers.cosineSimilaritiesMap.get(bigram);
				if (val == null) val = 0f;
				//System.out.println(bigram+": " + val);
				sum += val;
				
				Float val2 = bySymbol.get(ch1);
				if (val2 == null) val2 = 0f;
				val2 += val;
				bySymbol.put(ch1, val2);
				
				val2 = bySymbol.get(ch2);
				if (val2 == null) val2 = 0f;
				val2 += val;
				bySymbol.put(ch2, val2);
			}
		}
		
		//for (Character ch : bySymbol.keySet()) System.out.println("Sum for symbol [" + ch + "]: " + bySymbol.get(ch));
		return sum;
	}
	
	/** compute odds against this sequence occurring by chance. 
	 * C = the repeating cycle
	 * m = number of repeats
	 * seq = entire sequence
	 * nonRepeat = non-repeating portion of the sequence
	 * key = distinct set of symbols being tested
	 * */
	public static double odds(String C, int m, String seq, String nonRepeat) {
		int L = C.length();
		int k = nonRepeat.length();
		int len = seq.length();
		
		//System.out.println(k+","+L+","+m+","+C+","+seq);
		//BigDecimal result = factorial(k).multiply(factorial(L)).multiply(binomial(k+m, m));
		double fk = factorial(k).doubleValue();
		double fL = factorial(L).doubleValue();
		double b = binomial(k+m, m).doubleValue();
		double dr = fk * fL * b;
		//System.out.println(dr);

		/* permutations of repeated symbols in the entire sequence */
		Map<Character, Integer> g = countMap(seq);
		for (Character ch : g.keySet()) dr *= factorial(g.get(ch)).doubleValue();
		//System.out.println(dr);
		
		/* factor out duplicate anagrams of nonRepeat */
		Map<Character, Integer> f = countMap(nonRepeat);
		for (Character ch : f.keySet()) dr /= factorial(f.get(ch)).doubleValue();
		//System.out.println(dr);
		
		/* factor out duplicate anagrams of C */
		Map<Character, Integer> h = countMap(C);
		for (Character ch : h.keySet()) dr /= factorial(h.get(ch)).doubleValue();
		//System.out.println(dr);
		
		return factorial(len).doubleValue() / dr;
		
	}
	
	public static Map<Character, Integer> countMap(String str) {
		Map<Character, Integer> symbolCounts = new HashMap<Character, Integer>();
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			Integer val = symbolCounts.get(ch);
			if (val == null) val = 0;
			val++;
			symbolCounts.put(ch, val);
		}
		return symbolCounts;
		
	}
	public static BigDecimal binomial(int n, int k) {
		String key = n + "," + k;
		BigDecimal val = binomials.get(key);
		if (val != null) return val;
		val = factorial(n).divide((factorial(k).multiply(factorial(n-k))));
		binomials.put(key, val);
		return val;
	}
	
	/** return distinct set of symbols in the given string */
	public static Set<Character> symbols(String str) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			set.add(ch);
		}
		return set;
	}
	
	/* compute factorial */
	public static BigDecimal factorial(int n) {
		if (n<2) return new BigDecimal(1);
		BigDecimal val = factorials.get(n);
		if (val != null) return val;
		val = factorial(n-1).multiply(new BigDecimal(n));
		factorials.put(n, val);
		return val;
	}
	/* return sum of occurrences in cipher for given symbols */
	public static int counts(String symbols, Map<Character, Integer> counts) {
		int total = 0;
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<symbols.length(); i++) {
			char ch = symbols.charAt(i);
			if (set.contains(ch)) continue;
			total += counts.get(ch);
			set.add(ch);
		}
		return total;
	}
	/* count number of non-overlapping matches of given search string within the given str. */
	public static int count(String str, String search, boolean contig) {
		if (contig) return contig(str, search);
		int count = 0;
		int index = 0;
		
		int match = str.indexOf(search, index);
		while (match > -1 && index < str.length()) {
			count++;
			index = match+search.length();
			match = str.indexOf(search, index);
		}
		return count;
	}
	
	/** remove repeating occurrences of C from seq */
	public static Object[] nonRepeats(String seq, String C) {
		//String[] results = {"",""};
		StringBuffer sb = new StringBuffer(seq);
		StringBuffer matches = new StringBuffer();
		int ind=0;
		//System.out.println("seq " + seq + " C " + C);
		int total = 0;
		int ip = 0;
		while (sb.indexOf(C, ind) > -1 && ind < sb.length()) {
			total++;
			int i = sb.indexOf(C, ind);
			sb.replace(i, i+C.length(), "");
			if (i>0) matches.append(sb.substring(ip, i)).append(" ");
			matches.append("[").append(C).append("] ");
			ind=i;
			ip=i;
		}
		matches.append(sb.substring(ind));
		//System.out.println(sb+","+matches);
		return new Object[] {sb.toString(), matches.toString(), total};
	}
	
	public static int distinct(String key) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<key.length(); i++) set.add(key.charAt(i));
		return set.size();
	}
	
	
	static Object[] repeatScore(String input, int n) {
		Map<String, Integer> counts = new HashMap<String, Integer>(); 
		String sub; int max = 0; String maxPattern = null;
		for (int i=0; i<input.length()-n+1; i++) {
			sub = input.substring(i, i+n);
			int c = 0; Set<Character> u = new HashSet<Character>();
			for (int j=0; j<sub.length(); j++) { 
				if (!u.contains(sub.charAt(j))) c++; 
				u.add(sub.charAt(j));
			}			
			if (c == n) {
				if (counts.get(sub) != null) counts.put(sub, counts.get(sub)+1);
				else counts.put(sub, 1); 
				if (counts.get(sub) > max) {
					max = counts.get(sub);
					maxPattern = sub;
				}
			}
		}
		return new Object[] {max, maxPattern};
	}
	
	// count max number of repeating contiguous sequences
	static int contig(String str, String pattern) {

		if (str == null || pattern == null) {
			//System.out.println("null.  " + str + ", " + pattern);
			return 0;
		}
		//System.out.println("contig " + str + ", " + pattern);
		int i = str.indexOf(pattern); int j=i;
		int n = pattern.length();
		
		int count = 0; int max = 0;
		
		while (i>-1 && (i+n) < str.length()) {
			if (i==j) {
				count++;
				max = Math.max(max, count);
				i=str.indexOf(pattern, i+n);
				j+=n;
				
			} else {
				max = Math.max(max, count);
				count=0; 
				i=str.indexOf(pattern, i+1);
				j=i;
				if (i>-1) count++;
			}
		}
		return max;
	}
	
	/** dump cipher text sequence with all but the given symbols removed */
	public static String dumpSequenceFor(String cipherText, String symbols) {
		String result = "";
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<symbols.length(); i++) set.add(symbols.charAt(i));
		for (int i=0; i<cipherText.length(); i++) {
			char ch = cipherText.charAt(i);
			if (set.contains(ch)) result += ch;
		}
		return result;
	}
	
	public static String[] alphabetFrom(String cipher) {
		Set<Character> a = new HashSet<Character>();
		for (int i=0; i<cipher.length(); i++) a.add(cipher.charAt(i));
		String[] alphabet = new String[a.size()];
		int i=0;
		for (Character c : a) alphabet[i++] = ""+c;
		return alphabet;
	}
	
	
	// find longest series of n symbols that repeat contiguously
	//public static Object[] longestContiguousRepeats(String sequence, int n) {
		
		/* the current letter is part of a repeating contiguous sequence if:

		 */
	//	}

	public static void testHomophoneSearch() {
		List<Object[]> list = homophoneSearch(4, HomophonesProblem.alphabet, Ciphers.cipher[HomophonesProblem.which].cipher, false);
		for (Object[] o : list) {
			if (((Integer) o[1]) > -4) {
				String s = "";
				for (int i=0; i<o.length; i++) s+=o[i] + ",";
				System.out.println(s);
			}
		}
	}
	public static void testHomophoneSearch2(int n) {
		Ciphers.initCS();
		String cipher = Ciphers.cipher[HomophonesProblem.which].cipher;
		HomophonesProblem.symbolCounts = HomophonesProblem.countsMapFor(cipher);
		char[] alphabet = Ciphers.alphabet(cipher).toCharArray();
		System.out.println("Cipher " + HomophonesProblem.which + ": " + cipher);
		System.out.println("Alphabet: " + Ciphers.alphabet(cipher));
		Map<Character, Double> best = new HashMap<Character, Double>();
		double[] d = homophoneSearch(n, alphabet, cipher, true, true, true, false, false, HomophonesProblem.symbolCounts, best);
		System.out.println("Sum: " + d[0]);
		System.out.println("Average: " + d[1]);
	}
	
	/** return measurement array.  
	 * 
	 * @param cipher cipher text to measure
	 * @param counts symbol counts
	 * @return scores array.  elements: sum of best reps per symbol, min, max, mean 
	 */
	public static double[] measure(String cipher, Map<Character, Integer> counts) {
		Map<Character, Double> best = new HashMap<Character, Double>();
		double[] d = homophoneSearch(2, Ciphers.alphabet(cipher).toCharArray(), cipher, false, false, false, true, true, counts, best);
		return d;
	}
	
	public static void testLrs() {
		System.out.println(lrs("abcabckdlfjkljdabcabcabcabcjiojf"));
	}
	
	/** perform a length 2 brute force homophone search, and return scores in terms of maximum count per symbol */
	// Object[] is [count, homophone candidate, full sequence]
	// ignores any results that do not repeat at least thresholdCount times, or that do not cover at least thresholdRatio % of the full sequence
	public static Object[] scoreHomophones(String cipher, int thresholdCount, float thresholdRatio) {
		List<Object[]> results = Homophones.homophoneSearch(2, Homophones.alphabetFrom(cipher), cipher, true);
		
		//TODO: also track sums per symbol, or maybe associated sums per homophone candidate (and relate accuracy to them)

		// track best findings on a per-symbol basis
		Map<Character, Object[]> map = new HashMap<Character, Object[]>();
		
		// for each homophone candidate, add scores for all others that share one of its symbols
		Map<String, Integer[]> associatedSums = new HashMap<String, Integer[]>();
		
		// track all findings
		List<Object[]> list = new ArrayList<Object[]>();
		
		for (Object[] o : results) { // array is [len of sequences, # of matches found, homophone candidate, full sequence]
			
			String str = (String) o[2];
			String seq = (String) o[3];
			Integer count = (Integer) o[1];
			Integer len = (Integer) o[0];
			//System.out.println("h " + str + " seq " + seq + " count " + count + " len " + len);
			float ratio = (float) count*str.length()/len;
			if (ratio >= thresholdRatio && count > thresholdCount) {
				list.add(new Object[] {count, str, seq});
				for (int i=0; i<str.length(); i++) {
					Character key = str.charAt(i);
					Object[] val = map.get(key);
					if (val == null) val = new Object[] {0, null, null};
					int c = (Integer) val[0];
					if (count > c) {
						val[0] = count;
						val[1] = str;
						val[2] = seq;
					}
					map.put(key, val);
				}
				
				int total = 0;
				for (String key : associatedSums.keySet()) {
					//System.out.println("str " + str + " key " + key);
					if (key.indexOf(str.charAt(0)) == -1 && key.indexOf(str.charAt(1)) == -1) continue;
					Integer[] val = associatedSums.get(key);
					if (val == null) val = new Integer[] {0,0};
					total += val[0];
					val[1] += count;
					associatedSums.put(key, val);
					//System.out.println(" - val " + val[0] + ", " + val[1] + " total " + total);
				}
				associatedSums.put(str, new Integer[] {count, total});
			}
		}
		return new Object[] {map, list, associatedSums};
	}
	
	/** print out the score map */
	public static void dump(Map<Character, Object[]> map) {
		if (map == null) return;
		int total = 0; int hits=0;
		for (Character ch : map.keySet()) {
			Object[] val = map.get(ch);
			int c = (Integer) val[0];
			total += c;
			boolean hit =Ciphers.realHomophone(""+val[1]);
			if (hit) hits++;
			int len = (""+val[2]).length();
			System.out.println(ch + ": " + len + ", " + (float)c*2/len + ", " + c + ", " + val[1] + ", " + val[2] + (hit ? " HIT " : " MISS "));
		}
		System.out.println("Total: " + total + ", hits " + hits + " ratio " + (float)hits/map.size());
	}
	/** print out the score list */
	public static void dump(List<Object[]> list) {
		if (list == null) return;
		int total = 0; int hits=0;
		for (Object[] o : list) {
			boolean hit =Ciphers.realHomophone(""+o[1]);
			if (hit) hits++;
			System.out.println(o[0] + ", " + o[1] + ", " + o[2] + (hit ? " HIT " : " MISS "));
			total += (Integer) o[0]; 
		}
		System.out.println("Total: " + total + ", hits " + hits + " ratio " + (float)hits/list.size());
	}
	/** print out the associated sums map */
	public static void dumpAS(Map<String, Integer[]> map) {
		if (map == null) return;
		int total = 0; int hits=0;
		for (String key : map.keySet()) {
			Integer[] val = map.get(key);
			total += val[1];
			boolean hit =Ciphers.realHomophone(key);
			if (hit) hits++;
			System.out.println(val[1] + " for " + key + ": " + val[0] + ", " + (hit ? " HIT " : " MISS ")); 
		}
		System.out.println("Total: " + total + ", hits " + hits + " ratio " + (float)hits/total);
	}
	
	
	/** turn result file into mediawiki table */
	public static void postprocess(String path) {
		List<String> lines = com.zodiackillerciphers.io.FileUtil.loadFrom(path); 
			
		/* track anagrams of the cycle.  we only need to see one of each unique set of symbols. */ 
		Set<String> seen = new HashSet<String>();
		int counter = 0;
		for (int i=0; i<lines.size(); i++) {
			String[] split = lines.get(i).replaceAll("<","&lt;").split("\t");
			
			String key = sort(split[0]);
			if (seen.contains(key)) continue;
			seen.add(key);
			
			//System.out.println("SMEG " + lines.get(i));
			
			if (split.length > 9 && split[9].equals("Yes")) System.out.println("|-valign=\"top\" style=\"background-color:#cfc\"");
			else System.out.println("|-valign=\"top\"");
			
			for (int j=0; j<split.length; j++) {

				String prefix;
				String suffix = "";
				if (j==0 || j>6)
					prefix = "| style=\"border-style: solid; border-width: 1px\"| ";
				else 
					prefix = "| style=\"text-align: right; border-style: solid; border-width: 1px\"| ";
				if (j==1) {
					suffix = new DecimalFormat("#.##E0").format(Double.valueOf(split[j]));
				} else if (j==5) {
					suffix = new DecimalFormat("##0.00").format(Float.valueOf(split[j]));
				} else if (j==6) {
					suffix = new DecimalFormat("#.##E0").format(Double.valueOf(split[j]));
				} else if (j==8) {
					suffix = split[j].toUpperCase();
				} else if (j==0 || j==7) {
					suffix = split[j].replaceAll("\\|","&#124;");
				} else {
					suffix = split[j];
				}
				System.out.println(prefix + suffix);
				
			}
			counter++;
			if (counter == 200) break;
		}
	}

	/** return string in char-sorted order */
	public static String sort(String str) {
		char[] sorted = str.toCharArray();
		Arrays.sort(sorted);
		return String.valueOf(sorted);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testLongestRuns();
		//testLrs();
		//System.out.println(similarity("pW+U"));
		//System.out.println(count("+K+++K+++K++++K+++K+++++K++++K+","+K+"));
		//System.out.println(counts("B+"));
		//shit();
		//System.out.println(odds("B+", 10, "+BB++++++++++B++B+B+B+B++B+B++B++BB+", "+B+++++++++++++B"));
	
		//postprocess("/Users/doranchak/projects/work/java/zodiac/homophone-brute-force-search-results/second-hom-7-1-sorted");
		
		/*
		System.out.println(contig("F!F!F!F!F!F","F!"));
		System.out.println(contig("BB#BB#B#B#B#B#B#B#B##B","B#"));
		System.out.println(contig("BB#BB#B#B#B#B#B#B#B##B","#B"));*/
		//HomophonesProblem.which = Integer.valueOf(args[1]);
		testHomophoneSearch2(4);
		//testHomophoneSearch2(Integer.valueOf(args[0]));

		//testHomophoneSearch2(2);
		//odds(String key, String C, int m, String seq, String nonRepeat) {
		//System.out.println(odds("ZPW+ONE", "ZPW+ONE", 5, "ZPW+ONEZPW+ONEEZPW+ONEZPW+ONEZPW+ONEZPW++WZEOZ+WOEWOE","EZPW++WZEOZ+WOEWOE"));
		//System.out.println(odds("Df_94/", "Df_94/", 2, "Df_94/Df_94/D49f4449f/_D","D49f4449f/_D"));
		//System.out.println(odds("R2z8", "R2z8", 2, "R2z8R2z8R22z2zR2zR2Rz8R2z2Rzz8","R22z2zR2zR2Rz8R2z2Rzz8"));
		//System.out.println(odds("|B", "|BB|", 4, "|BB||BB|B|B|BB|BB||BB|","B|BBB|"));
		//System.out.println(nonRepeats("|BB||BB|B|B|BB|BB||BB|","|BB|"));
		//System.out.println(odds("BO<B", 4, "NBO<BOOON<BO<BBO<BNBNBBO<BO<BOBNO",nonRepeats("NBO<BOOON<BO<BBO<BNBNBBO<BO<BOBNO","BO<B")));
		//System.out.println(nonRepeats("NBO<BOOON<BO<BBO<BNBNBBO<BO<BOBNO","BO<B"));
		//System.out.println(odds("+K+", 1, "+K+++K+++K++++K+++K+++++K++++K+",nonRepeats("+K+++K+++K++++K+++K+++++K++++K+","+K+")));
		//System.out.println(similarity("5LMZpW"));
		//System.out.println(binomial(63,5));
//			System.out.println(factorial(65));
	}
}
