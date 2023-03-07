package com.zodiackillerciphers.ciphers.w168;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.algorithms.RailFence;
import com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTransposition;
import com.zodiackillerciphers.ciphers.algorithms.columnar.State;
import com.zodiackillerciphers.ciphers.algorithms.columnar.Variant;
import com.zodiackillerciphers.dictionary.InsertWordBreaks;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.dictionary.WordFrequencies.WordBean;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.old.decrypto.Word;
import com.zodiackillerciphers.tests.jarlve.Permutations;

public class W168 {
	public static String TAB = "	";
	public static String[] W168 = new String[] { 
			"REDACTED"
	};
	
	// jarl's test cipher (Z408) (this is Scheme 2)
	public static String[] J168_1 = new String[] { "I E LTIIKI E  SKIOSLLM INCUG", "  HPEUFOP.NLEI  B TECSIAUM S",
			"OEMREI  F NUNHT T EHAOFN ERK", "ITSLLB INCEG UAWIESLDM  GNAA", "  LISFO TA HELL M .OSOTT K D",
			"ALING LEROSOUEMS HTANNIIM GA" };
	
	// the plaintext for J168_1
	public static String[] Z408_PT = new String[] { "I LIKE KILLING PEOPLE BECAUS", "E IT IS SO MUCH FUN. IT IS M",
			"ORE FUN THAN KILLING WILD GA", "ME IN THE FOREST BECAUSE MAN", " IS THE MOST DANGEROUS ANIMA",
			"L OF ALL. TO KILL SOMETHING " };
	
	// Z408, Scheme 1.  - Stencil: [0, 0] [1, 1] [1, 0] [0, 1]
	public static String[] Z408_1 = new String[] { "IIKKIINPELEECSET  SMU F.  IM", "L  ELL GPOB UAI SI OHCNUTI S",
			"O F TN LLG LDAMINHEORT CAE N", "ERNUAHIKNIIWG  ET F SEEBSUAM", "  T MT NGOUANALF L.O LLOMHI ",
			"SIEHSOADRE SMIO LAT IKS TEGN" };
	
	public static String[] J168_2 = new String[] { "ILIC NG  WATILLD UIGAIMES  I", "KN EMTHEE   FO REISSTK BTIEC",
			"IAU  SEL MIEANL ISRS ITH OE ", "N GS NIGHTONEM OS U LPLIMFK ", "EOTUN .OLLCAA PFOHH LLAM TIN",
			"EA FLSU ORUIEGBNANKD ETS. OM" };
	
	// global transposition only.  AZDECRYPT appears in plaintext.
	public static String[] SAM184_1 = new String[] {
			"HE PACEIIFHEPT GOI LRS ",
			"ITR   UT POISGOS ONEVTH",
			"ND SOOSCLSEETNIF .IIHTR",
			"DIS EZPEIRYPE  AB LOIV.",
			" EFSARDMC YRTVCEN SLL E",
			" GETS RO VR GHIS CSPHE ",
			"IYSTANSFEFP NT LTGNI AO",
			"RIHDIV  B  OHE YNUVAOE "			
	};
	// "easy global + local transpo" 
	public static String[] SAM184_2 = new String[] {
			"HEDI ERI SSDFPH AREVACIZEMP DI EI I CFBRHRYOYE",
			" PPVEETTH  E YCG AO BUNIN  LLASLVOR IELSOV R.H",
			"EO  AHEVT THIIPNINESG C .TO LISOF SITTHSNNGG E",
			" OEPIRFLV SEP FSOUCSTRNO  OA ST TRSS EYNGIDIT "
	};
	
	public static StringBuilder[] cipherBuilder;
	
	public static String cipherLine = "";
	static {
		cipherBuilder = StringUtils.toStringBuilder(W168);
		for (String s : W168) cipherLine += s;
	}
	
	/** try out different railfence possibilities */
	public static void railFence() {
		for (int i=1; i<100; i++) {
			String pt = RailFence.decode(cipherLine, i);
			double zk = ZKDecrypto.calcscore(pt, "EN", true);
			System.out.println(zk + " " + i + " " + badSpaces(pt) + " " + averageWordLength(pt) + " " + pt);
		}
	}
	
	/** count number of times a space happens after another space.  too many like that implies a bad transposition. */
	public static int badSpaces(String str) {
		int count = 0;
		for (int i=1; i<str.length(); i++) 
			if (str.charAt(i) == ' ' && str.charAt(i-1) == ' ') count++;
		return count;
	}
	/** calculate average word length */
	public static float averageWordLength(String str) {
		String[] split = str.split("[ .]");
		int count = 0;
		int sum = 0;
		for (String s : split) {
			if (s.length() > 0) {
				count++;
				sum += s.length();
			}
		}
		float result = sum;
		result /= count;
		return result;
	}
	/** look for bugs in encode/decode procedure */
	public static void columnarVerify() {
		Variant variant = Variant.TOP_TO_BOTTOM;
		StringBuilder sb = new StringBuilder(
				"THIS IS A TEST OF THE COLUMNAR TRANSPOSITION IN JAVA SE 7 AND LATER, YOU CAN REPLACE THE TYPE ARGUMENTS REQUIRED TO INVOKE THE CONSTRUCTOR OF A GENERIC CLASS WITH AN EM");
		StateColumnarVerify state = new StateColumnarVerify(variant, new int[] { 0, 1 }, null, sb);
		com.zodiackillerciphers.util.Permutations.recurse(state);
		state = new StateColumnarVerify(variant, new int[] { 0, 1, 2 }, null, sb);
		com.zodiackillerciphers.util.Permutations.recurse(state);
		state = new StateColumnarVerify(variant, new int[] { 0, 1, 2, 3}, null, sb);
		com.zodiackillerciphers.util.Permutations.recurse(state);
		state = new StateColumnarVerify(variant, new int[] { 0, 1, 2, 3, 4 }, null, sb);
		com.zodiackillerciphers.util.Permutations.recurse(state);
	}

	/** brute force some columnar transposition keys */
	public static void columnarBrute1() {
		StringBuilder ct = new StringBuilder("   HLRNI AENTYAP TAERROOHNCOGIAINHSEF MTPINA7 RU AHPGTQDIE TO N SHESATTOAASNJSAA CEE  M ITVTOU  RLWATITOEU STIV DEONLTYRNEE KESTFECST I S CNROO   L, RCEEUSU N CRRAEC  M");
		
		for (int n=2; n<100; n++) {
			int[] elems = new int[n];
			for (int i=0; i<n; i++) {
				elems[i] = i;
			}
			System.out.println("====== KEY LENGTH " + n + ":");
			for (Variant variant : ColumnarTransposition.variants) {
				StateColumnarBrute state = new StateColumnarBrute(variant, elems, ct, null, false);
				com.zodiackillerciphers.util.Permutations.recurse(state);
			}
		}
		
	}
	public static void columnarBrute2(boolean encode) {
		StringBuilder ct = new StringBuilder(cipherLine);
		for (int n=2; n<100; n++) {
			int[] elems = new int[n];
			for (int i=0; i<n; i++) {
				elems[i] = i;
			}
			System.out.println("====== KEY LENGTH " + n + ":");
			for (Variant variant : ColumnarTransposition.variants) {
				StateColumnarBrute state = encode ? new StateColumnarBrute(variant, elems, null, ct, encode) :
					new StateColumnarBrute(variant, elems, ct, null, encode);
				com.zodiackillerciphers.util.Permutations.recurse(state);
			}
			
		}
		
	}
	/** try all words in the dictionary */
	public static void dictionary() {
		WordFrequencies.init();
		//StringBuilder ct = new StringBuilder("NALCEHWTTDTTFSEELEEDSOAFEAHL");
		StringBuilder ct = new StringBuilder(cipherLine);
		
		/** use TreeSet to implement min/max heap of fixed number of elements. */
		TreeSet<Double> treeSet = new TreeSet<Double>();
		/** max size of heap */
		int maxHeapSize = 10;
		
		for (String word : WordFrequencies.map.keySet()) {
			int[] key = ColumnarTransposition.keyFor(word);
			StringBuilder pt = ColumnarTransposition.decode(new State(Variant.TOP_TO_BOTTOM, null, ct, key, false));
			Double scorePt = StateColumnarBrute.ngramScore(pt, 4);
			boolean print = false;
			// if heap not full, just add it
			if (treeSet.size() < maxHeapSize) {
				treeSet.add(scorePt);
				print = true;
			} else {
				// is this score better than the worst score? 
				Double worst = treeSet.first();
				if (scorePt > worst && !treeSet.contains(scorePt)) { // it's better and not a dupe score
					print = true;
					treeSet.add(scorePt); // so add to heap
					treeSet.remove(worst); // and remove the worst score
				}
			}
			
			//if (print) System.out.println(scorePt + TAB + treeSet + TAB + Arrays.toString(elements) + TAB + pt);
			if (print) System.out.println(scorePt + TAB + word + TAB + Arrays.toString(key) + TAB + pt + TAB + treeSet);
}
	}
	
	/** process sam's enumerations - output ngram scores */
	public static void processEnumerationsNgrams(String path, boolean withSpaces, boolean withoutSpaces) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		String tab = "	";
		
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			if (line.isEmpty()) continue;
			if (line.startsWith("bigram_score")) continue;
			String result = "";
			if (withoutSpaces)
				result += NGramsCSRA.zkscore(new StringBuffer(line.replaceAll(" ", "").replaceAll("\\.", "")), "EN", false) + tab;
			if (withSpaces)
				result += NGramsCSRA.zkscore(new StringBuffer(line), "EN", true) + tab;
			System.out.println(result + tab + line);
			result = "";
			line = new StringBuffer(line).reverse().toString(); // test reverse direction too
			if (withoutSpaces)
				result += NGramsCSRA.zkscore(new StringBuffer(line.replaceAll(" ", "").replaceAll("\\.", "")), "EN", false) + tab;
			if (withSpaces) 
				result += NGramsCSRA.zkscore(new StringBuffer(line), "EN", true);
			System.out.println(result + tab + line);
		}
	}
	/** process sam's enumerations - output found words */
	public static void processEnumerationsWords(String path) {
//		WordFrequencies.init();
//		String tab = "	";
//		int maxLength = 0;
//		
//		List<String> lines = FileUtil.loadFrom(path);
//		Map<String, Integer> counts = new HashMap<String, Integer>(); 
//		for (String line : lines) {
//			if (line.isEmpty()) continue;
//			if (line.startsWith("bigram_score")) continue;
//			String line2 = line.replaceAll(" ", "").replaceAll("\\.", "");
//			System.out.println(line);
//			System.out.println(line2);
//			List<WordBean> words = WordFrequencies.findAllWordsInOrder(line, 4, -1);
//			if (words != null) {
//				for (WordBean word : words) {
//					System.out.println(word.word.length() + tab + word.frequency + tab + word.word);
//					if (word.word.length() > maxLength) maxLength = word.word.length();
//					Integer count = counts.get(word.word);
//					if (count == null) count = 0;
//					count++;
//					counts.put(word.word, count);
//				}
//			}
//		}
//		for (int L = maxLength; L >= 4; L--) {
//			for (String key : counts.keySet()) {
//				if (key.length() == L) {
//					System.out.println(L + tab + key + tab + counts.get(key) + tab + WordFrequencies.freq(key));
//				}
//			}
//		}
		WordFrequencies.findAllWordsInFile(path, 4, false, true);
	}
	
	public static void testCandidate() {
		int[] key = new int[] {1, 6, 0, 4, 5, 3, 7, 8, 2, 9, 10};
		State state = new State(Variant.TOP_TO_BOTTOM, null, new StringBuilder(cipherLine), key);
		ColumnarTransposition.decode(state);
		System.out.println(state);
	}
	
	public static void main(String[] args) {
//		InsertWordBreaks.init("EN", false);
//		railFence();
		//System.out.println(averageWordLength(cipherLine));
//		columnarVerify();
//		columnarBrute1();
//		columnarBrute2(false);
//		columnarBrute2(true);
//		WordFrequencies.findAllWordsInFile("/Users/doranchak/projects/ciphers/W168/brute-force-results-combined-for-word-search.txt", 4, true);
//		dictionary();
//		processEnumerationsNgrams("/Users/doranchak/projects/ciphers/W168/combined.txt");
//		processEnumerationsNgrams("/Users/doranchak/projects/ciphers/W168/fbi_cipher_enumerations/combined.txt", false, true);
//		processEnumerationsWords("/Users/doranchak/projects/ciphers/W168/fbi_cipher_enumerations/combined.txt");
//		processEnumerationsWords("/Users/doranchak/projects/ciphers/W168/brute-force-results-combined-for-word-search.txt");
//		testCandidate();
	}
}
