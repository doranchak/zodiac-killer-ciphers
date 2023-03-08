package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.Random;

/** An attempt to enumerate all or most of the possible stencil shapes and patterns that can
 * cover the entire grid properly when following the stencil movement rules.
 */
public class StencilTranspositionTemplates {

	/*****************************************************************************************
	 * 
	 * A stencil must be rectangular in shape.
	 * 
	 * Consider all 1D possibilities for grid width W=28. (Order does not matter, we
	 * are simply considering "holes" in the stencil and which arrangements are
	 * valid.) We also assume all stencils start being applied to grid at (0,0).
	 * 
	 * - Stencil size: 1x1: Trivial case, ignored. 
	 * - Stencil size: 2 holes (with and without gaps): 
	 * 		XX: valid (every 1 placement, stencil has filled 2 positions, then stencil jumps 2 positions) 
	 * 		X_X: valid (every 2 placements, stencil has filled 4 positions, then stencil jumps 3 positions)
	 *      X__X: invalid (every 3 placements, stencil has filled 6 positions, then stencil jumps 4 positions)
	 * 		Let g = gap size
	 * 		Let p = num positions filled when stencil pattern is "complete"
	 * 		Thus p = 2(g+1), and g=p/2 - 1 
	 * 		p must divide into W evenly.
	 * 		For W=28, p={1,2,4,7,14,28}
	 * 		Thus valid g={0,1,6,13}
	 * Just did the rest of the analysis on paper, tested all possible stencil hole patterns 
	 * for heights [1,6] and widths [1,7]
	 * 
	 ******************************************************************************************/

	/** all possible horizontal patterns, up to width 7 */
	public static int[][] patternsHorizontal = new int[][] {
		{1},
		{1,1},
		{1,0,1},
		{1,1,1,1},
		{1,1,1,1,1,1,1}
	};
	/** all possible vertical patterns, up to height 6 */
	public static int[][] patternsVertical = new int[][] {
		{1},
		{1,1},
		{1,1,1},
		{1,0,0,1},
		{1,0,1,0,1},
		{1,1,1,1,1,1}
	};
	
	public static Random random = new Random();
	
	/** generate a random stencil, using a random pattern from the collection of templates.
	 *  randomize the hole order.
	 */
	public static StencilTransposition randomStencil() {
		int[] patternHorizontal = null;
		int[] patternVertical = null;
		while (true) {
			patternHorizontal = patternsHorizontal[random.nextInt(patternsHorizontal.length)];
			patternVertical = patternsVertical[random.nextInt(patternsVertical.length)];
			if (patternHorizontal.length > 1 || patternVertical.length > 1) break; // don't allow 1x1
		}
		
		int N=patternVertical.length;
		int holeCountVertical = 0;
		for (int hole : patternVertical) if (hole == 1) holeCountVertical++; 
		
		int M=patternHorizontal.length;
		int holeCountHorizontal = 0;
		for (int hole : patternHorizontal) if (hole == 1) holeCountHorizontal++; 

		
		//int TOTAL=N*M;
		int TOTAL_HOLES=holeCountVertical*holeCountHorizontal;
		
		int[][] holes = new int[TOTAL_HOLES][2];
		
		int which = 0;
		for (int row=0; row<N; row++) {
			for (int col=0; col<M; col++) {
				if (patternVertical[row] == 1 && patternHorizontal[col] == 1) {
					holes[which] = new int[] {row,col};
					which++;
				}
			}
		}
		
//		System.out.println("pattern vertical: " + Arrays.toString(patternVertical));
//		System.out.println("pattern horizontal: " + Arrays.toString(patternHorizontal));
//		System.out.println("holes: ");
//		StringBuilder str = new StringBuilder("	");
//		for (int[] hole : holes) {
//			str.append(Arrays.toString(hole)).append( " ");
//		}
//		System.out.println(str);
//		System.out.println("shuffled: ");
		shuffle(holes);
//		str = new StringBuilder("	");
//		for (int[] hole : holes) {
//			str.append(Arrays.toString(hole)).append( " ");
//		}
//		System.out.println(str);

		StencilTransposition stencil = new StencilTransposition(holes);
		stencil.patternVertical = patternVertical;
		stencil.patternHorizontal = patternHorizontal;
		return stencil;
	}
	
	public static void shuffle(int[][] arr) {
		for (int a=0; a<arr.length-1; a++) { // arr is indexed from [0,len-1].  a counts from [0,len-2]
			// pick random element from range [a, len-1]
			int range = arr.length-a;
			int b = a + random.nextInt(range);
			if (a==b) continue; // no swap
			// swap a with b
			int[] tmp = arr[a];
			arr[a] = arr[b];
			arr[b] = tmp;
		}
	}
	
	public static void testRandomStencil() {
		for (int i=0; i<10; i++) { 
			StencilTransposition s = randomStencil();
			System.out.println(s);
		}
	}
	
	public static void main(String[] args) {
		testRandomStencil();
	}
	
}
