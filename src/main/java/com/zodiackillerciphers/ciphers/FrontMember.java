package com.zodiackillerciphers.ciphers;

/** declares utility API for objects within a pareto front for multiobjective optimizations */  
public interface FrontMember {
	/** return simple string representing fitness */
	public String fit();
	/** dump info about this individual */
	public String dump();
	/** html info about this individual */
	public String html();
	/** express the genome */
	public void expressGenome();
}
