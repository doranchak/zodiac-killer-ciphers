package com.zodiackillerciphers.corpus;

public class Score {
	public float[] scores;
	public String substring;
	public String solution;
	public int startPos;
	
	public String toString() {
		String line = substring+", "+(solution== null ? "" : solution)+", " + startPos+", ";
		for (int i=0; i<scores.length; i++) {
			line += scores[i];
			if (i<scores.length-1) line += ", "; 
		}
		return line;
	}
}
