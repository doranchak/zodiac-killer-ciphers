package com.zodiackillerciphers.anagram;

import java.util.List;

import com.zodiackillerciphers.names.Census;
import com.zodiackillerciphers.names.Name;

public class LaffertyZ13 {
	
	// AEN [[G | K]RT | [S | W]DF] [R | T | <null>] NAM
	
	
	public static String[] tests = new String[] {
		"AENGRTNAM", "AENGRTRNAM", "AENGRTTNAM", "AENKRTNAM", "AENKRTRNAM", "AENKRTTNAM", "AENSDFNAM", "AENSDFRNAM", "AENSDFTNAM", "AENWDFNAM", "AENWDFRNAM", "AENWDFTNAM"		
	};
	
	public static String[] tests2 = new String[] {
		"AENGRTRAMNAM",
		"AENGRTARIESNAM",
		"AENGRTTNAM",
		"AENSDFRAMNAM",
		"AENSDFARIESNAM",
		"AENSDFTNAM"
	};
	
	public static void makeVariations() {
		String[] p1 = new String[] {"GRT", "KRT", "SDF", "WDF"};
		String[] p2 = new String[] {"", "R", "T"};
		
		for (int i=0; i<p1.length; i++) {
			for (int j=0; j<p2.length; j++) {
				System.out.println("AEN" + p1[i] + p2[j] + "NAM");
			}
		}
	}
	
	public static void findNames() {
		List<Name> firstMale = Census.readFirstMale();
		List<Name> firstFemale = Census.readFirstFemale();
		List<Name> last = Census.readLast();
		
		for (int i=0; i<tests.length; i++) {
			for (int j=tests[i].length(); j>=5; j--) {
				
				for (Name name : last) {
					if (name.name.length() != j) continue;
					if (Anagrams.anagram(name.name, tests[i])) {
						System.out.println("Found last name [" + name + "] in [" + tests[i] + "].");
					}
				}
				
				for (Name name : firstMale) {
					if (name.name.length() != j) continue;
					if (Anagrams.anagram(name.name, tests[i])) {
						System.out.println("Found male first name [" + name + "] in [" + tests[i] + "].");
					}
				}

				for (Name name : firstFemale) {
					if (name.name.length() != j) continue;
					if (Anagrams.anagram(name.name, tests[i])) {
						System.out.println("Found female first name [" + name + "] in [" + tests[i] + "].");
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		//makeVariations();
		findNames();
	}
}
