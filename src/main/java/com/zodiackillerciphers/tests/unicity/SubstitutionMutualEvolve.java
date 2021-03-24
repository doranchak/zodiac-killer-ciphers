package com.zodiackillerciphers.tests.unicity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.io.Unzip;
import com.zodiackillerciphers.lucene.Stats;

public class SubstitutionMutualEvolve extends CorpusBase {
	public static Random rand = new Random();
	public static int populationSize = 10000;
	// max number of trials for current source before trying a different one
	public static int maxTrials = 1000;
	public static List<PlaintextBean> population;
	static {
		population = new ArrayList<PlaintextBean>();
		for (int i=0; i<populationSize; i++) {
			population.add(new PlaintextBean());
		}
	}
	
	/** returns null if unsuccessful */
	static String placeNgrams() {
		String str = null;
//		int L = rand.nextInt(13)+3; // [3,15]
		int L = rand.nextInt(11)+5; // [5,15]
		for (int i=0; i<population.size(); i++) {
			PlaintextBean pb = population.get(i);
			String[] ng1 = randomNgram(L);
			String[] ng2 = randomNgram(L);
			boolean result = pb.add(ng1, ng2, pb.randomPosition1(), pb.randomPosition2());
			if (result) {
				str = pb.toString();
			}
		}
		return str;
	}
	
	
	
	/** try to "evolve" mutual plaintexts.  maintain population of mutual plaintexts.  keep trying to insert
	 * words and phrases to maximize the plaintext lengths.
	 */
	public static void evolve() {
		// prepare the corpus
		initSources();
		long trials = 0;
		int maxTrials = 10000;
		while (true) {
			// pick a random source from the corpus
			randomSource();
			initNgramMap();
			// loop until we want to proceed to another random source
			boolean go = true;
			int trialsCurrent = 0;
			while (go) {
				// pick a pair of random ngrams for a randomly selected length
				// in each member of the population, try to place it in a random position.
				// report any successful attempts.
				String pb = placeNgrams();
				trials++;
				if (trials % 1000 == 0) {
					System.out.println("Trials: " + trials);
					for (int i=0; i<population.size(); i++) {
						PlaintextBean bean = population.get(i);						
						System.out.println("population member " + i + " at trial " + trials + ": " + bean);
					}
				}
				if (pb != null) {
					System.out.println(pb + "	" + trials);
					trialsCurrent = 0;
				} else {
					trialsCurrent++;
				}
				// if we haven't had any successes in a while, then try a different random source
				if (trialsCurrent > maxTrials) {
					System.out.println("PICKING NEW RANDOM SOURCE");
					break;
				}
			}
		}
	}

	public static void process2(String path) {
		List<String> list = FileUtil.loadFrom(path);
		for (String line : list) {
			if (line.contains("[")) {
				String[] split = line.split("	");
				String s1 = split[1].replaceAll("[\\[\\] ,]", "");
				String s2 = split[2].replaceAll("[\\[\\] ,]", "");
				System.out.println(Stats.iocNgram(s1, 2) + "	" + Stats.iocNgram(s2, 2) + "	" + split[0] + "	"
						+ split[1] + "	" + split[2]);
			}
		}
	}
	
	public static void main(String[] args) {
//		initSources();
//		evolve();
//		process("/Users/doranchak/projects/zodiac/sub-evolve-3.txt");
		testRandomSources();
	}
	
}
