package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;

/** snake and permutation get the best scores.  so let's combine them.
 * randomly pick between snake+permutation or permutation+snake.
 *
 */
public class SnakePermutation extends CipherBase {
	static Random rand = new Random();

	@Override
	public String firstLayer(String plaintext) {
		
		boolean order = rand.nextBoolean();
		if (order) {
			// snake then permutation
			String snake = new Snake().firstLayer(plaintext);
			String permutation = new Permutation().firstLayer(snake);
			return permutation;
		}
		// permutation then snake
		String permutation = new Permutation().firstLayer(plaintext);
		String snake = new Snake().firstLayer(permutation);
		return snake;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		SnakePermutation sp = new SnakePermutation();
		String result = sp.makeCipher(pt);
		System.out.println("Result: " + result);

	}

	public static void main(String[] args) {
		 test();
	}

}
