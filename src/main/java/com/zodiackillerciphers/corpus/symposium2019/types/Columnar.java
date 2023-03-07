package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTransposition;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;

public class Columnar extends CipherBase {
	public static boolean WITH_KEY_LENGTH = false;
	public int[] key;
	public Integer KEY_LENGTH_OVERRIDE = null;
	@Override
	public String firstLayer(String plaintext) {
		// columnar transposition:
		if (KEY_LENGTH_OVERRIDE == null)
			key = ColumnarTransposition.randomKey(20);
		else 
			key = ColumnarTransposition.randomKeyWithLength(KEY_LENGTH_OVERRIDE);
			
//		say(Arrays.toString(key));
		// TODO: this was broken due to method change:
		// StringBuilder col = ColumnarTransposition.encode(new StringBuilder(plaintext), key);
//		return col.toString();
		return "";
	}
	
	public static String untranspose(String cipher, int[] key) {
		// TODO: this was broken due to method change:
        // return ColumnarTransposition.decode(new StringBuilder(cipher), key, false).toString();
		return "";
	}
	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Columnar col = new Columnar();
		String result = col.makeCipher(pt);
		System.out.println("Result: " + result);
		System.out.println("Len: " + result.length());
		
	}
	public static void main(String[] args) {
		test();
//		System.out.println(untranspose("Q0wqfHXABMzKBvPSGPPJl8HwgxczQRTpOGwbYGP3dGq6jGGBoV8Zm8wVPHzxaHSUGmXIX7SoZwseOvZ7a3BwDy35wX4oRwYoEBwIvwHVffpTNFJP3WHqoRvvxe4GGzu5DXApwUvOwmD7wruGyHicerA5AAP3eZxrAy6PRJFscI!Vw4X9WZ8BP4Yw7jzbvhGyH0KdqiHIaeJkHaGfnaphX7O1eU13HPGeovPjyV6Uk8KOc2asLB8LHoFYjB!HGHhUrInKhcwewr27YHBetAYJ7t641rAQKszKddmOur7EHdbH6AuAyEKy8wdjH8lGLaz8PCHvwtsGL65ZNtyx7I85",
//				new int[] {7, 9, 4, 2, 0, 5, 6, 1, 3, 8}));
	}

}
