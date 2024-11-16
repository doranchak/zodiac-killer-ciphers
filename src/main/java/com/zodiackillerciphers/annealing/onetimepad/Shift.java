package com.zodiackillerciphers.annealing.onetimepad;

import java.util.Arrays;
import java.util.Random;

public class Shift {
	
//	public static String[] testPlaintext = {
//		"INCRYPTOGRAPHYTHEONETIMEPADOTPISANENCRYPTIONTECHNIQUETHATCANNOTBECRACKEDBUTREQUI",
//		"THEUSEOFASINGLEUSEPRESHAREDKEYTHATISLARGERTHANOREQUALTOTHESIZEOFTHEMESSAGEBEINGS",
//		"ENTINTHISTECHNIQUEAPLAINTEXTISPAIREDWITHARANDOMSECRETKEYALSOREFERREDTOASAONETIME",
//		"PADTHENEACHBITORCHARACTEROFTHEPLAINTEXTISENCRYPTEDBYCOMBININGITWITHTHECORRESPOND",
//		"INGBITORCHARACTERFROMTHEPADUSINGMODULARADDITIONTHERESULTINGCIPHERTEXTWILLBEIMPOS"			
//	};
	
	public static String[] testPlaintext = {
			"CHAMPIONSHIP",
			"ORGANIZATION",
			"PARTICULARLY",
			"RELATIONSHIP",
			"NEIGHBORHOOD"			
		};
	
	
	public static String shift(String input, int[] key, boolean decrypt) {
		StringBuffer output = new StringBuffer(input);
		for (int i=0; i<key.length && i<input.length(); i++) {
			output.setCharAt(i, shift(input.charAt(i), key[i], decrypt));
		}
		return output.toString();
	}
	public static char shift(char ch, int val, boolean decrypt) {
		if (decrypt) val = -val;
		char output = (char) (((ch - 'A' + val) % 26 + 26) % 26 + 'A');
		return output;
	}
	
	public static void testMakeCiphers(int keyLength) {
		Random rand = new Random();
		
		int[] key = new int[keyLength];		
		for (int i=0; i<keyLength; i++) key[i] = rand.nextInt(26);
		System.out.println(Arrays.toString(key));
		for (String pt : testPlaintext)
			System.out.println(shift(pt, key, false));
	}
	public static void test() {
		String input = "THISISATEST";
		int[] key = {1, 10, 7, 3, 20, 25, 2, 9, 19, 14};
		String cipher = shift(input, key, false);
		System.out.println(cipher);
		System.out.println(shift(cipher, key, true));
	}
	
	public static void main(String[] args) {
//		test();
		testMakeCiphers(12);
	}
}
