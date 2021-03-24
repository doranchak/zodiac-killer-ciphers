package com.zodiackillerciphers.tests.homophones;

import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

public class HomophoneShuffles {
	
	public static String z408E = "ZpW+6NEZpW+6NEEZpW+6NEZpW+6NENZpW+6NEZpW++WZE6Z+W6EW6E";
	
	public static String[] sequences = new String[] {
			"GSl8G8Sl8GS8l8G8lSlGS8SGG8",
			"fzzfzfz",
			"ZpW+6NEZpW+6NEEZpW+6NEZpW+6NENZpW+6NEZpW++WZE6Z+W6EW6E",
			"JQJQJQJQJJQ",
			"M)M)M)M)M)M)M)M)",
			"9PUk9PUk9PUk9PUk9PUk9PUk9PUk9PUk9P99U99PP9Uk",
			"%B%B#B%B#B#B#B%#B%%%%#B#B%%#B##%B",
			"O^D(O^D(O^D(O^D(O^DO^DO",
			"X!TdXTdX!TdX!TXTdT!XXd!dXTX",
			"tr\\tr\\tr\\tr\\trt\\trr",
			"F@K7F@K@KF@K7F@KF7F@",
			"HI5LHI5ILHI5LHI5LHI5LHI5LI5LHL5IIHI"
	};
	
	/** Z408's symbols for E form a nice pattern of repeated cycles.
	 * How often do they occur by chance in shuffles?
	 */
	public static void shuffleCycles(String cipher, int L, int shuffles) {
		System.out.println(L + ", " + cipher + ", " + shuffles);
		StatsWrapper stats = new StatsWrapper();
		stats.actual = maxOccurences(cipher, L);
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			int max = maxOccurences(cipher, L);
//			if (max > 1) System.out.println(cipher + " " + max);
			stats.addValue(max);
		}
		stats.output();
	}

	/** what is the max count of repetitions for the given string length */ 
	public static int maxOccurences(String str, int L) {
		int max = 0;
		for (int i=0; i<str.length()-L+1; i++) {
			String sub = str.substring(i, i+L);
			int val = countOccurences(str, sub);
			max = Math.max(max, val);
//			if (val == 2) System.out.println(str + " " + sub + " " + max);
		}
		return max;
	}
	
	public static int countOccurences(String str, String search) {
		int i = str.indexOf(search); 
		if (i<0) return 0;
		if (i==str.length()-search.length()) return 1;
		return 1+countOccurences(str.substring(i+search.length()), search);
	}
	
	public static void shuffleCycles() {
		for (String seq : sequences) {
			for (int L=2; L<8; L++) {
				shuffleCycles(seq, L, 1000000);
			}
		}
	}
	
	public static void main(String[] args) {
//		System.out.println(countOccurences(z408E, "ZpW+6NE"));
//		System.out.println(maxOccurences("N66ZN+EW6ZpN6pZ6+pEWEEE+ZZ++WENWpEWWWWN+Z+EW6ZpNZ+p66E", 7));
//		shuffleCycles(z408E, 7, 10000000);
		shuffleCycles("LVMLVMLVMLVMLMLVVMLM", 3, 1000000);
//		shuffleCycles();
	}
}
