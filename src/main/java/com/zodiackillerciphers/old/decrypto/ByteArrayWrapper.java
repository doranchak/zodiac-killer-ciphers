package com.zodiackillerciphers.old.decrypto;

import java.util.*;

class ByteArrayWrapper
{
    int hashCode = 0;

    byte[] array;

    public ByteArrayWrapper(byte[] array)
    {
	this.array = array;
	// the hashcodes look pretty good on linux/jdk1.5.0_06. - eolson (2/10/2006)
	//	System.out.println(String.format("%08X",hashCode));
    }

    public int hashCode()
    {
	if (hashCode!=0)
	    return hashCode;
	  
	hashCode = Arrays.hashCode(array);

	return hashCode;
    }

    public boolean equals(Object o)
    {
	if (o instanceof ByteArrayWrapper)
	    {
		ByteArrayWrapper bw = (ByteArrayWrapper) o;

		return Arrays.equals(bw.array, array);
	    }
	return false;
    }
}

