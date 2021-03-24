package com.zodiackillerciphers.corpus.symposium2019;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.operations.Permutation;

public class PermutationFragmentStats {

	public String cipher = null;
	public int keyLength;
	
	public int count;
	public long elapsed;
	
	public int Lmin;
	public int Lmax;
	
	List<Integer> fragStatsMax; // track the best values

	public PermutationFragmentStats(String cipher, int keyLength, int Lmin, int Lmax) {
		this.cipher = cipher;
		this.keyLength = keyLength;
		this.Lmin = Lmin;
		this.Lmax = Lmax;
	}

	/** generate fragment repeat stats for untranspositions for all keys of the given keylengths.
	 * does this for fragment sizes in the range [Lmin, Lmax]
	 * returns the max repeats found for each measurement.  
	 */
	public static List<Integer> generateUntransposedFragmentStatsFor(String cipher, int minKeyLength, int maxKeyLength, int Lmin, int Lmax) {
		List<Integer> best = null;
		for (int L=minKeyLength; L<=maxKeyLength; L++) {
			PermutationFragmentStats cs = new PermutationFragmentStats(cipher, L, Lmin, Lmax);
			cs.generate(); // this tracks max fragments for all tested keys
			if (best == null) best = cs.fragStatsMax;
			else FragmentStats.max(best, cs.fragStatsMax); // maximums of maximums
		}
		return best;
	}
	
	
	/** generate stats for the given cipher */
	public void generate() {
//		System.out.println(" - generate");
		elapsed = new Date().getTime();
		count = 0;
		int[] key = new int[keyLength];
		for (int i=0; i<keyLength; i++) key[i] = i;
		recurse(keyLength, key);
		elapsed = new Date().getTime() - elapsed;
	}
	
	public void recurse(int index, int[] key) {
		if (index == 1) {
			recurseAction(key);
		} else {
			for (int i = 0; i < index - 1; i++) {
				recurse(index - 1, key);
				if (index % 2 == 0) {
					swap(key, i, index - 1);
				} else {
					swap(key, 0, index - 1);
				}
			}
			recurse(index - 1, key);
		}
	}
	public void recurseAction(int[] key) {
		// untranspose cipher with the given key and compute fragment stats
		String untransposed = Permutation.transform(cipher, key, true); 
		List<Integer> fragStats = FragmentStats.generateFragmentStatsFor(untransposed, Lmin, Lmax);
		if (fragStatsMax == null) fragStatsMax = fragStats;
		else FragmentStats.max(fragStatsMax, fragStats);
		count++;
	}
	
	private void swap(int[] input, int a, int b) {
		int tmp = input[a];
		input[a] = input[b];
		input[b] = tmp;
	}
	
	public static void test1() {
		com.zodiackillerciphers.corpus.symposium2019.types.Permutation perm = new com.zodiackillerciphers.corpus.symposium2019.types.Permutation();

		int keyLength = 5;
		int Lmax = 7;
		
		String permCipher = perm.makeCipher(Ciphers.Z408_SOLUTION.substring(0, 340));
		System.out.println("perm: " + permCipher);
		System.out.println("perm key: " + Arrays.toString(perm.key));
		System.out.println("Ref stats for unmodified cipher:");
		System.out.println(FragmentStats.generateFragmentStatsFor(permCipher, 2, Lmax));
		System.out.println("Best measurements for all untranspositions:");
		double elapsed = new Date().getTime();
		System.out.println(generateUntransposedFragmentStatsFor(permCipher, 2, keyLength, 2, Lmax));
		elapsed = (new Date().getTime() - elapsed)/1000;
		System.out.println("elapsed: " + elapsed + " sec");
		String untransposed = Permutation.transform(permCipher, perm.key, true);
		System.out.println("Untransposition:");
		System.out.println(untransposed);
		System.out.println("Ref stats for untransposition:");
		System.out.println(FragmentStats.generateFragmentStatsFor(untransposed, 2, Lmax));
		String shuffled = CipherTransformations.shuffle(permCipher);
		System.out.println("Ref stats for shuffled:");
		System.out.println(FragmentStats.generateFragmentStatsFor(shuffled, 2, Lmax));
		System.out.println("Best measurements for all untranspositions of shuffled:");
		System.out.println(
				generateUntransposedFragmentStatsFor(shuffled, 2, keyLength, 2, Lmax));
		
		
	}
	
	public static void main(String[] args) {
		test1();
	}
}
