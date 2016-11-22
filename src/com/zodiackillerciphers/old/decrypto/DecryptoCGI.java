package com.zodiackillerciphers.old.decrypto;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.awt.*;
import javax.swing.*;

public class DecryptoCGI
{
    SolutionSet        set;
    GetOpt             opt = new GetOpt();
    Dictionary         dict;
    DecryptoPuzzle     puzzle;
    Random             r = new Random();
    DecryptoMultiSolver dms;

    public class MyDecryptoListener implements DecryptoMultiSolverListener
    {
	void msg(String s)
	{
	    System.out.println("<script>msg(\""+s+"\");</script>");
	}

	public void setMessage(String s)
	{
	    msg(s);
	}

	public void finished(double elapsedTime, boolean timedout)
	{
	    String msg = "";

	    if (timedout)
		msg = String.format("Finished (time limit exceeded after %.3f seconds)",dms.getElapsedTime());
	    else
		msg = String.format("Finished (%.3f seconds)", elapsedTime);

	    if (set.size()==0 && opt.getBoolean("partialthresh")==false)
		msg=msg+" Try enabling partial solutions.";

	    setMessage(msg);
	}

	public void decryptoSolution(MapSet mapset, double score)
	{
	    if (set.wouldAdd(score))
		{
		    Solution sol = new Solution(puzzle, new Map(mapset), score);

		    boolean best = set.size() > 0 ? set.getElement(0).score < score : false;		    
		    boolean added = set.add(sol);

		    if (added)
			{
			    String s = sol.solution;
			    s=s.replace("\"","'");
			    s=s.replace("\n"," ");
			    s=s.replace("\r","").trim();
			    System.out.println(String.format("<script>sol(%.4f,\"%s\");</script>",
							     sol.score/puzzle.words.size(), s));
			}
		}
	}

	public void decryptoPartialSolution(MapSet mapset, double score)
	{
	    if (set.wouldAdd(score))
		{
		    decryptoSolution(mapset, score);
		}
	}

	String lastprg = "";
	public void decryptoProgress(double progress)
	{
	    String newprg = String.format("<script>prg(%.2f);</script>", progress);
	    if (!newprg.equals(lastprg))
		{
		    System.out.println(newprg);
		    lastprg=newprg;
		}
	}
    }

    public static void main(String args[])
    {
	DecryptoCGI app = new DecryptoCGI();
	app.run(args);
    }

    public DecryptoCGI()
    {
	opt.addBoolean('h', "help", false, "Show help");
	opt.addString('d', "dictionary", "english-standard.dat", "Dictionary to use");
	opt.addString('c', "clues", "", "Clues, e.g.: A=C, M=R, JXJ=DAD");
	opt.addBoolean('\0', "silent", false, "Produce no output");
	opt.addInt('\0', "partialthresh", 10, "Accept partial solutions with this many valid words");
	opt.addDouble('\0', "timelimit", 10, "Maximum CPU time");
	opt.addInt('\0', "setsize", 100, "Maximum solutions to store");
	opt.addString('\0',"puzzlefile", "", "File containing the puzzle");
	opt.addBoolean('\0',"scramble", false, "Scramble the puzzle and exit");
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

	set = new SolutionSet(opt.getInt("setsize"));

	String ciphertext;
	String clues;

	if (opt.wasSpecified("puzzlefile"))
	    {
		StringBuffer ct = new StringBuffer();
		StringBuffer cs = new StringBuffer();

		try {
		    BufferedReader ins = new BufferedReader(new FileReader(opt.getString("puzzlefile")));
		    String line;
		    
		    while ((line = ins.readLine())!=null)
			{
			    if (line.contains("="))
				cs.append(line+" ");
			    else
				ct.append(line+" ");
			}
		} catch (IOException ex) {
		    System.out.println("Couldn't read puzzle file: "+ex);
		}

		ciphertext = ct.toString();
		clues = cs.toString();
	    }
	else
	    {
		ArrayList<String> extraArgs = opt.getExtraArgs();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < extraArgs.size(); i++)
		    sb.append(extraArgs.get(i)+" ");

		ciphertext = sb.toString();
		clues = opt.getString("clues");
	    }

	puzzle = new DecryptoPuzzle(dict, ciphertext);
	puzzle.handleClues(clues);

	if (opt.getBoolean("scramble"))
	    {
		puzzle.scramble();
		System.out.println(puzzle.cipherText);
		System.out.println(puzzle.clues);
		return;
	    }

	dms = new DecryptoMultiSolver(puzzle);
	int n = opt.getInt("partialthresh");
	// HACK: change partialthresh according to a crappy heuristic.
	if (n==10)
	    {
		n = 8*puzzle.words.size()/10;
		if (n > 14)
		    n = 14;
	    }

	dms.setPartialSolutionsThreshold(n);
	dms.setTimeLimit(opt.getDouble("timelimit"));

	dms.addListener(new MyDecryptoListener());
	dms.run();

	for (int i = 0; i < set.size(); i++)
	    {
		Solution sol = set.getElement(i);
		String s = sol.solution;
		s=s.replace("\"","'");
		s=s.replace("\n"," ");
		s=s.replace("\r","");
		System.out.println(String.format("<script>solsum(%d,%.3f,\"%s\");</script>",
						 i, sol.score/puzzle.words.size(), s));
	    }

	System.out.println("<script>elapse("+dms.getElapsedTime()+")</script>");
    }
}
