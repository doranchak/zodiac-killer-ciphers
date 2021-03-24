package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** a candidate key's constraints are known forever once the key is initialized. thus we can cache
 * the hard-coded symbol assignments. 
 */
public class CandidateKeyCache {
	
	/**
	 * map candidate key, by index, to its list of hard-coded assignments.
	 * assignment key is position. val is the assigned cipher symbol.
	 */
	public static Map<Integer, Map<Integer, Character>> map;
	static {
		map = new HashMap<Integer, Map<Integer, Character>>();
	}
	
	/** add given constraints to cache. */
	public synchronized static void addToCache(int index, CandidateConstraints cc, String plaintext) {
		Map<Integer, Character> mappings = new HashMap<Integer, Character>();
		List<Integer> positions = cc.positions();
		for (Integer pos : positions) {
			mappings.put(pos, plaintext.charAt(pos));
		}
		map.put(index, mappings);
	}
	
	public synchronized static boolean isCached(int index) {
		return map.containsKey(index);
	}
	
	public synchronized static boolean satisified(int index, String cipher) {
		if (!isCached(index))
			return false;
		for (Integer pos : map.get(index).keySet()) {
			char ch = map.get(index).get(pos);
			if (ch != cipher.charAt(pos)) {
				return false;
			}
		}
		return true;
	}
	
}
