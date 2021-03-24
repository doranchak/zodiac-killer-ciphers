package com.zodiackillerciphers.corpus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.names.Name;
import com.zodiackillerciphers.old.MyNameIs;
import com.zodiackillerciphers.suffixtree.LRS;
import com.zodiackillerciphers.tests.unicity.PlaintextBean;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

public class Z13CorpusScanner extends CorpusBase {
	// search corpus for plaintext that fits Z13.
	public static void search() {
		WordFrequencies.init();
		CorpusBase.REDDIT_ONLY = true;
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long hits = 0;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngrams = ngrams(13);
			for (List<String> plaintext : ngrams) {
				StringBuffer sbWithSpaces = flatten(plaintext, true);
				StringBuffer sbWithoutSpaces = flatten(plaintext, false);
				System.out.println("pt " + sbWithSpaces);
				samples++;
				if (MyNameIs.fitSub(sbWithoutSpaces)) {
					// reject if most words are too short
					if (PlaintextBean.badAverageTokenLength(plaintext))
						continue;
					// reject if string is too repetitive
					if (PlaintextBean.tooRepetitive(sbWithoutSpaces.toString()))
						continue;
					hits++;
					System.out.println(WordFrequencies.scoreLog(sbWithSpaces.toString()) + " " + sbWithSpaces);
				}
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
	}

	static void process() {
		WordFrequencies.init();
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/z13-corpus-scanner.txt");
		Set<String> seen = new HashSet<String>();
		for (String line : lines) {
			if (line.startsWith("Loading from"))
				continue;
			line = line.substring(line.indexOf(" ") + 1);
			if (seen.contains(line)) continue;
			String lineNS = line.replaceAll(" ", "");
			if (lineNS.length() != 13) {
				System.out.println("not 13: " + lineNS);
				continue;
			}
			String[] split = line.split(" ");

			if (split.length > 7)
				continue;
			int lrs = LRS.lrs(lineNS).length();
			if (lrs > 3) continue;
			float score = WordFrequencies.scoreLog(line);
			double ioc = Stats.ioc(lineNS);
			double scoreTotal = (1+score)*(1+ioc)/lrs;
			String hom = Ciphers.isHomophonic(Ciphers.decoderMapFor(Ciphers.Z13, lineNS)) ? "HOM" : "SUB";
			System.out.println(scoreTotal + "	" + hom + "	" + lrs + "	" + ioc + "	" + score + "	" + line);
			seen.add(line);
		}
	}
	
	public static void main(String[] args) {
		search();
//		process();
	}
}
