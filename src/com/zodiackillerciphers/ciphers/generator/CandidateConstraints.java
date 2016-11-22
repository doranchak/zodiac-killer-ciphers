package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.Utils;
import com.zodiackillerciphers.pivots.Direction;
import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotPair;



/** a plaintext or ciphertext was found meeting our requirements for certain patterns to appear.
 * this class represents those patterns.
 *
 */
public class CandidateConstraints {
	/** the trigram pair (columnar and intersecting trigrams) */
	TrigramCandidatePair trigrams;
	/** the pivot that shares a symbol with the columnar trigram (|5F) */
	Pivot pivotColumnar;
	/** the pivot that shares a symbol with the intersecting trigram (FBc) */
	Pivot pivotIntersecting;
	FoldMarks foldMarks;
	BoxCornerPair boxCornerPair;
	public CandidateConstraints(TrigramCandidatePair trigrams,
			Pivot pivotColumnar, Pivot pivotIntersecting, FoldMarks foldMarks, BoxCornerPair boxCornerPair) {
		super();
		this.trigrams = trigrams;
		this.pivotColumnar = pivotColumnar;
		this.pivotIntersecting = pivotIntersecting;
		this.foldMarks = foldMarks;
		this.boxCornerPair = boxCornerPair;
	}
	public TrigramCandidatePair getTrigrams() {
		return trigrams;
	}
	public void setTrigrams(TrigramCandidatePair trigrams) {
		this.trigrams = trigrams;
	}
	public Pivot getPivotColumnar() {
		return pivotColumnar;
	}
	public void setPivotColumnar(Pivot pivotColumnar) {
		this.pivotColumnar = pivotColumnar;
	}
	public Pivot getPivotIntersecting() {
		return pivotIntersecting;
	}
	public void setPivotIntersecting(Pivot pivotIntersecting) {
		this.pivotIntersecting = pivotIntersecting;
	}
	
	public String toString() {
		return "candidateConstraints [trigrams " + trigrams + " " +
				"pivotColumnar " + pivotColumnar + " " +
				"pivotIntersecting " + pivotIntersecting + " " +
				"foldMarks " + foldMarks + " " +
				"boxCornerPair " + boxCornerPair + "]";

	}
	

	public boolean correctEncodingsPivots() {
		return pivotColumnar.ngram.equals("*|JR") && pivotIntersecting.ngram.equals("Vc.b");
	}
	
	/** generate all positions covered by the relevant constraints */
	public List<Integer> positions() {
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(pivotColumnar.positions);
		list.addAll(pivotIntersecting.positions);
		list.addAll(trigrams.positions());
		list.add(foldMarks.positionLeft);
		list.add(foldMarks.positionRight);
		list.addAll(boxCornerPair.positions());
		return list;
	}
	
	public String highlight() {
		String js = "";
		if (trigrams != null) js += trigrams.highlight();
		if (pivotColumnar != null) js += pivotColumnar.highlight(); 
		if (pivotIntersecting != null) js += pivotIntersecting.highlight(); 
		if (foldMarks != null) js += foldMarks.highlight(); 
		if (boxCornerPair != null) js += boxCornerPair.highlight();
		return js;
	}
	/**
	 * externalized version of the pivot detection; useful for checking if test
	 * ciphers meet the pivots criteria
	 */	
	public static boolean hasPivots(String cipher) {
		CandidatePlaintext cp = new CandidatePlaintext(0, cipher);
		boolean result = cp.criteriaHasPivots();
		//System.out.println("hasPivots? " + result);
		return result;
	}
	/** does the cipher have "fold lines" in the middle of the cipher? */
	public static boolean hasFoldLines(String cipher) {
		if (cipher.charAt(153) == '-' && cipher.charAt(153) == cipher.charAt(169)) return true;
		if (cipher.charAt(170) == '-' && cipher.charAt(170) == cipher.charAt(186)) return true;
		return false;
	}
	/** return any box corner pairs found in the given cipher */
	public static List<BoxCornerPair> boxCornerPairs(String cipher, boolean showSteps) {
		List<BoxCorner> corners = new ArrayList<BoxCorner>();
		int w=17; // assumed width is 17
		
		char s1= '-';
		char s2 = '|';
		for (int pos=0; pos<cipher.length(); pos++) {
			char ch = cipher.charAt(pos);
			if (ch != '*') continue;
			int posEast = Utils.posEast(pos);
			int posSouth = Utils.posSouth(pos);
			int posWest = Utils.posWest(pos);
			int posNorth = Utils.posNorth(pos);
			
			char charEast = posEast > -1 ? cipher.charAt(posEast) : ' ';
			char charSouth = posSouth > -1 ? cipher.charAt(posSouth) : ' ';
			char charWest = posWest > -1 ? cipher.charAt(posWest) : ' ';
			char charNorth = posNorth > -1 ? cipher.charAt(posNorth) : ' ';
			
			if (showSteps) System.out.println(pos + " " + ch + ' ' + charEast + ' ' + charSouth + ' ' + charWest + ' ' + charNorth);
			
			String key = "*-|";
			if (charNorth == '|' && charEast == '-')
				corners.add(new BoxCorner(key, new Direction[] {Direction.N, Direction.E}, pos));
			if (charSouth == '|' && charEast == '-')
				corners.add(new BoxCorner(key, new Direction[] {Direction.S, Direction.E}, pos));
			if (charSouth == '|' && charWest == '-')
				corners.add(new BoxCorner(key, new Direction[] {Direction.S, Direction.W}, pos));
			if (charNorth == '|' && charWest == '-')
				corners.add(new BoxCorner(key, new Direction[] {Direction.N, Direction.W}, pos));
			
		}
		if (showSteps) System.out.println("corners: " + corners);
		List<BoxCornerPair> pairs = new ArrayList<BoxCornerPair>();
		for (int i=0; i<corners.size()-1; i++) {
			for (int j=i+1; j<corners.size(); j++) {
				if (BoxCornerPair.compatible(corners.get(i), corners.get(j))) {
					pairs.add(new BoxCornerPair(new BoxCorner[] {corners.get(i), corners.get(j)}));
				}
			}
		}
		return pairs;
	}
	
	
	
	
}
