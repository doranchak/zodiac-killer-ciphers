package com.zodiackillerciphers.tests;

public class MonomeDinome {
	// generate all possible ways to divide the cipher up into cipher units (monomes or dinomes)
	public static void findAllDivisions(String cipher) {
		MonomeDinomeCounter counter = new MonomeDinomeCounter();
		findAllDivisions("", cipher, counter);
		System.out.println("Length " + cipher.length() + ", Count: " + counter.count);
	}
	public static void findAllDivisions(String prefix, String current, MonomeDinomeCounter counter) {
		// stop condition: we reached the end of the input
		if (current.length() == 0) {
			System.out.println(prefix);
			counter.increment();
			return;
		}
		// two possibilities: the beginning of the input is a monome, or it is a dinome.
		if (current.length() > 0) {
			findAllDivisions(prefix + current.substring(0, 1) + " ", current.substring(1), counter);
		}
		if (current.length() > 1) {
			findAllDivisions(prefix + current.substring(0, 2) + " ", current.substring(2), counter);
		}
	}
	public static void test() {
		String test = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
		for (int L=1; L<test.length(); L++) {
			findAllDivisions(test.substring(0, L));
		}
	}
	public static void main(String[] args) {
		test();
	}
}
