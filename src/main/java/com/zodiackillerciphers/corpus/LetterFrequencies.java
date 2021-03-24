package com.zodiackillerciphers.corpus;

import java.util.HashMap;
import java.util.Map;

/** calculate letter and bigram frequencies */
public class LetterFrequencies extends CorpusBase {
	public static void calculate() {
		initSources();
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long letterSamples = 0;
		long bigramSamples = 0;
		boolean go = true;
		
		/** letter counts */
		Map<Character, Long> letterCounts = new HashMap<Character, Long>();
		/** bigram counts */
		Map<String, Long> bigramCounts = new HashMap<String, Long>();
		
		while (go) {
			go = !randomSource();
			StringBuffer text = flatten(tokens, false);
			// letters
			for (int i=0; i<text.length(); i++) {
				Character c = text.charAt(i);
				Long val = letterCounts.get(c);
				if (val == null) val = 0l;
				val++;
				letterCounts.put(c, val);
				letterSamples++;
			} // bigrams
			for (int i=0; i<text.length()-1; i++) {
				String bi = text.substring(i,i+2);
				Long val = bigramCounts.get(bi);
				if (val == null) val = 0l;
				val++;
				bigramCounts.put(bi, val);
				bigramSamples++;
			}
			samples++;
			if (samples % 100 == 0) {
				System.out.println("Letters:");
				for (Character c : letterCounts.keySet()) {
					float ratio = letterCounts.get(c);
					ratio /= letterSamples;
					System.out.println(c + "	" + ratio);
				}
				System.out.println("Bigrams:");
				for (String s : bigramCounts.keySet()) {
					float ratio = bigramCounts.get(s);
					ratio /= bigramSamples;
					System.out.println(s + "	" + ratio);
				}
				System.out.println("Doubles:");
				for (String s : bigramCounts.keySet()) {
					if (s.charAt(0) != s.charAt(1)) continue;
					float ratio = bigramCounts.get(s);
					ratio /= bigramSamples;
					System.out.println(s + "	" + ratio);
				}
			}
		}
	}

	public static void test() {
		Map<String, Long> bigramCounts = new HashMap<String, Long>();
		String cipher = "YMBFPTYSYSNAOSBYAACPTMYSNEMTMOIYJGTEBYNIAJAPSNDETMYLGAIDISYMIHFBYTFAAWOTYCMCHNEFTYIHSAWOYYHNEAWOWIWLTDIHAJBSASTFYTMMFDOETTDITWYGMTIPDTTOFYANRSITLOACYANWYAJAHCTDSIHNDONIPEICNCOACOHMOOOTYPMDISWTOKIEYWOPRAAOTWFFFYOTYOMIHLYDWICSTOTIHHNCFBYHNLAOWALYCWOWTDSOLIOMWWWYNTLTYMYFYLNAWNMWSSINDIDCWGOOBOYAYOFWGYKODLIAALACNSUFMSIATOJBAHTYIANAWJSWDETOMYHABEODIASMGWIHDTTOTYHNDTIAPASYOAYDTSWBAAFYFNSMTMOTOHYANSYJDWMABTDEKMMSAIHOMLTPYANWADNWTSWISAIHNGTSTBFANTDWLIAAYYAARYNTTMIFLYTIABYWYEWGOTAYNTTMWITACOSSOCAYSLIYATMTTOTTNAAMDOTWYNSOETBRNBAHTMAYSIDKINADRHBOFTWTTFBWSYJSBYKIWBMATOGTDALAYANIATADWCBYDDLOAHWIAFHWSTAYSTFOHSELTODTNOAMAHMAHWCUALFMTFYMYNWTGTOGAHTCHASODATRISWNBWISIAAOHBBYCSTSIPYAAFITIYWSTASIABBAAGIWSYCIATOYSIAWYDBHSAIIMYMMFUIOIYRYCHCLEFAOWNHOTSADIHIWYCTTACBIWLFYTTIIMLPIDOEGSSIIMATCIDLSDMSFHATHTLTIFTNWIATOYLMJOMATLOIMFJMWYTOOMDYLDBLPCEAT";
		int bigramSamples = 0;
		for (int i=0; i<cipher.length()-1; i++) {
			String bi = cipher.substring(i,i+2);
			bigramSamples++;
			if (bi.charAt(0) != bi.charAt(1)) continue;
			Long val = bigramCounts.get(bi);
			if (val == null) val = 0l;
			val++;
			bigramCounts.put(bi, val);
		}
		System.out.println("Doubles:");
		for (String s : bigramCounts.keySet()) {
			float ratio = bigramCounts.get(s);
			ratio /= bigramSamples;
			System.out.println(s + "	" + ratio);
		}
		
	}
	public static void main(String[] args) {
//		calculate();
//		test();
		LetterFrequencies f = new LetterFrequencies(); 
 		try {
			f.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
