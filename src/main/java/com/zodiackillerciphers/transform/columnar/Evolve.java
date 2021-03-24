package com.zodiackillerciphers.transform.columnar;

import java.util.Arrays;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.cosine.CosineSimilarity;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.RepeatingFragmentsFaster;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.FloatVectorIndividual;

public class Evolve extends Problem implements SimpleProblemForm {
	
	public static String CIPHER = Ciphers.cipher[0].cipher;
	
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		FloatVectorIndividual fvi = (FloatVectorIndividual) ind;
		Transposition t = new Transposition(fvi.genome);
		String cipher = t.decode(CIPHER);
		
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, objectives(cipher, t.n));
		ind.evaluated = true;
	}
	
	public static double[] objectives(String cipher, int n) {
		double[] obj = new double[4];
		double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher, 6, false);
		obj[0] = ioc;
		NGramsBean bean = new NGramsBean(2, cipher);
		obj[1] = bean.numRepeats();
		double[] CS = CosineSimilarity.measureSigmas(cipher, true);
		obj[2] = CS[0];
		obj[3] = 40-n; // reward smaller layouts
		return obj;
	}
	public static double[] objectives2(String cipher) {
		double[] obj = new double[2];
		double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher, 6, false);
		obj[0] = ioc;
		NGramsBean bean = new NGramsBean(2, cipher);
		obj[1] = bean.numRepeats();
		return obj;
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
	
	public static void testObjectives() {
		System.out.println(Arrays.toString(objectives(Ciphers.cipher[0].cipher, 0)));
	}
	public static void main(String[] arg) {
		testObjectives();
	}
	
}
