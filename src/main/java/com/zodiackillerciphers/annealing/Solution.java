package com.zodiackillerciphers.annealing;

public abstract class Solution {
	
	public static boolean DEBUG = false;
	/** a solution can be mutated to explore better/worse solutions */
	public abstract boolean mutate();
	/** the mutation of the solution needs to reversible (for efficiency, so we don't have to clone it) */
	public abstract void mutateReverse();
	/** if a solution is kept for the next iteration, then we can clear its mutation history. */
	public abstract void mutateReverseClear();
	
	/** number of iterations it took to get to this solution */
	public long iterations;
	
	/** the temperature of the algorithm at the time this solution was found */
	public double temperature;

	/** the name of the last mutator used */
	public String lastMutator;
	
	/** a solution can be output */
	public void output() {
		System.out.println(energy() + "	" + representation());
	}
	
	/** a string representation of this solution can be generated */
	public abstract String representation();

	/** a solution has an energy (fitness). lower is better. */
	public abstract double energy();
	/** return cached energy (to avoid recomputation) */
	public abstract double energyCached();

	/** a solution can be initialized */
	public abstract void initialize();

	/**
	 * need to be able to clone a solution when keeping it for the next
	 * iteration
	 */
	//public abstract Solution clone();
	
	public String toString() {
		return energy() + "	" + iterations + "	" + representation(); 
	}
	
	void debug(String msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}
	
}
