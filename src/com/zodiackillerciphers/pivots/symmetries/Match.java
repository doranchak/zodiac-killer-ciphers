package com.zodiackillerciphers.pivots.symmetries;

import java.util.Arrays;

/** represents a symbol that matches in the same spot along each of two legs (axes) */
public class Match {
	/** the matching symbol */
	public char symbol;
	/** the offset from the origin */
	public int offset;
	/** the two positions involved, in the same order as the Pair's direction array */
	public int[][] positions;
	
	public String toString() {
		return symbol + " " + offset + " " + p();
	}
	public String p() {
		String p = "";
		for (int[] pos : positions) p += Arrays.toString(pos) + " ";
		return p;
	}
	
	/** return the distance between two positions.  they are assumed to be along the same axis. */
	public static int distance(Match m1, Match m2) {
		
		int distance1 = 0;
		int distance2 = 0;
		
		int[] position1;
		int[] position2;
		position1 = m1.positions[0];
		position2 = m2.positions[0];
		
		if (position1[0] != position2[0]) distance1 = Math.abs(position1[0]-position2[0]);
		else if (position1[1] != position2[1]) distance1 = Math.abs(position1[1]-position2[1]);
		
		position1 = m1.positions[1];
		position2 = m2.positions[1];
		
		if (position1[0] != position2[0]) distance2 = Math.abs(position1[0]-position2[0]);
		else if (position1[1] != position2[1]) distance2 = Math.abs(position1[1]-position2[1]);
		
		if (distance1 == 0) return distance2;
		if (distance2 == 0) return distance1;
		return Math.min(distance1, distance2);
	}
}
