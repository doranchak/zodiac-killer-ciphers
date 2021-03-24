package com.zodiackillerciphers.ciphers;

public class PrimerTranspositions {

	String symbols = "DESJULYAUGSEPTOCT";
	int[][] columns = {
			{2,8,15},
			{1,6,9},
			{0,3,7,10},
			{5},
			{0,3,6,8,12},
			{4,12,15,16},
			{9,10},
			{12,13},
			{0,3,6,8,12},
			{1,8,11,14,15},
			{0,3,7,10},
			{1,6,9},
			{8,11},
			{2,9,12,13},
			{6,7,8,9,10,11,16},
			{4,7,8,10,15},
			{2,9,12,13}
	};
	
	String symbols3 = "BSORIETEMSTHHPITI";
	int[][] columns3 = {
			{5},
			{0,3,7,10},
			{7,8,9,10,11,16},
			{0,2,4,7,14,16},
			{0,7,8,10,13,14,16},
			{1,9},
			{2,9,12,13},
			{1,9},
			{1,2,4,11,13,14},
			{0,3,7,10},
			{2,9,12,13},
			{0,3,11,15},
			{0,3,11,15},
			{8,11},
			{0,7,8,10,13,14,16},
			{2,9,12,13},
			{6}
	};
	
	String symbols2 = "VEX¼W°ÑEHM£uË"; // last row of the 408.  symbols that aren't in the 340 are removed.
	int[][] columns2 = { // each of these is the list of columns in which the corresponding symbol appears 
			{4,7,8,11,12}, // V
			{1,6,9}, // E
			{2,10}, // X  (etc)
			{1,2,7,16},
			{3,4,6,9,9,12},
			{1,6,14},
			{4,14},
			{1,6,9},
			{0,3,11,15},
			{1,2,4,11,13,14},
			{2,11,15,16},
			{0,3,6,8,12},
			{2,6,9,11,15}
	};
	
	
	
	int[] slots = new int[17]; // as we select transpositions, mark the columns used so they don't get selected more than once in a single transposition.
	int[] selections = new int[columns.length];
	
	public PrimerTranspositions() {
		for (int i=0; i<slots.length; i++) slots[i] = -1;
		for (int i=0; i<selections.length; i++) selections[i] = -1;
		
		long prod = 1;
		for (int i=0; i<columns.length; i++) {
			prod *= columns[i].length;
		}
		System.out.println("upper limit: " + prod);
	}
	
	/** enumerate all unique transpositions */
	public void transpositions() {
		transpositions("", 0);
	}
	
	public void transpositions(String prefix, int index) {
		if (index >= columns.length) {
			dump();
			return;
		}

		boolean deadend = true;
		for (int i=0; i<columns[index].length; i++) {
			int column = columns[index][i];
			if (slots[column] > -1) {
				//System.out.println("index " + index + " i " + i + " skipping col " + column + " because occupied (" + slots[column] + ")");
				continue; // skip this column because it's already occupied
			}
			deadend = false;
			slots[column] = index;
			selections[index] = column;
			transpositions(prefix + column + " ", index+1);
			slots[column] = -1;
			selections[index] = -1;
		}
		//if (deadend) System.out.println("DEAD END: " + prefix);
	}
	
	public void dump() {
		String s = "";
		for (int i=0; i<selections.length; i++) s += selections[i] + " ";
		System.out.println(s);
	}
	
	public static void main(String[] args) {
		PrimerTranspositions pt = new PrimerTranspositions();
		pt.transpositions();
	}
	
	

}
