package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.List;

/** used to track best results when scanning multiple periods */ 
public class RepeatingFragmentsFasterResultsBean {
	public int bestPeriod = 1;
	public double bestScore = Double.MIN_VALUE;
	public List<Double> distribution = new ArrayList<Double>();

	

}
