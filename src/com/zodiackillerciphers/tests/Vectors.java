package com.zodiackillerciphers.tests;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** mainly to perform distance calculations between vectors */
public class Vectors {

	/** calculate euclidian distance between the two given integer vectors.
	 * caller is responsible for ensuring vectors both have the expected ordering.
	 * differences in sizes are allowed - missing elements contribute to the distance.
	 */
	public static double distance(List<Integer> list1, List<Integer> list2, boolean sqrt) {
		if (list1 == null) list1 = new ArrayList<Integer>();
		if (list2 == null) list2 = new ArrayList<Integer>();
		
		double diffs = 0;
		int max = Math.max(list1.size(), list2.size());
		for (int i=0; i<max; i++) {
			int v1 = 0;
			int v2 = 0;
			if (i < list1.size()) v1 = list1.get(i);
			if (i < list2.size()) v2 = list2.get(i);
			diffs += Math.pow(v1-v2, 2);
		}
		if (sqrt) return Math.sqrt(diffs);
		else return diffs;
	}

	public static double distanceDouble(List<Double> list1, List<Double> list2, boolean sqrt) {
		if (list1 == null) list1 = new ArrayList<Double>();
		if (list2 == null) list2 = new ArrayList<Double>();
		
		double diffs = 0;
		int max = Math.max(list1.size(), list2.size());
		for (int i=0; i<max; i++) {
			double v1 = 0;
			double v2 = 0;
			if (i < list1.size()) v1 = list1.get(i);
			if (i < list2.size()) v2 = list2.get(i);
			diffs += Math.pow(v1-v2, 2);
		}
		if (sqrt) return Math.sqrt(diffs);
		else return diffs;
	}
	
	/** return compact string form of the given list of counts, in descending order. */
	public static String toString(List<Integer> list) {
		return toString(list, true);
	}
	public static String toString(List<Integer> list, boolean includeRepeatTotals) {
		if (list == null) return null;
		String result = "";

		int numReps = 0;
		
		// map: key is the count, value is the number of things with the same count
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		for (Integer key : list) {
			Integer val = counts.get(key);
			if (val == null) val = 0;
			val++;
			counts.put(key, val);
		}
		
		List<Integer> sorted = new ArrayList<Integer>(counts.keySet());
		Collections.sort(sorted);
		Collections.reverse(sorted);
		
		for (Integer key : sorted) {
			Integer val = counts.get(key);
			result += key;
			if (val > 1) result += "(" + val + ")";
			result += " ";
			
			if (key > 1) numReps += val*(key-1);
		}
		
		if (includeRepeatTotals) result += "[" + numReps + " repeats]";
		
		return result;
	}
	public static String toStringDouble(List<Double> list, boolean decimalFormat) {
		DecimalFormat df = new DecimalFormat("#.##");
		if (list == null) return null;
		String result = "";

		// map: key is the count, value is the number of things with the same count
		Map<Double, Integer> counts = new HashMap<Double, Integer>();
		for (Double key : list) {
			Integer val = counts.get(key);
			if (val == null) val = 0;
			val++;
			counts.put(key, val);
		}
		
		List<Double> sorted = new ArrayList<Double>(counts.keySet());
		Collections.sort(sorted);
		Collections.reverse(sorted);
		
		for (Double key : sorted) {
			Integer val = counts.get(key);
			if (decimalFormat)
				result += df.format(key);
			else result += key;
			if (val > 1) result += "(" + val + ")";
			result += " ";
		}
		return result;
	}
}
