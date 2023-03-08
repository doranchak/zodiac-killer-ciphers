package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTransposition;
import com.zodiackillerciphers.ciphers.algorithms.columnar.Variant;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** generate W168-like test ciphers */
public class W168TestCiphers extends CorpusBase {
	// A-Z, period, and space (the full plaintext alphabet)
	public static String alphabet1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ. ";
	
	// curated collection of plaintexts produced by the generate() method
	public static String plaintextsFile = "/Users/doranchak/projects/ciphers/W168/jarl-plaintexts.txt";
	public static List<String> plaintexts;
	static {
		plaintexts = FileUtil.loadFrom(plaintextsFile);
	}
	static Random rand = new Random();
	
	public static void randomCipher(boolean alternateReversedWords) {

		StringBuilder plaintext = randomPlaintext();
		System.out.println("<p class=\"head2\">Plaintext:</p>");
		System.out.println("<div class=\"plaintext-inline\">" + plaintext + "</div>");
		if (alternateReversedWords) {
			plaintext = alternateReverse(plaintext);
			System.out.println("<p class=\"head2\">With reversals:</p>");
			System.out.println("<div class=\"plaintext-inline\">" + plaintext + "</div>");
		}
		StringBuilder[] plaintextGrid = StringUtils.toRows(plaintext);
		StencilTransposition stencil = StencilTranspositionTemplates.randomStencil();
		
		System.out.println(StringUtils.html(plaintextGrid, "plaintext"));
		System.out.println("<p class=\"head\">Stencil: " + stencil.dimensions() + "</p>");
		System.out.println(stencil.htmlHoles());
		
		StringBuilder[] cipherGrid = stencil.encode(plaintextGrid);
		System.out.println("<p class=\"head\">Ciphertext:</p>");
		System.out.println("<div class=\"ciphertext-inline\">" + StringUtils.toLine(cipherGrid)+"</div>");
		System.out.println(StringUtils.html(cipherGrid, "ciphertext"));
		System.out.println("<p class=\"head\">Transposition Matrix:</p>");
		System.out.println(stencil.htmlTranspositionMatrix());
		System.out.println("<p class=\"head\">Distance Analysis:</p><pre class=\"distance\">");
		stencil.ctPositionsPeriods();
		System.out.println("</pre>");
		
		StringBuilder[] plaintextGridDecoded = stencil.decode(cipherGrid);
		//System.out.println("Decoded Plaintext:");
		//System.out.println(StringUtils.html(cipherGrid, "plaintext"));
		
		if (!StencilTransposition.testCheckValid(plaintext.toString(), plaintextGridDecoded)) {
			System.out.println(" ======== ERROR!!!!! PLAINTEXTS DON'T MATCH!!!!    ====== ");
			throw new RuntimeException("Shit's broken, yo");
		}
		
		//System.out.println(stencil.htmlHoles());
		System.out.println();
	}
	
	public static StringBuilder randomPlaintext() {
		return new StringBuilder(plaintexts.get(rand.nextInt(plaintexts.size())));
	}
	
	public static StringBuilder alternateReverse(StringBuilder text) {
		String[] split = text.toString().split(" ");
		
		boolean reverse = rand.nextBoolean();
		StringBuilder newText = new StringBuilder();
		
		for (String s : split) {
			StringBuilder chunk = new StringBuilder(s);
			if (reverse) {
				chunk = fixPeriod(chunk.reverse());
			}
			//System.out.println(s + ", " + chunk);
			if (newText.length() > 0) newText.append(" ");
			newText.append(chunk);
			reverse = !reverse;
		}
		return newText;
		
	}
	public static StringBuilder fixPeriod(StringBuilder text) {
		if (text.indexOf(".") > -1) {
			text.deleteCharAt(text.indexOf("."));
			text.append(".");
		}
		return text;
	}
	
	public static void main(String[] args) {
		//generate(100, 168, alphabet1);
		//test();
		for (int i=0; i<100; i++) randomCipher(true);
	}

}
