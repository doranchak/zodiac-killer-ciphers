package com.zodiackillerciphers.old;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.zodiackillerciphers.math.NestedSums;

public class FragmentInfo implements Comparable {
	
	public static boolean forWiki = false;
	/** category (a way to tag the results) */
	public String category;
	/** the fragment template, possibly containing wildcards for any positions that don't match */
	public String fragment;
	/** list of fragments that match the template */
	public List<String> fragments;
	
	/** list of distinct positions occupied by the fragments */
	public Set<Integer> positions;
	
	/** static reference to info we need to compute probabilities. */
	public static Map<Character, Integer> countsMap;
	public static int L;
	
	public FragmentInfo() {
		this.fragments = new ArrayList<String>();
		this.positions = new HashSet<Integer>();
	}
	
	/** estimated probability of the appearances of this many repetitions of this fragment template.
	 * use the passed map of symbol frequencies, and passed cipher length L, to determine probability. */
	public double probability() {
		//if (probability != null) return probability;
		Map<Character, Integer> counts = new HashMap<Character, Integer>(); // running count of matching symbols
		double prob = 1;
		if (positions == null || positions.size() == 0) return prob;
		
		StringBuffer symbols = new StringBuffer();
		for (int i=0; i<fragment.length(); i++) if (fragment.charAt(i) != '?') symbols.append(fragment.charAt(i));
		
		int total = 0;
		for (int i=0; i<positions.size(); i++) {
			for (int j=0; j<symbols.length(); j++) {
				char ch = symbols.charAt(j);
				Integer val = counts.get(ch);
				if (val == null) val = 0;
				
				prob *= ((double)(countsMap.get(ch) - (val++))) / (L - (total++));
				counts.put(ch, val);
			}
		}
		
		double exact = prob * NestedSums.ways(fragment.length(), positions.size(), L).doubleValue();
		if (!fragment.contains("?")) return exact;
		// now we must account for all possible rearrangements for wildcard positions.
		int wildcards = StringUtils.countMatches(fragment, "?");
		int positions = fragment.length()-2;
		int syms = positions-wildcards;
		long factorial = 1;
		for (int i=0; i<syms; i++) factorial *= (positions-i);
		return exact * factorial;
		
	}
	
	/** the "order" of this fragment is the number of non-wildcard positions in the template */
	public int order() {
		int t = 0;
		for (int i=0; i<fragment.length(); i++) if (fragment.charAt(i) != '?') t++;
		return t;
	}
	
	/** the "wildcard length" of this fragment is the count of all wildcard positions in the template */
	public int wildcardLength() {
		int t = 0;
		for (int i=0; i<fragment.length(); i++) if (fragment.charAt(i) == '?') t++;
		return t;
	}
	
	/** the number of repetitions */
	public int repeats() {
		return fragments.size();
	}

	/** a combined score of the significance of this set of repetitions.  */
	public double score() {
		return 1/(probability() * (1+wildcardLength()));
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (forWiki) {
			
			sb.append("|-valign=\"top\"");
			sb.append(System.getProperty("line.separator"));
			sb.append("| style=\"white-space: nowrap; text-align: right; border-style: solid; border-width: 1px\"| ");
			sb.append("<tt>" + fragment + "</tt>");
			sb.append(System.getProperty("line.separator"));

			sb.append("| style=\"text-align: right; border-style: solid; border-width: 1px\"| ");
			for (Integer i : positions) sb.append(i).append(" ");
			sb.append(System.getProperty("line.separator"));
			
			sb.append("| style=\"text-align: right; border-style: solid; border-width: 1px\"| <tt>");
			for (String s : fragments) sb.append(WikiUtils.scrub(s)).append(" ");
			sb.append("</tt>" + System.getProperty("line.separator"));
			
			sb.append("| style=\"text-align: right; border-style: solid; border-width: 1px\"| ");
			sb.append(order());
			sb.append(System.getProperty("line.separator"));
			
			sb.append("| style=\"text-align: right; border-style: solid; border-width: 1px\"| ");
			sb.append(repeats());
			sb.append(System.getProperty("line.separator"));
			
			/*sb.append("| style=\"text-align: right; border-style: solid; border-width: 1px\"| ");
			sb.append(wildcardLength());
			sb.append(System.getProperty("line.separator"));*/
			
			sb.append("| style=\"text-align: right; border-style: solid; border-width: 1px\"| ");
			sb.append(new DecimalFormat("#.##E0").format(probability()));
			
			
		} else {
			sb.append(category);
			sb.append("	");			
			sb.append(fragment);
			sb.append("	");
			sb.append(fragment.length());
			sb.append("	");
			for (Integer i : positions) sb.append(i).append(" ");
			sb.append("	");
			for (String s : fragments) sb.append(s).append(" ");
			sb.append("	");
			sb.append(order()).append("	");
			sb.append(repeats()).append("	");
			//sb.append(wildcardLength()).append("	");
			sb.append(new DecimalFormat("#.##E0").format(probability())).append("	");
			//sb.append(score()).append("	");
		}
		return sb.toString();
	
	}

	@Override
	public int compareTo(Object o) {
		FragmentInfo a = this;
		FragmentInfo b = (FragmentInfo) o;
		double pa = a.probability();
		double pb = b.probability();
		if (pa < pb) return -1;
		if (pa > pb) return 1;
		return 0;
	}
}
