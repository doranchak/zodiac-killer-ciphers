package com.zodiackillerciphers.corpus.symposium2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.types.Columnar;
import com.zodiackillerciphers.corpus.symposium2019.types.Homophonic;
import com.zodiackillerciphers.lucene.Stats;

public class ColumnarStats {

	public String cipher = null;
	public int n;
	public int keyLength;
	
	/** reference ioc of unmodified cipher */
	public double refIoc;
	
	/** number of times ioc meets or beats ref ioc */
	public int betterIocCounts;
	
	/** average of differences between ioc and ref ioc */
	public double diffsAverage;
	
	/** max untransposed ioc for this key length */
	public double maxUntransposedIoc;
	
	public int count;
	public long elapsed;

	public ColumnarStats(String cipher, int n, int keyLength) {
		this.cipher = cipher;
		this.n = n;
		this.keyLength = keyLength;
	}

	public static void testHomVsColumnarStats() {
		Homophonic hom = new Homophonic();
		String homCipher = hom.makeCipher(Ciphers.Z408_SOLUTION.substring(0,340));
		Columnar col = new Columnar();
		col.KEY_LENGTH_OVERRIDE = 7;
		String colCipher = col.makeCipher(Ciphers.Z408_SOLUTION.substring(0,340));
		System.out.println("hom: " + homCipher);
		System.out.println("col: " + colCipher);
		System.out.println("col key: " + Arrays.toString(col.key));
		for (int n=2; n<5; n++) {
			ColumnarStats stats = new ColumnarStats(homCipher, n, col.KEY_LENGTH_OVERRIDE);
			stats.generate();
			stats.dump();
		}
		for (int n=2; n<5; n++) {
			ColumnarStats stats = new ColumnarStats(colCipher, n, col.KEY_LENGTH_OVERRIDE);
			stats.generate();
			stats.dump();
		}
	}
	
	public static double[] generateStatsFor(String cipher, int minKeyLength, int maxKeyLength) {
		List<Double> stats = new ArrayList<Double>();
		// reference (untransposed) stats
		for (int n=2; n<5; n++) {
			stats.add(Stats.iocNgram(cipher, n));
		}
		for (int L=minKeyLength; L<=maxKeyLength; L++) {
			for (int n=2; n<5; n++) {
				ColumnarStats cs = new ColumnarStats(cipher, n, L);
				cs.generate();
				stats.add(cs.refIoc);
				stats.add((double) cs.betterIocCounts);
				stats.add(cs.diffsAverage);
				stats.add(cs.maxUntransposedIoc);
				stats.add(cs.maxDiff());
			}
		}
		double[] result = new double[stats.size()];
		for (int i=0; i<stats.size(); i++) result[i] = stats.get(i);
		return result;
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
		elapsed = new Date().getTime();
		refIoc = Stats.iocNgram(cipher, n);
		betterIocCounts = 0;
		diffsAverage = 0;
		maxUntransposedIoc = 0;
		count = 0;
		int[] key = new int[keyLength];
		for (int i=0; i<keyLength; i++) key[i] = i;
		recurse(keyLength, key);
		elapsed = new Date().getTime() - elapsed;
	}
	
	public void dump() {
		System.out.println("cipher: " + cipher);
		System.out.println("n: " + n);
		System.out.println("keyLength: " + keyLength);
		System.out.println("refIoc: " + refIoc);
		System.out.println("betterIocCounts: " + betterIocCounts);
		System.out.println("diffsAverage: " + diffsAverage);
		System.out.println("maxUntransposedIoc: " + maxUntransposedIoc);
		System.out.println("maxDiff: " + (maxUntransposedIoc - refIoc));
		System.out.println("count: " + count);
		System.out.println("elapsed: " + elapsed + "ms (" + (elapsed/1000) + "s)");
	}
	
	public double maxDiff() {
		return maxUntransposedIoc - refIoc;
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
		// untranspose cipher with the given key and compute ngraphic ioc
		String untransposed = Columnar.untranspose(cipher, key);
		double ioc = Stats.iocNgram(untransposed, n);
		double diff = ioc - refIoc;
		if (diff > 0)
			betterIocCounts++;
		diffsAverage += diff;
		maxUntransposedIoc = Math.max(maxUntransposedIoc, ioc);
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
	
	static void testStats() {
		System.out.println(Arrays.toString(generateStatsFor(Ciphers.Z340, 2, 7)));
	}
	
	public static void main(String[] args) {
		//testHomVsColumnarStats();
		testStats();
	}
}
