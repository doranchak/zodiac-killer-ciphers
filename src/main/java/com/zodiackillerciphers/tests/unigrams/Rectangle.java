package com.zodiackillerciphers.tests.unigrams;

import java.util.ArrayList;
import java.util.List;

public class Rectangle {
	/** row and column of upper left corner */
	public int row;

	public int col;

	/** width and height of rectangle */
	public int height;
	public int width;

	public Rectangle(int row, int col, int height, int width) {
		super();
		this.row = row;
		this.col = col;
		this.height = height;
		this.width = width;
	}
	/** produce all four corners */
	public int[][] corners() {
		return new int[][] { { row, col }, { row, col + width - 1 },
				{ row + height - 1, col + width - 1 },
				{ row + height - 1, col } };
	}
	
	/** row range covered by this rectangle */
	public int[] rows() {
		return new int[] {row, row+height-1};
	}
	/** col range covered by this rectangle */
	public int[] cols() {
		return new int[] {col, col+width-1};
	}

	/** returns true if this rectangle overlaps the given rectangle */
	public boolean overlap(Rectangle r2) {
		Rectangle r1 = this;
		int[] rows = r1.rows();
		int[] cols = r1.cols();

		// each corner of r2 must not fall within row/col ranges of r1 
		for (int[] corner : r2.corners()) {
			int row = corner[0];
			int col = corner[1];
			if (row >= rows[0] && row <= rows[1] && col >= cols[0]
					&& col <= cols[1])
				return true;
		}
		return false;
	}
	
	/** extract rectangle of symbols from given cipher */
	public List<String> extractAsList(String cipher, int cipherWidth) {
		List<String> list = new ArrayList<String>();
		for (int r=row; r<=row+height-1; r++) {
			String line = "";
			for (int c=col; c<=col+width-1; c++) {
				int pos = r*cipherWidth+c;
				line += cipher.charAt(pos);
			}
			list.add(line);
		}
		return list;
	}
	public String extract(String cipher, int cipherWidth) {
		String result = "";
		for (int r=row; r<=row+height-1; r++) {
			for (int c=col; c<=col+width-1; c++) {
				int pos = r*cipherWidth+c;
				result += cipher.charAt(pos);
			}
		}
		return result;
	}
	
	public String render() {
		return "rgbRectangle(" + row + "," + col + "," + (row + height - 1)
				+ "," + (col + width - 1) + ");";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rectangle [row=" + row + ", col=" + col + ", height=" + height
				+ ", width=" + width + "]";
	}
}
