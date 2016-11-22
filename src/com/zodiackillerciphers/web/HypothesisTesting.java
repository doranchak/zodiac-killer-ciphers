package com.zodiackillerciphers.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.CiphersFromGeneratorExperiments;
import com.zodiackillerciphers.ciphers.generator.BoxCornerPair;
import com.zodiackillerciphers.ciphers.generator.CandidateConstraints;
import com.zodiackillerciphers.ciphers.generator.CandidateKey;
import com.zodiackillerciphers.ciphers.generator.CandidatePlaintext;
import com.zodiackillerciphers.ciphers.generator.PatternsAndFeatures;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.HomophonesResultBean;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotPair;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;

/** generate cipher output for Hypothesis Testing wiki page */
public class HypothesisTesting {

	public static void dumpall() {
		int num = 1;
		for (Cipher cipherr : CiphersFromGeneratorExperiments.cipher) {
			if (!cipherr.description
					.startsWith("doranchak multiobjective evolution experiment 60"))
				continue;
			
			String cipher = cipherr.cipher;
			//String cipher = Ciphers.cipher[0].cipher;
			
			System.out.println("======== " + cipherr.description + " ========");
			System.out.println();
			System.out.println("Symbolic form:");
			System.out.println();
			for (int row=0; row<20; row++) {
				System.out.println(cipher.substring(row*17,row*17+17));
			}
			System.out.println();
			System.out.println("Numeric form:");
			System.out.println();
			
			int[] numeric = Ciphers.toNumeric(cipher, false);
			for (int row=0; row<20; row++) {
				for (int col=0; col<17; col++) {
					int val = numeric[row*17+col];
					System.out.print((val<10 ? "0" : "") + val + " ");
				}
				System.out.println();
			}
			
			System.out.println();
			
			String re = Periods.rewrite3(cipher, 19);
			NGramsBean bean = new NGramsBean(2, re);
			//System.out.println("reps " + bean.numRepeats() + " count " + bean.count());
		}
	}
	public static void generate() {
		int num = 1;
		for (Cipher cipherr : CiphersFromGeneratorExperiments.cipher) {
			if (!cipherr.description
					.startsWith("doranchak multiobjective evolution experiment 60"))
				continue;

			String cipher = cipherr.cipher;
			
			//System.out.println(cipher);
			String args = PatternsAndFeatures.argsFor(
					PatternsAndFeatures.posPivots(cipher),
					PatternsAndFeatures.posBoxCorners(cipher),
					PatternsAndFeatures.posWords(cipher));
			
			num++;
			
			String js = "hsl_random(" + args + ")";
			//System.out.println(js);
			
			//System.out.println("hsl_random(" + PatternsAndFeatures.argsFor(PatternsAndFeatures.ngrams(cipher, 2)) + ")");
			//System.out.println("hsl_random(" + PatternsAndFeatures.argsFor(PatternsAndFeatures.ngrams(cipher, 3)) + ")");
			//System.out.println("hsl_random(" + PatternsAndFeatures.argsFor(PatternsAndFeatures.periodNgrams(cipher, 2, 19)) + ")");

			
			String cycles = "[";
			String a = Ciphers.alphabet(cipher);
			String[] alphabet = new String[a.length()];
			for (int i = 0; i < a.length(); i++)
				alphabet[i] = "" + a.charAt(i);
			
			Long startTime = new Date().getTime();
			List<HomophonesResultBean> beans = HomophonesNew.search(cipher,
					alphabet, 2, true, true, 2, null);
			for (int i=0; i<beans.size(); i++) {
				if (i==20) break;
				HomophonesResultBean bean = beans.get(i);
				List<List<Integer>> pos = bean.positions(cipher); 
				cycles += "new Cycle(\"" + bean.getSequence() + "\", " + pos.get(0) + ", " + pos.get(1) + ")";
				if (i<19) cycles += ", ";
			}
			
			cycles += "]";
			
			NGramsBean bean = new NGramsBean(2, cipher);
			int numRepeats1 = bean.numRepeats();
			String re = Periods.rewrite3(cipher, 19);
			bean = new NGramsBean(2, re);
			int numRepeats2 = bean.numRepeats();
			bean = new NGramsBean(3, cipher);
			int numRepeats3 = bean.numRepeats();
			
			String flipped = CipherTransformations.flipHorizontal(cipher, 20, 17);
			String re15 = Periods.rewrite3(flipped, 15);
			bean = new NGramsBean(2, re15);
			int numRepeats4 = bean.numRepeats();

			String info = "{\"bigrams\" : " + numRepeats1 + ", \"bigrams19\" : " + numRepeats2 + ", \"bigrams15\" : " + numRepeats4 + ", \"trigrams\" : " + numRepeats3 + "}";
			js = "new Cipher(\""
					+ cipherr.description
					+ "\", \""
					+ cipher
					+ "\", "
					+ PatternsAndFeatures.posPivots(cipher)
					+ ", "
					+ PatternsAndFeatures.posBoxCorners(cipher)
					+ ", "
					+ PatternsAndFeatures.posWords(cipher)
					+ ", "
					+ PatternsAndFeatures.posFoldMarks(cipher)
					+ ", "
					+ PatternsAndFeatures.argsFor(PatternsAndFeatures.ngrams(
							cipher, 2))
					+ ", "
					+ PatternsAndFeatures.argsFor(PatternsAndFeatures.ngrams(
							cipher, 3))
					+ ", "
					+ PatternsAndFeatures.argsFor(PatternsAndFeatures
							.periodNgrams(cipher, 2, 19)) 
					+ ", "
					+ PatternsAndFeatures.argsFor(PatternsAndFeatures.mirror(PatternsAndFeatures
							.periodNgrams(re15, 2, 15))) + ", " + cycles + ", " + info
					+ "),";

			System.out.println(js);
			//break;
		}
	}

	public static String cn(int row, int col, Map<Integer, String> classes) {
		int pos = row * 17 + col;
		String name = classes.get(pos);
		if (name == null)
			return "";
		return name;
	}

	public static void main(String[] args) {
		//generate();
		dumpall();
		// String cipher =
		// "cO9<U3dbW(8k7PH+N2PHER#)Z(YjyTSq%3C8Wb7OdV%PEDP>MdG>ZN8b6B)EMpUMplBD|_LG9bGY5FBcb.cVq*-<Hk7ZG+RJ|*NWcO>PL(5MH@Z9RD|Ak.Y)5/kHqVUMG+6J:lb7OLlVWR5)V7UfR5OB5-b@|S(5M7@D-*b..-5FBc:/pZN8q*|W)OE5NEE_L+*Vpl2b>6_@pk55/PG+@c:_5dMNL1pqZG@D<P57N>l).>k+|Ef5kU5d_Y#_>>NP.-pk7|zZ3D:Ak3z-5FVHSk9@)5W2|kl_H+BZBK<pZUML:8+V&4P|55|bEbL+5/8ZZlb1dOH@kdk/k+kPGDkT";
		// System.out.println(CandidateConstraints.hasPivots(cipher));
	}
}
