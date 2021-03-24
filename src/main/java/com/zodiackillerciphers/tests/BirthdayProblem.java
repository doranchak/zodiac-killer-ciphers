package com.zodiackillerciphers.tests;

import java.util.Random;

/** more generalized way to measure coincidences.
 * 
 * original birthday problem is:  what are the chances of 2 people among a group of N to have the same birthday?
 * birthday is a fact with 365 options.
 * 
 * generalize to:  what are the chances of S people among N to have the same fact f selected from a set of options F.
 * 
 * but let's expand facts to a larger variety.  instead of just birthdays, what about other facts like "places a person lived"
 * or "favorite color", etc.  how many types of facts are needed to guarantee a certain number of coincidences 
 * among S people selected from N?  
 * 
 * @author doranchak
 *
 */
public class BirthdayProblem {
	/** estimate birthday coincidence based on random trials */
	public static void testBirthday(int selectedPeople) {
		Random rand = new Random();
		long total = 0;
		int hits = 0;
		int misses = 0;
		while (true) {
			total++;
			
			int[] birthdays = new int[selectedPeople];
			for (int i=0; i<selectedPeople; i++) {
				birthdays[i] = rand.nextInt(365);
			}
			
			if (total % 10000 == 0) {
				float chance = hits;
				chance /= total;
				System.out.println(chance+ " in " + total + " trials.");
			}
		}
	}
}
