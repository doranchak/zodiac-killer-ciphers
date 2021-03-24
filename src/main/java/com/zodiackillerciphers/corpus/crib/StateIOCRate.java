package com.zodiackillerciphers.corpus.crib;

public class StateIOCRate {
	// how many attempts?
	public long trials;
	// how many matches?
	public long hits;
	
	// once rate is too high, mark as ignore to avoid recomputating rate
	public boolean ignore;

	public StateIOCRate() {
		trials = 0;
		hits = 0;
		ignore = false;
	}

	/** returns false if we didn't increment due to ignoring */
	public boolean increment(boolean hit) {
		if (ignore) return false;
		trials++;
		if (hit) hits++;
		return true;
	}
	
	public double rate() {
		double rate = hits;
		rate /= trials;
		return rate;
	}
	
	public boolean ignore() {
		if (ignore) return true;
		if (trials >= 100 && rate() > State.MAX_RATE) {
			ignore = true;
		}
		return ignore;
	}
}
