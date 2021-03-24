package com.zodiackillerciphers.annealing.homophonic;

import java.util.Random;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;

/** older version that used an outer loop on the main algorithm that adjusted a restart temperature */
public class HomophonicThreadOriginal extends Thread {
	static Random random = new Random();
	String cipher;
	String plaintext; 
	int maxFails = 1;
	double startingTemperature = 10000;
	double coolingRate = 0.00005;
	double temperatureReductionRatePerRun = 10;
	public boolean result;
	public int threadNum;
	
	public HomophonicThreadOriginal(String cipher, String plaintext, int threadNum) {
		this.cipher = cipher;
		this.plaintext = plaintext;
		this.threadNum = threadNum;
	}
	public HomophonicThreadOriginal(String cipher, String plaintext, int maxFails, double startingTemperature,
			double coolingRate, double temperatureReductionRatePerRun, int threadNum) {
		super();
		this.cipher = cipher;
		this.plaintext = plaintext;
		this.maxFails = maxFails;
		this.startingTemperature = startingTemperature;
		this.coolingRate = coolingRate;
		this.temperatureReductionRatePerRun = temperatureReductionRatePerRun;
		this.threadNum = threadNum;
	}
	public void say(String msg) {
		System.out.println(threadNum + "	" + msg);
	}
	public void run() {
		HomophonicSolution hom = new HomophonicSolution(cipher, plaintext);
		hom.initialize();
		boolean go = true;
		double bestScore = Double.MAX_VALUE;
		//double temperature = random.nextDouble()*28+2;  //[2,30)
		//double coolingRate = random.nextDouble()*0.199+0.001;  // [0.001,0.2)
		double temperature = startingTemperature;
		
		while (temperature >= 1) {
			int fails = 0;
			while (go) {
				hom = (HomophonicSolution) SimulatedAnnealing.run(temperature, 100000, hom, threadNum);
//				if (hom.percentMatchesZ408Solution() >= 80) {
//					say("SUCCESS!  Reached at least 80% accuracy of Z408 solution.");
//					result = true;
//					finish();
//					return;
//				}
				if (hom.iterations > 200000) {
					say("FAIL!  Too many iterations (" + hom.iterations + ")");
					break;
				}
				double score = hom.energyCached();
				// make new temperature a bit bigger than the temperature of the system at the time
				// the last best solution was found
				if (score < bestScore) {
					say("Got a better one, so continuing.");
					bestScore = score;
					fails = 0;
					temperature = hom.temperature * 10;
				} else {
					fails++;
					if (fails == maxFails) {
						say("Quitting, because did not improve the best solution in " + fails + " attempts.");
						break;
					} else {
						say("Trying again.  " + (maxFails-fails) + " attempts remaining.");
					}
				}
			}
			// since we got here, we couldn't find any improved solution.  so let's try starting
			// at a cooler temperature to help narrow the search, in case the system is too "hot"
			// and accepting too many bad neighboring solutions.
			temperature/=temperatureReductionRatePerRun;
		}
		result = false;
		finish();
	}
	
	public void finish() {
//		if (result) {
//			// quit for good.  we found Z408 solution.
//			System.exit(0);
//		}
		// kick off another thread
		Solver.threads[threadNum] = new HomophonicThreadOriginal(cipher, plaintext, threadNum);
		Solver.threads[threadNum].start();
	}
	
	public static void testNgrams() {
		String[] plaintexts = new String[] { Ciphers.Z408_SOLUTION.toUpperCase(),
				"LSSETEETSOILNRENTCTIENTELLESRENATENPTIEUONAESSRTDANSDELATSILIESMLAAROIINTPREUREETCETTRIANDLEUNLAETAISDLSSUTRAITANTERSATIRESATREARENEILLOOTEARUNCANTAINELATSDLIGNLUSDEREEOETORITCUAISINAILLEULDLLETEUREEREPRISANTLESSINSTREENNEUNPOALTINASINTDSIEROILATTCUITESISINSEAMEELNDINSSUNSONSORELSSTMISATTREEACALEATOIRLILTSIEALEOILRTALISACTTENTERLETILSAISLENETELIEIDRUNINNALERAALEASILERESNOTRPESALTPUIETSRCLTREELCULARENNNEUO",
				"USEDUDURSQDAALPARIONNESECUMEOIMLSNFGSVUEQLLIRIHSSTTREDUTREDACUSSABLNQVAFUGEARLEMOINESONLESUIETCLPETTREAIRESNBDELERIHRTRAEDETROPLNMFUVAAQOUILLETILMETAFAUTRESANSACRIENNUMQEUOLDOIETNRATBVACMEUSADISARENDOPGHCETFEAURSATIONIMAEAEMGQTUEAFBICTUEETULQCUTROIRNOIRDEVARABSUNCLSTESSEFIQTRONDUSSUSCETROLNPLILDETEONOUCURSDITAUONDESLDVRLIOUNESINDISCUSTARDMMERPUTPCSERLAMALDNETTCNTEDAPHESEOUOGIRLDEGENAREHICONPAUIECLNMFTAUEQ",
				"ESETETETSENTSEESTETESETEEENEREETTSTNTSETENTESETTTSSENTENTENTSESSTETEESETENETREENTESETRETETEETSETEESLENTESTTEENETETETENTEETENTRETENTESTTETEETETSETEESETTENTETTESSERENSEENEEETENTETSESESESTENTETTEETTRESTRENTSESTETESSESETEENSETTENESEEETEESSENELEEESENTTERETESNESSETESESENTLESSTTEESETETESSESSENTTESETETEENETERESETSNESTETEEETTESETETESETEEEETSESNESENEETEELESTERNEESTESENNESSENTETESETERNESTEENTETTETEETEETEETETENTSSETE" };
		for (String plaintext : plaintexts) {
			System.out.println(plaintext);
			float zk = NGrams.zkscore(new StringBuffer(plaintext), false, true);
			System.out.println("zkscore: " + zk);
		}
	}

	public static void testStats() {
		String[] plaintexts = new String[] { Ciphers.Z408_SOLUTION.toUpperCase(),
				"LSSETEETSOILNRENTCTIENTELLESRENATENPTIEUONAESSRTDANSDELATSILIESMLAAROIINTPREUREETCETTRIANDLEUNLAETAISDLSSUTRAITANTERSATIRESATREARENEILLOOTEARUNCANTAINELATSDLIGNLUSDEREEOETORITCUAISINAILLEULDLLETEUREEREPRISANTLESSINSTREENNEUNPOALTINASINTDSIEROILATTCUITESISINSEAMEELNDINSSUNSONSORELSSTMISATTREEACALEATOIRLILTSIEALEOILRTALISACTTENTERLETILSAISLENETELIEIDRUNINNALERAALEASILERESNOTRPESALTPUIETSRCLTREELCULARENNNEUO",
				"USEDUDURSQDAALPARIONNESECUMEOIMLSNFGSVUEQLLIRIHSSTTREDUTREDACUSSABLNQVAFUGEARLEMOINESONLESUIETCLPETTREAIRESNBDELERIHRTRAEDETROPLNMFUVAAQOUILLETILMETAFAUTRESANSACRIENNUMQEUOLDOIETNRATBVACMEUSADISARENDOPGHCETFEAURSATIONIMAEAEMGQTUEAFBICTUEETULQCUTROIRNOIRDEVARABSUNCLSTESSEFIQTRONDUSSUSCETROLNPLILDETEONOUCURSDITAUONDESLDVRLIOUNESINDISCUSTARDMMERPUTPCSERLAMALDNETTCNTEDAPHESEOUOGIRLDEGENAREHICONPAUIECLNMFTAUEQ",
				"ESETETETSENTSEESTETESETEEENEREETTSTNTSETENTESETTTSSENTENTENTSESSTETEESETENETREENTESETRETETEETSETEESLENTESTTEENETETETENTEETENTRETENTESTTETEETETSETEESETTENTETTESSERENSEENEEETENTETSESESESTENTETTEETTRESTRENTSESTETESSESETEENSETTENESEEETEESSENELEEESENTTERETESNESSETESESENTLESSTTEESETETESSESSENTTESETETEENETERESETSNESTETEEETTESETETESETEEEETSESNESENEETEELESTERNEESTESENNESSENTETESETERNESTEENTETTETEETEETEETETENTSSETE" };
		for (String plaintext : plaintexts) HomophonicSolution.dumpStats(plaintext);
	}
}
