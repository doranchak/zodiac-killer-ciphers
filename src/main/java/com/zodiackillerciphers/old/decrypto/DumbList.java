package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class DumbList
{
    DumbListChunk chunk;
    DumbListChunk tailchunk;
    DumbListAllocator dla;
    int nchunks = 0;

    public DumbList(DumbListAllocator dla)
    {
	this.dla = dla;
	chunk = null; // dla.get();
	tailchunk = null; // chunk;
    }

    public void reset()
    {
	//	chunk = null;
	//	tailchunk = null;
	nchunks = 0;
    }

    public DumbListChunk getHead()
    {
	if (nchunks == 0)
	    return null;
	return chunk;
    }

    public void add(ArrayList l)
    {
	for (Object o : l)
	    add(o);
    }

    public void add(Object o)
    {
	if (nchunks == 0 || tailchunk.sz >= tailchunk.o.length)
	    {
		DumbListChunk newchunk = dla.get();

		if (nchunks == 0)
		    {
			chunk = newchunk;
			tailchunk = newchunk;
		    }
		else
		    {
			tailchunk.next = newchunk;
			tailchunk = newchunk;
		    }

		nchunks++;
	    }

	tailchunk.o[tailchunk.sz++] = o;
    }

    public void unget()
    {
	dla.unget(nchunks);
	nchunks=0;
	chunk = null;
	tailchunk = null;
    }
}
