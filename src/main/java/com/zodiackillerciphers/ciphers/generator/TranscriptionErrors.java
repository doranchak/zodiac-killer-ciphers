package com.zodiackillerciphers.ciphers.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.Utils;

public class TranscriptionErrors {
	/** each string represents a list of symbols that are potentially ambiguous with each other */
	public static String[] ambiguousList = new String[] {
		"#%*@_",
		"()z",
		"123456O",
		"789^A",
		":;",
		"<>^V",
		"Bb",
		"Cc",
		"Dd",
		"EFf",
		"HN",
		"Jj",
		"Kk",
		"Ll",
		"P&p",
		"Tt",
		"Yy",
		"/|-"
	};
	/** index each set by its members */
	public static Map<Character, Set<String>> ambiguousMap;
	static {
		initMap();
	}
	
	/** max errors we are allowing to appear in ciphers of length 340 */
	public static int MAX_ERRORS = 20;
	
	/** track transcription errors */
	public Map<Character, AmbiguousMappingBean> map;
	
	public static void initMap() {
		ambiguousMap = new HashMap<Character, Set<String>>();
		for (String s : ambiguousList) {
			for (int i=0; i<s.length(); i++) {
				char key = s.charAt(i);
				Set<String> val = ambiguousMap.get(key);
				if (val == null) val = new HashSet<String>();
				val.add(s);
				ambiguousMap.put(key, val);
			}
		}
	}
	
	/** count the number of permitted transcription errors.
	 * if an error is found that does not result from ambigous encoding,
	 * return a very large value to indicate that it is a disallowed condition. 
	 */
	public int errors(Map<Character, Set<Character>> c2p,
			Map<Character, Set<Character>> p2c,
			Map<Character, Map<Character, Integer>> counts, CandidateKey ck) {
		int errors = 0;
		map = new HashMap<Character, AmbiguousMappingBean>();
		Set<Character> cipherSymbolsWithErrors = new HashSet<Character>();
		for (Character key : c2p.keySet()) {
			if (key == ' ') continue; // ignore spaces
 			Set<Character> val = new HashSet<Character>(c2p.get(key));
			val.remove('_'); // remove filler
			if (val.size() > 1) {
				Utils.debug("error check: symbol " + key + " maps to " + val);
				// this cipher symbol maps to more than one plaintext letter.
				// we accept this only if all of the plaintext letters have cipher mappings
				// drawn from the same set of ambiguous cipher symbols				
				
				Set<String> set = ambiguousMap.get(key);
				if (set == null || set.isEmpty()) {
					Utils.debug("there is no ambiguous map for symbol " + key);
					return Integer.MAX_VALUE;
				}

				boolean found = false;
				for (String s : set) {
					AmbiguousMappingBean bean = hasSameAmbiguousMapping(key,
							val, s, p2c);
					if (bean != null) {
						map.put(key, bean);
						cipherSymbolsWithErrors.add(key);
						found = true;
						break;
					}
				}
				if (!found) return Integer.MAX_VALUE;  // failed to find a common set of ambiguous symbols
			}
		}
		
		// now we have a list of symbols with acceptable errors.
		// look at the counts for each to determine the total number of errors.
		// also, enforce the following rules, derived from observations of the 408:
		// - only 1/6 of the polyphonic cipher symbols are allowed to have more than 1 error.
		
		int totalWithMoreThanOneError = 0;
		int maxAllowed = cipherSymbolsWithErrors.size()/6;
		for (Character cipher : cipherSymbolsWithErrors) {
			Map<Character, Integer> map = counts.get(cipher);
			if (map == null) throw new RuntimeException("Why is map null for " + cipher + "??? " + System.identityHashCode(counts) + " " + System.identityHashCode(ck));
			int total = 0;  int max = Integer.MIN_VALUE;
			for (Integer i : map.values()) {
				if (i > max) max = i;
				total += i;
			}
			total -= max; // remove the max count, since we wish that to represent the "correct" assignment.
			if (total > 1) totalWithMoreThanOneError++;
			if (totalWithMoreThanOneError > maxAllowed) return Integer.MAX_VALUE;
			errors += total;
		}
		
		return errors;
	}
	
	/** returns true if all the given plaintext letters maps to at least one cipher symbol contained
	 * in the given ambiguous symbol set.    
	 */
	public AmbiguousMappingBean hasSameAmbiguousMapping(
			char cipherSymbolWithMultipleAssignments, Set<Character> plaintext,
			String ambiguousSymbolSet, Map<Character, Set<Character>> p2c) {
		if (plaintext == null || ambiguousSymbolSet == null || p2c == null)
			return null;

		AmbiguousMappingBean bean = new AmbiguousMappingBean();
		bean.ambiguousSymbols = ambiguousSymbolSet;
		bean.cipher = cipherSymbolWithMultipleAssignments;
		bean.plain.addAll(plaintext);

		for (Character plain : plaintext) {
			if (plain == '_') continue;
			Set<Character> cipher = p2c.get(plain);
			if (cipher == null)
				throw new RuntimeException(
						"Why is there no cipher mapping for " + plain + " in "
								+ p2c + "?");
			boolean found = false;
			for (Character c : cipher) {
				Set<String> sets = ambiguousMap.get(c);
				if (sets == null || sets.isEmpty())
					continue;
				if (sets.contains(ambiguousSymbolSet)) {
					found = true;
					bean.map(plain, c);
					// break;
				}
			}
			if (!found)
				return null;
		}

		return bean.criteriaMet() ? bean : null;

	}	
	public String toString(Map<Character, Map<Character, Integer>> counts) {
		if (map == null) return "No transcription errors";
		String result = "";
		for (Character key : map.keySet()) {
			AmbiguousMappingBean val = map.get(key);
			result += val.toString(counts) + " "; 
		}
		return result;		
	}
}
