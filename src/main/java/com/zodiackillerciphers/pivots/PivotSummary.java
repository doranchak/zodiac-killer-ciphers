package com.zodiackillerciphers.pivots;

import java.util.List;

public class PivotSummary {
	/** list of pivots */
	List<Pivot> pivots;
	/** cipher converted to grid array, with pivot positions annotated */
	String[][][] grid;
	
	public static int WIDTH = 17;
	public List<Pivot> getPivots() {
		return pivots;
	}
	public void setPivots(List<Pivot> pivots) {
		this.pivots = pivots;
	}
	public String[][][] getGrid() {
		return grid;
	}
	public void setGrid(String[][][] grid) {
		this.grid = grid;
	}
	/** generate grid from the given cipher.  annotate all positions that are involved with pivots. */
	public void grid(String cipher) {
		int HEIGHT = cipher.length()/WIDTH;
		grid = new String[HEIGHT][WIDTH][2]; // the element at array entry [j,i] is: {cipher unit, "pivot" if involved in a pivot or blank otherwise}
		for (int row=0; row<HEIGHT; row++) {
			grid[row] = new String[WIDTH][2];
			for (int col=0; col<WIDTH; col++) {
				grid[row][col] = new String[] {
						"" + cipher.charAt(row*WIDTH + col),
						isPivot(row, col) ? "pivot" : ""
				};
			}
		}
	}
	public boolean isPivot(int row, int col) {
		if (pivots == null) return false;
		int pos = row*WIDTH + col;
		for (Pivot p : pivots) {
			if (p.positions.contains(pos)) return true;
		}
		return false;
	}
}
