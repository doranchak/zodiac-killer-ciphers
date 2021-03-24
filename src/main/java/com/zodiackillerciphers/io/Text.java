package com.zodiackillerciphers.io;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.lucene.LuceneService;
import com.zodiackillerciphers.lucene.Results;
import com.zodiackillerciphers.lucene.Scorer;
import com.zodiackillerciphers.morse.Morse;

/** object that represents textual input, its uppercase-letters-only converted format, 
 * and an index that maps the converted format to the original input. 
 */
public class Text {
	public StringBuffer textOriginal;
	public StringBuffer textConverted;
	public StringBuffer textMorse;
	
	public Map<Integer, Integer> mapOriginalToConverted;
	public Map<Integer, Integer> mapConvertedToOriginal;

	public Map<Integer, Integer> morseMapConvertedToMorse;
	public Map<Integer, Integer> morseMapMorseToConverted;
	
	
	public Text(StringBuffer sb) {
		textOriginal = new StringBuffer(sb);
		textConverted = new StringBuffer();
		mapOriginalToConverted = new HashMap<Integer, Integer>();
		mapConvertedToOriginal = new HashMap<Integer, Integer>();
		convert();
	}
	
	public void morse() {
		textMorse = new StringBuffer();
		morseMapConvertedToMorse = new HashMap<Integer, Integer>();		
		morseMapMorseToConverted = new HashMap<Integer, Integer>();
		
		int j=0; // index of morse
		String morse;
		for (int i=0; i<textConverted.length(); i++) { // index of converted text
			char ch = textConverted.charAt(i);
			morse = Morse.morseTable.get(ch);
			//System.out.println("convert " + ch + " to " + morse + ". " + textMorse +", j " + j);
			textMorse.append(morse);
			morseMapConvertedToMorse.put(i,j);
			morseMapMorseToConverted.put(j,i);
			for (int k=0; k<morse.length(); k++) {
				morseMapMorseToConverted.put(j,i);
				j++;
			}
		}
		
	}
	
	void convert() {
		if (textOriginal == null) return;
		String upper = textOriginal.toString().toUpperCase();
		
		int j=0;
		for (int i=0; i<upper.length(); i++) {
			char ch = upper.charAt(i);
			//System.out.println("i " + i + " j " + j + " ch [" + ch + "]");
			mapOriginalToConverted.put(i, j);
			mapConvertedToOriginal.put(j, i);
			if (ch > 64 && ch < 91) {
				textConverted.append(ch);
				j++;
			}
		}
		
	}
	
	public static String context(StringBuffer string, int contextBegin, int contextEnd, int prefixLength, int suffixLength, boolean highlight) {
		StringBuffer result = new StringBuffer();
		String prefix = "";
		int contextLength = contextEnd-contextBegin+1;
		int prefixBegin = Math.max(0, contextBegin-prefixLength);
		if (contextBegin > 0) prefix = string.substring(prefixBegin, contextBegin);
		String suffix = "";
		if (contextEnd < string.length()-1) {
			int suffixBegin = contextBegin+contextLength;
			int suffixEnd = Math.min(suffixBegin + suffixLength - 1, string.length() - 1);
			suffix = string.substring(suffixBegin, suffixEnd+1);
		}
		result.append(prefix);
		if (highlight) result.append(" [");
		result.append(string.substring(contextBegin,contextEnd+1));
		if (highlight) result.append("] ");
		result.append(suffix);
		return result.toString();
	}
	
	public void findStringAsMorse(String string) {
		String upper = FileUtil.convert(string.toUpperCase()).toString();
		String morse = Morse.toMorse(upper);
		
		int index = textMorse.indexOf(morse);
		while (index > -1) {
			int morseBegin = index;
			int morseEnd = index+morse.length()-1;
			//System.out.println("m " + morseBegin + ", " + morseEnd);
			int convertedBegin = morseMapMorseToConverted.get(morseBegin);
			//System.out.println("convertedBegin " + convertedBegin);
			int convertedEnd = morseMapMorseToConverted.get(morseEnd);
			int originalBegin = mapConvertedToOriginal.get(convertedBegin);
			int originalEnd = mapConvertedToOriginal.get(convertedEnd);
			String conv = textConverted.substring(convertedBegin,convertedEnd+1);
			String orig = textOriginal.substring(originalBegin,originalEnd+1);
			if (!upper.equals(conv)) {
				//System.out.println("[" + string + " (" + morse + ")] found at position as Morse [" + morse + "] at position [" + index + "] in converted [" + conv + "] original [" + orig + "]");
				
				// prefix/suffix of morse match
				
				/*String foundMorse = morse;
				String prefix = "";
				int prefixBegin = Math.max(0, morseBegin-20);
				if (morseBegin > 0) prefix = textMorse.substring(prefixBegin, morseBegin);
				String suffix = "";
				if (morseEnd < textMorse.length()-1) {
					int suffixBegin = morseBegin+foundMorse.length();
					int suffixEnd = Math.min(suffixBegin + 20 - 1, textMorse.length() - 1);
					suffix = textMorse.substring(suffixBegin, suffixEnd+1);
				}*/
				//Results r = LuceneService.query("+word:"+string, Scorer.freqScore, 10000000);
				//int freq = 0;
				//if (r.docs == null || r.docs.size() == 0) freq = 0;
				//else freq = LuceneService.freqAsIntFrom(r.docs.get(0));
				
				int freq = WordFrequencies.freq(string);
				System.out.println(string.length() + ", " + freq + ", " + string + ", " + morse.length());
				System.out.println("	" + string.toUpperCase());
				System.out.println("	..." + context(textMorse, morseBegin, morseEnd, 20, 20, true) + "...");
				System.out.println("	..." + context(textOriginal, originalBegin, originalEnd, 40, 40, false) + "...");
			}
			index = textMorse.indexOf(morse, index+1);
		}
	}
	
	public void dump() {
		System.out.println("Original: " + textOriginal);
		System.out.println("Converted: " + textConverted);
		System.out.println("Morse: " + textMorse);
	}
	
	public void dumpMapping() {
		int len = 10;
		for (int i=0; i<textOriginal.length() - len + 1; i++) {
			String subOriginal = textOriginal.substring(i,i+len);
			//System.out.println("i " + i + " i+len " + (i+len));
			int a = mapOriginalToConverted.get(i);
			int b = Math.min(textConverted.length(), mapOriginalToConverted.get(i+len-1));
			
			System.out.println("[" + subOriginal + "] [" + textConverted.substring(a,b+1) + "]");
		}
	}
	
	public static void main(String[] args) {
		StringBuffer test = new StringBuffer("THE CONFESSION BY____________ SHE WAS YOUNG AND BEAUTIFUL BUT NOW SHE IS BATTERED AND DEAD. SHE IS NOT THE  FIRST AND SHE WILL NOT BE THE LAST I LAY AWAKE NIGHTS THINKING ABOUT MY NEXT  VICTIM. MAYBE SHE WILL BE THE BEAUTIFUL BLOND THAT BABYSITS NEAR THE LITTLE  STORE AND WALKS DOWN THE DARK ALLEY EACH EVENING ABOUT SEVEN. OR MAYBE SHE WILL  BE THE SHAPELY BRUNETT THAT SAID NO WHEN I ASKED HER FOR A DATE IN HIGH SCHOOL.  BUT MAYBE IT WILL NOT BE EITHER. BUT I SHALL CUT OFF HER FEMALE PARTS AND  DEPOSIT THEM FOR THE WHOLE CITY TO SEE. SO DON'T MAKE IT TO EASY FOR ME. KEEP  YOUR SISTERS, DAUGHTERS, AND WIVES OFF THE STREETS AND ALLEYS. MISS BATES WAS  STUPID. SHE WENT TO THE SLAUGHTER LIKE A LAMB. SHE DID NOT PUT UP A STRUGGLE.  BUT I DID. IT WAS A BALL. I FIRST CUT THE MIDDLE WIRE FROM THE DISTRIBUTOR.  THEN I WAITED FOR HER IN THE LIBRARY AND FOLLOWED HER OUT AFTER ABOUT TWO  MINUTES. THE BATTERY MUST HAVE BEEN ABOUT DEAD BY THEN. I THEN OFFERED TO HELP.  SHE WAS THEN VERY WILLING TO TALK TO ME. I TOLD HER THAT MY CAR WAS DOWN THE  STREET AND THAT I WOULD GIVE HER A LIFT HOME. WHEN WE WERE AWAY FROM THE  LIBRARY WALKING, I SAID IT WAS ABOUT TIME. SHE ASKED ME, \"ABOUT TIME FOR WHAT?\"  I SAID IT WAS ABOUT TIME FOR HER TO DIE. I GRABBED HER AROUND THE NECK WITH MY  HAND OVER HER MOUTH AND MY OTHER HAND WITH A SMALL KNIFE AT HER THROAT. SHE  WENT VERY WILLINGLY. HER BREAST FELT WARM AND VERY FIRM UNDER MY HANDS, BUT  ONLY ONE THING WAS ON MY MIND. MAKING HER PAY FOR ALL THE BRUSH OFFS THAT SHE  HAD GIVEN ME DURING THE YEARS PRIOR. SHE DIED HARD. SHE SQUIRMED AND SHOOK AS I  CHOCKED HER, AND HER LIPS TWICHED. SHE LET OUT A SCREAM ONCE AND I KICKED HER  IN THE HEAD TO SHUT HER UP. I PLUNGED THE KNIFE INTO HER AND IT BROKE. I THEN  FINISHED THE JOB BY CUTTING HER THROAT. I AM NOT SICK. I AM INSANE. BUT THAT  WILL NOT STOP THE GAME. THIS LETTER SHOULD BE PUBLISHED FOR ALL TO READ IT. IT  JUST MIGHT SAVE THAT GIRL IN THE ALLEY. BUT THAT'S UP TO YOU. IT WILL BE ON  YOUR CONSCIENCE. NOT MINE. YES, I DID MAKE THAT CALL TO YOU ALSO. IT WAS JUST A  WARNING. BEWARE...I AM STALKING YOUR GIRLS NOW. CC. CHIEF OF POLICE ENTERPRISE");
		Text text = new Text(test);
		text.dump();
		text.dumpMapping();
		
		text.morse();
		System.out.println(text.textMorse);
		for (int j=0; j<text.textMorse.length(); j++) {
			int i=text.morseMapMorseToConverted.get(j);
			int k=text.mapConvertedToOriginal.get(i);
			//System.out.println(text.textMorse.charAt(j) + " " + text.textConverted.charAt(i) + " " + text.textOriginal.charAt(k));
		}
		text.findStringAsMorse("enter");
		
	}
}
