package com.zodiackillerciphers.annealing.concealment;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.dictionary.InsertWordBreaks;

/** Search for readable text hidden within null cipher text.  This is NOT meant to find solutions that are 
 * hidden within text that is already readable - the cipher should look like gibberish at the start. 
 * @author doranchak
 *
 */
public class ConcealmentCipher extends Solution {

	/** the original cipher text.  all uppercase, no spaces. */  
	private StringBuffer cipher;
	
	/** a bitmask for the cipher text.  if a position = true, then include the letter there in the solution. */ 
	private boolean[] mask;
	/** a copy to restore if mutated solution is not kept. */
	private boolean[] maskForReversal;
	
	Random rand = new Random();
	
	double energy;
	
	public ConcealmentCipher(StringBuffer cipher) {
		this.cipher = cipher;
	}
	
	@Override
	public boolean mutate() {
		
		maskForReversal = mask.clone();
		/** randomly flip bits a random number of times */
		for (int i=0; i<rand.nextInt(4)+1; i++) {
			boolean bit = rand.nextBoolean();
			int pos = rand.nextInt(mask.length);
			mask[pos] = bit;
		}
		return true;
	}

	@Override
	public void mutateReverse() {
		mask = maskForReversal;
	}

	@Override
	public void mutateReverseClear() {
		maskForReversal = null;
	}

	@Override
	public String representation() {
		// TODO Auto-generated method stub
		return Arrays.toString(mask) + " " + decode() + "	" + InsertWordBreaks.findWordBreaks(decode(), "EN", true);
	}
	
	StringBuffer decode() {
		StringBuffer result = new StringBuffer();
		for (int i=0; i<mask.length; i++) {
			if (mask[i]) result.append(cipher.charAt(i));
		}
		
		return result;
	}

	@Override
	public double energy() {
		StringBuffer sb = InsertWordBreaks.findWordBreaks(decode(), "EN", true);
		String[] split = sb.toString().split("	");
		energy = 0 - Float.valueOf(split[0]);
		return energy;
	}

	@Override
	public double energyCached() {
		// TODO Auto-generated method stub
		return energy;
	}

	@Override
	public void initialize() {
		mask = new boolean[cipher.length()];
		for (int i=0; i<mask.length; i++) {
			mask[i] = rand.nextBoolean();
		}
	}

}
