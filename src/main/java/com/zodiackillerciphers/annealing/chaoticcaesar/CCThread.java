package com.zodiackillerciphers.annealing.chaoticcaesar;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;

public class CCThread extends Thread {
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	
	public CCThread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		CCSolution sol = new CCSolution();
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (CCSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new CCThread(startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
