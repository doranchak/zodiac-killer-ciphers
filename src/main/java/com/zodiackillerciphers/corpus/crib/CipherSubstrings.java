package com.zodiackillerciphers.corpus.crib;

import java.util.HashMap;
import java.util.Map;

/** a collection of cipher substrings, tracked by length */
public class CipherSubstrings {
	/** map substring length to substring */
	public Map<Integer, CipherSubstringMap> map;
	
	/** make all the substrings */
	public CipherSubstrings(String cipher, int minLength) {
		init();
		for (int i=0; i<cipher.length(); i++) {
			for (int j=i+minLength; j<cipher.length(); j++) {
				String substring = cipher.substring(i, j);
				CipherSubstringMap sub = map.get(substring.length());
				if (sub == null) sub = new CipherSubstringMap();
				sub.add(substring, i);
				map.put(substring.length(), sub);
			}
		}
	}
	
	public void init() {
		if (map == null) 
			map = new HashMap<Integer, CipherSubstringMap>();
	}
	
	public String toString() {
		return map.toString();
	}

	public static void dump(CipherSubstrings cs) {
		for (Integer key : cs.map.keySet()) {
			System.out.println(key + ":");
			CipherSubstringMap map2 = cs.map.get(key);
			for (Integer key2 : map2.map.keySet()) {
				double rate = map2.map.get(key2).rate();
				if (rate > 0 && rate < 0.001) System.out.println("   " + key2 + ": " + map2.map.get(key2));
			}
		}
	}
	
	public void cull() {
		for (CipherSubstringMap csm : map.values() ) {
			csm.cull();
		}
	}
	
	public void resetCounts() {
		for (CipherSubstringMap csm : map.values() ) {
			csm.resetCounts();
		}
	}
}
