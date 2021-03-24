package com.zodiackillerciphers.annealing.last18;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;

public class Last18Thread extends Thread {
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	
	public Last18Thread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		Last18Solution sol = new Last18Solution();
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (Last18Solution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new Last18Thread(startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
