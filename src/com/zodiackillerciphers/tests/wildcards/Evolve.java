package com.zodiackillerciphers.tests.wildcards;

import java.util.ArrayList;

import java.util.List;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.FloatVectorIndividual;

public class Evolve extends Problem implements SimpleProblemForm {
	
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		
		WildcardCandidate key = (WildcardCandidate) ind;
		key.expressGenome();
		
		double[] objectives = ((MultiObjectiveFitness) ind.fitness)
				.getObjectives();

		String cipher = key.getCiphertext();
		//float[] m = Measurements.measure(cipher);
		float[] m = new float[] {Measurements.measureHomophonesMinProbPerSymbol(cipher)};
		
		key.homophoneScore = m[0];
		
		for (int i=0; i<m.length; i++) objectives[i] = m[i];
		
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, objectives);
		ind.evaluated = true;
	}
	
	
	public static String toString(Fitness fitness) {
		return toString((MultiObjectiveFitness) fitness);
	}
	public static String toString(MultiObjectiveFitness fitness) {
		String fit = "[";
		for (int i = 0; i < fitness.getNumObjectives(); i++)
			fit += fitness.getObjective(i) + " ";
		return fit + "]";
	}
	
}
