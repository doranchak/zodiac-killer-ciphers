package com.zodiackillerciphers.ciphers.generator;

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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;

public class LogProcessor {
	
	/** look for best scores */
	public static void process(String file) {
		float best1 = Float.MAX_VALUE;
		String best1Line; // objective 1
		float best2 = Float.MAX_VALUE;
		String best2Line; // objective 2
		float bestall = Float.MAX_VALUE;
		String bestallLine; // composite objective

		
		
		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				counter++;
				if (line.startsWith("generation ")) {
					String[] split = line.split(" ");
					int gen = Integer.valueOf(split[1]);
					float o1 = Float.valueOf(split[3]);
					float o2 = Float.valueOf(split[4]);
					float oall = o1*o2;
					
					if (o1 < best1) {
						System.out.println("NEW BEST1 " + line);
						best1 = o1;
						best1Line = line;
					}
					if (o2 < best2) {
						System.out.println("NEW BEST2 " + line);
						best2 = o2;
						best2Line = line;
					}
					if (oall < bestall) {
						System.out.println("NEW BEST ALL " + line);
						bestall = oall;
						bestallLine = line;
					}
					if (oall < 6) {
						System.out.println("GOOD SOLUTION " + line);
					}
				}
			}
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
	public static void process2(String file) {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("generation ")) {
					String[] split = line.split(" ");
					double o1 = Double.valueOf(split[3]);
					double o2 = Double.valueOf(split[4]);
					double o3 = Double.valueOf(split[5]);
					double o4 = Double.valueOf(split[6]);
					double oall = (1+o1)*(1+o2)*(1+o3)*(1+o4);
					if (oall >= Float.MAX_VALUE) continue;
					System.out.println(oall + " " + o1 + " " + o2 + " " + o3 + " " + o4);
				}
			}
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
	/** dump min value of composite fitness per generation */
	public static void process4(String file) {
		BufferedReader input = null;
		
		Map<Integer, Float> mins = new HashMap<Integer, Float>(); 
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.contains("archive size")) {
					String[] split = line.split(" ");
					int gen = Integer.valueOf(split[0]);
					float val = Float.valueOf(split[6]);
					Float min = mins.get(gen);
					if (min == null) min = val;
					else min = Math.min(min, val);
					mins.put(gen, min);
				}
			}
			
			for (Integer key : mins.keySet())
				System.out.println(key + " " + mins.get(key));
			
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
	
	/** look for archive members that are dominated by any from previous generations */
	public static void process3(String file, int gensBetweenDumps) {
		BufferedReader input = null;
		
		Map<Integer, List<double[]>> archives = new HashMap<Integer, List<double[]>>();
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("generation ") && line.contains("ARCHIVE")) {
					String[] split = line.split(" ");
					double[] archive = new double[6];
					int gen = Integer.valueOf(split[1]);
					archive[0] = Double.valueOf(split[3]);
					archive[1] = Double.valueOf(split[4]);
					archive[2] = Double.valueOf(split[5]);
					archive[3] = Double.valueOf(split[6]);
					archive[4] = Double.valueOf(split[7]);
					archive[5] = Double.valueOf(split[8]);
					
					List<double[]> val = archives.get(gen);
					if (val == null) val = new ArrayList<double[]>(); 
					val.add(archive);
					archives.put(gen, val);
					if (gen == 199999) break;
				}
			}
			
			for (Integer gen1 : archives.keySet()) {
				for (Integer gen2 : archives.keySet()) {
					if (gen2 < gen1) continue;
					// gen2 is after gen1
					// does an individual in gen1 dominate all individuals from gen2?
					for (double[] individual1 : archives.get(gen1)) {
						boolean dominates = true;
						for (double[] individual2 : archives.get(gen2)) {
							if (CandidateKey.isDominatedBy(individual2, individual1)) {
							} else {
								dominates = false;
								break;
							}
						}
						if (dominates) {
							System.out.println("gen " + gen1 + " individual " + Arrays.toString((individual1)) + " dominates all individuals from gen " + gen2);
						}
					}
				}
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
	
	/** extract ciphers/plaintext from log for use in test ciphers spreadsheet */
	public static void processForCiphersCollection(String file, int experiment, int generation) {
		BufferedReader input = null;
		int num = 1;
		Set<String> seen = new HashSet<String>();
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("generation " + generation + " ")) {
					String plain = line.substring(line.indexOf("plaintext ["), line.indexOf("]] errors ") + 2);
					String cipher = line.substring(line.indexOf("cipher [")+8, line.indexOf("] p2c: "));
					if (seen.contains(cipher)) continue;
					seen.add(cipher);
					System.out.println("new Cipher(\"doranchak multiobjective evolution experiment " + experiment + " generation " + generation + " cipher " + num++ + "\",\"" + cipher + "\", \"" + plain + "\", null, true),");
				}
			}
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

	/** extract ciphers from fragment ioc transposition explorer experiments.
	 * output is used for spreadsheet and for azdecrypt
	 *  */
	public static void processForFragmentIoc(String file, int experiment, int generation) {
		BufferedReader input = null;
		int num = 1;
		Set<String> seen = new HashSet<String>();
		
		List<String> forAzdecrypt = new ArrayList<String>();
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("generation " + generation + " ")) {
					
					
					String objectives = line.substring(line.indexOf("objectives [")+12, line.indexOf("] comp "));
					String[] split = objectives.replaceAll(",","").split(" ");
					double ioc = Double.valueOf(split[0]);
					int n2 = Double.valueOf(split[1]).intValue();
					double cs1 = Double.valueOf(split[2]);
					double cs2 = Double.valueOf(split[3]);
					//double cs2 = 0;
					int ops = 6-Double.valueOf(split[4]).intValue();
					String operations = line.substring(line.indexOf("] operations ")+13, line.indexOf(" genome ["));
					String genome = line.substring(line.indexOf(" genome [")+9, line.indexOf("] cipher "));
					String cipher = line.substring(line.indexOf("] cipher ")+9);
					if (seen.contains(cipher)) continue;
					seen.add(cipher);
					String tab = "	";
					System.out.println(experiment + tab + generation + tab + num + tab + operations + tab + genome + tab + "=(\"" + cipher + "\")" + tab + ioc + tab + n2 + tab + cs1 + tab + cs2 + tab + ops);
					//NGramsBean bean = new NGramsBean(2, cipher);
					//System.out.println(bean.numRepeats());
					
					forAzdecrypt.add("cipher_information=" + num + ": " + operations);
					List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
					for (StringBuffer sb : grid) forAzdecrypt.add(sb.toString());
					forAzdecrypt.add("");

					forAzdecrypt.add("cipher_information=" + num + ": " + operations + " (reversed)");
					StringBuffer rev = new StringBuffer(cipher);
					rev.reverse();
					grid = TransformationBase.toList(rev.toString(), 17);
					for (StringBuffer sb : grid) forAzdecrypt.add(sb.toString());
					forAzdecrypt.add("");
					
					num++;
				}
			}
			for (String s : forAzdecrypt) System.out.println(s);
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
	public static void processForColumnar(String file, int experiment, int generation) {
		BufferedReader input = null;
		int num = 1;
		Set<String> seen = new HashSet<String>();
		
		List<String> forAzdecrypt = new ArrayList<String>();
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("generation " + generation + " ")) {
					String info = line.substring(line.indexOf(" n "), line.indexOf("]"));
					String chunk = line.substring(line.indexOf(" genome [")+9);
					String genome = chunk.substring(0, chunk.indexOf("] "));
					String[] split = chunk.split(" ");
					String cipher = split[split.length-6];
					if (seen.contains(cipher)) continue;
					seen.add(cipher);
					String tab = "	";
					System.out.println("columnar" + experiment + tab + generation + tab + num + tab + info + tab + genome + tab + "=(\"" + cipher + "\")");
					//NGramsBean bean = new NGramsBean(2, cipher);
					//System.out.println(bean.numRepeats());
					System.out.println(Arrays.toString(com.zodiackillerciphers.transform.columnar.Evolve.objectives(cipher, 0)));
					
					forAzdecrypt.add("cipher_information=" + num + ": " + info);
					List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
					for (StringBuffer sb : grid) forAzdecrypt.add(sb.toString());
					forAzdecrypt.add("");

					forAzdecrypt.add("cipher_information=" + num + ": " + info + " (reversed)");
					StringBuffer rev = new StringBuffer(cipher);
					rev.reverse();
					grid = TransformationBase.toList(rev.toString(), 17);
					for (StringBuffer sb : grid) forAzdecrypt.add(sb.toString());
					forAzdecrypt.add("");
					
					num++;
				}
			}
			for (String s : forAzdecrypt) System.out.println(s);
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
	/** compute distribution stats for Cosine Similarity shuffle test.
	 **/
	public static void processForCosineSimilarityShuffle(String file) {
		BufferedReader input = null;
		
		//for (int i=0; i<1953; i++) {
		for (int i=0; i<1431; i++) {
			try {
				DescriptiveStatistics stat = new DescriptiveStatistics();
				int count = 0;
				input = new BufferedReader(new FileReader(new File(file)));
				String line = null; // not declared within while loop
				
				int num = 0;
				while ((line = input.readLine()) != null) {
					if (!line.startsWith("z408")) continue;
					String[] split = line.split("	");
					//if (split.length != 1954) {
					if (split.length != 1432) {
						//System.out.println("expected length 1954 for line " + num + " but got " + split.length);
						System.out.println("expected length 1432 for line " + num + " but got " + split.length);
						System.out.println(line);
					} else {
						stat.addValue(Double.valueOf(split[i+1]));
						count++;
						if (count >= 10000) break;
					}
					num++;
				}
				System.out.println(i+"	"+stat.getMin() + "	" + stat.getMax() + "	" + stat.getMean() + "	" + stat.getStandardDeviation());
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
		
	}
	
	public static void processZ340Shuffles() {
		//File dir = new File("/Users/doranchak/Downloads/az099/Results/shuffle_z340_10000_times");
		//File dir = new File("/Users/doranchak/Downloads/AZdecrypt0992c/Results/z340_shuffle_by_row");
		File dir = new File("/Users/doranchak/Downloads/AZdecrypt0992c/Results/z340_shuffle_full");
		File[] files = dir.listFiles();
		
		DescriptiveStatistics stat1 = new DescriptiveStatistics();
		DescriptiveStatistics stat2 = new DescriptiveStatistics();

		for (File file : files) {
			if (file.getName().equals("group_list.txt")) continue;
			if (file.getName().equals("ciphers.txt")) continue;
			if (!file.getName().endsWith(".txt")) continue;
			String[] split = file.getName().split("_");
			double score = Double.valueOf(split[0]); 
			double ioc = Double.valueOf(split[1]);
			stat1.addValue(score);
			stat2.addValue(ioc);
		}
		System.out.println(stat1.getMin() + " " + stat1.getMax() + " " + stat1.getMean() + " " + stat1.getStandardDeviation());
		System.out.println(stat2.getMin() + " " + stat2.getMax() + " " + stat2.getMean() + " " + stat2.getStandardDeviation());
	}

	public static void process54() {
		String file = "/Users/doranchak/projects/zodiac/54-0";
		BufferedReader input = null;
		Set<String> seen = new HashSet<String>();
		try {
			int count = 0;
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String plaintext = line.substring(line.indexOf("plaintext [")+11);
				plaintext = plaintext.substring(0, plaintext.indexOf("]"));
				if (seen.contains(plaintext)) continue;
				seen.add(plaintext);
				
				String tokens = line.substring(line.indexOf("tokens [")+9);
				tokens = tokens.substring(0, tokens.indexOf("]"));
				
				String cipher = line.substring(line.indexOf("cipher [")+8);
				cipher = cipher.substring(0, cipher.indexOf("]"));
				
				//System.out.println(plaintext);
				//System.out.println(tokens);
				//System.out.println(cipher);
				
				double u = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);				
				
				String re = Periods.rewrite3(cipher, 19);
				NGramsBean ng = new NGramsBean(2, re);
				String out = ng.count() + "	" + ng.numRepeats(); 
				ng = new NGramsBean(2, cipher);
				out = ng.count() + "	" + ng.numRepeats() + "	" + out;
				
				int[] num = Ciphers.toNumeric(cipher, false);
				String n = "";
				for (int i : num) n += (i<10 ? "0"+i : i) + " ";

				out = plaintext + "	" + tokens + "	'" + cipher + "	" + n + "	" + u + "	" + out; 
				System.out.println(out);
				
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

		
		file = "/Users/doranchak/projects/zodiac/54-1";
		try {
			int count = 0;
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String plaintext = line.substring(line.indexOf("plaintext [")+11);
				plaintext = plaintext.substring(0, plaintext.indexOf("]"));
				if (seen.contains(plaintext)) continue;
				seen.add(plaintext);
				
				String tokens = line.substring(line.indexOf("tokens [")+9);
				tokens = tokens.substring(0, tokens.indexOf("]"));
				
				String cipher = line.substring(line.indexOf("cipher [")+8);
				cipher = cipher.substring(0, cipher.indexOf("]"));
				
				//System.out.println(plaintext);
				//System.out.println(tokens);
				//System.out.println(cipher);
				
				double u = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);				
				String re = Periods.rewrite3(cipher, 19);
				NGramsBean ng = new NGramsBean(2, re);
				String out = ng.count() + "	" + ng.numRepeats(); 
				ng = new NGramsBean(2, cipher);
				out = ng.count() + "	" + ng.numRepeats() + "	" + out;
				
				int[] num = Ciphers.toNumeric(cipher, false);
				String n = "";
				for (int i : num) n += (i<10 ? "0"+i : i) + " ";

				out = plaintext + "	" + tokens + "	'" + cipher + "	" + n + "	" + u + "	" + out; 
				System.out.println(out);
				
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

	public static void process55() {
		String file = "/Users/doranchak/projects/zodiac/55-1";
		BufferedReader input = null;
		Set<String> seen = new HashSet<String>();
		try {
			int count = 0;
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String plaintext = line.substring(line.indexOf("plaintext [")+11);
				plaintext = plaintext.substring(0, plaintext.indexOf("]"));
				if (seen.contains(plaintext)) continue;
				seen.add(plaintext);
				
				String tokens = line.substring(line.indexOf("tokens [")+9);
				tokens = tokens.substring(0, tokens.indexOf("]"));
				
				String cipher = line.substring(line.indexOf("cipher [")+8);
				cipher = cipher.substring(0, cipher.indexOf("]"));
				
				//System.out.println(plaintext);
				//System.out.println(tokens);
				//System.out.println(cipher);
				
				double u = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
				double pcs = HomophonesNew.perfectCycleScoreFor(2, cipher, 3);
				
				String re = Periods.rewrite3(cipher, 19);
				NGramsBean ng = new NGramsBean(2, re);
				String out = ng.count() + "	" + ng.numRepeats(); 
				ng = new NGramsBean(2, cipher);
				out = ng.count() + "	" + ng.numRepeats() + "	" + out;
				
				int[] num = Ciphers.toNumeric(cipher, false);
				String n = "";
				for (int i : num) n += (i<10 ? "0"+i : i) + " ";

				out = plaintext + "	" + tokens + "	'" + cipher + "	" + n + "	" + u + "	" + out + "	" + pcs; 
				System.out.println(out);
				
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

	public static void process56() {
		String file = "/Users/doranchak/projects/zodiac/56-0";
		BufferedReader input = null;
		Set<String> seen = new HashSet<String>();
		try {
			int count = 0;
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String plaintext = line.substring(line.indexOf("plaintext [")+11);
				plaintext = plaintext.substring(0, plaintext.indexOf("]"));
				if (seen.contains(plaintext)) continue;
				seen.add(plaintext);
				
				String tokens = line.substring(line.indexOf("tokens [")+9);
				tokens = tokens.substring(0, tokens.indexOf("]"));
				
				String cipher = line.substring(line.indexOf("cipher [")+8);
				cipher = cipher.substring(0, cipher.indexOf("]"));
				
				//System.out.println(plaintext);
				//System.out.println(tokens);
				//System.out.println(cipher);
				
				double u = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
				double pcs = HomophonesNew.perfectCycleScoreFor(2, cipher, 3);
				
				String re = Periods.rewrite3(cipher, 19);
				NGramsBean ng = new NGramsBean(2, re);
				int c1 = ng.count();
				int n1 = ng.numRepeats(); 
				ng = new NGramsBean(2, cipher);
				//out = ng.count() + "	" + ng.numRepeats() + "	" + out;
				int c2 = ng.count();
				int n2 = ng.numRepeats();
				
				String flip = CipherTransformations.flipHorizontal(cipher, 20, 17);
				re = Periods.rewrite3(flip, 15);
				ng = new NGramsBean(2, re);
				int c3 = ng.count();
				int n3 = ng.numRepeats();
				
				
				int[] num = Ciphers.toNumeric(cipher, false);
				String n = "";
				for (int i : num) n += (i<10 ? "0"+i : i) + " ";

				String out = plaintext + "	" + tokens + "	'" + cipher + "	" + n + "	" + u + "	" + c2 + "	" + n2 + "	" + c1 + "	" + n1 + "	" + c3 + "	" + n3 + "	" + pcs; 
				System.out.println(out);
				
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

	public static void processdump() {
		String file = "/Users/doranchak/projects/zodiac/62-0";
		BufferedReader input = null;
		Set<String> seen = new HashSet<String>();
		try {
			int count = 1;
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String con = "new Cipher(\"";
				String plaintext = line.substring(line.indexOf("plaintext [")+11);
				plaintext = plaintext.substring(0, plaintext.indexOf("]"));
				if (seen.contains(plaintext)) continue;
				seen.add(plaintext);
				
				String tokens = line.substring(line.indexOf("tokens [")+9);
				tokens = tokens.substring(0, tokens.indexOf("]"));
				
				String cipher = line.substring(line.indexOf("cipher [")+8);
				cipher = cipher.substring(0, cipher.indexOf("]"));
				
				//System.out.println(plaintext);
				//System.out.println(tokens);
				//System.out.println(cipher);
				
				double u = CandidateKey.measureSymbolAgnosticUnigramDistance(cipher, true);
				double pcs = HomophonesNew.perfectCycleScoreFor(2, cipher, 3);
				
				String re = Periods.rewrite3(cipher, 19);
				NGramsBean ng = new NGramsBean(2, re);
				int c1 = ng.count();
				int n1 = ng.numRepeats(); 
				ng = new NGramsBean(2, cipher);
				//out = ng.count() + "	" + ng.numRepeats() + "	" + out;
				int c2 = ng.count();
				int n2 = ng.numRepeats();
				
				String flip = CipherTransformations.flipHorizontal(cipher, 20, 17);
				re = Periods.rewrite3(flip, 15);
				ng = new NGramsBean(2, re);
				int c3 = ng.count();
				int n3 = ng.numRepeats();
				
				
				int[] num = Ciphers.toNumeric(cipher, false);
				String n = "";
				for (int i : num) n += (i<10 ? "0"+i : i) + " ";

				String out = plaintext + "	" + tokens + "	'" + cipher + "	" + n + "	" + u + "	" + c2 + "	" + n2 + "	" + c1 + "	" + n1 + "	" + c3 + "	" + n3 + "	" + pcs; 
				System.out.println(out);
				
				con += (count) + "\", \"" + cipher+ "\", \"" + plaintext + ", tokens " + tokens + "\", null, true),";
				count++;
				System.out.println(con);
				
				
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

	/** dump all results that are within 3 units from origin */ 
	public static void processdumpall() {
		
		File dir = new File("/Users/doranchak/projects/zodiac/");
		File[] files = dir.listFiles();
		
		Map<String, Double> seen = new HashMap<String, Double>();
		
		for (File file : files) {
			if (file.getName().startsWith("log-generator-62-") && !file.getName().endsWith(".gz")) {
				BufferedReader input = null;
				try {
					int count = 0;
					input = new BufferedReader(new FileReader(file));
					String line = null; // not declared within while loop
					while ((line = input.readLine()) != null) {
						
						if (line.startsWith("generation ")) {
							String sub = line.substring(line.indexOf("distFromOrigin") + 15);
							String dist = sub.split(" ")[0];
							double d = Double.valueOf(dist);
							if (d <= 3) {
								String plaintext = line.substring(line.indexOf(" plaintext [")+12);
								plaintext = plaintext.substring(0, plaintext.indexOf(']'));
								boolean print = false;
								if (seen.containsKey(plaintext)) {
									Double val = seen.get(plaintext);
									if (d < val) {
										print = true;
										seen.put(plaintext, d);
									}
								} else {
									print = true;
									seen.put(plaintext, d);
								}
								if (print) System.out.println(file.getName()+":"+line);
							}
						}
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
		}

		
	}

	public static void tokenCount() {

		File file = new File("/Users/doranchak/projects/zodiac/tokens");
		BufferedReader input = null;
		try {
			int count = 0;
			input = new BufferedReader(new FileReader(file));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				line = line.replaceAll(",","");
				Map<String, Integer> counts = new HashMap<String, Integer>();
				for (String token : line.split(" ")) {
					Integer val = counts.get(token);
					if (val == null) val = 0;
					val++;
					counts.put(token, val);
				}
				for (String key : counts.keySet()) {
					System.out.println(counts.get(key) + " " + key + " " + count);
				}
				count++;
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
	
	public static void main(String[] args) {
		//process("/Users/doranchak/projects/zodiac/log-generator-15");
		//process2("/Users/doranchak/projects/zodiac/log-generator-20");
		//process4("/Users/doranchak/projects/zodiac/log-generator-42-1");
		//processForCiphersCollection("/Users/doranchak/projects/zodiac/log-generator-51", 51, 106700);
		//processForFragmentIoc("/Users/doranchak/projects/zodiac/log-transform-34", 34, 89500);
		//processForFragmentIoc("/Users/doranchak/projects/zodiac/log-transform-35", 35, 22000);
		//processForFragmentIoc("/Users/doranchak/projects/zodiac/log-transform-36", 36, 50000);
		//processForFragmentIoc("/Users/doranchak/projects/zodiac/log-transform-39", 39, 42000);
		//processForFragmentIoc("/Users/doranchak/projects/zodiac/log-transform-40", 40, 36000);
		//processForFragmentIoc("/Users/doranchak/projects/zodiac/log-transform-41", 41, 46500);
		//processForFragmentIoc("/Users/doranchak/projects/zodiac/log-transform-42", 42, 47500);
		//processForFragmentIoc("/Users/doranchak/projects/zodiac/log-transform-43", 43, 45000);
		//processForColumnar("/Users/doranchak/projects/zodiac/log-columnar-2", 2, 32000);
		//processZ340Shuffles();
		processdump();
		//tokenCount();
		//processdumpall();
		
		
		//float[] f1 = new float[] {15.427249f, 33.0f, 153.0f, 13.0f, 0.0f, 36.0f};
		//float[] f2 = new float[] {4.2426405f, 26.0f, 147.0f, 2.0f, 0.0f, 27.0f};
		
		//System.out.println(CandidateKey.isDominatedBy(f1, f2));
	}
}
