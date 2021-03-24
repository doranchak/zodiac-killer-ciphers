package com.zodiackillerciphers.old.decrypto;

import java.io.*;
import java.util.*;

public class DecryptoSolver
{
    DecryptoPuzzle puzzle;

    boolean autoDisableWords = false;

    ArrayList<DecryptoListener> listeners = new ArrayList<DecryptoListener>();
    ArrayList<Map> maps;

    public boolean gotFullSolution;

    int partialSolutionsThreshold = 10;

    boolean stopFlag = false;

    Planner planner;

    /** An array of word indices such that wordlist[0..solvedwords-1] are solved for and
	the rest are not. **/
    int wordlist[];
    int solvedwords;

    public DecryptoSolver(DecryptoPuzzle puzzle, Planner planner)
    {
	this.puzzle = puzzle;

	this.planner = planner.make(puzzle);
    }

    public void setPlanner(Planner p)
    {
	this.planner = p.make(puzzle);
    }

    public void setPartialSolutionsThreshold(int minwords)
    {
	if (minwords < 0)
	    minwords = Integer.MAX_VALUE;
	    
	this.partialSolutionsThreshold = minwords;
    }

    public void reset(DecryptoPuzzle puzzle)
    {
	this.puzzle = puzzle;
	reportdepth = 0;
	reportunits = 0;
    }

    public void stop()
    {
	stopFlag = true;
    }

    public void addListener(DecryptoListener listener)
    {
	if (!listeners.contains(listener))
	    listeners.add(listener);
    }

    public void solve()
    {
	stopFlag = false;
	gotFullSolution = false;

	MapSet map = new MapSet(puzzle.lang.letterCount());
	map.setFullSet();

	for (int i = 0; i < puzzle.lang.letterCount(); i++)
	    {
		int v = puzzle.initialMap.get(i);
		if (v >= 0)
		    {
			map.setMapping((byte) i, (byte) v);
		    }
	    }

	wordlist = new int[puzzle.words.size()];
	for (int i = 0; i < puzzle.words.size(); i++)
	    wordlist[i] = i;
	solvedwords = 0;
	
	if (DecryptoParameters.pruneRootNode)
	    reduceCandidates(map);

	solve(0, map);

	// restore the candidates
	for (int i = 0; i < puzzle.words.size(); i++)
	    puzzle.words.get(i).firstcand = 0;
    }

    /** truncate score to about 5 significant digits in order to prevent rounding errors from
	causing identical solutions to appear different **/
    protected double roundScore(double in)
    {
	long T = 1<<18;
	long s = (long) (in * T);

	return ((double) s)/T;
    }

    protected void reportSolution(MapSet set)
    {
	gotFullSolution = true;

	double score = puzzle.lang.computeScore(puzzle.cipherBytes, set);

	for (DecryptoListener listener : listeners)
	    listener.decryptoSolution(set, roundScore(score));
    }

    protected void reportPartialSolution(MapSet set)
    {
	if (solvedwords < partialSolutionsThreshold)
	    return;

	double score = puzzle.lang.computeScore(puzzle.cipherBytes, set);

	for (DecryptoListener listener : listeners)
	    listener.decryptoPartialSolution(set, roundScore(score));
	
    }

    static void swap(int array[], int j, int k)
    {
	int tmp = array[j];
	array[j] = array[k];
	array[k] = tmp;
    }

    static int indexOf(int array[], int v)
    {
	for (int i = 0; i < array.length; i++)
	    if (array[i]==v)
		return i;

	return -1;
    }

    protected void solve(int recursiondepth, MapSet map)
    {
	if (stopFlag)
	    return;

	int action = planner.getAction(wordlist, solvedwords, map);
	
	if (action == -1)
	    {
		reportSolution(map);
		return;
	    }
	
	if (action == -2) {
	    return;
	}

	//////////////////////// LETTER ///////////////////////
	if ((action & Planner.LETTER_FLAG)!=0) {
	    byte a[] = new byte[1], b[] = new byte[1];
	    a[0] = (byte) (action & Planner.MASK);
	    
	    reportProgressSize(recursiondepth, puzzle.lang.letterCount());

	    // this can be accelerated. See SpaceDecryptoSolver
	    int wordstate[] = new int[puzzle.words.size()];
	    MapSet ourmap = map.copy();
	    saveWordState(wordstate);
	    
	    for (int c = 0; c < puzzle.lang.letterCount(); c++)
		{
		    reportProgress(recursiondepth, c);
		    
		    b[0] = (byte) c;
		    
		    if (ourmap.isMappingOkay(a, b))
			{
			    ourmap.setMappings(a, b);
			    
			    // We MUST reduce candidates when substituting a single letter.
			    // That's the whole point...
			    // if (DecryptoParameters.pruneEachNode)
			    reduceCandidates(ourmap); // don't increment wordnum
			    
			    solve(recursiondepth+1, ourmap);
			    restoreWordState(wordstate);
			    ourmap.setTo(map);
			}
		}
	    return;
	}

	//////////////////////// WORD ///////////////////////
	if ((action & Planner.WORD_FLAG)!=0) {
	    int wordnum = action & Planner.MASK;

	    swap(wordlist, indexOf(wordlist, wordnum), solvedwords);
	    solvedwords++;

	    Word w = puzzle.words.get(wordnum);

	    if (!w.enabled)
		{
		    solve(recursiondepth+1, map);
		}
	    else
		{
		    // this can be accelerated. See SpaceDecryptoSolver
		    int wordstate[] = new int[puzzle.words.size()];
		    MapSet ourmap = map.copy();
		    saveWordState(wordstate);
	    
		    boolean leaf = true;
	    
		    reportProgressSize(recursiondepth, w.candidates.length - w.firstcand);
	    
		    for (int c = w.firstcand; c < w.candidates.length; c++)
			{
			    if (stopFlag)
				return;
		    
			    reportProgress(recursiondepth, c - w.firstcand);
		    
			    Candidate candidate = w.candidates[c];
		    
			    // if this word can't map successfully, try the next word.
			    if (!ourmap.isMappingOkay(w.word, candidate.cand))
				continue;
		    
			    // we found a match; we're not a leaf anymore.
			    leaf = false;
		    
			    // assume that this word maps to candidate.
			    ourmap.setMappings(w.word, candidate.cand);
		    
			    if (DecryptoParameters.pruneEachNode)
				reduceCandidates(ourmap);
		    
			    solve(recursiondepth+1, ourmap);
			    restoreWordState(wordstate);
		    
			    // we tampered with our map; restore it back to its
			    // original configuration so we can try a different mapping
			    ourmap.setTo(map);
			}
	    
		    /* we're at a leaf. We report partial solutions if either
		       they've requested it, or if we've already solved for a lot
		       of words. */
		    if (leaf)
			reportPartialSolution(ourmap);
		}

	    solvedwords--;
	    // unnecessary?
	    //	    swap(wordlist, wordnum, solvedwords);
	    return;
	}
	System.out.println("Planner error: No action specified (need WORD or LETTER flag).");
    }

    /** Remember which candidates which were enabled/disabled **/
    protected void saveWordState(int state[])
    {
	for (int i = 0; i < puzzle.words.size(); i++)
	    state[i] = puzzle.words.get(i).firstcand;
    }
    
    /** Restore which candidates which were enabled/disabled **/
    protected void restoreWordState(int state[])
    {
	for (int i = 0; i < puzzle.words.size(); i++)
	    puzzle.words.get(i).firstcand = state[i];
    }

    protected void reduceCandidates(MapSet set)
    {
	MapSet newset = new MapSet(puzzle.lang.letterCount());

	int iters = 0;

	boolean dirty = true;

	while (dirty && !stopFlag && iters < DecryptoParameters.maxReduceIterations)
	    {
		dirty = false;

		// take a look at every not-yet-solved cipherword
		for (int wordnum = solvedwords; wordnum < puzzle.words.size(); wordnum++)
		    {
			Word w = puzzle.words.get(wordlist[wordnum]);
			if (!w.enabled)
			    continue;

			newset.setEmptySet();

			for (int c = w.firstcand; c < w.candidates.length; c++)
			    {
				Candidate candidate = w.candidates[c];
				
				if (set.isMappingOkay(w.word, candidate.cand))
				    {
					newset.enableMappings(w.word, candidate.cand);
				    }
				else
				    {
					// shuffle this candidate to
					// index "firstcand", then
					// increase "firstcand". We've
					// already processed the
					// element at "firstcand", so
					// we don't have to worry
					// about missing it.
					Candidate tmp = w.candidates[w.firstcand];
					w.candidates[w.firstcand]=candidate;
					w.candidates[c] = tmp;
					w.firstcand++;

					dirty = true;
				    }
			    }

			set.intersectWith(newset);
		    }

		// can we reduce the set by any other logic?
		// i.e., if X={B,C} and Y={B,C}, then no other letter can map to B or C.
		if (dirty)
		    set.selfReduce();

		iters++;
	    }
    }

    int reportunits = 0;
    int reportdepth = 0;;
    static final int MAXREPORTDEPTH = 10;
    int workprogress[] = new int[MAXREPORTDEPTH];
    int worksize[] = new int[MAXREPORTDEPTH];


    protected void reportProgressSize(int depth, int amount)
    {
	if (reportunits < 1024 && depth>reportdepth && depth < MAXREPORTDEPTH)
	    {
		reportdepth = depth;
		if (reportunits == 0)
		    reportunits = amount;
		else
		    reportunits *= amount;
	    }
	
	if (depth <= reportdepth)
	    worksize[depth] = amount;
    }

    protected void reportProgress(int depth, int amount)
    {
	if (depth <= reportdepth)
	    {
		workprogress[depth] = amount;

		double frac = 1.0;
		double prog = 0;
		for (int i = 0; i <= depth; i++)
		    {
			prog += (frac*workprogress[i])/worksize[i];
			frac /= worksize[i];
		    }

		for (DecryptoListener listener : listeners)
		    listener.decryptoProgress(prog);
	    }
    }

}
