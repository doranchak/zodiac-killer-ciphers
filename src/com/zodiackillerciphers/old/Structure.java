package com.zodiackillerciphers.old;

import java.util.HashSet;
import java.util.Set;

public class Structure {
	public int difference;
	public Set<Integer> positions;
	
	public Structure(int d) {
		this.difference = d;
		this.positions = new HashSet<Integer>();
	}
	
	public void add(int p) {
		positions.add(p);
	}
	
	/** return true iff this structure is a subset of the given structure */
	public boolean subsetOf(Structure s) {
		if (s == null || s.positions == null) return false;
		for (Integer i : this.positions) {
			if (!s.positions.contains(i)) return false; 
		}
		return true;
	}
	
	public String toString() {
		String result = "Difference: [" + difference + "], Positions: [";
		for (Integer i : positions) result += i + " ";
		result += "]";
		return result;
	}
	
}

