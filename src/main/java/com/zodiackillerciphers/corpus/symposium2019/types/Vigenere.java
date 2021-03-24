package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;

public class Vigenere extends CipherBase {
	static Random rand = new Random();
	
	@Override
	public String firstLayer(String pt) {
		// vigenere
		int keyLength = rand.nextInt(29)+2; // [2,30]
		int[] key = new int[keyLength];
		for (int i=0; i<key.length; i++) {
			key[i] = rand.nextInt(25)+1; // [1,25]
		}
		String vig = com.zodiackillerciphers.ciphers.algorithms.Vigenere.encrypt(pt, key);
		return vig;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Vigenere sp = new Vigenere();
		sp.addPlaintext(pt);
		sp.run();
//		String result = sp.makeCipher(pt);
		System.out.println("Result: " + sp.getCiphers());
		System.out.println(Arrays.toString(sp.getStats().get(0)));
	}

	public static void main(String[] args) {
		 test();
	}

}
