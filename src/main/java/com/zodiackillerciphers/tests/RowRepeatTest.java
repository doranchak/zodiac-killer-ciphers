package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

public class RowRepeatTest {
	/** returns number of rows that have no repeats */
	public static int rowsWithNoRepeats(String cipher, int width) {
		int count = 0;
		
		int rows = cipher.length()/width;
		for (int row=0; row<rows; row++) {
			String r = "";
			for (int col=0; col<width; col++) {
				char ch = cipher.charAt(row*width+col); 
				if (r.contains(""+ch)) {
					count++;
					break;
				}
				r+=ch;
			}
		}
		return rows-count;
	}
	
	public static void test() {
		System.out.println("z408: " + rowsWithNoRepeats(Ciphers.cipher[1].cipher, 17));
		System.out.println("z340: " + rowsWithNoRepeats(Ciphers.cipher[0].cipher, 17));

		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		
		for (int i=0; i<1000000; i++) {
			String cipher = CipherTransformations.shuffle(Ciphers.cipher[1].cipher);
			int rows = rowsWithNoRepeats(cipher, 17);
			Integer val = counts.get(rows);
			if (val == null) val = 0;
			val++;
			counts.put(rows, val);
			
		}
		//System.out.println(result + ", sum " + sum);
		for (Integer key : counts.keySet()) System.out.println(key + " " + counts.get(key));
		
	}
	
	public static void main(String[] args) {
		test();
	}
}
