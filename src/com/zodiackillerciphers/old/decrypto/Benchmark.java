package com.zodiackillerciphers.old.decrypto;

import java.io.*;

public class Benchmark
{
    static class MyDecryptoListener implements DecryptoMultiSolverListener
    {
	double elapsedTime;

	public void setMessage(String s)
	{
	}

	public void finished(double elapsedTime, boolean timedout)
	{
	    this.elapsedTime = elapsedTime;
	}

	public void decryptoSolution(MapSet mapset, double score)
	{
	}

	public void decryptoPartialSolution(MapSet mapset, double score)
	{
	}

	public void decryptoProgress(double progress)
	{
	}
    }

    static Dictionary dict;
    public static void main(String args[])
    {
	try {

	    main_ex(args);
	} catch (IOException ex) {
	    System.out.println("Exception: "+ex);
	    return;
	}
    }

    static void main_ex(String args[]) throws IOException
    {
	dict = new Dictionary("english-standard.dat");
	BufferedReader ins = new BufferedReader(new FileReader(args[0]));

	String line;
	int test = 0;

	while ((line = ins.readLine())!=null)
	    {
		if (!line.startsWith("#"))
		    continue;
		
		String puzzle = ins.readLine();
		String clues = ins.readLine();

		System.out.printf("%5d",test);
		doRun(puzzle,clues);
		test++;
	    }
    }
    
    static void doRun(String ciphertext, String clues)
    {
	MyDecryptoListener listener = new MyDecryptoListener();

	DecryptoPuzzle puzzle = new DecryptoPuzzle(dict, ciphertext);
	puzzle.handleClues(clues);

	double totalTime = 0;
	int    totalCount = 0;
	double minTime = Double.MAX_VALUE;
	double maxTime = 0;

	while (totalTime < 15 && totalCount < 100)
	    {
		DecryptoMultiSolver dms = new DecryptoMultiSolver(puzzle);
		dms.setPartialSolutionsThreshold(-1);
		dms.addListener(listener);
		dms.run();
		
		totalTime += listener.elapsedTime;
		totalCount++;
		minTime = Math.min(minTime, listener.elapsedTime);
		maxTime = Math.max(maxTime, listener.elapsedTime);
	    }
	System.out.printf("%10.3f %10.3f %10.3f\n", minTime, totalTime/totalCount, maxTime);
    }
}
