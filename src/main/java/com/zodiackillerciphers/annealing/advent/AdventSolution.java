package com.zodiackillerciphers.annealing.advent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.annealing.cycles.CycleSolution;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.HomophonesResultBean;
/** https://blog.jgc.org/2018/12/the-search-for-perfect-advent-calendar.html */
public class AdventSolution extends Solution {

	/** pool of numbers to init with */
	public int[] pool = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};
	
	/** the calendar (4 rows by 6 columns) */
	public int[][] calendar;
	
	/** pre-mutated calendar */
	public int[][] preMutated;

	Random random = new Random();
	
	double score;

	public AdventSolution() {
		iterations = 0;
	}
	
	@Override
	public boolean mutate() {
		// randomly swap a random number of times
		preMutated = new int[4][6];
		for (int y=0; y<4; y++) 
			for (int x=0; x<6; x++) 
				preMutated[y][x] = calendar[y][x];
		for (int i = 0; i < random.nextInt(5) + 1; i++) {
			swap();
		}
		return true;
	}

	@Override
	public void mutateReverse() {
		calendar = preMutated;
	}
	@Override
	public void mutateReverseClear() {
		preMutated = null;
	}
	
	public void swap() {
		int y1 = random.nextInt(4);
		int x1 = random.nextInt(6);
		int y2 = y1;
		int x2 = x1;
		while (x1 == x2 && y1 == y2) {
			y2 = random.nextInt(4);
			x2 = random.nextInt(6);
		}
		int tmp = calendar[y1][x1];
		calendar[y1][x1] = calendar[y2][x2];
		calendar[y2][x2] = tmp;
	}
	
	@Override
	public String representation() {
		String result = score + " [";
		for (int y=0; y<calendar.length; y++) {
			for (int x=0; x<calendar[y].length; x++) {
				result += calendar[y][x] + " ";
			}
		}
		result += "]";
		return result;
	}
	
	@Override
	public double energy() {
		double max = Math.sqrt(5*5+3*3);
		double sum = 0;
		for (int y1=0; y1<4; y1++) {
			for (int x1=0; x1<6; x1++) {
				int i = calendar[y1][x1];
				if (i == 0) continue;
				// compare to every other number
				for (int y2=0; y2<4; y2++) {
					for (int x2=0; x2<6; x2++) {
						if (x1 == x2 && y1 == y2) continue;
						int j = calendar[y2][x2];
						if (j == 0) continue;
						sum += (max-Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)))/Math.abs(i-j);
					}
				}				
			}
		}
		score = sum;
		return score;
	}

	@Override
	public double energyCached() {
		return score;
	}

	@Override
	public void initialize() {
		// randomly pick a number from [1,24]
		// place it in the spot that minimizes the energy function
		// if more than one spot is at minimum, randomly choose one of the spots
		// repeat until all spots filled.
		
		calendar = new int[4][6];
		for (int i=pool.length-1; i>=1; i--) {
			int j = random.nextInt(i+1);
			if (i==j) continue;
			int tmp = pool[i];
			pool[i] = pool[j];
			pool[j] = tmp;
		}
		
		for (int i=0; i<pool.length; i++) {
			// get value we are gonna place
			int value = pool[i];
			// generate a list of coordinates for each energy value found 
			Map<Double, List<String>> coords = new HashMap<Double, List<String>>();
			double minEnergy = Double.MAX_VALUE;
			for (int y=0; y<4; y++) {
				for (int x=0; x<6; x++) {
					if (calendar[y][x] == 0) {
						calendar[y][x] = value;
						double key = energy();
						List<String> list = coords.get(key);
						if (list == null) list = new ArrayList<String>();
						list.add(y + " " + x);
						coords.put(key, list);
						calendar[y][x] = 0;
						minEnergy = Math.min(minEnergy, key);
					}
				}
			}
			// get coords for best energy
			List<String> list = coords.get(minEnergy);
			String yx = list.get(random.nextInt(list.size()));
//			System.out.println("i " + i + " coords " + coords + " minEnergy " + minEnergy + " yx " + yx);
			String[] split = yx.split(" ");
			int y = Integer.valueOf(split[0]);
			int x = Integer.valueOf(split[1]);
			calendar[y][x] = value;
			energy();
			System.out.println("Greedy init: " + representation());
		}
	}
	public void initializeFromBest() {
		
		String[] bests = new String[] {
				"15 9 17 7 19 12 4 22 2 23 14 5 20 11 24 1 3 21 13 6 18 8 16 10", // 376.2564055998211
				"10 16 8 18 6 13 21 3 23 2 11 20 5 14 1 24 22 4 12 19 7 17 9 15", // 376.2564055998211
				"13 6 18 8 16 10 20 11 2 23 3 21 4 22 24 1 14 5 15 9 17 7 19 12", // 376.25640559982105
				"12 19 7 17 9 15 5 14 23 2 22 4 21 3 1 24 11 20 10 16 8 18 6 13", // 376.25640559982105
				"13 8 18 6 11 16 20 3 1 23 4 21 15 22 24 12 2 9 10 5 17 7 19 14", // 376.2553225442494
				"12 17 7 19 14 9 5 22 24 2 21 4 10 3 1 13 23 16 15 20 8 18 6 11", // 376.2553225442494
				"9 14 19 7 17 12 4 21 2 24 22 5 16 23 13 1 3 10 11 6 18 8 20 15", // 376.25532254424934
				"16 11 6 18 8 13 21 4 23 1 3 20 9 2 12 24 22 15 14 19 7 17 5 10", // 376.25532254424934
				"15 20 8 18 6 11 10 3 1 13 23 16 5 22 24 2 21 4 12 17 7 19 14 9", // 376.2553225442488
				"14 19 7 17 5 10 9 2 12 24 22 15 21 4 23 1 3 20 16 11 6 18 8 13", // 376.2553225442488
				"11 6 18 8 20 15 16 23 13 1 3 10 4 21 2 24 22 5 9 14 19 7 17 12", // 376.2553225442488
				"10 5 17 7 19 14 15 22 24 12 2 9 20 3 1 23 4 21 13 8 18 6 11 16", // 376.2553225442488
				"9 14 19 7 12 17 4 21 2 24 22 5 16 23 13 1 3 10 11 6 18 8 20 15", // 376.2458102463137
				"16 11 6 18 13 8 21 4 23 1 3 20 9 2 12 24 22 15 14 19 7 17 5 10", // 376.2458102463137
				"8 13 18 6 11 16 20 3 1 23 4 21 15 22 24 12 2 9 10 5 17 7 19 14", // 376.2458102463136
				"17 12 7 19 14 9 5 22 24 2 21 4 10 3 1 13 23 16 15 20 8 18 6 11", // 376.2458102463136
				"14 19 7 17 5 10 9 2 12 24 22 15 21 4 23 1 3 20 16 11 6 18 13 8", // 376.2458102463133
				"11 6 18 8 20 15 16 23 13 1 3 10 4 21 2 24 22 5 9 14 19 7 12 17", // 376.2458102463133
				"15 20 8 18 6 11 10 3 1 13 23 16 5 22 24 2 21 4 17 12 7 19 14 9", // 376.24581024631317
				"10 5 17 7 19 14 15 22 24 12 2 9 20 3 1 23 4 21 8 13 18 6 11 16", // 376.24581024631317
				"9 20 7 18 5 12 13 15 2 23 10 16 4 22 24 1 3 21 17 11 6 19 14 8", // 376.20347874806936
				"16 5 18 7 20 13 12 10 23 2 15 9 21 3 1 24 22 4 8 14 19 6 11 17", // 376.20347874806936
				"13 20 7 18 5 16 9 15 2 23 10 12 4 22 24 1 3 21 17 11 6 19 14 8", // 376.2034787480693
				"12 5 18 7 20 9 16 10 23 2 15 13 21 3 1 24 22 4 8 14 19 6 11 17", // 376.2034787480693
				"8 14 19 6 11 17 21 3 1 24 22 4 12 10 23 2 15 9 16 5 18 7 20 13", // 376.2034787480689
				"17 11 6 19 14 8 4 22 24 1 3 21 13 15 2 23 10 16 9 20 7 18 5 12", // 376.2034787480689
				"8 14 19 6 11 17 21 3 1 24 22 4 16 10 23 2 15 13 12 5 18 7 20 9", // 376.20347874806873
				"17 11 6 19 14 8 4 22 24 1 3 21 9 15 2 23 10 12 13 20 7 18 5 16", // 376.20347874806873
				"15 20 7 18 5 10 9 13 2 23 12 16 4 22 24 1 3 21 17 11 6 19 14 8", // 376.19293389382284
				"10 5 18 7 20 15 16 12 23 2 13 9 21 3 1 24 22 4 8 14 19 6 11 17", // 376.19293389382284
				"8 14 19 6 11 17 21 3 1 24 22 4 16 12 23 2 13 9 10 5 18 7 20 15", // 376.19293389382267
				"17 11 6 19 14 8 4 22 24 1 3 21 9 13 2 23 12 16 15 20 7 18 5 10", // 376.19293389382267
				"13 18 6 20 4 15 8 23 1 11 22 9 16 3 14 24 2 17 10 21 5 19 7 12", // 376.1437057021058
				"12 7 19 5 21 10 17 2 24 14 3 16 9 22 11 1 23 8 15 4 20 6 18 13", // 376.1437057021058
				"15 4 20 6 18 13 9 22 11 1 23 8 17 2 24 14 3 16 12 7 19 5 21 10", // 376.14370570210576
				"10 21 5 19 7 12 16 3 14 24 2 17 8 23 1 11 22 9 13 18 6 20 4 15", // 376.14370570210576
				"9 15 4 20 6 12 17 22 11 1 23 18 7 2 24 14 3 8 13 19 5 21 10 16", // 376.0750039489986
				"16 10 21 5 19 13 8 3 14 24 2 7 18 23 1 11 22 17 12 6 20 4 15 9", // 376.0750039489986
				"13 19 5 21 10 16 7 2 24 14 3 8 17 22 11 1 23 18 9 15 4 20 6 12", // 376.07500394899824
				"12 6 20 4 15 9 18 23 1 11 22 17 8 3 14 24 2 7 16 10 21 5 19 13", // 376.07500394899824
				"13 20 7 18 5 12 9 15 2 23 10 16 4 22 24 1 3 21 17 11 6 19 14 8", // 376.06703699259884
				"12 5 18 7 20 13 16 10 23 2 15 9 21 3 1 24 22 4 8 14 19 6 11 17", // 376.06703699259884
				"8 14 19 6 11 17 21 3 1 24 22 4 16 10 23 2 15 9 12 5 18 7 20 13", // 376.0670369925985
				"17 11 6 19 14 8 4 22 24 1 3 21 9 15 2 23 10 16 13 20 7 18 5 12", // 376.0670369925985
				"15 20 7 18 5 12 9 13 2 23 10 16 4 22 24 1 3 21 11 17 6 19 14 8", // 376.0653430930989
				"10 5 18 7 20 13 16 12 23 2 15 9 21 3 1 24 22 4 14 8 19 6 11 17", // 376.0653430930989
				"13 20 7 18 5 10 9 15 2 23 12 16 4 22 24 1 3 21 17 11 6 19 8 14", // 376.06534309309876
				"12 5 18 7 20 15 16 10 23 2 13 9 21 3 1 24 22 4 8 14 19 6 17 11", // 376.06534309309876
				"8 14 19 6 17 11 21 3 1 24 22 4 16 10 23 2 13 9 12 5 18 7 20 15", // 376.06534309309825
				"17 11 6 19 8 14 4 22 24 1 3 21 9 15 2 23 12 16 13 20 7 18 5 10", // 376.06534309309825
				"14 8 19 6 11 17 21 3 1 24 22 4 16 12 23 2 15 9 10 5 18 7 20 13", // 376.06534309309825
				"11 17 6 19 14 8 4 22 24 1 3 21 9 13 2 23 10 16 15 20 7 18 5 12", // 376.06534309309825
				"15 20 7 18 5 12 9 13 2 23 10 16 4 22 24 1 3 21 17 11 6 19 14 8", // 375.9986727758853
				"13 20 7 18 5 10 9 15 2 23 12 16 4 22 24 1 3 21 17 11 6 19 14 8", // 375.9986727758853
				"12 5 18 7 20 15 16 10 23 2 13 9 21 3 1 24 22 4 8 14 19 6 11 17", // 375.9986727758853
				"10 5 18 7 20 13 16 12 23 2 15 9 21 3 1 24 22 4 8 14 19 6 11 17", // 375.9986727758853
				"8 14 19 6 11 17 21 3 1 24 22 4 16 12 23 2 15 9 10 5 18 7 20 13", // 375.9986727758851
				"17 11 6 19 14 8 4 22 24 1 3 21 9 13 2 23 10 16 15 20 7 18 5 12", // 375.9986727758851
				"8 14 19 6 11 17 21 3 1 24 22 4 16 10 23 2 13 9 12 5 18 7 20 15", // 375.998672775885
				"17 11 6 19 14 8 4 22 24 1 3 21 9 15 2 23 12 16 13 20 7 18 5 10", // 375.998672775885
				"8 20 13 6 18 11 15 3 1 23 16 4 10 22 24 2 9 21 17 5 12 19 7 14", // 375.9238099710736
				"17 5 12 19 7 14 10 22 24 2 9 21 15 3 1 23 16 4 8 20 13 6 18 11", // 375.9238099710736
				"14 7 19 12 5 17 21 9 2 24 22 10 4 16 23 1 3 15 11 18 6 13 20 8", // 375.92380997107347
				"11 18 6 13 20 8 4 16 23 1 3 15 21 9 2 24 22 10 14 7 19 12 5 17", // 375.92380997107347
		};
		
//		int[][][] bests = new int[][][] {
//				// 375.92380997107347
//				{ { 11, 18, 6, 13, 20, 8 }, { 4, 16, 23, 1, 3, 15 }, { 21, 9, 2, 24, 22, 10 },
//						{ 14, 7, 19, 12, 5, 17 } },
//				// 376.2458102463136
//				{ { 17, 12, 7, 19, 14, 9 }, { 5, 22, 24, 2, 21, 4 }, { 10, 3, 1, 13, 23, 16 },
//						{ 15, 20, 8, 18, 6, 11 } },
//				{ { 17, 12, 20, 10, 15, 6 }, { 7, 22, 3, 1, 23, 8 }, { 14, 5, 24, 16, 4, 18 },
//						{ 9, 19, 2, 11, 21, 13 } },
//				{ { 14, 22, 20, 11, 6, 17 }, { 9, 2, 4, 15, 23, 8 }, { 18, 12, 24, 1, 3, 19 },
//						{ 7, 16, 5, 21, 10, 13 } },
//				{ { 11, 3, 16, 8, 6, 13 }, { 18, 22, 1, 21, 15, 19 }, { 5, 9, 24, 2, 23, 4 },
//						{ 14, 20, 7, 12, 17, 10 } },
//				{ { 19, 6, 14, 9, 3, 16 }, { 12, 23, 1, 22, 20, 11 }, { 4, 17, 10, 24, 5, 7 },
//						{ 8, 21, 15, 2, 13, 18 } },
//				{ { 13, 18, 9, 15, 20, 7 }, { 21, 6, 2, 24, 3, 12 }, { 4, 16, 23, 1, 22, 10 },
//						{ 11, 8, 19, 14, 5, 17 } },
//				{ { 7, 16, 5, 10, 21, 13 }, { 12, 18, 23, 2, 15, 8 }, { 20, 3, 1, 24, 19, 4 },
//						{ 9, 14, 22, 6, 17, 11 } },
//				{ { 8, 15, 10, 5, 18, 12 }, { 13, 3, 21, 24, 2, 20 }, { 17, 23, 1, 22, 14, 7 },
//						{ 6, 19, 11, 4, 9, 16 } },
//				{ { 16, 6, 14, 19, 21, 12 }, { 9, 2, 24, 8, 3, 5 }, { 22, 18, 11, 1, 23, 17 },
//						{ 13, 4, 20, 7, 15, 10 } },
//				{ { 16, 6, 14, 19, 21, 12 }, { 9, 2, 24, 8, 3, 5 }, { 18, 22, 11, 1, 23, 17 },
//						{ 13, 4, 20, 7, 15, 10 } },
//				{ { 8, 16, 10, 5, 21, 13 }, { 18, 2, 24, 15, 19, 7 }, { 12, 22, 4, 1, 23, 3 },
//						{ 6, 20, 14, 9, 17, 11 } } };
		
//		calendar = bests[random.nextInt(bests.length)];
		calendar = new int[4][6];
		int which = random.nextInt(bests.length);
		String str = bests[which];
		String[] split = str.split(" ");
		System.out.println("Selected for init: " + str);
		for (int i=0; i<split.length; i++) {
			int y = i/6;
			int x = i%6;
			calendar[y][x] = Integer.valueOf(split[i]);
		}
	}
	public void initializeRandom() {
		calendar = new int[4][6];
		for (int i=pool.length-1; i>=1; i--) {
			int j = random.nextInt(i+1);
			if (i==j) continue;
			int tmp = pool[i];
			pool[i] = pool[j];
			pool[j] = tmp;
		}
		System.out.println("shuffled: " + Arrays.toString(pool));
		for (int y=0; y<4; y++) 
			for (int x=0; x<6; x++)
				calendar[y][x] = pool[y*6+x];
	}
	
	public String toString() {
		return representation();
	}

	public static void go() {
		AdventSolution sol = new AdventSolution();
		sol.initialize();
		boolean go = true;
		double bestScore = Double.MAX_VALUE;
		double temperature = 10000;
		SimulatedAnnealing.extendIterations = true;
		sol = (AdventSolution) SimulatedAnnealing.run(temperature, 10000000, sol, 0, sol.energy());
		
	}
	public static void main(String[] args) {
//		AdventSolution a = new AdventSolution();
//		a.initialize();
//		System.out.println(a.representation());
//		for (int i=0; i<10; i++) {
//			a.mutate();
//			System.out.println("mutated: " + a.representation());
//			a.mutateReverse();
//			System.out.println("   undo: " + a.representation());
//		}
//		System.out.println(a.energy());
		go();
	}

}
