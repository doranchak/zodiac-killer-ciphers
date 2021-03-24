package com.zodiackillerciphers.tests.mapcode;

import java.text.DecimalFormat;

public class EnglishNumberToWords {
	private static final String[] tensNames = { "", " ten", " twenty",
			" thirty", " forty", " fifty", " sixty", " seventy", " eighty",
			" ninety" };

	private static final String[] numNames = { "", " one", " two", " three",
			" four", " five", " six", " seven", " eight", " nine", " ten",
			" eleven", " twelve", " thirteen", " fourteen", " fifteen",
			" sixteen", " seventeen", " eighteen", " nineteen" };

	private EnglishNumberToWords() {
	}

	private static String convertLessThanOneThousand(int number) {
		String soFar;

		if (number % 100 < 20) {
			soFar = numNames[number % 100];
			number /= 100;
		} else {
			soFar = numNames[number % 10];
			number /= 10;

			soFar = tensNames[number % 10] + soFar;
			number /= 10;
		}
		if (number == 0)
			return soFar;
		return numNames[number] + " hundred" + soFar;
	}

	public static String convert(long number) {
		// 0 to 999 999 999 999
		if (number == 0) {
			return "zero";
		}

		String snumber = Long.toString(number);

		// pad with "0"
		String mask = "000000000000";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXnnnnnnnnn
		int billions = Integer.parseInt(snumber.substring(0, 3));
		// nnnXXXnnnnnn
		int millions = Integer.parseInt(snumber.substring(3, 6));
		// nnnnnnXXXnnn
		int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
		// nnnnnnnnnXXX
		int thousands = Integer.parseInt(snumber.substring(9, 12));

		String tradBillions;
		switch (billions) {
		case 0:
			tradBillions = "";
			break;
		case 1:
			tradBillions = convertLessThanOneThousand(billions) + " billion ";
			break;
		default:
			tradBillions = convertLessThanOneThousand(billions) + " billion ";
		}
		String result = tradBillions;

		String tradMillions;
		switch (millions) {
		case 0:
			tradMillions = "";
			break;
		case 1:
			tradMillions = convertLessThanOneThousand(millions) + " million ";
			break;
		default:
			tradMillions = convertLessThanOneThousand(millions) + " million ";
		}
		result = result + tradMillions;

		String tradHundredThousands;
		switch (hundredThousands) {
		case 0:
			tradHundredThousands = "";
			break;
		case 1:
			tradHundredThousands = "one thousand ";
			break;
		default:
			tradHundredThousands = convertLessThanOneThousand(hundredThousands)
					+ " thousand ";
		}
		result = result + tradHundredThousands;

		String tradThousand;
		tradThousand = convertLessThanOneThousand(thousands);
		result = result + tradThousand;

		// remove extra spaces!
		return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
	}

	public static void test1() {
		System.out.println("*** " + EnglishNumberToWords.convert(0));
		System.out.println("*** " + EnglishNumberToWords.convert(1));
		System.out.println("*** " + EnglishNumberToWords.convert(16));
		System.out.println("*** " + EnglishNumberToWords.convert(100));
		System.out.println("*** " + EnglishNumberToWords.convert(118));
		System.out.println("*** " + EnglishNumberToWords.convert(200));
		System.out.println("*** " + EnglishNumberToWords.convert(219));
		System.out.println("*** " + EnglishNumberToWords.convert(800));
		System.out.println("*** " + EnglishNumberToWords.convert(801));
		System.out.println("*** " + EnglishNumberToWords.convert(1316));
		System.out.println("*** " + EnglishNumberToWords.convert(1000000));
		System.out.println("*** " + EnglishNumberToWords.convert(2000000));
		System.out.println("*** " + EnglishNumberToWords.convert(3000200));
		System.out.println("*** " + EnglishNumberToWords.convert(700000));
		System.out.println("*** " + EnglishNumberToWords.convert(9000000));
		System.out.println("*** " + EnglishNumberToWords.convert(9001000));
		System.out.println("*** " + EnglishNumberToWords.convert(123456789));
		System.out.println("*** " + EnglishNumberToWords.convert(2147483647));
		System.out.println("*** " + EnglishNumberToWords.convert(3000000010L));
	}
	/** convert to uppercase words */
	public static String convertUpper(long number) {
		return convert(number).toUpperCase();
	}
	/** convert number such as 123 to digits: ONE TWO THREE. */
	public static String convertUpperDigits(long number) {
		String result = "";
		String str = "" + number;
		for (int i=0; i<str.length(); i++) {
			String num = "" + str.charAt(i);
			if (result.length() > 0) result += " ";
			result += convertUpper(Long.valueOf(num));
		}
		return result;
	}
	public static void test2() {
		for (int i=0; i<10000; i++) {
			System.out.println(i + ": " + EnglishNumberToWords.convert(i));
		}
		
	}
	
	/**
	 * testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		test2();
	}
}
