package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;

public class RepeatingFragmentsFasterBean implements Comparable {
	public String ngram;
	public int cipherLength;
	public Set<Integer> positions;
	public Map<Character, Integer> symbolCounts;
	
	public int count() { return positions.size(); }
	public Float probability = null;
	public float probability() {
		if (probability != null) return probability; // cached result
		probability = 1f;
		for (int i=0; i<ngram.length(); i++) {
			char c = ngram.charAt(i);
			if (c == '?' || c == ' ') continue;
			probability *= ((float)symbolCounts.get(ngram.charAt(i)))/cipherLength;
		}
		probability = (float) Math.pow(probability, count());
		
		return probability;
	}
	
	public String toString() {
		return probability() + "	" + ngram + "	" + positions;
	}
	
	public String js() {
		String js = "new Fragment(\"" + ngram.replace("\\", "\\\\") + "\", " + probability + ", " + count() + ", [";
		
		boolean first = true;
		for (Integer pos : positions) {
			if (!first) js += ", ";
			first = false;
			js += pos;
		}
		js += "])";
		return js;
	}
	
	/** generate the button for the visualizer */ 
	public String toHTML() {
		
		String onMouseOver = "showProb(" + count() + "," + probability() + "); ";
		String onMouseOut = "clearProb(); ";
		
		for (Integer position : positions) {
			
			for (int i=0; i<ngram.length(); i++) {
				if (ngram.charAt(i) == '?' || ngram.charAt(i) == ' ') continue;
				int newpos = position + i;
				int row = newpos/17;
				int col = newpos%17;
				onMouseOver += "darkenrc(" + row + "," + col + "); ";
				onMouseOut += "lightenrc(" + row + "," + col + "); ";
				
			}
		}
		String html = "<button onmouseover=\"" + onMouseOver + "\" onmouseout=\"" + onMouseOut + "\">";
		html += ngram + "</button>";
		return html;
	}

	@Override
	public int compareTo(Object o) {
		RepeatingFragmentsFasterBean bean1 = this;
		RepeatingFragmentsFasterBean bean2 = (RepeatingFragmentsFasterBean) o;
		return Float.compare(bean1.probability(), bean2.probability());
	}
	
	/** return true if this bean dominates the given bean.
	 * definition: a repeating fragment f1 dominates another repeating fragment f2 if:
	 * 1) f2's ngram is a substring of f1's ngram, taking any wildcards into account
	 *           (example: ABC is a substring of ABCDE; A?C?E is a substring of ABCDE).
	 * 2) f1 has as many or more occurrences than f2
	 * 3) all occurrences of f2's ngram occur at the same positions f1's ngrams cover
	 * 4) f1's relative probability is less than f2's relative probability.
	 */
	
	public boolean dominates(RepeatingFragmentsFasterBean that) {
		if (that == null) return false;
		if (that.count() > this.count()) return false;

		int count = 0;
		for (int posThat : that.positions) {
			int startThat = posThat;
			int endThat = posThat+that.ngram.length()-1;
			
			boolean found = false;
			for (int posThis : this.positions) {
				int startThis = posThis;
				int endThis = posThis+this.ngram.length()-1;
				if (startThis <= startThat && endThis >= endThat) {
					found = true;
					break;
				}
			}
			if (found) count++;
		}
		if (count < that.positions.size()) return false; 
		if (!that.isSubstring(that.ngram, this.ngram)) return false;
		return this.probability() < that.probability();
	}
	
	
	/** returns true if a is a substring of b, even when taking wildcards under consideration */
	public static boolean isSubstring(String a, String b) {
		if (a == null || b == null) return false;
		if (a.length() == 0) return true;
		if (a.length() > b.length()) return false;
		
		for (int i=0; i<b.length()-a.length()+1; i++) {
			String sub1 = a;
			String sub2 = b.substring(i,i+a.length());
			
			boolean result = true;
			for (int j=0; j<sub1.length(); j++) {
				char ch1 = sub1.charAt(j);
				char ch2 = sub2.charAt(j);
				if (ch1 == '?' || ch2 == '?') {
					;
				} else if (ch1 != ch2) {
					result = false;
					break;
				}
			}
			if (result) return true;
		}
		return false;
	}
 	public void dumpPositions() {
 		for (Integer pos : positions) {
 			for (int i=0; i<ngram.length(); i++) {
 				if (ngram.charAt(i)=='?') continue;
 				System.out.println("pos " + (pos+i));
 				
 				if ((pos+i)==37) {
 					System.out.println("got a 37 for " + ngram + " " + this.hashCode());
 				}
 			}
 		}
 	}
	
	public static void main(String[] args) {
		System.out.println(isSubstring("ABC?D","AB??E"));
	}
}
