package com.zodiackillerciphers.old.decrypto;

public class ObjectCache
{
    Object cache[];
    int    inpos = 0;
    int    outpos = 0;
    int    MASK;

    public ObjectCache(int sz)
    {
	assert((sz & (sz-1))==0);
	cache = new Object[sz];
	MASK = sz - 1;
    }

    public Object get()
    {
	Object o = cache[inpos];

	if (o == null)
	    return null;

	cache[inpos] = null;
	inpos = (inpos+1)&MASK;
	return o;
    }

    public void put(Object o)
    {
	int nextoutpos = (outpos + 1)&MASK;

	// don't loop around. let the garbage collector grab this
	// object.
	if (nextoutpos==inpos)
	    return;

	cache[outpos] = o;
	outpos = nextoutpos;
    }
}
