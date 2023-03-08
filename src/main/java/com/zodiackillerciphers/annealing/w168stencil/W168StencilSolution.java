package com.zodiackillerciphers.annealing.w168stencil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.ciphers.w168.W168;
import com.zodiackillerciphers.ciphers.w168.stenciltest.Stencil;

/** http://scienceblogs.de/klausis-krypto-kolumne/2020/05/10/christophs-chaotic-caesar-challenge */
public class W168StencilSolution extends Solution {

	/** set up the max height and width of the pattern */
	static int N = 2;
	static int M = 14;
	static int H = N*M;
	
	static StringBuilder[] CIPHER = W168.cipherBuilder; 
	/*
	 * defines pattern of "holes". we scan the entire cipher grid to sample ngrams,
	 * trying to find the patterns that yield the highest scoring ngrams.
	 */
	Stencil stencil;
	
	public Random random = new Random();
	
	public double energyCached;

	/** copy of holes prior to mutation */
	public int[] mutateHoles;
	/** track currently used positions.  ciphergrid-relative. */
	public Set<Integer> holePositions;
	
	int mutatePositionAdded;
	int mutatePositionRemoved;
	
	@Override
	public void mutateReverse() {
		stencil.holes = mutateHoles;
		if (mutatePositionAdded > -1) 
			holePositions.remove(mutatePositionAdded);
		if (mutatePositionRemoved > -1) 
			holePositions.add(mutatePositionRemoved);
		mutatePositionAdded = -1;
		mutatePositionRemoved = -1;
	}
	@Override
	public void mutateReverseClear() {
		mutateHoles = null;
		mutatePositionAdded = -1;
		mutatePositionRemoved = -1;
	}

	@Override
	public String representation() {
		energy();
		return energyCached + "	" + stencil.toString();
	}

	@Override
	public double energyCached() {
		return energyCached;
	}

	/** convert subgrid-relative position [0,N*M) to 
	 * ciphergrid-relative position [0,168] 
	 */
	public int convert(int pos) {
		int row = pos / M;
		int col = pos % M;
		int newPos = row*CIPHER[0].length() + col;
		return newPos;
	}
	
	@Override
	public void initialize() {
		holePositions = new HashSet<Integer>();
		stencil = new Stencil(2, CIPHER, N, M);
		stencil.holes = new int[2]; // always start from simple random 2 hole pattern.
		
		stencil.holes[0] = convert(random.nextInt(H));
		holePositions.add(stencil.holes[0]);
		stencil.holes[1] = convert(stencil.holes[0]);
		while (stencil.holes[1] == stencil.holes[0])
			stencil.holes[1] = random.nextInt(H);
		holePositions.add(stencil.holes[1]);
		
		stencil.alwaysZkscore = true;
		stencil.currentHole = 1000;
		
		//System.out.println("init " + stencil);
		
	}
	public W168StencilSolution() {
	}
	
	@Override
	public boolean mutate() {
		// mutators:
		// 0) Add a random hole.  But only if current number of holes < H.
		// 1) Remove a random hole.  But only if current number of holes > 2.
		// 2) Swap random holes.  This is always available.
		// 3) Randomly pick a hole and change its position.  Only if current number of holes < H.
		List<Integer> mutators = new ArrayList<Integer>();
		int L = stencil.holes.length;
		if (L < H) {
			mutators.add(0);
			mutators.add(3);
		}
		if (L > 2) {
			mutators.add(1);
		}
		mutators.add(2);
		
		int index = random.nextInt(mutators.size());
		int which = mutators.get(index);
		if (which == 0) 
			return mutateAdd();
		if (which == 1) 
			return mutateRemove();
		if (which == 2) 
			return mutateSwap();
		if (which == 3) 
			return mutatePos();
		throw new RuntimeException("INVALID MUTATOR: " + which);
		
	}

	// Function to insert x in arr at position pos.  0 <= pos < arr.length
	public static int[] insertX(int arr[], int x, int pos) {
		int i;
		int n = arr.length;

		// create a new array of size n+1
		int newarr[] = new int[n + 1];

		// insert the elements from
		// the old array into the new array
		// insert all elements till pos
		// then insert x at pos
		// then insert rest of the elements
		for (i = 0; i < n + 1; i++) {
			if (i < pos)
				newarr[i] = arr[i];
			else if (i == pos)
				newarr[i] = x;
			else
				newarr[i] = arr[i - 1];
		}
		return newarr;
	}
	public static int[] remove(int arr[], int pos) {
		int i;
		int n = arr.length;

		int newarr[] = new int[n - 1];

		for (i = 0; i < n - 1; i++) {
			if (i < pos)
				newarr[i] = arr[i];
			else 
				newarr[i] = arr[i+1];
		}
		return newarr;
	}
	public static int[] copy(int[] arr) {
		int[] copy = new int[arr.length];
		for (int i=0; i<arr.length; i++) copy[i] = arr[i];
		return copy;
	}

	public boolean mutateAdd() {
		//System.out.println("mutateAdd");
		mutateHoles = copy(stencil.holes);
		int pos = randomAvailablePosition(); // already converted to ciphergrid-relative
		int where = random.nextInt(stencil.holes.length+1);
		stencil.holes = insertX(stencil.holes, pos, where);
		holePositions.add(pos);
		mutatePositionAdded = pos;
		return true;
	}
	
	public int randomAvailablePosition() {
		int pos = convert(random.nextInt(H));
		while (holePositions.contains(pos))
			pos = convert(random.nextInt(H));
		return pos;
	}
	
	public boolean mutateRemove() {
		//System.out.println("mutateRemove");
		mutateHoles = copy(stencil.holes);
		int where = random.nextInt(stencil.holes.length);
		int pos = stencil.holes[where];
		stencil.holes = remove(stencil.holes, where);
		holePositions.remove(pos);
		mutatePositionRemoved = pos;
		return true;
	}
	public boolean mutateSwap() {
		//System.out.println("mutateSwap");
		mutateHoles = copy(stencil.holes);
		int a = random.nextInt(stencil.holes.length);
		int b = a;
		while (a == b) 
			b = random.nextInt(stencil.holes.length);
		swap(a, b);
		return true;
	}
	public void swap(int a, int b) {
		int tmp = stencil.holes[a];
		stencil.holes[a] = stencil.holes[b];
		stencil.holes[b] = tmp;
	}
	public boolean mutatePos() {
		//System.out.println("mutatePos");
		mutateHoles = copy(stencil.holes);
		int which = random.nextInt(stencil.holes.length);
		int posRemoved = stencil.holes[which];
		mutatePositionRemoved = posRemoved;
		holePositions.remove(posRemoved);
		int posAdded = randomAvailablePosition();
		mutatePositionAdded = posAdded;
		holePositions.add(posAdded);
		stencil.holes[which] = posAdded;
		return true;
	}
	
	@Override
	public double energy() { // lower is better
		energyCached = 0;
		this.stencil.successAction(false, false);
		//System.out.println("score " + this.stencil.freqSum);
		energyCached = -this.stencil.freqSum;
		return energyCached;
	}
	
	@Override
	public Solution clone() {
		W168StencilSolution newSol = new W168StencilSolution();
		Stencil newStencil;

		int[] newHoles = new int[this.stencil.holes.length]; 
		newStencil = new Stencil(newHoles.length, CIPHER, N, M);
		newStencil.holes = newHoles;
		newSol.stencil = newStencil;
		return newSol;
	}
	
	public static void main(String[] args) {
	}

}
