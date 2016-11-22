package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class DecryptoPlanner
{
    DecryptoPuzzle puzzle;
    ArrayList<Plan> heap = new ArrayList<Plan>();
    int heapsize = 0;

    public DecryptoPlanner(DecryptoPuzzle puzzle)
    {
	this.puzzle = puzzle;
	Language lang = puzzle.lang;

	Plan p = new Plan(lang.letterCount()); // an "empty" plan.
	p.steps = 1;
	heapAdd(p);
    }

    public Plan plan()
    {
	int max = 0;
	Plan bestplan = null;

	while (true)
	    {
		if (heapsize == 0)
		    {
			break;
		    }

		Plan p = heapRemoveMinimum();

		if (p.numknownletters > max && false)
		    {
			max = p.numknownletters;
			System.out.println(max);

			System.out.println(printPlan(p));
		    }
		//		System.out.println(p.numknownletters+" "+p.numactions);
		
		if (p.numactions > p.numknownletters)
		    continue;
		//		    System.out.println(printPlan(p));
		
		if (p.numactions==4)
		    {
			if (bestplan == null || p.score < bestplan.score)
			    bestplan = p;
		    }
		else
		    expandNode(p);
	    }

	System.out.println(printPlan(bestplan));
	return bestplan;
    }

    void expandNode(Plan p)
    {
	for (int i = 0; i < puzzle.lang.letterCount(); i++)
	    {
		if (!p.knownletters[i]) //isActionPerformed(Plan.LETTERFLAG | i))
		    {
			heapAdd(addLetter(p, i));
		    }
	    }

	for (int i = 0; i < puzzle.words.size(); i++)
	    {
		if (!p.isActionPerformed(Plan.WORDFLAG | i))
		    {
			heapAdd(addWord(p, i));
		    }
	    }
    }

    Plan addLetter(Plan root, int letter)
    {
	Plan p = root.copy();
	p.addAction(Plan.LETTERFLAG | letter);

	assert (!p.knownletters[letter]);

	p.knownletters[letter] = true;
	p.numknownletters=root.numknownletters+1;

	p.steps = root.steps * puzzle.lang.letterCount();
	p.score = p.steps / p.numknownletters;
	//	p.score = -p.numknownletters;
	//	double  cp = Math.pow(p.steps, 1.0 / p.numknownletters);
	//	p.score = p.steps * Math.pow(cp, puzzle.lang.letterCount()-p.numknownletters);

	return p;
    }

    Plan addWord(Plan root, int word)
    {
	Word w = puzzle.words.get(word);
	Plan p = root.copy();
	p.addAction(Plan.WORDFLAG | word);

	int known = 0, unknown = 0;
	for (int i = 0; i < w.word.length; i++)
	    {
		if (root.knownletters[w.word[i]])
		    {
			known++;
		    }
		else
		    {
			unknown++;
			if (!p.knownletters[w.word[i]])
			    {
				p.numknownletters++;
				p.knownletters[w.word[i]]=true;
			    }
		    }
	    }

	double costper = Math.pow(w.candidates.length, 1.0/w.word.length);
	double estcand = Math.pow(costper, unknown);
	if (estcand < 1)
	    estcand = 1;

	p.steps = root.steps * estcand * Math.pow(.5, known);
	//	p.score = -p.numknownletters;

	//	double  cp = Math.pow(p.steps, 1.0 / p.numknownletters);
	//	p.score = p.steps * Math.pow(cp, puzzle.lang.letterCount()-p.numknownletters);

	p.score = p.steps / p.numknownletters;

	return p;
    }

    Plan heapRemoveMinimum()
    {
	Plan p = heap.get(0);
	heap.set(0, heap.get(heapsize-1));
	heap.set(heapsize-1, null);
	heapsize--;

	if (heapsize > 0)
	    heapFix(0);
	return p;
    }

    int heapParent(int i)
    {
	return (i-1)/2;
    }

    void heapAdd(Plan p)
    {
	if (heapsize == heap.size())
	    heap.add(p);
	else
	    heap.set(heapsize, p);
	heapsize++;
	int idx = heapsize - 1;
	int pidx = heapParent(idx);

	while (heap.get(idx).score < heap.get(pidx).score)
	    {
		heapSwap(idx, pidx);
		idx = pidx;
		pidx = heapParent(idx);
	    }
    }

    void heapSwap(int a, int b)
    {
	Plan pa = heap.get(a);
	Plan pb = heap.get(b);
	heap.set(a, pb);
	heap.set(b, pa);
    }

    void heapFix(int parent)
    {
	int left  = 2*parent + 1;
	int right = 2*parent + 2;

	double pscore = heap.get(parent).score;
	double lscore = Double.MAX_VALUE, rscore = Double.MAX_VALUE;

	if (left < heapsize)
	    lscore = heap.get(left).score;
	if (right < heapsize)
	    rscore = heap.get(right).score;

	// heapified already?
	if (pscore < lscore && pscore < rscore)
	    return;

	// pscore is not the minimum
	if (lscore < rscore)
	    {
		heapSwap(parent, left);
		heapFix(left);
	    }
	else
	    {
		heapSwap(parent, right);
		heapFix(right);
	    }
    }

    public String printPlan(Plan p)
    {
	StringBuffer sb = new StringBuffer();

	sb.append(String.format("letters: %4d  actions: %4d  score: %8.5f\n",p.numknownletters, p.numactions, p.score));

	for (int i = 0; i < p.numactions; i++)
	    {
		sb.append(String.format("%08X  ", p.actions[i])); 
		if ((p.actions[i]&Plan.WORDFLAG)!=0)
		    sb.append(i+ " WORD   "+puzzle.words.get(p.actions[i]&Plan.MASK).text+"\n");
		else
		    sb.append(i+ " LETTER "+((char) ('A'+p.actions[i]&Plan.MASK))+"\n");
	    }

	return sb.toString();
    }


}
