package com.zodiackillerciphers.pivots.symmetries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.TransformationBase;

/** more general search for pivot-like patterns */
public class Search {

	public static boolean DEBUG = false;
	public static int[][] directions = new int[][] {
		{0,1},
		{1,0},
		{0,-1},
		{-1,0}
	};
	
	/** best counts per period.
	 * key: period
	 * value: 2nd map
	 * 
	 * 2nd map:
	 * key: best count
	 * value: number of times this best count occurs
	 **/
	public static Map<Integer, Map<Integer, Integer>> bestMap = new HashMap<Integer, Map<Integer, Integer>>();
	public static int period;
	public static Map<Character, Integer> countMap;
	
	public static char charAt(List<StringBuffer> cipher, int[] point) {
		return cipher.get(point[0]).charAt(point[1]);
	}

	public static List<Pair> search(List<StringBuffer> cipher) {
		//Map<Character, List<int[]>> map = new HashMap<Character, List<int[]>>();
		List<Pair> pairs = new ArrayList<Pair>();
		for (int row=0; row<cipher.size(); row++) {
			for (int col=0; col<cipher.get(0).length(); col++) {
				int[] origin = new int[] {row,col};
				
				for (int a=0; a<directions.length-1; a++) {
					for (int b=a+1; b<directions.length; b++) {
						Set<String> visited = new HashSet<String>();
						int[][] d = new int[][] {
								directions[a],
								directions[b]
						};
						Pair pair = new Pair();
						pair.origin = origin;
						pair.directions = d;
						search(cipher, origin, 1, pair, visited, d);
						pairs.add(pair);
					}
				}
			}
		}
		return pairs;
	}
	
	public static String p(int[][] points) {
		if (points == null) return null;
		String p = "";
		for (int[] point : points) p += (point == null ? null : Arrays.toString(point)) + " ";
		return p;
		
	}
	public static String d(int[][] dir) {
		String p = "";
		for (int[] d : dir) p += Arrays.toString(d) + " ";
		return p;
	}
	public static void search(List<StringBuffer> cipher, int[] origin, int offset, Pair pair, Set<String> visited, int[][] directions) {
		int[][] points = points(origin, offset, directions, cipher.size(), cipher.get(0).length(), visited);
		
		if (DEBUG) {
			System.out.println("origin " + Arrays.toString(origin) + " offset " + offset + " points " + p(points) + " directions " + d(directions));
		}
		
		if (points == null) {
			return;
		}

		for (int i=0; i<points.length; i++) {
			int[] point = points[i];
			if (visited.contains(Arrays.toString(point))) return; // we already visited one of these points
			visited.add(Arrays.toString(point));
		}
		
		char c1 = charAt(cipher, points[0]);
		char c2 = charAt(cipher, points[1]);
		// if any repeats are found, add to results
		if (c1 == c2) {
			Match match = new Match();
			match.offset = offset;
			match.positions = points;
			match.symbol = c1;
			pair.matches.add(match);
		}
		
		// keep searching along these axes until we're done
		search(cipher, origin, offset+1, pair, visited, directions);
	}
	
	/** get the four points that have the given offset from the origin.  allow wrapping.
	 * if any point is back at the origin, return null. */ 
	public static int[][] points(int[] origin, int offset, int length, int width, Set<String> visited) {
		int[][] points = new int[directions.length][2];
		for (int i=0; i<points.length; i++) {
			int[] direction = directions[i];
			points[i] = point(origin, offset, direction, length, width);
			if (points[i] == null) return null;
			
			// if we already marked this point in the map, then we're done with the search.
			if (visited.contains(Arrays.toString(points[i]))) return null;
			
		}
		return points;
	}
	/** same as above but use the given directions intead */
	public static int[][] points(int[] origin, int offset, int[][] directions, int length, int width, Set<String> visited) {
		int[][] points = new int[directions.length][2];
		for (int i=0; i<points.length; i++) {
			int[] direction = directions[i];
			points[i] = point(origin, offset, direction, length, width);
			if (points[i] == null) return null;
			
			// if we already marked this point in the map, then we're done with the search.
			if (visited.contains(Arrays.toString(points[i]))) return null;
			
		}
		return points;
	}
	
	/** returns the point in the given direction at the given offset from the origin.
	 * if the point is back at the origin, return null.
	 */
	public static int[] point(int[] origin, int offset, int[] direction, int length, int width) {
		int[] point = new int[] {
				origin[0]+direction[0]*offset,
				origin[1]+direction[1]*offset
		};
		
		// allow wrapping
		if (point[0] < 0) point[0] += length;
		if (point[1] < 0) point[1] += width;
		point[0] %= length;
		point[1] %= width;

		if (origin[0] == point[0] && origin[1] == point[1]) return null;
		return point;
	}
	
	public static void score(List<Result> results) {
		// 1) number of repeats
		// 2) max that occur along same pair of directions
		
		// map direction pair to count of occurrences
		
		int maxCount = -1;
		String maxDirection = null;
		
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for (Result result: results) {
			for (int[] d : result.directions) {
				String key = Arrays.toString(d);
				Integer val = counts.get(key);
				if (val == null) val = 0;
				val++;
				if (val > maxCount) {
					maxCount = val;
					maxDirection = key;
				}
				counts.put(key,val);
			}
		}

		if (maxDirection != null && maxCount > 1) {
			System.out.println("best: " + maxDirection + " " + maxCount);
			
			Map<Integer, Integer> map = bestMap.get(period);
			if (map == null) map = new HashMap<Integer, Integer>();
			Integer val = map.get(maxCount);
			if (val == null) val = 0;
			val++;
			map.put(maxCount, val);
			bestMap.put(period, map);

			//double score = 1;
			for (Result result : results) {
				if (result.hasDirection(maxDirection)) {
					/*for (int[] position : result.positions) {
						// lower symbol frequencies produce lower probabilities.
						score /= (double) countMap.get(result.symbol);
						// give reward for shorter distances from origin
						score *= distance(result.origin, position);
					}*/
					System.out.println(result);
				}
			}
			//System.out.println("Score: " + score);
		}
	}
	
	public static double distance(int[] p1, int[] p2) {
		double diff = Math.pow(p1[0]-p2[0],2) + Math.pow(p1[1]-p2[1],2);
		return Math.sqrt(diff);
	}
	
	public static void test() {
		String cipher = Ciphers.cipher[6].cipher;
		
		for (period = 1; period<cipher.length()/2; period++) {
			String re = Periods.rewrite3(cipher, period);
			countMap = Ciphers.countMap(re);
			System.out.println("===== period " + period + " ===== " + re);
			List<StringBuffer> list = TransformationBase.toList(re, 17);
			List<Pair> results = search(list);
			
			for (int i=0; i<list.size(); i++) {
				String line = "\"" + list.get(i) + "\"";
				if (i<list.size()-1) line += ",";
				System.out.println(line);
			}
			
			for (Pair pair : results) pair.dump(countMap, cipher.length(), period);
		}
		
		for (int period : bestMap.keySet()) {
			for (int key : bestMap.get(period).keySet()) {
				System.out.println("period " + period + " best " + key + " count " + bestMap.get(period).get(key));
			}
		}
		
	}
	
	public static void main(String[] args) {
		test();
	}
	
}
