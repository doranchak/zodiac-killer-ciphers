package com.zodiackillerciphers.anagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.dictionary.WordFrequencies.WordBean;
import com.zodiackillerciphers.lucene.NGramsCSRA;

/** Build anagrams for a given input using a generative process.
 * Start with a pool of words, sorted by frequency.
 * Select a word at random.  More common words are selected more often.
 * If letters are available from input to form the word, then start a candidate anagram using the word.
 * Remove the used letters.
 * When placing words into the candidate anagram, use ngram stats with spaces to pick the highest scoring placement.
 * If a word is picked that cannot be used in an anagram, remove it from the pool.
 * Continue until the word pool is empty or the letter pool is empty. 
 */
public class AnagramBuilder {
	
	static String TAB = "	";
	static boolean IGNORE_SINGLE_LETTERS = true;
	static int MAX_WORD_POOL_SIZE = 10000;
	
	/** the input string to try to find anagrams in */
	String input;
	/** pool of words, sorted by frequency */
	List<WordBean> wordPool;
	/** pool of letters */
	Map<Character, Integer> letterPool;
	/** current sum of frequencies, used to do proportional random selection */
	long frequencySum;
	
	
	/** current anagram candidate */
	List<WordBean> anagram;
	StringBuffer anagramStr;
	/** current anagram ngram score */
	float scoreNgram;
	/** current anagram word frequency score */
	int scoreFreq;
	
	Random random;
	
	public AnagramBuilder(String input) {
		// convert to uppercase, and remove all spaces and digits
		this.input = input.toUpperCase().replaceAll("[^A-Z]", "");
		System.out.println("Cleaned input: " + this.input);
		WordFrequencies.init();
		NGramsCSRA.init();
		
		anagram = new ArrayList<WordBean>();
		
		random = new Random();
	}
	
	/** build the sorted word pool */
	void buildWordPool() {
		wordPool = new ArrayList<WordBean>();
		frequencySum = 0;
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() == 1 && IGNORE_SINGLE_LETTERS) {
				if (!word.equals("I") && !word.equals("A")) continue;
			}
			int freq = WordFrequencies.map.get(word);
			WordBean bean = new WordBean();
			bean.word = word;
			bean.frequency = freq;
			wordPool.add(bean);
//			if (frequencySum < 916256990 / 1.5)
//				System.out.println("Added " + bean);
			frequencySum += freq;
			if (wordPool.size() >= MAX_WORD_POOL_SIZE) return;
		}
	}
	
	void buildLetterPool() {
		letterPool = new HashMap<Character, Integer>();
		for (int i=0; i<input.length(); i++) {
			char key = input.charAt(i);
			Integer val = letterPool.get(key);
			if (val == null) {
				val = 0;
			}
			val++;
			letterPool.put(key, val);
		}
		
	}
	
	/** frequency-proportional selection of word */
	WordBean randomWord() {
		float val = random.nextFloat();  // [0,1]
		long dart = (long) (val * frequencySum); // random "dart" somewhere in the weighted roulette wheel of words
		long sum = 0;
		WordBean bean = null;
		for (int i=0; i<wordPool.size(); i++) {
			bean = wordPool.get(i);
			sum += bean.frequency;
			if (sum >= dart) break;
		}
		return bean;
	}
	
	void wordPoolRemove(WordBean bean) {
		wordPool.remove(bean);
		frequencySum -= bean.frequency;
	}
	
	void place(WordBean word) {
		// list has N elements.  {0, 1, 2, ... N-1}
		// Can add word to positions [0, N]
		int N = anagram.size();
		if (N == 0) {
			anagram.add(word);
			scoreAnagram();
		} else {
			float bestZk = 0;
			int bestFreq = 0;
			int bestPos = 0;
			for (int pos=0; pos<=N; pos++) {
				anagram.add(pos, word);
				scoreAnagram();
				//System.out.println(" - trying " + anagram + " [" + scoreNgram + "]");
				if (scoreNgram > bestZk) {
					bestZk = scoreNgram;
					bestFreq = scoreFreq;
					bestPos = pos;
				}
				anagram.remove(pos);
			}
			anagram.add(bestPos, word);
			scoreNgram = bestZk;
			scoreFreq = bestFreq;
		}
	}
	
	void letterPoolRemove(WordBean word) {
		for (int i=0; i<word.word.length(); i++) {
			char key = word.word.charAt(i);
			Integer val = letterPool.get(key);
			if (val == null) {
				throw new RuntimeException("INVALID NULL LETTER POOL STATE when removing " + word);
			}
			val--;
			if (val < 0) {
				throw new RuntimeException("INVALID NEGATIVE LETTER POOL STATE when removing " + word);
			}
			letterPool.put(key, val);
		}
	}
	
	boolean isLetterPoolEmpty() {
		for (Character key : letterPool.keySet()) {
			if (letterPool.get(key) > 0) return false;
		}
		return true;
	}
	
	void clear() {
		if (anagram != null) anagram.clear();
	}
	
	/** generate random anagram candidate */
	void generateAnagram() {
		clear();
		buildWordPool();
		buildLetterPool();
		
		boolean go = true;
		while (go) {
			WordBean word = randomWord();
			if (fits(word)) {
				letterPoolRemove(word); // remove from letter pool
				place(word); // place word in best spot within anagram candidate
			} else {
				// doesn't fit, so remove this word from consideration
				wordPoolRemove(word);
			}

			// stop if letter pool is empty
			if (isLetterPoolEmpty())
				go = false;
			// or if word pool is empty
			else if (wordPool.isEmpty())
				go = false;
			//System.out.println(wordPool.size());
		}
	}
	
	boolean fits(WordBean word) {
		Map<Character, Integer> counts = Ciphers.countMap(word.word);
		for (Character key : counts.keySet()) {
			Integer count1 = counts.get(key);
			Integer count2 = letterPool.get(key);
			if (count2 == null || count2 < count1) return false;
		}
		return true;
	}
	
	/** compute ngram score and word frequency score */
	void scoreAnagram() {
		anagramToString();
		scoreNgram = NGramsCSRA.zkscore(anagramStr, "EN", true);
		scoreFreq = 0;
		for (WordBean bean : anagram) {
			scoreFreq += bean.frequency;
		}
	}
	
	/** output result */
	void outputAnagram() {
		StringBuffer leftover = new StringBuffer();
		for (Character key : letterPool.keySet()) {
			Integer val = letterPool.get(key);
			if (key == 0) continue;
			for (int i=0; i<val; i++) leftover.append(key);
		}
		StringBuffer output = new StringBuffer();
		if (leftover.length() > 0) {
			output.append("[INCOMPLETE: " + leftover + "]" + TAB);
		}
		output.append(scoreNgram).append(TAB).append(scoreFreq).append(TAB).append(anagramStr);
		System.out.println(output);
	}
	
	/** convert anagram to string */
	void anagramToString() {
		anagramStr = new StringBuffer();
		for (WordBean word : anagram) {
			if (anagramStr.length() > 0) 
				anagramStr.append(" ");
			anagramStr.append(word.word);
		}
	}
	
	static void testAnagram() {
		AnagramBuilder a = new AnagramBuilder("YNSETAIDOBLAO   90182390!@#!@#....   	RTRDFWN");
//		a.buildWordPool();
//		a.buildLetterPool();
//		System.out.println(a.frequencySum);
//		for (int i=0; i<100; i++) 
//			System.out.println("Random word: " + a.randomWord());
//		for (WordBean bean : a.wordPool) {
//			if (a.fits(bean)) System.out.println(bean + " fits in " + a.input);
//		}
		for (int i=0; i<1000; i++) {
			//System.out.println("=== Anagram #" + i);
			a.generateAnagram();
			a.outputAnagram();
		}
	}
	
	public static void main(String[] args) {
		testAnagram();
	}
}
