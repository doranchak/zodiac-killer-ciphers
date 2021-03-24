package com.zodiackillerciphers.tests;

/* http://www.zodiackillerfacts.com/forum/viewtopic.php?f=11&t=1589 */
public class EvilNumbers {
	public static boolean isEvil(int i) {
		String bin = Integer.toBinaryString(i);
		int count = 0;
		for (int a=0; a<bin.length(); a++) {
			char ch = bin.charAt(a);
			if (ch == '1') count++;
		}
		return count % 2 == 0;
	}
	
	public static void main(String[] args) {
		for (int i=1; i<100; i++) {
			if (isEvil(i)) {
				for (int j=i; j<100; j++) {
					if (isEvil(j)) {
						int k = i*j;
						if (isEvil(k)) {
							System.out.println(i + " * " + j + " = " + k + ". " + k + " is evil");
						} else {
							System.out.println(i + " * " + j + " = " + k + ". " + k + " is odious");
						}
					}
				}
			}
		}
	}
}
