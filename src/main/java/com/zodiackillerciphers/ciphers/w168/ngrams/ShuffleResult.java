package com.zodiackillerciphers.ciphers.w168.ngrams;

import java.util.Arrays;

public class ShuffleResult implements Comparable {
	public double sigma;
	public double value;
	public int ngramSize;
	public boolean reverse;
	public int[] periods;
	public int length;

	public ShuffleResult(double sigma, double value, int ngramSize, boolean reverse, int[] periods, int length) {
		super();
		this.sigma = sigma;
		this.value = value;
		this.ngramSize = ngramSize;
		this.reverse = reverse;
		this.length = length;
		this.periods = new int[periods.length]; // make a copy
		for (int i = 0; i < periods.length; i++)
			this.periods[i] = periods[i];
	}

	@Override
	public int compareTo(Object o) {
		ShuffleResult result1 = this;
		ShuffleResult result2 = (ShuffleResult) o;
		int comp = Double.compare(result1.sigma, result2.sigma);
		if (comp == 0)
			return Arrays.toString(result1.periods).compareTo(Arrays.toString(result2.periods));
		return comp;
	}
	
	public String toString() {
		String tab = "	";
		return sigma + tab + value + tab + ngramSize + tab + length + tab + reverse + tab + Arrays.toString(periods);
	}

}
