package com.zodiackillerciphers.annealing;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.zodiackillerciphers.annealing.cycles.CycleSolution;
import com.zodiackillerciphers.annealing.homophonic.HomophonicSolution;
import com.zodiackillerciphers.ciphers.Ciphers;

/**
 * http://www.theprojectspot.com/tutorial-post/simulated-annealing-algorithm-for
 * -beginners/6
 */
public class SimulatedAnnealing {
	public static boolean DEBUG = false;
	
	/** if true, reset a target number of iterations whenever a better solution is discovered.
	 * this is to encourage discovery of new best solutions.  
	 */
	public static boolean extendIterations;
	public static int DUMP_INTERVAL = 10000;
	
	public static double EPSILON = 0.00001;
	
	/** if extendIterations is true, this is a factor by which we increase the temperature to encourage
	 * escaping local minima.
	 */
	//public static double temperatureFactor; 

	/** mutation stats.  used to figure out which mutators are most effective.
	 * Long[] contains two items: number of successful mutations, and total mutations.
	 **/
//	public static ConcurrentHashMap<String, Long[]> mutationMap = new ConcurrentHashMap<String, Long[]>(); 	
//	
//	public static void logMutation(String key, boolean successful) {
//		Long[] val = mutationMap.get(key);
//		if (val == null) val = new Long[2];
//		val[0] = 0l; val[1] = 0l;
//		if (successful) val[0]++;
//		val[1]++;
//		mutationMap.put(key, val);
//	}

//	public static void dumpMutationMap() {
//		for (String key : mutationMap.keySet()) {
//			System.out.println(key + ": " + Arrays.toString(mutationMap.get(key)));
//		}
//	}
	
	// Calculate the acceptance probability
	public static double acceptanceProbability(double energy, double newEnergy,
			double temperature, double maxEnergy) {
		double prob = 0;
		// If the new solution is better, accept it
		if (newEnergy < energy) {
			prob = 1.0;
		} else if (newEnergy > maxEnergy) {
			// if we specified a max allowed energy then don't allow the new solution if its energy is worse.
			prob = 0;
		} else {
			// If the new solution is worse, calculate an acceptance probability
			prob = Math.exp((energy - newEnergy) / temperature);
		}
		// System.out.println("prob " + prob + " energy " + energy +
		// " newEnergy "
		// + newEnergy + " diff " + (energy - newEnergy) + " temp "
		// + temperature);
		return prob;
	}

	public static long d() {
		Date d = new Date();
		return d.getTime();
	}
	public static Solution run(double temperature, int targetIterations,
			Solution currentSolution, int threadNum) {
		return run(temperature, targetIterations, currentSolution, threadNum, Double.MAX_VALUE);
	}
	// run the annealing process.  return best solution at end of run.
	// IMPORTANT:  No longer clones solutions to test neighbors.  Therefore, you must make all mutators
	// reversible.  
	public static Solution run(double temperature, int targetIterations,
			Solution currentSolution, int threadNum, double maxEnergy) {
		double coolingRate = coolingRate(temperature, targetIterations, EPSILON);
		System.out.println(d() + "	" + threadNum + "	Running with temperature " + temperature + " and cooling rate " + coolingRate + " and max energy " + maxEnergy);
		int count = 0;
		System.out.println(d() + "	" + threadNum + "	Initial solution energy: "
				+ currentSolution.energy());
		System.out.println(d() + "	" + threadNum + "	Initial solution representation: "
			+ currentSolution.representation());
		double bestEnergy = currentSolution.energy();
		currentSolution.temperature = temperature;
		// Loop until system has cooled
		while (temperature > 1) {
			count++;
			currentSolution.iterations++;
			if (count % DUMP_INTERVAL == 0) {
				System.out.println(d() + "	" + threadNum + "	Current temperature: " + temperature
						+ ", count: " + count + ", solution " + currentSolution);
			}
			double currentEnergy = currentSolution.energy();
			try {
				currentSolution.mutate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			double neighborEnergy = currentSolution.energy();
//			String pt1 = ((HomophonicSolution)currentSolution).plaintext;
//			String m1 = currentSolution.toString();
			// Decide if we should accept the neighbor
			double prob = acceptanceProbability(currentEnergy, neighborEnergy,
					temperature, maxEnergy);
			double roll = Math.random();
			debug("prob " + prob + " roll " + roll + " current " + currentEnergy + " new "
					+ neighborEnergy + " temp " + temperature);
			if (prob > roll) { // keep neighbor solution (the mutated one)
//				System.out.println("kept better: " + currentSolution);
//				if (neighborEnergy > currentEnergy) {
//					debug("KEPT WORSE");
//				} else
//					debug("KEPT BETTER");
				currentSolution.mutateReverseClear();
			} else { // keep current solution (the non-mutated one).
				debug("KEPT CURRENT");
				// reverse the mutation.  that way we don't have to rely on inefficient clones.
				currentSolution.mutateReverse();
//				System.out.println("kept current: " + currentSolution);

			}
			boolean better = false;
			// Keep track of the best solution found
			if (neighborEnergy < bestEnergy) {
				bestEnergy = neighborEnergy;
//				debug(currentSolution.energy() + " < "
//						+ best.energy());
				// System.out.println("cloning best");
//				best = currentSolution.clone();
//				best.temperature = temperature;
				System.out.println(d() + "	" + threadNum + "	New best:	" + currentSolution);
				better = true;
//				System.out.println(d() + "	" + threadNum + "	Last mutation: " + currentSolution.lastMutator);
				
//				if (currentSolution instanceof HomophonicSolution) {
//					if (((HomophonicSolution)currentSolution).percentMatchesZ408Solution() >= 80) {
//						return currentSolution;
//					}
//				}
				
				if (extendIterations) {
					coolingRate = coolingRate(temperature, targetIterations, EPSILON);
					System.out.println(d() + "	" + threadNum + "	Extending iterations using new cooling rate: " + coolingRate);
				}
				
				// System.out.println("Best energy: " + best.energy());
				// System.out.println("Best energy: " + best.energy());

				// System.out.println("== BEST1: " + best.hashCode() + ", " +
				// best);
				// System.out.println("== CURR1: " + best);
			}
//			logMutation(currentSolution.lastMutator, better);
			// Cool system
			temperature *= 1 - coolingRate;
			// System.out.println("New temp: " + temperature);
		}
		System.out.println(d() + "	" + threadNum + "	Final solution: " + currentSolution);
		return currentSolution;
	}

	public static void test1() {
		while (true) {
			NumberWordsSolution sol = new NumberWordsSolution();
			sol.setCipher(Ciphers.cipher[0].cipher);
			run(2, 10000, sol, 1);
		}
	}
	
	/**
	 * determine a new cooling rate that will achieve the target number of
	 * iterations
	 */
	public static double coolingRate(double temperature, int targetIterations, double epsilon) {
		// 
		// f(r) = T(1-r)^I - 1 = 0
		// Solve for r for given constants T and I

		double rmin = 0;
		double rmax = 0.5;
		double rcurrent = 0.0001;
		boolean go = true;
		
		double f;
		int count = 0;
		while (go) {
			f = temperature*Math.pow(1-rcurrent, targetIterations) - 1;
			debug("current " + rcurrent + " f " + f + " rmin " + rmin + " rmax " + rmax);
			if (Math.abs(f) <= epsilon) {
				go = false;
			}
			if (f > 0) {
				// need to make r bigger
				rmin = rcurrent;
				rcurrent = (rcurrent + rmax) / 2;
			} else if (f < 0) {
				// need to make r smaller
				rmax = rcurrent;
				rcurrent = (rcurrent + rmin) / 2;
			}
			count++;
			if (count == 100) break;
		}
		return rcurrent;
		
	}

	public static void test2() {
		while (true) {
			NumberWordsContiguousSolution sol = new NumberWordsContiguousSolution();
			sol.setCipher(Ciphers.cipher[0].cipher);
			run(2, 1000, sol, 1);
		}
	}

	public static void testProb() {
		double e1 = 30.710289465469437;
		double e2 = 30.809462710253047;
		for (double t = 3; t >= 0; t -= 0.05) {
			System.out.println(acceptanceProbability(e1, e2, t, Double.MAX_VALUE));
		}
	}

	public static void testCraigishZ340() {
		while (true) {
			CraigishZ340Solution sol = new CraigishZ340Solution();
			sol.setCipher(new StringBuffer(Ciphers.cipher[0].cipher));
			run(2, 1000, sol, 1);
		}
	}
	/** brute force */
	public static void testCraigishZ340_2() {
		String tab = "	";
		CraigishZ340Solution sol = new CraigishZ340Solution();
		sol.initialize();
		System.out.println("tokens: " + sol.tokensOriginal.size());
		for (int i=0; i<sol.tokensOriginal.size(); i++) {
			if (i % 100 == 0) System.out.println("Token #" + i + "...");
			for (int c=0; c<=200; c++) {
				sol = new CraigishZ340Solution();
				sol.setCipher(new StringBuffer(Ciphers.cipher[0].cipher));
				sol.initialize();
				sol.startingToken = i;
				sol.cipherStartPosition = c;
				double energy = sol.energy();
				if (energy < 0.7)
					System.out.println(energy + tab + sol.plaintextResult + tab + sol.operations);
			}
		}
	}
	public static void debug(String msg) {
		if (DEBUG) System.out.println(msg);
	}
	
	public static void testCoolingRate() {
		for (int temp = 10000; temp >= 1; temp /= 10) {
			for (int iterations = 10; iterations <= 1000000; iterations *= 10) {
				System.out.println(temp + " " + iterations + " " + coolingRate(temp, iterations, 0.000001));
			}
		}
	}
	public static void main(String[] args) {
		// test1();
		//testCraigishZ340();
		//testCraigishZ340_2();
		//testCoolingRate();
		
//		double t = 10000;
//		double f = t*Math.pow(1d-0.0001d,100000) - 1;
//		System.out.println(f);
		
		//System.out.println(coolingRate(10000, 100000, 0.00001));
		
		// testProb();
	}
}
