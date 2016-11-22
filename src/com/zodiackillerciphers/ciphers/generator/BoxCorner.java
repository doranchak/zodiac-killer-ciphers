package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.pivots.Direction;
import com.zodiackillerciphers.pivots.DirectionDelta;

public class BoxCorner {
	String symbols;
	Direction[] directions;
	int pos;
	public BoxCorner(String symbols, Direction[] directions, int pos) {
		super();
		this.symbols = symbols;
		this.directions = directions;
		this.pos = pos;
	}
	/** generate list of positions */
	public List<Integer> positions() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(pos);
		for (Direction d : directions) {
			List<DirectionDelta> deltas = Direction.toDelta(d);
			for (DirectionDelta delta : deltas) list.add(delta.fromPos(pos));
		}
		return list;
	}
	
	/** generate cipher symbols */
	public String cipher() {
		return "*" + charFor(directions[0]) + charFor(directions[1]);
	}
	
	public char charFor(Direction d) {
		if (d == Direction.E || d == Direction.W) return '-';
		return '|';
	}

	public String toString() {
		return symbols + " " + directions[0] + " " + directions[1] + " " + pos; 
	}

	public String highlight() {
		String js = "box corner ";
		List<Integer> positions = positions();
		for (Integer pos : positions) {
			js += "darkenpos(" + pos + "); ";
		}
		return js;
	}
	
	/** return type (NE, ES, SW, WN) */
	public BoxCornerType type() {
		if (directions == null) return null;
		if (hasDirection(Direction.N) && hasDirection(Direction.E)) return BoxCornerType.NE;
		if (hasDirection(Direction.E) && hasDirection(Direction.S)) return BoxCornerType.ES;
		if (hasDirection(Direction.S) && hasDirection(Direction.W)) return BoxCornerType.SW;
		if (hasDirection(Direction.W) && hasDirection(Direction.N)) return BoxCornerType.WN;
		return null;
	}
	
	public boolean hasDirection(Direction d) {
		if (directions == null) return false;
		for (Direction d2 : directions) if (d2 == d) return true;
		return false;
	}
	
}
