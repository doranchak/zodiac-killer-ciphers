package com.zodiackillerciphers.tests;

/**
 * Gareth Pennanity

Sent: Mon Sep 22, 2014 11:40 am
by glurk
Hey, something I saw on OPORD or Ray Grant's blog was this from "Times 17"

"Here’s a mathematical curiosity. Berta Margoulies’ birthday is 9-7. When you write those numbers in binary,
you get 1001-111. Take out the hyphen and fuse the numbers together, as 1001111, and you have binary 79.
Nine and seven flip-flop. I believe that this is the only pair of numbers that does this."

I was bored, so I checked this. Of the set of 00 - 99 I found that four pairs worked. If you get bored, it's a fun experiment.

-glurk
 */
public class FlipFlopPairs {
	public static boolean isFlipFlopPair(int a, int b) {
		//a*2^(floor(log(b)/log(2))+1)+b = b*10^(floor(log(a)/log(10))+1)+a
		
		double y = a*Math.pow(2.0, Math.floor(Math.log(b)/Math.log(2))+1)+b;
		double z = b*Math.pow(10.0, Math.floor(Math.log(a)/Math.log(10))+1)+a;
		//System.out.println(y);
		//System.out.println(z);
		int yy = (int) y;
		int zz = (int) z;
		return y==z;
	}
	
	public static void testFlipFlopPairs() {
		for (int a=1; a<=10000; a++) {
			for (int b=1; b<=10000; b++) {
				if (isFlipFlopPair(a,b)) System.out.println(a + ", " + b);
			}
			
		}
	}
	public static void main(String[] args) {
		testFlipFlopPairs();
	}
}
