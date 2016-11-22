package com.zodiackillerciphers.tests.wordsearch;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.dictionary.BoggleBean;
import com.zodiackillerciphers.dictionary.BogglePuzzle;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;

public class Stats extends MultiObjectiveStatistics {
	public static int[] DIRECTIONS = new int[] {0,1,2,3,5,6,7};
	public static int MIN_PERCENTILE = 60;
	@Override
	public void postEvaluationStatistics(EvolutionState state) {

		if (state.generation % 200 == 0) {
			super.finalStatistics(state, 0);
			/*for (Individual ind : state.population.subpops[0].individuals) {
				CandidateKey key = (CandidateKey) ind;
				dump(state, key);
			}*/
		}
		super.postEvaluationStatistics(state);
	}
	public void dump(EvolutionState state, CandidateKey key) {
		key.expressGenome();
		String line = "generation " + state.generation + " fitness " + dump(key.fitness) + " key " + key + " genome " + key.genotypeToStringForHumans() + " html " + BoggleBean.toHtml(BogglePuzzle.solve(key.grid, false, true, DIRECTIONS, 4, 30, MIN_PERCENTILE));
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
