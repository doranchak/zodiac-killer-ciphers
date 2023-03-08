package com.zodiackillerciphers.ciphers.algorithms.columnar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.Stats;

public class ColumnarTranspositionOBSOLETE {
	public static StringBuilder encodeOLD(StringBuilder plaintext, int[] key) {
		return encodeOLD(plaintext, key,Variant.TOP_TO_BOTTOM);
	}
	public static StringBuilder encodeOLD(StringBuilder plaintext, int[] key, Variant variation) {
		if (key == null || plaintext == null)
			return plaintext;
		for (int i : key) if (i >= key.length) {
			throw new RuntimeException("Invalid key entry " + i);
		}
		StringBuilder encoded = new StringBuilder();
		int i = 0;
		int width = key.length;
		int pos = key[i];
		if (variation == Variant.BOTTOM_TO_TOP) {
			int rows = plaintext.length() / width; // there are at least this many rows
			pos = pos + (rows-1) * width;
			if (pos + width <= plaintext.length()) // irregular columnar has a partial row
				pos += width;
		}
		while (encoded.length() < plaintext.length()) {
			if (pos >= plaintext.length()) {
				i++;
				if (i == key.length)
					break;
				pos = key[i];
			}
			encoded.append(plaintext.charAt(pos));
			pos += width;
		}
		return encoded;
	}
	public static StringBuilder decodeOLD(StringBuilder ciphertext, int[] key, boolean removeSpaces) {
		if (key == null || ciphertext == null)
			return ciphertext;
		for (int i : key) if (i >= key.length) {
			throw new RuntimeException("Invalid key entry " + i);
		}
		
		int len = ciphertext.length();
		boolean irregular = len % key.length != 0;
		
		int width = key.length;
		int height = len/width;
		if (irregular) height++;

		int padding = irregular ? width - (len % width) : 0;
		int irregularMissingColumnStart = width - padding; 
		
		StringBuilder decoded = new StringBuilder(len);
		for (int i=0; i<len; i++) decoded.append(' ');
//		System.out.println("len " + ciphertext.length() + " width " + width + " height " + height
//				+ " irregularMissingColumnStart + " + irregularMissingColumnStart + " padding " + padding);
		
		int which = 0;
		int col = key[which];
		int row = 0;
		int lastRow = height-1;
		for (int i=0; i<len; i++) {
//			System.out.println(" i " + i+ " row " + row + " (i/width) " + (i/width) + " col " + col + " ch " + ciphertext.charAt(i) + " (row*width+col) " + (row*width+col));
			decoded.setCharAt(row*width+col, ciphertext.charAt(i));
//			System.out.println(decoded + ", " + row + " " + col + ", " + which);
//			System.out.println("DEFENDTHEEASTWALLOFTHECASTLE");
			
			row++;
			if (row > lastRow || (row == lastRow && col >= irregularMissingColumnStart)) {
				which++;
				if (which == key.length) {
					if (removeSpaces) ColumnarTransposition.removeSpaces(decoded);
					return decoded;
				}
				row = 0;
				col = key[which];
			}
		}
		return decoded;
	}
	
}
