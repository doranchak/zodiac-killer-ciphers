package com.zodiackillerciphers.old.decrypto;

public class Plan
{
    static final int LETTERFLAG = 0x100000;
    static final int WORDFLAG   = 0x200000;
    static final int MASK       = 0x0FFFFF;
    boolean knownletters[];
    int    numknownletters;
    int    actions[];
    int    numactions;
    double score;
    double steps;
    int    numwords;

    double    nodes;
    double    fanout;

    public Plan(int lettercount)
    {
	actions = new int[10];
	knownletters = new boolean[lettercount];
    }

    public boolean isWholePlan()
    {
	return numactions==4;
    }

    public Plan copy()
    {
	Plan p = new Plan(knownletters.length);
	p.numactions = numactions;
	p.score = score;
	p.numknownletters = numknownletters;
	p.steps = steps;
	p.numwords = numwords;
	p.nodes = nodes;
	p.fanout = fanout;

	p.actions = new int[actions.length];
	for (int i = 0; i < numactions; i++)
	    p.actions[i] = actions[i];

	for (int i = 0; i < knownletters.length; i++)
	    p.knownletters[i] = knownletters[i];

	return p;
    }

    public void addAction(int a)
    {
	if (numactions < actions.length) 
	    {
		actions[numactions] = a;
		numactions++;
		return;
	    }
	int actions2[] = new int[actions.length*2];
	for (int i = 0; i < numactions; i++)
	    actions2[i]=actions[i];

	actions = actions2;
	addAction(a);
    }

    public boolean isActionPerformed(int a)
    {
	for (int i = 0; i < numactions; i++)
	    if (actions[i] == a)
		return true;

	return false;
    }

}
