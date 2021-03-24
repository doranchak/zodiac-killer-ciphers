package com.zodiackillerciphers.annealing.namestuffer;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;

public class NamestufferThread extends HomophonicThread {
	String[] words;
	public NamestufferThread(String cipher, String plaintext, double startingTemperature,
			int targetIterations, int threadNum, String[] words) {
		super(cipher, plaintext, startingTemperature, targetIterations, threadNum);
		this.words = words;
	}
	public void run() {
		NamestufferSolution sol = new NamestufferSolution(cipher, plaintext, words);
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (NamestufferSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		result = false;
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new NamestufferThread(cipher, plaintext, startingTemperature, targetIterations, threadNum, words);
		Solver.threads[threadNum].start();
	}
	
}
