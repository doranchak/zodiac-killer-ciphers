package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.TreeSet;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.util.PermutationState;
import com.zodiackillerciphers.util.Permutations;

public class StateSubGrid implements PermutationState {

	private int[][] subgrid;
	private int N; // height (rows)
	private int M; // width (cols)
	private StringBuilder[] cipher;
	
	// one heap for all permutations.  NOTE: not threadsafe.
	public static TreeSet<Float> treeSet = new TreeSet<Float>();
	int maxHeapSize = 10000;
	static String TAB = "	";
	public int rowPermutation; // which row permutation are we using
	
	public StateSubGrid(int N, int M, int rowPermutation, StringBuilder[] cipher) {
		this.N = N;
		this.M = M;
		this.subgrid = new int[N][M];
		this.rowPermutation = rowPermutation;
		this.cipher = cipher;
		int i = 0;
		for (int row=0; row<N; row++) {
			for (int col=0; col<M; col++) {
				subgrid[row][col] = i++;
			}
		}
	}

	@Override
	public void action() {
		//String arr = "";
		//for (int[] row : subgrid) arr += Arrays.toString(row) + " ";
		//System.out.println(arr);
		
		// apply the permutation
		//StringBuilder sb = GridPermutations.applyGridPermutation(rowPermutations[rowPermutation], subgrid, true);
		StringBuilder[] sb = new StringBuilder[0]; // = GridPermutations.permutationDecode(cipher, subgrid);
		StringBuilder pt = new StringBuilder();
		for (StringBuilder s : sb) pt.append(s);
		// score the text
		float scorePt = NGramsCSRA.zkscore(new StringBuffer(pt.toString()), "EN", true);
		
		boolean print = false;
		// if heap not full, just add it
		if (treeSet.size() < maxHeapSize) {
			treeSet.add(scorePt);
			//System.out.println("TREE SIZE NOW " + treeSet.size());
			print = true;
		} else {
			// tree already has this score?  then ignore;
			if (treeSet.contains(scorePt)) {
				;
			} else {
				// is this score better than the worst score? 
				Float worst = treeSet.first();
				if (scorePt > worst) { // it's better
					print = true;
					if (!treeSet.remove(worst)) throw new RuntimeException("ERROR!  CANNOT REMOVE WORST!"); // and remove the worst score
					if (!treeSet.add(scorePt)) throw new RuntimeException("ERROR!  CANNOT ADD SCORE!"); // so add to heap
					//System.out.println("TREE REMOVING WORST " + worst + " BECAUSE BETTER ONE IS " + scorePt);
				}
			}
		}
		
		//if (print) System.out.println(scorePt + TAB + treeSet + TAB + Arrays.toString(elements) + TAB + pt);
		if (print) System.out.println(scorePt + TAB + N+"x"+M + TAB + (N*M) + TAB + rowPermutation + TAB + toString(subgrid) + TAB + pt);
		
	}
	public static StringBuilder toString(int[][] grid) {
		StringBuilder sb = new StringBuilder();
		for (int[] row : grid) sb.append(Arrays.toString(row));
		return sb;
	}

	@Override
	public void swap(int pos1, int pos2) { // recursion is on a 1D array so convert it to 2D 

		int row1 = pos1/M;
		int col1 = pos1%M;
		
		int row2 = pos2/M;
		int col2 = pos2%M;
		
		int tmp = subgrid[row1][col1];
		subgrid[row1][col1] = subgrid[row2][col2];
		subgrid[row2][col2] = tmp;
	}

	@Override
	public int numElements() {
		// TODO Auto-generated method stub
		return N*M;
	}
	
	public static void test() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		
		StateSubGrid s = new StateSubGrid(2, 2, 0, StringUtils.toRows(new StringBuilder(
				"IIKKIINPELEECSET  SMU F.  IML  ELL GPOB UAI SI OHCNUTI SO F TN LLG LDAMINHEORT CAE NERNUAHIKNIIWG  ET F SEEBSUAM  T MT NGOUANALF L.O LLOMHI SIEHSOADRE SMIO LAT IKS TEGN")));
		Permutations.recurse(s);
	}
	
	public static void main(String[] args) {
		test();
	}

}
