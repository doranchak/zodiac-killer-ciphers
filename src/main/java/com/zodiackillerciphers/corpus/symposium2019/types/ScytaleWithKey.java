package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.ngrams.Periods;

public class ScytaleWithKey extends CipherBase {
	static Random rand = new Random();
	
	@Override
	public String firstLayer(String plaintext) {
		// scytale with columns selected in varying order (i.e., not necessarily sequential like normal scytale)
		int period = rand.nextInt(39)+2; // [2,40]
		int[] order = new int[period];
		for (int i=0; i<order.length; i++) order[i] = i;
		HomophonicGenerator.shuffle(order); // random column ordering
//		say("period " + period + " order " + Arrays.toString(order));
		String scytale = Scytale.encode(plaintext, period, order, false);
		return scytale;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		ScytaleWithKey sp = new ScytaleWithKey();
		sp.addPlaintext(pt);
		sp.run();
//		String result = sp.makeCipher(pt);
		System.out.println("Result: " + sp.getCiphers());
		System.out.println(Arrays.toString(sp.getStats().get(0)));
	}

	public static void main(String[] args) {
//		 test();
		 System.out.println(Scytale.encode("jYUPCjmHX1zp6LV1WvzQblKwbSdHKIkSw3oFAqqEceXu3WaK3YUzm8cH78NtUAzR66Pqa69sDT4DTcCAQtyygqe24ckCBoYFIDycXHhcsF30q1WbEVNxEPPfjZNzcIdDLUYDUc1HRCB3ultCX8TowacqzXD3OWl4jL7SKefpC0H!mSpedjfV1q!r3YzMHmUeXc1Ge9Y3VBGsMgYTzqnPVjeo8rcDPs8eSSoV1TFfSvO0XfS3tsSb6aAqLfsjvMLHqnpFFqsUTzr!n5bDODWHj87aXSqph2YJKouzhfjhp7gCSW2hHzWT14DP8EWTVigT9XLNCo18tiCdN0f8eTrw", 
				 22, new int[] {7, 10, 11, 12, 5, 9, 19, 4, 6, 21, 8, 20, 0, 3, 2, 14, 18, 16, 15, 1, 13, 17}, true));
	}

}
