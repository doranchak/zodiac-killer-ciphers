package com.zodiackillerciphers.tests.samblake;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.InsertWordBreaks;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.ngrams.RepeatingFragmentsFaster;
import com.zodiackillerciphers.tests.ZodiacWords;

/** process results of Sam Blake's many cipher transformations */
public class TransformedCiphers {
	public static String folder = "/Users/doranchak/projects/zodiac/sam-blake-experiments";
	public static String tab = "	";
	public static boolean INIT = false;
	static {
		NGramsCSRA.useCachedMaps = true;
		if (INIT) ZodiacWords.init();
	}
	public static void process(Cipher cipher) {
		// calculate ngram scores
		if (cipher.cipherText != null) {
			NGramsBean ng = null;
			ng = new NGramsBean(2, cipher.cipherText);
			cipher.n2reps = ng.numRepeats();
			ng = new NGramsBean(3, cipher.cipherText);
			cipher.n3reps = ng.numRepeats();
			ng = new NGramsBean(4, cipher.cipherText);
			cipher.n4reps = ng.numRepeats();
			// 2-cycle homophone stats
			try {
				cipher.pcs2 = HomophonesNew.perfectCycleScoreFor(2, cipher.cipherText, 3, false);
			} catch (Exception e) {
				System.out.println("PCS2 ERROR FOR " + cipher.name);
				e.printStackTrace();
			}
		}
		
		// insert word breaks
		if (cipher.zkPlaintext != null && !cipher.zkPlaintext.isEmpty())
			cipher.zkPlaintextWithBreaks = InsertWordBreaks.findWordBreaks(new StringBuffer(cipher.zkPlaintext), "EN", false).toString();
		if (cipher.azPlaintext != null && !cipher.azPlaintext.isEmpty())
			cipher.azPlaintextWithBreaks = InsertWordBreaks.findWordBreaks(new StringBuffer(cipher.azPlaintext), "EN", false).toString();
		
		// word scores
		cipher.zkPlaintextWords = new ArrayList<String>();
		if (cipher.zkPlaintext != null && !cipher.zkPlaintext.isEmpty()) {
			cipher.zkZodiacWordScore = ZodiacWords.wordScore(cipher.zkPlaintextWithBreaks, false, cipher.zkPlaintextWords);
			cipher.zkZodiacWordScoreAscending = ZodiacWords.wordScore(cipher.zkPlaintextWithBreaks, true, null);
		}
		cipher.azPlaintextWords = new ArrayList<String>();
		if (cipher.azPlaintext != null && !cipher.azPlaintext.isEmpty()) {
			cipher.azZodiacWordScore = ZodiacWords.wordScore(cipher.azPlaintextWithBreaks, false, cipher.azPlaintextWords);
			cipher.azZodiacWordScoreAscending = ZodiacWords.wordScore(cipher.azPlaintextWithBreaks, true, null);
		}
	}
	public static void process() {
		
		InsertWordBreaks.init("EN", true);
		
		try {
			File root = new File(folder);
			Set<String> names = new HashSet<String>();
			for (File file : root.listFiles()) {
				if (file.getName().endsWith(".txt")) {
					names.add(file.getName().substring(0, file.getName().length()-4));
					System.out.println(file.getName());
				}
			}
			
			for (String name : names) {
				// read the cipher text
				Cipher cipher = new Cipher();
				cipher.name = name;
				File file = new File(folder + "/" + name + ".txt");
				cipher.cipherText = FileUtil.loadSBFrom(file).toString().replaceAll(" ", "");
//				if (cipher.cipherText.length() != 340) 
//					System.out.println("  - length is " + cipher.cipherText.length());

				// read the zk logs
				for (String line : FileUtil.loadFrom(folder + "/" + name + ".log")) {
					String[] split = line.split(",");
					cipher.zkScore = Integer.valueOf(split[0]);
					cipher.zkPlaintext = split[1];
					break;
				}
				
				process(cipher);
				System.out.println(cipher);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void processAZDecryptResults(String path) {

		List<Cipher> toProcess = new ArrayList<Cipher>();
		InsertWordBreaks.init("EN", true);

		File root = new File(path);
		for (File file : root.listFiles()) {
			if (file.getName().endsWith(".txt")) {
				// read the cipher text
				Cipher cipher = new Cipher();

				List<String> list = FileUtil.loadFrom(file.getAbsolutePath());
				int count = 0;
				StringBuffer pt = new StringBuffer();
				for (String line : list) {
					// name is on line 4
					// pt begins on line 6
					if (count == 3) {
//						if (!line.endsWith(".txt")) {
//							System.err.println("line 4 isn't the file name.  " + line + ", " + path);
//						} else
						cipher.name = line;
					} else if (count > 4) {
						pt.append(line);
					}
					count++;
				}
				cipher.azScore = Integer.valueOf(file.getName().split("_")[0]);
				cipher.azPlaintext = pt.toString();

				//process(cipher);
				//System.out.println(cipher);
				toProcess.add(cipher);
			}
		}
		
		int THREADS = 8;
		ProcessorThread[] threads = new ProcessorThread[THREADS];
		for (int i=0; i<THREADS; i++) {
			threads[i] = new ProcessorThread();
		}
		int which = 0;
		for (Cipher cipher : toProcess) {
			threads[which].addCipher(cipher);
			which = (which + 1) % THREADS;
		}
		
		for (ProcessorThread thread : threads) {
			thread.start();
		}
		for (ProcessorThread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (ProcessorThread thread : threads) {
			for (Cipher cipher : thread.ciphers) 
				System.out.println(cipher);
		}
		
	}
	
	/** return map of ciphers, keyed by distinct ciphertexts.  used to filter out duplicates */
	public static void processUniques(String path, Map<String, Set<String>> map) {
		File root = new File(path);
		for (File file : root.listFiles()) {
			if (file.getName().endsWith(".txt")) {
				List<String> list = FileUtil.loadFrom(file.getAbsolutePath());
				if (list.size() != 1) throw new RuntimeException(list.size() + " LINES IN " + file.getAbsolutePath() + ": " + list);
				String cipher = list.get(0);
				Set<String> set = map.get(cipher);
				if (set == null) set = new HashSet<String>();
				set.add(file.getAbsolutePath().replace("/Users/doranchak/projects/zodiac/sam blake ciphers/", ""));
				map.put(cipher, set);
			}
		}
	}
	public static void processUniques() {
		Map<String, Set<String>> map1 = new HashMap<String, Set<String>>();
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/1st batch (with duplicates)/block_enumerations_row_major", map1);
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/1st batch (with duplicates)/block_enumerations_col_major", map1);
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/3rd batch (duplicates removed, mistakes corrected)/block_enumerations_row_major", map1);
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/3rd batch (duplicates removed, mistakes corrected)/block_enumerations_col_major", map1);
		
//		System.out.println(map1.size());
//		int max = 0;
//		String maxKey = null;
//		for (String key : map1.keySet()) {
//			if (map1.get(key).size() > max) {
//				max = map1.get(key).size();
//				maxKey = key;
//			}
//		}
//		System.out.println("max: " + max);
//		System.out.println(maxKey);
//		System.out.println(map1.get(maxKey));
		
		for (String key : map1.keySet()) {
			String info = null;
			for (String name : map1.get(key)) {
				info = name;
				break;
			}
			String[] split = info.split("/");
			info = split[split.length-1];
			System.out.println("cipher_information=" + info);
			System.out.println(key);
			System.out.println();
		}

//		System.out.println("Checking deduped set for new ones...");
//		Map<String, Set<String>> map2 = new HashMap<String, Set<String>>();
//		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/3rd batch (duplicates removed, mistakes corrected)/block_enumerations_row_major", map2);
//		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/3rd batch (duplicates removed, mistakes corrected)/block_enumerations_col_major", map2);
//		for (String key : map2.keySet()) {
//			if (map1.containsKey(key)) continue;
//			System.out.println(key + " " + map2.get(key));
//		}
			
		
	}
	public static void processUniques2() {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/4th batch (grid enumerations)/4by9by8_A", map);
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/4th batch (grid enumerations)/4by9by8_F", map);
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/4th batch (grid enumerations)/4by9by8_E", map);
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/4th batch (grid enumerations)/4by9by8_B", map);
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/4th batch (grid enumerations)/4by9by8_C", map);
		processUniques("/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/4th batch (grid enumerations)/4by9by8_D", map);
		for (String key : map.keySet()) {
			String info = null;
			for (String name : map.get(key)) {
				info = name;
				break;
			}
			String[] split = info.split("/");
			info = split[split.length-1];
			System.out.println("cipher_information=" + info);
			System.out.println(key);
			System.out.println();
		}
	}

	public static void makeForAZDecrypt() {
		try {
			System.out.println("start");
			File root = new File(folder);
			Set<String> names = new HashSet<String>();
			for (File file : root.listFiles()) {
				if (file.getName().endsWith(".txt")) {
					String info = file.getName().substring(0, file.getName().length()-4);
					String cipherText = FileUtil.loadSBFrom(file).toString().replaceAll(" ", "");
					System.out.println("cipher_information=" + info);
					System.out.println(cipherText + "\n");
					//System.out.println(System.getProperty("line.separator"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** generate ngram map from given text */
	public static Map<String, Set<Integer>> ngramMapFor(int n, StringBuffer text) {
		Map<String, Set<Integer>> index = new HashMap<String, Set<Integer>>();
		for (int i=0; i<text.length()-n+1; i++) {
			String ng = text.substring(i, i+n);
			if (ng.contains(" ")) continue;
			Set<Integer> val = index.get(ng);
			if (val == null) val = new HashSet<Integer>();
			val.add(i);
			index.put(ng, val);
		}
		return index;
	}
	
	public static StringBuffer getPlaintexts(String plaintextsFile) {
		List<String> list = FileUtil.loadFrom(plaintextsFile);
		StringBuffer plaintexts = new StringBuffer();
		for (String s : list) plaintexts.append(s).append(" "); // adding space so we don't match on boundaries between candidate plaintexts
		
		System.out.println("Done making index");
		return plaintexts;
	}
	
	/** read all the putative plaintexts into memory.
	 * find large substring matches between the given corpus and the plaintexts.
	 * idea is to try to find long fragments of sensible English. 
	 */
	public static void findLargeMatchingSubstrings(int n, StringBuffer plaintexts, String corpus, Map<String, Set<Integer>> index, String description) {
		
		for (int i=0; i<corpus.length()-n+1;i++) {
			String sub = corpus.substring(i,i+n);
			Set<Integer> set = index.get(sub);
			if (set == null) {
//				System.out.println("NO MATCH FOR " + sub);
				continue;
			}
//			System.out.println("MATCH FOR " + sub);
			for (Integer pos : set) {
				String sub2 = sub;
				int offset = n;
				
				int pos1 = i+offset; // position in the corpus
				int pos2 = pos+offset; // position in the corpus;
				
				while (pos1 < corpus.length() && pos2 < plaintexts.length()) {
					char c1 = corpus.charAt(pos1);
					char c2 = plaintexts.charAt(pos2);
					if (c1 == ' ' || c2 == ' ') break;
					if (c1 != c2) break;
					sub2 += "" + c1;
					offset++;
					pos1 = i+offset; // position in the corpus
					pos2 = pos+offset; // position in the plaintexts;
				}
				if (sub2.length() > 12) 
					System.out.println(sub2.length() + "	" + i + "	" + pos + "	" + sub2 + "	" + description);
			}
		}
		
		
	}

	public static void testProcess() {
		InsertWordBreaks.init("EN", true);
		Cipher c = new Cipher();
		c.cipherText = "+z+.4./OO+B-k49kOp<pD;NFL+S+>%t^&8y7Nb+5+FlRZPJNH_E(B.+Oz+AfM|KGfdlc+B5EP7RZ)cF5<28Y6%2^(B+FSypl9(1GdB:pWV&cTHTldzt7YKz59.+OK>R^pSEXbf-+Cp>^4#*p^Rd55yW+zO|b-4@kcW2BDc+V+>pCpH3c5_cWVyYB-4##+BfUtN))RD.FqJJM+S*(-|)K1*2<cF#k(W1RJ|_/(#*(pM*pBXG|L+M+OO<2)c<tWA+BkYZ;G|DLFHGTOjVy2|*TC4.|jzLGM+Kz<P8RlclUFKdC2F|M/zOB9V:+CLUR6lUBT^23L8;6F2UqNM+KzVZU";
		c.name = "test cipher, first 340 of 408 solution";
		c.zkPlaintext = "ILIDEDILLINGPEOPLEBECAUSEITISSOMECHRUNITISMOREFUNTHANDILLINDWILDDOMEINTHERORRESTBECAUSEMANISTHEMOATDONHERTEEANAMOLORALLTODILLSOMETHANDGIVESMETHEMOOTTHRILLINGEIPERENCEITISEVENSETTERTHANGETTINDYOURROCDSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENADIEIWILLBEREBORNINPARADICESNDALETHEIHAVEDIELEDWILLBECOMEMYSLOVESIWILLNOTHIVEYOUMYNAMESECAUSEYOUWILLTRY";
		c.zkScore = 38947;
		process(c);
		System.out.println(c);
	}
	public static void testProcess2() {
		InsertWordBreaks.init("EN", true);
		Cipher c = new Cipher();
		// scramble of z340
		c.cipherText = "FfO|+|BAL*ZKBKtSO^pW5%z*<ROp_.d6+#L)c8E1>/+BTk(FYXpN2|3kz4O3HRCNH+-.OS|ORMzpWc5-G+Mk5lGpH7d:#1lBR2+Vt8BNz.FGGT2+Ydp7FUTcOE:X)Jb2fD+Op+KjW-B*4+LCtbKLWEDzt#2M|F_W+pp(bjpTK.^k<-4UV|+W%|A9l.**PF2#&U2)1+z<&^+7VJ+Z9lTGN8.5clOF</L(+M))++Bpd|kVcK(_^6B;PPRJcyVHR5z(M+B>RFqy|<Od6B2q^85UFzy>yFzl>+4cf|/CMCfDcY+c5+BJ9@KUDB4++ZSGc;LZN<CRYM*-;y4(9(VS^#l2";
		System.out.println(c.cipherText);
		c.name = "test cipher, random scramble of Z340";
		c.zkPlaintext = "AVESSSHEHOUTHTOREAREISNOLDERTHATSTHONTRIEDSHELIAMBRAISOLNAEORDEARSWHERSEDTNRENIWPSTLICPRRCASTICHDISOOTHANHAPPEISMARCAMENERSBOUFIVESERSTHEWHOASHEOFTHERENOTITSATESRRIFHRETHALLWAMOSSESSENCHOOLAITYMIOISNLYASCOUSUNCEPATHINCEALDHISTOOSSHRASLONTITATHELLDUNDORDINITSHEDANDSLEATHINATIMANDEDANCESANVSDETEVENMSNISHUNGTMEHASSURPNEHUALEDMTOWEDAINIORATCI";
		c.zkScore = 32241;
		process(c);
		System.out.println(c);
	}

	/** read all the plaintexts.  then look for all dictionary words that appear. */ 
	public static void findDictionaryWords() {
		WordFrequencies.init();
		StringBuffer plaintexts = TransformedCiphers
				.getPlaintexts("/Users/doranchak/projects/zodiac/sam-blake-combined-plaintexts-zk-and-az.txt");
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() < 4) continue;
			if (plaintexts.indexOf(word) > -1) {
				System.out.println(word.length() + "	" + WordFrequencies.percentile(word) + "	" + word);
			}
		}
	}
	
	/** compute fragment IOC for all ciphers.  path to text file of index of folders of ciphers is given. */   
	public static void processFragmentIOC(String path) {
		String tab = "	";
		List<String> list = FileUtil.loadFrom(path);
		int count = 0;
		
		// reference Z340 p1 and p19 for different lengths, for comparing fragment ioc values
		Map<Integer, double[]> refStats340p1 = new HashMap<Integer, double[]>();
		Map<Integer, double[]> refStats340p19 = new HashMap<Integer, double[]>();
		String p19 = Periods.rewrite3(Ciphers.Z340, 19);
		
		for (String f : list) {
			File folder = new File(f);
			File[] files = folder.listFiles();
			for (File file : files) {
				String fileName = file.getName();
				if (!fileName.endsWith(".txt")) continue;
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				StringBuffer cipher = new StringBuffer();
				for (String line : lines) {
					if (line == null || line.isEmpty()) continue;
					cipher.append(line);
				}
				if (cipher.length() == 0) {
					System.out.println("ERROR: can't find cipher in " + fileName);
					continue;
				}
				StringBuffer result = new StringBuffer();
				int L = cipher.length();
				double[] stats1 = refStats340p1.get(L);
				double[] stats19 = refStats340p1.get(L);
				if (stats1 == null) {
					stats1 = new double[4];
					stats19 = new double[4];
					refStats340p1.put(L, stats1);
					refStats340p19.put(L, stats19);
				}
				result.append(L).append(tab);
				double diffSum1 = 0;
				double diffSum19 = 0;
				for (int n=2; n<6; n++) {
					double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher.toString(), n, false, false);
					result.append(ioc).append(tab);
					if (stats1[n-2] == 0 || stats19[n-2] == 0) {
						stats1[n-2] = RepeatingFragmentsFaster.fragmentIOC(Ciphers.Z340.substring(0, L), n, false, false);
						stats19[n-2] = RepeatingFragmentsFaster.fragmentIOC(p19.substring(0, L), n, false, false);
					}
					double ioc1 = stats1[n-2]; // ref ioc for 340 of same length
					double diff1 = (ioc - ioc1) / ioc1;
					diffSum1+=diff1;
					double ioc19 = stats19[n-2]; // ref ioc for p19 340 of same length
					double diff19 = (ioc - ioc19) / ioc19;
					diffSum19+=diff19;
					result.append(ioc1).append(tab).append(diff1).append(tab);
					result.append(ioc19).append(tab).append(diff19).append(tab);
				}
				result.append(diffSum1).append(tab);
				result.append(diffSum19).append(tab);
				result.append(fileName);
				System.out.println(result);
				count++;
			}
		}
		System.out.println("TOTAL FILES PROCESSED: " + count);
	}
	/** compute rarity scores for all ciphers.  path to text file of index of folders of ciphers is given. */   
	public static void processRarityScores(String path) {
		String tab = "	";
		List<String> list = FileUtil.loadFrom(path);
		int count = 0;
		
		for (String f : list) {
			File folder = new File(f);
			File[] files = folder.listFiles();
			for (File file : files) {
				String fileName = file.getName();
				if (!fileName.endsWith(".txt")) continue;
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				StringBuffer cipher = new StringBuffer();
				for (String line : lines) {
					if (line == null || line.isEmpty()) continue;
					cipher.append(line);
				}
				if (cipher.length() == 0) {
					System.out.println("ERROR: can't find cipher in " + fileName);
					continue;
				}
				StringBuffer result = new StringBuffer();
				int L = cipher.length();
				double rarity = RepeatingFragmentsFaster.rarityScore(cipher.toString(), 6);
				result.append(rarity).append(tab).append(L).append(tab).append(fileName);
				System.out.println(result);
				count++;
			}
		}
		System.out.println("TOTAL FILES PROCESSED: " + count);
	}
	
	public static void findMatching() {
		String s = FileUtil.convert(FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/letters/all-letters-combined-including-mikado.txt"));
		//System.out.println(s);
//		findLargeMatchingSubstrings("/Users/doranchak/projects/zodiac/sam-blake-combined-plaintexts-zk-and-az.txt", s);
	}

	public static void main(String[] args) {
		//process();
//		testProcess2();
		// resume from z340_ngrams_22_00_00_col01_blocksize02_01_rllrabt.txt    17629_793_185_15502.txt
//		processAZDecryptResults("/Users/doranchak/projects/zodiac/azdecrypt/AZdecrypt 1.17/Output");
		//processUniques2();
//		makeForAZDecrypt();
//		findMatching();
//		findDictionaryWords();
//		processFragmentIOC("/Users/doranchak/projects/zodiac/sam blake ciphers/all-ciphers-folders.txt");
		processRarityScores("/Users/doranchak/projects/zodiac/sam blake ciphers/all-ciphers-folders.txt");
	}
}
