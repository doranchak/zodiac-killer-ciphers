package com.zodiackillerciphers.corpus.crib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** map positions to substrings. */
public class CipherSubstringMap {
	
	int length = 0;
	public Map<Integer, CipherSubstring> map;
	
	public CipherSubstringMap() {
		map = new HashMap<Integer, CipherSubstring>();
	}
	
	public void add(String substring, int pos) {
		if (length == 0) length = substring.length();
		CipherSubstring sub = new CipherSubstring(substring, pos);
		if (sub.ioc > 0) // ignore if none of the symbols repeat 
			map.put(pos, sub);
	}
	
	public String toString() {
		return map.toString();
	}
	
	public void cull() {
		
		Map<Integer, CipherSubstring> mapNew = new HashMap<Integer, CipherSubstring>();
		
		for (Integer key : map.keySet()) {
			CipherSubstring val = map.get(key);
			if (val.hits == 0 || val.rate() < State.MAX_RATE) {
				mapNew.put(key, val);
			} else {
				System.out.println("culled " + val);
			}
		}
		
		map = mapNew;
	}
	public void resetCounts() {
		for (CipherSubstring cs : map.values() ) {
			cs.resetCounts();
		}
	}
	
}
