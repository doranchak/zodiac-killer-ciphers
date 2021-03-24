package com.zodiackillerciphers.pivots.symmetries;

public class Score {
	/** relative probability based on symbol counts */
	public double probability;
	/** min total distance (a measurement of how separated the symbols are) */
	public double minTotalDistance;
	/** composite score */
	public double compositeScore() {
		return probability * minTotalDistance;
	}
	
	public String toString() {
		return "probability " + probability + " minTotalDistance " + minTotalDistance + " compositeScore " + compositeScore(); 
	}
}
