package com.zodiackillerciphers.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.tests.wordsearch.Stats;

/** searches a grid of characters for all possible Boggle-style formations of words */
public class BogglePuzzle {

	static String wordsFile = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/words-300k.txt";

	//static Map<String, Integer> prefixCounts = new HashMap<String, Integer>();
	//static Set<String> words = new HashSet<String>();
	
	static {
		WordFrequencies.init();  // all words
		//WordFrequencies.initZodiac();  // zodiac corpus words only
		//WordFrequencies.initSuess();
		//for (String word : WordFrequencies.map.keySet()) System.out.println("Loaded word: " + word);
	}

//	static Set<String> wordsZodiac = Words.lettersWords();
	                       
	
	static String[] puzzleBLAH = {
		"anugetsoonthathed",
		"relittheperormswh",
		"isonalfthethatday",
		"seemstomorehlluse",
		"onaldaidtheoldrns",
		"eousethemdinegept",
		"thlssebefnchoodat",
		"theywithlorrdlyan",
		"dralsltueeinsuars",
		"edtfolsylhemreise",
		"flushantpriestash",
		"erntuytmhtraoldie",
		"dansstlrtheintbou",
		"therswhersonorall",
		"sichmorningofthel",
		"tnordisthesowashe",
		"untlahairleteitha",
		"llheidenohsatsthe",
		"tendermaiorhimein",
		"gaparensdthoutsbl"
	};
	static String[] puzzleAK = {
		"thiscouldmeanenco",
		"dedwordsappearlef",
		"ttorightdiagonall",
		"yandverticallyasd",
		"oeshappenintheraw",
		"graysmithasdeterm",
		"inedbyedkitewilks",
		"yestherearemanyma",
		"nysolutionsandyou",
		"cansayalmostanyth",
		"inginthefirstline",
		"sasishowedinmyint",
		"ernetsolutiontime",
		"travelerpostbutim",
		"onootherproposeds",
		"olutionhasanywher",
		"eneartheverticald",
		"iagonalandbackwar",
		"dswordsastherawgr",
		"aysmithandnoneoft"
	};
	static String[] puzzle3 = {
		"sessenesolcha",
		"nensbssendnof",
		"geceoofnbuddy",
		"oionlyyadnels",
		"oeriantfvtonl",
		"ddolstelroolo",
		"tisdfsnsairaa",
		"ifhnirkinyepa",
		"mnaeuniaatone",
		"eoriafpehukld",
		"scernmuenrqfe",
		"ccpfoplpoduca",
		"anfcapnnedpoa"
	};
	static String[] puzzleboo = {
		"her_pl_vpk__ltg_d",
		"np_b__o_dwy___kf_",
		"by_cm_uzgw__l_zhj",
		"spp__l__v_po__rk_",
		"__m_ztjd__fp___k_",
		"p_r_flo__dckf__d_",
		"___kq___ucxgv_zl_",
		"_g_jfj_o__nyz__l_",
		"d_m_b_zr_fbcya__k",
		"_zluv__j_op__fby_",
		"u_r__te_dybpbtmko",
		"__clrj___t_m___bf",
		"z__sy__n__fbc___r",
		"lgfn_f___b_cv_t__",
		"ybx_____ce_vuz___",
		"_c__zbk_op__fmqg_",
		"rct_l__c__flwb__l",
		"___wczwcposht___p",
		"_fkdw__tb_yob__cc",
		"_mdhnpkszzo_a_k__"		
	};
	static String[] puzzleDuman = {
		"d_maanfmgqny_bheg",
		"oi__efrcsvthpipbj",
		"d_xjdq_okcepxc_wf",
		"xapal_bfdgzkx_chg",
		"pb__yi_varzhjoiwk",
		"jklas_gofmtbehdq_",
		"pgcf_xhdr_oiadpxc",
		"_vebbjuy_nzi_ckpa",
		"mljosfnbpftkpgcq_",
		"ghyhr_zawvpcpioib",
		"ljp_a_bcpaddsej_k",
		"mtbkydq_olcxfzgga",
		"b_cda_e_axref_hwv",
		"syndtmhpiuqwvjbbe",
		"frnzkeioldaxfepxc",
		"_wf_mjpgdhd__eds_",
		"oick_xlvgt_wj_vbq",
		"yhkoiwljkfaegpgzh",
		"ohdr_pibijoj_sm_k",
		"fd_lvecka__lbct_n",
		"nofaggphbinjcky_d",
		"_abld__zeq_n_wary",
		"hfoi_sm_je_______"
	};
		
	static String[] puzzle408 = {
		"ilikekillingpeopl",
		"ebecauseitissomuc",
		"hfunitismorefunth",
		"ankillingwildgame",
		"intheforrestbecau",
		"semanisthemoatdan",
		"gertueanamalofall",
		"tokillsomethinggi",
		"vesmethemoatthril",
		"lingexperenceitis",
		"evenbetterthanget",
		"tingyourrocksoffw",
		"ithagirlthebestpa",
		"rtofitiathaewheni",
		"dieiwillbereborni",
		"nparadicesndallth",
		"eihavekilledwillb",
		"ecomemyslavesiwil",
		"lnotgiveyoumyname",
		"becauseyouwilltry",
		"tosloidownorstopm",
		"ycollectingofslav",
		"esformyafterlifee",
		"beorietemethhpiti"		
	};
	
	static String[] puzzle4 = {
		"beensolvedbypolic",
		"emilitaryoramateu",
		"rcodebreakersifit",
		"isaconventionalco",
		"dewithaconvention",
		"alsolutionitveryl",
		"ikleywouldhavebee",
		"nsolvedbynowwhati",
		"fthefirststagesol",
		"utionisnotacodebu",
		"tawordpuzzlewould",
		"atauntingserialki",
		"llermakeawordpuzz",
		"lethebtkdidseehtt",
		"pwwwtabloidcolumn",
		"combtkpuzzlehtmlt",
		"herewerealsopossi",
		"blewordpuzzleelem",
		"entsintheblackdah",
		"liacaseandtheyapp"

	};
	static String[] puzzleGraysmith = {
		"herceanbigivethem",
		"helltoobtsalteseh",
		"lse_iluehstheolhs",
		"seeanamebweollrke",
		"seilllfmiapillsga",
		"emrnpaodemagpcett",
		"oalstbneu_shbllei",
		"thesefoolshallsee",
		"mtilklerepl_saask",
		"dlaublnsloeatplsd",
		"ulraaleitalektiso",
		"et_arsieataillllp",
		"laessolhiapl_tnmr",
		"ahphneaeakl_balll",
		"slsveeseaecbueadl",
		"i_lwllstoenleithe",
		"r_tleaeatlpaslihe",
		"llhsals_ioshtathe",
		"ipgmstallsaoleda_",
		"cithhegsleomaisnl"		
	};
	
	static String[] puzzle1 = {
		"tegkeanbiiaytoaro",
		"eeelnlnlneurhrsdh",
		"lljvheieaenhtldtf",
		"aeeanasrbneneegsr",
		"yihedrxoatsiepoik",
		"esgnsanfrociskrnn",
		"ltesml rivoabrdta",
		"narfdxlneyeudesti",
		"ohhebeegrslvlweos",
		"fdaibenfeneahsllf",
		"iegktreanulebohsn",
		"rhvagfartoohrepls",
		"deialleeatslvn ag",
		"aasendtrobrvboree",
		"lloyrjoicekbietfe",
		"avrndlsnnenrdhmar",
		"gvoetyechesaelaht",
		"eehecdevinatoknhe",
		"asioeharlyunlrfcv",
		"khnteeiadenswas e"
	};
	static String[] puzzle2 = {
		"lksdjflksdjf",
		"iueoqioopqop",
		"aenmaneameee",
		"oiqwpeipoqpq",
		"nmznemneoqia",
		"aeeaibneudof"
	};
	
	static String[] puzzleTest = {
		"EINDNNDERTNSENSDD",
		"SNENNSEDOIDTEEEEE",
		"NEEEDENCSINEESEEN",
		"ENNDDNDEEDNEEENED",
		"NEDEENEDNEEREETTO",
		"NDNDENEDEDVTEDDON",
		"SEEEDDEDNEESETEEN",
		"NSDNEESEENSDEEEEE",
		"DEDENECNDENEEMVTE",
		"DENNEEDNEENDEENED",
		"NENOENINODNNNNDEE",
		"DEENNNNEENTDTEENE",
		"EVEEESESNEENENEDN",
		"NSESDEEDTNTEETNEE",
		"ENESEETEVIDENCEDE",
		"NETDENENENDTEDDSD",
		"NENEESVVEEENINNEE",
		"EEEIVEIEREEENONEN",
		"NETDIEDNNNDENEDVE",
		"DDOESNTEECEDMNEEE"
	};

	static String[] puzzleStarliper = new String[] {
		"KILLSLFDRHELPMEKI",
		"LLMYSELFGASCHAMBE",
		"RAEIOURDAYSQUESTI",
		"ONSABLEEVERYYWAKI",
		"NGMOMENTIMALIVEMY",
		"PRIDELOSTICANTGOO",
		"NLIVINGINTHISWAYK",
		"ILLINGPEOPLEIHAVK",
		"ILLDSOMANYPEOPLEC",
		"ANTHELPMYSELFIMSO",
		"ANGRYICOULDDOMYTH",
		"INGIMALONEINTHISW",
		"ORLDMYWHOLELIFEFU",
		"LOLIESIMUNABLETOS",
		"TOPBYTHETIMEYOUSO",
		"LVETHISIWILLHAVKI",
		"LLDELEVENPEOPLEPL",
		"EASEHELPMESTOPKIL",
		"LINGPEOPLEPLEASEM",
		"YNAMEISLEIGHALLEN"		
	};
	
	static String[] puzzleLathers = new String[] {
		"HERCEANBASHOLTGOO",
		"NEEBNLOLNEY.TLSDC",
		"LYOVCEIZAWNHLLDHO",
		"SIISVAEEVSENEARKO",
		"LIMFDRPOUTSIEITSB",
		"DARNSRO.LDESSIODN",
		"LTEKMLIEYVOAB.DTI",
		"NGOMFHLNILEAGESTI",
		"GAMELSARDMLKUAEOS",
		".DAIBENFENIOTSLY.",
		"INGKTREIDOBEBOMSN",
		"AACLRFILEHTM.FILS",
		"LEWSKLOIILSLVNIAR",
		"AGFENITOTL.CBDREN",
		"NSOELIAICEOBUZK.O",
		"RK.ADBSDNEW.DMNAO",
		"RKOSTLOETESTEVIDE",
		"ENCSIDACIOSHTTNHE",
		"NFIDETSRLEEONL.CI",
		"TMDHNESSDZOAAISOE"		
	};
	
	static String[] puzzleTurkmen = new String[] {
		"AHLTIDIOTPAOLRIOP",
		"DISIKILLING.AORDL",
		"IDKNMSFCINKLLDIAL",
		"SIITIDLOOMILSSLRO",
		"NAMSIKOPAINTSTOPK",
		"ILLINDLEOPEPNTOIK",
		"DISRALIOFNLIO.ILA",
		"KIOLDODLSNDGISOLA",
		"PAMSTSCLONINDAWOR",
		"EIDFOSILSLITANIDE",
		"FSLKIKHAIGIITRMRL",
		"OANDLLAOIROM.STIN",
		"IWASDISTAININKILL",
		"DINDIDIOOT.NOOKSS",
		"DIKOOKOAEHTOFCIES",
		"AN.MIIRKLII.DMAIO",
		"LNRSLOWEASNDNIALL",
		"SSLNEINNTLSARKKLI",
		"ANPPNATKINGLIOEEN",
		"TMIADIPSICLLAARIS"
	};
	
	static String[] puzzleTraveller1st = new String[] {
		"TOSCHITHECOPEOFFE",
		"IHEITISONAMAMTRWH",
		"ISEGCONDFATHEIDTH",
		"EHHATIATHYHSKISRF",
		"ORCEDTHEOHEENBACK",
		"HASTEISITENCECFNT",
		"IHERLOSFNGOFHADEO",
		"TFFHWHISPOIMDOVER",
		"EMCOUNDSFEIGSYPAR",
		"IDINHETHESHAMEISI",
		"NGSKHTOONMIHUOCRS",
		"FMGISHOTHOACABBIE",
		"DPRESIDIOHEIGTSAS",
		"IFEITWHFAUAGHATHE",
		"SOIPTEARNOCHNDHIS",
		"OGAYDIRTSHTAWCLFF",
		"SGOBEPPNMYEIAIOHE",
		"BEHANDAGESETOKTHH",
		"OECEAMATIOMSITING",
		"CCNTIHCEDDSAYORSO"
	};
	
	static String[] puzzleJohnCecil = new String[] {
		"IWILLMAKETHISVERY",
		"CLEARMYONEREASONI",
		"AMKILLINGIFIDONTK",
		"ILLIAMLIKELYTOOAB",
		"ANDONMYCHANCETOCO",
		"LLECTMYSLAVESFORT",
		"HEAFTERLIFEEVENWH",
		"ENIKNOWYOUCANTFIN",
		"DMEIAMNOTSAFEWALK",
		"INGIFIAMCOLLECTMY",
		"SLAVESWHENSLAVESY",
		"OUFOUNDSAVEDDEADI",
		"NACARHECHANGEDHIS",
		"MINDANDRAODFROMME",
		"HEWALKEDBYWITHOUT",
		"HIDINGFROMAENOTHI",
		"NGISSAFEHEWASHID ",
		"INGAKNIFEYOUEVENL",
		"ISTENEDTOOMYSLAVE",
		"WASCALLINGFORHELP"		
	};
	
	static String[] puzzleHal = new String[] {
		"FOLLEIEFTWOALIVLI",
		"KEKNOWINGNOELHIDE",
		"NGAGIKNIVNOELWIFE",
		"SEELEIGHFREIKKLIL",
		"ERIKISSIOOITKNOWS",
		"EGLEIIISHITWILLGO",
		"WOKIRNALNGOVFEILO",
		"OVLEDSWIKEKOIKILR",
		"ILIKEKILLINGGODOI",
		"SIINFKEEKIELLINGS",
		"ATNLEEUNEEILGAEMA",
		"LEIGHSEESMEISEALE",
		"IGHNINETHEYATEDEA",
		"THSEMAELEHAEHIGHI",
		"NVALUEHIYSTHEYSEE",
		"MEATTOYSEEMELTHAE",
		"EHTOTELIEIUTEANVI",
		"IHEEDTHEGEANIEEDD",
		"EEDIETIHSEEKEGIDI",
		"EAGLIGLSEIMEETAEH",
		"MASKLIGETTEHAELGE"		
	};
	
	static String[] puzzleAK2 = new String[] {
		"THANKSINTERESTING",
		"NODOUBTWECANINTEN",
		"TIONALLYCREATEAWO",
		"RDPUZZLESOLUTIONW",
		"ITHVERTICALANDDIA",
		"GONALWORDSTHISPRO",
		"VESLITTLETOMEMAYB",
		"EIDONTUNDERSTANDW",
		"HATYOUWERETRYINGT",
		"OSHOWWHYNOTTAKESI",
		"XORSOACTUALPROPOS",
		"EDSOLUTIONSSTARLI",
		"PERANDOTHERSYOULO",
		"OKEDATPLUSMYRAWGR",
		"AYSMITHANDSEARCHF",
		"ORCORRECTSPELLING",
		"NONANAGRAMWORDSOF",
		"FOURORMORELETTERS",
		"APPEARINGVERTICAL",
		"DIAGONALANDBACKWA"		
	};
	
	static String[] puzzleRandom = new String[] {
		"ORESMIHSUMLSIOSUI",
		"AILCLLOLANTYYUARG",
		"UMEETEGISTECDRRSF",
		"TLOTETPHWDEHAWTIN",
		"OCRERSESONTVRBOST",
		"EHSTWTESINRUOSEWI",
		"HLNUTAETHINSIISAN",
		"REOTERGOWLHEPEKTE",
		"SOETUARGDATMAILFW",
		"DIRSATEAICNORCEDS",
		"YELCRNMHOATTNOGAA",
		"AUIETLTUFTGHHHESE",
		"RESUAAOLLDLRHEHSS",
		"TLHAAHDEDSVPUHCLA",
		"UEATILOTTHODATFIW",
		"ISPMCFOOTWIMUTLTM",
		"UAOENIDWEESSBTTET",
		"PALYIERIGFTBLBEDN",
		"AREOHCNTRHBNAENMI",
		"CPNHPEEGUDILINEDR"		
	};
	//static String[] puzzle = puzzleGraysmith;
	
	
	/*
	static void add(String prefix) {
		Integer val = prefixCounts.get(prefix);
		if (val == null)
			val = 1;
		else
			val++;
		prefixCounts.put(prefix, val);
	}*/
	/*
	static void addWord(String word) {
		words.add(word);
		for (int i = 1; i <= word.length(); i++) {
			add(word.substring(0, i));
		}
	}*/

	
	static public String read() {
		// ...checks on aFile are elided
		StringBuffer contents = new StringBuffer();

		// declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			input = new BufferedReader(new FileReader(new File(wordsFile)));
			String line = null; // not declared within while loop
			/*
			 * readLine is a bit quirky : it returns the content of a line MINUS
			 * the newline. it returns null only for the END of the stream. it
			 * returns an empty String if two newlines appear in a row.
			 */
			while ((line = input.readLine()) != null) {
				//addWord(line);
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					// flush and close both "input" and its underlying
					// FileReader
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return contents.toString();
	}

	public static char[][] makeGrid(String[] puzzle) {
		char[][] grid = new char[puzzle.length][puzzle[0].length()];
		for (int row=0; row<grid.length; row++) {
			for (int col=0; col<grid[0].length; col++) {
				grid[row][col] = puzzle[row].charAt(col);
			}
		}
		return grid;
	}
	
	public static List<BoggleBean> solve(char[][] grid) {
		return solve(grid, true, false, null, 3, 30, 0);
	}
	/**
	 * 
	 * @param dump Print out words when they are found
	 * @param straightOnly If true, only search in one straight direction at a time.  Otherwise, words can "snake" around the grid.
	 * @return
	 */
	public static synchronized List<BoggleBean> solve(char[][] grid, boolean dump, boolean straightOnly, int[] directions, int minLength, int maxLength, int minPercentile) {
		
		List<BoggleBean> results = new ArrayList<BoggleBean>();
		//boolean straightOnly = true;
		if (directions == null) 
			directions = new int[] {0,1,2,3,4,5,6,7};
		
		//for (int len=12; len>3; len--) {
			//System.out.println("- Searching for words of length [" + len + "]...");
		
		boolean[][] visited = new boolean[grid.length][grid[0].length];
		
			for (int row=0; row<grid.length; row++) {
				for (int col=0; col<grid[0].length; col++) {
					List<int[]> path = new ArrayList<int[]>();
					
					if (straightOnly) {
						for (int i=0; i<directions.length; i++) {
							results.addAll(solve(grid, row, col, row, col, new StringBuffer(), path, directions[i], minLength, maxLength, visited, minPercentile));
							path = new ArrayList<int[]>();
						}
					} else {
						results.addAll(solve(grid, row, col, row, col, new StringBuffer(), path, null, minLength, maxLength, visited, minPercentile));
					}
				}
			}
			//System.out.println("Found [" + count + "] words of length [" + len + "].");
		//}
		Collections.sort(results);
		if (dump) {
			for (BoggleBean bean : results) System.out.println(bean + " " + WordFrequencies.percentile(bean.word));
			System.out.println(BoggleBean.toHtml(results));
			System.out.println("total words: " + results.size());
		}
		return results;
	}

	// if direction is null, allow any direction.  otherwise, only search in that direction.
	public static List<BoggleBean> solve(char[][] grid, int row, int col, int startRow, int startCol, StringBuffer prefix, List<int[]> path, Integer direction, int minLength, int maxLength, boolean[][] visited, int minPercentile) {
		List<BoggleBean> list = new ArrayList<BoggleBean>();
		if (row < 0 || row > grid.length-1) return list;
		if (col < 0 || col > grid[0].length-1) return list;
		if (visited[row][col]) return list; // already been here
		prefix.append(grid[row][col]);
		if (prefix.length() > maxLength) return list;
		//System.out.println(row+","+col+","+startRow+","+startCol+","+prefix+","+length);
		if (!WordFrequencies.hasPrefix(prefix.toString())) return list; // stop search because nothing matches this prefix.
		path.add(new int[] {row,col});
		visited[row][col] = true;
		//if (prefix.length() == length)
		if (prefix.length() >= minLength && WordFrequencies.hasWord(prefix.toString()) && WordFrequencies.percentile(prefix.toString()) >= minPercentile) {
			BoggleBean bean = new BoggleBean(prefix.toString(),
					WordFrequencies.freq(prefix.toString()), direction,
					startRow, startCol, path, grid);
			// System.out.println(bean);
			// System.out.println(prefix + ", " + prefix.length() + ", " +
			// WordFrequencies.freq(prefix.toString()) + ", d" + direction +
			// ", row " + (startRow+1) + ", " +
			// (isZodiacWord(prefix.toString()) ? "ZODIAC WORD, " : "") +
			// "column " + (startCol+1) + ", path " + dumpPath(path));
			list.add(bean);
		}
		
		if (direction == null || direction == 0) list.addAll(solve(grid, row-1, col-1, startRow, startCol, new StringBuffer(prefix), path, direction, minLength, maxLength, visited, minPercentile));
		if (direction == null || direction == 1) list.addAll(solve(grid, row-1, col, startRow, startCol, new StringBuffer(prefix), path, direction, minLength, maxLength, visited, minPercentile));
		if (direction == null || direction == 2) list.addAll(solve(grid, row-1, col+1, startRow, startCol, new StringBuffer(prefix), path, direction, minLength, maxLength, visited, minPercentile));
		if (direction == null || direction == 3) list.addAll(solve(grid, row, col-1, startRow, startCol, new StringBuffer(prefix), path, direction, minLength, maxLength, visited, minPercentile));
		if (direction == null || direction == 4) list.addAll(solve(grid, row, col+1, startRow, startCol, new StringBuffer(prefix), path, direction, minLength, maxLength, visited, minPercentile));
		if (direction == null || direction == 5) list.addAll(solve(grid, row+1, col-1, startRow, startCol, new StringBuffer(prefix), path, direction, minLength, maxLength, visited, minPercentile));
		if (direction == null || direction == 6) list.addAll(solve(grid, row+1, col, startRow, startCol, new StringBuffer(prefix), path, direction, minLength, maxLength, visited, minPercentile));
		if (direction == null || direction == 7) list.addAll(solve(grid, row+1, col+1, startRow, startCol, new StringBuffer(prefix), path, direction, minLength, maxLength, visited, minPercentile));
		path.remove(path.size()-1);
		visited[row][col] = false;
		return list;
	}
	
	
	public static void testAK() {
		String excerpt = "btkwordpuzzleunmarkedbtkwordpuzzlepartiallysolvedproposedzodiacfirststagesolutionvirginunmarkedzodiacproposedfirststagesolutionasawordpuzzle" + 
		"markeddoranchakandthebigzandeveryonethebestfbisupercomputershavenotsolvedthezodiacandithasnotbeensolvedbypolicemilitaryoramateurcodebreakers" + 
		"ifitisaconventionalcodewithaconventionalsolutionitverylikleywouldhavebeensolvedbynowwhatifthefirststagesolutionisnotacodebutawordpuzzlewould" + 
		"atauntingserialkillermakeawordpuzzlethebtkdidseehttpwwwtabloidcolumncombtkpuzzlehtmltherewerealsopossiblewordpuzzleelementsintheblackdahliac" + 
		"aseandtheyappearincrimemoviestvshowspulpnovelsandcomicbooksandsimilarpuzzlesappearinthenewspaperandseekandfindbooksatthedrugstorezodiacsuspe" + 
		"cttedkaczynskialsomadesuchwordpuzzlestellingafriendhewashisvictimandhehadtofindthelettersofmynameinamusicalcompositionhewrotetheywerehiddeni" + 
		"nthelastletterofeachlineyourpostsmademethinkwhydoiclaimthatthewordpuzzleaspectoftheproposedsolutionislikelyvalidandintendedbythezodiacyoupro" + 
		"vedwhatweallkneworsuspectedanywaythatifyoutakeanycombinationofwordsandputtheminaxgridyouwillhaverandomlycreatednewwordsthatappearverticallyd" + 
		"iagonallyandbackwardsinyourexampleyoushoweddozensofwordsliketsaranalacremallmalttheoxfordenglishdictionaryhasoverwordsinitwhatisayissignific" + 
		"antabouttheproposedpartialsolutionisthetypeofwordscreatedthereisalsosomeinterestinggeographyduelleadstolistleadstobombsleadstotheobutunlessd" + 
		"oranchakorthebigzcanthinkofawaytoincludegeographywecanleavethatoutoftheexperimentwhenyoutrytosolveacodeonemethodistowritedownwordsyouexpectm" + 
		"aybeencodedsoinamilitarycontextyoumightwritedowntroopsattackmovetanksdawnretreatetcinakidnappingcasewithacodedmessageyoumightwritedownasword" + 
		"stolookforransommoneycashbagchildkidexchangemeetpoliceetcintryingtodecipherthewhatarethetowordswewouldthinkzodiacmightusemaybewordslikefunki" + 
		"llkillinghuntknifestabgunhoodsymbolcodeshepolicedeadslavescollectnamelistbombsblastgameballtiesropeetcletssetasidenamewhichappearsinnormalre" + 
		"adandblastwhichappearstwicebutanagrammedjustlookingfornonanagramwordsappearingverticaldiagonalorbackwardsintheproposedpartialsolutionwehavel" + 
		"istbombsgameballtiesthosearewordszodiacusedandthatithinkwouldbeonanyoneslisttolookforareyousayingthosewordsjustappearbychancewealsohaveother" + 
		"wordsthatzodiacmightuseandorwereusedbyazodiacsuspectandknownsfareaserialkillertedkaczynskiduelbarsleashstallstheobutletskeepthissimpleandsti" + 
		"cktothewordszodiachimselfusedgametieslistbombsballyousaythesewordsonlyappearbyrandomchanceokmychallengeisthisletscomeupwithalistofzodiacword" + 
		"sletsincludetheandotherslikeslaveskilletcithinkitwouldbefairlysimpletorunxgridsofenglishlangaugesentencesmobydicknytimesandthenhavethecomput" + 
		"erseekandfindthezodiacwordsappearingverticallydiagonallyandbackwardscorrectspellingandnoanagramsithinkandabovewouldbeaclearfailuretomebutiwi" + 
		"llacceptandaboveasafailuremeaningifyouruntestsandtimesormoreyougetormorezodiacwordsfromthelistofyouwillhaveprovenitcouldberandomchanceandthe" + 
		"reforiamwrongandiwillposttheresultshereandonwwwunazodcomandwebsleuthsbutifyouruntheandyougetormorezodiacwordszerotimesornotmorethantimesthat" + 
		"willbedeemedasuccessfulshowingthatsucharesultisveryunlikelytohappenbyrandomchanceandyouwillpostitatzkfdoesthatsoundfairdoranchakdoyoutakethe" + 
		"challengeorwanttomodifythetermswhataboutthebigzonboardsiranyoneelseiwouldloveitifdoranchakthebigzandaplayertobenamedlatertookthechallengeitc" + 
		"ouldbeinterestingtoseeifresultsareesentiallythesamewhydoesitmatterifiamrightandthisisanintendedwordpuzzlethenwehaveafirststagesolutiontothea" + 
		"ndastaringpointtogettotheendsolutionhighlydebatedgotopageoneofthisthreadforthehistorythiswasdoneorsoyearsagotakingtherawgraysmithsolutionasd" + 
		"oneandcorrectedbybullittkiteobiwanedandothersonoldzkboardthreeyearsagoinoticedwordslikelistbombsgametiesleashtheoetcappearingisaythisisveryu" + 
		"nlikelytohappenbychanceifiamrightthenthisisproofthisisavalisolutionoratleastpartiallyvalidbutifthesewordswouldappearinoutofwordgridsthenitco" + 
		"uldberandomsosolutionprobablynotvalidornomorevalidthananyotherhencethechallengeforatestexperimentchallengenotacceptedbydoranchakyouneedtorea" + 
		"daboutconfirmationbiasifiamwrongacceptthechallengeandprovemewrongwordsusedbythezodiaclikelistbombsgametieswouldnotallappearbychanceandyoukno" + 
		"witthatiswhyyoudeclinedthechallengeperhapsthebigztravorsomeoneelsewilltakeitonthanksgoodpointsandwhatiproposehereissimilartowhatbtklaterdidw" + 
		"hatblackdahliakillermayhavedoneearlierandwhathasbeendoneincrimemoviesandnovelszmayhavereadandiamveryconfidentinmyresearchskillsandanalytical" + 
		"abilitiesbutthereasoniaskdoranchakthebigzandotherstolookatthisisidonotownacomputerthatcandoitanddonotownabrainwithexcellentcomputerprogrammi" + 
		"ngskillslikedoranchakandbigzsoallicandoissayhereissomethingextraordinaryifiamrightitcouldbeafirststeponthepathtosolvingthedoranchakkaczynski" + 
		"wrotealotaboutrestraintsincludingleashbarsandloseingthemtherearerestraintsmentionedwealsohaveotherwordsrelevanttozortklikedueltheoetcplusall" + 
		"therandomwordsyouhaveshownweegtinanygridifiamwrongdowordgridsandseeifanycreatekwordszodiacusedandinproximityimeanwordszodiacusedandinproximi" + 
		"tyoranywheretravwhatbullittkiteobiwanandothersdidistrytofigureoutwhatwasarawcorrectandlogicalsolutionbeforegraysmithmuckeditupaboutyearsagot" + 
		"heycameupwiththissolutionwithlineslikeigivethemhelltoseeanamethesefoolshallseethenaboutyearsagoinoticedsomethingneverclaimedornotedbygraysmi" + 
		"thbullittoranyoneandthatisthatwordszodiacusedlikelistbombsgametiesappearedverticallydiagonallyorbackwardscorrectlyspellednoanagramsandwordst" + 
		"hattkusedlikeleashbarsstallsandalsowordslikeduelandtiesandoterstomeandsomeothersthatwasoddandinterestingthenifyoulookatseeanamerightbeforeit" + 
		"istheoandthelettersaroundthatcaesarshiftwithshiftstokaczynskithatissomethingdoranchakandbigzsayhappenstolessthanoflettersequencesithinkthere" + 
		"stofthisfirststagesolutionbecomesafinalreadablesolutionwithcaesarshiftsandsomeotherstepidontknowyet";
		System.out.println("Reading words...");
		read();
		
		/*
		for (int i=0; i<excerpt.length()-340; i++) {
			String s340 = excerpt.substring(i,i+340);

			System.out.println("excerpt: " + s340);
			puzzle = new String[20];
			for (int j=0; j<20; j++) {
				puzzle[j] = s340.substring(j*17,j*17+17);
			}
			makePuzzle();
			solve(false, true, null, 4, 20, 0);
			
		}*/
	}
	
	public static void testSolve() {
		//System.out.println("Reading words...");
		//read();
		//System.out.println(prefixCounts.keySet().size());
		//System.out.println(words.size());
		System.out.println("Solving...");
		
		
		//makePuzzle();
		solve(makeGrid(puzzleGraysmith), true, true, Stats.DIRECTIONS, 4, 30, 60);
	}

	public static void main(String[] args) {
		testSolve();
		//testAK();
	}

}
