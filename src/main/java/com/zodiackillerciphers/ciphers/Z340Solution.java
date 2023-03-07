package com.zodiackillerciphers.ciphers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.dictionary.WordFrequencies.WordBean;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.ngrams.RepeatingFragmentsFaster;

/** process Sam Blake ciphers' AZDecrypt results (this class used to be called SamBlake) */
public class Z340Solution {
	public static void process(String folder, boolean zodiac, int minPercentile) {
		WordFrequencies.init();
		File folderFile = new File(folder);
		File[] files = folderFile.listFiles();
		for (File file : files) {
			if (file.getName().endsWith("txt")) {
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				for (String line : lines) {
					if (line.startsWith("Score")) continue;
					if (line.startsWith("Repeats")) continue;
					if (line.length() == 0) continue;
					if (line.startsWith("z340")) continue;
					if (line.endsWith(".txt")) continue;
					//System.out.println(line);
					List<WordBean> list = zodiac ? WordFrequencies.findZodiacWordsFasterIn(line) : WordFrequencies.findAllWordsInOrder2(line, 4, minPercentile);
					score(file, list, line);
				}
			}
		}
	}
	/** compute ngram scores */
	public static void processNGrams(String path) {
		String tab = "	";
		List<String> list = FileUtil.loadFrom(path);
		String info = null;
		for (int i=0; i<list.size(); i++) {
			String line = list.get(i);
			if (line.startsWith("cipher_info")) {
				info = line.substring(line.indexOf("/")+1);
				continue;
			}
			if (line.length() == 0) continue;
			
			NGramsBean bean = new NGramsBean(2, line);
			int n2 = bean.numRepeats();
			bean = new NGramsBean(3, line);
			int n3 = bean.numRepeats();
			bean = new NGramsBean(4, line);
			int n4 = bean.numRepeats();

			double ioc = RepeatingFragmentsFaster.fragmentIOC(line, 4, false, false);			
			System.out.println(n2 + tab + n3 + tab + n4 + tab + ioc + tab + info);
		}
	}
	
	public static void score(File file, List<WordBean> list, String line) {
		String tab = "	";
		boolean[] matches = new boolean[line.length()];
		Map<Integer, Integer> countsByLength = new HashMap<Integer, Integer>();
		for (WordBean bean : list) {
			Integer key = bean.word.length();
			if (key > 10) key = 10;
			Integer val = countsByLength.get(key);
			if (val == null) val = 0;
			val++;
			countsByLength.put(key, val);
			for (int i=0; i<bean.word.length(); i++) 
				matches[bean.pos+i] = true;
		}
		long score1 = 0;
		for (int L=10; L>3; L--) {
			Integer count = countsByLength.get(L);
			if (count == null) count = 0;
			System.out.print(count + tab);
			score1 += Math.pow(count, L-3);
		}
		System.out.print(score1 + tab);
		double score2 = 0;
		String words = "";
		for (WordBean bean : list) {
			words += bean.word + " ";
			if (bean.frequency > 0) score2 += Math.log10(bean.frequency);
		}
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<line.length(); i++) {
			if (matches[i]) sb.append(line.charAt(i));
			else sb.append((""+line.charAt(i)).toLowerCase());
		}
		double score3 = score2 * score1;
		System.out.print(score2 + tab + score3 + tab + words + tab + sb + tab + file.getName());
		System.out.println();
		
	}
	
	/** go through all ciphers, compute length stats */
	public static void processCipherLengths() {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>(); 
		List<String> dirs = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/cipher collection/sam blake ciphers/all-ciphers-folders.txt");
		for (String dir : dirs) {
			System.out.println("Directory: " + dir);
			File dirFile = new File(dir);
			for (File file : dirFile.listFiles()) {
				if (file.getName().endsWith(".txt")) {
					StringBuffer sb = FileUtil.loadSBFrom(file, false);
					
					String path = file.getAbsolutePath();
					path = path.substring(path.indexOf("sam blake ciphers") + 18);
					int len = sb.length();
					System.out.println(len + "	" + path);
					Integer val = counts.get(len);
					if (val == null) {
						val = 0;
					}
					val++;
					counts.put(len, val);
					if (len > 340) {
						System.out.println("ERROR: MORE THAN 340 [" + sb + "]");
					}
					if (sb.indexOf(" ") > -1)
						System.out.println("ERROR: FOUND A SPACE [" + sb + "]");
				}
			}
		}
		System.out.println("COUNTS: " + counts);
	}
	/** produce batch file of all ciphers for azdecrypt */
	public static void processCiphersForAZ() {
		List<String> dirs = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/cipher collection/sam blake ciphers/all-ciphers-folders.txt");
		for (String dir : dirs) {
			File dirFile = new File(dir);
			for (File file : dirFile.listFiles()) {
				if (file.getName().endsWith(".txt")) {
					StringBuffer sb = FileUtil.loadSBFrom(file, false);
					
					String path = file.getAbsolutePath();
					path = path.substring(path.indexOf("sam blake ciphers") + 18);
					int len = sb.length();
					System.out.println("cipher_information=length_" + len + "_" + path);
					System.out.println(sb);
					System.out.println();
					
				}
			}
		}
	}
	/** go through all cipher variations, and look for z13-like patterns (same pattern of repeating symbols, using different symbols) */
	public static void processCiphersForZ13() {
		int count = 0;
		List<String> dirs = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/cipher collection/sam blake ciphers/all-ciphers-folders.txt");
		for (String dir : dirs) {
			File dirFile = new File(dir);
			for (File file : dirFile.listFiles()) {
				if (file.getName().endsWith(".txt")) {
					count++;
					if (count % 100000 == 0) System.out.println(count+"...");
					StringBuffer sb = FileUtil.loadSBFrom(file, false);
					if (z13PatternMatch(sb)) {
						String path = file.getAbsolutePath();
						path = path.substring(path.indexOf("sam blake ciphers") + 18);
						int len = sb.length();
						System.out.println("cipher_information=length_" + len + "_" + path);
						System.out.println(sb);
						System.out.println();
					}
				}
			}
		}
		System.out.println("Total ciphers: " + count);
	}
	
	public static boolean z13PatternMatch(StringBuffer sb) {
		for (int i=0; i<sb.length()-12; i++) {
			String sub = sb.substring(i,i+13);
			if (isZ13PatternMatch(sub)) return true;
		}
		return false;
	}
	// 0=11, 2=10, 4=6=8, 7=12
	// does not enforce uniqueness of remaining unmatching symbols
	public static boolean isZ13PatternMatch(String sub) {
		if (sub.charAt(0) != sub.charAt(11)) return false;
		if (sub.charAt(2) != sub.charAt(10)) return false;
		if (sub.charAt(4) != sub.charAt(6) || sub.charAt(6) != sub.charAt(8)) return false;
		if (sub.charAt(7) != sub.charAt(12)) return false;
		System.out.println(sub);
		return true;
	}

	/** go through AZdecrypt output directory, and look for interesting cribs */
	public static void processAZResultsForInterestingCribs(String folderPath) {
		String[] cribs = new String[] { "HAVINGLOTS", "TRYINGTOCATCH", "WASNTMEON", "THETVSHOW", "WHICHBRING",
				"GASCHAMBER", "WILLSENDME", "PARADICE", "PARADISE", "SLAVES" };
		
		File folder = new File(folderPath);
		if (!folder.isDirectory()) {
			System.out.println(folder + " is not a directory.");
			return;
		}
		File[] files = folder.listFiles();
		for(File file : files) {
			if (file.isDirectory())
				processAZResultsForInterestingCribs(file.getAbsolutePath());
			else if (file.getName().endsWith(".txt")) {
				StringBuffer sb = new StringBuffer();
				StringBuffer nospaces = new StringBuffer();
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				String cipherFile = null;
				for (String line : lines) {
					if (line.startsWith("Score:")) continue;
					if (line.startsWith("Repeats:")) continue;
					if (line.startsWith("PC-cycles:")) continue;
					if (line.contains(".txt")) {
						cipherFile = line;
					} else if (line.length() > 0) {
						sb.append(line).append(System.getProperty("line.separator"));
						nospaces.append(line.replaceAll(" ", ""));
					}
				}
				String matches = "";
				for (String crib : cribs) {
					if (nospaces.indexOf(crib) > -1) {
						matches += crib + " ";
					}
				}
				if (matches.length() > 0) {
					System.out.println(file.getAbsolutePath());
					System.out.println(cipherFile);
					System.out.println("MATCHES: " + matches);
					System.out.println(sb);
				}

			}
		}
		
	}
	
	
	/** this is the real Z340 solution */
	public static void testZ340Solution() {
		
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		String transposedCipher = z340Transposition();
		String part1 =  transposedCipher.substring(0, 9*17);
		String part2 =  transposedCipher.substring(9*17, 9*17+9*17);
		System.out.println(new NGramsBean(2, transposedCipher));
		System.out.println(new NGramsBean(2, part1));
		System.out.println(new NGramsBean(2, part2));
		System.out.println(new NGramsBean(3, transposedCipher));
		System.out.println(new NGramsBean(3, part1));
		System.out.println(new NGramsBean(3, part2));
		System.out.println(Ciphers.decode(Ciphers.Z340, z340SolutionKey()));
		
		String ptFirst9 = "IRONCAOOIIERGRTMLECHETTATNWNNIAABWEITEOHSRTWTWGTUISFCCLOAPAOYCAHHOAMBNOHULPLEVFIHSEIRCPOOFAASALYIFNMNTTVHAUTTMSEYTONUGETTMSBPTAHBENUHUGNLIOHEHROMFEEIDNEA";
		String ptLast = "SUASOHOSHACLIFEISSHORVLRENNECEROAAMIEAOSEAVREONHSEFUNNFITHEEVFEETTPOATFEOBVMEENEOELHHIEYRATENYRNOSRVSHEENYUEATACONBOUTMOERHGRNYIHFAWEEWGHHWWYUWEIAFIRRTWCEFILWILLEBNAEASYENONIECIFURAPDEATH";
		System.out.println(NGramsCSRA.zkscore(new StringBuffer(ptFirst9), false, false, "EN", false));
		int[][] m = Periods.transpositionMatrix(9, 17, 1, 2);
		String pt = Periods.matrixApply(ptFirst9, m);
		System.out.println(pt);
		System.out.println("First 9 zkscore: " + NGramsCSRA.zkscore(new StringBuffer(pt), false, false, "EN", false) + " " + pt);
		
		for (int rowOffset = 0; rowOffset < 5; rowOffset++) {
			for (int colOffset = 0; colOffset < 5; colOffset++) {
				if (rowOffset == 0 && colOffset == 0) continue;
				m = Periods.transpositionMatrix(11, 17, rowOffset, colOffset);
				if ( m==null) System.out.println("invalid for " + rowOffset + " " + colOffset);
				else {
					pt = Periods.matrixApply(ptLast, m);
					//System.out.println(NGramsCSRA.zkscore(new StringBuffer(pt), false, false, "EN", false) + " " + rowOffset + " " + colOffset + " " + pt);
				}
			}
		}
		for (int N=4; N <= 11; N++) {
			for (int i=0; i<ptLast.length(); i++) {
				if (i+N*17 > ptLast.length()) continue;
				String justN = ptLast.substring(i, i+N*17);
				for (int rowOffset = -6; rowOffset < 6; rowOffset++) {
					for (int colOffset = -6; colOffset < 6; colOffset++) {
						m = Periods.transpositionMatrix(N, 17, rowOffset, colOffset);
						if ( m==null) System.out.println("invalid for " + rowOffset + " " + colOffset);
						else {
//							System.out.println("=====");
//							for (int[] r : m) System.out.println(Arrays.toString(r));
							pt = Periods.matrixApply(justN, m);
							System.out.println(N + " " + NGramsCSRA.zkscore(new StringBuffer(pt), false, false, "EN", false) + " " + rowOffset + " " + colOffset + " " + i + " " + pt);
						}
					}
				}
			}
		}
		
	}
	/** best version of the Z340 key found so far */
	public static Map<Character, Character> z340SolutionKey() {
		Map<Character, Character> key = new HashMap<Character, Character>();
		key.put('#','T');
		key.put('%','T');
		key.put('&','S');
		key.put('(','T');
		key.put(')','W');
		key.put('*','A');
		key.put('+','H');
		key.put('-','S');
		key.put('.','N');
		key.put('/','U');
		key.put('1','R');
		key.put('2','M');
		key.put('3','Y');
		key.put('4','E');
		key.put('5','V');
		key.put('6','D');
		key.put('7','L');
		key.put('8','P');
		key.put('9','N');
		key.put(':','T');
		key.put(';','T');
		key.put('<','I');
		key.put('>','N');
		key.put('@','U');
		key.put('A','D');
		key.put('B','E');
		key.put('C','Y');
		key.put('D','N');
		key.put('E','R');
		key.put('F','F');
		key.put('G','T');
		key.put('H','I');
		key.put('J','S');
		key.put('K','A');
		key.put('L','G');
		key.put('M','O');
		key.put('N','E');
		key.put('O','A');
		key.put('P','I');
		key.put('R','O');
		key.put('S','D');
		key.put('T','R');
		key.put('U','S');
		key.put('V','O');
		key.put('W','W');
		key.put('X','R');
		key.put('Y','N');
		key.put('Z','R');
		key.put('^','O');
		key.put('_','B');
		key.put('b','E');
		key.put('c','E');
		key.put('d','L');
		key.put('f','B');
		key.put('j','P');
		key.put('k','I');
		key.put('l','A');
		key.put('p','C');
		key.put('q','U');
		key.put('t','L');
		key.put('y','I');
		key.put('z','A');
		key.put('|','E');
		return key;
	}
	/** plaintext solution of the unmanipulated z340 */
	public static String z340Solution() {
		return Ciphers.decode(Ciphers.Z340, z340SolutionKey());
	}

	/**
	 * just the last 11 lines of solution of unmanipulated z340. the section we
	 * still haven't gotten the correct untransposition for.
	 */
	public static String z340SolutionLast11() {
		return extractRows(Ciphers.decode(Ciphers.Z340, z340SolutionKey()), 17, 9,19);
	}
	/**
	 * just lines 10-18 of solution of unmanipulated z340. the section we
	 * still haven't gotten the correct untransposition for.
	 */
	public static String z340SolutionNext9() {
		return extractRows(Ciphers.decode(Ciphers.Z340, z340SolutionKey()), 17, 9,17);
	}
	/** untransposed plaintext solution of z340 */
	public static String z340SolutionUntransposed() {
		return Periods.matrixApply(Ciphers.decode(Ciphers.Z340, z340SolutionKey()),
				Periods.transpositionMatrixZ340Solution());
	}
	
	/** best transposition of the z340 found so far */
	public static String z340Transposition() {
		//return "H+M8|CV@KEB+*5k.LdR(UVFFz9<>#Z3P>L(MpOGp+2|G+l%WO&D#2b^D(+4(5J+VW)+kp+fZPYLR/8KjRk.#K_Rq#2|<z29^%OF1*HSMF;+BLKJp+l2_cTfBpzOUNyG)y7t-cYA2N:^j*Xz6dpclddG+4-RR+4>f|pz/JNbVM)+l5||.UqL+Ut*5cZGR)VE5FV52cW+|TB4-|TC^D4ct+c+zJYM(+y.LW+B.;+B31cOp+8lXz6Ppb&RG1BCO7TBzF*K<S<MF6N:(+HFK29^4OFTBO<Sf9pl/yUcy5C^W(-+l#2E.B)|kW7BYB-cFd<t_O*C>DNkzOAK+MHpSZ8|;";
		String z340 = Ciphers.Z340;
		return Periods.matrixApply(z340, Periods.transpositionMatrixZ340Solution());
	}
	
	/** extract given rows (inclusive range.  rows start at 0.) */
	public static String extractRows(String text, int width, int rowStart, int rowEnd) {
		return text.substring(width*rowStart, width*(rowEnd+1));
	}
	
	
	/** consider all rectangular selections of the plaintext for the last 11 lines.  find 
	 * the one with the best zkscore when transposing with offsets (1,2) since those seem to be dominating. 
	 */
	public static void findBestWindow() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		String[] grid = new String[] {
				"SUASOHOSHACLIFEIS",
				"SHORVLRENNECEROAA",
				"MIEAOSEAVREONHSEF",
				"UNNFITHEEVFEETTPO",
				"ATFEOBVMEENEOELHH",
				"IEYRATENYRNOSRVSH",
				"EENYUEATACONBOUTM",
				"OERHGRNYIHFAWEEWG",
				"HHWWYUWEIAFIRRTWC",
				"EFILWILLEBNAEASYE",
				"NONIECIFURAPDEATH"		
		};
		
		int H = grid.length;
		int W = grid[0].length();
		
		for (int row1=0; row1<H; row1++) {
			for (int col1=0; col1<W; col1++) {
				for (int row2=row1; row2<H; row2++) {
					for (int col2=col1; col2<W; col2++) {
						int windowHeight = row2-row1+1;
						int windowWidth = col2-col1+1;
						int size = windowHeight*windowWidth;
						if (size < 10) continue;
//						System.out.println(size + " " + windowHeight + " " + windowWidth);
						StringBuffer sb = new StringBuffer();
						for (int r=row1; r<=row2; r++) {
							sb.append(grid[r].substring(col1,col2+1));
						}
						int[][] m = Periods.transpositionMatrix(windowHeight, windowWidth, 1, 2);
						if (m == null) continue;
						String pt = Periods.matrixApply(sb.toString(), m);
//						System.out.println(pt);
						System.out.println(
								size + " " + NGramsCSRA.zkscore(new StringBuffer(pt), false, false, "EN", false) + " ("
										+ row1 + "," + col1 + ") (" + row2 + "," + col2 + ") " + pt);
						
					}
				}
				
			}
		}
	}

	/**
	 * find best split of the last 11 lines, where each section is enumerated
	 * separately. leftover rows are simply appended.
	 * a variety of offsets are explored.
	 */
	public static void findBestSplit() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		String[] grid = toGrid(z340SolutionLast11(), 17);
		
		
		int H = grid.length;
		int W = grid[0].length();

		//StringBuffer pt = new StringBuffer();
		for (int r1start = 0; r1start < H; r1start++) {
			for (int r1end = r1start; r1end < H; r1end++) {
				System.out.println("==== STARTING (" + r1start + "," + r1end + ")");
				int height1 = r1end-r1start+1;
				StringBuffer text1 = new StringBuffer();
				for (int r=r1start; r<=r1end; r++) text1.append(grid[r]); 
				for (int offsetRow1 = -3; offsetRow1 < 4; offsetRow1++) {
					for (int offsetCol1 = -3; offsetCol1 < 4; offsetCol1++) {
						int[][] m1 = Periods.transpositionMatrix(height1, W, offsetRow1, offsetCol1);
						// then look at the 2nd selection of rows
						for (int r2start = r1end+1; r2start < H; r2start++) {
							for (int r2end = r2start; r2end < H; r2end++) {
								int height2 = r2end-r2start+1;
								StringBuffer text2 = new StringBuffer();
								for (int r=r2start; r<=r2end; r++) text2.append(grid[r]); 
								for (int offsetRow2 = -3; offsetRow2 < 4; offsetRow2++) {
									for (int offsetCol2 = -3; offsetCol2 < 4; offsetCol2++) {
										StringBuffer pt = new StringBuffer();
										if (m1 == null) {
											// then just add the rows
											pt.append(text1);
										} else {
											pt.append(Periods.matrixApply(text1.toString(), m1)); // apply the enumeration for just these rows
										}
										
										int[][] m2 = Periods.transpositionMatrix(height2, W, offsetRow2, offsetCol2);
										if (m2 == null) {
											// then just add the rows
											pt.append(text2);
										} else {
											pt.append(Periods.matrixApply(text2.toString(), m2)); // apply the enumeration for just these rows
										}
										String info = "["+r1start+","+r1end+"] [" + r2start+","+r2end+"]";
										info += " " + offsetRow1 + "," + offsetCol1 + " " + offsetRow2 + "," + offsetCol2;
										//System.out.print("   ");
										for (int r=0; r<H; r++) { // find excluded rows
											if (r >= r1start && r <= r1end) continue;
											if (r >= r2start && r <= r2end) continue;
											pt.append(grid[r]);
											//System.out.print(r + " ");
										}
										float zk = NGramsCSRA.zkscore(pt, false, false, "EN", false); 
										if (zk > 4800) {
											System.out.println(zk + " " + info + pt);
										}
									}
								}
							}
						}
					}
					
				}
			}
		}
		
//		
//		for (int row1=0; row1<H; row1++) {
//			for (int col1=0; col1<W; col1++) {
//				for (int row2=row1; row2<H; row2++) {
//					for (int col2=col1; col2<W; col2++) {
//						int windowHeight = row2-row1+1;
//						int windowWidth = col2-col1+1;
//						int size = windowHeight*windowWidth;
//						if (size < 10) continue;
////						System.out.println(size + " " + windowHeight + " " + windowWidth);
//						StringBuffer sb = new StringBuffer();
//						for (int r=row1; r<=row2; r++) {
//							sb.append(grid[r].substring(col1,col2+1));
//						}
//						int[][] m = Periods.transpositionMatrix(windowHeight, windowWidth, 1, 2);
//						if (m == null) continue;
//						String pt = Periods.matrixApply(sb.toString(), m);
////						System.out.println(pt);
//						System.out.println(
//								size + " " + NGramsCSRA.zkscore(new StringBuffer(pt), false, false, "EN", false) + " ("
//										+ row1 + "," + col1 + ") (" + row2 + "," + col2 + ") " + pt);
//						
//					}
//				}
//				
//			}
//		}
	}
	
	/** make a crib list for azdecrypt */
	public static void cribList() {
		String[] cribs = new String[] {
				"IHOPEYOUARE",
				"HAVINGLOTSOFFUN",
				"TRYINGTOCATCHME",
				"PARADICE",
				"THEGASCHAMBER",
				"THATWASNTMEONTHETVSHOW",
				"WHICHBRING",
				"POINTABOUTME",
				"ITWILLSENDME",
				"SLAVE",
				"SLAVES"
		};
		for (int i=1; i<=340; i++) {
			String indent = i < 10 ? "    " : i < 100 ? "   " : "  ";
			for (String crib : cribs)
				System.out.println(i + indent + crib);
		}
	}
	// smaller one
	public static void cribList2() {
		String[] cribs = new String[] {
				"HOPEYOUARE",
				"TRYINGTOCATCHME",
				"GASCHAMBER"
		};
		for (int i=1; i<=340; i++) {
			String indent = i < 10 ? "    " : i < 100 ? "   " : "  ";
			for (String crib : cribs)
				System.out.println(i + indent + crib);
		}
	}
	
	/** enumerate given text with the given offset values */
	public static String enumerate(String text, int H, int W, int rowOffset, int colOffset) {
		int[][] m = Periods.transpositionMatrix(H, W, rowOffset, colOffset);
		if (m == null) {
			System.out.println("Invalid enumeration for " + H + "x" + W + " - (" + rowOffset + ", " + colOffset + ")");
			return null;
		}
		return Periods.matrixApply(text, m);
	}
	public static String[] toGrid(String text, int W) {
		if (text.length() % W != 0) {
			throw new IllegalArgumentException("Invalid width "+ W + " for len " + text.length());
		}
		String[] grid = new String[text.length() / W];
		for (int row=0; row<grid.length; row++) {
			grid[row] = text.substring(row*W, (row+1)*W);
		}
		return grid;
	}
	
	/** measure zkscores for each part */
	public static void zkscores() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		float zk;
		String text = extractRows(z340SolutionUntransposed(), 17, 0, 8);

		System.out.println("FIRST 9 LINES");
		System.out.println(text);
		zk = NGramsCSRA.zkscore(new StringBuffer(text), false, false, "EN", false);
		System.out.println("zkscore: " + zk);
		System.out.println("zkscore per character: " + zk/text.length());
		
		System.out.println("NEXT 9 LINES");
		text = extractRows(z340SolutionUntransposed(), 17, 9, 17);
		System.out.println(text);
		zk = NGramsCSRA.zkscore(new StringBuffer(text), false, false, "EN", false);
		System.out.println("zkscore: " + zk);
		System.out.println("zkscore per character: " + zk/text.length());
		
		System.out.println("LAST 11 LINES");
		text = extractRows(z340SolutionUntransposed(), 17, 9, 19);
		System.out.println(text);
		zk = NGramsCSRA.zkscore(new StringBuffer(text), false, false, "EN", false);
		System.out.println("zkscore: " + zk);
		System.out.println("zkscore per character: " + zk/text.length());
		
	}
	
	/** correct line 15 with brute force, maximizing ngram score */
	public static void fixLine15() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		StringBuffer middle9 = new StringBuffer(
				"SOOHENBECAUSEEOOWHAVEENSUGHSLAVERTOWORVFOVMEWHEREESERYONEELHEHASNOTHINGWHENTHEYREACHPAYALICESOTREYALREAFAAIDIOFLETTHIFAMNOEAFREAIDBNCAUISEIVYOWTSHATMRNEW");
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		boolean go = true;
		float zkBest = NGramsCSRA.zkscore(middle9, false, false, "EN", false);
		while (go) {
			int bestAlpha = -1;
			int bestInsert = -1;
			float zkCurrentBest = zkBest;
			for (int i=0; i<alpha.length(); i++) {
				char a = alpha.charAt(i);
				StringBuffer sb = new StringBuffer(middle9);
				for (int j=5; j<sb.length(); j+=9) {
					char old = sb.charAt(j);
					sb.setCharAt(j, a);
					float zk = NGramsCSRA.zkscore(sb, false, false, "EN", false);
					if (zk > zkCurrentBest) {
						bestAlpha = i;
						bestInsert = j;
						zkCurrentBest = zk;
					}
					sb.setCharAt(j, old);
				}
			}
			if (bestAlpha > -1) {
				zkBest = zkCurrentBest;
				middle9.setCharAt(bestInsert, alpha.charAt(bestAlpha));
				System.out.println(zkBest + " " + bestInsert + " " + alpha.charAt(bestAlpha) + " " + middle9);
			} else 
				go = false;
		}
	}

	/** do the Z408 and Z340 keys have any identical mappings? */
	public static void keysIdenticalMappings() {
		Map<Character, Character> key408 = Ciphers.decoderMapFor(Ciphers.Z408, Ciphers.Z408_SOLUTION.toUpperCase());
		Map<Character, Character> key340 = z340SolutionKey();
	
		for (Character c : key408.keySet()) {
			Character p1 = key408.get(c);
			Character p2 = key340.get(c);
			if (p2 == null) continue;
			if (p1 == p2) 
				System.out.println(c + " " + p1);
		}
	}
	
	/** dump homophone sequences */
	public static void dumpSequences(Map<Character, Character> key, String cipher) {
		Map<Character, StringBuffer> sequences = new HashMap<Character, StringBuffer>();
		for (int i=0; i<cipher.length(); i++) {
			char ct = cipher.charAt(i);
			char pt = key.get(ct);
			//if (pt == 'O') pt = 'A';
			StringBuffer val = sequences.get(pt);
			if (val == null) {
				val = new StringBuffer();
				sequences.put(pt, val);
			}
			val.append(ct);
		}
		for (Character pt : sequences.keySet()) {
			System.out.println("<span class=\"plain\">" + pt + "</span><span class=\"cipher\">" + sequences.get(pt).toString().replaceAll("<", "&lt;") + "</span><br>");
		}
	}
	
	/** what is accuracy of given plaintext compared to the known z340 plaintext? */ 
	public static double accuracy(String pt) {
		String zpt = z340SolutionUntransposed();
		float hits = 0;
		for (int i=0; i<zpt.length() && i<pt.length(); i++) {
			if (zpt.charAt(i) == pt.charAt(i)) hits++;
		}
		hits /= zpt.length();
		return hits;
	}
	
	public static void dumpSequences() {
		System.out.println("<h1>Z340:</h1>");
		dumpSequences(z340SolutionKey(), Ciphers.Z340);
		System.out.println("<h1>Z340 UNTRANSPOSED:</h1>");
		dumpSequences(z340SolutionKey(), Ciphers.Z340T);
	}
	
	/** output keys (in order of occurrence) as html */
	public static void dumpKeys(String cipher, String plaintext) {
		Map<Character, String> p2c = new HashMap<Character, String>();
		for (int i = 0; i < cipher.length(); i++) {
			char c = cipher.charAt(i);
			char p = plaintext.charAt(i);
			String val = p2c.get(p);
			if (val == null) {
				val = "";
			}
			if (val.indexOf(c) == -1)
				val += c;
			p2c.put(p, val);
		}
		for (Character p : p2c.keySet()) {
			System.out.println("<span class=\"homophones\">");
			System.out.println("<span class=\"p\">" + p + "</span><span class=\"c\">" + encode(p2c.get(p)) + "</span>");
			System.out.println("</span>");
		}
	}
	static String encode(String str) {
		return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
	}

	public static void dumpKeys() {
		dumpKeys(Ciphers.Z408, Ciphers.Z408_SOLUTION.toUpperCase());
		dumpKeys(Ciphers.Z340, Ciphers.Z340_SOLUTION_TRANSPOSED);
	}
	
	/** dump plaintext resulting from applying known 340 key to jarl's and sam's transpositions */
	public static void dumpJarlSamTransposition() {
		String jarl = "H+M8|CV@KEB+*5k.LdR(UVFFz9<>#Z3P>L(MpOGp+2|G+l%WO&D#2b^D(+4(5J+VW)+kp+fZPYLR/8KjRk.#K_Rq#2|<z29^%OF1*HSMF;+BLKJp+l2_cTfBpzOUNyG)y7t-cYA2N:^j*Xz6dpclddG+4-RR+4>f|pz/JNbVM)+l5||.UqL+Ut*5cZGR)VE5FV52cW+|TB4-|TC^D4ct+c+zJYM(+y.LW+B.;+B31cOp+8lXz6Ppb&RG1BCO7TBzF*K<S<MF6N:(+HFK29^4OFTBO<Sf9pl/yUcy5C^W(-+l#2E.B)|DFHkNdpWk<S7ztZBO_8YAO|BK*;-+C>cM";
		System.out.println(jarl);
		System.out.println(Ciphers.decode(jarl, z340SolutionKey()));
		String sam = "H+M8|CV@KEB+*5k.LdR(UVFFz9<>#Z3P>L(MpOGp+2|G+l%WO&D#2b^D(+4(5J+VW)+kp+fZPYLR/8KjRk.#K_Rq#2|<z29^%OF1*HSMF;+BLKJp+l2_cTfBpzOUNyG)y7t-cYA2N:^j*Xz6dpclddG+4-RR+4>f|pz/JNbVM)+l5||.UqL+Ut*5cZGR)VE5FV52cW+|TB4-|TC^D4ct+c+zJYM(+y.LW+B.;+B31cOp+8lXz6Ppb&RG1BCO7TBzF*K<S<MF6N:(+HFK29^4OFTBO<Sf9pl/yUcy5C^W(-+l#2E.B)|kW7BYB-cFd<t_O*C>DNkzOAK+MHpSZ8|;";
		System.out.println(sam);
		System.out.println(Ciphers.decode(sam, z340SolutionKey()));
		
	}
	
	public static void main(String[] args) {
		//process("/Users/doranchak/Downloads/sam");
//		process("/Volumes/Smeggabytes/projects/zodiac/cipher collection/sam blake ciphers/part 3/all-results-combined", false, 85);
//		processNGrams("/Users/doranchak/projects/zodiac/azdecrypt/AZdecrypt 1.19/Ciphers/Batch/diagonals-batch.txt");
//		processCipherLengths();
//		processCiphersForAZ();
//		testZ340Solution();
//		cribList();
//		findBestWindow();
//		processAZResultsForInterestingCribs("/Users/doranchak/projects/zodiac/azdecrypt/AZdecrypt 1.19/Output");
//		for (int r1 = 0; r1 < 20; r1++) {
//			for (int r2=r1; r2< 20; r2++) {
//				System.out.println(r1 + " " + r2 + " " + extractRows(z340Transposition(), 17, r1, r2));
//			}
//		}
//		System.out.println(z340Transposition());
//		System.out.println(z340SolutionLast11());
//		System.out.println(z340SolutionNext9());
//		System.out.println(Arrays.toString(toGrid(z340SolutionNext9(), 17)));
//		System.out.println(z340Solution());
//		System.out.println(z340Transposition());
//		System.out.println(accuracy(z340Solution()));
//		dumpKeys();
		dumpJarlSamTransposition();
//		System.out.println(enumerate(z340SolutionNext9(), 9, 17, 1, 2));
//		findBestSplit();
//		zkscores();
//		fixLine15();
		//processCiphersForZ13();
		//keysIdenticalMappings();
	}
}
