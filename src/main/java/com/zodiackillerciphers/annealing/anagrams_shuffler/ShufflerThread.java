package com.zodiackillerciphers.annealing.anagrams_shuffler;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;

public class ShufflerThread extends Thread {
	private String inputString;
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	public ShufflerThread(String inputString, double startingTemperature,
			int targetIterations, int threadNum) {
		this.inputString = inputString;
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		ShufflerSolution sol = new ShufflerSolution(inputString);
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (ShufflerSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new ShufflerThread(inputString, startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
