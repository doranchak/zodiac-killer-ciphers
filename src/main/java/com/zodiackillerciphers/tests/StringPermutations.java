package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.ngrams.NGramsBean;

public class StringPermutations {
	
	/** from http://stackoverflow.com/questions/4240080/generating-all-permutations-of-a-given-string */
	
	static int count = 0;
	static int hits1 = 0;
	static int hits2 = 0;
	static int hits3 = 0;
	public static void permutation(String str) { 
	    permutation("", str); 
	}

	private static void permutation(String prefix, String str) {
		int n = str.length();
		if (n == 0) {
			count++;
			System.out.println(prefix);
			NGramsBean bean = new NGramsBean(2, prefix);
			List<Integer> list1 = bean.positions.get("<S");
			List<Integer> list2 = bean.positions.get("S<");

			boolean b1 = list1 != null && list1.size() > 2;
			boolean b2 = list2 != null && list2.size() > 2;

			if (b1)
				hits1++;
			if (b2)
				hits2++;
			if ((list1 != null && list1.size() > 2))
				hits1++;
			if ((list2 != null && list2.size() > 2))
				hits2++;
			if (b1 && b2) {
				hits3++;
			}

		} else {
			for (int i = 0; i < n; i++)
				permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i + 1, n));
		}
	}
	
	public static void main(String[] args) {
		//permutation("<<<<<<SSSS");
		permutation("0123");
		System.out.println(hits1 + " " + hits2 + " " + hits3 + " " + count);
	}
}
