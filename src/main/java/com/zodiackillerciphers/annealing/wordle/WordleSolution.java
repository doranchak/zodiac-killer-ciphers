package com.zodiackillerciphers.annealing.wordle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;

public class WordleSolution extends Solution {
	static int N = 3;
	// top 5000 (or so) words that could be potential hidden words
	static List<String> topWords;
	// full list of valid Wordle words that can be used for guesses
	static List<String> wordList;
	
	static {
		WordFrequencies.init();
		topWords = new ArrayList<String>();
		// collect the top 5000 words
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != 5)
				continue;
			topWords.add(word.toLowerCase());
			if (topWords.size() == 5000)
				break;
		}
		wordList = FileUtil.loadFrom(
				"/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/dictionaries/wordle-5-letter-words.txt");
	}
	
	static double weightGreen = 2;
	static double weightYellow = 1;
	static double weightBlack = 0.1;
	static boolean allowRepeatedLetters = true;
	
	// current selection of words to play
	public int[] selections;
	
	public Random random = new Random();
	
	public int indexReverse; // which index element was mutated
	public int valueReverse; // its original value
	
	public double energyCached;
	
	public int countYellow;
	public int countGreen;
	public int countBlack;
	
	@Override
	public void mutateReverse() {
		selections[indexReverse] = valueReverse;
		mutateReverseClear();
	}
	@Override
	public void mutateReverseClear() {
		indexReverse = -1;
		valueReverse = -1;
	}

	@Override
	public String representation() {
		double rateGreen = countGreen;
		rateGreen /= topWords.size();
		double rateYellow = countYellow;
		rateYellow /= topWords.size();
		double rateBlack = countBlack;
		rateBlack /= topWords.size();
		return energyCached + "	" + words() + "	" + rateGreen + "	" + rateYellow + "	" + rateBlack; 
	}
	
	public String words() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<selections.length; i++) {
			if (i>0) sb.append(" ");
			sb.append(wordList.get(selections[i]));
		}
		return sb.toString();
	}

	@Override
	public double energyCached() {
		return energyCached;
	}

	@Override
	public void initialize() {
		selections = new int[N];
		for (int i=0; i<N; i++) selections[i] = random.nextInt(wordList.size());
		mutateReverseClear();
	}
	public WordleSolution() {
	}
	
	@Override
	public boolean mutate() {
		
		int which = random.nextInt(N);
		indexReverse = which;
		valueReverse = selections[which];
		selections[which] = random.nextInt(wordList.size());
		
		return true;
	}
	
	public double energyOLD() {
		energyCached = 0;
		
		countGreen = 0;
		countYellow = 0;
		countBlack = 0;
		
		if (!allowRepeatedLetters) {
			Set<Character> seen = new HashSet<Character>(); 
			for (int i=0; i<selections.length; i++) {
				String word = wordList.get(selections[i]);
				for (int k=0; k<word.length(); k++) {
					char ch = word.charAt(k);
					if (seen.contains(ch)) return 0;
					seen.add(ch);
				}
			}
		}
		
		// play the game, using the current selection of valid words, against all top words.
		// add up the total number of green and yellow hits across all games.
		
		for (String hiddenWord : topWords) {
			// track which green positions we've already seen
			Set<Integer> seenGreen = new HashSet<Integer>(); 
			// track which yellow letter+position combos we've already seen
			Set<String> seenYellow = new HashSet<String>(); 
			// track which black letters we've already seen
			Set<Character> seenBlack = new HashSet<Character>(); 
			for (int i=0; i<selections.length; i++) {
				String playedWord = wordList.get(selections[i]);
				for (int k=0; k<playedWord.length(); k++) {
					if (seenGreen.contains(k)) continue; // we already had a green hit here
					char c1 = playedWord.charAt(k); // the played word's letter here
					char c2 = hiddenWord.charAt(k); // the hidden word's letter here
					if (c1 == c2) { // this is a green hit, never seen before at this position
						seenGreen.add(k);
						energyCached -= weightGreen;
						countGreen++;
					} else if (hiddenWord.indexOf(c1) > -1) {
						String key = k + " " + c1;
						if (seenYellow.contains(key)) continue;
						// the played letter exists in the hidden word but at a different position
						seenYellow.add(key);
						energyCached -= weightYellow;
						countYellow++;
					} else {
						if (seenBlack.contains(c1)) continue;
						seenBlack.add(c1);
						energyCached -= weightBlack;
						countBlack++;
					}
				}
			}
		}
		
		return energyCached;
	}

	@Override
	public double energy() {

		// how many valid words are left after playing the current selections? 
		
		energyCached = 0;
		
		// play the game, using the current selection of valid words, against all top words.
		// add up the total number of green and yellow hits across all games.
		
		Set<String> eliminated = new HashSet<String>();
		for (String hiddenWord : topWords) {
			// track which green positions we've already seen
			Set<Integer> seenGreen = new HashSet<Integer>(); 
			// track which yellow letter+position combos we've already seen
			Set<String> seenYellow = new HashSet<String>(); 
			// track which black letters we've already seen
			Set<Character> seenBlack = new HashSet<Character>(); 
			for (int i=0; i<selections.length; i++) {
				String playedWord = wordList.get(selections[i]);
				for (int k=0; k<playedWord.length(); k++) {
					if (seenGreen.contains(k)) continue; // we already had a green hit here
					char c1 = playedWord.charAt(k); // the played word's letter here
					char c2 = hiddenWord.charAt(k); // the hidden word's letter here
					if (c1 == c2) { // this is a green hit, never seen before at this position
						seenGreen.add(k);
						countGreen++;
					} else if (hiddenWord.indexOf(c1) > -1) {
						String key = k + " " + c1;
						if (seenYellow.contains(key)) continue;
						// the played letter exists in the hidden word but at a different position
						seenYellow.add(key);
						countYellow++;
					} else {
						if (seenBlack.contains(c1)) continue;
						seenBlack.add(c1);
						energyCached++;
						countBlack++;
						eliminated.add(hiddenWord);
					}
				}
			}
		}
		// so far we have a set of words that aren't compatible with guesses.
		// convert to number of possible words remaining.
		energyCached = -eliminated.size();
		return energyCached;
	}
	
	@Override
	public Solution clone() {
		WordleSolution newSol = new WordleSolution();
		newSol.selections = Arrays.copyOf(selections, selections.length);
		return newSol;
	}
	
	public static void main(String[] args) {
	}

}
