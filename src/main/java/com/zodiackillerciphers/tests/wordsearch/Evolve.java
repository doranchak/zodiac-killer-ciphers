package com.zodiackillerciphers.tests.wordsearch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;

public class Evolve extends Problem implements SimpleProblemForm {

	
	/*public static Map<String, String> dupes;
	static {
		dupes = new ConcurrentHashMap<String, String>();
	}*/
	
	public synchronized void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {

		//if (CandidateKey.EXIT) return;
		
		CandidateKey key = (CandidateKey) ind;
		key.expressGenome();
		
		double[] score = key.scoreWords(key.search());
		
		//float[] obj = new float[score.length+1];
		//for (int i=0; i<score.length; i++) obj[i] = score[i];
		
		//for (int i=0; i<state.population.subpops[subpopulation].individuals.length; i++) {
		//	CandidateKey key2 = (CandidateKey) state.population.subpops[subpopulation].individuals[i];
		//	obj[obj.length-1] += key.diffs(key2);
		//}
		
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, score);
		ind.evaluated = true;
		
		
		//System.out.println("SMEG scored " + CandidateKey.objectInfo(key) + " fitness " + key.fit() + " fitnessobject " + System.identityHashCode(key.fitness));
		
		/*
		String k = "" + key.hashCode();
		String val = dupes.get(k);
		if (val != null) {
			if (!key.fit().equals(val)) {
				System.err.println("Two different fitnesses for key [" + k + "]: [" + key.fit() + "] [" + val + "]");
				System.exit(-1);
			}
		}
		dupes.put(k, key.fit());*/
	}
}
