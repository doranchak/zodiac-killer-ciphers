package com.zodiackillerciphers.annealing.onetimepad;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;

public class OTPThread extends Thread {
	
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	private String[] ciphers;
	private int keyLength;
	private boolean doOffsets;
	public OTPThread(String[] ciphers, int keyLength, boolean doOffsets, double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
		this.ciphers = ciphers;
		this.keyLength = keyLength;
		this.doOffsets = doOffsets;
	}
	public void run() {
		OTPSolution sol = new OTPSolution(ciphers, keyLength, doOffsets);
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (OTPSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new OTPThread(ciphers, keyLength, doOffsets, startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
