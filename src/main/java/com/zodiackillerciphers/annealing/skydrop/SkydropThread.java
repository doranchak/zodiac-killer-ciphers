package com.zodiackillerciphers.annealing.skydrop;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;

public class SkydropThread extends Thread {
	private String inputString;
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	public SkydropThread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		SkydropSolution sol = new SkydropSolution();
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (SkydropSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new SkydropThread(startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
