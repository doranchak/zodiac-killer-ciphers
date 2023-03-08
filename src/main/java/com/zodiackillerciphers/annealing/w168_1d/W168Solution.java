package com.zodiackillerciphers.annealing.w168_1d;

import java.util.Random;
import java.util.TreeSet;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.ciphers.w168.NGram;
import com.zodiackillerciphers.ciphers.w168.Permutation1D;

/** find 1D patterns that maximize zkscore */
public class W168Solution extends Solution {

	Permutation1D permutation;

	public Random random = new Random();
	
	public double energyCached;
	
	public int mutateSwap1;
	public int mutateSwap2;
	public int mutateElementWhich;
	public int mutateElementVal;
	
	public int mutateSelect;
	
	public static int TOP = 10000000;
	
	@Override
	public void mutateReverse() {
		if (mutateSelect == 0) swap(mutateSwap1, mutateSwap2);
		else {
			permutation.sequence[mutateElementWhich] = mutateElementVal;
		}
	}
	@Override
	public void mutateReverseClear() {
		mutateSwap1 = -1;
		mutateSwap2 = -1;
	}

	@Override
	public String representation() {
		energy();
		return energyCached + "	" + permutation;
	}

	@Override
	public double energyCached() {
		return energyCached;
	}

	@Override
	public void initialize() {
		mutateSwap1 = -1;
		mutateSwap2 = -1;
	}
	public W168Solution(int size, StringBuilder[] cipher) {
		this.permutation = new Permutation1D(size, cipher);
	}
	
	@Override
	public boolean mutate() {
		mutateSelect = random.nextInt(2);
		if (mutateSelect == 0) 
			return mutateSwap(); // swap the order of two holes
		return mutateElement(); // make a brand new random stencil
	}
	
	public boolean mutateSwap() {
		//System.out.println("mutateSwap");
		int a = random.nextInt(permutation.sequence.length);
		int b = a;
		while (a == b) 
			b = random.nextInt(permutation.sequence.length);
		
		mutateSwap1 = a;
		mutateSwap2 = b;
		swap(a, b);
		return true;
	}
	
	public boolean mutateElement() {
		int which = random.nextInt(permutation.sequence.length);
		int val = random.nextInt(permutation.cipherLength());
		
		mutateElementWhich = which;
		mutateElementVal = permutation.sequence[which];
		permutation.sequence[which] = val;
		
		return true;
	}
	
	public void swap(int a, int b) {
		int tmp = permutation.sequence[a];
		permutation.sequence[a] = permutation.sequence[b];
		permutation.sequence[b] = tmp;
	}
	
	@Override
	public double energy() { // lower is better
		TreeSet<NGram> topNGrams = new TreeSet<NGram>();
		int score = permutation.score(TOP, topNGrams);
		energyCached = -score;
		return energyCached;
	}
	
	@Override
	public Solution clone() {
		W168Solution newSol = new W168Solution(permutation.sequence.length, permutation.cipher);
		for (int i=0; i<permutation.sequence.length; i++) {
			newSol.permutation.sequence[i] = permutation.sequence[i];
		}
		return newSol;
	}
	
	public static void main(String[] args) {
	}

}
