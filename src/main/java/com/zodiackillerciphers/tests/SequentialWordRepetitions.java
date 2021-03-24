package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;

/**
 * https://www.voynich.ninja/thread-2357-post-20223.html
 * 
 * read corpus and find instances of words that repeat right next to each other,
 * or with one word in between
 */
public class SequentialWordRepetitions {
	static String[] files = new String[] { "11-0.txt", "1184-0.txt", "1342-0.txt", "1400-0.txt", "158-0.txt",
			"1966-11-29-the-confession.txt", "1966-12-desk.txt", "1967-04-30-bates.txt", "1969-07-31-cipher.txt",
			"1969-07-31-letter-envelope.txt", "1969-07-31-letter-sf-examiner-envelope.txt",
			"1969-07-31-letter-sf-examiner.txt", "1969-07-31-letter-vallejo-times-herald-envelope.txt",
			"1969-07-31-letter-vallejo-times-herald.txt", "1969-07-31-letter.txt", "1969-08-04-examiner.txt",
			"1969-09-27-car-door.txt", "1969-10-13-examiner-envelope.txt", "1969-10-13-examiner.txt",
			"1969-11-08-340-cipher-envelope.txt", "1969-11-08-340-cipher.txt", "1969-11-09-chronicle-envelope.txt",
			"1969-11-09-chronicle.txt", "1969-12-20-melvin-envelope.txt", "1969-12-20-melvin.txt",
			"1970-04-20-my-name-is-envelope.txt", "1970-04-20-my-name-is.txt", "1970-04-28-chronicle.txt",
			"1970-06-26-chronicle-cipher-envelope.txt", "1970-06-26-chronicle-cipher.txt",
			"1970-07-24-chronicle-envelope.txt", "1970-07-24-chronicle.txt", "1970-07-26-chronicle-envelope.txt",
			"1970-07-26-chronicle.txt", "1970-10-05.txt", "1970-10-27-avery-envelope.txt", "1970-10-27-avery.txt",
			"1971-03-13-times-envelope.txt", "1971-03-13-times.txt", "1971-03-22-lake-tahoe-card-back.txt",
			"1971-03-22-lake-tahoe-card.txt", "1974-01-29-exorcist-envelope.txt", "1974-01-29-exorcist.txt",
			"1974-02-14-sla-envelope.txt", "1974-02-14-sla.txt", "1974-05-08-badlands-envelope.txt",
			"1974-05-08-badlands.txt", "1974-07-08-count-marco-red-phantom-envelope.txt",
			"1974-07-08-count-marco-red-phantom.txt", "1978-04-24-chronicle.txt", "2591-0.txt", "2600-0.txt",
			"49783-0.txt", "74-0.txt", "atlas-shrugged.txt", "juliette.txt", "most-dangerous-game.txt",
			"oeuvresdumarquis00sade_djvu.txt", "pg10.txt", "pg100.txt", "pg10462.txt", "pg10799.txt", "pg11364.txt",
			"pg1184.txt", "pg11889.txt", "pg12180.txt", "pg1232.txt", "pg1260.txt", "pg1322.txt", "pg1342.txt",
			"pg135.txt", "pg14833.txt", "pg1661.txt", "pg16885.txt", "pg174.txt", "pg17707.txt", "pg1952.txt",
			"pg23.txt", "pg2542.txt", "pg25880.txt", "pg2591.txt", "pg2600.txt", "pg2701.txt", "pg27827.txt",
			"pg2800.txt", "pg28718.txt", "pg30.txt", "pg30254.txt", "pg30601.txt", "pg345.txt", "pg4300.txt",
			"pg505.txt", "pg5200.txt", "pg747.txt", "pg76.txt", "pg808.txt", "pg84.txt", "pg844.txt", "pg9296.txt",
			"pg9798.txt", "pg9881.txt", "tale.txt" };
	static String prefix = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/corpora-for-nickpelling-filter-test/";
	
	static String excerpt(String[] tokens, int i) {
		int beginning = Math.max(0,  i-10);
		int ending = Math.min(tokens.length-1, i+10);
		String result = "";
		for (int j=beginning; j<=ending; j++) {
			result += tokens[j] + " ";
		}
		return result;
	}
	
	static void find() {
		for (String file : files) {
			String text = FileUtil.loadSBFrom(prefix + file).toString();
			System.out.println("=== LOADED " + file);
			String[] tokensBefore = FileUtil.tokenize(text);
			List<String> tokensAfter = new ArrayList<String>();
			for (String token : tokensBefore) if (token.length() > 0) tokensAfter.add(token);
			String[] tokens = tokensAfter.toArray(new String[0]);
			
			for (int i=1; i<tokens.length; i++) {
				if (tokens[i].length() == 0) continue;
				if (tokens[i].equals(tokens[i-1])) {
					System.out.println("SEQUENTIAL [" + tokens[i] + "]: " + excerpt(tokens, i-1));
				}
				if (i>1 && tokens[i].equals(tokens[i-2])) {
					System.out.println("ALTERNATING [" + tokens[i] + "]: " + excerpt(tokens, i-1));
				}
				if (i > 2 && tokens[i].equals(tokens[i - 2]) && tokens[i - 1].equals(tokens[i - 3])) {
					System.out.println("XYXY [" + tokens[i - 1] + " " + tokens[i] + "]: " + excerpt(tokens, i - 1));
				}
			}
			
		}
	}
	
	static void findWordsThatAreWordsWhenRepeated() {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (WordFrequencies.map.containsKey(word + word)) {
				String ww = word + word;
				System.out.println(WordFrequencies.freq(ww) + " " + word + " " + ww);
			}
		}
	}
	
	public static void main(String[] args) {
		find();
		//findWordsThatAreWordsWhenRepeated();
	}
}
