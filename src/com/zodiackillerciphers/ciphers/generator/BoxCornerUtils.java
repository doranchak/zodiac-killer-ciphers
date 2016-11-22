package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.Utils;
import com.zodiackillerciphers.pivots.Direction;

public class BoxCornerUtils {
	
	/** cipher grid width */
	int W;
	/** text to search for box corners */
	String text;

	/** maps used to find candidate box corners.
	 * each map corresponds to the directions of the "box edges".
	 * each map's key is composed of the 3 symbols making up the box corner.
	 * each map's value is a list of all positions that have the same box corner.
	 */
	Map<String, List<Integer>> cornerNW = new HashMap<String, List<Integer>>();
	Map<String, List<Integer>> cornerNE = new HashMap<String, List<Integer>>();
	Map<String, List<Integer>> cornerSW = new HashMap<String, List<Integer>>();
	Map<String, List<Integer>> cornerSE = new HashMap<String, List<Integer>>();
	
	
	void map(Map<String, List<Integer>> map, String key, int val) {
		if (map == null || key == null) return;
		if (TrigramUtils.ignore(key)) return;
		List<Integer> list = map.get(key);
		if (list == null) list = new ArrayList<Integer>();
		list.add(val);
		map.put(key, list);
	}
	
	/** generate all qualifying box corner pairs from the given text */
	
	public List<BoxCornerPair> findPairs() {
		
		for (int i=0; i<text.length(); i++) {
			int row = Utils.rowFrom(i);
			int col = Utils.colFrom(i);
			char ch = text.charAt(i);
			
			if (row > 0 && col > 0)
				map(cornerNW,"" + ch + goNorth(row, col) + goWest(row, col), i);
			if (row > 0 && col < 16)
				map(cornerNE, "" + ch + goNorth(row, col) + goEast(row, col), i);
			if (row < 19 && col > 0)
				map(cornerSW,"" + ch + goSouth(row, col) + goWest(row, col), i);
			if (row < 19 && col < 16)
				map(cornerSE, "" + ch + goSouth(row, col) + goEast(row, col), i);
			
		}

		List<BoxCornerPair> pairs = new ArrayList<BoxCornerPair>();
		/** look for corners matching in the "interesting" directions */
		findPairs(pairs, cornerNW, new Direction[] {Direction.N, Direction.W}, cornerSE, new Direction[] {Direction.S, Direction.E});
		findPairs(pairs, cornerNE, new Direction[] {Direction.N, Direction.E}, cornerSW, new Direction[] {Direction.S, Direction.W});
		return pairs;
	}

	/** consider all possible pairs of box corners, and return only the pairs that are compatible */ 
	public void findPairs(List<BoxCornerPair> pairs, Map<String, List<Integer>> cornerMap1, Direction[] d1, Map<String, List<Integer>> cornerMap2, Direction[] d2) {
		
		for (String key : cornerMap1.keySet()) {
			// positions of corner 1
			List<Integer> pos1 = cornerMap1.get(key);
			// positions of corner 2
			List<Integer> pos2 = cornerMap2.get(key);
			
			if (pos2 == null || pos2.isEmpty()) continue;
			
			// if we got here, a corner in the first map matches one in the second map.
			// since each corner may appear more than once,
			// we generate all possible combinations of pairs of positions.
			
			for (Integer p1 : pos1) {
				for (Integer p2 : pos2) {
					BoxCorner bc1 = new BoxCorner(key, d1, p1);
					BoxCorner bc2 = new BoxCorner(key, d2, p2);
					BoxCornerPair pair = new BoxCornerPair(new BoxCorner[] {bc1, bc2});
					pairs.add(pair);
				}
			}
		}
	}
	
	public char goEast(int row, int col) {
		return text.charAt(Utils.posEast(row, col));
	}
	
	public char goSouth(int row, int col) {
		return text.charAt(Utils.posSouth(row, col));
	}
	public char goWest(int row, int col) {
		return text.charAt(Utils.posWest(row, col));
	}
	public char goNorth(int row, int col) {
		return text.charAt(Utils.posNorth(row, col));
	}
	public BoxCornerUtils(int w, String text) {
		super();
		W = w;
		this.text = text;
	}
	
	public static void test1() {
		BoxCornerUtils bcu = new BoxCornerUtils(17, "PRIESTTHEREWASONLYACHANGEOFBRIDEGROOMSWELMERCEDESWASMARRIEDPROCEEDEDCADEROUSSEBUTALTHOUGHINTHEEYESOFTHEWORLDSHEAPPEAREDCALMSHENEARLYFAINTEDASSHEPASSEDLARESERVEWHEREEIGHTEENMONTHSBEFORETHEBETROTHALHADBEENCELEBRATEDWITHHIMWHOM___________SHEMIGHTHAVEKNONSHESTILLLOVEDHADSHELOOKEDTOTHEBOTTOMOFHERHEARTFERNANDMOREHAPPYBBUTNOTMOREATHISEASEFORISAW");
		List<BoxCornerPair> boxCornerPairs = bcu.findPairs();
		System.out.println(boxCornerPairs);
		
	}
	public static void main(String[] args) {
		test1();
	}
}
