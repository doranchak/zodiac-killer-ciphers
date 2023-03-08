package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.util.Permutations;

public class GridPermutations {
	/*
	 * Divide W168 into a series of sub-grids with dimension N rows by M columns.
	 * A sub-grid has an ordering of N*M elements: {0...N*M-1}.
	 * We want to consider all possible reordering of those elements, to represent how to 
	 * transpose the elements within the sub-grid.
	 * 
	 * Re-arrange all sub-grids of W168 with the same permutation of elements of the sub-grid.
	 * Do not allow any sub-grids to overlap.
	 * 
	 * N can be drawn from: {1, 2, 3, 6} (those are factors of the 6 rows of the
	 * original layout). M can be drawn from: [1..max] such that (N*max)! is
	 * tractable.
	 * 
	 * If N < 6, treat original grid as N x (168/N) continuous grid, so we can
	 * simply walk the NxM grid from one end to the other.
	 * 
	 * Process all 6! row permutations.
	 * 
	 * If the last sub-grid overlaps the end of the cipher, simply apply the partial permutation and ignore
	 * any missing elements.
	 * 
	 * Optional followup: Allow more distinct values of N (and deal with the irregular subgrids in some way)
	 * 
	 */


	// all (6!) permutations of the 6 rows of W168 
//	public static StringBuilder[][] rowPermutationsW168;
//	static {
//		rowPermutationsW168 = new StringBuilder[720][6];
//		StateRowPermutations s = new StateRowPermutations(6, W168.W168, rowPermutationsW168);
//		Permutations.recurse(s);
//		System.out.println("Generated " + rowPermutationsW168.length + " row permutations of W168.");
//	}
	
	// all (6!) permutations of the 6 rows of J168_1 
//	public static StringBuilder[][] rowPermutationsJ168_1;
//	static {
//		rowPermutationsJ168_1 = new StringBuilder[720][6];
//		StateRowPermutations s = new StateRowPermutations(6, W168.J168_1, rowPermutationsJ168_1);
//		Permutations.recurse(s);
//		System.out.println("Generated " + rowPermutationsW168.length + " row permutations of J168_1.");
//	}
	
	
	public static void candidateGridSizes(int maxPermutations) {
		int[] Ns = new int[] { 1, 2, 3, 6 };
		for (int N : Ns) {
			for (int M = 1; M <= 168; M++) {
				int elements = N * M;
				if (elements == 1) continue;
				long permutations = factorial(elements);
				if (permutations > maxPermutations)
					break;
				System.out.println(N+"x"+M+": " + elements + "! = " + permutations);
			}
		}
	}
	
	/** generate all possible sub-grid dimensions that divide evenly into the cipher grid dimensions */
	public static void candidateGridSizesFactors() {
		for (int N = 1; N <= 6; N++) {
			for (int M = 1; M <= 28; M++) {
				if (N == 1 && M == 1)
					continue;
				if (6 % N == 0 && 28 % M == 0) {
					System.out.println(N + "x" + M);
				}
			}
		}
	}	
	
	public static long factorial(int n) {
		if (n <= 2) {
			return n;
		}
		return n * factorial(n - 1);
	}
	
	/** perform an "in-place" NxM untransposition on the given cipher grid using the given grid pattern.
	 * if grid dimensions don't divide evenly into the cipher grid dimensions, then resulting ciphertext may omit some letters. */
	public static StringBuilder applyGridPermutation(StringBuilder[] cipherGrid, int[][] subgrid, boolean includeLeftovers) {
		// Derivation:
		// Let f be a function that transforms a position in the cipher grid to its new
		// position in the candidate untransposed plain text.
		// Let cipher grid have dimensions YxX
		// Let S be the NxM subgrid determining the reordering of subgrid elements.
		// Let (row,col) be cipher position in cipher grid.  0 <= row < Y.  0 <= col < X
		// Which element of S is given by that position?
		// It is: pos1D = S[row % N][col % M]
		// Which cipher text position does that element of S refer to?
		// The element refers to a 1D position within the grid.
		// Convert it to 2D:
		// subrow = pos1D / M
		// subcol = pos1D % M
		// Now we have a reference to a new row,col relative to the sub-grid.
		// Convert that back to a new position in the cipher text.
		// Let's make it relative to the UL corner position in the cipher grid where the sub-grid appears
		// rowUL = row/N * N  (the divide operation discards the decimal portion)
		// colUL = col/M * M  (the divide operation discards the decimal portion)
		// rowNew = row/N * N + subrow;
		// colNew = col/M * M + subcol;
		// So, original cipher text position is: (row, col)
		// New cipher text position is:
		// rowNew = S[row % N][col % M] / M
		// colNew = col / M * M + S[row][col % M] % M

		// Can we do the inverse?  Determine the original cipher position from a given plain text position?
		// Yes, we can simply precompute it using the previous function and just map it backwards.
		// But that's an extra computational step.
		
		
		StringBuilder result = new StringBuilder();
		int N=subgrid.length;
		int M=subgrid[0].length;
		int Y = cipherGrid.length;
		int X = cipherGrid[0].length();
		boolean[][] seen = new boolean[Y][X]; // track unused (leftover) positions that we can append
		if (includeLeftovers) for (int row=0; row<Y; row++) seen[row] = new boolean[X];
		for (int row=0; row<Y; row++) {
			for (int col=0; col<X; col++) {
				int rowNew = row / N * N + subgrid[row % N][col % M] / M;
				int colNew = col / M * M + subgrid[row % N][col % M] % M;
				//System.out.println("row " + row + " col " + col + " rowNew " + rowNew + " colNew " + colNew + " subgrid[row % N][col % M] " + subgrid[row % N][col % M] + " colUL " + (col / M));
				if (rowNew < Y && colNew < X) {
					result.append(cipherGrid[rowNew].charAt(colNew));
					seen[rowNew][colNew] = true;
				}
				//System.out.println(result);
			}
		}
		if (includeLeftovers) {
			for (int row=0; row<Y; row++) {
				for (int col=0; col<X; col++) {
					if (!seen[row][col]) result.append(cipherGrid[row].charAt(col));
				}
			}
		}
		return result;
	}
	
	/** convert the 6-lined cipher to an N-lined version */
	public static StringBuilder[] toRows(int N) {
		StringBuilder[] newGrid = new StringBuilder[N];
		int M = W168.cipherLine.length() / N; 
		for (int i=0; i<N; i++) {
			for (int j=i; j<6; j+=N) { 
				if (newGrid[i] == null) newGrid[i] = new StringBuilder();
				newGrid[i].append(W168.W168[j]);
				//System.out.println("i " + i + " j " + j + " newGrid[" + i + "] " + newGrid[i]);
			}
			//newGrid[i] = new StringBuilder(W168.cipherLine.substring(i*M, i*M + M));
		}
		return newGrid;
	}
	
	public static void testToRows() {
		System.out.println("[" + W168.cipherLine + "]");
		for (int N : new int[] {1,2,3,6}) {
			System.out.println("========== " + N + ":");
			System.out.println(Arrays.toString(toRows(N)));
		}
	}
	
	public static void testApplyGridPermutation() {
		System.out.println(W168.cipherLine);
		//System.out.println(applyGridPermutationOLD(toRows(2), new int[][] {{0,1},{2,3}}));
		//System.out.println(applyGridPermutation(toRows(2), new int[][] {{0,1},{2,3}}));
		//System.out.println(applyGridPermutation(toRows(2), new int[][] {{3,2},{1,0}}));
		int[][] grid1 = new int[][] {{0,3},{2,1}};
		System.out.println(applyGridPermutation(W168.cipherBuilder, new int[][] {{0,1},{2,3}}, false));
		System.out.println(applyGridPermutation(W168.cipherBuilder, new int[][] {{3,2},{1,0}}, false));
		System.out.println(applyGridPermutation(W168.cipherBuilder, new int[][] {{0,1,2},{3,4,5},{6,7,8}}, false));
		System.out.println(applyGridPermutation(W168.cipherBuilder, new int[][] {{0,1,2},{3,4,5},{6,7,8}}, true));
		System.out.println(applyGridPermutation(W168.cipherBuilder, new int[][] {{8,7,6},{5,4,3},{2,1,0}}, false));
		System.out.println(applyGridPermutation(W168.cipherBuilder, new int[][] {{8,7,6},{5,4,3},{2,1,0}}, true));
		System.out.println(applyGridPermutation(W168.cipherBuilder, new int[][] {{2, 5, 4, 3, 6, 0, 1}}, false));
		StringBuilder sb = applyGridPermutation(StringUtils.toStringBuilder(W168.Z408_PT), grid1, true);
		System.out.println(sb);
		System.out.println(applyGridPermutation(StringUtils.toRows(sb), grid1, true));
		
	}
	
	
	public static void bruteSearch(StringBuilder[] cipher) {
		
		// all subgrid dimensions under consideration
		int[][] dimensions = new int[][] { { 1, 2 }, // 2
				{ 2, 1 }, // 2
				//{ 1, 3 }, // 6
				{ 3, 1 }, // 6
				{ 1, 4 }, // 24
				{ 2, 2 }, // 24
				//{ 1, 5 }, // 120
				//{ 1, 6 }, // 720
				//{ 2, 3 }, // 720
				{ 3, 2 }, // 720
				{ 6, 1 }, // 720
				{ 1, 7 }, // 5040
				//{ 1, 8 }, // 40320
				{ 2, 4 }, // 40320
				//{ 1, 9 }, // 362880
				//{ 3, 3 } // 362880
				{ 3, 4 } // 479001600
		};
		
		// generate all row permutations for the cipher
		StringBuilder[][] rowPermutations;
		rowPermutations = new StringBuilder[720][6];
//		StateRowPermutations s = new StateRowPermutations(6, cipher, rowPermutations);
//		Permutations.recurse(s);
//		
//		for (int[] dimension : dimensions) {
//			System.out.println("==== SUB-GRID N=" + dimension[0] + ", M=" + dimension[1]);
//			for (int r = 0; r<rowPermutations.length; r++) {
//				bruteSearch(dimension[0], dimension[1], rowPermutations[r], r);
//				
//			}
//		}
	}
	/** look through all permutations of NxM subgrids for the best plaintexts, across all permutations of the 6 cipher rows */
	public static void bruteSearch(int N, int M, StringBuilder[] cipher, int rowPermutation) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		StateSubGrid s = new StateSubGrid(N, M, rowPermutation, cipher);
		Permutations.recurse(s);
	}
	public static void main(String[] args) {
//		candidateGridSizes(700000); // accounting for 720 row permutations
//		candidateGridSizesFactors();
//		System.out.println(Runtime.getRuntime().availableProcessors());
//		System.out.println(ForkJoinPool.commonPool());
//		testToRows();
//		testApplyGridPermutation();
//		testPermutationEncode();
//		bruteSearch(2, 2);

		// z408: String ct = "IIKKIINPELEECSET  SMU F.  IML  ELL GPOB UAI SI OHCNUTI SO F TN LLG LDAMINHEORT CAE NERNUAHIKNIIWG  ET F SEEBSUAM  T MT NGOUANALF L.O LLOMHI SIEHSOADRE SMIO LAT IKS TEGN"
		// sam1: 
		//String ct = " TITT.NEPLL I TLAN ELHTT .EL  EF ALOSOTGL T A NESEDESEYMLEN.ES R   EGNGIIFTN RMALOT SAPMOTLIG OES GOOSN TTAY ITHEM S APLT EEDERXDFIDJCE MSDUMD  .S PEV OSHOMISOIUS TRR S";
		//bruteSearch(W168.toRows(new StringBuilder(
				//ct)));
//		bruteSearch(W168.cipherBuilder);
		
//		bruteSearchJarl();
	}
}
