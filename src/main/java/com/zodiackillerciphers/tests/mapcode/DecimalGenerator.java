package com.zodiackillerciphers.tests.mapcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 1.230:
 * 
 * One {dot, point} two three One {dot, point} two three {zero, oh} One {dot,
 * point} two three {zero, oh oh}
 * 
 * One {dot, point} twenty three One {dot, point} two hundred thirty One {dot,
 * point} two hundred and thirty
 * 
 */

public class DecimalGenerator {
	public static String[] dots = new String[] { "DOT", "POINT" };

	public static List<Decimal> generate(float val) {
		List<Decimal> list = new ArrayList<Decimal>();
		int whole = (int) val;
		int decimal = (int) Math.round((val - whole) * 10000);

		List<String> wholes = new ArrayList<String>();
		wholes.add(EnglishNumberToWords.convertUpper(whole));
		if (whole > 9) {
			wholes.add(EnglishNumberToWords.convertUpperDigits(whole));
		}
		if (whole == 0) {
			wholes.add("");
		}
		List<String> numbers = new ArrayList<String>();
		if (decimal > 0) {
			int truncates = 0;
			if (decimal % 1000 == 0) truncates = 3;
			else if (decimal % 100 == 0) truncates = 2;
			else if (decimal % 10 == 0) truncates = 1;
			
			int decimalTmp = decimal;
			for (int i=0; i<=truncates; i++) {
				numbers.add(EnglishNumberToWords.convertUpper(decimalTmp));
				if (decimalTmp > 9) {
					numbers.add(EnglishNumberToWords.convertUpperDigits(decimalTmp));
				}
				decimalTmp /= 10;
			}
		} else
			numbers.add("");

		String[] dots2 = dots;
		if (decimal == 0)
			dots2 = new String[] { "" };

		for (String d : dots2) {
			for (String w : wholes) {
				for (String n : numbers) {
					List<String> zeros = new ArrayList<String>();
					int leading = 0;
					// add leading zeros
					if (decimal == 0) {
						leading = 0;
					} else if (decimal < 10) {
						leading = 3;
					} else if (decimal < 100) {
						leading = 2;
					} else if (decimal < 1000) {
						leading = 1;
					}
					if (leading > 0) {
						zeros.add("OH");
						zeros.add("ZERO");
					} else
						zeros.add("");
					for (String z : zeros) {

						if (decimal == 0) {
							list.add(new Decimal(val, w));
							continue;
						}

						String str = "";
						if (!"".equals(w)) {
							str += w + " ";
						}
						str += d + " ";
						if (leading > 0) {
							for (int i = 0; i < leading; i++) {
								str += z + " ";
							}
						}
						str += n;
						list.add(new Decimal(val, str));
					}
				}
			}
		}

		// String decimalString = String.format("%04d", decimal);
		//
		// list.add(new Decimal(val, whole + " " + decimalString));
		return list;
	}

	public static void test() {
		float rnd = (float) Math.random()*1000; 
		System.out.println(generate(rnd));
		System.out.println(generate(1.1f));
		for (int mils = 0; mils <= 640; mils++) {
			float milsActual = ((float)mils)/10; // resolve to tenths of a (100*mil)
			System.out.println(generate(milsActual));
		}
		for (int i=0; i<64; i++) {
			System.out.println(generate(i));
			
		}

	}

	public static void main(String[] args) {
		test();
	}

}
