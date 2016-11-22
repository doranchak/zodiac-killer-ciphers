package com.zodiackillerciphers.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.PrimeShuffle;
import com.zodiackillerciphers.transform.CipherTransformations;

public class PrimePhobia {
	
	/** precomputed prime locations */
	public static Set<Integer> primes;
	static {
		primes = new HashSet<Integer>();
		for (int i=0; i<10000; i++) {
			if (PrimeShuffle.isPrime(i))
				primes.add(i);
		}
	}
	
	/** score the prime-phobia of the given cipher with the given symbol count map.
	 *
	 *	identifies the top 2 symbols.
	 *  for each, returns number of times symbols fall on non-prime positions.
	 *  
	 *  the original 340 returns {23,11}.  (only one + and one B fall on prime positions)  
	 * 
	 */
	public static int[] nonprimes(String cipher, Map<Character, Integer> countMap) {
		if (cipher == null || countMap == null) return null;
		
		int[] max = new int[] {-1, -1};
		char[] cmax = new char[] {0, 0};
		for (Character key : countMap.keySet()) {
			Integer val = countMap.get(key);
			if (val > max[0]) {
				max[1] = max[0];
				max[0] = val;
				
				cmax[1] = cmax[0];
				cmax[0] = key;
			} else if (val > max[1]) {
				max[1] = val;
				cmax[1] = key;
			}
		}
		
		//System.out.println("max " + Arrays.toString(max) + " cmax " + Arrays.toString(cmax));
		
		int[] nonprimes = new int[2];
		for (int i=0; i<cipher.length(); i++) {
			int pos = i+1;
			char ch = cipher.charAt(i);
			if (cmax[0] != ch && cmax[1] != ch) continue;
			if (primes.contains(pos)) continue;
			
			if (cmax[0] == ch) nonprimes[0]++;
			else nonprimes[1]++;
		}
		return nonprimes;
	}

	/** in z340, there are 24 +'s and 12 B's.  only one + falls on a prime position, and only one B falls on a prime position. */ 
	public static PrimePhobiaBean errors(String cipher) {
		PrimePhobiaBean bean = new PrimePhobiaBean();
		bean.numPlus = 0;
		bean.numPlusPrime = 0;
		bean.numB = 0;
		bean.numBPrime = 0;
		
		for (int i=0; i<cipher.length(); i++) {
			char c = cipher.charAt(i);
			boolean prime = primes.contains(i+1);
			if (c == '+') {
				bean.numPlus++;
				if (prime) {
					bean.numPlusPrime++;
					bean.positions.add(i);
				}
			} else if (c == 'B') {
				bean.numB++;
				if (prime) {
					bean.numBPrime++;
					bean.positions.add(i);
				}
			}
		}
		
		bean.errors = Math.abs(24-bean.numPlus) + Math.abs(12-bean.numB);
		if (bean.numBPrime > 1) bean.errors += bean.numBPrime-1;
		if (bean.numPlusPrime > 1) bean.errors += bean.numPlusPrime-1;
		return bean;
	}
	
	public static void test() {
		//for (Integer prime : primes) System.out.println(prime);
		
		String cipher = Ciphers.cipher[0].cipher;
	
		/*
		int hits = 0;
		int counts = 0;
		while (true) {
			cipher = CipherTransformations.shuffle(cipher);
			counts++;
			PrimePhobiaBean bean = errors(cipher);
			if (bean.errors == 0) {
				hits++;
				System.out.println(hits + " " + counts + " " + ((float)hits)/counts);
			}
		}*/
		
		PrimePhobiaBean bean = errors("dAO4UMp8y6Vz)q5NG3  N&KD_C81^R62q@8OW /:S NSKp<c fDql619fk zk*-5l O |5FBc NLJf|U7M  H c32)Wk|@  ^d%NzOjY&zZ#87:Cz@  D GZ  YZR>ZK N +b6 *  Pb.cVclC&< cX9|^Y6FFBct  (2O @-*- AB-l.R.&9z<2  -y 4^M1b&  VK7OMtE5RJ|*D E6b K.zD<9l 2O|kM;/ PZB4SRt.G8@J8)GCK   yTG^J /<RXN* OdU z/l2c@(BHERLTZX<Mz%K/&kBlAUO +R c 2MU@1 Z |5F4 @  6 t@ EOXGzd  3>^5B*.D2");
		System.out.println(bean);
		
	}
	public static void main(String[] args) {
		//test();
		String cipher = Ciphers.cipher[1].cipher; 
		System.out.println(Arrays.toString(nonprimes(cipher, Ciphers.countMap(cipher))));
	}
}
