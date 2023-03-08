package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.TreeSet;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.util.PermutationState;
import com.zodiackillerciphers.util.Permutations;

/** for permutations of stencil orderings */
public class StateStencil implements PermutationState {

	private StencilTransposition stencil;
	private StringBuilder[] cipher;
	
	private int counter;
	public static TreeSet<Float> treeSet = new TreeSet<Float>();
	int maxHeapSize = 10000;
	static String TAB = "	";
	
	
	public StateStencil(StencilTransposition stencil, StringBuilder[] cipher) {
		this.stencil = stencil;
		this.cipher = cipher;
		for (int i = 0; i < this.stencil.holes.length; i++) ;
			//this.stencil.order[i] = i;
	}

	@Override
	public void action() {
		StringBuilder[] sb = stencil.decode(cipher);
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
		
		if (print) System.out.println(scorePt + TAB + stencil + TAB + pt);
		
	}

	@Override
	public void swap(int pos1, int pos2) { // recursion is on a 1D array so convert it to 2D
		//int tmp = stencil.order[pos1];
		//stencil.order[pos1] = stencil.order[pos2];
		//stencil.order[pos2] = tmp;
	}

	@Override
	public int numElements() {
		// TODO Auto-generated method stub
		return stencil.holes.length;
	}
	
	public static void test() {
		StateStencil s = new StateStencil(
				//new Stencil(new int[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } }, new int[] { 0, 1, 2, 3 }),
				new StencilTransposition(new int[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } }),
				W168.cipherBuilder);
		Permutations.recurse(s);
	}
	
	public static void main(String[] args) {
		test();
	}

}
