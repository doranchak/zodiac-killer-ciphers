package com.zodiackillerciphers.constraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGrams;

/** an experiment with the YOU MY NAME BECAUSE YOU portion (and other portions) of the 408 cipher */
public class YouMyName {
	/** generate ngram scores for the partial decodings imposed by the collected cribs.
	 * we want to compare scores to see where the known solution ranks among all the putative solutions. */
	public static void experiment1(String plain, String file) {
		List<String> lines = FileUtil.loadFrom(file);
		
		String cipher = Ciphers.cipher[1].cipher;
		int pos = Ciphers.cipher[1].solution.indexOf(plain.toLowerCase());
		String sub = cipher.substring(pos, pos+plain.length()); 
		
		Map<Character, Character> decoder = new HashMap<Character, Character>();
		for (String line : lines) {
			for (int i=0; i<line.length(); i++) {
				char chcipher = sub.charAt(i);
				char chplain = line.charAt(i);
				decoder.put(chcipher, chplain);
			}
			
			StringBuffer decoded = new StringBuffer();
			for (int i=0; i<cipher.length(); i++) {
				char key = cipher.charAt(i);
				Character val = decoder.get(key);
				if (val == null || (i >= pos &&  i <= pos+plain.length()-1)) decoded.append(" ");
				else decoded.append(val);
			}
			System.out.println(line + ", " + decoded + ", " + NGrams.zkscore(decoded));
			
		}
		
		//System.out.println(sub);
	}
	
	/** another experiment - measure the constraint probabilities for sections of the 408's plaintext that matched against the gutenberg corpus */
	public static void experiment2() {
		List<String> lines = FileUtil.loadFrom("docs/corpus-search-results-new-408.txt");
		String cipher = Ciphers.cipher[1].cipher;
		String plain = Ciphers.cipher[1].solution;
		for (String line : lines) {
			int pos = plain.indexOf(line.toLowerCase());
			if (pos < 0) System.out.println("Could not find " + line + " in plaintext");
			else {
				String sub = cipher.substring(pos, pos+line.length());
				Info info = new Info(sub, 0);
				System.out.println(line + ", " + sub + ", " + info.probability);
			}
		}
	}
	
	public static void main(String[] args) {
		//experiment1("YOUMYNAMEBECAUSEYOU", "docs/constraint-search-408-article.txt");
		experiment1("LIKEKILLINGPEOPL", "docs/like-killing-people.txt");
	}
}
