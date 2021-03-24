package com.zodiackillerciphers.tests.unigrams;

import java.util.Set;

public class RegionRepeatsBean {

	public Rectangle rect1;
	public Rectangle rect2;

	/** actual density of shared unigrams */
	public Double density;
	
	public Set<Character> symbols;
	public int positions;
	public int area;

	public Boolean z340;

	/**
	 * expected (predicted) density. to compute: consider ioc for entire cipher.
	 * this is probability of two samples matching.
	 */
	public Double expectedDensity() {
		int L = rect1.height * rect1.width;
		double[] references = z340 ? RegionRepeats.expectedSharedByLengthZ340
				: RegionRepeats.expectedSharedByLengthZ408;
		if (L < references.length)
			return references[L]/L;
		return 0d;
	}

	public String toString() {
		return Math.abs(density - expectedDensity()) + "	" + density + "	"
				+ "	" + expectedDensity() + "	" + rect1 + "	" + rect2 + "	" + rect1.render() + "	" + rect2.render();
	}
	public String symbols() {
		String result = "";
		for (Character ch : symbols) result += ch;
		return result;
	}
}
