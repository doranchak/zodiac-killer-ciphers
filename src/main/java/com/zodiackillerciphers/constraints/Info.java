package com.zodiackillerciphers.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.tests.LetterFrequencies;

public class Info implements Comparable {
	/** the cipher substring */
	public String substring;
	/** the putative plaintext */
	public String plaintext;
	/** where it appears in the original cipher */
	public int index;
	/** map used to track symbol frequency distribution */
	public Map<Character, Integer> counts; 
	/** list of pairs of repeating symbols, represented by indices of the substring */
	public List<int[]> pairs;
	/** estimated probability of constraint being met by random english plaintext */
	public float probability;
	/** how close this result matches the real solution, if given */
	public float match;
	/** zkscore resulting from using this putative solution to partially decode the entire cipher */
	public float zkscore;

	
	public Info(String substring, int index) {
		this(substring, index, false);
	}
	public Info(String substring, int index, boolean compute) {
		this.substring = substring;
		this.index = index;
		if (compute) compute();
	}
	
	public void compute() {
		computeCounts();
		computePairs();
		computeProbability();
	}
	public void computeCounts() {
		counts = Ciphers.countMap(substring);
	}
	
	public void computeProbability() {
		probability = 1f;
		for (Character key : counts.keySet()) {
			Integer val = counts.get(key);
			if (val < 2) continue;
			probability *= LetterFrequencies.computeIOC(val);
		}
	}
	
	public void computePairs() {
		pairs = new ArrayList<int[]>();
		Map<Character, Integer> firsts = new HashMap<Character, Integer>();
		for (int i=0; i<substring.length(); i++) {
			char key = substring.charAt(i);
			Integer val = firsts.get(key);
			if (val == null) {
				val = i;
			} else {
				pairs.add(new int[] {val, i});
			}
			firsts.put(key, val);
		}
	}
	
	public String toString() {
		return substring.length() + ", " + index + ", " + substring + ", " + plaintext + ", " + Ciphers.countMap(plaintext).size() + ", " + probability + ", " + pairs() + ", " + match + ", " + zkscore + ", " + (zkscore/substring.length());
	}
	
	public String pairs() {
		String s = "";
		if (pairs == null) return s;
		for (int[] i : pairs) {
			s += "{" + i[0] + " " + i[1] + "} ";
		}
		return s;
	}
	
	/** return true if the given text does not violate the constraints */
	public boolean fit() {
		
		if (plaintext == null) return false;
		if (plaintext.length() != substring.length()) return false;
		if (pairs == null) return true;
		
		for (int[] pair : pairs) {
			if (plaintext.charAt(pair[0]) != plaintext.charAt(pair[1])) return false;
		}
		return true;
	}
	
	/** the ordering we want:
	 *   lower probability to high probability
	 *   if probability is the same, then high count of uniques in plaintext to low count of uniques in plaintext 
	 */
	public int compareTo(Object o) {
		Info a = this;
		Info b = (Info) o;
		
		if (a.probability < b.probability) return -1;
		if (a.probability > b.probability) return 1;
		
		Integer c1 = Ciphers.countMap(a.plaintext).size();
		Integer c2 = Ciphers.countMap(b.plaintext).size();
		return c2.compareTo(c1);
		
	}
	
	/** discard putative plaintext that is spurious due to too many repeated letters */
	public boolean enoughUniques() {
		return enoughUniques(plaintext);
		
	}
	
	public static boolean enoughUniques(String text) {
		if (text.length() > 15 && Ciphers.countMap(text).keySet().size() < 7) return false;
		if (text.length() < 6) return true;
		
		int threshold = 5; // no more than this many repetitions of a letter in a row are allowed
		
		int reps = 1;
		for (int i=1; i<text.length(); i++) {
			char ch1 = text.charAt(i);
			char ch2 = text.charAt(i-1);
			if (ch1==ch2) {
				reps++;
				if (reps == threshold) return false;
			} else reps = 1;
		}
		
		return true;
		
	}

	public static void test() {
		Info i = new Info("AABBCCC", 0);
		i.plaintext="";
		System.out.println(i);
		Info i2 = new Info("AABBCCC", 0);
		i2.plaintext="";
		Info i3 = new Info("AABBCCC", 1);
		i3.plaintext="";
		Info i4 = new Info("ZABBCCC", 0);
		i4.plaintext="";
		
		System.out.println((i.equals(i2)) + ", " + (i2.equals(i3)) + ", " + (i2.equals(i4)));
		
		
	}
	public static void main(String[] args) {
		test();
		//System.out.println(35/3);
		Info info = new Info("XQF%GcZ@JTtq_8JI+rBPQW6VEXr",276);
			info.plaintext = "YOUBECAUSEYOUWILL";
		System.out.println(info.enoughUniques());
		System.out.println(info);
	}
	public boolean equals(Object obj) {
		Info i = (Info) obj;
		System.out.println("Equals called.  " + this + "; " + i);
		return i.substring.equals(substring) && i.index == index && i.plaintext.equals(plaintext);
	}

	
	
}
