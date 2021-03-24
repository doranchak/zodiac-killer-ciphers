package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class DecryptoMultiSolver
{
    DecryptoPuzzle origpuzzle;
    DecryptoPuzzle puzzle;
    ArrayList<DecryptoMultiSolverListener> listeners = new ArrayList<DecryptoMultiSolverListener>();
    double elapsedTime;
    DecryptoSolver ds;

    long starttime, endtime;

    boolean exit = false;

    RunThread runThread = null;

    int partialSolutionsThreshold = Integer.MAX_VALUE;
    double timeLimit = Double.MAX_VALUE;
    boolean timedout = false;

    public DecryptoMultiSolver(DecryptoPuzzle _puzzle)
    {
	this.puzzle = _puzzle.copy();
	DecryptoSolverOptimizations.removeDuplicateWords(puzzle);
	puzzle.handleClues(puzzle.clues);
	
	DecryptoSolverOptimizations.removeDuplicateWords(puzzle);
	//	DecryptoSolverOptimizations.prioritizeWords(puzzle);
	ds = new DecryptoSolver(this.puzzle, DecryptoParameters.planner);
    }

    public void setPartialSolutionsThreshold(int v)
    {
	partialSolutionsThreshold = v;
	ds.setPartialSolutionsThreshold(v);
    }

    public void setTimeLimit(double s)
    {
	timeLimit = s;
    }

    public void addListener(DecryptoMultiSolverListener listener)
    {
	listeners.add(listener);
	ds.addListener(listener);
    }

    public double getElapsedTime()
    {	
	long t = (endtime == 0) ? System.currentTimeMillis() : endtime;
	    
	return (t - starttime)/1000.0;
    }

    public double getTimeLimit()
    {
	return timeLimit;
    }

    void setMessage(String s)
    {
	for (DecryptoMultiSolverListener listener : listeners)
	    listener.setMessage(s);
    }

    /** start the solver in a new thread. **/
    public void start()
    {
	new TimeoutThread().start();
	runThread = new RunThread();
	runThread.start();
    }

    /** start the solver and wait for it to finish **/
    public void run()
    {
	start();
	try {
	    runThread.join();
	} catch (InterruptedException ex) {
	    System.out.println("Couldn't join!");
	}	
    }

    /** stop the solver thread and wait for it to actually exit. **/
    public void stop()
    {
	requestStop();
	try {
	    runThread.join();
	} catch (InterruptedException ex) {
	    System.out.println("Couldn't join!");
	}
	    
    }

    class TimeoutThread extends Thread
    {
	public TimeoutThread()
	{
	    setDaemon(true);
	}

	public void run()
	{
	    try {
		Thread.sleep((int) (timeLimit*1000));
		requestStop();
	    } catch (InterruptedException ex) {
	    }
	}

    }

    class RunThread extends Thread
    {
	public void run()
	{
	    run_ex();
	}
    }

    void run_ex()
    {
	starttime = System.currentTimeMillis();
	endtime = 0;
	
	do {	    
	    setMessage("Doing initial search...");

	    ds.solve();
		
	    // if we got a solution, stop now.
	    if (ds.gotFullSolution)
		break;

	    // otherwise, let's try it again with more conservative settings.

	    setMessage("Non-dictionary words exist... trying elimination method.");

	    // remember which words were originally enabled
	    boolean origenabled[] = new boolean[puzzle.words.size()];
	    for (int i = 0; i < puzzle.words.size(); i++)
		origenabled[i] = puzzle.words.get(i).enabled;

	    // try disabling 1 word, 2 words, 3 words, ...
	    doRecursiveSearch();

	} while (false);

	endtime = System.currentTimeMillis();

	boolean timedout = getElapsedTime() > timeLimit;

	for (DecryptoMultiSolverListener listener : listeners)
	    listener.finished(getElapsedTime(), timedout);
    }

    void doRecursiveSearch()
    {
	// how many words are we disabling now?
	for (int maxdepth = 1; maxdepth <= Math.min(DecryptoParameters.maxRandomWordDisable,  puzzle.words.size()-1); maxdepth++)
	    {
		boolean stop = recurseHelper(0, maxdepth, 0, new int[puzzle.words.size()]);
		if (stop)
		    return;
	    }

	Planner oldplanner = ds.planner;
	boolean oldPruneRootNode = DecryptoParameters.pruneRootNode;
	boolean oldPruneEachNode = DecryptoParameters.pruneEachNode;
	
	DecryptoParameters.pruneRootNode = false;
	DecryptoParameters.pruneEachNode = false;

	ds.setPlanner(new RandomPlanner());
	
	setMessage("Using random planner");

	while (!exit)
	    {
		ds.reset(puzzle);
		ds.solve();

		if (ds.gotFullSolution)
		    break;
	    }

	ds.planner = oldplanner;
	DecryptoParameters.pruneRootNode = oldPruneRootNode;
	DecryptoParameters.pruneEachNode = oldPruneEachNode;
    }

    /** Suppose there are five words and we're disabling 2...
     * We would disable them as follows:
     *
     * (1,2) (1,3) (1,4) (1,5)
     * (2,3) (2,4) (2,5)
     * (3,4) (3,5)
     * (4,5)
     *
     * So we iterate over the words in order (i = 1-4), then
     * recursively iterate from (i+1, end).
     * 
     * We maintain a list of disabled words, disabledWords[] such that
     * disabledWords[i] is the ith disabled word. (-1 means no entry).
     **/
    boolean recurseHelper(int depth, int maxdepth, int lowword, int disabledWords[])
    {

	// if we've disabled the right number of words, then try solving.
	if (depth >= maxdepth)
	    {
		return doSolve(disabledWords, depth);
	    }

	// we need to disable more words
	boolean gotsolution = false;

	// disable all of the words that are left, one at a time
	for (int wordnum = lowword; wordnum < puzzle.words.size(); wordnum++)
	    {
		disabledWords[depth] = wordnum;

		// if this word is already disabled (by the user), don't search.
		if (!puzzle.words.get(wordnum).enabled)
		    continue;

		puzzle.words.get(wordnum).enabled = false;
		if (!exit)
		    gotsolution = gotsolution | recurseHelper(depth + 1, maxdepth, wordnum + 1, disabledWords);
		puzzle.words.get(wordnum).enabled = true;
	    }

	disabledWords[depth] = -1;
	return gotsolution;
    }

    // solve, given a single set of disabled words.
    boolean doSolve(int disabledWords[], int depth)
    {
	StringBuffer ignoredWords = new StringBuffer();

	// Mark the appropriate words as disabled
	for (int i = 0; i < depth; i++)
	    {
		ignoredWords.append(puzzle.lang.bytesToString(puzzle.words.get(disabledWords[i]).word));
		if (depth > 1 && i < (depth-1))
		    ignoredWords.append(", ");
	    }

	setMessage("Re-solving with disabled words: "+ignoredWords.toString());

	ds.reset(puzzle);
	ds.solve();

	return ds.gotFullSolution;
    }

    public void requestStop()
    {
	exit = true;
	ds.stop();
    }

}
