package com.zodiackillerciphers.pivots.symmetries;

import java.util.Arrays;
import java.util.List;

/** an instance of a repeating symbol, occurring some fixed distance from the given origin */
public class Result {
	/** the origin of the pivot */
	public int[] origin;
	
	/** the symbol that repeats */
	public char symbol;
	
	/** their positions */
	public List<int[]> positions;
	
	/** all pairs of directions */
	public List<int[]> directions;

	public int offset;
	
	public boolean best = false;
	
	public boolean hasDirection(String direction) {
		for (int[] d : directions)
			if (Arrays.toString(d).equals(direction)) {
				return true;
			}
		return false;
	}
	
	public String p() {
		String p = "";
		for (int[] pos : positions) p += Arrays.toString(pos) + " ";
		return p;
	}
	public String d() {
		String p = "";
		for (int[] d : directions) p += Arrays.toString(d) + " ";
		return p;
	}
	public String toString() {
		String result = "origin " + Arrays.toString(origin) + " symbol " + symbol + " positions " + p() + 
				" directions " + d();
		return result;
	}
}
