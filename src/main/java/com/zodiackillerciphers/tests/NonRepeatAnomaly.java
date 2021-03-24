package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

public class NonRepeatAnomaly {

	/**
	 * returns length of longest substring of the given cipher starting at
	 * position i, containing at most the given number of repeating symbols
	 */
	public static int nonrepeat(String cipher, int i, int maxRepeats) {
		Set<Character> seen = new HashSet<Character>();
		String sub = "";
		for (int j = i; j < cipher.length(); j++) {
			char ch = cipher.charAt(j);
			seen.add(ch);
			sub += ch;
			int diff = sub.length() - seen.size();
			if (diff > maxRepeats) return sub.length()-1;
		}
		return seen.size();
	}
	public static int nonrepeat(String cipher, int i) {
		return nonrepeat(cipher, i, 0);
	}

	/** output all substrings of the given cipher, having no repeating symbols */
	public static void allNonrepeatsNonMaximal(String cipher) {
		for (int i = 0; i < cipher.length(); i++) {
			int L = nonrepeat(cipher, i);
			String sub = "";
			for (int j = 0; j < L; j++) {
				sub += cipher.charAt(i + j);
				System.out.println(sub.length() + " " + sub);
			}
		}
	}

	/**
	 * output all maximal substrings of the given cipher, having no repeating
	 * symbols
	 */
	public static void allNonrepeats(String cipher) {
		for (int i = 0; i < cipher.length(); i++) {
			int L = nonrepeat(cipher, i, 0);
			System.out.println(L + " " + cipher.substring(i, i + L));
		}
	}

	/** return counts of non-repeating segments by length */
	public static Map<Integer, Integer> nonRepeatCounts(String cipher, int maxRepeats) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < cipher.length(); i++) {
			int key = nonrepeat(cipher, i, maxRepeats);
			Integer val = map.get(key);
			if (val == null)
				val = 0;
			val++;
			map.put(key, val);
		}
		return map;
	}

	/**
	 * compute non-repeat segment anomaly (i.e., maximum standard deviations
	 * observed between actual and shuffled cipher)
	 */
	public static void anomaly(String cipher, int shuffles, int maxRepeats) {
		// measure actual cipher
		Map<Integer, Integer> map = nonRepeatCounts(cipher, maxRepeats);
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (Integer key : map.keySet()) {
			min = Math.min(min, key);
			max = Math.max(max, key);
		}

		// measure observations on randomizations of the cipher
		Map<Integer, DescriptiveStatistics> stats = new HashMap<Integer, DescriptiveStatistics>();
		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			Map<Integer, Integer> mapShuffle = nonRepeatCounts(cipher, maxRepeats);
			for (int key = min; key <= max; key++) {
				DescriptiveStatistics desc = stats.get(key);
				if (desc == null)
					desc = new DescriptiveStatistics();
				stats.put(key, desc);

				Integer val = mapShuffle.get(key);
				if (val == null)
					val = 0;
				desc.addValue(val);
			}
		}

		// compute standard deviation by length
		for (Integer key : map.keySet()) {
			DescriptiveStatistics desc = stats.get(key);
			double std = desc.getStandardDeviation();
			double sigma = (map.get(key) - desc.getMean()) / std;
			System.out.println(sigma + " " + key + " " + map.get(key) + " "
					+ desc.getMin() + " " + desc.getMax() + " "
					+ desc.getMean() + " " + desc.getStandardDeviation());
		}

	}

	static void shuffleTest(String cipher, int shuffles, int maxRepeats) {
		DescriptiveStatistics stats = new DescriptiveStatistics();

		Map<Integer, Integer> byCount = new HashMap<Integer, Integer>();

		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			Map<Integer, Integer> map = nonRepeatCounts(cipher, maxRepeats);
			Integer val = map.get(17);
			if (val == null)
				val = 0;
			stats.addValue(val);
			Integer val2 = byCount.get(val);
			if (val2 == null)
				val2 = 0;
			val2++;
			byCount.put(val, val2);
		}
		System.out.println(stats.getMin() + " " + stats.getMax() + " "
				+ stats.getMean() + " " + stats.getStandardDeviation() + " "
				+ (26 - stats.getMean()) / stats.getStandardDeviation());
		System.out.println(byCount);
	}

	/** keep shuffling until we see something */
	static void shuffleTest2(String cipher, int maxRepeats) {
		int num = 0;
		int max = 0;
		while (true) {
			cipher = CipherTransformations.shuffle(cipher);
			Map<Integer, Integer> map = nonRepeatCounts(cipher, maxRepeats);
			num++;
			Integer val = map.get(17);
			if (val != null)
				max = Math.max(max, val);
			if (val != null && val >= 26) {
				System.out.println("found " + val
						+ " count at length 17 after " + num + " shuffles.");
				return;
			}
			if (num % 100000 == 0)
				System.out.println(max + " " + num + "...");
		}
	}

	static Integer max(Set<Integer> set) {
		int max = Integer.MIN_VALUE;
		for (Integer i : set)
			max = Math.max(max, i);
		return max;
	}

	/** track all counts by length, for a number of shuffles.  compare to actual counts for the given cipher. */
	static void shuffleTest3(String cipher, int shuffles, int maxRepeats) {
		System.out.println(cipher);
		String original = cipher;
		
		// key: length
		// val: map
		//        key: count
		//        val: # of occurrences with this count
		Map<Integer, Map<Integer, Integer>> counts = new HashMap<Integer, Map<Integer, Integer>>();

		for (int i = 0; i < shuffles; i++) {
			if (i % 10000 == 0) System.out.println(i+"...");
			cipher = CipherTransformations.shuffle(cipher);
			Map<Integer, Integer> map = nonRepeatCounts(cipher, maxRepeats);
			for (Integer L : map.keySet()) {
				Map<Integer, Integer> byCount = counts.get(L);
				if (byCount == null) {
					byCount = new HashMap<Integer, Integer>();
				}
				int count = map.get(L);
				Integer val = byCount.get(count);
				if (val == null)
					val = 0;
				val++;
				byCount.put(count, val);
				counts.put(L, byCount);
			}
		}

		// output the full results
		System.out.println("=== Results from shuffles, maxRepeats " + maxRepeats + "===");
		for (Integer L : counts.keySet()) {
			for (Integer count : counts.get(L).keySet()) {
				Integer val = counts.get(L).get(count);
				System.out.println(L + "	" + count + "	" + val);
			}
		}
		
		// now output the cipher's actual counts and compare them to the counts found during shuffles
		System.out.println("=== Cipher results, maxRepeats " + maxRepeats + "===");
		Map<Integer, Integer> map = nonRepeatCounts(original, maxRepeats);
		for (Integer L : map.keySet()) {
			Integer count = map.get(L);
			Integer occurrences = 0;
			if (counts.get(L) != null) {
				occurrences = counts.get(L).get(count);
				if (occurrences == null) occurrences = 0;
			}
			System.out.println(L + "	" + count + "	" + occurrences);
		}
		
		
	}

	/**
	 * in this variation, normalize the non-repeating segments based on how much
	 * of the cipher is covered by their occurrences. this might help adjust for
	 * overlapping segments.
	 */
	public static void allNonrepeatsNormalized(String cipher) {

		// map segment length to all positions covered by segments of that
		// length
		Map<Integer, Set<Integer>> seen = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();

		int max = 0;

		for (int i = 0; i < cipher.length(); i++) {
			int L = nonrepeat(cipher, i);
			Integer count = counts.get(L);
			if (count == null)
				count = 0;
			count++;
			counts.put(L, count);

			max = Math.max(max, L);
			System.out.println(L + " " + cipher.substring(i, i + L));
			Set<Integer> val = seen.get(L);
			if (val == null)
				val = new HashSet<Integer>();
			for (int j = 0; j < L; j++)
				val.add(i + j);
			seen.put(L, val);
		}

		System.out.println("=================");
		for (int L = 1; L <= max; L++) {
			System.out.println(L + " " + counts.get(L) + " "
					+ seen.get(L).size() + " " + ((float) seen.get(L).size())
					/ counts.get(L));

		}

	}

	/**
	 * estimate the probability of finding a non-repeating segment of the given
	 * length
	 */
	public static void testProb(String cipher, int L) {
		int total = 1;
		int n = 0;
		while (true) {
			cipher = CipherTransformations.shuffle(cipher);
			int len = nonrepeat(cipher, 0);
			if (len == L) {
				n++;
				double prob = ((double) n) / total;
				System.out.println(n + " " + total + " " + prob);
			}
			total++;
		}

	}

	/**
	 * estimate probability of k occurrences of non-repeating segments of length
	 * len in a cipher of length L
	 * 
	 * prob1: probability of non-repeating segment of length len appearing at any but the last possible position.
	 * prob2: probability of non-repeating segment of length len appearing at the last possible position.
	 */
	public static double prob(double prob1, double prob2, int k, int len, int L) {
		int slots = L-len+1;
		double c1 = binomCoeff(slots-1, k);
		double c2 = binomCoeff(slots-1, k-1);
		double prob = c1 * Math.pow(prob1, k) * Math.pow(1-prob1, slots-1-k) + prob2 * c2 * Math.pow(prob1, k-1) * Math.pow(1-prob1, slots-k);
		return prob;
	}

	/** from https://rosettacode.org/wiki/Evaluate_binomial_coefficients#Java */
	public static double binomCoeff(double n, double k) {
		double result = 1;
		for (int i = 1; i < k + 1; i++) {
			result *= (n - i + 1) / i;
		}
		return result;
	}

	public static void test() {
		//allNonrepeats(Ciphers.cipher[0].cipher);
		// System.out.println(nonRepeatCounts(Ciphers.cipher[0].cipher));
		// anomaly(Ciphers.cipher[0].cipher, 100000);
		// shuffleTest(Ciphers.cipher[0].cipher, 1000000);
		// allNonrepeatsNormalized(Ciphers.cipher[0].cipher);
		// shuffleTest2(Ciphers.cipher[0].cipher);
		/*for (int c = 0; c < 2; c++) {
			for (int maxRepeats = 1; maxRepeats <= 4; maxRepeats++) {
				 shuffleTest3(Ciphers.cipher[c].cipher, 10000000, maxRepeats);
			}
		}*/
		 //shuffleTest3(Ciphers.cipher[0].cipher, 1000, 0);
		//testProb(Ciphers.cipher[0].cipher, 17);
		for (int i=1; i<30; i++) {
			double prob = prob(0.0192633, 0.0642, i, 17, 340);
			System.out.println(i + "	" + prob + "	" + (10000000*prob));
		}
	}

	public static void main(String[] args) {
		test();
	}
}
