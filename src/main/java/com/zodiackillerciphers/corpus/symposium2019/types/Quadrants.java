package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Quadrants extends CipherBase {
	static Random rand = new Random();
	public boolean method = false;
	@Override
	public String firstLayer(String pt) {
		// quadrants.  don't allow quadrant width or height less than 5.
		// order is random.
		int r = rand.nextInt(11) + 4; // [4,14]
		int c = rand.nextInt(8) + 4; // [4,11]
		int[] order = {0,1,2,3};
		HomophonicGenerator.shuffle(order);
		String quadrants = com.zodiackillerciphers.transform.operations.Quadrants.transform(pt, r, c, order, 17, false);
		return quadrants;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Quadrants sp = new Quadrants();
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
