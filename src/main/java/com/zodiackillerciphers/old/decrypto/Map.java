package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class Map
{
    byte[] map;
    long ismapped = 0;
    public double score = 0;
    int sz;

    public Map(MapSet ms)
    {
	map = new byte[ms.size];
	for (int i = 0; i < ms.size; i++)
	    {
		long v = ms.set[i];
		if (v==0 || (v & (v-1))!=0)
		    map[i] = (byte) -1;
		else
		    map[i] = (byte) (oneHotLog2(v));
	    }
    }

    public Map(int sz)
    {
	this.sz = sz;

	map = new byte[sz];
	for (int i = 0; i < sz; i++)
	    map[i] = -1;
    }

    public long getMappedMask()
    {
	return ismapped;
    }

    public void setTo(Map m)
    {
	if (sz != m.sz) {
	    this.sz = m.sz;
	    this.map = new byte[sz];
	}

	this.score = m.score;
	this.ismapped = m.ismapped;

	System.arraycopy(m.map, 0, map, 0, sz);
    }

    public void setToRandomPermutation(boolean allowIdentityMappings)
    {
	Random r = new Random();

	while (true) 
	    {
		for (int i = 0; i < sz; i++)
		    map[i] = (byte) i;
		
		for (int i = 0; i < sz; i++)
		    {
			int j = r.nextInt(sz);
			byte tmp = map[i];
			map[i] = map[j];
			map[j] = tmp;
		    }
		
		if (allowIdentityMappings)
		    return;

		boolean reject = false;
		// reject any identity mappings
		for (int i = 0; i < sz; i++) {
		    if (map[i] == (byte) i) {
			reject = true;
			break;
		    }
		}
		
		if (!reject)
		    return;
	    }
    }

    protected Map(boolean noinit)
    {
    }

    public Map copy()
    {
	Map m = new Map(sz);
	System.arraycopy(map, 0, m.map, 0, sz);

	m.ismapped = ismapped;
	m.score = score;

	return m;
    }

    public final int get(int ctext)
    {
	int v = map[ctext];
	return v;
    }

    public void put(int ctext, int ptext)
    {
	ismapped |= (1<<(ptext&0xff));

	map[ctext&0xff] = ((byte) (ptext&0xff));
    }

    public boolean isMapped(byte ctext)
    {
	return map[ctext&0xff]!=(byte) 0xff;
    }

    public final boolean isMappedTo(byte ptext)
    {
	return ((ismapped & (1<<ptext))!=0);
    }

    public boolean isMappingOkay(byte[] a, int aoff, byte[] b)
    {
	for (int i = 0; i < b.length; i++)
	    {
		int am = get(a[aoff+i]);

		if (am == (b[i]&0xff)) // already mapped is okay
		    continue;
		if (am == -1 && !isMappedTo(b[i])) // unmapped, and mapping is availble
		    continue;

		return false;
	    }
	return true;
    }

    public boolean isMappingOkay(byte[] a, int aoff, byte[] b, byte ambg[], int nambg)
    {
	for (int i = 0; i < nambg; i++)
	    {
		int idx = ambg[i];

		int am = get(a[aoff+idx]);

		if (am == (b[idx]&0xff)) // already mapped is okay
		    continue;
		if (am == -1 && !isMappedTo(b[idx])) // unmapped, and mapping is availble
		    continue;

		return false;
	    }
	return true;
    }

    public boolean isMappingOkay(byte[] a, byte[] b)
    {
	for (int i = 0; i < a.length; i++)
	    {
		int am = get(a[i]);

		if (am == -1 && !isMappedTo(b[i])) // unmapped, and mapping is availble
		    continue;
		if (am == (b[i]&0xff)) // already mapped okay?
		    continue;

		return false;
	    }
	return true;
    }

    public void map(byte[] a, int offset, byte[] b)
    {
	for (int i = 0; i < b.length; i++)
	    put(a[offset+i], b[i]);
    }

   public void map(byte[] a, byte[] b)
    {
	for (int i = 0; i < a.length; i++)
	    put(a[i], b[i]);
    }

    public static class ScoreComparator implements Comparator<Map>
    {
	public int compare(Map a, Map b)
	{
	    if (a.score != b.score)
		{
		    return Double.compare(a.score, b.score);
		}
	    else
		{
		    for (int i = 0; i < a.sz; i++)
			{
			    if (a.map[i] != b.map[i])
				return a.map[i] - b.map[i];
			}
		    return 0;
		}
	}
    }

    /** helper table for oneHotLog2. **/
    static final int onehotlog2[] = {  0, 0, 1, 0, 2, 0, 0, 0, 3, 0,  0,  0,  0,  0,  0,  0,
				       4, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0,  0,  0,  0,  0,  0 };

    /** Given that 2 is a power of two, what is log2(v)? **/
    public static final int oneHotLog2(long v)
    {
	int cnt = 0;

	while (v >= (1<<16))
	    { cnt += 16; v=v>>16; }

	if (v >= (1<<8))
	    { cnt += 8; v=v>>8; }
	
	if (v >= (1<<4))
	    { cnt += 4; v=v>>4; }

	return cnt + onehotlog2[(int) v];
    }
}
