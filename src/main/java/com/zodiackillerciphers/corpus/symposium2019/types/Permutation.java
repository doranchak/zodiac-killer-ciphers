package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;

public class Permutation extends CipherBase {
	static Random rand = new Random();
	public int[] key;
	@Override
	public String firstLayer(String plaintext) {
		// https://crypto.interactive-maths.com/permutation-cipher.html
		int size = 2 + rand.nextInt(9); // [2,10]
		int[] permutation = new int[size];
		key = permutation;
		for (int j = 0; j < size; j++)
			permutation[j] = j;
		boolean retry = true;
		while (retry) {
			HomophonicGenerator.shuffle(permutation);
			for (int j = 0; j < size; j++) {
				if (permutation[j] != j) {
					retry = false;
					break;
				}
			}
		}
//		say(size + " " + Arrays.toString(permutation));
		String permute = com.zodiackillerciphers.transform.operations.Permutation.transform(plaintext, permutation,
				false);
		return permute;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Permutation perm = new Permutation();
		String result = perm.makeCipher(pt);
		System.out.println("Result: " + result);

	}

	public static void main(String[] args) {
//		test();
		String cipher = "pLaDhhiVbVuJdJq5OFm0l7onm1zGlVQU92MlE659lCPMB1fVp286hdA1bHL68aRXdKVMGAxZogyYoP0ldfCPGl5otMX1MkldXdfBDKPcJCzdv8d3LMdw2d9d2h7HluOf11MLXWDGJpJlksMSvfM1HCnP8da2OocVPqmECe6OClpEDSC0PyzzDGrWJ6a2Tdr!95U9YPUhRPljHXakpdCP0GvbplO2Pq9C1XjdpXdKRd6HaHoyyVy0L1P0wNOuptPYdtKPopdI1tlHLdO2dKHv1kSAvVRy3hKiLy0dk7lMSYTMrlz!iRfLaVDVMOSoM7dT0T85RYkaJ5Q4vdwlXATP";
		int[] key = new int[] {9, 1, 0, 10, 5, 3, 8, 6, 11, 2, 7, 4};
		System.out.println(com.zodiackillerciphers.transform.operations.Permutation.transform(cipher, key, true));
	}

}
