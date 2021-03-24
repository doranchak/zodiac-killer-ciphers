package com.zodiackillerciphers.tests;

import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.keyreconstruction.KeyReconstruction;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;
import com.zodiackillerciphers.transform.CipherTransformations;

/**
 * Chaotic Caesar cipher by Christoph Tenzer
 * http://scienceblogs.de/klausis-krypto-kolumne/2020/05/10/christophs-chaotic-caesar-challenge
 */
public class ChaoticCaesar extends CorpusBase {
	public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String challengeCipher = "YWXHZVFKTLJIPQPUHUSIEUNPXWTXHASLAADEOVXNDZJYRPUBKUGHSPXMOEPJTZITGQQRLXXERRJCJQPXICKJECPDZDMLUIDAHZRDFWUZPMXLSHUNJIXMDUENTZIIDSESYHBVEXSIUMPTZIJPDJNISEDERHHTYAQHEMTIRAICENTZMJXNDNVDBZLXTTLITCCDOOJSJESIIWPXJJNDUNPBBSLPSIAFRCRPAFIXRTKLQIRBIXSIWSRBEDANUUXAIDCTCEHYZOZARZRUXUHMCISUWEABIPDEJOZLERYTAERATETLZRZXAAOZKXIUWNPSYDZSIGPZJBZRKQLXSTSITYKKXHGXDWIXPRXOVRQTQFXXIVJOSBZDNOPWLFLNIOP";
	public static String z408Cipher = "QDEOIVEWOHCSBHLTOITTNXSUBTITGRLXGYTQRYBHEQAVUIHRCBFMXJEVOPMRVYJVUVNPCMPBYBJUGCBYSIIDGBXNWMCFCPMELJIWLVUMQXGVOWKVZOLSGOKVKOXZGFKEMMYVDLAMMWPVDWCNIZGDNXBMGAWTAJHSKFZJHODJJPAMGKFLKNUPZCGZAQOAKOLCNQSCARIRTBPDKOXOQZSQQMBEBRLFQSGQPNUQLBVGPJDFQOHLSGKAGATRERPGEGUOBLCUJTRRRLKTNUAJCBKTJYHPTAXWDFBPPMYNEMECCFPHMYRZCRCONPNYKJIUOJSGBEMAHLBPYKSPTQSCSUCXBULSBXZTGPASRAFNHJRRXIVSFLPWSTOITEVCRCFKHFAAZCJJCDQAWSSLGCPVNFYKNYSK"; // key is "THISISZODIAC" (THISZODACBEFGJKLMNPQRUVWXY), n=17
	/** decode the given cipher using the given key */
	public static String decode(String cipher, String key, int n) {
		StringBuffer sbKey = new StringBuffer(key);
		StringBuffer pt = new StringBuffer();
		for (int i=0; i<cipher.length(); i++) {
			char ct = cipher.charAt(i);
			int index1 = sbKey.indexOf(""+ct);
			int index2 = ((index1 - n) + key.length()) % key.length(); // move left, wrapping around if necessary.
			char p = sbKey.charAt(index2);
			pt.append(p);
			swap(sbKey, index1, index2);
			//System.out.println("new key: " + sbKey);
		}
		//System.out.println(key + " " + pt);
		return pt.toString();
	}
	/** encode the given cipher using the given key */
	public static String encode(String cipher, String key, int n) {
		StringBuffer sbKey = new StringBuffer(key);
		StringBuffer pt = new StringBuffer();
		for (int i=0; i<cipher.length(); i++) {
			char ct = cipher.charAt(i);
			int index1 = sbKey.indexOf(""+ct);
			int index2 = ((index1 + n) % key.length()); // move left, wrapping around if necessary.
			char p = sbKey.charAt(index2);
			pt.append(p);
			swap(sbKey, index1, index2);
			//System.out.println("new key: " + sbKey);
		}
		//System.out.println(key + " " + pt);
		return pt.toString();
	}
	
	public static void swap(StringBuffer sb, int a, int b) {
		char ca = sb.charAt(a);
		char cb = sb.charAt(b);
		sb.setCharAt(a, cb);
		sb.setCharAt(b, ca);
	}
	
	public static void scanCorpus() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");

		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		double best = 0;
		String tab = "	";
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			for (int i=tokens.length-100; i<tokens.length; i++) {
				for (int n=1; n<=4; n++) { // number of tokens to select
					StringBuffer sbTokens = new StringBuffer();
					for (int j=i; j<(i+n); j++) {
						if (j >= tokens.length) break;
						sbTokens.append(tokens[j]);
						String key = KeyReconstruction.keyFrom(sbTokens.toString());
						for (int z=1; z<26; z++) {
							String plaintext = decode(challengeCipher, key, z);
							double zk = ZKDecrypto.calcscore(plaintext, "EN", false);
							if (zk >= best*.9) {
								StringBuffer result = new StringBuffer();
								result.append(zk);
								result.append(tab);
								result.append(key);
								result.append(tab);
								result.append(sbTokens);
								result.append(tab);
								result.append(z);
								result.append(tab);
								result.append(plaintext);
								result.append(tab);
								result.append(file);
								if (zk > best) {
									result.append(tab).append("NEW BEST");
									best = zk;
								}
								System.out.println(result);
							}
						}
					}
				}
			}
		}
		
	}
	public static void testEncodeDecode() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		String key = KeyReconstruction.keyFrom("CHALLENGE");
		System.out.println("Key: " + key);
		System.out.println(encode("TOBEORNOTTOBE", key, 3));
		System.out.println(decode("WRIIUUDXZACMM", key, 3));
		System.out.println(encode(Ciphers.Z408_SOLUTION.toUpperCase(), KeyReconstruction.keyFrom("THISISZODIAC"), 17));
		
		key = "THISISZODIAC";
		for (int i=1; i<=key.length();i++) {
			String k = key.substring(0,i);
			String pt = decode(z408Cipher, KeyReconstruction.keyFrom(k), 17);
			System.out.println(NGramsCSRA.ngramSum(pt.substring(0,20), "EN", false, 3) + "	" + k + "	" + pt);		
		}
		for (int i=0; i<1000; i++) {
			String shuffled = CipherTransformations.shuffle(key);
			String pt = decode(z408Cipher, KeyReconstruction.keyFrom(shuffled), 17);
			System.out.println(NGramsCSRA.ngramSum(pt.substring(0,20), "EN", false, 3) + "	" + shuffled + "	" + pt);		
		}
	}
	
	public static void testSolution() {
		String keyword = "VICTORFRANCISHESS";
		String pt = decode(challengeCipher, KeyReconstruction.keyFrom(keyword), 18);
		System.out.println(pt);
		
		String testWord = "VICTORFANSHE";
		for (int i=0; i<testWord.length(); i++) {
			for (int j=i+1; j<=testWord.length(); j++) {
				String sub = testWord.substring(i, j);
				//System.out.println(sub);
				String pt2 = decode(challengeCipher, KeyReconstruction.keyFrom(sub), 18);
				float match = match(pt, pt2);
				System.out.println(match + "	" + sub + "	" + pt2);
			}
		}
	}

	public static float match(String s1, String s2) {
		float match = 0;
		for (int z=0; z<s1.length(); z++) {
			if (s1.charAt(z) == s2.charAt(z)) match++;
		}
		match = 100 * match / s1.length();
		return match;
	}
	public static void testSwaps() {
		Random rand = new Random();
		StringBuffer key = new StringBuffer("VICTORFANSHEBDGJKLMPQUWXYZ");
		String pt = decode(challengeCipher, key.toString(), 18);
		
		for (int a=0; a<key.length()-1; a++) {
			for (int b=a+1; b<key.length(); b++) {
				StringBuffer key2 = new StringBuffer(key);
				swap(key2, a, b);
				String pt2 = decode(challengeCipher, key2.toString(), 18);
				System.out.println(match(pt, pt2) + "	" + key2 + "	" + pt2);
			}
		}
		
//		for (int i=0; i<10000; i++) {
//			int a = rand.nextInt(key.length());
//			int b = a;
//			while (b==a) b = rand.nextInt(key.length());
//			swap(key, a, b);
//			String pt2 = decode(challengeCipher, key.toString(), 18);
//			System.out.println(match(pt, pt2) + "	" + key + "	" + pt2);
//			
//		}
		
	}
	public static void testDeletions() {
		String keyword = "VICTORFANSHE";
		String pt = decode(challengeCipher, KeyReconstruction.keyFrom(keyword), 18);
		for (int i=0; i<keyword.length(); i++) {
			StringBuffer sb = new StringBuffer(keyword);
			sb.deleteCharAt(i);
			String pt2 = decode(challengeCipher, KeyReconstruction.keyFrom(sb.toString()), 18);
			System.out.println(match(pt, pt2) + "	" + sb + "	" + pt2);
		}
	}
	public static void testRandom() {
		
		while (true) {
			System.out.println("STARTING TRIAL...");
			String key = alphabet;
			key = CipherTransformations.shuffle(key);
			String pt = decode(challengeCipher, KeyReconstruction.keyFrom("VICTORFANSHE"), 18);
			float best = 0;
			int tries = 0;
			Random rand = new Random();
			StringBuffer bestKey = new StringBuffer(key);
			while (true) {
				StringBuffer trialKey = new StringBuffer(bestKey); 
				// randomly swap letters a random number of times
				int n = rand.nextInt(4) + 1;
				for (int i=0; i<n; i++) {
					int a = rand.nextInt(trialKey.length());
					int b = a;
					while (b==a) b = rand.nextInt(trialKey.length());
					swap(trialKey, a, b);
					String pt2 = decode(challengeCipher, trialKey.toString(), 18);
					float match = match(pt, pt2);
					if (match > best) {
						best = match;
						bestKey = trialKey;
						if (best > 15) System.out.println(best + "	" + bestKey + "	" + pt2);
						tries = 0;
					}
				}
				tries++;
				if (tries > 100000) break;
			}
		}
	}
	public static void testCompare() {
		String pt1 = decode(challengeCipher, KeyReconstruction.keyFrom("VICTORFANSHE"), 18);
		String pt2 = decode(challengeCipher, "BRGFUPHINEWLYAMVCTOJZKQSXD", 18); // "ZOHXYSLLDKRSAUKQMYISDZCLMBSHXKTPPLEDBKHMEUEUELYOVZOXIAHNGRFDIUTIOYUEAYUREDINTZEZTMASPHERXTHFXSSKMQTILNQOTHQFIMOWVSOHVZTHEUVDIVTIQNWIPUDVXCEEGDKRIAHDKTIRENCEOJOMTHEDPFKHTHEWOFUCKOKIHHEGPRPDKONINCAFEJADFHLHELCFGORCOEJKJAIDKOPREIFWPVPPYEVNAZPJZLTMAFEMHZOJPEMRNDZQHXUPTWFWOZWNBLKOHJLCKLJLDUUELDGFKPJIDKJPOPXZSVZOENSOOXRIHJXVVHLNDQMLVBRNARFALOAIMGHZJUKCELBYATHRGSHGSKDUIHCJMWAORJTCSWK";
		String str = "";
		for (int i=0; i<pt1.length(); i++) {
			char c1 = pt1.charAt(i);
			char c2 = pt2.charAt(i);
			if (c1 == c2) 
				str += "" + c1;
			else str += ".";
		}
		System.out.println(pt1);
		System.out.println(pt2);
		System.out.println(str);
	}
	
	public static void main(String[] args) {
//		testEncodeDecode();
//		scanCorpus();
//		testSolution();
//		testSwaps();
//		testDeletions();
//		testRandom();
		testCompare();
	}
}
