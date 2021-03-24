package com.zodiackillerciphers.voynich;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.names.Census;


public class VoynichAnagrams {
	
	public static Map<String, String> langMap = new HashMap<String, String>();
	static { // https://www.opensubtitles.org/addons/export_languages.php
		langMap.put("aa", "Afar, afar");
		langMap.put("ab", "Abkhazian");
		langMap.put("ae", "Avestan");
		langMap.put("af", "Afrikaans");
		langMap.put("ak", "Akan");
		langMap.put("am", "Amharic");
		langMap.put("an", "Aragonese");
		langMap.put("ar", "Arabic");
		langMap.put("as", "Assamese");
		langMap.put("at", "Asturian");
		langMap.put("av", "Avaric");
		langMap.put("ay", "Aymara");
		langMap.put("az", "Azerbaijani");
		langMap.put("ba", "Bashkir");
		langMap.put("be", "Belarusian");
		langMap.put("bg", "Bulgarian");
		langMap.put("bh", "Bihari");
		langMap.put("bi", "Bislama");
		langMap.put("bm", "Bambara");
		langMap.put("bn", "Bengali");
		langMap.put("bo", "Tibetan");
		langMap.put("br", "Breton");
		langMap.put("bs", "Bosnian");
		langMap.put("ca", "Catalan");
		langMap.put("ce", "Chechen");
		langMap.put("ch", "Chamorro");
		langMap.put("co", "Corsican");
		langMap.put("cr", "Cree");
		langMap.put("cs", "Czech");
		langMap.put("cu", "Church Slavic");
		langMap.put("cv", "Chuvash");
		langMap.put("cy", "Welsh");
		langMap.put("da", "Danish");
		langMap.put("de", "German");
		langMap.put("dv", "Divehi");
		langMap.put("dz", "Dzongkha");
		langMap.put("ea", "Spanish (LA)");
		langMap.put("ee", "Ewe");
		langMap.put("el", "Greek");
		langMap.put("en", "English");
		langMap.put("eo", "Esperanto");
		langMap.put("es", "Spanish");
		langMap.put("et", "Estonian");
		langMap.put("eu", "Basque");
		langMap.put("ex", "Extremaduran");
		langMap.put("fa", "Persian");
		langMap.put("ff", "Fulah");
		langMap.put("fi", "Finnish");
		langMap.put("fj", "Fijian");
		langMap.put("fo", "Faroese");
		langMap.put("fr", "French");
		langMap.put("fy", "Frisian");
		langMap.put("ga", "Irish");
		langMap.put("gd", "Gaelic");
		langMap.put("gl", "Galician");
		langMap.put("gn", "Guarani");
		langMap.put("gu", "Gujarati");
		langMap.put("gv", "Manx");
		langMap.put("ha", "Hausa");
		langMap.put("he", "Hebrew");
		langMap.put("hi", "Hindi");
		langMap.put("ho", "Hiri Motu");
		langMap.put("hr", "Croatian");
		langMap.put("ht", "Haitian");
		langMap.put("hu", "Hungarian");
		langMap.put("hy", "Armenian");
		langMap.put("hz", "Herero");
		langMap.put("ia", "Interlingua");
		langMap.put("id", "Indonesian");
		langMap.put("ie", "Interlingue");
		langMap.put("ig", "Igbo");
		langMap.put("ii", "Sichuan Yi");
		langMap.put("ik", "Inupiaq");
		langMap.put("io", "Ido");
		langMap.put("is", "Icelandic");
		langMap.put("it", "Italian");
		langMap.put("iu", "Inuktitut");
		langMap.put("ja", "Japanese");
		langMap.put("jv", "Javanese");
		langMap.put("ka", "Georgian");
		langMap.put("kg", "Kongo");
		langMap.put("ki", "Kikuyu");
		langMap.put("kj", "Kuanyama");
		langMap.put("kk", "Kazakh");
		langMap.put("kl", "Kalaallisut");
		langMap.put("km", "Khmer");
		langMap.put("kn", "Kannada");
		langMap.put("ko", "Korean");
		langMap.put("kr", "Kanuri");
		langMap.put("ks", "Kashmiri");
		langMap.put("ku", "Kurdish");
		langMap.put("kv", "Komi");
		langMap.put("kw", "Cornish");
		langMap.put("ky", "Kirghiz");
		langMap.put("la", "Latin");
		langMap.put("lb", "Luxembourgish");
		langMap.put("lg", "Ganda");
		langMap.put("li", "Limburgan");
		langMap.put("ln", "Lingala");
		langMap.put("lo", "Lao");
		langMap.put("lt", "Lithuanian");
		langMap.put("lu", "Luba-Katanga");
		langMap.put("lv", "Latvian");
		langMap.put("ma", "Manipuri");
		langMap.put("me", "Montenegrin");
		langMap.put("mg", "Malagasy");
		langMap.put("mh", "Marshallese");
		langMap.put("mi", "Maori");
		langMap.put("mk", "Macedonian");
		langMap.put("ml", "Malayalam");
		langMap.put("mn", "Mongolian");
		langMap.put("mo", "Moldavian");
		langMap.put("mr", "Marathi");
		langMap.put("ms", "Malay");
		langMap.put("mt", "Maltese");
		langMap.put("my", "Burmese");
		langMap.put("na", "Nauru");
		langMap.put("nb", "Norwegian Bokmal");
		langMap.put("nd", "Ndebele, North");
		langMap.put("ne", "Nepali");
		langMap.put("ng", "Ndonga");
		langMap.put("nl", "Dutch");
		langMap.put("nn", "Norwegian Nynorsk");
		langMap.put("no", "Norwegian");
		langMap.put("nr", "Ndebele, South");
		langMap.put("nv", "Navajo");
		langMap.put("ny", "Chichewa");
		langMap.put("oc", "Occitan");
		langMap.put("oj", "Ojibwa");
		langMap.put("om", "Oromo");
		langMap.put("or", "Odia");
		langMap.put("os", "Ossetian");
		langMap.put("pa", "Panjabi");
		langMap.put("pb", "Portuguese (BR)");
		langMap.put("pi", "Pali");
		langMap.put("pl", "Polish");
		langMap.put("pm", "Portuguese (MZ)");
		langMap.put("ps", "Pushto");
		langMap.put("pt", "Portuguese");
		langMap.put("qu", "Quechua");
		langMap.put("rm", "Raeto-Romance");
		langMap.put("rn", "Rundi");
		langMap.put("ro", "Romanian");
		langMap.put("ru", "Russian");
		langMap.put("rw", "Kinyarwanda");
		langMap.put("sa", "Sanskrit");
		langMap.put("sc", "Sardinian");
		langMap.put("sd", "Sindhi");
		langMap.put("se", "Northern Sami");
		langMap.put("sg", "Sango");
		langMap.put("si", "Sinhalese");
		langMap.put("sk", "Slovak");
		langMap.put("sl", "Slovenian");
		langMap.put("sm", "Samoan");
		langMap.put("sn", "Shona");
		langMap.put("so", "Somali");
		langMap.put("sp", "Spanish (EU)");
		langMap.put("sq", "Albanian");
		langMap.put("sr", "Serbian");
		langMap.put("ss", "Swati");
		langMap.put("st", "Sotho, Southern");
		langMap.put("su", "Sundanese");
		langMap.put("sv", "Swedish");
		langMap.put("sw", "Swahili");
		langMap.put("sy", "Syriac");
		langMap.put("ta", "Tamil");
		langMap.put("te", "Telugu");
		langMap.put("tg", "Tajik");
		langMap.put("th", "Thai");
		langMap.put("ti", "Tigrinya");
		langMap.put("tk", "Turkmen");
		langMap.put("tl", "Tagalog");
		langMap.put("tn", "Tswana");
		langMap.put("to", "Tonga (Tonga Islands)");
		langMap.put("tr", "Turkish");
		langMap.put("ts", "Tsonga");
		langMap.put("tt", "Tatar");
		langMap.put("tw", "Twi");
		langMap.put("ty", "Tahitian");
		langMap.put("ug", "Uighur");
		langMap.put("uk", "Ukrainian");
		langMap.put("ur", "Urdu");
		langMap.put("uz", "Uzbek");
		langMap.put("ve", "Venda");
		langMap.put("vi", "Vietnamese");
		langMap.put("vo", "Volapük");
		langMap.put("wa", "Walloon");
		langMap.put("wo", "Wolof");
		langMap.put("xh", "Xhosa");
		langMap.put("yi", "Yiddish");
		langMap.put("yo", "Yoruba");
		langMap.put("za", "Zhuang");
		langMap.put("ze", "Chinese bilingual");
		langMap.put("zh", "Chinese (simplified)");
		langMap.put("zt", "Chinese (traditional)");
		langMap.put("zu", "Zulu");
	}
	
	public static void processVoynich(int limit, float minCoverage) {
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/word-frequency-lists/voynich-dictionary-with-frequencies.txt");
		List<String> list2 = new ArrayList<String>();
		for (String line : list) {
			String[] split = line.split("	");
			list2.add(split[2] + " " + split[1]);
		}
		process("voynich", list2, limit, minCoverage);
	}
	public static void processOpenSubtitles(int limit, float minCoverage) {
		String[] langs = new String[] { "af", "ar", "bg", "bn", "br", "bs", "ca", "cs", "da", "de", "el", "en", "eo",
				"es", "et", "eu", "fa", "fi", "fr", "gl", "he", "hi", "hr", "hu", "hy", "id", "is", "it", "ja", "ka",
				"kk", "ko", "lt", "lv", "mk", "ml", "ms", "nl", "no", "pl", "pt", "pt_br", "ro", "ru", "si", "sk", "sl",
				"sq", "sr", "sv", "ta", "te", "th", "tl", "tr", "uk", "vi", "zh", "zh_tw" };
		for (String langID : langs)
			processOpenSubtitles(langID, limit, minCoverage);
	}
	public static void processOpenSubtitles(String langID, int limit, float minCoverage) {
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/word-frequency-lists/FrequencyWords-master/content/2016/" + langID + "/" + langID + "_full.txt");
		process("OpenSubtitles_" + langID + "_" + langMap.get(langID.substring(0,2)), list, limit, minCoverage);
	}
	/** expects list of space delimited pairs of "word freq" */
	public static void process(String name, List<String> lines, int limit, float minCoverage) {
		System.out.println("=============== PROCESSING " + name);
		Map<String, Set<String>> words = new HashMap<String, Set<String>>(); 
		Map<String, Integer> wordFrequencies = new HashMap<String, Integer>();
		Map<Integer, Integer> groupSizeCounts = new HashMap<Integer, Integer>();
		int freqSum = 0;
		int count = 0;
		String tab = "	";
		for (String line : lines) {
			String[] split = line.split(" ");
			String word = split[0];
			Integer freq = Integer.valueOf(split[1]);
			freqSum += freq;
			wordFrequencies.put(word, freq);
			String sorted = Census.sortString(word);
			Set<String> val = words.get(sorted);
			if (val == null) {
				val = new HashSet<String>();
			}
			val.add(word);
			words.put(sorted, val);
			count++;
			if (count == limit) break;
		}
		int groups = 0;
		for (String key : words.keySet()) {
			Set<String> val = words.get(key);
			Integer gsc = groupSizeCounts.get(val.size());
			if (gsc == null) gsc = 0;
			gsc++;
			groupSizeCounts.put(val.size(), gsc);
			float freqSum2 = 0;
			for (String word : val) freqSum2 +=  wordFrequencies.get(word);
			freqSum2 = 100 * freqSum2 / freqSum;
			if (val.size() > 1 && freqSum2/100 >= minCoverage) {
				groups++;
				float var = variance(wordFrequencies, val); 
				//float score = (1-freqSum2/100)*(1+var);
				System.out.println(var + tab + freqSum2 + tab + val.size() + tab + key + tab + info(wordFrequencies, val));
			}
		}
		
		// output:
		// - number of words
		// - number of anagram groups of size > 1
		// - ratio of non-anagrammable words to anagrammable words
		// - mean group size for groups of size > 0
		// - max group size
		// - counts for group sizes
		
		String output = count + tab + groups + tab;
		float ratio = groupSizeCounts.get(1);
		ratio /= count;
		output += ratio + tab;
		
		// mean group size
		float meanGroupSize = 0; float meanGroupCount = 0;
		int maxGroupSize = 0;
		for (Integer key : groupSizeCounts.keySet()) {
			meanGroupSize += (key*groupSizeCounts.get(key));
			meanGroupCount += groupSizeCounts.get(key);
			maxGroupSize = Math.max(maxGroupSize, key);
		}
//		System.out.println("meanGroupSize " + meanGroupSize + " meanGroupCount " + meanGroupCount);
		meanGroupSize /= meanGroupCount; 
		output += meanGroupSize + tab + maxGroupSize + tab;
		for (int i=1; i<21; i++) {
			Integer val = groupSizeCounts.get(i);
			if (val == null) val = 0;
			output += val + tab;
		}
		System.out.println();
		System.out.println("RESULTS:");
		System.out.println(name + tab + output);
		System.out.println();
		
		
//		System.out.println("Group size counts:");
//		System.out.println(groupSizeCounts);
		
	}
	
	public static String info(Map<String, Integer> freq, Set<String> words) {
		String info = words.toString();
		String counts = "";
		for (String word : words) {
			if (counts.length() > 0) counts += ", ";
			counts += freq.get(word);
		}
		return info + " (" + counts + ")";
	}
	public static float variance(Map<String, Integer> freq, Set<String> words) {
		float mean = 0;
		for (String word : words) {
			mean += freq.get(word);
		}
		mean /= words.size();
		float variance = 0;
		for (String word : words) {
			variance += Math.pow(freq.get(word) - mean, 2);
		}
		variance /= words.size();
		return variance;
	}
	
	// to compare against the voynich dictionary
	public static void processEnglish(int limit, float minCoverage) {
		Map<String, Set<String>> words = new HashMap<String, Set<String>>(); 
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/english_words__practicalcryptography.txt");
		List<String> list2 = new ArrayList<String>();
		int count = 0;
		for (String word : list) {
			String w = word.split(" ")[0].toLowerCase();
			list2.add(w + " " + word.split(" ")[1]);
			count++;
			if (count == limit) break;
		}
		process("PracticalCryptography_en", list2, limit, minCoverage);
	}
//	public static void processEnglish2() {
//		Map<String, Set<String>> words = new HashMap<String, Set<String>>(); 
//		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/english_words__practicalcryptography.txt");
//		Map<String, Integer> wordFrequencies = new HashMap<String, Integer>();
//		int freqSum = 0;
//		int count = 0;
//		for (String line : list) {
//			String[] split = line.toLowerCase().split(" ");
//			String word = split[0];
//			Integer freq = Integer.valueOf(split[1]);
//			freqSum += freq;
//			wordFrequencies.put(word, freq);
//			String sorted = Census.sortString(word);
//			Set<String> val = words.get(sorted);
//			if (val == null) {
//				val = new HashSet<String>();
//			}
//			val.add(word);
//			words.put(sorted, val);
//			count++;
//			if (count == 8078) break;
//		}
//		for (String key : words.keySet()) {
//			Set<String> val = words.get(key);
//			float freqSum2 = 0;
//			for (String word : val) freqSum2 +=  wordFrequencies.get(word);
//			freqSum2 = 100 * freqSum2 / freqSum;
//			if (val.size() > 1) System.out.println(freqSum2 + "	" + val.size() + "	" + key + "	" + val);
//		}
//	}
	
	public static void main(String[] args) {
		//process(0.001f);
//		processOpenSubtitles("ka", 8078, -10f);
//		processOpenSubtitles(8078, -10f);
		processVoynich(8078, -10f);
//		processEnglish(8078, -10f);
//		processEnglish(8078, 0.001f);
//		processEnglish2();
	}
	
}
