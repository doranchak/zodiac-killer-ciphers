package com.zodiackillerciphers.homophones;

public class HomophoneStatBean {
	// max number of times the cycle repeats without interruption 
	public int run;
	// relative probability that this will occur by chance (based on symbol frequencies) 
	public float probability;
	// ratio of length of unbroken cycles to length of full sequence of these L symbols
	public float percent;
}
