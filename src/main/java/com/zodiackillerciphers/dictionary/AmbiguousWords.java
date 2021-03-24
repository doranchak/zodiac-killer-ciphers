package com.zodiackillerciphers.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.anagram.Anagrams;

public class AmbiguousWords {

	/** find the most ambiguous words */
	public static void search() {
		WordFrequencies.init();

		Map<String, List<String>> words = new HashMap<String, List<String>>();
		System.out.println(WordFrequencies.map.keySet().size());
		int count = 0;
		for (String word : WordFrequencies.map.keySet()) {
			count++;
			if (count % 1000 == 0)
				System.out.println(count);
			if (!WordFrequencies.isAlpha(word))
				continue;
			if (word.length() < 5)
				continue;

			for (int i = 0; i < word.length(); i++) {
				StringBuffer sb = new StringBuffer(word);
				sb.deleteCharAt(i);
				String key = sb.toString();
				List<String> val = words.get(key);
				if (val == null)
					val = new ArrayList<String>();
				val.add(word);
				words.put(key, val);
			}
			for (int i = 0; i < word.length(); i++) {
				for (int j = i + 1; j < word.length(); j++) {
					StringBuffer sb = new StringBuffer(word);
					sb.deleteCharAt(j);
					sb.deleteCharAt(i);
					String key = sb.toString();
					List<String> val = words.get(key);
					if (val == null)
						val = new ArrayList<String>();
					val.add(word);
					words.put(key, val);
				}
			}
		}

		for (String key : words.keySet()) {
			List<String> val = words.get(key);
			Collections.sort(val, new WordFrequencyComparator());
			if (val.size() > 10) {
				System.out.println(val.size() + ", " + key + ", " + val);
			}
		}

		System.out.println(words.get("HRD"));
	}

	public static void search2(String str, int L) {
		WordFrequencies.init();

		Map<String, List<String>> words = new HashMap<String, List<String>>();
		System.out.println(WordFrequencies.map.keySet().size());
		int count = 0;

		for (int i = 0; i < str.length() - L + 1; i++) {
			String sub = str.substring(i, i + L);
			
			int s1 = i;
			int s2 = i+L-1;
			
			int s3 = Math.max(0, i-10);
			int s4 = Math.max(0, i-1);
			
			int s5 = Math.min(str.length()-1, i+L);
			int s6 = Math.min(str.length()-1, i+L+9);
			
			String chunk = " [b]" + sub + "[/b] ";
			if (s3 > 0 || s4 > 0)
				chunk = str.substring(s3, s4+1) + chunk;
			if (s5 < str.length() || s6 < str.length())
				chunk = chunk + str.substring(s5, s6+1);
			
			
			List<String> wordsAnagrams = null;
			List<String> wordsLCS = new ArrayList<String>();
			
			for (String word : WordFrequencies.map.keySet()) {
				count++;
				//if (count % 1000 == 0)
				//System.out.println(count);
				if (!WordFrequencies.isAlpha(word))
					continue;
				if (word.length() < L + 1)
					continue;
				if (word.length() > L + 2)
					continue;

				LcsString seq = new LcsString(sub, word);
				
				//boolean b1 = anagrams.size() > 0;
				boolean b2 = (seq.getLcsLength() == L && seq.onlyVowelsAdded());
				if (b2) {
					List<String> val = words.get(sub);
					if (val == null)
						val = new ArrayList<String>();
					val.add(word);
					words.put(sub, val);

					wordsLCS.add(word);
					
				}
			}
			wordsAnagrams = Anagrams.anagramsFor(sub, L);
			
			Collections.sort(wordsLCS, new WordFrequencyComparator());
			Collections.sort(wordsAnagrams, new WordFrequencyComparator());
			
			
			if (wordsLCS.size() > 0 || wordsAnagrams.size() > 0) {
				chunk = chunk + ":";
				if (wordsLCS.size() > 0) chunk += " Similar words: " + wordsLCS;
				if (wordsAnagrams.size() > 0) chunk += " Anagrams: " + wordsAnagrams;
				System.out.println(chunk);
			}
			
		}

		/*
		for (String key : words.keySet()) {
			List<String> val = words.get(key);
			Collections.sort(val, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					return Integer.compare(WordFrequencies.freq(o2),
							WordFrequencies.freq(o1));
				}

			});
			System.out.println(val.size() + ", " + key + ", " + val);
		}*/

	}

	public static <E> List<E> LongestCommonSubsequence(E[] s1, E[] s2) {
		int[][] num = new int[s1.length + 1][s2.length + 1]; // 2D array,
																// initialized
																// to 0

		// Actual algorithm
		for (int i = 1; i <= s1.length; i++)
			for (int j = 1; j <= s2.length; j++)
				if (s1[i - 1].equals(s2[j - 1]))
					num[i][j] = 1 + num[i - 1][j - 1];
				else
					num[i][j] = Math.max(num[i - 1][j], num[i][j - 1]);

		System.out.println("length of LCS = " + num[s1.length][s2.length]);

		int s1position = s1.length, s2position = s2.length;
		List<E> result = new LinkedList<E>();

		while (s1position != 0 && s2position != 0) {
			if (s1[s1position - 1].equals(s2[s2position - 1])) {
				result.add(s1[s1position - 1]);
				s1position--;
				s2position--;
			} else if (num[s1position][s2position - 1] >= num[s1position][s2position]) {
				s2position--;
			} else {
				s1position--;
			}
		}
		Collections.reverse(result);
		return result;
	}

	public static void main(String[] args) {
		// search();
		//search2("HRDNSNPRHRBURCYTOGMEVMENMNAPAGNEUITNEDELONRTNHITPOFSTTCANDSUODINENSUSDAAHDATSDCAFRHAONMOSLIAMNNSPTUSANAAAEICOTRICRRTEAAPICNAIOAIMECSDMGSNTCAAEAEEIRUCTNAALIOARAFMDNFONPLONTHEATPAUMAIETCFONNAFMACIDUPIENTCOMEAALFTRHAUHEIANEWIFOSSAHUACSNIOOPAAESEOOCTTRNDCMMGNUTINAECAATWPUALRIOPMAANNSCRGDMONNIFDUEHDSSSEIALNCETDIPRFAHSEONESTANETOSOHRATECMDNOMTD", 4);
		for (int L=8; L>3; L--) {
			//search2("DFTIUSRETIOIPATGRETCSRDAHLRERNTOLUAOESTITLULOECORLONEHGCATMALEPSMTTLDREULTISENARGSNGGREIDSNTSEASMINLOMEFLTRNIEIISGUNRTEPSAGHMEOTHATDPATERCIRDUMESIRNHATTEHMNSOUDESITRNAMIEPHRGCUCEISTIVSHAELLCUNLTESAADILIEAANRRGTONEMMFTURSTEINRVDUEPGSADMTETLNGFOMATETSHOUICOLELLISPTNIEROSDGHCLEIATILRTIERALACGRIENMOTAMROM", L);
			search2("UOITLEANODLNFUDDHUNNDAAORRRMHSTYIHTGEEHVNADHWWOYNTOBROOAOIANTIENEDDSSKLCONRLTHNAPINEIRHWPASEEFDSHRESNIHEZMMDOIODRFHHETNANADRLERCHFREREUHOOWDSSFIRAMOSFTHBDSANHTFICLHEETASHSLSIUEMFOTCOPBTNSNOARUYWAPHSSYFFYRNNTRACTWOYAEIEDOODROANSERUMCHOHTIDOWEMASMMHYTTUJIFHUOHHVSNTSNEWGWDEEAKOFIUYTTHHIOTRAIEWNEHDLADEEGPYVTTOSCEHMHENEHKPELDSINOPFDFRDTTASRSRQ", L);// purely random
		}
		//search2("CANYOUGUYSDOANANALYSISONTHISPASSAGEANDTELLMEIFTHISISONLYACOINCIDENCESECONDQUESTIONCANYOUHIDEASHORTMESSAGEINALARGESUBSTITUTIONCIPHERLETSSAYINTHEBEGINNINGORTHEENDOFALARGERPLAINTEXTEXAMPLEBELOWRAWPLAINTEXTSOLUTION");
		// List<String> list = LongestCommonSubsequence<String>("ABC","CDE");
	}
	
	/*
... [b]HRDN[/b] SNPRHRBURC...: [color=#008000]HORDEN, HARDEN, HARDON[/color]
...HR [b]DNSN[/b] PRHRBURCYT...: [color=#008000]DONSON, DANSON[/color]
...HRDN [b]SNPR[/b] HRBURCYTOG...: [color=#008000]SANPRO, SNIPER, SUNPRO[/color]
...HRDNSNPRH [b]RBUR[/b] CYTOGMEVME...: [color=#008000]ARBOUR[/color]
...GMEVMENMNA [b]PAGN[/b] EUITNEDELO...: [color=#008000]PAGAN, PAGANO[/color]
...MNAPAGNEUI [b]TNED[/b] ELONRTNHIT...: [color=#008000]TONED, TUNED[/color]
...GNEUITNEDE [b]LONR[/b] TNHITPOFST...: [color=#008000]LONER[/color]
...EUITNEDELO [b]NRTN[/b] HITPOFSTTC...: [color=#008000]NORTON[/color]
...ELONRTNHIT [b]POFS[/b] TTCANDSUOD...: [color=#008000]POOFS[/color]
...NRTNHITPOF [b]STTC[/b] ANDSUODINE...: [color=#008000]STATIC[/color]
...RTNHITPOFS [b]TTCA[/b] NDSUODINEN...: [color=#008000]ATTICA[/color]
...TNHITPOFST [b]TCAN[/b] DSUODINENS...: [color=#008000]TOUCAN[/color]
...NHITPOFSTT [b]CAND[/b] SUODINENSU...: [color=#008000]CANED, CANADA, CANDIA[/color]
...HITPOFSTTC [b]ANDS[/b] UODINENSUS...: [color=#008000]ANDES[/color]
...FSTTCANDSU [b]ODIN[/b] ENSUSDAAHD...: [color=#008000]IODINE[/color]
...STTCANDSUO [b]DINE[/b] NSUSDAAHDA...: [color=#008000]UDINE, IODINE, DIANE, DAINE[/color]
...TCANDSUODI [b]NENS[/b] USDAAHDATS...: [color=#008000]NEONS[/color]
...CANDSUODIN [b]ENSU[/b] SDAAHDATSD...: [color=#008000]ENSUE[/color]
...ANDSUODINE [b]NSUS[/b] DAAHDATSDC...: [color=#008000]ENSUES[/color]
...NDSUODINEN [b]SUSD[/b] AAHDATSDCA...: [color=#008000]SOUSED[/color]
...NENSUSDAAH [b]DATS[/b] DCAFRHAONM...: [color=#008000]DATES[/color]
...TSDCAFRHAO [b]NMOS[/b] LIAMNNSPTU...: [color=#008000]NOMOS, INMOS[/color]
...SDCAFRHAON [b]MOSL[/b] IAMNNSPTUS...: [color=#008000]MOSUL, MOSEL[/color]
...RHAONMOSLI [b]AMNN[/b] SPTUSANAAA...: [color=#008000]EAMONN, AMANN[/color]
...HAONMOSLIA [b]MNNS[/b] PTUSANAAAE...: [color=#008000]MINNIS, MUNNS, MINNS[/color]
...LIAMNNSPTU [b]SANA[/b] AAEICOTRIC...: [color=#008000]SAUNA, SANAA[/color]
...SANAAAEICO [b]TRIC[/b] RRTEAAPICN...: [color=#008000]TIRIAC, TRICIA, TRICE, TRICO[/color]
...AAAEICOTRI [b]CRRT[/b] EAAPICNAIO...: [color=#008000]CARROT[/color]
...OTRICRRTEA [b]APIC[/b] NAIOAIMECS...: [color=#008000]APIECE[/color]
...TRICRRTEAA [b]PICN[/b] AIOAIMECSD...: [color=#008000]PICON[/color]
...EAAPICNAIO [b]AIME[/b] CSDMGSNTCA...: [color=#008000]AIMEE[/color]
...AIOAIMECSD [b]MGSN[/b] TCAAEAEEIR...: [color=#008000]MEGSON[/color]
...AIMECSDMGS [b]NTCA[/b] AEAEEIRUCT...: [color=#008000]ANTICA[/color]
...CAAEAEEIRU [b]CTNA[/b] ALIOARAFMD...: [color=#008000]CATENA[/color]
...ARAFMDNFON [b]PLON[/b] THEATPAUMA...: [color=#008000]PELION[/color]
...FMDNFONPLO [b]NTHE[/b] ATPAUMAIET...: [color=#008000]ANTHEA, IANTHE, INTHE[/color]
...NFONPLONTH [b]EATP[/b] AUMAIETCFO...: [color=#008000]IEATP[/color]
...NPLONTHEAT [b]PAUM[/b] AIETCFONNA...: [color=#008000]PAUME[/color]
...TPAUMAIETC [b]FONN[/b] AFMACIDUPI...: [color=#008000]FIONN[/color]
...AFMACIDUPI [b]ENTC[/b] OMEAALFTRH...: [color=#008000]ENTICE[/color]
...MACIDUPIEN [b]TCOM[/b] EAALFTRHAU...: [color=#008000]TACOMA[/color]
...PIENTCOMEA [b]ALFT[/b] RHAUHEIANE...: [color=#008000]ALOFT[/color]
...IENTCOMEAA [b]LFTR[/b] HAUHEIANEW...: [color=#008000]LIFTER[/color]
...AUHEIANEWI [b]FOSS[/b] AHUACSNIOO...: [color=#008000]FOSSA, FOSSE[/color]
...WIFOSSAHUA [b]CSNI[/b] OOPAAESEOO...: [color=#008000]CUSANI[/color]
...IOOPAAESEO [b]OCTT[/b] RNDCMMGNUT...: [color=#008000]OCTET[/color]
...OOPAAESEOO [b]CTTR[/b] NDCMMGNUTI...: [color=#008000]CUTTER, COTTER[/color]
...OPAAESEOOC [b]TTRN[/b] DCMMGNUTIN...: [color=#008000]TITRON[/color]
...PAAESEOOCT [b]TRND[/b] CMMGNUTINA...: [color=#008000]TURNED, TREND[/color]
...PUALRIOPMA [b]ANNS[/b] CRGDMONNIF...: [color=#008000]ANNIS, ANNES, ANIONS[/color]
...LRIOPMAANN [b]SCRG[/b] DMONNIFDUE...: [color=#008000]SCARAG[/color]
...PMAANNSCRG [b]DMON[/b] NIFDUEHDSS...: [color=#008000]DEMON, DAEMON, DAMON[/color]
...MAANNSCRGD [b]MONN[/b] IFDUEHDSSS...: [color=#008000]EAMONN[/color]
...MONNIFDUEH [b]DSSS[/b] EIALNCETDI...: [color=#008000]DASSES[/color]
...ONNIFDUEHD [b]SSSE[/b] IALNCETDIP...: [color=#008000]SUISSE, SOUSSE[/color]
...DUEHDSSSEI [b]ALNC[/b] ETDIPRFAHS...: [color=#008000]ALNICO[/color]
...UEHDSSSEIA [b]LNCE[/b] TDIPRFAHSE...: [color=#008000]LANCE[/color]
...SSEIALNCET [b]DIPR[/b] FAHSEONEST...: [color=#008000]DIAPER[/color]
...IALNCETDIP [b]RFAH[/b] SEONESTANE...: [color=#008000]RAFAH[/color]
...DIPRFAHSEO [b]NEST[/b] ANETOSOHRA...: [color=#008000]NESTE, NESTA[/color]
...PRFAHSEONE [b]STAN[/b] ETOSOHRATE...: [color=#008000]STEANE, SATAN, STAIN[/color]
...AHSEONESTA [b]NETO[/b] SOHRATECMD...: [color=#008000]NIETO[/color]
...NESTANETOS [b]OHRA[/b] TECMDNOMTD...: [color=#008000]OHARA[/color]
...ESTANETOSO [b]HRAT[/b] ECMDNOMTD...: [color=#008000]HERATI[/color]
...STANETOSOH [b]RATE[/b] CMDNOMTD...: [color=#008000]IRATE, AERATE[/color]
...ANETOSOHRA [b]TECM[/b] DNOMTD...: [color=#008000]TECUM[/color]
...ETOSOHRATE [b]CMDN[/b] OMTD...: [color=#008000]CAMDEN[/color]	 */
}
