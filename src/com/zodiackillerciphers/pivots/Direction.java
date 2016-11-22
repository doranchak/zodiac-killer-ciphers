package com.zodiackillerciphers.pivots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** represents the direction of one of a pivot's ngrams */
public enum Direction {
	N, NE, E, SE, S, SW, W, NW, ANY;

	/** return all directions (besides "ANY") */
	public static List<Direction> all() {
		List<Direction> list = new ArrayList<Direction>();
		list.add(Direction.N);
		list.add(Direction.NE);
		list.add(Direction.E);
		list.add(Direction.SE);
		list.add(Direction.S);
		list.add(Direction.SW);
		list.add(Direction.W);
		list.add(Direction.NW);
		return list;
	}

	/** convert to list of possible row/col offsets */
	public static List<DirectionDelta> toDelta(Direction d) {
		List<DirectionDelta> list = new ArrayList<DirectionDelta>();
		if (d == ANY || d == N)
			list.add(new DirectionDelta(-1, 0));
		if (d == ANY || d == NE)
			list.add(new DirectionDelta(-1, 1));
		if (d == ANY || d == E)
			list.add(new DirectionDelta(0, 1));
		if (d == ANY || d == SE)
			list.add(new DirectionDelta(1, 1));
		if (d == ANY || d == S)
			list.add(new DirectionDelta(1, 0));
		if (d == ANY || d == SW)
			list.add(new DirectionDelta(1, -1));
		if (d == ANY || d == W)
			list.add(new DirectionDelta(0, -1));
		if (d == ANY || d == NW)
			list.add(new DirectionDelta(-1, -1));
		return list;
	}

	public static int toDegrees(Direction d) {
		if (d == null || d == ANY)
			return -1;
		if (d == N)
			return 0;
		if (d == NE)
			return 45;
		if (d == E)
			return 90;
		if (d == SE)
			return 135;
		if (d == S)
			return 180;
		if (d == SW)
			return 225;
		if (d == W)
			return 270;
		if (d == NW)
			return 315;
		return -1;
	}

	public static boolean perpendicular(Direction d1, Direction d2) {
		Direction[] d = new Direction[] { d1, d2 };
		Arrays.sort(d); // now they are sorted in clockwise order
		int g0 = toDegrees(d[0]);
		int g1 = toDegrees(d[1]);

		if (g1 - g0 == 90)
			return true;
		if (g1 == 270 && g0 == 0)
			return true;
		return false;

	}
}
