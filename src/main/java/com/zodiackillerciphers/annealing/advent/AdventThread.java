package com.zodiackillerciphers.annealing.advent;

import java.util.Random;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;

public class AdventThread extends Thread {
	static Random random = new Random();
	String cipher;
	double startingTemperature = 10000;
	public boolean result;
	public int threadNum;
	public int targetIterations;
	
	public AdventThread(int threadNum) {
		super();
		this.threadNum = threadNum;
	}
	
	public void run() {
		AdventSolution sol = new AdventSolution();
		sol.initialize();
		boolean go = true;
		double bestScore = Double.MAX_VALUE;
		double temperature = 10000;
		SimulatedAnnealing.extendIterations = true;
		sol = (AdventSolution) SimulatedAnnealing.run(temperature, 10000000, sol, threadNum, sol.energy());
		finish();
	}
	
	public void finish() {
		System.out.println("Thread " + threadNum + " done.  Restarting.");
		AdventTest.threads[threadNum] = new AdventThread(threadNum);
		AdventTest.threads[threadNum].start();
	}
	
}
