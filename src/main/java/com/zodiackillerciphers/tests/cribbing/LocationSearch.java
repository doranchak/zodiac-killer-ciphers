package com.zodiackillerciphers.tests.cribbing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import com.zodiackillerciphers.io.FileUtil;

//TODO: larger lists of locations 
//TODO: add state abbrev
//TODO: expand/collapse other abbrevs (ST, DR, DR, N, S, etc)
//TODO: anagram search

public class LocationSearch {

	/**
	 * formats: 1: US.txt 2: geonames.usgs.gov  3: openstreetmaps
	 * 
	 * whichCipher: 1: unmodified L=20 2: modified L=20 3: L=9 4: modified L=29
	 * 
	 */
	public static void searchLocations(String path, int format,
			int whichCipher, int maxErrors) {

		int cipherLength = 0;
		if (whichCipher == 3)
			cipherLength = 9;
		else if (whichCipher == 4)
			cipherLength = 29;
		else
			cipherLength = 20;

		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String regex;
				if (format == 1) 
					regex = "	";
				else
					regex = "\\|";
					
				int col = 1;
				if (format == 3) col = 0;
				String[] split = line.split(regex);
				String raw = split[col];
				String converted = "";
				String[] tokens = tokenize(raw.toUpperCase());
				for (String s : tokens) {
					converted += FileUtil.convert(s).toString();
					if (converted.length() >= cipherLength)
						break;
				}

				int e = 0;
				if (whichCipher == 1)
					e = errors1(converted);
				else if (whichCipher == 2)
					e = errors2(converted);
				else if (whichCipher == 3)
					e = errors3(converted);
				else if (whichCipher == 4)
					e = errors4(converted);

				int diff = converted.length() - cipherLength;

				int s = (1 + e) * (1 + Math.abs(diff));

				String result = "C" + whichCipher + "	" + s + "	" + e + "	"
						+ diff + "	" + converted + "	" + line;
				if (e <= maxErrors)
					System.out.println(result);
				
				counter++;
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("TOTAL LINES: " + counter);
	}

	/** the 20 letter portion of the Donna Lass cipher */
	public static int errors1(String plaintext) {
		// ePTWYPNWA_8WkSNZf99Q
		// 01234567890123456789

		// P: 1==5
		// W: 3==7
		// 7==11
		// N: 6==14
		// 9: 17==18

		int errors = 0;
		if (plaintext.length() < 6)
			return errors;
		if (plaintext.charAt(1) != plaintext.charAt(5))
			errors++;
		if (plaintext.length() < 8)
			return errors;
		if (plaintext.charAt(3) != plaintext.charAt(7))
			errors++;
		if (plaintext.length() < 12)
			return errors;
		if (plaintext.charAt(7) != plaintext.charAt(11))
			errors++;
		if (plaintext.length() < 15)
			return errors;
		if (plaintext.charAt(6) != plaintext.charAt(14))
			errors++;
		if (plaintext.length() < 19)
			return errors;
		if (plaintext.charAt(17) != plaintext.charAt(18))
			errors++;
		return errors;
	}

	/**
	 * the 20 letter portion of the Donna Lass cipher (allowing for wildcard to
	 * account for "YOU" in original proposed solution)
	 */
	public static int errors2(String plaintext) {
		// ePTWYPNWA_8WkSNZf99Q
		// 01234567890123456789

		// P: 1==5
		// W: 3==7
		// 7==11
		// N: 6==14
		int errors = 0;
		if (plaintext.length() < 6)
			return errors;
		if (plaintext.charAt(1) != plaintext.charAt(5))
			errors++;
		if (plaintext.length() < 8)
			return errors;
		if (plaintext.charAt(3) != plaintext.charAt(7))
			errors++;
		if (plaintext.length() < 12)
			return errors;
		if (plaintext.charAt(7) != plaintext.charAt(11))
			errors++;
		if (plaintext.length() < 15)
			return errors;
		if (plaintext.charAt(6) != plaintext.charAt(14))
			errors++;
		return errors;
	}

	/** the 9 letter portion of the Donna Lass cipher */
	public static int errors3(String plaintext) {
		// %9ZZWkW__
		// 012345678

		// 2==3
		// 4==6
		// 7==8
		int errors = 0;
		if (plaintext.length() < 4)
			return errors;
		if (plaintext.charAt(2) != plaintext.charAt(3))
			errors++;
		if (plaintext.length() < 7)
			return errors;
		if (plaintext.charAt(4) != plaintext.charAt(6))
			errors++;
		if (plaintext.length() < 9)
			return errors;
		if (plaintext.charAt(7) != plaintext.charAt(8))
			errors++;
		return errors;
	}

	/**
	 * the entire Donna Lass cipher (allowing for wildcard to account for "YOU"
	 * in original proposed solution)
	 */
	public static int errors4(String plaintext) {
		// ePTWYPNWA_8WkSNZfX9Q%9ZZWkW__
		// 01234567890123456789012345678
		// 00000000001111111111222222222

		// 2 P: 1==5
		// 5 W: 3==7
		//      7==11
		//      11=24
		//      24=26      
		// 2 N: 6==14
		// 3 _: 9==27
		//      27=28
		// 3 Z: 15==22
		//      22==23
		// 2 k: 12==25
		// 2 9: 18==21
		
		int errors=0;
		if (!match(plaintext, 1, 5))
			errors++;
		if (!match(plaintext, 3, 7))
			errors++;
		if (!match(plaintext, 7, 11))
			errors++;
		if (!match(plaintext, 11, 24))
			errors++;
		if (!match(plaintext, 24, 26))
			errors++;
		if (!match(plaintext, 6, 14))
			errors++;
		if (!match(plaintext, 9, 27))
			errors++;
		if (!match(plaintext, 27, 28))
			errors++;
		if (!match(plaintext, 15, 22))
			errors++;
		if (!match(plaintext, 22, 23))
			errors++;
		if (!match(plaintext, 12, 25))
			errors++;
		if (!match(plaintext, 18, 21))
			errors++;
		return errors;
	}
	
	// returns false only if characters exist at the given locations and don't match.
	static boolean match(String plain, int a, int b) {
		if (plain.length() < a+1) return true;
		if (plain.length() < b+1) return true;
		return plain.charAt(a) == plain.charAt(b);
	}

	public static String[] tokenize(String str) {
		String[] result = str.split("[^A-Z]");
		// System.out.println(Arrays.toString(result));
		return result;
	}

	public static void testTokenize() {
		String test = "A B+_+*@#()*!()@#C";
		System.out.println(Arrays.toString(tokenize(test)));
	}

	public static void testSearchLocations() {
//		String path = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/locations/US/US.txt";
//		for (int which=1; which<=4; which++) {
//			searchLocations(
//					path,
//					1, which, which == 3 ? 0 : 1);
//			
//		}
//		String path = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/locations/geonames.usgs.gov/AllNames_20170801.txt";
//		for (int which=1; which<=4; which++) {
//			searchLocations(
//					path,
//					2, which, which == 3 ? 0 : 1);
//			
//		}
//		String path = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/locations/us census gazeteer/NationalFile_20170801.txt";
//		for (int which=1; which<=4; which++) {
//			searchLocations(
//					path,
//					2, which, which == 3 ? 0 : 1);
//			
//		}
		String path = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/locations/openstreetmap/map.4";
		for (int which=1; which<=4; which++) {
			searchLocations(
					path,
					3, which, which == 3 ? 0 : 1);
		}
		
	}
	
	public static void testC3() {
		String sol = "BURROCOURT";
		System.out.println(errors3(sol));
		
	}

	public static void main(String[] args) {
		testSearchLocations();
		// testTokenize();
		//testC3();
	}
}
