package com.zodiackillerciphers.transform.operations;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;

/** Linear permutation */
public class Permutation {
	
	/** permutation cipher. https://crypto.interactive-maths.com/permutation-cipher.html
	 * similar to columnar but read out via rows instead of columns.
	 */
	public static String transform(String input, int[] permutation, boolean reverse) {
		StringBuilder xform = new StringBuilder();
		for (int i=0; i<input.length(); i++) xform.append(" ");
		int i = 0;
		int index = 0;
		while (i < input.length()) {
//			System.out.println("i "+ i);
			for (int j = 0; j < permutation.length; j++) {
				int pos = i + permutation[j];
				if (pos < input.length()) {
					if (reverse) {
						xform.setCharAt(i+permutation[j], input.charAt(index++));
					} else {
						xform.setCharAt(index++, input.charAt(i + permutation[j]));
					}
				}
			}
			i += permutation.length;
			if (i >= input.length())
				break;
		}
		return xform.toString();
	}
	
	public static void test() {
		String input = Ciphers.Z408_SOLUTION.substring(0, 340);
		Random rand = new Random();
		for (int i=0; i<1000; i++) {
			int size = 2+rand.nextInt(19); // [2,20]
			int[] permutation = new int[size];
			for (int j=0; j<size; j++) permutation[j] = j;
			HomophonicGenerator.shuffle(permutation);
			boolean retry = true;
			for (int j=0; j<size; j++) {
				if (permutation[j] != j) {
					retry = false;
					break;
				}
			}
			if (retry) continue;
			String xform = transform(input, permutation, false);
			String unxform = transform(xform, permutation, true);
			System.out.println(Arrays.toString(permutation));
			System.out.println("  xform: " + xform);
			System.out.println("unxform: " + unxform);
			if (!unxform.equals(input))
				throw new RuntimeException("NO MATCH");
		}
	}
	
	public static void main(String[] args) {
		test();
	}
	
}
