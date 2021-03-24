package com.zodiackillerciphers.tests;

import java.util.HashSet;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/** Zach Epstein's idea   https://www.facebook.com/schmeh/posts/3237882686236268 
 * http://scienceblogs.de/klausis-krypto-kolumne/2020/04/27/an-unsolved-cryptogram-on-a-martin-gardner-book-cover/?fbclid=IwAR3RkHW7OCTAeOEpH3izb_N3y4tIoAWANhqTQZreYmgBQK89r8C4GYFKz5M
 **/

public class MartinGardnerCover {
	static String[] grid = new String[] {"NDSG","CTER","TWAP","RHDN"};
	static int H = grid.length;
	static int W = grid[0].length();
	// directions 
	static int[][] steps = new int[][] {
		{1,0}, // S
		{1,1}, // SE
		{0,1}, // E
		{-1,1}, // NE
		{-1,0}, // N
		{-1,-1}, // NW
		{0,-1}, // W
		{1,-1} // SW
	};
	// directions for knight
	static int[][] stepsKnight = new int[][] {
		{-2, 1},
		{-1, 2},
		{1, 2},
		{2, 1},
		{2, -1},
		{1, -2},
		{-1, -2},
		{-2, -1}
	};

	/* search for words.  start at any position.  then allow any chess move that can be performed by a knight, 
	 * rook, or bishop.
	 */
	public static void search() {
		Set<String> found = new HashSet<String>();
		//WordFrequencies.init(WordFrequencies.WORDS6, -1, false);
		WordFrequencies.init();
		for (int row=0; row<H; row++) {
			for (int col=0; col<W; col++) {
				System.out.println("Searching " + row+" " + col);
				search(row, col, new StringBuffer(), found);
			}
		}
	}
	// returns false only if we go out of bounds 
	public static boolean search(int row, int col, StringBuffer sb, Set<String> found) {
		//System.out.println("  - " + row + "," + col + " " + sb);
		if (row < 0 || row >= H || col < 0 || col >= W)
			return false; // out of bounds
		char ch = grid[row].charAt(col);
		String prefix = sb.toString() + ch;
		if (!WordFrequencies.hasPrefix(prefix)) return true; // no more words can be found with this prefix
		if (WordFrequencies.hasWord(prefix) && prefix.length() > 2) {
			// found a word
			if (!found.contains(prefix)) {
				System.out.println(prefix.length() + "	" + WordFrequencies.percentile(prefix) + "	" + prefix + "	" + letterCount(prefix));
				found.add(prefix);
			}
		}
		sb.append(ch);
		// keep exploring all possible chess moves from here.
		// rook:
		for (int r=row+1; r<H; r++) search(r, col, sb, found);
		for (int r=row-1; r>=0; r--) search(r, col, sb, found);
		for (int c=col+1; c<W; c++) search(row, c, sb, found);
		for (int c=col-1; c>=0; c--) search(row, c, sb, found);
		// bishop and rook moves:
		for (int[] step : steps) {
			int c=col; int r=row;
			while (true) {
				r = r + step[0];
				c = c + step[1];
				boolean result = search(r, c, sb, found);
				if (!result) break; // don't stop searching along this direction unless we go out of bounds
			}
		}
		// knight moves:
		for (int[] step : stepsKnight) {
			int c=col; int r=row;
			r = r + step[0];
			c = c + step[1];
			search(r, c, sb, found);
		}
		// no more can be found with this character
		sb.deleteCharAt(sb.length()-1);
		return true;
	}
	public static String letterCount(String word) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<word.length(); i++) set.add(word.charAt(i));
		return set.size() + "	" + set;
	}
	public static void main(String[] args) {
		search();
	}
}
