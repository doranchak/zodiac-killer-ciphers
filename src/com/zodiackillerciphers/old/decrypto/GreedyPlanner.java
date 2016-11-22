package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class GreedyPlanner implements Planner
{
    DecryptoPuzzle puzzle;
    //    int wordorder[];

    public GreedyPlanner()
    {
    }

    public Planner make(DecryptoPuzzle puzzle)
    {
	GreedyPlanner p = new GreedyPlanner();
	p.puzzle = puzzle;

	return p;
    }

    public int getAction(int wordlist[], int solvedwords, MapSet map)
    {
	// are we done?
	if (solvedwords == wordlist.length)
	    return -1;

	// small scores are good.
	double bestscore = Double.MAX_VALUE;
	int bestword = solvedwords;

	for (int i = solvedwords; i < wordlist.length; i++) {
	    Word w = puzzle.words.get(wordlist[i]);

	    if (!w.enabled)
		continue;

	    double thisscore = (w.candidates.length - w.firstcand);

	    if (thisscore == 0)
		return -2;

	    if (thisscore < bestscore)
		{
		    bestscore = thisscore;
		    bestword = i;
		}
	}

	if (DecryptoParameters.enableSingleLetters && bestscore > puzzle.lang.letterCount()) {
	    // find next untried letter
	    for (int i = 0; i < puzzle.historder.length; i++) 
		{
		    if (map.isMapped(puzzle.historder[i]))
			continue;

		    byte letter = puzzle.historder[i];
		    int count = puzzle.histogram[letter];
		    if (count == 0)
			break;
		    
		    return letter | LETTER_FLAG;
		}
	}

	return wordlist[bestword] | WORD_FLAG;
    }
}
