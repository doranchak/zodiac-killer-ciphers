package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxCornerPair {
	BoxCorner[] corners;

	public BoxCornerPair(BoxCorner[] corners) {
		super();
		this.corners = corners;
	}
	
	public String toString() {
		if (corners == null) return null;
		return "corners: [" + corners[0] + "] [" + corners[1] + "]";
	}
	
	/** return map of directions to symbols */
	public Map<String, String> directionMap() {
		Map<String, String> map = new HashMap<String, String>();
		for (BoxCorner corner : corners) {
			for (int i=0; i<corner.directions.length; i++) {
				map.put(""+corner.directions[i], ""+corner.symbols.charAt(i+1));
			}
		}
		return map;
	}
	
	/** generate list of positions */
	public List<Integer> positions() {
		List<Integer> positions = new ArrayList<Integer>();
		for (BoxCorner corner : corners) {
			positions.addAll(corner.positions());
		}
		return positions;
	}
	
	public boolean correctEncodings() {
		return corners[0].symbols.equals("*|-") && corners[1].symbols.equals("*|-");
	}
	
	public String highlight() {
		String js = "";
		for (BoxCorner b : corners) js += b.highlight();
		return js;
	}
	
	/** returns true if the two box corners are compatible as a pair */
	public static boolean compatible(BoxCorner bc1, BoxCorner bc2) {
		if (bc1 == null || bc2 == null) return false;
		BoxCornerType t1 = bc1.type();
		BoxCornerType t2 = bc2.type();
		//System.out.println(t1 + " " + t2);
		if (t1 == null || t2 == null) return false;
		if (t1 == BoxCornerType.NE && t2 == BoxCornerType.SW) return true; 
		if (t2 == BoxCornerType.NE && t1 == BoxCornerType.SW) return true; 
		if (t1 == BoxCornerType.ES && t2 == BoxCornerType.WN) return true; 
		if (t2 == BoxCornerType.ES && t1 == BoxCornerType.WN) return true;
		return false;
	}
}
