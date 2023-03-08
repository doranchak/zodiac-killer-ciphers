package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTransposition;

public class GridTranspositionCipher {

	/** the "flavor" of transposition */
	Variant variant;
	/** the plaintext */
	StringBuilder[] plaintext;
	/** the ciphertext */
	StringBuilder[] ciphertext;
	/** the transposition grid */
	int[][] subgrid;

	public GridTranspositionCipher(Variant variant, StringBuilder[] plaintext, StringBuilder[] ciphertext,
			int[][] subgrid) {
		super();
		this.variant = variant;
		this.plaintext = plaintext;
		this.ciphertext = ciphertext;
		this.subgrid = subgrid;
	}

	public void encode() {
		if (this.variant == Variant.SCHEME_1) encodeScheme1();
	}
	
	public void decode() {
		if (this.variant == Variant.SCHEME_1) decodeScheme1();
		
	}
	
	// decode the given grid of ciphertext using the pattern given in the subgrid.
	public void decodeScheme1() {
		// make a cipher grid
		plaintext = new StringBuilder[ciphertext.length];
		for (int row = 0; row < ciphertext.length; row++) {
			plaintext[row] = new StringBuilder();
			for (int col = 0; col < ciphertext[0].length(); col++) {
				plaintext[row].append(" ");
			}
		}

		// ciphertextGrid dimensions
		int Y = ciphertext.length;
		int X = ciphertext[0].length();

		int N = subgrid.length;
		int M = subgrid[0].length;
		int G = N * M;

		int newPos = 0;

		for (int row = 0; row < ciphertext.length; row++) {
			for (int col = 0; col < ciphertext[0].length(); col++) {

				// convert to 1D position
				int pos = row * X + col;

				// which sub-grid are we on?
				int gridNumber = pos / G;

				// locate the upper left coordinate of this sub-grid
				int rowUL = gridNumber * M / X * N;
				int colUL = gridNumber * M % X;

				// which element of the grid are we on?
				int g = pos % G; // the g'th element

				// convert to sub-grid row and col
				int gridRow = g / M;
				int gridCol = g % M;

				// get the element there
				int element = subgrid[gridRow][gridCol];

				// convert it to the transposed row and col
				gridRow = element / M;
				gridCol = element % M;

				// adjust the source row and col accordingly
				int ptRow = rowUL + gridRow;
				int ptCol = colUL + gridCol;

				int newRow = newPos / X;
				int newCol = newPos % X;
				newPos++;

				// System.out.println("row " + row + " col " + col + " pos " + pos + " g " + g +
				// " gridNumber "
				// + gridNumber + " rowUL " + rowUL + " colUL " + colUL + " g " + g + " element
				// " + element
				// + " gridRow " + gridRow + " gridCol " + gridCol + " ptRow " + ptRow + " ptCol
				// " + ptCol + " newRow " + newRow + " newCol " + newCol);

				plaintext[newRow].setCharAt(newCol, ciphertext[ptRow].charAt(ptCol));
			}
		}
	}

	// encode the given grid of plaintext using the pattern given in the subgrid.
	public void encodeScheme1() {
		// make a cipher grid
		ciphertext = new StringBuilder[plaintext.length];
		for (int row = 0; row < plaintext.length; row++) {
			ciphertext[row] = new StringBuilder();
			for (int col = 0; col < plaintext[0].length(); col++) {
				ciphertext[row].append(" ");
			}
		}

		// plaintext dimensions
		int Y = plaintext.length;
		int X = plaintext[0].length();

		int N = subgrid.length;
		int M = subgrid[0].length;
		int G = N * M;
		for (int row = 0; row < plaintext.length; row++) {
			for (int col = 0; col < plaintext[0].length(); col++) {

				// convert to 1D position
				int pos = row * X + col;

				// which sub-grid are we on?
				int gridNumber = pos / G;

				// locate the upper left coordinate of this sub-grid
				int rowUL = gridNumber * M / X * N;
				int colUL = gridNumber * M % X;

				// which element of the grid are we on?
				int g = pos % G; // the g'th element

				// convert to sub-grid row and col
				int gridRow = g / M;
				int gridCol = g % M;

				// get the element there
				int element = subgrid[gridRow][gridCol];

				// convert it to the transposed row and col
				gridRow = element / M;
				gridCol = element % M;

				// adjust the target row and col accordingly
				int newRow = rowUL + gridRow;
				int newCol = colUL + gridCol;

				/*
				 * System.out.println("row " + row + " col " + col + " pos " + pos + " g " + g +
				 * " gridNumber " + gridNumber + " rowUL " + rowUL + " colUL " + colUL + " g " +
				 * g + " element " + element + " gridRow " + gridRow + " gridCol " + gridCol +
				 * " newRow " + newRow + " newCol " + newCol);
				 */
				ciphertext[newRow].setCharAt(newCol, plaintext[row].charAt(col));
			}
		}
	}

	public static void testPermutationEncode() {
		// int[][] subgrid = new int[][] {{0,3},{2,1}};
		// StringBuilder[] ct = permutationEncode(W168.toStringBuilder(W168.J168_1_PT),
		// subgrid);
		// StringBuilder[] pt = permutationDecode(ct, subgrid);
		// System.out.println(Arrays.toString(ct));
		// System.out.println(Arrays.toString(pt));

		for (int i = 0; i < 1000; i++) {
			int[][] subgrid = randomSubGrid();
			StringBuilder[] ciphertext = new StringBuilder[6];
			GridTranspositionCipher cipher = new GridTranspositionCipher(Variant.SCHEME_1, StringUtils.toStringBuilder(W168.Z408_PT), ciphertext,
					subgrid);			
			
			// System.out.println(StateSubGrid.toString(subgrid));
			System.out.println("Transposition grid: " + subgrid.length + "x" + subgrid[0].length);
			System.out.println();
			dump(subgrid);
			System.out.println();
			//StringBuilder[] ct = encodeScheme1(W168.toStringBuilder(W168.J168_1_PT), subgrid);
			cipher.encode();
			dump(cipher.ciphertext);
			//StringBuilder[] pt = permutationDecode(ct, subgrid);
			System.out.println();
			cipher.decode();
			dump(cipher.plaintext);
			System.out
					.println("======================================================================================");
			// System.out.println(Arrays.toString(ct));
			// System.out.println(Arrays.toString(pt));
			if (!match(cipher.plaintext, StringUtils.toStringBuilder(W168.Z408_PT))) {
				dump(StringUtils.toStringBuilder(W168.Z408_PT));
				throw new RuntimeException("THEY DO NOT MATCH!");
			}
		}

		// best jarl decode
		int[][] subgrid = new int[][] {
				{ 17, 22, 54, 55, 39, 2, 42, 11, 3, 52, 45, 27, 21, 31, 50, 30, 10, 23, 53, 24, 32, 36, 20, 25, 7, 34,
						46, 44 },
				{ 49, 19, 40, 18, 51, 12, 5, 33, 43, 6, 29, 26, 38, 1, 37, 16, 35, 9, 4, 47, 14, 15, 8, 41, 48, 13, 0,
						28 } };
//		dump(permutationDecode(W168.cipherBuilder, subgrid));
//		dump(subgrid);

	}

	/** return a randomly generated transposition subgrid */
	public static int[][] randomSubGrid() {
		int[] dimensions = randomSubGridDimension();

		int N = dimensions[0];
		int M = dimensions[1];
		int count = N * M;
		int[] vals = new int[count];
		for (int i = 0; i < count; i++)
			vals[i] = i;
		// shuffle the array
		ColumnarTransposition.shuffle(vals);

		int[][] subgrid = new int[N][M];
		int i = 0;
		for (int row = 0; row < N; row++) {
			subgrid[row] = new int[M];
			for (int col = 0; col < M; col++) {
				subgrid[row][col] = vals[i++];
			}
		}
		return subgrid;
	}

	/** return a randomly selected regular subgrid dimension */
	public static int[] randomSubGridDimension() {
		int[][] dimensions = new int[][] { { 1, 2 }, { 1, 4 }, { 1, 7 }, { 1, 14 }, { 1, 28 }, { 2, 1 }, { 2, 2 },
				{ 2, 4 }, { 2, 7 }, { 2, 14 }, { 2, 28 }, { 3, 1 }, { 3, 2 }, { 3, 4 }, { 3, 7 }, { 3, 14 }, { 3, 28 },
				{ 6, 1 }, { 6, 2 }, { 6, 4 }, { 6, 7 }, { 6, 14 }, { 6, 28 } };
		Random rand = new Random();
		return dimensions[rand.nextInt(dimensions.length)];
	}

	public static void dump(int[][] grid) {
		for (int[] row : grid)
			System.out.println(Arrays.toString(row));
	}

	public static void dump(StringBuilder[] sb) {
		for (StringBuilder s : sb)
			System.out.println(s);
	}

	public static boolean match(StringBuilder[] sb1, StringBuilder[] sb2) {
		if (sb1.length != sb2.length)
			return false;
		for (int i = 0; i < sb1.length; i++) {
			if (!sb1[i].toString().equals(sb2[i].toString()))
				return false;
		}
		return true;
	}
	public static void main(String[] args) {
		testPermutationEncode();
	}

}
