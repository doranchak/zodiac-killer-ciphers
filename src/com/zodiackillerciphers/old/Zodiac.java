package com.zodiackillerciphers.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;
import java.text.*;

import com.zodiackillerciphers.ciphers.Ciphers;

import ec.util.MersenneTwisterFast;
import gnu.trove.*;

public class Zodiac {
	
	/* are we doing a steady-state GA? */
	public static final boolean STEADYSTATE = false;
	
	/* allele type.  0 = letters of the decoder.  1 = sequence of full words. */
	public static final int ENCODING = 0;
	
	public static final int CIPHER = 1;
	
	public static String[] COVER = new String[] { "_","__","___","____","_____","______","_______","________","_________" };
	
	public static String[] cipher;
	
	/* word encoding (for fitnessWord): 0 = words only; 1 = word/position tuples; 2 = positions only (fixed set of words) */
	public static final int WORD_ENCODING = 1;
	public static final String[] PLUGGER = {
		//"killing","people","because","itis","itis","killing","because","isthe","something","collecting"
		"killing","people","because","itis","isthe","something","collecting","more","than","most","kill","better","best","die","will","slave"
		//"killing","people","because","itis","itis","killing","because","isthe","something","collecting","more","than","name","slaves","afterlife"
		//"said","of","and","do","are","their","on","many","was","out","for","of","in","the","of","the","i","he","to","would","of","were","it","the" // 24 words.  only 14 of them fit in the decoded cipher.
		//"killing","people","because","itis","itis","killing","because","isthe","something","collecting","more","than","girl"
		//"killing","people","more","itis","itis","killing","because","isthe","something","the"
		//"killing","people","more","itis","itis","killing","because"
		//easy one:
		//"like","it","is","much","more","fun","than","killing","people","most"
		//"i","a","to","of","in","my","is","it","me","the","you","and","was","for","have","this","that","with","them","your","shall","people","slave","killing","speak","inthe","iam","ishall","isthe","kill"
		//"killing","slave","christmas","because","school","death","chronicle","zodiac","children","collect"
		//"halloween","killing","this","next","you","fun","die","zodiac","iam","iwill"
		//"congratulations","crack","cipher","before","kill","again","different","the","itis","die"
		//"cipher","zodiac","speaking","solve","kill","slave","itis","itis","isthe","kill","have","the","than","because"
	};
	public static HashMap pluggerHash = null;
	public static int pluggerHit = 0;
	public static int pluggerMiss = 0;
	
	public static final int ENCODING_MAX = 500;
	
	
	/** original 408 + transformations */
	public static String[] cipher408 = {
		// original 
		cipher[1],
		 // rot 90, no flip
		"VZ_HV%pN^zrPIZBcLR@kSMW9E@e!ZOek=UI59cPZdN+^(JV%XJdFeTX)Sp!MDpDK/tqL/Y+PrT#BG5qSrkJ8RORqPIGM9^e/9t#XYRWclAkR_V+p#YDZ#UGZWq69KUqEf95UTWjIBE9JBIY/I_ezEc_/U#9tYI=)@lKdPkFU685X_+F9eB8%r56WXOIrO76BqJPAT_#%6VLL\\+\\qq8)\\Rq9%EIODYd8%7WM)dtN!Eq6pATHkH+RdAYcZD\\lNeL(8HGqFUtPOMrX\\9q+fz+NV/)e5MBXH%N@R)BQ7%_@AGVAE@lELUT8VfQK==PFL#^9P%T)KX^UM^Q5WRY!pUQ%!LSA#%tZHJRHrRSzelDqXIWG=tq9BIO(=Q6k9R#S8q5Y=k6cq_WBVMPPGAHF#kB(YE)eB",
		// rot 0, flip 
		"B=Xp=ROk%BU/Z/P%9eYq!K@PH96FYGe+VW)5DYQNtTq7kIU^YJMEqlRf%UAROPB#9/(SY8eWVHFp\\rdJZML^k(Sz58Xq6)IK9DGq+@B#SQTBGq8OlEYItNRkRR^UMHEqX@B#P/dL#9rML58!qW)IpqKZcFkHUEe(N\\6=j+RDPBH6R^l)Lt+5IWVOpcZAQJX@/ed\\rYT_RD9IG=HKEVN)L%tUR8M5PP(Z)ANlML895kJ!IrPOtTV+\\WVB#9AkpUzMI%%GzD76eUflrS=^VB#PAfZ%%9/EcS)kNB9A9@+c8#F_qWqXepWqS^_qYd_+cUR5TO%_tL#%9AYT_EKYGeZVq=!L7\\dDAXz9XBF!HcG%FQXROP5e6##de_6WQPBr+IJ8_qtTJ@ZkIU=)MHEq6IW9rXEV",
        // rot 90, flip
		"k6cq_WBVMPPGAHF#kB(YE)eBIWG=tq9BIO(=Q6k9R#S8q5Y=UQ%!LSA#%tZHJRHrRSzelDqX=PFL#^9P%T)KX^UM^Q5WRY!p)BQ7%_@AGVAE@lELUT8VfQK=MrX\\9q+fz+NV/)e5MBXH%N@RH+RdAYcZD\\lNeL(8HGqFUtPOEIODYd8%7WM)dtN!Eq6pATHkqJPAT_#%6VLL\\+\\qq8)\\Rq9%685X_+F9eB8%r56WXOIrO76BI_ezEc_/U#9tYI=)@lKdPkFUWq69KUqEf95UTWjIBE9JBIY/9t#XYRWclAkR_V+p#YDZ#UGZrT#BG5qSrkJ8RORqPIGM9^e/XJdFeTX)Sp!MDpDK/tqL/Y+PE@e!ZOek=UI59cPZdN+^(JV%VZ_HV%pN^zrPIZBcLR@kSMW9"
	};
	
	/** original 340 + transformations */
	public static String[] cipher340 = {
		// original
		cipher[0],
		// rot 90, no flip
		">|+R|ylz2U-d(#p_SBNHMF+ccBG6<+z<G589pypEDk)T.XF9cRlM2+RMp:+RHdW+31NSl/U+JK^+7cB>NWCLz*^yR5VbfqFz^M(pp<z1B:f#Jt++j%ltl+#lk7W6K45+|E^Z#;Oj8UO^StcC(92N*|JRO2-d*Z%VzBP<OC4|5D+2+U*|VGDPZ_O+pEb5TYOF_cd53WWkOYSF^>.F4BpBNXCFp(Y|8OHl.VcBMp7cYGkPO).1ABTWfUVc.b<yzVF++L<L|*/BMZ4(+TFA+.>&+#*TK-(|q5t;&MB6@z24RzKG;C))G-+8BKy4LLDkKHf2+cpL2++RFO-K9|(/2J)d",
		// rot 0, flip 
		"d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>",
		// rot 90, flip
		"+cpL2++RFO-K9|(/2J)d;C))G-+8BKy4LLDkKHf2K-(|q5t;&MB6@z24RzKG|*/BMZ4(+TFA+.>&+#*TABTWfUVc.b<yzVF++L<L8OHl.VcBMp7cYGkPO).1OYSF^>.F4BpBNXCFp(Y|Z_O+pEb5TYOF_cd53WWkzBP<OC4|5D+2+U*|VGDPStcC(92N*|JRO2-d*Z%Vk7W6K45+|E^Z#;Oj8UO^p<z1B:f#Jt++j%ltl+#lNWCLz*^yR5VbfqFz^M(pHdW+31NSl/U+JK^+7cB>Dk)T.XF9cRlM2+RMp:+RMF+ccBG6<+z<G589pypE>|+R|ylz2U-d(#p_SBNH"
	};

	/* the Zodiac alphabets */
	public static final String[] alphabet = {
		"ABCDEFGH|JKLMNOPRSTUVWXYZ123456789plkdfycjqbtz()>^+.<-/#_@*%&;:",
		"ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefjklpqrtz!#%()=+/@\\^_",
		"#%)*+/123456789=>@ABCDEFGIJKLMNPQRSTVWXYZ_bcfjklnpqtxyz"
		//"ABDEFGHIJKLMNOPQRSTUVWXYZ56789defklpqrtz!#%()*+/@\\^", // alphabet for 100 char subset of solved cipher
		//"BCDEFGHIJKLMNOPRSTUVWYZ12345789cdfjklptyz#%&()+-./:<>\\^_", // alphabet for 100 char subset of unsolved cipher
		//"ABEFGKORSTUVWXYZ589cdepq#%+@^_", // alphabet for the 51 chars of the 408
		//null
	};
	public static HashMap<Character, Integer> alphabetHash; // used to speed up the getDecoderForWordAtPosition method.
	
	/* known solutions (as decoders) */
	public static final String[] solutions = {
		"UNKNOWN", // 63 chars
	 //ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefklpqrtz!#$%()*+/@\^_
		"wlnesattfsthenifgaoibeouetesaivocdxiaemrrdollnhpeksrny" // 54 chars
		//"wlnesattfsthenifgaoibeouetesaiocdiaemrrdollhnpeksrn",
		//null,
		//"wlesasngaoibeouetaivocemllesny", // the 30 chars of the 54
	};
	
	public static Map<Character, Character> solutionKey;
	static {
		solutionKey = new HashMap<Character, Character>();
		for (int i=0; i<alphabet[1].length(); i++)
			solutionKey.put(alphabet[1].charAt(i), solutions[1].charAt(i));
	}
	
	public static final String[] interestingDecoders = {
		"llvicdlotkrbeggbwmnsehontiitaapinegimteseielldstbirvkaeeibhsgtw", // the word "arrest" appears; "killinglhave" appears
		"isntgveocgaptnelfckausosrmglhlcfnisnyrmebgvjrhtoujscrvyraceeret", // "buttons cops ever july never rest save scream some something stop street streets thing"
		"isntgveocgaptnelfckausosrmglhlnfnisnyrmebgvjrhtoujscrvyrareeret", // "buttons cops ever july never nice rest save scream some something stop street streets thing", "sychrenicaes" appears at end (close match to "sfchronicles")
		"tienswhislfewqeznxcesokcsiyaiesdpwmbaqesrgvmkrentoovwecsufneddd" //  "been isnt kind legs move once rife same seen sept shoe west wire wise work zodiac" 
	};
	
	/* zodiac's first cipher decoded */
	public static final String firstCipherDecoded = "ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemostdangertueanamalofalltokillsomethinggivesmethemostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesfoemyafterlifeebeorietemethhpiti";

	/* Order of frequency of single letters */
	public static final String letterFrequencies = "etoanirshdlcwumfygpbvkxqjz";
	/* Order of frequency of single letters (from wikipedia) */
	public static final String letterFrequenciesWiki = "etaoinshrdlcumwfgypbvkjxqz";
	public static final double[] expectedFrequencies = {
		0.08167,   //a
		0.01492,   //b
		0.02782,   //c
		0.04253,   //d
		0.12702,   //e
		0.02228,   //f
		0.02015,   //g
		0.06094,   //h
		0.06966,   //i
		0.00153,   //j
		0.00772,   //k
		0.04025,   //l
		0.02406,   //m
		0.06749,   //n
		0.07507,   //o
		0.01929,   //p
		0.00095,   //q
		0.05987,   //r
		0.06327,   //s
		0.09056,   //t
		0.02758,   //u
		0.00978,   //v
		0.02360,   //w
		0.00150,   //x
		0.01974,   //y
		0.00074   //z
	};
	
	/* Order of frequency of digraphs */ 
	public static final String[] digraphs = {"th", "er", "on", "an", "re", "he", "in", "ed", "nd", "ha", "at", "en", "es", "of", "or", "nt", "ea", "ti", "to", "it", "st", "io", "le", "is", "ou", "ar", "as", "de", "rt", "ve"}; 
	
	/* Order of frequency of trigraphs */ 
	public static final String[] trigraphs = {"the", "and", "tha", "ent", "ion", "tio", "for", "nde", "has", "nce", "edt", "tis", "oft", "sth", "men"};

	/* Order of frequency of most common doubles */
	public static final String[] doubles = {"ss", "ee", "tt", "ff", "ll", "mm", "oo"};
	
	/* common words */
	public static final String[] commonWords = {"the", "of", "and", "to", "in", "a", "is", "that", "be", "it", "by", "are", "for", "was", "as", "he", "with", "on", "his", "at", "which", "but", "from", "has", "this", "will", "one", "have", "not", "were", "or", "all", "their", "an", "i", "there", "been", "many", "more", "so", "when", "had", "may", "today", "who", "would", "time", "we", "about", "after", "dollars", "if", "my", "other", "some", "them", "being", "its", "no", "only", "over", "very", "you", "into", "most", "than", "they", "day", "even", "made", "out", "first", "great", "must", "these", "can", "days", "every", "found", "general", "her", "here", "last", "new", "now", "people", "public", "said", "since", "still", "such", "through", "under", "up", "war", "well", "where", "while", "years", "before", "between", "country", "debts", "good", "him", "interest", "large", "like", "make", "our", "take", "upon", "what"};
	
	
	/* population size */
	public static final int POPULATION_SIZE = 1000;
	
	/* load population from a file? */
	public static final boolean POP_LOAD = false;
	//public static final String POP_FILE = "./good-decoders-cipher1-2007-04-03";
	public static final String POP_FILE = "./population-cipher1-gen350-2007-04-10--00-4-668.txt";
	
	/* whether or not to perform crossover */
	public static final boolean CROSSOVER = true;
	
	/* max number of crossover splice points */
	public static final int MAX_CROSSOVER = 10;

	/* niche tabu size */
	public static final int NICHE_TABU_SIZE = POPULATION_SIZE/10;
	
	/* mutate approx 1 out of every MUTATION_CYCLE individuals */
	public static final int MUTATION_CYCLE = 200;
	/* force the mutation operations to increase fitness */
	// obsoleted  public static final boolean MUTATION_FORCE_BETTER = false;
	
	/* mutate a single allele (one character in the decoder)? */
	//public static final boolean MUTATE_ALLELE = true;
	/* mutate by swapping two alleles */
	//public static final int MUTATE_SWAP = 1;
	
	/* genes are considered very similar if fewer than SIMILAR positions differ between their decoders */
	public static final int SIMILAR = alphabet[CIPHER].length()/10;
	
	/* dump top DUMP_TOP every DUMP_EVERY generations */
	public static final int DUMP_TOP = Math.min(POPULATION_SIZE,2500);
	public static final int DUMP_EVERY = 50;
	
	/* initialize with a word in each position of the decoded ciphertext */
	public static final String FIXED_WORD = "kill";
	/* if true, avoid doing any operation that destroys a fixed word in the decoded cipher. */
	public static final boolean PROTECT_FIXED_WORD = false;
	
	/* keep a collection of BEST unique genes to use for niching, to enforce diversity */ 
	TreeSet bestGenes;
	//HashMap bestGeneIndex; // indexed by decoder, to guarantee uniqueness.

	/* perform niching? */
	public static final boolean NICHE = false;
	/* how big is the "best gene" list in percentage terms of the population */
	public static final double NICHE_SIZE = 0.33;
	/* after NICHE_INTERVAL generations, the niching operation is performed (bottom NICHE_SIZE percentage of poor fitness Genes are replaced with unique, best Genes */
	public static final int NICHE_INTERVAL = 100;
	
	/* population */
	public CipherGene[] population;
	
	/* Randomizer */
	public MersenneTwisterFast rand;
	
	public double bestFitness;
	public CipherGene bestGene;
	
	public int bestZodiacScore = Integer.MIN_VALUE;
	public double bestZodiacMatch = Double.MIN_VALUE;
	public int generation;
	public int nicheCount;
	public double lastThree[] = {0,0,0};
	
	public String[] letterPool;
	
	/* cipher alphabet frequencies per letter */ 
	public HashMap alphabetFrequenciesHash;
	public double alphabetFrequencies[];

	/* hashmaps of n-graph / frequency mappings */
	HashMap frequency2graph;
	HashMap frequency3graph;
	HashMap frequency4graph;
	
	BufferedWriter output;
	int mutationType;
	
	public Zodiac() {
		rand = new MersenneTwisterFast(new Date().getTime());
		bestFitness = Double.MIN_VALUE;
		generation = 0;
		bestGenes = new TreeSet(new GeneComparator());
		nicheCount = (int) Math.floor((double)POPULATION_SIZE * NICHE_SIZE);
		initAlphabetFrequencies();
		initLetterPool();
		//frequency2graph = ZodiacDictionary.measureNgraphs(2);
		//frequency3graph = ZodiacDictionary.measureNgraphs(3);
		//frequency4graph = ZodiacDictionary.measureNgraphs(4);
		try {
			//output = new BufferedWriter( new FileWriter("/Users/doranchak/projects/work/java/zodiac/Zodiac-output-" + new Date().getTime() + ".txt") );
			output = new BufferedWriter( new FileWriter("./Zodiac-output-" + new Date().getTime() + ".txt") );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Can't proceed.");
		}
		write("          ENCODING: " + ENCODING);
		write("            CIPHER: " + CIPHER);
		write("        ciphertext: " + cipher[CIPHER]);
		write("   cipher alphabet: " + alphabet[CIPHER]);
		write("          pop size: " + POPULATION_SIZE);
		write("        crossover?: " + CROSSOVER);
		write("       # xover pts: " + MAX_CROSSOVER);
		write("        crossover?: " + CROSSOVER);
		write("    MUTATION_CYCLE: " + MUTATION_CYCLE);
		write("           SIMILAR: " + SIMILAR);
		write("          DUMP_TOP: " + DUMP_TOP);
		write("        DUMP_EVERY: " + DUMP_EVERY);
		write("        FIXED_WORD: " + FIXED_WORD);
		write("PROTECT_FIXED_WORD: " + PROTECT_FIXED_WORD);
		write("             NICHE: " + NICHE);
		write("        NICHE_SIZE: " + NICHE_SIZE);
		write("    NICHE_INTERVAL: " + NICHE_INTERVAL);
		write("        nicheCount: " + nicheCount);
		write("  fitness function: fitness = fitness = coverFactor*fitnessZodiacWords");
		
	}
	
	/* make a letter pool that we can select random letters from.  this letter pool has a random distribution
	 * that approximates the expected letter frequency distribution.
	 */ 
	public void initLetterPool() {
		int[] counts = new int[expectedFrequencies.length];
		int total = 0;
		for (int i=0; i<expectedFrequencies.length; i++) {
			counts[i] = (int)Math.round(1000*expectedFrequencies[i]);
			total += counts[i];
		}
		letterPool = new String[total];
		char letter;
		int index = 0;
		for (int i=0; i<counts.length; i++) {
			for (int j=0; j<counts[i]; j++) {
				letter = (char)(97+i);
				letterPool[index] = ""+letter;
				index++;
			}
		}
	}
	
	/* get letters pseudorandomly, with expected distribution of letter frequencies. */
	public static String getLetter() {
		return CipherGene.getLetter();
		//return letterPool[Gene.rand().nextInt(letterPool.length)];
	}
	
	/* init population */
	public void init() {
		population = new CipherGene[POPULATION_SIZE];
		
		if (POP_LOAD) {
			write("loading pop from [" + POP_FILE + "]");
			loadPopulation(new File(POP_FILE));
			write("loaded pop, size is " + population.length);
		} else {
			for (int i=0; i<POPULATION_SIZE; i++) {
				if (i % 100 == 0) say("at individual #" + i);
				//population[i] = new Gene();
				/*Gene gene = new Gene();
				gene.resetDecoder();
				gene.wordAdd("killing", 5);
				gene.wordAdd("slaves", 296);
				gene.resetDecoderFromWords();
				gene.fitness();
				population[i] = gene;
	*/
				population[i] = new CipherGene(new int[] {1,2} /*dummy*/);
				population[i].randomize();
				population[i].fitness();
			}
		}
	}
	
	public void loadPopulation(File aFile) {
    //...checks on aFile are elided
    StringBuffer contents = new StringBuffer();
    int counter = 0;
    //declared here only to make visible to finally clause
    BufferedReader input = null;
    try {
      //use buffering, reading one line at a time
      //FileReader always assumes default encoding is OK!
      input = new BufferedReader( new FileReader(aFile) );
      String line = null; //not declared within while loop
      /*
      * readLine is a bit quirky :
      * it returns the content of a line MINUS the newline.
      * it returns null only for the END of the stream.
      * it returns an empty String if two newlines appear in a row.
      */
      while (( line = input.readLine()) != null){
        contents.append(line);
        counter++;
       // contents.append(System.getProperty("line.separator"));
      }
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex){
      ex.printStackTrace();
    }
    finally {
      try {
        if (input!= null) {
          //flush and close both "input" and its underlying FileReader
          input.close();
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    //String[] decoders = contents.toString().split(System.getProperty("line.separator"));
    CipherGene[] pop = new CipherGene[counter];
    counter=0;
    try {
      //use buffering, reading one line at a time
      //FileReader always assumes default encoding is OK!
      input = new BufferedReader( new FileReader(aFile) );
      String line = null; //not declared within while loop
      /*
      * readLine is a bit quirky :
      * it returns the content of a line MINUS the newline.
      * it returns null only for the END of the stream.
      * it returns an empty String if two newlines appear in a row.
      */
      while (( line = input.readLine()) != null){
        //contents.append(line);
        //counter++;
      	pop[counter] = new CipherGene(new int[] {1,2} /*dummy*/);
      	pop[counter].decoder = new StringBuffer(line);
      	pop[counter].fitness();
      	counter++;
       // contents.append(System.getProperty("line.separator"));
      }
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex){
      ex.printStackTrace();
    }
    finally {
      try {
        if (input!= null) {
          //flush and close both "input" and its underlying FileReader
          input.close();
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    
    
    population = pop;
  }

	
	public void initAlphabetFrequencies() {
		alphabetFrequenciesHash = new HashMap();
		String c;
		for (int i=0; i<cipher[CIPHER].length(); i++) {
			c = ""+cipher[CIPHER].charAt(i);
			if (alphabetFrequenciesHash.get(c) == null)
				alphabetFrequenciesHash.put(c, new Integer(0));
			alphabetFrequenciesHash.put(c, new Integer(((Integer)alphabetFrequenciesHash.get(c)).intValue()+1));
		}
		alphabetFrequencies = new double[alphabet[CIPHER].length()];
		for (int i=0; i<alphabet[CIPHER].length(); i++) {
			String key = ""+alphabet[CIPHER].charAt(i);
			Double freq = new Double(((Integer)alphabetFrequenciesHash.get(key)).doubleValue() / cipher[CIPHER].length());
			alphabetFrequenciesHash.put(key, freq);
			//alphabetFrequencies[i] = ((Integer)alphabetFrequenciesHash.get(""+alphabet[CIPHER].charAt(i))).doubleValue() / cipher[CIPHER].length();
			//say("alpha freq " + key + " is " + freq);
		}
			
		
	}
	
	public void initWordAtAllPositions() {
		HashMap positions = new HashMap();
		int counter = 0;
		int index;
		int length = cipher[CIPHER].length();
		CipherGene gene;
		int found;
		int numOfPos = length - FIXED_WORD.length() - 1;
		ArrayList list;
		String d;
		//say(cipher[CIPHER]);
		//say(alphabet[CIPHER]);
		for (int i=0; i < population.length; i++) {
			index = i % numOfPos; // position within cipher
			//say(i + " before: " + population[i].decode());
			for (int j=0; j<FIXED_WORD.length(); j++) {
				char c = cipher[CIPHER].charAt(index+j);
				//say("c " + c);
				for (int k=0; j<alphabet[CIPHER].length(); k++) {
					if (alphabet[CIPHER].charAt(k) == c) {
						//say("c " + c + " " + alphabet[CIPHER].charAt(k));
						char[] newD = population[i].decoder.toString().toCharArray();
						newD[k] = FIXED_WORD.charAt(j);
						population[i].decoder = new StringBuffer(new String(newD));
						break;
					}
				}
			}
			//say(i + "  after: " + population[i].decode());
		}
		
	}
	
	/* get a random word from the zodiac dictionary.  restrict word length to given bounds, inclusive.
	 * if interesting is true, then return "interesting" words 70% of the time. */ 
	public static String getWord(int lenMin, int lenMax, boolean interesting) {
		String word = "";
		int chance = CipherGene.rand().nextInt(10);
		while (word.length() < lenMin || word.length() > lenMax) {
			if (chance < 7 && interesting) { // 70% of the time, focus on "interesting" words
				word = ZodiacDictionary.zodiacWordsInteresting[CipherGene.rand().nextInt(ZodiacDictionary.zodiacWordsInteresting.length)];
			}
			else { // 30% of the time, focus on all words that appeared in zodiac letters 
				word = ZodiacDictionary.zodiacWords[CipherGene.rand().nextInt(ZodiacDictionary.zodiacWords.length)];
				word = ZodiacDictionary.zodiacWords[CipherGene.rand().nextInt(ZodiacDictionary.zodiacWords.length)];
			}
		}
		return word;
	}
	
	public static CipherGene initRandomWordAtPosition(CipherGene gene) {
		String word = "";
		CipherGene newGene = new CipherGene(new int[] {1,2} /*dummy*/);
		newGene.decoder = gene.decoder;
		

		word = getWord(1,100,false);
		//say("word " + word);
		int index = CipherGene.rand().nextInt(cipher[CIPHER].length() - word.length());
		
		newGene.decoder = new StringBuffer(getDecoderForWordAtPosition(newGene.decoder.toString(), word, index, false));

		newGene.fitness();
		return newGene;
	}
	
	public static void initPluggerHash() {
		say("initPluggerHash...");
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
		//gene.resetDecoder();
		pluggerHash = new HashMap();
		String word;
		for (int i=0; i<Zodiac.PLUGGER.length; i++) {
			word = Zodiac.PLUGGER[i];
			for (int pos=0; pos<Zodiac.cipher[Zodiac.CIPHER].length() - word.length(); pos++) {
				pluggerHash.put(gene.decoder+"/"+word+"/"+pos, getDecoderForWordAtPosition(gene.decoder.toString(), word, pos, false));
			}
		}
		say("initPluggerHash done! hash size " + pluggerHash.size());
	}
	
	/** initialize the alphabet hash. */
	public static void initAlphabetHash() {
		alphabetHash = new HashMap<Character, Integer>();
		for (int i=0; i<alphabet[CIPHER].length(); i++) {
			alphabetHash.put(alphabet[CIPHER].charAt(i), new Integer(i));
		}
	}
	
	/* compute a new decoder given an old decoder and a word 
	 * we want to insert into the decoded ciphertext at the given position.
	 * if rejectConflict is true, then detect the case where the word cannot be in this position,
	 * because of conflicting mappings to cipher letters, and return a null decoder.
	 **/ 
	public static String getDecoderForWordAtPosition(String decoder, String word, int index, boolean rejectConflict) {
		if (alphabetHash == null) initAlphabetHash(); 
		StringBuffer newDecoder = new StringBuffer(decoder);
		char c;
		char check;
		int pos;
		for (int j=0; j<word.length() && (index+j) < cipher[CIPHER].length(); j++) {
			c = cipher[CIPHER].charAt(index+j);
			pos = alphabetHash.get(c);
			if (rejectConflict) {
				check = newDecoder.charAt(pos);
				if (check != '?' && check != word.charAt(j)) {
					//System.out.println("check " + check + ", word char " + j + " " + word.charAt(j) + " pos " + pos);
					return null;
				}
			}
			newDecoder.setCharAt(pos, word.charAt(j));
		}
		//pluggerHash.put(decoder+"/"+word+"/"+index, newDecoder); // update hash for later quickness
		return newDecoder.toString();
	}
	
	/* the older, slower, non-hashed decoder-from-word computer. */
	public static String getDecoderForWordAtPositionSlowerOld(String decoder, String word, int index) {
		//if (pluggerHash == null) initPluggerHash();
		//String hashed = (String) pluggerHash.get(decoder+"/"+word+"/"+index);
		//hashed=null;
		/*if (hashed != null && !(hashed.equals(""))) {
			//say("hit!!!");
			pluggerHit++;
			//say("hit " + pluggerHit + ", miss " + pluggerMiss);
			return hashed;
		}*/
		//say("wtf?" + decoder + "/" + word + "/" + index);
		//pluggerMiss++;
		//say("hit " + pluggerHit + ", miss " + pluggerMiss);
		String newDecoder = decoder;
		for (int j=0; j<word.length() && (index+j) < cipher[CIPHER].length(); j++) {
			char c = cipher[CIPHER].charAt(index+j);
			//say("c " + c);
			for (int k=0; j<alphabet[CIPHER].length(); k++) {
				if (alphabet[CIPHER].charAt(k) == c) {
					//say("c " + c + " " + alphabet[CIPHER].charAt(k));
					char[] newD = newDecoder.toCharArray();
					newD[k] = word.charAt(j);
					//say("c " + c + " j " + j + " k " + k + " alphabet cipher " + alphabet[CIPHER].charAt(k) + " oldchar " +newGene.decoder.charAt(k) + " newD[k] " + newD[k]);
					newDecoder = new String(newD);
					break;
				}
			}
		}
		//pluggerHash.put(decoder+"/"+word+"/"+index, newDecoder); // update hash for later quickness
		return newDecoder;
	}
	
	/* breed */
	public void breed() {
		CipherGene[] newPopulation = new CipherGene[POPULATION_SIZE];
		CipherGene mother;
		CipherGene father;
		CipherGene spawn;
		THashSet decoders = new THashSet();
		
		/* hash a count-by-decoder, to enforce diversity in the population */
		HashMap decodersNiche = new HashMap();
		
		double totalFitness = 0;
		
		int countMother = 0;
		int countFather = 0;
		int countCrossed = 0;
		int countSpawn = 0;

		double bestFitnessInGen = Double.MIN_VALUE;
		CipherGene bestGeneInGen = null;
		
		int countRepeat;
		int countTotalRepeats = 0;
		
		int dupeSpawn = 0;
		
		double speedSelectCrossover;
		double speedMutate;
		double speedAll;
		
		long timeStart;
		long timeStartAll;

		long timeTotalSelectCrossover = 0;
		long timeTotalMutate = 0;
		long timeTotalAll = 0;
		
		boolean goodSpawn;
		
		countRepeat = 0;
		
		for (int i=0; i<POPULATION_SIZE; i++) {
			goodSpawn = false;
			timeStartAll = new Date().getTime();
			if (i % 1000 == 0) say("at individual #" + i);
		
			timeStart = new Date().getTime();
			mother = select();
			father = mother;
			while (CROSSOVER && father != null && father.decoder.equals(mother.decoder)) // ensure no self-breeding
			father = select();
			
			CipherGene crossed = crossover(mother, father);
			
			/* spawn should only be true if we decided not to perform crossover. */
			if (crossed == null) {
				crossed = population[i];
				father = population[i];
				mother = population[i];
			}
			
			timeTotalSelectCrossover += new Date().getTime() - timeStart;

			timeStart = new Date().getTime();
			spawn = mutate(crossed);
			timeTotalMutate += new Date().getTime() - timeStart;

			//say("spawn fit " + spawn.fitness);
			/* choose the best of mother, father, and spawn */
			
			
			if (!CROSSOVER && father.shitness >= mother.shitness && father.shitness >= spawn.shitness && father.shitness >= crossed.shitness) {
				spawn = father; // father has best fitness
				countFather++;
			} else if (!CROSSOVER && mother.shitness >= father.shitness && mother.shitness >= spawn.shitness && mother.shitness >= crossed.shitness) {
				spawn = mother; // mother has best fitness
				countMother++;
			} else if (!CROSSOVER && crossed.shitness >= father.shitness && crossed.shitness >= mother.shitness && crossed.shitness >= spawn.shitness) {
				spawn = crossed;
				countCrossed++;
			} else { // otherwise, spawn has best fitness
				goodSpawn = true;
				countSpawn++;
				if (!CROSSOVER) {  // when we don't crossover, each individual mutates until it improves, independent of other individuals.
					// Also, if the spawn is better, but we already have this individual, discard this spawn since it'd be a dupe.
					if (decoders.contains(spawn.decoder)) {
						dupeSpawn++;
						spawn = father; // sorry, buddy.  you have good fitness but you're old news.  this is sorta like a tabu search.
					}
					else { 
						/*write("IMPROVED INDIVIDUAL! New fitness: " + spawn.fitness + ", old fitness " + father.fitness + ", mutationType " + mutationType + ", decoder " + spawn.decoder + ", decodes to " + spawn.decode()
								 + ", zodiacWords " + spawn.foundWordsZodiac);
						try {
							output.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new RuntimeException("FLUSH BROKE");
						}*/
						//spawn.debug();
					}
				}
			}
			
			/*
			if (CROSSOVER) {
				Integer val = (Integer)decodersNiche.get(spawn.decoder); // implement niching strategy using a niched tabu list.
				   // only keep NICH_TABU_SIZE copies of any given individual.
				if (goodSpawn && (val == null || val.intValue() < NICHE_TABU_SIZE)) {
					if (val == null) decodersNiche.put(spawn.decoder, new Integer(1));
					else decodersNiche.put(spawn.decoder, new Integer(val.intValue()+1));
				} else if (countRepeat <= -1) {
					i--;
					//if (i == -1) i = 0;
					countRepeat++;
					countTotalRepeats++;
					//say("sorry, i decremented to " + i + ", countRepeat " + countRepeat + ", val for this decoder was " + val + ", goodSpawn " + goodSpawn);
					if (countRepeat % 100 == 0) {
						write("had to repeat " + countRepeat + " times");
					}
					continue;
				}
			}
			else {
				decoders.add(spawn.decoder);
			}
			countRepeat = 0;
*/
			
			newPopulation[i] = spawn;
			
			totalFitness += spawn.shitness;
			
			if (spawn.shitness > bestFitness) {
				bestFitness = spawn.shitness;
				bestGene = new CipherGene(new int[] {1,2} /*dummy*/);
				bestGene.decoder = spawn.decoder;
				bestGene.fitness();
				
				write("BETTER! generation " + generation + " fitness " + spawn.shitness + " words " + spawn.foundWords + " foundWordsZodiac " + spawn.foundWordsZodiac);
				write(spawn.getDebug());
				try {
					output.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException("FLUSH BROKE");
				}
			}
			
			if (spawn.shitness > bestFitnessInGen) {
				bestFitnessInGen = spawn.shitness;
				bestGeneInGen = new CipherGene(new int[] {1,2} /*dummy*/);
				bestGeneInGen.decoder = spawn.decoder;
			}

			
			if (spawn.zodiacScore > bestZodiacScore) {
				bestZodiacScore = spawn.zodiacScore;
				write("ZODIAC SCORE IMPROVEMENT! generation " + generation + "zodiac score " + spawn.zodiacScore + " zodiac match " + spawn.zodiacMatch + " zodiac words " + spawn.zodiacWords + " fitness " + spawn.shitness + " words " + spawn.foundWords);
				write(spawn.getDebug());
				try {
					output.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException("FLUSH BROKE");
				}
			}
		
			if (spawn.zodiacMatch > bestZodiacMatch) {
				bestZodiacMatch = spawn.zodiacMatch;
				write("ZODIAC MATCH IMPROVEMENT! generation " + generation + "zodiac score " + spawn.zodiacScore + " zodiac match " + spawn.zodiacMatch + " zodiac words " + spawn.zodiacWords + " fitness " + spawn.shitness + " words " + spawn.foundWords);
				write(spawn.getDebug());
				try {
					output.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException("FLUSH BROKE");
				}
			}
			if (NICHE)
				track(spawn);

			timeTotalAll += new Date().getTime() - timeStartAll;
		}
		
		speedSelectCrossover = (double)1000*POPULATION_SIZE/timeTotalSelectCrossover;
		speedMutate = (double)1000*POPULATION_SIZE/timeTotalMutate;
		speedAll = (double)1000*POPULATION_SIZE/timeTotalAll;
		
		
		if (NICHE) {
			write("generation " + generation + ", average fitness: " + (totalFitness / (double)POPULATION_SIZE) + ", best fitness: " + bestFitness + ", best size " + bestGenes.size() +
				", best min " + ((CipherGene)bestGenes.last()).shitness + ", best max " + ((CipherGene)bestGenes.first()).shitness + 
				", rateFather " + nformat((double)countFather/POPULATION_SIZE) + 
				", rateMother " + nformat((double)countMother/POPULATION_SIZE) + 
				", rateCrossed " + nformat((double)countCrossed/POPULATION_SIZE) + 
				", rateSpawn " + nformat((double)countSpawn/POPULATION_SIZE) + 
				", countTotalRepeats " + countTotalRepeats + 
				", dupeSpawn " + dupeSpawn + 
				", speedSX " + nformat(speedSelectCrossover) + 
				", speedMutate " + nformat(speedMutate) + 
				", speedAll " + nformat(speedAll) + 
				", generation time " + nformat(timeTotalAll/1000));
		}

		else {
			write("generation " + generation + ", average fitness: " + (totalFitness / (double)POPULATION_SIZE) + ", best fitness: " + bestFitness +  
					", rateFather " + nformat((double)countFather/POPULATION_SIZE) + 
					", rateMother " + nformat((double)countMother/POPULATION_SIZE) + 
					", rateCrossed " + nformat((double)countCrossed/POPULATION_SIZE) + 
					", rateSpawn " + nformat((double)countSpawn/POPULATION_SIZE) +
					", countTotalRepeats " + countTotalRepeats + 
					", dupeSpawn " + dupeSpawn + 
					", speedSX " + nformat(speedSelectCrossover) + 
					", speedMutate " + nformat(speedMutate) + 
					", speedAll " + nformat(speedAll) + 
					", generation time " + nformat(timeTotalAll/1000));
		}
		if (bestGeneInGen != null) {
			bestGeneInGen.fitness();
			write("BEST GENE (THIS GENERATION): ");
			write(bestGeneInGen.getDebug());
		}
		if (bestGene != null) {
			write("BEST GENE (ALL TIME): ");
			write(bestGene.getDebug());
		}

		
		if (generation % DUMP_EVERY == 0) {
			Arrays.sort(newPopulation, new GeneComparator());
			write("====== POP DUMP, TOP " + DUMP_TOP + " INDIVIDUALS ================");
			for (int i=0; i<DUMP_TOP; i++) {
				write(newPopulation[i].getDebug());
			}
			write("====== END POP DUMP, TOP " + DUMP_TOP + " INDIVIDUALS ================");
			try {
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			savePopulation(generation);
		}
		
		
		population = newPopulation;
		generation++;
		
		
		if (NICHE) {
			THashSet distinct = new THashSet();
			for (int i=0; i<population.length; i++)
				distinct.add(population[i].decoder);
			
			write("distinct count " + distinct.size());
			
			lastThree[0] = lastThree[1];
			lastThree[1] = lastThree[2];
			lastThree[2] = totalFitness / (double)POPULATION_SIZE;
			
			
			if ((generation >= NICHE_INTERVAL && generation % NICHE_INTERVAL == 0) || ((generation > 100) && lastThree[0] == lastThree[1] && lastThree[1] == lastThree[2]) || distinct.size() < 10)
				niche(); 
			
		}
		
		
	}
	
	public static CipherGene mutate(CipherGene oldGene) { return mutate(oldGene, null); }
	
	public static CipherGene mutate(CipherGene oldGene, int[] counts) {
		//String decoder = gene.decoder;
		//double fitness = gene.fitness;
		//double newFitness = Double.MIN_VALUE;
		//int wordPos = gene.decode().indexOf(FIXED_WORD);
		
		
		// removed due to refactoring:  if (MUTATION_CYCLE == 0 || Gene.rand().nextInt(MUTATION_CYCLE) == 0) {
			CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
			gene.decoder = oldGene.decoder;
			gene.shitness = oldGene.shitness;
			gene.wordIndex = oldGene.wordIndexCopy();
			
			//if (ENCODING == 0) { // mutations on the decoder itself 
				
				//if (tries % 1000 == 0) write("tries " + tries + " new " + newFitness + " old " + fitness);
				//int which = Gene.rand().nextInt(8);
			int which = -1;
			int betterWhich = CipherGene.rand().nextInt(2);
				
				//int which = 9;
				//mutationType = which;
				//int which = 5;
				//say("which which " + which);
				//say("decoder before " + decoder);
				if (which == 0) { // swap a random number of positions of the decoder
					StringBuffer c;
					for (int a=0; a<1+CipherGene.rand().nextInt(10); a++) {
						int i; int j;
						c = new StringBuffer(gene.decoder);
						i = CipherGene.rand().nextInt(c.length());
						j = i;
						while (j==i) j = CipherGene.rand().nextInt(c.length());
						char tmp = c.charAt(i);
						c.setCharAt(i, c.charAt(j));
						//c[i] = c[j];
						c.setCharAt(j, tmp);
						//c[j] = tmp;
						gene.decoder = new StringBuffer(c.toString());
					}
					gene.fitness();
				} else if (which == 1) { // randomize a random number of positions of the decoder
					StringBuffer c = new StringBuffer(gene.decoder);
					for (int a=0; a<1+CipherGene.rand().nextInt(10); a++) {
						int split = CipherGene.rand().nextInt(c.length());
						c.setCharAt(split, getLetter().charAt(0));
					}
					gene.decoder = new StringBuffer(c.toString());
					gene.fitness();
				} else if (which == 2) { // randomize the entire decoder
					gene = new CipherGene(new int[] {1,2} /*dummy*/);
					gene.randomize();
					gene.fitness();
				} else if (which == 3) { // shift positions by one (left or right)
					int offset = CipherGene.rand().nextInt() == 0 ? -1 : 1;
					int j;
					char[] newD = new char[gene.decoder.length()];
					for (int i=0; i<gene.decoder.length(); i++) {
						j = i+offset;
						if (j<0) j=gene.decoder.length()-1;
						else if (j>gene.decoder.length()-1) j = 0;
						newD[j] = gene.decoder.charAt(i);
					}
					gene.decoder = new StringBuffer(new String(newD));
					gene.fitness();
				} else if (which == 4 || betterWhich == 0) { // add a frequent character somewhere 
					int j = CipherGene.rand().nextInt(9);
					char[] newD = gene.decoder.toString().toCharArray();
					newD[CipherGene.rand().nextInt(gene.decoder.length())] = letterFrequencies.charAt(j);
					gene.decoder = new StringBuffer(new String(newD));
					gene.fitness();
				} else if (which == 5) { // shift positions, all possible, until max fitness found.  well, that was way too slow.
					// so, let's just shift some random amount.
					String bestDecoder = gene.decoder.toString();
					double bestFitness = Double.MIN_VALUE;
					String decoder = gene.decoder.toString();
					//for (int offset=1; offset<gene.decoder.length()-1; offset++) {
					int offset = 1 + CipherGene.rand().nextInt(gene.decoder.length()-2);
						int j;
						char[] newD = new char[gene.decoder.length()];
						for (int i=0; i<gene.decoder.length(); i++) {
							j = i+offset;
							
							//if (j<0) j=gene.decoder.length()-1;
							//else if (j>gene.decoder.length()-1) j = 0;
							newD[j % gene.decoder.length()] = decoder.charAt(i);
						}
						gene.decoder = new StringBuffer(new String(newD));
						gene.fitness();
						//say("5 decoder " + gene.decoder + " fitness " + gene.fitness);
						/*if (gene.fitness > bestFitness) {
							//say("best fit " + bestFitness);
							bestFitness = gene.fitness;
							bestDecoder = gene.decoder;
						}*/
					//gene.decoder = bestDecoder;
					//gene.fitness();
				} else if (which == 6) {
					// try to guess a largish zodiac-ish word
					//say("before zodiacwords " + oldGene.foundWordsZodiac);
					mutateGuessZodiac(gene);
					gene.fitness();
					//say("after zodiacwords " + gene.foundWordsZodiac);
					
				} else if (which == 7 || betterWhich == 1){ // inject a random common word somewhere in the decoded cipher as a "guess"
					gene = initRandomWordAtPosition(gene);
				}
				
				if (counts != null && gene.shitness > oldGene.shitness)
					counts[which == -1 ? betterWhich : which]++;
				
				return gene;
				
			}
//			else { // mutations of the word list
//				return null; // BROKEN FOR NOW DUE TO REFACTORING
/*				
				int which = Gene.rand().nextInt(8);
				//int which = 0;
				mutationType = which;
				
				if (which == 0) { // replace one word with another
					int r = gene.wordRandom();
					if (r == -1) return oldGene;
					String word = gene.wordAt(r);
					String replacement = getWord(1,100,false);
					gene.wordRemove(r);
					while (!gene.wordFits(replacement, r)) {
						replacement = getWord(1,100,false);
					}
					gene.wordAdd(replacement, r);
					
				} else if (which == 1) { // swap two words
					// does not do "fits" checking.  thus, word removal may result, causing lesser fitness.
					if (gene.wordCount() < 2) return oldGene;
					int i1;
					int i2;
					int itmp;
					String w1;
					String w2;
					String wtmp;
					
					i1 = gene.wordRandom();
					i2 = i1;
					while (i1 == i2) {
						i2 = gene.wordRandom();
					}
					w1 = gene.wordAt(i1);
					w2 = gene.wordAt(i2);
					gene.wordRemove(i1);
					gene.wordRemove(i2);
					gene.wordAdd(w1, i2);
					gene.wordAdd(w2, i1);
					
				} else if (which == 2) { // add a new word somewhere
					mutateWordAdd(gene);
					
				} else if (which == 3) { // remove one of the words
					if (gene.wordCount() == 0) return oldGene;
					gene.wordRemove(gene.wordRandom());
				
				} else if (which == 4) { // shift a word a random amount left or right
					int r = gene.wordRandom();
					if (r == -1) return oldGene;
					int spacesLeft = gene.wordSpacesToLeft(r);
					int spacesRight = gene.wordSpacesToRight(r);
					boolean canLeft = spacesLeft > 0;
					boolean canRight = spacesRight > 0;
					
					int choice;
					if (canLeft && canRight) {
						choice = Gene.rand().nextInt(1);
					} else if (!canLeft && !canRight) {
						return oldGene;
					} else {
						choice = (canLeft ? 0 : 1);
					}
					
					int positions;
					if (choice == 0) {
						positions = 1+(spacesLeft == 1 ? 0 : Gene.rand().nextInt(spacesLeft-1));
						for (int i=0; i<positions; i++) {
							gene.wordShiftLeft(r-i);
						}
					} else {
						positions = 1+(spacesRight == 1 ? 0 : Gene.rand().nextInt(spacesRight-1));
						for (int i=0; i<positions; i++) {
							gene.wordShiftRight(r+i);
						}
					}
					
				} else if (which == 5) { // perturb empty spaces in the word sequence by randomizing one of the wildcard characters in the decoder
					// NOTE: this mutation is discarded because of the resetDecoderFromWords call at the end of mutate.
					if (gene.decoder.indexOf('?') == -1) return oldGene;
					StringBuffer d = new StringBuffer(gene.decoder);
					int where;
					for (int i=0; i<100; i++) {
						where = Gene.rand().nextInt(d.length());
						if (d.charAt(where) == '?') {
							d.setCharAt(where, (char)(97+Gene.rand().nextInt(26)));
							gene.decoder = d.toString();
							break;
						}
					}
				} else if (which == 6) { // random word misspellings: 
					if (gene.wordCount() == 0) return oldGene;
					
					int type = Gene.rand().nextInt(3);
					int r = gene.wordRandom();
					StringBuffer word = new StringBuffer(gene.wordAt(r));
					
					
					if (type == 0) { // take a letter and randomize it (using expected values from letter frequencies)
						gene.wordRemove(r);
						word.setCharAt(Gene.rand().nextInt(word.length()), getLetter().charAt(0));
						gene.wordAdd(word.toString(), r);
					} else if (type == 1) { // remove a random letter
						gene.wordRemove(r);
						if (word.length() == 1) {
							;
						} else {
							word.deleteCharAt(Gene.rand().nextInt(word.length()));
							gene.wordAdd(word.toString(), r);
						}
					} else { // insert a random letter  (expectedly)
						word.insert(Gene.rand().nextInt(word.length()), getLetter());
						gene.wordRemove(r);
						gene.wordAdd(word.toString(), r);
					}
				} else if (which == 7) { // remove a random word, then add a random word, in one step.
					
					if (gene.wordCount() > 0) 
						gene.wordRemove(gene.wordRandom());

					mutateWordAdd(gene);

				}
				gene.resetDecoderFromWords(); // remove remnants in the old decoder from old word positions
				gene.fitness();
				return gene;*/
		//	}
		// refactored: } else
		// refactored: 	return oldGene;
	 // }
	
	/* broken due to refactoring.
	public void mutateWordAdd(Gene gene) {
		int tries = 0;
		int i = Gene.rand().nextInt(cipher[CIPHER].length());
		String word = getWord(1,100,false);
		while (!gene.wordFits(word, i)) {
			i = Gene.rand().nextInt(cipher[CIPHER].length());
			word = getWord(1,100,false);
			tries++;
			if (tries == 100) return; // give up
		}
		gene.wordAdd(word, i);
	}
	*/
	public static void mutateGuessZodiac(CipherGene gene) {
		String word;
		String d = gene.getDecoded().toString();
		int index;
		for (int l=7; l>3; l--) {
			for (int i=0; i<ZodiacDictionary.zodiacWordsInteresting.length; i++) {
				word = ZodiacDictionary.zodiacWordsInteresting[i];
				if (word.length() == l) {
					index = fuzzyMatch(d, word);
					if (index > -1 && d.indexOf(word) == -1) {
						//say("filling in fuzzy match for word " + word);
						gene.decoder = new StringBuffer(getDecoderForWordAtPosition(gene.decoder.toString(), word, index, false));
						//say("decoded has word? " + (gene.decode().indexOf(word) > -1));
					}
				}
			}
		}
		
	}
	
	/* 2-player tournament selection */ 
	public CipherGene select() {
		if (CROSSOVER) {
			CipherGene player1 = population[CipherGene.rand().nextInt(POPULATION_SIZE)];
			CipherGene player2 = population[CipherGene.rand().nextInt(POPULATION_SIZE)];
			if (player1 == null || player2 == null) throw new RuntimeException("WTF?");
			if (player1.shitness >= player2.shitness) return player1;
			else return player2;
		}
		else return null;
	}
	
	/* simple crossover */
	public CipherGene crossover(CipherGene mother, CipherGene father) {
		
		if (CROSSOVER) { 
			if (ENCODING == 0) {
				int which = CipherGene.rand().nextInt(3);
				if (which == 0) { // randomly exchange chunks of decoder 
					/* only crossover if the fixed word is in the same position in the mother and father */
					if (PROTECT_FIXED_WORD)
						if (mother.getDecoded().indexOf(FIXED_WORD) != father.getDecoded().indexOf(FIXED_WORD)) return null;
					
					CipherGene spawn = new CipherGene(new int[] {1,2} /*dummy*/);
					
					int total = CipherGene.rand().nextInt(MAX_CROSSOVER) + 1;
					THashSet  points = new THashSet();
					while (points.size() < total)
						points.add(new Integer(1 + CipherGene.rand().nextInt(alphabet[CIPHER].length()-2)));
					
					Object[] splices = points.toArray();
					int pos = 0;
					int i = 0;
					CipherGene current = mother;
					char[] result = new char[mother.decoder.length()];

					while (pos < result.length) {
						if (i < splices.length && pos == ((Integer)splices[i]).intValue()) {
							current = (current == mother ? father : mother);
							i++;
						}
						result[pos] = current.decoder.charAt(pos);
						pos++;
					}
					spawn.decoder = new StringBuffer(new String(result));
					
					if (PROTECT_FIXED_WORD) 
						if (spawn.getDecoded().indexOf(FIXED_WORD) == -1) return null;
					
					spawn.fitness();
					return spawn;
					
				} else if (which == 1) { // take one character randomly from one decoder and put it in the other.  do it for each.
					CipherGene spawn1 = new CipherGene(new int[] {1,2} /*dummy*/);
					CipherGene spawn2 = new CipherGene(new int[] {1,2} /*dummy*/);
					
					StringBuffer d1 = new StringBuffer(mother.decoder);
					StringBuffer d2 = new StringBuffer(father.decoder);
					
					int x=0; int y=0;
					while (x==y) {
						x = CipherGene.rand().nextInt(mother.decoder.length());
						y = CipherGene.rand().nextInt(mother.decoder.length());
					}
					d1.setCharAt(x, d2.charAt(x));
					d2.setCharAt(y, d2.charAt(y));
					spawn1.decoder = new StringBuffer(d1);
					spawn2.decoder = new StringBuffer(d2);
					
					spawn1.fitness();
					spawn2.fitness();
					
					if (spawn1.shitness > spawn2.shitness) return spawn1;
					else return spawn2;
				} else { // position-wise mingle each decoder; return the best one
					CipherGene spawn1 = new CipherGene(new int[] {1,2} /*dummy*/);
					CipherGene spawn2 = new CipherGene(new int[] {1,2} /*dummy*/);
					
					StringBuffer d1 = new StringBuffer(mother.decoder);
					StringBuffer d2 = new StringBuffer(father.decoder);
					
					for (int i=0; i<d1.length(); i++) {
						if (CipherGene.rand().nextInt(1) == 0) {
							d1.setCharAt(i, mother.decoder.charAt(i));
							d2.setCharAt(i, father.decoder.charAt(i));
						} else {
							d2.setCharAt(i, mother.decoder.charAt(i));
							d1.setCharAt(i, father.decoder.charAt(i));
						}
					}
					
					spawn1.decoder = new StringBuffer(d1);
					spawn2.decoder = new StringBuffer(d2);
					
					spawn1.fitness();
					spawn2.fitness();
					
					if (spawn1.shitness > spawn2.shitness) return spawn1;
					else return spawn2;
				}
			}
			else {
				/* broken due to refactoring  
				Gene spawn = new Gene();
				spawn.resetDecoder();
				int point = 1 + Gene.rand().nextInt(alphabet[CIPHER].length()-2);
				Iterator it = mother.wordIndex.keySet().iterator();
				Integer key;
				String value;
				while (it.hasNext()) {
					key = (Integer)it.next();
					if (key.intValue() < point) {
						value = (String)mother.wordIndex.get(key);
						spawn.wordAdd(value, key.intValue());
					}
				}
				it = father.wordIndex.keySet().iterator();
				while (it.hasNext()) {
					key = (Integer)it.next();
					if (key.intValue() >= point) {
						value = (String)father.wordIndex.get(key);
						spawn.wordAdd(value, key.intValue());
					}
				}
				spawn.resetDecoderFromWords();
				spawn.fitness();
				return spawn;*/
				return null;
			}
			
		} else
			return null;
	}
	
	public static boolean similar(CipherGene gene1, CipherGene gene2) {
		int counter = 0;
		for (int i=0; i<gene1.decoder.length(); i++) {
			if (gene1.decoder.charAt(i) != gene2.decoder.charAt(i)) {
				counter++;
			}
		}
		if (counter < SIMILAR) return true;
		else return false;
	}
	
	/* check if we need to add this gene to the "top" list for niching */
	public void track(CipherGene gene) {
		//Object[] best = bestGenes.toArray();
		CipherGene item;
		Iterator it = bestGenes.iterator();
		while (it.hasNext()) {
			item = (CipherGene) it.next();
			if (similar(gene, item)) { // clustering scheme: avoid local minima
				if (gene.shitness > item.shitness) {
					bestGenes.remove(item);
					bestGenes.add(gene);
					if (bestGenes.size() > nicheCount) {
					  Object o = bestGenes.last();
						bestGenes.remove(o);
						if (bestGenes.size() > nicheCount) throw new RuntimeException("bloody hell");
					}
				}
				return;
			}
		}
		
	  bestGenes.add(gene);
		if (bestGenes.size() > nicheCount) {
		  Object o = bestGenes.last();
			bestGenes.remove(o);
			if (bestGenes.size() > nicheCount) {
				if (o == null) say("o was NULL!??");
				else {
					say("o " + o.getClass().getName());
					//((Gene)o).debug();
				}
				CipherGene poo = new CipherGene(new int[] {1,2} /*dummy*/);
				poo.decoder = ((CipherGene)o).decoder;
				say("equals? " + poo.equals(o) + "," + o.equals(poo));
				throw new RuntimeException("bloody hell");
			}
		}
	}
	
	/* niching */
	public void niche() {
		write("niching with " + bestGenes.size() + " best genes");
		Object[] best = bestGenes.toArray();
		CipherGene gene;
		for (int i=0; i<(3*population.length/4); i++) {
			if (i < nicheCount) {
				gene = (CipherGene)best[i % best.length];
				if (i < 200) write("niche, top 200, #" + i + " " + gene.decoder + ", fitness " + gene.shitness);
				population[i] = gene;
			} else {
				gene = new CipherGene(new int[] {1,2} /*dummy*/);
				gene.randomize();
				gene.fitness();
				population[i] = gene;
			}
		}
		//Gene newGene;
		//Arrays.sort(population, new GeneComparator());
		//for (int i=0; i<population.length; i++) {
			//write("niche: replacing gene of fitness " + population[POPULATION_SIZE-1 -i].fitness + " with gene of fitness " + ((Gene)best[i]).fitness);
		/*	if (i < population.length/2) {
				population[i]= ((Gene)best[i % best.length]);
			} else {
				newGene = new Gene();
				newGene.randomize();
				newGene.fitness();
				population[i] = newGene;
			}
			
		}*/
		
	}

	/* returns true if the decoded string given matches fuzzyWord minus one character */
	public static int fuzzyMatch(String decoded, String fuzzyWord) {
		char[] c;
		String d = decoded;
		for (int j=0; j<fuzzyWord.length(); j++) {
			c = fuzzyWord.toCharArray();
			c[j] = '.';
			//say("matches " + new String(c));
			if (Pattern.matches(".*" + new String(c) + ".*", d)) {
				String test = d;
				test = test.replaceAll("(.*)" + new String(c) + "(.*)", "$1");
				return test.length();
				//fitnessZodiacWordsFuzzy++;
				//say("got a match for " + new String(c));
				//return true;
			}
		}
		return -1;
	}
	
	
	
	public static void say(String msg) {
		System.out.println(new Date() + " : " + new Date().getTime() + ": " + msg);
	}
	
	public void write(String msg) {
		write(msg, output);
	}
	
	public static void write(String msg, BufferedWriter output) {
		try {
			say(msg);
			output.write(new Date() + " : " + new Date().getTime() + ": " + msg + System.getProperty("line.separator"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("File done broke.");
		}
		
	}
	
	

	
	/* gene factory that produces genes with approximations of "decent" decoders */
	public CipherGene makeGene() {
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
		ArrayList zodiac = new ArrayList();
		ArrayList toRemove = new ArrayList();
		int breakCounter = 0;
		int num ;
		int k;
		Object item;
		char[] newDecoder = new char[alphabet[CIPHER].length()];
		double freq = 0;
		for (int i=0; i<alphabet[CIPHER].length(); i++) {
			zodiac.add(""+alphabet[CIPHER].charAt(i));
		}
		for (int i=0; i<newDecoder.length; i++)
			newDecoder[i] = 'e';
		
		for (int i=0; i<26; i++) { // for each decoded letter
			/* choose a random number of zodiac letters that can map to the current decoded letter */
			freq = -10;
			breakCounter = 0;
			while (Math.abs(freq - expectedFrequencies[i]) > 0.005 && breakCounter < 100000) {
				freq = 0;
				for (int j=0; j<toRemove.size(); j++) {
					item = toRemove.get(j);
					zodiac.add(item);
					toRemove.remove(j);
				}
				//num = 1+ (int) Math.round(((double)Gene.rand().nextInt(8) * (expectedFrequencies[i]/0.12702)));
				num = (int) Math.round((alphabet[CIPHER].length()) * expectedFrequencies[i]);
				//say("num " + num);
				for (int j=0; j<num; j++) {
					k = CipherGene.rand().nextInt(zodiac.size());
					item = zodiac.get(k);
					toRemove.add(item);
					zodiac.remove(k);
					//say("item " + item);
					freq += ((Double)alphabetFrequenciesHash.get(item)).doubleValue();
				}
				breakCounter++;
				say("i " + i + " expect " + expectedFrequencies[i] + " freq " + freq);
			}
			if (breakCounter >= 10000) say ("wow, we broke.");
			for (int j=0; j<toRemove.size(); j++) {
				char c = ((String)toRemove.get(j)).charAt(0);
				for (int m=0; m<alphabet[CIPHER].length(); m++) {
					if (alphabet[CIPHER].charAt(m) == c) {
						newDecoder[m] = (char)(i+97);
						break;
					}
				}
			}
			
		}
		gene.decoder = new StringBuffer(new String(newDecoder));
		say("decoder " + gene.decoder);
		say("solutio " + solutions[CIPHER]);
		return gene;
	}
	
	public static String nformat(double num) {
    DecimalFormat formatter = new DecimalFormat("############.######");
    return formatter.format(num);
	}
	public static String nformat2(double num) {
    DecimalFormat formatter = new DecimalFormat("############.##############");
    return formatter.format(num);
	}

	public static void testGene () {
		
		Zodiac z = new Zodiac();
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
		gene.randomize();
		gene.fitness();
		gene.phiTest();
		say(gene.getDebug());

/*
		double max = Double.MIN_VALUE;
		for (int i=0; i<100; i++) {
		gene.randomize();
		gene.fitness();
			if (gene.fitnessDigraph > max) {
				max = gene.fitnessDigraph;
				say("new max from random's digraph: " + gene.fitnessDigraph);
			}
		}*/

		/*
		int[] counts = new int[8];
		Gene newGene;
		for (int i=0; i<1000; i++) {
			newGene = mutate(gene, counts);
			if (newGene.fitness > gene.fitness)
				gene = newGene;
		}
		
		for (int i=0; i< counts.length; i++) {
			say("mutate type " + i + ", count " + counts[i]);
		}*/
		
		
		
		/*gene = new Gene();
		gene.resetDecoder();
		gene.wordAdd("killing", 5);
		gene.wordAdd("forrest", 73);
		gene.wordAdd("something", 125);
		gene.wordAdd("paradice", 256);
		gene.wordAdd("slaves", 296);
		gene.resetDecoderFromWords();
		gene.fitness();
		gene.debug();
		*/
		
		
		gene = new CipherGene(new int[] {1,2} /*dummy*/);
		//gene.decoder = "dterhhohpldihrioepaoemssaolhslainevserttsyhoesdneta";
		//gene.decoder = "aenaseauroisernvllogpaaeosttkhdrrnscheswtaiatmttdrd";
		//gene.decoder = "artomiisramenhsgelkdntyttnuaewuhrtaslcinloajsreahki";
		//gene.decoder = "oalpruwasfdegolhtonwitwtsyeaaduhasceomintsscphwaimi";
		gene.decoder = new StringBuffer("sdusdtatlyghifiuglferehoateditlicftginasotrqahvaebclni");
		gene.fitness();
		gene.phiTest();
		gene.getDebug();
		say(gene.getDebug());

		/*
		for (int i = 0; i<100; i++) {
			gene = new Gene();
			gene.randomize();
			gene.decode();
			gene.fitness();
			say("digraph " + gene.fitnessDigraph);
		}*/
		
		//say(" better " + gene.fitnessZodiacWordsFaster(4, 12, true));
		//say(" better " + gene.foundWordsZodiac);
		//say("div 3 " + (gene.fitnessDigraph + gene.fitnessDouble + gene.fitnessTrigraph)/3.0);
		
		ZodiacDictionary.getZodiacCorpusWord(); // force init
		/*
		int[] genome = { 0,0,1,100,2,50,3,25,4,75,5,82,6,125,7,10,8,115,9,200 };
		gene = ZodiacVectorWordIndividual.getGene(genome,0);
		gene.fitnessWord(genome,0);
		say(gene.getDebug());
		
		int[] genome2 = { 7, Gene.getEncodingForScaledPosition(0), 
			9, Gene.getEncodingForScaledPosition(1), 
			44, Gene.getEncodingForScaledPosition(5), 
			42, Gene.getEncodingForScaledPosition(12), 
			191, Gene.getEncodingForScaledPosition(18), 
			168, Gene.getEncodingForScaledPosition(25), 
			100, Gene.getEncodingForScaledPosition(27), 
			296, Gene.getEncodingForScaledPosition(29), 
			126, Gene.getEncodingForScaledPosition(31), 
			244, Gene.getEncodingForScaledPosition(35) 
			};
		gene = ZodiacVectorWordIndividual.getGene(genome2,0);
		gene.fitnessWord(genome2,0);
		say(gene.getDebug());
		say("ngram bad 2 " + (gene.badNGramCount[2] >= 100 ? 0.0 : ((float) 100 - gene.badNGramCount[2])/100.0));
		say("ngram bad 3 " + (gene.badNGramCount[3] >= 200 ? 0.0 : ((float) 200 - gene.badNGramCount[3])/200.0));
		
		int[] genome3 = { 7,  
				9,  
				44,  
				42,  
				191, 
				168, 
				100, 
				296, 
				126, 
				244 
				};
			gene = ZodiacVectorWordIndividual.getGene(genome3,0);
			gene.fitnessWord(genome3,0);
			gene.fitnessNGramFreq();
			say(gene.getDebug());
			
			
			gene = new Gene();
			gene.decoder = "?t??nfhdpt?l?ue?s?eenouai?oo?t??i?j?bm???o??h??tsnr?n?";
			gene.fitness();
			gene.fitnessNGramFreq();
			say(gene.getDebug());
			say("ngram bad 2 " + (gene.badNGramCount[2] >= 100 ? 0.0 : ((float) 100 - gene.badNGramCount[2])/100.0));
			say("ngram bad 3 " + (gene.badNGramCount[3] >= 200 ? 0.0 : ((float) 200 - gene.badNGramCount[3])/200.0));

			
			
			int[] genome4 = { 
						52+153*2, 5, // killing
						42+153*2, 12, // people
						51+153*2, 18, // because
						132+153*2, 25, // itis
						132+153*2, 38, // itis
						52+153*2, 53, // killing
						51+153*2, 80, // because
						119+153*2, 90, // isthe
						73+153*2, 125, // something
						83+153*2, 358 // collecting
					};
			gene = ZodiacVectorWordIndividual.getGene(genome4,0);
			gene.fitnessWord(genome4,0);
			say(gene.getDebug());
			say("blah " + gene.totalConflicts);

			//[killing (292)]  [people (163)]  [because (6)]  [itis (386)]  [itis (64)]  [killing (197)]  [because (229)]  [isthe (46)]  [something (101)]  [collecting (362)]  
			int[] genome5 = { 
					52+153*2, 292, // killing
					42+153*2, 163, // people
					51+153*2, 6, // because
					132+153*2, 386, // itis
					132+153*2, 64, // itis
					52+153*2, 197, // killing
					51+153*2, 229, // because
					119+153*2, 46, // isthe
					73+153*2, 101, // something
					83+153*2, 362 // collecting
				};
		gene = ZodiacVectorWordIndividual.getGene(genome5,0);
		gene.fitnessWord(genome5,0);
		say(gene.getDebug());
		say("bleh " + gene.totalConflicts);
			*/
	/*	
		int[] genome6 = {
				Gene.rand().nextInt(500), Gene.rand().nextInt(500), Gene.rand().nextInt(500), Gene.rand().nextInt(500), Gene.rand().nextInt(500), Gene.rand().nextInt(500), Gene.rand().nextInt(500), Gene.rand().nextInt(500), Gene.rand().nextInt(500), Gene.rand().nextInt(500)
		};
		gene = ZodiacVectorWordIndividual.getGene(genome6,0);
		gene.fitnessWord(genome6,0);
		say(gene.getDebug());
*/		
		int[] genome7 = {
			5, 12, 18, 25, 38, 53, 80, 90, 125, 358
			  //5, 12, 42, 25, 38, 53, 80, 90, 125, 70
				//265,143,357,344,233,265,357,62,248,399
			//246,264,358,12,12,246,358,222,229,311
			//112,143,60,215,344,158,130,2,116,375
			//53,12,80,25,97,5,80,232,125,358
			//53,12,323,339,38,150,80,210,125,358 
		};
		gene = new CipherGene(genome7);
		//gene.genome = genome7;
		gene.fitnessWordSimple();
		gene.fitnessDictionaryWords(2, 10, true, false, true, null);
		say(gene.getDebug());
		say("fitnessWordSimple: " + gene.fitnessWord);

		gene.decoder=new StringBuffer("?lnesattfs?hinisgtiibeouetesaihoc?i?emkt?ol?lh?peks?n?");
		gene.decode();
		gene.zodiacScore();
		say("zm " + gene.getDebug());
		
		//gene.resetDecoder();
		//say("decode! " + Zodiac.getDecoderForWordAtPosition(gene.decoder.toString(), "killing", 112));
		
		/*
		int[] genome8 = {
				//5, 12, 18, 25, 38, 53, 80, 90, 125, 358
				39,167,83,341,238,74,272,94,62,357
				  // plugger [killing (39)]  [people (167)]  [because (83)]  [itis (341)]  [itis (238)]  [killing (74)]  [because (272)]  [isthe (94)]  [something (62)]  [collecting (357)]  

			};
			gene = ZodiacVectorWordIndividual.getGene(genome8,0);
			gene.fitnessWord(genome8,0);
			say(gene.getDebug());
			gene.fitnessWordSimple(genome8);
			say("fitnessWordSimple: " + gene.fitnessWord);

			*/
/*		String[] dd = {"kl??i???e?t?enitg?p?ini?????li???????o?i??l?sh??ek?g?p",
"kl??i???e?t?enitg?p?ini??te?li?oc????o?i??l?sh??ek?g?p",
"luehtii??tnb?es?????s?lsi??i?ctc????i?e?oga???b??est?e",
"kl??i???e?t?enitg?o?ini??te??i?oc????l?i??l?sh??pkpg?e",
"????p??k??i?g???ki?i?????ii?o?p????g???lt?eilnstl?es??",
"?????cbue????sitgi???im??tse????i????oa?hs????ne??????",
"?i?neug??e???h??e???eblsc???????a?t??i???ik?s??tc?l??s",
"?e?iikggil??e???iennis?el?l?hui?ttksni????scibcogall?t",
"eii?kl??g????it?s?mong?eii???clin?l?l?t?c?thi??etgnh?o",
"ilnbp?e?e?autni?g??tbeeus???ai??csei??k??ol?is??eks?cp",
"cs?i??i??tgt??a????ieil?bnii?a???ee?bl??glu??k?s????s?",
"?cuhle?ent??onaosig???ctt???gbc????i?s?m?ie?l???e?i?ti",
"ilnbp?e?e?auinisg?itbeeus???ai??cse???kt?ol?i???eks?cp",
"s???p??ka?i?g?euki?i??c??ii?o?p????g???lt?eilnstlbes??",
"s?i?p??ka?i?g?euki?i??c??i?so?pt???g???lt?e?lns?lbei??",
"?i?neug?pe???h??e???eblsc??????ea?t?lio??ik?s??tc?lp?s",
"ilnbp?eietau?ni?g??tbeeus???ai??cse???k??ol?is??eks?cp",
"?pnl?en?????kbe?tu?ioe????lii???lkge??ssg?????iap???c?",
"e??em?inapbe????l?p?ti?lcelhtghus??k?ss??te?????i?neio",
"?pnl?eno??g?kbe?tu?ioe???nlii???lkgestssgh???miap???c?",
"??e???st??g??????i???i?p?nii?h?o?????ls??l?i?k?tp??sie"};

		for (int i=0; i<dd.length; i++) {
		gene = new Gene();
		gene.decoder = dd[i];
		gene.fitnessNGramFreq();
		say(gene.fitnessNGrams[1] + "," + gene.fitnessNGrams[2] + "," + gene.fitnessNGrams[3] + "," + gene.badNGramCount[1] + "," + gene.badNGramCount[2] + "," + gene.badNGramCount[3]);
		}
	*/	
		
		/*
		int[] genome8 = {
				236, 403, 185, 163, 23, 313, 283, 404, 328, 222  	
			};
			gene = ZodiacVectorWordIndividual.getGene(genome8,0);
			gene.fitnessWord(genome8,0);
			say(gene.getDebug());
			
		gene = new Gene();
			gene.decoder = solutions[1];
			gene.fitness();
			gene.fitnessAlphaFreq();
			gene.phiTest();
			say(gene.getDebug());
			say("ngram bad 2 " + (gene.badNGramCount[2] >= 100 ? 0.0 : ((float) 100 - gene.badNGramCount[2])/100.0));
			say("ngram bad 3 " + (gene.badNGramCount[3] >= 200 ? 0.0 : ((float) 200 - gene.badNGramCount[3])/200.0));
		*/	
			/* complexification 
			for (int i=0; i<solutions[1].length(); i++) {
				System.out.println(solutions[1].charAt(i) + " " + alphabet[1].charAt(i));
			} */
	}
	
	public static void testGene2() {
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
		gene.decoder = new StringBuffer("?cc??o?ue??o??io?bt??i????l???i???sli???t????dor??p?e?t????????");
		say(gene.decode());
	}
	
	public static void testCrossover() {
		Zodiac z = new Zodiac();
		CipherGene mother = new CipherGene(new int[] {1,2} /*dummy*/);
		mother.randomize();
		mother.fitness();
		CipherGene father = new CipherGene(new int[] {1,2} /*dummy*/);
		father.randomize();
		father.fitness();
		CipherGene spawn = z.crossover(mother, father);
		say(mother.getDebug());
		say(father.getDebug());
		say(spawn.getDebug());
		int count = 0;
		for (int i=0; i<100; i++) {
			mother = new CipherGene(new int[] {1,2} /*dummy*/);
			mother.randomize();
			mother.fitness();
			father = new CipherGene(new int[] {1,2} /*dummy*/);
			father.randomize();
			father.fitness();
			spawn = z.crossover(mother, father);
		  //spawn = z.mutate(spawn);
			if (spawn.shitness > father.shitness && spawn.shitness > mother.shitness)
				count++;
			
			say("mother " + mother.shitness + ", father " + father.shitness + ", spawn "+ spawn.shitness);
		}
		say(z.nformat((double)count/100) + " improvement rate");
	}
	
	public static void testFitness() {
		Zodiac z = new Zodiac();
		CipherGene mother = new CipherGene(new int[] {1,2} /*dummy*/);
		mother.randomize();
		mother.fitness();
		say(mother.getDebug());
	}
	
	public static void evolve() {
		Zodiac z = new Zodiac();
		z.init();
		//z.initWordAtAllPositions();
		
		while (true) {
			z.breed();
		}
	}
	
	public void savePopulation(int generation) {
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd--HH-s-SSS");
		Date d = new Date();
		try {
			//output = new BufferedWriter( new FileWriter("/Users/doranchak/projects/work/java/zodiac/Zodiac-output-" + new Date().getTime() + ".txt") );
			BufferedWriter f = new BufferedWriter( new FileWriter("./population-cipher" + CIPHER + "-gen" + generation + "-" + s.format(d) + ".txt") );
			for (int i=0; i<population.length; i++) {
				f.write(population[i].decoder + System.getProperty("line.separator"));
			}
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Can't proceed.");
		}

	}
	
	public static void brute() {
		Zodiac z = new Zodiac();
		z.init();
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
		gene.randomize();
		gene.fitness();
		
		double bestFitness = gene.shitness;
		
		CipherGene newGene;
		
		newGene = new CipherGene(new int[] {1,2} /*dummy*/);
		newGene.decoder = gene.decoder;
		newGene.fitness();
		
		while (true) {
			while (newGene.shitness <= bestFitness) {
				newGene = new CipherGene(new int[] {1,2} /*dummy*/);
				newGene.decoder = gene.decoder;
				int i; int j;
				if (CipherGene.rand().nextInt(1) == 0) {
					for (int k=0; k<CipherGene.rand().nextInt(10); k++) {
					char[] c = newGene.decoder.toString().toCharArray();
					i = CipherGene.rand().nextInt(c.length);
					j = i;
					while (j==i) j = CipherGene.rand().nextInt(c.length);
					char tmp = c[i];
					c[i] = c[j];
					c[j] = tmp;
					newGene.decoder = new StringBuffer(new String(c));
					}
					newGene.fitness();
				} else {
					for (int k=0; k<CipherGene.rand().nextInt(10); k++) {
					char[] c = newGene.decoder.toString().toCharArray();
					i = CipherGene.rand().nextInt(c.length);
					c[i] =  (char)(CipherGene.rand().nextInt(26)+97);
					newGene.decoder = new StringBuffer(new String(c));
					}
					newGene.fitness();
				}
			}
			say("NEW BEST!  " + gene.shitness);
			gene = newGene;
			bestFitness = gene.shitness;
			say(gene.getDebug());
		}
	}
	public static void testBest() {
		Zodiac z = new Zodiac();
		TreeSet bestGenes = new TreeSet(new GeneComparator());
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
		gene.decoder = new StringBuffer("aeidmnnwroheohnoaettocsoegrpcattehsileecluyirtarntwwoofsdotesab");
		gene.fitness();
		bestGenes.add(gene);
		gene = new CipherGene(new int[] {1,2} /*dummy*/);
		gene.decoder = new StringBuffer("aeidmnnwroheohnoaettocsoegrpcattehsileecluyirtarntwwoofsdotesab");
		gene.fitness();
		say("best " + bestGenes.contains(gene));
	}
	
	public static void testFreq() {
			Zodiac z = new Zodiac();
			z.init();
			double best;
			for (int i=0; i<1000; i++) {
				say ("i " + i);
				best = 0;
				CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
				gene.fitnessAlpha = 0;
				while (gene.fitnessAlpha < 0.80) {
					if (gene.fitnessAlpha > best) {
						best = gene.fitnessAlpha;
						say("best "+ best);
					}
					gene.randomize();
					gene.fitness();
				}
			}
	}
	
	public static void testKill() {
		Zodiac z = new Zodiac();
		/*Gene gene;
		String d;
		while(true) {
			gene = new Gene();
			gene.randomize();
			d = gene.decode();
			if (d.contains("kill")) {
				say("kill at " + d.indexOf("kill") + ", " + gene.decoder + ", " + d);
			}
			int k = d.indexOf("kill"); 
			if (k == 5 || k == 53 || k == 121 || k == 261) {
				gene.fitness();
				gene.debug();
				break;
			}
		}*/
		z.init();
		z.initWordAtAllPositions();
	}
	
	public static void testRandomWord() {
		Zodiac z = new Zodiac();
		//z.init();
		CipherGene[] pop = new CipherGene[100];
		CipherGene newGene;
		int count = 0;
		double total;
		double bestFitness = 0;
		for (int i=0; i<pop.length; i++) {
			pop[i] = new CipherGene(new int[] {1,2} /*dummy*/);
			pop[i].randomize();
			pop[i].fitness();
		}
		
		while (true) {
			total = 0;
			count++;
			for (int i=0; i<pop.length; i++) {
				newGene = z.initRandomWordAtPosition(pop[i]);
				if (newGene.shitness > pop[i].shitness) {
					//z.write("BETTER " + newGene.fitness + " > " + pop[i].fitness);
					pop[i] = newGene;
					//pop[i].debug();
				}
				total += pop[i].shitness;
				if (newGene.shitness > bestFitness) {
					say("BETTER! new best " + newGene.shitness + ", old best " + bestFitness);
					say(newGene.getDebug());
					bestFitness = newGene.shitness;
				}
			}
			z.write("generation " + count + ", avg fitness " + total/1000);
			
		}
	}
	
	public static void testPhi() {
		Zodiac z = new Zodiac();
		CipherGene gene;
		gene = new CipherGene(new int[] {1,2} /*dummy*/);
		gene.randomize();
		gene.fitness();
int i; int j; String d; char[] c; char tmp;
		double bestMatch = Double.MAX_VALUE;
		double bestFitness = Double.MIN_VALUE;
		while (true) {
			i = CipherGene.rand().nextInt(gene.decoder.length());
			j = CipherGene.rand().nextInt(gene.decoder.length());
			d = gene.decoder.toString();
			c = gene.decoder.toString().toCharArray();
			tmp = c[i];
			c[i] = c[j];
			c[j] = tmp;
			gene.decoder = new StringBuffer(new String(c));
			gene.fitness();
			if (gene.phiPlaintextMatch < bestMatch) {
				z.write("BETTER PLAINTEXT " + gene.phiPlaintextMatch);
				bestMatch = gene.phiPlaintextMatch;
				d = gene.decoder.toString();
				say(gene.getDebug());
			}
			if (gene.shitness > bestFitness) {
				z.write("BETTER FITNESS " + gene.shitness);
				bestFitness = gene.shitness;
				d = gene.decoder.toString();
				say(gene.getDebug());
			}
			gene.decoder = new StringBuffer(d);
		}		
	}
	
	public static void testWord() {
	/*	Zodiac z = new Zodiac();
		Gene gene = new Gene();
		say("first gene");
		gene.randomize();
		gene.fitness();
		gene.debug();
	  z.mutateGuessZodiac(gene);
		gene.fitness();
		gene.debug();
		
		
		say("second gene");
		String d = gene.decode();
		while (fuzzyMatch(d, "anamal") == -1) {
			gene.randomize();
			d = gene.decode();
		}
		gene.fitness();
		gene.debug();
	  z.mutateGuessZodiac(gene);
		gene.fitness();
	  gene.debug();
		*/
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
		//gene.resetDecoder();
		gene.decode();
		
		String d = gene.decoder.toString();
		String word1 = "killing";
		String word2 = "slaves";

		double maxFit = 0.0;
		CipherGene bestGene = null;
		
		String d1;
		for (int i=0; i<alphabet[CIPHER].length() - word1.length(); i++) {
			d1 = getDecoderForWordAtPosition(d, word1, i, false);
			for (int j=0; j<alphabet[CIPHER].length() - word2.length(); j++) {
				gene.decoder = new StringBuffer(getDecoderForWordAtPosition(d1, word2, j, false));
				gene.decode();
				gene.fitnessDictionaryWords(4, 10, true, true, true, null);
				if (gene.fitnessCoverageDictionary > maxFit) {
					maxFit = gene.fitnessCoverageDictionary;
					bestGene = new CipherGene(new int[] {1,2} /*dummy*/);
					bestGene.decoder = gene.decoder;
					bestGene.decode();
					bestGene.fitnessDictionaryWords(4, 10, true, true, true, null);
				}
				/*if (i==5 && j==12) {
					bestGene = gene;
					break;
				}*/
			}
			//if (i==5) break;
		}
		
		say("best gene " + bestGene.getDebug());
		
		
	}
	
	public static void testSpeed() {
		Zodiac z = new Zodiac();
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);

		ZodiacDictionary.computeNGramFrequenciesFromZodiacWords(0.0003f, true);
		long start;
		long total = 0;
		int tests = 500;
		int[] genome;
		for (int i=0; i<tests; i++) {
			//gene.randomize();
			genome = new int[10];
			for (int j=0; j<genome.length; j++) {
				genome[j] = CipherGene.rand().nextInt(500);
			}
			gene.decode();
			start = new Date().getTime();
			gene = new CipherGene(genome);
			//gene.fitnessNGramFreq();
			//gene.genome = genome;
			gene.fitnessWord(0);
			total += new Date().getTime() - start;
		}
		
		say("rate for fitness 1 " + (double)1000*tests/total);
		say("pop size 10,000 would take " + 10000/((double)1000*tests/total) + "seconds");
	}
	
	public static void testEncoding() {
		/*Zodiac z = new Zodiac();
		Gene gene = new Gene();
		gene.randomize();
		gene.fitness();
		gene.debug();
		
		gene = new Gene();
		gene.resetDecoder();
		gene.wordAdd("smeghead", 0);
		gene.wordAdd("fartknocker", 50);
		gene.fitness();
		gene.debug();
		z.say("wordNext " + gene.wordNext(0));
		z.say("wordSpacesToRight " + gene.wordSpacesToRight(0));
		z.say("wordSpacesToRight " + gene.wordSpacesToRight(50));
		z.say("wordSpacesToLeft " + gene.wordSpacesToLeft(0));
		z.say("wordSpacesToLeft " + gene.wordSpacesToLeft(40));
		z.say("wordSpacesToLeft " + gene.wordSpacesToLeft(64));
		z.say("wordNext " + gene.wordNext(0));
		z.say("wordNext " + gene.wordNext(20));
		z.say("wordNext " + gene.wordNext(70));
		z.say("wordNext " + gene.wordNext(cipher[CIPHER].length()-1));
		z.say("wordPrev " + gene.wordPrevious(0));
		z.say("wordPrev " + gene.wordPrevious(20));
		z.say("wordPrev " + gene.wordPrevious(70));
		z.say("wordPrev " + gene.wordPrevious(cipher[CIPHER].length()-1));
		z.say("decoder " + gene.decoder);
		gene.wordAdd("bogus", 5);
		z.say("decoder " + gene.decoder + " " + gene.decode());
		z.say("decoder " + gene.decoder);
		gene.wordAdd("bogus", 7);
		z.say("decoder " + gene.decoder + " " + gene.decode());
		z.say("decoder " + gene.decoder);

		gene.wordAdd("bogus", 8);
		z.say("decoder " + gene.decoder + " " + gene.decode());
		z.say(gene.wordDebugAll());
		int i=8;
		boolean t = gene.wordShiftRight(i);
		i++;
		z.say("decoder " + gene.decoder + " " + gene.decode());
		while (t) {
			t = gene.wordShiftRight(i);
			i++;
			z.say("decoder " + gene.decoder + " " + gene.decode());
		}
		t = true;
		i=45;
		while (t) {
			t = gene.wordShiftLeft(i);
			i--;
			z.say("decoder " + gene.decoder + " " + gene.decode());
		}
		z.say("debug " + gene.wordDebugAll());

		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("random word " + gene.wordRandom());
		z.say("word count " + gene.wordCount());
		gene.wordRemove(0);
		z.say("decoder " + gene.decoder + " " + gene.decode());
		z.say("debug " + gene.wordDebugAll());
		gene.wordRemove(8);
		z.say("word count " + gene.wordCount());
		gene.wordRemove(50);
		gene.wordRemove(51);
		z.say("random word " + gene.wordRandom());
		z.say("word count " + gene.wordCount());
*/
	}
	
	public static void testLetter() {
		Zodiac z = new Zodiac();
		String s = "";
		for (int i=0; i<100; i++) {
			s += z.getLetter();
		}
		say(s);
	}
	
	public static void testLoad() {
		Zodiac z = new Zodiac();
		z.loadPopulation(new File("./good-decoders-cipher1-2007-04-03"));
		z.say("pop len " + z.population.length);
	}
	
	public static void testDate() {
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd--HH-s-SSS");
		Date d = new Date();
		System.out.println(s.format(d));
		
	}
	
	public static void testFit() {
		CipherGene g = new CipherGene(new int[] {1,2} /*dummy*/);
		String word;
		String randWord;
		String r;
		int length;
		while (true) {
			word = "";
			length = 0;
			//g.resetDecoder();
			r = g.decoder.toString();
			while (length < 30) {
				randWord = ZodiacDictionary.zodiacWords[g.rand().nextInt(ZodiacDictionary.zodiacWords.length)]; 
				word += randWord;
				length += randWord.length();
			}
			
			for (int i=0; i<g.decoder.length()-length; i++) {
				g.decoder = new StringBuffer(getDecoderForWordAtPosition(r, word, i, false));
				if (g.decode().contains(word)) {
					say("Word " + word + " fits with decoder " + g.decoder + ", decodes to " + g.decode());
				}
			}
		}
	}
	
	public static void testCorpus() {
		ZodiacDictionary.initZodiacCorpus();
		CipherGene gene = new CipherGene(new int[] {1,2} /*dummy*/);
		
		CipherGene bestGene;
		double bestFitness = 0.0;
		
		int genomeSize = 20;
		int popSize = 3000;
		int startPosition = Zodiac.WORD_ENCODING == 0 ? 334 : 0;
		
		int maxGene = Zodiac.WORD_ENCODING == 0 ? 3800 : 500;
		
		
		CipherGene[] pop = new CipherGene[popSize];
		int[][] gpop = new int[popSize][genomeSize];
		for (int i=0; i<popSize; i++) {
			int[] g = new int[genomeSize];
			for (int j=0; j<genomeSize; j++) {
				g[j] = CipherGene.rand().nextInt(maxGene);
			}
			
			gpop[i] = g;
			gene = new CipherGene(new int[] {1,2} /*dummy*/);
			//gene.resetDecoder();
			pop[i] = gene;
		}
		say("looping");
		int generation = 0;
		int num = 0;
		Zodiac z = new Zodiac();
		while(true) {
			for (int i=0; i<popSize; i++) {
				if (i % 100 == 0) System.out.println("i " + i);
				for (int tries=0; tries<10; tries++) {
					int[] n = new int[genomeSize];
					for (int j=0; j<genomeSize; j++) {
						n[j] = gpop[i][j];
					}
					num = CipherGene.rand().nextInt(genomeSize+1);
					for (int k=0; k<num; k++) {
						n[CipherGene.rand().nextInt(genomeSize)] = CipherGene.rand().nextInt(maxGene);
					}
					gene = new CipherGene(n);
					//gene.genome = n;
					gene.fitnessWord(startPosition);
					//say(""+gene.fitnessWord);
					if (gene.fitnessWord > bestFitness) {
						bestGene = new CipherGene(new int[] {1,2} /*dummy*/);
						bestGene.decoder = gene.decoder;
						bestFitness = gene.fitnessWord;
						z.write("NEW BEST OF RUN!  gen " + generation + " pop " + i + gene.getDebugLine());
						
					}
					if (gene.fitnessWord > pop[i].fitnessWord) {
						z.write("gen " + generation + " pop " + i + gene.getDebugLine());
						gpop[i] = n;
						pop[i] = gene;
						break;
					}
				}
			}
			generation++;
		}
		
	}
	
	public static void testConflicts() {
		THashSet letters;
		int u;
		int bestU;
		int bestP;
		int which=0;
		boolean showSolution = true;
		
		for (int len=3; len<30; len++) {
			bestU = Integer.MAX_VALUE;
			bestP = 0;
			for (int i=0; i<cipher[which].length()-len; i++) {
				letters = new THashSet();
				for (int j=i; j<i+len; j++) {
					letters.add(""+cipher[which].charAt(j));
				}
				u = letters.size();
				if (u < bestU) {
					bestU = u; bestP = i;
					//if (bestU == 30)
						say("len " + len + ", position " + bestP + ", uniqueness " + bestU + ", ciphertext " + (which == 1 && showSolution ? firstCipherDecoded.substring(bestP, bestP+len) : cipher[which].substring(bestP, bestP+len)));
				}
			}
		}
	}
	
	
	public static void testDigraph() {
		CipherGene g = new CipherGene(new int[] {1,2} /*dummy*/);
		g.decoder = new StringBuffer(solutions[1]);
		g.decode();
		//g.fitnessDigraphFreq();
		say(g.getDebug());
		g = new CipherGene(new int[] {1,2} /*dummy*/);
		g.decoder = new StringBuffer("?rv??y?st??c??wd?ac??e????t???i???dhas??n????yth??t?i?i????????");
		g.decode();
		//g.fitnessDigraphFreq();
		say(g.getDebug());
	}
	/* if stochastic is true, get one random position per
	 * random word choice.  otherwise, do pure min-conflict
	 * search by trying all positions per random word choice.
	 */
	public static void testMinConflict(boolean stochastic) {
		Zodiac z = new Zodiac();
		CipherGene tmp = new CipherGene(new int[] {1,2} /*dummy*/);
		//tmp.resetDecoder();
		String decoder = tmp.decoder.toString();
		
		int POP_SIZE = 100;
		int GENOME_SIZE = 10;
		int[][] genomes = new int[POP_SIZE][GENOME_SIZE];
		CipherGene[] genes = new CipherGene[POP_SIZE];
		
		for (int i=0; i<POP_SIZE; i++) {
			int[] genome = new int[PLUGGER.length];
			for (int j=0; j<genome.length; j++) {
				genome[j] = CipherGene.rand().nextInt(500);
			}
			/*int[] genome = { 
					Gene.rand().nextInt(500), // killing
					Gene.rand().nextInt(500), // people
					Gene.rand().nextInt(500), // because
					Gene.rand().nextInt(500), // itis
					Gene.rand().nextInt(500), // itis
					Gene.rand().nextInt(500), // killing
					Gene.rand().nextInt(500), // because
					//Gene.rand().nextInt(500), // isthe
					//Gene.rand().nextInt(500), // something
					//Gene.rand().nextInt(500) // collecting
				};
				*/
			genomes[i] = genome;
			genes[i] = new CipherGene(genomes[i]);
			//genes[i].fitnessWord(genomes[i],0);
			//genes[i].genome = genomes[i];
			genes[i].fitnessWordSimple();
			//z.write(genes[i].getDebug());
			//z.write("blah " + genes[i].totalConflicts);
		}
		
		int which = 0;
		String word;
		//double minConflict; 
		int countMin;
		double[] conflicts;
		CipherGene[] conflictGenes;
		ArrayList best;
		//int prevConflict = gene.totalConflicts;
		//int pos;
		THashSet results = new THashSet();
		int generation = 0;
		CipherGene gene;
		int[] genome;
		double oldFit;
		double bestFit = 0.0;
		double bestOfRun = Double.MIN_VALUE;
		String oldDecoder;
		while(true) {
			//z.write("gen " + generation);
			for (int p=0; p<POP_SIZE; p++) {
				//z.write("gen " + generation + ", pop " + p);
				// pick a random word
				which = CipherGene.rand().nextInt(PLUGGER.length);
				//word = Gene.getWordForGene(genomes[p][which]);
				word = PLUGGER[which];
				//pos = genome[which+1];
				//z.write(word + ":");
				gene = genes[p];
				oldFit = gene.fitnessWord;
				//say("oldFit " + oldFit);
				//z.write("fitness " + gene.fitnessWord);
				genome = genomes[p];
				// try all possible positions to compute the min conflict
				//minConflict = Integer.MAX_VALUE;
				bestFit = Double.MIN_VALUE;
				//z.write("new bestFit " + bestFit);
				conflicts = new double[cipher[CIPHER].length()];
				conflictGenes = new CipherGene[cipher[CIPHER].length()];
				int randpos = CipherGene.rand().nextInt(cipher[CIPHER].length());
				int istart = (stochastic ? randpos : 0);
				int iend = (stochastic ? randpos+1 : cipher[CIPHER].length());
				
				String fits = ""; 
				for (int i=istart; i<iend; i++) {
					int oldval = genome[which];
		  			//oldDecoder = gene.decoders[which];
					//oldFit = gene.fitnessWord;
					genome[which] = i;
	
					CipherGene newGene = new CipherGene(genome);
					// the decoder for this word will change b/c of the word's new position; so we recompute the decoder.
		  			//gene.decoders[which] = Zodiac.getDecoderForWordAtPosition(decoder, word, i);
						
					//newGene.fitnessWord(genome, 0);
					//gene.genome = genome;
					newGene.fitnessWordSimple();
					fits += " " + newGene.fitnessWord;
					//z.write("oldpos " + oldval + ", newpos " + i + ", word " + word + ", new gene " + newGene.fitnessWord + " old fit " + oldFit + ", best fit " + bestFit);
					if (newGene.fitnessWord > bestFit && newGene.fitnessWord > oldFit) {
						//z.write("gen " + generation + ", better fitness: " + newGene.fitnessWord);
						//if (newGene.fitnessWord > bestFit) {
						//	z.write("NEW BEST! " + newGene.getDebugLine());
						bestFit = newGene.fitnessWord;
						conflicts[i] = newGene.fitnessWord;
						conflictGenes[i] = newGene;
						//conflictGenomes[i] = newGene;
						
						//} else if (newGene.fitnessWord >= 190) {
						//	z.write(newGene.getDebugLine());
						//}
						//gene = newGene;
						//genes[p] = gene;
					} else {
						
						//newGene.decoders[which] = oldDecoder;
						//gene.fitnessWord(genome, 0);
						//z.write("kept gene with fitness " + gene.fitnessWord);
					}
					genome[which] = oldval;
				}
				//z.write("fits " + fits);
				if (bestFit > Double.MIN_VALUE) {
					best = new ArrayList();
					for (int i=0; i<conflicts.length; i++) {
						if (conflicts[i] == bestFit) {
							best.add(conflictGenes[i]); // put all top genes in list to pick randomly from.  
						}
					}
					
					genes[p] = (CipherGene) best.get(CipherGene.rand().nextInt(best.size()));
					
					// this commented part breaks this test     genomes[p] = genes[p].genome;
					
					//z.write("best size " + best.size() + ", we picked " + genes[p].encoding + ", fitnessWord " + genes[p].fitnessWord);
					if (bestFit >= 194.0)
						z.write(p + " BETTER! " + bestFit + " "  + genes[p].getDebugLine());
					if (bestFit > bestOfRun) {
						z.write(p + " BEST OF RUN! " + bestFit + " " + genes[p].getDebugLine());
						bestOfRun = bestFit;
					}
				}
					//say("i=" + i + ", total " + gene.totalConflicts);
					//conflicts[i] = gene.totalConflicts;
					//if (gene.totalConflicts < minConflict) {
					//	minConflict = gene.totalConflicts;
						//say("i:" + i + "  new min " + minConflict);
					//}
				//}
			
/*			
				countMin = 0;
				best = new ArrayList();
				for (int i=0; i<conflicts.length; i++) {
					if (conflicts[i] == minConflict) {
						countMin++;
						best.add(new Integer(i));
					}
				}*/
				// snag one of the best minConflict positions at random */
			/*
				int pickOne = ((Integer)best.get(Gene.rand().nextInt(countMin))).intValue();
				genome[which+1] = pickOne; // set the word to sit at a randomly selected min conflict position
				gene = ZodiacVectorWordIndividual.getGene(genome,0);
				
	  		//gene.decoders[which/2] = Zodiac.getDecoderForWordAtPosition(decoder, word, pickOne); // recompute this word's decoder, because we didn't keep it when looping above.
				gene.fitnessWord(genome, 0); // doing again, so it recomputes the proper decoded ciphertext.
				genes[p] = gene; // this should not be necessary.
				
				z.write("minConflict " + minConflict + ", countmin " + countMin + ", picking " + pickOne + ", plugger " + gene.encoding + ", decoder " + gene.decoder + ", decoded " + gene.decoded + ", best words " + gene.unclobberedWords + ", total conflicts " + gene.totalConflicts + ", zodiacMatch " + gene.zodiacMatch);
*/				
				/*if (gene.totalConflicts != prevConflict) {
					z.write("totalConflicts: " + gene.totalConflicts + ", decoder " + gene.decoder + ", decoded " + gene.decoded);
				}*/
				
				/*
				if (gene.totalConflicts == 0 && !results.contains(gene.decoder)) {
					results.add(gene.decoder);
					z.write("WE FOUND AWESOME DECODER #" + results.size() + "!  decoder: " + gene.decoder + ", decodes to " + gene.decoded + ", encoding " + gene.encoding);
				}*/
				
				//prevConflict = gene.totalConflicts;
				
			}
			generation++;
		}
		
	}
	
	public static void testWordDecoders() {
		CipherGene g = new CipherGene(new int[] {1,2} /*dummy*/);
		//g.resetDecoder();
		String d1,d2;
		String w;
		int pos;
		long s = new Date().getTime();
		for (int i=0; i<100000; i++) {
			w = PLUGGER[CipherGene.rand().nextInt(10)];
			pos = CipherGene.rand().nextInt(408);
			
			d1 = getDecoderForWordAtPosition(g.decoder.toString(), w, pos, false);
		}
		System.out.println("d1 " + (new Date().getTime() - s));
		s = new Date().getTime();
		for (int i=0; i<100000; i++) {
			w = PLUGGER[CipherGene.rand().nextInt(10)];
			pos = CipherGene.rand().nextInt(408);
			
			d1 = getDecoderForWordAtPositionSlowerOld(g.decoder.toString(), w, pos);
		}
		System.out.println("d2 " + (new Date().getTime() - s));
	}
	
	/** finds the k cipher letters that, when decoded, yield the maximum number of decoded n-grams. */
	public static void findBestLettersForSearch() {
		int which = 1;
		StringBuffer code;
		String letters;
		int[] best = new int[10];
		int[] counts = new int[10];
		
		char ca, cb, cc, cd, ce;
		for (int a=0; a<alphabet[which].length(); a++) {
			System.out.println("..." + alphabet[which].charAt(a));
			for (int b=a+1; b<alphabet[which].length(); b++) {
				for (int c=b+1; c<alphabet[which].length(); c++) {
					for (int d=c+1; d<alphabet[which].length(); d++) {
						for (int e=d+1; e<alphabet[which].length(); e++) {
							ca = alphabet[which].charAt(a);
							cb = alphabet[which].charAt(b);
							cc = alphabet[which].charAt(c);
							cd = alphabet[which].charAt(d);
							ce = alphabet[which].charAt(e);
							letters = ""+ca+cb+cc+cd+ce;

							counts = findBestLettersForSearch(which, ca,cb,cc,cd,ce);
							for (int i=0; i<9; i++) {
								if (counts[i] >= best[i] && counts[i] > 0) {
									best[i] = counts[i];
									System.out.println("new best " + (i+1) + "-gram count " + best[i] + " for " + letters);
								}
							}
						}
					}
				}
			}
		}
	}
	/* helper for caller */
	public static int[] findBestLettersForSearch(int which, char ca, char cb, char cc, char cd, char ce) {
		StringBuffer code = new StringBuffer(cipher[which]);
		char ch;
		for (int i=0; i<code.length(); i++) {
			ch = code.charAt(i);
			if (ch == ca || ch == cb || ch == cc || ch == cd || ch == ce) {
				code.replace(i, i+1, "|");
			}
		}
		
		int[] counts = new int[10];
		for (int i=0; i<code.length(); i++) {
			for (int j=1; j<10 && i+j-1<code.length(); j++) {
				if (code.charAt(i+j-1) == '|') {
					//System.out.println(j + "-gram found at " + cipher[which].substring(i+j-1, i+j-1+j));
					counts[j-1]++;
				} else {
					break;
				}
			}
		}
		return counts;
		
		
	}
	
	/** finds tightly clustered links.  that is, for every possible 3-letter combination, find where else those 3 letters occur close together. */  
	public static void testFindLinks() {
		int which=1;
		Zodiac z = new Zodiac();
		String word;
		ArrayList letters;
		int size = 3; // word size
		for (int i=0; i<cipher[which].length()-size-1; i++) { // for all words of length 3
			word = cipher[which].substring(i,i+size);
			for (int j=0; j<cipher[which].length()-size-1; j++) { // for all start positions in the cipher 
				if (j<i-size-1 || j>i+size-1) { // avoid positions occupied by comparison word
					letters = new ArrayList();
					String match = "";
					String letter;
					boolean found = false;
					for (int k=0; k<word.length(); k++) letters.add(word.substring(k, k+1)); // draw matches from this list until empty
					for (int k=j; k<cipher[which].length()-size-1; k++) { // keep looking ahead until we find all letters
						if (k<i-size-1 || k>i+size-1) { // avoid positions occupied by comparison word
							letter = cipher[which].substring(k, k+1);
							if (letters.contains(letter)) { // found a letter; remove it from the search list
									letters.remove(letter);
									found = true;
							}
							if (found) match += letter;
							if (letters.isEmpty()) { // search list is empty; thus we found all letters of the word.
								z.write(" word " + word + ", match " + match);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	/** for each string length, find the region in the cipher with the fewest distinct cipher characters.
	 * goal is to reduce the problem search space by finding long enough strings we can search on without
	 * having to search the entire search space.
	 */
	public static void findMaximalCipherMinimalLetters() {
		for (int len=10; len<300; len++) {
			
		}
	}
	
	/** fast brute search for all possible combinations of a 7-letter subset of the decoder.  this sucks because
	 * it is slow and finds numerous, useless solutions. */
	public static void testFastBrute() {
		ZodiacDictionary.initDictionaries();
		int numChars = 7;
		int cipherLength = 9;
		char letterA = 'a', letterZ = 'z';
		char[] decoder = new char[numChars];
		//char[] decoded = new char[9];
		char[] cipher = new char[] {'9','%','P','/','Z','/','U','B','%'};
		THashSet<String> wordHash;
		StringBuffer decoded = new StringBuffer("000000000");
		StringBuffer cover;
		String coverReplace;
		String word;  String wordList;
		for (char a=letterA; a<=letterZ; a++) {
			decoded.setCharAt(0, a);
			for (char b=letterA; b<=letterZ; b++) {
				decoded.setCharAt(1, b);
				decoded.setCharAt(8, b);
				for (char c=letterA; c<=letterZ; c++) {
					decoded.setCharAt(2, c);
					for (char d=letterA; d<=letterZ; d++) {
						decoded.setCharAt(3, d);
						decoded.setCharAt(5, d);
						for (char e=letterA; e<=letterZ; e++) {
							decoded.setCharAt(4, e);
							for (char f=letterA; f<=letterZ; f++) {
								decoded.setCharAt(6, f);
								for (char g=letterA; g<=letterZ; g++) {
									decoded.setCharAt(7, g);
									cover = new StringBuffer(decoded);
									wordList = getUniqueWords(decoded,cover);
									coverReplace = cover.toString().replaceAll("_", "");
									if (coverReplace.length() < 3)
										System.out.println(cover + "," + decoded + ": " + wordList + ", length " + wordList.length());
								}
							}
						}
					}
				}
			}
		}
	}

	public static String getUniqueWords(StringBuffer text, StringBuffer cover) {
		String word;
		String wordList = ""; THashSet<String> wordHash = new THashSet<String>();
		for (int i=3; i<text.length()+1; i++) {
			for (int j=0; j<=text.length()-i; j++) {
				word = text.substring(j, j+i);
				if (ZodiacDictionary.isWord(word, ZodiacDictionary.D_ZODIAC)) {
					if (!wordHash.contains(word)) {
						wordHash.add(word);
						wordList += word + " ";
						cover.replace(j, j+i, COVER[i-1]);
					}
				}
			}
		}
		return wordList;
	}
	
	public static void test29Chars() {

		/* derive decoder for the 29 char chunk --
		String cipherChunk = "", chunkDecoded = "";
		for (int i=333; i<333+29; i++) {
			cipherChunk += Zodiac.cipher[1].charAt(i);
			chunkDecoded += Zodiac.firstCipherDecoded.charAt(i);
		}
		System.out.println(cipherChunk);
		System.out.println(chunkDecoded);
		
		char[] decoder = new char[54];
		for (int i=0; i<decoder.length; i++) decoder[i] = '?';
		
		for (int i=0; i<alphabet[1].length(); i++) {
			for (int j=0; j<cipherChunk.length(); j++) {
				if (alphabet[1].charAt(i) == cipherChunk.charAt(j)) {
					decoder[i] = chunkDecoded.charAt(j);
					break;
				}
			}
		}
		System.out.println(""+new String(decoder));
		*/
		
		CipherGene gene = new CipherGene();
		gene.decoder = new StringBuffer("wln?s?t???t???????????o????s?i?oc????m?rdol?l??p???r?y");
		System.out.println(gene.decode());
	}
	
	public static void testGrid() {
		String[] grid = Ciphers.grid(cipher[0], 17);
		for (int i=0; i<grid.length; i++)
			System.out.println(grid[i]);
		grid = Ciphers.grid(cipher[1], 17);
		for (int i=0; i<grid.length; i++)
			System.out.println(grid[i]);
	}
	public static void main(String[] args) {
		testGrid();
		//testConflicts();
		//System.out.println(Zodiac.cipher[Zodiac.CIPHER].substring(CipherWordGene.CIPHER_START,CipherWordGene.CIPHER_END+1));
		//test29Chars();
		
		
		//testFastBrute();
		//System.out.println(""+getUniqueWords(new StringBuffer("ilikekill")));
		//testDate();
		//testGene();
		//testFindLinks();
		//findBestLettersForSearch();
		
		//findMaximalCipherMinimalLetters();
		
		//int[] c = findBestLettersForSearch(1,'5','6','d','e','#');
		//for (int i=0; i<c.length; i++) System.out.println(c[i]);
		//System.out.println("**".matches(".*[^\\*].*"));
		
//		System.out.println(cipher[CIPHER]);
		//System.out.println(PLUGGER.length);
		//testMinConflict(false);
		//testWordDecoders();
		//System.out.println(Zodiac.cipher[1].substring(334));
		//testGene2();
		//testCorpus();
		//testDigraph();
		//testConflicts();
		//compareDecoders();
		//testLoad();
		//testEncoding();
		//testLetter();
		//testPhi();
		//testKill();
		//testCrossover();
		//testFitness();
		//testSpeed();
		//evolve();
		//System.out.println(cipher[1].length());
		//testRandomWord();
		//testWord();
		//testFreq();
		//testBest();
		//brute();
		//testFit();
	}
}
