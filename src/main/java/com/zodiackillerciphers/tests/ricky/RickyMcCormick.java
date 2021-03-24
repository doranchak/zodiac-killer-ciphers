package com.zodiackillerciphers.tests.ricky;

import com.zodiackillerciphers.ngrams.NGramsBean;

/** ricky mccormick codes */
public class RickyMcCormick {
	public static String[] code = new String[] {
		"(MNDMKNEARSE-N-STA-KNARE)     (ALSM)",
		"TFRNENPINSENPBSERCBBNSENPRSEINC",
		"PRSE NMRSE BPREHLDCNLDNCBE(TFXLF TCXLNCBE)",
		"AL-PRPPITXLYPPIYNCBEMEKSEINCDRCBRNSEPRSE",
		"WLDRCBRNSE NTSBNENTXSE-CRSLE-CITRSEWLDNCBE",
		"ALWLPNCBETSMELRSERLSEURGLSNEASNWLDNCBE",
		"(NOPFSENLSRENCBE)NTEGDDMNSENCURERCBRNE",
		"(TENETFRNENCBRTSENCBEINC)",
		"(FLRSEPRSEDNDE71NCBE)",
		"(CDNSEPRSEDNSDE74NCBE)",
		"(PRTSEPRSEONREDE75NCBE)",
		"(TFNBCMSPSOLEMRDELUSETOTEWLDNINLDNCBE)",
		"(194WLD'SNCBE)(TRFXL)",
//		"ALPNTE GLSE-SE ERTE",
//		"VLSE MTSE-CTSE-WSE-FRTSE",
//		"PNRTRSEONPRSEWLDNCBE",
//		"NWLDXLRCMSPNEWLDSTSMEXL",
//		"DULMT6TUNSENCBEXL",
//		"(MUNSARSTENMLNARSE)",
//		"(SAE6NSESENMBSE)",
//		"NMNRCBRNSEPTE2PTEWSRCBRCSE",
//		"26MLSE74SPRKSE29KENOBOLE175RTRSE",
//		"35GLECLGSEOUNUTREDKRSEPSESHLE",
//		"651MTCSEHTLSENCUTCTRSNMRE",
//		"99.84.BZUNEPLSENCRSEAOLTSENSKSENRSE",
//		"NSREONSEPUTSEWLDNCBE(3XARL)",
//		"ANMSENRSEIN2NTRLERCBRNSENTSRCRBNE",
//		"LSPNSENGSPSEMKSERBSENCBEAUXLR",
//		"HMCRENMRENCBE 1/2 MUNDDLSE",
//		"S-W-M.4MIL XDRLX"
	};
	
	public static void ngrams() {
		String cipher = "";
		for (String c : code) cipher += c + " ";
		boolean go = true;
		int n = 2;
		while (go) {
			NGramsBean ng = new NGramsBean(n, cipher);
			if (ng.repeats.size() == 0) {
				go = false;
			} else {
				System.out.println(" ====== " + n + ":");
				System.out.println(ng.repeatsInfo());
			}
			n++;
		}
	}
	public static void main(String[] args) {
		ngrams();
	}
}
