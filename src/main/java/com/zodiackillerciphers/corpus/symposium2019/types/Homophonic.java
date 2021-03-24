package com.zodiackillerciphers.corpus.symposium2019.types;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;

public class Homophonic extends CipherBase {

	@Override
	public String firstLayer(String plaintext) {
		// just a plain homophonic, so we don't encipher a first layer.
//		System.out.println("plaintext is: " + plaintext);
		return plaintext;
	}
	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Homophonic col = new Homophonic();
		String result = col.makeCipher(pt);
		System.out.println("Result: " + result);
	}
	public static void main(String[] args) {
		test();
	}

}
