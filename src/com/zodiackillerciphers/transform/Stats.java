package com.zodiackillerciphers.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;

public class Stats extends MultiObjectiveStatistics {

	public List<float[]> previousGenElites;
	public List<String> previousGenKeys;
	
	public static int GENS_PER_DUMP = 500;
	
	@Override
	public void postEvaluationStatistics(EvolutionState state) {

		if (state.generation % GENS_PER_DUMP == 0) {
			super.finalStatistics(state, 0);
			for (Individual ind : state.population.subpops[0].individuals) {
				if (ind instanceof Operations) {
					Operations ops = (Operations) ind;
					dump(state, ops);
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
	
	public void dump(EvolutionState state, Operations ops) {
		String line = "generation " + state.generation + " " + ops; 
		System.out.println(line);
	}
	
	public String dump(Fitness fitness) {
		double[] f = ((MultiObjectiveFitness)fitness).getObjectives();
		String line = "";
		for (int i=0; i<f.length; i++) {
			line += f[i];
			if (i < f.length - 1) line += " ";
		}
		return line;
	}

}
