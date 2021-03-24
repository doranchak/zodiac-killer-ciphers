package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.names.Census;
import com.zodiackillerciphers.names.Name;

/** Lapumo (aka Sean O'Brien) found "LEE ALLEN IL" anagrammed in a column of the Melvin Belli letter */
public class LapumoTest {
	public static String[] letter = new String[] { 
			"DEARMELVIN", 
			"THISISTHEZODIACSPEAKING", 
			"IWISHYOUAHAPPYCHRISTMASS",
			"THEONETHINGIASKOFYOUISTHIS", 
			"PLEASEHELPME", 
			"ICANNOTREACHOUTBECAUSEOFTHISTHINGINMEWONTLETME",
			"IAMFINDINGITEXTREAMLYDIFICULTTOKEEPINCHECKIAMAFRAIDIWILLLOOSECONTROLAGAINANDTAKEMYNINETHANDPOSIBLYTENTHVICTOM",
			"PLEASEHELPMEIAMDROWNDING",
			"ATTHEMOMENTTHECHILDRENARESAFEFROMTHEBOMBBECAUSEITISSOMASSIVETODIGINANDTHETRIGERMECHREQUIRESSOMUCHWORKTOGETITADJUSTEDJUSTRIGHT",
			"BUTIFIHOLDBACKTOOLONGFROMNONINEIWILLLOOSECOMPLETALLCONTROOLOFMYSELFANDSETTHEBOMBUP",
			"PLEASEHELPMEICANNOTREMAININCONTROLFORMUCHLONGER" };

	public static List<String> names;
	static {
		// ARTHUR
		// LEE
		// LEIGH
		// ALLEN
		names = new ArrayList<String>();
		names.add("LEEALLEN");
		names.add("LEELEIGH");
		names.add("ARTHURLEE");
		names.add("ALALLEN");
		names.add("ARTALLEN");
		names.add("ZODIAC");
		names.add("BOBSMITH");
		names.add("JOESMITH");
		names.add("JAMESLEE");
		names.add("ALVARADO");
		names.add("ANDERSON");
		names.add("BENJAMIN");
		names.add("BRADFORD");
		names.add("BUCHANAN");
		names.add("CALDWELL");
		names.add("CAMPBELL");
		names.add("CASTILLO");
		names.add("CHAMBERS");
		names.add("CHANDLER");
		names.add("CLARENCE");
		names.add("CLIFFORD");
		names.add("COPELAND");
		names.add("COURTNEY");
		names.add("CRAWFORD");
		names.add("CUMMINGS");
		names.add("DAVIDSON");
		names.add("DOMINICK");
		names.add("EMMANUEL");
		names.add("ERICKSON");
		names.add("ESPINOZA");
		names.add("FERGUSON");
		names.add("FERNANDO");
		names.add("FIGUEROA");
		names.add("FLETCHER");
		names.add("FRANKLIN");
		names.add("FREDRICK");
		names.add("GEOFFREY");
		names.add("GILBERTO");
		names.add("GONZALES");
		names.add("GONZALEZ");
		names.add("GREGORIO");
		names.add("GRIFFITH");
		names.add("GUERRERO");
		names.add("HAMILTON");
		names.add("HARRISON");
		names.add("HOLLOWAY");
		names.add("HUMBERTO");
		names.add("JENNINGS");
		names.add("JEREMIAH");
		names.add("JERMAINE");
		names.add("JOHNSTON");
		names.add("JONATHAN");
		names.add("JONATHON");
		names.add("LAURENCE");
		names.add("LAWRENCE");
		names.add("LEONARDO");
		names.add("MARSHALL");
		names.add("MARTINEZ");
		names.add("MATTHEWS");
		names.add("MCCARTHY");
		names.add("MCDANIEL");
		names.add("MCDONALD");
		names.add("MCKENZIE");
		names.add("MCKINNEY");
		names.add("MITCHELL");
		names.add("MORRISON");
		names.add("NICHOLAS");
		names.add("PETERSON");
		names.add("PHILLIPS");
		names.add("RANDOLPH");
		names.add("REGINALD");
		names.add("REYNALDO");
		names.add("REYNOLDS");
		names.add("RICHARDS");
		names.add("ROBERSON");
		names.add("ROBINSON");
		names.add("RODERICK");
		names.add("SALVADOR");
		names.add("SANDOVAL");
		names.add("SANTIAGO");
		names.add("SAUNDERS");
		names.add("SCHWARTZ");
		names.add("SHEPHERD");
		names.add("STEPHENS");
		names.add("STERLING");
		names.add("SULLIVAN");
		names.add("TERRANCE");
		names.add("TERRENCE");
		names.add("THEODORE");
		names.add("THOMPSON");
		names.add("THORNTON");
		names.add("TOWNSEND");
		names.add("WHITAKER");
		names.add("WILFREDO");
		names.add("WILLIAMS");
		names.add("DICKGAIK");
		names.add("RICHGAIK");
		names.add("DICKGYKE");
		names.add("RICHGYKE");
		names.add("RICHARDG");
		names.add("KACZYNSKI");
		names.add("EDWARDS");
		names.add("EEDWARDS");
		names.add("LARRYKANE");
		names.add("MIKEOHARE");
		names.add("BRUCEDAVIS");
		names.add("RSULLIVAN");
		names.add("ROSSMERCER");
		names.add("KJELLQVALE");
		names.add("GSAXBERG");
		names.add("RHUFFMAN");
		names.add("DONBUJOK");
		names.add("BJWYSLING");
		names.add("BWYSLING");
		names.add("KENAMMAN");
		names.add("WENERSON");
		names.add("JAMESOWEN");
	}
	
	public static void search() {
		WordFrequencies.init();
		Census.init(5000);
		int max = 0;
		for (String line : letter)
			max = Math.max(max, line.length());

		for (int i = 0; i < max; i++) {
			String column = "";
			for (String line : letter) {
				if (i < line.length())
					column += line.charAt(i);
			}
			if (column.length() < 5)
				continue;
			System.out.println("COLUMN: " + column);
			for (String word : WordFrequencies.map.keySet()) {
				if (word.length() > column.length())
					continue;
				if (word.length() < 3)
					continue;
				if (Anagrams.anagram(word, column)) {
					int diff = column.length() - word.length();
					int freq = WordFrequencies.freq(word);
					float score = ((float) freq) / (diff + 1);
					System.out.println(word.length() + "	" + score + "	" + freq + "	" + diff + "	" + word
							+ "	" + column + "	WORD_MODE");
				}
			}

			/** names */
			List<Name> firstAndLast = new ArrayList<Name>();
			firstAndLast.addAll(Census.firstMale);
			firstAndLast.addAll(Census.last);
			for (Name first : Census.firstMale) {
				for (Name last : firstAndLast) {
					String name = first.name + last.name;
					if (name.length() > column.length())
						continue;
					if (Anagrams.anagram(name, column)) {
						int diff = column.length() - name.length();
						System.out.println(name.length() + "	" + diff + "	" + first.name + " " + last.name
								+ "	" + column + "	NAME_MODE");
					}
				}
			}
			for (Name first : Census.firstMale) {
				String name = first.name;
				if (name.length() > column.length())
					continue;
				if (Anagrams.anagram(name, column)) {
					int diff = column.length() - name.length();
					System.out.println(
							name.length() + "	" + diff + "	" + first.name + "	" + column + "	NAME_FIRST_MODE");
				}
			}
			for (Name last : Census.last) {
				String name = last.name;
				if (name.length() > column.length())
					continue;
				if (Anagrams.anagram(name, column)) {
					int diff = column.length() - name.length();
					System.out.println(
							name.length() + "	" + diff + "	" + last.name + "	" + column + "	NAME_LAST_MODE");
				}
			}
		}
	}
	
	public static void scrambleTest(int width, int height, int shuffles) {
		String[] grid = new String[height];
		
		int hits1 = 0;
		int hits2 = 0;
		int hits = 0;
		for (int i=0; i<shuffles; i++) {
			for (int r=0; r<height; r++) {
				grid[r] = "";
				for (int c=0; c<width; c++) {
					grid[r] += LetterFrequencies.randomLetter().toUpperCase(); 
				}
			}
			
			boolean found = false;
			for (String row : grid) {
				if (Anagrams.anagram("LEEALLEN", row)) {
					found = true;
					hits++;
					hits1++;
				}
				if (Anagrams.anagram("LEELEIGH", row)) {
					found = true;
					hits++;
					hits2++;
				}
			}
		}
		System.out.println(hits + " " + hits1 + " " + hits2);
	}
	
	public static void corpusTest() {
		String text = FileUtil
				.loadSBFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg135.txt").toString();
		text = FileUtil.convert(text).toString();

		List<String> lines = null;
		int H=10;
		for (int W=15; W<=15; W++) {
			for (int pos=0; pos<text.length()-H*W; pos+=W*H) {
				lines = new ArrayList<String>();
				for (int row=0; row<H; row++) {
					String sub = text.substring(pos+row*W, pos+row*W+W);
					lines.add(sub);
				}
				check(H, W, pos, lines);
			}
		}
	}
	
	public static void check(int H, int W, int pos, List<String> lines) {
		boolean found = false;
		String matches = "";
		for (int col=0; col<lines.get(0).length(); col++) {
			String column = "";
			for (int row=0; row<lines.size(); row++) {
				column += lines.get(row).charAt(col);
			}
			for (String name : names) {
				if (Anagrams.anagram(name, column)) {
					found = true;
					matches += "H " + H + " W" + W + " column " + (col+1) + " (" + column + "): " + name + "\r\n";
				}
			}
		}
		if (found) {
			System.out.println("Matches at position " + pos + ":");
			System.out.println();
			for (String line : lines) System.out.println(line);
			System.out.println();
			System.out.println(matches);
			System.out.println();
		}
	}

	public static void main(String[] args) {
		//search();
//		scrambleTest(10, 18, 100000);
		corpusTest();
	}
}
