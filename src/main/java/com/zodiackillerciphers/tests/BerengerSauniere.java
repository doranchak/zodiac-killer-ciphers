package com.zodiackillerciphers.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.zodiackillerciphers.ciphers.algorithms.Vigenere2;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/** https://www.evernote.com/shard/s1/nl/12205879/04857bc5-6490-40c3-8527-f103709f189d/ */
public class BerengerSauniere {
	public static String[] cipher = {
		"YENSZNUMGLNYYRFVHENMZF",
		"P*SOT+PECHEUR+A+L'EMBZ",
		"VOUCHURE+DU+RHONE,SONZ",
		"UPOISSON+SUR+LE+GRIL+F",
		"LDEUX+FOIS+RETOURNA.UD",
		"RN+MALIN+SURVINT+ET+XH",
		"RXV+FOIS+LE+GOUTA+.CUZ",
		"TIT,IL+NE+LUI+RESTA+QV",
		"KUE+L'ARETE.+UN+ANGE+T",
		"NVEILLAIT+ET+EN+FIT+UQ",
		"YNPEIGNE+D'OR.B.S.CURH",
		"OVTSVKYRMSTIJPZCKPFXKA"		
	};
	public static String[] cipherInnerOnly = {
		"*SOT+PECHEUR+A+L'EMB",
		"OUCHURE+DU+RHONE,SON",
		"POISSON+SUR+LE+GRIL+",
		"DEUX+FOIS+RETOURNA.U",
		"N+MALIN+SURVINT+ET+X",
		"XV+FOIS+LE+GOUTA+.CU",
		"IT,IL+NE+LUI+RESTA+Q",
		"UE+L'ARETE.+UN+ANGE+",
		"VEILLAIT+ET+EN+FIT+U",
		"NPEIGNE+D'OR.B.S.CUR"
	};
	// outer ring, read clockwise
	public static String cipherOuterOnly = "YENSZNUMGLNYYRFVHENMZFZZFDHZVTQHAKXFPKCZPJITSMRYKVSTVOYNKTRRLUVP";

	/** note: lacks the letter W */
	public static String[] vig = {
		"ABCDEFGHIJKLMNOPQRSTUVXYZ",
		"BCDEFGHIJKLMNOPQRSTUVXYZA",
		"CDEFGHIJKLMNOPQRSTUVXYZAB",
		"DEFGHIJKLMNOPQRSTUVXYZABC",
		"EFGHIJKLMNOPQRSTUVXYZABCD",
		"FGHIJKLMNOPQRSTUVXYZABCDE",
		"GHIJKLMNOPQRSTUVXYZABCDEF",
		"HIJKLMNOPQRSTUVXYZABCDEFG",
		"IJKLMNOPQRSTUVXYZABCDEFGH",
		"JKLMNOPQRSTUVXYZABCDEFGHI",
		"KLMNOPQRSTUVXYZABCDEFGHIJ",
		"LMNOPQRSTUVXYZABCDEFGHIJK",
		"MNOPQRSTUVXYZABCDEFGHIJKL",
		"NOPQRSTUVXYZABCDEFGHIJKLM",
		"OPQRSTUVXYZABCDEFGHIJKLMN",
		"PQRSTUVXYZABCDEFGHIJKLMNO",
		"QRSTUVXYZABCDEFGHIJKLMNOP",
		"RSTUVXYZABCDEFGHIJKLMNOPQ",
		"STUVXYZABCDEFGHIJKLMNOPQR",
		"TUVXYZABCDEFGHIJKLMNOPQRS",
		"UVXYZABCDEFGHIJKLMNOPQRST",
		"VXYZABCDEFGHIJKLMNOPQRSTU",
		"XYZABCDEFGHIJKLMNOPQRSTUV",
		"YZABCDEFGHIJKLMNOPQRSTUVX",
		"ZABCDEFGHIJKLMNOPQRSTUVXY"	
	};
	
	public static Map<Character, Set<String>> pairs;
	static {
		pairs = new HashMap<Character, Set<String>>();
		String alpha = vig[0];
		for (int a=0; a<alpha.length(); a++) {
			for (int b=0; b<alpha.length(); b++) {
				char c1 = alpha.charAt(a);
				char c2 = alpha.charAt(b);
				char key = vig(c1, c2);
				Set<String> val = pairs.get(key);
				if (val == null) val= new HashSet<String>();
				val.add(""+c1+""+c2);
				pairs.put(key, val);
			}
			
		}
		System.out.println(pairs);
	}
	

	static String lettersRingStr = "YENSZNUMGLNYYRFVHENMZFZZFDHZVTQHAKXFPKCZPJITSMRYKVSTVOYNKTRRLUVP";
	static String lettersRingStrRev = "PVULRRTKNYOVTSVKYRMSTIJPZCKPFXKAHQTVZHDFZZFZMNEHVFRYYNLGMUNZSNEY";
//	static String lettersRingStr = "AYYKYNXRKFFTPVRKHRCELZNUPMVJZPIFYTZESZNMFSRDZYHNKZUVVMSTGTQLVHNO";
	//static String WRONG lettersPerfStr = "PCLURDURHNOPIOSLIEFISRETURMALURIETVFOEGTATLELUIESUAETUGEATETEGNS";
	static String lettersPerfStr = "PCLURDURHNOPIOSLIEFISRETURMALURIETVFOEOACTLELUIESUAETUGEATETEGNS";
	static String lettersPerfStrRev = "SNGETETAEGUTEAUSEIULELTCAOEOFVTEIRULAMRUTERSIFEILSOIPONHRUDRULCP";
//	static String lettersRingStr = "YYKFTPRKRCNPVZPIFYTZESZNMFSRZYHNKZUVVMSTTLVHNO";
//	static String lettersPerfStr = "URPOLIISREURLURIETFOETATLELUIESUAETUGEATETEGNS";
//	static String lettersRingStr = "YRPYZFRZYKZN";
//	static String lettersPerfStr = "ELUEUEUETETE";
	
	

	
	static Set<Character> lettersRing;
	static {
		lettersRing = new HashSet<Character>();
		for (int i=0; i<lettersRingStr.length(); i++) lettersRing.add(lettersRingStr.charAt(i));
	}
	static Set<Character> lettersPerf;
	static {
		lettersPerf = new HashSet<Character>();
		for (int i=0; i<lettersPerfStr.length(); i++) lettersPerf.add(lettersPerfStr.charAt(i));
	}
	
	/** return the string represented by perforating the folder cipher as per Christer Boke.
	 * note that the cipher should not include the outer "ring" of cipher text
	 * returns array of {resulting string, number of perforations}
	 *  
	 **/
	
	public static String[] perforate(String[] cipher) {
		
		int H = cipher.length;
		int W = cipher[0].length();
		
		int perfs = 0;
		
		boolean[][] selected = new boolean[H][W];
		for (int row=0; row<selected.length; row++) {
			selected[row] = new boolean[W];
		}
		
		for (int row=0; row<H/2; row++) {
			for (int col=0; col<W/2; col++) {
				int r1 = row;
				int c1 = col;
				
				int r2 = row;
				int c2 = W-col-1;
				
				int r3 = H-row-1;
				int c3 = W-col-1;
				
				int r4 = H-row-1;
				int c4 = col;
				
				char ch1 = cipher[r1].charAt(c1);
				char ch2 = cipher[r2].charAt(c2);
				char ch3 = cipher[r3].charAt(c3);
				char ch4 = cipher[r4].charAt(c4);
				
				//String cc = "" + ch1 + ch2 + ch3 + ch4;
				//System.out.println("row " + row + " col + " + col + " " + cc);
				//System.out.println(r2 + " " + c2);
				
				boolean cross = ch1 == '+' || ch2 == '+' || ch3 == '+' || ch4 == '+';
				if (!cross) continue;
				perfs++;

				if (ch1 > 64 && ch1 < 91) {
					selected[r1][c1] = true;
				}
				if (ch2 > 64 && ch2 < 91) {
					selected[r2][c2] = true;
				}
				if (ch3 > 64 && ch3 < 91) {
					selected[r3][c3] = true;
				}
				if (ch4 > 64 && ch4 < 91) {
					selected[r4][c4] = true;
				}
				
			}
		}
		String result = "";
		for (int row=0; row<selected.length; row++) {
			for (int col=0; col<selected[row].length; col++) {
				if (selected[row][col]) {
					char ch = cipher[row].charAt(col);
					if (ch > 64 && ch < 91) 
						result += cipher[row].charAt(col);
				}
			}
		}
		return new String[] {result, ""+perfs};
	}
	/** convert ascii to int in range [0,25] */
	public static int charToInt(char ch) {
		if (ch < 87)
			return ch-65;
		return ch-66;
	}
	public static char vig(char c1, char c2) {
		int row = charToInt(c1);
		int col = charToInt(c2);
		return vig[row].charAt(col);
	}
	
	public static void test() {
		System.out.println(Arrays.toString(perforate(cipher)));
	}
	public static void shuffleTest() {
		for (int i=0; i<1000000; i++) {
			
		}
	}
	/**
	 * 
	 * @param tetx the text to search for
	 * @param index the current letter of the text we are searching for 
	 * @param a index within the ring
	 * @param b index within the perforation-generated letters
	 * @param pairs current set of pairs of letters that produces the partially found text so far
	 */
	public static void findText(String text, int index, Set<Character> setRing, Set<Character> setPerf, String pairsOutput, int[] stats) {
		stats[0]++;
		stats[1] = Math.max(stats[1], index);
		if (stats[0] % 1000000 == 0) System.out.println("Iterations: " + stats[0] + ", max match " + (stats[1]+1));
		if (index == text.length()) { // found entire text
			System.out.println(WordFrequencies.freq(text) + " Found " + text);
			dump(pairsOutput);
			return;
		}
		//System.out.println(index);
		// if one of the letter sets is empty, then we can no longer form pairs.
		if (setRing.isEmpty()) return;
		if (setPerf.isEmpty()) return;

		// let P = distinct pairs of letters {x,y} that can produce the current letter via the vigenere table.
		Set<String> P = pairs.get(text.charAt(index));
		// if P is empty, can't proceed.
		if (P == null || P.isEmpty()) return;
		// for each {x,y}, remove from stacks and recurse
		String oldPairs = pairsOutput;
		for (String xy : P) {
			
			char x = xy.charAt(0);
			char y = xy.charAt(1);
			boolean xb = setRing.contains(x); 
			boolean yb = setPerf.contains(y); 
			// pairs map should account for equivalence {x,y} <==> {y,x}
			
			if (!xb || !yb) continue; // {x,y} must exist between the sets of letters
			
			if (xb) setRing.remove(x);
			if (yb) setPerf.remove(y);
						
			findText(text, index+1, setRing, setPerf, oldPairs + xy + " ", stats);
			// after recurse, restore pairs and letter stacks

			if (xb) setRing.add(x);
			if (yb) setPerf.add(y);
		}
				
	}
	
	static String remove(String str, char ch) {
		String result = str.substring(0, str.indexOf(ch));
		result += str.substring(str.indexOf(ch)+1);
		//System.out.println("remove " + str + " " + ch + " = " + result);
		return result;
	}
	static void dump(String[] str) {
		for (String s : str) System.out.println(s);
	}
	static void dump(String pairsOutput) {
		String[] split = pairsOutput.split(" ");
		
		String nr = "";
		String np = "";
		
		String r = lettersRingStr;
		String p = lettersPerfStr;
		for (String s : split) {
			if (s.length() != 2) continue;
			r = remove(r, s.charAt(0));
			nr += s.charAt(0);
			p = remove(p, s.charAt(1));
			np += s.charAt(1);
		}
		
		System.out.println(pairsOutput);
		System.out.println(nr+r);
		System.out.println(np+p);
	}
	
	
	public static void testVig() {
		System.out.println(vig('A','B'));
		System.out.println(vig('P','D'));
		System.out.println(vig('Q','N'));
	}
	
	public static void testFindText(String text) {
		//System.out.println(text);
		findText(text, 0, lettersRing, lettersPerf, "", new int[2]);
	}
	
	/** rotate the outer ring.  mark coinciding letters in the same columns/rows. */
	public static void testRingRotations() {
		
	}
	
	public static String[] replaceRing(String[] cipher, String ring) {
		char[][] result = new char[cipher.length][cipher[0].length()];
		for (int row=0; row<cipher.length; row++) {
			for (int col=0; col<cipher[row].length(); col++) {
				result[row][col] = cipher[row].charAt(col);
			}
		}
		
		int i = 0;
		for (int col=0; col<cipher[0].length(); col++){
			result[0][col] = ring.charAt(i++);
		}
		for (int row=1; row<cipher.length; row++){
			result[row][cipher[row].length()-1] = ring.charAt(i++);
		}
		for (int col=cipher[0].length()-2; col>=0; col--){
			result[cipher.length-1][col] = ring.charAt(i++);
		}
		for (int row=cipher.length-2; row>0; row--){
			result[row][0] = ring.charAt(i++);
		}
		
		i=0;
		String[] arr = new String[result.length];
		for (char[] row : result) {
			arr[i++] = new String(row);
		}
		return arr;
			
	}
	
	/** mark coinciding letters in the same columns/rows. */
	public static IntersectionBean markIntersections(String[] cipher) {
		String[] result = new String[cipher.length];
		boolean[][] marked = new boolean[cipher.length][cipher[0].length()];
		String letters = "";
		int count = 0;
		for (int row=0; row<cipher.length; row++) {
			String line = "";
			for (int col=0; col<cipher[row].length(); col++) {
				if (row == 0 || row == cipher.length - 1 || col == 0
						|| col == cipher[row].length() - 1) {
					line += cipher[row].charAt(col);
					marked[row][col] = true;
				}
				else {
					if (intersects(cipher, row, col)) {
						line += "" + cipher[row].charAt(col);
						letters += cipher[row].charAt(col);
						count++;
						marked[row][col] = true;
					} else {
						line += cipher[row].charAt(col);
						/*char c = cipher[row].charAt(col);
						if (isAlpha(c)) line += "" + c;
						else line += "" + (char) (32 + c);*/ 
					}
				}
			}
			result[row] = line;
		}
		IntersectionBean bean = new IntersectionBean(result, count, marked, letters, ZKDecrypto.calcscore(new StringBuffer(letters)));
		return bean;
	}
	public static String shift(String str, int amount) {
		if (amount < 0) throw new RuntimeException("cannot be negative");
		String result = "";
		for (int i=0; i<str.length(); i++) {
			result += (str.charAt((i+amount)%str.length()));
		}
		return result;
	}
	public static boolean intersects(String[] cipher, int row, int col) {
		if (row == 0 || row == cipher.length - 1 || col == 0
				|| col == cipher[row].length() - 1) return false;
		char ch = cipher[row].charAt(col);
		if (ch == cipher[row].charAt(0)) return true;
		if (ch == cipher[row].charAt(cipher[row].length()-1)) return true;
		if (ch == cipher[0].charAt(col)) return true;
		if (ch == cipher[cipher.length-1].charAt(col)) return true;
		return false;
	}
	public static boolean isAlpha(char ch) {
		//System.out.println(ch + " " + (ch > 64 && ch < 91));
		return (ch > 64 && ch < 91);

	}

	public static void testRingShiftIntersections() {
		for (int shift=0; shift<64; shift++) {
			System.out.println("Forward, shift " + shift);
			String str = shift(lettersRingStr, shift);
			//System.out.println(str);
			String[] c = replaceRing(cipher, str);
			System.out.println(markIntersections(c).toHtml());
			//System.out.println(shift(lettersRingStrRev, shift));
		}
		for (int shift=0; shift<64; shift++) {
			System.out.println("Reversed, shift " + shift);
			String str = shift(lettersRingStrRev, shift);
			//System.out.println(str);
			String[] c = replaceRing(cipher, str);
			System.out.println(markIntersections(c).toHtml());
			//System.out.println(shift(lettersRingStrRev, shift));
		}
	}
	
	public static void bruteForceSearchForChrister() {
		//String ring1 = "AYYKYNXRKFFTPVRKHRCELZNUPMVJZPIFYTZESZNMFSRDZYHNKZUVVMSTGTQLVHNO";
		//String ring2 = new StringBuilder(ring1).reverse().toString();
		//String[] rings = new String[] {lettersRingStr, lettersRingStrRev};
		//String[] rings = new String[] {"UBKPVKRJDIKUUOCSEBKJVCVVCAEVSQNEXHTCMHZVMGFQPJOUHSPQSLUKHQOOIRSM", "MSRIOOQHKULSQPSHUOJPQFGMVZHMCTHXENQSVEACVVCVJKBESCOUUKIDJRKVPKBU"};
		String[] rings = new String[] {"YENSZNUMGLNYYRFVHENMZFZZFDHZVTQHAKXFPKCZPJITSMRYKVSTVOYNKTRRLUVP", "PVULRRTKNYOVTSVKYRMSTIJPZCKPFXKAHQTVZHDFZZFZMNEHVFRYYNLGMUNZSNEY"};
		//String[] inners = new String[] {lettersPerfStr, lettersPerfStrRev};
		//String[] inners = new String[] {lettersPerfStr};
		String[] inners = new String[] {"STEHEURAMBHUEOESNERUXOONANNNXILGTIINRTQLRNNVILINITUNPEIEDORBCURE"};
		String tab = "	";
		/*Vigenere2[] vigs = new Vigenere2[] {new Vigenere2(Vigenere2.ALPHA, 1, "normal 1"), new Vigenere2(Vigenere2.ALPHA, -1, "normal -1"),
				new Vigenere2(Vigenere2.ALPHA_BERENGER, 1, "omitW 1"), new Vigenere2(Vigenere2.ALPHA_BERENGER, -1, "omitW -1")};*/
		Vigenere2[] vigs = new Vigenere2[] { new Vigenere2(
				Vigenere2.ALPHA_BERENGER, 1, "omitW 1") };
		
		for (Vigenere2 vig : vigs) vig.dump();

		for (String ring : rings) {
			for (String inner : inners) {
				for (int shiftRing = 0; shiftRing<ring.length(); shiftRing++) {
					String ringShifted = shift(ring, shiftRing);
					for (int shiftInner = 0; shiftInner<inner.length(); shiftInner++) {
						String innerShifted = shift(inner, shiftInner);
						for (Vigenere2 vig : vigs) {
							String decoded = "";
							for (int i=0; i<ringShifted.length(); i++) {
								char c1 = ringShifted.charAt(i);
								char c2 = innerShifted.charAt(i);
								decoded += "" + vig.decode(c1, c2);
							}
							//String[] plaintexts = new String[] {decoded, caesar(decoded, 1), caesar(decoded, -1)};
							String[] plaintexts = new String[] {decoded};
							for (int i=0; i<plaintexts.length; i++) {
								String plaintext = plaintexts[i];
								String type;
								if (i==0) type = "normal";
								else if (i==1) type = "ROT(1)";
								else type = "ROT(-1)";
								
								
								char maxLetter = 'A';
								int maxLetterCount = 0;
								Map<Character, Integer> counts = new HashMap<Character, Integer>();
								for (int j=0; j<plaintext.length(); j++) {
									char key = plaintext.charAt(j);
									Integer val = counts.get(key);
									if (val == null) val = 0;
									val++;
									if (val > maxLetterCount) {
										maxLetterCount = val;
										maxLetter = key;
									}
									counts.put(key, val);
								}
								
								String line = shiftRing + tab + ringShifted + tab + shiftInner + tab + innerShifted + tab;
								line += vig.name + tab + type + tab + plaintext + tab + ZKDecrypto.calcscore(plaintext) + tab + maxLetter + tab + maxLetterCount + tab + countUnique(decoded);
								System.out.println(line);
							}
							
						}
					}
					
				}
			}
		}
	}
	
	public static String caesar(String msg, int shift){
	    String s = "";
	    int len = msg.length();
	    if (shift < 0) shift += 26;
	    for(int x = 0; x < len; x++){
	        char c = (char)(msg.charAt(x) + shift);
	        if (c > 'Z')
	            s += (char)(msg.charAt(x) - (26-shift));
	        else
	            s += (char)(msg.charAt(x) + shift);
	    }
	    return s;
	}
	
	public static int countUnique(String line) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<line.length(); i++) set.add(line.charAt(i));
		return set.size();
		
	}
	
	public static void process() {
		List<String> list = FileUtil.loadFrom("/Users/doranchak/Downloads/feh");
		for (String line : list) {
			System.out.println(countUnique(line));
		}
		
	}
	
	/** count non-overlapping doubles */
	public static int doubles(String str) {
		int total = 0;
		for (int i=0; i<str.length()-1; i++) {
			char c1 = str.charAt(i);
			char c2 = str.charAt(i+1);
			if (c1 != c2) continue;
			total++;
			i++;
		}
		return total;
	}

	/** test outer ring for statistical significance of doubles */
	public static void testDoubles(int n) {
		String cipher = cipherOuterOnly;
		StatsWrapper stats = new StatsWrapper();
		stats.actual = doubles(cipher);
		for (int i=0; i<n; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(doubles(cipher));
		}
		System.out.println(stats.header());
		stats.output();
	}
	
	
	public static void main(String[] args) {
		//System.out.println(Arrays.toString(perforate(cipherInnerOnly)));
		//testVig();
		/*WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() > 3) testFindText(word.toUpperCase());
		}*/
		
		//System.out.println(markIntersections(cipher));
		//testRingShiftIntersections();
		//bruteForceSearchForChrister();
		//String test = "XGPUCDEPRQDSFELEOAAISROLEDNEEGTXRINEEACUETBPRRXETAITTISANNAPSLNX";
		//System.out.println(ZKDecrypto.calcscore(test) + " " + test);
		//process();
		//System.out.println(caesar("HEYBUBBA", -1));
		testDoubles(1000000);
	}
}
