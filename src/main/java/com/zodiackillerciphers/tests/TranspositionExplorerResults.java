package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.Z340Solution;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.old.EditDistance;

public class TranspositionExplorerResults {
	static String tab = "	";
	/**
	 * go through the transposition explorer results. output the ngram counts and
	 * the plaintext. calculate accuracy compared to known solution.  calculate zkdecrypto score.
	 */
	public static void process(String path) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		List<String> ciphers = FileUtil.loadFrom(path);
		for (String cipher : ciphers) {
			String line = "";
			for (int n=2; n<6; n++) {
				NGramsBean ng = new NGramsBean(n, cipher);
				line += ng.numRepeats() + tab;
			}
			double zk = NGramsCSRA.zkscore(new StringBuffer(cipher), "EN", false);
			line += zk + tab;
			String pt = Ciphers.decode(cipher, Z340Solution.z340SolutionKey());
			line += Z340Solution.accuracy(pt) + tab + pt + tab + cipher;
			System.out.println(line);
		}
	}
	// compare untranspositions to real untransposition, using edit distance 
	public static void process2(String path) {
		List<String> ciphers = FileUtil.loadFrom(path);
		for (String cipher : ciphers) {
			System.out.println(EditDistance.LD(cipher, Ciphers.Z340T) + " " + cipher);
		}
	}
	public static void main(String[] args) {
		process2("/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/transposition-results-ciphers/ciphers.txt");
	}
}
