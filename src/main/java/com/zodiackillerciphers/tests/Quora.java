package com.zodiackillerciphers.tests;

public class Quora {
	/* http://www.quora.com/How-can-I-solve-p-q-q-p-1844 */
	public static void diophantine(int max, int n) {
		for (int p=0; p<=max; p++) {
			if (p % 1000 == 0) System.out.println(p+"..."); 
			for (int q=0; q<=max; q++) {
				if (Math.pow(p, q) - Math.pow(q, p) == n) 
					System.out.println(p + "^" + q + " - " + q + "^" + p + " = " + n);
			}
		}
	}
	
	public static void main(String[] args) {
		diophantine(10000, 1844);
	}
}
