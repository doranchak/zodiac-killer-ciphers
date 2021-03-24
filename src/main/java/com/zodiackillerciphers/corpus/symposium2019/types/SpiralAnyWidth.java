package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.ngrams.Periods;

public class SpiralAnyWidth extends CipherBase {
	static Random rand = new Random();
	public int width;
	@Override
	public String firstLayer(String plaintext) {
		boolean reverse = rand.nextBoolean();
		width = 2 + rand.nextInt(49); // [2,50]
		String spiral = com.zodiackillerciphers.transform.operations.Spiral.transform(plaintext, width, reverse);
//		System.out.println("first layer: " + spiral);
		if (spiral.length() != 340) throw new RuntimeException("LENGTH IS " + spiral.length());
		return spiral;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		SpiralAnyWidth sp = new SpiralAnyWidth();
		sp.addPlaintext(pt);
		sp.run();
//		String result = sp.makeCipher(pt);
		System.out.println("Result: " + sp.getCiphers());
		System.out.println("Width: " + sp.width);
//		System.out.println(Arrays.toString(sp.getStats().get(0)));
	}

	public static void main(String[] args) {
		 test();
	}

}
