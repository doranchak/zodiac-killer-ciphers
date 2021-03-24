package com.zodiackillerciphers.corpus.symposium2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.types.Columnar;
import com.zodiackillerciphers.corpus.symposium2019.types.Homophonic;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.transform.CipherTransformations;

public class ColumnarFragmentStats {

	public String cipher = null;
	public int keyLength;
	
	public int count;
	public long elapsed;
	
	public int Lmin;
	public int Lmax;
	
	List<Integer> fragStatsMax; // track the best values

	public ColumnarFragmentStats(String cipher, int keyLength, int Lmin, int Lmax) {
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
			ColumnarFragmentStats cs = new ColumnarFragmentStats(cipher, L, Lmin, Lmax);
			cs.generate(); // this tracks max fragments for all tested keys
			if (best == null) best = cs.fragStatsMax;
			else FragmentStats.max(best, cs.fragStatsMax); // maximums of maximums
		}
		return best;
	}
	
	
//	static void testHomVsColumnarStatsOLD() {
//		ColumnarStats stats = new ColumnarStats(); 
//		Homophonic hom = new Homophonic();
//		String homCipher = hom.makeCipher(Ciphers.Z408_SOLUTION.substring(0,340));
//		Columnar col = new Columnar();
//		col.KEY_LENGTH_OVERRIDE = 7;
//		String colCipher = col.makeCipher(Ciphers.Z408_SOLUTION.substring(0,340));
//		System.out.println("hom: " + homCipher);
//		System.out.println("col: " + colCipher);
//		System.out.println("col key: " + Arrays.toString(col.key));
//		
//		stats.betterIocCounts = new int[2];
//		stats.diffsAverage = new double[2];
//		
//		// output reference iocs for unmodified ciphers
////		refHom = new double[3];
////		refCol = new double[3];
//		for (n=2; n<5; n++) {
//			double ioc = Stats.iocNgram(homCipher, n);
////			refHom[n-2] = ioc;
//			System.out.println("homCipher unmodified n=" + n + " ioc: " + ioc);
//			ioc = Stats.iocNgram(colCipher, n);
////			refCol[n-2] = ioc;
//			System.out.println("colCipher unmodified n=" + n + " ioc: " + ioc);
//			
//		}
//		
//		for (n=2; n<5;n++) {
////			which = 0;
////			cipher = colCipher;
//			recurse(col.key.length, col.key);
//			
////			which = 1;
////			cipher = homCipher;
//			recurse(col.key.length, col.key);
//		}
//		diffsAverage[0] /= count;
//		diffsAverage[1] /= count;
//		System.out.println("diffsAverage: " + Arrays.toString(diffsAverage));
//		System.out.println("betterIocCounts: " + Arrays.toString(betterIocCounts));
//	}

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
//		System.out.println("key " + Arrays.toString(key));
		// untranspose cipher with the given key and compute ngraphic ioc
		String untransposed = Columnar.untranspose(cipher, key);
		List<Integer> fragStats = FragmentStats.generateFragmentStatsFor(untransposed, Lmin, Lmax);
		if (fragStatsMax == null) fragStatsMax = fragStats;
		else FragmentStats.max(fragStatsMax, fragStats);
		count++;
//		if (diff > 0) {
//			String tab = "	";
//			System.out.println(ioc + tab + Arrays.toString(key));
//		}
	}
	
	private void swap(int[] input, int a, int b) {
		int tmp = input[a];
		input[a] = input[b];
		input[b] = tmp;
	}
	
	public static void test1() {
		Columnar col = new Columnar();
		int keyLength = 5;
		int Lmax = 7;
		col.KEY_LENGTH_OVERRIDE = keyLength;
		
		String colCipher = col.makeCipher(Ciphers.Z408_SOLUTION.substring(0, 340));
		System.out.println("col: " + colCipher);
		System.out.println("col key: " + Arrays.toString(col.key));
		System.out.println("Ref stats for unmodified cipher:");
		System.out.println(FragmentStats.generateFragmentStatsFor(colCipher, 2, Lmax));
		System.out.println("Best measurements for all untranspositions:");
		double elapsed = new Date().getTime();
		System.out.println(generateUntransposedFragmentStatsFor(colCipher, 2, keyLength, 2, Lmax));
		elapsed = (new Date().getTime() - elapsed)/1000;
		System.out.println("elapsed: " + elapsed + " sec");
		String untransposed = Columnar.untranspose(colCipher, col.key);
		System.out.println("Untransposition:");
		System.out.println(untransposed);
		System.out.println("Ref stats for untransposition:");
		System.out.println(FragmentStats.generateFragmentStatsFor(untransposed, 2, Lmax));
		String shuffled = CipherTransformations.shuffle(colCipher);
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
