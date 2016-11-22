package com.zodiackillerciphers.pivots.symmetries;

import java.util.Arrays;

/** a symbol's position along an axis */
public class Position implements Comparable {
	// the row and column
	public int[] point;
	// the direction (i.e., index of direction array)
	public int direction;
	
	public String toString() {
		return "direction " + direction + " point " + Arrays.toString(point);
	}

	@Override
	public int compareTo(Object o) {
		Position p1 = this;
		Position p2 = (Position) o;
		return Integer.compare(p1.direction, p2.direction);
	}
}
