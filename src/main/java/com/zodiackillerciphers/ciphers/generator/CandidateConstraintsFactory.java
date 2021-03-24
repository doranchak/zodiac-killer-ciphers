package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.Utils;
import com.zodiackillerciphers.pivots.Pivot;

public class CandidateConstraintsFactory {
	/**
	 * given a list of trigram candidate pairs and a list of pivots, generate a
	 * list of CandidateConstraints for each distinct combination of compatible
	 * trigrams/pivots.
	 */
	public static List<CandidateConstraints> makeCandidateConstraints(
			Set<TrigramCandidatePair> trigramCandidatePairs,
			List<Pivot> pivots, List<FoldMarks> foldMarks, List<BoxCornerPair> boxCornerPairs, boolean enforceEncodings) {
		if (trigramCandidatePairs == null) {
			//System.out.println("No trigram pairs");
			return null;
		}
		if (pivots == null) {
			//System.out.println("No pivots");
			return null;
		}
		if (foldMarks == null) { 
			//System.out.println("No fold marks");
			return null;
		}
		if (boxCornerPairs == null) { 
			//System.out.println("No box corner pairs");
			return null;
		}
		List<CandidateConstraints> list = new ArrayList<CandidateConstraints>();
		for (TrigramCandidatePair pair : trigramCandidatePairs) {
			Utils.debug("looking at trigram pair " + pair);
			for (int i = 0; i < pivots.size() - 1; i++) {
				for (int j = i + 1; j < pivots.size(); j++) {
					Pivot pivot1 = pivots.get(i);
					Pivot pivot2 = pivots.get(j);
					Utils.debug("pivots " + i + " and " + j);
					if (pivot1.overlaps(pivot2)) continue; // skip any overlapping pivots
					if (pair.compatibleColumnarPivot(pivot1.ngram)
							&& pair.compatibleIntersectingPivot(pivot2.ngram)) {
						CandidateConstraints cc = new CandidateConstraints(
								pair, pivot1, pivot2, null, null);
						list.add(cc);
						//System.out.println("pair compatible with pivot1 ngram");
					} else if (pair.compatibleColumnarPivot(pivot2.ngram)
							&& pair.compatibleIntersectingPivot(pivot1.ngram)) {
						CandidateConstraints cc = new CandidateConstraints(
								pair, pivot2, pivot1, null, null);
						list.add(cc);
						//System.out.println("pair compatible with pivot2 ngram");
					}
				}
			}
		}
		
		// But wait!  Now we have to enforce the constraints imposed by fold marks and box corners.
		// Pivots: *|JR, Vc.b
		// Trigrams: FBc, |5F
		// Fold marks: -
		// Box corners: *, -, |
		
		// Fold mark's "-" is shared with the east/west edges of box corners.
		// Box corner's * is shared with columnar pivot's 1st symbol
		// Box corner's - is shared with fold marks
		// Box corner's | is shared with columnar pivot's 2nd symbol
		
		// we must check unique combinations and return all that satisfy these constraints.
		
		/** the trigram pair (columnar and intersecting trigrams) */
		//TrigramCandidatePair trigrams;
		/** the pivot that shares a symbol with the columnar trigram (|5F) */
		//Pivot pivotColumnar;
		/** the pivot that shares a symbol with the intersecting trigram (FBc) */
		
		
		
		List<CandidateConstraints> list2 = new ArrayList<CandidateConstraints>();
		for (CandidateConstraints cc : list) {
			for (FoldMarks fm : foldMarks) {
				for (BoxCornerPair boxCornerPair : boxCornerPairs) {
					char ch1 = boxCornerPair.corners[0].symbols.charAt(0); // the 'center' symbol of each pair
					char ch2 = boxCornerPair.directionMap().get("E").charAt(0); // the east and west edge should be the same character
					char ch3 = boxCornerPair.directionMap().get("N").charAt(0); // the north and south edge should be the same character

					char ch4 = cc.pivotColumnar.ngram.charAt(0); // columnar pivot's 1st symbol
					char ch5 = fm.symbol; // fold marks
					char ch6 = cc.pivotColumnar.ngram.charAt(1); // columnar pivot's 2nd symbol

					if (ch1 == ch4 && ch2 == ch5 && ch3 == ch6) {
						//System.out.println("box pivot fold compat");
						// so far, they are compatible.  but we need one final check:
						// does anything overlap?
						// pivots occupy 14 positions
						// box corners occupy 6 positions
						// fold marks occupy 2 positions
						// therefore there should be 22 total unique positions
						Set<Integer> uniques = new HashSet<Integer>();
						uniques.addAll(cc.pivotColumnar.positions);
						uniques.addAll(cc.pivotIntersecting.positions);
						uniques.addAll(boxCornerPair.positions());
						uniques.add(fm.positionLeft);
						uniques.add(fm.positionRight);
						Utils.debug("Uniques: "+uniques.size());
						if (uniques.size() == 22) {
							//System.out.println("correct uniques");
							
							// and finally, if enforceEncodings is true, make sure each constraint has preserved the expected assignment of cipher symbols.
							boolean add = !enforceEncodings; 
							if (enforceEncodings) {
								add = cc.trigrams.correctEncodings()
										&& cc.correctEncodingsPivots()
										&& fm.correctEncodings()
										&& boxCornerPair.correctEncodings();
							}
							if (add) {
								//System.out.println("encodings enforced");
								list2.add(new CandidateConstraints(cc.trigrams, cc.pivotColumnar, cc.pivotIntersecting, fm, boxCornerPair));
							}
						}
					}
					
				}
			}
		}
		return removeOverlaps(list2);
		//return list;
	}
	
	static List<CandidateConstraints> removeOverlaps(List<CandidateConstraints> list) {
		List<CandidateConstraints> results = new ArrayList<CandidateConstraints>();
		if (list == null) return results;
		for (CandidateConstraints cc : list) {
			
			if (overlaps(cc.trigrams.positions2(), cc.pivotColumnar.positions) > 1) continue;
			if (overlaps(cc.trigrams.positions2(), cc.pivotIntersecting.positions) > 1) continue;
			
			// none of these should share any positions: pivots, box corners, fold marks
			Set<Integer> set1 = new HashSet<Integer>(cc.pivotColumnar.positions); 
			Set<Integer> set2 = new HashSet<Integer>(cc.pivotIntersecting.positions);
			Set<Integer> set3 = new HashSet<Integer>(cc.boxCornerPair.positions());
			Set<Integer> set4 = new HashSet<Integer>();
			set4.add(cc.foldMarks.positionLeft);
			set4.add(cc.foldMarks.positionRight);
			
			Set<Integer> set5 = new HashSet<Integer>();
			set5.addAll(set1);
			set5.addAll(set2);
			set5.addAll(set3);
			set5.addAll(set4);
			
			int sum = set1.size() + set2.size() + set3.size() + set4.size();
			Utils.debug("overlap sum " + sum + " vs union " + set5.size());
			
			if (sum != set5.size()) {
				Utils.debug("overlap detected");
				Utils.debug("set1 " + set1);
				Utils.debug("set2 " + set1);
				Utils.debug("set3 " + set1);
				Utils.debug("set4 " + set1);
				Utils.debug("set5 " + set1);
				continue;
			}
			results.add(cc);
		}
		return results;
	}
	
	/** returns true if a trigram shares more than one position with a pivot */ 
	static boolean overlap(TrigramCandidatePair pair, Pivot pivot) {
		Utils.debug("overlap check pair positions " + pair.positions2());
		Set<Integer> set = new HashSet<Integer>(pair.positions2()); // should be 11 positions, since one is shared among the pair of trigram
		Utils.debug("overlap check pivot positions " + pivot.positions);
		set.addAll(pivot.positions); // pivot adds 7 more.  1 is shared with a trigram.  so total now should be 11+7-1 = 17.
		Utils.debug("unique positions for pair/pivot combo: " + set.size() + ".  " + pair + ", " + pivot);
		//return set.size() != 17;
		return false;
	}
	
	/** count number of overlap between each list of positions */
	static int overlaps(List<Integer> list1, List<Integer> list2) {
		Set<Integer> set1 = new HashSet<Integer>(list1);
		Set<Integer> set2 = new HashSet<Integer>(list2);
		Utils.debug("retainAll set1 before " + set1 + " set2 before " + set2);
		set1.retainAll(set2);
		Utils.debug("retainAll set1 after " + set1 + " size " + set1.size());
		return set1.size();
	}
	
	
}
