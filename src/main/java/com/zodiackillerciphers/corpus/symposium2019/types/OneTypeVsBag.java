package com.zodiackillerciphers.corpus.symposium2019.types;

import java.lang.reflect.Constructor;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.algorithms.ColumnarTransposition;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.corpus.symposium2019.ThreadedGenerator;

/** pile of ciphers.  half of them are of one type.  the rest are a mix of other types. */
public class OneTypeVsBag extends CipherBase {
	public static Random rand = new Random();
	public int[] key;
	public Integer KEY_LENGTH_OVERRIDE = null;
	public String oneType = "Columnar"; // default cipher type
	public String[] allTypes = new String[] {
			//"Columnar", "Diagonal", "Gibberish", "Homophonic", "Permutation", "RailFence", "Scytale", "Snake", "Spiral", "Vigenere"
			"Homophonic", "Columnar"
	};
	public String type;
	public int output;
	@Override
	public String firstLayer(String plaintext) {
		try {
			CipherBase instance;
			// 50% probability that we pick the primary type		
			if (rand.nextBoolean()) {
				Class<?> clazz = Class.forName(ThreadedGenerator.base + oneType);
				Constructor<?> ctor = clazz.getConstructor();
				instance = (CipherBase) ctor.newInstance();
				instance.skipHomophonic = this.skipHomophonic;
				output = 1;
			} else {
				// otherwise we pick on of the other types
				String selectedType = oneType;
				while (selectedType.equals(oneType)) {
					selectedType = allTypes[rand.nextInt(allTypes.length)];
				}
//				System.out.println("selectedType " + selectedType);
				Class<?> clazz = Class.forName(ThreadedGenerator.base + selectedType);
				Constructor<?> ctor = clazz.getConstructor();
				instance = (CipherBase) ctor.newInstance();
				instance.skipHomophonic = this.skipHomophonic;
				output = 0;
			}
			type = instance.getClass().getName();
			return instance.firstLayer(plaintext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String untranspose(String cipher, int[] key) {
		return ColumnarTransposition.decode(new StringBuilder(cipher), key, false).toString();
	}
	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		for (int i=0;i<100; i++) {
			OneTypeVsBag one = new OneTypeVsBag();
			String result = one.makeCipher(pt);
			System.out.println("Result: " + result);
			System.out.println("Len: " + result.length());
			System.out.println("Type: " + one.type);
			System.out.println("====================================");
		}
		
	}
	public static void main(String[] args) {
		test();
//		System.out.println(untranspose("Q0wqfHXABMzKBvPSGPPJl8HwgxczQRTpOGwbYGP3dGq6jGGBoV8Zm8wVPHzxaHSUGmXIX7SoZwseOvZ7a3BwDy35wX4oRwYoEBwIvwHVffpTNFJP3WHqoRvvxe4GGzu5DXApwUvOwmD7wruGyHicerA5AAP3eZxrAy6PRJFscI!Vw4X9WZ8BP4Yw7jzbvhGyH0KdqiHIaeJkHaGfnaphX7O1eU13HPGeovPjyV6Uk8KOc2asLB8LHoFYjB!HGHhUrInKhcwewr27YHBetAYJ7t641rAQKszKddmOur7EHdbH6AuAyEKy8wdjH8lGLaz8PCHvwtsGL65ZNtyx7I85",
//				new int[] {7, 9, 4, 2, 0, 5, 6, 1, 3, 8}));
	}

}
