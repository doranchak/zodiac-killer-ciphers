package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/**
 * Find phrases with the smallest alphabets.  That is, find phrases that have the fewest number of unique letters in them.
 * http://zodiackillersite.com/viewtopic.php?p=79214#p79214
 */
public class SmallestAlphabets extends CorpusBase {
	
	// plaintext 1: I HATE THAT THIS IS THE ASSASSINATION. IT IS IN THIS SENSE THAT
	// THE NOTION THAT SHE HAS A TOOTH TATTOO ANNOTATION IN THIS SEASON. IT IS NOT
	// IN THE SHOES. HE INSISTS THAT IT IS NOT A HAT HIT IN ONE SHOT. OH SHIT, THE
	// HOST IS THE ONE THAT HAS TO INITIATE THE TONES THAT HE ASTONISHES. THE
	// ATHEISTS, NOT THE SATANISTS, TASTE THE ASSISTANT'S SHOES. ANTIONETTE IS NOT
	// AN ANTITHEIST ON THE ANTITHESIS. ONE, SHE IS NONSENSE, NOT THE INSANE ONION
	// SENSATION.
	static String plaintext1 = "IHATETHATTHISISTHEASSASSINATIONITISINTHISSENSETHATTHENOTIONTHATSHEHASATOOTHTATTOOANNOTATIONINTHISSEASONITISNOTINTHESHOESHEINSISTSTHATITISNOTAHATHITINONESHOTOHSHITTHEHOSTISTHEONETHATHASTOINITIATETHETONESTHATHEASTONISHESTHEATHEISTSNOTTHESATANISTSTASTETHEASSISTANTSSHOESANTIONETTEISNOTANANTITHEISTONTHEANTITHESISONESHEISNONSENSENOTTHEINSANEONIONSENSATION";
	
	// plaintext 2: HE ASTONISHES ANTONIO'S ASSISTANT. HE IS NO SANTA IN THAT STATE
	// TO THE EAST. THE TESTES IN THIS STATION TASTE AS SHOES. NASA SENT ONE
	// ASSASSINATION IN ONE SHOT. IT IS NOT SO ASIAN. NO ONE HAS ONE. SOON IT IS IN
	// THE INITIATION TO SIT ON THESE. THE SATIN SHEEN HAS ITS HEAT ON INSTANT
	// TOAST. SO THEN HE IS ON THIS NOTE THAT SHE'S THE ONE THAT HAS THE SON IN A
	// TENT SO THAT THIS INTENTION IS SOON TO TEST HIS NOSE. AS HE HITS THIS SNOT,
	// THE NATION HAS NO SENSE.
	static String plaintext2 = "HEASTONISHESANTONIOSASSISTANTHEISNOSANTAINTHATSTATETOTHEEASTTHETESTESINTHISSTATIONTASTEASSHOESNASASENTONEASSASSINATIONINONESHOTITISNOTSOASIANNOONEHASONESOONITISINTHEINITIATIONTOSITONTHESETHESATINSHEENHASITSHEATONINSTANTTOASTSOTHENHEISONTHISNOTETHATSHESTHEONETHATHASTHESONINATENTSOTHATTHISINTENTIONISSOONTOTESTHISNOSEASHEHITSTHISSNOTTHENATIONHASNOSENSE";
	
	// cipher grid:  8x8.
	static String grid = "KpzcE9-SL43qNVF<XR5^lyH7Dbj%&GTf@Y8:C|UWOA1;*#B_>+Mt.d(26P[)J/Zk";
	
	// plaintext alphabet:  length 8
	static String ptAlphabet = "AEHINOST";
	
	public static void scanCorpus() {
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		double best = 0;
		String tab = "	";
		Set<String> seen = new HashSet<String>();
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			for (int i=0; i<tokens.length; i++) {
				StringBuffer sbWithSpaces = new StringBuffer();
				StringBuffer sbWithoutSpaces = new StringBuffer();
//				Map<String, StringBuffer> longest = new HashMap<String, StringBuffer>();
				for (int j=i; j<tokens.length; j++) {
					String token = tokens[j];
					sbWithoutSpaces.append(token);
					String alpha = Ciphers.alphabet(sbWithoutSpaces);
//					System.out.println(alpha);
					if (!lettersIn(alpha,ptAlphabet)) break;
					if (sbWithSpaces.length() > 0) sbWithSpaces.append(" ");
					sbWithSpaces.append(token);
					
//					if ("AEHINOST".equals(alpha)) {
					
					if (seen.contains(sbWithSpaces.toString())) continue;
					if (Stats.ioc(sbWithoutSpaces.toString()) >= 0.2) continue;
					seen.add(sbWithSpaces.toString());
					System.out.println(sbWithoutSpaces.length() + tab + sbWithSpaces);
//					}
					//if (alpha.length() > 8) break;
//					if (alpha.length() > 6 && sbWithoutSpaces.length() > 10) {
//						longest.put(alpha, sbWithSpaces);
//					}
					
				}
//				for (String alpha : longest.keySet()) {
//					System.out.println(alpha + tab + longest.get(alpha));
//				}
			}
		}
	}
	
	public static boolean lettersIn(String letters, String container) {
		for (int i = 0; i < letters.length(); i++)
			if (!container.contains("" + letters.charAt(i)))
				return false;
		return true;
	}
	
	public static int index(char ch) {
		for (int i=0; i<ptAlphabet.length(); i++)
			if (ptAlphabet.charAt(i) == ch) return i;
		return -1;
	}
	public static void encode() {
		String cipher = "";
		
		Map<Character, Character> key1 = new HashMap<Character, Character>(); 
		Map<Character, Character> key2 = new HashMap<Character, Character>();
		for (int i=0; i<plaintext1.length(); i++) {
			char p1 = plaintext1.charAt(i);
			char p2 = plaintext2.charAt(i);
			int col = index(p1);
			int row = index(p2);
			
			char ct = grid.charAt(8*row + col);
			cipher += ct;
			
			key1.put(ct, p1);
			key2.put(ct, p2);
		}
		System.out.println("Key 1: " + key1);
		System.out.println("Key 2: " + key2);
		System.out.println("Cipher: " + cipher);
		System.out.println("Decode 1: " + Ciphers.decode(cipher, key1));
		System.out.println("Decode 2: " + Ciphers.decode(cipher, key2));
	}

	public static void main(String[] args) {
//		scanCorpus();
		encode();
//		System.out.println(Ciphers.alphabet("AS SHE HAS ITS NOT A BIGGIE TO"));
//		System.out.println(Stats.ioc("SHITSHITSHIT"));
	}
}
