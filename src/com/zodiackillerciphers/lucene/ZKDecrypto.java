package com.zodiackillerciphers.lucene;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.transform.CipherTransformations;


import ec.util.MersenneTwisterFast;

public class ZKDecrypto {

	public static ZKDInfo compute(int[] genome, Set<String> tempTabu, Set<String> optimaTabu) {
		ZKDInfo info = new ZKDInfo();
		Map<Character, Character> dm;
		// derive key from genome
		info.key = ProblemLucene.keyFrom(genome);
		
		//if key is in TEMP TABU or OPTIMA TABU lists, set score really low
		if (tempTabu.contains(info.key.toString()) || optimaTabu.contains(info.key.toString())) {
			info.score = -100000;
		} else {
			// otherwise, decode and compute score
			dm = Scorer.decoderMap(info.key);
			info.decoded = Scorer.decode(dm);
			info.score = calcscore(info.decoded);
		}
		tempTabu.add(info.key.toString());
		return info;
	}
	
	public static void dump(int[] g) {
		String s = "";
		for (int i=0; i<g.length; i++) s += g[i] + " ";
	}
	
	public static void hillclimb(int[] g) {
		MersenneTwisterFast random = new MersenneTwisterFast();
		boolean go = true;
		
		boolean improve;
		int tolerance = 0;
		int cur_tol = 0;
		int cur_tabu = 0;
		Set<String> tempTabu = new HashSet<String>(); 
		Set<String> optimaTabu = new HashSet<String>(); 
		
		float lastScore = 0; float bestScore = 0; float curBestScore = 0;
		StringBuffer bestKey = null;
		StringBuffer curBestKey = null;
		int[] bestGenome = null;
		int[] curBestGenome = null;
		ZKDInfo info = null;
		
		
		//initial score
		info = compute(g, tempTabu, optimaTabu);
		curBestScore = info.score;
		lastScore = info.score;
		tempTabu.clear();
		optimaTabu.clear();
		
		
		
		int[] genome = g.clone();
		while (go) {
			improve = false;

			//for (int p1=0; p1<genome.length; p1++) {
			//	for (int p2=0; p2<genome.length; p2++) {
			
			for (int p=0; p<genome.length*genome.length; p++) {
				//int[] copy = genome.clone();
				//mutate(random, genome);
				
					// DO_SWAP; TABU_STR_A(key); SET_SCORE(score,DECODE_A); ADD_TEMP_TABU; //swap, decode, score

					int p1 = random.nextInt(genome.length);
					int p2 = p1;
					while (p2 == p1) p2 = random.nextInt(genome.length);
					// swap the genome positions
					swap(genome,p1,p2);
					
					info = compute(genome, tempTabu, optimaTabu);
					
					if (Settings.ZKD_MAX_TOL > 0) {
						//tolerance of going downhill starts out at max, and decreases with each iteration without improve
						//if(info->max_tol) tolerance=rand()%(info->max_tol-info->cur_tol+1);
						tolerance = random.nextInt(Settings.ZKD_MAX_TOL - cur_tol + 1);
					}
					
					// if(score<(last_score-tolerance)) {DO_SWAP;} //undo swap if beyond tolerance
					if (info.score<(lastScore-tolerance)) {
						swap(genome,p1,p2);
						//genome = copy; // back to original as of beginning of this loop
					}
					else { // better or same as last score
						lastScore = info.score;
						if (info.score > bestScore) {
							// best seen score
							improve = true;
							bestScore = info.score;
							bestKey = new StringBuffer(info.key);
							bestGenome = genome.clone();
							System.out.println("new best seen overall");
							dump(info);
						}
						if (info.score > curBestScore) {
							// best seen score since last tabu + restart
							curBestScore = info.score;
							curBestKey = new StringBuffer(info.key);
							curBestGenome = genome.clone();
							System.out.println("new best seen since last restart");
							dump(info);
						}
						
					}
					
				}
			//}
			
			if (!improve) {
				//System.out.println(cur_tol+","+cur_tabu);
				if (++cur_tol>=Settings.ZKD_MAX_TOL) cur_tol = 0; //reset downhill score tolerance
				
				if (Settings.ZKD_MAX_TABU > 0 && ++cur_tabu >= Settings.ZKD_MAX_TABU) { //blacklist best key since last restart, reset current best score, 50/50 back to best or random restart
					System.out.println("reached max tabu.  adding " + curBestKey + " to optima");
					System.out.println("temp tabu size " + tempTabu.size());
					System.out.println("optima tabu size " + optimaTabu.size());
					optimaTabu.add(curBestKey.toString());
					curBestScore = -10000;
					
					if (random.nextBoolean()) {
						System.out.println("random restart.  before: " + Scorer.decode(genome));
						for (int i=0; i<100; i++) shuffle(random, genome); // random restart;
						System.out.println("random restart.  after: " + Scorer.decode(genome));
					} else {
						info.key = new StringBuffer(bestKey); // back to best
						System.out.println("back to best key " + info.key);
						System.out.println("back to best key.  before: " + Scorer.decode(genome));
						genome = bestGenome;
						System.out.println("back to best key.  after: " + Scorer.decode(genome));
					}
					cur_tabu = 0;
				}
					
			} else { // improvement; reset variables.
				cur_tol = 0;
				cur_tabu = 0;
			}
			
			// random swaps at end of iteration
			
			for(int i=0; i<Settings.ZKD_SWAPS; i++) shuffle(random, genome); //random swaps at end of iteration
			//for(int i=0; i<Settings.ZKD_SWAPS; i++) mutate(random, genome);
				
			info = compute(genome, tempTabu, optimaTabu);
			lastScore = info.score;
			if (random.nextInt(Settings.ZKD_CLEAR_TABU_PROB) == 0) {
				System.out.println("clearing tempTabu at size " + tempTabu.size());
				tempTabu.clear();
			}
			if (Settings.ZKD_MAX_TOL == 0) tolerance = 0;
		}
	}
	
	public static void mutate(MersenneTwisterFast random, int[] genome) {
		int dart = random.nextInt(Settings.MUT_TYPES);
		float prob = 0.05f;
		if (dart == 0) {
			LuceneVectorIndividual.mutateGenome(prob, 0, 25, random, genome);
		}
		else if (dart == 1) {
			LuceneVectorIndividual.mutateWord(random, genome);
		}
		else if (dart == 2) { 
			LuceneVectorIndividual.mutateWithFreq(prob, random, genome);
		}
		else if (dart == 3) { 
			LuceneVectorIndividual.mutateSwapGenome(random, genome);
		}
		else if (dart == 4) {
			LuceneVectorIndividual.mutateWordPartial(random, genome);
		}
		
	}
	
	public static void shuffle(MersenneTwisterFast random, int[] genome) {
		int i=random.nextInt(genome.length);
		int j=i;
		while (j==i) j=random.nextInt(genome.length);
		swap(genome, i, j);
	}
	
	public static void dump(ZKDInfo info) {
		System.out.println("improvement " + info.score + " decoded " + info.decoded + " key " + info.key);
	}
	
	public static float calcscore(StringBuffer sb) {
		float score = NGrams.zkscore(sb);
		score *= (1.05f - 5*Stats.iocDiff(sb));
		score *= (1.05f - 5*Stats.entropyDiff(sb)/150.0);
		score *= (1.05f - 5*Stats.chi2Diff(sb)/60.0);
		return score;
		
	}
	
	public static float calcscore(String s) {
		return calcscore(new StringBuffer(s));
	}
	
	public static void swap(int[] genome, int i, int j) {
		int gi = genome[i];
		int gj = genome[j];
		genome[i] = genome[j];
		genome[j] = gi;
		
		assert genome[i] == gj;
		assert genome[j] == gi;
	}
	
	public static void test() {
		//int[] g = new int[] {21,15,3,0,0,8,4,8,0,4,21,19,4,24,8,18,14,4,11,19,17,12,18,7,19,0,7,17,13,19,7,18,18,24,18,18,13,10,14,3,11,22,17,14,14,7,2,20,0,8,11,2,1,18,18,4,4,11,8,2,13,0,15,20,19};

		// 408 key init'd from zkdecrypto 
		int[] g = new int[] {19, 4, 2, 20, 11, 10, 18, 8, 8, 22, 8, 0, 13, 18, 8, 0, 18, 4, 17, 13, 7, 2, 17, 19, 7, 7, 17, 13, 1, 19, 0, 15, 13, 4, 4, 4, 4, 3, 6, 19, 14, 5, 14, 14, 3, 21, 24, 4, 19, 14, 0, 12, 11, 20};

		// use key init
		//int[] g = new int[Settings.GENOME_SIZE];
		System.out.println(g.length);
		NGrams.genomeInitKey(g);
		
		hillclimb(g);
	}
	
	public static void main(String[] args) {
		//test();
		//System.out.println(calcscore(Settings.zodiacsolution));
		//System.out.println(calcscore(new StringBuffer("ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGERTUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI".substring(0,340))));
		System.out.println(calcscore("PCLURDURHNOPIOSLIEFISRETURMALURIETVFOEOACTLELUIESUAETUGEATETEGNS"));

		/*String c = "ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGERTUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI".substring(0,340);
		for (int i=0; i<1000; i++) {
			c = CipherTransformations.shuffle(c);
			System.out.println(calcscore(new StringBuffer(c)));
		}*/
	}
	
}
