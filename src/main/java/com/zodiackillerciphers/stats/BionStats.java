package com.zodiackillerciphers.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.Stats;

/** BION's stats: http://bionsgadgets.appspot.com/gadget_forms/acarefstats.html */
public class BionStats {
	/** get array of iocs for cipher split up into chunks of size ceil(cipher.length/period).  basis for BION's MIC statistic  http://bionsgadgets.appspot.com/gadget_forms/acarefstats.html */
	public static double[] iocPeriodicBROKEN(String cipher, int period) {
		int width = cipher.length()/period;
//		if (cipher.length() % period > 0) 
//			period++;
		System.out.println("length: " + cipher.length() + " width: " + width);
		double[] ioc = new double[period];
		
		int count = 0;
		for (int i=0; i<period; i++) {
			String sub = cipher.substring(width*i, (int) Math.min(width*(i+1), cipher.length()));
			double val = Stats.ioc(sub);
			System.out.println(i + " " + sub + " " + val);
			ioc[i] = val;
		}
		return ioc;
	}
	

	// max IC for periods 1-15,
	public static double MIC(String cipher, int maxPeriod) {
		double max = 0;
		for (int p=1; p<=maxPeriod; p++) {
			max = Math.max(max, MICSub(cipher, p));
		}
//		System.out.println("max: " + max);
		return max;
	}
	
	public static double MICSub(String cipher, int period) {
		double mean = 0;
		int L = cipher.length();
		String[] chunks = new String[period];
		for (int i=0; i<chunks.length; i++) chunks[i] = "";
		
		for (int i=0; i<L; i++) {
			int j=i%period;
			chunks[j] += cipher.charAt(i);
		}
		for (String chunk : chunks) {
			double ioc = Stats.ioc(chunk);
//			System.out.println(" - p " + period + " " + chunk + " " + ioc);
			mean += ioc;
		}
//		System.out.println("mean " + mean);
		return mean/period;
	}
	
	public static double MKA(String cipher, int maxPeriod) {
		double max = 0;
		for (int period=1; period<=maxPeriod; period++) {
			double kappa = 0;
//			String debug = "";
			for (int i=0; i<cipher.length(); i++) {
				char c1 = cipher.charAt(i);
				char c2 = cipher.charAt((i+period)%cipher.length());
//				debug += c2;
				if (c1 == c2) kappa++;
			}
			kappa = kappa / cipher.length();
//			System.out.println(period + " " + kappa + " " + debug);
			max = Math.max(max, kappa);
		}
		return max;
	}
	
	public static double mean(double[] array) {
		double mean = 0;
		for (double val : array) {
			if (Double.isNaN((val))) continue;
			mean += val;
		}
		mean = mean / array.length;
		return mean;
	}

	/**
	 * digraphic index of coincidence for only the digraphs starting on
	 * even(odd)-numbered positions (even: 0, 2, 4, etc) (odd: 1, 3, 5, etc)
	 */
	public static double EDI(String s, boolean odd) {
		
		Map<String, Long> map = new HashMap<String, Long>();
		double sum = 0;
		int N = s.length()-1;
		int samples = 0;
		for (int i = odd ? 1 : 0; i < N; i += 2) {
			String ngram = s.substring(i,i+2);
			Long val = map.get(ngram); 
			if (val == null) val = 0l;
			val++;
			map.put(ngram, val);
			samples++;
		}
		Long val;
		for (String key : map.keySet()) {
			val = map.get(key);
			sum += val*(val-1);
			//System.out.println(key+","+val+","+sum);
		}
		//System.out.println(sum+","+sb.length());
		return ((double)sum)/((double)samples*(samples-1));
	}

	/** 1st element is square root of the percentage of 3 symbol repeats, see MJ2002 page 7. 
	 * 2nd element is ROD (percentage of odd-spaced repeats to all repeats) 
	 * 3rd element is RED (my variation: percentage of even-spaced repeats to all repeats) 
	 **/
	public static double[] LR_and_ROD(String cipher) {
		int[] nReps = new int[10];
		int L = cipher.length();
		int sumAll = 0;
		int sumOdd = 0;
		int sumEven = 0;
		for (int i=0; i<L-3; i++) {
			for (int j=i+1; j<L; j++) {
				int N=0;
				char c1 = cipher.charAt(i+N);
				char c2 = cipher.charAt(j+N);
				while (j+N < L-1 && c1 == c2) {
					N++;
//					System.out.println(i + " " + j + " " + N);
					c1 = cipher.charAt(i+N);
					c2 = cipher.charAt(j+N);
				}
				if (N>1) {
					if (N>10) {
						N=10;
					}
					nReps[N-1]++;
					sumAll++;
					if ((j-i)%2 == 1) sumOdd++;
					else sumEven++;
				}
			}
		}
		double rod = ((double)sumOdd)/sumAll;
		return new double[] {Math.sqrt(nReps[2])/L, rod};
	}
	
	public static void main(String[] args) {
//		iocPeriodic(Ciphers.Z408, 19);
//		iocPeriodic(Ciphers.Z340, 19);
		String[] ciphers = new String[] {
				Ciphers.Z408,
				Ciphers.Z340,
				"bmjwjpnxymjpjdymjxmnkyfuuqnjiytjfhmqjyyjwfkyjwfuuqdnslymnxkzshyntsymjwjxzqynxfszrgjwbmnhmrzxyymjsgjywfsxqfyjigfhpnsytfqjyyjwymjijhwduyntskzshyntsnx",
				"ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti",
				"COMPAREUNKNOWNCIPHERAGAINSTACACIPHERTYPESEXTENDEDDIRECTIONSTYPEORPASTEUNKNOWNCIPHERINTOCIPHERBOXCLICKIDTESTCOMPAREORCHOOSESPECIFICCIPHERTYPEFORCOMPARISONCIPHERBMJWJPNXYMJPJDYMJXMNKYFUUQNJIYTJFHMQJYYJWFKYJWFUUQDNSLYMNXKZSHYNTSYMJWJXZQYNXFSZRGJWBMNHMRZXYYMJSGJYWFSXQFYJIGFHPNSYTFQJYYJWYMJIJHWDUYNTSKZSHYNTSNXSHOWALLSTATSSHOWQADIALOGUESMAXIMUMPERIODTOTRYSTATSLENGTHICMICMKADICEDILRRODLDINOMORNICPHICMPICBDISDDCDDSSTDHASLYHASJYDBLNSERPNHASNHASDNHASNDIVNDIVYDIVNDIVNDIVYDIVYPSQNALDIBLDIPLDISLDIVLDIPTXRDIUSEDMAXIMUMPERIODOFWINNERISPATRISTOCRATWITHVOTESOUTOFVOTEDISTRIBUTIONPATRISTOCRATFRACMORSEPLAINTEXTCHECKERBOARDBAZERIESVIGENEREVARIANTSTATSABBREVATIONSLENLENGTHICINDEXOFCOINCIDENCETIMESMICMAXICFORPERIODSTIMESMKAMAXKAPPAFORPERIODSTIMESDICDIGRAPHICINDEXOFCOINCIDENCETIMESEDIDICFOREVENNUMBEREDPAIRSTIMESLRLONGREPEATPERCENTAGEOFSYMBOLREPEATSRODPERCENTAGEOFODDSPACEDREPEATSTOALLREPEATSLDIAVERAGEENGLISHLOGDIGRAPHSCORESDDAVERAGEENGLISHSINGLELETTERDIGRAPHDISCREPANCYSCOREEXTENDEDSTATSDIVLENGTHDIVISIBLEBYDIVLENGTHDIVISIBLEBYDIVLENGTHDIVISIBLEBYDIVLENGTHDIVISIBLEBYDIVLENGTHDIVISIBLEBYINTEGERBETWEENANDDIVLENGTHDIVISIBLEBYINTEGERBETWEENANDPSQLENGTHISPERFECTSQUAREHASLCIPHERHASLETTERSHASDCIPHERHASDIGITSHASHCPHERHASSYMBOLHASJCIPHERHASAJDBLCIPHERHASEVENLENGTHANDADOUBLEDLETTERATANEVENPOSITIONVIGENEREFAMILYSTATSALDILOGDIGRAPHSCOREFORAUTOKEYBLDILOGDIGRAPHSCOREFORBEAUFORTPLDILOGDIGRAPHSCOREFORPORTASLDILOGDIGRAPHSCOREFORSLIDEFAIRVLDILOGDIGRAPHSCOREFORVIGENEREOTHERSTATSNOMORNORMALORDERRDIREVERSELOGDIGRAPHSCOREPTXLOGDIGRAPHSCOREFORPORTAXNICMAXNICODEMUSICFORPERIODSPHICPHILLIPSICHASDIGITALCIPHERTHATINCLUDESAZEROBDIMAXBIFIDDICFORPERIODSCDDMAXCOLUMNARSDDSCOREFORPERIODSTOSSTDTHEMAXSTDSCOREFORSWAGMANPERIODSTOMPICMAXPROGRESSIVEKEYICFORPERIODSTOSERPPOSSIBLESERIATEDPLAYFAIRPERIODFROMTO",
				"ACCESSIBILITYLINKSSKIPTOMAINCONTENTACCESSIBILITYHELPACCESSIBILITYFEEDBACKGOOGLESAMPLESUBSTITUTIONCIPHERABOUTRESULTSSECONDSINCLUDINGRESULTSFORSIMPLESUBSTITUTIONCIPHERSEARCHONLYFORSAMPLESUBSTITUTIONCIPHERSEARCHRESULTSWHATISSUBSTITUTIONCIPHEREXAMPLEANEXAMPLEKEYISPLAINALPHABETABCDEFGHIJKLMNOPQRSTUVWXYZCIPHERALPHABETPHQGIUMEAYLNOFDXJKRCVSTZWBANEXAMPLEENCRYPTIONUSINGTHEABOVEKEYPLAINTEXTDEFENDTHEEASTWALLOFTHECASTLECIPHERTEXTGIUIFGCEIIPRCTPNNDUCEIQPRCNISIMPLESUBSTITUTIONCIPHERPRACTICALCRYPTOGRAPHYPRACTICALCRYPTOGRAPHYCOMCIPHERSSIMPLESUBSTITUTIONCIPHERSEARCHFORWHATISSUBSTITUTIONCIPHEREXAMPLEFEEDBACKABOUTFEATUREDSNIPPETSWEBRESULTSSIMPLESUBSTITUTIONCIPHERPRACTICALCRYPTOGRAPHYPRACTICALCRYPTOGRAPHYCOMCIPHERSTHESIMPLESUBSTITUTIONCIPHERISACIPHERTHATHASBEENINUSEFORMANYHEREISAQUICKEXAMPLEOFTHEENCRYPTIONANDDECRYPTIONSTEPSINVOLVEDWITHTHEPEOPLEALSOASKWHATISSUBSTITUTIONCIPHEREXAMPLEHOWDOYOUSOLVEASUBSTITUTIONCIPHERCANONESYMBOLCOULDBESUBSTITUTEDWITHAKEYUSINGSUBSTITUTIONCIPHERWHICHTYPEOFSUBSTITUTIONISCALLEDMONOALPHABETICSUBSTITUTIONCIPHERFEEDBACKWEBRESULTSCAESARCIPHERPRACTICALCRYPTOGRAPHYPRACTICALCRYPTOGRAPHYCOMCIPHERSITISATYPEOFSUBSTITUTIONCIPHERINWHICHEACHLETTERINTHEPLAINTEXTISSHIFTEDACERTAINNUMBEROFPLACESDOWNTHEALPHABETFOREXAMPLEWITHASHIFTOFAWOULDBEREPLACEDBYBBWOULDBECOMECANDSOONTHEMETHODISNAMEDAFTERJULIUSCAESARWHOAPPARENTLYUSEDITTOCOMMUNICATEWITHHISGENERALSPEOPLEALSOSEARCHFORCAESARCIPHERPYTHONCAESARCIPHERDECODERALBERTISDISKBRIEFLYDEFINETHECAESARCIPHERVIGENRECIPHERATBASHSUBSTITUTIONCIPHERWIKIPEDIAHTTPSENWIKIPEDIAORGWIKISUBSTITUTIONCIPHERINCRYPTOGRAPHYASUBSTITUTIONCIPHERISAMETHODOFENCRYPTINGBYWHICHUNITSOFPLAINTEXTARETHEBEALECIPHERSAREANOTHEREXAMPLEOFAHOMOPHONICCIPHERTHISISASTORYOFBURIEDTREASURETHATWASDESCRIBEDINBYUSEOFA‎SIMPLESUBSTITUTIONPOLYGRAPHICSUBSTITUTIONMECHANICALSUBSTITUTIONSUBSTITUTIONCIPHERANOVERVIEWSCIENCEDIRECTTOPICSHTTPSWWWSCIENCEDIRECTCOMTOPICSCOMPUTERSCIENCESUBSTITUTIONCIPFOREXAMPLEDATAENCRYPTIONSTANDARDDESAPPLIESCYCLESOFSUBSTITUTIONCIPHERSENCRYPTTHEPLAINTEXTBYSWAPPINGEACHLETTERORSYMBOLINTHEPLAINTEXTALPHABETICALSUBSTITUTIONCIPHERCRYPTOGRAMDECODERSOLVERDCODEHTTPSWWWDCODEFRMONOALPHABETICSUBSTITUTIONTOOLTODECRYPTMONOALPHABETICALSUBSTITUTIONANDFINDEACHLETTEROFASUBSTITUTEDMESSAGEWITHADERANGEDALPHABETMONOALPHABETICCIPHEREXAMPLEWITHTHISSUBSTITUTIONDCODEISENCRYPTEDASJAKJYANYDERANGEDALPHABETCANBESUBSTITUTIONCIPHERCORNELLUNIVERSITYPIMATHCORNELLEDUMECCRYPTOGRAPHYSUBSSUBSTITUTIONMARTHENAMESUBSTITUTIONCIPHERCOMESFROMTHEFACTTHATEACHLETTERTHATYOUWANTTOENCIPHERISSUBSTITUTEDBYANOTHERLETTERORSYMBOLBUTTHESIMPLESUBSTITUTIONCIPHERJNICHOLLORGJNICHOLLORGCRYPTANALYSISCIPHERSSIMPLESUBSTITUTIONJUMPTOEXAMPLEEXAMPLESUPPOSETHATYOUWANTEDTOENCIPHERTHETEXTTHEMOMENTECHOSAWNARCISSUSSHEWASINLOVESHEFOLLOWEDHIMLIKEACRYPTOGRAPHYTUTORIALSUBSTITUTIONCIPHERSTICOMHTTPSWWWTICOMCRYPTOTUTSUBSTITUTIONMOSTOFTHECIPHERSINTHETUTORIALARESUBSTITUTI"
		};
		for (String cipher : ciphers) {
			System.out.println("========= CIPHER: " + cipher);
			System.out.println("length: " + cipher.length());
			System.out.println("ioc: " + Stats.ioc(cipher));
			System.out.println("iocNgram 2: " + Stats.iocNgram(cipher, 2));
			System.out.println("MIC: " + MIC(cipher, 15));
			System.out.println("MKA: " + MKA(cipher, 15));
			System.out.println("EDI (even): " + EDI(cipher, false));
			System.out.println("EDI (odd): " + EDI(cipher, true));
			System.out.println("LR and ROD: " + Arrays.toString(LR_and_ROD(cipher)));
		}
//		iocPeriodic(cipher, 2);
//		System.out.println(Stats.ioc(cipher));
	}

}
