package com.zodiackillerciphers.corpus.symposium2019;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.ciphers.algorithms.ColumnarTransposition;
import com.zodiackillerciphers.ciphers.algorithms.RailFence;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.ciphers.algorithms.Vigenere;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.stats.BionStats;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.tests.kasiski.Kasiski;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;
import com.zodiackillerciphers.transform.operations.Diagonal;
import com.zodiackillerciphers.transform.operations.Permutation;
import com.zodiackillerciphers.transform.operations.Quadrants;
import com.zodiackillerciphers.transform.operations.Snake;
import com.zodiackillerciphers.transform.operations.Spiral;

public class CipherGenerator {
	public static int[] WHICH_CIPHERS = new int[] {27};
	public static int ALPHABET_SIZE = 63; // match Z340's alphabet size
	static boolean INIT_NGRAPH_MAPS = false;
	public static int WHICH_STATS = 0; // 0: original (shotgun), 1: untransposed route digraphic iocs; 2: 0 and 1 combined; 3: columnar stats 
	static Map<String, Double> mapC2;
	static Map<String, Double> mapC3;
	static Map<String, Double> mapC4;
	static Map<String, Double> mapC5;
	static Map<String, Double> mapH2;
	static Map<String, Double> mapH3;
	static Map<String, Double> mapH4;
	static Map<String, Double> mapH5;
	static {
		if (INIT_NGRAPH_MAPS) {
			mapC2 = NGraphStats.ngraphsMapFor(
					"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/2graph-stats-columnar.txt");
			mapC3 = NGraphStats.ngraphsMapFor(
					"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/3graph-stats-columnar.txt");
			mapC4 = NGraphStats.ngraphsMapFor(
					"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/4graph-stats-columnar.txt");
			mapC5 = NGraphStats.ngraphsMapFor(
					"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/5graph-stats-columnar.txt");
			mapH2 = NGraphStats.ngraphsMapFor(
					"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/2graph-stats-homophonic.txt");
			mapH3 = NGraphStats.ngraphsMapFor(
					"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/3graph-stats-homophonic.txt");
			mapH4 = NGraphStats.ngraphsMapFor(
					"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/4graph-stats-homophonic.txt");
			mapH5 = NGraphStats.ngraphsMapFor(
					"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/5graph-stats-homophonic.txt");
		}
	}
	
	/** generate ciphers from candidate plaintexts */
	public static void generateFrom(String path, int offset, int max, boolean withStats) {
		BufferedReader input = null;
		int counter = 0;
		int hits = 0;
		try {
			String tab = "	";
			input = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

			Random rand = new Random();

			String line = null; // not declared within while loop
			String hom = null;
//			double[] doubles = null;
//			String suffix = null;
			int cipherTypeIndex = 0;
			int which = WHICH_CIPHERS[cipherTypeIndex];
			while ((line = input.readLine()) != null) {
				if (counter >= offset) {
					String[] split = line.split(tab);
					String pt = split[4];
					which = WHICH_CIPHERS[cipherTypeIndex];
					if (which == 0) {
						// homophonic:
						hom = HomophonicGenerator.makeHomophonic(pt, ALPHABET_SIZE);
//						hom = Ciphers.fromNumeric(Ciphers.toNumeric(hom, false), false, 5);
						// doubles = HomophonicGenerator.toDoubles(hom);
					} else if (which == 1) {
						// columnar transposition:
						int[] key = ColumnarTransposition.randomKey(20);
						StringBuilder col = ColumnarTransposition.encode(new StringBuilder(pt), key);
						hom = HomophonicGenerator.makeHomophonic(col.toString(), ALPHABET_SIZE);
//						hom = Ciphers.fromNumeric(Ciphers.toNumeric(hom, false), false, 5);
						// doubles = HomophonicGenerator.toDoubles(hom);
					} else if (which == 2) {
						// spiral
						boolean reverse = rand.nextBoolean();
						String spiral = Spiral.transform(pt, 17, reverse); 
						hom = HomophonicGenerator.makeHomophonic(spiral, ALPHABET_SIZE);
					} else if (which == 3) {
						// flipped horizontally
						String flipped = CipherTransformations.flipHorizontal(pt, 20, 17);
						hom = HomophonicGenerator.makeHomophonic(flipped, ALPHABET_SIZE);
					} else if (which == 4) {
						// flipped vertically
						String flipped = CipherTransformations.flipHorizontal(pt, 20, 17);
						hom = HomophonicGenerator.makeHomophonic(flipped, ALPHABET_SIZE);
					} else if (which == 5) {
						// diagonal transpositions.
						// works in upper left corner but can flip/mirror to orient for other diagonals.
						// UL: no transformation needed.
						// UR: horizontal flip
						// LL: vertical flip
						// LR: vertical then horizontal flip
						String flipped = pt;
						int whichFlip = rand.nextInt(4); // [0,3]
						if (whichFlip == 1) 
							flipped = CipherTransformations.flipHorizontal(pt, 20, 17);
						else if (whichFlip == 2) 
							flipped = CipherTransformations.flipVertical(pt, 20, 17);
						else if (whichFlip == 3) {
							flipped = CipherTransformations.flipVertical(pt, 20, 17);
							flipped = CipherTransformations.flipHorizontal(flipped, 20, 17);
						}
						int d = rand.nextInt(4); // [0,3]
						String diag = Diagonal.transform(flipped, 17, d); 
						hom = HomophonicGenerator.makeHomophonic(diag, ALPHABET_SIZE);
					} else if (which == 6) {
						// quadrants.  don't allow quadrant width or height less than 5.
						// order is random.
						int r = rand.nextInt(11) + 4; // [4,14]
						int c = rand.nextInt(8) + 4; // [4,11]
						int[] order = {0,1,2,3};
						HomophonicGenerator.shuffle(order);
						String quadrants = Quadrants.transform(pt, r, c, order, 17, false);
						hom = HomophonicGenerator.makeHomophonic(quadrants, ALPHABET_SIZE);
					} else if (which == 7) {
						// scytale
						int period = rand.nextInt(169)+2; // [2,170]
						String scytale = Scytale.encode(pt, period);
						hom = HomophonicGenerator.makeHomophonic(scytale, ALPHABET_SIZE);
					} else if (which == 8) {
						// snake
						int corner = rand.nextInt(4);
						boolean directionRow = rand.nextBoolean();
						String snake = Snake.transform(pt, corner, directionRow, 17, false, false);
						hom = HomophonicGenerator.makeHomophonic(snake, ALPHABET_SIZE);
					} else if (which == 9) {
						// 
					} else if (which == 10) {
						// vigenere
						int keyLength = rand.nextInt(29)+2; // [2,30]
						int[] key = new int[keyLength];
						for (int i=0; i<key.length; i++) {
							key[i] = rand.nextInt(25)+1; // [1,25]
						}
						String vig = Vigenere.encrypt(pt, key);
						hom = HomophonicGenerator.makeHomophonic(vig, ALPHABET_SIZE);
					} else if (which == 11) {
						// rail fence
						int rails = rand.nextInt(29)+2; // [2,30]
						boolean startAtBottom = rand.nextBoolean();
						String rail = RailFence.encode(pt, rails, startAtBottom);
						hom = HomophonicGenerator.makeHomophonic(rail, ALPHABET_SIZE);
					} else if (which == 12) {
						// random gibberish
						// one way: shuffle the pt then encode
						// another way: shuffle Z340
						boolean method = rand.nextBoolean();
						if (method) {
							String shuffled = CipherTransformations.shuffle(pt);
							hom = HomophonicGenerator.makeHomophonic(shuffled, ALPHABET_SIZE);
						} else {
							hom = CipherTransformations.shuffle(Ciphers.Z340);
						}
					} else if (which == 13) {
					} else if (which == 14) {
					} else if (which == 15) {
					} else if (which == 16) {
						// scytale with columns selected in varying order (i.e., not necessarily sequential like normal scytale)
						int period = rand.nextInt(39)+2; // [2,40]
						int[] order = new int[period];
						for (int i=0; i<order.length; i++) order[i] = i;
						HomophonicGenerator.shuffle(order); // random column ordering
						String scytale = Scytale.encode(pt, period, order, false);
						hom = HomophonicGenerator.makeHomophonic(scytale, ALPHABET_SIZE);
					} else if (which == 17) {
						// scytale fixed at period 19
						int period = 19;
						String scytale = Scytale.encode(pt, period);
						hom = HomophonicGenerator.makeHomophonic(scytale, ALPHABET_SIZE);
					} else if (which == 18) {
						// adfgvx
						// (http://docs.neu.edu.tr/staff/fahreddin.sadikoglu/Chapter_2%20Crypto_25.pdf
						// says it uses a step similar to scytale)
					} else if (which == 19) {
					} else if (which == 20) {
					} else if (which == 21) {
					} else if (which == 22) {
						// scytale combined with snake (this is basically the same as snake with arbitrary dimensions)
						// period of scytale and dimensions of snake's grid do not need to match.
					} else if (which == 23) {
						// snake but allow ANY grid width (not just clean factors of 340).
						int corner = rand.nextInt(4);
						boolean directionRow = rand.nextBoolean();
						int width = 2 + rand.nextInt(169); // [2,170]
						String snake = Snake.transform(pt, corner, directionRow, width, false, false);
						snake = snake.replaceAll(" ", "");
						hom = HomophonicGenerator.makeHomophonic(snake, ALPHABET_SIZE);
						
					} else if (which == 24) {
						// snake but path is 2 characters (lines) thick
						// output bigrams as we follow path, or output entire 1st line then entire 2nd line
						
					} else if (which == 25) {
						// split snake:
						// pick a horizontal or vertical line that splits the grid into two subgrids.
						// snake operation is performed in each subgrid.
						// generalize to n subgrids (within reason)
					} else if (which == 26) {
						// snake but allow ANY grid width (not just clean factors of 340),
						// and perform untransposition instead of transposition
						int corner = rand.nextInt(4);
						boolean directionRow = rand.nextBoolean();
						int width = 2 + rand.nextInt(169); // [2,170]
						String snake = Snake.transform(pt, corner, directionRow, width, false, true);
						snake = snake.replaceAll(" ", "");
						hom = HomophonicGenerator.makeHomophonic(snake, ALPHABET_SIZE);
					} else if (which == 27) {
						// permutation cipher
						// https://crypto.interactive-maths.com/permutation-cipher.html
						int size = 2+rand.nextInt(19); // [2,20]
						int[] permutation = new int[size];
						for (int j=0; j<size; j++) permutation[j] = j;
						boolean retry = true;
						while (retry) {
							HomophonicGenerator.shuffle(permutation);
							for (int j=0; j<size; j++) {
								if (permutation[j] != j) {
									retry = false;
									break;
								}
							}
						}
						String permute = Permutation.transform(pt, permutation, false);
						hom = HomophonicGenerator.makeHomophonic(permute, ALPHABET_SIZE);
					} else if (which == 28) {
					}
					
//					suffix = suffixFor(cipherTypeIndex);
					cipherTypeIndex = (cipherTypeIndex + 1) % WHICH_CIPHERS.length;
					// System.out.println(line + tab + hom + tab + Arrays.toString(doubles) + tab +
					// Arrays.toString(key));
					// StringBuilder doublesCSV = new StringBuilder();
					// for (int i=0; i<doubles.length; i++) {
					// if (doublesCSV.length() > 0)
					// doublesCSV.append(",");
					// doublesCSV.append(doubles[i]);
					// }
					// System.out.println(doublesCSV + suffix);
					double[] stats = statsFor(hom);
					StringBuilder sb = new StringBuilder(hom);
					sb.append(",").append(pt);
					for (double stat : stats) {
						if (sb.length() > 0) sb.append(",");
						sb.append(stat);
					}
//					sb.append(suffix);
					System.out.println(sb);
					hits++;
				}
				counter++;
				if (hits == max)
					break;

			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public static double[] statsFor(String cipher) {
		if (WHICH_STATS == 0) return statsShotgunFor(cipher);
		if (WHICH_STATS == 1) return statsRoutesFor(cipher);
		if (WHICH_STATS == 2) return statsShotgunAndRoutesFor(cipher);
		if (WHICH_STATS == 3) return statsColumnar(cipher);
		if (WHICH_STATS == 4) return statsColumnarFragments(cipher);
		if (WHICH_STATS == 5) return statsColumnarAndFragments(cipher);
		if (WHICH_STATS == 6) return statsShotgunRoutesColumnar(cipher);
		if (WHICH_STATS == 7) return statsPermutationFragments(cipher);
		if (WHICH_STATS == 8) return statsSnakeFragments(cipher);
		
		return null;
	}
	public static double[] statsSnakeFragments(String cipher) {
		int Lmax = 5;
		// 2) fragment stats, tracking max counts across all permutation untranspositions
		List<Integer> list = SnakeFragmentStats.generateUntransposedFragmentStatsFor(cipher, 2, Lmax);
		// 3) merge them together into one double array
		int index = 0;
		double[] result = new double[list.size()];
		for (Integer i : list) 
			result[index++] = i;
		return result;
	}
	public static double[] statsPermutationFragments(String cipher) {
		int Lmax = 7;
		int keyLengthMax = 5;
		// 1) reference fragment stats
		List<Integer> list1 = FragmentStats.generateFragmentStatsFor(cipher, 2, Lmax);
		// 2) fragment stats, tracking max counts across all permutation untranspositions
		List<Integer> list2 = PermutationFragmentStats.generateUntransposedFragmentStatsFor(cipher, 2, keyLengthMax, 2, Lmax);
		// 3) merge them together into one double array
		int index = 0;
		double[] combined = new double[list1.size()+list2.size()];
		for (Integer i : list1) 
			combined[index++] = i;
		for (Integer i : list2) 
			combined[index++] = i;
		return combined;
	}
	public static double[] statsColumnar(String cipher) {
		// get these stats for each digraph size n={2,3,4}, for each key length={2,...,maxLength}:
		// reference ioc
		// count of how often untransposed ioc beats reference (untransposed) ioc
		// max ioc found for all untransposed ciphers
		// diff of max ioc and ref ioc
		// average differences of untransposed iocs and ref ioc
		return ColumnarStats.generateStatsFor(cipher, 2, 5); // was [2,7]
	}
	public static double[] statsColumnarFragments(String cipher) {
		int Lmax = 7;
		int keyLengthMax = 5;
		// 1) reference fragment stats
		List<Integer> list1 = FragmentStats.generateFragmentStatsFor(cipher, 2, Lmax);
		// 2) fragment stats, tracking max counts across all columnar untranspositions
		List<Integer> list2 = ColumnarFragmentStats.generateUntransposedFragmentStatsFor(cipher, 2, keyLengthMax, 2, Lmax);
		// 3) merge them together into one double array
		int index = 0;
		double[] combined = new double[list1.size()+list2.size()];
		for (Integer i : list1) 
			combined[index++] = i;
		for (Integer i : list2) 
			combined[index++] = i;
		return combined;
	}
	public static double[] statsColumnarAndFragments(String cipher) {
		return null;
	}
	public static double[] statsShotgunAndRoutesFor(String cipher) {
		double[] stats1 = statsShotgunFor(cipher);
		double[] stats2 = statsRoutesFor(cipher);
		double[] combined = new double[stats1.length+stats2.length];
		int index = 0;
		for (double stat : stats1) combined[index++] = stat;
		for (double stat : stats2) combined[index++] = stat;
		return combined;
	}
	// shotgun + routes + columnar
	public static double[] statsShotgunRoutesColumnar(String cipher) {
		double[] stats1 = statsShotgunAndRoutesFor(cipher);
		double[] stats2 = statsColumnar(cipher);
		double[] combined = new double[stats1.length+stats2.length];
		int index = 0;
		for (double stat : stats1) combined[index++] = stat;
		for (double stat : stats2) combined[index++] = stat;
		return combined;
	}
	
	/** return statistics about the given cipher */
	public static double[] statsShotgunFor(String cipher) {
		StringBuilder cipherSB = new StringBuilder(cipher);
		
		// multiplicity
		double multiplicity = Stats.multiplicity(cipher);
		// ioc
		double ioc = Stats.ioc(cipher);
		// chi2
		double chi2 = Stats.chi2(cipher);
		// entropy
		double entropy = Stats.entropy(cipher);
		// ngraphic ioc (for n = {2,3,4}
		double ioc2gram = Stats.iocNgram(cipher, 2);
		double ioc3gram = Stats.iocNgram(cipher, 3);
		double ioc4gram = Stats.iocNgram(cipher, 4);
			// mean, min, and max ngraphic ioc across all periods
		
		double ioc2gramMean = 0;
		double ioc3gramMean = 0;
		double ioc4gramMean = 0;

		double ioc2gramMin = 0;
		double ioc3gramMin = 0;
		double ioc4gramMin = 0;

		double ioc2gramMax = 0;
		double ioc3gramMax = 0;
		double ioc4gramMax = 0;

		
		//	ngraphic iocs at all periods (within reason).
		double[][] ngraphicIocs = new double[19][3]; 
		int count = 0;
		for (int p=1; p<cipher.length()/2; p++) {
			String re = Periods.rewrite3(cipher, p);
			double i2g = Stats.iocNgram(re, 2);
			double i3g = Stats.iocNgram(re, 3);
			double i4g = Stats.iocNgram(re, 4);
			ioc2gramMean += i2g;
			ioc3gramMean += i3g;
			ioc4gramMean += i4g;
			ioc2gramMin = Math.min(ioc2gramMin, i2g);
			ioc3gramMin = Math.min(ioc3gramMin, i3g);
			ioc4gramMin = Math.min(ioc4gramMin, i4g);
			ioc2gramMax = Math.max(ioc2gramMax, i2g);
			ioc3gramMax = Math.max(ioc3gramMax, i3g);
			ioc4gramMax = Math.max(ioc4gramMax, i4g);

			if (p >= 2 && p <= 20) {
				ngraphicIocs[p-2] = new double[3];
				ngraphicIocs[p-2][0] = i2g;
				ngraphicIocs[p-2][1] = i3g;
				ngraphicIocs[p-2][2] = i4g;
			}
			count++;
		}
		ioc2gramMean = ioc2gramMean / count;
		ioc3gramMean = ioc3gramMean / count;
		ioc4gramMean = ioc4gramMean / count;

		// ngraphic iocs at all untransposed periods 
		double[][] ngraphicUntransposedIocs = new double[19][3]; 
		for (int p=2; p<=20; p++) {
			String re = Periods.rewrite3undo(cipher, p);
			double i2g = Stats.iocNgram(re, 2);
			double i3g = Stats.iocNgram(re, 3);
			double i4g = Stats.iocNgram(re, 4);
			ngraphicUntransposedIocs[p-2] = new double[3];
			ngraphicUntransposedIocs[p-2][0] = i2g;
			ngraphicUntransposedIocs[p-2][1] = i3g;
			ngraphicUntransposedIocs[p-2][2] = i4g;
		}
		
//		TODO: fragment ioc?  maybe if ngraphic iocs at all periods don't improve accuracy
//		TODO: specific transpositional iocs (spirals, diagonals, etc)
		
		double iocColumnMax = 0;
		double iocColumnMean = 0;
		double[][] iocColumns = new double[17][4]; // ngraphic iocs, n=[1,4]
		for (int col=0; col<17; col++) {
			double iocCol = Stats.iocColumn(cipher, 17, col);
			String columnString = Stats.column(cipher, 17, col);
			iocColumns[col][0] = iocCol;
			for (int n=2; n<5; n++) {
				iocColumns[col][n-1] = Stats.iocNgram(columnString, n);
			}
			iocColumnMax = Math.max(iocColumnMax, iocCol);
			iocColumnMean += iocCol;
		}
		iocColumnMean = iocColumnMean / 17;
		
		double iocRowMax = 0;
		double iocRowMean = 0;
		double[][] iocRows = new double[20][4]; // ngraphic iocs, n=[1,4]
		for (int row=0; row<20; row++) {
			double iocRow = Stats.iocRow(cipher, 17, row);
			String rowString = Stats.row(cipher, 17, row);
			iocRows[row][0] = iocRow;
			for (int n=2; n<5; n++) {
				iocRows[row][n-1] = Stats.iocNgram(rowString, n);
			}
			iocRowMax = Math.max(iocRowMax, iocRow);
			iocRowMean += iocRow;
		}
		iocRowMean = iocRowMean / 20;
		
		//TODO: best/avg untransposed columnar ioc? (is it still useful even if column order is random?)
		//TODO: vector of column iocs, vector of row iocs, quadrant/region iocs
		
		// period with highest number of repeating bigrams
		int[] peaks = Periods.peakPeriodicBigrams(cipher, false);
		double peakBigramPeriod = peaks[0];
		
		// alphabet size
		//double alphabetSize = Ciphers.alphabet(cipher).length();
		// TODO: kasiski exam
		// (maybe make a vector for each period, each entry expressed in terms of ioc of the coincident symbols)
		// (and run it in multiple directions)
		// bartW counts (max unigram repeats at any period)
		double maxUnigramRepeatsAtAnyPeriod = 0;
		for (int period=1; period < cipher.length()/2; period++) {
			maxUnigramRepeatsAtAnyPeriod = Math.max(maxUnigramRepeatsAtAnyPeriod, Kasiski.bartWCount(cipher, period, false));
		}
		// hom cycle measurements
		double perfectCycle = HomophonesNew.perfectCycleScoreFor(2, cipher, 3, false);
		
		// average length of non-repeating sequences
		double nonRepeatAverageLength = JarlveMeasurements.nonrepeatAverage(cipher);
		
		//TODO: longest non-repeating sequence

		double MIC = BionStats.MIC(cipher, 20);
		double MKA = BionStats.MKA(cipher, 20);
		double EDI_even = BionStats.EDI(cipher, false);
		double EDI_odd = BionStats.EDI(cipher, true);
		double[] LR_and_ROD = BionStats.LR_and_ROD(cipher);
		//add higher periods for bion's periodic measurements
		double MIC_2 = BionStats.MIC(cipher, cipher.length()/2);
		double MKA_2 = BionStats.MKA(cipher, cipher.length()/2);
		
//		System.out.println("LR and ROD: " + Arrays.toString(LR_and_ROD(cipher)));
		
		// compute log ngraph stats for each cipher type.  create a log ngraph score for each one based on those stats.
		// (cipher must first be "symbol order normalized")
		
//		double ng1 = NGraphStats.ngraphScoreFor(cipher, mapC2);
//		double ng2 = NGraphStats.ngraphScoreFor(cipher, mapC3);
//		double ng3 = NGraphStats.ngraphScoreFor(cipher, mapC4);
//		double ng4 = NGraphStats.ngraphScoreFor(cipher, mapC5);
//		double ng5 = NGraphStats.ngraphScoreFor(cipher, mapH2);
//		double ng6 = NGraphStats.ngraphScoreFor(cipher, mapH3);
//		double ng7 = NGraphStats.ngraphScoreFor(cipher, mapH4);
//		double ng8 = NGraphStats.ngraphScoreFor(cipher, mapH5);

		// untransposed columnar ngraphic iocs for small key lengths
		double[][][] iocsColumnarUntransposed = new double[6][3][2];
		for (int keyLength=2; keyLength<=7; keyLength++) {
			iocsColumnarUntransposed[keyLength-2] = ColumnarTransposition.untransposedIocs(cipherSB, keyLength, false);
		}
		
		// untransposed spirals ngraphic iocs
		double[][] iocsSpirals = new double[2][3]; // 2 rows (one per spiral transformation), 3 cols (one per ngram size)
		List<StringBuffer> list = TransformationBase.toList(
				cipher, 17);
		Transformation t = new Spiral(list, 0);
		t.execute(false);
		String spiral1 = Transformation.transform(cipher, t.getTransformMap(), false);
		String spiral2 = Transformation.transform(cipher, t.getTransformMap(), true);
		for (int n=2; n<5; n++) {
			iocsSpirals[0][n-2] = Stats.iocNgram(spiral1, n);
			iocsSpirals[1][n-2] = Stats.iocNgram(spiral2, n);
		}
		
		// untransposed snake ngraphic iocs
		
		double[][][] iocsSnakes = new double[4][2][3]; // 4 corners, 2 primary directions, 3 ngram sizes
		for (int corner = 0; corner < 4; corner++) {
			iocsSnakes[corner] = new double[2][3];
			for (int direction = 0; direction < 2; direction++) {
				iocsSnakes[corner][direction] = new double[3];
				for (int n=2; n<5; n++) {
					String untransposed = Snake.transform(cipher, corner, direction == 1, 17, false, true);
					iocsSnakes[corner][direction][n-2] = Stats.iocNgram(untransposed, n); 
				}
			}
		}
		
		// TODO average ngraphic iocs for untransposed/transposed snakes for all widths in [2,170]
//		double[] iocsSnakesMeanUntransposed = Snake.iocsSnakesMean(cipher, true);
//		double[] iocsSnakesMeanTransposed = Snake.iocsSnakesMean(cipher, false);
		
		return new double[] { ioc, chi2, entropy, ioc2gram, ioc3gram, ioc4gram, ioc2gramMax, ioc3gramMax, ioc4gramMax,
				ioc2gramMean, ioc3gramMean, ioc4gramMean, peakBigramPeriod, maxUnigramRepeatsAtAnyPeriod, perfectCycle,
				nonRepeatAverageLength, MIC, MKA, EDI_even, EDI_odd, LR_and_ROD[0], LR_and_ROD[1], MIC_2, MKA_2,
				iocColumnMax, iocColumnMean, iocRowMax, iocRowMean, ngraphicIocs[2][0], ngraphicIocs[2][1],
				ngraphicIocs[2][2], ngraphicIocs[3][0], ngraphicIocs[3][1], ngraphicIocs[3][2], ngraphicIocs[4][0],
				ngraphicIocs[4][1], ngraphicIocs[4][2], ngraphicIocs[5][0], ngraphicIocs[5][1], ngraphicIocs[5][2],
				ngraphicIocs[6][0], ngraphicIocs[6][1], ngraphicIocs[6][2], ngraphicIocs[7][0], ngraphicIocs[7][1],
				ngraphicIocs[7][2], ngraphicIocs[8][0], ngraphicIocs[8][1], ngraphicIocs[8][2], ngraphicIocs[9][0],
				ngraphicIocs[9][1], ngraphicIocs[9][2], ngraphicIocs[10][0], ngraphicIocs[10][1], ngraphicIocs[10][2],
				ngraphicIocs[11][0], ngraphicIocs[11][1], ngraphicIocs[11][2], ngraphicIocs[12][0], ngraphicIocs[12][1],
				ngraphicIocs[12][2], ngraphicIocs[13][0], ngraphicIocs[13][1], ngraphicIocs[13][2], ngraphicIocs[14][0],
				ngraphicIocs[14][1], ngraphicIocs[14][2], ngraphicIocs[15][0], ngraphicIocs[15][1], ngraphicIocs[15][2],
				ngraphicIocs[16][0], ngraphicIocs[16][1], ngraphicIocs[16][2], ngraphicIocs[17][0], ngraphicIocs[17][1],
				ngraphicIocs[17][2], ngraphicIocs[18][0], ngraphicIocs[18][1], ngraphicIocs[18][2],
				ngraphicUntransposedIocs[2][0], ngraphicUntransposedIocs[2][1], ngraphicUntransposedIocs[2][2],
				ngraphicUntransposedIocs[3][0], ngraphicUntransposedIocs[3][1], ngraphicUntransposedIocs[3][2],
				ngraphicUntransposedIocs[4][0], ngraphicUntransposedIocs[4][1], ngraphicUntransposedIocs[4][2],
				ngraphicUntransposedIocs[5][0], ngraphicUntransposedIocs[5][1], ngraphicUntransposedIocs[5][2],
				ngraphicUntransposedIocs[6][0], ngraphicUntransposedIocs[6][1], ngraphicUntransposedIocs[6][2],
				ngraphicUntransposedIocs[7][0], ngraphicUntransposedIocs[7][1], ngraphicUntransposedIocs[7][2],
				ngraphicUntransposedIocs[8][0], ngraphicUntransposedIocs[8][1], ngraphicUntransposedIocs[8][2],
				ngraphicUntransposedIocs[9][0], ngraphicUntransposedIocs[9][1], ngraphicUntransposedIocs[9][2],
				ngraphicUntransposedIocs[10][0], ngraphicUntransposedIocs[10][1], ngraphicUntransposedIocs[10][2],
				ngraphicUntransposedIocs[11][0], ngraphicUntransposedIocs[11][1], ngraphicUntransposedIocs[11][2],
				ngraphicUntransposedIocs[12][0], ngraphicUntransposedIocs[12][1], ngraphicUntransposedIocs[12][2],
				ngraphicUntransposedIocs[13][0], ngraphicUntransposedIocs[13][1], ngraphicUntransposedIocs[13][2],
				ngraphicUntransposedIocs[14][0], ngraphicUntransposedIocs[14][1], ngraphicUntransposedIocs[14][2],
				ngraphicUntransposedIocs[15][0], ngraphicUntransposedIocs[15][1], ngraphicUntransposedIocs[15][2],
				ngraphicUntransposedIocs[16][0], ngraphicUntransposedIocs[16][1], ngraphicUntransposedIocs[16][2],
				ngraphicUntransposedIocs[17][0], ngraphicUntransposedIocs[17][1], ngraphicUntransposedIocs[17][2],
				ngraphicUntransposedIocs[18][0], ngraphicUntransposedIocs[18][1], ngraphicUntransposedIocs[18][2],
				iocsColumnarUntransposed[0][0][0], iocsColumnarUntransposed[0][0][1], iocsColumnarUntransposed[0][1][0],
				iocsColumnarUntransposed[0][1][1], iocsColumnarUntransposed[0][2][0], iocsColumnarUntransposed[0][2][1],
				iocsColumnarUntransposed[1][0][0], iocsColumnarUntransposed[1][0][1], iocsColumnarUntransposed[1][1][0],
				iocsColumnarUntransposed[1][1][1], iocsColumnarUntransposed[1][2][0], iocsColumnarUntransposed[1][2][1],
				iocsColumnarUntransposed[2][0][0], iocsColumnarUntransposed[2][0][1], iocsColumnarUntransposed[2][1][0],
				iocsColumnarUntransposed[2][1][1], iocsColumnarUntransposed[2][2][0], iocsColumnarUntransposed[2][2][1],
				iocsColumnarUntransposed[3][0][0], iocsColumnarUntransposed[3][0][1], iocsColumnarUntransposed[3][1][0],
				iocsColumnarUntransposed[3][1][1], iocsColumnarUntransposed[3][2][0], iocsColumnarUntransposed[3][2][1],
				iocsColumnarUntransposed[4][0][0], iocsColumnarUntransposed[4][0][1], iocsColumnarUntransposed[4][1][0],
				iocsColumnarUntransposed[4][1][1], iocsColumnarUntransposed[4][2][0], iocsColumnarUntransposed[4][2][1],
				iocsColumnarUntransposed[5][0][0], iocsColumnarUntransposed[5][0][1], iocsColumnarUntransposed[5][1][0],
				iocsColumnarUntransposed[5][1][1], iocsColumnarUntransposed[5][2][0], iocsColumnarUntransposed[5][2][1],
				iocsSpirals[0][0], iocsSpirals[0][1], iocsSpirals[0][2], iocsSpirals[1][0], iocsSpirals[1][1],
				iocsSpirals[1][2], iocColumns[0][0], iocColumns[0][1], iocColumns[0][2], iocColumns[0][3],
				iocColumns[1][0], iocColumns[1][1], iocColumns[1][2], iocColumns[1][3], iocColumns[2][0],
				iocColumns[2][1], iocColumns[2][2], iocColumns[2][3], iocColumns[3][0], iocColumns[3][1],
				iocColumns[3][2], iocColumns[3][3], iocColumns[4][0], iocColumns[4][1], iocColumns[4][2],
				iocColumns[4][3], iocColumns[5][0], iocColumns[5][1], iocColumns[5][2], iocColumns[5][3],
				iocColumns[6][0], iocColumns[6][1], iocColumns[6][2], iocColumns[6][3], iocColumns[7][0],
				iocColumns[7][1], iocColumns[7][2], iocColumns[7][3], iocColumns[8][0], iocColumns[8][1],
				iocColumns[8][2], iocColumns[8][3], iocColumns[9][0], iocColumns[9][1], iocColumns[9][2],
				iocColumns[9][3], iocColumns[10][0], iocColumns[10][1], iocColumns[10][2], iocColumns[10][3],
				iocColumns[11][0], iocColumns[11][1], iocColumns[11][2], iocColumns[11][3], iocColumns[12][0],
				iocColumns[12][1], iocColumns[12][2], iocColumns[12][3], iocColumns[13][0], iocColumns[13][1],
				iocColumns[13][2], iocColumns[13][3], iocColumns[14][0], iocColumns[14][1], iocColumns[14][2],
				iocColumns[14][3], iocColumns[15][0], iocColumns[15][1], iocColumns[15][2], iocColumns[15][3],
				iocColumns[16][0], iocColumns[16][1], iocColumns[16][2], iocColumns[16][3], iocRows[0][0],
				iocRows[0][1], iocRows[0][2], iocRows[0][3], iocRows[1][0], iocRows[1][1], iocRows[1][2], iocRows[1][3],
				iocRows[2][0], iocRows[2][1], iocRows[2][2], iocRows[2][3], iocRows[3][0], iocRows[3][1], iocRows[3][2],
				iocRows[3][3], iocRows[4][0], iocRows[4][1], iocRows[4][2], iocRows[4][3], iocRows[5][0], iocRows[5][1],
				iocRows[5][2], iocRows[5][3], iocRows[6][0], iocRows[6][1], iocRows[6][2], iocRows[6][3], iocRows[7][0],
				iocRows[7][1], iocRows[7][2], iocRows[7][3], iocRows[8][0], iocRows[8][1], iocRows[8][2], iocRows[8][3],
				iocRows[9][0], iocRows[9][1], iocRows[9][2], iocRows[9][3], iocRows[10][0], iocRows[10][1],
				iocRows[10][2], iocRows[10][3], iocRows[11][0], iocRows[11][1], iocRows[11][2], iocRows[11][3],
				iocRows[12][0], iocRows[12][1], iocRows[12][2], iocRows[12][3], iocRows[13][0], iocRows[13][1],
				iocRows[13][2], iocRows[13][3], iocRows[14][0], iocRows[14][1], iocRows[14][2], iocRows[14][3],
				iocRows[15][0], iocRows[15][1], iocRows[15][2], iocRows[15][3], iocRows[16][0], iocRows[16][1],
				iocRows[16][2], iocRows[16][3], iocRows[17][0], iocRows[17][1], iocRows[17][2], iocRows[17][3],
				iocRows[18][0], iocRows[18][1], iocRows[18][2], iocRows[18][3], iocRows[19][0], iocRows[19][1],
				iocRows[19][2], iocRows[19][3], iocsSnakes[0][0][0], iocsSnakes[0][0][1], iocsSnakes[0][0][2],
				iocsSnakes[0][1][0], iocsSnakes[0][1][1], iocsSnakes[0][1][2], iocsSnakes[1][0][0], iocsSnakes[1][0][1],
				iocsSnakes[1][0][2], iocsSnakes[1][1][0], iocsSnakes[1][1][1], iocsSnakes[1][1][2], iocsSnakes[2][0][0],
				iocsSnakes[2][0][1], iocsSnakes[2][0][2], iocsSnakes[2][1][0], iocsSnakes[2][1][1], iocsSnakes[2][1][2],
				iocsSnakes[3][0][0], iocsSnakes[3][0][1], iocsSnakes[3][0][2], iocsSnakes[3][1][0], iocsSnakes[3][1][1],
				iocsSnakes[3][1][2]}; /* iocsSnakesMeanUntransposed[0], iocsSnakesMeanUntransposed[1], iocsSnakesMeanUntransposed[2],
				iocsSnakesMeanTransposed[0], iocsSnakesMeanTransposed[1], iocsSnakesMeanTransposed[2] };*/
	}

	public static String[] statsHeader() {
		return new String[] { "ioc", "chi2", "entropy", "ioc2gram", "ioc3gram", "ioc4gram", "ioc2gramMax",
				"ioc3gramMax", "ioc4gramMax", "ioc2gramMean", "ioc3gramMean", "ioc4gramMean", "peakBigramPeriod",
				"maxUnigramRepeatsAtAnyPeriod", "perfectCycle", "nonRepeatAverageLength", "MIC", "MKA", "EDI_even",
				"EDI_odd", "LR", "ROD", "MIC_2", "MKA_2", "iocColumnMax", "iocColumnMean", "iocRowMax", "iocRowMean",
				"ngraphicIocs[2][0]", "ngraphicIocs[2][1]", "ngraphicIocs[2][2]", "ngraphicIocs[3][0]",
				"ngraphicIocs[3][1]", "ngraphicIocs[3][2]", "ngraphicIocs[4][0]", "ngraphicIocs[4][1]",
				"ngraphicIocs[4][2]", "ngraphicIocs[5][0]", "ngraphicIocs[5][1]", "ngraphicIocs[5][2]",
				"ngraphicIocs[6][0]", "ngraphicIocs[6][1]", "ngraphicIocs[6][2]", "ngraphicIocs[7][0]",
				"ngraphicIocs[7][1]", "ngraphicIocs[7][2]", "ngraphicIocs[8][0]", "ngraphicIocs[8][1]",
				"ngraphicIocs[8][2]", "ngraphicIocs[9][0]", "ngraphicIocs[9][1]", "ngraphicIocs[9][2]",
				"ngraphicIocs[10][0]", "ngraphicIocs[10][1]", "ngraphicIocs[10][2]", "ngraphicIocs[11][0]",
				"ngraphicIocs[11][1]", "ngraphicIocs[11][2]", "ngraphicIocs[12][0]", "ngraphicIocs[12][1]",
				"ngraphicIocs[12][2]", "ngraphicIocs[13][0]", "ngraphicIocs[13][1]", "ngraphicIocs[13][2]",
				"ngraphicIocs[14][0]", "ngraphicIocs[14][1]", "ngraphicIocs[14][2]", "ngraphicIocs[15][0]",
				"ngraphicIocs[15][1]", "ngraphicIocs[15][2]", "ngraphicIocs[16][0]", "ngraphicIocs[16][1]",
				"ngraphicIocs[16][2]", "ngraphicIocs[17][0]", "ngraphicIocs[17][1]", "ngraphicIocs[17][2]",
				"ngraphicIocs[18][0]", "ngraphicIocs[18][1]", "ngraphicIocs[18][2]", "ngraphicUntransposedIocs[2][0]",
				"ngraphicUntransposedIocs[2][1]", "ngraphicUntransposedIocs[2][2]", "ngraphicUntransposedIocs[3][0]",
				"ngraphicUntransposedIocs[3][1]", "ngraphicUntransposedIocs[3][2]", "ngraphicUntransposedIocs[4][0]",
				"ngraphicUntransposedIocs[4][1]", "ngraphicUntransposedIocs[4][2]", "ngraphicUntransposedIocs[5][0]",
				"ngraphicUntransposedIocs[5][1]", "ngraphicUntransposedIocs[5][2]", "ngraphicUntransposedIocs[6][0]",
				"ngraphicUntransposedIocs[6][1]", "ngraphicUntransposedIocs[6][2]", "ngraphicUntransposedIocs[7][0]",
				"ngraphicUntransposedIocs[7][1]", "ngraphicUntransposedIocs[7][2]", "ngraphicUntransposedIocs[8][0]",
				"ngraphicUntransposedIocs[8][1]", "ngraphicUntransposedIocs[8][2]", "ngraphicUntransposedIocs[9][0]",
				"ngraphicUntransposedIocs[9][1]", "ngraphicUntransposedIocs[9][2]", "ngraphicUntransposedIocs[10][0]",
				"ngraphicUntransposedIocs[10][1]", "ngraphicUntransposedIocs[10][2]", "ngraphicUntransposedIocs[11][0]",
				"ngraphicUntransposedIocs[11][1]", "ngraphicUntransposedIocs[11][2]", "ngraphicUntransposedIocs[12][0]",
				"ngraphicUntransposedIocs[12][1]", "ngraphicUntransposedIocs[12][2]", "ngraphicUntransposedIocs[13][0]",
				"ngraphicUntransposedIocs[13][1]", "ngraphicUntransposedIocs[13][2]", "ngraphicUntransposedIocs[14][0]",
				"ngraphicUntransposedIocs[14][1]", "ngraphicUntransposedIocs[14][2]", "ngraphicUntransposedIocs[15][0]",
				"ngraphicUntransposedIocs[15][1]", "ngraphicUntransposedIocs[15][2]", "ngraphicUntransposedIocs[16][0]",
				"ngraphicUntransposedIocs[16][1]", "ngraphicUntransposedIocs[16][2]", "ngraphicUntransposedIocs[17][0]",
				"ngraphicUntransposedIocs[17][1]", "ngraphicUntransposedIocs[17][2]", "ngraphicUntransposedIocs[18][0]",
				"ngraphicUntransposedIocs[18][1]", "ngraphicUntransposedIocs[18][2]",
				"iocsColumnarUntransposed[0][0][0]", "iocsColumnarUntransposed[0][0][1]",
				"iocsColumnarUntransposed[0][1][0]", "iocsColumnarUntransposed[0][1][1]",
				"iocsColumnarUntransposed[0][2][0]", "iocsColumnarUntransposed[0][2][1]",
				"iocsColumnarUntransposed[1][0][0]", "iocsColumnarUntransposed[1][0][1]",
				"iocsColumnarUntransposed[1][1][0]", "iocsColumnarUntransposed[1][1][1]",
				"iocsColumnarUntransposed[1][2][0]", "iocsColumnarUntransposed[1][2][1]",
				"iocsColumnarUntransposed[2][0][0]", "iocsColumnarUntransposed[2][0][1]",
				"iocsColumnarUntransposed[2][1][0]", "iocsColumnarUntransposed[2][1][1]",
				"iocsColumnarUntransposed[2][2][0]", "iocsColumnarUntransposed[2][2][1]",
				"iocsColumnarUntransposed[3][0][0]", "iocsColumnarUntransposed[3][0][1]",
				"iocsColumnarUntransposed[3][1][0]", "iocsColumnarUntransposed[3][1][1]",
				"iocsColumnarUntransposed[3][2][0]", "iocsColumnarUntransposed[3][2][1]",
				"iocsColumnarUntransposed[4][0][0]", "iocsColumnarUntransposed[4][0][1]",
				"iocsColumnarUntransposed[4][1][0]", "iocsColumnarUntransposed[4][1][1]",
				"iocsColumnarUntransposed[4][2][0]", "iocsColumnarUntransposed[4][2][1]",
				"iocsColumnarUntransposed[5][0][0]", "iocsColumnarUntransposed[5][0][1]",
				"iocsColumnarUntransposed[5][1][0]", "iocsColumnarUntransposed[5][1][1]",
				"iocsColumnarUntransposed[5][2][0]", "iocsColumnarUntransposed[5][2][1]", "iocsSpirals[0][0]",
				"iocsSpirals[0][1]", "iocsSpirals[0][2]", "iocsSpirals[1][0]", "iocsSpirals[1][1]", "iocsSpirals[1][2]",
				"iocColumns[0][0]", "iocColumns[0][1]", "iocColumns[0][2]", "iocColumns[0][3]", "iocColumns[1][0]",
				"iocColumns[1][1]", "iocColumns[1][2]", "iocColumns[1][3]", "iocColumns[2][0]", "iocColumns[2][1]",
				"iocColumns[2][2]", "iocColumns[2][3]", "iocColumns[3][0]", "iocColumns[3][1]", "iocColumns[3][2]",
				"iocColumns[3][3]", "iocColumns[4][0]", "iocColumns[4][1]", "iocColumns[4][2]", "iocColumns[4][3]",
				"iocColumns[5][0]", "iocColumns[5][1]", "iocColumns[5][2]", "iocColumns[5][3]", "iocColumns[6][0]",
				"iocColumns[6][1]", "iocColumns[6][2]", "iocColumns[6][3]", "iocColumns[7][0]", "iocColumns[7][1]",
				"iocColumns[7][2]", "iocColumns[7][3]", "iocColumns[8][0]", "iocColumns[8][1]", "iocColumns[8][2]",
				"iocColumns[8][3]", "iocColumns[9][0]", "iocColumns[9][1]", "iocColumns[9][2]", "iocColumns[9][3]",
				"iocColumns[10][0]", "iocColumns[10][1]", "iocColumns[10][2]", "iocColumns[10][3]", "iocColumns[11][0]",
				"iocColumns[11][1]", "iocColumns[11][2]", "iocColumns[11][3]", "iocColumns[12][0]", "iocColumns[12][1]",
				"iocColumns[12][2]", "iocColumns[12][3]", "iocColumns[13][0]", "iocColumns[13][1]", "iocColumns[13][2]",
				"iocColumns[13][3]", "iocColumns[14][0]", "iocColumns[14][1]", "iocColumns[14][2]", "iocColumns[14][3]",
				"iocColumns[15][0]", "iocColumns[15][1]", "iocColumns[15][2]", "iocColumns[15][3]", "iocColumns[16][0]",
				"iocColumns[16][1]", "iocColumns[16][2]", "iocColumns[16][3]", "iocRows[0][0]", "iocRows[0][1]",
				"iocRows[0][2]", "iocRows[0][3]", "iocRows[1][0]", "iocRows[1][1]", "iocRows[1][2]", "iocRows[1][3]",
				"iocRows[2][0]", "iocRows[2][1]", "iocRows[2][2]", "iocRows[2][3]", "iocRows[3][0]", "iocRows[3][1]",
				"iocRows[3][2]", "iocRows[3][3]", "iocRows[4][0]", "iocRows[4][1]", "iocRows[4][2]", "iocRows[4][3]",
				"iocRows[5][0]", "iocRows[5][1]", "iocRows[5][2]", "iocRows[5][3]", "iocRows[6][0]", "iocRows[6][1]",
				"iocRows[6][2]", "iocRows[6][3]", "iocRows[7][0]", "iocRows[7][1]", "iocRows[7][2]", "iocRows[7][3]",
				"iocRows[8][0]", "iocRows[8][1]", "iocRows[8][2]", "iocRows[8][3]", "iocRows[9][0]", "iocRows[9][1]",
				"iocRows[9][2]", "iocRows[9][3]", "iocRows[10][0]", "iocRows[10][1]", "iocRows[10][2]",
				"iocRows[10][3]", "iocRows[11][0]", "iocRows[11][1]", "iocRows[11][2]", "iocRows[11][3]",
				"iocRows[12][0]", "iocRows[12][1]", "iocRows[12][2]", "iocRows[12][3]", "iocRows[13][0]",
				"iocRows[13][1]", "iocRows[13][2]", "iocRows[13][3]", "iocRows[14][0]", "iocRows[14][1]",
				"iocRows[14][2]", "iocRows[14][3]", "iocRows[15][0]", "iocRows[15][1]", "iocRows[15][2]",
				"iocRows[15][3]", "iocRows[16][0]", "iocRows[16][1]", "iocRows[16][2]", "iocRows[16][3]",
				"iocRows[17][0]", "iocRows[17][1]", "iocRows[17][2]", "iocRows[17][3]", "iocRows[18][0]",
				"iocRows[18][1]", "iocRows[18][2]", "iocRows[18][3]", "iocRows[19][0]", "iocRows[19][1]",
				"iocRows[19][2]", "iocRows[19][3]", "iocsSnakes[0][0][0]", "iocsSnakes[0][0][1]", "iocsSnakes[0][0][2]",
				"iocsSnakes[0][1][0]", "iocsSnakes[0][1][1]", "iocsSnakes[0][1][2]", "iocsSnakes[1][0][0]",
				"iocsSnakes[1][0][1]", "iocsSnakes[1][0][2]", "iocsSnakes[1][1][0]", "iocsSnakes[1][1][1]",
				"iocsSnakes[1][1][2]", "iocsSnakes[2][0][0]", "iocsSnakes[2][0][1]", "iocsSnakes[2][0][2]",
				"iocsSnakes[2][1][0]", "iocsSnakes[2][1][1]", "iocsSnakes[2][1][2]", "iocsSnakes[3][0][0]",
				"iocsSnakes[3][0][1]", "iocsSnakes[3][0][2]", "iocsSnakes[3][1][0]", "iocsSnakes[3][1][1]",
				"iocsSnakes[3][1][2]" }; /*, "iocsSnakesMeanUntransposed[0]", "iocsSnakesMeanUntransposed[1]", "iocsSnakesMeanUntransposed[2]",
				"iocsSnakesMeanTransposed[0]", "iocsSnakesMeanTransposed[1]", "iocsSnakesMeanTransposed[2]" };*/
	}
	public static double[] statsRoutesFor(String cipher) {
		// ngraphic iocs for all untransposed routes
		
		double[] stats = new double[4*2*2*(170-2+1)];
		int i=0;
		for (int corner=0; corner<4; corner++) {
			for (boolean directionRow : new boolean[] {false, true}) {
				for (boolean rectangle : new boolean[] {false, true}) {
					for (int width=2; width<=170; width++) {
						stats[i++] = Stats.iocNgram(Snake.transform(cipher, corner, directionRow, width, rectangle, true), 2);
					}
				}
				
			}
		}
		return stats;
	}
	
	/** standardize the alphabet for the given cipher. */
	public static String standardizeAlphabetFor(String cipher) {
		return Ciphers.fromNumeric(Ciphers.toNumeric(cipher, false), false, 5);
	}
	
	/** add more stats to an existing file */
	public static void addStatsTo(String inputPath, String outputPath) {
		System.out.println("adding to " + inputPath);
//		Map<String, Double> mapC2 = NGraphStats.ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/2graph-stats-columnar.txt");
//		Map<String, Double> mapC3 = NGraphStats.ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/3graph-stats-columnar.txt");
//		Map<String, Double> mapC4 = NGraphStats.ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/4graph-stats-columnar.txt");
//		Map<String, Double> mapC5 = NGraphStats.ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/5graph-stats-columnar.txt");
//		Map<String, Double> mapH2 = NGraphStats.ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/2graph-stats-homophonic.txt");
//		Map<String, Double> mapH3 = NGraphStats.ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/3graph-stats-homophonic.txt");
//		Map<String, Double> mapH4 = NGraphStats.ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/4graph-stats-homophonic.txt");
//		Map<String, Double> mapH5 = NGraphStats.ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/5graph-stats-homophonic.txt");
		BufferedReader input = null;
		List<String> list = new ArrayList<String>();
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String[] split = line.split(",");
				String cipher = split[0];
				int numInputs = split.length;
				StringBuilder sb = new StringBuilder();
				for (int i=0; i<numInputs; i++) {
					if (i > 0)
						sb.append(",");
					sb.append(split[i]);
				}
				
				double[] iocsSnakesMeanUntransposed = Snake.iocsSnakesMean(cipher, true);
				double[] iocsSnakesMeanTransposed = Snake.iocsSnakesMean(cipher, false);
				
				for (double d : iocsSnakesMeanUntransposed) {
					sb.append(",");
					sb.append(d);
				}
				for (double d : iocsSnakesMeanTransposed) {
					sb.append(",");
					sb.append(d);
				}
				list.add(sb.toString());
				System.out.println(sb);
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Writing to " + outputPath);
		FileUtil.writeText(outputPath, list);
		System.out.println("Done");
	}

	/** convert file of ciphers into integer sequences */
	public static void toNumeric(String inputPath, int numOutputs) {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String[] split = line.split(",");
				String cipher = standardizeAlphabetFor(split[0]);
				int[] vals = Ciphers.toNumeric(cipher, false, true);
				StringBuilder sb = new StringBuilder();
				for (int i: vals) {
					sb.append(i);
					sb.append(",");
				}
				sb.append(split[split.length-2]);
				sb.append(",");
				sb.append(split[split.length-1]);
				System.out.println(sb);
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	
	
	public static void dump(double[] stats) {
		String[] header = statsHeader();
		for (int i=0; i<stats.length; i++) {
			System.out.println(header[i] + ": " + stats[i]);
		}
		System.out.println();
	}
	
	public static String suffixFor(int which) {
		StringBuilder suffix = new StringBuilder(); 
		for (int i=0; i<WHICH_CIPHERS.length; i++) {
			suffix.append(",").append(i == which ? "1" : "0");
		}
		return suffix.toString();
	}
	
	public static void testStats() {
		String[] ciphers = new String[] {
				Ciphers.Z408,
				Ciphers.Z340,
				"bmjwjpnxymjpjdymjxmnkyfuuqnjiytjfhmqjyyjwfkyjwfuuqdnslymnxkzshyntsymjwjxzqynxfszrgjwbmnhmrzxyymjsgjywfsxqfyjigfhpnsytfqjyyjwymjijhwduyntskzshyntsnx",
				"ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti",
				"COMPAREUNKNOWNCIPHERAGAINSTACACIPHERTYPESEXTENDEDDIRECTIONSTYPEORPASTEUNKNOWNCIPHERINTOCIPHERBOXCLICKIDTESTCOMPAREORCHOOSESPECIFICCIPHERTYPEFORCOMPARISONCIPHERBMJWJPNXYMJPJDYMJXMNKYFUUQNJIYTJFHMQJYYJWFKYJWFUUQDNSLYMNXKZSHYNTSYMJWJXZQYNXFSZRGJWBMNHMRZXYYMJSGJYWFSXQFYJIGFHPNSYTFQJYYJWYMJIJHWDUYNTSKZSHYNTSNXSHOWALLSTATSSHOWQADIALOGUESMAXIMUMPERIODTOTRYSTATSLENGTHICMICMKADICEDILRRODLDINOMORNICPHICMPICBDISDDCDDSSTDHASLYHASJYDBLNSERPNHASNHASDNHASNDIVNDIVYDIVNDIVNDIVYDIVYPSQNALDIBLDIPLDISLDIVLDIPTXRDIUSEDMAXIMUMPERIODOFWINNERISPATRISTOCRATWITHVOTESOUTOFVOTEDISTRIBUTIONPATRISTOCRATFRACMORSEPLAINTEXTCHECKERBOARDBAZERIESVIGENEREVARIANTSTATSABBREVATIONSLENLENGTHICINDEXOFCOINCIDENCETIMESMICMAXICFORPERIODSTIMESMKAMAXKAPPAFORPERIODSTIMESDICDIGRAPHICINDEXOFCOINCIDENCETIMESEDIDICFOREVENNUMBEREDPAIRSTIMESLRLONGREPEATPERCENTAGEOFSYMBOLREPEATSRODPERCENTAGEOFODDSPACEDREPEATSTOALLREPEATSLDIAVERAGEENGLISHLOGDIGRAPHSCORESDDAVERAGEENGLISHSINGLELETTERDIGRAPHDISCREPANCYSCOREEXTENDEDSTATSDIVLENGTHDIVISIBLEBYDIVLENGTHDIVISIBLEBYDIVLENGTHDIVISIBLEBYDIVLENGTHDIVISIBLEBYDIVLENGTHDIVISIBLEBYINTEGERBETWEENANDDIVLENGTHDIVISIBLEBYINTEGERBETWEENANDPSQLENGTHISPERFECTSQUAREHASLCIPHERHASLETTERSHASDCIPHERHASDIGITSHASHCPHERHASSYMBOLHASJCIPHERHASAJDBLCIPHERHASEVENLENGTHANDADOUBLEDLETTERATANEVENPOSITIONVIGENEREFAMILYSTATSALDILOGDIGRAPHSCOREFORAUTOKEYBLDILOGDIGRAPHSCOREFORBEAUFORTPLDILOGDIGRAPHSCOREFORPORTASLDILOGDIGRAPHSCOREFORSLIDEFAIRVLDILOGDIGRAPHSCOREFORVIGENEREOTHERSTATSNOMORNORMALORDERRDIREVERSELOGDIGRAPHSCOREPTXLOGDIGRAPHSCOREFORPORTAXNICMAXNICODEMUSICFORPERIODSPHICPHILLIPSICHASDIGITALCIPHERTHATINCLUDESAZEROBDIMAXBIFIDDICFORPERIODSCDDMAXCOLUMNARSDDSCOREFORPERIODSTOSSTDTHEMAXSTDSCOREFORSWAGMANPERIODSTOMPICMAXPROGRESSIVEKEYICFORPERIODSTOSERPPOSSIBLESERIATEDPLAYFAIRPERIODFROMTO",
				"ACCESSIBILITYLINKSSKIPTOMAINCONTENTACCESSIBILITYHELPACCESSIBILITYFEEDBACKGOOGLESAMPLESUBSTITUTIONCIPHERABOUTRESULTSSECONDSINCLUDINGRESULTSFORSIMPLESUBSTITUTIONCIPHERSEARCHONLYFORSAMPLESUBSTITUTIONCIPHERSEARCHRESULTSWHATISSUBSTITUTIONCIPHEREXAMPLEANEXAMPLEKEYISPLAINALPHABETABCDEFGHIJKLMNOPQRSTUVWXYZCIPHERALPHABETPHQGIUMEAYLNOFDXJKRCVSTZWBANEXAMPLEENCRYPTIONUSINGTHEABOVEKEYPLAINTEXTDEFENDTHEEASTWALLOFTHECASTLECIPHERTEXTGIUIFGCEIIPRCTPNNDUCEIQPRCNISIMPLESUBSTITUTIONCIPHERPRACTICALCRYPTOGRAPHYPRACTICALCRYPTOGRAPHYCOMCIPHERSSIMPLESUBSTITUTIONCIPHERSEARCHFORWHATISSUBSTITUTIONCIPHEREXAMPLEFEEDBACKABOUTFEATUREDSNIPPETSWEBRESULTSSIMPLESUBSTITUTIONCIPHERPRACTICALCRYPTOGRAPHYPRACTICALCRYPTOGRAPHYCOMCIPHERSTHESIMPLESUBSTITUTIONCIPHERISACIPHERTHATHASBEENINUSEFORMANYHEREISAQUICKEXAMPLEOFTHEENCRYPTIONANDDECRYPTIONSTEPSINVOLVEDWITHTHEPEOPLEALSOASKWHATISSUBSTITUTIONCIPHEREXAMPLEHOWDOYOUSOLVEASUBSTITUTIONCIPHERCANONESYMBOLCOULDBESUBSTITUTEDWITHAKEYUSINGSUBSTITUTIONCIPHERWHICHTYPEOFSUBSTITUTIONISCALLEDMONOALPHABETICSUBSTITUTIONCIPHERFEEDBACKWEBRESULTSCAESARCIPHERPRACTICALCRYPTOGRAPHYPRACTICALCRYPTOGRAPHYCOMCIPHERSITISATYPEOFSUBSTITUTIONCIPHERINWHICHEACHLETTERINTHEPLAINTEXTISSHIFTEDACERTAINNUMBEROFPLACESDOWNTHEALPHABETFOREXAMPLEWITHASHIFTOFAWOULDBEREPLACEDBYBBWOULDBECOMECANDSOONTHEMETHODISNAMEDAFTERJULIUSCAESARWHOAPPARENTLYUSEDITTOCOMMUNICATEWITHHISGENERALSPEOPLEALSOSEARCHFORCAESARCIPHERPYTHONCAESARCIPHERDECODERALBERTISDISKBRIEFLYDEFINETHECAESARCIPHERVIGENRECIPHERATBASHSUBSTITUTIONCIPHERWIKIPEDIAHTTPSENWIKIPEDIAORGWIKISUBSTITUTIONCIPHERINCRYPTOGRAPHYASUBSTITUTIONCIPHERISAMETHODOFENCRYPTINGBYWHICHUNITSOFPLAINTEXTARETHEBEALECIPHERSAREANOTHEREXAMPLEOFAHOMOPHONICCIPHERTHISISASTORYOFBURIEDTREASURETHATWASDESCRIBEDINBYUSEOFA‎SIMPLESUBSTITUTIONPOLYGRAPHICSUBSTITUTIONMECHANICALSUBSTITUTIONSUBSTITUTIONCIPHERANOVERVIEWSCIENCEDIRECTTOPICSHTTPSWWWSCIENCEDIRECTCOMTOPICSCOMPUTERSCIENCESUBSTITUTIONCIPFOREXAMPLEDATAENCRYPTIONSTANDARDDESAPPLIESCYCLESOFSUBSTITUTIONCIPHERSENCRYPTTHEPLAINTEXTBYSWAPPINGEACHLETTERORSYMBOLINTHEPLAINTEXTALPHABETICALSUBSTITUTIONCIPHERCRYPTOGRAMDECODERSOLVERDCODEHTTPSWWWDCODEFRMONOALPHABETICSUBSTITUTIONTOOLTODECRYPTMONOALPHABETICALSUBSTITUTIONANDFINDEACHLETTEROFASUBSTITUTEDMESSAGEWITHADERANGEDALPHABETMONOALPHABETICCIPHEREXAMPLEWITHTHISSUBSTITUTIONDCODEISENCRYPTEDASJAKJYANYDERANGEDALPHABETCANBESUBSTITUTIONCIPHERCORNELLUNIVERSITYPIMATHCORNELLEDUMECCRYPTOGRAPHYSUBSSUBSTITUTIONMARTHENAMESUBSTITUTIONCIPHERCOMESFROMTHEFACTTHATEACHLETTERTHATYOUWANTTOENCIPHERISSUBSTITUTEDBYANOTHERLETTERORSYMBOLBUTTHESIMPLESUBSTITUTIONCIPHERJNICHOLLORGJNICHOLLORGCRYPTANALYSISCIPHERSSIMPLESUBSTITUTIONJUMPTOEXAMPLEEXAMPLESUPPOSETHATYOUWANTEDTOENCIPHERTHETEXTTHEMOMENTECHOSAWNARCISSUSSHEWASINLOVESHEFOLLOWEDHIMLIKEACRYPTOGRAPHYTUTORIALSUBSTITUTIONCIPHERSTICOMHTTPSWWWTICOMCRYPTOTUTSUBSTITUTIONMOSTOFTHECIPHERSINTHETUTORIALARESUBSTITUTI"
		};
		for (String cipher : ciphers) {
			System.out.println("========== CIPHER: " + cipher);
			dump(statsFor(cipher));
		}
		
	}
	public static void dumpZ408Z340Stats(int which) {
		CipherGenerator.WHICH_STATS = which;
		String MY_Z408 = "6abF5FdalIZyOA!sLtWteNpSG6zDcR!jKeX9KZIzdSj!7APpZzXoTFHlaiZvmba8YoVAHZzXrPn77rczk5eNpRGjoZHSzXGjnxz8xTvA2zKrNTEVNl!MxalznFillRnVrz06Zvyi3rcjGzXtjnozz02bLl6ZvGwOt7tTe5bz6fG3rZkAzzr2zXNTvtzzITvB!KQ2neFR!P4qIzXxvgQaz0tktczON2znPdzHNzXNGm0rZiJIt6q6lLkr7AWnQZdZsE2NhHe5fZ1olLzX5bXN3tFiLL5umialkAe!VrjCfaU3AfDqdaaZnzvi3AB!pjBTxjAkGexpfABnpqDaaz2C";
		for (String hom : new String[] {
				Ciphers.Z408.substring(0,340), 
				standardizeAlphabetFor(MY_Z408.substring(0, 340)),
				standardizeAlphabetFor(Ciphers.Z340)
		}) {
			System.out.println(Arrays.toString(Ciphers.toNumeric(hom, false, true)));
			double[] stats = statsFor(hom);
			StringBuilder sb = new StringBuilder();
			sb.append(hom);
			sb.append(",");
			sb.append(hom);
			for (double stat : stats) {
				sb.append(",");
				sb.append(stat);
			}
			System.out.println(sb);
		}
	}
	
	public static void testSnake() {
		Random rand = new Random();
		String pt = Ciphers.Z408_SOLUTION.substring(0,340);
		int corner = rand.nextInt(4);
		boolean directionRow = rand.nextBoolean();
		int width = 2 + rand.nextInt(169); // [2,170]
		int leftover = pt.length() % width;
		boolean irregular = leftover > 0;
		if (irregular) {
			for (int i=0; i<width-leftover; i++) {
				pt = pt + " ";
			}
		}
		System.out.println(corner + " " + directionRow + " " + width + " " + leftover);
		String snake = Snake.transform(pt, corner, directionRow, width, false, false);
		System.out.println(snake);
		System.out.println(snake.replaceAll(" ", ""));
		snake = Snake.transform(snake, corner, directionRow, width, false, true);
		System.out.println(snake);
	}

	public static void addStats() {
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/columnar.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/columnar.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/diagonal.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/diagonal.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/gibberish.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/gibberish.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/homophonic2.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/homophonic2.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/permutation.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/permutation.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/quadrants.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/quadrants.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/railfence.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/railfence.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/scytale-period-19.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/scytale-period-19.csv");
//		addStatsTo(
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/scytale-random-column-order.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/scytale-random-column-order.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/scytale.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/scytale.csv");
//		addStatsTo(
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/snake-after-bugfix--untransposition.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/snake-after-bugfix--untransposition.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/snake-after-bugfix.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/snake-after-bugfix.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/spiral.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/spiral.csv");
//		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/vigenere.csv",
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/vigenere.csv");
		addStatsTo("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-2/zodiac.csv",
				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-3/zodiac.csv");
	}

	public static void main(String[] args) {
		
		// NOTE: if ciphers used for ngraph stats, use first 100,000.  otherwise, sample from an offset of at least 100,000.
//		int jump = 0;
//		int max = 10000;
//		generateFrom(
//				"/Users/doranchak/projects/zodiac/com.zodiackillerciphers.corpus.symposium2019.GeneratePlaintexts.4.txt",
//				80000 + max*jump /*offset*/, max /*max*/, true /*withstats*/);

//		addStats();
		for (int i : new int[] {0,1,3,4,7,8}) {
			System.out.println("===== stats "+ i);
			dumpZ408Z340Stats(i);
		}
//		testSnake();

				//		toNumeric("/Users/doranchak/projects/deep-learning-ec2/symposium2019/100,000-ciphers-with-stats-04.csv", 2);

//		double[][] iocColumns = new double[17][4]; // ngraphic iocs, n=[1,4]
//		double[][] iocRows = new double[20][4]; // ngraphic iocs, n=[1,4]
		
//		StringBuilder sb = new StringBuilder();
//		for (int col=0; col<17;col++) {
//			for (int n=0; n<4; n++) {
//				sb.append(", iocColumns[" + col + "][" + n + "]");
//			}
//		}
//		System.out.println(sb);
//		sb = new StringBuilder();
//		for (int row=0; row<20;row++) {
//			for (int n=0; n<4; n++) {
//				sb.append(", iocRows[" + row + "][" + n + "]");
//			}
//		}
//		System.out.println(sb);
		
		
		// double[4][2][3]
//		for (int i=0; i<4; i++) {
//			for (int j=0; j<2; j++) {
//				for (int k=0; k<3; k++) {
//					sb.append("iocsSnakes[" + i + "][" + j + "][" + k + "], ");
//				}
//			}
//		}
//		System.out.println(sb);
		
//		System.out.println(CipherTransformations.flipHorizontal(Ciphers.Z340, 20, 17));
//		System.out.println(Diagonal.transform(Ciphers.Z340, 17, 0)); 
//		System.out.println(Diagonal.transform(Ciphers.Z340, 17, 1)); 
//		System.out.println(Diagonal.transform(Ciphers.Z340, 17, 2)); 
//		System.out.println(Diagonal.transform(Ciphers.Z340, 17, 3)); 
		
//		testStats();
	}
}
