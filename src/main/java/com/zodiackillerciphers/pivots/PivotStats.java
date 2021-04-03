package com.zodiackillerciphers.pivots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

public class PivotStats {
	/** list of pivots */
	List<Pivot> pivots;

	/** counts of pivots for every combination of directions */
	public Map<String, Integer> counts;

	static Direction[] directions = new Direction[] { Direction.N, Direction.E, Direction.S, Direction.W };

	public static List<String> allDirectionCombinations() {
		List<String> list = new ArrayList<String>();
		for (int i=0; i<directions.length; i++) {
			for (int j=i+1; j<directions.length; j++) {
				list.add("" + directions[i] + directions[j]);
			}
		}
		return list;
	}
	public PivotStats(String cipher, int pivotLength) {
		pivots = PivotUtils.findPivots(cipher, pivotLength);
		counts = new HashMap<String, Integer>();
		for (String key : allDirectionCombinations()) {
			counts.put(key, 0);
			
		}
		for (Pivot pivot : pivots) {
			Direction[] directions = pivot.getDirections();
			if (directions.length != 2)
				throw new RuntimeException(pivot.toString());
			//System.out.println(pivot.ngram);
			if (pivot.ngram.length() != pivotLength) continue;
			String key = translate("" + directions[0] + directions[1]);
			counts.put(key, counts.get(key)+1);
		}
		
		//System.out.println(counts);
	}
	
	public static String translate(String directions) {
		if (directions.equals("WE")) return "EW";
		if (directions.equals("WS")) return "SW";
		if (directions.equals("SN")) return "NS";
		if (directions.equals("EN")) return "NE";
		if (directions.equals("SE")) return "ES";
		return directions;
	}
	
	@Override
	public String toString() {
		return "PivotStats [pivots=" + pivots + ", counts=" + counts + "]";
	}
	public static void main(String[] args) {
		System.out.println("Z340");
		for (int l=2; l<6; l++) {
			System.out.println("=== LENGTH " + l);
			System.out.println(new PivotStats(Ciphers.Z340, l));
			System.out.println(new PivotStats(CipherTransformations.shuffle(Ciphers.Z340), l));
			
		}
		System.out.println("Z340_SOLUTION_TRANSPOSED");
		for (int l=2; l<6; l++) {
			System.out.println("=== LENGTH " + l);
			System.out.println(new PivotStats(Ciphers.Z340_SOLUTION_TRANSPOSED, l));
			System.out.println(new PivotStats(CipherTransformations.shuffle(Ciphers.Z340_SOLUTION_TRANSPOSED), l));
			
		}
		System.out.println("Z340_SOLUTION_UNTRANSPOSED");
		for (int l=2; l<6; l++) {
			System.out.println("=== LENGTH " + l);
			System.out.println(new PivotStats(Ciphers.Z340_SOLUTION_UNTRANSPOSED, l));
			System.out.println(new PivotStats(CipherTransformations.shuffle(Ciphers.Z340_SOLUTION_UNTRANSPOSED), l));
			
		}
	}
}
