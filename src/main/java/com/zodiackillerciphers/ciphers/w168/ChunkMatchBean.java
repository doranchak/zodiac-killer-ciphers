package com.zodiackillerciphers.ciphers.w168;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * represents a non-overlapping match of chunks for a particular search word or
 * phrase
 */
public class ChunkMatchBean {
	private List<Integer> positions; // same order as original chunks

	public ChunkMatchBean() {
		positions = new ArrayList<Integer>();
	}
	
	public void add(int position) {
		positions.add(position);
	}
	
	public String toString() {
		return positions.toString();
	}
	
	/** returns true if any of the chunks overlap */
	public boolean overlap(List<String> chunks) {
		if (chunks.size() < 2) return false;
		Set<Integer> seen = new HashSet<Integer>();
		for (int i=0; i<chunks.size(); i++) {
			int pos = positions.get(i);
			for (int k=0; k<chunks.get(i).length(); k++) {
				int val = pos+k;
				if (seen.contains(val)) return true;
				seen.add(val);
			}
		}
		return false;
	}
}
