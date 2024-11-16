package com.zodiackillerciphers.annealing.onetimepad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.ngrams.AZDecrypt;

public class OTPSolution extends Solution {

	// At least 2 ciphers encrypted with the same one time pad
	List<String> ciphers;
	List<String> decrypted;

	// The OTP key, in integer array form (simple shifts).
	int[] key;
	// for each cipher, what offset to start decrypting 
	int[] offsets;
	
	boolean doOffsets = false;

	int mutateWhich;

	int mutatePos;
	int mutatePreviousVal;
	
	int mutateOffsetPos;
	int mutateOffsetPreviousVal;
	
	public Random random = new Random();
	
	public double energyCached;
	
	@Override
	public void mutateReverse() {
		if (mutateWhich == 0) 
			key[mutatePos] = mutatePreviousVal;
		else if (mutateWhich == 1) 
			offsets[mutateOffsetPos] = mutateOffsetPreviousVal;
	}
	@Override
	public void mutateReverseClear() {
		mutatePos = -1;
		mutatePreviousVal = -1;
		mutateOffsetPos = -1;
		mutateOffsetPreviousVal = -1;
		
	}

	@Override
	public String representation() {
		String combined = "";
		for (String val : decrypted) {
			if (combined.length() > 0) combined += " "; 
			combined += val;
		}
		return energyCached + " " + combined + " " + Arrays.toString(key) + " " + Arrays.toString(offsets);
	}
		
	@Override
	public double energyCached() {
		// TODO Auto-generated method stub
		return energyCached;
	}

	@Override
	public void initialize() {
		for (int i=0; i<key.length; i++) key[i] = random.nextInt(26);
		offsets = new int[ciphers.size()];
		for (int i=0; i<offsets.length; i++) offsets[i] = doOffsets ? random.nextInt(ciphers.get(i).length()-key.length+1) : 0;
	}
	public OTPSolution(String[] ciphers, int keyLength, boolean doOffsets) {
		this.ciphers = new ArrayList<String>();
		for (String cipher : ciphers) this.ciphers.add(cipher);
		this.key = new int[keyLength];
		this.doOffsets = doOffsets;
	}
	
	@Override
	public boolean mutate() {
		if (random.nextBoolean() || !doOffsets) {
			mutateWhich = 0;
			int pos = random.nextInt(key.length);
			mutatePos = pos;
			mutatePreviousVal = key[pos];
			key[pos] = random.nextInt(26);			
		} else {
			mutateWhich = 1;
			int pos = random.nextInt(offsets.length);			
			mutateOffsetPos = pos;
			mutateOffsetPreviousVal = offsets[pos];
			offsets[pos] = random.nextInt(ciphers.get(pos).length()-key.length+1);
		}		
		
		return true;
	}
		
	@Override
	public double energy() {
		decrypt();
		energyCached = -score();
		return energyCached;
	}
	@Override
	public Solution clone() {
		OTPSolution newSol = new OTPSolution(null, key.length, doOffsets);
		newSol.ciphers = new ArrayList<String>();
		newSol.ciphers.addAll(ciphers);
		newSol.key = new int[key.length];
		for (int i=0; i<key.length; i++) newSol.key[i] = key[i];		
		return newSol;
	}
	
	public void decrypt() {
		decrypted = new ArrayList<String>();
		for (int i=0; i<ciphers.size(); i++) {
			decrypted.add(Shift.shift(ciphers.get(i).substring(offsets[i], offsets[i]+key.length), key, true));
		}
	}
	
	public static double score(String inp) {
//		return NGramsCSRA.zkscore(new StringBuffer(inp), "EN", false) / inp.length();
		return AZDecrypt.score(inp);
	}
	
	public double score() {
		double sum = 0;
		for (String val : decrypted) {
			sum += score(val);
		}
		return sum;
	}
	
}
