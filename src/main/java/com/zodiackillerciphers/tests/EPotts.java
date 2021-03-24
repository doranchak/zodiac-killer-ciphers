package com.zodiackillerciphers.tests;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;

/** https://thez340.blogspot.com/p/information-about-earl-vanbest.html */
public class EPotts {
	/** find "home" scrambled in text */
	public static void findHome(String text) {
		text = text.toUpperCase();
		for (int i = 0; i < text.length() - 4; i++) {
			String sub = text.substring(i, i + 5);
			if ((!sub.contains("HOME")) && sub.contains("H") && sub.contains("O") && sub.contains("M")
					&& sub.contains("E")) {
				System.out.println(i + " " + sub);
			}
		}
	}

	public static void findShortAnagrams(String text, int threshold) {
		text = text.toUpperCase();
		WordFrequencies.init();
		for (int i = 0; i < text.length() - 4; i++) {
			String sub = text.substring(i, i + 5);

			for (String word : WordFrequencies.map.keySet()) {
				if (word.length() == 4 || word.length() == 5) {
					if ((!sub.contains(word)) && Anagrams.anagram(word, sub, false)) {
						int perc = WordFrequencies.percentile(word);
						if (perc >= threshold)
							System.out.println(perc + " " + i + " " + sub + " " + word);
					}

				}
			}
		}

	}

	public static void main(String[] args) {
		// findHome(
		// "DIDYOUEVERNOTICEITLOOKSLIKEHOMESCRAMBLEDTIMESILOOKEDTOSEEIFTHEREWASAWILMOREKYBEFORETHATBOOKABOUTEVBANDASUSPECTWITHTHEBIRTHDAYITWASGARYSTEWARTHIMSELFTHATCONFIRMEDTHEDATEANDPLACEOFBIRTHTHATSOCALLEDQLOOKEDLIKEITCOULDBEANOTOMEWITHALINEASAPOINTERINEVERSETOUTTOMAKEALINKTOEVBOKMAYBEALITTLEAFTERTHEFACTBUTATTHETIMEIDIDNOTGETALLEXCITEDANDCONVINCEDIFOUNDSOMETHINGIMPORTANTLINKISBELOWHOMEISINATTACHMENTATLEASTTHATISWHATICALLITBTWIONLYGOTINVOLVEDWITHTHISBECAUSEAFRIENDHADABESTFRIENDMURDEREDBYASERIALKILLERNAMEDTEDBUNDYANDDONNALASSISSTILLMISSING");
		// findShortAnagrams(
		// "DIDYOUEVERNOTICEITLOOKSLIKEHOMESCRAMBLEDTIMESILOOKEDTOSEEIFTHEREWASAWILMOREKYBEFORETHATBOOKABOUTEVBANDASUSPECTWITHTHEBIRTHDAYITWASGARYSTEWARTHIMSELFTHATCONFIRMEDTHEDATEANDPLACEOFBIRTHTHATSOCALLEDQLOOKEDLIKEITCOULDBEANOTOMEWITHALINEASAPOINTERINEVERSETOUTTOMAKEALINKTOEVBOKMAYBEALITTLEAFTERTHEFACTBUTATTHETIMEIDIDNOTGETALLEXCITEDANDCONVINCEDIFOUNDSOMETHINGIMPORTANTLINKISBELOWHOMEISINATTACHMENTATLEASTTHATISWHATICALLITBTWIONLYGOTINVOLVEDWITHTHISBECAUSEAFRIENDHADABESTFRIENDMURDEREDBYASERIALKILLERNAMEDTEDBUNDYANDDONNALASSISSTILLMISSING");
		findShortAnagrams(Ciphers.Z408_SOLUTION, 80);
	}

}
