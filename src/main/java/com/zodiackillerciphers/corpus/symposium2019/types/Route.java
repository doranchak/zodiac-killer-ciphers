package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.corpus.symposium2019.CipherGenerator;

/** Snake but more generalized:
 * 1) Write the plaintext into grid in any direction (snake, horizontally LTR or RTL, vertically TTB or BTTM)
 * 2) Read off this grid in any direction (snake, horizontally LTR or RTL, vertically TTB or BTTM)
 * Inspired by https://www.dcode.fr/route-cipher
 */
public class Route extends CipherBase {
	static Random rand = new Random();
	
	/** overrides to force preselected settings */
	public static Integer CORNER = null;
	public static Boolean BOTH_STEPS = true;
	public static Boolean DIRECTION_ROW = null;
	public static Boolean RECTANGLE = null;
	public static Integer WIDTH = null;
	
	public static Boolean OUTPUT_WIDTH = false;
	
	public int width; // last width used for this cipher
	
	@Override
	public String firstLayer(String plaintext) {
		/*
			Two snakes in a row:
				1) a route to write into the grid, then
				2) a route to read cipher text off the grid
			We assume that both routes use the same grid width 
		*/ 
		String snake2 = plaintext;
		while (snake2.equals(plaintext)) {
			int width = WIDTH == null ? 2 + rand.nextInt(169) : WIDTH; // pick random grid width [2,170]
			this.width = width;
			String snake1 = snake(plaintext, width);
//			say("snake1: " + snake1);
			if (BOTH_STEPS) {
				snake2 = snake(snake1, width);
			} else {
				snake2 = snake1;
			}
//			say("snake2: " + snake2);
		}
		return snake2;
	}

	public String snake(String input, int width) {
		// snake
		int corner = CORNER == null ? rand.nextInt(4) : CORNER; // pick a random corner or use preselected one 
		boolean directionRow = DIRECTION_ROW == null ? rand.nextBoolean() : DIRECTION_ROW; // pick a main direction (by rows or by columns)
		boolean rectangle = RECTANGLE == null ? rand.nextBoolean() : RECTANGLE; // if true, then read off horizontally or vertically without snaking
//		say("corner " + corner + " dir " + directionRow + " rectangle " + rectangle + " width " + width);
		String snake = com.zodiackillerciphers.transform.operations.Snake.transform(input, corner, directionRow, width, rectangle, false);
		snake = snake.replaceAll(" ", "");
		return snake;
	}
	
	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Route.OUTPUT_WIDTH = true;
		CipherGenerator.WHICH_STATS = 1;
		Route sp = new Route();
		sp.addPlaintext(pt);
		sp.run();
//		String result = sp.makeCipher(pt);
		System.out.println("Result: " + sp.getCiphers());
		System.out.println(sp.width);
		System.out.println(Arrays.toString(sp.getStats().get(0)));
	}

	public static void main(String[] args) {
		 test();
//		String cipher = "EtToXS6pPqnQP41z7D7w3n2pPTnrCcc!BPlJrvg7N1l2PMxzaw9Ylcvrm7ElJfWKyiVGmVGVrJcsYpkGYqBtjdO8B0mVwLg4m1cE!iV4WiHW3io1SZj5qhB3bZgGYGiZlUCA8FoSGKLtDGhZpB!F0dX7ohSdAPaIBBqufgTR5r2jZJ7utWBLOeGXtjEcvkio!1TQxihxMgpKX7UoI7y4yz0sT50lsoafSsEgqM9!ij4XXv48mZ0TMLiogVd1Mpgn0wpYZgxBUT5xiodJF5ntSZVgQakGRIthFydfVEEJdXHSsmA1MxwnMTg2vhVp2nywpD7EbqxbA4jlsCf9fjsv";
//		cipher = com.zodiackillerciphers.transform.operations.Snake.transform(cipher, 0, true, 34, false, true); 
//		cipher = com.zodiackillerciphers.transform.operations.Snake.transform(cipher, 2, true, 34, true, true);
//		System.out.println(cipher);
	}

}
