package com.zodiackillerciphers.ciphers;

import java.util.HashMap;
import java.util.Map;

/* the cipher mike moffatt found.  see swontario.ca */
public class MoffattCipher {
	public static String[][] pages = new String[][] {
			{ "ufo", "leaf", "olive", "fish right", "apple", "dotted square",
					"yin yang", "crosshairs", "square", "clover", "key",
					"bird", "dee", "arrow", "circle", "golf green", "triangle",
					"clover", "ufo", "fire", "box", "arrows diag", "fish left",
					"pac man", "crosshairs", "3 lines", "eye",
					"dashed triangle", "arrow", "sun", "earth", "golf green",
					"addidas", "rhombus", "grass", "key", "apple", "olive",
					"dotted circle", "eye", "crosshairs", "pac man",
					"filled star", "apple", "hook", "yin yang", "clover",
					"triangle", "bird", "eye", "square", "toothbrush",
					"dotted square", "3 lines", "hollow star", "olive",
					"equals", "crosshairs", "eye", "triangle", "dotted circle",
					"slashed circle", "filled star", "grass", "leaf", "ufo",
					"filled star", "rhombus", "ufo", "circle",
					"triangled square", "arrows diag", "golf green", "lamp",
					"sun", "dotted circle", "ufo", "eye", "fire",
					"dotted square", "hollow star", "box", "key", "leaf",
					"dotted square", "slashed circle", "olive", "prohibited",
					"leaf", "feather", "addidas", "toothbrush", "circle",
					"triangle", "filled star", "tag", "pac man", "tag",
					"dotted circle", "ufo", "diamond", "superman", "triangle",
					"square", "flower", "puzzle", "bird", "apple", "arrow",
					"yin yang", "feather", "golf green", "hollow star", "lamp",
					"apple", "filled star", "eye", "toothbrush", "square",
					"leaf", "hand", "ufo", "olive", "dotted circle",
					"slashed circle", "sun" },
			{ "diamond", "box", "broccoli", "bird", "feather", "leaf", "key",
					"triangled square", "eye", "dotted square", "olive",
					"square", "arrow", "grass", "filled star", "hand", "sun",
					"triangled square", "feather", "box", "radioactive", "eye",
					"filled star", "earth", "hand", "hollow star",
					"toothbrush", "rhombus", "apple", "key", "broccoli",
					"feather", "box", "slashed circle", "dashed triangle",
					"dotted square", "puzzle", "superman", "arrow", "dee",
					"golf green", "eye", "box", "key", "hook", "square",
					"triangle", "equals", "dotted circle", "diamond", "leaf",
					"diamond", "equals", "ufo", "earth", "clover",
					"slashed circle", "olive", "dotted circle", "hook", "bird",
					"box", "feather", "arrows diag", "hook", "links",
					"hollow star", "radioactive", "olive", "3 lines", "square",
					"flower", "dotted square", "hollow star", "bird", "arrow",
					"fish right", "golf green", "hand", "triangle", "3 lines",
					"yin yang", "hand", "equals", "radioactive", "hollow star",
					"ufo", "circle", "dotted square", "hollow star", "olive",
					"fish left", "leaf", "key", "hook", "diamond", "diamond",
					"feather", "earth", "slashed circle", "dotted circle",
					"triangled square", "fire", "equals", "prohibited",
					"triangle", "leaf", "square", "tag", "hand", "arrow",
					"apple", "grass", "ufo", "golf green", "feather",
					"crosshairs", "diamond", "pac man", "ufo", "dotted circle",
					"diamond", "slashed circle", "triangle", "dotted circle",
					"olive", "apple", "bird", "puzzle" },
			{ "circle", "triangled square", "bird", "leaf", "golf green",
					"superman", "box", "diamond", "fire", "addidas",
					"broccoli", "feather", "lamp", "links", "wind", "knife",
					"key", "box", "equals", "radioactive", "3 lines", "square",
					"hollow star", "olive", "grass", "links", "wind", "puzzle",
					"fish right", "pac man", "ufo", "dotted circle", "eye",
					"yin yang", "diamond", "dee", "box", "addidas", "lamp",
					"bird", "toothbrush", "clover", "earth", "dotted square",
					"circle", "square", "dashed triangle", "hand",
					"golf green", "dotted circle", "triangle", "equals",
					"dotted circle", "triangle", "flower", "dotted square",
					"hand", "yin yang", "square", "crosshairs", "tag",
					"circle", "yin yang", "filled star", "flower",
					"dashed triangle", "superman", "broccoli", "wind",
					"superman", "addidas", "feather", "fire", "box", "hook",
					"golf green", "triangled square", "feather", "circle",
					"toothbrush", "3 lines", "radioactive", "feather",
					"triangled square", "diamond", "olive", "toothbrush",
					"square", "crosshairs", "fish left", "golf green",
					"dotted square", "grass", "bird", "pac man", "ufo",
					"diamond", "dotted square", "eye", "puzzle", "equals",
					"radioactive", "filled star", "square", "yin yang",
					"equals", "radioactive", "square", "crosshairs",
					"dotted circle", "hollow star", "3 lines", "box",
					"feather", "arrow", "apple", "sun", "leaf", "bird",
					"triangled square", "prohibited", "puzzle", "ufo",
					"broccoli", "hollow star", "dotted square", "feather",
					"ufo", "square", "arrow", "crosshairs", "3 lines", "earth" } };

	public static String zkSolution = "SLHCASDREIEDONAFNISTRUEEREIGNLWFUBIEAHAIRETANDINDIEMSETHERINATTILSTBSARUFHLASITSTRELSTHELOUMANTHEHASTONEANDANDOFTHATIMELISHATLYTRPDOLERISHENITILRORSITWITMBAEPORTGSNONOFIRENENEATLTESWITHANDROUNCTSHEEASTDNCFINEDIESTSASTHELENTTOWTARTEENLEHINAISFORTESATTNAHADNKARDLFORTTUPOHCOVERESEETHICONCESAIDTORUHDMIWSAEGIFANEANASIDERHADTAGOPOOUOTRNFROAMESORTHMEREFSIDESTSINESTEDESERATERONALLDRENSPTSOSENREW";

	/** convert to single-character symbols */
	public static void toCipher() {
		String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		int current = 0;
		String cipher = "";
		Map<String, Character> map = new HashMap<String, Character>();
		
		for (String[] page : pages) {
			for (String key : page) {
				Character val = map.get(key);
				if (val == null) {
					val = symbols.charAt(current++);
					System.out.println("KEY/VAL: " + key + " = " + val);
					System.out.println("VAL/KEY: " + val + " = " + key);
				}
				map.put(key, val);
				cipher += val;
			}
			System.out.println(cipher);
			cipher = "";
		}
	}

	public static void main(String[] args) {
		toCipher();
		/*
		String c = toCipher();
		System.out.println(c);
		NGramsBean bean = NGrams.ngrams(2, c);
		bean.dump();

		WordFrequencies.init();
		for (int i = 0; i < zkSolution.length(); i++) {
			List<String> words = WordFrequencies.candidateWordsFor(
					zkSolution.substring(i), 60);
			for (String word : words)
				System.out.println(word.length() + " " + i + " " + word);
		}*/
	}
}
