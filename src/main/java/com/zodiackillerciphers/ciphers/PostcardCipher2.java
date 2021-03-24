package com.zodiackillerciphers.ciphers;

import java.util.Map;

import com.zodiackillerciphers.dictionary.Solver;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;

//http://scienceblogs.de/klausis-krypto-kolumne/2018/05/26/a-postcard-with-a-sherlock-holmes-type-encryption/?utm_source=feedly&utm_medium=rss&utm_campaign=a-postcard-with-a-sherlock-holmes-type-encryption

// ABCDE EDBF ABEBGC
public class PostcardCipher2 {
	public static void postProcess() {
		WordFrequencies.init();
		for (String line : FileUtil.loadFrom("/Users/doranchak/projects/zodiac/postcard-cipher-results-sorted.txt")) {
			String[] split = line.split(" ");
			long score = Long.valueOf(split[0]);
			for (String word : WordFrequencies.map.keySet()) {
				if (word.length() == 4) {
					if (word.substring(2).equals(split[2].substring(4))) {
						long score2 = score * (1+WordFrequencies.percentile(word));
						System.out.println(score2 + " " + word + " ____ " + split[1] + " " + split[2]);
					}
				}
			}

		}
	}

	public static void search() {
		//WordFrequencies.init();
		// LIMANOPNOQK HIJK
		// 01234567890 0123
		
//		Solver.solve(new String[] {"LIMANOPNOQK", "HIJK"});
		
		// HIJK.LIMANOPNOQK.ORR.ABCDE.DI.ST.F.EDBF.ST.ABEBGC.OGF.ST.KSU
		//Solver.solve(new String[] {"ABEBGC", "ABCDE", "EDBF", "F", "LIMANOPNOQK"});
		
	}

	public static void main(String[] args) {
		//search();
		//postProcess();
		//Solver.findRepeats("HIJK LIMANOPNOQK ORR ABCDE DI F EDBF ABEBGC OGF ST KSU");
		Solver.solve("ABEBGC ABCDE EDBF F", "HIJK LIMANOPNOQK ORR ABCDE DI ST F EDBF ST ABEBGC OGF ST KSU");
	}
}
