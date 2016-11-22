package com.zodiackillerciphers.old.decrypto;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.awt.*;
import javax.swing.*;

public class DecryptoCLI
{
    String             cipherText;
    SolutionSet        set = new SolutionSet(10);
    GetOpt             opt = new GetOpt();
    Dictionary         dict;
    DecryptoPuzzle     puzzle;
    Random             r = new Random();
    DecryptoMultiSolver dms;

    public class MyDecryptoListener implements DecryptoMultiSolverListener
    {
	public void setMessage(String s)
	{
	    System.out.println(s);
	}

	public void finished(double elapsedTime, boolean timedout)
	{
	}

	public void decryptoSolution(MapSet mapset, double score)
	{
	    if (set.wouldAdd(score))
		{
		    Solution sol = new Solution(puzzle, new Map(mapset), score);

		    boolean added = set.add(sol);
		    if (added && !opt.getBoolean("silent"))
			System.out.println(String.format("%8.3f : %s",sol.score, sol.solution));
		}
	}

	public void decryptoPartialSolution(MapSet mapset, double score)
	{
	    if (set.wouldAdd(score))
		{
		    decryptoSolution(mapset, score);
		}
	}

	public void decryptoProgress(double progress)
	{
	    //System.out.print(progress*100+" %\r");
	    System.out.print(String.format("[%6.2f%%]\r", progress*100));
	}
    }

    public static void main(String args[])
    {
	DecryptoCLI cli = new DecryptoCLI();
	cli.run(args);
    }

    public DecryptoCLI()
    {
	opt.addBoolean('h', "help", false, "Show help");
	opt.addString('d', "dictionary", "english-standard.dat", "Dictionary to use");
	opt.addString('c', "clues", "", "Clues, e.g.: A=C, M=R, JXJ=DAD");
	opt.addBoolean('\0', "silent", false, "Produce no output");
	opt.addBoolean('\0', "partials", false, "Allow partial solutions immediately");
    }

    public void run(String args[])
    {
	opt.parse(args);

	if (opt.getBoolean("help"))
	    {
		opt.doHelp();
		return;
	    }

	try {
	    dict = new Dictionary(opt.getString("dictionary"));
	} catch (IOException ex) {
	    System.out.println("Couldn't open dictionary: "+ex);
	    return;
	}

	if (false)
	    {
		JFrame frame = new JFrame("TreeList Widget");
		frame.setLayout(new BorderLayout());
		frame.add(set.set.getWidget(), BorderLayout.CENTER);
		frame.setSize(1000,400);
		frame.setVisible(true);
		set.set.animateDelay = 1000;
	    }

	ArrayList<String> extraArgs = opt.getExtraArgs();
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < extraArgs.size(); i++)
	    sb.append(extraArgs.get(i)+" ");

	cipherText = sb.toString();

	puzzle = new DecryptoPuzzle(dict, cipherText);
	DecryptoMultiSolver dms = new DecryptoMultiSolver(puzzle);
	dms.addListener(new MyDecryptoListener());
	dms.run();

	int n = 10;
	if (set.size() < n)
	    n = set.size();

	System.out.println("Best "+n+" solutions: ");

	for (int i = 0; i < n; i++)
	    {
		Solution sol = set.getElement(i);
		System.out.println(String.format("%8.3f : %s",sol.score, sol.solution));
	    }

	System.out.println("elapsed time: "+dms.getElapsedTime()+" seconds");
	//	System.out.println(String.format("elapsed time: %.3f seconds", elapsedTime));
    }
}
