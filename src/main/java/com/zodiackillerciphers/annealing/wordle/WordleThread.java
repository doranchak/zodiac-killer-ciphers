package com.zodiackillerciphers.annealing.wordle;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;

public class WordleThread extends Thread {
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	
	public WordleThread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		WordleSolution sol = new WordleSolution();
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (WordleSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new WordleThread(startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
