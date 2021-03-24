package com.zodiackillerciphers.annealing.namestuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.annealing.homophonic.HomophonicSolution;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.LetterFrequencies;

/** try to maximize the number of times a particular person's name appears in the cipher text. */
public class NamestufferSolution extends HomophonicSolution {

	/** words/names to stuff */
	public String[] words;
	
	/** we want about the same number of each word, with only this many differences in the count of one word vs
	 * another.
	 */
	public static int MAX_DIFFERENCE = 5;
	
	public NamestufferSolution(String ciphertext, String plaintext, String[] words) {
		this.ciphertext = ciphertext;
		this.plaintext = plaintext;
		iterations = 0;
		this.words = words; 
	}
	
	@Override
	public boolean mutate() {
		// try to stuff a word
		int which = random.nextInt(words.length);
		String word = words[which];
		int n = word.length();
		int pos = random.nextInt(ciphertext.length() - n + 1);
		for (int j = 0; j < n; j++) {
			char c = ciphertext.charAt(pos + j);
			put(c, word.charAt(j));
		}
		return true;
	}

	@Override
	public double energy() {
		decode();
		Map<String, Integer> counts = new HashMap<String, Integer>(); 
		for (String word : words) {
			counts.put(word, 0);
			int count = 0;
			int index = 0;
			while (index < plaintext.length()) {
				index = plaintext.indexOf(word, index);
				if (index > -1) {
					Integer val = counts.get(word);
					val++;
					counts.put(word,  val);
				} else break;
				index++;
			}
		}
		score = 0;
		
		for (String key1 : counts.keySet()) {
			int val1 = counts.get(key1);
			for (String key2 : counts.keySet()) {
				if (key1.equals(key2)) continue;
				int val2 = counts.get(key2);
				if (Math.abs(val1-val2) > MAX_DIFFERENCE) {
					score = Float.MAX_VALUE;
					return score;
				}
			}
			score += val1;
		}
		return -score;
	}
	
	@Override
	public Solution clone() {
		NamestufferSolution newSol = new NamestufferSolution(this.ciphertext, this.plaintext, this.words);
		newSol.score = this.score;
		newSol.cipherAlphabet = this.cipherAlphabet;
		newSol.plainAlphabet = this.plainAlphabet;
		newSol.key = new HashMap<Character, Character>();
		for (Character k : this.key.keySet())
			newSol.key.put(k, this.key.get(k));
		newSol.iterations = this.iterations;
		newSol.temperature = this.temperature;
		return newSol;
	}

}
