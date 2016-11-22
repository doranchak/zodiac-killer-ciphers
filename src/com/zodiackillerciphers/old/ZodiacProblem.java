package com.zodiackillerciphers.old;

import ec.*;
import ec.multiobjective.*;
import ec.simple.*;
import ec.util.*;
import ec.vector.*;

public class ZodiacProblem extends Problem { //implements SimpleProblemForm {

	public CipherGene bestGeneByFitness = null;
	public CipherGene bestGeneByZodiacMatch = null;
	
	public void describe(Individual ind, EvolutionState state, int threadnum, int log, int verbosity) {
		// TODO Auto-generated method stub
    ZodiacVectorIndividual ind2 = (ZodiacVectorIndividual)ind;
    CipherGene gene = ind2.getGene();
    gene.fitness();
    //gene.debug();
    state.output.println(gene.getDebug(),
        verbosity,log);
	}

	public void evaluate(EvolutionState state, Individual ind, int threadnum) {
    if (ind.evaluated) return;

    if (!(ind instanceof ZodiacVectorIndividual))
        state.output.fatal("Whoa!  It's not a ZodiacVectorIndividual!!!",null);
    
    int sum=0;
    ZodiacVectorIndividual ind2 = (ZodiacVectorIndividual)ind;
    
    
    ind2.setGene(null);
    CipherGene gene = ind2.getGene();
    gene.fitness();
    
    ind2.setGene(gene);
    
    /* simple fitness version 
    if (!(ind2.fitness instanceof SimpleFitness))
        state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
    ((SimpleFitness)ind2.fitness).setFitness(state,
                                             /// ...the fitness...
                                             (float)gene.fitness,
                                             ///... is the individual ideal?  Indicate here...
                                             false);
                                             */
    
    /* multiobjective */
    if (!(ind2.fitness instanceof MultiObjectiveFitness))
      state.output.fatal("Whoa!  It's not a MultiObjectiveFitness!!!" + ind2.fitness.getClass().getName(),null);
    MultiObjectiveFitness fit = (MultiObjectiveFitness) ind2.fitness;

    
    /* uncomment all this for the n-wise fitness */ 
    float[] fitnesses = new float[3];
    
    
    //fitnesses[1] = ((float) gene.fitnessZodiacWordsFuzzy/20.0f);
    //if (fitnesses[1] > 1.0) fitnesses[1] = 0.0f;
    
    //fitnesses[2] = (float) (gene.fitnessZodiacWordsPairings/30.0);
    /*fitnesses[0] = (float)gene.fitnessZodiacWords/100.0f;
    if (fitnesses[0] > 1.0) fitnesses[0] = 0.0f;
    */
    //fitnesses[0] = (float) gene.fitnessAlphaFreq;
    //fitnesses[1] = (float) (gene.phiPlaintextMatch <= 0.025 ? 1 : (1 - gene.phiPlaintextMatch));
    //fitnesses[0] = (float) (gene.fitnessZodiacWords >= 56 ? 1.0 : gene.fitnessZodiacWords / 56.0);
    //fitnesses[1] = (float) gene.fitnessDigraph;
    //fitnesses[2] = (float) gene.fitnessDouble;
    //fitnesses[3] = (float) gene.fitnessTrigraph;
    //fitnesses[3] = (float) (gene.fitnessZodiacWords >= 55 ? 1.0 : gene.fitnessZodiacWords / 55.0);
    //if (fitnesses[0] >= 0.5 && fitnesses[1] >= 0.3 && fitnesses[2] >= 0.5 && fitnesses[3] >= 0.3) {
      //fitnesses[1] = (float) (gene.fitnessDictionaryWords >= 61 ? 1.0 : gene.fitnessDictionaryWords / 61.0);
      //fitnesses[2] = (float) (gene.fitnessDictionaryWordsInteresting >= 50 ? 1.0 : gene.fitnessDictionaryWordsInteresting / 50.0);
      //fitnesses[5] = (float) gene.fitnessWordLengthDistribution;
    //fitnesses[0] = (float) gene.fitnessCoverageDictionaryScaled;
    //fitnesses[0] = (float) (gene.fitnessCoverageDictionary * gene.fitnessDigraph);
    
    fitnesses[0] = (float) (gene.fitnessNGrams[1]);
    fitnesses[1] = (float) (gene.fitnessNGrams[2]);
    fitnesses[2] = (float) (gene.fitnessNGrams[3]);
    /*
    fitnesses[3] = (float) (gene.badNGramCount[2] >= 100 ? 0.0 : ((float) 100 - gene.badNGramCount[2])/100.0);
    fitnesses[4] = (float) (gene.badNGramCount[3] >= 200 ? 0.0 : ((float) 200 - gene.badNGramCount[3])/200.0);
    */
    
    //float f1 = (float) (gene.badNGramCount[2] >= 200 ? 0.0 : ((float) 200 - gene.badNGramCount[2])/200.0);
    //float f2 = (float) (gene.badNGramCount[3] >= 400 ? 0.0 : ((float) 400 - gene.badNGramCount[3])/400.0);
    //fitnesses[0] = (float) gene.fitness;
    
    //fitnesses[1] = (float) gene.fitnessCoverageDictionaryScaled;
    //fitnesses[1] = (float) (gene.fitnessZodiacWordsFuzzy >= 234282.0 ? 1.0 : (float) gene.fitnessZodiacWordsFuzzy / 234282.0f);
                                                            
    //}
    //else {
    //  fitnesses[4] = 0.0f;
    //  fitnesses[5] = 0.0f;
    	
    //}
    /*
    if (fitnesses[0] >= 0.5 && fitnesses[1] >= 0.5 && fitnesses[2] >= 0.25)
    	fitnesses[3] = (float) gene.fitnessCoverageZodiac;
    else
    	fitnesses[3] = 0.0f;*/
    
    //fitnesses[0] = (float) gene.fitnessCoverageZodiac;
    
    
    //fitnesses[3] = (float) (gene.fitnesscommonWordsUnique >= 20 ? 1.0 : gene.fitnesscommonWordsUnique / 20.0);
    /*
    fitnesses[2] = (float) gene.fitnessCoverageZodiac;
    */
    //fitnesses[1] = (float) ((2-gene.phiPlaintextMatch)/2.0);
    
    
    /* a single fitness.  kind of stupid in a multiobjective context. */
    //float[] fitnesses = new float[1];
    //fitnesses[0] = (float) gene.fitness;
    //fit.multifitness = fitnesses;
    
    ind2.evaluated = true;
		
	}

}
