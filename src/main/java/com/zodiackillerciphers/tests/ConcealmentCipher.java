package com.zodiackillerciphers.tests;

import com.zodiackillerciphers.dictionary.InsertWordBreaks;
import com.zodiackillerciphers.lucene.ZKDecrypto;

public class ConcealmentCipher {
	/** simple brute force attack: on each iteration, remove the letter whose removal maximizes the ngram score. */
	public static void bruteForce(String cipher, boolean showIntermediates) {
		boolean found = true;
		cipher = cipher.toUpperCase();
		String bestCipherWithBreaks = null;
		float currentScore = 0;
		float bestScore = 0;
		int bestPosition = -1;
		StringBuffer bestCipher = null;
		while (found) {
			found = false;
			bestScore = currentScore;
			bestCipher = null;
			bestPosition = -1;
			for (int i=0; i<cipher.length(); i++) {
				StringBuffer sb = new StringBuffer(cipher);
				sb.deleteCharAt(i);
//				float candidateScore = ZKDecrypto.calcscore(sb);
				StringBuffer withBreaks = InsertWordBreaks.findWordBreaks(new StringBuffer(cipher), "EN", true);
				float candidateScore = Float.valueOf(withBreaks.toString().split("	")[0]);
				String cipherWithBreaks = withBreaks.toString().split("	")[1];
				if (candidateScore > bestScore) {
					bestPosition = i;
					bestScore = candidateScore;
					found = true;
					bestCipher = sb;
					bestCipherWithBreaks = cipherWithBreaks;
				}
			}
			if (found) {
				currentScore = bestScore;
				if (showIntermediates) System.out.println(bestScore + "	" + bestPosition + "	" + cipher.charAt(bestPosition) + "	" + bestCipher + "	" + bestCipherWithBreaks);
				cipher = bestCipher.toString();
			}
		}
		System.out.println(currentScore + "	" + cipher + "	" + bestCipherWithBreaks);		
		
	}
	
	public static void main(String[] args) {
		InsertWordBreaks.init("EN", true);

		bruteForce("thiasuhasiuh	xsixsaxtexst", false);
//		String cipher = "YMBFPTYSYSNAOSBYAACPTMYSNEMTMOIYJGTEBYNIAJAPSNDETMYLGAIDISYMIHFBYTFAAWOTYCMCHNEFTYIHSAWOYYHNEAWOWIWLTDIHAJBSASTFYTMMFDOETTDITWYGMTIPDTTOFYANRSITLOACYANWYAJAHCTDSIHNDONIPEICNCOACOHMOOOTYPMDISWTOKIEYWOPRAAOTWFFFYOTYOMIHLYDWICSTOTIHHNCFBYHNLAOWALYCWOWTDSOLIOMWWWYNTLTYMYFYLNAWNMWSSINDIDCWGOOBOYAYOFWGYKODLIAALACNSUFMSIATOJBAHTYIANAWJSWDETOMYHABEODIASMGWIHDTTOTYHNDTIAPASYOAYDTSWBAAFYFNSMTMOTOHYANSYJDWMABTDEKMMSAIHOMLTPYANWADNWTSWISAIHNGTSTBFANTDWLIAAYYAARYNTTMIFLYTIABYWYEWGOTAYNTTMWITACOSSOCAYSLIYATMTTOTTNAAMDOTWYNSOETBRNBAHTMAYSIDKINADRHBOFTWTTFBWSYJSBYKIWBMATOGTDALAYANIATADWCBYDDLOAHWIAFHWSTAYSTFOHSELTODTNOAMAHMAHWCUALFMTFYMYNWTGTOGAHTCHASODATRISWNBWISIAAOHBBYCSTSIPYAAFITIYWSTASIABBAAGIWSYCIATOYSIAWYDBHSAIIMYMMFUIOIYRYCHCLEFAOWNHOTSADIHIWYCTTACBIWLFYTTIIMLPIDOEGSSIIMATCIDLSDMSFHATHTLTIFTNWIATOYLMJOMATLOIMFJMWYTOOMDYLDBLPCEAT";
//		int L=30;
//		for (int i=0; i<cipher.length()-L+1; i++) {
//			bruteForce(cipher.substring(i,i+L), false);
//		}
	}
}
