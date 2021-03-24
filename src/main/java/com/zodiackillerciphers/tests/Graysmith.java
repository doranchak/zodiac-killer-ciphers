package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.ZodiacSymbols;

/** testing graysmith's cipher related claims */
public class Graysmith {
	
	// Kahn's mappings
	public static Map<Character, Character> p2c = new HashMap<Character, Character>();
	public static Map<Character, Character> c2p = new HashMap<Character, Character>();
	public static String plain = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String cipher = "LBQACSRDTOFVMHWIJXGKYUNZEP";
	static {
		for (int i=0; i<plain.length(); i++) {
			p2c.put(plain.charAt(i), cipher.charAt(i));
			c2p.put(cipher.charAt(i), plain.charAt(i));
		}
	}
	
	/** testing Graysmith's claim that Zodiac used a sample cipher alphabet developed by David Kahn */ 
	public static void test(int trials) {
		
		// number of symbols to assign to each plain text letter [A,Z].
		int[] totals = new int[] {
			4,1,1,2,7,2,1,2,4,0,1,3,1,4,4,1,0,3,4,4,1,1,1,1,1,0	
		};
		
		Random rand = new Random();
		// alphabet of the 408
		String alphabet = Ciphers.alphabet[1];
		
		int total = 0; int total2 = 0;
		for (int i=0; i<trials; i++) {
			int hits1 = 0; int hits2 = 0;
			List<Character> list = new ArrayList<Character>();
			for (char ch : alphabet.toCharArray()) list.add(ch);
			
			String[] assignments = new String[totals.length];
			for (int j=0; j<assignments.length; j++) assignments[j] = "";
			
			int j=0;
			while (!list.isEmpty()) {
				while (assignments[j].length() >= totals[j]) j++;
				
				int r = rand.nextInt(list.size());
				assignments[j] += list.get(r);
				
				list.remove(r);
			}
			for (int k=0; k<totals.length; k++) {
				char letter = (char)(65+k);
				
				String line = letter + " : " + assignments[k];
				
				String interpretations = ZodiacSymbols.interpret(assignments[k]);
				
				line += ", " + interpretations;
				
				if (interpretations.contains(""+p2c.get(letter))) {
					line += ", HIT1";
					hits1++;
				}
				if (interpretations.contains(""+c2p.get(letter))) {
					line += ", HIT2";
					hits2++;
					
				}
				System.out.println(line);
			}
			System.out.println(hits1 + ", " + hits2 + ", " + (hits1+hits2) + ", trial " + i);
			total += hits1 + hits2;
			if (hits1 >= 3) total2++;
			
		}
		System.out.println("Trials: " + trials + ", total: " + total + ", total2: " + total2 + ", average: " + (((float)total))/trials);
	}

	/** from Graysmith's solution in FBI files, compare raw decryption to anagrams.  find the mistakes.  measure "tightness" of anagrams. */ 
	public static void verify() {
		int WINDOW=18;
		String        raw = "HERCEANBIGIVETHEMHELLTOOOTED.DELEHLSACILUEHETHEOMHSSEEANAMEBWEOLLRKESAILMEFMIAPILLSG/EMRNPAOLEMSGPCETTOALKTOTEUCSHB.MEITHESEFOOLSHDMLPEAMDILKLEREPLCSAESKLMAUBLNSLOEADPLSLULR/AEEITDLEKTIKOEDCARSIEATAILLLPMEASSOLHIAPLCTOMRAHPHNEAESK.CBSELLSLSVEASASECBUEALLIC.WMLLTOEN.EITHERCTLEVESDLPAELIHELLHESMECIOSHT/THEIPGMEDAELSDOLELSCCITHHEGSMEOMAIKTL";
		String transposed = "HERBCAENIGIVETHEMHELLTOOTODE EDHELLISACLUEHETHESOMESEEANAMEBELOWAKILLERSGAMEISPILLS  PAROLEDMECOPSMETTOTALKTOMESUCHTIMETHESEFOOLSHELPDMEMADKILLERPLACESAMASKBULLSALONEPLEASUREDIDLIKETOKILLSCAREDIEATAPILLMASSHOLEIPLANTOHARM PHENEASK CBSELLSSLAVESBECAUSEALL COLLECTION EITHERPLEASEDTOLIEINHELLHESMETOSCHI THEPIGLEADSMECOLLECTSEIGHTHSOMEMAILKT";
		StringBuffer raw2 = new StringBuffer();
		for (int i=0; i<raw.length(); i++) raw2.append(".");
		int notFound = 0;
		int distances = 0;
		boolean[] used = new boolean[raw.length()];
		for (int i=0; i<transposed.length(); i++) {
			char c1 = transposed.charAt(i);
			if (c1 == ' ') continue;
			int distance = 0;
			char c2 = raw.charAt(i);
			if (c1 == c2 && !used[i]) {
				used[i] = true;
				raw2.setCharAt(i, c1);
				out(c1, i, i, 0, distances, raw2);
				continue;
			}
			// check up to WINDOW positions ahead or behind for the given letter
			boolean found = false;
			int[] m = new int[] {1, -1};
			for (int j=1; j<=WINDOW; j++) {
				if (found) break;
				for (int k : m) {
//					System.out.println("k " + k);
					if (found) break;
					int pos = i+j*k;
//					System.out.println("pos " + pos);
//					System.out.println(Arrays.toString(used));
					if (pos >= 0 && pos<raw.length()) {
						if (used[pos]) continue;
						c2 = raw.charAt(pos);
//						System.out.println(i + " " + j + " " + k + " " + pos + " " + c1 + " " + c2);
						if (c1 == c2) {
							used[pos] = true;
							raw2.setCharAt(pos, c1);
							distance = Math.abs(i-pos);
							distances += distance;
							out(c1, i, pos, distance, distances, raw2);
							found = true;
						}
					}
				}
			}
			if (!found) {
				notFound++;
				out(c1, i, -1, -1, distances, raw2);
			}
		}
		int unused = 0;
		for (boolean b : used)
			if (!b) unused++;
		System.out.println(unused + " letters from raw decryption have not been used.");
	}
	public static void out(char letter, int pos1, int pos2, int distance, int distances, StringBuffer sb) {
		System.out.println(letter + "	" + pos1 + "	" + pos2 + "	" + distance + "	" + distances + "	" + sb);
	}
	
	// for animation of decoding raw graysmith solution
	public static void makeHtml() {
		String keyStr = "#O%O&L(T)H*E+L-L..//1V2E3W4S5A6E7A8M9A:A;T<D>C@PAABLCSDTEEFPGHHHJSKKLEMINHOOPIRRSSTTUUVBWEXSYDZE^N_SbKcCdMfEjFkGlApEqTtEySzM|I";
		Map<Character, String> key = new HashMap<Character, String>();
		for (int i=0; i<keyStr.length(); i+=2) {
			Character c = keyStr.charAt(i);
			Character p = keyStr.charAt(i+1);
			String val = key.get(p);
			if (val == null) val = "";
			val += c;
			key.put(p, val);
		}
		String keyRGMap = "";
		String lf = System.getProperty("line.separator");
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i=0; i<alphabet.length(); i++) {
			char p = alphabet.charAt(i);
			char pl = Character.toLowerCase(p);
			System.out.println("<td id=\"p_" + pl + "\">");
			System.out.println("   <span id=\"p_" + pl + "_2\" class=\"p\">" + p + "</span></br>");
			String c = key.get(p);
			if (c != null) {
				for (int j=0; j<c.length(); j++) {
					char ct = c.charAt(j);
					System.out.println("   <span id=\"c_" + pl + "_" + (j+1) + "\" class=\"c\">" + ct +"</span><br>");
					
					keyRGMap += "keyMapRG[\"" + ct + "\"] = \"c_" + pl + "_" + (j+1) + "\";" + lf;
				}
				
			}
			System.out.println("</td>");
		}
		System.out.println(keyRGMap);
	}
	public static void main(String[] args) {
//		test(10000);
//		verify();
		makeHtml();
	}
}
