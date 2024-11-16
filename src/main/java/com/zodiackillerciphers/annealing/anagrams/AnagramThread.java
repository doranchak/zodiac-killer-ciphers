package com.zodiackillerciphers.annealing.anagrams;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;

public class AnagramThread extends Thread {
	private String inputString;
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	public AnagramThread(String inputString, double startingTemperature,
			int targetIterations, int threadNum) {
		this.inputString = inputString;
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		AnagramSolution sol = new AnagramSolution(inputString);
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (AnagramSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new AnagramThread(inputString, startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
