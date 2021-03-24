package com.zodiackillerciphers.tests;

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

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.dictionary.WordFrequencies.WordBean;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;
import com.zodiackillerciphers.transform.operations.FlipHorizontal;
import com.zodiackillerciphers.transform.operations.FlipVertical;
import com.zodiackillerciphers.transform.operations.Reverse;
import com.zodiackillerciphers.transform.operations.Rotate;
import com.zodiackillerciphers.transform.operations.Width;

/** http://www.zodiackillersite.com/viewtopic.php?p=49159#p49159 */
public class MrLowePatterns {
	
	static Set<String> seen;
	/** find all Mr Lowe patterns in the given cipher.
	 * 
	 *  Mr Lowe pattern is an unbroken pattern of ngram repeats.
	 *  
	 *  Examples:
	 *  
	 *     ABAB
	 *     
	 *     ABABCDCD
	 *     
	 *     ABCABC
	 *      
	 *     TODO: what about ABABACDCD?
	 *     
	 **/
	static void find(String cipher, int n, List<MrLowePatternsBean> results) {
		find(cipher, n, 0, results);
	}
	/**
	 */
	static void find(String cipher, int n, int pos, List<MrLowePatternsBean> results) {
		// bounds check
		if (pos >= cipher.length()) return;
		// find largest candidate sequence at current position
		String largest = largestAt(cipher, n, pos);
		if (largest == null) {
			// if none found, search from next spot
		} else {
			// if one found, keep looking until no more are found
			String piece = "";
			int k = pos;
			while (largest != null) {
				piece += largest;
				k += largest.length();
				largest = largestAt(cipher, n, k);
			}
			if (piece.length() > 0) {
				MrLowePatternsBean bean = new MrLowePatternsBean();
				bean.pos = pos;
				bean.sequence = piece;
				results.add(bean);
				//System.out.println("bean " + bean);
			}
			
		}
		find(cipher, n, pos+1, results);
	}
	
	/** start at position, return largest repeating bigram sequence there. */
	static String largestAt(String cipher, int n, int pos) {
		String piece = "";
		for (int i=pos; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			//System.out.println(pos + " " + piece.length() +" " + piece + " " + n + " " + ch);
			if (piece.length() < n) piece += ch;
			else {
				if (ch != piece.charAt(i-pos-n)) break;
				if (ch == piece.charAt(piece.length()-1)) break; // avoid spurious patterns such as AAAAAA
				piece += ch; 
			}
		}
		if (piece.length() < n*2) return null; // must be at least two consecutive appearances of the bigram 
		return piece;  
	}
	
	static void shuffleTest() {
		String cipher = Ciphers.cipher[0].cipher;
		Map<Integer, DescriptiveStatistics> stats = new HashMap<Integer, DescriptiveStatistics>(); 
		for (int i=0;i<1000000; i++) {
			if (i % 10000 == 0) System.out.println(i+"...");
			cipher = CipherTransformations.shuffle(cipher);
			List<MrLowePatternsBean> results = new ArrayList<MrLowePatternsBean>();		
			find(cipher, 2, results);
			//if (results.size() > 1) System.out.println(results.size() + " !");
			if (results.isEmpty()) continue;
			info(i, results, stats);
		}
		
		// add zero values
		for (DescriptiveStatistics stat : stats.values()) {
			for (int i=0; i<1000000-stat.getN(); i++) stat.addValue(0);
		}
		
		for (Integer key : stats.keySet()) {
			DescriptiveStatistics stat = stats.get(key);
			System.out.println("length " + key + " min " + stat.getMin() + " max " + stat.getMax() + " mean " + stat.getMean() + " median " + stat.getPercentile(0.5)
					+ " stddev " + stat.getStandardDeviation());
		}
		
		
	}
	static void info(int i, List<MrLowePatternsBean> results, Map<Integer, DescriptiveStatistics> stats) {
		Map<Integer, Integer> countByLength = countByLength(results); 
		//System.out.println("smeg " + countByLength);
		for (Integer key : countByLength.keySet()) {
			Integer val = countByLength.get(key);
			DescriptiveStatistics stat = stats.get(key);
			if (stat == null) stat = new DescriptiveStatistics();
			stat.addValue(val);
			stats.put(key, stat);
		}
	}
	
	static Map<Integer, Integer> countByLength(List<MrLowePatternsBean> results) {
		Map<Integer, Integer> countByLength = new HashMap<Integer, Integer>(); 
		for (MrLowePatternsBean bean : results) {
			Integer key = bean.sequence.length();
			Integer val = countByLength.get(key);
			if (val == null) val = 0;
			val++;
			countByLength.put(key, val);
		}
		return countByLength;
	}
	
	
	
	/** generate many variations to test */
	public static void generate() {
		seen = new HashSet<String>();
		//String cipher = Ciphers.cipher[0].cipher;
		//List<StringBuffer> list = TransformationBase.toList(cipher,  17);
		
		//long best = 0;
		long count = 0;

		for (boolean reverse : new boolean[] {false, true}) {
			for (int width1=1; width1<=34; width1++) {
				for (int[] rotflip1 : Rotate.rotFlipOperations) {
					int rotate1 = rotflip1[0]/90;
					int flipH1 = rotflip1[1];
					int flipV1 = rotflip1[2];
					for (int scytale=1; scytale<=34; scytale++) {
						for (int width2=1; width2<=34; width2++) {
							for (int[] rotflip2 : Rotate.rotFlipOperations) {
								int rotate2 = rotflip2[0]/90;
								int flipH2 = rotflip2[1];
								int flipV2 = rotflip2[2];

								String cipher = Ciphers.cipher[0].cipher;
								List<StringBuffer> list = TransformationBase.toList(cipher,  17);
								String result = performOperations(list,
										reverse, width1, rotate1, flipH1,
										flipV1, scytale, width2, rotate2,
										flipH2, flipV2);
								//result = "H*H*F+F+DlE+YHT#t-|B+WRjMJYUTpP+T+MZ2OGB>4S9DCyR.NU%@kd|9C)O5<G52#*/.l46)+p(X.fA^+7>#c4lK5-U|ZcBz>z-|f^+c_cEBV|+M7(#:qOKkF8ZKS-PzbRGdAJ;d(PzBB2kLzMY:O3W+B%JC5NDL63+;/U1SVlz7;5pFTHVTc.pCLNb+RK)2EYFVq+p2y+ccp^G++*+_RM^&1(JOU4^FdSKGyp2cp*f529zNNKzVFO25MkWBt&*/<<p<W+OB+O+)1fj6V)48Xk8BF_(LW<lct2WCDyp.lbFRLBdLZ|#z4G+>|9cF.<KO(+^(ty|8|ROlpM+B|R-";
								// measure
								NGramsBean ng2 = new NGramsBean(2, result);
								NGramsBean ng3 = new NGramsBean(3, result);
								NGramsBean ng4 = new NGramsBean(4, result);
								
								int nr2 = ng2.numRepeats();
								int nr3 = ng3.numRepeats();
								int nr4 = ng4.numRepeats();

								List<MrLowePatternsBean> results = new ArrayList<MrLowePatternsBean>();		
								find(result, 2, results);
								Map<Integer, Integer> counts = countByLength(results);
								int[] c = new int[7];
								for (int L=4; L<=10; L++) {
									c[L-4] = counts.get(L) == null ? 0 : counts.get(L); 
								}
								
								long score = (1+nr2)*(1+nr3)*(1+nr4);
								for (int cc : c) score *= (1+cc);

								String key = reverse + " " + width1 + " " + rotate1*90 + " " + flipH1 + " " 
										+ flipV1 + " " + scytale + " " + width2 + " " + rotate2*90 + " " + flipH2 + " " + flipV2;
								String measure = nr2 + " " + nr3 + " " + nr4;
								for (int cc : c) measure += " " + cc;
								
								if (!seen.contains(result)) {
									System.out.println(score + " key " + key + " measure " + measure + " result " + result);
									seen.add(result);
								}
								
								count++;
								if (count % 10000 == 0) {
									System.out.println("count " + count + " unique " + seen.size() + " diff " + (count-seen.size()));
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
	}
	
	public static String performOperations(List<StringBuffer> list,
			boolean reverse, int width1, int rotate1, int flipH1, int flipV1,
			int scytale, int width2, int rotate2, int flipH2, int flipV2) {
		if (reverse) {
			Reverse rev = new Reverse(list);
			rev.execute();
			list = rev.getOutput();
		}
		Width w = new Width(list, width1);
		w.execute();
		list = w.getOutput();

		Rotate rot = new Rotate(list, rotate1);
		rot.execute();
		list = rot.getOutput();

		if (flipH1 > 0) {
			FlipHorizontal flip = new FlipHorizontal(list);
			flip.execute();
			list = flip.getOutput();
		}
		if (flipV1 > 0) {
			FlipVertical flip = new FlipVertical(list);
			flip.execute();
			list = flip.getOutput();
		}

		String line = TransformationBase.fromList(list).toString();
		String sc = Scytale.encode(line, scytale);
		list = TransformationBase.toList(sc, 17);

		w = new Width(list, width2);
		w.execute();
		list = w.getOutput();

		rot = new Rotate(list, rotate2);
		rot.execute();
		list = rot.getOutput();

		if (flipH2 > 0) {
			FlipHorizontal flip = new FlipHorizontal(list);
			flip.execute();
			list = flip.getOutput();
		}
		if (flipV2 > 0) {
			FlipVertical flip = new FlipVertical(list);
			flip.execute();
			list = flip.getOutput();
		}

		String result = TransformationBase.fromList(list).toString();
		return result;

	}

	/** starbug8 can process about 7 ciphers per minute (with 30 restarts per cipher, 1,000,000 iterations per restart)
	 * that works out to 10080 ciphers per day.  8640 at 6 ciphers per minute.
	 **/
	
	static void processForAzdecrypt(String file) {
		BufferedReader input = null;
		int num = 1;
		Set<String> seen = new HashSet<String>();

		List<String> forAzdecrypt = new ArrayList<String>();
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("count ")) continue;
				String[] split = line.split(" ");
				String cipher = split[25];
				String key = line.substring(0, line.indexOf(" result "));
				key = key.replaceAll(" ", "_");
				if (seen.contains(cipher))
					continue;
				seen.add(cipher);

				forAzdecrypt.add("cipher_information=" + num + ": " + key);
				List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
				for (StringBuffer sb : grid)
					forAzdecrypt.add(sb.toString());
				forAzdecrypt.add("");

				if (num == 20000) break;
				num++;
			}
			for (String s : forAzdecrypt)
				System.out.println(s);
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
	
	/** pretty-print the results from the azdecrypt run */
	static void processFromAzdecrypt(String path) {
		BufferedReader input = null;
		int num = 0;

		File f = new File(path);
		for (File file : f.listFiles()) {
			//System.out.println(file.getName() + "...");
			if (file.isDirectory()) continue;
			if (file.getName().endsWith(".txt")) {
				if (file.getName().equals("group_list.txt")) continue;

				String cipher = "";
				String plaintext = "";
				String info = "";
				String words = "";
				String zodiacwords = "";
				String tab = "	";
				try {
					input = new BufferedReader(new FileReader(file));
					String line = null; // not declared within while loop
					int linenum=1;
					while ((line = input.readLine()) != null) {
						if (linenum == 1) { // info
							String ciphernum = line.split(":")[0];
							info += ciphernum + tab;
							String[] split = line.split("_");
							for (int i=0; i<split.length;i++) {
								if (i==2) continue;
								if (i==13) continue;
								info += split[i] + tab;
							}
						} else if (linenum == 3) { // score
							String[] split = line.split(" ");
							String score = split[1];
							String ioc = split[3];
							String chi2 = split[5];
							info = score + tab + ioc + tab + chi2 + tab + info;
						} else if (linenum >= 5 && linenum <= 24) { // plaintext
							plaintext += line;
							
						} else if (linenum >= 26) { // ciphertext
							cipher += line;
						
						}
						linenum++;
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
				
				// search for words and zodiac words
				List<WordBean> beans = WordFrequencies.findAllWordsIn(plaintext, 4, true);
				for (WordBean bean : beans) {
					words += bean.word + " ";
				}
				beans = WordFrequencies.findZodiacWordsIn(plaintext);
				for (WordBean bean : beans) {
					zodiacwords += bean.word + " ";
				}
				
				System.out.println(info + tab + "=\"" + cipher + "\"" + tab + plaintext + tab + words + tab + zodiacwords);  
				
			}
		}
		
	}
	
	/** compute normalized combined scores */
	static void normalize(String file) {
		BufferedReader input = null;
		
		int[] min = new int[10];
		int[] max = new int[10];
		int num = 0;
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("count ")) continue;
				String[] split = line.split(" ");
				for (int i=13; i<=22; i++) {
					int val = Integer.valueOf(split[i]);
					int k = i-13;
					min[k] = Math.min(min[k], val);
					max[k] = Math.max(max[k], val);
				}
				num++;
				if (num % 100000 == 0) System.out.println(num+"...");
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
		
		System.out.println("min: " + Arrays.toString(min));
		System.out.println("max: " + Arrays.toString(max));
		
		// normalize
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("count ")) continue;
				String[] split = line.split(" ");
				String newLine = "";
				String after = "";
				float[] n = new float[10];
				for (int i=0; i<split.length; i++) {
					if (i<13) newLine += split[i] + " ";
					else if (i>22) after += split[i] + " ";
					else {
						newLine += split[i] + " ";
						int k = i-13;
						int val = Integer.valueOf(split[i]);
						int minVal = min[k];
						int maxVal = max[k];
						int diff = maxVal-minVal;
						n[k] = diff == 0 ? 0 : ((float)val-minVal)/diff;
					}
				}
				float score = 1;
				for (float f : n) {
					//newLine += f + " ";
					score *= (1+f);
				} 
				newLine = score + " " + newLine + after;
				System.out.println(newLine);
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

	public static void test() {
		//System.out.println(largestAt("ABABCDCD", 2, 0));
		List<MrLowePatternsBean> results = new ArrayList<MrLowePatternsBean>();		
		find("++++++ABABCDCD", 2, results);
		for (MrLowePatternsBean bean : results) System.out.println(bean);
		System.out.println("countByLength " + countByLength(results));
		
	}
	public static void main(String[] args) {
		//test();
		//System.out.println(performOperations(TransformationBase.toList(Ciphers.cipher[0].cipher, 17), 
		//		false, 17, 1, 1, 0, 30, 17, 1, 1, 0));
		//generate();
		//processForAzdecrypt("/Users/doranchak/projects/zodiac/mr-lowe-all.txt");
		processFromAzdecrypt("/Volumes/doranchak-2/Downloads/AZdecrypt0992c/for-upload/output-results");
		//normalize("/Users/doranchak/projects/zodiac/mr-lowe-patterns-generate-sorted.txt");
		//shuffleTest();
	}
}
