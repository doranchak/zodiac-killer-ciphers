package com.zodiackillerciphers.old;

import java.text.DecimalFormat;

import com.zodiackillerciphers.ciphers.Ciphers;

/** container for measurement results */ 
public class Measurement {
	public float[] ngrams;
	// mean probability of top 20 ngram patterns  
	public float ngramTop20Mean;
	public float[] cosineDistances;
	public float meanSimilarityPerSymbol;
	public double[] homophones;
	public float multiplicity;
	
	public double ioc;
	public double chi2;
	public double entropy;
	
	public int numSymbols; // nuber of symbols in cipher
	public int length; // cipher length
	
	public String ciphertext;
	
	public static DecimalFormat formatter1 = new DecimalFormat("##0.000");
	public static DecimalFormat formatter2 = new DecimalFormat("#.##E0");
	
	private Measurement() {
		;
	}
	
	public Measurement(String cipher) {
		ciphertext = cipher;
		numSymbols = Ciphers.alphabet(cipher).length();
		length = cipher.length();
		multiplicity = (float)numSymbols / length; // length includes whitespace
	}
	/** produce a combined score, based on sums.  defined as product of:  
	 * 		ngrams combined score,
	 * 		cosine distances sum,
	 * 		homophones sum.
	 */
	public double scoreSums() {
		return ngrams[8]*cosineDistances[0]*homophones[0];
	}

	/** produce a combined score, based on per-symbol averages.  defined as product of:  
	 * 		ngrams combined score, per symbol
	 * 		cosine distances sum, per symbol
	 * 		homophones sum, per symbol
	 */
	public double scoreMeans() {
		return ngrams[9]*cosineDistances[3]*homophones[3];
	}
	
	public String toString() {
		String line = length + "	" + numSymbols + "	" + multiplicity + "	" + ioc + "	" + chi2 + "	" + entropy + "	";
		for (int i=0; i<ngrams.length; i++) {
			line += ngrams[i] + "	";
		}
		for (int i=0; i<cosineDistances.length; i++) {
			line += cosineDistances[i] + "	";
		}
		for (int i=0; i<homophones.length; i++) {
			line += homophones[i] + "	";
		}
		line += scoreSums() + "	" + scoreMeans();
		return line;
	}
	public void dumpForWiki() {
		dumpLine(""+length, true);
		dumpLine(""+numSymbols, true);
		dumpLine(""+formatter1.format(multiplicity), true);
		dumpLine(""+formatter1.format(ioc), true);
		dumpLine(""+formatter1.format(chi2), true);
		dumpLine(""+formatter1.format(entropy), true);
		for (int i=0; i<ngrams.length; i++) {
			if (i==3 || i>6)
				dumpLine(""+formatter1.format(ngrams[i]),true);
			else
				dumpLine(""+(int)ngrams[i],true);
		}
		for (int i=0; i<cosineDistances.length; i++) {
			dumpLine(""+formatter1.format(cosineDistances[i]),true);
		}
		for (int i=0; i<homophones.length; i++) {
			if (i==3) 
				dumpLine(""+formatter1.format(homophones[i]),true);
			else
				dumpLine(""+(int)homophones[i],true);
		}
		dumpLine(""+formatter2.format(scoreSums()), true);
		dumpLine(""+formatter1.format(scoreMeans()), true);
	}
	
	public void dumpLine(String text, boolean alignRight) {
		System.out.println("| style=\"" + (alignRight ? "text-align: right; " : "") + "border-style: solid; border-width: 1px\"| " + text);
	}
/*	
	public String header() {
		return 
	}*/
}
