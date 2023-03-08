package com.zodiackillerciphers.ciphers.kaczynski;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** searches of the unabomber matrix (looking for patterned sequences) */
public class MatrixSearch {
	/* starting with the number at this row and column, output all sequential sequences (incrementing by the given amount),
	 * allowing for at most [delta] horizontal/vertical movements per step (i.e., a window size of (2*delta+1) x (2*delta+1))
	 */

	public static int LIMIT = 10000;
	public static boolean REMOVE_OVERLAPS = true;
	boolean[][] visited;
	int currentRow, currentCol, currentValue;
	int increment;
	int delta;
	int W, H;
//	boolean first;
	
	int[][] grid;
	
	public static String tab = "	";
	
	List<int[]> matches;
	public static List<MatrixSearchBean> beans;
	static {
		beans = new ArrayList<MatrixSearchBean>();
	}
	
	public MatrixSearch(int row, int col, int increment, int delta, boolean fibonacci, int maxErrors, int maxDirectionChanges, int minLength) {
		//System.out.println("New search " + row + " " + col + " " + increment + " " + delta);
		// first = true;
		grid = Matrix.unabomberMatrix;
		H = grid.length;
		W = grid[0].length;
		visited = new boolean[H][W];
		for (var r=0; r<H; r++)
			visited[r] = new boolean[W];
		currentValue = grid[row][col] - (fibonacci ? 0 : increment);
		this.increment = increment;
		this.delta = delta;
		this.currentRow = row;
		this.currentCol = col;
		this.matches = new ArrayList<int[]>();
		search(0, fibonacci, null, null, maxErrors, maxDirectionChanges, minLength);
	}
	
	// returns true if we found a match at the current position before recursing.
	// f1 and f2 are for tracking the last two numbers when doing fibonacci searches 
	public boolean search(int level, boolean fibonacci, Integer f2, Integer f1, int maxErrors, int maxDirectionChanges, int minLength) {
		
		// bounds check
		if (currentCol < 0) return false;
		if (currentCol >= W) return false;
		if (currentRow < 0) return false;
		if (currentRow >= H) return false;
		
		// did we already visit here?
		if (visited[currentRow][currentCol]) return false;

//		System.out.println("search " + level + " " + currentRow + " " + currentCol + " " + currentValue + " " + f1 + " " + f2);
		int previousValue = currentValue;
		currentValue = grid[currentRow][currentCol];
		
		int expectedValue = previousValue + increment;
		// if (first) first = false;
		
		// return if the next expected number is not found.
		if (fibonacci && f1 != null && f2 != null) {
			if (currentValue != f1 + f2) {
				if (maxErrors == 0) {
					currentValue = previousValue;
					return false;
				} else maxErrors--;
			}
		} else if (!fibonacci) {
			if (currentValue != expectedValue) {
				if (maxErrors == 0) {
					currentValue = previousValue;
					return false;
				} else maxErrors--;
			}
		}

		// found it!  print it and mark it visited
		//for (int i=0; i<level; i++) {			
		//		System.out.print(tab);
		//}
		//System.out.println(currentValue + tab + currentRow + tab + currentCol);
		if (!addMatch(new int[] {currentRow,currentCol}, maxDirectionChanges)) {
			currentValue = previousValue;
			return false;
		}
		
		visited[currentRow][currentCol] = true;		
		// then explore all the possible neighbors
		
		int currentColTmp = currentCol;
		int currentRowTmp = currentRow;
		boolean found = false;
		for (int row=currentRowTmp-delta; row<=currentRowTmp+delta && row<H; row++) {
			for (int col=currentColTmp-delta; col<=currentColTmp+delta && col<W; col++) {
				currentRow = row;
				currentCol = col;
				//currentValue = grid[row][col];
				found = found | search(level+1, fibonacci, f1, currentValue, maxErrors, maxDirectionChanges, minLength);
			}
		}
		
		if (!found && matches.size() >= minLength) {
			process(matches);
		}
		
		currentCol = currentColTmp;
		currentRow = currentRowTmp;
		currentValue = previousValue;
		matches.remove(matches.size()-1);
		
		// unmark visited
		visited[currentRow][currentCol] = false;
				
		return true;
	}
	
	/**
	 * add to matches but track # of direction changes. returns false only if we've
	 * exceeded the given max allowed number of direction changes.
	 */
	boolean addMatch(int[] rc, int maxDirectionChanges) {
		matches.add(rc);
		boolean result = directionChanges(matches) <= maxDirectionChanges;
		if (!result) matches.remove(rc);
		return result;
	}
	
	static int directionChanges(List<int[]> positions) {
		if (positions == null || positions.size() < 2) return 0;
		Set<String> set = new HashSet<String>();
		for (int i=1; i<positions.size(); i++) {
			int[] posCurrent = positions.get(i);
			int[] posPrevious = positions.get(i-1);
			int dy = posCurrent[0] - posPrevious[0]; 
			int dx = posCurrent[1] - posPrevious[1];
			if (dy != 0) dy /= Math.abs(dy);
			if (dx != 0) dx /= Math.abs(dx);
			set.add(dy + " " + dx);
		}
		return set.size();
	}
	
	void process(List<int[]> matches) {
		
		MatrixSearchBean bean = new MatrixSearchBean();
		bean.positions = new ArrayList<int[]>();
		for (int[] rc : matches) bean.positions.add(new int[] {rc[0], rc[1]});
		bean.calculateDistance();
		
		this.beans.add(bean);
		
		//System.out.println(matches.size() + tab + score + tab + jumps + tab + toString(matches));
		
	}
	
	
	public static void searchGrid(int increment, int delta, boolean finobacci, int maxErrors, int maxDirectionChanges, int minLength) {
		for (int row=0; row<54; row++) {
			for (int col=0; col<42; col++) {
				new MatrixSearch(row, col, increment, delta, finobacci, maxErrors, maxDirectionChanges, minLength);
			}
		}
//		new MatrixSearch(0, 0, 1, 1);
		processBeans();
	}
	public static void searchGrid2() {
	
		new MatrixSearch(24, 0, 1, 1, true, 0, 1000000, 2);
		processBeans();
	}
	
	public static void searchGrid3() {
		
		new MatrixSearch(20, 13, 1, 2, true, 0, 2, 2);
		processBeans();
	}
	
	public static void clusters() {
		Integer currentClusterId;
		int seq = 0;
		Map<String, Integer> clusters = new HashMap<String, Integer>();
		for (MatrixSearchBean bean : beans) {
			currentClusterId = null;
			for (int[] rc : bean.positions) {
				String key = Arrays.toString(rc);
				Integer clusterId = clusters.get(key);
				if (clusterId != null) {
					currentClusterId = clusterId;
					break;
				}
			}
			if (currentClusterId == null) 
				currentClusterId = seq++;
			for (int[] rc : bean.positions) {
				String key = Arrays.toString(rc);
				clusters.put(key, currentClusterId);
			}
			bean.clusterId = currentClusterId;
		}
		
	}
	public static void processBeans() {
		System.out.println("Results size before: " + beans.size());
		// normalize length and jumps
		int minLength = Integer.MAX_VALUE;
		int maxLength = Integer.MIN_VALUE;
		
		float minJumps = Float.MAX_VALUE;
		float maxJumps = Float.MIN_VALUE;
		
		for (MatrixSearchBean bean : beans) {
			minLength = Math.min(minLength, bean.positions.size());
			maxLength = Math.max(maxLength, bean.positions.size());
			
			minJumps = Math.min(minJumps, bean.jumps);
			maxJumps = Math.max(maxJumps, bean.jumps);
		}
//		System.out.println(minLength);
//		System.out.println(maxLength);
//		System.out.println(minJumps);
//		System.out.println(maxJumps);
		
		for (MatrixSearchBean bean : beans) {
			bean.normLength = bean.positions.size() - minLength;
			bean.normLength /= (maxLength - minLength);
			bean.normJumps = bean.jumps - minJumps;
			bean.normJumps /= (maxJumps - minJumps);
		}
		
		
		// calculate clusters based on which positions they cover
		// clusters();
		
		Collections.sort(beans, new Comparator<MatrixSearchBean>() {

			@Override
			public int compare(MatrixSearchBean o1, MatrixSearchBean o2) {
				// TODO Auto-generated method stub
				return Float.compare(o2.score(), o1.score());
			}
			
		});
		
		if (REMOVE_OVERLAPS) beans = removeOverlaps(beans);
		System.out.println("Results size after: " + beans.size());
		
		
		int count = 0;
		for (MatrixSearchBean bean : beans) {
			System.out.println(bean);
			count++;
			if (count == LIMIT) break;
		}

		
	}
	
	/** beans are sorted by score (highest first).  remove overlapping results. */
	public static List<MatrixSearchBean> removeOverlaps(List<MatrixSearchBean> beans) {
		if (beans == null || beans.isEmpty()) return beans;
		Set<String> seen = new HashSet<String>();
		List<MatrixSearchBean> newBeans = new ArrayList<MatrixSearchBean>();
		for (int i=0; i<beans.size(); i++) {
			boolean add = true;
			for (int[] position : beans.get(i).positions) {
				String key = Arrays.toString(position);
				if (seen.contains(key)) {
					add = false;
					break;
				}
				seen.add(key);
			}
			if (add) newBeans.add(beans.get(i));
		}
		return newBeans;
	}
	
	public static void testDirectionChanges() {
		List<int[]> list = new ArrayList<int[]>();
		list.add(new int[] {12,0});
		list.add(new int[] {10,0});
		list.add(new int[] {10,1});
		list.add(new int[] {10,2});
		list.add(new int[] {11,0});
		list.add(new int[] {11,1});
		list.add(new int[] {11,2});
		list.add(new int[] {12,1});
		list.add(new int[] {12,2});
		list.add(new int[] {13,0});
		list.add(new int[] {13,1});
		list.add(new int[] {13,2});
		list.add(new int[] {14,0});
		list.add(new int[] {14,1});
		list.add(new int[] {14,2});
		System.out.println(directionChanges(list));
	}
	public static void main(String[] args) {
		System.out.println("Begin");
		//searchGrid(1, 2, true, 0, 2); // inc, delta, fib, max errors, max direction changes
		searchGrid(1, 1, true, 0, 1, 6);
//		searchGrid(10, 2, false, 0, 10);
//		searchGrid2();
//		searchGrid3();
//		testDirectionChanges();	
	}

}
