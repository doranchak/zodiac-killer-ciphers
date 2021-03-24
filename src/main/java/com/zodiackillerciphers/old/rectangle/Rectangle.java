package com.zodiackillerciphers.old.rectangle;

import com.zodiackillerciphers.ciphers.Ciphers;

/** represents a rectangular selection of cipher text */
public class Rectangle {

	/** the cipher block */
	public static char[][] block; 
	
	/** upper-left corner */
	public int row1;
	public int col1;
	
	/** lower-right corner */
	public int row2;
	public int col2;

	/** compute area of rectangle */
	public int area() {
		return width() * height();
	}
	
	/** compute width of rectangle */
	public int width() {
		return col2-col1+1;
	}
	
	/** compute height of rectangle */
	public int height() {
		return row2-row1+1;
	}
	
	/** obtain cipher text from rectangle using the given method */
	
	/** read from the given corner, and proceed in the given order */
	//public StringBuffer normal(Corner corner, boolean direction) {
	//	
	//}
	
	/** init the block from the given cipher text */
	public static void initBlock(String cipher, int W) {
		int c=0;
		block = new char[cipher.length()/W][W];
		for (int i=0; i<cipher.length(); i++) {
			block[i/W][i%W] = cipher.charAt(i);
		}
	}
	
	public static void testInitBlock() {
		//initBlock(Ciphers.cipher[0], 17);
		String line;
		for (int row=0; row<block.length; row++) {
			line = "";
			for (int col=0; col<block[row].length; col++) {
				line += block[row][col];
			}
			System.out.println(line);
		}
	}
	
	
}
