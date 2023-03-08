package com.zodiackillerciphers.annealing.w168old;

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

	StringBuilder[] cipher = StringUtils.toStringBuilder(W168.J168_2);

	static int[] VALID_HEIGHTS = new int[] {
			1, 2, 3, 6
	};
	static int[] VALID_WIDTHS = new int[] {
			1, 2, 4, 7
			// maybe try 14 later
	};
	
	public Random random = new Random();
	
	public double energyCached;
	
	public int mutateSwap1;
	public int mutateSwap2;
	
	StringBuffer plaintextCached;
	
	@Override
	public void mutateReverse() {
		swap(mutateSwap1, mutateSwap2);
	}
	@Override
	public void mutateReverseClear() {
		mutateSwap1 = -1;
		mutateSwap2 = -1;
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
		// make a random subgrid with random element order
		int N = VALID_HEIGHTS[random.nextInt(VALID_HEIGHTS.length)];
		int M = VALID_WIDTHS[random.nextInt(VALID_WIDTHS.length)];
		
		// for now, hard code to 3x7
		N = 6; M = 7;
		
		int[] order = new int[N*M];
		for (int i=0; i<N*M; i++) order[i] = i;
		CipherTransformations.shuffle(order);
		
		stencil = new StencilTransposition(new int[N*M][2]);
		for (int i=0; i<order.length; i++) {
			int pos = order[i];
			stencil.holes[i] = new int[] {pos/M, pos%M};
			
		}
		
		mutateSwap1 = -1;
		mutateSwap2 = -1;
	}
	public W168Solution() {
	}
	
	@Override
	public boolean mutate() {
		
		//int which = random.nextInt(3);
		//if (which == 0) 
			return mutateSwap();
	}
	
	public boolean mutateSwap() {
		int a = random.nextInt(stencil.holes.length);
		int b = a;
		while (a == b) 
			b = random.nextInt(stencil.holes.length);
		
		mutateSwap1 = a;
		mutateSwap2 = b;
		swap(a, b);
		return true;
	}
	
	public void swap(int a, int b) {
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
		W168Solution newSol = new W168Solution();
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
