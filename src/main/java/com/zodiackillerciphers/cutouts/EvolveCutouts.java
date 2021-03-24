package com.zodiackillerciphers.cutouts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.ciphers.generator.CandidateKey;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.lucene.ZKDecrypto;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.FloatVectorIndividual;

public class EvolveCutouts extends Problem implements SimpleProblemForm {
	
	public static String[] cutouts = new String[] {
		"S", "THE", "AND", "DRES", "ME", "THE", "S", "GIRL", "THAT", "NE", "L", "UL", "U", "P", "NEAT", "I", "HER", "P"		
	};
	
	public static List<String> cutoutsList;
	static {
		cutoutsList = new ArrayList<String>();
		for (String s : cutouts) cutoutsList.add(s);
		WordFrequencies.init();
	}
	
	
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		
		FloatVectorIndividual f = (FloatVectorIndividual) ind;
		String line = interpret(f.genome, false);
		
		double[] objectives = ((MultiObjectiveFitness) ind.fitness)
				.getObjectives();

		objectives[0] = zkscore(line);
		objectives[1] = wordscore(line, 4);
		objectives[2] = wordscore(line, 5);
		objectives[3] = wordscore(line, 6);
		
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, objectives);
		ind.evaluated = true;
		
		if (state.generation % 200 == 0) {
			dump(state, f);
		}
	}
	
	String interpret(float[] g, boolean withSpaces) {
		List<String> list = new ArrayList<String>();
		list.addAll(cutoutsList);
		
		String line = "";
		for (int i=0; i<g.length; i++) {
			int which = CandidateKey.toInt(g[i], 0, list.size()-1);
			line += list.remove(which);
			if (withSpaces) line += " ";
		}
		return line;
	}
	
	float zkscore(String line) {
		return ZKDecrypto.calcscore(new StringBuffer(line));
	}
	
	float wordscore(String line, int len) {
		float sum = 0;
		
		Set<String> seen = new HashSet<String>();
		for (int i=0; i<line.length()-len+1; i++) {
			String word = line.substring(i,i+len);
			if (!seen.contains(word)) {
				float p = WordFrequencies.percentile(word);
				sum += p;
				//if (p > 0) d += word + " (" + p + ") ";
			}
			seen.add(word);
		}
		//if (dump) System.out.println(d);
		return sum;
	}
	
	String wordsFor(String line, int len) {
		String d = "Words of length [" + len + "]: ";
		Set<String> seen = new HashSet<String>();
		for (int i=0; i<line.length()-len+1; i++) {
			String word = line.substring(i,i+len);
			if (!seen.contains(word)) {
				float p = WordFrequencies.percentile(word);
				if (p > 0) d += word + " (" + p + ") ";
			}
			seen.add(word);
		}
		return d;
	}
	
	
	public void dump(EvolutionState state, FloatVectorIndividual f) {
		String str = interpret(f.genome, false);
		String line = "generation " + state.generation + " fitness " + dump(f.fitness) + " cutouts [" + interpret(f.genome, true) + "] line [" + str + "] words [" + wordsFor(str, 4) + ", " + wordsFor(str, 5) + ", " + wordsFor(str, 6) + "] genome " + f.genotypeToStringForHumans();
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
