package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.util.Permutations;

/** A stencil (or grille) used to encode and decode W168-like messages.
 * 
 *  A stencil is an ordered list of (row,col) positions that determine the order plaintext is written.
 *  The stencil is applied to a plaintext grid to produce a ciphertext grid.
 *  The stencil is placed in the upper left corner of the ciphertext grid.
 *  Plaintext letters are written to the corresponding (row,col) positions.
 *  When all stencil positions are filled, the stencil is moved to the right one position at a time until no overlaps are detected.
 *  Process is repeated until stencil reaches rightmost edge.
 *  The stencil is returned to the left of the grid, one row down.
 *  Stencil keeps moving down until no overlaps are detected.
 *  Then the encryption process continues like this until the entire cipher grid is filled.
 *  
 *  Variations:
 *   - Apply Caesar shift every [x] [columns/rows] before decrypting with the stencil
 *   - Apply Caesar shift every [x] [columns/rows] after decrypting with the stencil (assuming we write plaintext into grid of same dimensions)
 *      
 **/
public class StencilTransposition {
	// locations of the holes.  given in (row,col) tuples, relative to 0,0
	public int[][] holes; 
	// ordering of the holes.  
	//public int[] order;
	
	int[][] ctPositions;
	
	/** if we initialize from a template, store a reference here */
	public int[] patternHorizontal;
	public int[] patternVertical;
	
	/** optional caesar shift */
	ShiftType shiftType;
	
	/** optional shift pattern */
	boolean[] shiftPattern;
	
	/** dimensions of valid (proper or regular) stencils */
	public static int[][] validStencilsDimensions = new int[][] { { 2, 2 }, { 2, 3 }, { 2, 8 }, { 2, 15 }, { 4, 2 }, { 4, 3 },
		{ 4, 8 }, { 4, 15 } };

	public StencilTransposition(int[][] holes) {
		//if (holes.length != order.length) throw new IllegalArgumentException("Must be as many holes as order entries.");
		this.holes = holes;
	}
	
	/** decode the given cipher text using this stencil */
	public StringBuilder[] decode(StringBuilder[] cipher) {
		if (cipher == null) return null;

		int[] rowcol = new int[] {0,0}; // current stencil position in the cipher text block
		// track visited positions, to avoid revisiting them.
		Map<Integer, Set<Integer>> seen = new HashMap<Integer, Set<Integer>>();
		
		int H = cipher.length; 
		int W = cipher[0].length();
		int L = H*W;
		StringBuilder[] pt = new StringBuilder[H];
		for (int i=0; i<H; i++) pt[i] = new StringBuilder();
		boolean go = true;
		int pos = 0;
		while (pos < L && go) {
			for (int[] hole : holes) {
				int cipherRow = rowcol[0] + hole[0];
				int cipherCol = rowcol[1] + hole[1];
				pt[pos/W].append(cipher[cipherRow].charAt(cipherCol));
				pos++;
				saw(seen, cipherRow, cipherCol);
			}
			go = moveStencil(rowcol, seen, H, W);
		}
		if (pos < L) return null;
		return pt;
	}
	/** encode the given cipher text using this stencil */
	public StringBuilder[] encode(StringBuilder[] plaintext) {
		if (plaintext == null) return null;
		int[] rowcol = new int[] {0,0}; // current stencil position in the cipher text block
		int pos = 0;
		// track visited positions, to avoid revisiting them.
		Map<Integer, Set<Integer>> seen = new HashMap<Integer, Set<Integer>>();
		
		int H = plaintext.length; 
		int W = plaintext[0].length();
		int L = H*W;
		StringBuilder[] ct = new StringBuilder[H];
		// track positions from original plaintext (this is the transposition matrix)
		ctPositions = new int[H][W];
		for (int i=0; i<H; i++) {
			ct[i] = new StringBuilder();
			ctPositions[i] = new int[W];
			for (int j=0; j<W; j++) ct[i].append(" ");
		}
		boolean go = true;
		while (pos < L && go) {
			StringBuilder slice = new StringBuilder();
			
			// h = number of holes
			// get next h chars from plaintext (call them a "slice")
			// stream them into the stencil shape but in the new order
			for (int i = 0; i < holes.length; i++) {
				//slice.append(plaintext[pos / W].charAt(pos % W));
				//pos++;
				int[] hole = holes[i];
				int cipherRow = rowcol[0] + hole[0];
				int cipherCol = rowcol[1] + hole[1];
				if (cipherRow >= H) return null; // bounds failure
				if (cipherCol >= W) return null; // bounds failure
				int ptRow = pos / W;
				int ptCol = pos % W;
				ct[cipherRow].setCharAt(cipherCol, plaintext[ptRow].charAt(ptCol));
				ctPositions[cipherRow][cipherCol] = pos;
				pos++;
				saw(seen, cipherRow, cipherCol);
			}
			go = moveStencil(rowcol, seen, H, W);
		}
		if (pos < L) return null;
		return ct;
	}
	
	static void saw(Map<Integer, Set<Integer>> seen, int row, int col) {
		Set<Integer> set = seen.get(row);
		if (set == null) {
			set = new HashSet<Integer>();
			seen.put(row, set);
		}
		set.add(col);
	}
	static boolean seen(Map<Integer, Set<Integer>> seen, int row, int col) {
		Set<Integer> set = seen.get(row);
		if (set == null) return false;
		return set.contains(col);
	}
	boolean moveStencil(int[] rowcol, Map<Integer, Set<Integer>> seen, int H, int W) {
		// first, try horizontal
		boolean success = moveStencilRight(rowcol, seen, H, W);
		if (!success) {
			// couldn't move right, so reset column to 0 and try to move down.
			rowcol[1] = 0;
			success = moveStencilDown(rowcol, seen, H, W);
		}
		return success && rowcol[0] < H && rowcol[1] < W; // bounds test, too
	}
	
	// try to move the stencil to the right until completely unseen positions are visited, or boundary reached.
	// returns false if no further moves possible.
	boolean moveStencilRight(int[] rowcol, Map<Integer, Set<Integer>> seen, int H, int W) {
		int dx = 1;
		boolean go = true;
		boolean success = true;
		while (go) {
			success = true;
//			System.out.println("move right");
//			System.out.println("dx: " + dx);
			for (int[] hole : holes) {
				//System.out.println("- hole: " + Arrays.toString(hole));
				int newRow = hole[0] + rowcol[0];
				int newCol = hole[1] + rowcol[1] + dx;
				//System.out.println("- new: " + newRow + "," + newCol);
				if (newCol >= W) return false;
				if (seen(seen, newRow, newCol)) {
					//System.out.println("Already visited this!");
					dx++;
					success = false;
					break;
				}
			}
			if (success) {
				rowcol[1] = rowcol[1] + dx;
//				System.out.println("- successfully moved to col: " + rowcol[1]);
				go = false;
			}
		}
		return true;
	}
	// try to move the stencil down until completely unseen positions are visited, or boundary reached.
	// returns false if no further moves possible.
	boolean moveStencilDown(int[] rowcol, Map<Integer, Set<Integer>> seen, int H, int W) {
//		System.out.println("move down");
		int dy = 1;
		boolean go = true;
		while (go) {
//			System.out.println("dy: " + dy);
			go = false;
			for (int[] hole : holes) {
				int newRow = hole[0] + rowcol[0] + dy;
				int newCol = hole[1] + rowcol[1];
				if (newRow >= H) return false;
				if (seen(seen, newRow, newCol)) {
					dy++;
					go = true;
					break;
				}
			}
		}
		rowcol[0] = rowcol[0] + dy;
		return true;
	}
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int[] hole : holes) {
			str.append(Arrays.toString(hole)).append( " ");
		}
		return str.toString();
	}
	
	public static void testEncodeDecode() {
		
		int[][] holes3x7 = new int[21][2];
		int i = 0;
		for (int row=0; row<3; row++) {
			for (int col=0; col<7; col++) {
				holes3x7[i] = new int[] {(row + 1) % 3, (col + 3) % 7}; // shift the grid
				i++;
			}
		}
		
		String[] plaintexts = new String[] {
				"ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKL",
				"I LIKE KILLING PEOPLE BECAUSE IT IS SO MUCH FUN. IT IS MORE FUN THAN KILLING WILD GAME IN THE FOREST BECAUSE MAN IS THE MOST DANGEROUS ANIMAL OF ALL. TO KILL SOMETHING " };
		StencilTransposition[] stencils = new StencilTransposition[] { new StencilTransposition(new int[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } }),
				new StencilTransposition(new int[][] { { 0, 0 }, { 1, 1 }, { 1, 0 }, { 0, 1 } }),
				new StencilTransposition(holes3x7)};
		for (String plaintext : plaintexts) {
			for (StencilTransposition stencil : stencils) {
				System.out.println("- Plaintext: " + plaintext);
				System.out.println("- Stencil: " + stencil);
				StringBuilder[] ct;
				StringBuilder[] pt;
				pt = StringUtils.toRows(new StringBuilder(plaintext));
				ct = stencil.encode(pt);
				System.out.println("Encoded: " + Arrays.toString(ct));
				System.out.println(" - transposition matrix: " + stencil.ctPositions());
				System.out.println(" - periods (distances): ");
				stencil.ctPositionsPeriods();
				pt = stencil.decode(ct);
				System.out.println("Decoded: " + Arrays.toString(pt));
				if (testCheckValid(plaintext, pt)) {
					System.out.println("- Good - They match.");
				} else {
					System.out.println(" ======== ERROR!!!!! PLAINTEXTS DON'T MATCH!!!!    ====== ");
				}
				System.out.println();
			}
		}
		
//		System.out.println(Arrays.toString(stencil.decode(W168.toStringBuilder(new String[] {
//				"IIKKIINPELEECSET  SMU F.  IM", "L  ELL GPOB UAI SI OHCNUTI S", "O F TN LLG LDAMINHEORT CAE N",
//				"ERNUAHIKNIIWG  ET F SEEBSUAM", "  T MT NGOUANALF L.O LLOMHI ", "SIEHSOADRE SMIO LAT IKS TEGN" }))));
	}

	public static void testBestW168() {
		int[][] holes3x7 = new int[][] { { 0, 2 }, { 1, 2 }, { 1, 1 }, { 1, 3 }, { 0, 3 }, { 1, 5 }, { 1, 4 }, { 2, 4 },
				{ 2, 3 }, { 2, 2 }, { 2, 5 }, { 1, 6 }, { 0, 1 }, { 0, 0 }, { 2, 6 }, { 2, 0 }, { 2, 1 }, { 0, 5 },
				{ 0, 6 }, { 0, 4 }, { 1, 0 } };
		StencilTransposition stencil = new StencilTransposition(holes3x7);
		StringBuilder[] pt = stencil.decode(W168.cipherBuilder);
		System.out.println("Decoded: " + Arrays.toString(pt));
	}
	
	public static boolean testCheckValid(String p1, StringBuilder[] p2) {
		StringBuilder p3 = new StringBuilder();
		for (StringBuilder s : p2) p3.append(s);
		return p1.equals(p3.toString());
	}
	

	// try all possible subgrid sizes to find the "proper" ones (that cover all
	// positions based on our movement rules)
	public static void testFindProperStencilSizes() {
		String plaintext = "I LIKE KILLING PEOPLE BECAUSE IT IS SO MUCH FUN. IT IS MORE FUN THAN KILLING WILD GAME IN THE FOREST BECAUSE MAN IS THE MOST DANGEROUS ANIMAL OF ALL. TO KILL SOMETHING ";
		System.out.println("- Plaintext: " + plaintext);
		StringBuilder[] pt = StringUtils.toRows(new StringBuilder(plaintext));
		for (int N=2; N<=6; N++) {
			for (int M=2; M<=28; M++) {
				//System.out.println("Trying " + N + "x" + M + "...");
				StencilTransposition stencil = new StencilTransposition(new int[][] { { 0, 0 }, { 0, M - 1 }, { N - 1, 0 }, { N - 1, M - 1 } });
				StringBuilder[] ct = stencil.encode(pt);
				if (ct == null) continue;
				System.out.println(N + "x" + M + " Stencil: " + stencil);
				System.out.println("Encoded: " + Arrays.toString(ct));
				StringBuilder[] ptNew = stencil.decode(ct);
				System.out.println("Decoded: " + Arrays.toString(ptNew));
				System.out.println();
			}
		}
	}
	public String ctPositions() {
		StringBuilder sb = new StringBuilder();
		for (int[] row : ctPositions) sb.append(Arrays.toString(row));
		return sb.toString();
	}
	
	public static HashMap<Integer, Integer> sortByValue(Map<Integer, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, Integer> > list =
               new LinkedList<Map.Entry<Integer, Integer> >(hm.entrySet());
 
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer> >() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
         
        // put data from sorted list to hashmap
        HashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }	
	public static HashMap<String, Integer> sortByValue2(Map<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
               new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());
 
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
         
        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }	

	public static int adjust(int distance, int length) {
		if (distance < 0) distance += length;
		if (distance >= length) distance %= length;
		return distance;
	}
	
	// calculate distances between successive positions in the transposition grid, to look for periodicity.
	public void ctPositionsPeriods() {
		// map pt position to ct position
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		int H = ctPositions.length;
		int W = ctPositions[0].length;
		int L = H*W;
		int pos = 0;
		
		for (int row=0; row<H; row++) {
			for (int col=0; col<W; col++) {
				map.put(ctPositions[row][col], pos);
				pos++;
			}
		}
		
		// map distances to counts
		Map<Integer, Integer> distances = new HashMap<Integer, Integer>();  
		StringBuilder distanceSequence = new StringBuilder();
		StringBuilder distanceSequenceNeg = new StringBuilder();
		for (int i = 1; i < L; i++) {
			int distance = adjust(map.get(i) - map.get(i - 1), L);
			if (distanceSequence.length() > 0) {
				distanceSequence.append(", ");
				distanceSequenceNeg.append(", ");
			}
			distanceSequence.append(distance);
			distanceSequenceNeg.append(distance-L);
			Integer val = distances.get(distance);
			if (val == null) {
				val = 0;
			}
			val++;
			distances.put(distance, val);
		}
		System.out.println("== Distance sequence: ");
		System.out.println("   " + distanceSequence);
		System.out.println("   " + distanceSequenceNeg);
		distances = sortByValue(distances);
		System.out.println("== Distance single counts:");
		for (int key : distances.keySet()) {
			System.out.println("Distance " + key + " (" + (key-L) + ") count " + distances.get(key));
		}
		
		
		// map distance pairs to counts
		Map<String, Integer> distancePairs = new HashMap<String, Integer>();  
		
		for (int i = 2; i < L; i++) {
			int distance1 = adjust(map.get(i - 1) - map.get(i - 2), L);
			int distance2 = adjust(map.get(i) - map.get(i - 1), L);
			String key = distance1 + " " + distance2 + " (" + (distance1-L) + " " + (distance2-L) + ")";
			Integer val = distancePairs.get(key);
			if (val == null) {
				val = 0;
			}
			val++;
			distancePairs.put(key, val);
		}
		distancePairs = sortByValue2(distancePairs);
		System.out.println("== Distance pair counts:");
		for (String key : distancePairs.keySet()) {
			System.out.println("Distance pair " + key + " count " + distancePairs.get(key));
		}

		// map distance triplets to counts
		Map<String, Integer> distanceTriplets = new HashMap<String, Integer>();  
		
		for (int i = 3; i < L; i++) {
			int distance1 = adjust(map.get(i - 2) - map.get(i - 3), L);
			int distance2 = adjust(map.get(i - 1) - map.get(i - 2), L);
			int distance3 = adjust(map.get(i) - map.get(i - 1), L);
			String key = distance1 + " " + distance2 + " " + distance3 + " (" + (distance1-L) + " " + (distance2-L) + " " + (distance3-L) + ")";
			Integer val = distanceTriplets.get(key);
			if (val == null) {
				val = 0;
			}
			val++;
			distanceTriplets.put(key, val);
		}
		distanceTriplets = sortByValue2(distanceTriplets);
		System.out.println("== Distance triplet counts:");
		for (String key : distanceTriplets.keySet()) {
			System.out.println("Distance triplet " + key + " count " + distanceTriplets.get(key));
		}

		
	}
	
	public static void permuteW168() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");

		
		StateRowPermutations rp = new StateRowPermutations(StringUtils.toStringBuilder(W168.W168));
		Permutations.recurse(rp);
		
		for (StringBuilder ct[] : rp.permutations) {
			System.out.println("Current row permutation: " + Arrays.toString(ct));
			for (int[] stencil : validStencilsDimensions) {
				int N = stencil[0];
				int M = stencil[1];
				StateStencil s = new StateStencil(
						new StencilTransposition(new int[][] { { 0, 0 }, { 0, M - 1 }, { N - 1, 0 }, { N - 1, M - 1 } }),
						W168.cipherBuilder);
				Permutations.recurse(s);
			}
		}
	}
	
	/** compute subgrid height based on holes (or patterns if present) */
	public int height() {
		if (patternVertical != null) return patternVertical.length;
		int max = Integer.MIN_VALUE;
		for (int[] hole : holes)
			max = Math.max(max, hole[0]);
		return max+1;
	}
	
	/** compute subgrid width based on holes (or patterns if present) */
	public int width() {
		if (patternHorizontal != null) return patternHorizontal.length;
		int max = Integer.MIN_VALUE;
		for (int[] hole : holes)
			max = Math.max(max, hole[1]);
		return max+1;
	}
	
	public String dimensions() {
		return height() + "x" + width();
	}
	
	/** convert the stencil holes to an HTML representation */
	public String htmlHoles() {
		
		// produce the subgrid and compute ordinal values for the holes
		int N = height();
		int M = width();
		
		int[][] subgrid = new int[N][M];
		for (int row=0; row<N; row++) subgrid[row] = new int[M];

		for (int i=0; i<holes.length; i++) {
			int row = holes[i][0];
			int col = holes[i][1];
			subgrid[row][col] = i+1;
		}
		
		String html = "<table class=\"holes\">";
		for (int row=0; row<N; row++) {
			html += "<tr>";
			for (int col=0; col<M; col++) {
				html += "<td>";
				if (subgrid[row][col]>0)
					html += subgrid[row][col];
				else 
					html += "&nbsp;";
				html += "</td>";
			}
			html += "</tr>";
		}
		html += "</table>";
		return html;
	}
	/** convert the transposition matrix to an HTML representation */
	public String htmlTranspositionMatrix() {
		
		String html = "<table class=\"transpo\">";
		for (int row=0; row<6; row++) {
			html += "<tr>";
			for (int col=0; col<28; col++) {
				html += "<td>";
				//html += toString(ctPositions[row][col]);
				html += (ctPositions[row][col]+1);
				html += "</td>";
			}
			html += "</tr>";
		}
		html += "</table>";
		return html;
	}
	
	public static String toString(int val) {
		if (val < 10) return "0" + val;
		return "" + val;
	}
	
	public static void main(String[] args) {
		testEncodeDecode();
//		test_3x7_Z408();
//		testFindProperStencilSizes();
		//permuteW168();
//		testBestW168();
	}
}
