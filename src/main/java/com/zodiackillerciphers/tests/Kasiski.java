package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.FatesUnwind;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Kasiski {
	public static int MAX_FACTOR=100;
	public static List<Integer> factors(int x) {
		List<Integer> f = new ArrayList<Integer>();
		for (int i=2; i<MAX_FACTOR && i<=x; i++) {
			if (x % i == 0) f.add(i);
		}
		return f;
	}
	public static void test(String cipher) {
		
		Map<Integer, Integer> factorCounts = new HashMap<Integer, Integer>();
		
		boolean go = true;
		int n = 2;
		
		while (go) {
			NGramsBean bean = new NGramsBean(n, cipher);
			for (String str : bean.repeats) {
				String line = str + " ";
				List<Integer> locations = bean.locationsFor(str);
				if (locations.size() < 2) {
					System.err.println("Expected at least 2, got " + locations.size() + " for " + str);
					continue;
				}
				//line += locations.size() + " ";
				for (Integer i : locations) line += i + " ";
				System.out.println(line);
				
				for (int j=1; j<locations.size(); j++) {
					int diff = locations.get(j)-locations.get(0);
					line = " - locs " + locations.get(0) + " " + locations.get(j) + " diff " + diff + " factors "; 
					List<Integer> factors = factors(diff);
					for (Integer f: factors) {
						line += f + " ";
						Integer val = factorCounts.get(f);
						if (val == null) val = 0;
						val++;
						factorCounts.put(f, val);
					}
					System.out.println(line);
				}
			}
			if (bean.repeats.size() == 0) {
				go = false;
				break;
			}
			n++;
		}
		for (Integer key : factorCounts.keySet()) {
			System.out.println(key + ", " + factorCounts.get(key) + ", " + (key * factorCounts.get(key)));
		}
	}
	
	/** count ngram repeats based on their distances */
	public static int[] distribution(String cipher, int n, boolean showAll) {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		// track position of most recent occurrence of the ngram  
		Map<String, Set<Integer>> positions = new HashMap<String, Set<Integer>>(); 
		for (int i=0; i<cipher.length()-n+1; i++) {
			String ngram = cipher.substring(i,i+n);
			//System.out.print(i + " " + ngram + " ");
				
			Set<Integer> set = positions.get(ngram);
			if (set == null) set = new HashSet<Integer>();
			positions.put(ngram, set);
			for (Integer pos : set) {
				int diff = i-pos;
				Integer val = counts.get(diff);
				if (val == null) val = 0;
				val++;
				counts.put(diff, val);
			}
			set.add(i);
			//System.out.print(set);
			//System.out.println();
		}
		int max = -1;  int which = -1;
		for (Integer key : counts.keySet()) {
			if (showAll) System.out.println(counts.get(key) + " " + key);
			if (counts.get(key) > max) {
				max = counts.get(key);
				which = key;
			}
		}
		//System.out.println("Max " + max + " at " + which);
		return new int[] {which, max};
	}
	
	/** bartW's way of performing the KE   http://www.zodiackillersite.com/viewtopic.php?p=48156#p48156  */
	public static void bartW(String input) {
		int length = input.length();
		int count;
		for (int offset = 1; offset < length; offset++) {
			count = 0;
			for (int index = 0; index < length; index++) {
				if ((input.charAt(index) == input.charAt((index + offset)
						% length))) {
					count++;
					//System.out.println("repeat: " + input.charAt(index) + " " + index + " " + (index+offset));
				}
			}
			System.out.println(count + " " + offset);
		}
	}

	/** return list of positions for a given offset (shift) instead */
	public static Set<Integer> bartWPositions(String input, int offset) {
		int length = input.length();
		int count;
		Set<Integer> set = new HashSet<Integer>();
		count = 0;
		for (int index = 0; index < length; index++) {
			if ((input.charAt(index) == input.charAt((index + offset) % length))) {
				count++;
				set.add(index % length);
				set.add((index + offset) % length);
				//System.out.println((index % length) + "," + ((index + offset) % length));
			}
		}
		// System.out.println("" + offset + "," + count);
		return set;
	}
	
	/** modified bartW's way of performing the KE, so that it is run on all periods (including Scytale.decode) */
	public static void bartWPeriodic(String input) {
		
		for (int period=1; period<=170; period++) {
			String[] ciphers = new String[] { Scytale.decode(input, period),
					Periods.rewrite3(input, period) };
			for (int i=0; i<ciphers.length; i++) {
				String type = i == 0 ? "scytale" : "period";
				String decoded = ciphers[i];
				int length = decoded.length();
				int count;
				for (int offset = 1; offset < length; offset++) {
					count = 0;
					for (int index = 0; index < length; index++) {
						if ((decoded.charAt(index) == decoded.charAt((index + offset)
								% length))) {
							count++;
							//System.out.println("repeat: " + input.charAt(index) + " " + index + " " + (index+offset));
						}
					}
					System.out.println(count + " " + offset + " " + type + " " + period);
				}
			}
		}
	}

	/** return the intersection of KE exam positions at the given shift, and periodic repeating bigram positions at the given period */
	public static void intersections(String input, int shift, int period) {
		Set<Integer> set1 = bartWPositions(input, shift);
		Set<Integer> set2 = Periods.positions(input, period);
		Set<Integer> intersection = new HashSet<Integer>(set1);
		intersection.retainAll(set2);
		System.out.println(shift + "	" + period + "	" + set1.size() + "	" + set2.size() + "	" + intersection.size());
	}
	
	public static void testCompare() {
		for (int c = 0; c<2; c++) {
			for (int n = 1; n<5; n++) {
				String cipher = Ciphers.cipher[c].cipher;
				for (int period=1; period<cipher.length()/2; period++) {
					String re = Periods.rewrite3(cipher, period);
					//System.out.println(period);
					int[] max = distribution(re, n, false);
					System.out.println((c==0?"z340":"z408") + "	" + n + "	" + period + "	" + max[0] + "	" + max[1]);
				}
			}
		}
	}
	
	public static void testShuffle() {
		String cipher = Ciphers.cipher[1].cipher;
		/** maps max KE score to number of occurrences */ 
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		DescriptiveStatistics d = new DescriptiveStatistics();
		for (int i=0; i<1000000; i++) {
			if (i%10000 == 0) System.out.println(i+"...");
			cipher = CipherTransformations.shuffle(cipher);
			int[] dist = distribution(cipher, 1, false);
			Integer key = dist[1];
			Integer val = map.get(key);
			if (val == null) val = 0;
			val++;
			map.put(key, val);
			d.addValue(key);
		}
		for (Integer key : map.keySet()) {
			System.out.println(key + " " + map.get(key));
		}
		System.out.println("Min: " + d.getMin());
		System.out.println("Max: " + d.getMax());
		System.out.println("Mean: " + d.getMean());
		System.out.println("Std Dev: " + d.getStandardDeviation());
	}
	
	public static void testFactors() {
		//int[] nums = new int[] {78, 75, 150, 162, 109, 35, 42, 6, 39, 105, 30, 71, 10, 129, 113, 137, 26, 15, 69, 106, 133, 149, 5, 43, 25, 136, 110, 7, 81, 163, 84, 70, 3, 80, 68, 142, 120, 143, 21, 40, 23, 128, 100, 60, 115, 50, 66, 93, 2, 119, 20, 147};
		int[] nums = new int[] {61, 163, 49, 92, 97, 91, 23, 149, 74, 16, 46, 21, 169, 136, 189, 173, 39, 63, 98, 145, 32, 64, 141, 194, 73, 53, 115, 126, 128, 166, 35, 157, 176, 187, 37};
		for (int num : nums) {
			List<Integer> factors = factors(num);
			for (Integer factor : factors) System.out.println(factor);
		}
	}
	
	public static void main(String[] args) {
		//distribution(Ciphers.cipher[1].cipher, 1, true);
		bartW("YENSZNUMGLNYYRFVHENMZFZZFDHZVTQHAKXFPKCZPJITSMRYKVSTVOYNKTRRLUVP");
		//bartW(Ciphers.cipher[0].cipher);
		//bartWPeriodic(Ciphers.cipher[1].cipher);
		//testFactors();
		//bartWPositions(Ciphers.cipher[1].cipher, 61);
		//System.out.println(bartWPositions(Ciphers.cipher[0].cipher, 78));
		/*for (int shift=1; shift<=204; shift++) {
			for (int period=1; period<=204; period++) {
				intersections(Ciphers.cipher[1].cipher, shift, period);
			}
		}*/
		//testShuffle();
		//test("CSASTPKVSIQUTGQUCSASTPIUAQJB");
		//test(FatesUnwind.cipherFullDallison);
		//test("VQBUPPVSPGGFPNUEDOKDXHEWTIYCLKXRZAPVUFSAWEMUXGPNIVQJMNJJNIZYKBPNFRRHTBWWNUQJAJGJFHADQLQMFLXRGGWUGWVZGKFBCMPXKEKQCQQLBODOQJVEL");
	}
}
