package com.zodiackillerciphers.tests.wordsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.FrontMember;
import com.zodiackillerciphers.dictionary.BoggleBean;
import com.zodiackillerciphers.dictionary.BogglePuzzle;
import com.zodiackillerciphers.dictionary.WordFrequencies;

import ec.EvolutionState;
import ec.multiobjective.MultiObjectiveFitness;
import ec.vector.IntegerVectorIndividual;

public class CandidateKey extends IntegerVectorIndividual implements FrontMember {
	
	public static boolean EXIT = false;
	
	public static int WHICH = 0;
	public static Cipher cipher = Ciphers.cipher[WHICH];
	public static String ciphertext = cipher.cipher;
	public static String alphabet = Ciphers.alphabet(ciphertext);

	public static List<String> allWords; 
	
	public String decoderAsString;
	public Map<Character, Character> decoder;
	
	public String decoded;
	public char[][] grid;
	
	public static Set<String> mustHaves;
	static {
		mustHaves = new HashSet<String>();
		mustHaves.add("HERCEAN");
		mustHaves.add("IGIVETHEMHELLTOO");
		mustHaves.add("THEO");
		mustHaves.add("THEODORE");
		mustHaves.add("KACZYNSKI");
		mustHaves.add("SEEANAME");
		mustHaves.add("THESEFOOLSSHALLSEE");
	}

	public static String[] zwordsArray = new String[] {
		"AMMO", "ARMY", "BANG", "BLUE", "BOMB", "BURN", "BURY", "BUSS", "CAEN", "CODE", "COPS", "DAMN", "DAMP", "DARK", "DEAD", "DIED", "DUMP", "FACE", "FIRE", "GAME", "GIRL", "HANG", "HEAT", "HELL", "HERB", "HOLE", "HUNG", "KILL", "LADY", "LAKE", "LEGS", "LIES", "LIPS", "MASK", "NECK", "NEWS", "PAIN", "PARK", "PAUL", "PIGS", "RAGE", "ROAD", "ROPE", "SFPD", "SHIT", "SHOE", "SHOT", "SICK", "SKIN", "SLAY", "TAXI", "TEEN", "THEO", "TIRE", "WILD", "ACCID", "ALLEY", "ANGER", "ANGRY", "ASSES", "BATES", "BELLI", "BLOND", "BLOOD", "BOMBS", "BUTON", "CAGES", "CALIF", "CLAIF", "CLEWS", "DEATH", "FIRED", "GIRLS", "GLORY", "GRAVE", "KNIFE", "LAUGH", "MAPLE", "MARCO", "NAILS", "NASTY", "PINES", "PROWL", "ROCKS", "SHOOT", "SHOTS", "TAHOE", "TIRES", "ALLEYS", "ANAMAL", "AVERLY", "BARREL", "BEWARE", "BLUBER", "BREAST", "BROKEN", "BULLET", "BURNED", "BUSSES", "BUTONS", "CAUGHT", "CIPHER", "CIRCLE", "CLEVER", "COMIDY", "COUPLE", "DIABLO", "DOOMED", "DUNGEN", "EDITOR", "FEMALE", "FOUGHT", "GORGED", "HERMAN", "IDIOUT", "INCHES", "INSANE", "KICKED", "KILLED", "KILLER", "KISSED", "LAUGHS", "LETTER", "LONELY", "MAILED", "MELVIN", "MURDER", "POLICE", "PUNISH", "REBORN", "SCREAM", "SIERRA", "SIGHTS", "SLAVES", "SQUIRM", "SQWIRM", "STRIKE", "STUPID", "TARGET", "THROAT", "TOSCHI", "TRIGER", "VICTIM", "VICTOM", "WOEMAN", "ZODIAC", "ALLWAYS", "BATTERY", "BILLOWY", "BOOBOOS", "BURNING", "BUTTONS", "CHOCKED", "CITIZEN", "COLLECT", "CONCERN", "CONTROL", "COUPPLE", "CRACKED", "CROOKED", "CUTTING", "CYIPHER", "EDITORS", "FIREING", "FORREST", "GRABBED", "HERCEAN", "HOLDING", "IDENITY", "KIDDIES", "KILLING", "LIBRARY", "MACHINE", "MEANNIE", "MELVINS", "MURDERS", "NITRATE", "PHANTOM", "PLUNGED", "RADIANS", "RAMPAGE", "SMARTER", "SOCIETY", "STAINED", "STATION", "STRANGE", "SUCIDES", "TARGETS", "THECOPS", "TORTURE", "TWICHED", "TWISTED", "UNHAPPY", "VALLEJO", "VICTIMS", "VICTOMS", "WAITING", "WALKING", "WARNING", "WATEING", "WIPEING", "ABNOMILY", "AMMONIUM", "BADLANDS", "BASEMENT", "BATTERED", "BULLSHIT", "CHILDREN", "CONCERNS", "CONTROOL", "COUPPLES", "CRUZEING", "DARKENED", "DISORDER", "DRAINING", "DRIPPING", "EXAMINER", "EXORCIST", "HORRIBLE", "IMPRIEST", "INITIALS", "KILLINGS", "MACHIENE", "MEANNIES", "MURDERER", "NUCENCES", "PARADICE", "PATERNED", "PHOMPHIT", "PHRAISES", "PROMICED", "SEARCHED", "SEEANAME", "SILOWETS", "SPEAKING", "SPILLING", "SPOILING", "SPURTING", "SQUIRMED", "STALKING", "STRUGGLE", "SUICIDES", "SUPERIOR", "TERITORY", "THASHING", "THEODORE", "THINKING", "TITWILLO", "VIOLENCE", "ABNORMILY", "ACCIDENTS", "AFTERLIFE", "ANILATING", "ATTENTION", "CHRONICLE", "DANGEROUE", "DAUGHTERS", "DELICIOUS", "DROWNDING", "EXPERENCE", "FERTLIZER", "FINGERTIP", "FRANCISCO", "HUMMEREST", "KACZYNSKI", "OFFENDERS", "RIVERSIDE", "ROBBERIES", "SATERICAL", "SCREAMING", "SEARCHING", "SERANADER", "SLAUGHTER", "SPLINTERS", "SQUEALING", "STUMBLING", "TEENAGERS", "THRILLING", "VENTALATE", "VIOLENTLY", "WANDERING", "CALIFORNIA", "CHRISTMASS", "COLLECTING", "CONFESSION", "CONSCIENCE", "CRACKPROOF", "DEPLORABLE", "ENTERPRISE", "FERTILIZER", "FINGERTIPS", "INFLICTING", "LIBERATION", "SQUEALLING", "SUPICISOUS", "SUSPICIOUS", "SYMBIONESE", "WASHINGTON", "ANONYMOUSLY", "DISAPPEARED", "DISTRIBUTOR", "INTHUSASTIC", "MASTERPIECE", "MOTORCICLES", "FINGERPRINTS", "PESTULENTUAL", "WACHMACALLIT", "WHATSHISNAME", "CONSTERNATION", "GLORIFICATION", "PHOTOELECTRIC", "PSYCHOLOGICAL", "SENSIBILITIES", "WACHAMACALLIT", "UNCOMPROMISING", "UNCOMPROMISEING", "IGIVETHEMHELLTOO", "EBEORIETEMETHHPITI", "THESEFOOLSSHALLSEE", "LIST", "FOOL", "FOOLS", "BARS", "BALL", "LEASH", "LOSE"		
	};
	public static Set<String> zwords;
	static {
		zwords = new HashSet<String>();
		for (String w : zwordsArray) zwords.add(w);
	}
	public static String[] zwordsAKArray = new String[] {
		"ACCID", "ALLEY", "AMMO", "ANGER", "ANGRY", "BALL", "BANG", "BARS", "BATES", "BELLI", "BLOND", "BLOOD", "BOMB", "BOMBS", "BURN", "BURY", "BUSS", "BUTON", "CAEN", "CAGES", "CLEWS", "CODE", "COPS", "DARK", "DEAD", "DEATH", "DIED", "DUEL", "DUMP", "FACE", "FIRE", "FIRED", "FOOL", "FOOLS", "GAME", "GIRL", "GIRLS", "GLORY", "GRAVE", "GUNS", "HANG", "HEAT", "HELL", "HERB", "HOLE", "HUNG", "HURT", "KILL", "KNIFE", "LADY", "LAKE", "LAUGH", "LEASH", "LIES", "LIPS", "LIST", "LOSE", "MAPLE", "MASK", "NAILS", "NASTY", "NECK", "NEWS", "PAIN", "PARK", "PAUL", "PIGS", "PINES", "PROWL", "RAGE", "ROAD", "ROCKS", "ROPE", "SFPD", "SHIT", "SHOOT", "SHOT", "SHOTS", "SICK", "SKIN", "SLAVE", "SLAY", "STAB", "STALLS", "TAHOE", "TAKE", "TAXI", "TEEN", "THEO", "TIES", "WILD"
	};
	public static Set<String> zwordsAK;
	static {
		zwordsAK = new HashSet<String>();
		for (String w : zwordsAKArray) zwordsAK.add(w);
}
	
	public CandidateKey() {
		decoder = new HashMap<Character, Character>();
		grid = new char[cipher.height][cipher.width];
	}
	
	// convert the genome into a key
	public synchronized void expressGenome() {
		decoder = new HashMap<Character, Character>();
		decoderAsString = "";
		grid = new char[cipher.height][cipher.width];
		for (int i=0; i<genome.length; i++) {
			char ch = (char)(genome[i]+65);
			decoderAsString += ch;
			decoder.put(alphabet.charAt(i), ch);
		}
		decode();
	}
	
	// apply key to cipher text
	public synchronized void decode() {
		String result = "";
		for (int i=0; i<ciphertext.length(); i++) {
			char c = ciphertext.charAt(i);
			char p = decoder.get(c);
			result += p;
			
			int row = i/cipher.width;
			int col = i%cipher.width;
			grid[row][col] = p;
		}
		this.decoded = result;
		//System.out.println("SMEG " + objectInfo(this) + " decoded " + decoded);
		
	}
	
	// generate multidirectional strings
	public synchronized List<BoggleBean> search() {
		return BogglePuzzle.solve(grid, false, true, Stats.DIRECTIONS, 4, 30, Stats.MIN_PERCENTILE);
	}
	
	public synchronized double[] scoreWords(List<BoggleBean> words) {
		
		double[] score = new double[] {0,0};
		if (words == null) return score;
		List<BoggleBean> uniqs = uniqs(words);

		//int min = 0;
		//System.out.println("SMEG " + objectInfo(this) + " words " + uniqs.size());
		//String w = "";
		for (BoggleBean word : uniqs) {
			//w += word.word + " ";
			//if (mustHaves.contains(word.word)) score[0]++;

			//if (word.word.equals("IGIVETHEMHELLTOO")) min++;
			//if (word.word.equals("THESEFOOLSSHALLSEE")) min++;
			//if (min < 2) continue; // force those two strings to appear before further scoring is performed
			
			/*int len = word.word.length();
			if (len == 4 || len == 5) score[1]++;
			else if (len > 5 && len < 9) score[2]++;
			else if (len > 8) score[3]++;
			else throw new RuntimeException("Unexpected length: " + len);*/
			
			if (zwordsAK.contains(word.word)) score[0]++;
			score[1]++;
			//if (zwords.contains(word.word)) score[1]++;
			//score[2]++;
			
			if (score[0] == zwordsAKArray.length) // && score[2] >= 71)
				EXIT = true;
			
		}
		//System.out.println("SMEG " + objectInfo(this) + " words " + w);
		return score;
		
	}

	// return a new list that has no duplicate words
	static List<BoggleBean> uniqs(List<BoggleBean> beans) {
		if (beans == null ) return null;
		Set<String> uniq = new HashSet<String>();
		List<BoggleBean> list = new ArrayList<BoggleBean>();
		for (BoggleBean bean : beans) {
			if (uniq.contains(bean.word)) continue;
			// Consider two strings, A and B, where A is longer than B, and B is a proper substring of A.
			// String A contributes to the score.
			// String B does not contribute to the score if it is fully contained in A.
			// String B contributes to the score when it appears in a different location of the grid than A.
			
			// For now, though, take the easy way: remove all proper substrings
			boolean cont = false;
			for (String u : uniq) { 
				if (u.contains(bean.word)) {
					//System.out.println(u + " + contains " + bean.word);
					cont = true;
					break;
				}
			}
			if (cont) continue;
			list.add(bean);
			uniq.add(bean.word);
		}
		return list;
	}
	
	public String toString() {
		return decoderAsString + ", " + decoded; 
	}

	public static String grid(CandidateKey key) {
		String g = "";
		for (int row=0; row<key.grid.length; row++)
			for (int col=0; col<key.grid[row].length; col++) 
				g += key.grid[row][col];
		return g;
	}
	public static String objectInfo(CandidateKey key) {
		if (key == null) return "";
		return "hash " + key.hashCode() + " genome " + key.genotypeToStringForHumans() + " objectid " + System.identityHashCode(key) + " grid " + grid(key);
	}
	
	@Override
	public void defaultMutate(EvolutionState state, int thread) {
		//System.out.println("SMEG gen " + state.generation + " mutating individual " + objectInfo(this));
		Random rand = new Random();
		if (rand.nextBoolean()) super.defaultMutate(state, thread);
		else {
			
			
			// try to insert a random word
			if (allWords == null) {
				allWords = new ArrayList<String>();
				allWords.addAll(WordFrequencies.map.keySet());
			}
			
			int which = rand.nextInt(allWords.size());
			String word;
			
			if (rand.nextBoolean()) {
				word = allWords.get(which);
			} else {
				// force an AK-specific word 
				word = CandidateKey.zwordsAKArray[rand.nextInt(CandidateKey.zwordsAKArray.length)];
			}
			
			//if (rand.nextBoolean()) word = "THESEFOOLSSHALLSEE"; // force this temporarily
			//else word = "IGIVETHEMHELLTOO";

			int row = rand.nextInt(grid.length);
			int col = rand.nextInt(grid[0].length);
			int len = 0;
			
			// generate a random direction
			int dr = 0;
			int dc = 0;
			while (dr == dc) {
				// pick random number in range [0,2]
				int r = rand.nextInt(3);
				// adjust to range [-1,1]
				r -= 1;
				dr = r;
				
				r = rand.nextInt(3);
				r -= 1;
				dc = r;
			}
			
			while (len < word.length() && row < grid.length && row >= 0 && col < grid[0].length && col >= 0) {
				char c = word.charAt(len);
				update(row, col, c);
				len++;
				row += dr;
				col += dc;
			}
			
			expressGenome();
			
		}
	}
	
	// update the symbol at the given row and col so it decodes to the given plaintext
	void update(int row, int col, char p) {
		// get the cipher symbol
		char c = ciphertext.charAt(row*cipher.width+col);
		// where is it in the cipher alphabet?
		int a = alphabet.indexOf(c);
		if (a == -1) throw new RuntimeException("Can't find cipher symbol [" + c + "].  This should be impossible.");
		// update the genome to reflect the new substitution
		genome[a] = p-65;
	}

	public static void testGenomes() {
		CandidateKey key = new CandidateKey();
		key.genome = new int[] {18, 3, 4, 13, 4, 4, 4, 3, 19, 14, 18, 3, 3, 19, 4, 21, 3, 3, 4, 4, 4, 4, 3, 4, 12, 13, 21, 14, 8, 4, 18, 4, 13, 4, 4, 3, 18, 4, 17, 13, 4, 13, 13, 4, 8, 4, 3, 2, 3, 13, 13, 4, 3, 4, 4, 19, 13, 13, 3, 13, 4, 4, 13};
		key.expressGenome();
		System.out.println(key);
	}
	
	
	public int diffs(CandidateKey key) {
		int diffs = 0;
		for (int i=0; i<key.genome.length; i++)
			if (key.genome[i] != this.genome[i]) diffs++;
		return diffs;
	}
	
	public String fit() {
		if (fitness == null) return "";
		MultiObjectiveFitness m = (MultiObjectiveFitness) fitness;
		String f = "";
		for (int i=0; i<m.getNumObjectives(); i++) f += m.getObjective(i) + " ";
		return f;
	}
	@Override
	public Object clone() {
		Object c = super.clone();
		/*System.out.println("SMEG cloning from ind "
				+ CandidateKey.objectInfo(this) + " (fit " + fit() + ") to "
				+ CandidateKey.objectInfo((CandidateKey) c) + " (fit "
				+ ((CandidateKey) c).fit() + ")");*/
		CandidateKey k = (CandidateKey) c;
		k.grid  = new char[this.grid.length][this.grid[0].length];
		for (int row=0; row<k.grid.length; row++) 
			for (int col=0; col<k.grid[row].length; col++)
				k.grid[row][col] = this.grid[row][col];
		return c;
	}

	public static String zwordsFor(String str) {
		String s = "";
		String[] split = str.split(" ");
		for (String word : split) if (zwordsAK.contains(word)) s += word + " ";
		return s;
	}
	
	public String html() {
		return BoggleBean.toHtml(BogglePuzzle.solve(grid, false, true, Stats.DIRECTIONS, 4, 30, Stats.MIN_PERCENTILE));		
	}
	
	public String dump() {
		return CandidateKey.objectInfo(this) + " fitness: " + fit();		
	}
	
	public static void main(String[] args) {
		//testGenomes();
		//System.out.println(alphabet);
		System.out.println(zwordsFor("DEATH SALES SCALE TALKS TELLS HARSH BALLS BEAMS BLOND STEER ATLAS SENNA RESET ANNES THEN TAKE MADE CASE SEEN LESS TELL ELSE GAME DEAD ONES TEST TALK SALE CASH BALL READ CARD HEAT HELL LOSE ANNE NOSE LIES SALT SALT SETS LAKE BARS BEAT BELT BAGS HATE ROPE SHIT KATE ARAB NEST SARA RAGE TIES LAMB BEAM TOES SACK BANG BASS BEES DARE DAME THEE PEAS MARS ARSE MAST OLDS LEST LIED GOSH RITA BATS TESS TORT TSAR OPEC COKE AMEN STAN LENT HERB RAGS STAB PANS CSCE LEAR SNAP NASA PAPA ELLA SUPT DANE TROT LAOS SOCK PORN REPS CASA ACES ADEN AMES DAIS NESS LDCS EELS MACE SEMA PISA TIRE ANTE ACER SEEP PIED NODS ALES ANGE ECSC RAMS ACAS SAMS NAAS SATS SATS LARS RADA ETNA LEDS CAEN SUES SIEN SAAD ELSA"));
	}

}
