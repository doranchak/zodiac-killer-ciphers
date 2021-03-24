package com.zodiackillerciphers.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.zodiackillerciphers.numerology.DatesTest;

public class RayGrant {

	static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
	static {
		sdf.setLenient(false);
	}
	/*
	 * Speaking of anagrams, before I move on, note that Gareth begins page 14
	 * of his book with a reference to Dolly Pentreath, the last native speaker
	 * of Cornish, who died on December 26, 1777. As I point out in the 1990
	 * edition of my book, the date Gareth Penn says he discovered the Mt.
	 * Diablo Radian (December 26, 1980) is numerically identical to the date of
	 * the Zodiac's first "public" murder (December 20, 1968).
	 * 
	 * 122068 = 122680, in the sense that the only difference between the
	 * sequence is the position of the zero, which is also a cipher.
	 */
	public static void test1() {
		// generate all possible dates resulting from swap operations
		
		Calendar cal = Calendar.getInstance();
		cal.set(1968, 0, 0);
		
		for (int a=0; a<365*4+3; a++) {
			String d = sdf.format(cal.getTime());
			System.out.println("Testing " + d);
			String arrangements = "";
			Set<String> seen = new HashSet<String>();
			seen.add(d);
			int count = 0; int dupes = 0;
			for (int i=0; i<d.length()-1; i++) {
				for (int j=i+1; j<d.length(); j++) {
					StringBuffer sb = new StringBuffer(d);
					char c1 = sb.charAt(i);
					char c2 = sb.charAt(j);
					if (c1 == '-') continue;
					if (c2 == '-') continue;
					sb.setCharAt(j, c1);
					sb.setCharAt(i, c2);
					if (seen.contains(sb.toString())) {
						dupes++;
						continue;
					}
					seen.add(sb.toString());
					//System.out.println(sb + " ? " + validDate(sb.toString()));
					if (validDate(sb.toString())) {
						count++;
						arrangements += sb + " ";
					}
				}
			}
			//System.out.println(seen.size() + " rearrangements, " + count + " are valid, " + dupes + " dupes");
			System.out.println(d + ": " + count + ": " + arrangements);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		
	}
	
	/** show factors that produce dates of Z crimes */
	public static void test2() {

		int[] nums = new int[] {
				122068,
				70469,
				92769,
				101169,
				60463,
				103066,
				90670,
				32270
		};
		for (int num : nums) {
			System.out.println(num + ": " + DatesTest.factors(num, 1));
		}
	}
	public static boolean validDate(String d) {
		try {
			sdf.setLenient(false);
			sdf.parse(d);
			return true;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return false;
	}
	public static void main(String[] args) {
		test2();
	}
}
