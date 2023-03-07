package com.zodiackillerciphers.ciphers.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.algorithms.Myszkowski;
import com.zodiackillerciphers.ciphers.algorithms.Vigenere;
import com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTranspositionRumkin;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.tests.LetterFrequencies;
import com.zodiackillerciphers.transform.CipherTransformations;

import ec.util.MersenneTwisterFast;

/** generate ciphers that resemble the 340 */
public class Generator {
	public static String LETTERS_PATH = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others";
	//public static String OUTPUT_PATH = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/candidate-plaintexts";
	//public static String OUTPUT_PATH = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/candidate-plaintexts-columnar";
	//public static String OUTPUT_PATH = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/candidate-plaintexts-scytale";
	public static String OUTPUT_PATH = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/candidate-plaintexts-myszkowski";
	//public static String OUTPUT_PATH = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/candidate-plaintexts-vigenere";
	public static String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	public static List<String> tokens;
	public static StringBuffer allText;
	
	public static Random rand = new Random();
	
	public static void loadLetters() {
		if (tokens != null) return;
		allText = new StringBuffer();
		try {
			File dir = new File(LETTERS_PATH);
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.getName().contains("envelope")) continue;
				if (file.getName().endsWith(".txt")) {
					System.out.println(file);
					allText.append(FileUtil.loadSBFrom(file));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] rawTokens = FileUtil.tokenize(allText.toString());
		tokens = new ArrayList<String>();
		for (String token: rawTokens) {
			String newToken = token.toUpperCase().replaceAll("[^A-Z]", "");
			if (newToken.length() == 0) continue;
			tokens.add(newToken);
		}
		
		//for (String token : tokens) System.out.println(token);
	}
	
	/** get plaintext from tokens, starting at token #start.  return text with given total length.  */
	public static CandidatePlaintext plaintext(int start, int length) {
		CandidatePlaintext cp = new CandidatePlaintext();
		StringBuffer plain = new StringBuffer();
		
		// pick some random length of filler in the range [10,20]
		int fillerLength = 10 + rand.nextInt(11);
		// pick a random location for the filler text
		int fillerPosition = rand.nextInt(340-fillerLength+1);
		boolean fillerUsed = false;
		while (plain.length() < length && start < tokens.size()) {
			
			String token = tokens.get(start++);
			String tokenMutated = mutate(token, 1.0f/15);
			if (!token.equals(tokenMutated)) cp.errors++;
			if (plain.length() + tokenMutated.length() <= length) plain.append(tokenMutated);
			else plain.append(tokenMutated.substring(0,length-plain.length()));
			cp.tokens.add(tokenMutated);

			// put filler approximately at the random spot 
			if (!fillerUsed && plain.length() >= fillerPosition) {
				cp.fillerStart = plain.length();
				cp.fillerEnd = plain.length() + fillerLength - 1;
				for (int i=0; i<fillerLength && plain.length() < length; i++) plain.append('_');
				fillerUsed = true;
			}
			
		}
		if (plain.length() < length) return null;
		
		cp.plaintext = plain.toString();
		cp.index = start;
		return cp;
	}
	
	/** occasionally introduce spelling mistakes */
	public static String mutate(String token, float probability) {
		if (rand.nextFloat() > probability) return token;
		
		float p = rand.nextFloat();
		if (p < 0.05) { // infrequently, we remove or duplicate entire words
			int which = rand.nextInt(2);
			if (which == 0) return ""; // delete word
			return token+token; // duplicate word
		}
		
		// mess around at the letter level
		StringBuffer sb = new StringBuffer(token);
		int which = rand.nextInt(4);
		if (which == 0) { // remove random letter
			sb.deleteCharAt(rand.nextInt(sb.length()));
		} else if (which == 1) { // add random letter
			sb.insert(rand.nextInt(sb.length()+1), LetterFrequencies.randomLetter().toUpperCase());
		} else if (which == 2) { // replace random letter
			int i = rand.nextInt(sb.length());
			sb.replace(i, i+1, LetterFrequencies.randomLetter().toUpperCase());
		} else if (which == 3) { // duplicate random letter
			int i = rand.nextInt(sb.length());
			char ch = sb.charAt(i);
			sb.insert(i, ch);
		}
		return sb.toString();
	}

	public static void write(CandidatePlaintext can) {
		FileUtil.writeText(OUTPUT_PATH + "/candidate-" + can.index + suffix(can) + ".txt", can.toString());
	}
	
	public static String suffix(CandidatePlaintext can) {
		String s = "";
		
		if (can.vigenereKey != null) return "-" + can.vigenereKey;
		if (can.scytalePeriod != null) return "-" + can.scytalePeriod;
		if (can.columnarTranspositionInfo == null) return s;
		for (int i=0; i<can.columnarTranspositionInfo.columns.length; i++) {
			s += "-";
			s += can.columnarTranspositionInfo.columns[i];
		}
		return s;
		
	}
	public static void writeHTML(CandidatePlaintext can) {
		String fileName = "candidate-" + can.index + ".html";
		System.out.println("writing html " + fileName);
		FileUtil.writeText(OUTPUT_PATH + "/" + fileName, can.toHtml());
	}
	
	/** look for plain text candidates that have 2 pivots pointing in the same direction */
	public static void candidateSearch() {
		
		//MersenneTwisterFast rand = new MersenneTwisterFast();
		
		loadLetters();
		
		int count = 0;
		while (true) {
			
			try {
				int i = rand.nextInt(tokens.size());
				CandidatePlaintext can = plaintext(i, 340);
				can.originalPlaintext = can.plaintext;
				//columnar(can);
				//scytale(can);
				myszkowski(can);
				//vigenere(can);
				if (can.criteriaAll()) {
					if (ignore(can)) {
						System.out.println("IGNORING: " + can.toString());
						continue;
					}
					System.out.println(can.toString());
					write(can);
					//break;
				} else {
				  //TODO: fail map	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			count++;
			if (count % 100000 == 0) 
				System.out.println(count + " tries");
		}
		
		/* sequential version
		for (int i=0; i<tokens.size(); i++) {
			System.out.println(i + " of " + tokens.size() + "...");
			for (int j=0; j<5000; j++) {
				CandidatePlaintext can = plaintext(i, 340);
				columnar(can);
				if (can.criteriaAll()) {
					System.out.println(can.toString());
					write(can);
					break;
				}
			}
		}*/
	}
	
	static boolean ignore(CandidatePlaintext can) {
		Map<String, Integer> counts = new HashMap<String, Integer>(); 
		for (String key : can.tokens) {
			Integer val = counts.get(key);
			if (val == null) val = 0;
			val++;
			if (key.length() > 5 && val > 15) {
				return true;
			}
			if (key.length() > 8 && val > 9) {
				return true;
			}
			if (val > 20) return true;
			counts.put(key, val);
		}
		return false;
	}
	
	/** do it randomly */
	public static void candidateSearchRandom() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		loadLetters();
		
		int threads = 10;
		Thread[] gens = new Thread[threads];
		
		
		for (int i=0; i<gens.length; i++) gens[i] = new GeneratorThread();
		for (int i=0; i<gens.length; i++) gens[i].start();
	}
	
	/** apply a random columnar transposition key to the given plaintext */
	static void columnar(CandidatePlaintext cp) {
		int[] columns;
		MersenneTwisterFast rand = new MersenneTwisterFast();
		// generate a key length somewhere in [2,20]
		int L = rand.nextInt(19) + 2;
		columns = new int[L];
		for (int i=0; i<L; i++) columns[i] = i+1; // columns are numbered beginning with 1
		
		CipherTransformations.shuffle(columns);
		
		cp.columnarTranspositionInfo = new ColumnarTranspositionInfo();
		cp.columnarTranspositionInfo.columns = columns;
		cp.columnarTranspositionInfo.decoded = cp.plaintext;

		// perform transposition.  result uses irregular transposition unless the key length
		// is a factor of the cipher length.
		String encoded = ColumnarTranspositionRumkin.encode(cp.plaintext, columns);
		cp.plaintext = encoded;
	}
	
	/** apply a random scytale transposition key to the given plaintext */
	static void scytale(CandidatePlaintext cp) {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		// generate a key length somewhere in [2,170]
		int period = rand.nextInt(169) + 2;
		cp.plaintext = Periods.rewrite3(cp.plaintext, period);
		cp.scytalePeriod = period;
	}
	
	/** apply a random myszkowski transposition key to the given plaintext */
	static void myszkowski(CandidatePlaintext cp) {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		
		// generate a key length somewhere in [2,20]
		int L = rand.nextInt(19) + 2;
		int[] key = new int[L];
		Random r = new Random();
		for (int j=0; j<key.length; j++) {
			key[j] = r.nextInt(L);
		}
		
		cp.columnarTranspositionInfo = new ColumnarTranspositionInfo();
		cp.columnarTranspositionInfo.columns = key;
		cp.columnarTranspositionInfo.decoded = cp.plaintext;

		// perform transposition
		String encoded = Myszkowski.encode(cp.plaintext, key);
		cp.plaintext = encoded;		
	}

	/** apply a random vigenere key to the given plaintext */
	static void vigenere(CandidatePlaintext cp) {
		String key = "";
		MersenneTwisterFast rand = new MersenneTwisterFast();
		// generate a key length somewhere in [2,40]
		int length = rand.nextInt(39) + 2;
		
		for (int i=0; i<length; i++) {
			key += alphabet.charAt(rand.nextInt(alphabet.length()));
		}
		cp.plaintext = Vigenere.encrypt(cp.plaintext, key).toUpperCase();
		cp.vigenereKey = key;
	}
	
	
	// http://www.zodiackillersite.com/viewtopic.php?p=43385#p43385
	public static void findForSmokie() {
		loadLetters();
		allText = FileUtil.convert(allText.toString());
		//System.out.println(allText);
		for (int i=0; i<allText.length()-340+1; i++) {
			String sub = allText.substring(i,i+340);
			for (int p=19; p<20; p++) {
				String s = Periods.rewrite3(sub, p);
				NGramsBean bean = new NGramsBean(2, s);
				if (bean.numRepeats() > 35) {
					System.out.println(p + " " + bean.numRepeats() + " " + sub);
				}
			}
		}
	}
	
	public static void testTokens() {
		loadLetters();
		System.out.println(tokens);
	}
	
	public static void main(String[] args) {
		//candidateSearch();
		//candidateSearchRandom();
		//findForSmokie();
		//dump(5049);
		testTokens();
		
	}	
}
