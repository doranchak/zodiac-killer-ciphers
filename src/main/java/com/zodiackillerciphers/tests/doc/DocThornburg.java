package com.zodiackillerciphers.tests.doc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/**
 * test Doc's ideas (a quadrant has N total symbols. the rest of the cipher
 * has exactly N occurrences of symbols not appearing in that quadrant.)
 */
public class DocThornburg {
	public static void search(String cipher, int width, int shuffles, boolean dump) {
		System.out.println(cipher);
		Map<Integer, StatsWrapper> map = new HashMap<Integer, StatsWrapper>();
		for (int i=1; i<=150; i++) {
			StatsWrapper stats = new StatsWrapper();
			stats.name = "Doc quadrant matches for N=" + i;
			map.put(i, stats);
		}
		Map<Integer, Integer> counts = counts(cipher, width, true);
		System.out.println(counts);
		
		for (int i=1; i<=150; i++) {
			Integer count = counts.get(i);
			if (count == null) count = 0;
			map.get(i).actual = count;
		}
		
		if (shuffles < 1) return;
		for (int k=0; k<shuffles; k++) {
			if (k % 100 == 0) System.out.println(k + "...");
			cipher = CipherTransformations.shuffle(cipher);
			counts = counts(cipher, width, dump);
			for (int i=1; i<=150; i++) {
				Integer count = counts.get(i);
				if (count == null) count = 0;
				map.get(i).addValue(count);
			}
		}
		
		for (int i=1; i<=150; i++) {
			Integer count = counts.get(i);
			if (count == null) count = 0;
			map.get(i).output();
		}
		
	}
	
	/** consider quadrant Q with upper left corner (r1, c1) and lower right (r2, c2).
	 * consider rest of cipher not in Q, call it R.
	 * let N be count of all symbols in Q.
	 *  let M be count of all symbols in R.
	 *  return {N,M}.  We are interested in cases where N=M.
	 */
	static int[] count(String cipher, int width, int r1, int c1, int r2, int c2, boolean dump) {
		StringBuffer sb = new StringBuffer(cipher); 
		int N = (r2-r1+1)*(c2-c1+1);
		Set<Character> Q = new HashSet<Character>(); 
		for (int r=r1; r<=r2; r++) {
			for (int c=c1; c<=c2; c++) {
				int pos = r*width+c;
				char ch = sb.charAt(pos);
				Q.add(ch);
				sb.setCharAt(pos, ' ');
			}
		}
		Set<Character> R = new HashSet<Character>();
		int M = 0;
		for (int i=0; i<sb.length(); i++) {
			char ch = sb.charAt(i);
			if (ch == ' ') continue;
			if (Q.contains(ch)) continue;
			M++;
			R.add(ch);
		}
		if (dump && N==M) {
			System.out.println("====================");
			System.out.println("Q: " + Q);
			System.out.println("R: " + R);
			System.out.println("N: " + N);
			System.out.println("M: " + M);
			System.out.println(r1+","+c1 + " - " + r2+","+c2);
		}
		return new int[] {N,M};
	}
	
	/** return map of counts.  key is value of N (when equal to M).  value is number of occurrences of N==M observed over all possible quadrants. */
	public static Map<Integer, Integer> counts(String cipher, int width, boolean dump) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int r1=0; r1<cipher.length()/width; r1++) {
			for (int c1=0; c1<width; c1++) {
				for (int r2=r1; r2<cipher.length()/width; r2++) {
					for (int c2=c1; c2<width; c2++) {
						int[] counts = count(cipher, width, r1, c1, r2, c2, dump);
						if (counts[0] == counts[1]) {
							Integer key = counts[0];
							Integer val = map.get(key);
							if (val == null) val = 0;
							val++;
							map.put(key,  val);
						}
					}
				}
			}
		}
		
		return map;
	}
	
	public static void test(String cipher, int width) {
		search(cipher, width, 0, true);
	}
	public static void main(String[] args) {
		test(Ciphers.Z408, 17);
	}
}
