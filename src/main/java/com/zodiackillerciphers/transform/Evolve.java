package com.zodiackillerciphers.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.Utils;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.generator.CandidateKey;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.homophones.ProgressiveSearch;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.RepeatingFragmentsFaster;
import com.zodiackillerciphers.old.Measurement;
import com.zodiackillerciphers.tests.EvenOddAndFactors;
import com.zodiackillerciphers.tests.jarlve.AZDecryptInterface;
import com.zodiackillerciphers.tests.jarlve.AZResultBean;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.FloatVectorIndividual;

public class Evolve extends Problem implements SimpleProblemForm {
	
	/** reference multiplicity */
	public static float Z340_MULTIPLICITY;
	static {
		Measurement m = new Measurement(Ciphers.cipher[0].cipher);
		Z340_MULTIPLICITY = m.multiplicity; 
	}
	
	/** the generation that was last fully evaluated.  NOT THREADSAFE. */
	public static int lastGenerationEvaluated = -1;

	/** evaluation for azdecrypt integration - NOT THREADSAFE */
	public void evaluatePrevious(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		
		if (state.generation != lastGenerationEvaluated) {
			process(state); // do them all at once
			lastGenerationEvaluated = state.generation;
		}
	}
	
	public static void process(EvolutionState state) {
		
		//System.out.println("WTF pop size " + state.population.subpops[0].individuals.length);
		//List<String> ciphers = new ArrayList<String>();
		for (Individual ind : state.population.subpops[0].individuals) {
			Operations ops = (Operations) ind;
			ops.performTransformations(false, Operations.MAX_OPERATIONS);
			ops.outputString = ops.outputString.replaceAll(" ", "");
			//String cipher = ops.outputString;
			//ciphers.add(cipher);
			//float rf = RepeatingFragmentsFaster.measure(cipher);
			//if (rf < -200) ops.lastObjectives[1] = 1;
			//ops.lastObjectives[1] = Operations.squash(200+rf);
		}
		AZDecryptInterface.process(state.population.subpops[0].individuals, "/Users/doranchak/Downloads/AZdecrypt0992/Ciphers", "/Users/doranchak/Downloads/AZdecrypt0992/Results");
		for (int i=0; i<state.population.subpops[0].individuals.length; i++) {
			Individual ind = state.population.subpops[0].individuals[i];
			Operations ops = (Operations) ind;
			ops.lastObjectives = new double[3];
			if (ops.azBean == null || ops.azBean.getScore() == 0) ops.lastObjectives[0] = 1;
			else {
				// normalize by length
				float adjusted = ops.azBean.getScore()*ops.outputString.length()/340;
				// then normalize by multiplicity
				Measurement m = new Measurement(ops.outputString);
				adjusted = adjusted * Z340_MULTIPLICITY / m.multiplicity;
				// simple scaling down to smaller range
				adjusted /= 10000;
				ops.lastObjectives[0] = Operations.squash(adjusted);
				ops.azBean.setMultiplicity(m.multiplicity);
				ops.azBean.setScoreAdjusted(adjusted);
			}
			
			if (ops.azBean == null || ops.azBean.getPlaintext() == null) {
				ops.lastObjectives[1] = 1;
			} else {
				Set<String> words = WordFrequencies.wordsWithMinPercentile(ops.azBean.getPlaintext(), 6, 10, 70);
				String wordsInfo = "";
				if (words != null) {
					wordsInfo += "Words: " + words.size() + " [";
					for (String word : words) 
						wordsInfo += word + " " + WordFrequencies.percentile(word) + " ";
					wordsInfo += "]";
				} else wordsInfo = "No words found."; 
				ops.lastObjectives[1] = Operations.squash(words.size());
				
				NGramsBean ng = new NGramsBean(2, ops.outputString);
				ops.lastObjectives[2] = Operations.squash(ng.numRepeats());
				
				System.out.println("result bean generation " + state.generation + " bigrams " + ng.numRepeats() + " " + ops.azBean + " " + wordsInfo);
			}
			((MultiObjectiveFitness) ind.fitness).setObjectives(state, ops.lastObjectives);
			ind.evaluated = true;
		}
		
		
	}
	
	/** evaluation of ciphers resulting from transposition operations */
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		Operations ops = (Operations) ind;
		ops.performTransformations(false, Operations.MAX_OPERATIONS);
		
		// now we have the transformed cipher text.  interpret the remainder of the genome as a key and compute the plaintext with it.
		// each operation took up 6 elements of the genome.
		ops.decode(Operations.MAX_OPERATIONS*6+1);
		
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, ops.measure(ops.outputString, ops.operationsActual, ops.plaintext));
		ind.evaluated = true;
	}
	

	public static String toString(Fitness fitness) {
		return toString((MultiObjectiveFitness) fitness);
	}
	public static String toString(MultiObjectiveFitness fitness) {
		String fit = "[";
		for (int i = 0; i < fitness.getNumObjectives(); i++)
			fit += fitness.getObjective(i) + " ";
		return fit + "]";
	}
	
	public static boolean same(MultiObjectiveFitness f1,
			MultiObjectiveFitness f2) {
		for (int i = 0; i < f1.getNumObjectives(); i++) {
			if (f1.getObjective(i) != f2.getObjective(i))
				return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
	}
}
