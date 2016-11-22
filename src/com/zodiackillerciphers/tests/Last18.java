package com.zodiackillerciphers.tests;

public class Last18 {
	/** permutations of the variants in the last 18, based on the concerned citizen key */
	public static void concerned() {
		for (String a : new String[] {"I","W"}) {
			for (String b : new String[] {"O","T"}) {
				for (String c : new String[] {"F","K","L","M"}) {
					for (String d : new String[] {"E","S"}) {
						for (String e : new String[] {"O","T"}) {
							System.out.println("EBEOR" + a + "E" + b + "E" + c + d + "THHPI" + e + "I");
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		concerned();
	}
}
