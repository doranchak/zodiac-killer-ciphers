package com.zodiackillerciphers.tests.unicity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.lucene.ZKDecrypto;

public class CorpusScannerBean {
	/** the string that identifies this plaintext sample and links it to other plaintext samples */
	public String isomorphism;
	/** the plaintext length under consideration */
	public int length;
	/** set of unique plaintexts. */
	public Set<String> plaintexts;
	/** set of rejects */
	public Set<String> rejects;
	
	public CorpusScannerBean(String isomorphism, int length) {
		this.isomorphism = isomorphism;
		this.length = length;
		plaintexts = new HashSet<String>();
		rejects = new HashSet<String>();
	}
	/** add the given plaintext.  ignore it if we've seen it already.
	 * add it to a cluster of plaintexts if it is overly similar.
	 * reject it if it has too many self-repeats.
	 * 
	 * returns false only if we wish to count this as a "rejected" plaintext
	 * i.e., overly repetitive plaintexts are rejected.
	 * but dupes still count towards the number of samples 
	 */
	public boolean add(String plaintext) {
		if (plaintexts.contains(plaintext))
			return true; // already seen it
		String ns = plaintext.replaceAll(" ", "");
		// ignore overly repetitive strings
		if (PlaintextBean.tooRepetitive(ns)) {
			rejects.add(plaintext + " (rep)");
			return false;
		}
		// if tokens are too short on average, then plaintext is spurious collection of letters or short abbreviations 
		if (PlaintextBean.badAverageTokenLength(plaintext)) {
			rejects.add(plaintext + " (atl)");
			return false;
		}
		// digraphic ioc that is too high also suggests overly repetitive text to ignore
		if (PlaintextBean.badIoc(ns)) {
			rejects.add(plaintext + " (dioc)");
			return false;
		}
		// reject low ngram-scoring texts
		float zkscore = ZKDecrypto.calcscore(ns);
		zkscore /= ns.length();
		if (zkscore < 30) { 
			rejects.add(plaintext + " (zk)");
			return false;
		}
		
		// made it this far.  so add new entry for this plaintext.
		plaintexts.add(plaintext);
		return true;
	}
	
	public String toString() {
//		String result = length + "	" + isomorphism + "	" + plaintexts.size() + "	" + averageLetterUniqueness() + "	" + plaintexts;
		String result = length + "	" + isomorphism + "	" + plaintexts.size() + "	" + plaintexts;
		return result;
	}

	/** count distinct letters in each position of all plaintexts.
	 * example: if position 1 uses 20 out of 26 letters, compute ratio 20/26.
	 * 1/26 means the position tends to repeat the same letter for every plaintext.
	 * 26/26 suggests variety of all letters at that position.
	 * if there are less than 26 plaintexts, divide by the total count of plaintexts instead of 26.
	 * 
	 *  compute average for all positions.
	 *  low values suggest too much repetition.
	 */
	public static float averageLetterUniquenessUNUSED(Collection<String> plaintexts) {
//		System.out.println("SMOG " + plaintexts);
		if (plaintexts.size() == 0) return 0;
		float sum = 0;
		Set<Character> letters = new HashSet<Character>(); 
		
		int length = 0;
		for (String text : plaintexts) {
			length = text.length();
			break;
		}
		
		for (int i=0; i<length; i++) {
			//int unique = 0;
			letters.clear();
			for (String s : plaintexts) {
				s = s.replaceAll(" ", "");
				System.out.println(i + "," + s);
				letters.add(s.charAt(i));
			}
			float unique = letters.size();
			unique /= Math.min(26, plaintexts.size());
//			System.out.println("smeg " + i + " " + plaintexts.size() + " " + letters.size() + " " + unique);
			sum += unique;
		}
		sum /= length;
//		System.out.println("smeg result " + sum + " " + plaintexts);
		return sum;
	}
//	public float averageLetterUniqueness() {
//		return averageLetterUniqueness(plaintexts);
//	}
	
	// try to remove any plaintext that is not at least 50% dissimilar to some other plaintext 
	void filter() {
		if (plaintexts.size() > 99) {
			// too big for the n^2 loop below
			return;
		}
		List<String> list1 = new ArrayList<String>(plaintexts);
		Set<String> set2 = new HashSet<String>();
		boolean[] added = new boolean[list1.size()];
		for (int i=0; i<list1.size(); i++) {
			String s1 = list1.get(i);
			String ns1 = list1.get(i).replaceAll(" ", "");
			for (int j=i+1; j<list1.size(); j++) {
				String s2 = list1.get(j);
				String ns2 = list1.get(j).replaceAll(" ", ""); 
				if (PlaintextBean.tooSimilar(ns1, ns2)) {
					continue;
				}
				// add both 
				set2.add(list1.get(i));
				set2.add(list1.get(j));
				added[i] = true;
				added[j] = true;
			}
		}
		for (int i=0; i<added.length; i++) {
			if (!added[i])
				rejects.add(list1.get(i) + " (ts) ");
		}
		plaintexts = set2;
		
	}
	
	
	/** output the bean, but only if there is more than one plaintext cluster. */
	public void dump() {
		if (plaintexts.size() < 2) return;
		System.out.println(toString());
	}
}
