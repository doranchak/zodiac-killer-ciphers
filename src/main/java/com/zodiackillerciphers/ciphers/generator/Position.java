package com.zodiackillerciphers.ciphers.generator;

public class Position {
	public int row;
	public int col;
	
	public String toString() {
		return "(" + row + ", " + col + ")";
	}
	
	/** generate the raw index of this position based on the grid dimensions */
	public int index(int W) {
		return W*row + col;
	}
	/** default width is 17 */
	public int index() {
		return index(17);
	}
	
	
}
