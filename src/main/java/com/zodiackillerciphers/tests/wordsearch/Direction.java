package com.zodiackillerciphers.tests.wordsearch;

public enum Direction {
	NORTH,
	NORTHEAST,
	EAST,
	SOUTHEAST,
	SOUTH,
	SOUTHWEST,
	WEST,
	NORTHWEST;
	
	public static int[] deltasFor(Direction d) {
		if (d == NORTH) return new int[] {-1, 0};
		if (d == NORTHEAST) return new int[] {-1, 1};
		if (d == EAST) return new int[] {0, 1};
		if (d == SOUTHEAST) return new int[] {1, 1};
		if (d == SOUTH) return new int[] {1, 0};
		if (d == SOUTHWEST) return new int[] {1, -1};
		if (d == WEST) return new int[] {0, -1};
		if (d == NORTHWEST) return new int[] {-1, -1};
		throw new RuntimeException("Bad direction: " + d);
	}
	
	public static Direction[] allDirections() {
		return new Direction[] {NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST};
	}
}
