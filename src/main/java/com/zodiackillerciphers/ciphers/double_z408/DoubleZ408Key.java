package com.zodiackillerciphers.ciphers.double_z408;

import java.util.Arrays;

import com.zodiackillerciphers.ciphers.Ciphers;

/**
 * Allen TX shooter cipher has a line that decodes to GHOST if you apply Z408
 * key, treat result as cipher text, and apply Z408 key again
 */
public class DoubleZ408Key {
	public static String decode(int n, String cipher) {
		System.out.println("Decoder map: " + Ciphers.decoderMap);
		System.out.println("Applying Z408 key " + n + " times to cipher [" + cipher + "]:");
		for (int i = 0; i < n; i++) {
			cipher = Ciphers.decode(cipher, Ciphers.decoderMap).toUpperCase();
			System.out.println(" - Round " + (i+1) + ": " + cipher); 
		}
		return cipher;
	}

	public static void main(String[] args) {
		decode(Integer.valueOf(args[0]), args[1]);
	}
}
