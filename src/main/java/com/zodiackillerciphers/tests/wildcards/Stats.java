package com.zodiackillerciphers.tests.wildcards;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;

public class Stats extends MultiObjectiveStatistics {

	static WildcardCandidate best = null;
	@Override
	public void postEvaluationStatistics(EvolutionState state) {

		// find the best one
		for (Individual ind : state.population.subpops[0].individuals) {
			WildcardCandidate wc = (WildcardCandidate) ind;
			if (best == null) best = wc;
			else {
				if (wc.homophoneScore < best.homophoneScore) {
					best = wc;
					dump(state, best, "NEW BEST");
				}
			}
		}
		
		
		if (state.generation % 200 == 0) {
			super.finalStatistics(state, 0);
			for (Individual ind : state.population.subpops[0].individuals) {
				WildcardCandidate wc = (WildcardCandidate) ind;
				dump(state, wc, "");
			}
		}
		super.postEvaluationStatistics(state);
	}
	public void dump(EvolutionState state, WildcardCandidate wc, String message) {
		String line = message + " generation " + state.generation + " fitness " + dump(wc.fitness) + " assignments " + wc.getAssignments() + " genome " + wc.genotypeToStringForHumans() + " cipher " + wc.getCiphertext();
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
