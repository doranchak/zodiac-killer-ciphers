package com.zodiackillerciphers.tests.unigrams;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

public class SymbolBalanceTest {
	// use shuffles to find symbols that have unusual linear imbalances in the
	// cipher text
	public static void shuffleTest(String cipher, int shuffles) {
		Map<Character, Double> actual = new HashMap<Character, Double>();
		Set<Character> alphabet = Ciphers.alphabetAsSet(cipher);
		Map<Character, StatsWrapper> map = new HashMap<Character, StatsWrapper>();
		for (Character ch : alphabet) {
			actual.put(ch, averagePosition(cipher, ch));
			StatsWrapper stats = new StatsWrapper();
			stats.name = "average position for " + ch;
			stats.actual = actual.get(ch);
			map.put(ch, stats);
		}
		
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			for (Character ch : alphabet) {
				StatsWrapper stats = map.get(ch);
				double val = averagePosition(cipher, ch);
				if (val >= actual.get(ch)) stats.hits++;
				stats.addValue(val);
			}
		}
		
		for (StatsWrapper stats : map.values()) {
			stats.output();
		}
	}

	// calculate average linear position of all occurrences of the given symbol
	// in the given cipher
	public static double averagePosition(String cipher, char symbol) {
		double result = 0;
		int total = 0;
		for (int i = 0; i < cipher.length(); i++) {
			if (cipher.charAt(i) == symbol) {
				result += i;
				total++;
			}
		}
		result /= total;
		return result;
	}

	// TODO: 2d (row/col) version

	public static void main(String[] args) {
		shuffleTest(Ciphers.Z340, 100000);
	}
}
