package com.zodiackillerciphers.pivots;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Positions {
	public Map<Character, Set<Integer>> added;
	public Map<Character, Set<Integer>> removed;
	
	public Positions() {
		added = new HashMap<Character, Set<Integer>>();
		removed = new HashMap<Character, Set<Integer>>();
	}
	
	public void add(Character c, int pos) {
		Set<Integer> val = added.get(c);
		if (val == null) val = new HashSet<Integer>();
		val.add(pos);
		added.put(c, val);
	}
	public void remove(Character c, int pos) {
		Set<Integer> val = removed.get(c);
		if (val == null) val = new HashSet<Integer>();
		val.add(pos);
		removed.put(c, val);
	}
	
	public void dump() {
		System.out.println("posmap added: " + added);
		System.out.println("posmap removed: " + removed);
	}
}
