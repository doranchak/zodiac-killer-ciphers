package com.zodiackillerciphers.old.decrypto;

import java.util.*;

/** Planner determines what we do at every step of the search **/
public class RandomPlanner implements Planner
{
    Random r = new Random();

    /** factory method **/
    public Planner make(DecryptoPuzzle p)
    {
	return this;
    }

    public int getAction(int wordlist[], int solvedwords, MapSet map)
    {
	if (wordlist.length == solvedwords)
	    return -1;

	return Planner.WORD_FLAG | wordlist[(r.nextInt(wordlist.length - solvedwords) + solvedwords)];
    }
}
