package com.zodiackillerciphers.ciphers.algorithms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.zodiackillerciphers.ciphers.FatesUnwind;

public class InlineTransposition {

	public static void validate(String str, int[] columns) {
		int[] validate = columns.clone();
		Arrays.sort(validate);
		for (int i = 0; i<validate.length; i++) {
			if (validate[i] != i) throw new RuntimeException("Invalid columns");
		}
		
		if (str.length() % columns.length > 0)
			throw new RuntimeException("Plaintext must be a multiple of column length");
		
	}
	
	/** columns are indexed starting at 1.
	 * NOTE: assumes input has no spaces */
	public static String encode(String plaintext, int[] columns) {
		validate(plaintext, columns);
		
		StringBuffer ct = new StringBuffer();
		
		int c = 0; int offset = 0; int col;
		for (int i=0; i<plaintext.length(); i++) {
			if (i>0 && i % columns.length == 0) offset += columns.length;
			col = columns[c];
			//System.out.println(i+","+offset+","+col);
			if (offset+col < plaintext.length()) {
				ct.append(plaintext.charAt(offset+col));
				//System.out.println(plaintext.charAt(i)+" -> " + plaintext.charAt(offset+col));
				
			}
			c = (c+1) % columns.length;
		}
		return ct.toString().replaceAll(" ", "");
	}
	
	public static String decode(String ciphertext, int[] columns) {
		validate(ciphertext, columns);
		StringBuffer pt = new StringBuffer(ciphertext);
		
		int c = 0; int offset = 0; int col;
		for (int i=0; i<ciphertext.length(); i++) {
			if (i>0 && i % columns.length == 0) offset += columns.length;
			col = columns[c];
			//System.out.println(i+","+offset+","+col);
			if (offset+col < ciphertext.length()) {
				pt.setCharAt(offset+col, ciphertext.charAt(i));
				//System.out.println(plaintext.charAt(i)+" -> " + plaintext.charAt(offset+col));
				
			}
			c = (c+1) % columns.length;
		}
		return pt.toString().replaceAll(" ", "");
	}
	
	public static void test() {
		String pt = "WEAREDISCOVEREDFLEEATONCE";
		int[] cols = new int[] {2,0,1,4,3};
		String ct = encode(pt, cols);
		System.out.println(ct);
		
		System.out.println(decode(ct, cols));
		
		for (int i=0; i<100; i++) {
			cols = rand();
			for (int k=0; k<cols.length; k++) cols[k]--;
			String pt2 = FatesUnwind.pad(pt, cols.length);
			//String pt2 = pt;
			System.out.println("----");
			System.out.println(encode(pt2, cols));
			System.out.println(decode(FatesUnwind.pad(encode(pt2, cols), cols.length), cols));
		}
	}
	
	public static String toString(int[] array) {
		String result = "";
		for (int i : array) result += i + " ";
		return result;
	}
	
	public static int[] rand() {
		int L=(int)(1+Math.random()*10);
		int[] result = new int[L];
		Set<Integer> set = new HashSet<Integer>();
		for (int i=0; i<L; i++) {
			int col = 1 + (int) (Math.random()*L);
			while (set.contains(col)) col = 1 + (int) (Math.random()*L);
			set.add(col);
			result[i] = col;
		}
		return result;
	}
	
	public static void main(String[] args) {
		test();
	}
}
