package com.zodiackillerciphers.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.FrontMember;
import com.zodiackillerciphers.ciphers.generator.CandidateKey;
import com.zodiackillerciphers.cosine.CosineSimilarity;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.HomophonesResultBean;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.ngrams.RepeatingFragments;
import com.zodiackillerciphers.ngrams.RepeatingFragmentsBeanEntry;
import com.zodiackillerciphers.ngrams.RepeatingFragmentsFaster;
import com.zodiackillerciphers.tests.jarlve.AZResultBean;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.operations.Delete;
import com.zodiackillerciphers.transform.operations.DeleteLinear;
import com.zodiackillerciphers.transform.operations.DeleteRectangle;
import com.zodiackillerciphers.transform.operations.Diagonal;
import com.zodiackillerciphers.transform.operations.FlipHorizontal;
import com.zodiackillerciphers.transform.operations.FlipVertical;
import com.zodiackillerciphers.transform.operations.LinearSelection;
import com.zodiackillerciphers.transform.operations.Merge;
import com.zodiackillerciphers.transform.operations.Nothing;
import com.zodiackillerciphers.transform.operations.Period;
import com.zodiackillerciphers.transform.operations.PeriodColumn;
import com.zodiackillerciphers.transform.operations.PeriodRow;
import com.zodiackillerciphers.transform.operations.Quadrants;
import com.zodiackillerciphers.transform.operations.RectangularSelection;
import com.zodiackillerciphers.transform.operations.Reverse;
import com.zodiackillerciphers.transform.operations.Rotate;
import com.zodiackillerciphers.transform.operations.Shift;
import com.zodiackillerciphers.transform.operations.Snake;
import com.zodiackillerciphers.transform.operations.Spiral;
import com.zodiackillerciphers.transform.operations.Swap;
import com.zodiackillerciphers.transform.operations.SwapLinear;
import com.zodiackillerciphers.transform.operations.Width;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.MersenneTwisterFast;
import ec.vector.FloatVectorIndividual;
import ec.vector.VectorIndividual;

/** represents operations that map the plain text to the cipher text */
public class Operations extends FloatVectorIndividual implements FrontMember {
	// public String lastMutator;
	// public float lastFitness;
	public double[] lastObjectives;
	
	public static int NUM_OBJECTIVES = 4;
	public static int MAX_OPERATIONS = 0;
	public static double[] BAD;
	public AZResultBean azBean;
	public boolean archive = false;
	
	public static int initIndex = 0;
	static {
		BAD = new double[NUM_OBJECTIVES];
		//for (int i=0; i<BAD.length; i++) BAD[i] = Double.MAX_VALUE;
		//for (int i=0; i<BAD.length; i++) BAD[i] = 1.0;
		for (int i=0; i<BAD.length; i++) BAD[i] = 0.0;
	}

	/**
	 * similarity to rest of population. 1 = totally similar. 0 = totally
	 * dissimilar.
	 */
	public float similarity;
	
	/** count of Delete operations */
	int deletes;
	int MAX_DELETES = 0;
	
	/** count of Merge operations */
	int merges;
	int MAX_MERGES = 0;

	static MersenneTwisterFast rand = new MersenneTwisterFast();

	/** list of operations being performed */
	//List<Transformation> operations;   unused
	/** number of possible operations */
	public static int NUM_OPERATIONS = 22; // op # 0 = "do nothing"; the rest are actual operations
	/** width of operational unit (operation + arguments) */
	public static int OPERATION_WIDTH = 6;
	
	public static String CIPHER_STRING = Ciphers.cipher[1].cipher.substring(0, 340);
	public static String CIPHER_ALPHABET = Ciphers.alphabet(CIPHER_STRING);
	public static List<StringBuffer> CIPHER = TransformationBase.toList(
			CIPHER_STRING, 17);
	
	/** a cheesy way to map transposed positions to original positions */
	public static List<StringBuffer> CIPHER_FOR_POSITIONS;
	static {
		CIPHER_FOR_POSITIONS = new ArrayList<StringBuffer>();
		int pos = 0;
		for (int row=0; row<20; row++) {
			StringBuffer sb = new StringBuffer();
			for (int col=0; col<17; col++) {
				sb.append((char)pos);
				pos++;
			}
			CIPHER_FOR_POSITIONS.add(sb);
		}
	}

	public List<StringBuffer> output;
	public String outputString;
	/** number of operations that produced actual changes to the ciphertext */
	public int operationsActual;
	public String operationsActualSequence;
	public String operationsActualSequenceFull; // version showing args

	public static int FRAG_N1 = 2;
	public static int FRAG_N2 = 9;
	/** sorted repeating fragment probabilities for the reference cipher. */
	public static List<RepeatingFragmentsBeanEntry> referenceFragments;
	static {
		referenceFragments = RepeatingFragments.repeatingFragmentsBeansFor(FRAG_N1, FRAG_N2, CIPHER_STRING);
	}
	/** reference homophone cycles */
	public static List<HomophonesResultBean> referenceHomophones;
	static {
		referenceHomophones = HomophonesNew.beansFor(2, CIPHER_STRING);
		
		//for (HomophonesResultBean bean : referenceHomophones) System.out.println(bean);
	}
	
	public String plaintext;
	
	public Operations() {
		
	}
	
	/** initialize a key from the given genome */
	public Operations(float[] genome) {
		this.genome = genome;
	}
	public Operations(double[] genome) {
		float[] g = new float[genome.length];
		for (int i=0; i<g.length; i++) g[i] = (float)genome[i];
		this.genome = g;
	}

	public void init() {
		//operations = new ArrayList<Transformation>();
	}

	/** convert genome into series of operations.  use the given input, since parameter domains sometimes depend on input characteristics. */
	public Transformation operationFor(List<StringBuffer> input, float[] vals) {

		//init();

		int op = CandidateKey.toInt(vals[0], 0, NUM_OPERATIONS - 1);
		float g1 = vals[1];
		float g2 = vals[2];
		float g3 = vals[3];
		float g4 = vals[4];
		float g5 = vals[5];
		Transformation operation = null;
		switch (op) {
		case 0:
			operation = new Nothing(input);
			break;
		case 1:
			operation = new Delete(input, g1, g2);
			//operation = new Nothing(input); // Delete off the menu for now
			break;
		case 2:
			operation = new Diagonal(input, g1);
			break;
		case 3:
			operation = new FlipHorizontal(input);
			break;
		case 4:
			operation = new FlipVertical(input);
			break;
		case 5:
			operation = new LinearSelection(input, g1, g2);
			break;
		case 6:
			//operation = new Merge(input, g1, g2);
			operation = new Nothing(input); // Merge off the menu for now
			break;
		case 7:
			operation = new Period(input, g1);
			break;
		case 8:
			operation = new PeriodColumn(input, g1);
			break;
		case 9:
			operation = new PeriodRow(input, g1);
			break;
		case 10:
			operation = new Quadrants(input, g1, g2, g3, g4, g5);
			//operation = new Nothing(input); // Quadrants off the menu for now
			break;
		case 11:
			operation = new RectangularSelection(input, g1, g2, g3, g4);
			break;
		case 12:
			operation = new Reverse(input);
			break;
		case 13:
			operation = new Rotate(input, g1);
			break;
		case 14:
			operation = new Shift(input, g1, g2);
			break;
		case 15:
			operation = new Snake(input);
			break;
		case 16:
			operation = new Spiral(input, g1);
			break;
		case 17:
			operation = new Swap(input, g1, g2, g3, g4);
			break;
		case 18:
			operation = new SwapLinear(input, g1, g2, g3);
			break;
		case 19:
			operation = new Width(input, g1);
			break;
		case 20:
			operation = new DeleteRectangle(input, g1, g2, g3, g4);
			break;
		case 21:
			operation = new DeleteLinear(input, g1, g2);
			break;
		/*case 22:
			operation = new WildcardExpansion(input, g1);
			break;*/
		}

		if (operation == null)
			throw new RuntimeException("Cannot determine operation for val ["
					+ vals[0] + "] op [" + op + "]");
		return operation;
	}

	/** max: maximum number of tranformations to interpret out of the genome. */
	public void performTransformations(boolean showSteps, int max) {
		try {
			// TODO: if box selection or linear selection are included, make sure to
			// merge the result back into the original
			// TODO: what about 2 selection operations in a row?
			
			operationsActual = 0;
			operationsActualSequence = "";
			operationsActualSequenceFull = "";
			deletes = 0;
			merges = 0;
			
			if (showSteps) {
				TransformationBase.dump(CIPHER);
				System.out.println(" - Measurements on original cipher: "
						+ Arrays.toString(measure(CIPHER_STRING,
								operationsActual)));
			}
			
			//if (operations == null) return;
			
			Selector selectionOperation = null;
			init();
			int i=0;
			List<StringBuffer> input = CIPHER;
			int count = 1;
			while (i<genome.length) {
				if (count > max) break;
				count++;
				if (input == null || input.size() == 0) break; // no point in continuing if cipher got busted
				
				try {
					Transformation operation = operationFor(input, new float[] {
							genome[i], genome[i+1], genome[i+2], genome[i+3], genome[i+4], genome[i+5], 	
					});
					if (selectionOperation != null && operation instanceof Selector) {
						if (showSteps) {
							System.out.println("Ignoring " + operation + " because we are already running selector " + selectionOperation);
						}
						break; // do not allow two selectors in a row
					}
					if (showSteps) {
						System.out.println("Operation: " + operation);
					}
					operation.execute(showSteps);
					if (selectionOperation == null && operation instanceof Selector) {
						// 1) perform the selection, 2) run the next operation on the selection, 3) put the result back
						// where it was selected from 
						selectionOperation = (Selector) operation;
					} else if (selectionOperation != null) {
						//System.out.println("selection op is " + (selectionOperation.getClass().getSimpleName()));
						// we have performed a selection and an operation on it, so we need to put the result back
						operation.setOutput(selectionOperation.replaceSelection(operation.getOutput()));
						if (showSteps) {
							System.out.println("After replacing selection:");
							TransformationBase.dump(operation.getOutput());
						}
						selectionOperation = null;
					}
					
					if (different(input, operation.getOutput())) {
						
						if (operation instanceof Delete) deletes++;
						if (operation instanceof Merge) merges++;
						
						operationsActual++;
						if (operationsActualSequence.length() > 0) operationsActualSequence += " ";
						operationsActualSequence += operation.getClass().getSimpleName();

						if (operationsActualSequenceFull.length() > 0) operationsActualSequenceFull += " ";
						operationsActualSequenceFull += operation.toString();
						
						if (showSteps) {
							System.out
									.println(" - Measurements after this step: "
											+ Arrays.toString(measure(
													TransformationBase
															.fromList(
																	operation
																			.getOutput())
															.toString(),
													operationsActual)));
						}
					}
					
					input = operation.getOutput();

				} catch (Exception e) {
					System.out.println("Error during operation on genome: " + Arrays.toString(genome));
					e.printStackTrace(); 
					
					// log the error and continue working
				}
				i += OPERATION_WIDTH;
			}
			
			/** last step might have been a Selector, so we still need to perform the replacement. */
			if (selectionOperation != null) {
				if (showSteps) {
					System.out.println("Last step was a Selector (" + selectionOperation + "), so reverting input.");
				}
				input = ((Transformation) selectionOperation).getInput();
				selectionOperation = null;
			}			
			
			if (showSteps) {
				System.out.println(operationsActual + " actual ops.  Sequence: " + operationsActualSequence);
				System.out.println("Full Sequence: " + operationsActualSequenceFull);
				System.out.println("Final result:");
				TransformationBase.dump(input);
			}
			
			output = input;
			outputString = TransformationBase.fromList(input, false).toString();
		} catch (Exception e) {
			System.out.println("Error during genome " + Arrays.toString(genome));
			e.printStackTrace();
			//System.exit(-1);
		}
	}
	
	/** returns true only if the two lists have at least one difference */
	public boolean different(List<StringBuffer> list1, List<StringBuffer> list2) {
		if (list1 == null && list2 == null) return false;
		if (list1 != null && list2 == null) return true;
		if (list1 == null && list2 != null) return true;
		
		if (list1.size() != list2.size()) return true;
		
		for (int i=0; i<list1.size(); i++) {
			StringBuffer sb1 = list1.get(i);
			StringBuffer sb2 = list2.get(i);
			if (sb1.length() != sb2.length()) return true;
			if (!(sb1.toString().equals(sb2.toString()))) return true;
		}
		return false;
	}
	
	

	public double[] measure(String cipher) {
		return measure(cipher, operationsActual);
	}
	public double[] measure2(String cipher, int numoperations) {
		String mrlowe = "H+M8|CV@Kz/JNbVM)N:^j*Xz6-+l#2E.B)BpzOUNyBO<Sf9pl/CSMF;+B<MF6N:(+H*;_Rq#2pb&RG1BCOO|2p+fZ+B.;+B31c_8Tf#2b^D4ct+c+ztZ1*H(MVE5FV52cW<Sk.#Kdl5||.UqL+dpVW)+k-RR+4>f|pFHl%WO&DUcy5C^W(cM>#Z3P>L29^4OFT-+EB+*5k.LzF*K<SBKdpclddG+4lXz6PYAG)y7t-cYAyy.LWBOLKJp+l2_cFK|TC7z|<z29^%OF7TBR)WkPYLR/8KjROp+8+kN^D(+4(5J+JYM(+|DpOGp+2|G++|TB4->R(UVFFz9<Ut*5cZG";
		try {
			double[] objectives = new double[8];
			int diffs = mrlowe.length();
			for (int i=0; i<mrlowe.length() && i<cipher.length(); i++) {
				if (mrlowe.charAt(i) == cipher.charAt(i)) diffs--;
			}
			for (int i=0; i<objectives.length; i++) objectives[i] = diffs;
			return objectives;
		} catch (Exception e) {
			System.out.println("Error during measure, genome " + Arrays.toString(genome));
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	/** map the value to the range [0,1].
	 * this function's value decreases as x increases.
	 */
	public static double squash(double x) {
		return ((double)1)/(1+x);
	}
	
	public static double unsquash(double y) {
		if (y == 0) return 0;
		return 1/y - 1; 
	}
	
	/** smokie's "maximize the area between period graphs" idea */
	public double[] measureSmokie(String cipher, int numoperations) {
		try {
			
			if (deletes > MAX_DELETES) {
				return BAD;
			}
			if (merges > MAX_MERGES) {
				return BAD;
			}
			if (numoperations > 6) return BAD;
			cipher = cipher.replaceAll(" ", "");
			if (cipher.length() != 340) return BAD;
			
			lastObjectives = new double[NUM_OBJECTIVES];
			
			if (cipher == null || cipher.length() < 50) { // no point if the cipher has disintegrated
				//return lastObjectives;
				return BAD;
			}
			
			//double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher, 6, RepeatingFragmentsFaster.z340median, false);

			
			lastObjectives[0] = 0;
			for (int p=20; p<=160; p+=20) {
				String re = Periods.rewrite3(cipher, p);
				NGramsBean bean = new NGramsBean(2, re);
				lastObjectives[0] += bean.numRepeats();
			}
			NGramsBean bean = new NGramsBean(2, cipher);
			lastObjectives[1] = bean.numRepeats();
			bean = new NGramsBean(3, cipher);
			lastObjectives[2] = 6-numoperations;
			for (int i=0; i<lastObjectives.length; i++) {
				if (lastObjectives[i] < 0) {
					System.out.println("SMEG OBJECTIVE " + i + " LESS THAN ZERO " + lastObjectives[i]);
					lastObjectives[i] = 0;
				}
			}
			return lastObjectives;
			
		} catch (Exception e) {
			System.out.println("Error during measure, genome " + Arrays.toString(genome));
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	public double[] measure(String cipher, int numoperations) {
		try {
			
			if (deletes > MAX_DELETES) {
				return BAD;
			}
			if (merges > MAX_MERGES) {
				return BAD;
			}
			if (numoperations > 6) return BAD;
			cipher = cipher.replaceAll(" ", "");
			if (cipher.length() != 340) return BAD;
			
			lastObjectives = new double[NUM_OBJECTIVES];
			
			if (cipher == null || cipher.length() < 50) { // no point if the cipher has disintegrated
				//return lastObjectives;
				return BAD;
			}
			
			double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher, 6, false);
			lastObjectives[0] = ioc;
			NGramsBean bean = new NGramsBean(2, cipher);
			lastObjectives[1] = bean.numRepeats();
			//bean = new NGramsBean(3, cipher);
			//lastObjectives[2] = bean.numRepeats();
			
			double[] CS = CosineSimilarity.measureSigmas(cipher, true);
			lastObjectives[2] = CS[0];
			lastObjectives[3] = CS[1];
			
			lastObjectives[4] = 6-numoperations;
			for (int i=0; i<lastObjectives.length; i++) {
				if (lastObjectives[i] < 0) {
					System.out.println("SMEG OBJECTIVE " + i + " LESS THAN ZERO " + lastObjectives[i]);
					lastObjectives[i] = 0;
				}
			}
			return lastObjectives;
			
		} catch (Exception e) {
			System.out.println("Error during measure, genome " + Arrays.toString(genome));
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}

	public double[] measure(String cipher, int numoperations, String plaintext) {
		try {
			
			if (deletes > MAX_DELETES) {
				return BAD;
			}
			if (merges > MAX_MERGES) {
				return BAD;
			}
			if (numoperations > 6) return BAD;
			cipher = cipher.replaceAll(" ", "");
			if (cipher.length() != 340) return BAD;
			
			lastObjectives = new double[NUM_OBJECTIVES];
			
			if (cipher == null || cipher.length() < 50) { // no point if the cipher has disintegrated
				//return lastObjectives;
				return BAD;
			}
			
			lastObjectives[0] = 6-numoperations;
			lastObjectives[1] = ZKDecrypto.calcscore(new StringBuffer(plaintext));
			lastObjectives[2] = 1-com.zodiackillerciphers.lucene.Stats.iocDiff(plaintext);
			NGramsBean bean = new NGramsBean(2, cipher);
			lastObjectives[3] = bean.numRepeats();
			
			return lastObjectives;
			
		} catch (Exception e) {
			System.out.println("Error during measure, genome " + Arrays.toString(genome));
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	
	public double[] measurePrevious(String cipher, int numoperations) {
		try {
			
			if (deletes > MAX_DELETES) {
				return BAD;
			}
			if (merges > MAX_MERGES) {
				return BAD;
			}
			if (numoperations > 6) return BAD;
			cipher = cipher.replaceAll(" ", "");
			/*
			String mrlowe = "H+M8|CV@Kz/JNbVM)N:^j*Xz6-+l#2E.B)BpzOUNyBO<Sf9pl/CSMF;+B<MF6N:(+H*;_Rq#2pb&RG1BCOO|2p+fZ+B.;+B31c_8Tf#2b^D4ct+c+ztZ1*H(MVE5FV52cW<Sk.#Kdl5||.UqL+dpVW)+k-RR+4>f|pFHl%WO&DUcy5C^W(cM>#Z3P>L29^4OFT-+EB+*5k.LzF*K<SBKdpclddG+4lXz6PYAG)y7t-cYAyy.LWBOLKJp+l2_cFK|TC7z|<z29^%OF7TBR)WkPYLR/8KjROp+8+kN^D(+4(5J+JYM(+|DpOGp+2|G++|TB4->R(UVFFz9<Ut*5cZG";
			int diffs = mrlowe.length();
			for (int i=0; i<mrlowe.length() && i<cipher.length(); i++) {
				if (mrlowe.charAt(i) == cipher.charAt(i)) diffs--;
			}*/
			
			lastObjectives = new double[NUM_OBJECTIVES];
			
			if (cipher == null || cipher.length() < 50) { // no point if the cipher has disintegrated
				//return lastObjectives;
				return BAD;
			}
			
			
			/*for (int n=2; n<5; n++) {
				NGramsBean bean = new NGramsBean(n, cipher);
				lastObjectives[n-2] = squash(bean.numRepeats());
			}*/
			float rf = RepeatingFragmentsFaster.measure(cipher);
			if (rf < -200) lastObjectives[0] = 1;
			lastObjectives[0] = squash(200+rf);
			double hs = JarlveMeasurements.homScore(JarlveMeasurements.cipherToShort(cipher), 5);
			lastObjectives[1] = squash(hs);
			//lastObjectives[5] = (double) Math.pow(numoperations,5);
			lastObjectives[2] = 1-squash(numoperations);
			
			for (int i=0; i<lastObjectives.length; i++) {
				if (lastObjectives[i] < 0) {
					System.out.println("SMEG OBJECTIVE " + i + " LESS THAN ZERO " + lastObjectives[i]);
					lastObjectives[i] = 1;
				}
			}
			
			//System.out.println("SMEG rf " + rf + " hs " + hs);
			if (hs < 2150.71 && rf < 0) return BAD;

			return lastObjectives;
			
		} catch (Exception e) {
			System.out.println("Error during measure, genome " + Arrays.toString(genome));
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	/** measure objectives */
	public double[] measure_old(String cipher, int numoperations) {
		try {
			
			if (deletes > MAX_DELETES) {
				return BAD;
			}
			if (merges > MAX_MERGES) {
				return BAD;
			}
			cipher = cipher.replaceAll(" ", "");
			/*
			String mrlowe = "H+M8|CV@Kz/JNbVM)N:^j*Xz6-+l#2E.B)BpzOUNyBO<Sf9pl/CSMF;+B<MF6N:(+H*;_Rq#2pb&RG1BCOO|2p+fZ+B.;+B31c_8Tf#2b^D4ct+c+ztZ1*H(MVE5FV52cW<Sk.#Kdl5||.UqL+dpVW)+k-RR+4>f|pFHl%WO&DUcy5C^W(cM>#Z3P>L29^4OFT-+EB+*5k.LzF*K<SBKdpclddG+4lXz6PYAG)y7t-cYAyy.LWBOLKJp+l2_cFK|TC7z|<z29^%OF7TBR)WkPYLR/8KjROp+8+kN^D(+4(5J+JYM(+|DpOGp+2|G++|TB4->R(UVFFz9<Ut*5cZG";
			int diffs = mrlowe.length();
			for (int i=0; i<mrlowe.length() && i<cipher.length(); i++) {
				if (mrlowe.charAt(i) == cipher.charAt(i)) diffs--;
			}*/
			
			lastObjectives = new double[NUM_OBJECTIVES];
			
			if (cipher == null || cipher.length() < 50) { // no point if the cipher has disintegrated
				//return lastObjectives;
				return BAD;
			}
			
			int unfolded = 0;
			
			String[] trigrams = new String[] {"RJ|", "|JR", "b.c", "c.b"}; 
					
			for (int n=2; n<6; n++) {
				NGramsBean bean = new NGramsBean(n, cipher);
				lastObjectives[n-2] = -bean.numRepeats();
				
				if (n==3) {
					for (String trigram : trigrams)
						if (bean.counts.containsKey(trigram))
							unfolded += bean.counts.get(trigram);
				}
			}
			
			//lastObjectives[4] = measureFragments(cipher, 25); // median of top 51 entries
			lastObjectives[4] = JarlveMeasurements.nonrepeatAlternate(cipher);
			if (lastObjectives[4] == 0) lastObjectives[4] = Float.MAX_VALUE; // because 0 would be too low
			//objectives[5] = -HomophonesNew.compare(HomophonesNew.beansFor(2, cipher), referenceHomophones);
			
			/*List<HomophonesResultBean> hbeans = HomophonesNew.beansFor(2, cipher);
			if (hbeans.size() > 11)
				lastObjectives[5] = hbeans.get(4).getRunProbability(); // median of top 11 cycles
			if (lastObjectives[5] == 0) lastObjectives[5] = Float.MAX_VALUE; // because 0 would be too low
			*/
			
			//lastObjectives[5] = (float) -HomophonesNew.perfectCycleScoreFor(2, cipher, 3);
			lastObjectives[5] = (float) -JarlveMeasurements.homScore(JarlveMeasurements.cipherToShort(cipher), 5);
			lastObjectives[6] = numoperations;
			lastObjectives[7] = -unfolded;
			//objectives[7] = diffs;
			return lastObjectives;
			
		} catch (Exception e) {
			System.out.println("Error during measure, genome " + Arrays.toString(genome));
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	public static float measureFragments(String cipher) {
		List<RepeatingFragmentsBeanEntry> beans = RepeatingFragments.repeatingFragmentsBeansFor(FRAG_N1, FRAG_N2, cipher);
		int count = 0;
		for (int i=0; i<beans.size() && i<referenceFragments.size(); i++) {
			RepeatingFragmentsBeanEntry bean1 = beans.get(i);
			RepeatingFragmentsBeanEntry bean2 = referenceFragments.get(i);
			if (bean1.probability >= bean2.probability) break;
			count++;
		}
		return -count;
	}

	/** return nth probability, a way to measure based on median */
	public static float measureFragments(String cipher, int n) {
		List<RepeatingFragmentsBeanEntry> beans = RepeatingFragments.repeatingFragmentsBeansFor(FRAG_N1, FRAG_N2, cipher);
		if (n >= beans.size()) return 0;
		return (float) beans.get(n).probability;
	}
	
	public String genome() {
		String g = "";
		for (int i = 0; i < genome.length; i++)
			g += genome[i] + " ";
		return g;
	}

	public String toString() {
		return archive() + " objectives " + objectivesToString() + " comp " + compositeFitnessAsDouble() + " unsquash " + unsquashObjectives() + " operations " + operationsActualSequenceFull + " genome " + Arrays.toString(genome) + " cipher " + outputString + " plain " + plaintext;
	}
	
	public String archive() {
		if (archive) return "archive";
		return "not-archive";
	}

	public void defaultMutate(EvolutionState state, int thread) {
		super.defaultMutate(state, thread);
	}

	void resetGenome() {
		genome = new float[genome.length];
		for (int i = 0; i < genome.length; i++)
			genome[i] = -1;
	}

	public Object clone() {
		Operations op = (Operations) super.clone();
		op.archive = false;
		return op;
	}

	@Override
	public String fit() {
		Fitness fit = this.fitness;
		return fit.fitnessToStringForHumans();
	}

	/** effectively kill off this individual by punishing its objectives */
	public void punish() {
		double[] objectives = objectives();
		if (objectives == null)
			return;
		for (int i = 0; i < objectives.length; i++)
			objectives[i] = Float.MAX_VALUE;
	}

	@Override
	public String dump() {
		return toString() + " genome " + genome();
	}

	@Override
	public String html() {
		// TODO Auto-generated method stub
		return "TODO: make some html";
	}

	@Override
	public void defaultCrossover(EvolutionState state, int thread,
			VectorIndividual ind) {
		super.defaultCrossover(state, thread, ind);
	}
	
	public double[] objectives() {
		if (fitness != null) return ((MultiObjectiveFitness)fitness).getObjectives();
		return null;
	}
	public String objectivesToString() {
		double[] obj = objectives();
		if (obj == null) {
			if (lastObjectives == null) return null;
			return Arrays.toString(lastObjectives);
		}
		return Arrays.toString(obj);
	}
	public String unsquashObjectives() {
		double[] obj = objectives();
		if (obj == null) obj = lastObjectives;
		if (obj == null) return null;
		obj = obj.clone();
		for (int i=0; i<obj.length; i++) obj[i] = unsquash(obj[i]);
		return Arrays.toString(obj);
	}
	
	public static void test32() {
		
		
		
		List<StringBuffer> grid = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		Transformation op = new LinearSelection(grid, 49, 28);
		op.execute(true);
		Transformation op2 = new FlipVertical(op.getOutput());
		op2.execute(true);
		
		op2.setOutput(((Selector)op).replaceSelection(op2.getOutput()));
		System.out.println(op2.getOutput());
		
		
		Transformation op3 = new PeriodRow(op2.getOutput(), 2);
		op3.execute(true);
		
	}
	
	public static void testGenome() {
		boolean DUMP = false;
		if (DUMP) CIPHER = CIPHER_FOR_POSITIONS;
		boolean random = false;
		double[] genome = new double[OPERATION_WIDTH * 50];
		for (int i=0; i<genome.length; i++) {
			genome[i] = rand.nextFloat();
		}
		
		if (!random) 
		genome = new double[] {
				0.47874096, 0.12235918, 0.03209252, 0.5661193, 0.0072713867, 0.107416116, 0.35166892, 0.08694711, 0.64814436, 0.12682848, 0.9820313, 0.39466387, 0.016405243, 0.3986115, 0.5971803, 0.8455708, 0.052320924, 0.82988966, 0.6506404, 0.3273728, 0.5753867, 0.2633028, 0.8242662, 0.6244511, 0.4514873, 0.6745273, 0.19924818, 0.9810895, 0.767329, 0.039401215, 0.79849386, 0.21747856, 0.9536119, 0.6997341, 0.22272162, 0.16348054, 0.9496114, 0.3578275, 0.9832239, 0.18886875, 0.8642462, 0.08739374, 0.82921344, 0.39460447, 0.45732608, 1.1545788E-5, 0.5238213, 0.4551416, 0.5124661, 0.87420374, 0.68196905, 0.8672664, 0.25884235, 0.7123256, 0.24453442, 0.54482764, 0.15030332, 0.039049115, 0.8187598, 0.22534338, 0.7140485, 0.8964482, 0.11658578, 0.20554753, 0.60597223, 0.99646246, 0.43536115, 0.23645744, 0.8094354, 0.21588854, 0.77181387, 0.4351523, 0.6735067, 0.8108421, 0.10061802, 0.3663887, 0.37592345, 0.17694545, 0.9998337, 0.5636072, 0.13499644, 0.27348247, 0.61192214, 0.9067244, 0.09738418, 0.80488366, 0.75568366, 0.3631457, 0.41554832, 0.12939388, 0.6602378, 0.77554363, 0.58128864, 0.9450882, 0.041812353, 0.97810656, 0.69109595, 0.54118115, 0.6886826, 0.7137135, 0.7595036, 0.59729904, 0.23226851, 0.6137862, 0.51430315, 0.61068153, 0.8876745, 0.23090428, 0.9902211, 0.6136471, 0.515227, 0.45393252, 0.87058294, 0.23921669, 0.7071878, 0.15641178, 0.21277978, 0.5898363, 0.41343403, 0.83049315, 0.48428822, 0.52240795, 0.43476543, 3.934933E-4, 0.33000845, 0.9346874, 0.42745054, 0.6039867, 0.47093308, 0.7897008, 0.30677673, 0.677847, 0.005243762, 0.3878787, 0.076881334, 0.07857328, 0.6169177, 0.12152743, 0.124225736, 0.4583939, 0.24941702, 0.5213389, 0.7558939, 0.18614759, 0.643812, 0.85423315, 0.36526686, 0.24027951, 0.07019536, 0.8420504, 0.5722927, 0.97143346, 0.40494716, 0.42930454, 0.82855666, 0.2783191, 0.6003029, 0.91692054, 0.9622098, 0.10705856, 0.20076762, 0.10917558, 0.3282895, 0.41112524, 0.028185632, 0.16644154, 0.7262694, 0.49119064, 0.42034835, 0.14478199, 0.9541873, 0.56633866, 0.5009138, 0.49258372, 0.6786411, 0.2803596, 0.4895252, 0.3040724, 0.15280177, 0.43558887, 0.22058737, 0.73760986, 0.97198904, 0.31892505, 0.25560766, 0.90896285, 0.023906682, 0.9701236, 0.99370366, 0.19867714, 0.16674775, 0.0514592, 0.34927765, 0.423774, 0.83835864, 0.69708693, 0.36382663, 0.43644387, 0.44252735, 0.46572644, 0.6697482, 0.27532855, 0.8298781, 0.7800826, 0.5112873, 0.22067356, 0.26708683, 0.6101119, 0.36335534, 0.36133498, 0.54785675, 0.8767252, 0.78898257, 0.24692042, 0.26725745, 0.36857617, 0.24399662, 0.059886537, 0.6897359, 0.068106115, 0.051979464, 0.3644441, 0.56077045, 0.3485359, 0.24235906, 0.7532234, 0.2555128, 0.040537808, 0.08777406, 0.668687, 0.9548691, 0.028678771, 0.18385708, 0.4091896, 0.77696717, 0.39746985, 0.21826528, 0.29700828, 0.20205685, 7.3864165E-4, 0.6097962, 0.63800764, 0.20658666, 0.4805354, 0.08395722, 0.3760483, 0.9743332, 0.24268936, 0.047383126, 0.9723983, 0.64798576, 0.63444877, 0.92129034, 0.53551644, 0.9635621, 0.27130258, 0.2254153, 0.5840389, 0.7026647, 0.042394657, 0.8528077, 8.629975E-4, 0.6838089, 0.65127647, 0.78636044, 0.33256587, 0.1870768, 0.60422486, 0.69425094, 0.21521844, 0.27229407, 0.15015817, 0.19692102, 0.7055014, 0.13995571, 0.49002635, 0.21000323, 0.88666767, 0.4101955, 0.59025353, 0.71135676, 0.21337633, 0.35726205, 0.51302767, 0.5746145, 0.69671386, 0.20042574, 0.28567103, 0.9752878, 0.15617795, 0.1526389, 0.6902098, 0.20417206, 0.7509002, 0.22968169, 0.50074905, 0.3372303, 0.0546049, 0.8105006, 0.9266348
				//0,0,0,0,0,0
		};
		
		System.out.println(Arrays.toString(genome));
		Operations o = new Operations(genome);
		o.performTransformations(true, 50);
		System.out.println("Actual operations: " + o.operationsActual);
		String cipher = o.outputString;
		System.out.println("Measurements: " + Arrays.toString(o.measure(cipher)));
		
		if (DUMP) {
			int pos = 0;
			for (StringBuffer sb : o.output) {
				for (int i=0; i<sb.length(); i++) {
					System.out.println("map.put(" + pos + ", " + ((int)sb.charAt(i)) + ");");
					pos++;
				}
			}
		}
	}
	
	public static void test44bigrams() {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(); 
		map.put(0, 338);
		map.put(1, 319);
		map.put(2, 300);
		map.put(3, 281);
		map.put(4, 262);
		map.put(5, 243);
		map.put(6, 224);
		map.put(7, 205);
		map.put(8, 203);
		map.put(9, 184);
		map.put(10, 165);
		map.put(11, 146);
		map.put(12, 127);
		map.put(13, 108);
		map.put(14, 89);
		map.put(15, 70);
		map.put(16, 51);
		map.put(17, 32);
		map.put(18, 13);
		map.put(19, 336);
		map.put(20, 317);
		map.put(21, 298);
		map.put(22, 279);
		map.put(23, 260);
		map.put(24, 241);
		map.put(25, 222);
		map.put(26, 220);
		map.put(27, 201);
		map.put(28, 182);
		map.put(29, 163);
		map.put(30, 144);
		map.put(31, 125);
		map.put(32, 106);
		map.put(33, 87);
		map.put(34, 68);
		map.put(35, 49);
		map.put(36, 30);
		map.put(37, 11);
		map.put(38, 334);
		map.put(39, 315);
		map.put(40, 296);
		map.put(41, 277);
		map.put(42, 258);
		map.put(43, 239);
		map.put(44, 237);
		map.put(45, 218);
		map.put(46, 199);
		map.put(47, 180);
		map.put(48, 161);
		map.put(49, 142);
		map.put(50, 123);
		map.put(51, 104);
		map.put(52, 85);
		map.put(53, 66);
		map.put(54, 47);
		map.put(55, 28);
		map.put(56, 9);
		map.put(57, 332);
		map.put(58, 313);
		map.put(59, 294);
		map.put(60, 275);
		map.put(61, 256);
		map.put(62, 254);
		map.put(63, 235);
		map.put(64, 216);
		map.put(65, 197);
		map.put(66, 178);
		map.put(67, 159);
		map.put(68, 140);
		map.put(69, 121);
		map.put(70, 102);
		map.put(71, 83);
		map.put(72, 64);
		map.put(73, 45);
		map.put(74, 26);
		map.put(75, 7);
		map.put(76, 330);
		map.put(77, 311);
		map.put(78, 292);
		map.put(79, 273);
		map.put(80, 271);
		map.put(81, 252);
		map.put(82, 233);
		map.put(83, 214);
		map.put(84, 195);
		map.put(85, 176);
		map.put(86, 157);
		map.put(87, 138);
		map.put(88, 119);
		map.put(89, 100);
		map.put(90, 81);
		map.put(91, 62);
		map.put(92, 43);
		map.put(93, 24);
		map.put(94, 5);
		map.put(95, 328);
		map.put(96, 309);
		map.put(97, 290);
		map.put(98, 288);
		map.put(99, 269);
		map.put(100, 250);
		map.put(101, 231);
		map.put(102, 212);
		map.put(103, 193);
		map.put(104, 174);
		map.put(105, 155);
		map.put(106, 136);
		map.put(107, 117);
		map.put(108, 98);
		map.put(109, 79);
		map.put(110, 60);
		map.put(111, 41);
		map.put(112, 22);
		map.put(113, 3);
		map.put(114, 326);
		map.put(115, 307);
		map.put(116, 305);
		map.put(117, 286);
		map.put(118, 267);
		map.put(119, 248);
		map.put(120, 229);
		map.put(121, 210);
		map.put(122, 191);
		map.put(123, 172);
		map.put(124, 153);
		map.put(125, 134);
		map.put(126, 115);
		map.put(127, 96);
		map.put(128, 77);
		map.put(129, 58);
		map.put(130, 39);
		map.put(131, 20);
		map.put(132, 1);
		map.put(133, 324);
		map.put(134, 322);
		map.put(135, 303);
		map.put(136, 284);
		map.put(137, 265);
		map.put(138, 246);
		map.put(139, 227);
		map.put(140, 208);
		map.put(141, 189);
		map.put(142, 170);
		map.put(143, 151);
		map.put(144, 132);
		map.put(145, 113);
		map.put(146, 94);
		map.put(147, 75);
		map.put(148, 56);
		map.put(149, 37);
		map.put(150, 18);
		map.put(151, 16);
		map.put(152, 339);
		map.put(153, 320);
		map.put(154, 301);
		map.put(155, 282);
		map.put(156, 263);
		map.put(157, 244);
		map.put(158, 225);
		map.put(159, 206);
		map.put(160, 187);
		map.put(161, 168);
		map.put(162, 149);
		map.put(163, 130);
		map.put(164, 111);
		map.put(165, 92);
		map.put(166, 73);
		map.put(167, 54);
		map.put(168, 35);
		map.put(169, 33);
		map.put(170, 14);
		map.put(171, 337);
		map.put(172, 318);
		map.put(173, 299);
		map.put(174, 280);
		map.put(175, 261);
		map.put(176, 242);
		map.put(177, 223);
		map.put(178, 204);
		map.put(179, 185);
		map.put(180, 166);
		map.put(181, 147);
		map.put(182, 128);
		map.put(183, 109);
		map.put(184, 90);
		map.put(185, 71);
		map.put(186, 52);
		map.put(187, 50);
		map.put(188, 31);
		map.put(189, 12);
		map.put(190, 335);
		map.put(191, 316);
		map.put(192, 297);
		map.put(193, 278);
		map.put(194, 259);
		map.put(195, 240);
		map.put(196, 221);
		map.put(197, 202);
		map.put(198, 183);
		map.put(199, 164);
		map.put(200, 145);
		map.put(201, 126);
		map.put(202, 107);
		map.put(203, 88);
		map.put(204, 69);
		map.put(205, 67);
		map.put(206, 48);
		map.put(207, 29);
		map.put(208, 10);
		map.put(209, 333);
		map.put(210, 314);
		map.put(211, 295);
		map.put(212, 276);
		map.put(213, 257);
		map.put(214, 238);
		map.put(215, 219);
		map.put(216, 200);
		map.put(217, 181);
		map.put(218, 162);
		map.put(219, 143);
		map.put(220, 124);
		map.put(221, 105);
		map.put(222, 86);
		map.put(223, 84);
		map.put(224, 65);
		map.put(225, 46);
		map.put(226, 27);
		map.put(227, 8);
		map.put(228, 331);
		map.put(229, 312);
		map.put(230, 293);
		map.put(231, 274);
		map.put(232, 255);
		map.put(233, 236);
		map.put(234, 217);
		map.put(235, 198);
		map.put(236, 179);
		map.put(237, 160);
		map.put(238, 141);
		map.put(239, 122);
		map.put(240, 103);
		map.put(241, 101);
		map.put(242, 82);
		map.put(243, 63);
		map.put(244, 44);
		map.put(245, 25);
		map.put(246, 6);
		map.put(247, 329);
		map.put(248, 310);
		map.put(249, 291);
		map.put(250, 272);
		map.put(251, 253);
		map.put(252, 234);
		map.put(253, 215);
		map.put(254, 196);
		map.put(255, 177);
		map.put(256, 158);
		map.put(257, 139);
		map.put(258, 120);
		map.put(259, 118);
		map.put(260, 99);
		map.put(261, 80);
		map.put(262, 61);
		map.put(263, 42);
		map.put(264, 23);
		map.put(265, 4);
		map.put(266, 327);
		map.put(267, 308);
		map.put(268, 289);
		map.put(269, 270);
		map.put(270, 251);
		map.put(271, 232);
		map.put(272, 213);
		map.put(273, 194);
		map.put(274, 175);
		map.put(275, 156);
		map.put(276, 137);
		map.put(277, 135);
		map.put(278, 116);
		map.put(279, 97);
		map.put(280, 78);
		map.put(281, 59);
		map.put(282, 40);
		map.put(283, 21);
		map.put(284, 2);
		map.put(285, 325);
		map.put(286, 306);
		map.put(287, 287);
		map.put(288, 268);
		map.put(289, 249);
		map.put(290, 230);
		map.put(291, 211);
		map.put(292, 192);
		map.put(293, 173);
		map.put(294, 154);
		map.put(295, 152);
		map.put(296, 133);
		map.put(297, 114);
		map.put(298, 95);
		map.put(299, 76);
		map.put(300, 57);
		map.put(301, 38);
		map.put(302, 19);
		map.put(303, 0);
		map.put(304, 323);
		map.put(305, 304);
		map.put(306, 285);
		map.put(307, 266);
		map.put(308, 247);
		map.put(309, 228);
		map.put(310, 209);
		map.put(311, 190);
		map.put(312, 171);
		map.put(313, 169);
		map.put(314, 150);
		map.put(315, 131);
		map.put(316, 112);
		map.put(317, 93);
		map.put(318, 74);
		map.put(319, 55);
		map.put(320, 36);
		map.put(321, 17);
		map.put(322, 321);
		map.put(323, 302);
		map.put(324, 283);
		map.put(325, 264);
		map.put(326, 245);
		map.put(327, 226);
		map.put(328, 207);
		map.put(329, 188);
		map.put(330, 186);
		map.put(331, 167);
		map.put(332, 148);
		map.put(333, 129);
		map.put(334, 110);
		map.put(335, 91);
		map.put(336, 72);
		map.put(337, 53);
		map.put(338, 34);
		map.put(339, 15);
		String re = ";*H+(:N6FM<B+;FMSfT|OOCB1GR&bp2#qR_H*18_c13B+;.B+Zf+pK#.kZtz+c+tc4D^b2#k+)WVS<Wc25VF5EVM(D&OW%lpd+LqU.||5ldL>P3Z#>HFp|f>4+RR-L.k5*+BEMc(W^C5ycU4+Gddlcpd+-TFO4^92yAYc-t7y)GKBS<K*FzKFc_2l+pJKLAYP6zXlBT7FO%^92z<|OBWL.y8+pORjK8/RLYPz7CT|+(MYJ+J5(4+(D^kW)R-4BT|++G|2+pGOpNk+GZc5*tU<9zFFVU(RD|)MVbNJ/zK@VC|8M+H>)B.E2#l+-6zX*j^:NC/lp9fS<OByNUOzpB2";
		int n = 3;
		NGramsBean bean = new NGramsBean(n, re);
		//bean.dump();
		for (String key : bean.positions.keySet()) {
			if (bean.counts.get(key) > 1) {
				String darken = "";
				String lighten = "";
				for (Integer pos : bean.positions.get(key)) {
					for (int i=pos; i<pos+n; i++) {
						int pos2 = map.get(i);
						int r = pos2/17;
						int c = pos2 % 17;
						darken += "darkenrc(" + r + "," + c + "); ";
						lighten += "lightenrc(" + r + "," + c + "); ";
					}
				}
				System.out.println(bean.counts.get(key) + " <button onmouseover=\"" + darken + "\" onmouseout=\"" + lighten + "\">" + key + "</button>");
			}
		}

	}
	
	public void testMeasure() {
		for (int i=0; i<2; i++) {
			String cipher = Ciphers.cipher[i].cipher;
			System.out.println(Arrays.toString(measure(cipher)) + ": " + Ciphers.cipher[i].description);
		}
		String cipher = "K)IM3$Um8lnJ#X5-*-MTCgN(96AcPgna$HaRY6cWb0*dN4LCAORc4_RejkHFDE3#KpnAPIH!M(TUS7*50*mHaMbQd*RK4iXHVAL$AGRQXCSa#k-&LK02aiIW1TK7!QJpUmMbH9nH!T*_(OfRH@7lQbHBkSC4K67HSR-gQ3Rc561-ZN(RDd9A%X_VSUhJaRg7&XW4Mn5hRT_m*3D$IHgo!X54b*8T6cpYEHml%Cabk*I@0U9oA#$4G0QX$SW_9E3A(FUCjYnhHdKS0H4m6*CBn-IQMkg-A6(*nH!aLm4GX5JU01CPDZMb4AXH8k*n%d_Vb*F_)mX9#$B6*&%D$TF)";
		System.out.println(Arrays.toString(measure(cipher)) + ": chris cactus cipher");
		cipher = "++t4Vc.b425f^>REHR8;(cBF5|N+#yB+pNFB&+.M4T5*|JRc:yBOKMTbpBYD|Et57ppS-yBF<7pO+J^+V+M9_K46AycBF2RZ+b^R8p9L@+zYN_+O#jfK+5#|Lz.VGXcU2;%qJ2G((D2>FkCd*-OlF+;K|A8OZzSkpN+M<d>MDH/k4&+PF5|djtzcC-*BOY_Bt7<WUlz-|Fkd2KR++Op3V*8l^p)(/THSOPcWzC/R+U++)WJHz#L)(WGZU+ML)|BWlF+<C61Llc<2RcT+)fK*<.YWD%O#(2GqMf.^pO(KBzS96z|c.3d2GTL1|kPV^lp+-5ZUV>EC94:*NFGlyBX1";
		System.out.println(Arrays.toString(measure(cipher)) + ": 40 bigrams from recent quadrant experiment");
		cipher = "2<cl>MDHU+R/|Fkd-zlU++)Wd<M+RcT+(G2J|c.3#5+KyBX1p8R^lGFN_9M+z69SSpp7By:cNp+BHER>RJ|*5T4M.+&BFNpkSzZO8A|K;+5tE|DYBpbTMKOW<7tB_YOB*-CcV+^J+Op7<FBy-CzWcPOSHT/()pb+ZR2FBcyA64KL16C<+FlWB|)Lfj#O+_NYz+@L9zBK(Op^.fMqG2q%;2UcXGV.zL|*:49CE>VUZ5-+FlO-*dCkF>2D(^f524b.cV4t++ztjd|5FP+&4k/y#+N|5FBc(;8R^l8*V3pO++RK2M+UZGW()L#zHJ(#O%DWY.<*Kf)pl^VPk|1LTG2d";
		System.out.println(Arrays.toString(measure(cipher)) + ": 5 trigrams from recent quadrant experiment");
		cipher = "3:aON;653<!%C>.\\7#8Z<L6O\\Y?U:AO#7Y=(UB8,^aP+7/a>S#IOY5W;AOGYQ2C_Q3OF[+7PGU]N74()1\\6'\\*@M153\\86O&HRIL;3]4X56NE+*M187!Y<N[7\"\\;YMK\\3\\]6<&HR#Y.+X(>Y#]N\"(N7O3_9O'#P(M12Y+Y^>Q77>KCa4K8$3/,=(UB/,$;I]S-CO0;ND+YEQX;-BQ5\"<;Y(50>+.^18%<SO<Y\"Y3\\<,7<6*]V,/YL]B8#CF[,7_\\M:Q7`+I1DZ:\"7KO1>P5]Y<`[3O8,F*W5'J5\\)`'VZ<X*G05.BP<!(T8*\\^-+XC18%OVU\\,MG:DF(NI/V.Oa6";
		System.out.println(Arrays.toString(measure(cipher)) + ": gardibolt");
		cipher = "XN*T-d>+dy+4C./1tFJH3(^pTCF+FB_+DL>+dRO5OBc^@.p5#L|4+pW)S_2+qtD+^<#TY5c|N.DXLEb-R5;cO<k5&Rl^yWRG2f)OOzV+2l)p9*_f.HFl<+kF+.%98*Ap5YZW+dfp76MMjBBlFO-U(BZL%BUR26LF5J8*S1R:N/kl2-q<SYbAp>)zKWVc<ky+7+Jy#U1zk+lOz#9:pZ+4+V;MlGLK#+GRJUB7M(D|z&FzT|+.KZ42)2V^CG+8(pY6MMUz39(fyM*2+HB+(2p|Cc+FBOztOK4+/VbE|RF|(zTp|8<P^W>KG*B-OCKSj|NcdBK|EGtc4PBPcNHcVc;W";
		System.out.println(Arrays.toString(measure(cipher)) + ": best random shuffle from fragments experiment");
		cipher = "I4X91iZg;`GJ=b=7[2j2fKG8OPEUH6B:dXFXLfXmaJJ0QdWATXPkKZUHkl@ZHnCK4[TJA0Qnnd[gnOSK6CM\7[U0VL32G415Y4Y9[7KT[J=?dM9;`O5=kM8bK4_CfDT=m5G5IkY4VDFcWZB`TEFcXb67fKTJ9IQ25iVJ0?diZQ]hDLQ;A^ToMkK9[]XQgHX4THmJ]D1a4_K8J;10V_3Ua45onZG=5d^IVgW5iAhBACdWL;LD]GZQAa42GcPY[URpH:;AF[72dWBD2?D765i8KAO^K1cECdM6JeH?nI4JPD1jLbO0V^KN^kWeU0XVB:gdK0VLDgR=?fK92jN7kYAU";
		System.out.println(Arrays.toString(measure(cipher)) + ": mike cole");
		cipher = "kpP+tVH7Vz(;btYtWMJGLX.UtN%:JM/6Z3)Y!b5#TFB!_tVH#K%S8fCd7Et98>AR1QY2fQN1lLdEDW63X5Tp.GJlP)-Ctf1zdUOV_/4JZ(K;)Y+pl5TB/EXtKLESHV6!B!PptY%^3M(V&zk%RbF_H>CX93lW#LN+t<RLD:dCq.;bz1AH#GcU57f_DpR4HD-MZ/W6V!QN3_lGcZ;7)XtJM&EX%:U-<RdQYP+19S5C:tdGftJ7Mb;)NY(WE1Y3Z9YF8(./K&Tp)1S>Bdl+ttRttG;CK;#V)%:JZ(M88k;NY5W:FEZFCX(1q+PtUj#-V>XZJM^Ftt+3HMXFASXpK^+1";
		System.out.println(Arrays.toString(measure(cipher)) + ": mike eaton");
		cipher = "jfHQCHbc[GS`DlnMz[HKgaPH:VIbA2bG@Tz[HReWQU>H5k6GzY8QCH0HRi[BechjH^LmH9nS;]aHl=3KZHRDMHIJA1<:E@f4U]Y8F2g0cNmSVaXdbT<BOWQI;J>5i^HHA46kjGzh[9RLCH@=nlKGQM`5W1H3]8ifH0d^RkJ:Q2HHjmDW`UJlL0H^g`@4i6XeBGI5=Q:[fL3HKN;]YG^bMd=Q17?Ie8VQTmQ]Hcb[a9hSJSzb1i>Q1N?CmQ:E3<2GHR5@[F9XRz:7dcVWdIUe84RBNd<RkD]0e8<RKE09NOQl^[O=;4MnSTd1W7fzHChm9?R3jHSU1naA_3gBQBV0";
		System.out.println(Arrays.toString(measure(cipher)) + ": tony baloney 1");
		cipher = ",CurN[?dISMT^yhvosdWSPFYwptc+gbfdBzOR`yA+0ZIG-VYa6+_C-UroLZD60Zfu4Jbsdq4{pikGFBJtTc:zS+:OS]mNjxlIJJWgu9vP[C,4Yy7UXr`hJsFclXfN@wtVRKTa8-NB_oDxZrZsUdliqXj4dIWn@JSdl9p,XT`kTU-UlFa[-zX@JhkYq+b@26gGRdfOyDpcA+Vi20ZBu,dE_oZbT^vjIGtT`C2ULZn*YW_PzufSU9rUo[sbZFTUWhw6BGRI_taD-qVrNisp+nmoWxFVjVtu9T`OE_,1n^oW[gYJrTy{CsHhaL]?cR_o-V+ZlDibTUdXjq-SgFZfzZl";
		System.out.println(Arrays.toString(measure(cipher)) + ": tony baloney 2");
		
		cipher = Ciphers.cipher[0].cipher;
		cipher = Periods.rewrite3(cipher, 19);
		System.out.println(Arrays.toString(measure(cipher)) + ": z340 period 19 scheme");
		cipher = Ciphers.cipher[5].cipher;
		cipher = Periods.rewrite3(cipher, 15);
		System.out.println(Arrays.toString(measure(cipher)) + ": z340 flipped, period 15 scheme");
		cipher = "L1|kPV^lp>REHTG2dSpp7^l8*V3pO++RK2#5+Kq%;2UcXGV.zL|yBpB5/R+U-yBF<YOF<6G2zlOFRKB8M&;T+|c.3zBK(Op^.fMqG2|FkdW<7tB_YOB*-CcNp+B(#O%DWY.<*Kf)_9M+ztjd|5FP+&4k/(G2Jfj#O+_NYb+M<dD+2|JRE^Zt++RyBF5(b.c^f524b.cV4t++RcT+L16C<+FlWB|)L>MDHNpkSzZO8A|K;+By:cM+UZGW()L#zHJp8R^FlO-*dCkF>2D(9L@+zp7cVUlz-K46A|N+#lSNM4T5*|Jc9FyBX1*:49CE>VUZ5-+++)WCzWcPOSHT/()p";
		System.out.println(Arrays.toString(measure(cipher)) + ": experiment");
	}

	@Override
	public void expressGenome() {
		performTransformations(false, MAX_OPERATIONS);
	}

	@Override
	public void reset(EvolutionState state, int thread) {
		//resetRandom(state, thread);
		//resetZeroes(state, thread);
		resetFromGenomes(state, thread);
	}
	public void resetRandom(EvolutionState state, int thread) {
		super.reset(state, thread);
	}
	public void resetZeroes(EvolutionState state, int thread) {
		for (int i=0; i<genome.length; i++) genome[i] = 0;
	}
	public void resetFromGenomes(EvolutionState state, int thread) {
		
		// PeriodColumn(2) Period(18)
		/*double[] genomeForInit = new double[] {
				0.010807433, 0.042211026, 0.0560333, 0.093898386, 0.048398037, 0.14299238, 0.065441735, 0.04418141, 0.17488529, 0.20591563, 0.2463839, 0.04601291, 0.07634401, 0.1609952, 0.19894734, 0.009363861, 0.028838199, 0.17214006, 0.009109178, 0.03442892, 0.041893076, 0.103039764, 0.14005236, 0.05459777, 0.0029927327, 0.014167723, 0.18149742, 0.11114735, 0.020461434, 0.0014473503, 0.094744034, 0.026969332, 0.2390603, 0.22190134, 0.018178787, 0.13323072, 5.4164254E-4, 0.005782895, 0.11310247, 0.08675262, 0.022415632, 0.012166226, 0.042259376, 0.2008684, 0.07846326, 0.20013842, 0.007476524, 0.07807765, 0.0030879704, 0.16781771, 0.09670423, 0.09869219, 0.04470184, 0.018535431, 0.08813473, 0.17895278, 0.18237494, 0.14339617, 0.3557988, 0.050052464, 0.441845, 0.10557511, 0.008076913, 0.04866287, 0.11814425, 0.15650478, 0.09921388, 0.37201747, 0.10818868, 0.0055461545, 0.03395196, 0.121369734, 0.08914525, 0.105275676, 6.691002E-4, 0.24303819, 0.06235991, 0.08262558, 0.0041570757, 0.08466629, 0.11258204, 0.2382696, 0.17631443, 0.325505, 0.016762754, 0.03811475, 0.07593728, 0.022782378, 0.14058319, 0.031369515, 0.044241007, 0.19201069, 0.17512648, 0.04679513, 0.1441861, 0.055278394, 0.09330151, 0.018611997, 0.119458504, 0.18319121, 0.089408115, 0.131702, 0.0011852663, 0.21224488, 0.044009157, 0.0014968495, 0.27773398, 0.1674259, 0.021935085, 0.018056253, 0.16015425, 0.034592614, 0.27897978, 0.008575023, 0.03230859, 0.005842355, 0.008986839, 0.19665235, 0.13338207, 0.0012395122, 0.052069753, 0.050394487, 0.14552091, 0.15886828, 0.0012853071, 0.051696215, 0.037728645, 0.23292695, 0.19415598, 0.10799726, 0.18427058, 0.04429276, 0.09382076, 0.020933568, 0.0032065932, 0.001690412, 0.18837066, 0.20913781, 0.019054117, 0.07912983, 0.066447824, 0.059149437, 0.2324139, 7.576658E-4, 7.792519E-4, 0.04874466, 0.018615581, 0.1640632, 0.0688976, 0.05544251, 0.049418855, 0.08193168, 0.13512297, 0.035966482, 0.035067394, 0.22093374, 0.03804271, 0.11270317, 0.15775059, 0.18099761, 0.009957127, 0.13439615, 0.094031826, 0.047455765, 0.02778476, 0.085581906, 0.14501305, 0.28209907, 0.0032257666, 0.06504699, 0.033377524, 0.062760815, 0.0024682595, 0.060075954, 0.0843335, 0.033148866, 0.0028017624, 0.35820785, 0.0048857974, 0.3047574, 0.37248078, 0.1019722, 0.23096181, 0.4150797, 0.3363548, 1.1197036E-4, 0.07722715, 0.052026715, 0.026153909, 7.382837E-4, 0.0027326925, 0.10908653, 0.001419281, 0.002466773, 0.014296411, 0.23755062, 0.34367016, 0.1421671, 0.0027375917, 0.1352734, 0.021816138, 0.22957353, 0.15922666, 0.13898164, 0.01553672, 0.02355541, 0.27612796, 0.049706977, 0.049949862, 0.04472141, 0.010794036, 0.07099141, 0.44994769, 0.03000836, 0.24227749, 0.09394667, 0.003601856, 0.123801515, 0.02449589, 0.0022883373, 0.086868815, 0.155291, 0.0059220134, 0.015335088, 0.15157838, 0.124735706, 0.023152003, 0.00158531, 0.012697412, 0.23554188, 0.021418413, 0.33788007, 0.07633165, 0.08641674, 0.013473599, 0.048253886, 0.008137094, 0.2518277, 0.006328272, 0.064309835, 0.011894419, 0.0032906032, 0.16813353, 0.106020525, 0.008569091, 0.05684082, 0.055985212, 0.015187328, 0.10678113, 0.022518609, 0.045545172, 0.14651842, 0.027797269, 0.02386206, 4.5443003E-4, 0.008786525, 0.038966544, 0.017821413, 0.08746706, 0.06762673, 0.24899022, 0.008726602, 0.0058996193, 0.0052941795, 0.049733922, 0.18638477, 0.14236256, 0.007154533, 0.115687236, 0.016112586, 0.0036296914, 0.18859695, 0.12573269, 0.11653733, 0.09510217, 0.18674262, 0.008672324, 0.11102906, 0.14617369, 0.02718099, 0.0015167604, 0.029707229, 0.007004447, 0.035890277, 0.006168309, 0.09511176, 0.0053577805, 0.07647613, 0.063312024, 0.016905082, 0.0745854, 0.35929435, 0.012061044, 0.16888405, 0.010706345, 0.010201759, 0.08020035, 0.019362433, 0.037575368, 0.2540488
		};
		
		for (int i=0; i<genome.length; i++) genome[i] = (float) genomeForInit[i];
		
		*/
		//for (int i=0; i<genome.length; i++) genome[i] = 0;
		genome = Genomes.toFloat(Genomes.genomes3[initIndex++]);
		initIndex %= Genomes.genomes3.length; // wrap around 
		//super.reset(state, thread);
	}

	public double compositeFitnessAsDouble() {
		return compositeFitnessAsDouble(this.fitness);
	}
	public static double compositeFitnessAsDouble(Fitness fitness) {
		MultiObjectiveFitness fit = (MultiObjectiveFitness) fitness;
		return compositeFitnessAsDouble(fit.getObjectives());
		
	}
	public static double compositeFitnessAsDouble(double[] objectives) {
		if (objectives == null) return 0;
		double result = 1d;
		
		// 
		
		for (double d : objectives) {
			if (d > 0)
				result *= d;
			else if (d < 0) 
				result /= (0-d);

			// if f is zero, do nothing
				
		}
		return result;
	}
	
	/** decode the given cipher.  key is derived from the genome starting at the given position. */
	public void decode(int genomeStartPosition) {
		float[] genome = this.genome;
		
		/** map symbols to plaintext */
		Map<Character, Character> decoder = new HashMap<Character, Character>();
		
		int pos = 0;
		while (decoder.size() < Operations.CIPHER_ALPHABET.length()) {
			float val = genome[genomeStartPosition++];
			char symbol = Operations.CIPHER_ALPHABET.charAt(pos++);
			char plain = (char) CandidateKey.toInt(val, 65, 90);
			decoder.put(symbol, plain);
		}
		plaintext = "";
		for (int i=0; i<outputString.length(); i++) {
			if (outputString.charAt(i) == ' ') continue;
			plaintext += decoder.get(outputString.charAt(i));
		}
	}
	
	public static void main(String[] args) {
		//testGenome();
		test32();
		
		/*for (int x=0; x<100; x++) {
			System.out.println(squash(x) + ", " + unsquash(squash(x)));
		}*/
		
		
		//test44bigrams();
		//Operations o = new Operations();
		//o.testMeasure();
	}

}
