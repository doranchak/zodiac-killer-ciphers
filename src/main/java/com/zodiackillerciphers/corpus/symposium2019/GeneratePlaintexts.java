package com.zodiackillerciphers.corpus.symposium2019;

import java.util.Arrays;
import java.util.List;

import com.cybozu.labs.langdetect.LangDetectException;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

public class GeneratePlaintexts extends CorpusBase {
	/** generate n plaintexts of the given length.  output both with and without spaces, and some language stats.
	 * write the output to the given file. */
	public static void generate(int length, int n, String outputPath) {
		WordFrequencies.init();
		String tab = "	";
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		long hits = 0;

		LanguageDetection ld = new LanguageDetection();
		try {
			ld.init("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/lib/language-detection-master/profiles");

			while (true) {
				SubstitutionMutualEvolve.randomSource();
				List<List<String>> ngrams = ngrams(length);
				if (ngrams == null || ngrams.isEmpty())
					continue;

				for (int i = 0; i < 25; i++) {
					List<String> plaintext = ngrams.get(rand.nextInt(ngrams.size()));
					StringBuffer sbWithSpaces = flatten(plaintext, true);
					StringBuffer sbWithoutSpaces = flatten(plaintext, false);
					StringBuffer line = new StringBuffer();

					double stat1 = Stats.ioc(sbWithoutSpaces);
					double stat2 = Stats.entropy(sbWithoutSpaces);
					double stat3 = Stats.chi2(sbWithoutSpaces);

					double diff1 = Stats.iocDiff(sbWithoutSpaces);
					double diff2 = Stats.entropyDiff(sbWithoutSpaces);
					double diff3 = Stats.chi2Diff(sbWithoutSpaces);

					double diffsum = Math.abs(diff1) + Math.abs(diff2) + Math.abs(diff3);
					if (diffsum > 0.2)
						continue;
					line.append(ld.detectLangs(sbWithSpaces.toString()) + tab + (hits++) + tab + file + tab + sbWithSpaces + tab + sbWithoutSpaces + tab + stat1 + tab
							+ diff1 + tab + stat2 + tab + diff2 + tab + stat3 + tab + diff3 + tab + diffsum);
					System.out.println(line);
				}
				if (hits >= n)
					break;
			}
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	public static void processPlaintexts(String path) {
		String tab = "	";
		LanguageDetection ld = new LanguageDetection();
		try {
			ld.init("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/lib/language-detection-master/profiles");
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		List<String> list = FileUtil.loadFrom(path);
		for (String line : list) {
			String text = line.split(tab)[2];
			try {
				System.out.println(ld.detectLangs(text) + tab + line);
			} catch (LangDetectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		generate(340, 1000000, null);
//		processPlaintexts("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/candidate-plaintexts-1.txt");
	}
	
}
