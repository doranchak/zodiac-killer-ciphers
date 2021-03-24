package com.zodiackillerciphers.old;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.multiobjective.*;
import ec.simple.SimpleBreeder;
//import ec.simple.SimpleBreeder.EliteComparator;
import ec.util.QuickSort;
import ec.util.SortComparatorL;
import gnu.trove.*;
import java.util.ArrayList;
import java.util.Date;

/** extended so we can modify selection of elites. */
public class ZodiacBreeder extends SimpleBreeder {
	
	public static boolean LOAD_UNSEEN_WORDS = false;
	public static float ELITE_TOP_RESERVED = 0.0f; // how much of the elite space to reserve for the top edge of the pareto front.
	public static float ELITE_BOTTOM_RESERVED = 0.0f; // how much of the elite space to reserve for new individuals 
	
	
  protected class ZodiacEliteComparator implements SortComparatorL
  {
  Individual[] inds;
  public ZodiacEliteComparator(Individual[] inds) {super(); this.inds = inds;}
  public boolean lt(long a, long b) {
  	CipherWordGene geneA = (CipherWordGene)inds[(int) a];
  	CipherWordGene geneB = (CipherWordGene)inds[(int) b];
  	
  	double[] fitA = ((MultiObjectiveFitness)geneA.fitness).getObjectives();
  	double[] fitB = ((MultiObjectiveFitness)geneB.fitness).getObjectives();
  	
    for (int i=0; i<fitA.length; i++) {
    	if (fitA[i] < fitB[i]) return true;
    	if (fitA[i] > fitB[i]) return false;
    }
    return false;
  	
  }
     // { return inds[(int)b].fitness.betterThan(inds[(int)a].fitness); }
  public boolean gt(long a, long b) {
  	CipherWordGene geneA = (CipherWordGene)inds[(int) a];
  	CipherWordGene geneB = (CipherWordGene)inds[(int) b];
  	
  	double[] fitA = ((MultiObjectiveFitness)geneA.fitness).getObjectives();
  	double[] fitB = ((MultiObjectiveFitness)geneB.fitness).getObjectives();
  	
    for (int i=0; i<fitA.length; i++) {
    	if (fitA[i] > fitB[i]) return true;
    	if (fitA[i] < fitB[i]) return false;
    }
    return false;
  }
     // { return inds[(int)a].fitness.betterThan(inds[(int)b].fitness); }
  }
	
	public void loadElites(EvolutionState state, Population newpop) {
		// we assume that we're only grabbing a small number (say <10%), so
		// it's not being done multithreaded
		
		boolean TOP_ONLY = false; // if true, we only seed the elites with topmost members; also, we only care about the top half.
		int MAX_BIN_SIZE = Integer.MAX_VALUE; // only allow this many top individuals within each first objective's bin
		boolean ENFORCE_UNIQUE = true; // if true, sortedKey is used to enforce a rough measure of uniqueness within bins
		
		Date date1 = new Date();
		System.out.println("loading elites");
		THashSet<String> seenWords = new THashSet<String>();
		for (int sub = 0; sub < state.population.subpops.length; sub++) {
			int[] orderedPop = new int[state.population.subpops[sub].individuals.length];
			CipherWordGene gene = null;
			for (int x = 0; x < state.population.subpops[sub].individuals.length; x++) {
				orderedPop[x] = x;
				
				gene = (CipherWordGene) state.population.subpops[sub].individuals[x];
				
				if (ZodiacESStatistics.PERFORM_SHARING) {
			        // swap unshared with shared.  we want to perform elitism by sorting by raw fitness values.
					if (ZodiacESStatistics.ELITISM_UNSHARED) {
						//gene.fitnessShared = gene.fitness;
						//gene.fitness = gene.fitnessUnshared;
					}
				}
				
				/** track the words so we can find out what words are missing from the population */
				if (LOAD_UNSEEN_WORDS) {
					for (int i=0; i<gene.genome.length; i+=2) {
						seenWords.add(CipherWordGene.getWordForGene(gene.genome[i]));
					}
				}
				
			}
			
			/** make a list of all words that are NOT repesented in this population */
			ArrayList<String> unseenWords = new ArrayList<String>();
			if (LOAD_UNSEEN_WORDS) {
				for (int i=0; i<CipherWordGene.wordPool.length; i++) {
					if (!seenWords.contains(CipherWordGene.wordPool[i])) {
						unseenWords.add(CipherWordGene.wordPool[i]);
						//System.out.println("missing word: " + CipherWordGene.wordPool[i]);
					}
				}
			}
			

			// sort the best so far where "<" means "not as fit as"
			QuickSort.qsort(orderedPop, new ZodiacEliteComparator(
					state.population.subpops[sub].individuals));
			// load the top N individuals

			Individual[] inds = newpop.subpops[sub].individuals;
			Individual[] oldinds = state.population.subpops[sub].individuals;
			
			
			/*for (int x = inds.length - elite[sub]; x < inds.length; x++) {
				inds[x] = (Individual) (oldinds[orderedPop[x]].clone());
			}*/
			/*for (int i=0; i<orderedPop.length; i++) {
				System.out.println("sorted fitness: " + oldinds[orderedPop[i]].fitness.fitnessToStringForHumans());
			}*/
			
			/* start at the end, and grab DISTINCT (or significantly non-similar) elite individuals */
			THashSet<String> keys = new THashSet<String>();
			String key;
			
			/** get the max fitness of the first objective.  used to init our indices. */
			int maxFit = 0; //(int)((MultiObjectiveFitness)oldinds[orderedPop[inds.length-1]].fitness).multifitness[0];
			ArrayList[] elitesByFitness = new ArrayList[maxFit+1];
			int[] elitesIndices = new int[maxFit+1]; 
			
			int fit;
			for (int i=inds.length-1; i>=0; i--) { // stick every gene in a bin based on its first objective.
				fit = 0; //(int)((MultiObjectiveFitness)oldinds[orderedPop[i]].fitness).multifitness[0];
				if (elitesByFitness[fit] == null) {
					elitesByFitness[fit] = new ArrayList<CipherWordGene>();
				}
				key = ((CipherWordGene)oldinds[orderedPop[i]]).sortedKey;
				if (elitesByFitness[fit].size() < MAX_BIN_SIZE && (!keys.contains(key) || !ENFORCE_UNIQUE)) {
					//System.out.println("added to bin fitness " + ((CipherWordGene)oldinds[orderedPop[i]]).fitness.fitnessToStringForHumans());
					elitesByFitness[fit].add(oldinds[orderedPop[i]]);
					if (ENFORCE_UNIQUE) keys.add(key);
				}
			}
			System.out.println("loaded " + keys.size() + " distinct sortedKeys into the elite bins.");
			
			String bins = "elite bin sizes: ";
			for (int i=0; i<maxFit+1; i++)
				if (elitesByFitness[i] != null) {
					bins += i + ": " + elitesByFitness[i].size() + ", "; 
				}
			System.out.println(bins);
			
			/*
			for (int i=elitesByFitness.length-1; i>=0; i--) {
				System.out.println("i: " + i); 
				if (elitesByFitness[i] != null) {
					for (int j=0; j<elitesByFitness[i].size(); j++) {
						gene = (CipherWordGene)elitesByFitness[i].get(j);
						System.out.println(gene.fitness.fitnessToStringForHumans() + ", " + gene.sortedKey);
					}
				}
			}*/
			 
			
			int count = 0;
			//int index = inds.length-1;
			int index = elitesIndices.length-1;
			int len = CipherWordGene.GENOME_LENGTH;

			/* fill up the reserved space with individuals at the top edge of the pareto front */
			while (count < elite[sub] * ELITE_TOP_RESERVED) {
				if (elitesByFitness[index] != null && elitesByFitness[index].size() > 0) { 
					gene = (CipherWordGene) elitesByFitness[index].get(0);
					inds[inds.length-count-1] = gene;
					gene.elite = true;
					count++;
				}
				index--;
				if (index < 0) index = elitesIndices.length-1;
			}
			/* fill up the reserved space with newly-initialized individuals */
			CipherWordGene newGene;
			while (count < (elite[sub] * ELITE_TOP_RESERVED + elite[sub] * ELITE_BOTTOM_RESERVED)) {
				newGene = (CipherWordGene) gene.clone();
				newGene.conflictInit();
				newGene.genome = new int[CipherWordGene.GENOME_LENGTH];
				newGene.resetOneRandomWordWithoutSpecies(state, 0);
				newGene.elite = true; // but not really
				inds[inds.length-count-1] = newGene; 
				count++;
				//index--;
				//if (index < 0) index = elitesIndices.length-1;
			}
			index = elitesIndices.length-1;
			
			
			boolean foundOne = false;
			while (count < elite[sub]) {
				//System.out.println("loadelite: count " + count + ", index " + index);
				if (index == -1 || (TOP_ONLY && index < maxFit / 2)) { // we got to the end of the bottom fitnesses; reset back to top fitnesses.
					if (!foundOne) { // we didn't find a good distinct elite on this pass.  so, let's reset everything.
						System.out.println("loadElites: not enough elites to fill up bin, so resetting seen sortedKeys and elitesIndices.");
						keys = new THashSet<String>();
						elitesIndices = new int[maxFit+1]; 
					}
					foundOne = false;
					index = elitesIndices.length-1;
				}
				if (elitesByFitness[index] != null) { // there are elites for this fitness
					//if (elitesIndices[index] >= elitesByFitness[index].size()) { // start back at the top of this bin.  this way we can be fair to under-represented "good" pareto optimal individuals.
					//	elitesIndices[index] = 0;
					//}
					while (elitesIndices[index] < elitesByFitness[index].size()) { // repeat until we find one we can use, or we reach the end.
						gene = (CipherWordGene) elitesByFitness[index].get(elitesIndices[index]);
						if (!TOP_ONLY) elitesIndices[index]++;
						//if (!keys.contains(gene.sortedKey) || TOP_ONLY) { // we haven't seen this sortedKey before. (unnecessary: done via bin loading above)
							keys.add(gene.sortedKey);
							inds[inds.length-count-1] = gene;
							gene.elite = true;
							count++;
							//System.out.println("count " + count + "; loadelite: added an elite at " + (inds.length-count-1) + ", fitness " + gene.fitness.fitnessToStringForHumans() + ", sortedKey " + gene.sortedKey);
							foundOne = true;
							break;
						//}
					} 
					//System.out.println(count+","+index);
				}
				index--;
				//gene = (CipherWordGene) oldinds[orderedPop[index]];
				
				/** fill first half with elites, 2nd half with new initializations of unseen words (only if there ARE some unseen words) */
				/*if (!LOAD_UNSEEN_WORDS || count < elite[sub]/2 || unseenWords.size() == 0) {
					if (!keys.contains(gene.sortedKey)) { // we haven't seen this sortedKey before.
						keys.add(gene.sortedKey);
						inds[inds.length-count-1] = gene;
						gene.elite = true;
						//System.out.println("loadelite: added an elite at " + (inds.length-count-1) + ", fitness " + gene.fitness.fitnessToStringForHumans());
						count++;
					}
				} else { // reset with a new unseen word
					if (!LOAD_UNSEEN_WORDS) throw new RuntimeException("We are not loading unseen words.  This code should not run!");
					String word = unseenWords.get(count % unseenWords.size());
					int w = count % unseenWords.size();
					while (w<CipherWordGene.wildCardProbability*CipherWordGene.maxAllele) w += CipherWordGene.wordPool.length;
					gene = (CipherWordGene) gene.clone();
					gene.conflictInit();
					gene.genome = new int[len];
					gene.resetWithThisRandomWord(state, 0, w);
					inds[inds.length-count-1] = gene;
					count++;
				}
				index--;
				if (index == -1) {*/
					/* we didn't find enough unique individuals to fill the elite bucket. so,
					 * reset the "seen" individuals and continue filling.
					 */
					/*System.out.println("loadelite: resetting seen elites");
					keys = new THashSet<String>();
					index = inds.length-1;
				}*/
			}
			
			/* now, we need to put the shared fitnesses back, so we can be fair to the fitness-weighed selections in the next steps. */
			if (ZodiacESStatistics.PERFORM_SHARING) {
				if (ZodiacESStatistics.ELITISM_UNSHARED) {
					for (int x = 0; x < state.population.subpops[sub].individuals.length; x++) {
						gene = (CipherWordGene) state.population.subpops[sub].individuals[x];
						//gene.fitness = gene.fitnessShared;
					}
				}
			}
			System.out.println("done loading elites");
			Date date2 = new Date();
  		//CipherWordGene.statsInc("loadElitesTime",(float)(date2.getTime()-date1.getTime()));
			
		}
	}
}
