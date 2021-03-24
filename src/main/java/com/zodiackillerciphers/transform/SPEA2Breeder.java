/*
  Portions copyright 2010 by Sean Luke, Robert Hubley, and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package com.zodiackillerciphers.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.Utils;
import com.zodiackillerciphers.ciphers.FrontMember;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.spea2.SPEA2MultiObjectiveFitness;
import ec.simple.SimpleBreeder;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

/* 
 * SPEA2Breeder.java
 * 
 * Created: Sat Oct 16 11:24:43 EDT 2010
 * By: Faisal Abidi and Sean Luke
 * Replaces earlier class by: Robert Hubley, with revisions by Gabriel Balan and Keith Sullivan
 */

/**
 * This subclass of SimpleBreeder overrides the loadElites method to build an
 * archive in the top elites[subpopnum] of each subpopulation. It computes the
 * sparsity metric, then constructs the archive.
 */

public class SPEA2Breeder extends SimpleBreeder {

	public static boolean TRACK_BEST = false;
	public static boolean TRUNCATE_RANDOM = false;
	public Set<String> seen;
	static MersenneTwisterFast rand = new MersenneTwisterFast();

	// archive fitness history used during debugging
	Map<Integer, List<float[]>> history = new HashMap<Integer, List<float[]>>();
	static boolean TRACK_HISTORY = false;

	static Individual[] archiveCustomized;
	static int MAX_ARCHIVE_SIZE = 100;
	
	/** number of distinct plaintext candidates to maintain in the archive */
	static int NUM_CANDIDATES = 10;
	
	static double[] bestObjectivesOverall;
	
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		if (sequentialBreeding) // uh oh, haven't tested with this
			state.output.fatal(
					"SPEA2Breeder does not support sequential evaluation.",
					base.push(P_SEQUENTIAL_BREEDING));

		if (!clonePipelineAndPopulation)
			state.output
					.fatal("clonePipelineAndPopulation must be true for SPEA2Breeder.");
	}

	protected void loadElites(EvolutionState state, Population newpop) {
		// are our elites small enough?
		for (int x = 0; x < state.population.subpops.length; x++) {
			// System.out.println("SMEG elites " + elite[x] + " inds " +
			// state.population.subpops[x].individuals.length);
			if (elite[x] > state.population.subpops[x].individuals.length)
				state.output.error("The number of elites for subpopulation "
						+ x + " exceeds the actual size of the subpopulation",
						new Parameter(EvolutionState.P_BREEDER).push(P_ELITE)
								.push("" + x));
			state.output.exitIfErrors();
		}

		// do it
		for (int sub = 0; sub < state.population.subpops.length; sub++) {
			Individual[] newInds = newpop.subpops[sub].individuals; // The new
																	// population
																	// after we
																	// are done
																	// picking
																	// the
																	// elites
			Individual[] oldInds = state.population.subpops[sub].individuals; // The
																				// old
																				// population
																				// from
																				// which
																				// to
																				// pick
																				// elites

			//buildArchive(state, oldInds, newInds, elite[sub]);
			buildArchiveCustomized(state, oldInds, newInds);
		}

		// optionally force reevaluation
		//unmarkElitesEvaluated(state, newpop);
	}

	public double[] calculateDistancesFromIndividual(Individual ind,
			Individual[] inds) {
		double[] d = new double[inds.length];
		for (int i = 0; i < inds.length; i++)
			d[i] = ((SPEA2MultiObjectiveFitness) ind.fitness)
					.sumSquaredObjectiveDistance((SPEA2MultiObjectiveFitness) inds[i].fitness);
		// now sort
		Arrays.sort(d);
		return d;
	}

	List<Individual> dedupe(List<Individual> inds) { 
		//int MAX = MAX_ARCHIVE_SIZE/NUM_CANDIDATES;
		int MAX = MAX_ARCHIVE_SIZE;
		if (inds == null) return null;
		List<Individual> result = new ArrayList<Individual>();
		Set<String> seen = new HashSet<String>();
		
		Map<String, Integer> sequenceCounts = new HashMap<String, Integer>(); 
		
		for (Individual ind : inds) {
			Operations ops = (Operations) ind;
			String o = ops.objectivesToString();
			if (seen.contains(o)) continue;
			
			String key = ops.operationsActualSequence;
			Integer count = sequenceCounts.get(key);
			if (count == null) count = 0;
			count++;
			if (count > MAX) continue; // don't allow a plaintext to be overrepresented within the archive
			sequenceCounts.put(key, count);
			
			seen.add(o);
			result.add(ind);
		}
		return result;
	}
	public void buildArchiveCustomized(EvolutionState state, Individual[] oldInds,
			Individual[] newInds) {
		
		
		
		// concatenate current archive with population, then compute pareto-nondominated
		// front.
		// that front becomes the new archive.
		// clone a new population based on random selections from the newly formed archive.
		
		
		// concatenate current archive with population, then compute pareto-nondominated
		// front.
		
		if (archiveCustomized == null) {
			//System.out.println("made new archiveCustomized array");
			archiveCustomized = new Individual[0];
		}
		Individual[] inds = new Individual[archiveCustomized.length + oldInds.length];
		//System.out.println("archiveCustomized " + archiveCustomized.length + " oldInds " + oldInds.length + " newInds " + newInds.length + " inds " + inds.length);
		int index = 0;
		for (Individual ind : archiveCustomized) {
			inds[index++] = ind;
			//CandidateKey ck = (CandidateKey) ind;
			//System.out.println("breeder archive " + ck.hashCode() + " " + Arrays.toString(ck.objectives()));
		}
		for (Individual ind : oldInds) {
			inds[index++] = ind;
			//CandidateKey ck = (CandidateKey) ind;
			//System.out.println("breeder oldinds " + ck.hashCode() + " " + Arrays.toString(ck.objectives()));
			
		}
		List<Individual> front = MultiObjectiveFitness.partitionIntoParetoFront(inds, null, null);
		
		/*
		System.out.println("--- smeg dump front gen " + state.generation);
		for (Individual ind : archiveCustomized) {
			Operations op = (Operations) ind;
			System.out.println(state.generation + " smeg archive obj " + op.objectivesToString());
		}
		for (Individual ind : front) {
			Operations op = (Operations) ind;
			System.out.println(state.generation + " smeg front obj " + op.objectivesToString());
		}*/
		//for (Individual ind : front) {
			//CandidateKey ck = (CandidateKey) ind;
			//System.out.println("breeder front " + ck.hashCode() + " " + Arrays.toString(ck.objectives()));
			
		//}
		
		front = dedupe(front); /* keep out the ones that have duplicate genomes, and limit the number of 
		//times a single candidate plaintext can appear */ 
		
		// that front becomes the new archive.
		if (front.size() <= MAX_ARCHIVE_SIZE) {
			front = extremaFirst(front, state.generation);
			archiveCustomized = new Individual[front.size()];
			for (int i=0; i<front.size(); i++) archiveCustomized[i] = front.get(i);
		} else {
			// the front is too big, so we have to truncate the front
			archiveCustomized = new Individual[MAX_ARCHIVE_SIZE];
			// one way to truncate: select random entries up to the max archive size.
			if (TRUNCATE_RANDOM) {
				for (int i=0; i<MAX_ARCHIVE_SIZE; i++) {
					archiveCustomized[i] = front.remove(rand.nextInt(front.size()));
				}
			} else {  // another way: sort by compositeFitness and pick the best ones
				Collections.sort(front, new Comparator<Individual>() {
					@Override
					public int compare(Individual o1, Individual o2) {
						Operations c1 = (Operations) o1; 
						Operations c2 = (Operations) o2;
						return Double.compare(c1.compositeFitnessAsDouble(), c2.compositeFitnessAsDouble());
					}
				});
				front = extremaFirst(front, state.generation); // after sorting, put extrema first to ensure their survival 
				for (int i=0; i<MAX_ARCHIVE_SIZE; i++) {
					archiveCustomized[i] = front.get(i);
					//System.out.println(state.generation + " archive member: " + front.get(i));
				}
			}
		}
		
		if (state.generation % Stats.GENS_PER_DUMP == 0 || state.generation == 199999
				|| state.generation == 299999)
			for (Object o : archiveCustomized)
				System.out.println(state.generation + " archive size "
						+ archiveCustomized.length + " member " + o.toString());
		
		// clone a new population based on random selections from the newly formed archive.
		// and go ahead and mutate them 
		for (int i=0; i<newInds.length; i++) {
			newInds[i] = (Individual) archiveCustomized[rand.nextInt(archiveCustomized.length)].clone();
			oldInds[i] = newInds[i]; 
			//((CandidateKey)newInds[i]).defaultMutate(state, 0);
			//System.out.println("cloned " + newInds[i].hashCode());
		}
		
	}
	
	public void initBest(int L) {
		if (bestObjectivesOverall == null) {
			bestObjectivesOverall = new double[L];
			for (int i=0; i<L; i++) bestObjectivesOverall[i] = Float.MAX_VALUE;
		}
	}
	
	/** ensure extrema among objectives are returned first, so they don't get lost during truncation */
	public List<Individual> extremaFirst(List<Individual> front, int gen) {
		if (front == null || front.isEmpty()) return front;
		if (front.size() < 6) return front;
		// map objective # to index of individual with that best objective   
		//Map<Integer, Integer> bests = new HashMap<Integer, Integer>(); 
		double[] bestObjectives = new double[((MultiObjectiveFitness)front.get(0).fitness).getObjectives().length];
		initBest(bestObjectives.length);
		//boolean found[] = new boolean[bestObjectives.length];
		for (int i=0; i<bestObjectives.length; i++) bestObjectives[i] = Float.MAX_VALUE;
		//System.out.println("smeg extremaFirst");
		
		List<Individual> newFront = new ArrayList<Individual>();
		
		Map<Integer, Integer> forSwap = new HashMap<Integer, Integer>();
		Map<Integer, Operations> bests = new HashMap<Integer, Operations>(); 
		
		for (int i=0; i<front.size(); i++) {
			Individual ind = front.get(i);
			MultiObjectiveFitness fit = (MultiObjectiveFitness)ind.fitness;
			double[] objectives = fit.getObjectives();
			for (int o=0; o<objectives.length; o++) {
				if (objectives[o] < bestObjectives[o]) {
					bestObjectives[o] = objectives[o];
					bests.put(o, (Operations)ind);
					//if (bestObjectives[o] <= bestObjectivesOverall[o]) found[o] = true;
					//bests.put(o, i);
					//if (o != i) {
						forSwap.put(o, i);
						//Collections.swap(front, o, i); // ith position in front has best ith objective.
						//System.out.println("smeg swapped best objective [" + o + "]=[" + bestObjectives[o] + " from pos " + i + " in front to position " + o);
					//}
				}
			}
		}
		
		/*
		for (int o=bestObjectives.length-1; o>=0; o--) {
			if (forSwap.get(o) == null) {
				System.out.println("smeg wtf, null for " + o + ", front size " + front.size());
				continue;
			}
			int val = forSwap.get(o);
			String s1 = Arrays.toString(((Operations)front.get(o)).objectives());
			String s2 = Arrays.toString(((Operations)front.get(val)).objectives());
			System.out.println("smeg swap " + o + " " + val + ": " + s1 + " for " + s2);
			Collections.swap(front, o, val);
		}*/
		
		for (Integer val : new HashSet<Integer>(forSwap.values())) {
			newFront.add(front.get(val));
			//System.out.println(gen + " added extrema " + val);
		}
		for (int i=0; i<front.size(); i++) {
			if (forSwap.values().contains(i)) continue;
			newFront.add(front.get(i));
			//System.out.println(gen + " added normal " + i);
		}
		if (newFront.size() != front.size()) {
			System.out.println("WRONG SIZES " + newFront.size() + " vs " + front.size());
			System.exit(-1);
		}
		
		/*String msg = "=== smeg best: ";
		for (int i=0; i<bestObjectives.length; i++) {
			msg += bestObjectives[i] + " ";
			System.out.println("smeg top " + i + " " + ((Operations)front.get(i)).objectivesToString());
		}
		System.out.println(msg);*/
		
		if (gen > 0) for (int i=0; i<bestObjectives.length; i++) {
			if (bestObjectives[i] > bestObjectivesOverall[i]) {
				System.out.println("lost best! overall: " + Arrays.toString(bestObjectivesOverall) + ", current: " + Arrays.toString(bestObjectives));
				System.exit(-1);
			} else if (bestObjectives[i] < bestObjectivesOverall[i]) {
				System.out.println("NEW BEST " + i + ": " + bests.get(i));
			}
		}
		bestObjectivesOverall = bestObjectives.clone();
		
		return newFront;
		
	}
}
