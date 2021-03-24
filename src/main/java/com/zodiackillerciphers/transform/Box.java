package com.zodiackillerciphers.transform;


public class Box {
	public Point corner1; // upper left corner
	public Point corner2; // lower right corner
	public Box(Point corner1, Point corner2) {
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	public Point[] corners() {
		return new Point[] { corner1, new Point(corner1.row, corner2.col),
				corner2, new Point(corner2.row, corner1.col) };
	}

	/** returns true if the given point is inside this box */
	public boolean isPointInside(Point point) {
		return point.row >= corner1.row && point.row <= corner2.row
				&& point.col >= corner1.col && point.col <= corner2.col;
	}
	
}