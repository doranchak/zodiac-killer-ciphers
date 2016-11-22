package com.zodiackillerciphers.old.decrypto;

/** A very dumb "lossy" hash map (useful for caching) which can be
 * very quickly erased. **/
public class DumbHashMap
{
    Object[] vs;
    int[] ks;
    int MASK;
    int generation = 1;
    int[] gens;
    int sz;

    public DumbHashMap(int sz)
    {
	this.sz = sz;

	vs = new Object[sz];
	ks = new int[sz];
	gens = new int[sz];

	MASK = sz-1;
	assert((sz&(sz-1))==0);
    }

    public void reset()
    {
	generation++;

	// wrap around (as unlikely as it would seem)
	if (generation == 0)
	    {
		// restore ourselves to our initial state.
		for (int i = 0; i < sz; i++)
		    {
			vs[i] = null;
			ks[i] = 0;
			gens[i] = 0;
		    }
		generation = 1;
	    }
    }

    public void put(int k, Object v)
    {
	int idx = k&MASK;
	ks[idx] = k;
	vs[idx] = v;
	gens[idx] = generation;
    }

    public Object get(int k)
    {
	int idx = k&MASK;

	if (gens[idx]!=generation)
	    return null;

	if (ks[idx]==k)
	    return vs[idx];
	return null;
    }
}
