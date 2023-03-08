package com.zodiackillerciphers.tests.keyboard;

import com.zodiackillerciphers.ciphers.crimo.CrimoCipher;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/** measurements based on how close numbers are on keyboard layouts */
public class NumberDistance {
	public static double averageNumberDistance(String cipher, NumberLayout layout) {
		double average = 0;
		for (int i=1; i<cipher.length(); i++) {
			String a = cipher.substring(i-1,i);
			String b = cipher.substring(i,i+1);
			average += layout.distance(a, b);
//			System.out.println(a + " " + b + " " + layout.distance(a, b));
		}
		average /= cipher.length()-1;
		return average;
	}
	
	public static void testCrimo(int shuffles) {
		
		System.out.println("=== 1D ===");
		StatsWrapper stats = new StatsWrapper();
		stats.name = "1d";
		stats.actual = averageNumberDistance(CrimoCipher.cipherCrimo9, new NumberLayout1D());
		for (int i=0; i<shuffles; i++)
			stats.addValue(averageNumberDistance(CipherTransformations.shuffle(CrimoCipher.cipherCrimo9), new NumberLayout1D()));
		stats.output();
		
		System.out.println("=== 2D ===");
		stats = new StatsWrapper();
		stats.name = "2d";
		stats.actual = averageNumberDistance(CrimoCipher.cipherCrimo9, new NumberLayout2D());
		for (int i=0; i<shuffles; i++)
			stats.addValue(averageNumberDistance(CipherTransformations.shuffle(CrimoCipher.cipherCrimo9), new NumberLayout2D()));
		stats.output();
		
		System.out.println("=== BASIC ===");
		stats = new StatsWrapper();
		stats.name = "basic";
		stats.actual = averageNumberDistance(CrimoCipher.cipherCrimo9, new NumberLayoutBasic());
		for (int i=0; i<shuffles; i++)
			stats.addValue(averageNumberDistance(CipherTransformations.shuffle(CrimoCipher.cipherCrimo9), new NumberLayoutBasic()));
		stats.output();

	}
	
	public static void main(String[] args) {
		testCrimo(10000);
	}
}
