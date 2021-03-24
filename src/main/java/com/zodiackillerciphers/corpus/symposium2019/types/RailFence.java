package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;

public class RailFence extends CipherBase {
	static Random rand = new Random();
	public boolean method = false;
	@Override
	public String firstLayer(String pt) {
		// rail fence
		int rails = rand.nextInt(29)+2; // [2,30]
		boolean startAtBottom = rand.nextBoolean();
		String rail = com.zodiackillerciphers.ciphers.algorithms.RailFence.encode(pt, rails, startAtBottom);
		return rail;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		RailFence sp = new RailFence();
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
