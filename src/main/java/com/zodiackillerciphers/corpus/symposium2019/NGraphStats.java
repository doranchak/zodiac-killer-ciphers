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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;

public class NGraphStats {
	
	/** generate hexagraph statistics from generated ciphers.  automatically standardizes all ciphertexts to the same alphabet. */
	public static void ngraphStatsFor(String pathInput, String pathOutput, int n) {
		BufferedReader input = null;
		Map<String, Long> counts = new HashMap<String, Long>();
		int count = 0;
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(pathInput), "UTF-8"));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String cipher = line.substring(0, 340);
				cipher = Ciphers.fromNumeric(Ciphers.toNumeric(cipher, false), false, 5);
				
				for (int i=0; i<cipher.length()-n+1; i++) {
					String ngraph = cipher.substring(i, i+n);
					Long val = counts.get(ngraph);
					if (val == null) val = 0l;
					val++;
					counts.put(ngraph, val);
				}
				count++;
				if (count % 10000 == 0) System.out.println("Read " + count);
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
	
		count = 0;
		try {
			File fout = new File(pathOutput);
			FileOutputStream fos = new FileOutputStream(fout);
	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			System.out.println("Writing...");
			for (String key : counts.keySet()) {
				Long val = counts.get(key);
				if (val < 2) continue;
				double log = Math.log10(val);
				bw.write(key + "	" + log);
				bw.newLine();
				count++;
			}
	
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		System.out.println("Done writing.");
		
	}
	public static Map<String, Double> ngraphsMapFor(String path) {
		System.out.println("loading stats from " + path);
		BufferedReader input = null;
		Map<String, Double> map = new HashMap<String, Double>();
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String[] split = line.split("	");
				String key = split[0];
				Double val = Double.valueOf(split[1]);
				map.put(key, val);
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
		System.out.println("done loading stats");
		return map;
	}
	
	/** calculate an ngraph score for the given cipher, using the given ngraph stats map */
	public static double ngraphScoreFor(String cipher, Map<String, Double> map) {
		double sum = 0;
		int n = 0;
		for (String key : map.keySet()) {
			n = key.length();
			break;
		}
		for (int i=0; i<cipher.length()-n+1; i++) {
			String ngraph = cipher.substring(i, i+n);
			Double val = map.get(ngraph);
			if (val != null) 
				sum += map.get(ngraph);
		}
		return sum;
	}
	
	/** output the differences between these two ngraph maps */
	public static void differences(Map<String, Double> map1, Map<String, Double> map2) {
		for (String key : map1.keySet()) {
			Double val1 = map1.get(key);
			Double val2 = map2.get(key);
			if (val2 == null) val2 = 0d;
			System.out.println(Math.abs(val1-val2) + " " + key);
		}
		for (String key : map2.keySet()) {
			if (map1.containsKey(key)) continue;
			System.out.println(map2.get(key) + " " + key);
		}
	}
	
	/** compute average ngraph scores for each kind of cipher */
	public static void scoreStats() {
		int nMax=5;
		for (String pathInput : new String[] {
				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/100,000-ciphers-with-stats-02-col-only.csv",				
				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/100,000-ciphers-with-stats-02-hom-only.csv"				
		}) {
			System.out.println("================= " + pathInput);
			Map<Integer, Map<String, Double>> ngraphMapsColumnar = new HashMap<Integer, Map<String, Double>>();
			for (int n = 2; n <= nMax; n++) {
				ngraphMapsColumnar.put(n,
						ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/" + n
								+ "graph-stats-columnar.txt"));
			}
			Map<Integer, Map<String, Double>> ngraphMapsHomophonic = new HashMap<Integer, Map<String, Double>>();
			for (int n = 2; n <= nMax; n++) {
				ngraphMapsHomophonic.put(n,
						ngraphsMapFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/" + n
								+ "graph-stats-homophonic.txt"));
			}
			
			Map<Integer, DescriptiveStatistics> statsColumnar = new HashMap<Integer, DescriptiveStatistics>();
			for (int n = 2; n <= nMax; n++) {
				statsColumnar.put(n, new DescriptiveStatistics());
			}
			
			Map<Integer, DescriptiveStatistics> statsHomophonic = new HashMap<Integer, DescriptiveStatistics>();
			for (int n = 2; n <= nMax; n++) {
				statsHomophonic.put(n, new DescriptiveStatistics());
			}
			
			BufferedReader input = null;
			int count = 0;
			try {
				input = new BufferedReader(new InputStreamReader(new FileInputStream(pathInput), "UTF-8"));
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					count++;
					String cipher = line.substring(0, 340);
					for (int n = 2; n <= nMax; n++) {
						statsColumnar.get(n).addValue(ngraphScoreFor(cipher, ngraphMapsColumnar.get(n)));
						statsHomophonic.get(n).addValue(ngraphScoreFor(cipher, ngraphMapsHomophonic.get(n)));
					}
					if (count % 10000 == 0) System.out.println(count + "...");
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
			for (int n = 2; n <= nMax; n++) {
				DescriptiveStatistics statsCol = statsColumnar.get(n);
				DescriptiveStatistics statsHom = statsHomophonic.get(n);
				System.out.println("n=" + n + " columnar mean " + statsCol.getMean() + " stddev " + statsCol.getStandardDeviation());
				System.out.println("n=" + n + " homophonic mean " + statsHom.getMean() + " stddev " + statsHom.getStandardDeviation());
			}
		}
		
	}
	
	public static void main(String[] args) {
//		for (int n=2; n<=5; n++) {
//			System.out.println("===== n "+ n);
//			NGraphStats.ngraphStatsFor("/Users/doranchak/projects/zodiac/alphabet-normalized-for-hexagraph-stats-homophonic.txt", "/Users/doranchak/projects/zodiac/" + n + "graph-stats-homophonic.txt", n);
//			NGraphStats.ngraphStatsFor("/Users/doranchak/projects/zodiac/alphabet-normalized-for-hexagraph-stats-columnar.txt", "/Users/doranchak/projects/zodiac/" + n + "graph-stats-columnar.txt", n);
//			NGraphStats.ngraphStatsFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/100,000-ciphers-with-stats-02-hom-only.csv", "/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/" + n + "graph-stats-homophonic.txt", n);
//			NGraphStats.ngraphStatsFor("/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/100,000-ciphers-with-stats-02-col-only.csv", "/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/fixed-alphabet-size/" + n + "graph-stats-columnar.txt", n);
//		}
		scoreStats();
//		differences(ngraphsMapFor(
//				"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/2graph-stats-homophonic.txt"),
//				ngraphsMapFor(
//						"/Users/doranchak/projects/deep-learning-ec2/symposium2019/ngraph-statistics/2graph-stats-columnar.txt"));
				
	}

}
