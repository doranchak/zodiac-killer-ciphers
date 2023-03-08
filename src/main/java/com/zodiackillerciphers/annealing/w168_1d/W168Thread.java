package com.zodiackillerciphers.annealing.w168_1d;

import com.zodiackillerciphers.annealing.SimulatedAnnealing;
import com.zodiackillerciphers.annealing.homophonic.HomophonicThread;
import com.zodiackillerciphers.ciphers.w168.StringUtils;

public class W168Thread extends Thread {
	private int targetIterations;
	private int threadNum;
	private double startingTemperature;
	
	public W168Thread(double startingTemperature,
			int targetIterations, int threadNum) {
		this.startingTemperature = startingTemperature;
		this.targetIterations = targetIterations;
		this.threadNum = threadNum;
	}
	public void run() {
		StringBuilder[] cipher = StringUtils.toStringBuilder(new String[] {

				// W168
//				"REDACTED", 
//				"REDACTED", 
//				"REDACTED", 
//				"REDACTED", 
//				"REDACTED", 
//				"REDACTED", 

				// SAM184_1
				"HE PACEIIFHEPT GOI LRS ",
				"ITR   UT POISGOS ONEVTH",
				"ND SOOSCLSEETNIF .IIHTR",
				"DIS EZPEIRYPE  AB LOIV.",
				" EFSARDMC YRTVCEN SLL E",
				" GETS RO VR GHIS CSPHE ",
				"IYSTANSFEFP NT LTGNI AO",
				"RIHDIV  B  OHE YNUVAOE "			
				
				
//				" UNPNNOAALHIT  S  DETP   ISH",
//				"KEDEEAR MROFTIUHAIHWOFC AWH ", 
//				"PTIOFAI  O BRUNE MNBACEL  ES", 
//				" HISO EUG C   BTTATYT CSALTS",
//				"AL A DO LLFIHBEOTSOOOITEOTF.", 
//				"  IW RYEIONM NIKTN  UONRTE L" 
		
//				"SEL  ON WOH L EL VM.P EO IHR",
//				"IF OHAMETOM COAP IEN MWENLTH",
//				"H YNSDEOH ESDUAPEFR YA RWYE ",
//				" I AA UHHTENRANWDC OETLT AHT",
//				" ARSB THT  SEVGAOUAR SHETLN ",
//				"FTBTDHGOAT H E I LTE EI MEAT"
				
//				"S UIRT IHRTERRAAINCI  HGHTO ",
//				"SG EASDH  EOT A M NONTIEGHTU",
//				"  U.  DNI ROESEUFECIDSHRORSO",
//				"TJASAINEVPOEP RX NIENT AC NE",
//				" .HDROTBATNA I BYIGH  EUEMUO",
//				"W AOCUE  I GFNTO NOSLFCESPDL"
				
//				"TEPRTL FEPEWEYAECTASOEWAO  O",
//				"T SW.CRGLWNM ORER ACTNTTFCG.",
//				"FOC GIWAT ALEHUOL DE ONR TAC",
//				"W HTROPOTH SSONU SNINO T EAH",
//				" HOYH SOH C R  ROA LGV HYUND",
//				"IHI K YI AI TCTIAE  SASAO NE"
				
//				"SAA SNGTNTOEFERARW   BUIRERH",
//				"PS MO EHFEUI RYONEETIE   IAS",
//				"RI AA WPTOOE G AG EALTOTNHTF",
//				"TEYSNIIR  WE OCYSLMNIECN FOL",
//				" SAN E O SFL N EOSSOSSTVS O ",
//				"NOIELRHHECEC FDE TGTB EOMAAE"				

//				" TEHNTARNSOCETNILNATM OCCMRE",
//				"FEO I HTOSC RUTNHYS DOLUT ON",
//				"  EBTI NCH ECLTUNHA RDG  APS",
//				"OO FCN EOOPRIRTA O.N ALLTSCE",
//				"SINO  FO TEHOUINHNS DOLU  SA",
//				" FRAPA SIOSS BELBB EFEENDIET"				
		});
		W168Solution sol = new W168Solution(5, cipher);
		sol.initialize();
		double temperature = startingTemperature;
		SimulatedAnnealing.extendIterations = true;
		sol = (W168Solution) SimulatedAnnealing.run(temperature, targetIterations, sol, threadNum);
		finish();
	}
	public void finish() {
		Solver.threads[threadNum] = new W168Thread(startingTemperature, targetIterations, threadNum);
		Solver.threads[threadNum].start();
	}	
}
