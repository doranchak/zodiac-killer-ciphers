package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.numerology.DatesTest;
import com.zodiackillerciphers.transform.CipherTransformations;

/** testing Dan Johnson's prime placement ideas */
public class PrimeShuffle {
	// checks whether an int is prime or not.
	public static boolean isPrime(int n) {
		if (n < 2) return false;
		if (n == 2) return true;
		// check if n is a multiple of 2
		if (n % 2 == 0)
			return false;
		// if not, then just check the odds
		for (int i = 3; i * i <= n; i += 2) {
			if (n % i == 0)
				return false;
		}
		return true;
	}
	
	public static Map<Character, int[]> primeCountMapFor(String cipher) {
		Map<Character, int[]> map = new HashMap<Character, int[]>();
		for (int i = 0; i < cipher.length(); i++) {
			int num = i + 1;
			boolean prime = isPrime(num);
			char key = cipher.charAt(i);
			int[] val = map.get(key);
			if (val == null)
				val = new int[2];
			val[prime ? 1 : 0]++;
			map.put(key, val);
		}
		return map;
	}
	
	/** dump prime counts in sorted order */
	public static void dump(Map<Character, int[]> map) {
		List<int[]> list = new ArrayList(map.values());
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return Integer.compare(((int[])o1)[1], ((int[])o2)[1]);
			}
			
		});
		
		for (int[] item : list) {
			System.out.println(item[0] + " " + item[1]);
		}
	}
	
	/** dump just the relative "phobia" scores */
	public static void dump2(Map<Character, int[]> map) {
		
		// each symbol is tested for prime phobia (it falls on only zero or one prime positions)
		// if a symbol is prime phobic, add its frequency to the score, as a measure of "coverage"
		
		int coverage = 0;
		for (int[] val : map.values()) {
			int n = val[0] + val[1];
			if (val[1] < 2) coverage += n; 
		}
		System.out.println(coverage);
	}

	/*
	 * for each symbol, determine how many prime positions it falls on, and how
	 * many it does not.
	 */
	public static boolean countPrimesFor(String cipher, boolean test) {
		Map<Character, int[]> map = primeCountMapFor(cipher);
		int result = 0;

		boolean b1 = false;
		boolean b2 = false;
		boolean b3 = false;

		
		for (Character key : map.keySet()) {
			int[] val = map.get(key);

			if (!test)
				System.out.println(key + ", " + val[0] + ", " + val[1] + ", "
						+ (val[0] + val[1]) + ", " + ((float) val[0])
						/ (val[0] + val[1]));
			if (test) {
				// if (key == 'B' && val[1] < 2) b1 = true;
				// if (key == 'P' && val[1] < 2) b2 = true;
				// if (key == '9' && val[1] < 2) b3 = true;
				if (val[1] == (val[0] + val[1])) {
					// System.out.println(key + ", " + val[0] + ", " + val[1] +
					// ", " + (val[0]+val[1]) + ", " +
					// ((float)val[0])/(val[0]+val[1]));
					b1 = true;
				}
			}
		}
		// return b1 && b2 && b3;
		return b1;
	}

	// is a message hidden ONLY at the prime positions?
	public static void nullCipher() {
		int[] primes = new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37,
				41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103,
				107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167,
				173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233,
				239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307,
				311, 313, 317, 331, 337 };
		
		String z340 = Ciphers.cipher[0].cipher;
		
		String chunk = "";
		for (int i=0; i<primes.length; i++) {
			System.out.println(primes[i] + ": " + z340.charAt(primes[i]-1));
			chunk += z340.charAt(primes[i]-1);
		}
		System.out.println(chunk);
	}
	
	// given a cipher block of r rows, and c columns: 
	// if you remove everything but the prime positions, is
	// the resulting cipher length evenly divisible by c?
	public static boolean primeFeature(int r, int c) {
		int p = primesBetween(0, r*c);
		return (p % c == 0);
	}
	
	public static int primesBetween(int a, int b) {
		int count = 0;
		for (int i=a; i<=b; i++) if (isPrime(i)) count++;
		return count;
	}
	
	public static void testFeatures() {
		int tested = 0;
		int hits = 0;
		for (int col=2; col<=100; col++) {
			for (int row=2; row<=100; row++) {
				tested++;
				int p = primesBetween(0,row*col);
				if (primeFeature(row, col)) {
					hits++;
					System.out.println("Grid with " + row + " rows and " + col + " cols has " + (row*col) + " total spots, of which " + p + " are prime.  The number " + p + " divides evenly by " + col + " into " + (p/col) + " rows.");
				}
			}
		}
		System.out.println("Hits: " + hits + " out of " + tested);
	}
	
	public static void factorsFind() {
		for (int i=0; i<10000; i++) {
			i=340;
			int p = primesBetween(0, i);
			//System.out.println(p);
			
			List<Integer> list = DatesTest.factors(p, 2);
			if (list.size() > 0) {
				String line = "" + i + ", " + p + ": ";
				
				for (Integer factor : list) 
					line += factor + " X " + (i/factor);
				
			}
			
			
		}
		
	}
	
	static void testMap() {
		//Map<Character, int[]> map = primeCountMapFor(Ciphers.cipher[0].cipher);
		Map<Character, int[]> map = primeCountMapFor(CipherTransformations.shuffle(Ciphers.cipher[0].cipher));
		dump2(map);
	}

	public static void main(String[] args) {
		testMap();
		// System.out.println(countPrimesFor(Ciphers.cipher[0].cipher, true));
		
		//nullCipher();
		
		//factorsFind();
		//System.out.println(primeFeature(20, 17));
		//testFeatures();
		
		/*
		int matches = 0;
		for (int i = 0; i < 1000000; i++) {
			if (i % 10000 == 0)
				System.out.println(i);
			if (countPrimesFor(Ciphers.shuffle(Ciphers.cipher[0].cipher), true))
				matches++;
		}

		System.out.println(matches);
		*/

		// System.out.println(Math.pow(1.0/5.0,24)+ Math.pow(1.0/5.0,12)+
		// Math.pow(1.0/5.0,11)+ Math.pow(1.0/5.0,10)+ Math.pow(1.0/5.0,10)+
		// Math.pow(1.0/5.0,10)+ Math.pow(1.0/5.0,10)+ Math.pow(1.0/5.0,9)+
		// Math.pow(1.0/5.0,9)+ Math.pow(1.0/5.0,8)+ Math.pow(1.0/5.0,7)+
		// Math.pow(1.0/5.0,7)+ Math.pow(1.0/5.0,7)+ Math.pow(1.0/5.0,7)+
		// Math.pow(1.0/5.0,7)+ Math.pow(1.0/5.0,6)+ Math.pow(1.0/5.0,6)+
		// Math.pow(1.0/5.0,6)+ Math.pow(1.0/5.0,6)+ Math.pow(1.0/5.0,6)+
		// Math.pow(1.0/5.0,6)+ Math.pow(1.0/5.0,6)+ Math.pow(1.0/5.0,6)+
		// Math.pow(1.0/5.0,6)+ Math.pow(1.0/5.0,5)+ Math.pow(1.0/5.0,5)+
		// Math.pow(1.0/5.0,5)+ Math.pow(1.0/5.0,5)+ Math.pow(1.0/5.0,5)+
		// Math.pow(1.0/5.0,5)+ Math.pow(1.0/5.0,5)+ Math.pow(1.0/5.0,5)+
		// Math.pow(1.0/5.0,5)+ Math.pow(1.0/5.0,5)+ Math.pow(1.0/5.0,4)+
		// Math.pow(1.0/5.0,4)+ Math.pow(1.0/5.0,4)+ Math.pow(1.0/5.0,4)+
		// Math.pow(1.0/5.0,4)+ Math.pow(1.0/5.0,4)+ Math.pow(1.0/5.0,4)+
		// Math.pow(1.0/5.0,4)+ Math.pow(1.0/5.0,4)+ Math.pow(1.0/5.0,4)+
		// Math.pow(1.0/5.0,4)+ Math.pow(1.0/5.0,3)+ Math.pow(1.0/5.0,3)+
		// Math.pow(1.0/5.0,3)+ Math.pow(1.0/5.0,3)+ Math.pow(1.0/5.0,3)+
		// Math.pow(1.0/5.0,3)+ Math.pow(1.0/5.0,3)+ Math.pow(1.0/5.0,3)+
		// Math.pow(1.0/5.0,3)+ Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+
		// Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+
		// Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+
		// Math.pow(1.0/5.0,1));
		// System.out.println(Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+
		// Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+
		// Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+ Math.pow(1.0/5.0,2)+
		// Math.pow(1.0/5.0,1));

	}
}
