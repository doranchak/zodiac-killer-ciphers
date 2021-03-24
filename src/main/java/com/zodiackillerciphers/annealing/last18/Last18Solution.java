package com.zodiackillerciphers.annealing.last18;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.annealing.homophonic.HomophonicSolution;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.InsertWordBreaks;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.LetterFrequencies;
import com.zodiackillerciphers.transform.CipherTransformations;

/** maximize zkscore for anagrams of the last 18 letters of the plaintext */
public class Last18Solution extends Solution {

	public static String LAST18 = "EBEORIETEMETHHPITI";
	public Random random = new Random();
	public String currentPlaintext;
	public String currentPlaintextWithBreaks;
	public String reversePlaintext;
	public double energyCached;
	@Override
	public void mutateReverse() {
		currentPlaintext = reversePlaintext;
	}
	@Override
	public void mutateReverseClear() {
		reversePlaintext = null;
	}

	@Override
	public String representation() {
		return energyCached + "	" + currentPlaintext + "	" + currentPlaintextWithBreaks;
	}

	@Override
	public double energyCached() {
		// TODO Auto-generated method stub
		return energyCached;
	}

	@Override
	public void initialize() {
		// init with random permutation of the last 18
		this.currentPlaintext = CipherTransformations.shuffle(LAST18);
	}

	public Last18Solution() {
	}
	
	@Override
	public boolean mutate() {
		reversePlaintext = currentPlaintext;
		// swap random letters a random number of times
		for (int i=0; i<random.nextInt(3)+1; i++) {
			StringBuffer sb = new StringBuffer(currentPlaintext);
			int a = random.nextInt(sb.length());
			char ca = sb.charAt(a);
			int b = a;
			while (b==a) { 
				b = random.nextInt(sb.length());
			}
			char cb = sb.charAt(b);
			sb.setCharAt(b, ca);
			sb.setCharAt(a, cb);
			currentPlaintext = sb.toString();
		}
		return true;
	}
	
	@Override
	public double energy() {
//		energyCached = -ZKDecrypto.calcscore(currentPlaintext);
//		return energyCached;
		StringBuffer sb = InsertWordBreaks.findWordBreaks(new StringBuffer(currentPlaintext), "EN", true);
		String[] split = sb.toString().split("	");
		energyCached = -Double.valueOf(split[0]);
		currentPlaintextWithBreaks = split[1];
		return energyCached;
	}
	
	@Override
	public Solution clone() {
		Last18Solution newSol = new Last18Solution();
		newSol.currentPlaintext = this.currentPlaintext;
		newSol.energyCached = this.energyCached;
		return newSol;
	}

}
