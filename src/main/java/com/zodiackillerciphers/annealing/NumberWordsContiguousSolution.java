package com.zodiackillerciphers.annealing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.tests.mapcode.EnglishNumberToWords;

/**
 * try to stuff as many number words (ONE, TWO, THREE, etc) into a cipher as
 * possible
 * 
 * this is like NumberWordsSolution but enforces contiguous number words
 */
public class NumberWordsContiguousSolution extends NumberWordsSolution {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#mutate()
	 */
	@Override
	public boolean mutate() {

		// prepend a number to current stream of number words
		// append a number to current stream of number words
		System.out.println("we got here");
		int which = rand.nextInt(2);
		if (which == 0)
			return mutatePrepend();
		else if (which == 1)
			return mutateAppend();
		else
			throw new RuntimeException("Unexpected selection: " + which);

	}

	boolean mutatePrepend() {
		int minPos = Integer.MAX_VALUE;
		for (NumberWord nw : numberWords) {
			minPos = Math.min(minPos, nw.pos);
		}
		if (minPos == 0)
			return false;

		minPos--;
		// find all candidates that end at minPos
		List<NumberWord> candidates = new ArrayList<NumberWord>();
		for (NumberWord nw : placements()) {
			if (nw.pos + nw.word().length() - 1 == minPos) {
				candidates.add(nw);
				// System.out.println("added " + nw);
			}
		}

		if (candidates.isEmpty())
			return false;
		numberWords.add(candidates.get(rand.nextInt(candidates.size())));
		return true;
	}

	boolean mutateAppend() {
		int maxPos = Integer.MIN_VALUE;
		for (NumberWord nw : numberWords) {
			maxPos = Math.max(maxPos, nw.pos + nw.word().length());
		}
		if (maxPos == cipher.length() - 1)
			return false;

		// find all candidates that end at minPos
		List<NumberWord> candidates = new ArrayList<NumberWord>();
		for (NumberWord nw : placements()) {
			if (nw.pos == maxPos) {
				candidates.add(nw);
				// System.out.println("added " + nw);
			}
		}

		if (candidates.isEmpty())
			return false;
		numberWords.add(candidates.get(rand.nextInt(candidates.size())));
		return true;
	}

	@Override
	public void initialize() {
		numberWords = new ArrayList<NumberWord>();
		key = new HashMap<Character, Character>();
		rand = new Random();

		/** force a single number word to appear somewhere */
		boolean valid = false;
		NumberWord nw = null;
		while (!valid) {
			nw = new NumberWord(rand.nextInt(26) + 1, rand.nextInt(cipher
					.length()));
			valid = valid(nw);
		}
		numberWords.add(nw);
	}

	public Solution clone() {
		// TODO Auto-generated method stub
		// System.out.println("CLONE?!?!?");
		NumberWordsContiguousSolution sol = new NumberWordsContiguousSolution();
		sol.cipher = this.cipher;
		sol.initialize();
		sol.numberWords.clear();
		for (NumberWord nw : this.numberWords)
			sol.numberWords.add(new NumberWord(nw.number, nw.pos));
		sol.energy();
		return sol;
	}

	/** try out some random solution */
	public static void test() {
		NumberWordsContiguousSolution sol = new NumberWordsContiguousSolution();
		sol.setCipher(Ciphers.cipher[0].cipher);
		sol.initialize();

		boolean prepend = true;
		boolean append = true;

		while (true) {
			if (rand.nextBoolean()) {
				prepend = sol.mutatePrepend();
			} else
				append = sol.mutateAppend();
			if (!prepend && !append)
				break;
			// System.out.println(sol);
			sol.energy();
		}
		System.out.println("Final: " + sol);
	}

	public static void main(String[] args) {
		while (true)
			test();
	}

}
