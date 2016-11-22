package com.zodiackillerciphers.old;

public class FragmentScore {
	/** the smallest probability pattern */
	double min;
	/** sum of the probabilities for the lowest probability patterns */
	double sum;
	/** mean of the probabilities for the lowest probability patterns */
	double mean;
	
	public String toString() {
		return "Scores: " + min + ", " + sum + ", " + mean;
	}
	
	public boolean equalOrBetterThan(FragmentScore score) {
		if (this.min <= score.min) return true;
		if (this.sum <= score.sum) return true;
		if (this.mean <= score.mean) return true;
		return false;
	}
}
