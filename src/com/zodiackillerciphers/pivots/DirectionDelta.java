package com.zodiackillerciphers.pivots;

import com.zodiackillerciphers.Utils;

/** represents movments in a grid based on Direction */
public class DirectionDelta {
	public DirectionDelta(int drow, int dcol) {
		super();
		this.drow = drow;
		this.dcol = dcol;
	}
	public int drow;
	public int dcol;
	
	/** return new position when this direction delta is applied to it */
	public int fromPos(int pos) {
		int row = Utils.rowFrom(pos) + drow;
		int col = Utils.colFrom(pos) + dcol;
		return Utils.posFrom(row, col);
	}
	
	public String toString() {
		return drow + " " + dcol;
	}
}
