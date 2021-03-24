package com.zodiackillerciphers.homophones;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.CipherTransformations;

public class ShuffleStats extends Thread {
	
	static String[] alphabet;
	// track run scores per unique combination of L symbols
	//Map<String, List<HomophoneStatBean>> map;
	//static Map<Character, List<Float>> probPerSymbol;
	//track max runs per symbol
	Map<Character, List<Integer>> runsPerSymbol;
	
	// "perfect cycle" score
	List<Float> perfectCycleScores;
	// jarlve's m_2s_cycles scores
	List<Float> jarlveCycleScores;
	// jarlve's various nonrepeat scores
	List<Float> nonrepeat;
	List<Float> nonrepeatAlternate;
	List<Float> nonrepeatNormalizedAlternate;
	
	public int L; public String cipher; public int n;
	
	public ShuffleStats(int L, String cipher, int n) {
		this.L = L;
		this.cipher = cipher;
		this.n = n;
		
		
		//map = new HashMap<String, List<HomophoneStatBean>>();
		// track lowest probability per symbol
		//probPerSymbol = new ConcurrentHashMap<Character, List<Float>>();
		runsPerSymbol = new HashMap<Character, List<Integer>>();
		// "perfect cycle" score
		perfectCycleScores = new ArrayList<Float>();
		// jarlve's m_2s_cycles scores
		jarlveCycleScores = new ArrayList<Float>();
		// jarlve's various nonrepeat scores
		nonrepeat = new ArrayList<Float>();
		nonrepeatAlternate = new ArrayList<Float>();
		nonrepeatNormalizedAlternate = new ArrayList<Float>();
	}
	
	/** shuffle the given cipher and compute stats for use in significance tests */
	public static void testShuffleStats(int L, String cipher, int n, int numThreads) {
		Long timeStart = new Date().getTime(); 
		// cipher alphabet
		alphabet = Ciphers.alphabetAsArray(cipher);
		// track run scores per unique combination of L symbols
		

		List<ShuffleStats> threads = new ArrayList<ShuffleStats>();
		for (int i=0; i<numThreads; i++) {
			ShuffleStats thread = new ShuffleStats(L, cipher, n/numThreads);
			thread.start();
			threads.add(thread);
		}
		for (ShuffleStats thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		summarize(threads);
		Long timeEnd = new Date().getTime();
		System.out.println("Elapsed: " + (timeEnd-timeStart));
		
	}
	
	
	public void run() {
		Long startTime = new Date().getTime();
		float rate = 0;
		for (int i=0; i<n; i++) {
			if (i>0 && i % 10 == 0) {
				float rem = (n-i)/rate;
				System.out.println("Thread " + Thread.currentThread().hashCode() + " " + i + " remaining " + rem); 
			}
			cipher = CipherTransformations.shuffle(cipher);
			PerfectCyclesBean pcb = new PerfectCyclesBean(); 
			List<HomophonesResultBean> beans = HomophonesNew.search(cipher, alphabet, L, false, false, 1, pcb, false);
			perfectCycleScores.add((float)pcb.score);
			// index by sequence
			for (HomophonesResultBean bean : beans) {
				String key = bean.sorted(bean.runSequence);
				//List<HomophoneStatBean> val = map.get(key);
				//if (val == null) val = Collections.synchronizedList(new ArrayList<HomophoneStatBean>());
				//HomophoneStatBean h = new HomophoneStatBean();
				//h.run = bean.run;
				if (key != null) System.out.println("by key " + key + " " + bean.run);
				//h.probability = bean.runProbability;
				//h.percent = bean.runPercent();
				//val.add(h);
				//map.put(key, val);
			}
			
			// max runs per symbol
			
			/*Map<Character, Integer> probs = HomophonesNew.maxRunsPerSymbol(beans);
			for (Character key : probs.keySet()) {
				Integer val = probs.get(key);
				List<Integer> val2 = runsPerSymbol.get(key);
				if (val2 == null) val2 = new ArrayList<Integer>();
				val2.add(val);
				runsPerSymbol.put(key, val2);
			}*/
			
			// perfect cycle score
			//perfectCycleScores.add((float)HomophonesNew.perfectCycleScoreFor(L, cipher, 3));
			
			// jarlve cycle score
			jarlveCycleScores.add((float)JarlveMeasurements.homScore(cipher));

			// jarlve's nonrepeat scores
			nonrepeat.add((float)JarlveMeasurements.nonrepeat(cipher));
			nonrepeatAlternate.add(JarlveMeasurements.nonrepeatAlternate(cipher));
			nonrepeatNormalizedAlternate.add(JarlveMeasurements.nonrepeatNormalizedAlternate(cipher));
			
			Long endTime = new Date().getTime();
			rate = i/((float)(endTime/1000-startTime/1000));
			
		}
		System.out.println("thread completed...");
	}
	
	public static void summarize(List<ShuffleStats> threads) {
		List<ShuffleStats> list = (List<ShuffleStats>) threads; 
		
		Map<String, DescriptiveStatistics> statsRunByKey = new HashMap<String, DescriptiveStatistics>();
		Map<Character, DescriptiveStatistics> statsRunBySymbol = new HashMap<Character, DescriptiveStatistics>();
		DescriptiveStatistics statsPerfectCycle = new DescriptiveStatistics();
		DescriptiveStatistics statsJarlveCycle = new DescriptiveStatistics();
		DescriptiveStatistics statsNonrepeat = new DescriptiveStatistics();
		DescriptiveStatistics statsNonrepeatAlternate = new DescriptiveStatistics();
		DescriptiveStatistics statsNonrepeatNormalizedAlternate = new DescriptiveStatistics();
		
		for (ShuffleStats s : list) {
			/*for (String key : s.map.keySet()) {
				DescriptiveStatistics stats = statsRunByKey.get(key);
				if (stats == null) stats = new DescriptiveStatistics();
				statsRunByKey.put(key, stats);
				for (HomophoneStatBean bean : s.map.get(key)) {
					stats.addValue(bean.run);
				}
			}*/

			for (Character key : s.runsPerSymbol.keySet()) {
				DescriptiveStatistics stats = statsRunBySymbol.get(key);
				if (stats == null) stats = new DescriptiveStatistics();
				statsRunBySymbol.put(key, stats);
				for (Integer val : s.runsPerSymbol.get(key)) {
					//if (val == null) System.out.println("why null for " + key);
					stats.addValue(val);
				}
			}
			for (Float val : s.perfectCycleScores) {
				statsPerfectCycle.addValue(val);
			}
			for (Float val : s.jarlveCycleScores) {
				statsJarlveCycle.addValue(val);
			}
			for (Float val : s.nonrepeat) {
				statsNonrepeat.addValue(val);
			}
			for (Float val : s.nonrepeatAlternate) {
				statsNonrepeatAlternate.addValue(val);
			}
			for (Float val : s.nonrepeatNormalizedAlternate) {
				statsNonrepeatNormalizedAlternate.addValue(val);
			}
		}

		for (String key : statsRunByKey.keySet()) System.out.println(info("run " + key, statsRunByKey.get(key)));
		for (Character key : statsRunBySymbol.keySet()) System.out.println(info("symbol " + key, statsRunBySymbol.get(key)));
		System.out.println(info("perfectCycle", statsPerfectCycle));
		System.out.println(info("jarlveCycle", statsJarlveCycle));
		System.out.println(info("nonrepeat", statsNonrepeat));
		System.out.println(info("nonrepeatAlternate", statsNonrepeatAlternate));
		System.out.println(info("nonrepeatAlternate", statsNonrepeatNormalizedAlternate));
	}
	public static String info(String prefix, DescriptiveStatistics stats) {
		return prefix + " " + stats.getMin() + " " + stats.getMax() + " " + stats.getMean() + " " + stats.getStandardDeviation();
	}
	
	/** process shuffle file.  compare to cycles detected in the original cipher.  display stats only for 
	 * cycles that have run > 1.
	 */
	public static void process(String cipher, String shuffleFile, int L) {
		System.out.println(shuffleFile);
		Map<Character, Integer> counts = Ciphers.countMap(cipher);		
		/** map cycles to stats.  only tracks cycles that have run > 1 */
		Map<String, DescriptiveStatistics> map = new HashMap<String, DescriptiveStatistics>();  
		Map<String, HomophonesResultBean> mapBeans = new HashMap<String, HomophonesResultBean>();  
		List<HomophonesResultBean> beans = HomophonesNew.search(cipher, Ciphers.alphabetAsArray(cipher), L, false, false, 2, null, false);
		for (HomophonesResultBean bean : beans) {
			if (bean.run < 2) continue;
			System.out.println(bean);
			String key = bean.sorted(bean.sequence);
			map.put(key, new DescriptiveStatistics());
			mapBeans.put(key, bean);
		}
		
		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(shuffleFile)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				counter++;
				//if (counter % 100000 == 0) System.out.println(counter+"...");
				if (line.startsWith("by key ")) {
					String[] split = line.split(" ");
					String key = split[2];
					if (map.containsKey(key)) {
						DescriptiveStatistics stat = map.get(key);
						stat.addValue(Double.valueOf(split[3]));
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
		
		for (String key : map.keySet()) {
			DescriptiveStatistics stat = map.get(key);
			HomophonesResultBean bean = mapBeans.get(key);
			String line = "";	
			
			int count = 0;
			for (int i=0; i<key.length(); i++) {
				count += counts.get(key.charAt(i));
			}
			line += "'" + key + "	" + count + "	" + stat.getMin() + "	" + stat.getMax() + "	" + stat.getMean() + "	";
			line += stat.getStandardDeviation() + "	'" + bean.fullsequence.fullSequence + "	" + bean.run + "	";
			float sigma = (((float)bean.run) - (float)stat.getMean())/(float)stat.getStandardDeviation();
			line += sigma;
			System.out.println(line);
		}
		
	}
	
	public static void main(String[] args) {
		//String cipher = "ABCDEFGHIJKLMNOPQRSTUFVEWXVYZabcdefghXijklmnopqrsPtuJJJUEvWwxkBpJXXaplRcyzRM01Of0XSspL2KCQNDMZJDgo2bJKILTTJ3LGzJMy4qW5i6FRmfdh7XN8QMoAup8yrcMavPJfdJ0wVxWszSFJMtRL8MXGrZXnNpRefgPXJAJUb1JCBHJKdaqcki3liMuJJEGfYRUWakrqORwlEsJ3SDNS51ugwxkEzgF6XOV7JtRJWEIkEBli2JGm8UpXhYWPXkcWJRflH0ZRWvvlsQ4MXqAJkl0zCMJaGPYJzukD6MXWJ0Czk9X8Cxl7ZtFLzRcJEXG1Oa6PMU";
		
		String cipher = Ciphers.cipher[0].cipher;
		//testShuffleStats(4, cipher, 1000, 5);
		
		process(cipher, "/Users/doranchak/projects/zodiac/hom-shuffle-stats-z340-L2-10000.txt", 2);
		
		
	}

}
