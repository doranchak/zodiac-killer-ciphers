package com.zodiackillerciphers.annealing.w168stencil;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;

public class W168StencilThread extends Thread {
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	
	public W168StencilThread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		W168StencilSolution sol = new W168StencilSolution();
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (W168StencilSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new W168StencilThread(startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
