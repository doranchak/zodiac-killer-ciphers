package com.zodiackillerciphers.ciphers.generator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.Utils;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.ProgressiveSearch;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.ngrams.RepeatingFragmentsFaster;
import com.zodiackillerciphers.pivots.PivotUtils;
import com.zodiackillerciphers.tests.EvenOddAndFactors;
import com.zodiackillerciphers.tests.PrimePhobia;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.MeasurementsBean;
import com.zodiackillerciphers.transform.Operations;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.FloatVectorIndividual;

public class Evolve extends Problem implements SimpleProblemForm {
	
	//public Map<String, String> seen = new HashMap<String, String>();
	public static boolean showMutatorPerformance = false;
	
	public static double pcsz340 = HomophonesNew.perfectCycleScoreFor(2, Ciphers.cipher[0].cipher, 3);
	public static double homscore340 = JarlveMeasurements.homScore(Ciphers.cipher[0].cipher);
	
	/*
	static List<CandidateKey> pareto;
	static {
		pareto = new ArrayList<CandidateKey>();
	}

	public void paretoProcess(CandidateKey key) {
		boolean add = true; boolean nondominated = true;
		MultiObjectiveFitness  currentFitness = null;
		for (int i = pareto.size() - 1; i >= 0; i--) {
			CandidateKey frontMember = pareto.get(i);
			MultiObjectiveFitness frontMemberFitness = (MultiObjectiveFitness) frontMember.fitness;
			currentFitness = (MultiObjectiveFitness) key.fitness;

			if (currentFitness.paretoDominates(frontMemberFitness)) {
				System.out.println("New dominator "
						+ toString(currentFitness) + " better than "
						+ toString(frontMemberFitness));
				pareto.remove(i);
				nondominated = false;
			} else if (frontMemberFitness.paretoDominates(currentFitness)
					|| same(frontMemberFitness, currentFitness)) {
				add = false;
				nondominated = false;
				break;
			}
		}
		if (add) {
			if (currentFitness != null && nondominated) System.out.println("Non-dominated "
					+ toString(currentFitness));
			pareto.add((CandidateKey) key.clone());
			System.out.println("Added to pareto: " + toString(key.fitness) + ", " + key);
			System.out.println("New pareto size: " + pareto.size());
		}		
	}
	*/
	
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		
		CandidateKey key = (CandidateKey) ind;
		if (key.isBroken()) {
			System.err.println("WTF: key is bad " + key);
			System.exit(-1);
		}
		
		key.expressGenome();
		
		double[] objectives = ((MultiObjectiveFitness) ind.fitness)
				.getObjectives();

		//Utils.DEBUG = true;
		boolean valid = objectives(objectives, key);
		//key.objectivesActual = (double[]) key.objectives().clone();
		if (!valid) {
			for (int i=0; i<objectives.length; i++) 
				objectives[i] = Float.MAX_VALUE;
			//System.out.println("ERROR: " + key);
			//System.exit(-1);
		}
			//if (showMutatorPerformance) System.out.println(key.lastMutator + " hit");
		//} else {
		//	if (showMutatorPerformance) System.out.println(key.lastMutator + " miss");
		//}
/*else {
			// force variety by limiting to at most 10 individuals per candidate plaintext.
			if (limit(key, state))
				for (int i=0; i<objectives.length; i++) 
					objectives[i] = Float.MAX_VALUE;
		}*/
		
		/*else {
			// now apply fitness sharing to punish overly similar populations
			float sim = similarity(state, key);
			key.similarity = sim;
			for (int i=0; i<objectives.length; i++) {
				objectives[i] = objectives[i]/(1-sim);	 // should we use a "temperature" to gradually reduce this effect, to encourage final convergence?			
			}
		}*/
		String msg = "SMEG EVAL ind " + Utils.info(ind);
		Utils.debug(msg);

		double compFit = CandidateKey.compositeFitnessAsDouble(objectives);
		
		/*if (showMutatorPerformance) {
			if (compFit < key.lastFitness) {
				System.out.println(key.lastMutator + " better");
			}
			else System.out.println(key.lastMutator + " worse");
		}*/
		//System.out.println("eval " + key.hashCode() + " " + Arrays.toString(objectives));
		
		/*
		if (key.lastObjectives != null) {
			for (int i=0; i<objectives.length; i++) {
				if (objectives[i] < key.lastObjectives[i]) {
					System.out.println("mutator " + key.lastMutator + " improved objective" + i + " from " + key.lastObjectives[i] + " to " + objectives[i]);
				}
			}
		}*/
		
		
		/*if (objectives[0] == Float.MAX_VALUE && key.lastFitness != Float.MAX_VALUE) {
			System.out.println("mutator " + key.lastMutator + " ruined individual that had fitness " + key.lastFitness);
		}*/
		//key.lastFitness = compFit;
		//key.lastObjectives = objectives.clone();
		
		/*
		if (key.plain.index > 400000) {
			for (int i=0; i<objectives.length; i++) objectives[i] *= 10000;
		}*/
		
		/*int dupes = dupes(state, (FloatVectorIndividual)ind, 0.8f);
		if (dupes > 9 && !key.archive) {
			for (int i=0; i<objectives.length; i++) 
				objectives[i] = Float.MAX_VALUE;
		}*/
		
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, objectives);
		ind.evaluated = true;
		//paretoProcess(key);
		
		//System.out.println("hashes " + key.genome.hashCode() + " " + key.cipher.cipher.hashCode() + " " + Arrays.toString(((MultiObjectiveFitness)key.fitness).objectives));
	
		/*
		String s = seen.get(key.cipher.cipher);
		String obj =  Arrays.toString(key.objectives()) + " " + key.compositeFitness() + key.c2pErrors() + key.plain.toString() + " " + key.cipher.toString() + " " + key.dumpp2c() + " " + key.dumpc2p() + " words [" + key.words() + "]";

		if (s == null) {
			s = obj; 
		} else {
			if (!s.equals(obj)) {
			System.out.println("Already saw  with different result.  " + Utils.info(key));
			System.out.println("Seen: " + s);
			System.out.println("Current: " + obj);
			System.exit(-1);
			}
		}
		seen.put(key.cipher.cipher, s);*/
	}

	public static boolean objectives(double[] objectives, CandidateKey key) {
		float errors = key.errors(false); // side effect: computes transcriptionErrors, used as an objective
		if (errors > 0) Utils.debug("Errors: " + errors);
		if (errors > 0) return false;
		String cipher = key.c();
		return objectives56(objectives, cipher);
	}
	/** same as 54 but with comparison of hom cycles */
	public static boolean objectives56(double[] objectives, String cipher) {
		//Map<Character, Integer> countMap = Ciphers.countMap(cipher);
		// objectives:
		// 1-grams distribution distance to Z340's
		objectives[0] = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
		// 2-grams distribution distance to Z340's (count only).  include trigram comparison.
		objectives[1] = CandidateKey.measureNgramDistance(cipher);
		NGramsBean ng = new NGramsBean(3, cipher);
		objectives[1] = (objectives[1] + Math.abs(2-ng.numRepeats()))/2; 
		// match on period 19 numRepeats (37) and count (67) 
		String re = Periods.rewrite3(cipher, 19);
		ng = new NGramsBean(2, re);
		//int sum = ng.count() + ng.numRepeats();
		objectives[2] = Math.abs(37-ng.numRepeats());
		// match on period 15 fliped numRepeats too (Z340 has 41 at period 15)
		String flip = "";
		for (int row=0; row<20; row++) {
			for (int col=16; col>=0; col--) {
				flip += cipher.charAt(row*17+col);
			}
		}
		re = Periods.rewrite3(flip, 15);
		ng = new NGramsBean(2, re);
		//sum = ng.count() + ng.numRepeats(); 
		objectives[3] = Math.abs(41-ng.numRepeats()); 
		
		// perfect cycle score (L=2)
		double score1 = Math.abs(pcsz340 - HomophonesNew.perfectCycleScoreFor(2, cipher, 3));
		//double score2 = Math.abs(homscore340-JarlveMeasurements.homScore(cipher));
		//objectives[3] = (score1+score2)/2; // average of two ways to measure cycles
		objectives[4] = score1; 

		// make a composite of 3 misc measurements
		/*double m1 = Math.abs(MeasurementsBean.referenceBean.evenOddSpread - MeasurementsBean.evenOddSpread(cipher)); 
		double m2 = Math.abs(MeasurementsBean.referenceBean.olsonNonRepeats - CandidateKey.measureLineNonRepeats(cipher)); 
		int[] np1 = PrimePhobia.nonprimes(cipher, Ciphers.countMap(cipher));
		int[] np2 = MeasurementsBean.referenceBean.nonprimes;
		double dist = 0;
		for (int j=0; j<np1.length; j++) {
			dist += Math.pow(np1[j]-np2[j],2);
		}
		double m3 = Math.sqrt(dist);
		objectives[4] = (m1+m2+m3)/3;*/
		
		return true;
	}
	/** same as 54 but with comparison of hom cycles */
	public static boolean objectives55(double[] objectives, String cipher) {
		int i=0;
		//Map<Character, Integer> countMap = Ciphers.countMap(cipher);
		// objectives:
		// 1-grams distribution distance to Z340's
		objectives[i++] = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
		// 2-grams distribution distance to Z340's (count only)
		objectives[i++] = CandidateKey.measureNgramDistance(cipher);
		// match on period 19 numRepeats (37) and count (67) 
		String re = Periods.rewrite3(cipher, 19);
		NGramsBean ng = new NGramsBean(2, re);
		int sum = ng.count() + ng.numRepeats();
		objectives[i++] = Math.abs(104-sum);
		// perfect cycle score (L=2)
		objectives[i++] = Math.abs(pcsz340 - HomophonesNew.perfectCycleScoreFor(2, cipher, 3));
		return true;
	}
	/*
	 * smokie's question: Is it possible to make a message that is not
	 * transposed which has +/- 70 period 19 bigram repeats? If you have all 1:1
	 * substitutes, then the answer is yes. But can it be done with 63 symbols?
	 */
	public static boolean objectives54(double[] objectives, String cipher) {
		int i=0;
		//Map<Character, Integer> countMap = Ciphers.countMap(cipher);
		// objectives:
		// 1-grams distribution distance to Z340's
		objectives[i++] = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
		// 2-grams distribution distance to Z340's (count only)
		objectives[i++] = CandidateKey.measureNgramDistance(cipher);
		// match on period 19 numRepeats (37) and count (67) 
		String re = Periods.rewrite3(cipher, 19);
		NGramsBean ng = new NGramsBean(2, re);
		int sum = ng.count() + ng.numRepeats();
		objectives[i++] = Math.abs(104-sum);
		return true;
	}
	// test 4 objectives at a time
	public static boolean objectives53(double[] objectives, CandidateKey key) {
		float errors = key.errors(false); // side effect: computes transcriptionErrors, used as an objective
		if (errors > 0) return false;
		int i=0;
		String cipher = key.c();
		Map<Character, Integer> countMap = Ciphers.countMap(cipher);
		// objectives:
		// 1-grams distribution distance to Z340's
		objectives[i++] = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
		// 2-grams distribution distance to Z340's (but modify it to use products of symbol counts,
		// since some 2-grams are much easier to repeat than others
		objectives[i++] = CandidateKey.measureNgramDistance(2, cipher);
		// ditto for 3-grams
		objectives[i++] = CandidateKey.measureNgramDistance(3, cipher);
		// even/odd spread
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.evenOddSpread - MeasurementsBean.evenOddSpread(cipher)); 
		return true;
	}
	public static double[] objectives50(String cipher) {
		CandidateKey key = new CandidateKey();
		key.cipher = new CandidateCiphertext();
		key.cipher.cipher = new StringBuffer(cipher);
		double[] objectives = new double[13];
		objectives50(objectives, key);
		return objectives;
	}

	// 11 objectives - tried to take out the slow/redundant ones
	public static boolean objectives51(double[] objectives, CandidateKey key) {
		float errors = key.errors(false); // side effect: computes transcriptionErrors, used as an objective
		if (errors > 0) return false;
		int i=0;
		String cipher = key.c();
		Map<Character, Integer> countMap = Ciphers.countMap(cipher);
		// objectives:
		// 1-grams distribution distance to Z340's
		objectives[i++] = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
		// 2-grams distribution distance to Z340's (but modify it to use products of symbol counts,
		// since some 2-grams are much easier to repeat than others
		objectives[i++] = CandidateKey.measureNgramDistance(2, cipher);
		// ditto for 3-grams
		objectives[i++] = CandidateKey.measureNgramDistance(3, cipher);
		// even/odd spread
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.evenOddSpread - MeasurementsBean.evenOddSpread(cipher)); 
		// overall repeating fragment improvement (we want it to be close to zero)
		objectives[i++] = RepeatingFragmentsFaster.measure(cipher);
		// unique per-line symbol counts for Olson lines
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.olsonNonRepeats - CandidateKey.measureLineNonRepeats(cipher)); 
		// jarlve nonrepeat score 2 
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.jarlveNonRepeatScore2 - JarlveMeasurements.nonrepeatAlternate(cipher)); 
		// perfect cycle score (L=2)
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.perfectCycleScore2 - HomophonesNew.perfectCycleScoreFor(2, cipher, 3));
		// pivot score
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.pivotScore - PivotUtils.pivotScore(cipher, countMap));
		// prime phobia
		int[] np1 = PrimePhobia.nonprimes(cipher, countMap);
		int[] np2 = MeasurementsBean.referenceBean.nonprimes;
		double dist = 0;
		for (int j=0; j<np1.length; j++) {
			dist += Math.pow(np1[j]-np2[j],2);
		}
		objectives[i++] = Math.sqrt(dist);
		// transcription errors
		objectives[i++] = Math.abs(TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors);
		return true;
		
	}
	// 14 objectives
	public static boolean objectives50(double[] objectives, CandidateKey key) {
		float errors = key.errors(false); // side effect: computes transcriptionErrors, used as an objective
		if (errors > 0) return false;
		int i=0;
		String cipher = key.c();
		Map<Character, Integer> countMap = Ciphers.countMap(cipher);
		// objectives:
		// 1-grams distribution distance to Z340's
		objectives[i++] = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
		// 2-grams distribution distance to Z340's (but modify it to use products of symbol counts,
		// since some 2-grams are much easier to repeat than others
		objectives[i++] = CandidateKey.measureNgramDistance(2, cipher);
		// ditto for 3-grams
		objectives[i++] = CandidateKey.measureNgramDistance(3, cipher);
		// even/odd spread
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.evenOddSpread - MeasurementsBean.evenOddSpread(cipher)); 
		// repeating 2-gram distribution for all periods (distance from z340)
		objectives[i++] = Periods.periodicBigramDistributionDistance(cipher);
		// overall repeating fragment improvement (we want it to be close to zero)
		objectives[i++] = RepeatingFragmentsFaster.measure(cipher);
		// unique per-line symbol counts for Olson lines
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.olsonNonRepeats - CandidateKey.measureLineNonRepeats(cipher)); 
		// jarlve nonrepeat score 1
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.jarlveNonRepeatScore1 - JarlveMeasurements.nonrepeat(cipher)); 
		// jarlve nonrepeat score 2 
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.jarlveNonRepeatScore2 - JarlveMeasurements.nonrepeatAlternate(cipher)); 
		// perfect cycle score (L=2)
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.perfectCycleScore2 - HomophonesNew.perfectCycleScoreFor(2, cipher, 3));
		// jarlve m_2s_cycles	
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.jarlveHomScore - JarlveMeasurements.homScore(JarlveMeasurements.cipherToShort(cipher), 5));
		// pivot score
		objectives[i++] = Math.abs(MeasurementsBean.referenceBean.pivotScore - PivotUtils.pivotScore(cipher, countMap));
		// prime phobia
		int[] np1 = PrimePhobia.nonprimes(cipher, countMap);
		int[] np2 = MeasurementsBean.referenceBean.nonprimes;
		double dist = 0;
		for (int j=0; j<np1.length; j++) {
			dist += Math.pow(np1[j]-np2[j],2);
		}
		objectives[i++] = Math.sqrt(dist);
		// transcription errors
		objectives[i++] = Math.abs(TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors);
		return true;
	}
	public static boolean objectives49(double[] objectives, CandidateKey key) {
		boolean success = objectives45(objectives, key);
		if (!success) return false;
		objectives[8] = (float) (1-Operations.squash(key.measureBigrams19()));
		objectives[9] = (float) (1-Operations.squash(key.measureNonRepeatAlternateDistance()));
		
		return true;
	}
	public static boolean objectives47(double[] objectives, CandidateKey key) {
		return objectives45(objectives, key);
	}
	
	// as val goes down, squashed value goes down
	public static float squash1(float val) {
		return (float) (1-Operations.squash(val));
	}
	
	// as val goes up, squashed value goes down
	public static float squash2(float val) {
		return (float) (Operations.squash(val));
	}
	
	public static boolean objectives45(double[] objectives, CandidateKey key) {
		float errors = key.errors(false); // side effect: computes transcriptionErrors, used as an objective
		if (errors > 0) return false;
		
		objectives[0] = squash1(key.measureUnigramDistance());
		
		objectives[1] = squash1(key.measureNgramDistance());
		/*objectives[2] = ProgressiveSearch.measureDifferences(
				key.cipher.cipher.toString(),
				Ciphers.alphabet(key.cipher.cipher.toString()));*/
		objectives[2] = squash1(Math.abs(2150.717455652698f - (float) JarlveMeasurements.homScore(key.cipher.cipher.toString())));
		objectives[3] = squash1(key.measurePrimePhobia());
		objectives[4] = squash1(Math.abs(TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors));
		objectives[5] = squash1(EvenOddAndFactors.measureEvenOddNgramsCountsOnly(key.cipher.cipher.toString()));
		objectives[6] = squash1(key.measureLineRepeats());
		objectives[7] = squash1(key.measureNonRepeatDistance());
		return true;
		
	}
	public static boolean objectives44(double[] objectives, CandidateKey key) {
		float errors = key.errors(false); // side effect: computes transcriptionErrors, used as an objective
		if (errors > 0) return false;
		objectives[0] = key.measureUnigramDistance();
		objectives[1] = key.measureNgramDistance();
		objectives[2] = key.measureCycleComparison();
		objectives[3] = key.measurePrimePhobia();
		objectives[4] = TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors;
		objectives[5] = EvenOddAndFactors.measureEvenOddNgramsCountsOnly(key.cipher.cipher.toString());
		objectives[6] = key.measureLineRepeats();
		objectives[7] = key.measureNonRepeatDistance();
		return true;
		
	}
	
	
	public static boolean objectives41(double[] objectives, CandidateKey key) {
		float errors = key.errors(false); // side effect: computes transcriptionErrors, used as an objective
		if (errors > 0) return false;
		objectives[0] = key.measureUnigramDistance();
		objectives[1] = key.measureNgramDistance();
		objectives[2] = key.measureHomophoneCycleErrors();
		objectives[3] = key.measurePrimePhobia();
		objectives[4] = TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors;
		objectives[5] = EvenOddAndFactors.measureEvenOddNgramsCountsOnly(key.cipher.cipher.toString());
		objectives[6] = key.measureLineRepeats();
		return true;
		
	}
	public static boolean objectives32(double[] objectives, CandidateKey key) {
		float errors = key.errors(false); // side effect: computes transcriptionErrors, used as an objective
		if (errors > 0) return false;
		
		objectives[0] = key.measureUnigramDistance();
		objectives[1] = key.measureNgramDistance();
		objectives[2] = key.measureHomophoneCycleErrors();
		//Utils.debug("key.hashcode " + key.hashCode() + " key.cipher.cipher.hashcode " + key.cipher.cipher.hashCode() + " obj " + objectives[2]);
		objectives[3] = key.measurePrimePhobia();
		objectives[4] = TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors;
		objectives[5] = EvenOddAndFactors.measureEvenOddNgrams(key.cipher.cipher.toString());
		return true;
	}

	public static void objectives31(double[] objectives, CandidateKey key) {
		objectives[0] = EvenOddAndFactors.measureEvenOddNgrams(key.cipher.cipher.toString());
	}

	public static void objectives30(double[] objectives, CandidateKey key) {
		objectives[0] = key.measureUnigramDistance();
	}

	public static void objectives29(double[] objectives, CandidateKey key) {
		objectives[0] = key.measurePrimePhobia();
	}

	public static void objectives28(double[] objectives, CandidateKey key) {
		objectives[0] = key.measureHomophoneCycleErrors();
	}

	public static void objectives27(double[] objectives, CandidateKey key) {
		objectives[0] = key.measureNgramDistance();
	}

	public static void objectives26(double[] objectives, CandidateKey key) {
		objectives25(objectives, key);
	}
	public static void objectives25(double[] objectives, CandidateKey key) {
		objectives[0] = key.measureUnigramDistance();
		objectives[1] = key.measureNgramDistance();
		objectives[2] = key.measureHomophoneCycleErrors();
		objectives[3] = key.measurePrimePhobia();
		objectives[4] = TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors;
		objectives[5] = EvenOddAndFactors.measureEvenOddNgrams(key.cipher.cipher.toString());
	}
	public static void objectives24(double[] objectives, CandidateKey key) {
		objectives[0] = EvenOddAndFactors.measureEvenOddNgrams(key.cipher.cipher.toString());
	}

	public static void objectives23(double[] objectives, CandidateKey key) {
		objectives[0] = key.measureUnigramDistance();
		objectives[1] = key.measureNgramDistance();
		objectives[2] = key.measureHomophoneCycleErrors();
		objectives[3] = key.measurePrimePhobia();
		objectives[4] = TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors;
	}

	public static void objectives22(double[] objectives, CandidateKey key) {
		objectives[0] = TranscriptionErrors.MAX_ERRORS - key.transcriptionErrors;
	}

	public static void objectives21(double[] objectives, CandidateKey key) {
		
		//objectives[1] = key.measureRepeatingFragmentDistance();
		//objectives[2] = key.measureNonRepeatDistance();
		//objectives[2] = 0-similarity(state, key);
		
		objectives[0] = key.measureUnigramDistance();
		objectives[1] = key.measureNgramDistance();
		objectives[2] = key.measureHomophoneCycleErrors();
		objectives[3] = key.measurePrimePhobia();
	}
	
	/** prevent population from overrepresenting any single candidate plaintext */
	public boolean limit(CandidateKey key1, EvolutionState state) {
		//String msg = "limit " + System.identityHashCode(key1) + " ";
		if (key1.plain == null) return false;
		Individual[] inds = state.population.subpops[0].individuals;
		int MAX = 10;
		int count = 0;
		for (Individual ind : inds) {
			CandidateKey key2 = (CandidateKey) ind;
			//msg += System.identityHashCode(key2) + " ";
			if (System.identityHashCode(key1) == System.identityHashCode(key2)) {
				//System.out.println(msg);
				return false;
			}
			int index1 = key1.plain.index;
			int index2 = CandidatePlaintextLoader.get(CandidateKey.indexFromGenomeValue(key2.genome[0])).index;
			//msg += "(" + index1 + "=" +index2 + ") ";
			if (index1 == index2) { // compute the index for the 2nd one because its genome might not be expressed yet
				count++;
				if (count > MAX) {
					//msg += " MAX";
					System.out.println("killing " + key1);
					//key1.punish();
					return true;
				}
			}
		}
		return false;
	}
	
	/** try to encourage the search to include many distinct plain text candidates.
	 * returns value in range [0,1], representing how much this individual matches every other individual (at the genome level)
	 * maximally similar [dissimilar] genome will score value of 1 [0].
	 *  */
	public float similarity(EvolutionState state, FloatVectorIndividual ind1) {
		int total = 0;
		int matches = 0;
		for (Individual in : state.population.subpops[0].individuals) {
			FloatVectorIndividual ind2 = (FloatVectorIndividual) in;
			if (ind2 == ind1) continue;
			for (int i=0; i<ind1.genome.length; i++) {
				total++;
				// we have to look at what the genome translates into, because 
				// continuous intervals are mapped to discrete ones.
				if (i==0) {
					int which1 = CandidateKey.toInt(ind1.genome[i], 0, CandidatePlaintextLoader.size()-1);
					int which2 = CandidateKey.toInt(ind2.genome[i], 0, CandidatePlaintextLoader.size()-1);
					if (which1 == which2) matches++;
				} else {
					char a1 = CandidateKey.alphaForAllele(ind1.genome[i]);
					char a2 = CandidateKey.alphaForAllele(ind2.genome[i]);
					if (a1 == a2) matches++;
				}
			}
		}
		return ((float)matches)/total;
	}

	/** how many already-evaluated individuals are similar to this genome? */ 
	public int dupes(EvolutionState state, FloatVectorIndividual ind1, float samenessThreshold) {
		int count = 0;
		for (Individual in : state.population.subpops[0].individuals) {
			FloatVectorIndividual ind2 = (FloatVectorIndividual) in;
			if (ind2 == ind1) continue;
			if (!ind2.evaluated) continue;
			int same = 0;
			for (int i=0; i<ind1.genome.length; i++) {
				if (ind1.genome[i] == ind2.genome[i]) same++;
			}
			float result = ((float)same)/ind1.genome.length;
			if (result >= samenessThreshold) count++; 
		}
		return count;
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
		String[] ciphers = new String[] {
				"Z<<B1lM_+FBZM<|kKR+lMXWX_|ft+5F+/ODK^ORdlfb4KLfH2/M<dK*JBCG.D-*MdR^>EJWN|X#+ct(|pb*Op)RJ|*b.cVU2Y/+COpLy|DyONL&>k(O)+W4c+*-(<F+:GPzZ389;c(l)+<YRT|5FBcW5OFq#/Uk-p4NHE1OdKF-AyJ+p6B*7AyB)S%-_(R%B(AUkNz;9zz8893zzzzVV99UE@M#t2BcG6D>M1TTKL2|dBL5U#|OpC2T+YC+5FlcB:47l22T+(HqppNy+c+5c+OG+)<YSSPB4+F+^5Hj+G6|5FBcK5ZF&j#>p+p2^HERtf^>2WpLW^S+C47lGRP+k",
				"yWk^<MBcD;|^LcY2p9&t1SUWLA<>Y8.X4p<y/V+4yU_+XBK.2:+V1NVPOpp+pPFH<B+C9LKG5DWcj9D(bB+2;R_W^MFBlL+6O.tkMfK)N+VpcMkWcpcl)2k4(5G+*7Y2_8dVc.bflp^(%+87|4y>):tN_-FUk852-*+-2FBcN-OF.WL5^NBFf>T+j+TJGZZZG#JzGGJGCWyH3|5FBcf+R(B+*-MdH+/bCO<FBJN5P|T6cLlHCOK)+/|5.|;3+T(RKS+)OMU*|JRSOVpKt|5FB+NRY^D1P*(U<6OT(dpH9S+Ll%pOdqqR2zZ#d7|CO|KFBl4+;RlHERMA>*24+@&+",
				"16OjYU_ykp+BB+^WU(KX<Mbpc^4|5FBcfjkkqlHpbY5p^1GG(dHERJ|*8YzZ)d;7Clj9@WqB|z+WUB;-2U<J/T25+J8D(+cc31l*F2T9W^RLN<pYDN)p5M6zklF%+b.cV2lX|OWB4<F3+lkBFcFP-*8yp-f+(WMDJ_.U1V((T-42>SkFB;qbN+%+P25&NZ^+^/FBctO4#++EJD2A/+&LO*cGC7+Z+CC...py3#pD+j_DZBF2S:DzM<p8|5FUlS_LtVT)pGzKM<*-12MALc1bDd(|+B1|pqYNz+6&jKDPA|Vyf++|SV#)5V2z+1K)VTft1LG+D<:#G>1f464*%+Tz",
				"+Kc+l(Ht+p%qzd|+HN-p4+Lc-l(Ft+p%pzdP^_dDWMf_zMW<k+R2C1N#OVG:ABGBG:VGLzZ*9:AClf4Y*4y>4Y78z9MWR2(+6kC1tU.KD#>LJ@JpKO2C5S2U1N#OFRz+<RUp(bq.>+l+3OX5FRJLXOM)6O/pKT3c)S+||5FBc<-cFU1CF|t+*|JRFN-(YEWPRCy1+G(dU)K27b(y+1.2)HO)Kk6W/<.)pKTj5l-*|5FBc<+c^#1yF|t+|*-N+pJfVc.bp+^8d2z;/LkO>5p+Tc&S+^^7z9M;|_9MWLzY|O2+OTj&S+8HER2yl*4ZP4#f4Z8FED;MKDZ^<k+.Tc5l",
				"pL<ydR+|5FBcYOUz#l9+pOpFd5WOT+4UL+<yROpWcc+|GFf3l2N6FF(MkKl9*-V.D8<24cO<L+O>DzZ*7;8C^+2RZy4+|5FBcT(V(8VlKj+pKRN8^2+X/>5WfyZz|@JPV+W/bR1b>p#p:15|A+#Gc.Dck-FldEtf*|JRL*c.&-RMN^^+dT<L(M(Vc.b#NtK4+J%5yf>G4U2L6GM.pz*Y7-*94Ek__Y;2F_+6G)d|2q+WO6<MTM3+7&BBC7H7#H7H7)HzXPVYS.^2+9SN2OHERlzS#+:+5MPT+qtpW|#JF(AZG:|t+cl(2Skzzj^+OkzUFOKD^K;p/1p-+jtkK|Uz",
				"l_((G6DAF<U9&Yp+GBD5EV^V#BGDBCy7+yp/SL1fyO<Uc2CZ+LFt1TPF+MFO21l4K4jWl5|+L/O|5/y+z<(OzpU_:pP+%WERJ|*7|+MFO218*d+4z+L|-*|5Ff(z+.|Oz><-JZ|FBc+2|).F*C87@RM12).l+)M8b+.|O2FkZY-l>O+Md>&cYzp+cT-+SWd4&%HER(C(+LFO2|lTkYKp+OKp)M|4|5FBc*-K4AcSSptFNNK2F;|f3zZ#D^/C6&p9D1/jd_&WA2|k>PXLl+b.cV;FyNKTUN:pJ+M12Fc*J+WcX+Wc+;FftKT.kq6zS8pN1<|3qA2KbUz9tk9()dj<",
				"%L/+MTy#l+<FzB.1|5FBcUkF2dOlzB8YB>^+K4FWM#qTK9O.KCBL(+.j*L<BL)+K*k+OZ/HERSf;pzyRSfOWczA2pA27+KY(+C#VBlFpNV^+p9G6D_tY(+5DRMWSVc.b@^O84N2RW+9B4cPy>;K+p<2LL-VOz-.N7+fd4U_Uk-S+CNpb98k<2y^%EGH%444G&&|%HE64&+Xcz+P1d4Mpp&YR*|JRTJOtl#4+zZ48^|cl*-7W2fDc<TZ2UOJJp|55clyD(+B/Z+3Rcz>BtPBVRl:+FBcU(F2dUj;T_1k|pC^#5+CMF+(+><X-*5OtKb+LMW|5FNp(MV3qdF:2*zO.",
				"&7t/Vc.bP4()TOM+p/^#Cc(yOBKf#8d5-G>|5F.kO8_F&9KzctGzO-6bk+fX;cZ+^N5k2+MzUX2+d|71y;d44.|5FBcj)+21y/2+9y(KPHER+*8++WB9MV&:t+UNFdTllt^.%&NqyO7zZ3D|^C;PF#+VEAK:EGGEEppR+FC9YK-5*<Fcc3+5C|*-Ek-AU+lO+U<zS-*|H&Oc)qMT^L*S+LMCD*|JR(l|_4Z4DY+JR_|52).)z2zZ&Rj&VNLJ#BS@W+@*+5cFBcR+RB>WBUC5&G*TbPBbUJFBNF+SB2G(dO>5(#3TK8DlB>&*M+V<W&B#kz64f|<G.@+8(lL<&O1l",
				"lB%6JE6H66JZHEJ|d+Up+M25LcF^zY2CUCFt)+pl@+4P+GM<++G5D+RJ|*+MWMLTOF>l#KzT2.|5FBC>UCFXK*OUfM7J8yfp48;8G|^Y<cK+R+j-)+zcF/(p+GL2l;yOWW32q(2kOHERfz2#9|pc.1M.N-TdR_y|5FBcBKNDf-p8(YS5(5RL<OBVlytp|4^VPq)#SW7_3DdOplz4+SK*k<+4<+OVzSd9(pcB1b.cVc^FY+zTR;-*+GcFBcB|5FtKpO(N|)O>9^.+B&pMGtd/UlyXPKVb)2CV+_.AUB%N+D|TzZ69^:k+bz(*#AL*->4NWW12#L<+BBk/OR7&+Rkj",
				"S|(z5K__KMCL+#py7:d9:ZJRV.Jd7dRR.VpXUU4zO4yzzZ#7:9C63WXp+PO2LFyj+T+>C/C1;+pcf(6zc>YNcLSLC@|WF62)p+-pWSp62Nf(2R8F8p<+FLP+f^TyELJBMp<+M+B|O+LFBcY|5A/2)z+Tlypl>RJ|*tF@1(ODcB-D)/)bzCBMWCy5lk-+)B8lUG#dOHER&tEN+UA%-OHk(%z(>T+t4OO+@|<FCkq24fk+O^YHK-*+N&4H4B8b5Kq2+%zXl#PTK%FB.K8X*-+;3W+|5FN<+c2MW|^_l2M5pBlb.cV<S;OKBM+1^UD^ByD)cYt(2k%+#c|5FBcj|<^2",
				"#++|fO7_+Y*f1+BlGVz&(pzttp+b_1HER<O-*f^c+DFp7T>SM^K@:|GpS2/yTC)FKBpRPzlYKD+4LGzD(+Kpd2N1UW8T+2<>6LpS4F^UWBFBcT24c+f(AN1+B#F*C.PP>.cGW.d+2<tq7d6Vy^UOlj+K-W(<d6b.cVlMO>5P%F-VXB8+4BcRd)OyRF-)Otp((/l.Aqy)MN*DG5C4^lMYb|5FByN/z2#5+1Mc^WU4_#Oz(<MGLO8+KL+JE;3HH9ZZJ9J+B|NBLMRJ|*<5+*z&V|W|jpS2z:|Tc52#F+*-2Lp+C+BJ.+VCO%VcURt|5FBcRX8NpYR|lOzZ379;k+K)",
				"^2W_A<+W2(WpZU/P#T29pUN<<+E/45WB25W3CT++jc3^+92NX+B+Bt&:|FJ1<Vc.b64BB|TURM<:cKcSL+>+(O*-:bNMqlC.C%y|5lNyFp+>O+Stb<SYL2q)/RC5#5+f8#FBc)U&OE#(lC5^#zZ(d7;k^-_ZR<XYK+y8X/1^_-O8p|Tp+yM2jU|5Fp+lNFO+LY(W>OK41lpOc(/d7.z7d;DDz.;H.B)T&+MfNy:|PBHER68MF*|JRffL#5F+KA%+KX|>F>+6R5Z|5FBcSLRJc(@c8<lL2S+>pbPpR:4U4J9YyclK49-*+)3tBcKCG)D-Op-M|OOM+FBc^t(B+Jp2",
				"%7zJL.b|5FBcfW&7Dp62+)p.cMzz+)52WkJ^q-Bpcyz1pycOTD_+Yqb.cVSZLZF4|+%f4UOBz7PY&XR249%#9^3%E#&%_TyHt|2Ok)8+A+pCz|5Fp*>j2&+c8zT^6FBc2+pO7|FB7O+RKO8LULtMTdy+3RPO+dKRFP<+.;8TkM-pkyG@DL/l8B5:4<-FK(lzZ#d;9C8&V:pCN.lR>cySD6+1+7U4+tB<JkpZ5<NjTG+XdN2+G|B)MYRl(WUUfHERJ|*V<(pd1F+524+|+._/c<W(F<+c1PN-*P>O&O+>Cz5l3p)YGKkB/VJWA*-+CNF+P5B.BS|LWj|MO>-j2yLf",
				"O<>+^SK6OTc<P5;1DJG_dSp|^)8OfAqVFV+-<+<l(zZ%7:^KD:B7*zzd:77dd_lNLt+ZUMfqDq.-*|5FMK)4E2VNtXlyH|19LFBc<+#<)VlLkFVc.bDC8LcM_4HERV|cS.y4|Z|5FBc^+#<).lLAT*-.2p+<++Dp+bU2T6+(UR-6+2p+O1#FOk#c8f-S^O/C+X2%+pOK4U1HF;FfZC+K|5ERF>p+y#AH9fT2bO/Cp+K@p(/tMKN5WcbR)qNb>&kWWt2.P^R+O(2+8kjp+MyUCp+WYR*|JR(M91&Zp>p+OYW|5(Nl|5FPblc;TLZjJ9fc/Y43+2bMY43O(RJW>k#y",
				"d)W+c4l6)RSO(+l<:pU|y>p^4S5Bz+y2Rk+OLVT(M_Zpt2DXSLN9A)5+(2lG#D<OcYlZ8JL+y>p^)SW+X<pKS+VM+HERBCL3+q:Uk1^3PBfUMzBz+j|C9ZO;4<5pt+y2Hz4O62R<O(+l%RBL2<W2KK)O+9AU5p;LWBz+>f%OLH-f/7bb7/bEbGb7EC-p(Y^OLNTcpzZOd/7K<qD*@B^1LV8lBc+lUN1K+T54JSjT9L>+8_-+W+8U>f%OUR|W4M2KKkYM(+^;LV&*-MtbPcNRM|*tZTz_|Bz+.zNHJ-*76UCk+B(|PcV&p|*z|5FCFBcb.cVRJ|*pc|5FBcYy2Dk+",
				"M+)RpOWd5Bj^96*EzJ1111JD/S;<Yt+TC(P(YO|8p4^BNlU+H++3O+_MYd+q_HB94pC*GWPl^lGL2^N+MNO9-2GSkKMl4Ut+yH5B*VHERN+p)KU+FBc4Y.pOMdpf2)<MOtfRpO+P2O6|5FG1Dy7RJ2pBl-VW|5FBc8(BC|G4&-2yTRd|bPPRJ|*kWf^(5T&K;k<RPydS#>+;R(2TK<#cV+pfbj^N+#9:p#*-LB2PC.+:KG+UcG4|@(B#+3cSO#+)|<>5T|L.b.cVk(A+3cB^5L+>+-L_XM2OVK6kL7y%(X.>|Tc>p8C.cA+4tU|V-*lFGW<KzZ%7/8c)qW+lOBH+",
				"5(B_yF;zO+zM<&(BC-(p+.CGzZ%89;cT|<zc(L|tlCNp2Pb189;EEH;Ed1VHZd91V*fcSbL(6^BLF+NM<>3+F+z)NG6GB+yKJp_l>lKDf2WD:7kl(+.tOCOqU/plB*LF+4pqO+5Ok^pc(KK3+R+TF+z_R-p.+(W2M4BK)G1dy-^<W2|5FBcM^Y>#K)l+b+-*<f>+lL#CMB)(+.J|kB2@M5.DOy&:R4cc2|5FBcRtOU4zY4/Vc.bz7USJHER*-B)YppjfG6SD|#Tt|N+GpWY42|5FB*|JRUk%#LKPRWMPA2OFTO+X+ztXk+Gj^U+/LlN|S#c<AW^Oyp*TF+5R+372",
				"KL_+M^@EHkV7HRHkE7fdLY;:PO(:+<*|JR5S^5d%lC^KJSc|WOzpAC+M|.zJFBc|J4L_c>ylZ%CUB4F+WRBc)pclLF422UZNN:(PO)JG5D9UYN.<(5pOX(y2Xt(T*3+M>tYp+BM)5<CSNT+fd##<-k:p4F<+9l9KJ+OzF^ktAO-+_%BVc.b|.HERSf-Ozl6^cT#-*3F+BWN_+SF+K.2|pUY;1T+b|1Up+Bb>W*zW2Ky+y+529B&Llc>.|5FBpM)TKjJ+6B6p(c>tld^2M;q(+qlOz:41+PzZ#8/7k6F#C+<*-4&M)pOczp2C+*z6|5FBcfLYy+<W+BO|dKjtZ^2M",
				"<2OBV4MOLp4O7(5(R2)M>/W(+OB*2+GyzZ_D:7C/Z_E/:Z_EE:&_E_:_4pBp8ldNKF6cLy6lV8^K+OzANkB+(<VMJcYVc.b^W|4Y5T+O.tG4cC+C)Ndp4K<T&+FBc.2cLRtY*|JR2+UG1|bF^3S(+|5FBcXt)pT+-*2UpkJBSO-p#@fWNS|+-qzR22-9f<MRd5F>PlM+O*GF3^+pFU.K)^F%<.t+O#9KYGlWRC+VBG5DzR^WV7C>cjTA.K+BpLl|BF8<pyy2+|)*Lbf+9B#Uz>1kUcXS+cpfO;MB(lN|+qWzz;|5F+#6J1HERdTk(+|lzz;L8Py#+9d5MkP*-K+j",
				"<(+%Y_N<|l_MU26WRKM+NqCZ^MVc.b(p9y8+|5FB).+cSF6GV7Gz*76766*Y.C)O4HpjTld+2<Sp<bG1dOp)pL+S9ZBc2OL+Cl4+c|<#B/d^U(yFBcy82>4/D5tF(+ltC|B(tp+|pN+Bcf+R#d:YDF+W1-k|5FBc2zR+CZ83P-A-*tAO#_Bcf:f>D12|^^|8.5BlTKW9)LBT<4^W+(M+#kS5yq3O;*|JRWKdGBU2L/;*-P|+K2M9HB;zZ687|k+JUf>2lljpdP5W+k(bR.FO&K)bMU4FXONOLkyLO+c#&+YD.p4qHERK-F7TO#pT^cT+RCRHp+%5F>%+@LXMNRKB",
				"#)k>-O|Mb^M)b)cdAqKUWb)*-)+<HTB)j3):Kf.L^3SyO)2pb6)YE(pcOALS+)zR4zV;^b.cVkq)Uy%T9CN+CyC^ZS#7yqVq#GDGjDZDjjHG+G*DN<BK&9dlR%zY;pb)>S1qMpF(Nc&pL|qFkzT)KlcXc)<|5F|8N_9WU^8^T:-C<E#HER66M%FUB2-B8%@1*7J_#/pUBz*6O)-lNq2|lt+7(4q9^FBckRJ|*OBYfL%)-7kt^/-*W;p)()l6pTMJB2^/|l)VRc6()fS4l9|5FBcO%y3Wz^)R|bKd9Y7zZ#d8|c/pz)lcPTLpFlWfLBFKNqKSLBRP1)tFBf)dqXtP",
				"+W8^+VRN_ZRWl#t(RTOZCtT<#cfFWzB(5:p3RqcFCd/1#ESLY*F+DG4G999D49G9GD4-zZ47;9KyAG6DZNMUM1CV2.V+|<BXWFBMT+yU%<N_lpTT+|c+ycb.5<OC^+y#d2BV+_O.j2@H*pzN7WBpl+lCcU|Y+f28lVBz9cb.cV-*-M+t3+J5Ok+Op)-MUYLzy+J*L)NP2%O+|+fHpX+62((+83#j+*ORl<>zPfk.S)|5FBc1-*BMHZE.8Pd7pYHER8|B5cF2(p^k)>LM<F2&5F+Uz:zNR&dBz+(pkkL(Op)LpJ|5FBcSt>ZF/zWBl2|>(S^^q+d6O+|5RJ|*O/A^",
				"76TJ6+<)9O_c+)3+3j9A#+>SY|5FBc+2Gl98<fZYd#yp+9;(yKHtzcV+Bk5%R%R.5RE5.5FR.4|C&zKtbVcPj93#p31Gf+DHCOy:2pNVTP<O7H<OctOlySl|UJ9p+_Y|5FBc+6MldLX|UY>#HER>O@2+N-D^*-pT-*Xd_+YdK-8c+M2++M|)2N+W^P>9)CdLM.*2p#;W^cC(&4Ky-+*|JR*O(>G%d1&NpzD|A:K34Vc.bz+*|;ALJ)AfqOcZ1^/MzUzp3RW|<4L.(y<OSOD4GO+WzMG+b^l>L+(SVp+W2ZUq2/MzAyVp+WK+(^2CLpN8Gl4zZ(78/kTDlYKfU|Tt",
				"%z.2LVc.b<dW|d+OKl|5FBc/+Zp4L*-zyRt+(9p.+MMctPF5(p^:yY)JbpBLz^#<;U<+z6+2O7k*|JROBk+BFlCAFBc^|>pBp7M+)4.F*N-*KJB^Uf++H1F>cRO(|6RX+TZ4L<+zKMcTXO>k;Uky+S(U6-/WF5)ANBL45>NKZ-(9TpTj|lOSKcl1+B9)1z2GD8G3%VEDG83NDVGOkk5#+f_|2WF2+OPOf<z^CH_p:YMHERW|5Fl+2Mp7y-RL|.FbCHN+24t#OSWO2&qz(Y+MKBc^pK|dT_);G@dA#9SYll+d+R5zZ3D8/cjU*RW#.+*N2fpBNCJt<qB(c4yPC+_+",
				"<Yl|Oy<p|^lOd94zT+|L)LXSzC_+kK8.3*383HKJ3J.:Z3-G3d<TWl7OUU&(R1(>O|+)<#Tft-pEB6YB+-*N)/y|5FBcB1+kM9pO|zP+|5FMzO2VOy<GV9Y9pLzN(;l/&kN^pWqSR(SGD(LV(LO1#TftSp+26b.cV+fFzCq+)l-pCt+FBcBV+kMSp^-*-zW2dY.DBzZ4d8:K|_yMpS%bzT+R>/7%BHERJ|*#+jdNlCBN#|p47AG|2-+;U6P4M(RB2DlWJlzRFRX|G^@DE)#UyRF1+OUSpLbU4M+^)2WAP>>+F<%pWVzP+S+FMz^2OtBf2_+j+F@CF|+4k|G+B+2;",
				"PH+pC(B;B<+F%R+AUHYM_#2-3+2j(J)*BOpMTK)U4TjB+98p^-*+cKct+BcL:FBc|UF|W12ClG*dKyMYlq+zHq(TLf5;c^FMpOtt>l(z^+NzpLlzZ*7/7kz:+5dyDFbkBBUORXK++ATf4KB|<+2+c|+8yb()b.cV*-1yMPF^+<-2+)>+cW;2Fpkdd&-D^(^6l.pL#Ez<O_+99#LpL<by+RS4+4S2kl(zSz<6f>28D./GZFFV.%G%FVF.JW1kYjB#WO89273)U|OpY++tFNM(dfR5FRJ|*CN@>WlB|5FRpO4TK|z5+_CEcR|5CNP4O4J6pM#OND|5FBcOGHERSWXK",
				"B&tfO8+AK9yM3p)7K2M^(9YBK2JpK+z#+<)UzVOWS3Al-T8GGdj;<F@KMtF|5+>)d<FBU9A*:TWOzp;fNB8PN|6(pOO+p-lX+O#;yy25+<Tc+)p(+CU:|#LzZ_D/^kcKYBN>R5(lT9+1W;f|5FB|XJlj+-z+VVS6|5FBcL|+8-W%7p3Ac<7ORJ|*<F(5Plb2l9Tzd1Ey8d>zSG2.N#M8p)YB+HERLNcPc)+LlGFB*LM25^b.cVESC;GFUCp&t<zUCOLK1G(pct#BdY2V|C(qJ+-*q+WW%M^+6*-T+9zM|+f*>T+M+O+FFBcG4D^y2Bcp^4.RZZ4D/bD_RR_ZV+O2",
				"N+.4ML|_EJD_GED8_c>1+lt+4RlSZ^4k7F<3F#^C41JMzT*)|5FBcpU2P<B|2G(DdBR+|<&p+RJ|*%-*zBOA/2PCB+26zWkR|+Z9N(pdR7F<*t+5fBOp/2UTBz26zWB#|+TR++#y|5FBcp/&PZB+&6(Wk*-+C;WHY(7>jNHUMT-k9)MUO:POKH5cK1-yK^^KBzMUjUO>^lSOd;;+.cp3NKBj7f<Y(2zMzZ_8|8KYNbOOFWTMSdc^lLtpL+.+5^WC2p@C4HER)RScYlL%4O+*y+#Pqb.cVpX(F2p-l+9+L)Kc+5y|5FBcpSqfF.qb.O+AXzf9t(<d#L)>:+ly+lFk",
				"&(8;#:7A:9LCKY_+<W)>4&@pp+%^C|zZ(8/Ak9K-MM3UT)<5dSCz+_|dDy+lW-+2tf-*+ly2YH.42LLd+P+_|kWyq2W>%tVc.bMdj11)lSX|5FBcL>k+j6KMUK99>4Ft.p_P#)5<ZNp25z(J+bWDAzD-zYWN|4MF>p5FBc+bf8-KZ6U|5F.D1|^(CD-M4yT+#y^N<_+pD2++Z|^LFF7:GOdccEGGOOEVVOEG*V*|JR7p1|#HS.Nll:U|#T(++|^z2T)^<+l-J|+#FM3dpSfzC(<HERNl6X>4KLL1z+lPp|k;kATpS5z&t@U18;*-F2++H1;qq2p6+(F5;Y6KfJ.+",
				"KOM:+WRpX2FKFKL|+Z+4^#M^T**yV4+.*-tcUz8Ld;WGfDb/3bA3HBJkJkA<T>Rp-YTFc.9^lOzFO+;JyFMWp5lOLX_+OOMp|5FBcU4+WHER|TRJ|*+CFOGpGlRV#plK<Oj+ft-*1PMWdY)pS:z>+|<K|->Rc6G#DF_NdL%cd-YOlK7(()49MEq)pPEy72.N@2|&+^+S+^+p+|C+LZ542|8|5FFc9+FBcVf+9#NS*cZ)U1O+q42Gtl(NP_p<Uy&8>)#2Y(Gf(+zU1;yFK.zb5VCljz<zV+^5y&T2.&XRCLS6+Cp(+NLdqc2.8<5(zZ37/Akb.cVRt<z62Y8%^W+M",
				"+T|lFCTlcMBT+OOtHMc(*-K28p<SOG2dY2|lK|^|+2k18RFyMFB2+pNdDqyd+z#7O|13l^zj1#%5(R<pN-*B+zLF-GVZ*9*99GGGbbZ-+>+Bp83^DtKpBy6UF.%J#@MFBcb(fFMYAl+pNKpR<f(.3|5FN-zF.)_LJP.Bc6#cf-z&2dpR+|^2kVc.bX/M(SW3kc*|JR+4MWNKMqU:H|+By)+E#7O>+_O+TL/tW2)pR>C&:++zjKSJP&Ul)k^ByOc<4<pA^z4HO+&7R(dt)zZ(D9;KO_YO^2kcDB>8|lWX+.4>fC+LWSPk+z4CL+W&Y%/|5FBcLHER46+TU<BpCUU+",
				".BM^7Acf+/VKF(.XTYfcpj#KM+S4l|5FBck<22fTZl424U+L6^N@)3/Z4dkF(T<@kTt@N+_P&+O4+GHCJDG8DCR9CCG1DCz)yRJ|*<dFWb.cVYF2VB++O|A+*yW);c+TN5+F(+JKV%+p5U.c5KcB#|lRb-EVVK+b+lNW+|k68-W_WOt%lUt&)#p#>M+kA2F*-/UOBcS@BSf(.Kd5|l>|#|Y+HEROK2ycB++-*^Uq)MF*yML:P(:L<SyBk<F(Lqc3+6M+_pM4Wl%pO<2EcXBYXB*|dG1D|5FBckZOp2kOpB^dzZO98;C7-jp+O;*2tc>NLk.5:L7TPpY(>^^p2+p^",
				"@L25K+bcKG6d+&+NK#P+<_VpK<(b.cVO<l^OWUdS+;|Hc:^c(+DY;U2F*V-*ylA+._DV+DFHMlB1pN|3kb5pVF<4Kf7+CJk_.(pHDlj21TYLU2KOA+l))O6OHER(+2pN)#cyLPyYJ+Wt1Otc(pB5><lyW-O*-V+/XZ<TSG<+9-;E|TF>COt4B5E|&|5FP2+N^8#KS+8CcRB4flpBdy/pBM+CCKF3t7+WLM6FBcUtF>4*L*ZOW4.>LM(MRJ|*X|5FBc+WUkB5:^cV|q#B2%dkY)p.TS4q(JE|VF2^#ZB2f/p)M+R.N8+TXLdfp+7j+C^OM+zZ%9|8kB5--9-zRGGG",
				"VczZ@9;9KdMcR+G&JG&&J&DG33&XRP+RR6NUdz4*KdLzV/7l)3K^F|B+yz|^2+pPU5^TO#<NzF>5k4_RSF&OV+2)(YcOB#HW>+:El:)U.+St88M+Mp2(ApT<+qpTObB+P)2Y<^FFBcN-dt.*p8B>T;+VS-85<+Uc+kW52ZppA-*45WY6Vc.bM+.^OXcy4FNt(k#klYKG(D%KWpy+KlWFB+ljC|H<2j^lBLB>.Wq+.<zf/|5FB+M+R_B-*ZL7f+_LMt|FBJlpz|Ud+N+fMC7*-E|p4Lz/+HER(1+yCL(*|JR1CV46T|5FBc;2p^yZzkO#|d)4O#SfK1c2cOHC(OO2",
				"5p(+*RN.#K+4YRCO+U84zZ3D|Ak+P>JzWL1M+UKpM5F_OU2|O^++Ml_<2BW+8p/B*|JRR.+p69N:t+G6+p^c.+;l<.cFBcBf+TLSSqWHER#Y>zlzcK+p^^+t7pZBW-KF5MBT#(J2Jccp2zX71LyqzCADHkbbkEHV%DbAVE(|*_-jWRf+2B/|y%+t+2-)&S4G3d|O*-FO^NS2N<<b2BlpG9T48cdZ>;l(.+y/j@F<Xdd)+yB;zcWCzt^PLG9l2(4ZG#Vc.bCNOO#GFY:J(|5FBcf+K|MM7MpYLU|5F1)6KF8R-*>(BT+RT+p5OLO)z*|5F_O<KlUCdP(4ycBf+9p)",
				"NWy+T4f>NY+ZK1>pU+BF^^R9HJbb9b^F2Z+CM*4+NCy+jG(PK(T&ycU5B2>l42>*|JRXzW;+1MO5RD2p|42M6VfMU4/UVc.b|JFBcG+L+|5FBcdp+yRLVz)l-*dpO-.O_2CBp+lkcK|+Yq/b#cZ7+pc.O-O5%|O(8tE+OG5dt-lB+X*-8HERBz#DWL+AL6qB/zO#.V7VY6fV|<GW7^8:lKCD_2T+#1MSk;K5%TO)<V.k;)zZ39^:K+2ycBMk2G)zlG8<(PSNN|4+M+k|t)UVfA(+&ycppcSB+B<C|5FSBt(.pl+;jO(.p<WDSzEPpY_>L+43z+T<dz*W@dKS*c#L",
				"DpN.F4V_)^dXbbA28AAAbAd2AAGz8+kU(>*OEPV-3t5<+%O4V;y|5FBcE)MLLTpRkp(63PTR5MBOFK+|J+/H++W1Jl57DM(-*|_f4c6HNKF.+^W(5j*|JR59p(B|#lKDOy.%O4Y*|5F+1pR6TpFF#TBLH4cM*^cXHERX>CB.<l-j^p*-zZ#8A|K9:(-Y9f+|S+B3R+V#yYK+YCN>4cLWCylU+7O++Up<+M+Bpt&qUpO+SLK1KC^b&Cl9W(T.+U)<MBOq./<llNJPD+MfL5@yBcOkW)^|JE:FWG2d>7Vc.b/H+fV_BBc#OkkSR+SRJ<X3;F;+pt.FBcJ|./tK)+Nc",
				")Fyp+c+Op:HER:+V)Mkc+DAPSU;5Vc.b%AT2Y(LW<Rkk>cftKlfO8U:*|JRtVP.c:Tc:T)>X|4*pK(;b#p1HqW5pEJPKTX#YW2d+AUHN++R+SOOz42G#D9NlF)%pCKM-:dq^K*|5FBc1N4WdXc9+(6kL>2R|ZJ7z.zbGz^d+H(-y+<6+<48p8CR+Oj-AMlY^/+f>+5O1p9OWSU+Ot5O_+t2A(^@/CPK(+#p2EcAyLO&D|5N/2CRX+*pktd^+-*+#<FBc:1Fl3^9KAjDE+;<VTl3MMVy%LLy*-t2F8%qS4&c2(MRC|5FfFT&W<FlFN_U))Vypc+_LzZ477/C+Y6Ml",
				"#&c*_Zb.cVPRJ|*9l&Xt6)ES7Kc&3Ly|F&Dq%U&TbXU.j_1&J#B5<&T<plcpbp6HERM%G2DLRYEGjFBcdl413RjVUWMNzX)-*|5FPt^^NzB>ztckdFY)j+cVyRX&(|6V&<BOUFyz#CT&CU8R@X6|c/#&f-N6&HqN:&J>b:8*--RDYplqHZ<|&6G<|/Fd6UB3F1zX42B|5FBcRf9kY|-&(J2M8FNR;&lW@-*&4B/+5&(fZ/4q6)CNBZ.22Sq<..DDE2yqD7ySE(&8W9YKfWfzb/:&6WWzZ3DA/c|M;Fp8zLz6#b&3WB33GA2lL3&6#9&3tXPp7B4(N)3:)^X6&kpk",
				"6#l<;KL3FOpCMT(K_%Mc#.4;qlM2YkO(DBBFpjP/cOXMyf+BB@7#72UDOp%2&W*L+ppB2(KLjcOSlb(KM:WOG3d1FCc/yO.yOF&5T.z+R4/FO>Lcf+Bq4.#7<>WOpC_YVc.b>FOS+tM<lR%+DBFK25X+>-pc2p|5FBcK2R3+U-pF2p#|5FBcK<JUl+NU+T5l+kC+95l|)%+PzZz8;^Kyl+Rb*|JRTNc5UX4N<BB+%2z+zzP)Y*CEGGEJV-EVZ^VJ8J>9k<BBA1*-*z4kYRCt+kj+9zt|NU+4Rt|)j+9z.M+N_+S(TA-*3LWpb6Wf(>+C)P6WHER>LyS16Df(>+:)",
				"(3O5SWcHY+WL+R|5FWk+82CV-N1((T^:2JP2KkB(4F*NBKcFWON3(>yH&Z&./%ZH9/H&q|Vbpd4S#pYDKpESpY*-z2G+t<^(plc|5FBcGBlfzZz9|^KqN4f+ET5zVyM2k-*K%ApMlX5F476O7)D|<;M1_-PUFBcU^Xp8W&+b)-p5P2k+8VN2dB+K.L+GS(B:>z4ABUOWBcFz>dB+/+L+C)Lb.cV+cR|6#+j+tt@O+_f<lR|Dt+G<Rz^5C;lfF#+j+CCTL1_>U+TzyHERk;OM+yyldTF(<ORG4dU*2JMOpMl#LF+JDY+pGVcRz276O7)*|<BM+kc|^8K)B#RJ|*Op",
				"2+%p(+jc24NS+yOCU_lU^8qC+p#+T|*FOK#fyV<5L7@LM-*A2;l|5FBcLW5J9W<DNdtM|5FO(kBZZEzRRGZZH.R3H.3HEEV6lO<p-bp<+(f#^4;qJADSkKV+X|>+X+|t+6VjCA1N>cB7b.cV&)Fd+KM24-<+ycFBcL|M2ON>U-*-(TBy).+BdS@19)_|TlOYpWb(#7p+;lpBPMRJ|*K&P1)4+L@K|S@lOS|zZ*88/C)O#+LB+4kJ4d+2+%pYD:+52K^lR2WcU8VY+@^@O56FBU9t++M2OpYkW(t5WcF9dTBTybBkcPcF:+FpM)FN>*BMzKf^^)(+_G3DFfHERCp<",
				"M+VOF)OMW+UUHlE:G%_:%/:%pF+46|ycFB+R(#^z(LKU^pP>N1+<A(1D+<j4X|222+Vkp4U5L2B+WBb.cVOAtFpBt&#cVdy#2^c+M2OSHERB+Skdy|2.PN53VLT(O&.S|5F2bpy9DSTq+^Bq|P;zBW5ld-y7pd^l-*|5FBcJY-KT+WB7#fL+N(*-Ccf5LM3RJ|*9%+*|f/TF+BZ|pjK|C+Yz)OKXR(-VM+llJ;z4+R6kfpM+W<M4<R+*#Dc>.pUt4;lbH7G_d.F^C+Z.Z<NKH+2zK;O)@<OcFCzDltCJR1zdz)<KcL*)JpYO)NkY+WR(9>pT>+5^B6zZO89:kFBc",
				"MDT+^zZ(8|9C2c)764T1+>>11p8</&#lG&;D+pXt<LcUt_^F::-p|+&z_k+T9LfK2G4LT&Y)399PtFBc*-&%K#&k4FM4&FbO+|KtF;AL&>X+zFTNy4p#/N|8W^2jd*+N7p#+POFC#&>|5FBcO%OGW@4W)fGk|5F4p</NKd+^>2-y+VWOjqpG6dA^U2-O6kyMMkRc+U<ld|OpMLy2+&7J2_ycl-*+fY1lqC2+|D9&&<SZPb+y3+RJ|*+^U)<lfY.W.K+S+d<.cMDKOZDcSp+Y7p9+k8|26b.cVZpSGlCT+.zpL4K+lkCOWAl&;<.cSO9UFV*55EEV*BJ)<HERM77N",
				"O<*1lWD%b%pM4|5FBcpWyyOB^:GS7*M29-52/DRJ|*KS_+CVpY|BW#@<4T|L+<ABp|-*M+6U97+JKjl+pcf2Bd+9UO<ERYD%K)pCL*#LyB+fFb.cV^TW._<THERf+ByT+c+G.:NS*>c+1<dP+K.*#VllU-(^C4y+BctbYFf+K-L4pFD+M6+Bq&_tq+FMF).Ot6+OG5dpc3)+WNPFBc|(O)7dM3N^;+AB(&GH88HH8zkR8k8kC2UZ*/Ll3E*-V|3OKp2KjV1^FTX|dtNpL+#(WlPGO|XSB++>OVzZ58^;k2lZ2UcZ2GF)M(Y/C2p94#ONf+U5dc>;4WS5>.((|5FB",
				"+GzdRL9V+4|#THER*y2+XFdbAR;O<TOT;+XBdR*q*|JRL3yNCt<B9kSKO|2O<BdLM+UR:fTp+GJd_.(52+WB)tR9k+4R#T4L+4KY^GP+MDY^FBc|l5B(3/lGFHk>(V/2&7Ec&z%&zc77c%&J%7WVc.b#5pB6>|5FB)jCcMML<C-8t<p+GF)&F.+UfM-TBD(^pN18+Ub.Y<pGP+M>|5FBcHl^pW6kB)-4Y+4KL5+Wp)|Dt^pf(AKjF+.#fFN1OV>(VKO@1.+U|8-*yp:+2C+>OyDl5*-C|^2lVSBNlpFNHS2d+9%/W+PMzZ_87|KU_2q*y+#8OKOplkS;694+W(2O",
				"(2+CLN4b+9z+DHyU12G%<TS<.TML)ON+p>8BtM<RNcMKHCH2X^PMG44@JWVc.bRWfl+2lzPk:|qyA+7NpFU+1zcRzO*|JRZk(Wzz7KVTB+F+-*+N(2c8B32Y_d#|5F|:L4Op_&KY|UKA2FLOj^B+dcC^))6lB.B&%B5%E.B(d6-+lR/c2z*|zO3l*+-;lL>|bS<GpPV>BcYWZXtJ*-Ry>MB#S6Kf+D*Jpj+pFkBZ^p>9+Vp+84+CtB#UY+fFBc_DG-O|5FBcSBRqEWO1f#+UFBtD(4yk|WUpcLG2d+)OHERF(zZ(d;^CGO);lF<K9^VB+O7pT+8Kp|9#y+kTVMM<",
				"#l1>_UC*ttFlyR_VJp2+z6|K:zZ%7|8kZKjf+zqPddk77^+kdXl2+L%1B|5FBc<pCA)c45M*+T*-BpPR+5+6+P^Y<FzWO-*BfU5.XEFBcyU;fO/|#9^lN+zK5K_+O>MN6-V^4C+l2%Ay#)(p4SU@py+b/-t(/L++W.pyCP<pL-B+*4%R|5FRzDK/&G3D+l-TMc&BAORz|b.N63+9_fVXb.cVyYl>^4;RJ|*3Nc|.c^)+dc_t9M+|+DKc6.+29pWzNOO/J%;WHpb|Hc%6LpOTLRDM1dRlRB+<PFBDpZUO8pqF2SMF)+Gz2TMA6jFL>WKF)OcHEROY%<:d12S#S+C/",
				"dy+Tb.E|8.|Jb;WYzCHMyzZ3d8;k/p>22W+B<5L(MfGFRYp@bLcVTB5F1^N9RJBc-.<VGp+D2Mj>RY|<5Lctl|5F95z+RJ|*b.cVVG|5FBc+kCp1+6&lR+p<+L37fN<|R^p1/TNAZMfOXSk-*/%q&H4Oc-FKG#d+6y#PSK)Bc-6>HW+B*CPOMVN*BR^pc/HAK7fVOX/)+*9/FqSl4O*-D^GBd+6y#L>Zk+l|+zWKU_)+F(MU2MWC%:PU4+tBClSGpUK+tp&OpdtTFZlNHEROB<+y)4OT5z_2+FBcWK2(k+Fz9L2K(+Oc>2U(7)_+4B:Y^lOp+SDj^2((z+D4O/zz",
				")B;AzqVS(WM19D48y<tlzZzd/^C8+G#dlB(<YTHERUKOOL|MBqVZ//.d/##CC#GSKVpG<F+K7*k->YG2|5FBcO^FWB3RN<L+4W<UKNjt|)X|+c|+9p&+R+clB>-D+@_+fKp2(t+A2ED<YWtk|T+czp4Ok-FBEpf2+*-cLS+))-p^Nf+6V5yk_p1Fl%OUYcRP<49MV_%+UJ&PGMzTBS6-*OF+^7OlVLM(4yO+U|5T(^:L-;8P-yWlzT+56-WO+2J8VK3RJ|*l5zz;b.cVFc+KpyM|>jG2|5FBcF1ORBNRJkL+^*)2z.F%BNpE+Rb2cMB>|Db95+f4p2(+97p%:*X(",
				"W<TWN<1+L<|BOMSy+lOF*<Ml2tY*-5O+My;/&l+C|NDY#+y2(OU^9|5FBc|+WSTj4GO+#N+W@.GcM+p-Dfl2(RHkVVHdzzVHzH_zdddk94BO+#j+pYOES-*OM(;lllA2(bKCGR7|5Kt/;ZP|b2.VTcSZT-W7OLDZFcFc#p+F6-^K1TLO#U*+Vc.b<yUANL<U+p4G<3L%pzZ_98|kD|^+1+Y*.)pFBc)pL+U)dNDq)4BFG3d+7P@^+CG.BOfK>RU45RMj2>(+p2.c&2(BKCJ7+pF5cP2X/pOS>q+|5FBR+EB%2X4>^5+(*|JRMFBfK^)p:K:BRHER6O@CBc6ytfWt",
				")+X<pGY;kO5MOYcKjC^F%+FDXOKzR|+)D)5VGKRqWdUM+T*-z#B+FTpp;|B.5|5Fd*+2B+Wl^2TPKB5PSkc(MOlzZd22K4C>F1#ll(FY+UM9bBFBc9(8+p^FM(tRM.+W.V/y+p+CzZ67:^c1PHER+k41L2G4Ny+Vc.b:O+Z<YM-^N9Nz#V<24(RLVc-+l_fDT#6+LR+Rc_k+pN(*Zc-*O-JFC+5UL+#BO2>>|Od|5FBctKCt+Wy/^pN9*|JR|cSy.G4BzyDBOSBOf.>ppT|Bp;Lzfp*kH2HLf_<)Sl%333AE38JJA3AEb%7zUG4dV|z6<lt+W)2W+<jqKU@(H|/G",
				"l>SApY+OkG1DSC9lGy<Y(z_KcdS+.zb/tW4f#8Y;TM+C#3t+6R*G#Uplkp+t+(EB11BHEEE1M+HER.L2+_2q|;YlldCtO2NF(<|5FBcRJ4Upkk9+b+-*Myc+8T9^)Vlp+.z|cV<@t52Nz|OdRUc8q|5FBcRNb4*&L;fVc.bOK#-J.G|*-VlLO4(XpA-z|p(G|>+dL24c<|5FV*|JR4C5dTkW|.|Wy<RMP)+M+&JFU+9(z4+fTp//R2S*FyPz)O#U2F+5R+pK<t2K|R+P%)5V^6NF+O2N|zZ3D7:C+)F^Wy(>Fjt^cRO:+OT>6%LWWL+fK8^-zKGpKMcMjt^Xf_pO",
				"p.MMzVqf^MpN&WT+t7E2OV+#C#>6<p|5W/;SbBU4-^KX_+%zZ1D9|C++24T4%|5FBc+pUc;Op|(+BU&yl)D8_L+DB-*M<l+PN(LlFBcBPWHERBc(O3(8Y+pZT(kyJO+N2**2/+AUMp|Z+47|^T_:Op@TURRO1VK22>DG2dVlAz-jzCS8ylOS/NKK;z-YNz^:L*.qEf^f5+B<K#CMRL^c5pz#YJt7R<.+zJ^c(+Z)c:*-lFjL5D|1+k)KK.S|LMBb(2T*|JR4Bz53Wy+6.+O+B)RkXk|5F+VOpc+8k<>tO+R+6Bfl2_Vc.btYpW&3Wy>P#C<jcp9FHFd9H9FGFH4)",
				"YS6MNX3GYp2P*:+BBfO-MztWKO+B;P44_2>)FRcJRZ7FZH7FFHER<+<D6fXNqj5)pdTV%B/4<E(VYTkDl)VpGfL+>)+<;zN3++Sl+pK+K9D1Vc.b9|M-**KPLd|5*c932MD:/|O#.O8d+#.8+BT&(pOb.-Vyq^lbk#p1ftWy(-M^(^22DlUW^+zZ67;9KO+B^9G^+4.bUzl25OOz4p>kLWKON||O+<Cj_AWyA|5FBcp*-6C&UpL+|O*|JR5z(#W82+BSWyG|5FBcEk+B726/Y)zLJD(t+.CdBz(VNTlyCRDl+%2<1p>@DkU2LGS8H5k+zt+BT4|_G#dKMMp++CU+",
				"G2+AB+1cHL52lp(<C1F7;<p_G+RJ|*yOMp7O)VS+j(PNpT|c+K^M+p(BcHW9|<FJ#B#HER(5<+-*XTY<RV9G|5FBc++@|+zBZK6ZZDKK--R..--F(|O+blMU7/dOW:%+UM*-2KB+tOkV215lPKA^KMpEG-c+(k+LtSWNNWl2z-L>XC5c*9y:)3*dN9lUpE28++3kz4LL>#z5+zBPNpTTpBO&T4q_|5Ft)Sy^zK+O)SU9JG_DMGCTWLFz4KcWf2YOb.cVC4<|NFfkqj>B)d+|c2V*d&BV#4OB;Fy^K.FMtCz4|FBcH#lplObfUpY+Jfp(+zZ6d^;K2Y+8^>yOk88+",
				"(Bdll3+>4pLld+TfBU;-*62OA2tz|7U)T+fy+|7t#+>4j*-D(WF2O27y:|Bpfd+E#5*W2p+SF#L1+NGWzk+7b++yW4J;#PPpllH46.LO+KDWGzDzMO+(FBc+5k(p))YtR&+Jb.cVNU;ByH(pRHRf<DK%<-5O<y(Y1NTpCYRqO->_k(j9KNUBk@+JcRF2p_*d+STMMY(A|HERC>#Bkz2cXGRJ|*XO+|5FK9)CcWzEPF+<SM<FqU_%KbZ.8^__b/Z^.///VV8|BG2dLl|c|5FBctzG-5FSMpzzZ_8^:cDNBCBG9MMO+41+Kc46+Cc&5*l9RNBpLLp+K/<T2|BO+)OF",
				"HG(qNd%+l2L+Wf(<^+A-__G|5FB1NRJ|*RzJJ8Z7z7D8DO^VL|%+6)STD9)LO+kMH(Jc2jTYUkKNG@DX/5pR+qO1%l+E91K:|tVUcS_d<%c/9X^Y.Of&+B<)UMP|W<2l+Edl&YL+p+O5*-@S#(9B1)d2.S|pp+b.cVXy4X+p<^-PX:c5FBc.6GHER5-B<2WO>tc.M+kOb^A^|M(N+lBObT-*VW#K)#UlMt+#<HOG|*#+2jzZ(78|C^2Vp+N+VLKB5;4P+.yjUB+py3M(J+24f^|5FBcp;3>OBGYkf2B+>y*WK*6><p/KBLcpTWK44c)+BP4p(l<%k;d<:|T:ySMt",
				"KUO9Ul7-R4+qNt)*XU(Rc+yb#plzWT<+Bcp|5FBc.l(2)W2Y.49T<B5FBc+PdMkz+2Lf(NDqNtVc.bTjtMdVVFy+7yK52TGU+zYR4+DR^O#k/VfSM+^O|B>:;_*;4p2Dzd)+^yXKzZ%8A|kLU(M:cO^Y+-OCJ>*-J+-*|J+&.-OGG+f+^OBL|k+ld2BBcp<59OS_4R3SC+B7WzT2z(K.5FR>+6F6HERNM|(V#G+JJ%EE8J%1JHH88)4+|;/.pW9BcY2Fz<Fll*|JRcpfLt+NMy^F@pC_(pKFCL|5FKzOGp#CVcSK#6zP+|*5jbWPWG1dl2L<AMk<>+DO)B/pB+3p",
				"2+dX2+F+UR^F;+B.k/l.VlT*-3O2qpVT/ULtB)R(|.7M/HERjy+F+Y9#K+zKfC_p(LM4++Lfy+bt|Ktp_BG)d^45S1WOy#Oz;FOpKW;kCCR<zZO+fVc.b^5+@H|5FBc+Y9c#M_<O5*d(+Op(H2+.VMBNp-D7z+>(DOylb|Opc-Sk+FCNpz)WRB+YK2NK(>|5FBcHc*|JR^45tRWzZ68A:c|l1&AAG36:A:%A8%AJ7TS<PqYF2-BUZBLWR#d^PB*FBcB>+l|.dC4fXMW1C2lZj+-*|)%EK#4<z)9MBk+SUzUVEF2+Oc+kp>(DLcNW&M29z+lV^4*5NpPp<|Ly<TDT",
				"24M>W2Y(L^VOp)c|+J+j|p+;*z(ZK+t>kL(M<SC38+H)+<|5Fy;kD^|WUG_DB*-*OF/M|5FBcG-UF&2#<2pkU|5Fyp4BZ<MyW2%Nl#+pO8CS(/3zNY#462+)HW+@+HERYH68D^tlKcB_b.cVTNzp+:cFX-K+^cFBcGl5zflKY-*-pLGtO.+Bp#N^fO(|<M>WVlbp8cl+/M%BPcRJ|*;TPtO(+GD:Z#OMyK|.25SC_kOf)+BB+XLJ(p+z+jVW<A+Uz6CMRFlTqSE7R1b117JR9bbJJ9dRR2K+OCO&UqzB4y)++5FfVKL2pk5VTF>p4BL^NB4cPczA+zZ179|KTG*d",
				"#Tk<d_/LU#9LUHER>4FA(8<dtkFECcJR111B:77C77Ey#2LtyYV>-.)|^VMXU+Hf+N6+zZ*9:7CXV<M%;z+YWH8++FBclkN/^|5FBc.62+OM+DXftLKPO2+.*V5zz+.)bO&pF3()Sz+f+zGTVp6.#flq2-YWyN|lLWp9N-*8Z-^2Oj+N.Tky#M/|54+_/TMZGT|5Fz+O*;+4O&P+<b(*-pKAtKlG1DApz5.^|UO3+DdpKlG<PlFc/pWD2l@%^Ky#8ZkJVc.b+(4+4KO2H#)M_Z3|5FR+dOj(p|WSLd(^S-zJfq9Z)K5WGS4pYFpO|M>6;+.Up><fl2+(G*|JRF+2",
				"FBcX8#kp5l/Nt:A+D4+(|MZWFM/OyP+UCX2*-*ZLpWS#Y);dT;y(ML<%3+N|W5Rbq*-pp+94LDRA:+flGU|5FM+9j5l(HER#f4/Od+ftc+(+pfW23+S&1>pq268*F2l2OcyEbczZO7|^K94TSRL_8.<<H-4cS/O1UtJG*dcWl-_Z9Fp1DR+|+p(Vc.bl5OZYEF|Z*|JR+NO84#OR6MT3%yAL+klG)(+C)+|t2@cN#kR|5FM+>jLY2G<6M#(F+pqU8TNLS<K>y2C+PGRbT&:>^.VHKHVBBJ.7BHBBV4c>8*1)C;48kEO1)U;C+p<bDp|5FBcl+OdP_W+t-YGF+kd2",
				"#F+MOzLpO8zP23DO+pM^L(BN2B|R9HERVBq2yRpG+O-*&CB|5FBc+dC+tyRpGVPp2pOX+L+42zN(lN7+R>VUF7OTfX+2D6+-Sj*-U+4&F_+ZG+Z^ZCB|5FBc+(q+pSAzLKYW|KD)KY(dCJM8-d1+|KDTz-kWyp^N)kMG:|p;+-&Wy4B%:8Ml2BG*dMpGFBy+L)FBb.cV7UTttzZ)9/AkzY4Kc+@T#)B>VSdRJ|*3#.K;+(TUpR^Ub|J|K:b#fW%f7^lSGTWWJN4lO3<<CkMV(F#j+R|9lzP1lO(Ok6N4<F_<86>2Y+;lO9+t^&3+1<F_*./cHE55H*5/.*<fz>2L",
				"c#F|FCT2b>W|;dGk+61EdSddP.21dHZ%<BL>ckz-N2cG6D#^pYK1T8_@b.cVWU6c*Kjyk2URBqb1W<HjKdWfBGTdJ)W|t^Cdqd(j1#N*N|*-dNkdbl2j(PORJ|*|G<^>1F4FtcHERGzZz9A|KElRKd@@W-;RtjXdyTd5Y3XXt-.|5FBc3@-)Ny2/G5j6>WdWUkAU5p*Y::9+J:D:VLVVddL)lFp&P(GpzS148142</d%<F;Fl)q1)TlE7dG(EdcK|5F2fZtq1dlXd&;T<@LGO|ZNy1d5#74O72t^&-*7p2dp()SpzD>XGpMFBcTNO(lpf5_;/q;)KZ^)UpWkqppP",
				"tWp<5^Gll1DOZ>^+KX4Z+KyRUz;<<+L-2tlz279+)9b.cVGX8HER:q5cLMS.67c+O<2T+K>>+T|MY51.OBzZ%D/^kqpy|5F;b2SpCy#L+6BV|f#5tApdM|Y^kM<d-*|5FBc*-l#(+O7(c_P<+)N4|V2BB^&98VpGOCRJ|*5)+Y-LE#f+BT.kTU|N+S-(GlURWpLO+d3JW#N+R4pVkO+)KfBKR+YpCcjzElcN/d4+^DMM.O69_ypTPRU++<.U/JFFHFHHJ%%JG(@K2cfB|CdctWNbzMz(Kl82*(WG*D(RpS+)B4&Z2+C+p1>Wzy2SzLzOB:+O_A348B;PFBcj|Vk*",
				"+SH4ZjZyf2lpkz++81lS>@^zk+Z_MfTkG8>(+Lpt%*G2DG5FFE..EE.EO*dl^2)>O+S1d<##|+2<7<Pp4*-WN6YB*RTppJUV#d|8TBMb)|5FBcO:z2BLP#|OVN+|8<)zFBc+/XK2B4H-*cW%C2y<Vc+HER^3+KMW|B(/p+UGJK-+UXVJGTTfRld+_G-yA2lCLM+YzZ59^;k+4>RCYN4Vc.bz6LOzOVcYzRp&c#KcOlSB+3*|JR|_^.U++f)1zMy|2lCKMGb&Ly/(pM4|JkB+LftN)p^+++/ATR1t(qU;P7Bjd<N+pc(Hz);WC-B((pROVcW8<7|5FqB:KtWOz6KO",
				"|#P-z4_Xl>(HjlDXk+lj;ScLMGM:z;MVTFX2CRqMEEjEd9EM7.%)&X(kWWK8@V8M&|6GH^Syq-76./t1T-*bFU(|5FBc12fG3l7p2;Oc|5F%;kt2&(%#z2bV^M;t)WXUKGYD#A<|M)L8MOMl)MHz^STql7c./b.cV6tF;M<5GX-jM+6FBcyzpN42#&-*-;&.qy.Mz;CY+3YX|A(4C|Lb;8*LMU(z1f5RJ|*^cfqtXMBt<|5Y(N7|1V*WP/St3)MBzMXDJX;MFM:L#HERTN^P(RFz*>W(#MbWt4ZpJBDH)MMcG%2jAl;86|FG4dA.D+yBH5fcJEMFzZ_9|7cB5B^t",
				"+STL5O%C5+AF*c|M+PfzZ39|7klU8Nz/LF^2)LCCkpRNtzO.jY1+<HO1(Kq^1>cZ+BPWp2O.pf|(G_d+(L+WcM+p/zY*-kBRM8O<HER8>TKfzO()F&UPF+^S8W#>R#lM;+BR+ppJ+t-:bJ@kLY#>Bzfz4-4WzY|Zt)>4^&6)+-TK2y+*|JRHD+p2c:<4|UC+SV79G9G_EG_97V7EB|5FBcF3l2KNpU2ZMp+A|KB^4F*KSHy#.cy+DAb.+*DMO+<qlJ;t|5F.j542#RMOFBc+S6NBcOXl+B(ck2lCARy(^Vc.b5-*zDOp/KfTB)N(X<FW|L6+l.U2yl;B+W5pTJ%<",
				"8|MMRy4W6KO|2+TSL<*-&<RfDOM+c;zZ_9A|c<O/GYA);RT2#U27lKYp8BbGJB7ZO8+C+k)D+Z^.2>Ubd2(t:O7|5FBcc1q+pcNRU#(C+^pdBVc.bd+dLUWC@|5FBcOyyq(Ht+NZ+l8B5G^|2+CSONETk-(GUK4)^RW<OM+%V--*t42B)+Jl&HERN+/)|L1(M-y|BVkK4>D|5G)D*K>^*|JRp#cW(L_PR6Yp6<+;pk5O36p_+Vc^+2+tW+Lp/6lOK#pKk.*.z.FFF*..9.>TJXlP:MjAYC1fVlHB5pLONK+WBV><+MTXcLT2+BP(#Hf%l3y4SdpjMBGEc34S+5+f",
				"8OBR24KRTMNb7YO+kpMz8L+SG%D).2*Mp+>F+LM+Xb1p<cfyO3pYF@2RU4lzkVVc.bkMD+dD(PWE72++pK(<VG_/LMCzScVBN82HfOUfypT*H4<)jCK+q>O<O<(Dy2+KR+^l)OzSWP6kC5+#9-|;;-ZZE|1BNA_HTRV+FBz.>(-Cl/Y/KGJO4^WW2L-zZ#9A;cT|pz+>^.)lzSHER44t*|JR;+5FG61tBpBlqt:.2fGB8(<5c*VNB3clA_^&+8p&)*-d+GU+FBkM+dycCp|(O|(p2zt:A73+NUyAd-*j+LdU+5l|5FBc^RYOz5FPKczL+.+X*KFBW6W6^FBcT|5F",
				"J2NzWU3z%MUz(dMzAM+|jy+q.^p+#Tl((_zBy&/SX.HdMpf6+%G6DPTC+;B73cFBc:+YT+<2(#++TN.d_FByW(WH#Bz&P>1-M6JKY:z+UppUp#2f142OO2Z)KC+2Z^p|cFNLd4l;PYOSR9kGGEVVEVG+jq|c4JptNOf8+*lMB8-B@S|5FqWc4-*K)>-LW)d*|JR8+_||*KTq5t8KLOHER|zZ279/kdZOFY+5B<7t*/BLA>yZ|2#XLO<bFK5JFKOb<*-JfcP^.cU|)Cl^.p+N|5FBc1+LT+^B(clf(Nl5F4t@+SfOp4Vc.bB>)C+yCFpBq+;<O52:+WMH^++z)pl<",
				"4+O#>+|^OkSX4GcKc+N>Yy@OW%p5&+|tDPTUc+F5ZLMc8K-*d+F*p)lWOf4FLVc.b9GCKXMT5jKOWlc(AR(D#fj+V:G2^p4.|5FBckMZ5j+O(:N6bANG*d&YL#Ffp2p#Fy(T2|5pLtl2d8+OdUR<c>_(9-*|JR9T;V^+|5FBc-%|;R28+DYC++|_2V48J<%)7OSlC+)M+<fyVR2)+RL|kS9)KFcGtNc+F#KOYHERSLqjpp^j+<PP+RB7H77b/B3EE3P)+-2C4+lW+4k>UV&Y6*-*d4kq4G&zZ37/8K|M+k1NWTtZ2Cy<W_^6D1y(^Up14<M+lMSp;O5pU%FWlc(&",
				"VL2^c+zZ*D/7k(+lB*2#lO+_9;^U-NHyz)U+|K;YcB*|JRF3K+O<H%2z8+z|pUcBM+GW|5FBcCzPJF)F#<Ojp|5FBcVMBR+fbySO+K7p<M>7pT>K+plHERWtk%/.%.J/GJ&D//%.&GJP5Fp)6#<:C4*-Y-B+O(+2BpV(2K5|L-<+924z9L+NpU+YFWT8AP(TRq4t_;y:Vc.b#jBcOq2zW2>+^cWt@+fFyflzF+zNYR.kX(CB|(+dBp^U48BbMRN&-*Hf|LMFkcOdN3V4_cyl9LT6|KlOl+S6^^5RS6OtCAR+>(C8)5M9pMV+XdG1dK+)4WSk+p#+OOyLl|d*5<T2",
				"_Kzy.O<&2:6O&LY/2MPN1T)<pFt)L+BFNVC6O#)+Rq(UVW7/9NV;#G%D4RJ|*9SCz+BOJdSYz.+lz|BFHER7>Wl7TT#+z1J2ff>(94z2+zLBF.cR6dYz2@93_lK*++B*-NFSCty++KFBcV4j|:L+5O5*MAF#+M+|5FBc(45+3L-KOk(44c;+5RMUp^-cUPMKRd+jp<TT^+)18ZH88G8J8HJE||kpXR2tOlp5yAOfzZ%8^|KFBR.UFWHb.cV4K+XyMWByUOfz+kpc>;O>++32^+B(#<c*.lBS|q(d<)2^y<tBpbpT-*WVbN(C2+c^lpf-kYkP+_ppC|5FlzMcdW/L",
				"MK)fpLl>6dzj3G%dR(#O<GR3tON#LVVHV9EV199HG+zUN+lpBk+Rj^|5F)U<p(M>Kz5(4<F|DBUNW>kN*|JRMDy)pO:lDcPO+|tUP6TU:d5Y7Vc.b_JFBcMpSR|5FBc2+F<RBT+KW-*K+l-._XkOS+pWfF8|RLWYbL1+/p+c6*-|>4|l52zZ1d9;Kz-HERM*-28&pSC++POBG^B.WSY+7LCT/TL4T|Mk/p2+A(8jPXO(cLy^.q)zt.(_2*T4##2++y8G+zpO<FB^fkM2+AM2%JCf#lJc^c)Kz27Ty^5F4<p++GCBcBdO*DF4Bz@.+WFq(l5C+|OPY+&K+LXNBcty",
				"K3;Pc:%7|yRL8yFz2T/16_OD+pFc@-*cNzUKPlF+pK1;.+q|p+z)KZl+SE+NS2T<Z2Lkzy+U17pK1l+WO>5kSppOpVzZ58:8CWVO<yc/+2)GHCCdH#dC##HHCCK&;NlL/y^VF3WO>bUXlf+pzT59tzY+2-f9(^2Y|t8(DZj^M-.OpEj%%W4f+JM+VFBcTR2^*+(4zM2XFW^k(K<(AV^U.c4S87Lp+>)U_Ok*Rp9M|JbFZF_RM3SyHERk|*-.Rf+NPG#d++)DKp>DcJ<cLlYY|O4OM6b.cV|jAW4L-.|5FBcTRJ|*+(+Mq+5<O)+54N+Hz2T+q9&(|5FRttX*6l1<",
				"KFBcz4pp+K@F*zB+kpML+O2HCHH-CD.-...&C%D%8S+Wqd^2:FBUplK<^#OS4j#6l)Nc;Vz(1O4YFB(d4T(dVEM|qFB+NfM1+Wl8T+O)>2TRUU2RK&+#t<BXW/|jkt2>XcT6KO4Zb.cVGpp+5My*FB:t5l)kc<++W|5F_9+E+M-KF.czzpp+Zl)#%;-y9Jb5|_((yVA(GV7MG*dB+3+NpM9+O287ROY|BPAzBUf2+YLHER2>Nc9t5J|5FBcyGfpRZWzZ5D^;CdS2)kc<RSO-*z_L+LRJ|*4^(zy/(|F7l>kc<G|LOV6Y|^<+T+NfW1+Jl83+O*-^PLGB/UpPRK+#",
		};
		for (String cipher : ciphers) {
			double[] obj = new double[3];
			objectives54(obj, cipher);
			System.out.println(Arrays.toString(obj) + " " + cipher);
		}
	}
}
