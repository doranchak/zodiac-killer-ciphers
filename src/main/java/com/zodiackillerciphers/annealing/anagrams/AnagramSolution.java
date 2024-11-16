package com.zodiackillerciphers.annealing.anagrams;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.ngrams.AZDecrypt;
import com.zodiackillerciphers.old.decrypto.Map;

/*
 * 
 *  Strategy:
 *  - operate on a List of words, initially empty
 *  - init: generate pool (Set) of words that can be found in the input string
 *  - mutate 1: insert a randomly selected word somewhere in the list.  then remove words from pool that no longer appear in the leftovers of the input string.
 *  - if reverse mutation 1, need to put any removed words back in the pool.
 *  - mutate 2: swap words
 *  - mutate 3: remove word.  recompute word pool and add any missing words, since the leftovers got larger.
 *  - if reverse mutation 3, need to remove any added words from the pool.
 *  - score: azdecrypt score (with spaces) divided by total length (without spaces) (goal is to form exact anagrams from all letters of the input string)
 * 
 *  - todo: include word frequency information and do roulette selection to encourage more common words
 * 
*/

public class AnagramSolution extends Solution {

	// the input string we want to rearrange
	String inputString;
	// sequence of words anagrammed from the input string
	List<String> words;

	// word pool of valid anagrams
	// List<String> wordPool;

	// words that were added to / removed from wordpool during mutation
	// List<String> wordPoolMutated;

	int mutatePosition; // at which position did we add/remove a word
	String mutateWord; // the word we added/removed
	String mutateInputString; // the original input string before adding/removing word.

	int mutateSwapPos1; 
	int mutateSwapPos2; 

	// history of words that were removed from the word pool, which we look back at when words are deleted from the candidate solution.
	// Set<String> wordPoolRemovalHistory;

	// which mutation did we just do
	// 0 = insert
	// 1 = delete
	// 2 = swap
	int mutateWhich;

	public Random random = new Random();
	public double energyCached;
	
	@Override
	public void mutateReverse() {
		// int mutatePosition; // at which position did we add/remove a word
		// String mutateWord; // the word we added/removed
		// System.out.println("reversing mutation " + mutateWhich);
		if (mutateWhich == 0) {
			// wordPool.addAll(wordPoolMutated);
			words.remove(mutatePosition);
			inputString = mutateInputString;
		} else if (mutateWhich == 1) {
			// wordPool.removeAll(wordPoolMutated); // TODO: does this actually work?
			words.add(mutatePosition, mutateWord);
			inputString = mutateInputString;
		} else if (mutateWhich == 2) {
			swapWords(mutateSwapPos1, mutateSwapPos2);
		}
	}
	@Override
	public void mutateReverseClear() {
		// System.out.println("clearing mutation info " + mutateWhich);
		mutateWhich = -1;
		mutatePosition = -1;
		mutateWord = null;
		// wordPoolMutated.clear();
		mutateSwapPos1 = -1; 
		mutateSwapPos2 = -1;	
		mutateInputString = null;
	}

	@Override
	public String representation() {
		return energyCached + " " + wordsToStr(true) + " [" + this.inputString + "]";
	}

	public String wordsToStr(boolean spaces) {
		StringBuffer sb = new StringBuffer();
		for (String word : words) {
			if (spaces && sb.length() > 0) sb.append(" ");
			sb.append(word);
		}
		return sb.toString();
	}
		
	@Override
	public double energyCached() {
		return energyCached;
	}

	@Override
	public void initialize() {
		WordFrequencies.init();
		words = new ArrayList<String>();
		// wordPool = new ArrayList<String>();
		// wordPoolMutated = new ArrayList<String>();
		// wordPoolRemovalHistory = new HashSet<String>();
		// WordFrequencies.map.forEach((word, freq) -> {
		// 	if (Anagrams.anagram(word, inputString)) wordPool.add(word);
		// });
		// System.out.println("Word pool size: " + wordPool.size());
	}
	public AnagramSolution(String inputString) {
		this.inputString = inputString;
	}
	
	// weighted selection of a random word, limited to words that can be found as anagrams in the given string
	public String randomWord(String inputString) {
		if (inputString == null || inputString.length() == 0)
			throw new RuntimeException("ERROR: Input string is missing or empty.");

		int sum = 0;
		int count = 0;
		for (Entry<String, Integer> entry : WordFrequencies.map.entrySet()) {
			if (Anagrams.anagram(entry.getKey(), inputString, false)) {
				sum += entry.getValue();
				count++;
			}
        }
		// System.out.println("Pool size: " + count);
		int randomWeight = random.nextInt(sum) + 1;
		int currentSum = 0;
		for (Entry<String, Integer> entry : WordFrequencies.map.entrySet()) {
			if (Anagrams.anagram(entry.getKey(), inputString, false)) {
				currentSum += entry.getValue();
				if (randomWeight <= currentSum) {
					return entry.getKey();
				}				
			}
        }
		throw new RuntimeException("ERROR: Unexpected failure to return a random word");
	}

	// 0 = insert
	// 1 = delete
	// 2 = swap
	@Override
	public boolean mutate() {
		mutateWhich = random.nextInt(3);
		// System.out.println("Current: " + words + " (leftover: " + inputString + "), Mutate which: " + mutateWhich);
		if (mutateWhich == 0 && inputString.length() > 0) {
			mutateWord = randomWord(inputString);
			mutatePosition = random.nextInt(words.size()+1);
			// System.out.println(" - add word, pos: " + mutateWord + ", " + mutatePosition);
			words.add(mutatePosition, mutateWord);
			mutateInputString = inputString;
			try {
				inputString = Anagrams.leftover(mutateWord, inputString);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			// System.out.println(" - new input string: " + inputString);
			// limit word pool to anagrams within remaining inputString
			// for (int i=wordPool.size()-1; i>=0; i--) {
			// 	String word = wordPool.get(i);
			// 	if (!Anagrams.anagram(word, inputString, false)) { // word can't be found in remaining inputString to remove word from pool
			// 		// System.out.println(" - word pool remove " + word);
			// 		wordPoolMutated.add(word);
			// 		wordPool.remove(i);
			// 		wordPoolRemovalHistory.add(word);
			// 	}
			// }
			// System.out.println(" - new word pool size: " + wordPool.size());
		} else if (mutateWhich == 1 && words.size() > 0) {
			mutatePosition = random.nextInt(words.size());
			mutateWord = words.get(mutatePosition);
			// System.out.println(" - remove word, pos: " + mutateWord + ", " + mutatePosition);
			words.remove(mutatePosition);
			mutateInputString = inputString;
			inputString += mutateWord; // put letters back in input string since we removed a word from the candidate solution
			// System.out.println(" - new input string: " + inputString);
			// look through words we might have already removed to see if they are again anagrams in the input string.
			// if we find them, put them back in the word pool.
			// List<String> addedBack = new ArrayList<String>();
			// for (String removedWord : wordPoolRemovalHistory) {
			// 	if (Anagrams.anagram(removedWord, inputString))
			// 		addedBack.add(removedWord);
			// }
			// for (String removedWord : addedBack) {
			// 	// System.out.println(" - word pool add " + removedWord);
			// 	wordPool.add(removedWord);
			// 	wordPoolRemovalHistory.remove(removedWord);
			// }
			// System.out.println(" - new word pool size: " + wordPool.size());
		} else if (mutateWhich == 2 && words.size() > 1) {
			mutateSwapPos1 = random.nextInt(words.size());
			mutateSwapPos2 = mutateSwapPos1;
			while (mutateSwapPos1==mutateSwapPos2) mutateSwapPos2 = random.nextInt(words.size());
			// System.out.println("Swap " + words.get(mutateSwapPos1) + " and " + words.get(mutateSwapPos2) + " (" + mutateSwapPos1 + ", " + mutateSwapPos2 + ")");
			swapWords(mutateSwapPos1, mutateSwapPos2);
		}		
		return true;
	}

	void swapWords(int a, int b) {
		// System.out.println(" - swap a,b " + a + "," + b);
		String tmp = words.get(a);
		words.set(a, words.get(b));
		words.set(b, tmp);
	}
		
	@Override
	public double energy() {
		energyCached = -score();
		return energyCached;
	}
	@Override
	public Solution clone() {
		AnagramSolution sol = new AnagramSolution(inputString);
		sol.words = new ArrayList<String>();
		sol.words.addAll(this.words);
		// sol.wordPool = new ArrayList<String>();
		// sol.wordPool.addAll(this.wordPool);
		// sol.wordPoolMutated = new ArrayList<String>();
		// sol.wordPoolMutated.addAll(this.wordPoolMutated);
		// sol.wordPoolRemovalHistory = new HashSet<String>();
		// sol.wordPoolRemovalHistory.addAll(this.wordPoolRemovalHistory);
		return sol;
	}
	
	public double score() {
		if (words.size() == 0) return 0;
		// return AZDecrypt.score(wordsToStr(true))/wordsToStr(false).length();
		return AZDecrypt.score(wordsToStr(true));
	}
	
}
