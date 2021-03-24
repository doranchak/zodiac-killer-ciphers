package com.zodiackillerciphers.annealing.cycles;

import java.util.Random;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;

public class CycleThread extends Thread {
	static Random random = new Random();
	String cipher;
	double startingTemperature = 10000;
	public boolean result;
	public int threadNum;
	public int targetIterations;
	
	public CycleThread(String cipher, double startingTemperature,
			int targetIterations, int threadNum) {
		super();
		this.cipher = cipher;
		this.startingTemperature = startingTemperature;
		this.threadNum = threadNum;
		this.targetIterations = targetIterations;
	}
	public void say(String msg) {
		System.out.println(threadNum + "	" + msg);
	}
	public void run2() {
		CycleSolution cs = new CycleSolution(Ciphers.Z340);
		cs.initialize();
		double bestEnergy = Double.MAX_VALUE;
		int count = 0;
		while (true) {
			cs.mutate();
			count++;
			if (count % 100 == 0) System.out.println("count: " + count);
			double currentEnergy = cs.energy();
			if (currentEnergy < bestEnergy) {
				bestEnergy = currentEnergy;
				System.out.println("NEW BEST " + cs);
				cs.mutateReverseClear();
				count = 0;
			} else {
				cs.mutateReverse();
			}
			if (count == 3000) {
				System.out.println("No best in 3000 iterations so quitting.");
				break;
			}
		}
		finish();
	}
	public void run() {
		CycleSolution sol = new CycleSolution(cipher);
		sol.initialize();
		boolean go = true;
		double bestScore = Double.MAX_VALUE;
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (CycleSolution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		result = false;
		finish();
	}
	
	public void finish() {
		CycleTest.threads[threadNum] = new CycleThread(cipher, startingTemperature, targetIterations, threadNum);
		CycleTest.threads[threadNum].start();
	}
	
}
