package com.zodiackillerciphers.suffixtree;

import java.util.Arrays;

/**
 * from http://introcs.cs.princeton.edu/java/42sort/LRS.java. lrs(String) is a
 * good way to find longest repeating substrings but it doesn't exclude
 * overlaps.
 * */
public class LRS {

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
		int n = s.length();
		String[] suffixes = new String[n];
		for (int i = 0; i < n; i++) {
			suffixes[i] = s.substring(i, n);
		}

		// sort them
		Arrays.sort(suffixes);

		// find longest repeated substring by comparing adjacent sorted suffixes
		String lrs = "";
		for (int i = 0; i < n - 1; i++) {
			String x = lcp(suffixes[i], suffixes[i + 1]);
			if (x.length() > lrs.length())
				lrs = x;
		}
		return lrs;
	}

	// return the longest repeated string in s
	public static StringBuffer lrs(StringBuffer s) {

		// form the N suffixes
		int n = s.length();
		String[] suffixes = new String[n];
		for (int i = 0; i < n; i++) {
			suffixes[i] = s.substring(i, n);
		}

		// sort them
		Arrays.sort(suffixes);

		// find longest repeated substring by comparing adjacent sorted suffixes
		StringBuffer lrs = new StringBuffer();
		for (int i = 0; i < n - 1; i++) {
			String x = lcp(suffixes[i], suffixes[i + 1]);
			if (x.length() > lrs.length())
				lrs = new StringBuffer(x);
		}
		return lrs;
	}
	// read in text, replacing all consecutive whitespace with a single space
	// then compute longest repeated substring
	public static void main(String[] args) {
		System.out.println(lrs("ABCBCACABABCBCACAB"));
		System.out.println(lrs("1234321234321"));
		System.out.println(lrs("1234512345123454321123432112321"));
		System.out.println(lrs("PHLEGYASPHLEGYASTHOU"));
		System.out.println(lrs("ANOBJECTANOBJECTINMY"));

	}
}
