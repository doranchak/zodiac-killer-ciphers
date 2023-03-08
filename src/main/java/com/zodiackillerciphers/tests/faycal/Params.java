package com.zodiackillerciphers.tests.faycal;

import java.util.Arrays;
import java.util.Map;

public class Params {

	/** substitution key */ 
	public Map<Character, Character> key;
	public int whichKey;
	/** offset when converting letters to numbers */
	public int offset;
	/** numerical substitutes for Cancer and Aries symbols */
	public int[] substitutes;
	/** modulo operation value */
	public int modulo;
	/** which arithmetical operator to apply */
	public int operator;
	/** ordering of the distinct digits */
	public int[] digitOrdering;
	/** which base to operate in */
	public int base;
	@Override
	public String toString() {
		return "Params [key=" + whichKey + ", offset=" + offset + ", substitutes=" + Arrays.toString(substitutes)
				+ ", modulo=" + modulo + ", operator=" + operator + ", digitOrdering=" + Arrays.toString(digitOrdering)
				+ ", base=" + base + "]";
	}
}
