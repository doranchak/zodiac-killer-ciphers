package com.zodiackillerciphers.transform.columnar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;
import ec.vector.FloatVectorIndividual;

public class Stats extends MultiObjectiveStatistics {

	public List<float[]> previousGenElites;
	public List<String> previousGenKeys;
	
	public static int GENS_PER_DUMP = 500;
	
	@Override
	public void postEvaluationStatistics(EvolutionState state) {

		if (state.generation % GENS_PER_DUMP == 0) {
			super.finalStatistics(state, 0);
			for (Individual ind : state.population.subpops[0].individuals) {
				if (ind instanceof FloatVectorIndividual) {
					FloatVectorIndividual fvi = (FloatVectorIndividual) ind;
					dump(state, fvi);
				}
			}
		}		
		//checkElites(state);
		super.postEvaluationStatistics(state);
	}
	String dump(float[] ff) {
		String s = "";
		for (float f : ff) s += f + " ";
		return s;
	}
	/*
	void checkElites(EvolutionState state) {
			List<float[]> newList = new ArrayList<float[]>();
			List<String> newKeys = new ArrayList<String>();
			// raise an error if one of the current elites is dominated by an elite from the previous generation
			for (Individual ind : state.population.subpops[0].individuals) {
				CandidateKey key = (CandidateKey) ind;
				if (key.archive) {
					if (previousGenElites != null) {
						for (float[] objectives: previousGenElites) {
							if (key.isDominatedBy(objectives)) {
								System.err.println("ERROR!  LOST AN ELITE");
								System.err.println("ITS FITNESS WAS: " + dump(objectives));
								System.err.println("WHICH DOMINATES CURRENT ARCHIVE MEMBER: " + Arrays.toString(((MultiObjectiveFitness)key.fitness).objectives));
								for (String s: previousGenKeys) System.err.println(s);
								System.exit(-1);
							}
						}
					}
					newList.add(((MultiObjectiveFitness) key.fitness).objectives.clone());
					newKeys.add(key.toString());
				}
			}
			previousGenElites = newList;
			previousGenKeys = newKeys;
			
	}*/
	
	public void dump(EvolutionState state, FloatVectorIndividual ind) {
		Transposition t = new Transposition(ind.genome);
		String line = "generation " + state.generation + " " + t + " " + t.decode(Evolve.CIPHER) + " " + ind.fitness.fitnessToStringForHumans(); 
		System.out.println(line);
	}
}
