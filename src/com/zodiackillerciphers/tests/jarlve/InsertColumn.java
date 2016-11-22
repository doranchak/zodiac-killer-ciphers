package com.zodiackillerciphers.tests.jarlve;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;

/** http://www.zodiackillersite.com/viewtopic.php?p=44258#p44258 */
public class InsertColumn {
	
	/** add a column of spaces.
	 * 
	 * width: column number where we want to insert the column of spaces
	 * shift: number of positions to the left   
	 * 
	 * for example, converting Z340 to width 18 by adding a column of spaces at column 18:
	 * 
	 * start = 17
	 * distance = 18
	 * 
	 */
	public static String insert(String cipher, int width, int shift) {
		if (shift >= width) throw new IllegalArgumentException("Shift should be less than the width.");
		if (width < 1) throw new IllegalArgumentException("Width must be > 0");
		String newCipher = "";
		
		int j=0;
		for (int i=0; i<=cipher.length(); i++) {
			if ((j+1+shift) % width == 0) {
				newCipher += ' ';
				j++;
			}
			if (i<cipher.length()) newCipher += cipher.charAt(i);
			j++;
		}
		return newCipher;
	}
	
	public static void test() {
		String cipher = Ciphers.cipher[0].cipher;
		for (int shift=0; shift<18; shift++) {
			System.out.println(insert(cipher, 18, shift));
		}
		
		System.out.println("=====================");
		for (int width=1; width<100; width++) {
			System.out.println(insert(cipher, width, 0));
		}
	}
	public static void test(int col, int shift) {
		String cipher = Ciphers.cipher[0].cipher;
		cipher = insert(cipher, col, shift);
		System.out.println(cipher);
		String rewritten = Periods.rewrite3(cipher, 105);
		System.out.println(rewritten);
		
		
	}

	public static void testPeaks() {
		String cipher = Ciphers.cipher[0].cipher;
		//cipher = CipherTransformations.shuffle(cipher);
		for (int width=1; width<31; width++) {
			for (int shift=0; shift<width; shift++) {
				String newCipher = insert(cipher, width, shift);
				for (int n=1; n<340/2; n++) {
					String rewritten = Periods.rewrite3(newCipher, n);
					NGramsBean bean = new NGramsBean(6, rewritten);
					int num = bean.numRepeats();
					if (num < 1) continue;
					System.out.println(num + " " + width+" "+shift+" "+n);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		//testPeaks();
		test(2,0);
		//3 28 0 105
	}
}
