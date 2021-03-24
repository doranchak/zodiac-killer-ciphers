package com.zodiackillerciphers.old.decrypto;

/** Planner determines what we do at every step of the search **/
public class InOrderPlanner implements Planner
{
    DecryptoPuzzle p;

    public InOrderPlanner()
    {
    }

    /** factory method **/
    public Planner make(DecryptoPuzzle p)
    {
	InOrderPlanner planner = new InOrderPlanner();
	planner.p = p;
	return planner;
    }

    public int getAction(int wordlist[], int solvedwords, MapSet map)
    {
	// are we done?
	if (solvedwords == wordlist.length)
	    return -1;

	return solvedwords | WORD_FLAG;
    }
}
