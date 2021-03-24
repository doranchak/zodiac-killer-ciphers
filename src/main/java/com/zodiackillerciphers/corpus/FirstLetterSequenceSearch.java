package com.zodiackillerciphers.corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.io.Unzip;
import com.zodiackillerciphers.lucene.ZKDecrypto;

/** scan a large corpus for word ngrams.  find the largest ngram sequences where the sequence of 
 * initial letters of each ngram matches sequences in the tamad shud or ricky mccormick codes.
 * 
 * search also for matching substrings
 * 
 * maintain a heap of the best results
 * 
 */
public class FirstLetterSequenceSearch {
	// https://en.wikipedia.org/wiki/Tamam_Shud_case#/media/File:SomertonManCode.jpg
	public static String[] ciphers = new String[] {
			"MRGOABABDMLIAOIMTBIMPANETPMLIABOAIAQCITTMTSAMSTGAB", // tamad shud cipher
			"WRGOABABDMLIAOIWTBIMPANETPMLIABOAIAQCITTMTSAMSTGAB",  // alternate version: some Ms look like Ws.
			"MNDMKNEARSENSMKNAREACSMTFRNENPTNSENPBSERCBRNSENPRSEINCPRSENMRSEOPREHLDNLDNCBETFXLETCXLNCBEALPRPPITXLYPPIYNCBEMGKSEWCDRCBRNSEPRSEWLDRCBRNSENTSGNENTXSECRSLECLTRSEWLDNCBEALWLDNCBETSMELRSERLSEVRGLSNEASNWLDNCBENOPFSENLSRENCBENTEGDDMNSENCURERCBRNETENETFRNENCBRTSENCBEINCFLRSEPRSEONDE71NCBECDNSEPRSEONSDE74NCBEPRTSEPRSEONREDE75NCBETFNRCMSPSOLEMRDELUSETOTEWLDNWLDNCBE194WLDSNCBETRFXLMCCORMICKPAGE1JPGWHATISTHECONTENTOFPAGE2THEUNENCRYPTEDCONTENTOFPAGE1REMAINSUNKNOWNBUTHEREISATRANSCRIPTPAGE2ALPNTEGLSESEERTEVLSEMTSECTSEWSEFRTSEPNRTRSEONDRSEWLDNCBENWLDXLRCMSPNEWLDSTSMEXLDVLMT6TUNSENCBEXLMUNSARSTENMUNARSEKLSELRSTETRSETRSEMKSEMRSESAEGNSESENMRSENMNRCBRNSEPTEZPTEWSRCBRMSE36MLSE74SPRKSE29KENOSOLE173RTRSE35GLECLGSEVUNVTREDKRSEPSESHLE651MTCSEHTLSENCVTCTRSNMRE9984SZUNEPLSENCRSEAOLTSENSRSENBSENSREONSEPVTSEWLDNCBE3XORLBNMSENRSEINZNTRLERCBRNSENTSRCRSNELSPNSENGSPSEMKSERBSENCBEAVXLRHMCRENMRENCBE12MUNDPLSEDWM4MILXDRLX", // ricky mccormick codes
			"PDGHMPDGPHMWTLPWOOFPGIMUISGP", // http://scienceblogs.de/klausis-krypto-kolumne/2018/12/10/an-unsolved-message-found-at-the-university-of-texas/
			"PDGNHBOBVPNSNHANAOENCNANHPNCPNDNUOCPNNPNAPNMSMDKBMLPOWPNAPNEENTGBTMLSHHSSSTMALHFFTMOFPANSTPNIOOIPNTPNROANTRSANTOTTAPODPLUADONNOANPBOOLLPKUASASIMABPAGAPMOMTAVMAJAMMSSSLLABAPMUTTCTCCATFGSHHILANRNPHTAOLPPSTWAAHASIOLAIOWPSTWALOJTHTPATPPSTWAGAOKWOCWASAPSTWADVGWAIIRARBOSAOPSTWAHPPAAETWWPSTWAHTPMAESTCWEPSTAOUCOOTIOWHAMPAPSTTPDNHAPSTWAAHASPAGAPSTWAAIEHAPSTWARIEHPSTTPAIDNBTVJEOAOUPAGAPSTTOUWWTLWATOUWSLWDSWGOBSPSTWAHSAHVASASBTATWARHSFAOTPSTWAHBPITNRSTNOTROUHTTBPMPSTVCADAFLAFESJMPLGBGBMBATROURSSASFOHPPSTWAGAVWWOAPAGAPSTEIFFAWAUTYAGFPFGTYAGFLTMPAATTYAGFEATYAGFEAAAOFWAIHHBTNTKCTWBDOEAIIIHFUTDODBAFUOTAWFTWTAUALUNITBDUFEFTITKTPATGFAEAOFWAIHHBTNTKCTWBDOEAIIIHFUTDODBAFUOTAWFTWTAUALUNITBDUFEFTITKTPATGFAEA", // https://ask.metafilter.com/255675/Decoding-cancer-addled-ramblings
			"YMBFPTYSYSNAOSBYAACPTMYSNEMTMOIYJGTEBYNIAJAPSNDETMYLGAIDISYMIHFBYTFAAWOTYCMCHNEFTYIHSAWOYYHNEAWOWIWLTDIHAJBSASTFYTMMFDOETTDITWYGMTIPDTTOFYANRSITLOACYANWYAJAHCTDSIHNDONIPEICNCOACOHMOOOTYPMDISWTOKIEYWOPRAAOTWFFFYOTYOMIHLYDWICSTOTIHHNCFBYHNLAOWALYCWOWTDSOLIOMWWWYNTLTYMYFYLNAWNMWSSINDIDCWGOOBOYAYOFWGYKODLIAALACNSUFMSIATOJBAHTYIANAWJSWDETOMYHABEODIASMGWIHDTTOTYHNDTIAPASYOAYDTSWBAAFYFNSMTMOTOHYANSYJDWMABTDEKMMSAIHOMLTPYANWADNWTSWISAIHNGTSTBFANTDWLIAAYYAARYNTTMIFLYTIABYWYEWGOTAYNTTMWITACOSSOCAYSLIYATMTTOTTNAAMDOTWYNSOETBRNBAHTMAYSIDKINADRHBOFTWTTFBWSYJSBYKIWBMATOGTDALAYANIATADWCBYDDLOAHWIAFHWSTAYSTFOHSELTODTNOAMAHMAHWCUALFMTFYMYNWTGTOGAHTCHASODATRISWNBWISIAAOHBBYCSTSIPYAAFITIYWSTASIABBAAGIWSYCIATOYSIAWYDBHSAIIMYMMFUIOIYRYCHCLEFAOWNHOTSADIHIWYCTTACBIWLFYTTIIMLPIDOEGSSIIMATCIDLSDMSFHATHTLTIFTNWIATOYLMJOMATLOIMFJMWYTOOMDYLDBLPCEAT" // cipher text in library book.  uppercased and with punctuation removed
	};
//	public static String  cipherTamadShud = "MRGOABABDMLIAOIMTBIMPANETPMLIABOAIAQCITTMTSAMSTGAB";
//	// alternate version: some Ms look like Ws.
//	public static String cipherTamadShud2 = "WRGOABABDMLIAOIWTBIMPANETPMLIABOAIAQCITTMTSAMSTGAB";
//	public static String cipherRicky = "MNDMKNEARSENSMKNAREACSMTFRNENPTNSENPBSERCBRNSENPRSEINCPRSENMRSEOPREHLDNLDNCBETFXLETCXLNCBEALPRPPITXLYPPIYNCBEMGKSEWCDRCBRNSEPRSEWLDRCBRNSENTSGNENTXSECRSLECLTRSEWLDNCBEALWLDNCBETSMELRSERLSEVRGLSNEASNWLDNCBENOPFSENLSRENCBENTEGDDMNSENCURERCBRNETENETFRNENCBRTSENCBEINCFLRSEPRSEONDE71NCBECDNSEPRSEONSDE74NCBEPRTSEPRSEONREDE75NCBETFNRCMSPSOLEMRDELUSETOTEWLDNWLDNCBE194WLDSNCBETRFXLMCCORMICKPAGE1JPGWHATISTHECONTENTOFPAGE2THEUNENCRYPTEDCONTENTOFPAGE1REMAINSUNKNOWNBUTHEREISATRANSCRIPTPAGE2ALPNTEGLSESEERTEVLSEMTSECTSEWSEFRTSEPNRTRSEONDRSEWLDNCBENWLDXLRCMSPNEWLDSTSMEXLDVLMT6TUNSENCBEXLMUNSARSTENMUNARSEKLSELRSTETRSETRSEMKSEMRSESAEGNSESENMRSENMNRCBRNSEPTEZPTEWSRCBRMSE36MLSE74SPRKSE29KENOSOLE173RTRSE35GLECLGSEVUNVTREDKRSEPSESHLE651MTCSEHTLSENCVTCTRSNMRE9984SZUNEPLSENCRSEAOLTSENSRSENBSENSREONSEPVTSEWLDNCBE3XORLBNMSENRSEINZNTRLERCBRNSENTSRCRSNELSPNSENGSPSEMKSERBSENCBEAVXLRHMCRENMRENCBE12MUNDPLSEDWM4MILXDRLX";
	
	public static int MAX_QUEUE_SIZE = 10000;
	
	public static void searchCorpus() {
		Map<Integer, TreeSet<FirstLetterSequenceSearchBean>> queues = new HashMap<Integer, TreeSet<FirstLetterSequenceSearchBean>>();
		Map<Integer, FirstLetterSequenceSearchBean> bests = new HashMap<Integer, FirstLetterSequenceSearchBean>();
		// map the cipher strings to how often they match word ngrams
//		Map<Integer, Map<String, Long>> matches = new HashMap<Integer, Map<String, Long>>(); 
		Map<Integer, Set<String>> seenMap = new HashMap<Integer, Set<String>>();
		WordFrequencies.init();
		System.out.println("====== starting search ======");
//		List<String> fileList = FileUtil.loadFrom("/Volumes/Smeggabytes/projects/zodiac/docs/all-files.txt");
		List<String> fileList = FileUtil.loadFrom("/Volumes/Smeggabytes/projects/zodiac/docs/one-file.txt");

		// shuffle the list of files
		Random rand = new Random();
		for (int i=fileList.size()-1; i>0; i--) {
			// swap with an element in [0,i-1]
			int j = rand.nextInt(i);
			String iVal = fileList.get(i);
			String jVal = fileList.get(j);
			fileList.set(i, jVal);
			fileList.set(j, iVal);
		}
		
		long bytes = 0;
		long countFiles = 0;
		for (String file : fileList) {
			if (!file.endsWith(".zip")) continue;
			countFiles++;
			if (countFiles % 1000 == 0) {
				System.out.println("Processed " + countFiles + " files.  Dumping results...");
				dump(queues);
			}
			File f = new File(file);
			Long length = f.length();
//			System.out.println("File " + countFiles + ": " + file + " length " + length);
			Unzip.SHOW_INFO = false;
			String contents = Unzip.read(f);
			bytes += contents.length();
			String[] tokens = FileUtil.tokenizeAndConvert(contents);
			for (int which=0; which<ciphers.length; which++) {
				Set<String> seen = seenMap.get(which);
				if (seen == null) seen = new HashSet<String>();
				seenMap.put(which, seen);
				TreeSet<FirstLetterSequenceSearchBean> queue = queues.get(which);
				if (queue == null) queue = new TreeSet<FirstLetterSequenceSearchBean>();
				queues.put(which, queue);
//				Map<String, Long> matchesMap = matches.get(which);
//				if (matchesMap == null) matchesMap = new HashMap<String, Long>();
//				matches.put(which, matchesMap);
				String cipher = ciphers[which];
				for (int i=0; i<tokens.length; i++) {
					StringBuffer lineWithSpaces = new StringBuffer();
					StringBuffer firstLetters = new StringBuffer();
					for (int j=i; j<tokens.length; j++) {
						String token = tokens[j];
						if (token.length() < 1) continue;
						lineWithSpaces.append(token).append(" ");
						firstLetters.append(token.charAt(0));
						if (cipher.contains(firstLetters)) {
//							String fls = firstLetters.toString();
//							Long matchesCount = matchesMap.get(fls);
//							if (matchesCount == null) matchesCount = 0l;
//							matchesCount++;
//							matchesMap.put(fls, matchesCount);
							if (seen.contains(lineWithSpaces.toString())) continue;
							FirstLetterSequenceSearchBean bean = new FirstLetterSequenceSearchBean();
							bean.letters = new StringBuffer(firstLetters);
							bean.ngram = new StringBuffer(lineWithSpaces);
							bean.file = f.getName();
							bean.score = WordFrequencies.scoreLog(lineWithSpaces.toString());
							bean.which = which;
							bean.pos = cipher.indexOf(firstLetters.toString());
							queue.add(bean);
//							System.out.println("Added bean: " + bean);
							seen.add(lineWithSpaces.toString());
//							System.out.println("seen add [" + lineWithSpaces.toString() + "] " + seen.size() + " " + queue.size());
//							boolean print = true;
							if (queue.size() > MAX_QUEUE_SIZE) {
								FirstLetterSequenceSearchBean removed = queue.last();
								queue.remove(removed);
								seen.remove(removed.ngram.toString());
//								System.out.println("seen remove [" + removed.ngram.toString() + "] " + seen.size() + " " + queue.size());
//								print = !removed.equals(bean);
							}
							if (queue.size() > MAX_QUEUE_SIZE) {
								System.err.println("QUEUE SIZE: " + queue.size());
								System.exit(-1);
							}
							if (seen.size() > MAX_QUEUE_SIZE) {
								System.err.println("SEEN SIZE: " + seen.size());
								System.exit(-1);
							}
//							if (print) 
//								System.out.println(bean);
							
							// do we have a new best at the head of the queue?
							boolean printBest = false;
							FirstLetterSequenceSearchBean best = bests.get(which);
							if (best == null || bean.score > best.score) {
								best = bean;
								printBest = true;
							} 
							if (printBest) {
								System.out.println(which + " NEW BEST: " + bean);
							}
							bests.put(which,  best);
							
						} else break;
					}
				}
			}
		}
		dump(queues);
//		System.out.println("Matches map: ");
//		System.out.println(matches);
		System.out.println("TOTAL BYTES: " + bytes);
	}
	public static void dump(Map<Integer, TreeSet<FirstLetterSequenceSearchBean>> queues) {
		for (Integer which : queues.keySet()) {
			System.out.println("========== DUMPING RESULTS FOR CIPHER #" + which + " =============");
			for (FirstLetterSequenceSearchBean bean : queues.get(which)) {
				System.out.println(bean);
			}
		}
	}
	static void check(TreeSet<FirstLetterSequenceSearchBean> queue) {
		FirstLetterSequenceSearchBean previous = null;
		for (FirstLetterSequenceSearchBean current : queue) {
			if (previous != null) {
				int lenP = previous.length();
				int lenC = current.length();
				double scoreP = previous.score;
				double scoreC = current.score;
				if (lenC > lenP) {
					System.out.println("BAD: " + queue);
					System.exit(-1);;
				}
				if (lenC == lenP && scoreC > scoreP) {
					System.out.println("BAD: " + queue);
					System.exit(-1);;
				}
				
			}
			previous = current;
		}
		
	}
	
	/**
	 * the library book cipher has letters circled within words. one sequence,
	 * IAAYYA, appears as circled letters and in the cipher text.
	 * http://www.zodiackillersite.com/download/file.php?id=8265
	 * 
	 * search the book for all sequences (of letters within words) that match the
	 * given letter sequence
	 * 
	 **/

	public static void findGivenSequence(String sequence) {
		StringBuffer sb = FileUtil.loadSBFrom("/Volumes/Smeggabytes/projects/zodiac/docs/Robin_Hobb_Assassins_Apprentice.txt");
		sb = new StringBuffer(sb.toString().toLowerCase());
		sequence = sequence.toLowerCase();
		
		int best = Integer.MAX_VALUE;
		int max = 1000;
		
		for (int i=0; i<sb.length(); i++) {
			if (!(sb.charAt(i)==sequence.charAt(0))) continue;
			int which = 0;
			String match = "";
			for (int j=i; j<sb.length(); j++) {
				char ch = sb.charAt(j);
				if (ch == sequence.charAt(which)) {
					which++;
					match += (""+ch).toUpperCase();
				} else {
					if (ch > 31)
						match += ch;
				}
				if (match.length() > max)
					break;
				if (which == sequence.length()) {
					if (match.length() < best) {
						best = match.length();
						System.out.println(match.length() + " " + match);
					}
					break;
				}
			}
		}
		
	}
	/** same as findGivenSequence but split into tokens to more easily deal with individual words */
	public static void findGivenSequence2(String sequence) {
		StringBuffer sb = FileUtil.loadSBFrom("/Volumes/Smeggabytes/projects/zodiac/docs/Robin_Hobb_Assassins_Apprentice.txt");
		sb = new StringBuffer(sb.toString().toLowerCase());
		sequence = sequence.toLowerCase();
		
		String[] split = sb.toString().split("((?<=[^A-Za-z'])|(?=[^A-Za-z']))");
//		for (int i=0; i<1000; i++)
//		System.out.println(split[i]);

		int max = 1000;

		for (int i=0; i<split.length; i++) {
			String positions = "";
			int which = 0;
			String match = "";
			boolean firstHit = false;
			int firstWhere = i;
			for (int j=i; j<split.length; j++) {
				String token = split[j];
				char ch = sequence.charAt(which);
				if (token.contains(""+ch)) {
					which++;
					int where = token.indexOf(ch);
					StringBuffer newWord = new StringBuffer(token);
					newWord.setCharAt(where, (""+ch).toUpperCase().charAt(0));
					match += newWord.toString();
					if (positions.length() > 0)
						positions += " ";
					positions += where;
					if (!firstHit) firstWhere = j;
					firstHit = true;
				} else {
					if (firstHit) match += token;
				}
				if (which == sequence.length()) {
					System.out.println(match.length() + " " + match + ", " + positions + ", " + check(positions, 5));
					i = j;
					break;
				}
			}
		}
		
	}
	
	static String check(String positions, int num) {
		String[] split = positions.split(" ");
		for (int i=0; i<num; i++) {
			int j = split.length-num+i;
			if (!split[i].equals(split[j])) return "FALSE";
		}
		return "TRUE";
	}
	
	/** find all words of the given length where each letter appears in the corresponding given words */
	public static void findInWords(int length, String[] words) {
		WordFrequencies.init();
		
		for (int start = 0; start < words.length - length + 1; start++) {
			String[] slice = Arrays.copyOfRange(words, start, start+length);
			for (String word : WordFrequencies.map.keySet()) {
				if (word.length() != length) continue;
				
				boolean found = true;
				for (int i=0; i<length; i++) {
					char ch = word.charAt(i);
					if (!slice[i].contains(""+ch)) {
						found = false;
						break;
					}
				}
				if (found) {
					System.out.println(WordFrequencies.percentile(word) + " " + word + " " + format(slice, word));
				}
			}
		}
		
	}
	
	public static String format(String[] words, String word) {
		String result = "";
		
		int which = 0;
		for (String w : words) {
			StringBuffer toLower = new StringBuffer(w.toLowerCase());
			char letter = word.toLowerCase().charAt(which);
			toLower.setCharAt(toLower.indexOf(""+letter), word.charAt(which));
			result += toLower + " ";
			which++;
		}
		return result;
	}
	
	/** smokie:  "I also thought, if someone randomly circled 7 symbols from a chunk of text the same size as the chunk of text from I to A in IAAYYAA, how many times would he have to do it to get 7 consecutive symbols in the cryptogram?" */
	public static void testRandomCircledLetters(int n, int size, String cipher) {
		Random rand = new Random();
		StringBuffer sb = FileUtil.loadSBFrom("/Volumes/Smeggabytes/projects/zodiac/docs/Robin_Hobb_Assassins_Apprentice.txt");
		String[] tokens = FileUtil.tokenizeAndConvert(sb.toString());
		int hits = 0;
		Map<String, Integer> counts = new HashMap<String, Integer>(); 
		for (long i=0; i<Long.MAX_VALUE; i++) {
			String[] sample = sampleFrom(tokens, size);
			// select n words at random
			TreeSet<Integer> whichWords = new TreeSet<Integer>();
			while (whichWords.size() < n) {
				whichWords.add(rand.nextInt(sample.length));
			}
			String words = "";
			// for each word, select a random letter
			String sequence = "";
			for (Integer which : whichWords) {
				String word = sample[which];
				words += word + " ";
				sequence += word.charAt(rand.nextInt(word.length()));
			}
			// does the sequence of letters occur in the cipher text?
			if (cipher.contains(sequence)) {
				hits++;
				Integer count = counts.get(sequence);
				if (count == null) count = 0;
				count++;
				counts.put(sequence, count);
				System.out.println(i + " " + whichWords + " " + words + " " + sequence + " " + Arrays.toString(sample));
			}
			if (i % 100000000 == 0) {
				System.out.println("After " + i + " trials: " + hits + " hits.  Counts: " + counts);
				
			}
//			System.out.println(whichWords + " " + words + " " + sequence + " " + Arrays.toString(sample));
		}
		System.out.println("Hits: " + hits);
	}
	
	public static String[] sampleFrom(String[] tokens, int size) {
		Random rand = new Random();
		List<String> list = new ArrayList<String>();
		while (true) {
			list.clear();
			int length = 0;
			int index = rand.nextInt(tokens.length); 
			while (length < size && index < tokens.length) {
				String token = tokens[index++];
				length += token.length();
				if (length > size) {
					int diff = length - size;
					token = token.substring(0, token.length() - diff);
					length = size;
				}
				list.add(token);
			}
			if (length == size) {
				return list.toArray(new String[0]);
			}
		}
	}

	public static void main(String[] args) {
		// searchCorpus();
		
		
		
//		findGivenSequence("IAAYYAARYNTTMIFLYTIA" + "BYWYEWGOTAYNTTMWITA" + "COSSOCAYSLIYATMTTOTT" + "NAAMDOTWYNSOETBRNBAH");
//		findGivenSequence("EKMMSAIHO,M,LTPYAN");
//		findGivenSequence("OKIEYWOP,R,AAOTWFFFYOTYO");
//		findGivenSequence("YNTTMIFLYTIABYWYEWGOTAYNTTM");
//		findGivenSequence("IAAYYAARYNTTMIFLYTIA");
//		findGivenSequence2("YNTTMIFLYTIABYWYEWGOTAYNTTM");
//		findGivenSequence2("SAIHO,M,LTPYANWADNWTSWISAIH");
//		findGivenSequence2("YNTTMYNTTM");
		String cipher = "YBMFPTYSYSNAOSBYAACPTMYSNEMTMOIYJGTEBYNIAJAPSNDETMYLGAIDISYMIHFBYTFAAWOTYCMCHNEFTYIHSAWOYYHNEAWOWIWLTDIHAJBSASTFYTMMFDOETTDITWYGMTIPDTTOFYANRSITLOACYANWYAJAHCTDSIHNDONIPEICNCOACOHMOOOTYPMDISWTOKIEYWOPRAAOTWFFFYOTYOMIHLYDWICSTOTIHHNCFBYHNLAOWALYCWOWTDSOLIOMWWWYNTLTYMYFYLNAWNMWSSINDIDCWGOOBOYAYOFWGYKODLIAALACNSUFMSIATOJBAHTYIANAWJSWDETOMYHABEODIASMGWIHDTTOTYHNDTIAPASYOAYDTSWBAAFYFNSMTMOTOHYANSYJDWMABTDEKMMSAIHOMLTPYANWADNWTSWISAIHNGTSTBFANTDWLIAAYYAARYNTTMIFLYTIABYWYEWGOTAYNTTMWITACOSSOCAYSLIYATMTTOTTNAAMDOTWYNSOETBRNBAHTMAYSIDKINADRHBOFTWTTFBWSYJSBYKIWBMATOGTDALAYANIATADWCBYDDLOAHWIAFHWSTAYSTFOHSELTODTNOAMAHMAHWCUALFMTFYMYNWTGTOGAHTCHASODATRISWNBWISIAAOHBBYCSTSIPYAAFITIYWSTASIABBAAGIWSYCIATOYSIAWYDBHSAIIMYMMFUIOIYRYCHCLEFAOWNHOTSADIHIWYCTTACBIWLFYTTIIMLPIDOEGSSIIMATCIDLSDMSFHATHTLTIFTNWIATOYLMJOMATLOIMFJMWYTOOMDYLDBLPCEAT";
		testRandomCircledLetters(7, 97, cipher);
//		for (int length = 10; length >= 5; length--) {
//			System.out.println("============ FOR LENGTH " + length + ": ============");
//			findInWords(length, new String[] {
//					"CHIVALRYS", "HAWK", "SAW", "SADLY", "OVERJOYED", "AND", "SUFFUSED", "COULD", "CONVEY", "THICKSKULLED"
//			});
//			
//		}
//		findInWords(4, new String[] {
//				"SUFFUSED", "COULD", "CONVEY", "THICKSKULLED"
//		});
	}
}
