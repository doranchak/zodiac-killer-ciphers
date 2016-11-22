package com.zodiackillerciphers.ciphers.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AmbiguousMappingBean {
	/** this cipher symbol */
	public char cipher;
	/** maps to these plaintext letters. */
	public Set<Character> plain;
	/**
	 * all the plaintext letters have cipher symbol assignments that fall in
	 * this set of ambiguous symbols
	 */
	public String ambiguousSymbols;
	/** the cipher symbols that are mapped to the above plaintext and fall in the set of ambiguous symbols. */
	public Map<Character, Set<Character>> ambP2c;
	
	/** count of occurrences per cipher-plain assignments */
	public Map<Character, Map<Character, Integer>> counts;
	
	public AmbiguousMappingBean() {
		plain = new HashSet<Character>();
		ambP2c = new HashMap<Character, Set<Character>>();
		counts = new HashMap<Character, Map<Character, Integer>>();
	}
	
	public void map(char plain, char cipher) {
		Set<Character> val = ambP2c.get(plain);
		if (val == null) val = new HashSet<Character>();
		val.add(cipher);
		ambP2c.put(plain, val);
	}
	
	public String toString(Map<Character, Map<Character, Integer>> counts) {
		String result = "[" + cipher + "] mapped to [" + plain + "]. Plain mappings: [";
		String error = "";
		for (Character key : ambP2c.keySet()) {
			Set<Character> val = ambP2c.get(key);
			result += key + ":";
			for (Character c : val) {
				
				if (counts == null) {
					error = "[Null counts!]";
				} else if (counts.get(c) == null) {
					error = "[map is null for [" + c + "] from [" + val + "] for key [" + key + "]]";
				} else result += c + "(" + counts.get(c).get(key) + ")";
				if (error.length() > 0) {
					result += error;
					System.err.println("ERROR: " + error);
				}
			}
			result += " ";
		}
		result += "]. Drawn from set [" + ambiguousSymbols + "].";
		return result;
	}
	
	/** criterion is met when at least one other cipher symbol, different than the given one,
	 * is assigned to some plain text letter that was erroneously assigned to the given cipher symbol. 
	 * @return
	 */
	public boolean criteriaMet() {
		if (ambP2c == null) return false;
		for (Set<Character> set : ambP2c.values())
			for (Character ch : set) if (ch != cipher) return true;
		
		return false;
	}

}
