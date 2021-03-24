package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Gibberish extends CipherBase {
	static Random rand = new Random();
	public boolean method = false;
	@Override
	public String firstLayer(String pt) {
		// random gibberish
		// one way: shuffle the pt then encode
		// another way: shuffle Z340
		this.method = rand.nextBoolean();
		if (method) {
			return CipherTransformations.shuffle(pt);
		}
		return pt;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Gibberish sp = new Gibberish();
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
