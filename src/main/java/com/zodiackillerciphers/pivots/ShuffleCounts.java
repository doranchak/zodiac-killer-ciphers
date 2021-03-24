package com.zodiackillerciphers.pivots;

import java.util.HashMap;
import java.util.Map;

public class ShuffleCounts {
	/** map ngram repeat count to pivot counts */
	Map<Integer, PivotCounts> map;
	
	public ShuffleCounts() {
		map = new HashMap<Integer, PivotCounts>();
	}
	/** report a pivot (any direction) count for the given bigram count */
	public void reportPivotAnyDirection(int count, int numRepeats) {
		PivotCounts pc = map.get(numRepeats);
		if (pc == null) pc = new PivotCounts();
		pc.reportPivotAnyDirection(count);
		map.put(numRepeats, pc);
	}
	/** report a pivot (any direction) count for the given bigram count */
	public void reportPivotPair(int count, int numRepeats) {
		PivotCounts pc = map.get(numRepeats);
		if (pc == null) pc = new PivotCounts();
		pc.reportPivotPair(count);
		map.put(numRepeats, pc);
	}

	public void dump() {
		System.out.println("Shuffle counts:");
		for (Integer key : map.keySet()) {
			System.out.println("   " + key + " reps: ");
			map.get(key).dump();
		}
//		System.out.println(map);
	}
}
