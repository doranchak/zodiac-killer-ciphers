package com.zodiackillerciphers.old;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleShortStatistics;
import ec.steadystate.SteadyStateEvolutionState;
import ec.util.Output;
import ec.es.*;
import ec.*;
import java.util.Date;
import java.util.HashSet;
public class ZodiacESStatistics extends SimpleShortStatistics implements ec.steadystate.SteadyStateStatisticsForm {

	private static Date lastTime; 
	private static HashSet seen;
	
	/** if true, perform fitness sharing after each generation */
	public static boolean PERFORM_SHARING = true;
	/** if true, we perform elitism on unshared fitness rather than shared fitness */
	public static boolean ELITISM_UNSHARED = true;
	
	/** print a message, but also print the elapsed time in ms since the last message. */
	public void say(EvolutionState state, String msg) {
		
		if (lastTime == null) {
			lastTime = new Date();
			msg = "[init time] " + msg;
		}
		else {
			Date d = new Date();
			msg = "[" + (d.getTime() - lastTime.getTime()) + "] " + msg;
			lastTime = d;
		}
    state.output.println(msg,Output.V_NO_GENERAL,statisticslog);
	}
	@Override
	public void postBreedingStatistics(EvolutionState state) {
		say(state, "time postBreeding");
		say(state, "STATS: " + CipherWordGene.statsDump());
		CipherWordGene.statsInit();
		printGenomeDiversity(state,"postBreeding");
		super.postBreedingStatistics(state);
	}

	@Override
	public void preBreedingStatistics(EvolutionState state) {
		say(state, "time preBreeding");
		printGenomeDiversity(state,"preBreeding");
		super.preBreedingStatistics(state);
	}

	@Override
	public void preEvaluationStatistics(EvolutionState state) {
		say(state, "time preEval");
		printGenomeDiversity(state,"preEvaluation");
		super.preEvaluationStatistics(state);
	}

	public void postCheckpointStatistics(EvolutionState state) {
		say(state, "time postCheck");
		printGenomeDiversity(state,"postCheckpoint");
	 /*for (int i=0; i<state.population.subpops.length; i++) {
			Subpopulation sp = state.population.subpops[i];
			CipherGene g;
			for (int j=0; j<sp.individuals.length; j++) {
				ZodiacIndividual z = (ZodiacIndividual) sp.individuals[j];
				g = z.getGene();
		    state.output.println("Checkpoint pop dump " + j + ":" + g.getDebugLine(),Output.V_NO_GENERAL,statisticslog);
			}
		}*/
		//state.population.printPopulationForHumans(state, statisticslog, Output.V_NO_GENERAL);
	}

	public void postEvaluationStatistics(EvolutionState state) {
		
		say(state, "time postEval");
		
		
		/* call the fitness sharing scheme from here since all of the threads have been gathered.  */
		say(state, "time preSharing");
		if (PERFORM_SHARING)
			ZodiacWordProblem.fitnessSharingViaWordPos(state, 1, 0);
		say(state, "time postSharing");
		
		
		
		printGenomeDiversity(state,"postEvaluation");
		if (!Zodiac.STEADYSTATE) {
			Date timeStart = new Date();
			Date timeStop;
			String timing = "";
			
	    // print the best fitness per subpopulation.
	    Individual[] best_i = new Individual[state.population.subpops.length];  // quiets compiler complaints
	    Individual[] worst_i = new Individual[state.population.subpops.length];  // quiets compiler complaints
	    for(int x=0;x<state.population.subpops.length;x++)
	        {
	        best_i[x] = state.population.subpops[x].individuals[0];
	        worst_i[x] = state.population.subpops[x].individuals[0];
	        for(int y=1;y<state.population.subpops[x].individuals.length;y++) {
	            if (state.population.subpops[x].individuals[y].fitness.betterThan(best_i[x].fitness)) {
	                best_i[x] = state.population.subpops[x].individuals[y];
	            }
	            if (!state.population.subpops[x].individuals[y].fitness.betterThan(worst_i[x].fitness)) {
	              	worst_i[x] = state.population.subpops[x].individuals[y];
	            }
	        }
	        
	        }
	    
	    timeStop = new Date();
	    timing += "Best fitness stat took [" + (timeStop.getTime() - timeStart.getTime()) + "] ms.  "; 
	    timeStart = new Date();
	    
	    
	    
	    // print the best-of-generation individual
	    state.output.println("\n========================================================================================",Output.V_NO_GENERAL,statisticslog);
	    state.output.println("\nGeneration: " + state.generation,Output.V_NO_GENERAL,statisticslog);
	    state.output.println("Best Individual:",Output.V_NO_GENERAL,statisticslog);
	    for(int x=0;x<state.population.subpops.length;x++)
	        best_i[x].printIndividualForHumans(state,statisticslog,Output.V_NO_GENERAL);

	    state.output.println("Worst Individual:",Output.V_NO_GENERAL,statisticslog);
	    for(int x=0;x<state.population.subpops.length;x++)
	        worst_i[x].printIndividualForHumans(state,statisticslog,Output.V_NO_GENERAL);
			
			/*
			MuPlusLambdaBreeder b = (MuPlusLambdaBreeder) state.breeder;
			
			for (int i=0; i<b.comparison.length; i++) {
				state.output.println("comparison["+i+"]: " + b.comparison[i],Output.V_NO_GENERAL,statisticslog);
			}*/
	    timeStop = new Date();
	    timing += "Best-of-generation stat took [" + (timeStop.getTime() - timeStart.getTime()) + "] ms.  "; 
	    timeStart = new Date();
			
			
			/* compute a uniqueness ratio to indicate our population diversity.  also check the zodiac match measurements. */
	    double matchMax = -100.0;
	    CipherGene matchMaxGene = null;
	    double matchMean = 0.0;
			for (int i=0; i<state.population.subpops.length; i++) {
				Subpopulation sp = state.population.subpops[i];
				HashSet h = new HashSet();
				CipherGene g;
				for (int j=0; j<sp.individuals.length; j++) {
					ZodiacIndividual z = (ZodiacIndividual) sp.individuals[j];
					g = (CipherGene) z;
				
					//state.output.println("ENCODING " + g.encoding,Output.V_NO_GENERAL,statisticslog);
					//System.out.println("ES ENCODING " + g.encoding);
					
					//if (Zodiac.WORD_ENCODING == 2)
					//	h.add(g.encoding);
					//else
						h.add(g.decoder);
					
					if (Zodiac.CIPHER == 1) {
						matchMean += g.zodiacMatch;
						//System.out.println("g " + g.zodiacMatch);
						if (g.zodiacMatch > matchMax) {
							matchMax = g.zodiacMatch;
							matchMaxGene = g;
						}
					}
				}
				//System.out.println("ES CLUSTER SIZE: " + h.size());
				matchMean = (double) matchMean / sp.individuals.length;
				state.output.println("zodiacMatch max: " + matchMax + ", mean " + matchMean,Output.V_NO_GENERAL,statisticslog);
				if (Zodiac.CIPHER == 1) state.output.println("Best zodiacMatch gene: " + matchMaxGene.getDebugLine(),Output.V_NO_GENERAL,statisticslog);
				float dr = ((float)h.size()/sp.individuals.length);
				state.output.println("subpop["+i+"] size: " + sp.individuals.length + ", num unique: "+h.size()+", diversity ratio: " + dr,Output.V_NO_GENERAL,statisticslog);
				if (dr <= 0.01) {
					state.output.println("AT LEAST 99% OF THIS POPULATION HAS CONVERGED.",Output.V_NO_GENERAL,statisticslog);
					//System.exit(0);
				}
				
				// crossover/mutation success ratios
				if (CipherGene.TRACK_SUCCESS_RATES) {
					if (CipherGene.xoverTotal > 0)
						state.output.println("crossover success ratio: " + ((float)CipherGene.xoverGood/CipherGene.xoverTotal),Output.V_NO_GENERAL,statisticslog);
					if (CipherGene.mutationTotal > 0)
						state.output.println("mutation success ratio: " + ((float)CipherGene.mutationGood/CipherGene.mutationTotal),Output.V_NO_GENERAL,statisticslog);
					if (CipherGene.RESET_SUCCESS_RATES_EACH_GEN) {
						CipherGene.xoverGood = 0;
						CipherGene.xoverTotal = 0;
						CipherGene.mutationGood = 0;
						CipherGene.mutationTotal = 0;
					}
				}
			}
	    timeStop = new Date();
	    timing += "ZodiacMatch and diversity stat took [" + (timeStop.getTime() - timeStart.getTime()) + "] ms.  "; 
			state.output.println(timing,Output.V_NO_GENERAL,statisticslog);
			
			
			/** ok, now let's debug dump the pop if we're at a checkpoint interval, since we want to see what the pop looks like post-evaluation. */
      if (state.checkpoint && state.generation%state.checkpointModulo == 0) {
      	state.population.printPopulationForHumans(state, statisticslog, Output.V_NO_GENERAL);
      }

      /** if enabled, display dead end stats */ 
      if (CipherWordGene.TRUNCATE_DEAD_ENDS && CipherWordGene.deadEndHash != null) {
      	state.output.println("DEAD ENDS FOUND: " + CipherWordGene.deadEndHash.size(),Output.V_NO_GENERAL,statisticslog);
      }
			
			super.postEvaluationStatistics(state);
		}
		else { // stats for steady state
			//if (!((SteadyStateEvolutionState) state).firstTimeAround) {
			if (false) {
				double worst = Double.POSITIVE_INFINITY;
				double best = Double.NEGATIVE_INFINITY;
				double bestZm = Double.NEGATIVE_INFINITY;
				double total = 0;
				double totalZm = 0;
				
				HashSet dHash = new HashSet();
				HashSet iHash = new HashSet();
				int counter = 0;
				CipherGene bestGene = null;
				CipherGene bestGeneZm = null;
				CipherGene worstGene = null;
				CipherGene gene;
				for (int i=0; i<state.population.subpops.length; i++) {
					for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
						gene = (CipherGene)state.population.subpops[i].individuals[j];
						dHash.add(gene.decoder);
						iHash.add(state.population.subpops[i].individuals[j]);
						if (gene.fitnessWord < worst) {
							worst = gene.fitnessWord;
							worstGene = gene;
						}
						if (gene.fitnessWord > best) {
							best = gene.fitnessWord;
							bestGene = gene;
						}
						if (gene.zodiacMatch > bestZm) {
							bestZm = gene.zodiacMatch;
							bestGeneZm = gene;
						}
						total += gene.fitnessWord;
						totalZm += gene.zodiacMatch;
						counter++;
					}
				}
				
				if (bestGene != null) {
			    state.output.println("___MEAN FITNESS: " + (total/counter),Output.V_NO_GENERAL,statisticslog);
			    state.output.println("___MEAN ZODIAC MATCH: " + (totalZm/counter),Output.V_NO_GENERAL,statisticslog);
			    state.output.println("___BEST INDIVIDUAL: " + bestGene.getDebugLine(),Output.V_NO_GENERAL,statisticslog);
			    state.output.println("___BEST ZODIAC MATCH INDIVIDUAL: " + bestGeneZm.getDebugLine(),Output.V_NO_GENERAL,statisticslog);
			    state.output.println("___WORST INDIVIDUAL: " + worstGene.getDebugLine(),Output.V_NO_GENERAL,statisticslog);
			    state.output.println("___DECODER UNIQUENESS: " + ((float)dHash.size()/counter) + ", GENOME UNIQUENESS: " + ((float)iHash.size()/counter),Output.V_NO_GENERAL,statisticslog);
			    state.output.println("_ Free mem: " + Runtime.getRuntime().freeMemory(),Output.V_NO_GENERAL,statisticslog);

			    dHash = null; iHash = null;
			    
			    /*state.output.println(state.generation + " gen " + state.breeder + " breeder " + state.evaluator + " eval " + 
			    		state.finisher + " finish " + state.population + " pop " + state.population.subpops[0].individuals.length + " subpop ind len " +
			    		((SteadyStateEvolutionState)state).newIndividuals.length + " new ind len " + 
			    		((SteadyStateEvolutionState)state).generationSize + " gen size " 
			    		,Output.V_NO_GENERAL,statisticslog);*/
			    
			    state.output.println("==================================================================================================================",Output.V_NO_GENERAL,statisticslog);
					
				}
				
			}
			
		}
	}

	/** for steady-state, show some stats with each new individual evaluation */
	public void individualsEvaluatedStatistics(SteadyStateEvolutionState state) {
		
		/*for (int i=0; i<state.newIndividuals.length; i++) {
			Individual ind = state.population.subpops[0].individuals[state.newIndividuals[i]];
			String key = ((ZodiacVectorWordIndividual) ind).getGene().decoder;
			
			int c = 0;
			for (int j=0; j<state.population.subpops[0].individuals.length; j++) {
				if (key.equals(((ZodiacVectorWordIndividual) state.population.subpops[0].individuals[j]).getGene().decoder)) {
					c++;
				}
			}
			if (c>1) state.output.message("DUPE! " + key + ", count " + c);
		}
	}*/
	/*
		HashSet hash = new HashSet();
		HashSet hash2 = new HashSet();
		for (int j=0; j<state.population.subpops[0].individuals.length; j++) {
			hash.add(((ZodiacVectorWordIndividual) state.population.subpops[0].individuals[j]).getGene().decoder);
			hash2.add(state.population.subpops[0].individuals[j]);
		}
		if (hash.size() != 500 || hash2.size() != 500) {
			state.output.message("Hash1 size " + hash.size() + ", hash2 size " + hash2.size());
		}*/
	}
	
	/** prints the current populations' genome diversity */
	public void printGenomeDiversity(EvolutionState state, String msg) {
		int total = 0;
		HashSet<String> genomes = new HashSet<String>();
		for (int i=0; i<state.population.subpops.length; i++) {
			for (int j=0; j<state.population.subpops[i].individuals.length; j++) {
				if (state.population.subpops[i].individuals[j] instanceof CipherWordGene)
					genomes.add(((CipherWordGene)state.population.subpops[i].individuals[j]).encoding);
				else {
					genomes.add(state.population.subpops[i].individuals[j].genotypeToStringForHumans());
				}
				total++;
			}
		}
    state.output.println(msg + " | Genotype diversity: [" + genomes.size() + "/" + total + "] or " + ((float)100*genomes.size()/total) + "%.",Output.V_NO_GENERAL,statisticslog);
	}
	
	
	//@Override
	public void postInitialEvaluationStatistics(SteadyStateEvolutionState state) {
		say(state, "time postInitEval");
		// TODO Auto-generated method stub
		//super.postInitialEvaluationStatistics(state);
	}
	@Override
	public void postPostBreedingExchangeStatistics(EvolutionState state) {
		say(state, "time postPostBreedExch");
		// TODO Auto-generated method stub
		super.postPostBreedingExchangeStatistics(state);
	}
	@Override
	public void postPreBreedingExchangeStatistics(EvolutionState state) {
		say(state, "time postPreBreedExch");
		// TODO Auto-generated method stub
		super.postPreBreedingExchangeStatistics(state);
	}
	@Override
	public void preCheckpointStatistics(EvolutionState state) {
		say(state, "time preCheck");
		// TODO Auto-generated method stub
		super.preCheckpointStatistics(state);
	}
	//@Override
	public void preInitialEvaluationStatistics(SteadyStateEvolutionState state) {
		say(state, "time preInitEval");
		// TODO Auto-generated method stub
		//super.preInitialEvaluationStatistics(state);
	}
	@Override
	public void prePostBreedingExchangeStatistics(EvolutionState state) {
		say(state, "time prePostBreedExch");
		// TODO Auto-generated method stub
		super.prePostBreedingExchangeStatistics(state);
	}
	@Override
	public void prePreBreedingExchangeStatistics(EvolutionState state) {
		say(state, "time prePreBreedExch");
		// TODO Auto-generated method stub
		super.prePreBreedingExchangeStatistics(state);
	}
	
	
}

