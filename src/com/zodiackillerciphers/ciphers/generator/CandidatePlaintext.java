package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.Utils;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotPair;
import com.zodiackillerciphers.pivots.PivotUtils;

/** represents a candidate plain text */
public class CandidatePlaintext {
	
	public static boolean DEBUG = false;
	/** grid width */
	public int width = 17;
	/** grid height */
	public int height = 20;
	/** plaintext derived from a section of the corpus */
	public String plaintext;
	/** index of the start token of the corpus from which the plaintext was derived */
	public int index;
	/** the tokens used to make this plaintext */
	public List<String> tokens;

	/** all pivots found in this plaintext */ 
	public List<Pivot> pivots;
	/** list of all pivot pairs.  we must enforce at least one pivot pair.
	 * TODO: does this sometimes find more than 2 pivots per orientation? 
	 **/
	public List<PivotPair> pivotPairs;
	
	/** candidate fold marks */
	public List<FoldMarks> foldMarks;
	
	/** filler starts at this position */
	public int fillerStart;
	/** filler ends at this position */
	public int fillerEnd;
	
	/** optional reference back to the CandidateKey */
	public CandidateKey candidateKey;
	
	/** list of all trigrams, one of which we need to enforce. */
	//List<Trigrams> trigrams;
	// (might be easier to just search for them again during fitness evaluation, and reject individuals
	// that no longer have the needed trigrams)
	
	/** first entry is the trigram that repeats in the same column.
	 * second entry is the trigram that intersects with one of the first trigrams.
	 */
	//public String[] trigrams;
	public Set<TrigramCandidatePair> trigrams;
	
	public List<CandidateConstraints> candidateConstraints; 

	public List<BoxCornerPair> boxCornerPairs;
	
	/** number of spelling errors purposefully introduced */
	public int errors;
	
	/** optional columnar transposition details */
	public ColumnarTranspositionInfo columnarTranspositionInfo;
	/** optional vigenere key */
	public String vigenereKey = null;
	
	public Integer scytalePeriod = null;
	
	public String originalPlaintext = null;
	
	public CandidatePlaintext() {
		this(-1, null);
	}
	public CandidatePlaintext(int index, String plaintext) {
		this.index = index;
		this.plaintext = plaintext;
		tokens = new ArrayList<String>();
		errors = 0;
		this.trigrams = new HashSet<TrigramCandidatePair>();
		boxCornerPairs = new ArrayList<BoxCornerPair>(); 
	}
	public CandidatePlaintext(String file) {
		this.candidateKey = candidateKey;
		List<String> lines = FileUtil.loadFrom(file);
		if (lines == null || lines.size() == 0) return;
		String line = lines.get(0);
		
		this.index = Integer.valueOf(fieldValueFor("index", line));
		this.plaintext = fieldValueFor("plaintext", line);
		String tokens = fieldValueFor("tokens", line);
		this.tokens = new ArrayList<String>();
		String[] split = tokens.split(",");
		for (String s : split) this.tokens.add(s.replaceAll(" ", ""));
		this.errors = Integer.valueOf(fieldValueFor("errors", line));
		
		System.out.println("Loaded " + file + ", criterialAll = " + this.criteriaAll());
	}
	
	public String fieldValueFor(String fieldName, String line) {
		line = line.replaceAll("\\[\\[", "[");
		line = line.replaceAll("\\]\\]", "]");

		String key = fieldName + " [";
		int index = line.indexOf(key);
		if (index < 0) return null;
		
		return line.substring(index + key.length()).split("\\]")[0];

	}
	/** returns true only if the plaintext, when arranged into a cipher block,
	 * contains at least two pivots with the same orientation.
	 */
	public boolean criteriaHasPivots() {
		
		pivots = PivotUtils.findPivots(plaintext, 4);
		
		pivotPairs = PivotUtils.pairsFrom(pivots);
		if (pivotPairs == null) return false;
		return pivotPairs.size() > 0;
	}
	
	public long pivotScore(Map<Character, Integer> countMap) {
		return pivotScore(countMap, false);
	}
	
	/** return the "best" score (lowest probability) among all discovered pivot pairs */
	public long pivotScore(Map<Character, Integer> countMap, boolean showSteps) {
		long best = Long.MAX_VALUE;
		if (pivots == null) return 0;
		for (PivotPair p : pivotPairs) {
			long score = p.score(countMap);
			if (showSteps) {
				System.out.println(score + " " + p);
			}
			if (score < best) {
				best = score;
			}
		}
		return best;
	}
	
	/** returns true if the plaintext has:
	 * 1) a trigram that repeats in the same column
	 * 2) the trigram in 1) intersects with another repeating trigram (in the way the trigram in the 340 does)
	 */
	public boolean criteriaNgrams() {
		
		trigrams = TrigramUtils.trigramsFrom(plaintext);
		if (trigrams == null || trigrams.isEmpty()) return false;
		return true;
	}
	
	/** looks for the "fold" marks (i.e., a line beginning and ending with the same symbol, allowing us
	 * to assign a hyphen for each to indicate a hypothesized fold mark)
	 */
	public boolean criteriaFold() {
		foldMarks = FoldMarks.foldMarksFrom(plaintext);
		return foldMarks.size() > 0;
	}
	
	/** return the candidate fold positions, as pairs */
	/*public List<int[]> folds() {
		List<int[]> result = new ArrayList<int[]>();
		if (plaintext.charAt(153) == plaintext.charAt(169)) result.add(new int[] {153, 169});
		if (plaintext.charAt(170) == plaintext.charAt(186)) result.add(new int[] {170, 186});
		return result;
	}*/
	
	/** looks for suitable "box corners" */
	public boolean criteriaBoxCorners() {
		BoxCornerUtils bcu = new BoxCornerUtils(width, plaintext);
		boxCornerPairs = bcu.findPairs();
		//System.out.println(list);
		return boxCornerPairs != null && !boxCornerPairs.isEmpty(); 
	}
	
	/** returns true if there is at least one trigram candidate pair that is compatible with a pair of pivots.
	 * if enforceEncodings is true, then we treat this as a cipher text, wherein we are trying to preserve specific assignments for the constraint features. */
	public boolean criteriaCompatible(boolean enforceEncodings) {
		Utils.debug("criteriaCompatible 1");
		if (trigrams == null || trigrams.isEmpty()) return false;
		Utils.debug("criteriaCompatible 2");
		//System.out.println("trigrams " + trigrams);
		candidateConstraints = new ArrayList<CandidateConstraints>(); 
		
		List<Pivot> pivots = PivotUtils.toList(pivotPairs);
		List<CandidateConstraints> ccs = CandidateConstraintsFactory.makeCandidateConstraints(trigrams, pivots, foldMarks, boxCornerPairs, enforceEncodings);
		if (ccs != null && !ccs.isEmpty()) {
			candidateConstraints.addAll(ccs);
		}
		return !candidateConstraints.isEmpty();
	}
	
	public static int posFrom(int row, int col) {
		int result = row*17 + col;
		if (result < 0 || result > 339) {
			System.out.println("row " + row + " col " + col + " pos " + result);
		}
		return result;
	}
	
	public boolean criteriaAll() { return criteriaAll(false); }
	public boolean criteriaAll(boolean enforceEncodings) {
		if (enforceEncodings) {
			// check the cache of hard-coded assignments, so we don't have to keep searching
			// for the same constraints over and over again.
			
		}
		
		if (candidateKey == null) candidateKey = new CandidateKey();
		/* force the computation */
		
		if (!criteriaNgrams()) {
			candidateKey.failMapIncrement("ngrams");
			//System.out.println("fail ngrams");
			return false;
		}
		//System.out.println("succeed ngrams");
		if (!criteriaHasPivots()) {
			candidateKey.failMapIncrement("pivots");
			//System.out.println("fail pivots");
			return false;
		}
		//System.out.println("succeed pivots");
		if (!criteriaFold()) {
			candidateKey.failMapIncrement("fold");
			//System.out.println("fail fold");
			return false;
		}
		//System.out.println("succeed fold");
		if (!criteriaBoxCorners()) {
			candidateKey.failMapIncrement("boxCorners");
			//System.out.println("fail box");
			return false;
		}
		//System.out.println("succeed box");
		if (!criteriaCompatible(enforceEncodings)) {
			/*
			 * check criteriaCompatible() last because it relies on side effects
			 * of the other criteria calculations
			 */
			candidateKey.failMapIncrement("criteriaCompatible("
					+ enforceEncodings + ")");
			//System.out.println("fail enforce");
			return false;
		}
		//System.out.println("succeed enforce");
		return true;
		
		/*boolean all = criteriaNgrams() && criteriaHasPivots() && criteriaFold()
				&& criteriaBoxCorners() && criteriaCompatible(enforceEncodings);*/ 
		// 
		/* clear out the data structures since they seem to fill up for certain plaintexts */
		//if (trigrams != null) this.trigrams.clear();
		//if (pivots != null) this.pivots.clear();
		//if (pivotPairs != null) this.pivotPairs.clear();
		//if (boxCornerPairs != null) this.boxCornerPairs.clear();
		
		//return all;
		
	}
	
	public String dump(Set<TrigramCandidatePair> trigrams) {
		if (trigrams == null) return null;
		String result = "";
		for (TrigramCandidatePair tri : trigrams) result += tri + " ";
		return result;
	}
	
	public String toString() { return toString(false); }
	public String toString(boolean showMore) {
		String line = "index [" + index + "] " + 
				"plaintext [" + plaintext + "] " + 
				"plaintextOriginal [" + originalPlaintext + "] " + 
				"tokens [" + tokens + "] " + 
				"errors [" + errors + "] ";
		if (showMore) {
			line += "trigrams [" + dump(trigrams) + "] ";
			line += "boxCorners [" + boxCornerPairs + "] "; 
			line += "foldMarks [" + foldMarks + "] "; 
			line += "pivotPairs [" + pivotPairs + "] ";
	
			if (pivotPairs != null) {
				for (PivotPair pair : pivotPairs) {
					line += "pair [" + pair + "] ";
				}
			}	
			if (trigrams != null) {
				line += "trigrams [" + trigrams + "] ";
			}
		}
		if (columnarTranspositionInfo != null) line += "columnarInfo [" + columnarTranspositionInfo.toString() + "]";
		return line;
	}
	
	/** return grid of html cells for this plaintext */
	public String[][] htmlCells() {
		String[][] cells = new String[height][width];
		
		
		for (int row=0; row<height; row++) {
			for (int col=0; col<width; col++) {
				cells[row][col] = ""+plaintext.charAt(posFrom(row, col));
			}
		}
		return cells;
	}
	
	/** return html style */
	public static String htmlStyle() {
		String html = "<style> " +
		".g { font-family: courier, monospace; font-size: 10pt;} <!-- grid --> " + 
		".h { color: white; background-color: black; font-weight: bold;} <!-- highlighted letter --> " +
		".t { font-weight: bold; color: #090; } <!-- title --> " + 
		"td { text-align: center; } " + 
		"</style>";
		return html;
	}
	
	/** return grid that highlights pivot pairs */
	/*
	public String htmlPivots() {
		if (pivotPairs == null || pivotPairs.isEmpty()) return "";
		String html = htmlStyle();
		String[][] grid = htmlCells();
		for (String key : pivotPairs.keySet()) {
			
			html += "<span style=\"t\">Pivot " + key + "</span>";
			List<Pivot> val = pivotPairs.get(key);
			for (Pivot pivot : val) {
				List<int[]> positions = pivot.positions;
				for (int[] rc : positions) {
					grid[rc[0]][rc[1]] = "<span class=\"h\">" + plaintext.charAt(posFrom(rc[0],rc[1]));
				}
			}
		}
		return html;
	}*/
	
	public static void test1() {
		String[] tests = new String[] {
				"THEAFTERNOONOFFRYDTOFAUGIWILLEGOONAKILLRAM__________________PAGEEFRYNIEGHTIUILLCUSEAROUNDALLWEEKENDKILLINGLONEPEOPLEINTHENIGHTTHENMOVEONTOKILLAGAINUNTILIENDUPWITHADOZENPEOPLEOVERTEWEEKENDTHISISTHEZODIACSPEAKINGINANSWERTOYURASKINGFORMOREDETAILSABOUTTHEGOODTIMESIHAVEHADINVALLEJOSHALLBEVERYHAPPYTOSUPPLYEVENMOREWMATERIALBYTHEWAYKARETHEPOLICEH",
				"IENDUPWITHADOZUENPEOPLEOVERHEWEEKENDTHISISTHEZODIACSPEAKINGINANSWERTOYOURASKINGFORMOREDDETAILSIBOUTGOODTIMESIHAVE___________________HADINVALLEJOISHALLBEVERYHAPPYTOSUPPLYEVENMOREMATERIALBBYTHEWAYARETTHEPOLICEHAVINGAGOODTIMEWITHTHECODEIFNOTTELLTHEMTOECHEEIUPWHENWHENTHEYDOCRACKITTTHEWILLHAVEMEONTHETHOFJULYIDIDNOTOPENTHECARODOORTHEWINDOWWASRO",
				"IFYOUCOPSTHINKIMGOINGTOTAKEONABUSTHEWAYSTATEDIWASYOUDESERVETOHAVEHOLESINYOURHEADSTAKEONEBAGOFAMMONIUMNITRATEFERTILIZERGALOFSTOVEOILDUMPAFEWBAGSOFGRAVELONTOPTHENSETTHESHITOFFWILLPOSITIVILYVENTALATEANYTHINGTHATSHOULDBEINTHEHWAYOFTHEBLASTTHEDEATHMACHINEISALLREADYMADEIWOULD__________________HAVESENTYOUPICTURESBUTYOUWOULDBENASTYENOUUGHTOTRACET",
				"YOUDESERVETOHAVEHOLESINYOURHEADSTTAKEONEBAGOFAMMONIUMNITRATEFERTILIZERGALOFSTOVEOILDUMPAFEWBAGSOFGRAVELONTOPTHENSETTHESHITOFFWIELPOSITIVILYVENTALATEANYTHINGTHATSHOULDBEINTHEWAAYOFTHEBLASTTHEDEATHMACHINEISALLREADYMADE_______________IWOULDHAVESENTYOUPICTBURESBUTYOUWOULDBENASTYENOUGHTOTRACETTHEMBACKTOEVELOPERTHENTOMESOISHALLDESCRIBEDESCRIBEM",
				"WATERANDISHALLLAUGHATTHEMOTHERSWILLHANGBYYTHEIRTHUEBSBURINTHESUNTHENIWILLRUBTHEMDOWNWITHDEEPHEATTOWARMTHEMUPOTHERSISHALLSKIN______________THEMALIVELETTHEMRUNAROUNNDSCREAMINGANDALLBILLIARDPLAYERSISHALLHAVETHEMPLAYINAADARKENEDDUNGENCELLWITHCROOKEDCUESTWISTEDSHOESYESISHALLHOVEGREATFUNINFLICTINGTHEMOSTDELICIOUSOFPAINTOMYSLAVESSFPDASSOMEDAYITT",
				"THEMRUNAROUNDSCREAMINGANDALLBILLIARDPLAYERSISHALLHAVETHEMPLAYINADARKENEDDUNGENCELLITHCROOKEDCUESTWISTEDSHOESYESISHALLHAVEGREATFUNINFLICTINGTHEMOSTDELICOUSOFPAINTOMYSLAVESSFPDASSOMEDAYITMAYHAPENTHATAVICTOMMUSTBEFOUNDIVEGOTTALITTLELISTAIVEGOTALITTLELISTTOFSOCIETYOFFENDERSWWHOMIGHTWELLBEUNDERGROUNDWHOWOULDNEVERBEMISSEDWHO______________WOULDN",
				"MYAFTERLIFEIDOHAVETNGIVETHEMCREDITFORSTUMBLINGACROSSMYRIVERSIDEACTIVITYBUTTHEYAREONLYFINDINGTHHEEASYONESTHEREAREAHELLOALOTMOREDOWNTHERETHEREASONIMWRITINGTOTHETIMESISTHISTHEYDONTBURYMEONTHEBNACKPAGESLIKESOMEOFTHEOTHERSSFPDZODIAC____________________THETIMESSEXAMINERSANFRANCISCOCHRONICLEATTPAULAVERLYCHRONICLESIERRACLUBAROUNDINTHESNOWSOUGHTVI",
				"TOPRINTTHISCIPHERONTVEERUNTPAGEBYFRYAFTERNOONAUGIFYYOUDONOT____________PRINTTHISCIPHERIIWLLGOONAKILLRAMPAGEFRYNIGHTTHISWILLLASTTHEWHOLEWEEKENDIWILLCRUSEAROUNDKILLINGWHOAREALONEATNIGHTUNTILLSUNNIGHTORUNTILLIKILLADOZENPEOPLEDEAREDITORIAMTHEKILLEROFTHETEEENAGERSLASDCHHRISTMASSATLAKEHERMANANDTHEGIRLLASTTHOFJULYTOPROVETHISMISHALLSTATESOMEFACWS",
				"NOPRINTTHISCIPHERIWILLGOONAKILLRAIMPAGEFRYNIGHTTHISWILLLASTTHEWHOLEWEEKENDIWILLCRUSEAROUNDKILLINGPEOPLEWHOAREALONEATNIGHTUNTILLSUNNIGHTORUNTILLI___________KILLADOZENPEOPLEDEAREDITORIAMTHEKILLEROFTHETEENAGERSLASTCHRISTMASSATLAKEHERMANANDTHEGGIRLLASNTTHFJULYTOPROVETHISISHALLSTATESOMEFACTSWHICHONLYITHEPOLICEKNOWCHRISTMASSDBRANDNAMEOFAMMOSRPE",
				"IWILLEGOONRKILLRAMPAGEFRYNIGHTTHISWILLLASTTHEWHOLEWEEKENDIWILLCRUSEAROUNDKILLINGPEOPLEWHOAREALONEATNIGHTUNTILLSUNNIGHTORUNTILL___________IKILLADOZENPEOPLEDEAREDITORIAMTHEKILLEROFTHETEENAGERSLASTCHRISTMASSATLAKEHERMANANDTHEGIRLLASTTHFJULYTOPROVETHISISHALLSTATESOMEFACTSWHICHONLYITHEPOLICEKNOWCHRISTMASSBRANDNAMEOAMMOSUPERXSHOTSFIREDBOYWASONB",
				"ONAKILLRAMPAGEFRYNIGHTTHATWILLLASTTHEWHOLEWEEKENDIIWILLCRUSEAROUNDANDPICKOFFUALLSTRAYPEOPLEORORCOUPPLESTHATAREALONETHENMOVEONTOKILLSOMEMOREUNTILLIHAVEKILLED___________OVERADOZENPEOPLEDEAREDITORTHISISTHEMURDEREROFTWHETEENAGERSLASTCHRISTMASSATLAKEHERMANTHEOGIRLONTHETHOFJULYNEARTHEGOLFCOURSEIINVALLEJOTOPROVEIKILLEDTHEMISHALLSTATESOMEFACTSWWH",
				"RUBEDIYYOURBOOBOOSIFFYOUCOPSTHINKIMGOINGTOTAKEONABUSTHEWWAYISTATEDIWASYOUDESERVETOHAVEHOLESINYOURHEADSTAKEONEBAGOFAMMONIUMNITRATEFERTIEIZERGALOFSTOVEOILDUMPAFEWBAGSOFGRAVELONTOPTHENSETTHESHITOFFWILLPOSITIVILYVENTALATEANYTHINGTHATSHOULDBEIINTHEWAYAFTHEBLASTTHEDEATHMACHINETISALLREADYMADEIWOULDHAVESENTYOUPICTURES_______________BUTYOUWOULDBEN",
				"STILLNESSOFHISFIGURETAATMADEITREALTHESHUDDERTHATTHREWHERUPRIGHTWASLDKEACRYOFTERRORSNDDENIALBUTSHEHAELDHISGLANCEANDANSWEREDEVENLYTHATSTRUEIDIDTHENSTANDBYITHERVOICEWASLOWITS____________________INTENSITYWASBOTHASURRENDERANDASCORNFULREPROACHYOUKNOWBETTERTHANTHATDONTYOUHESHOOKHISHEADNOIWANTYOUTOREMEMBERTHHATTHATHADBEENYOURWISHYOUWERERIGHTINTHE",
				"INCLINEDPOLICETARANTARASERGEANTANDANYTHINGBUTBLINDPOLICETARANTARATARANTARASERGEANTTONHEDANGERTHATSBEHINDPOLICETARANTARASERGEANTYETWHENTHEDANGERSNEARPEOLICETARANTARATARANTARASERGEANTWEMANAGETOOAPPEARPOLICETARANTARASERGEANTASINSENSIBLETOFEARASANYBODYHEREASANYBODYHEREPOLICETARANTARATARANTARATCMABELSERGEANTAPPROACHYOUNG__________FREDERICWASTO",
				"WHICHFORMMEDTHEANGLEOFTHERUEPOLONCEAUTHATHYMNOFTHEANGELSWHICHHEHADHEARDINTHEMIDDLEOFTHENIGHTWASTHENUNSCHANTINGMATINSTHATHALLOFWHICHHEHSDCAUGHTAGEIMPSE________________INTHEGLOOMWASTHECHAPELTHATPHANTOMWHICHHEHADSEENSTRETCHEDONTHHEGROUNDWASTHESISTERWHOWAASMAKINGREPARATIONTHATBELLTHESOUNDOFWHICHHADSOSTRANGELYSURPRISEDHIMWASTHEGARDENERSBELLATT",
				"kfF+LS.4+ypcMN*d(>|/7z5CP*RHG%O+CKt;BUcp)B+K/+OpNc|TB|yy%29kG;/++9-O/MO+GWYNNp Y8#<lV;YCVF5Sb+E>+NWRlZ6cWG)9U4+zkk+#y69/|(BT^2d+zf)>R#Fd.p9M:UV2k#LLEO<#K+@pD Pc^pD++%Cb8EpdBW5l7T1+R*G2S4pH+&G*lZ|<VzOHP25/p(2Hy|lJ9c%l^7FBcZjBj8:yz2+|*EFUZ2BFC4.^z p_dtdLZpT.F<()t 6OV#O:^-)WX_JO8;.V-1jl.WBXR^+D7+MfSR6K5|5OB*G24#  3H1Y.5t.Bty-MSZ%LJM@+R*K%qzA",
				Ciphers.cipher[0].cipher
				};
		for (String test : tests) {
			CandidatePlaintext cp = new CandidatePlaintext(0, test);
			System.out.println(cp.criteriaAll() + ", " + cp.criteriaAll(true));
			System.out.println(cp.candidateConstraints);
		}
	}
	
	public static void test2() {
		//CandidatePlaintext cp = new CandidatePlaintext("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/candidate-plaintexts/candidate-99231.txt");
		//CandidatePlaintext cp = new CandidatePlaintext(0, "ITWERETREACHEROUSLYBECKONINGUSONANDONINORDERTHATTHEMONSTERMIGHTTURNROUNDUPONUSAND_______________RENDUSEATLASTINTHEREMOTESTANDMOSTSAVAGESEASTHESETEMPORARYAPPREHENSIONSSOVAGURBUTSOAWFULDERIVEDAWONDROOUSPOTENCYFROMTHECONTRASTINGSERENITYOFFTHEWEATHERINWHICHBENEATHALLITSBLUEBLANDNESSSOMETHOUGHTTHERELURKEDADEVILISHCHARMASFORDAYSANDDAYSWEVOYAGED");
		//System.out.println(cp.criteriaNgrams());
		//CandidatePlaintext cp = new CandidatePlaintext(0, Ciphers.cipher[0].cipher);
		CandidatePlaintext cp = new CandidatePlaintext(0, "ITRIGHTBUTITSALREADYTENTHEYHADDECIDED___________TOBEATTHEBALLBYHALFPASTTENANDNATASHAHADSTILLTOGETDRESSEDANDTHEYHADTOCALLATTHETAURIDAGARDENSWHENHERHAIRHWASDONENATASHAIINHERSHORTPETTICOATFROMUNDERWHICHHERDANCINGSHFOESSHOWEDANDINHERMOTHERSDRESSINGJACKETANUPTOSONYASCRUTINIZEDHERANDTHENDRANTOHERMOTHERTURNINGHERMOTHERSHEADTHISWAYANDTHATSHEFASTE");
				
		System.out.println(cp.criteriaNgrams());
		System.out.println(cp.criteriaHasPivots());
		System.out.println(cp.criteriaFold());
		System.out.println(cp.criteriaBoxCorners());
		System.out.println(cp.criteriaCompatible(false));
		System.out.println(cp.criteriaAll());
		System.out.println(cp.candidateConstraints);
		//System.out.println(cp.pivots.size());
		//System.out.println(cp.pivots);
	}
	
	public static void test3() {
		String cipher = Ciphers.cipher[316].cipher;
		System.out.println(cipher);
		CandidatePlaintext cp = new CandidatePlaintext(0, cipher);
		System.out.println(cp.criteriaHasPivots());
		System.out.println(cp.pivotPairs);
		
	}
	
	public static void main(String[] args) {
		test3();
	}
	
	/** dump all the info on this CandidatePlaintext as html to help visualize it and its candidate constraints. */
	public String toHtml() {
		String html = "<style>table {border-collapse: collapse; } td { font-family: courier; font-weight: bold; padding: 3px;} .trigram { background-color: #ff9999; } .pivotC { background-color: #66ff66; } .pivotI { background-color: #9999ff; } .foldMarks { background-color: yellow; } .boxCorners { background-color: orange; } </style>";
		html += "<p><u>Index:</u> " + index + "</p>";
		html += "<p><u>Plaintext:</u> " + plaintext + "</p>";
		html += "<p><u>Tokens:</u> " + tokens + "</p>";
		html += "<p><u>Errors:</u> " + errors + "</p>";
		
		if (candidateConstraints != null && !candidateConstraints.isEmpty()) {
			html += "<u>Constraints:</u><br/>";
			
			for (CandidateConstraints cc : candidateConstraints) {
				/** track tooltip names for each grid position */
				String[][] tooltips = new String[height][width];
				for (int row=0; row<height; row++) {
					tooltips[row] = new String[width];
					for (int col=0; col<width; col++) {
						tooltips[row][col] = "";
					}
				}
				/** track html class names for each grid position */ 
				String[][] classes = new String[height][width];
				for (int row=0; row<height; row++) {
					classes[row] = new String[width];
					for (int col=0; col<width; col++) {
						classes[row][col] = "";
					}
				}

				List<Integer> allTrigramPositions = new ArrayList<Integer>();
				allTrigramPositions.addAll(cc.trigrams.columnarTrigram.positions);
				allTrigramPositions.addAll(cc.trigrams.intersectingTrigram.positions);
				for (Integer pos : allTrigramPositions) {
					for (int i=0; i<3; i++) {
						int row = Utils.rowFrom(pos+i);
						int col = Utils.colFrom(pos+i);
						tooltips[row][col] += "trigram ";
						classes[row][col] = "trigram";
					}
				}
				
				for (Integer pos : cc.pivotColumnar.positions) {
					int row = Utils.rowFrom(pos);
					int col = Utils.colFrom(pos);
					tooltips[row][col] += "pivotC ";
					classes[row][col] = "pivotC";
				}				
				for (Integer pos : cc.pivotIntersecting.positions) {
					int row = Utils.rowFrom(pos);
					int col = Utils.colFrom(pos);
					tooltips[row][col] += "pivotI ";
					classes[row][col] = "pivotI";
				}				
				for (Integer pos : new Integer[] {cc.foldMarks.positionLeft, cc.foldMarks.positionRight}) {
					int row = Utils.rowFrom(pos);
					int col = Utils.colFrom(pos);
					tooltips[row][col] += "foldMarks ";
					classes[row][col] = "foldMarks";
				}
				for (Integer pos : cc.boxCornerPair.positions()) {
					int row = Utils.rowFrom(pos);
					int col = Utils.colFrom(pos);
					tooltips[row][col] += "boxCorners ";
					classes[row][col] = "boxCorners";
				}
			
				String[] grid = Ciphers.grid(plaintext, width, true);
				html += "<hr><table>";
				for (int row=0; row<grid.length; row++) {
					html += "<tr>";
					for (int col=0; col<grid[row].length(); col++) {
						char ch = grid[row].charAt(col);
						if (!classes[row][col].isEmpty()) {
							html += "<td class=\"" + classes[row][col] + "\">";
						} else {
							html += "<td>";
						}
						boolean tool = !tooltips[row][col].isEmpty();
						if (tool) {
							html += "<div title=\"" + tooltips[row][col] + "\">" + ch + "</div>";
						} else html += ch;
							
					}
					html += "</tr>";
					
				}
				html += "</table>";
			}
		}
		
		
		return html;
	}
	/** transpose the plaintext to the "dan olson" layout */
	public void transposeOlson() {
		
	}
}
