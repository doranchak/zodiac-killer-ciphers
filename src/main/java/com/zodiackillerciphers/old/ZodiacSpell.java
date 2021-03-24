/* this class is no longer used.*/
package com.zodiackillerciphers.old;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.*;
*/
public class ZodiacSpell {
  private static String dictFile = "/Users/doranchak/projects/work/java/zodiac/spellcheckers/jazzy/dict/english.0";
  //private static String phonetFile = "dict/phonet.en";
  
  /*
  private static SpellChecker spellCheck = null;
  
  public static boolean isWord(String word) {
  	if (spellCheck == null) {
      SpellDictionary dictionary;
			try {
				dictionary = new SpellDictionaryHashMap(new File(dictFile));
	      spellCheck = new SpellChecker(dictionary);
	      spellCheck.setUserDictionary(dictionary);
	      Zodiac.say("Spell Checker initialized.");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
  	}
  	return spellCheck.isCorrect(word);
  }*/
}
