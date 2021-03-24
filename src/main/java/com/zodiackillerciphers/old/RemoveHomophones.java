/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package com.zodiackillerciphers.old;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ngrams.NGrams;

import ec.util.*;
import ec.*;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.*;
import ec.vector.*;

/**
 * Several standard Multi-objective benchmarks are implemented:
 * <ul>
 * <li>ZDT1: Zitzler, Deb & Thiele
 * <li>ZDT2: Zitzler, Deb & Thiele
 * <li>ZDT3: Zitzler, Deb & Thiele
 * <li>ZDT4: Zitzler, Deb & Thiele
 * <li>ZDT6: Zitzler, Deb & Thiele
 * <li>SPHERE: ftp.tik.ee.ethz.ch/pub/people/zitzler/ZLT2001a.pdf
 * <li>SCH: (Schaffer), (a.k.a. F1 in Srinivas & Deb); requires exactly 1
 * decision variables (genes)
 * <li>F2: (Schaffer), (Srinivas & Deb), (Coello Coello & Cortes); requires
 * exactly 1 decision variables (genes)
 * <li>unconstrained F3: Schaffer, Srinivas & Deb (Chankong & Haimes); requires
 * exactly 2 decision variables (genes)
 * <li>QV: Quagliarella & Vicini
 * <li>FON: Fonseca & Fleming; requires exactly 3 decision variables (genes)
 * <li>POL: Poloni; requires exactly 2 decision variables (genes)
 * <li>KUR: Kursawe from the Errata of Zitzler's TIK-Report 103:
 * "SPEA2: Improving the Strength Pareto Evolutionary Algorithm" (note that many
 * different versions are described in the literature).
 * </ul>
 * 
 * <p>
 * <b>Parameters</b><br>
 * <table>
 * <tr>
 * <td valign=top><i>base</i>.<tt>type</tt><br>
 * <font size=-1>String, one of: zdt1, zdt2, zdt3, zdt4, zdt6, sphere, sch, fon,
 * qv, pol, kur, f1, f2, unconstrained-f3</font></td>
 * <td valign=top>The multi-objective optimization problem to test against.</td>
 * </tr>
 * </table>
 * 
 * @author Gabriel Catalin Balan
 */

public class RemoveHomophones extends Problem implements SimpleProblemForm {

	static int which = 1;
	static String alphabet = Zodiac.alphabet[which];
	static int len = 54;
	
	
	public void evaluate(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum) {
		if (!(ind instanceof RemoveHomophonesIndividual))
			state.output
					.fatal("The individuals for this problem should be RemoveHomophonesIndividual.");

		RemoveHomophonesIndividual temp = (RemoveHomophonesIndividual) ind;
		int[] genome = temp.genome;
		if (genome.length != len)
			state.output
					.fatal("Expected genome length of " + len + " but found " + genome.length);
		
		double[] objectives = ((MultiObjectiveFitness) ind.fitness)
				.getObjectives();

		int correct = 0;
		
		Map<Character, Character> map = new HashMap<Character, Character>();
		StringBuffer na = new StringBuffer(alphabet.length());
		for (int i=0; i<genome.length; i++) {
			map.put(alphabet.charAt(i), alphabet.charAt(genome[i]));
			na.append(alphabet.charAt(genome[i]));
			if (Zodiac.solutionKey.get(new Character(alphabet.charAt(i))) == Zodiac.solutionKey.get(alphabet.charAt(genome[i]))) {
				correct++;
			}
		}
		//temp.newAlphabet = na.toString();
		//temp.correctness = (float) correct / len;
		
		StringBuffer cipher = new StringBuffer(Zodiac.cipher[which].length());
		for (int i=0; i<Zodiac.cipher[which].length(); i++) {
			cipher.append(map.get(Zodiac.cipher[which].charAt(i)));
		}
		
		//temp.newCipher = cipher.toString();
		
		int[] totals = new int[3];
		//NGrams.countNgrams(temp.newCipher, 5, totals);
		objectives[0] = totals[1];
		objectives[1] = totals[2];
		
		
		
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, objectives);
		ind.evaluated = true;
	}

	static void testSolution() {

		System.out.println("Original cipher:");
		NGrams.testSolved();
		
		System.out.println("Evolved cipher:");
		String cipher = "dI!!f!GOIGfGGdzGOIG!feG7fd+!ffQfGf+fGKGfG8fGLffGfKG!K!dIO!fGfGI#GffcGKY+ffdfOd7+GIfeGf!fefdffGffzeKG!KGfLfGcffefeOGf!IOYd!!IOfzfc++GKGGGOfffdfGIfQeKY+fdIO!fG!OGfOfKfcG+G7fOdfGIfK!LYGfKGf+fdfGHGGfOdf!fzfff!K+eGGLIYGfGcf+GeffQfGKdeY+fffGfK!GGdGfdIOGIO!GGLf!KG!ff#Gff8fGeIIf+fGG!Oc!dIIf#f!IOGdfzfIfH7IeO!fdfdOIfGKGGO!HdGfHK!fIGffeGfcHGGfdIIYLH+Q7OzdGzffdO8YQGfHfdIIffK!fGzf7IeOfffGLfHeff!fO!fIfGczfdIfffc++GGGfG";
		int[] totals = new int[3];
		
		for (int n=2; n<6; n++) { 
			NGrams.countNgrams(cipher, n, totals);
			System.out.println(n+","+totals[0] + ","+ totals[1] + "," + totals[2]);
		}
		
	}

	public static void main(String[] args) {
		testSolution();
	}
}
