/** hunt for strings in caesar-shifted matrix, a la Kite and AK Wilks */
package com.zodiackillerciphers.ak;
                               
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.tests.LetterFrequencies;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
public class AKSearch {

	static {
		//WordFrequencies.init("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/all.num.o5");
	}
	/** generate shifted grid for the given line and shift values */
	public static String[] gridFor(String line, int[] shifts) {
		String[] grid = new String[shifts.length];
		
		int pos = 0;
		for (int shift : shifts) {
			String shifted = "";
			for (int i=0; i<line.length(); i++) {
				char ch = line.toLowerCase().charAt(i);
				int val = ch - 97 + shift;
				while (val < 0) val += 26;
				val %= 26;
				val += 97;
				shifted += (char) val;
			}
			grid[pos++] = shifted;
		}
		return grid;
	}

	
	public static int search(String[] grid, int[] shifts, String search, boolean firstOnly) {
		return search(grid, shifts, search, firstOnly, true, null);
	}
	/** search for the given string in the given caesar matrix.  show all matches.
	 * @grid grid of shifted strings to search
	 * @shifts the shift group used
	 * @search the string to search for
	 * @firstOnly if true, then exit when first possible match is found
	 * @contains if not null, then only count matches that exactly contain the given string 
	 *  */
	public static int search(String[] grid, int[] shifts, String search, boolean firstOnly, boolean printMatch, String contains) {
		String searchLower = search.toLowerCase();
		
		//System.out.println("Searching for: " + search);
		
		boolean[] selected = new boolean[grid[0].length()];

		StringBuffer found = new StringBuffer();
		List<Integer> fs = new ArrayList<Integer>();
		for (int i=0; i<grid[0].length(); i++) {
			found.append(" ") ;    
			fs.add(null);
		}
		return search(grid, shifts, selected, search, search, found, fs, new HashSet<String>(), firstOnly, printMatch, contains);
	}
	public static int search(String[] grid, int[] shifts, boolean[] selected, String search, String suffix, StringBuffer found, List<Integer> foundShifts, Set<String> output, boolean firstOnly, boolean printMatch, String contains) {
//		System.out.println("Suffix: " + suffix);
		int total = 0;
		if ("".equals(suffix)) { // if we made it this far, we found a match
			String s = "";
//			for (Integer i : foundShifts) s += i + " ";
			Line line = line(found, foundShifts, shifts);
			if (output.contains(line.w1)) return 0;
			if (contains == null || line.w1.contains(contains)) {
				output.add(line.w1);
				if (printMatch) System.out.println(line);
				return 1;
			}
			return 0;
		}
//		for (int i=0; i<suffix.length(); i++) {
			char ch = suffix.charAt(0);
//			System.out.println("Looking for " + ch);
			// search for all available occurrences of ch.  then recursively search for substrings of the search term until the full search is found.
			for (int row=0; row<grid.length; row++) {
				for (int col=0; col<grid[0].length(); col++) {
									   if (search.length() == suffix.length()) {
//											System.out.println("Row " + row + " Col " + col);
										}
				   if (selected[col]) {
					   ;
				} else {
				   char g = grid[row].charAt(col);
				   if (g == ch) {                 
    					//System.out.println("found " + g + " at col " + col + " row " + row + " (shift " + shifts[row] + ")");
				   		selected[col] = true;
						found.setCharAt(col, g);
						foundShifts.set(col, shifts[row]);
						total += search(grid, shifts, selected, search, suffix.substring(1), found, foundShifts, output, firstOnly, printMatch, contains);
						if (total > 0 && firstOnly) return total;
						selected[col] = false;
						found.setCharAt(col, ' ');
						foundShifts.set(col, null);
				   }
				}
				}
			}
//		}
		return total;
	}
	public static Line line(StringBuffer found, List<Integer> foundShifts, int[] shifts) {
		Line line = new Line();
		//String s = "Match: ";
		String w1 = ""; String w2 = "";
		for (int i=0; i<found.length(); i++) {
			char ch = found.charAt(i);
			w1 += ch;
			if (ch != ' ') 
				w2 += ch;
		}
		line.w1 = w1;
		line.w2 = w2;
		line.foundShifts = "";
		for (Integer i : foundShifts) line.foundShifts += i + " ";
		line.shifts = "";
		for (int i=0; i<shifts.length; i++) line.shifts += shifts[i] + (i<shifts.length-1 ? "," : "");
		return line;
	}
	
	public static float score(List<int[]> positions) {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		int maxCount = 0;
		for (int i=1; i<positions.size(); i++) {
			int key = positions.get(i)[0] - positions.get(i-1)[0];
			Integer val = counts.get(key);
			if (val == null) val = 0;
			val++;
			maxCount = Math.max(maxCount, val);
			counts.put(key, val);
		}
		return ((float)maxCount)/(positions.size()-1);
	}
	
	public static String dump(List<int[]> positions) {
		String d = "";
		for (int[] pos : positions) d += "(" + pos[0] + " " + pos[1] + ") ";
		return d;
	}
	/* find all possible words of the given length whose letters appear in order in the given grid */
	public static void wordSearch(String[] grid, int L) {
		if (grid[0].length() < L) throw new RuntimeException("Grid not wide enough");
		System.out.println("=== Search for words of length " + L + " ===");
		Totals totals = new Totals();
		wordSearch(grid, L, 0, 0, new StringBuffer(), new ArrayList<int[]>(), totals);
		System.out.println("Total positions: " + totals.totalPositions);
		System.out.println("Total words: " + totals.totalWords);
		
	}
	
	static void wordSearch(String[] grid, int L, int row, int col, StringBuffer word, List<int[]> positions, Totals totals) {
		if (col == L) {
			totals.totalPositions++;
			int freq = WordFrequencies.freq(word.toString());
			if (freq > 0) {
				totals.totalWords++;
				System.out.println(dump(positions) + ", " + score(positions) + ", " + word + ", " + freq);
			}
			return;
		}
		for (int r = 0; r < grid.length; r++) {
			word.append(grid[r].charAt(col));
			positions.add(new int[] {r, col});
			wordSearch(grid, L, r, col+1, word, positions, totals);
			positions.remove(positions.size()-1);
			word.deleteCharAt(word.length()-1);
		}
	}
	public static void print(String[] grid) {
		for (String g : grid) System.out.println(g);
	}
	public static void test() {
		int[] shifts = new int[] {-9, -6, -3, 0, 3, 6, 9};
		String[] grid = gridFor("ebeorietemethhpiti", shifts);
		int total = search(grid, shifts, "theodorejkaczynski", true);
//		int total = search(grid, shifts, "mikehenryohare");
		System.out.println("Total matches: " + total);
		

		int[] shifts1 = new int[] {-3, -2, -1, 0, 1, 2, 3};
		int[] shifts2 = new int[] {-6, -4, -2, 0, 2, 4, 6};
		int[] shifts3 = new int[] {-9, -6, -3, 0, 3, 6, 9};
		int[] shifts4 = new int[] {-12, -8, -4, 0, 4, 8, 12};
		int[] shifts5 = new int[] {-15, -10, -5, 0, 5, 10, 15};
		int[] shifts6 = new int[] {-18, -12, -6, 0, 6, 12, 18};
		int[] shifts7 = new int[] {-21, -14, -7, 0, 7, 14, 21};
		String[] letterPool = null; //LetterFrequencies.letterPool();
		for (int i=0; i<1000; i++) {                        
			String word = LetterFrequencies.randomWord(18,letterPool);
    		/*grid = gridFor(word, shifts1);
			for (String g : grid) System.out.println(g);
			total = search(grid, shifts1, "theodorejkaczynski", true);
 		   	System.out.println("Total matches: " + total);
    		grid = gridFor(word, shifts2);
			for (String g : grid) System.out.println(g);
			total = search(grid, shifts2, "theodorejkaczynski", true);
 		   	System.out.println("Total matches: " + total);*/
    		grid = gridFor(word, shifts3);
			for (String g : grid) System.out.println(g);
			total = search(grid, shifts3, "theodorejkaczynski", true);
 		   	System.out.println("Total matches: " + total);
    		/*grid = gridFor(word, shifts4);
			for (String g : grid) System.out.println(g);
			total = search(grid, shifts4, "theodorejkaczynski", true);
 		   	System.out.println("Total matches: " + total);
    		grid = gridFor(word, shifts5);
			for (String g : grid) System.out.println(g);
			total = search(grid, shifts5, "theodorejkaczynski", true);
 		   	System.out.println("Total matches: " + total);
    		grid = gridFor(word, shifts6);
			for (String g : grid) System.out.println(g);
			total = search(grid, shifts6, "theodorejkaczynski", true);
 		   	System.out.println("Total matches: " + total);
    		grid = gridFor(word, shifts7);
			for (String g : grid) System.out.println(g);
			total = search(grid, shifts7, "theodorejkaczynski", true);
 		   	System.out.println("Total matches: " + total);*/
		}
	}
	
	public static void words() {
		int[] shifts = new int[] {-12, -9, -6, -3, 0, 3, 6, 9, 12};
		
		String[] grid = gridFor("blue", shifts);
		print(grid);
		for (int L=3; L<5; L++)
			wordSearch(grid, L);
		
	}
	public static void print(String word, int index) {
		String s = "";
		for (int i=0; i<index; i++) s += " ";
		s += word;
		System.out.println(s);
	}
	
	static StringBuffer sb(int L) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<L+10; i++) sb.append(" ");
		return sb;
	}
	
	static void placeWord(StringBuffer sb, String word, int index) {
		//System.out.println("place word " + word + " at " + index + " in " + sb);
		for (int i=0; i<word.length(); i++) {
			char ch = word.charAt(i);
			sb.setCharAt(index+i, ch);
		}
		//System.out.println("done place word " + word + " at " + index + " in " + sb);
	}
	
	static boolean fit(StringBuffer sb, String word, int index) {
		for (int i=0; i<(word.length()+1) && (i+index)<sb.length(); i++) {
			if (sb.charAt(index+i) != ' ') return false;
		}
		return true;
	}
	
	static StringBuffer fit(List<StringBuffer> words, String word, int index) {
		StringBuffer sb = null;
		for (StringBuffer line : words) {
			if (fit(line, word, index)) return line;
		}
		return sb;
	}
	
	static void add(int L, List<StringBuffer> words, String word, int index) {
		boolean add = false;
		StringBuffer sb = fit(words, word, index);
		if (sb == null) {
			sb = sb(L);
			add = true;
		}
		placeWord(sb, word, index);
		if (add) words.add(sb(L));
	}
	
	public static void wordsLinear() {
		//int[] shifts = new int[] {-30,-20,-10,0,10,20,30};
		int[] shifts = new int[] {-12,-9,-6,-3,0,3,6,9,12};
		//int[] shifts = new int[] {-10,-8,-6,-4,-2,0,2,4,6,8,10};
//		int[] shifts = new int[] {0};
		
		//String[] grid = gridFor("herceanbigivethemhelltoobtsaltesehlseiluehstheolhsseeanamebweollrseseilllfmiapillsgaemrnpaodemagpcettoalstbneushblleithesefoolsshallseemtilklereplsaassdlaublnsloeatplsdulraaleitalektisoetarfieataillllplaessolhiapltnmrahphneaeaklballlslsveeseaecbueadlilwllstoenleithertleaeatlpaslihellhsalsioshtatheipgmstallsaoledacithhegsleomaisnl", shifts);
		//String[] grid = gridFor("lnsiamoelsgehhticadeloasllatsmgpiehtathsoislashllehilsapltaeaeltrehtielneotsllwlildaeubceaeseevslslllablkaeaenhpharmntlpaihlossealplllliataeifrateositkelatielaarludslptaeolsnlbualdssaaslperelklitmeesllahssloofesehtiellbhsuenbtslaottecpgamedoapnrmeagsllipaimfllliesesrlloewbemanaeesshloehtsheulieslhesetlastbootllehmehtevigibnaecreh", shifts);
		String[] grid = gridFor("itiphhtemeteiroebeefilretfaymrofsevalsfognitcellocympotsronwodiolsotyrtlliwuoyesuacebemanymuoyevigtonlliwisevalsymemoceblliwdellikevahiehtlladnsecidarapninrobereblliwieidinehweahtaitifotraptsebehtlrigahtiwffoskcorruoygnittegnahtrettebnevesitiecnerepxegnillirhttaomehtemseviggnihtemosllikotllafolamanaeutregnadtaomehtsinamesuacebtserrofehtniemagdliwgnilliknahtnuferomsitinufhcumossitiesuacebelpoepgnillikekili", shifts);
		print(grid);
		
		List<StringBuffer> words = new ArrayList<StringBuffer>();
		
		int shift = 0;
		for (String line : grid) {
			System.out.println("Shift line " + (shifts[shift] >= 0 ? "+" : "") + shifts[shift] );  
			System.out.println(line);
			for (String word : WordFrequencies.map.keySet()) {
				if (word.length() < 3) continue;
				word = word.toLowerCase();
				if (line.contains(word)) {
					int i = line.indexOf(word);
					while (i>-1) {
						//System.out.println(shift + ", " + word.length() + ", " + word + ", " + WordFrequencies.freq(word) + ", " + i);
						add(line.length(), words, word+"[" + WordFrequencies.freq(word) + "]", i);
						//print(word, i);
						i = line.indexOf(word, i+1);
					}
				}
			}
			for (StringBuffer sb : words) System.out.println(sb);
			System.out.println(" ");
			System.out.println(" ");
			words.clear();
			shift++;
		}
		
	}
	
	public static void robison() {
		//int[] shifts = new int[] {-9, -6, -3, 0, 3, 6, 9};
		//String[] grid = gridFor("ebeorietemethhpiti", shifts);
		//int total = search(grid, shifts, "theodorejkaczynski", false);
//		int total = search(grid, shifts, "mikehenryohare");
		//System.out.println("Total matches: " + total);
		String[] letterPool = null;//LetterFrequencies.letterPool();
		//for (int k=0; k<200000; k++) {
//			if (k > 0 && k% 1000 == 0) System.out.println(k+"...");
			//String word = LetterFrequencies.randomWord(9, letterPool);
			//System.out.println(word);
			//word = "kaczynski";
		//String word = "dennisrader";
		WordFrequencies.init();
		int count=0;
		for (String word : WordFrequencies.map.keySet()) {
			count++;
			if (count % 1000 == 0) System.out.println("count " + count);
			if (word.length() > 5 && word.length() < 10) {
				
				word = word.toLowerCase();
			for (int i=1; i<26; i++) {
				int[] shifts = new int[] {-3*i, -2*i, -i, 0, i, 2*i, 3*i};
				String[] grid = gridFor(word, shifts);
				//System.out.println(i+"=======");
				//print(grid);
				int total = search(grid, shifts, "zodiac", true, false, "zodiac");
				total += search(grid, shifts, "zodius", true, false, "zodius");
				total += search(grid, shifts, "guidini", true, false, "guidini");
				if (total == 3) { 
				System.out.println(" word " + word + " shift multiple: " + i);
					//System.out.println("ALL THREE " + total);
				//}
				}
			}
			
		}
		}
			
		//}
	}
	
	public static void robisonWordSearch() {
		int i=10;
		int[] shifts = new int[] {-3*i, -2*i, -i, 0, i, 2*i, 3*i};
		String word = "zanderkite";
		String[] grid = gridFor(word, shifts);
		print(grid);
		
		int total;
		for (String wordToTry : WordFrequencies.map.keySet()) {
			if (wordToTry.length() >= 6) {
				wordToTry = wordToTry.toLowerCase();
				total = search(grid, shifts, wordToTry, true, true, wordToTry);
				if (total > 0) System.out.println(WordFrequencies.freq(wordToTry) + ","+ wordToTry);
			}
		}
		
	}
	
	public static void robisonPrint(String word, String wordToTry, int i) {
		int[] shifts = new int[] {-3*i, -2*i, -i, 0, i, 2*i, 3*i};
		String[] grid = gridFor(word, shifts);
		print(grid);
		search(grid, shifts, wordToTry, true, true, wordToTry);
		
	}
	
	public static void main(String[] args) {
		words();
		//wordsLinear();
		//robison();
		//robisonWordSearch();
		
		
		//String[] words = {"guidini","zodius", "zodiac"};
		/*
		String[] words = {"gasped", "quiver", "torsos"};

		String[] others = {"kaczynski"};
		for (String other : others) {
			System.out.println(" == " + other + " == ");
			for (String word: words) robisonPrint(other, word, 10);
			//for (String word: words) robisonPrint(other, word, 16);
		}*/

		
		
	
	}
	
	public static class Totals {
		public int totalPositions = 0;
		public int totalWords = 0;
		
	}
	
	public static class Line {
		String w1; // includes spaces (nulls)
		String w2; // all spaces (nulls) removed
		String foundShifts;
		String shifts;
		public String toString() {
			String s = "Match: ";
			s += w1;
			if (!w1.equals(w2)) s += " (" + w2 + ")";
			s += ", Shifts: " + foundShifts;
			s += ", Group: " + shifts;
			return s;
		}
		
	}
}