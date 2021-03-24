package com.zodiackillerciphers.math;

import java.math.BigInteger;

/**
 * trying to compute how many ways there are to place a number of repeating
 * ngrams in a length of cipher text
 */
public class NestedSums {

	/**
	 * how many ways are there to select 3 bigrams from the 340 cipher?
	 * */
	public static void testBigrams() {
		long count = 0;
		for (int i = 0; i <= 334; i++) {
			for (int j = i + 2; j <= 336; j++) {
				for (int k = j + 2; k <= 338; k++) {
					count++;
				}
			}
		}
		System.out.println(count);
	}

	/**
	 * how many ways are there to select m ngrams from a cipher text of length
	 * L?
	 * 
	 * generalization discovered from pattern via FullSimplify in Mathematica
	 */
	public static BigInteger ways(int n, int m, int L) {
		if (m == 0) return new BigInteger("0");
		BigInteger denom = new BigInteger("1");
		for (long i=1; i<=m; i++) {
			BigInteger bi = new BigInteger(""+i);
			denom = denom.multiply(bi);
		}
		
		BigInteger num = new BigInteger("1");
		for (long i=1; i<=m; i++) {
			BigInteger bL = new BigInteger(""+L);
			BigInteger bm = new BigInteger(""+m);
			BigInteger bn = new BigInteger(""+n);
			BigInteger bi = new BigInteger(""+i);
			
			BigInteger tmp = new BigInteger("0");
			tmp = tmp.add(bL);
			tmp = tmp.subtract(bm.multiply(bn));
			tmp = tmp.add(bi);			
			num = num.multiply(tmp);
		}
		
		return num.divide(denom);
	}

	/**
	 * how many ways are there to select m ngrams from a cipher text of length
	 * L?
	 */
	public static long waysManual(long n, long m, long L) {
		if (m == 0)
			return 0;
		if (m == 1) {
			return L - n + 1;
		}
		if (m == 2) { // Sum[Sum[1,{j,(i+n),L-n}],{i,0,L-2n}]
			return (L - 2 * n + 1) * (L - 2 * n + 2) / 2;
		}
		if (m == 3) { // Sum[Sum[Sum[1,{k,(j+n),L-n}],{j,(i+n),L-2n}],{i,0,L-3n}]
			return (1 + L - 3 * n) * (2 + L - 3 * n) * (3 + L - 3 * n) / 6;
		}
		if (m == 4) { // Sum[Sum[Sum[Sum[1,{l,(k+n),L-n}],{k,(j+n),L-2n}],{j,(i+n),L-3n}],{i,0,L-4n}]
			return (1 + L - 4 * n) * (2 + L - 4 * n) * (3 + L - 4 * n)
					* (4 + L - 4 * n) / 24;
		}
		if (m == 5) { // Sum[Sum[Sum[Sum[Sum[1,{m,(l+n),L-n}],{l,(k+n),L-2n}],{k,(j+n),L-3n}],{j,(i+n),L-4n}],{i,0,L-5n}]
			return (1 + L - 5 * n) * (2 + L - 5 * n) * (3 + L - 5 * n)
					* (4 + L - 5 * n) * (5 + L - 5 * n) / 120;
		}
		if (m == 6) { // Sum[Sum[Sum[Sum[Sum[Sum[1, {o, (m + n), L - n}], {m,
						// (l+ n), L - 2n}], {l, (k + n), L - 3 n}], {k, (j +
						// n), L- 4 n}], {j, (i + n), L - 5 n}], {i, 0, L - 6
						// n}]
			return (1 + L - 6 * n) * (2 + L - 6 * n) * (3 + L - 6 * n)
					* (4 + L - 6 * n) * (5 + L - 6 * n) * (6 + L - 6 * n) / 720;
		}
		throw new IllegalArgumentException("m=" + m + " is too damned high.");
	}

	public static void testWays() {
		int[] L = new int[] { 340, 408 };

		for (int l = 0; l < L.length; l++) {
			for (int n = 1; n < 10; n++) {
				for (int m = 1; m < 20; m++) {
					BigInteger w = ways(n, m, L[l]);
					System.out.println("ways(L=" + L[l] + ", n=" + n + ", m="
							+ m + ") = " + w + ", " + w.doubleValue());
				}
			}

		}
	}

	public static void main(String[] args) {
		// testBigrams();
		//testWays();
		System.out.println(ways(18, 2, 340));
	}
}
