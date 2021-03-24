package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;

public class Stats extends MultiObjectiveStatistics {
	
	/** list of candidates we've already processed, so we don't reuse them in subsequent runs. */
	public static String TABOO = "/Users/doranchak/projects/zodiac/myszkowski-taboo.txt";
	public static int LAST_GENERATION = 10000;
	
	public List<float[]> previousGenElites;
	public List<String> previousGenKeys;
	
	@Override
	public void postEvaluationStatistics(EvolutionState state) {

		boolean found = false;
		if (state.generation % 100 == 0 || state.generation == 199999) {
		//if (1 != 1) {
			double bestDist = Double.MAX_VALUE;
			double sum = 0;
			double dist;
			super.finalStatistics(state, 0);
			for (Individual ind : state.population.subpops[0].individuals) {
				if (ind instanceof CandidateKey) {
					CandidateKey key = (CandidateKey) ind;
					boolean dump = dump(state, key);
					found = found || dump;
					dist = key.distanceFromZero();
					bestDist = Math.min(bestDist, dist);
					sum += dist; 
				}
			}
			System.out.println("Gen " + state.generation + " best distance " + bestDist + " mean " + (sum/state.population.subpops[0].individuals.length));
		}
		
		//checkElites(state);
		super.postEvaluationStatistics(state);
		
		if (state.generation == LAST_GENERATION) {
			updateTaboo(state);
		}
		
		if (found) {
			System.out.println("FOUND AN ALMOST PERFECT SOLUTION");
			updateTaboo(state);
			System.exit(0);
		}
		

	}
	String dump(float[] ff) {
		String s = "";
		for (float f : ff) s += f + " ";
		return s;
	}
	void updateTaboo(EvolutionState state) {
		Set<String> set = new HashSet<String>();
		set.addAll(FileUtil.loadFrom(TABOO));
		for (String line : set) System.out.println("Previous taboo list: " + line);
		
		for (Individual ind : state.population.subpops[0].individuals) {
			if (ind instanceof CandidateKey) {
				CandidateKey key = (CandidateKey) ind;
				String plain = key.plain.plaintext;
				if (set.contains(plain)) continue;
				double dist = key.distanceFromZero();
				if (dist < 3) {
					System.out.println("Close to goal: " + dist + " " + plain);
					set.add(plain);
				} else {
					System.out.println("Too far from goal: " + plain);
				}
			}
		}
		for (String line : set) System.out.println("Current taboo list: " + line);
		FileUtil.writeText(TABOO, set);
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
	
	public boolean dump(EvolutionState state, CandidateKey key) {
		double z = key.distanceFromZero();
		String line = "generation " + state.generation + " fitness " + dump(key.fitness) + " distFromOrigin " + z + " key " + key + " genome " + key.genotypeToStringForHumans();
		System.out.println(line);
		return z < 1;
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
