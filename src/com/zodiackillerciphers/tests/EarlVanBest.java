package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.names.Census;
import com.zodiackillerciphers.names.Name;
import com.zodiackillerciphers.names.NameType;

/**
 * based on Gary Stewart's claims that "EARL VAN BEST JUNIOR" can be found a
 * letter at a time among the columns of the 340.
 */
public class EarlVanBest {
	static int W;
	static int H;
	static Map<Character, String> translations;
	static String[] cipher;
	static {
		translations = new HashMap<Character, String>();
		translations.put('A', "A789^");
		translations.put('B', "Bb");
		translations.put('C', "Cc");
		translations.put('D', "Dd");
		translations.put('E', "Ee12");
		translations.put('F', "Ff");
		translations.put('G', "G");
		translations.put('H', "H");
		translations.put('I', "I!:;/\\|");
		translations.put('J', "Jj");
		translations.put('K', "Kk=");
		translations.put('L', "Ll|/\\");
		translations.put('M', "M");
		translations.put('N', "N?^");
		translations.put('O', "O()z0123456#%*@_.");
		translations.put('P', "P&p");
		translations.put('Q', "Qpq");
		translations.put('R', "Rr");
		translations.put('S', "S");
		translations.put('T', "T+[tj");
		translations.put('U', "UV");
		translations.put('V', "V");
		translations.put('W', "W");
		translations.put('X', "X");
		translations.put('Y', "Yy");
		translations.put('Z', "Zz");
	}
	
	static List<EarlVanBestResult> searchResults;
	
	/** map column number to all plaintext letters that can be "seen" in that column. */ 
	//static Map<Integer, Set<Character>> map; 

	public static void initSearch(String[] cipher) {
		EarlVanBest.cipher = cipher;
		WordFrequencies.init();
		H = cipher.length;
		W = cipher[0].length();
		/*
		map = new HashMap<Integer, Set<Character>>();
		for (int col=0; col<cipher[0].length(); col++) {
			map.put(col, new HashSet<Character>());
			for (int row=0; row<cipher.length; row++) {
				char ch = cipher[row].charAt(col);
				String tr = translations.get(ch);
				if (tr == null) continue;
				for (int i=0; i<tr.length(); i++) 
					map.get(col).add(tr.charAt(i));
			}
		}
		
		for (Integer key : map.keySet()) {
			String val = key + ": ";
			for (Character ch : map.get(key)) {
				val += ch;
			}
			//System.out.println(val);
		}*/
	}
	
	public static void test() {
		initSearch(Ciphers.grid(Ciphers.cipher[0].cipher, 17));
		
		
		for (String word : WordFrequencies.map.keySet()) {
			search(word, true);
		}
	}
	
	public static void nameSearch() {
		initSearch(Ciphers.grid(Ciphers.cipher[0].cipher, 17));
		int matches = 0; int count = 0;
		while (true) {
			List<Name> name = Census.buildName(17, 5000);
			String full = "";
			for (Name n : name) full += n.name;
			if (search(full, false)) {
				System.out.println(Census.dump(name) + " after " + count + " tries");
				matches++;
			}
			count++;
		}
	}
	public static void femaleNameSearch() {
		initSearch(Ciphers.grid(Ciphers.cipher[0].cipher, 17));
		int matches = 0; int count = 0;
		while (true) {
			List<Name> name = Census.buildNameFemale(-1, 5000);
			String full = "";
			for (Name n : name) full += n.name;
			if (search(full, false)) {
				System.out.println(Census.dump(name));
				matches++;
				if (matches > 100000) return;
			}
			count++;
		}
	}
	
	static List<int[]> clone(List<int[]> list) {
		if (list == null) return null;
		List<int[]> result = new ArrayList<int[]>();
		for (int[] rc : list) {
			result.add(new int[] {rc[0], rc[1]});
		}
		return result;
	}
	// word: word to search for
	// index: current position within word
	// path: current path under consideration
	// row: current row
	// col: current col
	// drow: how many rows to change in each search step
	// dcol: how many columns to change in each search step
	// 
	// returns true if the search is successful.  otherwise false.
	static boolean search2(String word, int index, List<int[]> path, int row, int col, int drow, int dcol, boolean print) {
		if (word.length() == index) {
			searchResults.add(new EarlVanBestResult(word, clone(path)));
			// success
			if (print) System.out.println(word.length() + ", " + WordFrequencies.freq(word) + ", "  + word + ", (" + path.get(0)[0]+","+path.get(0)[1] + "), " + drow + ", " + dcol);
			return true; // if we made it this far, a complete match was found.
		}
		
		// check for out of bounds.  
		if (row >= cipher.length) return false; 
		if (row < 0) return false;
		if (col >= cipher[0].length()) return false;
		if (col < 0) return false;
		
		char letter = word.charAt(index);
		String symbols = translations.get(letter);
		if (symbols == null) return false; // there are no interpretations for this plaintext letter.  should not occur.
		
		boolean bycol = dcol != 0; // are we checking by row or column?
		
		String strip = "";
		if (bycol) 
			for (int r = 0; r < cipher.length; r++) 
				strip += cipher[r].charAt(col);
		else
			for (int c = 0; c < cipher[row].length(); c++) 
				strip += cipher[row].charAt(c);
		// does the strip of cipher symbols contain any of the symbols that resemble the current letter?
		for (int i=0; i<symbols.length(); i++) {
			char ch = symbols.charAt(i);
			int ind = strip.indexOf(ch);
			if (ind > -1) { // a match!
				// recurse to find more matches of the current word.
				int p = path.size();
				
				if (bycol) {
					path.add(new int[] {ind, col});
				} 
				else {
					path.add(new int[] {row, ind});
				}
				
				if (search2(word, index+1, path, row + drow, col + dcol, drow, dcol, print)) return true;
 				path.remove(path.size()-1); // remove most recent path entry so we can backtrack
			}
		}
		return false;
	}
	
	public static String fix(String word) {
		if (word == null) return null;
		word = word.toUpperCase();
		String newWord = "";
		for (int i=0; i<word.length(); i++) {
			char ch = word.charAt(i);
			if (ch >= 'A' && ch <= 'Z') newWord += ch;
		}
		return newWord;
	}
	public static boolean search(String word, boolean print) {
		if (word.length() < 1) return false;
		word = fix(word);
		searchResults = new ArrayList<EarlVanBestResult>();
		// horizontal searches
		for (int col=0; col<cipher[0].length(); col++) {
			if (search2(word, 0, new ArrayList<int[]>(), 0, col, 0, 1, print)) return true; // start from the left
			if (search2(word, 0, new ArrayList<int[]>(), 0, cipher[0].length()-(col+1), 0, -1, print)) return true; // start from the right
		}
		// vertical searches
		for (int row=0; row<cipher.length; row++) {
			if (search2(word, 0, new ArrayList<int[]>(), row, 0, 1, 0, print)) return true; // start from the top
			if (search2(word, 0, new ArrayList<int[]>(), cipher.length-(row+1), 0, -1, 0, print)) return true; // start from the bottom
		}
		return false;
	}
	
	
	
	
	/*
	public static void findWord(String word) {
		for (int i=0; i<W; i++) findWord(word.toUpperCase(), 0, i, 1);
		for (int i=0; i<W; i++) findWord(word.toUpperCase(), 0, 16-i, -1);
	}
	public static void findWord(String word, int index, int col, int step) {
		if (col >= W) return;
		if (col < 0) return;
		if (index >= word.length()) return;
		if (index < 0) return;
		
		char ch = word.charAt(index);
		if (map.get(col).contains(ch)) {
			
			if (word.length() == index + 1) {
				// success
				System.out.println(word.length() + ", " + WordFrequencies.freq(word) + ", "  + word +", " + step);
			}
			findWord(word, index+1, col + step, step);
		}
		
	}*/
	
	public static void process(String filePath) {
		Census.init();
		if (filePath == null) return;
		List<String> lines = FileUtil.loadFrom(filePath);
		for (String line : lines) {
			String[] split = line.split(" ");
			int num = 0; float score = 0; boolean last = true;
			String full = "";
			for (int i=split.length-4; i>0; i--) {
				String s = split[i];
				if ("after".equals(s)) break;
				if (s.length() < 1) continue;
				full = s + " " + full;
				score += Census.score(s, last ? NameType.LAST : NameType.FIRSTMALE);
				num++;
			}
			System.out.println(score/num + " " + full);
		}
	}
	public static void process2(String filePath) {
		Census.init();
		if (filePath == null) return;
		List<String> lines = FileUtil.loadFrom(filePath);
		for (String line : lines) {
			String[] split = line.split(" ");
			int num = 0; float score = 0; boolean first = true;
			String full = "";
			String link = "<a href=\"http://zodiackillerciphers.com/word-search-gadget-earlvanbest/index.html?c=0&w=";
			for (int i=1; i<split.length; i++) {
				String s = split[i];
				if (first) first = false;
				else {
					full += " ";
					link += "%20";
				}
				full += s;
				link += s;
			}
			link += "\" target=\"_new\">" + full + "</a><br>";
			System.out.println(link);
		}
	}
	
	public static void main(String[] args) {
		//test();
		//nameSearch();
		//femaleNameSearch();
		process2("/Users/doranchak/projects/zodiac/all-earl-female-sorted.txt");
	}
}
