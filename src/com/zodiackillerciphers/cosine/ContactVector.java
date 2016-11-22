package com.zodiackillerciphers.cosine;

import java.util.HashMap;
import java.util.Map;

/** used by CosineSimilarity */
public class ContactVector {
	public Map<Character, Map<Character, Integer>> mapBefore = new HashMap<Character, Map<Character, Integer>>(); 
	public Map<Character, Map<Character, Integer>> mapAfter = new HashMap<Character, Map<Character, Integer>>();

	public ContactVector() {
		super();
		this.mapBefore = new HashMap<Character, Map<Character, Integer>>();
		this.mapAfter = new HashMap<Character, Map<Character, Integer>>();
	}
	
	public void incBefore(Character ch, Character chBefore) {
		if (ch == null || chBefore == null) return;
		Map<Character, Integer> val = mapBefore.get(ch);
		if (val == null) val = new HashMap<Character, Integer>();
		
		Integer count = val.get(chBefore);
		if (count == null) count = 0;
		count++;
		
		val.put(chBefore, count);
		mapBefore.put(ch, val);
	}
	
	public void incAfter(Character ch, Character chAfter) {
		if (ch == null || chAfter == null) return;
		Map<Character, Integer> val = mapAfter.get(ch);
		if (val == null) val = new HashMap<Character, Integer>();
		
		Integer count = val.get(chAfter);
		if (count == null) count = 0;
		count++;
		
		val.put(chAfter, count);
		mapAfter.put(ch, val);
	}
	
	public int before(char ch1, char ch2) {
		Map<Character, Integer> map = mapBefore.get(ch1);
		if (map == null) return 0;
		Integer val = map.get(ch2);
		if (val == null) return 0;
		return val;
	}
	
	public int after(char ch1, char ch2) {
		Map<Character, Integer> map = mapAfter.get(ch1);
		if (map == null) return 0;
		Integer val = map.get(ch2);
		if (val == null) return 0;
		return val;
	}
	
}
