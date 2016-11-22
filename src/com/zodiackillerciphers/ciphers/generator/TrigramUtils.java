package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrigramUtils {

	/** generate all trigrams from a given text, then keep only the special cases we want to preserve (column repeats, and intersections) */
	public static Set<TrigramCandidatePair> trigramsFrom(String text) {
		if (text == null)
			return null;
		//List<Trigram> trigrams = new ArrayList<Trigram>();
		Map<String, Trigram> map = new HashMap<String, Trigram>();
		for (int i = 0; i < text.length() - 2; i++) {
			String key = text.substring(i, i + 3);
			if (ignore(key)) continue;
			Trigram val = map.get(key);
			if (val == null)
				val = new Trigram(key);
			val.addPosition(i); // adds the position as well as maps the column # to the list of positions
			map.put(key, val);
		}
		
		// make another pass over the input.  find trigram that repeats in the same column.
		// if its preceding or following intersecting trigram also repeats, then return 
		// these trigrams.
		
		Set<TrigramCandidatePair> results = new HashSet<TrigramCandidatePair>(); 
		for (int i = 0; i < text.length() - 2; i++) {
			String key = text.substring(i, i + 3);
			if (ignore(key)) continue;
			Trigram val = map.get(key);
			val.cull(); // remove column positions for non-column-repeats.
			if (!val.repeats()) continue;
			if (!val.isSameColumn()) continue;
			String tri;
			boolean found = false;
			
			/*if (i > 1) {
				tri = text.substring(i-2,i+1); // the preceding trigram
				if (map.get(tri).repeats()) {
					found = true;
					val.addIntersectingTrigram(map.get(tri), i-2, i);
					results.add(new TrigramCandidatePair(val, i % Trigram.W, map.get(tri), i-2));
				}
			}*/
			if (i+4 < text.length()) {
				tri = text.substring(i+2, i+5); // the following trigram
				if (!ignore(tri)) {
					if (map.get(tri).repeats()) {
						found = true;
						val.addIntersectingTrigram(map.get(tri), i+2, i);
						results.add(new TrigramCandidatePair(val, i % Trigram.W, map.get(tri), i+2));
					}
				}
			}
			//if (found) results.add(val);
		}
		return results;
		
	}
	
	/** we don't want to include spaces and underscores in trigram counts */
	public static boolean ignore(String str) {
		if (str == null) return false;
		if (str.contains(" ")) return true;
		if (str.contains("_")) return true;
		return false;
	}

		/** generate all trigrams from a given text, then keep only the special cases we want to preserve (column repeats, and intersections) */
	public static List<Trigram> trigramsFromOBSOLETE(String text) {
		if (text == null)
			return null;
		//List<Trigram> trigrams = new ArrayList<Trigram>();
		Map<String, Trigram> map = new HashMap<String, Trigram>();
		for (int i = 0; i < text.length() - 2; i++) {
			String key = text.substring(i, i + 3);
			Trigram val = map.get(key);
			if (val == null)
				val = new Trigram(key);
			val.addPosition(i); // adds the position as well as maps the column # to the list of positions
			map.put(key, val);
		}
		
		Set<String> keysToRemove = new HashSet<String>();
		for (String key : map.keySet()) {
			Trigram val = map.get(key);
			val.cull();
			if (!val.repeats()) keysToRemove.add(key);
		}		
		
		for (Trigram tri : map.values()) System.out.println("before: " + tri);
		for (String key : keysToRemove) map.remove(key);
		for (Trigram tri : map.values()) System.out.println("after 1: " + tri);
		
		
		// now we have a list of repeating trigrams.  some may repeat in the same column.
		// now let's mark the repeating trigrams that intersect with a column-repeating trigram
		for (String key : map.keySet()) {
			Trigram val = map.get(key);
			if (val.isSameColumn()) {
				// look at its adjacent trigrams and see if they repeat.  if so, mark them as intersecting trigrams.
				Set<String> candidates = new HashSet<String>();
				for (Integer colNum : val.columnRepeatPositions.keySet()) {
					for (int pos : val.columnRepeatPositions.get(colNum)) {
						if (pos > 1) candidates.add(text.substring(pos-2, pos+1));
						if (pos < text.length() - 4) candidates.add(text.substring(pos+2, pos+5));
					}
					for (String candidate : candidates) {
						Trigram val2 = map.get(candidate);
						if (val2 == null) continue;
						val2.setIntersects(true);
						//val.addIntersectingTrigram(colNum, candidate);
					}
				}
			}
		}
		
		keysToRemove.clear();
		for (String key : map.keySet()) {
			Trigram val = map.get(key);
			if (!val.isSameColumn() && !val.isIntersects()) keysToRemove.add(key);
		}
		for (String key : keysToRemove) map.remove(key);
		
		
		for (Trigram tri : map.values()) System.out.println("after 2: " + tri);
		
		return new ArrayList<Trigram>(map.values());
	}
	
	/** remove trigrams that don't repeat in the same column

	/** return only the trigrams that have repeats in the same column (mark them, too) */
	/*public static List<Trigram> columnRepeatsFrom(List<Trigram> trigrams) {
		if (trigrams == null) return null;
		List<Trigram> result = new ArrayList<Trigram>();
		for (Trigram trigram : trigrams) {
			boolean found = false;
			if (!trigram.repeats()) continue;
			Set<Integer> cols = new HashSet<Integer>();
			for (Integer pos : trigram.positions) {
				int col = pos % Trigram.W;
				if (cols.contains(col)) { //  
					found = true;
					trigram.addColumnRepeatPosition(col, pos);
				} else cols.add(col);
			}
			if (found) result.add(trigram);
		}
	}*/

}
