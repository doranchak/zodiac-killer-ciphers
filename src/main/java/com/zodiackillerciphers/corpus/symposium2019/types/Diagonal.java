package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Diagonal extends CipherBase {
	static Random rand = new Random();
	
	@Override
	public String firstLayer(String pt) {
		// diagonal transpositions.
		// works in upper left corner but can flip/mirror to orient for other diagonals.
		// UL: no transformation needed.
		// UR: horizontal flip
		// LL: vertical flip
		// LR: vertical then horizontal flip
		String flipped = pt;
		int whichFlip = rand.nextInt(4); // [0,3]
		if (whichFlip == 1) 
			flipped = CipherTransformations.flipHorizontal(pt, 20, 17);
		else if (whichFlip == 2) 
			flipped = CipherTransformations.flipVertical(pt, 20, 17);
		else if (whichFlip == 3) {
			flipped = CipherTransformations.flipVertical(pt, 20, 17);
			flipped = CipherTransformations.flipHorizontal(flipped, 20, 17);
		}
		int d = rand.nextInt(4); // [0,3]
		String diag = com.zodiackillerciphers.transform.operations.Diagonal.transform(flipped, 17, d);
		System.out.println("diag " + diag);
		return diag;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		for (int i=0; i<100; i++) {
			Diagonal sp = new Diagonal();
			sp.addPlaintext(pt);
			sp.run();
//			String result = sp.makeCipher(pt);
			for (String cipher : sp.getCiphers()) {
				System.out.println("Result, length " + cipher.length() + ": " + sp.getCiphers());
			}
			System.out.println(Arrays.toString(sp.getStats().get(0)));
		}
	}

	public static void main(String[] args) {
		 test();
	}

}
