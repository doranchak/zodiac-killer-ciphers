package com.zodiackillerciphers.annealing.w168old;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;

public class W168Thread extends Thread {
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	
	public W168Thread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		W168Solution sol = new W168Solution();
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (W168Solution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new W168Thread(startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
