package com.zodiackillerciphers.annealing.w168periods;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;
import com.zodiackillerciphers.ciphers.w168.StringUtils;
import com.zodiackillerciphers.ciphers.w168.W168;

public class W168PeriodsThread extends Thread {
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	
	public W168PeriodsThread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		W168PeriodsSolution sol = new W168PeriodsSolution(3, StringUtils.toStringBuilder(W168.Z408_1));
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (W168PeriodsSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		SolverPeriods.threads[threadNum] = new W168PeriodsThread(startingTemperature, targetIterations, threadNum);
		SolverPeriods.threads[threadNum].start();
	}
	
}
