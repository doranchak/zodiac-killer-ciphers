package com.zodiackillerciphers.tests.tonypolito;

import java.util.ArrayList;
import java.util.List;

/** parts of names and phrases that can be in possible solutions */
public class Phrases {
	
	public static List<String> strings = null;
	static int MAX = 18;
	// each suspect has a list of name parts. each name part has a list of
	// variations.
	// it is assumed the first and middle names are in first two positions and
	// can be omitted or abbreviated.
	// it is also assumed any name parts beyond the last name are optional.
	static String[][][] suspects = new String[][][] {
			{ { "ARTHUR", "ART" }, { "LEIGH", "LEE" }, { "ALLEN" } },
			{ { "BRUCE" }, { "" }, { "DAVIS" } },
			{ { "CHARLES", "CHUCK", "CHARLEY" }, { "MILLES" }, { "MANSON" } },
			{ { "DAVID", "DAVE" }, { "ODELL" }, { "MARTIN" } },
			{ { "DONALD", "DON" }, { "LEE" }, { "BUJOK" } },
			{ { "GEORGE" }, { "HILL" }, { "HODEL" }, { "DOCTOR", "DOC", "DR" } },
			{ { "EARL" }, { "VAN" }, { "BEST" }, { "JUNIOR", "JR" } },
			{ { "EDWARD", "ED" }, { "WAYNE" }, { "EDWARDS" } },
			{ { "FREDERIC", "FRED" }, { "STEVEN", "STEVE" }, { "MANALLI" } },
			{ { "GARETH" }, { "SEWELL" }, { "PENN" } },
			{ { "GUY" }, { "WARD" }, { "HENDRICKSON" } },
			{ { "JOHN", "JACK" }, { "WALKER" }, { "TARRANCE" },
					{ "JUNIOR", "JR" } },
			{ { "JOSEPH", "JOE", "JOEY" }, { "NEWTON" }, { "CHANDLER" },
					{ "III" } },
			{ { "KJELL" }, { "" }, { "QVALE" } },
			{ { "LAWRENCE", "LARRY" }, { "" }, { "KANE", "KLEIN" } },
			{ { "LAWRENCE", "LARRY" }, {}, { "BARTON" } },
			{ { "LAWRENCE", "LARRY" }, {}, { "KLEIN" } },
			{ { "LOUIS", "LOUIE" }, { "JOSEPH", "JOE" }, { "MYERS" } },
			{ { "MICHAEL", "MIKE" }, { "HENRY" }, { "OHARE" },
					{ "DOCTOR", "DOC", "DR", "PROFESSOR" } },
			{ { "PETER", "PETE" }, { "STEPHEN", "STEVE" }, { "PLANTE" } },
			{ { "RICHARD", "DICK", "RICH" }, { "JOSEPH", "JOE", "JOEY" },
					{ "GAIKOWSKI", "GAIK", "GYKE" } },
			{ { "RICHARD", "RICK", "RICH" }, { "REED", "REID" }, { "MARSHALL" } },
			{ { "JOSEPH", "JOE", "JOEY" }, { "DON", "DONALD" }, { "DICKEY" } },
			{ { "ROBERT", "BOB", "BOBBY", "ROBBY" }, { "GRAY" }, { "SMITH" } },
			{ { "ROSS" }, { "M" }, { "SULLIVAN" } },
			{ { "THEODORE", "THEO", "TED" }, { "JOHN" }, { "KACZYNSKI" },
					{ "UNABOMBER" } },
			{ { "WILLIAM", "WILL", "BILL", "BILLY", "WILLY" },
					{ "JOSEPH", "JOE", "JOEY" }, { "GRANT" } },
			{ { "ANTHONY", "TONY" }, { "" }, { "POLITO" },
					{ "DOCTOR", "DOC", "DR", "PROFESSOR" } } };

	// a list of possible phrases and words that can precede or follow each name
	static String[] phrases = new String[] { "AKA", "ANNIHILATE",
			"ANNIHILATED", "ANNIHILATEDBY", "ANNIHILATER", "BEWAREOF",
			"CALLME", "COLLECTEDBY", "DEATHBY", "DIDIT", "DUMPEDBY",
			"EXECUTED", "EXECUTEDBY", "GETSAWAY", "GOTAWAY", "HADTOKILL",
			"HASSLAVES", "HELLOIAM", "HEREIS", "HIIAM", "IAM", "ISKILLER",
			"ISMURDERER", "ISREBORN", "ISTHEKILLER", "ISTHEONE", "KILLEDAGAIN",
			"KILLEDBY", "KILLEDEM", "KILLEDTHEM", "KILLER", "KILLSAGAIN",
			"KNOWNAS", "KNOWNAS", "LIKESDEATH", "LIKESKILLING", "LOOKFOR",
			"MISTER", "MR", "MURDERER", "MYNAME", "MYNAMEIS", "NAME", "NAMEIS",
			"REBORN", "SHOOTERIS", "SHOTBY", "SHOTTHEM", "SLAUGHTEREDBY",
			"SLAUGHTERER", "SLAYED", "SLAYEDBY", "SLAYER", "SPEAKING",
			"STRIKESAGAIN", "THEKILLER", "THEMURDERER", "THEREIS", "THEZODIAC",
			"THISIS", "ZODIAC", "ZODIACIS", "ISTHEZODIAC" };

	/** generate all possible 18-character candidate plaintexts */
	public static void generateStrings() {
		strings = new ArrayList<String>();
		for (String[][] suspect : suspects) {
			// all phrases
			String full = "";
			for (String phrase : insertEmptyElement(phrases, false)) {
				if ((phrase).length() > MAX) continue;
				// all first names
				for (String firstName : insertEmptyElement(suspect[0], true)) {
					if ((phrase+firstName).length() > MAX) continue;
					// all middle names
					for (String middleName : insertEmptyElement(suspect[1], true)) {
						if ((phrase+firstName+middleName).length() > MAX) continue;
						//System.out.println(phrase + ", " + firstName + ", " + middleName);
						// all last names
						for (String lastName : suspect[2]) {
							if ((phrase+firstName+middleName+lastName).length() > MAX) continue;
							// all prefixes/suffixes
							if (suspect.length > 3)
								for (String prefix : suspect[3]) {
									if ((phrase+firstName+middleName+lastName+prefix).length() != MAX) continue;
									full = phrase+firstName+middleName+lastName+prefix;
									System.out.println(full + " (" + phrase + " " + firstName + " " + middleName + " " + lastName + " " + prefix + ")");
									strings.add(full);
								}
							else {
								if ((phrase+firstName+middleName+lastName).length() != MAX) continue;
								full = phrase+firstName+middleName+lastName;
								System.out.println(full + " (" + phrase + " " + firstName + " " + middleName + " " + lastName + ")");
								strings.add(full);
							}
						}
					}
				}
			}
		}
		System.out.println("=== Generated " + strings.size() + " strings ===");
	}

	public static List<String> insertEmptyElement(String[] elements, boolean insertInitial) {
		List<String> list = new ArrayList<String>();
		list.add("");
		if (insertInitial && elements.length > 0 && elements[0].length() > 0) list.add(""+elements[0].charAt(0));
		for (String elem : elements) {
			if ("".equals(elem))
				continue;
			list.add(elem);
		}
		return list;
	}
	public static void init() {
		if (strings == null) generateStrings();
	}
	
	public static void main(String[] args) {
		generateStrings();
	}
}