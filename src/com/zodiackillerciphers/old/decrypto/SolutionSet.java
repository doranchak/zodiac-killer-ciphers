package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class SolutionSet
{
    TreeList<Solution> set = new TreeList<Solution>(new SolutionComparator());
    int capacity;
    double worst = -Double.MAX_VALUE;

    public SolutionSet(int capacity)
    {
	this.capacity = capacity;
    }

    public void reset()
    {
	set.reset();
	worst = -Double.MAX_VALUE;
    }

    public int size()
    {
	return set.size();
    }

    public double getWorst()
    {
	return worst;
    }

    public Solution getElement(int idx)
    {
	return set.elementAt(idx);
    }

    public boolean wouldAdd(double score)
    {
	if (score < worst && set.size() >= capacity)
	    return false;

	return true;
    }

    /** (Conditionally) add to the set. Returns true if the sol was added. **/
    public boolean add(Solution sol)
    {
	if (sol.score < worst && set.size() >= capacity)
	    return false;

	if (!set.add(sol))
	    return false;

	if (set.size() > capacity)
	    {
		set.remove(set.size()-1);
	    }
	worst = set.elementAt(set.size()-1).score;

	return true;
    }

    public static class SolutionComparator implements Comparator<Solution>
    {
	public int compare(Solution a, Solution b)
	{
	    if (a.score != b.score)
		return Double.compare(b.score, a.score);
	    else
		return b.solution.compareTo(a.solution);
	}
    }

}
