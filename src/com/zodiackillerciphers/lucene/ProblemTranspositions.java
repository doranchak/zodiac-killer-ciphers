package com.zodiackillerciphers.lucene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;
import ec.simple.SimpleProblemForm;

public class ProblemTranspositions extends Problem implements SimpleProblemForm {

	/** the optima tabu */
	public static List<int[]> tabu = new ArrayList<int[]>(Settings.MAX_TABU);
	
	/** keys in the current pareto front */
	public static Set<String> paretoKeys = new HashSet<String>();
	
	/** the temp tabu (list of key strings) */
	public static List<String> tabuTempList = new ArrayList<String>(Settings.MAX_TABU_TEMP); // keep a list to make it easier to do random replacement 
	public static Set<String> tabuTemp = new HashSet<String>(Settings.MAX_TABU_TEMP); // a hash set for quick lookups
	public static long tabuTempHits = 0;
	public static long tabuTempMisses = 0;
	public static long paretoHits = 0;
	public static long paretoMisses = 0;
	public static synchronized void tempHit() { tabuTempHits++; }
	public static synchronized void tempMiss() { tabuTempMisses++; }
	public static synchronized void paretoHit() { paretoHits++; }
	public static synchronized void paretoMiss() { paretoMisses++; }
	public static boolean tabuSay = true;
	
	
	
	public ProblemTranspositions() {
		init();
	}
	
	public void init() {
		
	}
	
	/** measure the non-similarity of this genome to others in the population, or others in the tabu list */
	public float nonSimilarity(int[] genome, int sensitivity, EvolutionState state) {
		if (sensitivity < 1) throw new RuntimeException("Cannot be less than one.");
		int sum = 0; int pop = 0;
		
		// make list of genomes to compare this one against.
		// if tabu list empty, take genomes from current pop.
		// if tabu list contains items, take genomes from tabu list.
		List<int[]> genomes = new ArrayList<int[]>();
		if (tabu.isEmpty()) {
			if (tabuSay) {
				System.out.println("NOT USING TABU");
			}
			for (Individual ind : state.population.subpops[0].individuals) {
				TranspositionVectorIndividual vec = (TranspositionVectorIndividual) ind;
				int[] genome2 = vec.genome;
				genomes.add(genome2);
			}
		} else {
			if (tabuSay) {
				System.out.println("USING TABU");
			}
			for (int[] g : tabu) genomes.add(g);
		}
		
		if (tabuSay) tabuSay = false;
		
		
		for (int[] genome2 : genomes) {
			//TranspositionVectorIndividual vec = (TranspositionVectorIndividual) ind;
			//int[] genome2 = vec.genome;
			int sumThis = 0;
			if (sensitivity > genome.length) sumThis = 0;
			else {
				for (int i=0; i<genome.length; i+=sensitivity) {
					boolean same = true;
					int thisCount = 0;
					for (int j=0; j<sensitivity && i+j<genome.length; j++) {
						if (genome[i+j] != genome2[i+j]) {
							same = false;
						}
						thisCount++;
					}
					if (same) sumThis += thisCount;
					//if (genome[i] == genome2[i]) sumThis++; // count similarity
					if (sumThis > genome.length / 4) { // if more than 25% match, then consider the whole thing matched.
						sumThis = genome.length;
						break;
					}
				}
			}
			pop += genome.length;
			sum += genome.length - sumThis;
		}
		//System.out.println("sum " + sum + " pop " + pop);
		return ((float)sum)/pop;
	}

	/** TODO: this is just a stub to make this compile.  where is the real keyFrom? */
	StringBuffer keyFrom(int[] genome) {
		return new StringBuffer();
	}
	
	/** multiobjective evaluator */
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		try {
			TranspositionVectorIndividual vec = (TranspositionVectorIndividual) ind;
			int[] genome = vec.genome;
			StringBuffer decoder = keyFrom(genome);
			Map<Character, Character> dm = Scorer.decoderMap(decoder);
			StringBuffer decoded = Scorer.decode(dm);
			
			//int nonwilds = Scorer.nonWilds(decoded);
			
			double[] scores = new double[Settings.NUM_OBJECTIVES];
			//scores[0] = norm(NGrams.zkscore(decoded), 1000);
			//scores[0] = NGrams.zkscore(decoded);
			/*scores[0] *= (1.05f - 5*Stats.iocDiff(decoded));
			scores[0] *= (1.05f - 5*Stats.entropyDiff(decoded)/150.0);
			scores[0] *= (1.05f - 5*Stats.chi2Diff(decoded)/60.0);*/
			
			// don't bother if we've got this decoder in the temp tabu
			String ds = decoder.toString();
			
			if (paretoKeys.contains(ds)) {
				paretoHit();
			} else if (tabuTemp.contains(ds)) {
				tempHit();
				paretoMiss();
				for (int i=0; i<Settings.NUM_OBJECTIVES; i++) {
					scores[i] = 0;
				}
		        ((MultiObjectiveFitness)ind.fitness).setObjectives(state, scores);
		        ind.evaluated = true;
		        return;
			}
			tempMiss();
			
			scores[0] = NGrams.zkscore(decoded);
			scores[1] = 1/(1+Stats.iocDiff(decoded));
			//scores[2] = 1/(1+Stats.entropyDiff(decoded));
			//scores[3] = 1/(1+Stats.chi2Diff(decoded));
			
			//scores[0] = 1/(1+NGrams.ngraphDistance(1, decoded)[0]);
			//float[] ng = NGrams.ngraphDistance(2, decoded);
			//scores[1] = 1/(1+ng[0]*(ng[1]+1));
			//float iocd = 1/(1+Stats.iocDiff(decoded));
			//scores[0] *= iocd*iocd*iocd;
			//scores[1] = 1/(1+Stats.iocDiff(decoded));
			//scores[2] = norm(Math.max(0, decoded.length() - NGrams.diffs(decoded)), 100);
			//float bad = 0;
			//for (int i=2; i<6; i++) bad += NGrams.badNgraphs(i, decoded); 
			//scores[1] = 1/(1+bad); 
			
			int MAX = 15;
			boolean[] searchResult = { false };
			int[] nodeCounter = { 0 };
			int[] nodeCounterArray = new int[MAX];
			String[] m = new String[MAX];
			int[] wordCount = { 0 };
			
			//scores[2] = Cwg.score(cwg, decoded, searchResult, m, nodeCounter, wordCount, nodeCounterArray);
			
			
			//int sensitivity = (int) (((float)state.generation)/GEN_SCALE*genome.length + 1);
			MultiObjectiveStatistics stat = (MultiObjectiveStatistics) state.statistics;
			int sensitivity = 0; // TODO: the below didn't compile so just forced this 
			//int sensitivity = (int) (((float)stat.gensSinceReset)/Settings.GEN_SCALE*genome.length + 1);
			scores[2] = nonSimilarity(genome, sensitivity, state);

			// punish objectives based on iocDiff
			/*
			float iocd = 1/(1+Stats.iocDiff(decoded));
			float punish;
			if (iocd < 0.95) punish = 0.00592f * iocd/0.95f;
			else punish = (float)Math.pow(iocd, 100);
			scores[0]*=punish;
			scores[1]*=punish;
			*/
			
			vec.decoded = decoded;
			vec.decoder = decoder;
			
			//TODO: find real zodiacScore method.  vec.zodiacscore = zodiacScore(decoded);
			vec.zodiacscore = 0;
			
			
			
			//scores[0] = vec.zodiacscore;
			
	        ((MultiObjectiveFitness)ind.fitness).setObjectives(state, scores);
	        ind.evaluated = true;
	        
	        //say(scores[0]+","+scores[1]+","+vec.genotypeToStringForHumans());
	        
	        //say("SMEG " + vec.genotypeToStringForHumans());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	static void say(String msg) {
		System.out.println(msg);
	}

	public static void main(String[] args) {
		System.out.println((char)97);
	}
	
	
}
