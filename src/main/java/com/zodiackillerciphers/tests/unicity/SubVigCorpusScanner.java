package com.zodiackillerciphers.tests.unicity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.algorithms.Vigenere;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.lucene.Stats;

/** pick random plaintexts from a big corpus.  see if ciphertexts exist that can decode into two 
 * different plaintexts.
 */
public class SubVigCorpusScanner extends CorpusBase {
	/** pick random plaintexts of length N.  one is considered for substitution, the other for vigenere.
	 * see if a cipher text exists that decodes to one under sub, and the other under vig.
	 */
	public static void search(int N) {
		Random rand = new Random();
		List<String> listSub;
		List<String> listVig;
		StringBuffer noSpaceSub;
		StringBuffer noSpaceVig;
		while (true) {
//			listSub = randomNgrams(N); // for sub
			listSub = new ArrayList<String>();
			listSub.add("THEYHADEATENUPALLTHE");
//			listSub.add("RETAINS");
//			listSub.add("SOMEWHAT");
//			listVig = randomNgrams(N); // for vig
			listVig = new ArrayList<String>();
			listVig.add("INTOHYSTERICSCHAPTER"); 
			noSpaceSub = new StringBuffer();
			for (String s : listSub) noSpaceSub.append(s);
			noSpaceVig = new StringBuffer();
			for (String s : listVig) noSpaceVig.append(s);
			break;
//			if (noSpaceVig.charAt(3) == noSpaceVig.charAt(13)) break;
			
		}
		
		System.out.println(N + ", Lists: " + listSub + " " + listVig);
		
		int[] matches = nextMatches(noSpaceSub.toString());
		char[] key = new char[5];
		permute(key, 0, noSpaceVig, noSpaceSub, matches, listVig, listSub);
	}
	
	static boolean permute(char[] key, int index, StringBuffer vigPlaintext, StringBuffer subPlaintext, int[] matchesSub, List<String> listVig, List<String> listSub) {
		if (index == key.length) {
			// stop condition
			String keyStr = new String(key);
			String cipher = Vigenere.encrypt(vigPlaintext.toString().toLowerCase(), keyStr.toLowerCase()).toUpperCase();
			// for the vig cipher to decode as the sub plaintext, it must have matching letters in the same positions as matching pairs in the sub plaintext.
			boolean hit = true;
			for (int i=0; i<matchesSub.length; i++) {
				int next = matchesSub[i];
				if (next == -1) continue;
				if (cipher.charAt(i) != cipher.charAt(next)) {
					hit = false;
					break;
				}
			}
			hit = validSubstitution(cipher, subPlaintext);
//			if (keyStr.equals("GBSBJ")) {
//				System.out.println("smeg " + vigPlaintext.length() + "	" + vigPlaintext + "	" + keyStr + "	" + cipher + "	" + subPlaintext + "	" + listVig + "	" + listSub);
//				System.out.println(Arrays.toString(matchesSub));
//			}
			if (hit) {
				System.out.println("hit	" + vigPlaintext.length() + "	" + vigPlaintext + "	" + keyStr + "	" + cipher + "	" + subPlaintext + "	" + listVig + "	" + listSub);
			}
			return hit;
		}
		for (int i=0; i<alpha.length(); i++) {
			key[index] = alpha.charAt(i);
			if (permute(key, index + 1, vigPlaintext, subPlaintext, matchesSub, listVig, listSub))
				return true;
		}
		return false;
	}
	
	
	/** return a random list of word ngrams of total length N */ 
	public static List<String> randomNgrams(int N) {
		List<String> list = new ArrayList<String>();
		
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			for (int k=0; k<10; k++) {
				list.clear();
				int len = SubstitutionMutualEvolve.tokens.length;
				int total = 0;
				int start = rand.nextInt(len);
				for (int i=start; i<len; i++) {
					String token = SubstitutionMutualEvolve.tokens[i];
					list.add(token);
					total += token.length();
					if (total == N) return list;
					if (total > N) continue;
				}
			}
		}
	}
	
	public static boolean tooManyRepeats(List<String> list, int maxAllowed) {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		int max = 0;
		for (String str : list) {
			Integer val = counts.get(str);
			if (val == null) val = 0;
			val++;
			counts.put(str, val);
			max = Math.max(val, max);
			if (max > 2) return true;
		}
		return false;
	}
	public static int diff(char a, char b) {
		int i = alphaFull.indexOf(a);
		int j = alphaFull.indexOf(b);
		if (i<0) throw new RuntimeException("Invalid char " + a);
		if (j<0) throw new RuntimeException("Invalid char " + b);
		while (i<j) i+=26;
		return i-j;
	}
	public static int diff(StringBuffer q, int a, int b) {
		return diff(q.charAt(a), q.charAt(b));
	}
	
	/* return a random list of word ngrams that satisfies the given matches */
	public static List<String> randomNGrams(int N, int[] matches) {
		if (matches == null || matches.length != N) 
			throw new RuntimeException("Invalid matches array.");
		while (true) {
			List<String> list = randomNgrams(N);
			StringBuffer plain = flatten(list);
			boolean found = true;
			for (int i=0; i<matches.length; i++) {
				int next = matches[i];
				if (next == -1) continue;
				if (plain.charAt(i) != plain.charAt(next)) {
					found = false;
					break;
				}
			}
			if (found) return list;
		}
	}

	/* return a random list of word ngrams that satisfies the given matches, AND differences constraints.
	 * Differences constraints take the form of a list.
	 * Each list element is a 4-tuple, of the values for a, b, c and d for the constraint: qa-qb = qc-qd,
	 * where q is indexed starting at zero.
	 **/
	public static List<String> randomNGrams(int N, int[] matches, List<int[]> differences) {
		if (matches == null || matches.length != N) 
			throw new RuntimeException("Invalid matches array.");
		
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			// get all ngrams of length N
			List<List<String>> list = ngrams(N);
			for (int i=list.size()-1; i>=0; i--) {
				List<String> ngrams = list.get(i);
				StringBuffer sb = flatten(ngrams);
				
				// enforce the matches
				boolean found = true;
				for (int k=0; k<matches.length; k++) {
					int next = matches[k];
					if (next == -1) continue;
					if (sb.charAt(k) != sb.charAt(next)) {
						found = false;
						break;
					}
				}
				if (!found) list.remove(i);
				else {
					// enforce the differences
					found = true;
					for (int[] diffs : differences) {
						boolean neg = diffs[2] < 0 && diffs[3] < 0;
						char a = sb.charAt(diffs[0]);
						char b = sb.charAt(diffs[1]);
						char c = sb.charAt(Math.abs(diffs[2]));
						char d = sb.charAt(Math.abs(diffs[3]));
						int diff1 = diff(a,b);
						int diff2 = diff(c,d);
						if (neg) diff2 = -diff2;
						if (diff2 < 0) diff2 += 26; // diff function is always positive
						if (diff1 != diff2) {
							found = false;
							break;
						}
					}
					if (!found) list.remove(i);
					else if (tooManyRepeats(ngrams, 2)) list.remove(i);
					else {
						// enforce special condition: d(q19,q2) = (26+d(q9,q6)-d(q17,q16)) mod 26
						int d1 = diff(sb, 18, 1);
						int d2 = diff(sb, 8, 5);
						int d3 = diff(sb, 16, 15);
						int d4; 
						int d5;
						if (d1 != (26+d2-d3) % 26) {
							list.remove(i);
						} else {
							// enforce special condition d5 = (26+d2-d1) mod 26 = (26+d3-d1) mod 26 = (26+d4-d1) mod 26
							/*let d1=d(q17,q16)
									let d2=d(q10,q1)
									let d3=d(q15,q6)
									let d4=d(q20,q11)
									let d5=q5-q2 */
							d1 = diff(sb, 16, 15);
							d2 = diff(sb, 9, 0);
							d3 = diff(sb, 14, 5);
							d4 = diff(sb, 19, 10);
							d5 = diff(sb, 4, 1);
							int val1 = (26+d2-d1) % 26;
							int val2 = (26+d3-d1) % 26;
							int val3 = (26+d4-d1) % 26;
							if (d5 == val1 && val1 == val2 && val2 == val3) {
								;
							} else {
								list.remove(i);
							}
						}
					}
				}
			}
			if (!list.isEmpty()) {
				return list.get(rand.nextInt(list.size()));
			}
		}
		
	}
	
	/** find high multiplicity plaintext samples for ngrams of the given length. */
	public static void findHighMultiplicityPlaintexts(int N) {
		int max = 5;
		long total = 0;
//		Map<Float, List<List<String>>> ngramsByMult = new HashMap<Float, List<List<String>>>();
//		Map<Float, Set<String>> ngramsSeen = new HashMap<Float, Set<String>>();
		TreeSet<Float> sorted = new TreeSet<Float>(); 
		while (true) {
			total++;
			SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngrams = ngrams(N);
			for (List<String> list : ngrams) {
				StringBuffer text = flatten(list);
				float mult = Stats.multiplicity(text.toString());
				//System.out.println(mult + " " + list);
				sorted.add(mult);
				if (sorted.size() > max) {
					Float lowest = sorted.pollFirst();
//					ngramsByMult.remove(lowest);
//					ngramsSeen.remove(lowest);
				}
				
				if (sorted.contains(mult)) {
//					List<List<String>> nglist = ngramsByMult.get(mult);
//					if (nglist == null) nglist = new ArrayList<List<String>>();
//					nglist.add(list);
//					ngramsByMult.put(mult,  nglist);
					System.out.println(mult + "	" + list);
					total++;
				}
			}
			if (total % 1000000 == 0) 
				System.out.println("Sorted: " + sorted);
		}
	}
	
	
	/** start with a candidate plaintext for vigenere.
	 * loop through every possible key of length k.
	 * generate an index of every isomorphism ("next matches" arrays).
	 * sample a large number of candidate plaintexts that fit those isomorphisms
	 */
	
	static void testNgrams() {
		SubstitutionMutualEvolve.randomSource();
		List<List<String>> list = ngrams(20);
		System.out.println(ngrams(20));
		System.out.println("Size " + list.size());
	}
	static void testRandomWithConstraints() {
		int[] matches = new int[] { -1, -1, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		List<int[]> diffs = new ArrayList<int[]>();
		diffs.add(new int[] {17,0,-10,-2});
		diffs.add(new int[] {9,0,14,5});
		diffs.add(new int[] {14,5,19,10});
		diffs.add(new int[] {9,0,19,10});
		diffs.add(new int[] {19,2,-17,-9});
		diffs.add(new int[] {14,8,-18,-4});
		while (true) 
			System.out.println("HIT: " + randomNGrams(20, matches, diffs));
	}
	public static void testMatches() {
		System.out.println(Arrays.toString(nextMatches("THEYHADEATENUPALLTHE")));
	}
	public static void testValidSubstitutions() {
		String[] pairs = new String[] {
				"OPCTMOHVAVFUXPS","ANYTRACEBEFOUND",
				"CVJEFUZKYSTXOJM","OFTURKEYANDWITH",
				"BRDKUSEYXPUARRQ","NOWREADYTHEBOOK",
				"QCAEECZMTATOQP","IWASSWORNANDIM",
				"MUXEHPTQLEHNDQ","CONELMARDELSUR",
				"HJMXUQRTHFDQPI","COMPLIANCEWITH",
				"EKWRWMXUSYSXKRSJASUI","UPITISNOWHERPLEDGEOF",
				"EBOUYBOELBFVOUYSUBAH","BATHWASREADYTOUCHING",
				"JKKKIFPKKUJKDKKIFEQJ","COURTSWOULDEVERTHINK",
				"IPFIXUUEDPIIRINEBRDW","SHOULDGETHISWIFEBACK",
				"NVIKOBBVLAQKVKHVOKAK","ZEFILTHYNURSESAELOUD",
				"IBIMPOBNTTMFKMMFJFKF","OFOTHERWAYSINCLUDING",
				"XLCLVOWCVUFJELECFLUZ","WAYANDBUTFORHISVOICE",
				"UIMIOOOBLJUIMIOOOBLJ","TOMIZPEHANDIWILLPRAY",
				"UOXUTBOOXFBTOUBDNCYU","SEINWOBEIMFRHSTCKLAS",
				"KTMLKJBHZIJMLLHLTBTK","CITYWEFOUNDAPROLIFIC",
				"XMPTOFIEAXTTXTMBBLXU","HOLIDAYSWHICHCOMEBUT",
				"BKQBKLBQXZBQJBKXOBOY","HERHUSBANDBYTHEMOBOF",
				"URMDYSOYZXODTDTSYCXX","COMPLYINGWITHTHELAWS",
				"IQEMJTJJJQJIQMIJJCPJ","COMPILATIONCOPYRIGHT"
		};
		for (int i=0; i<pairs.length; i+=2) {
			String c = pairs[i];
			StringBuffer p = new StringBuffer(pairs[i+1]);
			System.out.println(validSubstitution(c, p) + " " + c + " " + p);
		}
	}
	public static void test() {
		CorpusBase.SHOW_INFO = false;
		SubstitutionMutualEvolve.initSources();
//		while (true) {
//			int N = 10 + rand.nextInt(40);
//			search(N);
//		}
//		while (true) 
//			search(20);
//		findHighMultiplicityPlaintexts(30);
//		testValidSubstitutions();
//		System.out.println(Arrays.toString(Ciphers.toNumeric("RETAINSSOMEWHAT",false)));
//		for (int[] pair : equalPairs("THISISATEST")) {
//			System.out.println(Arrays.toString(pair));
//		}
//		System.out.println(Vigenere.shift('a', -17));
//		System.out.println(randomNGrams(20,
//				new int[] { -1, -1, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }));
//		testNgrams();
//		testRandomWithConstraints();

		int[] result;
		result = nextMatches("ANYTRACEBEFOUND");
		System.out.println(Arrays.toString(result));
		System.out.println(Arrays.hashCode(result));
		result = nextMatches("OPCTMOHVAVFUXPS");
		System.out.println(Arrays.toString(result));
		System.out.println(Arrays.hashCode(result));
		result = nextMatches("OPCTMOHVAVFUXPC");
		System.out.println(Arrays.toString(result));
		System.out.println(Arrays.hashCode(result));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();
//		testMatches();
	}
}
