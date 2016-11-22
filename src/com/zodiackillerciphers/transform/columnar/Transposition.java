package com.zodiackillerciphers.transform.columnar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.generator.CandidateKey;

/** expressd from the genome */
public class Transposition {
	
	/** n: keyword/keyphrase length */
	public int n;
	/** ci...: selection (without replacement) of column #i  (subsequent selections are drawn from a decreasing pool of columns until they are all gone) */
	public List<Integer> C;
	/** copy of the genome */
	public float[] genome;
	
	public Transposition(float[] genome) {
		this.genome = genome;
		n = CandidateKey.toInt(genome[0], 1, 20);
		
		List<Integer> pool = new ArrayList<Integer>();
		for (int i=0; i<n; i++) pool.add(i);
		
		// draw selections from remaining columns
		C = new ArrayList<Integer>();
		for (int i=0; i<n; i++) {
			int which = CandidateKey.toInt(genome[1+i], 0, pool.size()-1);
			C.add(pool.remove(which));
		}
	}
	
	public String decode(String cipher) {
		return ColumnarTransposition.decode(cipher, C);
		
	}
	
	public String toString() {
		return "n " + n + " C " + C + " genome " + Arrays.toString(genome);
		
	}
	
	public static void test() {
		float[] genome = new float[22];
		for (int i=0; i<genome.length; i++) {
			genome[i] = (float) Math.random();
		}
		Transposition t = new Transposition(genome);
		System.out.println(t + " " + t.decode(Ciphers.cipher[0].cipher));
	}
	public static void main(String[] args) {
		for (int i=0; i<100; i++) test();
	}
}
