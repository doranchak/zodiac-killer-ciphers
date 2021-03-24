package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.RepeatingFragments;
import com.zodiackillerciphers.transform.MeasurementsBean;
import com.zodiackillerciphers.transform.Operations;

/** test ryan garlick's transpositions of z340 */
public class RyanGarlick {
	/** perform measurements on all of ryan's ciphers */
	public static void measure(String path) {
		List<String> ciphers = FileUtil.loadFrom(path);
		for (String cipher : ciphers) {
			//System.out.println(Operations.measureFragments(cipher, 25) + "	" + cipher);
			//System.out.println(HomophonesNew.meanSigma(cipher, false, false) + "	" + cipher);
			System.out.println(RepeatingFragments.meanProbability(cipher) +"	" + cipher);
			// MeasurementsBean bean = MeasurementsBean.measure(cipher, null);
			// System.out.println(bean);
//			NGramsBean ng2 = new NGramsBean(2, cipher);
//			NGramsBean ng3 = new NGramsBean(3, cipher);
//			NGramsBean ng4 = new NGramsBean(4, cipher);
//			System.out.println(ng2.numRepeats() + "	" + ng3.numRepeats() + "	"
//					+ ng4.numRepeats() + "	" + cipher);
			
		}
	}
	
	public static void process(String path) {
		List<String> ciphers = FileUtil.loadFrom(path);
		String info = "";
		for (String cipher : ciphers) {
			if (cipher.startsWith("cipher_information")) {
				info = cipher.substring(19);
			} else {
				System.out.println(info + "	" + "=(\"" + cipher + "\")");
				info = "";
			}
		}
	}

	public static void main(String[] args) {
		//measure("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ciphers/ryan-garlick/340_path_batch_ciphers_only.txt");
		measure("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ciphers/transposition-explorer/ciphers2.txt");
		//measure("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ciphers/ryan-garlick/process_me.txt");
		//process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ciphers/ryan-garlick/process_me.txt");
	}
}
