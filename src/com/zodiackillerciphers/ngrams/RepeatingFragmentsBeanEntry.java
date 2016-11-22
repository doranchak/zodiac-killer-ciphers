package com.zodiackillerciphers.ngrams;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RepeatingFragmentsBeanEntry implements Comparable {
	public String key;
	public int count;
	public double probability;
	public String positions;
	public Set<Integer> positionsInt;
	public RepeatingFragmentsBeanEntry() {
		positionsInt = new HashSet<Integer>(); 
	}
	@Override
	public int compareTo(Object o) {
		RepeatingFragmentsBeanEntry r1 = this;
		RepeatingFragmentsBeanEntry r2 = (RepeatingFragmentsBeanEntry) o;
		return Double.compare(r1.probability, r2.probability);
	}
	
	/** returns true if this bean is a substring of the given bean */
	public boolean isSubstringOf(RepeatingFragmentsBeanEntry bean) {
		if (key.length() > bean.key.length()) return false;
		if (!bean.key.contains(key)) return false;
		if (count > bean.count) return false; // might be substring with higher count
		for (Integer pos1 : positionsInt) {
			int p1 = pos1;
			int p2 = pos1 + key.length() - 1;
			for (Integer pos2 : bean.positionsInt) {
				int p3 = pos2;
				int p4 = pos2 + bean.key.length() - 1;
				
				if (p1 >= p3 && p1 <= p4 && p2 >= p3 && p2 <= p4) return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return toString(false); 
	}
	public String toString(boolean quoteSymbols) {
		String k = key;
		if (quoteSymbols) k = "=(\"" + k + "\")";
		return key.length() + "	" + probability + "	" + count + "	" + k + "	" + positions + "	" + numNonWildcards(); 
	}
	
	public String toJS(Map<Integer, Integer> map, int W) {
		String darken = "";
		String lighten = "";
		String high = "";
		String unhigh = "";
		for (Integer pos : positionsInt) {
			for (int i=0; i<key.length(); i++) {
				int pos2 = pos+i;
				if (map != null) {
					pos2 = map.get(pos+i);
				}
				int r = pos2/W;
				int c = pos2%W;
				char ch = key.charAt(i);
				if (ch == '?') {
					high += "highrc(" + r + "," + c + "); ";
					unhigh += "unhighrc(" + r + "," + c + "); ";
				} else {
					darken += "darkenrc(" + r + "," + c + "); ";
					lighten += "lightenrc(" + r + "," + c + "); ";
				}
			}
		}
		return count + " <button onmouseover=\"" + darken + " " + high + "\" onmouseout=\"" + lighten + " " + unhigh + " \">" + key.replaceAll("<", "&lt;") + "</button>";
	}
	
	public int numWildcards() {
		int result = 0;
		for (int i=0; i<key.length(); i++) if (key.charAt(i) == '?') result++;
		return result;
	}
	public int numNonWildcards() {
		return key.length() - numWildcards();
	}
}
