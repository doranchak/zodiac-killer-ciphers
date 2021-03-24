package com.zodiackillerciphers.old;

//--------------------------------------
//Systematically generate combinations.
//--------------------------------------

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.zodiackillerciphers.ciphers.Ciphers;

public class CombinationGenerator {

private int[] a;
private int n;
private int r;
private BigInteger numLeft;
private BigInteger total;

//------------
// Constructor
//------------

public CombinationGenerator (int n, int r) {
 if (r > n) {
   throw new IllegalArgumentException ();
 }
 if (n < 1) {
   throw new IllegalArgumentException ();
 }
 this.n = n;
 this.r = r;
 a = new int[r];
 BigInteger nFact = getFactorial (n);
 BigInteger rFact = getFactorial (r);
 BigInteger nminusrFact = getFactorial (n - r);
 total = nFact.divide (rFact.multiply (nminusrFact));
 reset ();
}

//------
// Reset
//------

public void reset () {
 for (int i = 0; i < a.length; i++) {
   a[i] = i;
 }
 numLeft = new BigInteger (total.toString ());
}

//------------------------------------------------
// Return number of combinations not yet generated
//------------------------------------------------

public BigInteger getNumLeft () {
 return numLeft;
}

//-----------------------------
// Are there more combinations?
//-----------------------------

public boolean hasMore () {
 return numLeft.compareTo (BigInteger.ZERO) == 1;
}

//------------------------------------
// Return total number of combinations
//------------------------------------

public BigInteger getTotal () {
 return total;
}

//------------------
// Compute factorial
//------------------

private static BigInteger getFactorial (int n) {
 BigInteger fact = BigInteger.ONE;
 for (int i = n; i > 1; i--) {
   fact = fact.multiply (new BigInteger (Integer.toString (i)));
 }
 return fact;
}

//--------------------------------------------------------
// Generate next combination (algorithm from Rosen p. 286)
//--------------------------------------------------------

public int[] getNext () {

 if (numLeft.equals (total)) {
   numLeft = numLeft.subtract (BigInteger.ONE);
   return a;
 }

 int i = r - 1;
 while (a[i] == n - r + i) {
   i--;
 }
 a[i] = a[i] + 1;
 for (int j = i + 1; j < r; j++) {
   a[j] = a[i] + j - i;
 }

 numLeft = numLeft.subtract (BigInteger.ONE);
 return a;

}

public static void test() {
	int[] elements = {1,2,3,4,5,6};
	int[] indices;
	
	for (int len = 2; len < 5; len++) {
		CombinationGenerator x = new CombinationGenerator (elements.length, len);
		StringBuffer combination;
		while (x.hasMore ()) {
		  combination = new StringBuffer ();
		  indices = x.getNext ();
		  for (int i = 0; i < indices.length; i++) {
		    combination.append (elements[indices[i]]);
		  }
		  System.out.println (combination.toString ());
		}	
	}
}

static boolean distinct(String key) {
	Set<Character> set = new HashSet<Character>();
	for (int i=0; i<key.length(); i++) set.add(key.charAt(i));
	if (key.length() == set.size()) return true;
	return false;
}

public static void lookForRepeats(String cipherText, String symbols) {
	String sequence = Homophones.dumpSequenceFor(cipherText, symbols);
	Map<String, Integer> map = new HashMap<String, Integer>();
	if (sequence.length() < symbols.length()) return;
	
	int i=0;
	while ((i+symbols.length())<sequence.length()+1) {
		String key = sequence.substring(i, i+symbols.length());
		if (distinct(key)) {
			Integer val = map.get(key);
			if (val == null) val = 1;
			else val++;
			map.put(key, val);
		}
		i++;
	}
	
	int max = 0;
	for (String key : map.keySet()) {
		Integer val = map.get(key);
		max = Math.max(val, max);
		//if (val > 1) System.out.println(symbols + "," + sequence + "," + key + "," + val);
	}
	for (String key : map.keySet()) {
		Integer val = map.get(key);
		if (val == max) {
			float p = (float) 100 * key.length() * val / sequence.length();
			
			System.out.println("|<tt>"+symbols + "</tt>||<tt>" + (sequence.replaceAll(Pattern.quote(key), " '''"+key+"''' ")).replaceAll("  "," ") + "</tt>||<tt>" + key + "</tt>||" + val + "||" + Math.round(100.0*p)/100.0 + "%");
			System.out.println("|-");
		}
	}
}

public static void repeats() {
	String[][] elementsAll = {
		{"E","N","W","Z","6","p","+"},
		{"H","I","L","5"},
		{"F","K","7","@"},
		{"T","X","d","!"},
		{"D","O","(","^"},
		{"P","U","9","k"},
		{"G","S","8","l"},
		{"r","t","\\"},
		{"B","#","%"},
		{"M",")"},
		{"J","Q"},
		{"f","z"}
	};
	
	for (int j=0; j<elementsAll.length; j++) {
		String[] elements = elementsAll[j];
		int[] indices;
		for (int len = 2; len < elements.length+1; len++) {
			CombinationGenerator x = new CombinationGenerator (elements.length, len);
			StringBuffer combination;
			while (x.hasMore ()) {
			  combination = new StringBuffer ();
			  indices = x.getNext ();
			  for (int i = 0; i < indices.length; i++) {
			    combination.append (elements[indices[i]]);
			  }
			  String symbols = combination.toString ();
			  // TODO: this causes compilation error: lookForRepeats(Ciphers.cipher[1], symbols);
			}	
		}
	}
}

public static void main(String[] args) {
	test();
	//repeats();

}

}