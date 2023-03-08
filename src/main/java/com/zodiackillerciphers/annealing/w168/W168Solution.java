package com.zodiackillerciphers.annealing.w168;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.ciphers.w168.StencilTransposition;
import com.zodiackillerciphers.ciphers.w168.StencilTranspositionTemplates;
import com.zodiackillerciphers.ciphers.w168.StringUtils;
import com.zodiackillerciphers.ciphers.w168.W168;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.keyreconstruction.KeyReconstruction;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.tests.ChaoticCaesar;
import com.zodiackillerciphers.tests.LetterFrequencies;
import com.zodiackillerciphers.transform.CipherTransformations;

public class W168Solution extends Solution {

	StencilTransposition stencil; // a matrix that defines the transposition pattern applied at each NxM subgrid
						// within a cipher grid.

	// TODO: should we track the arrays of patterns?
	
	StringBuilder[] cipher;

	public Random random = new Random();
	
	public double energyCached;
	
	public int mutateSwap1;
	public int mutateSwap2;
	public StencilTransposition stencilMutate;
	
	StringBuffer plaintextCached;
	
	@Override
	public void mutateReverse() {
		swap(mutateSwap1, mutateSwap2);
		if (stencilMutate != null) {
			stencil = stencilMutate;
			stencilMutate = null;
		}
	}
	@Override
	public void mutateReverseClear() {
		mutateSwap1 = -1;
		mutateSwap2 = -1;
		stencilMutate = null;
	}

	@Override
	public String representation() {
		if (plaintextCached == null) energy();
		return energyCached + "	" + plaintextCached + "	" + stencil.toString();
	}

	@Override
	public double energyCached() {
		return energyCached;
	}

	@Override
	public void initialize() {
		
		// random stencil from a pile of possible stencil patterns and shapes
		stencil = StencilTranspositionTemplates.randomStencil();		
		
		mutateSwap1 = -1;
		mutateSwap2 = -1;
		stencilMutate = null;
	}
	public W168Solution(StringBuilder[] cipher) {
		this.cipher = cipher;
	}
	
	@Override
	public boolean mutate() {
		int which = random.nextInt(2);
		if (which == 0) 
			return mutateSwap(); // swap the order of two holes
		return mutateInit(); // make a brand new random stencil
	}
	
	public boolean mutateSwap() {
		//System.out.println("mutateSwap");
		int a = random.nextInt(stencil.holes.length);
		int b = a;
		while (a == b) 
			b = random.nextInt(stencil.holes.length);
		
		mutateSwap1 = a;
		mutateSwap2 = b;
		swap(a, b);
		return true;
	}
	
	public boolean mutateInit() {
		//System.out.println("mutateInit");
		stencilMutate = stencil;
		stencil = StencilTranspositionTemplates.randomStencil();
		mutateSwap1 = -1;
		mutateSwap2 = -1;
		return true;
	}
	
	public void swap(int a, int b) {
		if (a == -1 || b == -1)
			return;
		//System.out.println(a + ", " + b + ": " + stencil);
		int[] tmp = stencil.holes[a];
		stencil.holes[a] = stencil.holes[b];
		stencil.holes[b] = tmp;
	}
	
	@Override
	public double energy() { // lower is better
		energyCached = 0;
		//StringBuilder[] cipher = W168.toStringBuilder(W168.Z408_1);
		
//		StringBuilder[] cipher = W168.toStringBuilder(new String[] { "PLE PEOMUC SO FUNORE  GAWILD",
//				"KE I LIAUS BECN. H FUN K THA", "INGKILL ISE ITS MIT ING ILLI", "ECAST BT D MOS ALL OFNG ETHI",
//				"N TME IMANUSE ROUANGEO KL. T", "OREHE FTHE IS IMAS ANSOMILL " });
		
		//System.out.println("SMEG stencil " + stencil.toString());
		StringBuilder[] arr = stencil.decode(cipher);
		plaintextCached = new StringBuffer();
		for (StringBuilder line : arr) plaintextCached.append(line);
		float zk = NGramsCSRA.zkscore(plaintextCached, "EN", true);
		energyCached = -zk;
		return energyCached;
	}
	
	@Override
	public Solution clone() {
		W168Solution newSol = new W168Solution(cipher);
		StencilTransposition newStencil;

		int[][] newHoles = new int[this.stencil.holes.length][this.stencil.holes[0].length]; 
		newStencil = new StencilTransposition(newHoles);
		if (plaintextCached != null) newSol.plaintextCached = new StringBuffer(plaintextCached.toString());
		newSol.stencil = newStencil;
		return newSol;
	}
	
	public static void main(String[] args) {
	}

}
