package com.zodiackillerciphers.tests.cribbing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class State extends Base {
	/** ciphertext we are cribbing for */
	String cipher;
	/** current ciphertext positions under consideration */
	int posCipherStart;
	int posCipherEnd; // actual end pos is posCipherEnd-1 
	/** current plaintext positions under consideration */
	int posPlainStart;
	int posPlainEnd; // actual end pos is posPlainEnd-1
	
	/** map of cipher symbols to plaintext symbols */
	Map<Character, Character> key;
	/** count of cipher symbols */
	Map<Character, Integer> counts;
	/** map cipher symbols to set of plaintext symbols (so we can support polyphones) */
	Map<Character, Set<Character>> polyphones;
	/** map cipher+plain combo to count */
	Map<String, Integer> polyphoneCounts;
	
	/** Results */
	Results results;
	
	/** plaintext (temporary) */
	public StringBuffer plaintext = new StringBuffer();

	public State() {
		reset(true);
	}
	public void reset(boolean resetCipherPos) {
		if (resetCipherPos) {
			posCipherStart = 0;
			posCipherEnd = 1;
		}
		posPlainStart = 0;
		posPlainEnd = 1;
		key = new HashMap<Character, Character>();
		/** count of cipher symbols */
		counts = new HashMap<Character, Integer>();
		
		if (results == null) results = new Results();
		
		polyphones = new HashMap<Character, Set<Character>>();
		polyphoneCounts = new HashMap<String, Integer>();
		
	}
	/** compute multiplicity.  can do it directly from key map. */
	public float multiplicity() {
		return ((float)key.size())/(posCipherEnd-posCipherStart);
	}

	/**
	 * @return the cipher
	 */
	public String getCipher() {
		return cipher;
	}

	/**
	 * @param cipher the cipher to set
	 */
	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	/**
	 * @return the posCipherStart
	 */
	public int getPosCipherStart() {
		return posCipherStart;
	}

	/**
	 * @param posCipherStart the posCipherStart to set
	 */
	public void setPosCipherStart(int posCipherStart) {
		this.posCipherStart = posCipherStart;
	}

	/**
	 * @return the posCipherEnd
	 */
	public int getPosCipherEnd() {
		return posCipherEnd;
	}

	/**
	 * @param posCipherEnd the posCipherEnd to set
	 */
	public void setPosCipherEnd(int posCipherEnd) {
		this.posCipherEnd = posCipherEnd;
	}

	/**
	 * @return the posPlainStart
	 */
	public int getPosPlainStart() {
		return posPlainStart;
	}

	/**
	 * @param posPlainStart the posPlainStart to set
	 */
	public void setPosPlainStart(int posPlainStart) {
		this.posPlainStart = posPlainStart;
	}

	/**
	 * @return the posPlainEnd
	 */
	public int getPosPlainEnd() {
		return posPlainEnd;
	}

	/**
	 * @param posPlainEnd the posPlainEnd to set
	 */
	public void setPosPlainEnd(int posPlainEnd) {
		this.posPlainEnd = posPlainEnd;
	}

	/**
	 * @return the key
	 */
	public Map<Character, Character> getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Map<Character, Character> key) {
		this.key = key;
	}

	/**
	 * @return the counts
	 */
	public Map<Character, Integer> getCounts() {
		return counts;
	}

	/**
	 * @param counts the counts to set
	 */
	public void setCounts(Map<Character, Integer> counts) {
		this.counts = counts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "State [posCipherStart=" + posCipherStart + ", posCipherEnd="
				+ posCipherEnd + ", posPlainStart=" + posPlainStart
				+ ", posPlainEnd=" + posPlainEnd + ", key=" + key + ", counts="
				+ counts + "]";
	}
	/**
	 * @return the results
	 */
	public Results getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(Results results) {
		this.results = results;
	}
	/**
	 * @return the polyphones
	 */
	public Map<Character, Set<Character>> getPolyphones() {
		return polyphones;
	}
	/**
	 * @param polyphones the polyphones to set
	 */
	public void setPolyphones(Map<Character, Set<Character>> polyphones) {
		this.polyphones = polyphones;
	}
	/**
	 * @return the polyphoneCounts
	 */
	public Map<String, Integer> getPolyphoneCounts() {
		return polyphoneCounts;
	}
	/**
	 * @param polyphoneCounts the polyphoneCounts to set
	 */
	public void setPolyphoneCounts(Map<String, Integer> polyphoneCounts) {
		this.polyphoneCounts = polyphoneCounts;
	}
	
}
