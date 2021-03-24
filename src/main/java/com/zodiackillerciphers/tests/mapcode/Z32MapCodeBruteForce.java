package com.zodiackillerciphers.tests.mapcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Z32MapCodeBruteForce {

	static Map<String, Integer> violationCounts;
	static String tab = "	";
	static String applied408key = "_IFTLNIRWHDA_NGOAOESNBXLT_ETDIEI";

	static void search(int which) {
		// long total = 1;
		// for (GrammarElement ge : Grammars.grammars[which]) {
		// total *= Grammars.grammarElementToStrings(ge).size();
		// }
		//
		// total *= 2; // because of two possible delimiters between grammar
		// elements
		// System.out.println("SEARCH SPACE: " + total);

		violationCounts = new HashMap<String, Integer>();
		Counter counter = new Counter();
		for (String delim : new String[] { "", " " }) {
			for (boolean fraction : new boolean[] { true, false }) {
				for (boolean unit : new boolean[] { true, false }) {
					search(new StringBuffer(), new StringBuffer(),
							new StringBuffer(), new StringBuffer(),
							Grammars.grammars[which], 0, delim, fraction, unit,
							new HashSet<String>(), counter);
				}
			}
		}
		System.out.println("Violation counts: " + violationCounts);
		System.out.println("Final count: " + counter);

	}

	static void search(StringBuffer sb, StringBuffer description,
			StringBuffer descriptionRadians, StringBuffer descriptionInches,
			GrammarElement[] grammar, int index, String delimiter,
			boolean fractions, boolean units, Set<String> seen, Counter counter) {
		// System.out.println(sb + " (" + index + ")");
		counter.inc();

		if (!valid(sb)) {
			// if (sb.length() == 32)
			// System.out.println("INVALID: " + sb);
			return; // violates cipher constraints
		}
		if (index >= grammar.length) {
			// we reached the end without violating a constraint
			if (sb.length() == 32) {
				// success!
				// System.out.println("SUCCESS: " + sb + (" ".equals(delimiter)
				// ? "" : "  (" + description + ")"));
				System.out.println(sb + tab 
						+ description.toString().replaceAll("  ", " ") + tab + descriptionRadians + tab + descriptionInches + tab + key408(sb));
				if (seen.contains(sb)) {
					System.out.println("SEEN");
					System.exit(-1);
				}
				seen.add(sb.toString());
				violation("Success");
			}
			violation("Too short");
			// else System.out.println("INCOMPLETE: " + sb);
			return;
		}

		GrammarElement element = grammar[index];
		int originalLength = sb.length();
		int originalLength2 = description.length();
		int originalLength3 = descriptionRadians.length();
		int originalLength4 = descriptionInches.length();
		for (GrammarString gs : Grammars.grammarElementToStrings(element,
				fractions, delimiter)) {
			// System.out.println(delimiter + "," + elemStr);
			if (" ".equals(delimiter) && sb.length() > 0
					&& sb.charAt(sb.length() - 1) != ' ')
				sb.append(delimiter);
			sb.append(gs.string);
			description.append(gs.description + " ");
			if (element.equals(GrammarElement.MILS_AMOUNT)) {
				descriptionRadians.append(gs.amount + " ");
			} else if (element.equals(GrammarElement.INCHES_AMOUNT)) {
				descriptionInches.append(gs.amount + " ");
			}
			search(sb, description, descriptionRadians, descriptionInches,
					grammar, index + 1, delimiter, fractions, units, seen,
					counter);
			// revert string after recursion
			sb.replace(originalLength, sb.length(), "");
			description.replace(originalLength2, description.length(), "");
			descriptionRadians.replace(originalLength3,
					descriptionRadians.length(), "");
			descriptionInches.replace(originalLength4,
					descriptionInches.length(), "");
		}
	}

	/**
	 * C9J|#OktAMf8oORTGX6FDVj%HCELzPW9
	 * 
	 * repeated symbols are O, C, and 9 O repeats at 5 and 13 C repeats at 0 and
	 * 25 9 repeats at 1 and 31
	 * 
	 */
	static boolean valid(StringBuffer sb) {
		if (sb.length() > 32) {
			violation("Too long");
			return false; // too long
		}
		if (sb.length() > 13) {
			if (sb.charAt(5) != sb.charAt(13)) {
				violation("O violated");
				return false;
			}
		}
		if (sb.length() > 25) {
			if (sb.charAt(0) != sb.charAt(25)) {
				violation("O and C violated");
				return false;
			}
		}
		if (sb.length() > 31) {
			if (sb.charAt(1) != sb.charAt(31)) {
				violation("O, C and 9 violated");
				return false;
			}
		}
		return true;
	}

	static void violation(String key) {
		Integer val = violationCounts.get(key);
		if (val == null)
			val = 0;
		val++;
		violationCounts.put(key, val);
	}
	
	/* count the number of symbol assignments that match the 408's key.
	 * allows polyphones to contribute a small amount. 
	 * 
	 * C9J|#OktAMf8?ORTGX6FDVj%HCELzPW9
	 * _IFTLNIRWHDA_NGOAOESNBXLT_ETDIEI
     * _W_O_______I______________S____W
     * ___________S____________________ 
	 *  
	 **/
	static float key408(StringBuffer sb) {
		float count = 0f;
		for (int i=0; i<sb.length(); i++) {
			// don't count dupes
			if (i==13 || i==25 || i==31) continue;
			if (sb.charAt(i) == applied408key.charAt(i)) count++;
		}
		if (sb.charAt(1) == 'W') count += 0.25f;
		else if (sb.charAt(1) == 'W') count += 0.25f;
		else if (sb.charAt(3) == 'O') count += 0.25f;
		else if (sb.charAt(11) == 'I') count += 0.25f;
		else if (sb.charAt(11) == 'S') count += 0.25f;
		else if (sb.charAt(26) == 'S') count += 0.25f;
		return count;
		
	}

	public static void main(String[] args) {
		// StringBuffer sb = new StringBuffer("SHITBALLS");
		// sb.replace(4,sb.length(),"");
		// System.out.println(sb);
		search(0);
		search(1);
		
	}
}
