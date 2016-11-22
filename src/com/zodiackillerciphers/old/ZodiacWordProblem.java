package com.zodiackillerciphers.old;

import ec.*;
import ec.multiobjective.*;
import ec.simple.*;
import ec.util.*;
import ec.vector.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;

import gnu.trove.*;

public class ZodiacWordProblem extends Problem { // implements SimpleProblemForm {

	public static boolean DO_SPEA2 = true;
	
	/** factor by which we punish the clusters found during fitness sharing evaluation */
	public static float FITNESS_SHARING_PUNISHMENT = 9.0f;
	/** if true, we select punishment factors at random in an attempt to determine the values that produce the greatest improvement to mean fitness */
	public static boolean FITNESS_SHARING_EXPLORATORY = false;
	public static float FITNESS_SHARING_EXPLORATORY_MIN = 0.0f; // bottom of range for random assignments of punishment value
	public static float FITNESS_SHARING_EXPLORATORY_MAX = 20.0f; // top of range for random assignments of punishment value
	
	/** used to track previous generation's mean fitness */
	public static float[] previousMeanFitness = new float[CipherWordGene.NUM_OBJECTIVES];
	
	/** this collapses fitnesses into discrete groupings.  change this depending on your fitness range. */
	public static int DISCRETIZATION_FACTOR = 1;
	/** triangle function applied to fitness-based fitness sharing, to encourage higher fitnesses to be more prolific in the population. */ 
	public static int FITNESS_SCALING_FACTOR = 1;
	
	public CipherGene bestGeneByFitness = null;
	public CipherGene bestGeneByZodiacMatch = null;
	
	public static float[] bestFitness = new float[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static double bestZodiacMatch = 0.0f;
	
	public void describe(Individual ind, EvolutionState state, int threadnum, int log, int verbosity) {
		// TODO Auto-generated method stub
    CipherGene gene = (CipherGene) ind;
    /*if (Zodiac.WORD_ENCODING == 0) {
    	gene.genome = ind2.genome;
    	gene.fitnessWord(275);
    }*/
    //gene.debug();
    state.output.println(gene.getDebugLine(),
        verbosity,log);
	}

	public void evaluate(EvolutionState state, Individual ind, int threadnum) {
		
		if (ZodiacDictionary.wordConstraintDatabase == null)
			ZodiacDictionary.makeIsWordHash();
		
		if (Zodiac.alphabetHash == null)
			Zodiac.initAlphabetHash();
		
    if (ind.evaluated) return;

    /* if in steady state mode, let's punish this individual if it is a dupe of any other individual 
     * NOTE - looks thru all subpopulations for dupes.
     */
    /*if (Zodiac.STEADYSTATE) {
    	for (int i=0; i<state.population.subpops.length; i++) {
    		for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
    			ZodiacVectorWordIndividual zind = (ZodiacVectorWordIndividual) state.population.subpops[i].individuals[j];
    			if (ind != zind && zind.equals(ind)) { // two different pointers pointing to the same decoder string
    				state.output.message("We found a dupe individual at subpop# " + i + ", individual# " + j + " for " + zind.getGene().decoder);
    				state.output.message("encoding 1: " + ((ZodiacVectorWordIndividual)ind).getGene().encoding);
    				state.output.message("encoding 2: " + zind.getGene().encoding);
    		    MultiObjectiveFitness fit = (MultiObjectiveFitness) ind.fitness;
    		    for (int k=0; k<fit.multifitness.length; k++) {
      		    fit.multifitness[k] = 0; // punish this individual for being a lousy dupe.
    		    }
    		    ind.evaluated = true;
    		    return;
    			}
    		}
    	}
    }*/
    
    if (!(ind instanceof CipherGene) && !(ind instanceof CipherWordGene))
        state.output.fatal("Whoa!  It's not a CipherGene or CipherWordGene!!!",null);
    
    //int sum=0;
    CipherGene gene = (CipherGene)ind;
    
    // the individual may have a different genome array that hasn't been passed into the CipherGene yet.
    //zind.setGene(new CipherGene(zind.genome));
    
    
    //ind2.setGene(null);
    //CipherGene gene = zind.getGene();
    
    //gene.genome = ind2.genome;
    /*if (Zodiac.WORD_ENCODING == 0) {
    	gene.fitnessWord(275);
    } else {
    	;
    }*/
    
    //ind2.setGene(gene);
    
    
    /* multiobjective */
    if (!(gene.fitness instanceof MultiObjectiveFitness))
      state.output.fatal("Whoa!  It's not a MultiObjectiveFitness!!!" + gene.fitness.getClass().getName(),null);
    //MultiObjectiveFitness fit = (MultiObjectiveFitness) gene.fitness;
    
    if (gene instanceof CipherGene)
    	gene.fitness(); // compute all fitnesses
    else
    	((CipherWordGene) gene).fitness();
    
    /* uncomment all this for the n-wise fitness */ 
    //float[] fitnesses = new float[2];

  	//gene.fitnessWordSimple();
  	//gene.decode();
  	//fitnesses[0] = (float)gene.fitnessWord;
  	//gene.fitnessExperiment142();
  	//fitnesses[1] = (float)gene.fitnessWord;
  	
    
    MultiObjectiveFitness fit = (MultiObjectiveFitness) gene.fitness;
    
    int bestCount = 0;
  	/*for (int i=0; i<fit.multifitness.length; i++) {
      if (fit.multifitness[i] > bestFitness[i]) {
      	bestCount++;
      	bestFitness[i] = fit.multifitness[i];
      	state.output.message("*** NEW BEST FITNESS[" + i + "] *** " + bestFitness[i] + ", " + ind.genotypeToStringForHumans());
      }
      if (gene.zodiacMatch > bestZodiacMatch) {
      	bestZodiacMatch = gene.zodiacMatch;
      	state.output.message("*** NEW BEST ZODIAC MATCH *** " + bestZodiacMatch + ", " + ind.genotypeToStringForHumans());
      }
  	}
  	if (bestCount == fit.multifitness.length) { // each objective was superior to previously known best
    	state.output.message("*** NEW BEST MULTIOBJECTIVE FITNESS *** , " + ind.genotypeToStringForHumans());
  	}*/

	}

	/** Let's do some fitness sharing as a niching method.
	 * NO LONGER DONE FROM HERE, B/C IT IS NOT THREADSAFE.
	 **/
	public void finishEvaluatingOBSOLETE(EvolutionState state, int threadnum) {
		/* NO LONGER TRUE: we don't compute a distance.  instead, either the genomes are different, or they are the same.
		 * this lets us avoid an O(n^2*l) loop (l is the genome length).
		 */
		
		/* 
		 * Note: if there is more than one subpopulation, a single sharing count
		 * is performed for ALL subpopulations.
		 */
		/*
		for (int i=0; i<state.population.subpops.length; i++) {
			fitnessSharingFast(state.population.subpops[i]);
		}*/
		
		/* TEST: maybe the radix-based one needs tweaking. */
		if (Zodiac.STEADYSTATE) {
			state.output.message("finishEval called");
		} else {
			state.output.message("finishEval called");
			//fitnessSharingViaFitness(state);
			//fitnessSharingViaWordPos(state, CipherWordGene.MIN_WORD_LENGTH);
			//fitnessSharingSimple(state, false);
			fitnessSharingViaWordPos(state, 1, threadnum); /* this is now called from ZodiacESStatistics */
			//fitnessSharingViaWordPosSortedKey(state, CipherWordGene.MIN_WORD_LENGTH);
		}
		if (!DO_SPEA2) {
			//fitnessSharingViaFitness(state);
		}
	}
	
	
	/** simple fitness sharing by looking at each metric in a multiobjective fitness.
	 *  if multiple individuals share the same fitness vector, then the fitness vector is scaled down
	 *  by a factor proportionate to the number of shared vectors.
	 *  
	 *  NOTE - it is applied across ALL subpopulations.
	 */
	public static void fitnessSharingViaFitness(EvolutionState state) {
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		MultiObjectiveFitness fit;
		String key;
		Integer value;
		float max = Float.MIN_VALUE;
		//int mean;
		float localMax;
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				fit = (MultiObjectiveFitness) state.population.subpops[i].individuals[j].fitness;
				key = ""; //mean = 0;			
				/*for (int k=0; k<fit.multifitness.length; k++) {
				//for (int k=0; k<1; k++) {
					max = Math.max(max,fit.multifitness[k]);
					key += ((int)fit.multifitness[k]) / DISCRETIZATION_FACTOR + " ";
					//mean += ((int)fit.multifitness[k]);
					//key += fit.multifitness[k] + " ";
				}*/
				//mean /= fit.multifitness.length;
				//key = ""+mean;
				//System.out.println(key);
				value = count.get(key);
				if (value == null) value = new Integer(0);
				value = new Integer(value.intValue()+1);
				count.put(key, value);
			}
		}
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				fit = (MultiObjectiveFitness) state.population.subpops[i].individuals[j].fitness;
				key = ""; //mean = 0;
				/*for (int k=0; k<fit.multifitness.length; k++) {
				//for (int k=0; k<1; k++) {
					key += ((int)fit.multifitness[k]) / DISCRETIZATION_FACTOR + " ";
					//mean += ((int)fit.multifitness[k]);
					//key += fit.multifitness[k] + " ";
				}*/
				//mean /= fit.multifitness.length;
				//key = "" + mean;
				value = count.get(key);
				localMax = Float.MIN_VALUE;
				if (value.intValue() > 1) {
					/*for (int k=0; k<fit.multifitness.length; k++) {
						localMax = Math.max(localMax, fit.multifitness[k]);
					}
					for (int k=0; k<fit.multifitness.length; k++) {
						//fit.multifitness[k] /= (value.intValue() * (1 + (FITNESS_SCALING_FACTOR-1)*(max - localMax) / max)) ; // encourage higher fitnesses 
						fit.multifitness[k] /= value.intValue(); // punish clusters
					}*/
				}
			}
		}
		
	}
	
	/** simple fitness sharing by looking at each word/position tuple in the genome.
	 *  an individual's fitness is punished by counting how many times each of the individual's word/pos tuples
	 *  appear in the entire population.
	 *  
	 *  minWordLength: the lower cutoff where we ignore smaller words
	 *  
	 *  NOTE - it is applied across ALL subpopulations.
	 *  
	 *  NOTE - we now count based on the phenotypes, not the genotypes.
	 *  
	 *  UPDATE: This technique is messed up somehow - it seems to destroy high-fitness individuals rather than 
	 *  force clusters that preserve them.
	 *  
	 */
	public static void fitnessSharingViaWordPos(EvolutionState state, int minWordLength, int threadNum) {
		THashMap<String, Integer> count = new THashMap<String, Integer>();
		THashSet<String> words = new THashSet<String>();
		MultiObjectiveFitness fit;
		String key;
		String word;
		Integer value;
		IntegerVectorIndividual ind;
		
		float[] meanFitness = new float[CipherWordGene.NUM_OBJECTIVES];
		int total = 0;
		
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				ind = (IntegerVectorIndividual) state.population.subpops[i].individuals[j];
				total++;

				if (FITNESS_SHARING_EXPLORATORY) {
					for (int k=0; k<CipherWordGene.NUM_OBJECTIVES; k++) {
						//meanFitness[k] += ((MultiObjectiveFitness)ind.fitness).multifitness[k];
					}
				}
				
				for (int k=0; k<ind.genome.length; k+=2) {
					key = CipherWordGene.makeKeyFor(ind.genome[k],ind.genome[k+1], minWordLength);
					word = CipherWordGene.getWordFromAllele(ind.genome[k]);
					if (!word.equals(""))
						words.add(word);
					//key = CipherWordGene.makeSortedKeyFor(ind.genome, minWordLength);
					//key = ((CipherWordGene)ind).sortedKey;
					if (key != null) { // key is null when word/pos represents a wildcard
						value = count.get(key);
						if (value == null) value = new Integer(0);
						value = new Integer(value.intValue()+1);
						count.put(key, value);
					}
				}
			}
		}

		if (FITNESS_SHARING_EXPLORATORY) {
			String fitString = "";
			String fitStringPrev = "";
			String improvementString = "";
			float[] improvement = new float[CipherWordGene.NUM_OBJECTIVES];
			for (int k=0; k<CipherWordGene.NUM_OBJECTIVES; k++) {
				meanFitness[k] /= (float) total;
				fitString += (k == 0 ? "" : ", ") + meanFitness[k]; 
				fitStringPrev += (k == 0 ? "" : ", ") + previousMeanFitness[k];
				improvement[k] = meanFitness[k]/previousMeanFitness[k];
				improvementString += (k == 0 ? "" : ", ") + improvement[k];
				previousMeanFitness[k] = meanFitness[k];
			}
			System.out.println("punishment " + FITNESS_SHARING_PUNISHMENT + ", previousMeans [" + fitStringPrev + "], current means [" + fitString + "], improvement [" + improvementString + "]");
			
			/** get a new punishment value randomly from the preset range */
			FITNESS_SHARING_PUNISHMENT = (FITNESS_SHARING_EXPLORATORY_MAX - FITNESS_SHARING_EXPLORATORY_MIN) * 
			   state.random[threadNum].nextFloat() + FITNESS_SHARING_EXPLORATORY_MIN; 			
		}

		/* get the max possible sum.  this is used to scale the sum values. */
		int maxSum = Integer.MIN_VALUE;
		int popSize = 0;
		float sumMean = 0;
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				popSize++;
				ind = (IntegerVectorIndividual) state.population.subpops[i].individuals[j];
				int sum = 0;
				for (int k=0; k<ind.genome.length; k+=2) {
					key = CipherWordGene.makeKeyFor(ind.genome[k],ind.genome[k+1], minWordLength);
					if (key != null) { // key is null when word/pos represents a wildcard
						value = count.get(key);
						sum += value.intValue();
					}
				}
				if (sum > maxSum) maxSum = sum;
				sumMean += sum;
			}
		}
		sumMean /= (float)state.population.subpops[0].individuals.length;
		
		float factor = 0;
		float factorTotal = 0;
		float factorVariance = 0;
		int sum;
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				ind = (IntegerVectorIndividual) state.population.subpops[i].individuals[j];
				sum = 0;
				for (int k=0; k<ind.genome.length; k+=2) {
					key = CipherWordGene.makeKeyFor(ind.genome[k],ind.genome[k+1], minWordLength);
					//key = CipherWordGene.makeSortedKeyFor(ind.genome, minWordLength);
					//key = ((CipherWordGene)ind).sortedKey;
					if (key != null) { // key is null when word/pos represents a wildcard
						value = count.get(key);
						sum += value.intValue();
					}
				}
				fit = (MultiObjectiveFitness) ind.fitness;
				factor = (float)sum/maxSum; // ranges from 0 to 1
				factorTotal += factor;
				factorVariance += (sumMean/maxSum - factor) * (sumMean/maxSum - factor);
				/*for (int k=0; k<fit.multifitness.length; k++) {
					//factor = (float)sum/(ind.genome.length/2);
					//if (factor >= 1)
					//fit.multifitness[k] /= factor;
					if (sum > 0 && !((CipherWordGene)ind).elite) { 
						//fit.multifitness[k] /= (1.0 + (factor * (popSize - 1))); // punishment, only on non-elites
						//fit.multifitness[k] /= sum; // punishment, only on non-elites
						fit.multifitness[k] /= (1.0 + factor * FITNESS_SHARING_PUNISHMENT);
						((CipherWordGene)ind).clusterCount = (float) (1.0 + factor * FITNESS_SHARING_PUNISHMENT);
						//((CipherWordGene)ind).clusterCount = (float) (1.0 + factor/FITNESS_SHARING_PUNISHMENT);
						//((CipherWordGene)ind).clusterCount = sum;
						//((CipherWordGene)ind).clusterCount = (float) (1.0 + (factor * (popSize - 1)));
					}
				}*/
				
			}
		}
		factorVariance /= (float)state.population.subpops[0].individuals.length;
		System.out.println("max tuple sum," + maxSum + ", mean tuple sum, " + sumMean + ", mean factor " + (sumMean/maxSum) + ", factorVariance: " + factorVariance + ", word diversity: " + words.size());
		
	}

	/** sorted key version - only looks as unique combinations of tuples satisfying minWordLength */
	public static void fitnessSharingViaWordPosSortedKey(EvolutionState state, int minWordLength) {
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		MultiObjectiveFitness fit;
		String key;
		Integer value;
		IntegerVectorIndividual ind;
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				ind = (IntegerVectorIndividual) state.population.subpops[i].individuals[j];
				//for (int k=0; k<ind.genome.length; k+=2) {
					//key = CipherWordGene.makeKeyFor(ind.genome[k],ind.genome[k+1], minWordLength);
					//key = CipherWordGene.makeSortedKeyFor(ind.genome, minWordLength);
					key = ((CipherWordGene)ind).sortedKey;
					if (key != null) { // key is null when word/pos represents a wildcard
						value = count.get(key);
						if (value == null) value = new Integer(0);
						value = new Integer(value.intValue()+1);
						count.put(key, value);
					}
				//}
			}
		}
		
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				ind = (IntegerVectorIndividual) state.population.subpops[i].individuals[j];
				key = ((CipherWordGene)ind).sortedKey;
				value = count.get(key);
				fit = (MultiObjectiveFitness) ind.fitness;
				if (value != null && value.intValue() > 0) {
					//for (int k=0; k<fit.multifitness.length; k++) {
					//	fit.multifitness[k] /= value.intValue();
					//}
				} else if (value != null && value.intValue() == 0) {
					System.out.println("ERROR - there really should have been at least one count for the key [" + key + "].  ");
				}
			}
		}
		
	}
	
	
	/* instead of trying to estimate similarity, compute it directly using the dreaded n^2 loop. */
	public static void fitnessSharingViaWordPos2(EvolutionState state, int minWordLength) {
		MultiObjectiveFitness fit;
		String key;
		Integer value;
		IntegerVectorIndividual ind;
		IntegerVectorIndividual ind2;
		HashSet<String> hash;
		int count = 0;
		float factor;
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				ind = (IntegerVectorIndividual) state.population.subpops[i].individuals[j];
				hash = new HashSet<String>();
				factor = 0;
				for (int k=0; k<ind.genome.length; k+=2) {
					key = CipherWordGene.makeKeyFor(ind.genome[k],ind.genome[k+1], minWordLength);
					if (key != null) { // key is null when word/pos represents a wildcard
						hash.add(key);
					}
				}
				for (int k=0; k<state.population.subpops[i].individuals.length; k++) {
					ind2 = (IntegerVectorIndividual) state.population.subpops[i].individuals[k];
					count = 0;
					for (int m=0; m<ind2.genome.length; m+=2) {
						key = CipherWordGene.makeKeyFor(ind2.genome[m],ind2.genome[m+1], minWordLength);
						if (key != null) { // key is null when word/pos represents a wildcard
							if (hash.contains(key)) count++;
						}
					}
					if (hash.size() > 0)
						factor += (float)count/hash.size();
				}
				fit = (MultiObjectiveFitness) ind.fitness;
				//for (int k=0; k<fit.multifitness.length; k++) {
				//	fit.multifitness[k] /= factor;
				//}
			}
		}
	}
	
	/** the simplistic fitness sharing scheme, that treats two genes as completely different if at least one char differs between their decoders or sortedKeys. */
	
	public static void fitnessSharingSimple(EvolutionState state, boolean useDecoder) {
		HashMap count = new HashMap();
		HashMap pop = new HashMap();
		String key;
		Frequency value;
		Subpopulation sp;
		CipherGene gene;
		//double bestFitness = 0.0; 
		Individual ind;
		//ZodiacVectorWordIndividual ind2;		
		for (int i=0; i<state.population.subpops.length; i++) {
			sp = state.population.subpops[i];
			for (int j=0; j<sp.individuals.length; j++) {
				  //ind2 = (ZodiacVectorWordIndividual)sp.individuals[j];
			      //key = sp.individuals[j].genotypeToString();
				  gene = (CipherGene)sp.individuals[j];
				  //key = gene.encoding;
				  key = (useDecoder ? gene.decoder.toString() : gene.sortedKey);
				  //if (gene.shitness > bestFitness) bestFitness = gene.shitness; 
				  //System.out.println("ENCODING " + key);
			      value = (Frequency) count.get(key);
			      pop.put(key, sp.individuals[j]);
			      if (value == null) {
			    	  value = new Frequency();
			    	  value.item = key;
			      }
			      value.total++;
			      count.put(key, value);
			}
		}
		MultiObjectiveFitness fit;
		for (int i=0; i<state.population.subpops.length; i++) {
			sp = state.population.subpops[i];
			for (int j=0; j<sp.individuals.length; j++) {
				  //ind2 = (ZodiacVectorWordIndividual)sp.individuals[j];
			      //key = sp.individuals[j].genotypeToString();
				  gene = (CipherGene) sp.individuals[j]; 
				  //key = gene.encoding;
				  key = (useDecoder ? gene.decoder.toString() : gene.sortedKey);
			      value = (Frequency) count.get(key);
			      fit = (MultiObjectiveFitness) sp.individuals[j].fitness;
			      //if (value == null) System.err.println("WTF, value is null for " + key);
			      if (fit == null) System.err.println("WTF, fit is null for " + key);
			      if (value != null) { // I STILL DONT KNOW WHY VALUE IS NULL SOMETIMES
				      //for (int k=0; k<fit.multifitness.length; k++) {
				    //	  fit.multifitness[k] /= ((float)value.total); // punish similarity!  DO NOT CONFORM!
				    //	  ((CipherWordGene)sp.individuals[j]).clusterCount = value.total; 
				    	  /*if (value.total > 1)
				    	  	state.output.message(sp.individuals[j].fitness.fitnessToStringForHumans() + ", " + sp.individuals[j].genotypeToStringForHumans());*/
				    //  }
				      //ind2 = (ZodiacVectorWordIndividual) sp.individuals[j];
				      //gene = ind2.getGene();
				      //gene.fitnessWord = gene.shitness / ((float)value.total);
				      //gene.clusterCount = value.total;
			      }
			}
		}
		//System.out.println("number of clusters: " + count.size());
		
		/*
		Object[] sorted = count.values().toArray();
		Arrays.sort(sorted, new FrequencyComparator());
*/
		/* uncomment if we want cluster info 
		Frequency f;
		for (int i=0; i<sorted.length; i++) {
			f = (Frequency) sorted[i];
			ind = (Individual) pop.get(f.item);
			ind2 = (ZodiacVectorWordIndividual)ind;
			gene = ind2.getGene();
			if (i<25 || gene.fitness >= 200.0 || gene.fitness >= bestFitness) {
				System.out.println("cluster top " + i + " total " + f.total + ", gene " + gene.getDebugLine());
			}
		}*/
		
	}

	/** for each given string, compute how many characters (respecting position) it shares with all the other strings in the set.
	 * idea suggested by Chris.
	 * 
	 * Example:
	 * 
	 * 	abc : score is 3 (for itself) + one other 'a' in pos 1, one other 'c' in position 2 = 5
	 * 	acc : score is 3 (for itself) + one other 'a' in pos 1, one other 'c' in position 2 = 5
	 *  def : score is 3 (for itself).  no other chars are shared.
	 *  
	 *  */ 
	public static int[] radixCompare(String[] decoders) {
		int[] buckets = new int[27]; // 'a' through 'z', and '?'
		int[] counts = new int[decoders.length];
		
		
		String d1;
		
		char index;
		for (int c=0; c<decoders[0].length(); c++) {
			buckets = new int[27]; // reset buckets for counting
			for (int i=0; i<decoders.length; i++) { // count each letter occurrance
				index = decoders[i].charAt(c);
				buckets[index=='?' ? 26 : index-'a']++;
			}
			for (int i=0; i<decoders.length; i++) { // count each letter occurrance
				index = decoders[i].charAt(c);
				counts[i] += buckets[index=='?' ? 26 : index-'a'];
			}
		}
		return counts;
	}
	
	/** a faster version of fitness sharing, using the radix-based comparison.  suggested by Chris. 
	 * @param sp subpopulation for which to apply fitness sharing (each subpopulation is treated independently) */
	private static void fitnessSharingFast(Subpopulation sp) {
		String[] decoders = new String[sp.individuals.length];
		
		/* pull out all the decoders from the genes */
		for (int i=0; i<sp.individuals.length; i++) {
		  decoders[i] = ((CipherGene)sp.individuals[i]).decoder.toString();
		}
		double totalShared = 0;
		double totalNonShared = 0;
		
		int totalConflict = 0;
		int[] counts = radixCompare(decoders);
		/* the fitnesses; they must be punished. if they conform to the crowd, they perish. */
		//ZodiacVectorWordIndividual zind;
		CipherGene gene;
		HashMap best = new HashMap();
		for (int i=0; i<sp.individuals.length; i++) {
		  //zind = (ZodiacVectorWordIndividual)sp.individuals[i];
		  gene = (CipherGene)sp.individuals[i];
		  gene.fitnessWord /= counts[i];
		  gene.clusterCount = counts[i];
		  
		  totalShared += gene.fitnessWord;
		  totalNonShared += gene.shitness;
		  totalConflict += gene.totalConflicts;
		  
		  if (gene.totalConflicts == 0) { // store all the best ones for display
		  	best.put(gene.decoder, gene);
		  }
		}

		System.out.println("== MEAN SHARED FITNESS: " + (totalShared / sp.individuals.length));
		System.out.println("== MEAN NON-SHARED FITNESS: " + (totalNonShared / sp.individuals.length));
		System.out.println("== MEAN TOTAL CONFLICT: " + ((float)totalConflict / sp.individuals.length));

		/* dump the best */
		Iterator it = best.keySet().iterator();
		String key;
		if (it.hasNext()) System.out.println("======================= FITTEST DUMP =========");
		while (it.hasNext()) {
			key = (String)it.next();
			gene = (CipherGene) best.get(key);
			System.out.println(gene.getDebugLine());
		}
		
	}	
	
	/** NO LONGER USED DUE TO POOR PERFORMANCE.  perform fitness sharing by comparing every individual to every other individual.  allele-based similarily is computed.
	 * @param state the evolution state
	 */
	private static void fitnessSharing(EvolutionState state) {
		HashMap pop = new HashMap();
		String d1;
		String d2;
		Subpopulation sp;
		CipherGene gene1;
		CipherGene gene2;
		double bestFitness = 0;
		float share;
		
		double totalShared = 0;
		double totalNonShared = 0;
		//ZodiacVectorWordIndividual zind1;		
		//ZodiacVectorWordIndividual zind2;		
		for (int i=0; i<state.population.subpops.length; i++) {
			sp = state.population.subpops[i];
			for (int j=0; j<sp.individuals.length; j++) {
				  //zind1 = (ZodiacVectorWordIndividual)sp.individuals[j];
				  gene1 = (CipherGene)sp.individuals[j];
				  d1 = gene1.decoder.toString();
				  if (gene1.shitness > bestFitness) bestFitness = gene1.shitness;
				  
				  share = 0;
					for (int k=0; k<sp.individuals.length; k++) {
					  //zind2 = (ZodiacVectorWordIndividual)sp.individuals[k];
					  gene2 = (CipherGene)sp.individuals[k];
					  d2 = gene2.decoder.toString();
					  share += CipherGene.sameness(d1, d2);
					}
					gene1.fitnessWord = gene1.shitness / share;
					gene1.clusterCount = share;
					
					if (gene1.shitness >= 200) { /* TODO: shouldn't hard code this. */
						pop.put(d1, gene1);
					}
					
					totalShared += gene1.fitnessWord;
					totalNonShared += gene1.shitness; 
			}
			System.out.println("== MEAN SHARED FITNESS: " + (totalShared / sp.individuals.length));
			System.out.println("== MEAN NON-SHARED FITNESS: " + (totalNonShared / sp.individuals.length));
			
		}
		
		Iterator it = pop.keySet().iterator();
		String key;
		
		System.out.println("======================= FITTEST DUMP =========");
		while (it.hasNext()) {
			key = (String)it.next();
			gene1 = (CipherGene) pop.get(key);
			System.out.println(gene1.getDebugLine());
		}
	}

	public static void testRadixCompare() {
		String[] d = new String[] {"abc","acc","def"};
		int[] i = radixCompare(d);
		for (int j=0; j<i.length; j++) {
			System.out.println(j+":" + i[j]);
		}
	}
	
	public static void testSpeed() {
		java.util.Date s = new java.util.Date();
		for (int i=0; i<10000*10000; i++) {
			if (i % 1000000 == 0) System.out.println(i);
		}
		java.util.Date e = new java.util.Date();
		
		System.out.println(e.getTime() - s.getTime() + " ms");
		System.out.println(1000*10000*10000/(e.getTime() - s.getTime()) + " per second.");
	}
	public static void main (String[] args) {
		//testRadixCompare();
		//testProblem();
		
		testSpeed();
	}
}
