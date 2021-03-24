/*
  Portions copyright 2010 by Sean Luke, Robert Hubley, and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package ec.multiobjective.spea2;

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
import com.zodiackillerciphers.ciphers.generator.CandidateKey;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.multiobjective.MultiObjectiveFitness;
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
	static int NUM_PLAINTEXT_CANDIDATES = 10;
	
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
		int MAX = MAX_ARCHIVE_SIZE/NUM_PLAINTEXT_CANDIDATES;
		if (inds == null) return null;
		List<Individual> result = new ArrayList<Individual>();
		Set<String> seen = new HashSet<String>();
		
		Map<Integer, Integer> candidatePlaintextCounts = new HashMap<Integer, Integer>(); 
		
		for (Individual ind : inds) {
			CandidateKey key = (CandidateKey) ind;
			String o = key.objectivesToString();
			if (seen.contains(o)) continue;
			
			int index = key.plain.index;
			Integer count = candidatePlaintextCounts.get(index);
			if (count == null) count = 0;
			count++;
			if (count > MAX) continue; // don't allow a plaintext to be overrepresented within the archive
			candidatePlaintextCounts.put(index, count);
			
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
		//for (Individual ind : front) {
			//CandidateKey ck = (CandidateKey) ind;
			//System.out.println("breeder front " + ck.hashCode() + " " + Arrays.toString(ck.objectives()));
			
		//}
		
		front = dedupe(front); /* keep out the ones that have duplicate genomes, and limit the number of 
		times a single candidate plaintext can appear */ 
		
		// that front becomes the new archive.
		if (front.size() <= MAX_ARCHIVE_SIZE) {
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
						CandidateKey c1 = (CandidateKey) o1; 
						CandidateKey c2 = (CandidateKey) o2;
						return Float.compare(c1.compositeFitnessAsFloat(), c2.compositeFitnessAsFloat());
					}
				});
				for (int i=0; i<MAX_ARCHIVE_SIZE; i++) {
					archiveCustomized[i] = front.get(i);
					//System.out.println(state.generation + " truncating with " + front.get(i));
				}
			}
		}
		
		if (state.generation % 1000 == 0 || state.generation == 199999
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
	
	public void buildArchive(EvolutionState state, Individual[] oldInds,
			Individual[] newInds, int archiveSize) {
		Individual[] dummy = new Individual[0];

		List<CandidateKey> oldArchive = new ArrayList<CandidateKey>();

		for (int i = 0; i < oldInds.length; i++) {
			CandidateKey key = (CandidateKey) oldInds[i];
			if (key.archive)
				oldArchive.add(key);
			key.archive = false;
			// Utils.debug("SMEG OLDINDS POP DUMP " + )
		}
		// step 1: load the archive with the pareto-nondominated front
		ArrayList archive = new ArrayList();
		ArrayList nonFront = new ArrayList();
		MultiObjectiveFitness.partitionIntoParetoFront(oldInds, archive,
				nonFront);
		int currentArchiveSize = archive.size();

		Utils.debug("SMEG gen " + state.generation + " current size "
				+ archive.size() + " archive size " + archiveSize);
		if (TRACK_BEST) {
			Set<String> newSeen = new HashSet<String>();
			for (Individual ind : (List<Individual>) archive) {
				if (ind instanceof FrontMember) {
					FrontMember fm = (FrontMember) ind;
					String fit = fm.fit();
					// CandidateKey key = (CandidateKey) ind;
					// String fit = key.fit();
					if (seen == null || !seen.contains(fit)) {
						if (!newSeen.contains(fit)) {
							System.out.println("NEW BEST generation "
									+ state.generation + " genome "
									+ ind.genotypeToStringForHumans()
									+ " fitness " + fit);
							System.out.println("NEW BEST generation "
									+ state.generation + " html " + fm.html());
						}
					}
					newSeen.add(fit);
					if (state.generation % 100 == 0) {
						fm.expressGenome();
						String html = fm.html();
						System.out.println("DUMP FRONT generation "
								+ state.generation + " " + fm.dump() + " html "
								+ html);

					}

				}
			}

			seen = newSeen;

		}

		// for (Individual ind : (List<Individual>) nonFront) {
		// System.out.println("SMEG gen " + state.generation +
		// " non-front individual " + CandidateKey.objectInfo((CandidateKey)ind)
		// + " fitness: " + ind.fitness.fitnessToStringForHumans());
		// }
		// step 2: if the archive isn't full, load the remainder with the
		// fittest individuals (using customFitnessMetric) that aren't in the
		// archive yet
		if (currentArchiveSize < archiveSize) {
			Collections.sort(nonFront); // the fitter individuals will be
										// earlier
			int len = (archiveSize - currentArchiveSize);
			for (int i = 0; i < len; i++) {
				archive.add(nonFront.get(i));
				Utils.debug("SMEG ARCHIVE ADD NONFRONT "
						+ Utils.info(nonFront.get(i)) + " new size "
						+ archive.size());
				Utils.debug("SMEG gen "
						+ state.generation
						+ " padded with non-front ind "
						+ ((Individual) nonFront.get(i)).fitness
								.fitnessToStringForHumans());
				currentArchiveSize++;
			}
		}

		// step 3: if the archive is OVERFULL, iterate as follows:
		// step 3a: remove the k-closest individual in the archive
		SPEA2Evaluator evaluator = ((SPEA2Evaluator) (state.evaluator));
		Individual[] inds = (Individual[]) (archive.toArray(dummy));

		for (Individual ind : inds) {
			Utils.debug("SMEG ARCHIVE DUMP BEFORE TRIMMING/CLONING "
					+ Utils.info(ind));
		}

		while (currentArchiveSize > archiveSize) {
			Individual closest = (Individual) (archive.get(0));
			int closestIndex = 0;
			double[] closestD = calculateDistancesFromIndividual(closest,
					oldInds);

			for (int i = 1; i < currentArchiveSize; i++) {
				Individual competitor = (Individual) (archive.get(i));
				double[] competitorD = calculateDistancesFromIndividual(
						competitor, oldInds);

				for (int k = 0; k < oldInds.length; k++) {
					if (closestD[i] > competitorD[i]) {
						closest = competitor;
						closestD = competitorD;
						closestIndex = k;
						break;
					} else if (closestD[i] < competitorD[i]) {
						break;
					}
				}
			}

			// remove him destructively -- put the top guy in his place and
			// remove the top guy. This is O(1)
			Utils.debug("SMEG ARCHIVE OVERFULL, REPLACED "
					+ Utils.info(archive.get(closestIndex)) + " WITH "
					+ Utils.info(archive.get(archive.size() - 1))
					+ ", NEW SIZE " + (archive.size() - 1));
			// int h1 = archive.get(closestIndex).hashCode();
			// int h2 = archive.get(archive.size()-1).hashCode();
			archive.set(closestIndex, archive.get(archive.size() - 1));
			archive.remove(archive.size() - 1);

			currentArchiveSize--;
		}

		// step 4: put clones of the archive in the new individuals
		Object[] obj = archive.toArray();
		for (int i = 0; i < archiveSize; i++) {
			int index = newInds.length - archiveSize + i;
			newInds[index] = (CandidateKey) (((CandidateKey) obj[i]).clone());
			((CandidateKey) obj[i]).archive = true;
			((CandidateKey) newInds[index]).archive = true;

			if (((CandidateKey) obj[i]).isBroken()) {
				System.out.println("BAD ARCHIVE ENTRY FOUND: "
						+ Utils.info(obj[i]));
				System.exit(-1);
			}
			Utils.debug("SMEG CLONED FROM ARCHIVE from " + Utils.info(obj[i])
					+ " to " + Utils.info(newInds[index]));
		}

		// add new archive fitnesses to our running history
		if (TRACK_HISTORY) {
			List<float[]> fitnesses = new ArrayList<float[]>();
			for (Object o : obj) {
				CandidateKey key = (CandidateKey) o;
				fitnesses.add(((MultiObjectiveFitness) key.fitness).objectives
						.clone());
			}
			history.put(state.generation, fitnesses);

			// look for any old elite that suddenly dominates all individuals in the current generation 
			for (Integer gen1 : history.keySet()) {
				Integer gen2 = state.generation;
				if (gen2 < gen1) continue;
				// gen2 is after gen1
				// does an individual in gen1 dominate all individuals from gen2?
				for (float[] individual1 : history.get(gen1)) {
					boolean dominates = true;
					for (float[] individual2 : history.get(gen2)) {
						if (CandidateKey.isDominatedBy(individual2, individual1)) {
						} else {
							dominates = false;
							break;
						}
					}
					if (dominates) {
						System.out.println("gen " + gen1 + " individual " + Arrays.toString((individual1)) + " dominates all individuals from gen " + gen2);
						System.exit(-1);
					}
				}
			}
		}
		
	}
}
