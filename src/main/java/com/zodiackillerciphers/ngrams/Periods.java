package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.w168.W168;
import com.zodiackillerciphers.homophones.SymbolMergeTest;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.Vectors;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.MeasurementsBean;
import com.zodiackillerciphers.transform.Operations;

/** rewrite cipher text using different periods.  jarlve observed that "period 19" ngrams
 * have the highest counts (that is, symbols with 19 positions between them).
 */
public class Periods {

	/** generate count by ngram of the given cipher, where each ngram has the given period */
	public static PeriodBean applyPeriodOBSOLETE(String cipher, int n, int period, boolean countOnly) {
		// made it obsolete, because i think the rewrite method is more consistent with what jarlve and smokie are doing 
		PeriodBean bean = new PeriodBean();
		bean.period = period;
		bean.n = n;
		bean.rewritten = "";
		for (int i=0; i<cipher.length(); i++) {
			String key = "";
			Set<Integer> seen = null;
			if (!countOnly) seen = new HashSet<Integer>(); // track positions to avoid duplicate locations within an ngram
			for (int j=0; j<n; j++) {
				if (j == 0) { 
					if (!countOnly && seen.contains(i)) break;
					key += cipher.charAt(i);
					if (!countOnly) seen.add(i);
				}
				else {
					int pos = (i+j*(period+1)) % cipher.length();
					if (!countOnly && seen.contains(pos)) break;
					key += cipher.charAt(pos);
					if (!countOnly) seen.add(pos);
				}
			}
			if (key.length() == n) {
				Integer val = bean.counts.get(key);
				if (val == null) val = 0;
				val++;
				bean.counts.put(key, val);
				
				if (val > 1) {
					// track positions involved with repeats
					if (!countOnly) {
						bean.positions.add(i);
						for (int j=1; j<n; j++) bean.positions.add((i+j*(period+1)) % cipher.length());
					}
				}
			}
		}
		return bean;
	}
	
	public static double score(Map<Character, Integer> counts, int reps, String ngram) {
		double prob = 1;
		for (int i=0; i<ngram.length(); i++) {
			char key = ngram.charAt(i);
			prob *= ((double)counts.get(key))/340;
		}
		prob = Math.pow(prob, reps-1);
		System.out.println(prob + " " + ngram + " " + reps);
		return prob;
	}
	
	public static void testPeaks() {
		int sum = 0;
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		
		for (int i=0; i<1000000; i++) {
			String cipher = CipherTransformations.shuffle(Ciphers.cipher[0].cipher);
			int n = 2;
			int max = 0;
			for (int p=1; p<=170; p++) {
				String rewritten = rewrite3(cipher, p);
				NGramsBean bean = new NGramsBean(n, rewritten);
				//System.out.println(bean.numRepeats() + "	" + i + "	" + bean.repeatsInfo());
				max = Math.max(max, bean.numRepeats());
			}

			Integer val = counts.get(max);
			if (val == null) val = 0;
			val++;
			counts.put(max, val);
			sum += max;
			if (i % 1000 == 0) {
				System.out.println(new Date() + ": " + i+"...");
				System.out.println(counts);
				//System.out.println(sum/(i+1));
			}
			
		}
		
		System.out.println(sum/1000000);

		System.out.println("Final counts:");
		System.out.println(counts);
		
	}
	
	/** Measure fragFaster for all periods of Z340  
	 * 
	 * http://www.zodiackillersite.com/viewtopic.php?p=44818#p44818 */
	public static void testForJarlve() {
		String cipher = Ciphers.cipher[1].cipher.substring(0,340);
		for (int i=1; i<=cipher.length()/2; i++) {
			String rewritten = rewrite3(cipher, i);
			float frag = RepeatingFragmentsFaster.measure(rewritten);
			System.out.println(i + "	" + rewritten + "	" + frag);
		}
	}
	
	public static void test(String cipher, boolean countOnly) {
		//String cipher = Ciphers.cipher[0].cipher;
		//String cipher = "HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+";
		//String cipher = "d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>"; // z340 flipped horizontally
		//Operations o = new Operations();
		int n = 2;
		for (int i=1; i<=cipher.length()/2; i++) {
			String rewritten = rewrite3(cipher, i);
			//PeriodBean bean = Periods.applyPeriod(cipher, n, i, countOnly);
			//System.out.println(bean.toString(true));
			NGramsBean bean = new NGramsBean(n, rewritten);
			
			String line = i + "	" + bean.numRepeats() + "	" + bean.count() + "	";
			//line += RepeatingFragmentsFaster.measure(rewritten, false, false) + "	" + RepeatingFragmentsFaster.measure(rewritten, true, false) + "	";
			line += "=(\"" + bean.repeatsInfo() + "\")	=(\"" + rewritten + "\")";
			System.out.println(line);
			//System.out.println(bean.numRepeats() + "	" + i + "	" + bean.repeatsInfo());
			//System.out.println(RepeatingFragmentsFaster.measure(rewritten, true, false) + " frag");
			/*if (bean.numRepeats() > 0) {
				System.out.println(Arrays.toString(o.measure(rewritten)) + " " + rewritten);
			}*/
		}
		Map<Character, Integer> counts = Ciphers.countMap(Ciphers.cipher[0].cipher);
		
		/*
		score(counts, 4, "p+");
		score(counts, 3, "G+");
		score(counts, 3, "<S");
		score(counts, 3, "+4");
		score(counts, 3, "(+");
		score(counts, 3, "#2");
		score(counts, 2, "|T");
		score(counts, 2, "|<");
		score(counts, 2, "z6");
		score(counts, 2, "k.");
		score(counts, 2, "^D");
		score(counts, 2, "YA");
		score(counts, 2, "Xz");
		score(counts, 2, "TB");
		score(counts, 2, "PY");
		score(counts, 2, "OF");
		score(counts, 2, "N:");
		score(counts, 2, "MF");
		score(counts, 2, "D(");
		score(counts, 2, "BO");
		score(counts, 2, ";+");
		score(counts, 2, "9^");
		score(counts, 2, ".L");
		score(counts, 2, "-R");
		score(counts, 2, "+|");
		score(counts, 2, "+l");
		score(counts, 2, "+k");
		score(counts, 2, "+c");
		score(counts, 2, "+B");
		score(counts, 2, "*5");*/
	}
	
	/**
	 * generate periodic transposition matrix for cipher of height H, width W, and
	 * the given row and col offsets. wraps around.
	 */
	public static int[][] transpositionMatrix(int H, int W, int rowOffset, int colOffset) {
		Set<Integer> seen = new HashSet<Integer>();
		int[][] matrix = new int[H][W];
		int rowNew = 0; int colNew = 0;
		for (int row=0; row<H; row++) {
			matrix[row] = new int[W];
			for (int col=0; col<W; col++) {
				matrix[row][col] = ((rowNew+H)%H) * W + (colNew+W)%W;
				if (seen.contains(matrix[row][col])) return null;
				seen.add(matrix[row][col]);
				colNew  = (W + colNew + colOffset) % W;
				rowNew = (H + rowNew + rowOffset) % H;
			}
		}
		return matrix;
	}
	
	/** transposition matrix for diagonal-variations/vertical_3split_decimations/z340_vertical_3split_decimations_9_9_1_19_2.txt */
	public static int[][] transpositionMatrixSamBlake1() {
		return new int[][] {
			{0, 19, 38, 57, 76, 95, 114, 133, 152, 1, 20, 39, 58, 77, 96, 115, 134},
			{136, 2, 21, 40, 59, 78, 97, 116, 135, 137, 3, 22, 41, 60, 79, 98, 117},
			{119, 138, 4, 23, 42, 61, 80, 99, 118, 120, 139, 5, 24, 43, 62, 81, 100},
			{102, 121, 140, 6, 25, 44, 63, 82, 101, 103, 122, 141, 7, 26, 45, 64, 83},
			{85, 104, 123, 142, 8, 27, 46, 65, 84, 86, 105, 124, 143, 9, 28, 47, 66},
			{68, 87, 106, 125, 144, 10, 29, 48, 67, 69, 88, 107, 126, 145, 11, 30, 49},
			{51, 70, 89, 108, 127, 146, 12, 31, 50, 52, 71, 90, 109, 128, 147, 13, 32},
			{34, 53, 72, 91, 110, 129, 148, 14, 33, 35, 54, 73, 92, 111, 130, 149, 15},
			{17, 36, 55, 74, 93, 112, 131, 150, 16, 18, 37, 56, 75, 94, 113, 132, 151},

			{153, 172, 191, 210, 229, 248, 267, 286, 305, 154, 173, 192, 211, 230, 249, 268, 287},
			{289, 155, 174, 193, 212, 231, 250, 269, 288, 290, 156, 175, 194, 213, 232, 251, 270},
			{272, 291, 157, 176, 195, 214, 233, 252, 271, 273, 292, 158, 177, 196, 215, 234, 253},
			{255, 274, 293, 159, 178, 197, 216, 235, 254, 256, 275, 294, 160, 179, 198, 217, 236},
			{238, 257, 276, 295, 161, 180, 199, 218, 237, 239, 258, 277, 296, 162, 181, 200, 219},
			{221, 240, 259, 278, 297, 163, 182, 201, 220, 222, 241, 260, 279, 298, 164, 183, 202},
			{204, 223, 242, 261, 280, 299, 165, 184, 203, 205, 224, 243, 262, 281, 300, 166, 185},
			{187, 206, 225, 244, 263, 282, 301, 167, 186, 188, 207, 226, 245, 264, 283, 302, 168},
			{170, 189, 208, 227, 246, 265, 284, 303, 169, 171, 190, 209, 228, 247, 266, 285, 304},

			{306, 308, 310, 312, 314, 316, 318, 320, 322, 307, 309, 311, 313, 315, 317, 319, 321},
			{323, 325, 327, 329, 331, 333, 335, 337, 339, 324, 326, 328, 330, 332, 334, 336, 338}
		};
	}

	/** Final transposition matrix M for the Z340 solution.
	 * M[r][c] = p
	 * r,c => row and column of resulting transposition
	 * p => position from original cipher  
	 **/
	public static int[][] transpositionMatrixZ340Solution() {
		return new int[][] { 
				{ 0, 19, 38, 57, 76, 95, 114, 133, 152, 1, 20, 39, 58, 77, 96, 115, 134 },
				{ 136, 2, 21, 40, 59, 78, 97, 116, 135, 137, 3, 22, 41, 60, 79, 98, 117 },
				{ 119, 138, 4, 23, 42, 61, 80, 99, 118, 120, 139, 5, 24, 43, 62, 81, 100 },
				{ 102, 121, 140, 6, 25, 44, 63, 82, 101, 103, 122, 141, 7, 26, 45, 64, 83 },
				{ 85, 104, 123, 142, 8, 27, 46, 65, 84, 86, 105, 124, 143, 9, 28, 47, 66 },
				{ 68, 87, 106, 125, 144, 10, 29, 48, 67, 69, 88, 107, 126, 145, 11, 30, 49 },
				{ 51, 70, 89, 108, 127, 146, 12, 31, 50, 52, 71, 90, 109, 128, 147, 13, 32 },
				{ 34, 53, 72, 91, 110, 129, 148, 14, 33, 35, 54, 73, 92, 111, 130, 149, 15 },
				{ 17, 36, 55, 74, 93, 112, 131, 150, 16, 18, 37, 56, 75, 94, 113, 132, 151 },

				// LIFE IS fix only:
//				{ 153, 172, 191, 210, 229, 248, 267, 286, 305, 154, 173, 192, 211, 230, 249, 268, 287 },
//				{ 289, 155, 174, 193, 212, 231, 250, 269, 288, 290, 156, 175, 194, 213, 232, 251, 270 },
//				{ 272, 291, 157, 176, 195, 214, 233, 252, 271, 273, 292, 158, 177, 196, 215, 234, 253 },
//				{ 255, 274, 293, 159, 178, 197, 216, 235, 254, 256, 275, 294, 160, 179, 198, 217, 236 },
//				{ 238, 257, 276, 295, 161, 180, 199, 218, 237, 239, 258, 277, 296, 162, 181, 200, 219 },
//				{ 221, 240, 259, 278, 297, 163, 182, 201, 220, 222, 241, 260, 279, 298, 183, 202, 204, },
//				{ 223, 242, 261, 280, 299, 184, 203, 205, 224, 243, 262, 281, 300, 185, 187, 206, 225, },
//				{ 244, 263, 282, 301, 186, 188, 207, 226, 245, 264, 283, 302, 170, 189, 208, 227, 246, },
//				{ 265, 284, 303, 171, 190, 209, 228, 247, 266, 285, 304, 164, 165, 166, 167, 168, 169 },
				
				{ 153, 172, 191, 210, 229, 247, 267, 286, 305, 154, 173, 192, 211, 230, 248, 268, 287 },
				{ 289, 155, 174, 193, 212, 231, 249, 269, 288, 290, 156, 175, 194, 213, 232, 250, 270 },
				{ 272, 291, 157, 176, 195, 214, 233, 251, 271, 273, 292, 158, 177, 196, 215, 234, 252 },
				{ 255, 274, 293, 159, 178, 197, 216, 235, 253, 256, 275, 294, 160, 179, 198, 217, 236 },
				{ 238, 257, 276, 295, 161, 180, 199, 218, 237, 239, 258, 277, 296, 162, 181, 200, 219 },
				{ 221, 240, 259, 278, 297, 163, 182, 201, 220, 222, 254, 260, 279, 298, 183, 202, 204, },
				{ 223, 241, 261, 280, 299, 184, 203, 205, 224, 242, 262, 281, 300, 185, 187, 206, 225, },
				{ 243, 263, 282, 301, 186, 188, 207, 226, 244, 264, 283, 302, 170, 189, 208, 227, 245, },
				{ 265, 284, 303, 171, 190, 209, 228, 246, 266, 285, 304, 164, 165, 166, 167, 168, 169 },

				// shift of full row 6
//				{ 153, 172, 191, 210, 229, 247, 267, 286, 305, 154, 173, 192, 211, 230, 248, 268, 287 },
//				{ 289, 155, 174, 193, 212, 231, 249, 269, 288, 290, 156, 175, 194, 213, 232, 250, 270 },
//				{ 272, 291, 157, 176, 195, 214, 233, 251, 271, 273, 292, 158, 177, 196, 215, 234, 252 },
//				{ 255, 274, 293, 159, 178, 197, 216, 235, 253, 256, 275, 294, 160, 179, 198, 217, 236 },
//				{ 254, 257, 276, 295, 161, 180, 199, 218, 237, 238, 258, 277, 296, 162, 181, 200, 219 },
//				{ 221, 239, 259, 278, 297, 163, 182, 201, 220, 222, 240, 260, 279, 298, 183, 202, 204, },
//				{ 223, 241, 261, 280, 299, 184, 203, 205, 224, 242, 262, 281, 300, 185, 187, 206, 225, },
//				{ 243, 263, 282, 301, 186, 188, 207, 226, 244, 264, 283, 302, 170, 189, 208, 227, 245, },
//				{ 265, 284, 303, 171, 190, 209, 228, 246, 266, 285, 304, 164, 165, 166, 167, 168, 169 },
				
				{ 309, 308, 307, 306, 310, 311, 312, 313, 315, 314, 317, 316, 318, 319, 320, 321, 324 },
				{ 323, 322, 326, 325, 334, 333, 332, 331, 330, 329, 328, 327, 335, 336, 337, 338, 339 } };
	}	
	
	/** translate position from z340 to transposed (solved/readable) position */
	public static Map<Integer, Integer> mapPositionFromZ340;
	/** translate transposed (solved/readable) position back to original position in z340 */
	public static Map<Integer, Integer> mapPositionToZ340;
	
	static {
		mapPositionFromZ340 = new HashMap<Integer, Integer>();
		mapPositionToZ340 = new HashMap<Integer, Integer>();
		
		int pos1 = 0; // translated from z340
		int pos2 = 0; // translated to z340
		for (int row=0; row<transpositionMatrixZ340Solution().length; row++) {
			for (int col=0; col<transpositionMatrixZ340Solution()[row].length; col++) {
				pos1 = transpositionMatrixZ340Solution()[row][col];
				mapPositionFromZ340.put(pos2, pos1);
				mapPositionToZ340.put(pos1, pos2);
				pos2++;
			}
		}
		System.out.println("mapPositionFromZ340: " + mapPositionFromZ340);
		System.out.println("mapPositionToZ340: " + mapPositionToZ340);
		// test: transposed z340 plaintext should untranspose to the readable solution
//		String result = "";
//		String pt = Ciphers.Z340_SOLUTION_TRANSPOSED;
//		for (int i=0; i<pt.length(); i++) {
//			result += pt.charAt(mapPositionFromZ340.get(i));
//		}
//		System.out.println(result);
		// test: readable solution should transpose back to the original z340 layout
//		result = "";
//		pt = Ciphers.Z340_SOLUTION_UNTRANSPOSED;
//		for (int i=0; i<pt.length(); i++) {
//			result += pt.charAt(mapPositionToZ340.get(i));
//		}
//		System.out.println(result);
	}
	
	
	
	/** untranspose, using modulo 340 method */
	public static String rewrite2(String cipher, int n) {
		//String cipher = Ciphers.cipher[0].cipher;
		
		int length = cipher.length();
		Set<Integer> seen = new HashSet<Integer>();
		String result = "";
		
		int pos = 0;
		int count = 0;
		while (true) {
			if (count == length) break;
			pos %= 340;
			if (seen.contains(pos)) {
				pos++;
				continue;
			}
			result += cipher.charAt(pos);
			seen.add(pos);
			pos += n;
			count++;
		}
		
		return result;
		
		
	}
	/** untranspose, using a "grid movement" method */
	public static void rewrite(String cipher) {
		//String cipher = Ciphers.cipher[0].cipher;
		String[] grid = Ciphers.grid(cipher, 17);
		
		boolean[][] visited = new boolean[20][17];
		for (int row=0; row<20; row++) { 
			visited[row] = new boolean[17];
			for (int col=0; col<17; col++) {
				visited[row][col] = false;
			}
		}
		
		int row = 0;
		int col = 0;
		String result = "";
		while (true) {
			if (row < 0) row += 20;
			if (col < 0) col += 17;
			row = row % 20;
			col = col % 17;
			if (visited[row][col]) break;
			result += grid[row].charAt(col);
			visited[row][col] = true;
			row++; col-=3; // period 14
		}
		
		System.out.println(result);
		System.out.println(result.length());
		
		
	}
	
	public static boolean showMapping(int pos) {
		
		// pivots in horizontally flipped z340
		int[] pivots = new int[] {
				174,
				191,
				208,
				225,
				226,
				227,
				228,
				145,
				162,
				179,
				196,
				197,
				198,
				199				
		};
		for (int pivot : pivots) if (pivot == pos) return true;
		return false;
	}
	
	/** untranspose, using a non-wrapping method. */
	public static String rewrite3(String cipher, int n) {
		return rewrite3(cipher, n, false);
	}
	/** this is also the same as scytale transposition */
	public static String rewrite3(String cipher, int n, boolean showMapping) {
		String result = "";
		for (int i=0; i<n; i++) {
			for (int j=i; j<cipher.length(); j+=n) {
				result += cipher.charAt(j);
				if (showMapping)
					System.out.println(j + " -> " + (result.length()-1));
//				if (j==196 || j==225) System.out.println(result.length()-1);
				//if (showMapping(j))
				//	System.out.println("from " + j + " to " + (result.length()-1));
			}
		}
		return result;
	}
	/** this version of rewrite3 (scytale transposition) performs the transposition in a different column order.
	 * (the normal version of rewrite3 does the transposition in a sequential column order.)
	 */
	public static String rewrite3(String cipher, int n, int[] order, boolean reverse) {
		String result = "";
		StringBuilder reversed = new StringBuilder();
		if (reverse) {
			for (int i=0; i<cipher.length(); i++) reversed.append(" ");
		}
		int index = 0;
		for (int i : order) {
			for (int j=i; j<cipher.length(); j+=n) {
				if (reverse) {
					reversed.setCharAt(j, cipher.charAt(index++));
				} else {
					result += cipher.charAt(j);					
				}
			}
		}
		if (reverse) return reversed.toString();
		return result;
	}
	/** given the untransposed cipher, go back to the original */
	public static String rewrite3undo(String cipher, int n) {
		return rewrite3undo(cipher, n, false);
	}
	public static String rewrite3undo(String cipher, int n, boolean showMapping) {
		StringBuffer sb = new StringBuffer(cipher);
		int k=0;
		for (int i=0; i<n; i++) {
			for (int j=i; j<cipher.length(); j+=n) {
				sb.setCharAt(j, cipher.charAt(k));
				if (showMapping)
					System.out.println(k + " -> " + j);
				k++;
			}
		}
		return sb.toString();
	}

	
	/** return map of new positions to old positions */
	public static Map<Integer, Integer> rewrite3Map(String cipher, int n) {
		String result = "";
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int count = 0;
		for (int i=0; i<n; i++) {
			for (int j=i; j<cipher.length(); j+=n) {
				//result += cipher.charAt(j);
				map.put(count++, j);
				//if (showMapping(j))
				//	System.out.println("from " + j + " to " + (result.length()-1));
			}
		}
		return map;
	}
	
	/** return set of positions covered by all repeating bigrams found in the untransposition of the given period */
	public static Set<Integer> positions(String cipher, int period) {
		String result = "";
		Set<Integer> set = new HashSet<Integer>();
		/** map new (untransposed) position back to original position */
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		int positionNew = 0;
		for (int i=0; i<period; i++) {
			for (int j=i; j<cipher.length(); j+=period) {
				result += cipher.charAt(j);
				map.put(positionNew++, j);
			}
		}
		
		NGramsBean bean = new NGramsBean(2, result);
		for (String ngram : bean.repeats) {
			for (int pos : bean.positions.get(ngram)) {
				for (int i=0; i<ngram.length(); i++) {
					set.add(map.get(pos+i));
				}
			}
		}
		
		//System.out.println(map);
		return set;
	}
	
	/** return set of positions covered by all repeating bigrams found in the untransposition of the given period,
	 * in the form of highlight function calls for the cipher-highlighter */
	public static String positionsJS(String cipher, int period) {
		String result = "";
		/** map new (untransposed) position back to original position */
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		int positionNew = 0;
		for (int i=0; i<period; i++) {
			for (int j=i; j<cipher.length(); j+=period) {
				result += cipher.charAt(j);
				map.put(positionNew++, j);
			}
		}
		
		String js = "hsl_random([";
		NGramsBean bean = new NGramsBean(2, result);
		for (String ngram : bean.repeats) {
			js += "[";
			String csv = "";
			for (int pos : bean.positions.get(ngram)) {
				for (int i=0; i<ngram.length(); i++) {
					if (!csv.isEmpty()) csv += ",";
					csv += map.get(pos+i);
				}
			}
			js += csv + "],";
		}
		js += "])";
		//System.out.println(map);
		return js;
	}
	
	/** Measure clustering of periodic bigrams around certain columns of the untransposed cipher text.
	 * See http://www.zodiackiller.net/viewtopic.php?p=50901&sid=f43e27c5d880da17e72a67a6867eee6b#p50901
	 **/
//	public static void bigramClusters(String cipher, int period, int width) {
//		/*for (int i=0; i<n; i++) {
//			for (int j=i; j<cipher.length(); j+=n) {
//				result += cipher.charAt(j);
//			}
//		}*/
//		
//		for (int i=0; i<period; i++) {
//		for (int j=i; j<cipher.length(); j+=period) {
//			result += cipher.charAt(j);
//		}
//	}
//		
//	}
	
	/** return rewritten cipher and map to the corresponding positions in the original cipher */
	public static PeriodMap rewrite4(String cipher, int n) {
		PeriodMap pm = new PeriodMap();
		String result = "";
		int k=0;
		for (int i=0; i<n; i++) {
			for (int j=i; j<cipher.length(); j+=n) {
				result += cipher.charAt(j);
				pm.map.put(k++, j);
				//System.out.println((k++) + " " + j);
			}
		}
		pm.rewritten = result;
		return pm;
	}
	
	public static void visualize() {

		String cipher = Ciphers.cipher[0].cipher;
		PeriodMap pm = rewrite4(cipher, 18);
		String re = pm.rewritten;
		
		System.out.println(re);
		System.out.println(pm.map);
		int n = 2;
		NGramsBean bean = new NGramsBean(n, re);
		//bean.dump();
		for (String key : bean.positions.keySet()) {
			if (bean.counts.get(key) > 1) {
				String darken = "";
				String lighten = "";
				for (Integer pos : bean.positions.get(key)) {
					for (int i=pos; i<pos+n; i++) {
						int pos2 = pm.map.get(i);
						int r = pos2/17;
						int c = pos2 % 17;
						darken += "darkenrc(" + r + "," + c + "); ";
						lighten += "lightenrc(" + r + "," + c + "); ";
					}
				}
				System.out.println(bean.counts.get(key) + " <button onmouseover=\"" + darken + "\" onmouseout=\"" + lighten + "\">" + key + "</button>");
			}
		}
	}
	
	/** determine peak bigrams and its period.
	 * returned array: {period, number of bigram repeats}
	 **/  
	public static int[] peakPeriodicBigrams(String cipher, boolean showSteps) {
		int[] best = new int[2];
		best[0] = 1; // 
		for (int period = 1; period < cipher.length()/2; period++) {
			String re = rewrite3(cipher, period);
			NGramsBean bean = new NGramsBean(2, re);
			int reps = bean.numRepeats();
			if (reps > best[1]) {
				best[0] = period;
				best[1] = reps;
			}
			if (showSteps) {
				System.out.println(period + " " + reps + " " + re);
			}
		}
		return best;
	}
	
	/** return a list showing distribution of repeating bigram counts across
	 * all periods.  list is sorted in descending order.  
	 **/  
	public static List<Integer> periodicBigramDistribution(String cipher) {
		List<Integer> list = new ArrayList<Integer>();
		for (int period = 1; period < cipher.length()/2; period++) {
			String re = rewrite3(cipher, period);
			NGramsBean bean = new NGramsBean(2, re);
			list.add(bean.numRepeats());
		}
		Collections.sort(list);
		Collections.reverse(list);
		return list;
	}
	
	/** measure distance between this cipher's periodic bigram distribution and that of the Z340 */ 
	public static double periodicBigramDistributionDistance(String cipher) {
		List<Integer> list1 = periodicBigramDistribution(cipher);
		List<Integer> list2 = MeasurementsBean.referenceBean.periodicBigramDistribution;
		return Vectors.distance(list1, list2, true);
	}
	
	public static void testMap() {
		String cipher = Ciphers.cipher[6].cipher;
		Map<Integer, Integer> map = rewrite3Map(cipher, 29);
		String re = rewrite3(cipher, 29);
		NGramsBean bean = new NGramsBean(2, re);
		System.out.println(bean.toString());
		
		Set<Integer> seen = new HashSet<Integer>();
		for (String ngram : bean.positions.keySet()) {
			if (bean.counts.get(ngram) < 2) continue;
			for (Integer pos : bean.positions.get(ngram)) {
				int p1 = pos;
				
				int p2 = pos+1;
				
				int o1 = map.get(p1);
//				int r1 = o1/17;
//				int c1 = 16-o1%17;
//				o1 = r1*17+c1;
				
				int o2 = map.get(p2);
//				int r2 = o2/17;
//				int c2 = 16-o2%17;
//				o2 = r2*17+c2;
				
				if (!seen.contains(p1)) System.out.println("rgb(" + o1 + ", 255, 150, 0);");
				if (!seen.contains(p2)) System.out.println("rgb(" + o2 + ", 255, 0, 0);");
				seen.add(p1);
				seen.add(p2);
			}
		}
	}
	
	public static void jarlveMergeTest() {
		String cipher = Ciphers.cipher[0].cipher;
		String merged = SymbolMergeTest.mergeSymbols(cipher, "2z");
		for (int i=1; i<141; i++) {
			String re1 = rewrite3(cipher, i);
			String re2 = rewrite3(merged, i);
			String re3 = rewrite3undo(cipher, i);
			String re4 = rewrite3undo(merged, i);
			String line = i + "	";
			line += new NGramsBean(2, re1).numRepeats() + "	";
			line += new NGramsBean(2, re2).numRepeats() + "		";
			line += new NGramsBean(2, re3).numRepeats() + "	";
			line += new NGramsBean(2, re4).numRepeats();
			System.out.println(line);
		}
	}
	public static void significantMergeTest() {
		String cipher = Ciphers.cipher[0].cipher;
		cipher = SymbolMergeTest.mergeSymbols(cipher, "%4");
		System.out.println(cipher);
		cipher = Periods.rewrite3(cipher, 19);
		System.out.println(cipher);
		int n=2;
		while (true) {
			NGramsBean ng = new NGramsBean(n, cipher);
			if (ng.numRepeats() < 1) break;
			ng.dump();
			n++;
		}
			
	}
	
	public static void transposeMergeTest() {
		String cipher = Ciphers.cipher[0].cipher;
		String re = SymbolMergeTest.mergeSymbols(cipher, "(W");
		System.out.println(re);
		String re2 = rewrite3(re, 5, false);
		System.out.println(re2);
		int n = 2;
		while (true) {
			NGramsBean ng = new NGramsBean(n, re2);
			if (ng.numRepeats() < 1) break;
			System.out.println(n + ": " + ng.repeatsInfo());
			n++;
		}
	}
	
	/** determine equivalencies between transposed and untransposed periods */
	public static void testEquivalancies() {
		String cipher = Ciphers.cipher[0].cipher;
		
		cipher = CipherTransformations.shuffle(cipher);
		Map<String, Integer> uToPeriod = new HashMap<String, Integer>(); // untransposed cipher to period 
		Map<String, Integer> tToPeriod = new HashMap<String, Integer>(); // transposed cipher to period
		for (int p=1; p<=170; p++) uToPeriod.put(rewrite3(cipher, p), p);
		for (int p=1; p<=170; p++) tToPeriod.put(rewrite3undo(cipher, p), p);
		
		for (String key : uToPeriod.keySet()) {
			if (tToPeriod.containsKey(key)) {
				System.out.println("untransposed period " + uToPeriod.get(key) + " = transposed period " + tToPeriod.get(key));
			}
		}
	}
	
	/** swap symbols in the given cipher to maximize the number of repeating ngrams */
	public static void maxRepeats(String cipher, int n) {
		boolean foundBetter = true;
		NGramsBean ng = new NGramsBean(2, cipher);
		int best = ng.numRepeats();
		while (foundBetter) {
			foundBetter = false;
			for (int a=0; a<cipher.length(); a++) {
				for (int b=0; b<cipher.length(); b++) {
					if (a==b) continue;
					StringBuffer sb = new StringBuffer(cipher);
					char c1 = sb.charAt(a);
					char c2 = sb.charAt(b);
					sb.setCharAt(a, c2);
					sb.setCharAt(b, c1);
					NGramsBean ngTry = new NGramsBean(2, sb.toString());
					if (ngTry.numRepeats() > best) {
						foundBetter = true;
						best = ngTry.numRepeats();
						ng = ngTry;
						cipher = sb.toString();
						System.out.println(best + " " + cipher);
					}
				}
			}
		}
	}

	public static void testSamBlakeCipher() {
		// the one with "TRYING TO CATCH ME" and "OR THE GAS CHAMBER"
		// diagonal-variations/vertical_3split_decimations/z340_vertical_3split_decimations_9_9_1_19_2.txt
		String cipher = "H+M8|CV@KEB+*5k.LdR(UVFFz9<>#Z3P>L(MpOGp+2|G+l%WO&D#2b^D(+4(5J+VW)+kp+fZPYLR/8KjRk.#K_Rq#2|<z29^%OF1*HSMF;+BLKJp+l2_cTfBpzOUNyG)y7t-cYA2N:^j*Xz6dpclddG+4-RR+4>f|pz/JNbVM)+l5||.UqL+Ut*5cZGR)VE5FV52cW+|TB4-|TC^D4ct+c+zJYM(+y.LW+B.;+B31cOp+8lXz6Ppb&RG1BCO7TBzF*K<S<MF6N:(+HFK29^4OFTBO<Sf9pl/yUcy5C^W(-+l#2E.B)|kW7BYB-cFd<t_O*C>DNkzOAK+MHpSZ8|;";
		System.out.println(new NGramsBean(2, cipher));
		System.out.println(new NGramsBean(3, cipher));
		System.out.println(new NGramsBean(4, cipher));
		String z340upper9 = Ciphers.Z340.substring(0, 17*9);
		System.out.println(Periods.rewrite3(z340upper9, 19));
		int[][] m = transpositionMatrix(9, 17, 1, 2);
		for (int[] r : m) {
			System.out.println(Arrays.toString(r)+",");
		}
		System.out.println(matrixApply(z340upper9, m));
		String z340lower9 = Ciphers.Z340.substring(17*9, 17*9+17*9);
		System.out.println("lower9: " + z340lower9);
		System.out.println(matrixApply(z340lower9, m));

		for (int row=0; row<m.length; row++) {
			for (int col=0; col<m[row].length; col++) {
				m[row][col] += 153;
			}
		}
		for (int[] r : m) {
			System.out.println(Arrays.toString(r)+",");
		}
		
		m = transpositionMatrix(1, 17, 0, 2);
		System.out.println(matrixApply(Ciphers.Z340.substring(17*18, 17*18+17), m));
		for (int row=0; row<m.length; row++) {
			for (int col=0; col<m[row].length; col++) {
				m[row][col] += 18*17;
			}
		}
		for (int[] r : m) {
			System.out.println(Arrays.toString(r)+",");
		}

		m = transpositionMatrix(1, 17, 0, 2);
		System.out.println(matrixApply(Ciphers.Z340.substring(17*19, 17*19+17), m));
		for (int row=0; row<m.length; row++) {
			for (int col=0; col<m[row].length; col++) {
				m[row][col] += 19*17;
			}
		}
		for (int[] r : m) {
			System.out.println(Arrays.toString(r)+",");
		}
		int[][] ms = transpositionMatrixSamBlake1();
		String sam = matrixApply(Ciphers.Z340, ms);
		System.out.println(sam);
		String undo = matrixUndo(sam, ms);
		System.out.println(undo);
		System.out.println(undo.equals(Ciphers.Z340));
		String pt = "EHOPEYOUAREHESINGISTTORRAENNTRYINGTOCATCHMETHAFTAINTMTONTHETSSHOTWHICHBRINGSUPALSINTABSITMENAMEOFARHEEDORTHEGASCHAMBERBECAATEITWILDVENTMEROLERADICEAIITHEVSSHENBECAUSETOOWHASEENTIGHTDESERTSWORSROSMETHEREEVERYONEEDHEHASNOTHINGTHENTHEYHEACHPARADICTISTHEYALREAREANDNORDERTHERAMEOEARREANDBECAUITEISYOTTVHATMRNEWEITLENEVERINDBAEYNNEIAATAHOECDRPET";
		// transposed ct with "or the gas chamber"
		//String ct = "H+M8|CV@KEB+*5k.LdR(UVFFz9<>#Z3P>L(MpOGp+2|G+l%WO&D#2b^D(+4(5J+VW)+kp+fZPYLR/8KjRk.#K_Rq#2|<z29^%OF1*HSORTHEGASCHAMBERfBpzOUNyG)y7t-cYA2N:^j*Xz6dpclddG+4-RR+4>f|pz/JNbVM)+l5||.UqL+Ut*5cZGR)VE5FV52cW+|TB4-|TC^D4ct+c+zJYM(+y.LW+B.;+B31cOp+8lXz6Ppb&RG1BCO7TBzF*K<S<MF6N:(+HFK29^4OFTBO<Sf9pl/yUcy5C^W(-+l#2E.B)|kW7BYB-cFd<t_O*C>DNkzOAK+MHpSZ8|;";
		//String ct = "                                                                                                       ORTHEGASCHAMBER                                                                                                                                                                                                                              ";
		String ct = "H+M8|CV@KEB+*5k.LdR(UVFFz9<>TRYINGTOCATCHMEG+l%WO&D#2b^D(+4(5J+VW)+kp+fZPYLR/8KjRk.#K_Rq#2|<z29^%OF1*HSMF;+BLKJp+l2_cTfBpzOUNyG)y7t-cYA2N:^j*Xz6dpclddG+4-RR+4>f|pz/JNbVM)+l5||.UqL+Ut*5cZGR)VE5FV52cW+|TB4-|TC^D4ct+c+zJYM(+y.LW+B.;+B31cOp+8lXz6Ppb&RG1BCO7TBzF*K<S<MF6N:(+HFK29^4OFTBO<Sf9pl/yUcy5C^W(-+l#2E.B)|kW7BYB-cFd<t_O*C>DNkzOAK+MHpSZ8|;";
		ct = ".HOPEYOUARE.................TRYINGTOCATCHME..................SHOTWHICHBRINGSUP.........................ORTHEGASCHAMBER..............................................................................................HEHASNOTHINGTHENTHEY............................................................................................................";
		System.out.println(matrixUndo(ct, ms));
		
	}
	
	public static String matrixApply(String cipher, int[][] matrix) {
		StringBuffer sb = new StringBuffer();
		for (int[] r : matrix) {
			for (int c : r) {
				sb.append(cipher.charAt(c));
			}
		}
		return sb.toString();
	}
	public static String matrixUndo(String cipher, int[][] matrix) {
		StringBuffer sb = new StringBuffer(cipher.length());
		for (int i=0; i<cipher.length(); i++) sb.append(" ");
		int pos = 0;
		for (int[] r : matrix) {
			for (int c : r) {
				sb.setCharAt(c, cipher.charAt(pos));
				pos++;
			}
		}
		return sb.toString();
	}
	
	public static int matrixPosApply(int pos, int[][] matrix) {
		int row = pos / 17;
		int col = pos % 17;
		return matrix[row][col];
	}
	
	/** what positions (in the solved untransposition) do the pivots correspond to in the original z340 plaintext? */
	public static void translatePivots() {
		int[] pivot1 = new int[] { 126, 143, 160, 177, 194, 193, 192, 191, 190 };
		int[] pivot2 = new int[] { 182, 199, 216, 233, 232, 231, 230 };
		System.out.println("pivot1");
		for (int pos : pivot1)
			System.out.println("darkenpos2(" + mapPositionToZ340.get(pos) + ")");
		System.out.println("pivot2");
		for (int pos : pivot2)
			System.out.println("darkenpos2(" + mapPositionToZ340.get(pos) + ")");
	}
	public static void testZ340Sections1and2() {
		String section1 = 
		"IRONCAOOIIERGRTML" +
		"ECHETTATNWNNIAABW" +
		"EITEOHSRTWTWGTAIS" +
		"DCCLOAPAOYCAHHOAM" +
		"BNOHALPLEVFIHSEIU" +
		"CPOOFAASALYIFNMNT" +
		"TVHAUTTMSERTONAGE" +
		"TTMSBPTAHBENAHUGN" +
		"LIOHEHROMFEEIDDEA";
		String section2 = 
		"SAASOHOSHACLIFEIS" +
		"SHOUVLRENNECEROAA" +
		"MIEAOSEAVREONHSEF" +
		"ADNDITHEEVFEETTPO" +
		"ATFEOBVMEENEOELHH" +
		"IERRATENYRNOSRVSH" +
		"EENYAEATACONBOUTM" +
		"OERHGRDYIHFAWEEWG" +
		"HHWWYAWEIADIRUTWC";
		System.out.println(Periods.rewrite3(section1, 19));
		System.out.println(Periods.rewrite3(section2, 19));

	}
	
	public static void dumpZ340MatrixFormatted() {
//		for (int[] row : transpositionMatrixZ340Solution()) {
//			for (int col : row) {
//				System.out.print(col + " ");
//			}
//			System.out.println();
//		}
		
		System.out.println("From Z340 to readable");
		for (int row=0; row<20; row++) {
			for (int col=0; col<17; col++) {
				System.out.print(String.format("%03d", mapPositionFromZ340.get(row*17+col)) + " ");
			}
			System.out.println();
		}
		System.out.println("From readable to Z340");
		for (int row=0; row<20; row++) {
			for (int col=0; col<17; col++) {
				System.out.print(String.format("%03d", mapPositionToZ340.get(row*17+col)) + " ");
			}
			System.out.println();
		}
		
	}
	
	public static void main(String[] args) {
//		dumpZ340MatrixFormatted();
//		translatePivots();
//		transposeMergeTest();
		//testEquivalancies();
		//jarlveMergeTest();
		//testScytale();
		//String cipher = "+A|d4SNp(+EHp_z>||7|(RB4S+HfN6OO_pRlcpV&c4clTzVccWFG^4+K+BB+yJB+6PtLcVcCKC)RF^)F3H*EfO#+-B>FLCkV|zT4p++|;#8(bzB+Ep9||22<F>ZK5OyG.zO2F+J+;Bt&OKU*)CWD+ZR+/+-*RSlFM|9T#bGNt<Oj#G9zV8Mc1RUMzpBW<+MFk/%52cT+K6fd2_^-yMRkt)M+2Y/.W+.X(5*.5.+85:)^X42((B|PY18^p3d%lYK>pqzl<yfN+O.FkMD;FZ:-ljD(ABC*TYWZ#9L<NH75WVUOb5LBqDGPS<cRL+l2Jz2K^1pL*-+BypUOkdJdUG7@";
//		String re = "<p;c)K3;MWN<G2F+O+LBFlcypZU+4+CVYEkMUS#R+2KF8pz5Y|-GG+1+Y++2+;LM:O^|<++F6JS+4F+|H9p-jkJpRcDcFc)NtLVGKTL2B2J%5dM)HEHlkcjp_>/&U*lZO9D|N4:>2y9bk<ROp.OTfcBHBN>lWK7--q*|8P1YJF8z^4OM/tRCW.V.y+|Vbp<p4KT@ZGy(y|++2+XCOKDdL)F*EVB6Rf&f|/(UOlB*W7zd-(Mzl+kRF+#(Bz7c^_#*2dAMz|PScBd5LBBRU5^.^.q+Z#*^BCC6++RXpO>pWT35b|W9SDfG1(5(4%z_P.8TcBKl#Nz2FA)zt(5V+tO<";
//		String re = Ciphers.cipher[0].cipher;
//		re = rewrite3(re, 19, true);
//		System.out.println(re);
		test(Ciphers.Z340, false);
//		test("HRp^P|LGdE>lVk1T2N+(ODY<K)pB#%W.*fB:MUG(LzJyc+ZW)#HSp^8Vp+R2p7l*3O+K_Mzj|F+4/9+td5P&kpRFO*CF2(8^l-dk>D#+q;UXVz|5K%2cG.L(2f#+Nz@9GJjO_Y+LdMbZ2By6K<++RFcA4-lV^+p<B-zU+JO7FyUR5EDBbMO+/t|YpTK2cR|54.&F<lJ*TM+Bz9y+|Fc;R6S#N5B(8lF^54.Vt+GNf2bc4+yX*4C>U5+B1:9EVZ-|.zKO^fq2c3B(p.MGRTL6<FW|Lc+1C+lB)+)CWPST(p+WzcOH/)|kW7BYB-cFd<t_O*C>DNkzOAK+MHpSZ8|;", false);
//		System.out.println(new NGramsBean(2, re).numRepeats());
//		new NGramsBean(2, re).dump();
//		re = rewrite3undo(re, 39);
//		System.out.println(re);
//		System.out.println(new NGramsBean(2, re).numRepeats());
		
//		re = rewrite3undo(re, 39);
//		System.out.println(re);
//		System.out.println(cipher);
		
		
		/*String[] ciphers = new String[] {
				Ciphers.cipher[0].cipher,
				"16OjYU_ykp+BB+^WU(KX<Mbpc^4|5FBcfjkkqlHpbY5p^1GG(dHERJ|*8YzZ)d;7Clj9@WqB|z+WUB;-2U<J/T25+J8D(+cc31l*F2T9W^RLN<pYDN)p5M6zklF%+b.cV2lX|OWB4<F3+lkBFcFP-*8yp-f+(WMDJ_.U1V((T-42>SkFB;qbN+%+P25&NZ^+^/FBctO4#++EJD2A/+&LO*cGC7+Z+CC...py3#pD+j_DZBF2S:DzM<p8|5FUlS_LtVT)pGzKM<*-12MALc1bDd(|+B1|pqYNz+6&jKDPA|Vyf++|SV#)5V2z+1K)VTft1LG+D<:#G>1f464*%+Tz",
				"&7t/Vc.bP4()TOM+p/^#Cc(yOBKf#8d5-G>|5F.kO8_F&9KzctGzO-6bk+fX;cZ+^N5k2+MzUX2+d|71y;d44.|5FBcj)+21y/2+9y(KPHER+*8++WB9MV&:t+UNFdTllt^.%&NqyO7zZ3D|^C;PF#+VEAK:EGGEEppR+FC9YK-5*<Fcc3+5C|*-Ek-AU+lO+U<zS-*|H&Oc)qMT^L*S+LMCD*|JR(l|_4Z4DY+JR_|52).)z2zZ&Rj&VNLJ#BS@W+@*+5cFBcR+RB>WBUC5&G*TbPBbUJFBNF+SB2G(dO>5(#3TK8DlB>&*M+V<W&B#kz64f|<G.@+8(lL<&O1l",
				"%7zJL.b|5FBcfW&7Dp62+)p.cMzz+)52WkJ^q-Bpcyz1pycOTD_+Yqb.cVSZLZF4|+%f4UOBz7PY&XR249%#9^3%E#&%_TyHt|2Ok)8+A+pCz|5Fp*>j2&+c8zT^6FBc2+pO7|FB7O+RKO8LULtMTdy+3RPO+dKRFP<+.;8TkM-pkyG@DL/l8B5:4<-FK(lzZ#d;9C8&V:pCN.lR>cySD6+1+7U4+tB<JkpZ5<NjTG+XdN2+G|B)MYRl(WUUfHERJ|*V<(pd1F+524+|+._/c<W(F<+c1PN-*P>O&O+>Cz5l3p)YGKkB/VJWA*-+CNF+P5B.BS|LWj|MO>-j2yLf",
				"#)k>-O|Mb^M)b)cdAqKUWb)*-)+<HTB)j3):Kf.L^3SyO)2pb6)YE(pcOALS+)zR4zV;^b.cVkq)Uy%T9CN+CyC^ZS#7yqVq#GDGjDZDjjHG+G*DN<BK&9dlR%zY;pb)>S1qMpF(Nc&pL|qFkzT)KlcXc)<|5F|8N_9WU^8^T:-C<E#HER66M%FUB2-B8%@1*7J_#/pUBz*6O)-lNq2|lt+7(4q9^FBckRJ|*OBYfL%)-7kt^/-*W;p)()l6pTMJB2^/|l)VRc6()fS4l9|5FBcO%y3Wz^)R|bKd9Y7zZ#d8|c/pz)lcPTLpFlWfLBFKNqKSLBRP1)tFBf)dqXtP",
				"#&c*_Zb.cVPRJ|*9l&Xt6)ES7Kc&3Ly|F&Dq%U&TbXU.j_1&J#B5<&T<plcpbp6HERM%G2DLRYEGjFBcdl413RjVUWMNzX)-*|5FPt^^NzB>ztckdFY)j+cVyRX&(|6V&<BOUFyz#CT&CU8R@X6|c/#&f-N6&HqN:&J>b:8*--RDYplqHZ<|&6G<|/Fd6UB3F1zX42B|5FBcRf9kY|-&(J2M8FNR;&lW@-*&4B/+5&(fZ/4q6)CNBZ.22Sq<..DDE2yqD7ySE(&8W9YKfWfzb/:&6WWzZ3DA/c|M;Fp8zLz6#b&3WB33GA2lL3&6#9&3tXPp7B4(N)3:)^X6&kpk",
				"#l1>_UC*ttFlyR_VJp2+z6|K:zZ%7|8kZKjf+zqPddk77^+kdXl2+L%1B|5FBc<pCA)c45M*+T*-BpPR+5+6+P^Y<FzWO-*BfU5.XEFBcyU;fO/|#9^lN+zK5K_+O>MN6-V^4C+l2%Ay#)(p4SU@py+b/-t(/L++W.pyCP<pL-B+*4%R|5FRzDK/&G3D+l-TMc&BAORz|b.N63+9_fVXb.cVyYl>^4;RJ|*3Nc|.c^)+dc_t9M+|+DKc6.+29pWzNOO/J%;WHpb|Hc%6LpOTLRDM1dRlRB+<PFBDpZUO8pqF2SMF)+Gz2TMA6jFL>WKF)OcHEROY%<:d12S#S+C/",
				"|#P-z4_Xl>(HjlDXk+lj;ScLMGM:z;MVTFX2CRqMEEjEd9EM7.%)&X(kWWK8@V8M&|6GH^Syq-76./t1T-*bFU(|5FBc12fG3l7p2;Oc|5F%;kt2&(%#z2bV^M;t)WXUKGYD#A<|M)L8MOMl)MHz^STql7c./b.cV6tF;M<5GX-jM+6FBcyzpN42#&-*-;&.qy.Mz;CY+3YX|A(4C|Lb;8*LMU(z1f5RJ|*^cfqtXMBt<|5Y(N7|1V*WP/St3)MBzMXDJX;MFM:L#HERTN^P(RFz*>W(#MbWt4ZpJBDH)MMcG%2jAl;86|FG4dA.D+yBH5fcJEMFzZ_9|7cB5B^t",
				"zFT-+p)OZk*+MNCLtV-X_W+<<yNBR)p+fMp-*+3cGKkcM/.9(BCWA|2:jl6LU+.TB>WR7L+FBzZOD8|K_FBc9l#)H^zUG&PS1zc5RdfH^*G>c91OWNJ;;#MG3DdB+z77O4ct^K5N1P<tM5l2+OHp+(F>+^_)/lb.cV+(pSj74F-V<BzGOBcOU4p|OF-zp<c;q6%.q2L4(#B485+OR+YGb|5FCL#6+lz5dPYyR^HERSp+q)Y2zpDTCKG8K%VE%+V%J+EFC|#BDYRJ|*MUF*|Xt|^L(:/l+A|9yUlSTG*-&KyT+k>J4k2+p.2WHO<|5FBcR<K#ydf++@+(YMBWpGfd"
		};
		for (String cipher : ciphers) {
			String re = rewrite3(cipher, 19);
			NGramsBean ng = new NGramsBean(2, re);
			String line = ng.count() + "	" + ng.numRepeats(); 
			//System.out.println("period19 " + ng.count() + " " + ng.numRepeats());
			ng = new NGramsBean(2, cipher);
			//System.out.println("period1 " + ng.count() + " " + ng.numRepeats());
			line = ng.count() + "	" + ng.numRepeats() + "	" + line;
			System.out.println(line);
			
			System.out.println(cipher);
			int[] num = Ciphers.toNumeric(cipher, false);
			String n = "";
			for (int i : num) n += (i<10 ? "0"+i : i) + " ";
			System.out.println(n);
		}*/
		//System.out.println(positions(Ciphers.cipher[0].cipher, 39));
		
		//String cipher = "C;XO^z-7Y(SG5DYk>AUFcK2ypW<TXf847yWc<p>2pEVXT9#6LjW9OpHER;b.2-*Xc)l%FTB<:O3.dcK|5FBcMpK/fU)1c>X(zLcT/X28pOb.cVO;X5N/2L_2JfY/<XXk1MX9T&t|TBO>P9#dFNzd4zK<)-EX+ZBpB|C6kWKOW-YRL#@7G*^UXRcBXO.MSXz7)f:@jyPz>%lfXNOX3G2&L>X2|X+|q3()zk_NRO8t|5FC*-NJ6qU4BJz)W1lf<X8|Hp&//L|*^dzX3VX)XPAyRJ|*(dzZ_77|kqXO4MCY+/jM&5G2GKGFPB9l5(-O#FXWl#FWpU:FBcdTBBdKq*2+";
		/*String cipher = Ciphers.cipher[0].cipher;
		String flip = "";
		for (int row=0; row<20; row++) {
			for (int col=16; col>=0; col--) {
				flip += cipher.charAt(row*17+col);
			}
		}
		System.out.println(new NGramsBean(2, rewrite3(cipher, 19)).numRepeats());
		System.out.println(new NGramsBean(2, rewrite3(cipher, 19)).count());
		System.out.println(new NGramsBean(2, rewrite3(flip, 15)).numRepeats());
		System.out.println(new NGramsBean(2, rewrite3(flip, 15)).count());
		
		System.out.println(rewrite3(cipher, 19));
		System.out.println(rewrite3(flip, 15));
		*/
		//cipher = " HER>pl^VPk|1LTG2 Np+B(#O%DWY.<*Kf By:cM+UZGW()L#zH Spp7^l8*V3pO++RK _9M+ztjd|5FP+&4k p8R^FlO-*dCkF>2D #5+Kq%;2UcXGV.zL (G2Jfj#O+_NYz+@L d<M+b+ZR2FBcyA64 -zlUV+^J+Op7<FBy U+R/5tE|DYBpbTMK 2<clRJ|*5T4M.+&B z69Sy#+N|5FBc(;8 lGFN^f524b.cV4t+ yBX1*:49CE>VUZ5- |c.3zBK(Op^.fMqG RcT+L16C<+FlWB|) ++)WCzWcPOSHT/() |FkdW<7tB_YOB*-C >MDHNpkSzZO8A|K;";
		//String cipher = "THECONFESSIONBYSHEWASYOUNGANDBEAUTIFULBUTNOWSHEISBATTEREDANDDEADSHEISNOTTHEFIRSTANDSHEWILLNOTBETHELASTILAYAWAKENIGHTSTHINKINGABOUTMYNEXTVICTIMMAYBESHEWILLBETHEBEAUTIFULBLONDTHATBABYSITSNEARTHELITTLESTOREANDWALKSDOWNTHEDARKALLEYEACHEVENINGABOUTSEVENORMAYBESHEWILLBETHESHAPELYBRUNETTTHATSAIDNOWHENIASKEDHERFORADATEINHIGHSCHOOLBUTMAYBEITWILLNO";
//		String cipher = Ciphers.cipher[0].cipher;
//		test(cipher, false);
//		significantMergeTest();
		//System.out.println(rewrite3(cipher, 15));
		//test ("HER>pl^VPk|1LTG2d Np+B(#O%DWY.<*Kf) By:cM+UZGW()L#zHJ Spp7^l8*V3pO++RK2 _9M+ztjd|5FP+&4k/ p8R^FlO-*dCkF>2D( #5+Kq%;2UcXGV.zL| (G2Jfj#O+_NYz+@L9 d<M+b+ZR2FBcyA64K -zlUV+^J+Op7<FBy- U+R/5tE|DYBpbTMKO 2<clRJ|*5T4M.+&BF z69Sy#+N|5FBc(;8R lGFN^f524b.cV4t++ yBX1*:49CE>VUZ5-+ |c.3zBK(Op^.fMqG2 RcT+L16C<+FlWB|)L ++)WCzWcPOSHT/()p |FkdW<7tB_YOB*-Cc >MDHNpkSzZO8A|K;+ ", false);
		/*String[] ciphers = new String[] {
				Ciphers.cipher[6].cipher,
				Ciphers.cipher[0].cipher,
				Ciphers.cipher[1].cipher.substring(0,340)
		};*/
		//test(Ciphers.cipher[0].cipher, false);
		//test(ciphers[1], false);
		//test(ciphers[2], false);
		//test(Ciphers.cipher[0].cipher, false);
		//testScytale();
		
		//System.out.println(positionsJS(Ciphers.cipher[0].cipher, 19));
		
		
		//System.out.println(rewrite3(Ciphers.cipher[6].cipher, 15));
		//System.out.println(rewrite3("0123456789", 4));
		//System.out.println(rewrite3("HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+", 57));
//		System.out.println(new NGramsBean(2, 
//				"dpclddG+4Ucy5C^W(+H+M8|CV@-+l#2E.B)>EB+*5k.L-RR+4>f|cMR(UVFFzKz/JNbVM)|D>#Z3P>Ldl5||.UqpFHpOGp+29<Ut*5cZG+kNl%WO&D(MVE5FV5L+dp^D(+4|G++|TB4-R)WkVW)+k#2b^D4ct2cW<SPYLR(5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z/8KjROp+8y.LWBO1*H_Rq#2pb&+B31c_8LK29^%OF7TBlXz6PYATfSMF;+B<MRG1BCOO|GJp+l2_cFKzF*K<SBK2BpzOUNyBF6N:(+H*;)y7t-cYAy29^4OFT-N:^j*Xz6O<Sf9pl/C"
//				).numRepeats());

		//testPeaks();
		//System.out.println(re);
		
		//System.out.println(Arrays.toString(peakPeriodicBigrams(Ciphers.cipher[1].cipher, true)));
		//test(re, false);
		//testForJarlve();
//		String re = rewrite3("dHER>pl^VPk|1LTG2)Np+B(#O%DWY.<*KfJBy:cM+UZGW()L#zH2Spp7^l8*V3pO++RK/_9M+ztjd|5FP+&4k(p8R^FlO-*dCkF>2D|#5+Kq%;2UcXGV.zL9(G2Jfj#O+_NYz+@LKd<M+b+ZR2FBcyA64--zlUV+^J+Op7<FByOU+R/5tE|DYBpbTMKF2<clRJ|*5T4M.+&BRz69Sy#+N|5FBc(;8+lGFN^f524b.cV4t++yBX1*:49CE>VUZ5-2|c.3zBK(Op^.fMqGLRcT+L16C<+FlWB|)p++)WCzWcPOSHT/()c|FkdW<7tB_YOB*-C+>MDHNpkSzZO8A|K;", 19);
//		System.out.println(re);
//		NGramsBean bean = new NGramsBean(2, "#####%%&&((((((()))))******++++++++++++++++++++++++-----......///11122222222233444444555555566677788889999::;;;<<<<<<>>>>@AABBBBBBBBBBBBCCCCCDDDDEEEFFFFFFFFFFGGGGGGHHHHJJJJKKKKKKKLLLLLLMMMMMMMNNNNNOOOOOOOOOOPPPRRRRRRRRSSSSTTTTTUUUUUVVVVVVWWWWWWXXYYYYZZZZ^^^^^^___bbbccccccccccdddddffffjjkkkkklllllllpppppppppppqqttttyyyyyzzzzzzzzz||||||||||");
//		System.out.println(bean);
//		test("HUzlFly.UzM2Xc>K)*6FAL@4CqDH(4RP@A1-@Eq+O(PR_V-@S+tGElOJUHE29qJ/ZGPCySD1C-Rp_V(d6FAf@4cP:Ey4D&WP3LD/:S2cpd@cTE:PL%R-LU.D3l;)U(3P*d&BS2Zybc(PC+|cPRGN+&_H%%HSZkYKVpcS<*H+q(#^kM6Vd+*pD|fSZCDNOMZj>@_p35l(R--9OD&:Zq_%+1c6#4dXfpd7-q+;fN&Tk9|FSH/H*;9zJU6V_#6>_SqMKL_G/y<c4M/f.)+&VztL8Flz%)C/9BJ)6CA7P*Pj4.f8W4;9zU%M3DZ;b#jR@PjSLPV6*<G@(cj>@9yJ%Dpj", false);
//		test(W168.cipherLine, true);
//		bean.dump();
//		maxRepeats(Ciphers.Z340, 2);
		
		//visualize();
		//rewrite(cipher);
		//testMap();
		//for (int i=1; i<=170; i++) System.out.print(i + "	");
//		System.out.println(rewrite3(Ciphers.Z340, 19));
//		System.out.println(rewrite3("HRp^P|LGdE>lVk1T2N+(ODY<K)pB#%W.*fB:MUG(LzJyc+ZW)#HSp^8Vp+R2p7l*3O+K_Mzj|F+4/9+td5P&kpRFO*CF2(8^l-dk>D#+q;UXVz|5K%2cG.L(2f#+Nz@9GJjO_Y+LdMbZ2By6K<++RFcA4-lV^+p<B-zU+JO7FyUR5EDBbMO+/t|YpTK2cR|54.&F<lJ*TM+Bz9y+|Fc;R6S#N5B(8lF^54.Vt+GNf2bc4+yX*4C>U5+B1:9EVZ-|.zKO^fq2c3B(p.MGRTL6<FW|Lc+1C+lB)+)CWPST(p+WzcOH/)|kW7BYB-cFd<t_O*C>DNkzOAK+MHpSZ8|;", 18));
//		System.out.println(rewrite2(Ciphers.Z408, 19));
//		System.out.println(rewrite3undo(rewrite3(Ciphers.Z408, 19),19));
//		System.out.println(rewrite3undo(Ciphers.Z408, 19));
//		testSamBlakeCipher();
//		test(Ciphers.Z340, false);
//		testZ340Sections1and2();
	}
}
