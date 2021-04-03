package com.zodiackillerciphers.pivots;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.corpus.CorpusSample;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/**
 * Do pivots appear more often in Z340-style ciphers than in shuffles and
 * "normal" homophonic ciphers?
 */
public class PivotStudy extends CorpusBase {
	/** compare the given plaintext/ciphertext to shuffles */
	public static void shuffle(String text, int pivotLength, int shuffles) {
		Map<String, StatsWrapper> statsMap = new HashMap<String, StatsWrapper>();
		for (String key : PivotStats.allDirectionCombinations())
			statsMap.put(key, new StatsWrapper());
		PivotStats ps = new PivotStats(text, pivotLength);
		for (String key : PivotStats.allDirectionCombinations()) {
			StatsWrapper stats = statsMap.get(key);
			stats.actual = ps.counts.get(key);
		}

		for (int i = 0; i < shuffles; i++) {
			text = CipherTransformations.shuffle(text);
			ps = new PivotStats(text, pivotLength);
			for (String key : PivotStats.allDirectionCombinations()) {
				StatsWrapper stats = statsMap.get(key);
				stats.addValue(ps.counts.get(key));
			}
		}
		for (String key : PivotStats.allDirectionCombinations()) {
			System.out.println(key + " ================================ ");
			statsMap.get(key).output();
		}
	}

	/** generate stats for randomly selected candidate plaintexts */
	public static void plaintexts(int pivotLength, int targetSamples, boolean shuffle) {
		
		/** calculate ioc of Z340's known plaintext */
		double iocZ340 = Stats.ioc(Ciphers.Z340_SOLUTION_UNTRANSPOSED);
		/** calculate a 1% range above and below that value. */
		double iocMin = 0.99 * iocZ340;
		double iocMax = 1.01 * iocZ340;
		int samples = 0;
		
		Map<String, StatsWrapper> statsMap = new HashMap<String, StatsWrapper>();
		for (String key : PivotStats.allDirectionCombinations())
			statsMap.put(key, new StatsWrapper());
		while (true) {
			
			List<CorpusSample> list = produceRandomSamplesWithLength(340, false);
			for (CorpusSample cs : list) {
				// reject the sample if the ioc isn't within 1% of the Z340 plaintext's ioc
				double ioc = Stats.ioc(cs.getNoSpaces());
				if (ioc < iocMin)
					continue;
				if (ioc > iocMax)
					continue;
				samples++;
				
				// perform Z340-like transposition
				String text = Periods.matrixUndo(cs.getNoSpaces(), Periods.transpositionMatrixZ340Solution());
				if (shuffle) text = CipherTransformations.shuffle(text);
				
				System.out.println("TEXT: " + cs.getNoSpaces());
				System.out.println("TRANSPOSED: " + text);
				boolean found = false;
				PivotStats ps = new PivotStats(text, pivotLength);
				for (String key : PivotStats.allDirectionCombinations()) {
					StatsWrapper stats = statsMap.get(key);
					stats.addValue(ps.counts.get(key));
					if (ps.counts.get(key) > 0)
						found = true;
				}
				for (String key : PivotStats.allDirectionCombinations()) {
					System.out.println(key + " ================================ ");
					statsMap.get(key).output();
				}
				if (found) {
					System.out.println("Found: ");
					for (Pivot pivot : ps.pivots) {
						if (pivot.ngram.length() == pivotLength)
							System.out.println(" --- " + pivot);
					}
				}
				if (samples == targetSamples) return; // stop when we have enough samples
			}
		}
	}

	/** generate candidate plaintexts from a corpus and check for pivots */
//	public static void plaintextPivots() {
//		TODO
//	}

	public static void testShuffle() {
		String[] texts = new String[] { Ciphers.Z408, Ciphers.Z340, Ciphers.Z340_SOLUTION_TRANSPOSED, Ciphers.Z340_SOLUTION_UNTRANSPOSED };
		for (String text : texts) {
			System.out.println(" = TEXT: " + text);
			for (int L = 2; L < 6; L++) {
				System.out.println(">>>>> L=" + L + " for " + text);
				shuffle(text, L, 10000);
			}
		}
	}
	
	public static void plaintextStudy(boolean shuffle) {
		CorpusBase.initSources();
		plaintexts(4, 10000, shuffle);		
	}
	
	/** what are the chances of preserving the pivots when assigning cipher text symbols to the 
	 * letters in the pivots?
	 * 
	 * predicted: 1 in 17280
	 */
	public static void encodingStudy(int samples) {
		// pivot 1: O, S, E
		// pivot 2: E, N, E
		// number of homophones for each pt letter:
		// - O: 4
		// - S: 4
		// - E: 6
		// - N: 5
		Random r = new Random();
		int hits = 0;
		for (int i = 0; i < samples; i++) {
			if (r.nextInt(4) == r.nextInt(4) && r.nextInt(4) == r.nextInt(4) && r.nextInt(6) == r.nextInt(6)
					&& r.nextInt(6) == r.nextInt(6) && r.nextInt(5) == r.nextInt(5) && r.nextInt(6) == r.nextInt(6)) {
				hits++;
				double prob = hits;
				prob /= i;
				System.out.println(prob + " " + (prob * 17280) + " " + hits + "/" + (i + 1));
			}
		}
	}
	
	public static void main(String[] args) {
//		plaintextStudy(false);
		encodingStudy(1000000000);
//		testShuffle();

//		System.out.println(
//				Periods.matrixUndo(Ciphers.Z340_SOLUTION_UNTRANSPOSED, Periods.transpositionMatrixZ340Solution()));

	}
}
