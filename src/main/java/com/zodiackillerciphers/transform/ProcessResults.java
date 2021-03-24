package com.zodiackillerciphers.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.generator.CandidateKey;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.dictionary.WordFrequencies.WordBean;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.transform.operations.Swap;

public class ProcessResults {
	public static void process() {
		String[] files = new String[] {
				"/Users/doranchak/projects/zodiac/transform-uniq.txt"
		};
		
		double[] best = new double[8];
		for (int i=0; i<best.length; i++) best[i] = Float.MAX_VALUE;
		List<Operations> front = new ArrayList<Operations>(); 

		for (String path : files) {
			System.out.println("======== Processing " + path);
			BufferedReader input = null;
			int counter = 0;
			try {
				input = new BufferedReader(new FileReader(new File(path)));
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					//if (line.contains("archive size")) {
						int a = line.lastIndexOf("[")+1;
						int b = line.lastIndexOf("]");
						String genomeStr = line.substring(a,b).replaceAll(",","");
						String[] genomeStrArray = genomeStr.split(" ");
						Operations o = new Operations();
						o.genome = new float[genomeStrArray.length];
						for (int i=0; i<genomeStrArray.length; i++) {
							o.genome[i] = Float.valueOf(genomeStrArray[i]);
						}
						o.performTransformations(false, 100);
						double[] obj = o.measure(o.outputString);
						o.lastObjectives = obj;
						
						for (int i=0; i<obj.length; i++) {
							if (obj[i] < best[i]) {
								best[i] = obj[i];
								System.out.println("NEW BEST " + i + ": " + o);
							}
						}
						
						// update pareto front
						boolean dominated = false;
						boolean dominator = false;
						List<Operations> forRemoval = new ArrayList<Operations>();
						for (Operations frontMember : front) {
							// case 1) front member is dominated by current op
							if (CandidateKey.isDominatedBy(frontMember.lastObjectives, o.lastObjectives)) {
								forRemoval.add(frontMember);
								dominator = true;
							} else if (CandidateKey.isDominatedBy(o.lastObjectives, frontMember.lastObjectives)) {
								// case 2) front member dominates current op
								dominated = true;
								break;
							} else if (CandidateKey.equal(o.lastObjectives, frontMember.lastObjectives)) {
								// case 3) member has equal objectives as op
								// treat it as if it were dominated
								dominated = true;
								break;
							}
						}
						for (Operations op : forRemoval) {
							front.remove(op);
							System.out.println("Removed " + op);
						}
						if (!dominated) {
							front.add(o); // if we got here, o is nondominated.
							String which = dominator ? "dominator" : "non-dominated"; 
							System.out.println("Counter " + counter + " Front size " + front.size() + ", New " + which + ": " + o);
						}
						counter++;
					}
					
				//}
				//System.out.println("read " + counter + " lines.");
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
		
		System.out.println("Final front size: " + front.size());
		for (Operations op : front) {
			System.out.println("Front member: " + op);
		}
	}
	
	public static void uniq() {
		Set<String> genomes = new HashSet<String>();
		String path = "/Users/doranchak/projects/zodiac/1";
		BufferedReader input = null;
		int counter = 0;

		int hits = 0;
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				counter++;
				int a = line.lastIndexOf("[") + 1;
				int b = line.lastIndexOf("]");
				String genomeStr = line.substring(a, b).replaceAll(",", "");
				if (genomes.contains(genomeStr)) {
					hits++;
					continue;
				}
				genomes.add(genomeStr);
				System.out.println(line);
				if (counter % 1000 == 0) {
					System.out.println("hits " + hits + " total " + counter);
				}
			}
			// System.out.println("read " + counter + " lines.");
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
	public static void front() {
		Set<String> genomes = new HashSet<String>();
		String path = "/Users/doranchak/projects/zodiac/transform-pareto.txt";
		BufferedReader input = null;
		int counter = 0;

		Set<String> front = new HashSet<String>();
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("Counter")) {
					front.add(line.substring(line.indexOf(":")+2));
				} else if (line.startsWith("Removed")) {
					front.remove(line.substring(line.indexOf("Removed")+8));
				}
			}
			// System.out.println("read " + counter + " lines.");
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
		
		for (String str : front) {
			String line = "";
			String[] split = str.replaceAll(",","").replaceAll("\\[","").replaceAll("\\]","").split(" ");
			for (int i=0; i<8; i++) {
				line += split[i] + "	";
			}

			int a = str.lastIndexOf("[") + 1;
			int b = str.lastIndexOf("]");
			String genomeStr = str.substring(a, b);
			int c = str.indexOf("]")+2;
			
			line += str.substring(c,a-2) + "	";
			line += genomeStr + "	" + str.substring(b+2);
			
			System.out.println(line);
		}

	}
	public static void jarlve() {
		Set<String> genomes = new HashSet<String>();
		String path = "/Users/doranchak/projects/zodiac/transform-pareto-tab.txt";
		BufferedReader input = null;
		int counter = 0;

		Set<String> front = new HashSet<String>();
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String[] split = line.split("	");
				String info = split[8];
				String cipher = split[10];
				String rev = new StringBuffer(cipher).reverse().toString();
				
				System.out.println("cipher_information=" + info);
				TransformationBase.dump(TransformationBase.toList(cipher, 17));
				System.out.println();
				System.out.println("cipher_information=" + info + " (reverse)");
				TransformationBase.dump(TransformationBase.toList(rev, 17));
				System.out.println();
			}
			// System.out.println("read " + counter + " lines.");
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
		
		for (String str : front) {
			String line = "";
			String[] split = str.replaceAll(",","").replaceAll("\\[","").replaceAll("\\]","").split(" ");
			for (int i=0; i<8; i++) {
				line += split[i] + "	";
			}

			int a = str.lastIndexOf("[") + 1;
			int b = str.lastIndexOf("]");
			String genomeStr = str.substring(a, b);
			int c = str.indexOf("]")+2;
			
			line += str.substring(c,a-2) + "	";
			line += genomeStr + "	" + str.substring(b+2);
			
			System.out.println(line);
		}

	}
	
	/** process archive from the given generation.  turn into input file for azdecrypt. */
	public static void jarlve2(String path, int gen) {
		Set<String> genomes = new HashSet<String>();
		BufferedReader input = null;
		int counter = 0;

		Set<String> front = new HashSet<String>();
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (!line.startsWith(gen + " archive size")) continue;
				
				int a = line.indexOf("]")+2;
				int b = line.lastIndexOf("[")-1;
				int c = line.lastIndexOf("]")+2;
				String info = line.substring(a,b);
				String cipher = line.substring(c).replaceAll(" ","");
				//if (cipher.length() < 100) continue;
				String rev = new StringBuffer(cipher).reverse().toString();
				
				System.out.println("cipher_information=" + info);
				TransformationBase.dump(TransformationBase.toList(cipher, 17));
				System.out.println();
				System.out.println("cipher_information=" + info + " (reverse)");
				TransformationBase.dump(TransformationBase.toList(rev, 17));
				System.out.println();
			}
			// System.out.println("read " + counter + " lines.");
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
	public static void jarlveResults() {
		/** map azdecrypto results to scores.
		 * key: operations (including reversals)
		 * value: int[] {score, ioc}
		 */
		Map<String, int[]> scores = new HashMap<String, int[]>();
		String path = "/Users/doranchak/projects/zodiac/transform-pareto-tab.txt";
		String pathAzResults = "/Users/doranchak/Downloads/az099/Results/with_meta_information_transform_1_through_7_pareto";
		BufferedReader input = null;
		int counter = 0;

		
		File dir = new File(pathAzResults);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".txt")) {
				if (file.getName().startsWith("group")) continue;
				if (file.getName().equals("log.txt")) continue;
				if (file.getName().equals("stats.txt")) continue;
				if (file.getName().equals("1.txt")) continue;
				System.out.println(file);
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				String key = lines.get(0);
				if (key.contains("Score: ")) continue;
				String scoresStr = lines.get(2);
				String[] split = scoresStr.split(" ");
				int[] s = new int[] {Integer.valueOf(split[1]), Integer.valueOf(split[3])};
				if (scores.get(key) != null) {
					System.out.println("dupe key " + key + " val " + Arrays.toString(scores.get(key)));
				} else scores.put(key, s);
			}
		}
		
		
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String[] split = line.split("	");
				String info = split[8];
				int[] s = scores.get(info);
				String key = info + " (reverse)";
				int[] sRev = scores.get(key);
				if (s == null) { 
					System.out.println("no scores for key [" + key + "]");
					s = new int[] {0,0};
				}
				if (sRev == null) { 
					System.out.println("no scores (reverse) for key [" + key + "]");
					sRev = new int[] {0,0};
				}
				System.out.println(line + "	" + s[0] + "	" + s[1] + "	" + sRev[0] + "	" + sRev[1]);
			}
			// System.out.println("read " + counter + " lines.");
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

	public static void jarlveResults(String pathAzResults, String pathExperiment, int gen, boolean removeCompositeFitness) {
		/** map azdecrypto results to scores.
		 * key: operations (including reversals)
		 * value: int[] {score, ioc}
		 */
		Map<String, int[]> scores = new HashMap<String, int[]>();
		BufferedReader input = null;
		int counter = 0;

		
		File dir = new File(pathAzResults);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".txt")) {
				if (file.getName().startsWith("group")) continue;
				if (file.getName().equals("log.txt")) continue;
				if (file.getName().equals("stats.txt")) continue;
				if (file.getName().equals("1.txt")) continue;
				//System.out.println(file);
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				String line = lines.get(0);
				if (line.contains("Score: ")) continue;
				String key = line.substring(line.indexOf(" ")+1);
				String scoresStr = lines.get(2);
				String[] split = scoresStr.split(" ");
				int[] s = new int[] {Integer.valueOf(split[1]), Integer.valueOf(split[3])};
				if (scores.get(key) != null) {
					System.out.println("dupe key " + key + " val " + Arrays.toString(scores.get(key)));
				} else scores.put(key, s);
			}
		}
		

		counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(pathExperiment)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (!line.startsWith(gen + " archive size")) continue;
				
				int a = line.indexOf("]")+2;
				int b = line.lastIndexOf("[")-1;
				int c = line.lastIndexOf("]")+2;
				String info = line.substring(a,b);
				if (removeCompositeFitness) info = info.substring(info.indexOf(" ")+1);
				
				int[] s = scores.get(info);
				String key = info + " (reverse)";
				int[] sRev = scores.get(key);
				if (s == null) { 
					System.out.println("no scores for key [" + key + "]");
					s = new int[] {0,0};
				}
				if (sRev == null) { 
					System.out.println("no scores (reverse) for key [" + key + "]");
					sRev = new int[] {0,0};
				}
				//System.out.println(line + "	" + s[0] + "	" + s[1] + "	" + sRev[0] + "	" + sRev[1]);
				System.out.println(s[0] + "	" + s[1] + "	" + sRev[0] + "	" + sRev[1]);
				
				
			}
			// System.out.println("read " + counter + " lines.");
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
	
	public static void findWordsInAzResults() {
		/** map azdecrypto results to scores.
		 * key: operations (including reversals)
		 * value: int[] {score, ioc}
		 */
		//String pathAzResults = "/Users/doranchak/Downloads/az099/Results/with_meta_information_transform_1_through_7_pareto";
		String pathAzResults = "/Users/doranchak/Downloads/AZdecrypt0992c/Results/smokie33_choose3";
		BufferedReader input = null;

		
		/** maximum azscore per word */
		Map<String, Float> maxScore = new HashMap<String, Float>();
		/** word counts */
		Map<String, Integer> counts = new HashMap<String, Integer>();
		
		File dir = new File(pathAzResults);
		File[] files = dir.listFiles();
		int count = 0;
		for (File file : files) {
			if (file.getName().endsWith(".txt")) {
				if (file.getName().startsWith("group")) continue;
				if (file.getName().equals("log.txt")) continue;
				if (file.getName().equals("stats.txt")) continue;
				if (file.getName().equals("1.txt")) continue;
				System.out.println(file + " " + (++count));
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				String first = lines.get(0);
				if (first.contains("Score: ")) continue;
				String scoresStr = lines.get(2);
				String[] split = scoresStr.split(" ");
				float s = Float.valueOf(split[1]);
				
				String plaintext = ""; int i=4;
				while (true) {
					String line = lines.get(i++);
					if (line.length() == 0) break;
					plaintext += line;
				}
				
				List<WordBean> beans = WordFrequencies.findAllWordsIn(plaintext, 4, false);
				for (WordBean bean : beans) {
					String key = bean.word;
					Float val1 = maxScore.get(key);
					Integer val2 = counts.get(key);
					if (val1 == null) val1 = 0f;
					if (val2 == null) val2 = 0;
					val1 = Math.max(val1, s);
					val2++;
					maxScore.put(key, val1);
					counts.put(key,  val2);
				}
			}
		}
		
		for (String key : counts.keySet()) {
			System.out.println(key.length() + " " + WordFrequencies.freq(key) + " " + key + " " + maxScore.get(key) + " " + counts.get(key));
		}

	}

	
	public static void swaps() {
		String path = "/Users/doranchak/projects/zodiac/transform-all-swaps-n6.txt";
		String cipher = "H+M8|CV@Kz/JNbVM)|DR(UVFFz9<Ut*5cZG+kNpOGp+2|G++|TB4-R)Wk^D(+4(5J+JYM(+|TC7zPYLR/8KjROp+8y.LWBO|<z29^%OF7TBlXz6PYALKJp+l2_cFKzF*K<SBKG)y7t-cYAy29^4OFT-+dpclddG+4Ucy5C^W(cMEB+*5k.L-RR+4>f|pFH>#Z3P>Ldl5||.UqL+dpl%WO&D(MVE5FV52cW<SVW)+k#2b^D4ct+c+ztZk.#Kp+fZ+B.;+B31c_81*H_Rq#2pb&RG1BCOO|TfSMF;+B<MF6N:(+H*;2BpzOUNyBO<Sf9pl/CN:^j*Xz6-+l#2E.B)>";
		List<StringBuffer> list = TransformationBase.toList(cipher, 17);
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.contains("out of")) continue;
				String[] split = line.split(" ");
				Integer pos1 = Integer.valueOf(split[1]);
				Integer pos2 = Integer.valueOf(split[2]);
				if (pos1 > pos2) continue;
				Integer height = Integer.valueOf(split[3]);
				Integer width = Integer.valueOf(split[4]);
				Swap swap = new Swap(list, pos1, pos2, height, width);
				swap.execute();
				Operations o = new Operations();
				String m = Arrays.toString(o.measure(TransformationBase.fromList(swap.getOutput()).toString()));
				m = m.replaceAll("\\[","").replaceAll("\\]","").replaceAll(",","");
				m = line + " " + m;
				m = m.replaceAll(" ","	");
				System.out.println(m);
			}
			// System.out.println("read " + counter + " lines.");
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
	
	/** process archive from the given generation.  measure everything and output results in spreadsheet-friendly format. */
	public static void measure(String path, int gen, boolean removeCompositeFitness) {
		Set<String> genomes = new HashSet<String>();
		BufferedReader input = null;
		int counter = 0;
		String tab = "	";

		Set<String> front = new HashSet<String>();
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (!line.startsWith(gen + " archive size")) continue;
				counter++;
				int a = line.indexOf("]")+2;
				int b = line.lastIndexOf("[")-1;
				int c = line.lastIndexOf("]")+2;
				String info = line.substring(a,b);
				if (removeCompositeFitness) info = info.substring(info.indexOf(' ')+1); // take out the compositeFitness
				String cipher = line.substring(c).replaceAll(" ","");
				//if (cipher.length() < 100) continue;
				String genome = line.substring(b+2, c-3);
				MeasurementsBean bean = MeasurementsBean.measure(cipher, null);
				bean.ciphertext = cipher;
				bean.ops = info;
				bean.genome = genome;
				System.out.println(bean);
				/*
				System.out.println("cipher_information=" + info);
				TransformationBase.dump(TransformationBase.toList(cipher, 17));
				System.out.println();
				System.out.println("cipher_information=" + info + " (reverse)");
				TransformationBase.dump(TransformationBase.toList(rev, 17));
				System.out.println();*/
			}
			// System.out.println("read " + counter + " lines.");
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
		
		System.out.println("DONE MEASURING " + path);
	}
	
	
	public static void main(String[] args) {
		
		//process();
		//uniq();
		//front();
		//jarlve();
		//jarlveResults();
		findWordsInAzResults();
		//swaps();
		//jarlve2("/Users/doranchak/projects/zodiac/log-transform-7", 51300);
		/*jarlveResults(
				"/Users/doranchak/Downloads/az099/Results/with_meta_information_transform_9",
				"/Users/doranchak/projects/zodiac/log-transform-9", 26100, true);*/
		//measure("/Users/doranchak/projects/zodiac/log-transform-8", 51300, false);
	}
}
