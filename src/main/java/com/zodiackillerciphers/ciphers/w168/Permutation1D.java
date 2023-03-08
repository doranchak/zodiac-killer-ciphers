package com.zodiackillerciphers.ciphers.w168;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.stats.StatsWrapper;

/**
 * Consider stencil: {4,3},{1,2}
 * 
 * It produces the following transposition matrix (each number i is the ith
 * position from the plaintext):
 * 
 * 4 3 8 7 12 11 16 15 20 19 24 23 28 27 32 31 36 35 40 39 44 43 48 47 52 51 56
 * 55 1 2 5 6 9 10 13 14 17 18 21 22 25 26 29 30 33 34 37 38 41 42 45 46 49 50
 * 53 54 60 59 64 63 68 67 72 71 76 75 80 79 84 83 88 87 92 91 96 95 100 99 104
 * 103 108 107 112 111 57 58 61 62 65 66 69 70 73 74 77 78 81 82 85 86 89 90 93
 * 94 97 98 101 102 105 106 109 110 116 115 120 119 124 123 128 127 132 131 136
 * 135 140 139 144 143 148 147 152 151 156 155 160 159 164 163 168 167 113 114
 * 117 118 121 122 125 126 129 130 133 134 137 138 141 142 145 146 149 150 153
 * 154 157 158 161 162 165 166
 * 
 * If you look at the sequence 1, 2, 3, 4, 5, ... 168, the following "distance
 * sequence" shows how to move (in 1D) in the ciphertext from each plaintext
 * letter to its following letter:
 * 
 * 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1,
 * 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28,
 * -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 58, 1,
 * -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30,
 * 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1,
 * 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 58, 1, -28,
 * -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1,
 * -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30,
 * 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1, 30, 1, -28, -1
 * 
 * It is very repetitive.  So, the idea is that stencil-based and other 2D transpositions
 * translate to repetitive 1D transpositions.  Can we find short sequences that
 * tend to produce high-scoring n-grams?
 *
 * Determine the best distance sequence of size n that maximizes the sum of (n+1)gram scores
 * calculated across the entire cipher grid.
 * 
 * This is similar to the "walking a fixed set of holes across the entire cipher grid" idea,
 * but a 1D version.  The 2D stencil version of this might have been too limiting (such as limiting the
 * dimensions of the subgrid).
 *
 */

public class Permutation1D {
	/** distance sequence, or the "moves" we make after each position */
	public int[] sequence;
	/** cipher */
	public StringBuilder[] cipher;
	StringBuilder cipherLine;
	
	static boolean DEBUG = false;
	
	public Permutation1D(int size, StringBuilder[] cipher) {
		this.sequence = new int[size];
		this.cipher = cipher;
		this.cipherLine = StringUtils.toLine(this.cipher);
	}
	
	public int cipherLength() {
		return cipher.length * cipher[0].length();
	}
	
	/** Let n = sequence length.  Let L = cipher length.
	 * Sample (n+1) grams by applying the current sequence starting at every position in the cipher text.
	 * Add up the (n+1) gram scores.
	 * This will produce L (n+1) grams, for a combined length of L*(n+1).
	 * But the cipher has at most L/(n+1) of these (n+1) grams.
	 * So only sum the best L/(n+1) of them.
	 * 
	 * The sequence pattern is likely NOT applicable to the entire transposition (because it is a subsequence
	 * of the full transposition sequence, or is interrupted by different sequences).
	 * So we may want to limit the sum further to only track the top (n+1) grams.
	 * For example, (L/2)/(n+1) of them.  
	 * 
	 * We also need better stats which include periods.
	 * And according to Jeanne, W168 author's other messages never had spaces after periods.
	 * Or, maybe we can adjust ngrams as we sample them, replacing periods with spaces.
	 *  
	 */
	public int score(int top, TreeSet<NGram> topNGrams) {
		topNGrams.clear();
		for (int pos=0; pos<cipherLine.length(); pos++) {
			int pos2 = pos;
			StringBuilder ngram = new StringBuilder();
			ngram.append(cipherLine.charAt(pos));
			Set<Integer> seen = new HashSet<Integer>();
			seen.add(pos2);
			List<Integer> positions = new ArrayList<Integer>();
			positions.add(pos2);
			for (int i=0; i<sequence.length; i++) {
				pos2 += sequence[i];
				if (pos2 < 0) pos2 += cipherLine.length(); 
				pos2 = pos2 % 168; // wrap around
				if (seen.contains(pos2)) return 0;
				seen.add(pos2);
				positions.add(pos2);
				ngram.append(cipherLine.charAt(pos2));
			}
			String ngramStr = ngram.toString().replaceAll("\\.", " "); // replace periods with spaces since our stats don't include periods.
			Double val = NGramsCSRA.valueFor(ngramStr, "EN", true);
			NGram ng = new NGram(ngramStr, val.intValue());
			ng.positions = positions;
			if (DEBUG && val > 0) System.out.println(ng + "	sequence: " + Arrays.toString(sequence));
			manageHeap(topNGrams, ng, top);
		}
		return score(topNGrams);
	}
	
	static int score(TreeSet<NGram> heap) {
		int score = 0;
		for (NGram ngram : heap) {
			score += ngram.score;
		}
		return score;
	}
	
	static void manageHeap(TreeSet<NGram> heap, NGram ngram, int MAX_HEAP_SIZE) {
		// if heap not full, just add it
		if (heap.size() < MAX_HEAP_SIZE) {
			heap.add(ngram);
			//if (feedback) System.out.println(key + " Added1 " + bean);
			//dump(heap, key);
		} else {
			// tree already has this score?  then ignore;
			if (heap.contains(ngram)) {
				;
			} else {
				// is this score better than the worst score? 
				NGram worst = heap.first();
				if (ngram.score > worst.score) { // it's better
					if (!heap.remove(worst)) throw new RuntimeException("ERROR!  CANNOT REMOVE WORST!"); // and remove the worst score
					if (!heap.add(ngram)) {
						//System.out.println(heap);
						throw new RuntimeException("ERROR!  CANNOT ADD SCORE! " + ngram); // so add to heap
					}
					//if (feedback) System.out.println(key + " Added2 " + bean + " Removed " + worst);
					//dump(heap, key);
				}
			}
		}
		
	}
	
	static void testPermutation1D() {
		NGramsCSRA.init();
		StringBuilder[] cipher = StringUtils.toStringBuilder(new String[] { " UNPNNOAALHIT  S  DETP   ISH",
		"KEDEEAR MROFTIUHAIHWOFC AWH ", "PTIOFAI  O BRUNE MNBACEL  ES", " HISO EUG C   BTTATYT CSALTS",
		"AL A DO LLFIHBEOTSOOOITEOTF. ", "  IW RYEIONM NIKTN  UONRTE L" });

//		StringBuilder[] cipher = StringUtils.toStringBuilder(new String[] { "TOWI T A IMYATRYVE D.  SND M",
//				"SP THHECTT  BTE ERYAYONTABYY", "IPNEARY OS ORNHTON SIL NE E ", " HO BELLOESVEIG.  AIMAROTONO",
//				" MPE W SEH FMEWI T ST  T BTO", "FY GSASOMOWORD THHELOONHEOTM" });

		StatsWrapper stats = new StatsWrapper();
		Permutation1D perm = new Permutation1D(1, cipher);
		for (int d1 = 1; d1 < 168; d1++) {
			for (int d2 = 1; d2 < 168; d2++) {
				perm.sequence = new int[] { d1, d2 };
				TreeSet<NGram> topNGrams = new TreeSet<NGram>();
				int score = perm.score(999999, topNGrams);
				stats.addValue(score);
				System.out.println(score + "	" + d1 + " " + d2 + " (" + (d1 - 168) + " "
						+ (d2 - 168) + ")");
//			for (NGram n : topNGrams) 
//				System.out.println(" - " + n);
			}
		}
		stats.output();
	}
	
	/** high scoring sequences found when hillclimbing them for W168 */
	static void testHighScoringW168() {
		NGramsCSRA.init();
		StringBuilder[] cipher = W168.cipherBuilder;
		
		int[][] sequences = new int[][] {
			{72, 157, 46, 143, 167}, // score: 959.0 
			{44, 167, 7, 124, 116}, // score: 959.0 
			{142, 147, 92, 46, 95}, // score: 959.0 
			{116, 10, 167, 147, 167}, // score: 959.0 
			{167, 48, 167, 9, 40}, // score: 960.0 
			{126, 134, 137, 167, 60}, // score: 961.0 
			{90, 26, 66, 147, 167}, // score: 962.0 
			{4, 128, 142, 153, 157}, // score: 962.0 
			{167, 9, 167, 9, 167}, // score: 962.0 
			{10, 48, 44, 87, 167}, // score: 962.0 
			{18, 142, 128, 79, 54}, // score: 964.0 
			{10, 71, 71, 90, 120}, // score: 964.0 
			{26, 66, 147, 167, 167}, // score: 965.0 
			{165, 116, 78, 10, 167}, // score: 965.0 
			{70, 48, 167, 167, 148}, // score: 966.0 
			{7, 40, 95, 38, 54}, // score: 966.0 
			{16, 5, 10, 38, 27}, // score: 967.0 
			{137, 48, 167, 116, 110}, // score: 967.0 
			{67, 43, 167, 120, 34}, // score: 971.0 
			{18, 167, 98, 167, 84}, // score: 971.0 
			{167, 40, 155, 147, 167}, // score: 973.0 
			{4, 165, 101, 32, 78}, // score: 974.0 
			{112, 37, 44, 131, 167}, // score: 974.0 
			{98, 5, 7, 13, 167}, // score: 975.0 
			{6, 75, 167, 134, 100}, // score: 975.0 
			{167, 98, 10, 116, 139}, // score: 976.0 
			{91, 165, 101, 32, 78}, // score: 979.0 
			{10, 102, 10, 167, 2}, // score: 979.0 
			{55, 128, 167, 19, 73}, // score: 980.0 
			{25, 102, 10, 167, 167}, // score: 980.0 
			{137, 48, 167, 116, 60}, // score: 981.0 
			{167, 155, 167, 143, 10}, // score: 982.0 
			{96, 26, 66, 147, 167}, // score: 988.0 
			{112, 98, 167, 167, 37}, // score: 993.0 
			{10, 79, 101, 18, 167}, // score: 1002.0 
			{49, 35, 79, 167, 167}, // score: 1007.0 
			{117, 7, 147, 167, 134}, // score: 1011.0 
			{92, 44, 167, 7, 124}, // score: 1014.0 
			{157, 26, 167, 53, 1}, // score: 1020.0 
			{125, 159, 167, 167, 25}, // score: 1024.0 
			{137, 48, 167, 32, 110}, // score: 1046.0 
			{10, 74, 167, 167, 114}, // score: 1054.0 
			{15, 104, 27, 101, 32} // score: 1062.0 
		};
		for (int[] sequence : sequences) {
			Permutation1D perm = new Permutation1D(5, cipher);
			perm.sequence = sequence;
			DEBUG = true;
			TreeSet<NGram> topNGrams = new TreeSet<NGram>();
			int score = perm.score(Integer.MAX_VALUE, topNGrams);
			System.out.println(score);
		}
	}
	
	public String toString() {
		return Arrays.toString(sequence);
	}
	
	public static void main(String[] args) {
		//testPermutation1D();
		testHighScoringW168();
	}
}
