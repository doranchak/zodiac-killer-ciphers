package com.zodiackillerciphers.tests.unicity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.hadoop.io.Text;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.ZKDecrypto;

public class CorpusScanner {

	/** generate a "key" to represent this plaintext's uniqueness among plaintexts with regard
	 * to encipherments.  i.e., the key is the same for two plaintexts if they can decode to each other.
	 */
	public static String vigKeyFor(String str, int d) {
		return Arrays.toString(VigenereMutual.letterDifferences(str.replaceAll(" ", ""), d));
	}
	public static String subKeyFor(String str) {
		return Arrays.toString(Ciphers.toNumeric(str.replaceAll(" ", ""), false));		
	}
	
	/**
	 * generate index of isomorphisms, outputted to large file. then process them
	 * later (via external sort / merge sort)
	 */
	public static void index(int minLength, int maxLength, long maxSizeInBytes) {
		System.out.println("maxSizeInBytes: " + maxSizeInBytes);
		SubstitutionMutualEvolve.initSources();
		long samples = 0;
		long bytes = 0;
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			for (int i = 0; i < SubstitutionMutualEvolve.tokens.length; i++) {
				String withSpace = "";
				String withoutSpace = "";
				for (int j = i; j < SubstitutionMutualEvolve.tokens.length; j++) {
					String token = SubstitutionMutualEvolve.tokens[j];
					if (withSpace.length() > 0)
						withSpace += " ";
					withSpace += token;
					withoutSpace += token;
					int L = withoutSpace.length();
					if (L > maxLength)
						break;
					if (L >= minLength) {
//						System.out.println(
//								L + "	" + Arrays.toString(Ciphers.toNumeric(withoutSpace, false)) + "	" + withSpace);
//						System.out.println(
//								L + "	5	" + vigKeyFor(withSpace, 5) + "	" + withSpace);
//						System.out.println(
//								L + "	10	" + vigKeyFor(withSpace, 10) + "	" + withSpace);
						String line = L + "	1	" + vigKeyFor(withSpace, 1) + "	" + withSpace;
						System.out.println(line);
						bytes += line.length();
						if (bytes > maxSizeInBytes) {
							System.out.println("Total Samples: " + samples);
							return;
						}
						samples++;
						if (samples % 10000 == 0)
							System.out.println("Samples: " + samples);
					}
				}
	
			}
		}
	}
	
	/** process the big sorted file and output only the significant results. */
	public static void process(String path, String outputFile, int cipherType) {
		BufferedReader input = null;
		long counter = 0;
		String isoPrev = null;
		/** number of ngram samples per length */
		Map<Integer, Long> samples = new HashMap<Integer, Long>();
		/** number of distinct isomorphisms per length */
		Map<Integer, Long> isos = new HashMap<Integer, Long>();
		/** total number of plaintexts that had alternate decodings (i.e., a plaintext that had another plaintext with an equivalent isomorphism) */
		Map<Integer, Long> hits = new HashMap<Integer, Long>();
		/** weighted number of plaintexts that had alternate decodings (i.e., a plaintext that had another plaintext with an equivalent isomorphism) 
		  (weighting is done by measuring average letter uniqueness) */
//		Map<Integer, Float> hitsWeighted = new HashMap<Integer, Float>();
		/** for each length, count the number of rejected plaintexts */
		Map<Integer, Long> rejects = new HashMap<Integer, Long>();
		
		try {
			input = new BufferedReader(new FileReader(new File(path)), 3000000);
			String lineCurrent = null;
			CorpusScannerBean bean = null;
			
			File fout = new File(outputFile);
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos), 3000000);
			Integer lenPrev = null;
			while ((lineCurrent = input.readLine()) != null) {
				if (lineCurrent.contains("[") && lineCurrent.contains("]")) {
					counter++;
					if (counter % 1000000 == 0) {
						System.out.println("Processed " + counter + " lines.");
					}
					String[] split = lineCurrent.split("	");
					int which = 0;
					Integer len = Integer.valueOf(split[which++]);
					Integer d;
					if (cipherType == 1) {
						d = Integer.valueOf(split[which++]);
						if (d != 1) continue;
					}
//					if (d != 5) continue;

					Long val = samples.get(len);
					if (val == null) val = 0l;
					val++;
					samples.put(len,  val);
					
					String iso = split[which++];
					String plaintext = split[which++];

					if (isoPrev == null || !isoPrev.equals(iso)) {
						// switching to a new isomorphism.  output the old one.
						if (bean != null) {
							bean.filter();
//							if (!bean.rejects.isEmpty()) {
//								bw.write("Rejects: " + bean.rejects);
//								bw.newLine();
//							}
							if (bean.plaintexts.size() > 1) {
								bw.write(bean.toString());
								bw.newLine();
								val = hits.get(len);
								if (val == null) val = 0l;
								val += bean.plaintexts.size();
								hits.put(len,  val);
								
//								Float valFloat = hitsWeighted.get(len);
//								if (valFloat == null) valFloat = 0f;
//								valFloat += (bean.averageLetterUniqueness() * bean.plaintexts.size());
//								hitsWeighted.put(len,  valFloat);
							}
						}
						bean = new CorpusScannerBean(iso, len);
						// and increment the isomorphism count
						val = isos.get(len);
						if (val == null) val = 0l;
						val++;
						isos.put(len,  val);
						
					}
					if (lenPrev != null && !lenPrev.equals(len)) {
						bw.write("map samples " + samples);
						bw.newLine();
						bw.write("map isos " + isos);
						bw.newLine();
						bw.write("map hits " + hits);
						bw.newLine();
//						bw.write("map hitsWeighted " + hitsWeighted);
//						bw.newLine();
					}
					if (counter % 1000000 == 0) {
						System.out.println("map samples " + samples);
						System.out.println("map rejects " + rejects);
						System.out.println("map isos " + isos);
						System.out.println("map hits " + hits);
//						System.out.println("map hitsWeighted " + hitsWeighted);
					}
					if (!bean.add(plaintext)) {
						val = rejects.get(len);
						if (val == null) val = 0l;
						val++;
						rejects.put(len,  val);
					}
					isoPrev = iso;
					lenPrev = len;
				}
			}
			bean.filter();
//			if (!bean.rejects.isEmpty()) {
//				bw.write("Rejects: " + bean.rejects);
//				bw.newLine();
//			}
			if (bean.plaintexts.size() > 1) {
				bw.write(bean.toString());
				bw.newLine();
			}
			bw.write("map samples " + samples);
			bw.newLine();
			bw.write("map rejects " + rejects);
			bw.newLine();
			bw.write("map isos " + isos);
			bw.newLine();
			bw.write("map hits " + hits);
			bw.newLine();
//			bw.write("map hitsWeighted " + hitsWeighted);
//			bw.newLine();
			bw.write("read " + counter + " lines.");
			bw.newLine();
			bw.close();
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
	// find the given string in the given file.  output file names that match. 
	public static void search(String path, String search) {
		long count = 0;
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File(path)), 3000000);
			String line;
			String lastFileName = null;
			while ((line = input.readLine()) != null) {
				count++;
				if (count % 1000000 == 0) {
					System.out.println("Read " + count + " lines...");
				}
				if (line.startsWith("File ")) {
					lastFileName = line;
				}
				if (line.contains(search)) {
					System.out.println(lastFileName + " " + line);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** re-process and filter out crappy results */
	static void postProcessUNUSED(String path) {
		long count = 0;
		// map length to number of hits
		Map<Integer, Long> hits = new HashMap<Integer, Long>(); 
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File(path)), 3000000);
			String line;
			while ((line = input.readLine()) != null) {
				count++;
				if (count % 1000 == 0) {
					System.out.println("Read " + count + " lines...");
					System.out.println("map hits " + hits);
					
				}
				if (line.startsWith("map")) {
					continue;
				}
				String[] split = line.split("	");
				Integer length = Integer.valueOf(split[0]);
				String iso = split[1];
				Float score = Float.valueOf(split[3]);
				String all = split[4].replaceAll("[\\[\\]]", "");
				String[] plaintexts = all.split(",");
				plaintexts = filterUNUSED(plaintexts);
				if (plaintexts.length < 2) 
					continue;
				
				
				System.out.println(length + "	" + iso + "	" + plaintexts.length + "	" + Arrays.toString(plaintexts)); 
				
				Long val = hits.get(length);
				if (val == null) val = 0l;
				val += plaintexts.length;
				hits.put(length, val);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("map hits " + hits);
	}
	
	// filter out junk texts
	// 1) zkscore divided by length must be at least 30
	// 2) there must be at least one other plaintext that is no more than half
	//    identical (by comparing letter-by-letter at each position)
	static String[] filterUNUSED(String[] texts) {
//		System.out.println("before: " + Arrays.toString(texts));
		List<String> list1 = new ArrayList<String>();
		String rejected = "";
		for (String s : texts) {
			String ns = s.replaceAll(" ", "");
			float zkscore = ZKDecrypto.calcscore(ns);
			zkscore /= ns.length();
			if (zkscore < 30) {
				rejected += s + " (zk " + zkscore + ") ";
//				System.out.println(" - rejected " + s + " (" + zkscore + ")");
				continue;
			}
			list1.add(s);
		}
		if (list1.size() == 0) {
//			System.out.println("after: empty");
			return new String[0];
		}
		if (list1.size() > 99) {
			// too big for the n^2 loop below
			return list1.toArray(new String[0]);
		}
		Set<String> set2 = new HashSet<String>();
		boolean[] added = new boolean[list1.size()];
		for (int i=0; i<list1.size(); i++) {
			if (added[i]) continue; // already added
			String ns1 = list1.get(i).replaceAll(" ", "");
			for (int j=i+1; j<list1.size(); j++) {
				String ns2 = list1.get(j).replaceAll(" ", ""); 
				if (PlaintextBean.tooSimilar(ns1, ns2)) continue;
				// add both 
				set2.add(list1.get(i));
				set2.add(list1.get(j));
				added[i] = true;
				added[j] = true;
			}
		}
		for (int i=0; i<added.length; i++) {
			if (!added[i])
				rejected += list1.get(i) + " (ts) ";
		}
		if (rejected.length() > 0) {
			System.out.println("Rejected (" + texts.length + " to " + set2.size() + "): " + rejected);
		}
//		System.out.println(" - added " + Arrays.toString(added));
//		System.out.println("after: " + set2);
		return set2.toArray(new String[0]);
	}
	
	
	public static void main(String[] args) {
//		long max = 300;
//		max *= 1024;
//		max *= 1024;
//		max *= 1024;
//		index(5, 25, max);

		//		process("/Volumes/Biggie/projects/zodiac/substitution-isomorphisms-index-sorted-all.txt", "/Volumes/Biggie/projects/zodiac/substitution-isomorphisms-processed-5th-pass.txt", 0);
		//		process("/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-index-sorted-all.txt", "/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-processed-d10-3rd-pass.txt", 1);
		process("/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-index-d1-sorted-all.txt", "/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-processed-d1.txt", 1);
		
//		search("/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-index.txt", "UQJFXJ");
	}
}
