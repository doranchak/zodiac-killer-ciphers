package com.zodiackillerciphers.old.decrypto;

/** A MapSet tracks for every letter in the langauge the set of
 * letters to which it could map. 
 **/
public class MapSet
{
    long set[];
    int size;

    /** Create a new set. The set is initially the empty set. **/
    public MapSet(int size)
    {
	this.size = size;
	set = new long[size];
    }

    public MapSet copy()
    {
	MapSet ms = new MapSet(size);
	ms.setTo(this);

	return ms;
    }

    public void setTo(MapSet ms)
    {
	size = ms.size;
	System.arraycopy(ms.set, 0, set, 0, size);
    }

    /** Set the set to be the empty set. **/
    public void setEmptySet()
    {
	for (int i = 0; i < size; i++)
	    set[i] = 0;
    }

    /** Set the set to be the full set (i.e., all mappings are
     *  legal.)
     **/
    public void setFullSet()
    {
	for (int i = 0; i < size; i++)
	    set[i] = (1<<(size+1))-1;
    }

    public void setMapping(byte a, byte b)
    {
	int ai = a&0xff;
	int bi = b&0xff;

	// no other letter is allowed to map to b.
	for (int i = 0; i < size; i++)
	    set[i] &= ~(1<<bi);

	// (except, of course, a)
	set[ai]=1<<bi;
    }

    public boolean isMapped(byte a)
    {
	int ai = a&0xff;

	long v = set[ai];
	return (v&(v-1))==0;
    }

    /** Are the mappings a[i] -> b[i] valid for all i? **/
    public boolean isMappingOkay(byte a[], byte b[])
    {
	for (int i = 0; i < a.length; i++)
	    {
		int ai = a[i]&0xff;
		int bi = b[i]&0xff;

		/*		if (ai >= size || bi >= size)
		    {
			for (int j = 0; j < a.length; j++)
			    System.out.print((char) ('A'+(a[j]&0xff)));

			System.out.println("size bad: "+ai+" "+bi);
		    }
		*/

		if ((set[ai]&(1<<bi))==0)
		    return false;
	    }
	return true;
    }

    public void setMappings(byte a[], byte b[])
    {
	long mask = 0;

	// compute a mask that forbids a mapping to any letter in b
	for (int i = 0; i < b.length; i++)
	    {
		int bi = b[i]&0xff;

		mask |= (1<< bi);
	    }

	mask = ~ mask;

	// forbid all letters to map to a letter in b
	for (int i = 0; i < size; i++)
	    {
		set[i] &= mask;
	    }

	// now set just the letters in a to b.
	for (int i = 0; i < b.length; i++)
	    {
		int ai = a[i]&0xff;
		int bi = b[i]&0xff;

		set[ai] = (1<< bi);
	    }
    }

    /** Add to the bit-vector the mappings a[i] -> b[i] for all i.
     **/
    public void enableMappings(byte a[], byte b[])
    {
	for (int i = 0; i < a.length; i++)
	    {
		int ai = a[i]&0xff;
		int bi = b[i]&0xff;

		set[ai] |= (1<<bi);
	    }
    }

    /** For each letter in the alphabet, take the intersection of
     *  this set and the specified set; except, if the specified 
     *  set is the null set, no intersection is computed for that
     *  letter.
     **/
    public void intersectWith(MapSet m)
    {
	for (int i = 0; i < size; i++)
	    {
		if (m.set[i] != 0)
		    {
			// remember the old set...
			long v = set[i];
			
			// compute the new set...
			set[i] &= m.set[i];

			// if the set changed, and the set now
			// contains only a single letter, then make
			// sure that no other set contains that
			// letter.  this optimization is good for
			// about 30% speed-up on "the quick brown
			// fox..."
			if (set[i] != v && (set[i]&(set[i]-1))==0)
			    {
				v = set[i];

				// we actually clear *all* sets
				for (int j = 0; j < size; j++)
				    set[j]&=~v;

				// then re-enable the mapping for i
				set[i]=v;
			    }
		    }
	    }
    }

    /** If a ciphertext letter has only one possible plaintext, then
	no other letter can map to that letter.  In general, if N
	different ciphertext letters all map to a subset of set S, and
	|S|==N, then no other letter can map to an element in S. I
	don't see how to implement the full rule in a reasonable way,
	so I'll only implement the case for N=1.

	This is now unnecessary, since intersectWith() performs this logic?
    **/
    public void selfReduce() 
    { 
	long lls[] = new long[set.length];

	for (int i = 0; i < set.length; i++)
	    {
		long b0 = set[i];

		// are there two elements set?
		long b1 = b0&(b0-1); // clear low bit
		long b2 = b1&(b1-1); // clear next lowest bit

		// more than one bit set.
		if (b2 != 0)
		    continue;

		int bidx0 = oneHotLog2(b1);      // get indices of the two set bits
		int bidx1 = oneHotLog2(b0^b1);
		if (bidx0 > bidx1)               // make sure idx0 < idx 1
		    {
			int tmp = bidx0;
			bidx0 = bidx1;
			bidx1 = tmp;
		    }

		long m = 1<<bidx1;
		if ((lls[bidx0]&m)!=0)
		    {
			for (int j = i+1 ; j < set.length; j++)
			    {
				set[j]&=(~b0);
			    }

			//			System.out.println("*");
		    }
		lls[bidx0]|=m;
	    }
    }
        /** helper table for oneHotLog2. **/
    static final int onehotlog2[] = {  0, 0, 1, 0, 2, 0, 0, 0, 3, 0,  0,  0,  0,  0,  0,  0,
				       4, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0,  0,  0,  0,  0,  0 };

    static final int LogTable256[] = 
    {
	0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3,
	4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
	5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
	5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7
    };

    /** returns -1 if no unique mapping */
    public int getMapping(byte a)
    {
	if (a<0)
	    return -1;

	long v = set[a&0xff];
	if ((v&(v-1))!=0)
	    return -1;

	return oneHotLog2(v);
    }

    /** Given that 2 is a power of two, what is log2(v)? 
	ONLY WORKS FOR v < 32 bits
    **/
    public static final int oneHotLog2(long _v)
    {
	int v = (int) _v;
	int tt = v>>16;
	
	if (tt!=0)
	    {
		int t = v>>24;
		if (t!=0)
		    return 24 + LogTable256[t];
		else
		    return 16 + LogTable256[(tt&0xff)];
	    }
	else
	    {
		int t = v>>8;
		if (t!=0)
		    return 8 + LogTable256[t];
		else
		    return LogTable256[v];
		    
	    }
	/*
	while (v >= (1<<16))
	    { cnt += 16; v=v>>16; }

	if (v >= (1<<8))
	    { cnt += 8; v=v>>8; }
	
	if (v >= (1<<4))
	    { cnt += 4; v=v>>4; }

	return cnt + onehotlog2[(int) v];
	*/
    }

}

