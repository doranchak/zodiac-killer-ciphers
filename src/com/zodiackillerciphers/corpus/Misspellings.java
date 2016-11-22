package com.zodiackillerciphers.corpus;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;

/** search for misspellings and other unique words from the Zodiac letters */
public class Misspellings {
	public static String[] words = new String[] { "abnormily", "abot", "accid",
			"allways", "anamal", "anilating", "aprox", "averly", "bluber",
			"booboos", "brunett", "bussy", "buton", "butons", "caen", "cene",
			"christmass", "cicles", "circut", "claif", "closeing", "comidy",
			"commic", "committ", "complet", "consternt", "controol",
			"coupples", "crackproof", "cruzeing", "cyipher", "dangeroue",
			"darck", "descise", "diablo", "dificult", "disconect", "doo",
			"dungen", "ebeorietemethhpiti", "efect", "efective", "entirle",
			"epasode", "evere", "experence", "extreamly", "figgure", "fireing",
			"frunt", "fryst", "hapen", "howers", "hummerest", "idenity",
			"idiout", "impriest", "intersting", "inthusastic", "lyeing",
			"meannie", "meannies", "mery", "motorcicles", "nevermind",
			"nineth", "noze", "nucences", "orginast", "origionaly", "oute",
			"paradice", "paterned", "pepermint", "pestulentual", "phomphit",
			"phraises", "pleass", "posibly", "positivily", "provences",
			"raceing", "rhbates", "roat", "rubed", "runnig", "saterical",
			"seranader", "shabbly", "shakeing", "silowets", "singurly", "sloi",
			"som", "srounded", "swiches", "symbionese", "teritory", "thashing",
			"themsf", "thingmebob", "thoes", "titwillo", "toschi", "triger",
			"truley", "twich", "twiched", "unnoticible", "unspoiling",
			"useing", "vallejo", "ventalate", "victom", "wachmacallit",
			"wateing", "waveing", "whashisname", "whrite", "wipeing", "woeman",
			"xmass" };

	public static Set<String> set() {
		Set<String> set = new HashSet<String>();
		for (String word : words)
			set.add(word);
		return set;
	}

	public static void search(String dirCorpus, String dirTmp, String gPrefix) {

		Set<String> dict = set();
		Search.makeUnzipDir(dirTmp + "/unzipped");

		Map<String, IndexEntry> index = Index.makeIndex("docs/corpus/gutenberg-index.txt");
		List<File> files = Reader.list(dirCorpus);
		System.out.println("Number of files to process: " + files.size());
		long length = 0;
		String text;
		for (int f = 0; f < files.size(); f++) {
			float p = 100 * ((float) f) / files.size();
			System.out.println(f + " of " + files.size() + " (" + (int) p
					+ "%)");
			File file = files.get(f);
			if (file.getName().toLowerCase().endsWith("_h.zip")) {
				continue; // ignoring HTML zips
			}
			try {
				text = Search.read(file, dirTmp);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if (text == null)
				continue;
			length += text.length();

			// tokenize text
			String[] tokens = FileUtil.tokenize(text);
			Set<String> foundWords = new HashSet<String>();
			for (String token : tokens) {
				String lower = token.toLowerCase();
				//System.out.println(lower);
				if (dict.contains(lower)) {
					System.out.println("Match: " + lower.length() + ", "
							+ lower + ", " + file.getAbsolutePath());
					String upper = lower.toUpperCase();
					foundWords.add(upper);
				}
			}
			for (String word : foundWords) {
				System.out.println("Locating " + word + " in text of length " + text.length());
				if (text.length() > 2000000) {
					System.out.println("Skipping lengthy text");
					continue;
				}
				String[] bib = Processor.infoFrom(file.getAbsolutePath(), index, gPrefix);
				FileUtil.locate(word, text, file.getName(), false, bib[1], bib[0], 50, false, null);
			}
		}

		System.out.println("Done processing files.  Total text length: "
				+ length);

	}
	public static void main(String[] args) {
		search(args[0], args[1], args[2]);
	}
}
