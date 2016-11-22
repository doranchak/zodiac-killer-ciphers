package com.zodiackillerciphers.ciphers.generator;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;

public class EvolveOlson extends Problem implements SimpleProblemForm {
	
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		
		CandidateKey key = (CandidateKey) ind;
		if (key.isBroken()) {
			System.err.println("WTF: key is bad " + key);
			System.exit(-1);
		}
		key.expressGenome();
		
		double[] objectives = ((MultiObjectiveFitness) ind.fitness)
				.getObjectives();
		
		objectives[0] = key.measureOlson1(); 
		objectives[1] = key.measureOlson2();  
				
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, objectives);
		ind.evaluated = true;
	}

}
