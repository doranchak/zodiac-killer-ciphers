package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.pivots.Direction;
import com.zodiackillerciphers.pivots.DirectionDelta;

public class PlusSymbolAdjacencies {
	/** http://www.zodiackillersite.com/viewtopic.php?p=39791#p39791
	 *  Grouping of symbols "+" and "R" is also interesting.
	 */
	
	public static void printAdjacencies() {
		String cipher = Ciphers.cipher[0].cipher;
		String[] grid = Ciphers.grid(cipher, 17);
		
		List<Direction> directions = Direction.all();
		
		for (int row=0; row<grid.length; row++) {
			for (int col=0; col<grid[0].length(); col++) {
				char ch1 = grid[row].charAt(col);
				if (ch1 == '+') {
					for (Direction direction : directions) {
						DirectionDelta delta = Direction.toDelta(direction).get(0);
						try {
							char ch2 = grid[row+delta.drow].charAt(col+delta.dcol);
							System.out.println(ch2+","+direction);
						} catch (Exception e) {}
						
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		printAdjacencies();
	}
}
