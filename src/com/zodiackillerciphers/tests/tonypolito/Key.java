package com.zodiackillerciphers.tests.tonypolito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;

/** representation of a key for the last 18 symbols of Z408 */
public class Key {
	
	
	/**
	 * the unique symbols among the last 18 plaintext letters of the harden
	 * solution
	 */
	public static char[] symbols = new char[] { 'B', 'E', 'H', 'I', 'M', 'O',
			'P', 'R', 'T' };
	/** how often each symbol occurs */
	public static int[] freq = new int[] { 1, 5, 2, 3, 1, 1, 1, 1, 3 };
	
	/** mapping of symbols to plaintext */
	public static char[] plain;
	
	/** list of possible offsets */
	public static int[] offsets = new int[] { 1,2,3,5,7,11,13,17,19,23 };
	
	public static int total = 0;
	
	/** count the number of ways to select offsets for each of the given number of slots */
	public static long count(int slots, int maxMissingOffset) {
		return count(slots, 0, new ArrayList<Integer>(), new HashSet<Integer>(), maxMissingOffset);
	}
	public static long count(int totalSlots, int currentSlot, List<Integer> key, Set<Integer> usedOffsets, int maxMissingOffset) {
		if (currentSlot >= totalSlots) { // we hit the total number of slots.
			// did we use up enough offsets?  if so, count it.  otherwise, don't.
			int diff = offsets.length - usedOffsets.size();
			if (diff > maxMissingOffset) return 0;
			
			// now we have a list of offsets.  optionally apply it as a key and see if 
			// the plaintext is an anagram for any of the pre-generated 18-character strings.
			total++;
			//if (total % 1000 == 0)
			//	System.out.println("Total: " + total);
			checkKey(key);
			return 1;
		}

		// check to see if it's impossible to achieve the desired number of distinct offsets.
		// how many more slots to assign?
		int remainingSlots = totalSlots - currentSlot;
		// how many distinct offsets are left?
		int remainingOffsets = offsets.length - usedOffsets.size();
		if (remainingOffsets - remainingSlots > maxMissingOffset) return 0; // too many offsets are left after best possible assignments

		// recursively try combinations of offsets
		long count = 0;
		for (int i=0; i<offsets.length; i++) {
			int offset = offsets[i];
			key.add(offset);
			boolean hadOffsetAlready = usedOffsets.contains(offset);
			if (!hadOffsetAlready) usedOffsets.add(offset);
			count += count(totalSlots, currentSlot+1, key, usedOffsets, maxMissingOffset);
			if (!hadOffsetAlready) usedOffsets.remove(offset);
			key.remove(key.size()-1);
		}
		return count;
	}
	
	static void checkKey(List<Integer> key) {
		Phrases.init();
		String decoded = "";
		if (key.size() != symbols.length)
			throw new RuntimeException("Size mismatch " + key.size() + " vs " + symbols.length);

		// A is dec 65
		for (int i=0; i<symbols.length; i++) {
			char symbol = symbols[i];
			int offset = key.get(i);
			int f = freq[i];
			int alpha = ((symbol-65 + offset) % 26)+65;
			char plain = (char) alpha;
			for (int j=0; j<f; j++) decoded += plain;
		}
		//System.out.println(decoded + ", " + key);
		for (String string : Phrases.strings) {
			if (Anagrams.anagram(string, decoded, true))
				System.out.println(string + ", " + key);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(count(9, 2));
		//System.out.println(count(10, 2));
		//System.out.println(count(11, 2));
	}
}
