package com.zodiackillerciphers.annealing.concealment;

import java.util.Random;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;

public class ConcealmentCipherThread extends Thread {
	protected static Random random = new Random();
	protected double startingTemperature = 10000;
	public boolean result;
	public int threadNum;
	public int targetIterations;
	public StringBuffer cipher;
	
	public ConcealmentCipherThread(StringBuffer cipher, double startingTemperature,
			int targetIterations, int threadNum) {
		super();
		this.startingTemperature = startingTemperature;
		this.threadNum = threadNum;
		this.targetIterations = targetIterations;
		this.cipher = cipher;
	}
	public void say(String msg) {
		System.out.println(threadNum + "	" + msg);
	}
	public void run() {
		ConcealmentCipher sol = new ConcealmentCipher(cipher);
		sol.initialize();
		boolean go = true;
		double bestScore = Double.MAX_VALUE;
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (ConcealmentCipher) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum, 0);
		result = false;
		finish();
	}
	
	public void finish() {
		Solver.threads[threadNum] = new ConcealmentCipherThread(cipher, startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}
	
}
