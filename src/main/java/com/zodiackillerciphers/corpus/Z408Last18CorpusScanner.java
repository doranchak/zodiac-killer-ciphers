package com.zodiackillerciphers.corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.old.EditDistance;
import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.names.Census;
import com.zodiackillerciphers.names.Name;
import com.zodiackillerciphers.old.MyNameIs;
import com.zodiackillerciphers.suffixtree.LRS;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** look in corpus for anagrams of the last 18 letters of Z408 */
public class Z408Last18CorpusScanner extends CorpusBase {

	/** possible variations of last 18:   http://zodiackillersite.com/viewtopic.php?p=69280#p69280 */
	public static Set<String> z18 = new HashSet<String>();
	static {
		z18.add("EBEORIETEMETHHPITI");
		z18.add("EBEORIETEMSTHHPITI");
		z18.add("EBEORWEOEMSTHHPITI");
		z18.add("EBEORWETEMETHHPITI");
		z18.add("EBEORWETEMSTHHPIOI");
		z18.add("EBEORWETEMSTHHPITI");
		z18.add("EBSORIEOEMSTHHPITI");
		z18.add("EBSORIETEMETHHPITI");
		z18.add("EBSORIETEMSTHHPIOI");
		z18.add("EBSORIETEMSTHHPITI");
		z18.add("EBSORWEOEMETHHPITI");
		z18.add("EBSORWEOEMSTHHPIOI");
		z18.add("EBSORWEOEMSTHHPITI");
		z18.add("EBSORWETEMETHHPIOI");
		z18.add("EBSORWETEMETHHPITI");
		z18.add("EBSORWETEMSTHHPIOI");
		z18.add("EBSORWETEMSTHHPITI");
	}

	static String[] garbles = new String[] {
		"EBEO RIET EMETH HPITI",
		"EEOR I EME ME THH PITI",
		"EBEOR I ETE ME TH PETI",
		"EBFO RIET EMETH IPYTI"
	};

	/** look for exact anagrams, matching any of the possible garbles */
	public static void search() {
		WordFrequencies.init();
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long hits = 0;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngrams = ngrams(18);
			for (List<String> plaintext : ngrams) {
				StringBuffer sbWithSpaces = flatten(plaintext, true);
				StringBuffer sbWithoutSpaces = flatten(plaintext, false);
				samples++;
				for (String test : z18) {
					if (Anagrams.anagram(test, sbWithoutSpaces.toString(), true)) {
						System.out.println(WordFrequencies.scoreLog(sbWithSpaces.toString()) + " " + sbWithSpaces + " " + test);
					}
				}
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
		
	}
	/** look for phrases that anagram to last 18 of z408 but allow some errors.
	 * this is based on proposed solution "ROBERT EMMET THE HIPPIE" which has 4 errors and an edit distance of 3 to the sorted letters of the last 18 
	 */
	public static void search2(int maxEditDistance) {
		WordFrequencies.init();
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		
		String z18 = Census.sortString("EBEORIETEMETHHPITI");
		System.out.println("sorted z18: " + z18);
		Set<String> seen = new HashSet<String>();
		long samples = 0;
		long hits = 0;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngrams = new ArrayList<List<String>>();
			for (int n=18-maxEditDistance; n<=18+maxEditDistance; n++) {
				ngrams.addAll(ngrams(n));
			}
			for (List<String> plaintext : ngrams) {
				StringBuffer sbWithSpaces = flatten(plaintext, true);
				StringBuffer sbWithoutSpaces = flatten(plaintext, false);
				String sorted = Census.sortString(sbWithoutSpaces.toString());
				int editDistance = EditDistance.LD(z18, sorted);
				samples++;
				if (editDistance <= maxEditDistance) {
					if (seen.contains(sbWithSpaces.toString())) continue;
					seen.add(sbWithSpaces.toString());
					System.out.println(editDistance + "	" + WordFrequencies.scoreLog(sbWithSpaces.toString()) + "	" + sbWithSpaces + "	" + sorted);
				}
				
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
	}
	
	/** search giant list of facebook names for close matches to anagrams of z18 */
	static void searchFacebookNames(String path, int maxEditDistance) {
		String z18 = Census.sortString("EBEORIETEMETHHPITI");
		BufferedReader input = null;
		long counter = 0;
		try {
//			input = new BufferedReader(new FileReader(new File(path)));
			input = new BufferedReader(
			           new InputStreamReader(new FileInputStream(path), "UTF-8"));
			
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String sorted = Census.sortString(line.toUpperCase());
				int editDistance = EditDistance.LD(z18, sorted);
				counter++;
				if (editDistance <= maxEditDistance) {
					System.out.println(editDistance + "	" + line);
				}
			}
			//System.out.println("read " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * search giant list of facebook names for close matches to anagrams of z18.
	 * allow leftover letters so we can try manipulating them to form some
	 * additional word(s).
	 */
	static void searchFacebookNames2(String path, int minLength, int maxDiffs) {
		String z18 = Census.sortString("EBEORIETEMETHHPITI");
		Map<Character, Integer> counts1 = Ciphers.countMap(z18);
		BufferedReader input = null;
		long counter = 0;
		try {
//			input = new BufferedReader(new FileReader(new File(path)));
			input = new BufferedReader(
			           new InputStreamReader(new FileInputStream(path), "UTF-8"));
			
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.length() < minLength) continue;
				line = line.toUpperCase();
				Map<Character, Integer> counts2 = Ciphers.countMap(line);
				
				int diffs = 0;
				// count how many letters in this name exceed the counts in z18
				for (Character c : counts2.keySet()) {
					Integer val1 = counts1.get(c);
					if (val1 == null) val1 = 0;
					Integer val2 = counts2.get(c);
					if (val2 > val1) {
						diffs += (val2-val1);
					}
				}
				if (diffs > maxDiffs) continue;
				
				// determine the leftover letters
				String leftovers = "";
				for (Character c : counts1.keySet()) {
					Integer val1 = counts1.get(c);
					Integer val2 = counts2.get(c);
					if (val2 == null) val2 = 0;
					if (val1 > val2) {
						for (int i=0; i<(val1-val2); i++) {
							leftovers += c;
						}
					}
				}
				int score = diffs + leftovers.length();
				
				System.out.println(score + " " + diffs + " " + leftovers.length() + " " + line + " " + leftovers);
//				String sorted = Census.sortString(line.toUpperCase());
//				int editDistance = EditDistance.LD(z18, sorted);
//				counter++;
//				if (editDistance <= maxEditDistance) {
//					System.out.println(editDistance + "	" + line);
//				}
			}
			//System.out.println("read " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void testEditDistance() {
		String z18 = Census.sortString("EBEORIETEMETHHPITI");
		for (String s : new String[] {
				"EBEORIETEMETHHPITI",
				"IHOPEIBETTERGETHIM",
				"THETIPIMROBERTEEEEE",
				"ROBERTEMMETTHEHIPPIE",
				"ITIMETHEOTHERPIECE",
				"THETIMETODECIPHERI"
		}) {
			String sorted = Census.sortString(s);
			System.out.println(EditDistance.LD(z18, sorted) + " " + s + " " + sorted);
		}
		
	}
	
	public static void differences(String str) {
		str = str.replace(" ", "");
		String sorted1 = Census.sortString(str);
		for (String garble : garbles) {
			String garble2 = garble.replace(" ", "");
			String sorted2 = Census.sortString(garble2);
			System.out.println("	" + Ciphers.countMapDifferences(Ciphers.countMap(str), Ciphers.countMap(garble2)) + " " + EditDistance.LD(sorted1, sorted2) + " " + garble);
		}
	}
	
	public static void testDifferences() {
		String[] tests = new String[] {
				"TIMOTHIE E PHEIBERTE",
				"BEFORE I MEET THEM I PITY",
				"BEFORE I MEET THEM I PITY THEM",
				"BEFORE I MEET ETERNITY",
				"BE HERE TIPTOE TIE HIM",
				"BE THERE TO MEET PHIL",
				"BETTIE THORPE EEHIIM",
				"BITE HOPE EITHER TIME",
				"BITE THE PIE HERO ITEM",
				"BITTER PETE HI HOMIE E",
				"BUT I HOPE NEITHER DIE",
				"EERIE BEHEMOTH TIP IT",
				"EITHER TO DIE IN THREE",
				"HE BIT EITHER EPITOME",
				"HE BITE I PETE MORETTI ",
				"HE BITE METEORITE HIP",
				"HE EMBODIES THE SPIRIT",
				"HE EMIT I GET THE BIG CREEP",
				"HE IS TO BE PITIED THEN",
				"HE MIGHT TIE THE ROPE",
				"HE THE TIME TO BE RIPE",
				"HE TIE HIM ROPE BETTIE",
				"HEIDI TEMPLETON HEBERT",
				"HEIR TO THE TIDE EMPIRE",
				"HERIBETO THE PIE TIME",
				"HI BEEP HIT METEORITE",
				"HI I TIME THE BEER POET",
				"HI IM THE TEEPEE ORBIT",
				"HI IT TIME TO BEEP HERE",
				"HI TIME TO HIRE PET BEE",
				"HI TIME TO HIT BEER PEE",
				"HI TO THE PRIME BEETLE",
				"HIDE IT BETTER I HOPE",
				"HIT TEMPT BEFORE I DIE",
				"HOE PIE BETTER TIE HIM",
				"I BE HER TIME TO THE PIE",
				"I BE THE HIP METEORITE",
				"I BE THERE TIME THE POI",
				"I BEER POEM WITH TEETH",
				"I BEGIN THE TIGHTROPE",
				"I BETTER HOPE I TEE HIM",
				"I BITE THE PIE THEOREM",
				"I HIT THE BEER EPITOME",
				"I HOE THE TIBET EMPIRE",
				"I HOPE HE BITE TERMITE",
				"I HOPE I BETTER GET HIM",
				"I HOPE ITS THE BEER TIME",
				"I HOPE TO BE THIRTEEN",
				"I MET THE PIE BITE HERO",
				"I PITIED THE MOTHER",
				"I TIME THE OTHER PIECE",
				"I TIME THE THREE BODIES",
				"IE THIRTIETH BEE POEM",
				"IM BOTH EERIE EPITHET",
				"IM EITHER TO BE THE PIE",
				"IM HERE I BITE THE POET",
				"IM HERE TO BITE THE PIE",
				"IM HERIBETO TEETH PIE",
				"IM PETER I BITE THE HOE",
				"IM THE BIG CREEP EIGHT TEE",
				"IM THE BORE TIE THE PIE",
				"IM THE EERIE PITH TO BE",
				"IM THE PETITE BIO HERE",
				"IM THE TIE BORE THE PIE",
				"IM THE TIME TO BE HERE",
				"IM THE WHITE BEER POET",
				"IMO THE I GET THE BIG CREEP",
				"IT MIGHT BE THE ROPE",
				"IT PIECE HIM TOGETHER",
				"IT TIE PHOEBE THEIMER",
				"KEPT HIM THERE TO DIE",
				"LET EITHER OF THEM DIE",
				"M HIDES IT BETTER I HOPE",
				"OH BITE THEIR PEE TIME",
				"OR IT MIGHT BE HER DIET",
				"POLICE LET THEM EITHER",
				"TEETH BITE HOMIER PIE",
				"THE EERIE POET BIT HIM",
				"THE EMPIRE BITE THEM",
				"THE HIP WET EERIE TOMB",
				"THE KILLER BE THE POET",
				"THE OTHER TIME I DIED",
				"THE POLICE ARE THE TIME",
				"THE POLICE RETIME HEAT",
				"THE PROHIBITED TIME",
				"THE THREE POLICE A TIME",
				"THE TIME HE TIED A ROPE",
				"THE TIME THEIR BODIES",
				"TIE THE ROPE TO THE BIKE",
				"TIME TO BE THE PIE HIER",
				"TO BE THEIR POLICE",
				"TO HELP THEM DIE THERE",
				"TO KEEP HIM TETHERED",
				"WHIP EERIE TEETH TOMB",
				"BEFORE I TAME THE PITY"
		};
		
		for (String test : tests) {
			System.out.println(test);
			differences(test);
		}
	}
	
	/** If we let the last 18 end with "THEZODIAC", the rest of it fits this pattern: T_H____A_.  Find
	 * all phrases that fit that pattern. */
	public static void searchTheZodiac() {
		WordFrequencies.init();
		SubstitutionMutualEvolve.initSources();
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long hits = 0;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngrams = ngrams(9);
			for (List<String> plaintext : ngrams) {
				StringBuffer sbWithSpaces = flatten(plaintext, true);
				StringBuffer sbWithoutSpaces = flatten(plaintext, false);
				samples++;
				char c1 = sbWithoutSpaces.charAt(0);
				char c2 = sbWithoutSpaces.charAt(2);
				char c3 = sbWithoutSpaces.charAt(7);
				char c4 = sbWithoutSpaces.charAt(8);
				if (c1 == c4 && c2 == 'H' && c3 == 'A') System.out.println(sbWithSpaces);
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
		
	}

	
	public static void main(String[] args) {
//		System.out.println(Anagrams.anagram("EBEORIETEMETHHPITI", "BEEEEEHHIIIMOPRTTT", true));
//		search2(3);
//		searchFacebookNames("/Volumes/Smeggabytes/projects/zodiac/docs/facebook-names/names-nospaces", 4);
//		searchFacebookNames2("/Volumes/Smeggabytes/projects/zodiac/docs/facebook-names/names-nospaces", 9, 3);
//		testEditDistance();
//		String s1 = Census.sortString("EBEORIETEMETHHPITI");
//		String s2 = Census.sortString("ROBERTEMMETTHEHIPPIE");
//		System.out.println(s1);
//		System.out.println(s2);
//		System.out.println(EditDistance.LD(s1, s2));
//		differences("TIMOTHIE E PHEIBERTE");
//		testDifferences();
		searchTheZodiac();
	}
}
