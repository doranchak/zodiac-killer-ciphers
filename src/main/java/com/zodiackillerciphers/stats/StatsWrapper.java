package com.zodiackillerciphers.stats;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class StatsWrapper {
	public DescriptiveStatistics stats;
	public double actual;
	public String name;
	public int hits;
	public boolean biggerIsBetter = true;
	public boolean histogram = false;
	
	public Map<Integer, Integer> counts;
	public StatsWrapper() {
		stats = new DescriptiveStatistics();
	}
	public void addValue(double val) {
		if (histogram) {
			if (counts == null) counts = new HashMap<Integer, Integer>();
			Integer count = counts.get((int)val);
			if (count == null) count = 0;
			count++;
			counts.put((int)val, count);
		}
		stats.addValue(val);
		if (biggerIsBetter) { 
			if (val >= actual) hits++;
		} else {
			if (val <= actual) hits++;
		}
	}
	public void output() {
		if (histogram) System.out.println("Histogram: " + counts);
		double sigma = sigma();
		System.out.println(name+ "	" + stats.getMin()
				+ "	" + stats.getMax() + "	" + stats.getMean() + "	"
				+ stats.getPercentile(50) + "	"
				+ stats.getStandardDeviation() + "	" + actual + "	"
				+ sigma + "	" + hits);
	}
	public static String header() {
		return "name	min	max	mean	median	stddev	actual	sigma	hits";
	}
	public double sigma() {
		double sigma = actual-stats.getMean();
		sigma /= stats.getStandardDeviation();
		return sigma;
	}
	
	/** generate stats from a histogram */
	public static void statsFromHistogram(Map<Double, Long> histogram, double actual) {
		double mean = 0;
		long samples = 0;
		double min = 0;
		double max = 0;
		double stddev = 0;
		long hits = 0;
		for (Double key : histogram.keySet()) {
			Long val = histogram.get(key);
			mean += val * key;
			samples += val;
			min = Math.min(min, key);
			max = Math.max(max, key);
			if (key >= actual) {
				hits += val;
			}
		}
		mean /= samples;
		for (Double key : histogram.keySet()) {
			Long val = histogram.get(key);
			double diff = val * (key - mean) * (key - mean);
			stddev += diff;
		}
		stddev /= samples;
		stddev = Math.sqrt(stddev);
		double sigma = actual - mean;
		sigma /= stddev;

		System.out.println(min + "	" + max + "	" + mean + "	" + "	" + stddev + "	" + actual + "	"
				+ sigma + "	" + hits);
	}
	
	public static void processFragmentResults() {
		Map<Double, Long> hist = new HashMap<Double, Long>();
		hist.put(0d, 12957792047l);
		hist.put(1d, 28189424l);
		hist.put(2d, 18527l);
		hist.put(3d,  2l);
		statsFromHistogram(hist, 2);
	}
	public static void main(String[] args) {
		processFragmentResults();
	}
}
