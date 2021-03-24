package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;

/**
 * do the angled brackets in the 340 indicate direction? if some sections are
 * forward, and some reversed, then does reverting them to normal orientation
 * improve n-gram statistics?
 * 
 * here we explore via brute force the ngram statistics via exhaustively
 * searching a small number of split points.
 */
public class Reversals {

	// locations at which to split the cipher text.
	// no zero-length substrings are permitted.
	// given a string of length L, let split point = p.
	// range of p: [0, L-2]
	// a point at p makes two substrings: [0, p] and [p+1, L-1]

	public static List<Integer> points;
	// if true, reverse. otherwise, forward.
	// for n points, there must be n+1 orientations.
	public static List<Boolean> orientations;

	public static String cipher = Ciphers.cipher[1].cipher;
	public static int L = cipher.length();
	public static int count = 0;
	public static Set<Character> removalSet;
	public static void search() {

		// n is the number of split points
		int n = 4;
		System.out.println("n = " + n);
		init(n);

		// search all possible combinations of points.
		// for each combination of points, search all possible combinations
		// of orientations
		search(0);
		System.out.println("Searched " + count + " combinations.");
	}

	static void search(int pointsIndex) {

		if (pointsIndex >= points.size()) {
			// count++;
			searchOrientations(0);
			return;
		}
		int first;
		if (pointsIndex == 0)
			first = 0;
		else
			first = points.get(pointsIndex - 1) + 1;

		for (int i = first; i < L - 1; i++) {
			points.set(pointsIndex, i);
			search(pointsIndex + 1);
		}

		points.get(pointsIndex);

	}

	static void searchOrientations(int index) {
		if (index >= orientations.size()) {
			count++;
			dump();
			return;
		}

		orientations.set(index, false);
		searchOrientations(index + 1);
		orientations.set(index, true);
		searchOrientations(index + 1);
	}

	static void dump() {
		String line = "";
		for (Integer i : points)
			line += i + " ";
		for (Boolean b : orientations)
			line += b + " ";
		 //if (count % 100000 == 0)
		System.out.println(count + ": " + line);
	}

	public static void init(int n) {
		points = new ArrayList<Integer>();
		for (int i = 0; i < n; i++)
			points.add(i);
		orientations = new ArrayList<Boolean>();
		for (int i = 0; i <= n; i++)
			orientations.add(false);
	}
	
	/** treat each symbol as delimiters (i.e., each occurrence is a split point) */
	public static void searchSymbols(boolean dump) {
		String alphabet = Ciphers.alphabet(cipher);
		for (int i=0; i<alphabet.length(); i++) {
			char alpha = alphabet.charAt(i);
			System.out.println(alpha);
			points = new ArrayList<Integer>();
			for (int j=0; j<cipher.length(); j++) {
				if (cipher.charAt(j) == alpha) points.add(j);
			}
			orientations = new ArrayList<Boolean>();
			for (int j=0; j<points.size()+1; j++) {
				orientations.add(false);
			}
			searchSymbols(alpha, 0, dump);
		}
	}
	
	static void searchSymbols(char alpha, int index, boolean dump) {
		if (index >= orientations.size()) {
			count++;
			if (count % 100000 == 0) System.out.println(count+"...");
			int score;
			String ciphertext1 = cipherFromOrientations(false);
			//System.out.println(ciphertext1);
			score = fragments(ciphertext1, dump);
			if (score > 11) {
				dump();
				System.out.println("Alpha: " + alpha + ", Score: " + score + ", no delim, " + ciphertext1);
			}
			String ciphertext2 = cipherFromOrientations(true);
			//System.out.println(ciphertext2);
			score = fragments(ciphertext2, dump);
			if (score > 11) {
				dump();
				System.out.println("Alpha: " + alpha + ", Score: " + score + ", delim, " + ciphertext2);
			}
			return;
		}
		orientations.set(index, false);
		searchSymbols(alpha, index+1, false);
		orientations.set(index, true);
		searchSymbols(alpha, index+1, false);
	}
	
	static String cipherFromOrientations(boolean keepDelimiter) {
		StringBuffer sb = new StringBuffer();
		
		for (int i=0; i<=points.size(); i++) {
			StringBuffer sub;
			int a, b;
			if (i==0) {
				a=0;
				b=points.get(i);
			}
			else if (i==points.size()) {
				a=points.get(i-1)+1;
				b=cipher.length();
			} else {
				a=points.get(i-1)+1;
				b=points.get(i);
			}
			sub = new StringBuffer(cipher.substring(a,b));
			
			boolean orientation = orientations.get(i);
			if (orientation) sb.append(sub.reverse());
			else sb.append(sub);
			if (keepDelimiter && i<points.size()) sb.append(cipher.charAt(points.get(i)));
		}
		return sb.toString();
	}
	
	// locate fragments with at least 3 symbols in common
	static int fragments(String ciphertext, boolean dump) {
		
		int score = 0;
		for (int L=3; L<7; L++) {
			Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		
			for (int i=0; i<ciphertext.length()-L+1; i++) {
				String ngram = ciphertext.substring(i, i+L);
				String key = "" + ngram.charAt(0) + ngram.charAt(ngram.length()-1);
				List<Integer> val = map.get(key);
				if (val == null) val = new ArrayList<Integer>();
				val.add(i);
				map.put(key, val);
			}
			
			score += findFragments(ciphertext, map, L, dump);
			
			//for (String key : map.keySet()) System.out.println(L + ", " + key + ", " + map.get(key));
		}
		if (dump) System.out.println("Score: " + score);
		return score;

	}
	
	static int findFragments(String ciphertext, Map<String, List<Integer>> map, int L, boolean dump) {
		// check all positions between first and last symbol.
		// for an ngram of length L, there are L-2 such positions.
		
		int score = 0; 
		for (String key : map.keySet()) {
			List<Integer> val = map.get(key);
			if (val.size() < 2) continue;
			
			for (int middle=0; middle < L-2; middle++) {
				Map<String, List<Integer>> patterns = new HashMap<String, List<Integer>>(); 
				for (Integer pos : val) {
					String ngram = ciphertext.substring(pos, pos+L);
					String patternKey = "" + ngram.charAt(0) + ngram.charAt(middle+1) + ngram.charAt(ngram.length()-1);
					List<Integer> patternVal = patterns.get(patternKey);
					if (patternVal == null) patternVal = new ArrayList<Integer>();
					patternVal.add(pos);
					patterns.put(patternKey, patternVal);
				}
				for (String patternKey : patterns.keySet()) {
					List<Integer> patternVal = patterns.get(patternKey);
					if (patternVal.size() < 2) continue;
					score += patternVal.size() - 1;
					if (dump) System.out.println(L + ", " + middle + ", " + patternKey + ", " + patternVal + ", " + fragmentInfo(L, ciphertext, patternKey, middle, patternVal));
				}
			}
		}
		return score;
		
	}
	
	static String fragmentInfo(int L, String ciphertext, String patternKey, int middle, List<Integer> patternVal) {
		String result = "";
		result += "" + patternKey.charAt(0);
		for (int i=0; i<L-2; i++) {
			if (i==middle) result += patternKey.charAt(1); 
			else result += "?";
		}
		result += "" + patternKey.charAt(2);
		for (Integer i : patternVal) {
			result += ", " + ciphertext.substring(i, i+L); 
		}
		return result;
		
	}
	
	/* do more patterns emerge when we remove combinations of symbols ? */
	public static void searchRemovals() {
		String alphabet = Ciphers.alphabet(cipher);
		// 63 choose 5 = 7,028,847 combinations
		for (int n=1; n<=5; n++) {
			int[] pointers = new int[n]; 
			for (int i=0; i<pointers.length; i++) pointers[i] = i;
			count = 0;
			searchRemovals(alphabet, pointers, 0);
		}
		
	}
	static void searchRemovals(String alphabet, int[] pointers, int index) {
		if (index >= pointers.length) {
			count++;
			searchRemovalsProcess(alphabet, pointers, index);
			return;
		}
		if (index > 0) 
			pointers[index] = pointers[index-1]+1;
		while (pointers[index] < alphabet.length() - pointers.length + index + 1) {
			searchRemovals(alphabet, pointers, index+1);
			pointers[index]++;
		}
	}
	static void searchRemovalsProcess(String alphabet, int[] pointers, int index) {
		String cipher = cipherFromRemovals(alphabet, pointers);
		int score = fragments(cipher, false);
		if (score > 49) {
			dump(score, pointers, cipher);
		}
	}
	static String cipherFromRemovals(String alphabet, int[] pointers) {
		StringBuffer sb = new StringBuffer();
		removalSet = new HashSet<Character>();
		for (int i=0; i<pointers.length; i++) removalSet.add(alphabet.charAt(pointers[i]));
		//System.out.println(removalSet);
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			if (removalSet.contains(ch)) continue;
			sb.append(ch);
		}
		return sb.toString();
	}
	static void dump(int score, int[] pointers, String cipher) {
		String line = "";
		for (int i : pointers) line += i + " ";
		System.out.println("Score: " + score + ", " + line + ", " + removalSet + ", " + cipher);
	}
	
	public static void fragmentsAllCiphers() {
		for (Cipher c : Ciphers.cipher) {
			cipher = c.cipher;
			L = cipher.length();
			System.out.println(c.description);
			fragments(cipher, false);
		}
	}
	
	public static void fragmentsTest() {
		//fragments("B+pNd2GTL1|kPV^lp>REH(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(/THSOPcWzCW)++L)|BWlF+<C61L+TcR2GqMf.^pO(+;K|A8OZzSkpNHDM>cC-*BOY_Bt7<WdkF|p)", true);
		//fragments("HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++M9_2KR+PF5|djtz+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+VUlz-K46AycBF2RZ+^J+U-yBF<7pO+.M4T5*|JRlc<2OKMTbpBYD|Et5/R+#yS96zFB&+t4Vc.b425f^NFGlR8;(cBF5|N++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+<C61L+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+", true);
		
		//fragments(Ciphers.cipher[1].cipher, true);
		//fragments("HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2fj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^Rlc<2OKMTbpBYD|Et5/R+U-yBF<7pO++;K|A8OZzSkpNHDM>cC-*BOY_Bt7<WdkF|p)(/THSOPcWzCW)++L)|BWlF+<C61L+TcR2GqMf.^pO(KBz3.c|+-5ZUV>EC94:*1XBy++t4Vc.b425f^NFGlR8;(cBF5|N+#yS96zFB&+.M4T5*|", true);
		//fragments("HER>pl^V|1LTG2dNp+B#O%DWY.<*Kf)By:cM+UZGW)L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5F+&4/p8R^FlO*dCF>2D#5+Kq%;2UcXGV.zL|G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64KzlUV+^J+Op7<FByU+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5+|c.3zBKOp^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcOSHT/)p|FdW<7tB_YOB*Cc>MDHNpSzZO8A|K;+", true);
		
		fragments("HER>pl^VP1LTG2dNp+B(#O%DWY.<*f)By:cM+UGW()L#zHJSpp7^l8*V3pO++R2_9M+ztjd5FP+&4/p8R^FlO-*dCF>2D(#5+q%;2UcXGV.zL(G2Jfj#O+_NYz+@L9d<M++R2FBcyA64-zlUV+^J+Op7<FBy-U+R/5tEDYBpTMO2<clRJ*5T4M.+&BFz69Sy#+N5FBc(;8RlGFN^f524.cV4t++yBX1*:49CE>VU5-+c.3zB(Op^.fMqG2RcT+L16C<+FlWB)L++)WCzWcPOSHT/()pFdW<7tB_YOB*-Cc>MDHNpSzO8A;+", true);
	}

	public static void main(String[] args) {
		//searchSymbols(false);
		fragmentsTest();
		//fragmentsAllCiphers();
		//searchRemovals();
	}
}
