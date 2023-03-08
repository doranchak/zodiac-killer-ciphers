package com.zodiackillerciphers.annealing.pollux;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;

public class PolluxThread extends Thread {
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	
	public PolluxThread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		PolluxSolution sol = new PolluxSolution();
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (PolluxSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new PolluxThread(startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
