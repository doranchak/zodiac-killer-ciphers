package com.zodiackillerciphers.corpus.crib;

import com.zodiackillerciphers.lucene.Stats;

/** a cipher substring.  we track its position and ioc.
 *  whenever we decide to ignore ioc for this particular length,
 *  we need to remove this substring from consideration.
 **/
public class CipherSubstring {
	// its position in original cipher
	public int position;
	// its ioc 
	public double ioc;
	// the substring
	public String substring;
	
	// number of trials
	public long trials;
	
	// number of hits
	public long hits;
	
	public CipherSubstring(String substring, int position) {
		this.substring = substring;
		this.ioc = Stats.ioc(substring);
		this.position = position;
	}
	
	public int length() {
		return substring.length();
	}
	
	public String toString() {
		return "sub " + substring + " pos " + position + " ioc " + ioc + " hits " + hits + " trials " + trials + " rate " + rate();
	}
	
	public void increment(boolean hit) {
		trials++;
		if (hit) hits++;
	}
	
	public double rate() {
		double result = hits;
		return result / trials;
	}
	public void resetCounts() {
		hits = 0;
		trials = 0;
	}
}
