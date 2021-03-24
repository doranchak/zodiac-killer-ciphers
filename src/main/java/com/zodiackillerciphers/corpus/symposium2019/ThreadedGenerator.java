package com.zodiackillerciphers.corpus.symposium2019;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.types.Columnar;
import com.zodiackillerciphers.corpus.symposium2019.types.Homophonic;
import com.zodiackillerciphers.corpus.symposium2019.types.OneTypeVsBag;
import com.zodiackillerciphers.corpus.symposium2019.types.Route;

public class ThreadedGenerator {
	static String ptpath = "/Users/doranchak/projects/zodiac/com.zodiackillerciphers.corpus.symposium2019.GeneratePlaintexts.4.txt";
	public static String base = "com.zodiackillerciphers.corpus.symposium2019.types.";
	/** generate the given number of ciphers, divided among the given number of threads.
	 * write the output to the given path.
	 * plaintexts are taken from the given path to file of plaintexts. 
	 */
	public static void generate(String cipherClassName, String plaintextPath, int offset, int numCiphers, int numThreads, String outputPath, boolean skipHomophonic) {
		Long timeStart = new Date().getTime();
		// load all the plaintexts
		List<String> plaintextsAll = plaintextsFrom(plaintextPath, offset, numCiphers);
		// create threads
		
		CipherBase[] threads = new CipherBase[numThreads];
		for (int i=0; i<numThreads; i++) {
			try {
				Class<?> clazz = Class.forName(cipherClassName);
				Constructor<?> ctor = clazz.getConstructor();
				Object object = ctor.newInstance();		
				threads[i] = (CipherBase) object;
				threads[i].skipHomophonic = skipHomophonic;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		// divide up the plaintext evenly
		int index = 0;
		for (String pt : plaintextsAll) {
			threads[(index++)%threads.length].addPlaintext(pt);
		}
		
		// kick off threads
		for (int i=0; i<numThreads; i++) {
			threads[i].start();
		}

		// wait for threads to finish
		for (int i=0; i<numThreads; i++) {
			try {
				threads[i].join();
				System.out.println("Thread " + i + " (id " + threads[i].threadNum() + ") done.");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// generate the output
		try {
			File fout = new File(outputPath);
			FileOutputStream fos = new FileOutputStream(fout);
 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
 
			for (CipherBase cb : threads) {
				for (int i=0; i<cb.getCiphers().size(); i++) {
					String cipher = cb.getCiphers().get(i);
					String pt = cb.getPlaintexts().get(i);
					double[] stats = cb.getStats().get(i);
					StringBuilder sb = new StringBuilder();
					sb.append(cipher);
					sb.append(",");
					sb.append(pt);
					
					for (double stat : stats) {
						sb.append(",");
						if (cb instanceof OneTypeVsBag) {
							sb.append((int)stat);
						} else {
							sb.append(stat);
						}
					}
					bw.write(sb.toString());
					bw.newLine();
					
				}
				
			}
 
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Long timeEnd = new Date().getTime();
		System.out.println("FINISHED IN " + (timeEnd-timeStart) + " MS");
		
	}
	
	public static List<String> plaintextsFrom(String plaintextPath, int offset, int num) {
		List<String> list = new ArrayList<String>();
		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(plaintextPath), "UTF-8"));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (counter >= offset) {
					String[] split = line.split("	");
					String pt = split[4];
					list.add(pt);
				}
				counter++;
				if (list.size() == num)
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
		return list;
	}
	
	static int randomOffset() {
		Random rand = new Random();
		return rand.nextInt(930000);
	}
	
	// TODO
	static void testHomVsColumnarStats() {
		Homophonic hom = new Homophonic();
		String homCipher = hom.makeCipher(Ciphers.Z408_SOLUTION.substring(0,340));
		Columnar col = new Columnar();
		col.KEY_LENGTH_OVERRIDE = 5;
		String colCipher = col.makeCipher(Ciphers.Z408_SOLUTION.substring(0,340));
		System.out.println("hom: " + homCipher);
		System.out.println("col: " + colCipher);
		System.out.println("col key: " + Arrays.toString(col.key));
		
		// 
		
	}
	
	static void generate1() {
	//		generate(base + "SnakePermutation", ptpath, /*offset*/ 90000, /*numCiphers*/ 10000, /*numThreads*/ 10,
	//		"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/snake-permutation.csv");
	//generate(base + "Homophonic", ptpath, /*offset*/ 100000, /*numCiphers*/ 10000, /*numThreads*/ 10,
	//		"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/homophonic3.csv");
	//generate(base + "Snake", ptpath, /*offset*/ 110000, /*numCiphers*/ 10000, /*numThreads*/ 10,
	//		"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/snake-after-bugfix2.csv");
	//generate(base + "Permutation", ptpath, /*offset*/ 120000, /*numCiphers*/ 10000, /*numThreads*/ 10,
	//		"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/permutation2.csv");
	//generate(base + "Route", ptpath, /*offset*/ 140000, /*numCiphers*/ 10000, /*numThreads*/ 10,
	//		"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-untransposed.csv");
	}
	
	static void generateRoutes() {
//		Route.BOTH_STEPS = false;
//		Route.RECTANGLE = false;
//		
//		Route.CORNER = 0;
//		Route.DIRECTION_ROW = false;
//		generate(base + "Route", ptpath, /*offset*/ 150000, /*numCiphers*/ 10000, /*numThreads*/ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-0-false.csv");
//		Route.DIRECTION_ROW = true;
//		generate(base + "Route", ptpath, /*offset*/ 160000, /*numCiphers*/ 10000, /*numThreads*/ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-0-true.csv");
//		Route.CORNER = 1;
//		Route.DIRECTION_ROW = false;
//		generate(base + "Route", ptpath, /*offset*/ 170000, /*numCiphers*/ 10000, /*numThreads*/ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-1-false.csv");
//		Route.DIRECTION_ROW = true;
//		generate(base + "Route", ptpath, /*offset*/ 180000, /*numCiphers*/ 10000, /*numThreads*/ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-1-true.csv");
//		Route.CORNER = 2;
//		Route.DIRECTION_ROW = false;
//		generate(base + "Route", ptpath, /*offset*/ 190000, /*numCiphers*/ 10000, /*numThreads*/ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-2-false.csv");
//		Route.DIRECTION_ROW = true;
//		generate(base + "Route", ptpath, /*offset*/ 200000, /*numCiphers*/ 10000, /*numThreads*/ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-2-true.csv");
//		Route.CORNER = 3;
//		Route.DIRECTION_ROW = false;
//		generate(base + "Route", ptpath, /*offset*/ 210000, /*numCiphers*/ 10000, /*numThreads*/ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-3-false.csv");
//		Route.DIRECTION_ROW = true;
//		generate(base + "Route", ptpath, /*offset*/ 220000, /*numCiphers*/ 10000, /*numThreads*/ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-3-true.csv");
	}
	
	static void generateRoutesByWidth() {
		Route.BOTH_STEPS = false;
		Route.RECTANGLE = false;		
		Route.CORNER = 1;
		Route.DIRECTION_ROW = true;
		
		for (int W=2; W<171; W++) {
			Route.WIDTH = W;
			generate(base + "Route", ptpath, /*offset*/ (230000 + 10000*(W-2)) % 1000000, /*numCiphers*/ 10000, /*numThreads*/ 10,
			"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route-1-true-width-" + W + ".csv", false);
			
		}
	}

	static void generateRoutesEachType() {
		CipherGenerator.WHICH_STATS = 1;
		Route.BOTH_STEPS = false;
		Route.RECTANGLE = false;

		int start = 30000;
		for (int corner = 0; corner < 4; corner++) {
			for (boolean directionRow : new boolean[] { false, true }) {
				Route.CORNER = corner;
				Route.DIRECTION_ROW = directionRow;
				generate(base + "Route", ptpath, /* offset */ start, /* numCiphers */ 10000, /* numThreads */ 10,
						"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route2-" + corner
								+ "-" + directionRow + ".csv", false);
				start += 10000;
			}
		}
	}
	
	static void generateRoutesWithWidthOutput() {
		Route.BOTH_STEPS = false;
		Route.RECTANGLE = false;		
		Route.CORNER = 1;
		Route.DIRECTION_ROW = true;
		
		// homophonic with new untransposed route iocs
		CipherGenerator.WHICH_STATS = 1;
		Route.OUTPUT_WIDTH = true;
//		generate(base + "Homophonic", ptpath, /* offset */ 10000, /* numCiphers */ 10000, /* numThreads */ 10,
//				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/homophonic4.csv");		
		// routes with new untransposed route iocs
		generate(base + "Route", ptpath, /* offset */ 20000, /* numCiphers */ 10000, /* numThreads */ 10,
				"//Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-4/route2-1-true-with-width-output.csv", false);		
	}
	
	static void generateScytale(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Scytale", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/scytale-stats" + whichStats + ".csv", false);		
	}
	static void generateScytaleWithKey(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "ScytaleWithKey", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/scytale-with-key-stats" + whichStats + ".csv", false);		
	}
	static void generateColumnar(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Columnar", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/columnar-stats" + whichStats + ".csv", false);		
	}
	static void generateColumnarWithKeyLength(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		Columnar.WITH_KEY_LENGTH = true;
		generate(base + "Columnar", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/columnar-with-keylength.csv", false);		
	}
	static void generateDiagonal(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Diagonal", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/diagonal-stats" + whichStats + ".csv", false);		
	}
	static void generateGibberish(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Gibberish", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/gibberish-stats" + whichStats + ".csv", false);		
	}
	static void generatePermutation(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Permutation", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/permutation-stats" + whichStats + ".csv", false);		
	}
	static void generateQuadrants(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Quadrants", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/quadrants-stats" + whichStats + ".csv", false);		
	}
	static void generateRailFence(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "RailFence", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/railfence-stats" + whichStats + ".csv", false);		
	}
	static void generateRouteTwoSteps(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Route", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/route-two-steps-stats" + whichStats + ".csv", false);		
	}
	static void generateScytalePeriod19(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "ScytalePeriod19", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/scytale-period-19-stats" + whichStats + ".csv", false);		
	}
	static void generateSpiral(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Spiral", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/spiral-stats" + whichStats + ".csv", false);		
	}
	static void generateSpiralAnyWidth(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "SpiralAnyWidth", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/spiral-any-width-stats" + whichStats + ".csv", false);		
	}
	static void generateSnake(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Snake", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/snake-stats" + whichStats + ".csv", false);		
	}
	static void generateVigenere(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Vigenere", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/vigenere-stats" + whichStats + ".csv", false);		
	}
	static void generateHomophonic(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "Homophonic", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/homophonic-stats" + whichStats + ".csv", false);		
	}
	static void generateLRoute(int whichStats, String folder, int quantity) {
		CipherGenerator.WHICH_STATS = whichStats;
		generate(base + "LRoute", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/lroute-stats" + whichStats + ".csv", false);		
	}
	
	static void generateCipherSet(int whichStats, String folder, int quantity) {
//		generateColumnar(whichStats, folder, quantity);
		generateColumnarWithKeyLength(whichStats, folder, quantity);
//		generateDiagonal(whichStats, folder, quantity);
//		generateGibberish(whichStats, folder, quantity);
//		generateHomophonic(whichStats, folder, quantity);
//		generatePermutation(whichStats, folder, quantity);
//		generateQuadrants(whichStats, folder, quantity);
//		generateRailFence(whichStats, folder, quantity);
//		generateRouteTwoSteps(whichStats, folder, quantity);
//		generateScytale(whichStats, folder, quantity);
		//generateScytalePeriod19(whichStats, folder, quantity);
		//generateScytaleWithKey(whichStats, folder, quantity);
//		generateSnake(whichStats, folder, quantity);
//		generateSpiral(whichStats, folder, quantity);
//		generateSpiralAnyWidth(whichStats, folder, quantity);
//		generateVigenere(whichStats, folder, quantity);
//		generateLRoute(whichStats, folder, quantity);
	}
	static void generateOneTypeVsBag(String folder, int quantity) {
		generate(base + "OneTypeVsBag", ptpath, /* offset */ randomOffset(), /* numCiphers */ quantity, /* numThreads */ 10,
				folder + "/columnar-vs-homophonic--200,000.csv", false);		
	}
	
	public static void main(String[] args) {
		//generateRoutesEachType();
//		generateRoutesWithWidthOutput();
//		generateScytale();
//		generateScytaleWithKey();
//		generateColumnar();
//		generateDiagonal();
//		generateGibberish();
//		generatePermutation();
//		generateQuadrants();
//		generateRailFence();
//		generateRouteTwoSteps();
//		generateScytalePeriod19();
//		generateSpiral();
//		generateVigenere();
		
//		for (int which : new int[] {0, 1, 3, 4, 7, 8})
		int batch = 15;
		
		while (true) {
			for (int which : new int[] {0})
				generateCipherSet(which, "/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-" + (batch++), 1000);
		}
//		generateOneTypeVsBag("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ciphers-batch-13", 200000);
	}
	
}
