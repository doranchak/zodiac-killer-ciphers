package com.zodiackillerciphers.annealing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.tests.mapcode.EnglishNumberToWords;

/**
 * try to stuff as many number words (ONE, TWO, THREE, etc) into a cipher as
 * possible
 */
public class NumberWordsSolution extends Solution {

	/** the cipher text */
	String cipher;
	/** the plain text */
	String plain;
	/** list of words and positions */
	List<NumberWord> numberWords;
	/** current key (ciphertext to plaintext) */
	Map<Character, Character> key;
	/** number of unencoded positions remaining */
	int unencoded;
	/** store copy of energy score to avoid recomputing */
	public double energy;
	/** number of errors encountered during encoding */
	int errors;

	public static Random rand;

	/** map of each cipher symbol to all of its locations in the cipher text */

	/**
	 * encode the given NumberWord. place the word form of the number at the
	 * given position. returns the number of encoding errors encountered.
	 */
	int encode(NumberWord numberWord) {
		String word = numberWord.word();
		boolean valid = true;
		int pos = numberWord.getPos();
		int errors = 0;
		// String str = "";
		String undo = "";
		for (int i = 0; i < word.length(); i++) {
			if (pos + i > cipher.length() - 1) {
				errors++;
				valid = false;
				break;
			}
			Character c = cipher.charAt(pos + i);
			Character p1 = key.get(c);
			Character p2 = word.charAt(i);
			if (p1 == null) {
				// no key entry
				undo += c + "" + p2;
				key.put(c, p2);
			} else if (p1 == p2) {
				// no violation of key
			} else {
				// violation of key
				errors++;
				valid = false;
			}
		}
		if (valid) {
			debug(undo);
			for (int i = 0; i < undo.length(); i += 2) {
				key.put(undo.charAt(i), undo.charAt(i + 1));
			}
		} else {
			for (int i = 0; i < undo.length(); i += 2) {
				key.remove(undo.charAt(i));
			}
		}
		debug(numberWord + ", " + word + ", " + valid + ", " + undo + ", "
				+ errors + ", " + key);
		return errors;
	}

	/**
	 * encode all of this solution's number words, and return the total number
	 * of encoding errors
	 */
	int encode() {
		key.clear();
		int errors = 0;
		for (int i = 0; i < numberWords.size(); i++) {
			NumberWord nw = numberWords.get(i);
			errors += encode(nw);
			for (int j = 0; j < i; j++) {
				NumberWord nw2 = numberWords.get(j);
				errors += nw2.overlaps(nw);
			}
		}
		this.errors = errors;
		return errors;
	}

	/** decode plaintext from current key */
	void decode() {
		StringBuffer sb = new StringBuffer();
		unencoded = 0;
		for (int i = 0; i < cipher.length(); i++) {
			Character p = key.get(cipher.charAt(i));
			if (p == null) {
				unencoded++;
				sb.append("_");
			} else {
				sb.append(p);
			}
		}
		plain = sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#mutate()
	 */
	@Override
	public boolean mutate() {
		// System.out.println("mutate " + this.hashCode());

		// for (int i = 0; i < rand.nextInt(4) + 1; i++) {
		int which = rand.nextInt(6);
		if (which == 0)
			mutateSwapOrder();
		else if (which == 1)
			mutateSwapPositions();
		else if (which == 2)
			mutateDelete();
		else if (which == 3)
			mutateAdd();
		else if (which == 4)
			mutateMove();
		else if (which == 5)
			mutateGreedy();
		else
			throw new RuntimeException("Unexpected selection: " + which);
		// }
		return true;
	}

	/** swap the order of two randomly selected number words */
	void mutateSwapOrder() {
		if (numberWords.size() < 2)
			return;
		int a = 0;
		int b = 0;
		while (a == b) {
			a = rand.nextInt(numberWords.size());
			b = rand.nextInt(numberWords.size());
		}
		NumberWord tmp = numberWords.get(a);
		numberWords.set(a, numberWords.get(b));
		numberWords.set(b, tmp);
	}

	/** swap the positions of two randomly selected number words */
	void mutateSwapPositions() {
		if (numberWords.size() < 2)
			return;
		int a = 0;
		int b = 0;
		while (a == b) {
			a = rand.nextInt(numberWords.size());
			b = rand.nextInt(numberWords.size());
		}
		int tmp = numberWords.get(a).getPos();
		numberWords.get(a).setPos(numberWords.get(b).getPos());
		numberWords.get(b).setPos(tmp);
	}

	/** delete a randomly selected number word */
	void mutateDelete() {
		if (numberWords.size() < 1)
			return;
		int a = rand.nextInt(numberWords.size());
		numberWords.remove(a);
	}

	NumberWord randomWord() {
		return new NumberWord(rand.nextInt(26) + 1, rand.nextInt(cipher
				.length()));
	}

	/** add a random number word somewhere */
	void mutateAdd() {
		int n = numberWords.size() == 0 ? 0 : rand.nextInt(numberWords.size());
		numberWords.add(n, randomWord());
	}

	/** move a randomly selected word to a random position */
	void mutateMove() {
		if (numberWords.size() < 1)
			return;
		int a = rand.nextInt(numberWords.size());
		numberWords.get(a).setPos(rand.nextInt(cipher.length()));
	}

	/**
	 * generate all possible valid placements and pick one at random. returns
	 * false if no placements are possible.
	 */
	boolean mutateGreedy() {
		List<NumberWord> list = placements();
		if (list == null || list.isEmpty())
			return false;
		// System.out.println(list.size() + " placements.");
		numberWords.add(list.get(rand.nextInt(list.size())));
		return true;
	}

	public double energy() {
		// goals:
		// 1) should have IOC similar to English
		// 2) should maximize coverage of cipher text
		// 3) word placements must not violate cipher constraints
		encode();
		decode();
		double iocdiff = Stats.iocDiff(plain);
		if (numberWords.size() == 0)
			return Double.MAX_VALUE;

		/**
		 * count unique nums. we want to maximize the variety of number
		 * selections
		 */
		Set<Integer> seen = new HashSet<Integer>();
		int uncovered = cipher.length();
		for (NumberWord nw : numberWords) {
			seen.add(nw.number);
			uncovered -= nw.word().length();
		}

		// energy = (1 + ((double) 1) / seen.size())
		// * (1 + ((double) 1) / numberWords.size()) * (1 + errors)
		// * (1 + 10 * iocdiff);
		energy = (1 + 10 * iocdiff) * (1 + errors)
				* (1 + ((double) uncovered) / cipher.length())
				* (1 + ((double) 26) / seen.size());
		energy = (1 + errors) * (1 + ((double) uncovered) / cipher.length());
		
		return energy;
	}

	public double energy3() {
		// goals:
		// 1) should have IOC similar to English
		// 2) should maximize coverage of cipher text
		// 3) word placements must not violate cipher constraints
		encode();
		decode();
		double iocdiff = Stats.iocDiff(plain);
		if (numberWords.size() == 0)
			return Double.MAX_VALUE;
		energy = (1 + iocdiff) * (1 + ((double) 1) / numberWords.size())
				* (1 + errors);
		return energy;
	}

	public double energy2() {
		// goals:
		// 1) should have IOC similar to English
		// 2) should maximize coverage of cipher text
		// 3) word placements must not violate cipher constraints
		encode();
		decode();
		double iocdiff = Stats.iocDiff(plain);
		energy = (1 + iocdiff) * (1 + unencoded) * (1 + errors);
		return energy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#initialize()
	 */
	@Override
	public void initialize() {
		numberWords = new ArrayList<NumberWord>();
		key = new HashMap<Character, Character>();
		rand = new Random();

		int num = 1 + rand.nextInt(19);
		for (int i = 0; i < num; i++) {
			numberWords.add(randomWord());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#clone()
	 */
	@Override
	public Solution clone() {
		// TODO Auto-generated method stub
		// System.out.println("CLONE?!?!?");
		NumberWordsSolution sol = new NumberWordsSolution();
		sol.cipher = this.cipher;
		sol.initialize();
		sol.numberWords.clear();
		for (NumberWord nw : this.numberWords)
			sol.numberWords.add(new NumberWord(nw.number, nw.pos));
		sol.energy();
		return sol;
	}

	/**
	 * @return the cipher
	 */
	public String getCipher() {
		return cipher;
	}

	/**
	 * @param cipher
	 *            the cipher to set
	 */
	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#representation()
	 */
	@Override
	public String representation() {
		String rep = "";
		rep += highlight(plain) + "	" + key + "	" + numberWords + "	"
				+ unencoded + "	" + errors;
		return rep;
	}

	public String highlight(String plain) {
		String[] nums = new String[] { "TWENTY", "THIRTEEN", "FIFTEEN", "TEEN",
				"TWELVE", "ELEVEN", "ONE", "TWO", "THREE", "FOUR", "FIVE",
				"SIX", "SEVEN", "EIGHT", "NINE", "TEN" };
		for (String num : nums)
			plain = plain.replaceAll(num, " [" + num + "] ");
		return plain;
	}

	/** generate list of all possible remaining placements of number words */
	public List<NumberWord> placements() {
		List<NumberWord> result = new ArrayList<NumberWord>();
		for (int i = 0; i < cipher.length(); i++) {
			for (int n = 1; n <= 26; n++) {
				NumberWord nw = new NumberWord(n, i);
				if (exists(nw))
					continue;
				if (valid(nw))
					result.add(nw);
			}
		}
		return result;
	}

	/** returns true if this number word already exists in this solution's list */
	public boolean exists(NumberWord nw1) {
		for (NumberWord nw2 : numberWords)
			if (nw1.getPos() == nw2.getPos()
					&& nw1.getNumber() == nw2.getNumber())
				return true;
		return false;
	}

	/**
	 * returns true if the given number word can be encoded at the given
	 * position
	 */
	public boolean valid(NumberWord nw) {
		String word = nw.word();
		if (nw.pos + word.length() > cipher.length())
			return false;
		for (int i = 0; i < word.length(); i++) {
			char p = word.charAt(i);
			char c = cipher.charAt(nw.pos + i);
			if (key.get(c) == null)
				continue;
			if (key.get(c) == p)
				continue;
			return false;
		}
		return true;
	}

	public static void testGreedy() {
		double best = Double.MAX_VALUE;
		while (true) {
			NumberWordsSolution sol = new NumberWordsSolution();
			sol.setCipher(Ciphers.cipher[0].cipher);
			sol.initialize();
			sol.numberWords.clear();
			sol.energy();
			boolean go = true;
			while (go) {
				go = sol.mutateGreedy();
				sol.energy();
			}
			// System.out.println("Final solution: " + sol);
			if (sol.energy() < best) {
				best = sol.energy();
				System.out.println("NEW BEST: " + sol);
			}
		}
	}

	public static void testSolutions() {

		NumberWordsSolution sol = new NumberWordsSolution();
		sol.setCipher(Ciphers.cipher[0].cipher);
		sol.initialize();
		sol.numberWords.clear();
		// int[][] vals = new int[][] { { 20, 306 }, { 26, 319 }, { 2, 220 },
		// { 6, 127 }, { 5, 276 }, { 9, 256 }, { 4, 179 }, { 2, 43 },
		// { 4, 107 }, { 6, 299 }, { 1, 123 }, { 5, 173 }, { 8, 332 },
		// { 1, 146 }, { 1, 243 }, { 5, 6 }, { 1, 81 }, { 2, 118 },
		// { 2, 30 }, { 6, 325 }, { 20, 319 }, { 2, 336 } };

		int[][] vals = new int[][] { { 6, 199 }, { 1, 248 }, { 10, 155 },
				{ 5, 107 }, { 1, 162 }, { 10, 16 }, { 5, 167 }, { 10, 303 },
				{ 4, 24 }, { 3, 221 }, { 2, 240 }, { 2, 56 }, { 10, 155 },
				{ 6, 131 }, { 2, 56 }, { 2, 1 }, { 10, 136 }, { 6, 147 },
				{ 1, 58 }, { 2, 56 }, { 1, 263 }, { 10, 136 }, { 1, 162 },
				{ 2, 124 }, { 6, 140 }, { 6, 79 }, { 2, 21 }, { 1, 263 },
				{ 2, 1 }, { 10, 83 }, { 10, 44 }, { 2, 1 }, { 1, 263 },
				{ 10, 136 }, { 10, 83 }, { 4, 24 }, { 10, 5 }, { 2, 1 },
				{ 5, 107 }, { 10, 326 }, { 2, 176 }, { 10, 16 }, { 10, 83 },
				{ 1, 267 }, { 1, 267 }, { 10, 155 }, { 6, 204 }, { 10, 136 },
				{ 2, 240 }, { 10, 303 }, { 5, 103 }, { 2, 190 } };

		for (int[] pair : vals) {
			sol.numberWords.add(new NumberWord(pair[0], pair[1]));
		}
		System.out.println(sol);
	}

	public static void process(String path) {
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			
			
			String sub = line.substring(line.indexOf("[{")+1);
			sub = sub.split("\\]")[0];
			String nws = sub.replaceAll("\\{", "").replaceAll("\\}", "")
					.replaceAll(" ", "");

			NumberWordsSolution sol = new NumberWordsSolution();
			sol.setCipher(Ciphers.cipher[0].cipher);
			sol.initialize();
			sol.numberWords.clear();
			String[] vals = nws.split(",");
//			System.out.println("NWS " + nws + " LINE " + line);
			for (int i = 0; i < vals.length; i += 2) {
				sol.numberWords.add(new NumberWord(Integer.valueOf(vals[i]),
						Integer.valueOf(vals[i + 1])));
			}
			System.out.println(sol);
		}
	}

	public static void main(String[] args) {
		// testGreedy();
		// testSolutions();
		process("/Users/doranchak/projects/zodiac/sa-all.txt");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#energyCached()
	 */
	@Override
	public double energyCached() {
		// TODO Auto-generated method stub
		return energy;
	}

	public String toString() {
		return energy() + "	" + representation() + "	" + numberWords.size();
	}

	@Override
	public void mutateReverse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mutateReverseClear() {
		// TODO Auto-generated method stub
		
	}
}
