package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class NextBestPlanner implements Planner
{
    DecryptoPuzzle puzzle;
    //    int wordorder[];

    static final int METHOD_LINEAR=1, METHOD_POW=2, METHOD_EXPERIMENTAL=3;
    static int METHOD = METHOD_LINEAR;

    public NextBestPlanner()
    {
    }

    public Planner make(DecryptoPuzzle puzzle)
    {
	NextBestPlanner p = new NextBestPlanner();
	p.puzzle = puzzle;

	return p;
    }

    public int getAction(int wordlist[], int solvedwords, MapSet map)
    {
	// are we done?
	if (solvedwords == wordlist.length)
	    return -1;

	double bestcost = Double.MAX_VALUE;
	int bestword = solvedwords;

	for (int i = solvedwords; i < wordlist.length; i++) {
	    Word w = puzzle.words.get(wordlist[i]);

	    if (!w.enabled)
		continue;

	    int letterCount = 0;
	    for (int j = 0; j < w.uniqueletters.length; j++)
		if (!map.isMapped(w.uniqueletters[j]))
		    letterCount += puzzle.histogram[w.uniqueletters[j]];

	    if (letterCount == 0)
		continue;

	    // this word has no candidates left!
	    if (w.candidates.length - w.firstcand == 0)
		return -2;

	    double thiscost = cost(w.uniqueletters.length, letterCount, (w.candidates.length - w.firstcand));

	    if (thiscost < bestcost)
		{
		    bestcost = thiscost;
		    bestword = i;
		}

	    //	    System.out.printf("\n%12s %4d %8d %f",w.text, letterCount, w.candidates.length - w.firstcand, thisscore);
	}

	if (bestword == -1)
	    return -2;

	if (DecryptoParameters.enableSingleLetters) {
	    for (int i = 0; i < puzzle.historder.length; i++) {

		//		System.out.print(puzzle.lang.byteToChar(puzzle.historder[i]));

		if (map.isMapped(puzzle.historder[i]))
		    continue;
		byte letter = puzzle.historder[i];
		int count = puzzle.histogram[letter];
		if (count == 0)
		    break;

		double thiscost = cost(1, 1, puzzle.lang.letterCount());

		if (thiscost < bestcost) {
		    //   System.out.println("Going with letter: "+solvedwords+" "+puzzle.lang.byteToChar(letter));
		    return letter | LETTER_FLAG;
		}
		break;
	    }
	}

	if (false) {
	    System.out.printf("\n\nPicking %s\n\n", puzzle.words.get(wordlist[bestword]).text);
	}

	return wordlist[bestword] | WORD_FLAG;
    }

    static final double cost(int uniqueletters, int lettercount, int ncand)
    {
	    switch (METHOD)
		{
		case METHOD_LINEAR:
		    return ncand / lettercount;
		case METHOD_POW:
		    return fastlog(ncand) / lettercount;
		case METHOD_EXPERIMENTAL:
		    return ncand / Math.sqrt(lettercount);
		}
	    assert(false);
	    return 0;
    }

    static double fastlog(int v)
    {
	int b[] = {0x2, 0xC, 0xF0, 0xFF00, 0xFFFF0000};
	int S[] = {1, 2, 4, 8, 16};
	int i;

	int r = 0; // result of log2(v) will go here
	for (i = 4; i >= 0; i--) // unroll for speed...
	    {
		if ((v & b[i]) > 0)
		    {
			v >>= S[i];
			r |= S[i];
		    } 
	    }
	return r;
    }
}
