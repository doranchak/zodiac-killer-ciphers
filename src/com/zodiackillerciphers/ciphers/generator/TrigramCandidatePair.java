package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.List;


public class TrigramCandidatePair {
	/** trigram that repeats in a column */
	public Trigram columnarTrigram;
	/** the column in which it repeats */
	public int column;
	/**
	 * one of the trigrams that intersects with this columnar trigram. note that
	 * there can be mulitple trigrams that intersect, so they form distinct
	 * pairs of trigrams.
	 */
	public Trigram intersectingTrigram;
	/** the position of intersection */
	public int intersectingPosition;
	
	

	public TrigramCandidatePair(Trigram columnarTrigram, int column,
			Trigram intersectingTrigram, int intersectingPosition) {
		//System.out.println("added ctri " + columnarTrigram);
		//System.out.println("added itri " + intersectingTrigram);
		this.columnarTrigram = columnarTrigram;
		this.column = column;
		this.intersectingTrigram = intersectingTrigram;
		this.intersectingPosition = intersectingPosition;
	}

	/**
	 * We encode the columnar trigram with |5F, and the intersecting trigram
	 * with FBc. We wish to encode a pair of pivots with RJ|* and b.cV, which
	 * means | and c are shared symbols, and thus the trigrams share underlying
	 * plaintext with the pivots.
	 * 
	 */

	/** returns true if the pivot is compatible with the columnar trigram */
	public boolean compatibleColumnarPivot(String pivot) {
		if (pivot == null)
			return false;
		//System.out.println("c pivot " + pivot + " compare to " + columnarTrigram);
		return columnarTrigram.trigram.charAt(0) == pivot.charAt(1);
	}

	/** returns true if the pivot is compatible with the intersecting trigram */
	public boolean compatibleIntersectingPivot(String pivot) {
		if (pivot == null)
			return false;
		//System.out.println("i pivot " + pivot + " compare to " + intersectingTrigram);
		return intersectingTrigram.trigram.charAt(2) == pivot.charAt(1);
	}
	
	/*
	public boolean compatiblePivots(String pivot1, String pivot2) {
		if (pivot1 == null)
			return false;
		if (pivot2 == null)
			return false;
		return compatibleColumnarPivot(pivot1)
				&& compatibleIntersectingPivot(pivot2)
				|| compatibleColumnarPivot(pivot2)
				&& compatibleIntersectingPivot(pivot1);
	}*/

	
	public boolean correctEncodings() {
		return intersectingTrigram.trigram.equals("FBc") && columnarTrigram.trigram.equals("|5F");
	}
	
	public String toString() {
		return "column " + column + " intPos " + intersectingPosition + " cTri: " + columnarTrigram + "; iTri: " + intersectingTrigram;
	}
	
	/** NOTE: I think this does not work properly.  Seems to always return empty lists. */
	public List<Integer> positions() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i=0; i<3; i++) {
			if (intersectingTrigram != null && intersectingTrigram.positionsEnciphered != null) for (Integer pos : intersectingTrigram.positionsEnciphered) {
				list.add(pos+i);
			}
			if (columnarTrigram != null && columnarTrigram.positionsEnciphered != null) for (Integer pos : columnarTrigram.positionsEnciphered) {
				list.add(pos+i);
			}
		}
		return list;
	}

	public List<Integer> positions2() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i=0; i<3; i++) {
			if (intersectingTrigram != null && intersectingTrigram.positions != null) for (Integer pos : intersectingTrigram.positions) {
				list.add(pos+i);
			}
			if (columnarTrigram != null && columnarTrigram.positions != null) for (Integer pos : columnarTrigram.positions) {
				list.add(pos+i);
			}
		}
		return list;
	}
	
	public String highlight() {
		return columnarTrigram.highlight() + intersectingTrigram.highlight();
	}
}
