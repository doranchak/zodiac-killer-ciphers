package com.zodiackillerciphers.ciphers.w168.stenciltest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.ciphers.w168.W168;
import com.zodiackillerciphers.lucene.NGramsCSRA;

/** Variation of the stencil idea.  This time the order is implied by the order of holes. 
 * And we will explore the space of all possible selections of a number of holes (H) within
 * an NxN subgrid.
 * 
 * The idea: Which "shape" of H holes, when scanned across the entire cipher grid,
 * produces the best H-gram scores?
 *
 * Let H = 4
 * There are (168 * 167 * 166 * 165) = 768,453,840 ways to pick 4 holes from the 168 positions.
 * To sample all the 4-grams, treat the holes as offsets, and allow them to wrap over cipher boundaries.
 * Maybe we can find shapes that yield higher 4-gram scores, which might point towards the correct 
 * encryption scheme. 
 * 
 * So to test this, we need to scan 768,453,840 stencil shapes, each one over 168 positions which gives
 * 129,100,245,120 total 4-grams to measure.
 * 
 * We can narrow the search by limiting the height/width of the stencil shape to some fixed value.  
 *  
 * @author doranchak
 *
 */
public class Stencil {
	public static float BEST_MARGIN = 0.9f; // print results that are at least this percentage of the best-so-far result.
	public int[] holes; // H holes.  each hole is defined by a position number.  it is an exact position within the cipher grid.  [0,167] 
	public StringBuilder[] cipher;
	public int maxWidth; // maximum width of stencil shape
	public int maxHeight; // maximum height of stencil shape
	
	// stateful variables
	public int currentHole;
	public Set<Integer> positions; // track every position occupied by a hole
	public int L;
	public long total;
	public Date dateStart;
	public Date dateEnd;
	public float bestFreq;
	public boolean countOnly;
	// list of positions for the characters forming the current ngram being scored
	public List<int[]> ngramPositions;
	// "hotspot" grid (zkscore for an ngram is added to each position it covers)
	double[][] hotspots;
	public boolean alwaysZkscore; // optionally force use of zkscore
	public float freqSum;
	
	public Stencil(int H, StringBuilder[] cipher, int maxHeight, int maxWidth) {
		holes = new int[H];
		this.cipher = cipher;
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
		this.L = cipher.length * cipher[0].length();
	}
	
	// returns total number of possible stencils for the given number of holes for this cipher
	public long spaceSize() {
		long size = 1;
		int dimensions = cipher.length * cipher[0].length();
		for (int i=0; i<holes.length; i++) {
			size *= dimensions - i;
		}
		return size;
	}
	/** search for all ordered selections of H holes */ 
	public void search() {
		this.dateStart = new Date();
		this.currentHole = 0;
		this.positions = new HashSet<Integer>();
		this.total = 0;
		searchIteration();
		System.out.println("Total selections possible: " + spaceSize());
		System.out.println("Total selections found: " + this.total);
		this.dateEnd = new Date();
		float rate = this.dateEnd.getTime() - this.dateStart.getTime(); // ms
		rate /= 1000; // seconds
		System.out.println("Elapsed: " + rate + " seconds.");
		rate = this.total / rate; // stencils per second
		System.out.println("Rate: " + rate + " stencils per second.");
	}
	
	void searchIteration() {
		// bounds check
		//System.out.println("ITERATION: " + out());
		if (tooBig()) {
			//System.out.println(" - too big!");
			return;
		}
		// check for success condition
		if (currentHole >= holes.length) {
			successAction(true, true);
			return;
		}
		for (int pos=0; pos<L; pos++) {
			if (positions.contains(pos)) continue; // already occupied by a hole
			holes[currentHole] = pos;
			positions.add(pos);
			currentHole++;
			searchIteration();
			currentHole--;
			positions.remove(pos);
		}
	}
	public void successAction(boolean printNgrams, boolean bestCheck) {
		this.total++;
		if (countOnly) return;

		hotspots = new double[cipher.length][cipher[0].length()];
		// sample the H-grams at all positions in the cipher grid by using all possible stencil offsets.
		// whenever a hole goes out of bounds, wrap it around to the other side (modulo operation).
		// sum the log frequencies of all sampled H-grams.
		// track the top results.
		freqSum = 0;
		StringBuilder dump = new StringBuilder();
		for (int rowOffset = 0; rowOffset < cipher.length; rowOffset++) {
			for (int colOffset = 0; colOffset < cipher[0].length(); colOffset++) {
				StringBuilder ngram = ngram(rowOffset, colOffset);
				double val = 0;
				if (ngram.length() > 6 || alwaysZkscore) {
					// too long so use the zkscore
					val = NGramsCSRA.zkscore(new StringBuffer(ngram), "EN", true);
					//System.out.println("val " + val + " ngram " + ngram);
				} else {
					val = NGramsCSRA.valueFor(ngram.toString(), "EN", true);
				}
				
				// track hotspots by observing max 4-gram scores found
				for (int i=0; i<ngram.length()-3; i++) {
					String fourGram = ngram.substring(i, i+4);
					// update val just for this fourGram
					val = NGramsCSRA.valueFor(fourGram, "EN", true);
					for (int j=i; j<i+4; j++) {
						int[] rc = ngramPositions.get(j);
						double valCurrent = hotspots[rc[0]][rc[1]];
						hotspots[rc[0]][rc[1]] = Math.max(valCurrent, val);
					}
				}
				freqSum += val;
				dump.append(val).append("	").append(ngram).append("\n");
			}
		}
		if (bestCheck) {
			if (freqSum > bestFreq) {
				bestFreq = freqSum;
				System.out.println(freqSum + " NEW BEST, HOLES: " + out());
				if (printNgrams) {
					System.out.println("Ngrams:");
					System.out.println(dump);
				}
			} else if (freqSum >= BEST_MARGIN*bestFreq) {
				System.out.println(freqSum + " CLOSE TO BEST, HOLES: " + out());
				if (printNgrams) {
					System.out.println("Ngrams:");
					System.out.println(dump);
				}
			}
		}
		//System.out.println("freqSum " + freqSum);
	}
	
	StringBuilder ngram(int rowOffset, int colOffset) {
		if (ngramPositions == null) 
			ngramPositions = new ArrayList<int[]>();
		ngramPositions.clear();
		StringBuilder ngram = new StringBuilder();
		for (int pos : holes) {
			int row = toRow(pos);
			int col = toCol(pos);
			row = (row + rowOffset) % cipher.length;
			col = (col + colOffset) % cipher[0].length();
			char ch = cipher[row].charAt(col);
			ngramPositions.add(new int[] {row,col});
			ngram.append(ch);
		}
		//System.out.println("ngram: " + ngram);
		return ngram;
	}
	StringBuilder ngramInfo(int rowOffset, int colOffset) {
		StringBuilder ngram = new StringBuilder();
		StringBuilder ngramInfo = new StringBuilder();
		for (int pos : holes) {
			int row = toRow(pos);
			int col = toCol(pos);
			row = (row + rowOffset) % cipher.length;
			col = (col + colOffset) % cipher[0].length();
			char ch = cipher[row].charAt(col);
			ngram.append(ch);
			ngramInfo.append(ch + " (" + row + "," + col+") ");
		}
		ngram.append(" ").append(ngramInfo);
		return ngram;
	}
	
	StringBuilder out() {
		StringBuilder sb = new StringBuilder();
		int minCol = Integer.MAX_VALUE;
		int minRow = Integer.MAX_VALUE;
		for (int i=0; i<=currentHole && i<holes.length; i++) {
			int pos = holes[i];
			sb.append(pos);
			int row = toRow(pos);
			int col = toCol(pos);
			minCol = Math.min(minCol, col);
			minRow = Math.min(minRow, row);
			sb.append(" (").append(row).append(",").append(col).append(") ");
		}
		sb.append("Pattern: "); // stencil pattern relative to (0,0)
		for (int i=0; i<=currentHole && i<holes.length; i++) {
			int pos = holes[i];
			int row = toRow(pos) - minRow;
			int col = toCol(pos) - minCol;
			sb.append(" (").append(row).append(",").append(col).append(") ");
		}
		return sb;
	}
	int toRow(int pos) {
		return pos / cipher[0].length();
	}
	int toCol(int pos) {
		return pos % cipher[0].length();
	}
	boolean tooBig() {
		if (holes == null || holes.length < 2) return false;
		int minRow = Integer.MAX_VALUE;
		int maxRow = Integer.MIN_VALUE;
		int minCol = Integer.MAX_VALUE;
		int maxCol = Integer.MIN_VALUE;
		for (int i=0; i<currentHole; i++) {
			int pos = holes[i];
			int row = toRow(pos);
			int col = toCol(pos);
			minRow = Math.min(minRow, row);
			minCol = Math.min(minCol, col);
			maxRow = Math.max(maxRow, row);
			maxCol = Math.max(maxCol, col);
			
			//System.out.println(row + " " + col + " " + minRow + " " + minCol + " " + maxRow + " " + maxCol);
			
			if (maxRow-minRow+1 > maxHeight) {
//				System.out.println("TOO HIGH " + (maxRow-minRow+1));
//				System.out.println(out());
				return true;
			}
			if (maxCol-minCol+1 > maxWidth) {
//				System.out.println("TOO WIDE " + (maxCol-minCol+1));
//				System.out.println(out());
				return true;
			}
		}
		return false;
	}
	public static void testTooBig() {
		Stencil s = new Stencil(4, W168.cipherBuilder, 2, 2);
		s.holes = new int[] {
				1
		};
		System.out.println(s.tooBig());
		s.holes = new int[] {
				1, 2
		};
		System.out.println(s.tooBig());
		s.holes = new int[] {
				1, 2, 29
		};
		System.out.println(s.tooBig());
		s.holes = new int[] {
				1, 2, 29, 30
		};
		System.out.println(s.tooBig());
		
	}
	
	public static void testHoles() {
		int[][] holesAll = new int[][] { 
			//{ 111, 110, 109, 105, 106, 107 }, // 770.0, Pattern: (0,6) (0,5) (0,4) (0,0)
																			// (0,1) (0,2)};
			//	{ 6, 5, 4, 0, 1, 2 }, // 547.0, Pattern: (0,6) (0,5) (0,4) (0,0) (0,1) (0,2)
				//{ 5, 9, 8, 35, 37, 33 }, // 654.0, Pattern: (0,0) (0,4) (0,3) (1,2) (1,4) (1,0)
				//{ 3, 13, 0, 10, 9, 8, 7, 6, 5, 1, 2, 12, 11, 4 },
				//{ 4, 3, 13, 12, 11, 7, 6, 5, 20, 2, 1, 0, 10, 9, 8, 18, 17, 16, 15, 14, 19 },
				//{ 5, 4, 1, 29, 28, 32, 31, 13, 12, 11, 20, 19, 18, 0, 27, 26, 23, 2, 3, 10, 9, 30, 22, 21, 25, 24, 35,
						//34, 33, 40, 41, 38, 37, 16, 15, 8, 7, 6, 39, 14, 17, 36 },
				//{33, 59, 1, 0, 32, 29, 28, 3, 2, 34, 62, 61, 60, 6, 5, 4, 58, 57, 56, 31, 30},
				{34, 33, 30, 29, 28, 32, 31, 6, 5, 4, 3, 2, 1, 0}, // best hillclimbed 2x7
				{33, 59, 1, 0, 32, 29, 28, 3, 2, 34, 62, 61, 60, 6, 5, 4, 58, 57, 56, 31, 30}, // best hillclimbed 3x7
				{37, 36, 33, 32, 11, 10, 29, 41, 28, 7, 6, 5, 2, 9, 8, 35, 34, 13, 31, 30, 39, 38, 1, 0, 40, 4, 3, 12} // best hillclimbed 2x14
				};
		for (int[] holes : holesAll) {
			Stencil st = new Stencil(holes.length, W168.cipherBuilder, 2, 7);
			st.holes = holes;
			st.currentHole = holes.length;
			System.out.println("CURRENT STENCIL: " + st.out());
			float freqSum = 0;
			for (int rowOffset = 0; rowOffset < st.cipher.length; rowOffset++) {
				for (int colOffset = 0; colOffset < st.cipher[0].length(); colOffset++) {
					StringBuilder ngram = st.ngram(rowOffset, colOffset);
					//System.out.println("ngram " + ngram);
					double val = NGramsCSRA.zkscore(new StringBuffer(ngram), "EN", true);
					freqSum += val;
					System.out.println(val + "	" + st.ngramInfo(rowOffset, colOffset));
				}
			}
			System.out.println("SUM: " + freqSum);
			// print out data for heat map
			st.alwaysZkscore = false;
			st.successAction(false, false);
			System.out.println("HEATMAP DATA");
//			for (int row=0; row<st.hotspots.length; row++) {
//				for (int col=0; col<st.hotspots[0].length; col++) {
//					System.out.println(col + " " + (0-row) + " " + st.hotspots[row][col]);
//				}
//			}
			
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for (int col = 0; col < st.hotspots[0].length; col++) {
				for (int row = st.hotspots.length-1; row >= 0; row--) {
					double val = st.hotspots[row][col];
					System.out.println("{group: '" + col + "', variable: '" + row + "', value: '" + val + "'},");
					min = Math.min(min, val);
					max = Math.max(max, val);
				}
			}
			System.out.println("]; var domainMin = " + min + ";");
			System.out.println("var domainMax = " + max + ";");

		}
	}
	
	public static void testStencilSearch() {
//		Stencil st = new Stencil(4, new StringBuilder[] { new StringBuilder("ABCDEFGH"), new StringBuilder("ABCDEFGH"),
//				new StringBuilder("ABCDEFGH") }, 2, 2);
		Stencil st = new Stencil(7, W168.cipherBuilder, 2, 7);
		st.countOnly = false;
//		Stencil st = new Stencil(4, W168.toStringBuilder(W168.Z408_1), 3, 3);
		st.search();
	}
	static void testScore() {
		Stencil st = new Stencil(4, W168.cipherBuilder, 2, 7);
		st.holes = new int[] {0,1,2,3};
		st.successAction(false, true);
	}
	static void init() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");		
	}
	public String toString() {
		return out().toString();
	}
	public static void main(String[] args) {
		init();
//		testStencilSearch();
//		testTooBig();
		testHoles();
//		testScore();
	}
}
