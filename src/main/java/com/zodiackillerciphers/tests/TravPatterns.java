package com.zodiackillerciphers.tests;

import com.zodiackillerciphers.ciphers.Ciphers;

/** http://www.zodiackillersite.com/viewtopic.php?t=964&p=8593 */
public class TravPatterns {

	static int W;
	static int H;
	/** search the given cipher for repeated bigrams that also have a nearby repeated symbol */
	public static void search(String cipher, int width) {
		String[] grid = Ciphers.grid(cipher, width);
		W = width;
		H = grid.length;
		
		for (int i=0; i<cipher.length()-3; i++) {
			String bigram = cipher.substring(i,i+2);
			//System.out.println(i);
			//String sub = cipher.substring(i+2);
			int j = cipher.indexOf(bigram, i+2);
			while (j > -1) {
				//System.out.println("..." + j);
				String surround1 = surround(grid, i);
				String surround2 = surround(grid, j);
				
				for (int k=0; k<surround1.length(); k++) {
					char ch1 = surround1.charAt(k);
					char ch2 = surround2.charAt(k);
					if (ch1==ch2) {
						System.out.println(i+","+j+","+bigram+","+ch1+","+surround1+","+surround2);
					}
				}
				j = cipher.indexOf(bigram,j+2);
			}
		}
	}
	
	/** return the string that surrounds the bigram at position p */
	public static String surround(String[] grid, int position) {
		String surround = "";
		
		int row = position/W;
		int col = position % W;
		
		row = (H+row-1)%H;
		col = (W+col-1)%W;
		surround += grid[row].charAt(col);
		col = (col+1)%W;
		surround += grid[row].charAt(col);
		col = (col+1)%W;
		surround += grid[row].charAt(col);
		col = (col+1)%W;
		surround += grid[row].charAt(col);
		row = (row+2)%H;
		surround += grid[row].charAt(col);
		col = (W+col-1)%W;
		surround += grid[row].charAt(col);
		col = (W+col-1)%W;
		surround += grid[row].charAt(col);
		col = (W+col-1)%W;
		surround += grid[row].charAt(col);
		return surround;
	}
	
	public static void main(String[] args) {
		search(Ciphers.cipher[0].cipher, 17);
	}
	
	
}
