package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.List;

/**
 * generate all possible routes in a grid, but only return routes that visit all
 * positions.
 * 
 * http://zodiackillersite.com/viewtopic.php?p=65066#p65066
 * 
 **/
public class GridRoutes {
	public static void search(int height, int width, boolean printRoutes) {
		System.out.println("Search all routes in grid with dimensions " + height + "x" + width + ":");
		boolean[][] grid = new boolean[height][width];
		List<String> visited = new ArrayList<String>();
		long[] count = new long[1];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (printRoutes) System.out.println("Starting at (" + row + ", " + col + "):");
				else System.out.print("(" + row + ", " + col + ") ");
				search(grid, height, width, row, col, visited, count, printRoutes);
				System.out.println(" - subtotal: " + count[0]);
			}
		}
		System.out.println();
		System.out.println("Total: " + count[0]);
	}

	public static void search(boolean[][] grid, int height, int width, int row, int col, List<String> visited,
			long[] count, boolean printRoutes) {

		// bounds check
		if (row < 0 || row == height || col < 0 || col == width) {
			return;
		}

		if (grid[row][col]) {
			return; // already visited this position so return (we don't allow routes to intersect
					// with themselves)
		}

		grid[row][col] = true;

		// add current position to list of visited positions
		visited.add("(" + row + "," + col + ")");

		// if we visited every position, print the route
		if (visited.size() == height * width) {
			if (printRoutes)
				System.out.println(visited);
			count[0]++;
		} else {
			// otherwise, visit all neighbors (horizontal and vertical moves only)
			search(grid, height, width, row, col + 1, visited, count, printRoutes);
			search(grid, height, width, row + 1, col, visited, count, printRoutes);
			search(grid, height, width, row, col - 1, visited, count, printRoutes);
			search(grid, height, width, row - 1, col, visited, count, printRoutes);
		}
		visited.remove(visited.size() - 1);
		grid[row][col] = false;

	}

	public static void main(String[] args) {
		search(17, 3, false);
//		int n = 1;
//		while (true) {
//			search(n, n, false);
//			n++;
//		}
	}
}
