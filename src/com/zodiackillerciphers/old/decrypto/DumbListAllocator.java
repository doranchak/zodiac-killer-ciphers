package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class DumbListAllocator
{
    ArrayList<DumbListChunk> chunks = new ArrayList<DumbListChunk>(4096);
    int nchunks;

    public DumbListChunk get()
    {
	DumbListChunk chunk;

	if (nchunks < chunks.size())
	    {
		chunk = chunks.get(nchunks++);
		chunk.clear();
	    }
	else
	    {
		//		System.out.print("M");
		chunk = new DumbListChunk();
		chunks.add(chunk);
		nchunks++;
	    }

	return chunk;
    }

    /** if you *just* called get(), you can put it back if you decide
     * you don't need it after all. 
     **/
    public void unget(int n)
    {
	nchunks-=n;
    }

    public void reclaimAll()
    {
	nchunks = 0;
    }
}
