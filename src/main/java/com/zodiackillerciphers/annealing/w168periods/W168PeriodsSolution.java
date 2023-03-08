package com.zodiackillerciphers.annealing.w168periods;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.ciphers.w168.StringUtils;
import com.zodiackillerciphers.ciphers.w168.ngrams.ShuffleNgramsTest;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/** look for period combinations that, when applied throughout the cipher, produce high ngram scores. */
public class W168PeriodsSolution extends Solution {

	static int PERIOD_MIN = -84;
	static int PERIOD_MAX = 84;
	static int SHUFFLES = 2500;
	int[] periods;
	int[] periodsCopy; // to reverse mutations
	StringBuilder cipher; 
	public Random random = new Random();
	
	public double energyCached;

	int mutatePositionAdded;
	int mutatePositionRemoved;
	
	@Override
	public void mutateReverse() {
		periods = periodsCopy;
	}
	@Override
	public void mutateReverseClear() {
		periodsCopy = null;
	}

	@Override
	public String representation() {
		if (energyCached == 0) energy();
		return energyCached + "	" + Arrays.toString(periods);
	}

	@Override
	public double energyCached() {
		return energyCached;
	}

	@Override
	public void initialize() {
		// random periods
		for (int i=0; i<periods.length; i++) {
			periods[i] = randomPeriod();
		}
//		periods = new int[] { 29, -1, -27};
	}
	
	public int randomPeriod() {
		return PERIOD_MIN + random.nextInt(PERIOD_MAX - PERIOD_MIN + 1);		
	}
	
	public W168PeriodsSolution(int numPeriods, StringBuilder[] cipher) {
		this.periods = new int[numPeriods];
		this.cipher = StringUtils.toLine(cipher);
	}
	
	@Override
	public boolean mutate() {
		energyCached = 0;
		periodsCopy = Arrays.copyOf(periods, periods.length);
		if (random.nextFloat() < 0.05) return mutateSwap(); // every once in a while
		return mutatePeriod(); // most of the time
	}

	public boolean mutateSwap() {
		int a = random.nextInt(periods.length);
		int b = a;
		while (a == b) 
			b = random.nextInt(periods.length);
		swap(a, b);
		return true;
	}
	public void swap(int a, int b) {
		int tmp = periods[a];
		periods[a] = periods[b];
		periods[b] = tmp;
	}
	public boolean mutatePeriod() {
		periods[random.nextInt(periods.length)] = randomPeriod();
		return true;
	}
	
	@Override
	public double energy() { // lower is better
		
		// TODO - redo this to use the cached version
		
		int n = periods.length + 1;
		StatsWrapper stats = new StatsWrapper();
		StringBuilder cipherStr = new StringBuilder(cipher);
		double score = ShuffleNgramsTest.score(ShuffleNgramsTest.periodCombinations(cipherStr, periods, false), n, false, false);
		stats.actual = score;
		for (int i=0; i<SHUFFLES; i++) {
			cipherStr = new StringBuilder(CipherTransformations.shuffle(cipherStr.toString()));
			score = ShuffleNgramsTest.score(ShuffleNgramsTest.periodCombinations(cipherStr, periods, false), n, false, false);
			stats.addValue(score);
		}
		stats.name = n + "	" + Arrays.toString(periods);
		//stats.output();
		
		energyCached = -stats.sigma();
		if (Double.isNaN(energyCached)) energyCached = Double.MAX_VALUE;
		return energyCached;
	}
	
	@Override
	public Solution clone() {
		W168PeriodsSolution newSol = new W168PeriodsSolution(periods.length, null);
		newSol.periods = Arrays.copyOf(periods, periods.length);
		newSol.cipher = cipher;
		return newSol;
	}
	
	public static void main(String[] args) {
	}

}
