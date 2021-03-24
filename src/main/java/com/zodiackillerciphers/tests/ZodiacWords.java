package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;

/** manage corpus of words that appear in Zodiac's correspondences.  use them to score potential solutions. */
public class ZodiacWords {
	/** file containing all the words zodiac used */
	public static String wordsPath = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/letters/all-words-with-counts.txt";
	/** word frequency counts from his letters */
	public static Map<String, Integer> countsZodiac;
	/** word frequency scores from regular english */
	public static Map<String, Integer> countsEnglish;
	
	/** rank of words in sorted order by English frequencies (descending).  rank is float in [0,1] */
	public static Map<String, Float> wordsSorted;
	
	/** init */
	public static void init() {
		WordFrequencies.init();
		countsZodiac = new HashMap<String, Integer>();
		countsEnglish = new HashMap<String, Integer>();
		for (String line : FileUtil.loadFrom(wordsPath)) {
			String[] split = line.split(" ");
			String key = split[1];
			Integer val = Integer.valueOf(split[0]);
			countsZodiac.put(key, val);
			
			val = WordFrequencies.freq(key);
			countsEnglish.put(key, val);
		}
//		System.out.println(countsZodiac);
//		System.out.println(countsEnglish);
		
		// sort map
		countsEnglish = WordFrequencies.sortByValue(countsEnglish, true);
//		System.out.println("sorted: " + countsEnglish);
		wordsSorted = new HashMap<String, Float>();
		
		int count = 0; // [0, n-1], n = number of words
		for (String word : countsEnglish.keySet()) {
			float rank = count++;
			rank = 1 - (rank / (countsEnglish.size()-1));
			wordsSorted.put(word, rank);
		}
//		System.out.println(wordsSorted);
	}
	/** compute a zodiac word score, based on relative frequency of individual words, of the given space 
	 * if reverse is true, then the score is based on relative INfrequency of individual words. 
	 */
	public static float wordScore(String text, boolean reverse, List<String> foundWords) {
		if (countsEnglish == null) init();
		float score = 0;
		String[] split = text.split(" ");
		Float rank;
		for (String word : split) {
			 rank = wordsSorted.get(word);
			 if (rank == null) continue;
			 if (foundWords != null) foundWords.add(word);
			 if (reverse) rank = 1 - rank;
			 score += rank;
		}
		return score;
	}
	public static void testInit() {
		init();
	}
	public static void testWordScore() {
		String[] lines = new String[] {
				"ROSAOCILE IN OTTON DER PRISH HEAVY SLY AT L TO COMING HAS IT HAIRES AROUS A POES BURD I HIS MEES THER REALL WE CAT CER FILER TOO YLUEURTMTSSOSITIST INT EVENT SOME AT HDOLIDED THAT TER WOMADE NOW AS CLEDYD A NOTISES I WOCAID AND SOME THED IN HOT BEEN DE DIE RAINE OER SAIOTE OLD PIA CHERO THE AND DGAUSE HE RICHAEYCLCISSLY LOW HER ANTS HIS SAR EST WHEN TO FT THE REVOLEN A LEA HAGGAN SLEECNL LINE PRES HOLES SAID NE TO THEM GO",
				"RGH AND IND PROF COME A SUREN PIERATURE IT HWST ENTER AND THE PERISVEOES AN ISS STOME A STATRHMNOITURE EMAID HER LACESS THONER WITH TER A NOISITH SOMES THAT HE BET OF WHICT FOR YATND MORO FINN DID TLNVN AT THE MEN TO SHATUMB SALES OR NPE MAD NO HBAOLLY IN THIND ALLEY PNAM US TO COLLEWYGANERAL TLED ENTIET AND ANOTLE THE ANGRUBONACSEAD ONE IS A MEASINENT THAT ET TIME I CONSE FRT TO M YE MID AIRCE SO NOT LISTER OMS OF HER E",
				"NE HAM IS ALL SORS I HAVE SITE AN ING TOO WAS A SIDAUOHBORINWFT DEATH IN TVVS SHE SLEEPER SITE REL HEE OF TLLANT I THE UPPTE HER M AND STRAPPROST OBERY THE THE CLHYYEED SEMSEGAAD CORN TO DEATITOU INT WITDTSYHILE CAME TH AGAIDGT NOT THE TR WAS BALOEOHHT AND IT AR THE DISCONVEE IN AS I THEN DOME EN IS IT OSTTED MAN O TELL SORTNER HER ORDS MAND OUT UPE ARE AT THE RDDSOD OR WHERE YES SHOP BAT DOORDS SUMER OF THE DENING TDMNEHI",
				"THIS IS THE ZODIAC SPEAKING UP TO THE END OF OCT I HAVE KILLED 7 PEOPLE I HAVE GROWN RATHER ANGRY WITH THE POLICE FOR THEIR TELLING LIES ABOUT ME SO I SHALL CHANGE THE WAY THE COLLECTING OF SLAVES I SHALL NO LONGER ANNOUNCE TO ANYONE WHEN I COMMITT MY MURDERS THEY SHALL LOOK LIKE ROUTINE ROBBERIES KILLINGS OF ANGER A FEW FAKE ACCIDENTS ETC"
		};
		boolean[] reverse = new boolean[] {false, true};
		for (String line : lines) {
			for (boolean rev : reverse) {
				List<String> words = new ArrayList<String>();
				float score = wordScore(line, rev, words);
				System.out.println(score + ", " + rev + ", " + words + ", " + line);
			}
		}
	}
	public static void main(String[] args) {
//		testInit();
		testWordScore();
	}
}
