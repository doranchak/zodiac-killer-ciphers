package com.zodiackillerciphers.lucene;


public class Settings {

	public static int MAX_STALLED = 5000; // max # of gens we're willing to wait before resetting
	public static int MAX_TABU = 10000; // largest optima tabu list size permitted
	public static int MAX_TABU_TEMP = 1000000; // largest temp tabu list size permitted
	public static int MAX_GEN = 0; // force a tabu reset every MAX_GEN generations unless MAX_GEN is zero
	public static int NUM_OBJECTIVES = 3;
	/** number of generations it takes to fully scale back the nonsimilarity pressure */
	public static int GEN_SCALE = MAX_STALLED;
	//public static int MUT_TYPES = 9;
	public static int MUT_TYPES = 5;
	public static int GENOME_SIZE;
	public static String[] alpha = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	public static float[] freqs = {.08167f, .01492f, .02782f, .04253f, .12702f, .02228f, .02015f, .06094f, .06966f, .00153f, .00772f, .04025f, .02406f, .06749f, .07507f, .01929f, .00095f, .05987f, .06327f, .09056f, .02758f, .00978f, .02360f, .00150f, .01974f, .000749f};
	public static int L_MIN = 3; // min word length under consideration
	public static int L_MAX = 10; // max word length under consideration
	//public static StringBuffer cipher = new StringBuffer(Ciphers.cipher[1]);
	// fuck this one.  not enough constraints.  public static StringBuffer cipher = new StringBuffer("#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX"); // yield analysis: 80% of cipher text
	//public static StringBuffer cipher = new StringBuffer("F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@"); // 408 cipher, yield analysis: 80% of cipher text.  80 symbols long, only 41 unique.
	
	
	public static int whichcipher = 1;
	public static StringBuffer cipher			= new StringBuffer(com.zodiackillerciphers.ciphers.Ciphers.cipher[whichcipher].cipher);
	public static StringBuffer zodiacsolution 	= new StringBuffer(com.zodiackillerciphers.ciphers.Ciphers.cipher[whichcipher].solution);
	
	
	static boolean DEBUG = false;
	//public static StringBuffer cipher = new StringBuffer("PBP/N/PBBPDR=NT=BNVNeGYFNPHPFFTqYeMJYDPHPFqTtNJYDHMGD/PBBPDRAPBzRGqNPDHMNJTttNFHVNeGYFNqGDPFHMNqTGHzGDRNtHYNGDGqGBTJGBBHT/PBBFTqNHMPDRRPcNFqNHMNqTGHHMtPBBPDRNj=NtNDeNPHPFNcNDVNHHNtHMGDRNHHPDR_TYttTe/FTJJAPHMGRPtBHMNVNFH=GtHTJPHPGHMGNAMNDPzPNPAPBBVNtNVTtDPD=GtGzPeNFDzGBBHMNPMGcN/PBBNzAPBBVNeTqNq_FBGcNFPAPBBDTHRPcN_TYq_DGqNVNeGYFN_TYAPBBHt_HTFBTPzTADTtFHT=q_eTBBNeHPDRTJFBGcNFJTtq_GJHNtBPJNNVNTtPNHNqNHMM=PHP"); // 408 as simple substitution
	//public static StringBuffer cipher = new StringBuffer("64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV"); // 340 cipher, 80% yield, length 84, 41 repeats
	//public static StringBuffer zodiacsolution = new StringBuffer("slavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslaves");
	public static int[] solutionGenome = new int[] {14, 11, 11, 13, 7, 4, 10, 19, 4, 18, 0, 8, 15, 18, 22, 11, 13, 4, 18, 0, 19, 19, 5, 18, 19, 7, 4, 13, 8, 5, 6, 0, 14, 8, 1, 4, 14, 20, 4, 17, 13, 24, 21, 14, 2, 3, 23, 8, 0, 4, 12, 17, 17, 3};
	public static StringBuffer cipdec = new StringBuffer("dehghgwceielrrhrcsauilsildohnptasiaaseweifarndisasouggdschelcwefllatiepahadrorioasifsnualadpeolahfseugldnestlefalcriuscpdghscnhatoawellifhpareosatfspardschalunrlodgitwoiihfreasesunpolelloedalnrsrodignhaichsaflwnepodatporlretaisdfpaldcohghewricdscasouarneherurlfwilfaeleeeadiouftgdeehfchscarihasanisffundcdceerslwfundsaneuasahilsptnrscdespnnotichdehcadofptranidsslishelhiielfhnarnanfaeurchislathrdselatoaorwei");
	
	static {
		System.out.println("Current cipher: " + cipher);
		System.out.println("Current solution: " + zodiacsolution);
		Scorer.alphabet = com.zodiackillerciphers.ciphers.Ciphers.alphabet(Settings.cipher.toString());
		GENOME_SIZE = Scorer.alphabet.length();
		System.out.println("Current alphabet: " + Scorer.alphabet + " [" + GENOME_SIZE + "]");
	}
	
	/** probability of swapping one pair in the genome, instead of up to 10 pairs */
	public static float PROB_SWAP_ONE = 0.999f;
	
	/* ZKD SETTINGS */
	public static int ZKD_MAX_TOL = 40;
	public static long ZKD_MAX_TABU = 300;
	public static long ZKD_SWAPS = 5;
	public static int ZKD_CLEAR_TABU_PROB = 80; // there is a 1 in ZKD_CLEAR_TABU_PROB chance the tabu will be cleared at the end of genome.length*genome.length iterations.  
	

}
