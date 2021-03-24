package com.zodiackillerciphers.pivots;

import java.util.HashMap;
import java.util.Map;

public class PivotCounts {
	/** histogram of number of pivots founds in any direction.  key is count, val is number of observances of the count. */
	public Map<Integer, Integer> pivotAnyDirection;
	/** histogram of number of pivot pairs found.  key is count, val is number of observances of the count. */
	public Map<Integer, Integer> pivotPairs;

	public PivotCounts() {
		pivotAnyDirection = new HashMap<Integer, Integer>();
		pivotPairs = new HashMap<Integer, Integer>();
	}
	
	public void reportPivotAnyDirection(int count) {
		Integer val = pivotAnyDirection.get(count);
		if (val == null) val = 0;
		val++;
		pivotAnyDirection.put(count, val);
	}
	public void reportPivotPair(int count) {
		Integer val = pivotPairs.get(count);
		if (val == null) val = 0;
		val++;
		pivotPairs.put(count, val);
	}
	
	public void dump() {
//		System.out.println("any " + pivotAnyDirection);
//		System.out.println("pair " + pivotAnyDirection);
		for (Integer key : pivotAnyDirection.keySet()) {
			System.out.println("      - any direction count " + key + ", occurrences " + pivotAnyDirection.get(key));
		}
		for (Integer key : pivotPairs.keySet()) {
			System.out.println("      - pair count " + key + ", occurrences " + pivotPairs.get(key));
		}
	}
	
}
