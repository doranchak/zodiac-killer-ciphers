package com.zodiackillerciphers.annealing.vigsub;

import java.util.Random;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;

public class VigSubThread extends Thread {
	protected static Random random = new Random();
	protected double startingTemperature = 10000;
	public boolean result;
	public int threadNum;
	public int targetIterations;
	public int vigKeyLength;
	
	public VigSubThread(int vigKeyLength, double startingTemperature,
			int targetIterations, int threadNum) {
		super();
		this.startingTemperature = startingTemperature;
		this.threadNum = threadNum;
		this.targetIterations = targetIterations;
		this.vigKeyLength = vigKeyLength;
	}
	public void say(String msg) {
		System.out.println(threadNum + "	" + msg);
	}
	public void run() {
		VigSubSolution sol = new VigSubSolution(vigKeyLength);
		sol.initialize();
		boolean go = true;
		double bestScore = Double.MAX_VALUE;
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (VigSubSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum, 0);
		result = false;
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new VigSubThread(vigKeyLength, startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
