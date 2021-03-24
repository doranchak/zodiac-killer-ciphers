package com.zodiackillerciphers.corpus;

import java.util.ArrayList;
import java.util.List;

public class Pareto {

	/** extract pareto optimal front from given list of scores.  remove front from the list, so we can continue to extract fronts. */
	public static List<Score> frontFrom(List<Score> scores) {

		String line = scores.size() + ",";
		List<Score> front = new ArrayList<Score>();
		//front.add(scores.get(scores.size()-1));
		
		for (int i=scores.size()-1; i>=0; i--) {
			Score A = scores.get(i);
			//System.out.println("A: " + A);
			boolean add = true;
			// compare A to every member of the front
			for (int j=front.size()-1; j>=0; j--) {
				Score B = front.get(j);
				if (A == B) {
					add = false;
					break;
				}
				//System.out.println("   B: " + B);
				
				Compare comp = compare(A,B);
				//System.out.println("   comp: " + comp);
				
				//if (comp == EQUAL) { // keep dupes in the front.
				//	add = true;
				//} else 
				if (comp == Compare.DOMINATES) {
					//System.out.println("   removing B.");
					front.remove(B);
					//add = true;
				} else if (comp == Compare.DOMINATED) {
					add = false; 
					break;
				} 
			}
			if (add) {
				//System.out.println("Adding A.");
				front.add(A);
			}
		}
		
		for (int i=front.size()-1; i>=0; i--) {
			Score B = front.get(i);
			for (int j=scores.size()-1; j>=0; j--) {
				Score A = scores.get(j);
				if (A==B) scores.remove(j);
				//continue;
			}
		}
		System.out.println("buh " + line+scores.size()+","+front.size());
		return front;
	}
	
	public static void dump(List<Score> scores) {
		for (Score s : scores) System.out.println(s);
	}
	public static Compare compare(Score A, Score B) {
		
		int countEqual = 0;
		int countSuperior = 0;
		int countInferior = 0;
		
		for (int i=0; i<A.scores.length; i++) {
			if (A.scores[i] < B.scores[i]) countSuperior++;
			else if (A.scores[i] > B.scores[i]) countInferior++;
			else countEqual++;
		}
		
		if (countEqual == A.scores.length) return Compare.EQUAL;

		// A dominates B: 
		if (countInferior == 0 && countSuperior > 0) return Compare.DOMINATES; 

		// B dominates A: 
		if (countInferior > 0 && countSuperior == 0) return Compare.DOMINATED; 

		return Compare.NON_DOMINATED;
	}
	
}
