package com.zodiackillerciphers.transform.columnar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;

public class ColumnarTransposition {
	/**
	 * Un-transpose columnar transposition for the given transposed cipher text
	 * @param cipher cipher to untranspose
	 * @param regular if true, padding is accounted for.  otherwise it isn't.
	 * @param columns ordering of columns
	 * @return untransposed cipher text
	 */
	public static String decode(String cipher, List<Integer> columns) {

		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i=0; i<columns.size(); i++) {
			if (columns.get(i) >= columns.size() || columns.get(i) < 0) throw new IllegalArgumentException("Invalid selection: " + columns.get(i));
			map.put(columns.get(i), i);
		}
		if (map.size() != columns.size()) throw new IllegalArgumentException("Columns list has a duplicate element.");
		
		// compute inscription rectangle dimensions
		int L = cipher.length();
		int W = columns.size();
		int H = (int) Math.ceil(((float)L)/W);
		// determine how many padding characters are left over
		int p = W*H-L;
		// if p > 0, then columns at or beyond this one have one leftover position at the last row  
		int colPad = W-p;

		//System.out.println(columns + " W " + W + " H " + H + " p " + p + " colPad " + colPad);

		// make a grid for untransposition
		char[][] grid = new char[H][W];
		for (int row=0; row<H; row++)
			for (int col=0; col<W; col++)
				grid[row][col] = ' ';

		// current selection from the passed list of columns
		int currentColumn = 0;
		// current row of the current column
		int currentRow = 0;
		for (int i=0; i<cipher.length(); i++) {
			boolean endColumn = false;
			// did we reach the end of a column?
			if (currentRow == H)
				endColumn = true;
			else if (map.get(currentColumn) >= colPad && currentRow == H-1) {
				//System.out.println("currentColumn " + currentColumn + " >= colPad " + colPad + " currentRow " + currentRow + " == H-1 " + (H-1));
				endColumn = true;
			}
			
			if (endColumn) {
				currentRow = 0;
				currentColumn++;
			}
			//System.out.println("currentRow " + currentRow + " currentColumn " + currentColumn + " i " + i);
			int whichColumn = map.get(currentColumn);
			//System.out.println("whichColumn " + whichColumn);
			grid[currentRow][whichColumn] = cipher.charAt(i);
			currentRow++;
		}
		String result = "";
		for (int row=0; row<H; row++) {
			//System.out.println(Arrays.toString(grid[row]));
			for (int col=0; col<W; col++) {
				char ch = grid[row][col];
				if (ch == ' ') continue;
				result += ch;
			}
		}
		return result;
	}
	
	
	public static void test1() {
		for (int n=1; n<340; n++) {
			int L = 340;
			int W = n;
			int H = (int) Math.ceil(L/W);
			// determine how many padding characters are left over
			int p = L-W*H;
			System.out.println("W " + W + " H " + H + " p " + p);
		}
	}
	public static void test2() {
		String cipher = Ciphers.cipher[0].cipher;
		List<Integer> columns = new ArrayList<Integer>();
		columns.add(1);
		columns.add(0);

		System.out.println(cipher);
		System.out.println(decode(cipher, columns));
	}	
	public static void test3() {
		//String cipher = "HTHESTHHRASWRASCSCRSSCWWWESWWEIITAIIT";
		String cipher = "SIOTUDECVPUSNDEJLGFHRODKEHAICEXWBXZOEDAOSQIERTAOSQIERTDMIRSOCTAYNTYDOJLGFHRODCVPUSNDEAYNTYDODMIRSOCTSIOTUDEKEHAICEXWBXZOED";
		List<Integer> columns = new ArrayList<Integer>();
		/*columns.add(3);
		columns.add(1);
		columns.add(4);
		columns.add(2);
		columns.add(0);*/
		columns.add(1);
		columns.add(3);
		columns.add(5);
		columns.add(7);
		columns.add(6);
		columns.add(4);
		columns.add(2);
		columns.add(0);
		System.out.println(cipher);
		System.out.println(decode(cipher, columns));
	}
	public static void main(String[] args) {
		test3();
	}
	
}
