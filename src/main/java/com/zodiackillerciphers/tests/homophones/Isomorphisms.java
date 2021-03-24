package com.zodiackillerciphers.tests.homophones;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.MapUtil;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Isomorphisms {
	public static String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/** for each possible selection of k symbols from the cipher alphabet, remove any symbol that isn't one of the k symbols.
	 * convert the string to an isomorphic sequence.
	 * for example, XYZXYZ becomes ABCABC.  TUVTUV also becomes ABCABC.  Thus, certain types of cycling will be classified together.
	 * 
	 *  then, track counts of all substrings of every isomorphic string.
	 *  
	 *  these counts will be compared to shuffles to find anomalous sequences, which we hope will be the homophone cycles.
	 *  
	 *  "search" argument is an optional isomorphism to print whenever it is encountered.
	 */
	public static Map<String, Long> countSequences(String cipher, int k, String search, int minLength, int maxLength) {
		Map<String, Long> result = new HashMap<String, Long>();
		String alphabet = Ciphers.alphabet(cipher);
		int[] selections = new int[k];
		for (int i=0; i<k; i++) selections[i] = i;
		countSequences(cipher, result, alphabet, selections, 0, search, minLength, maxLength);
		return result;
	}

	public static void countSequences(String cipher, Map<String, Long> result, String alphabet, int[] selections, int which, String search, int minLength, int maxLength) {
		
		//System.out.println("SMEG " + Arrays.toString(selections) + " " + which);
		
		if (which >= selections.length) {
			Set<Character> symbols = new HashSet<Character>();
			for (int selection : selections) {
				symbols.add(alphabet.charAt(selection));
			}
			String seq = sequence(cipher, symbols);
			for (int a=0; a<seq.length()-1; a++) {
				for (int b=a+1; b<seq.length(); b++) {
					String sub = seq.substring(a,  b);
					if (sub.length() < minLength || sub.length() > maxLength) continue;
					String iso = isomorphism(sub);
					if (search != null && iso.contains(search)) {
						boolean is408 = Ciphers.Z408.equals(cipher);
						String hom = "";
						if (is408) {
							hom = Ciphers.isHomophone(sub) ? "TRUE CYCLE" : "FALSE CYCLE";
						}
						System.out.println(symbols + "	" + sub + "	" + iso + "	" + hom);
					}
					Long val = result.get(iso);
					if (val == null) val = 0l;
					val++;
					result.put(iso,  val);
				}
			}
			
			return;
		}

		int start = 0;
		if (which > 0) start = selections[which-1]+1;
		int end = alphabet.length() - (selections.length-which);
		for (int i=start; i<=end; i++) {
			selections[which] = i;
			countSequences(cipher, result, alphabet, selections, which+1, search, minLength, maxLength);
		}
	}
	
	public static String sequence(String cipher, Set<Character> alphabet) {
		String result = "";
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			if (alphabet.contains(ch)) result += ch;
		}
		return result;
	}
	
	public static String isomorphism(String str) {
		String result = "";
		Map<Character, Character> map = new HashMap<Character, Character>(); 
		int current = 0;
		for (int i=0; i<str.length(); i++) {
			Character key = str.charAt(i);
			Character val = map.get(key);
			if (val == null) {
				val = alpha.charAt(current++);
			}
			map.put(key, val);
			result += val;
		}
		return result;
	}
	
	public static void shuffleCounts(String cipher, int k, int shuffles, int minLength, int maxLength) {
		Map<String, Long> actual = countSequences(cipher, k, null, minLength, maxLength);
		Map<String, StatsWrapper> stats = new HashMap<String, StatsWrapper>();
		for (String key : actual.keySet()) {
			StatsWrapper stat =new StatsWrapper();
			stat.name = "Count for " + key + "	" + key.length();
			stat.actual = actual.get(key);
			stats.put(key, stat);
		}
		String shuffle = cipher;
		for (int i=0; i<shuffles; i++) {
			shuffle = CipherTransformations.shuffle(shuffle);
			Map<String, Long> result = countSequences(shuffle, k, null, minLength, maxLength);
			for (String key : stats.keySet()) {
				Long val = result.get(key);
				if (val == null) val = 0l;
				stats.get(key).addValue(val);
			}
			if (i>0 && i%100 == 0) {
				System.out.println("=== AFTER " + i + " SHUFFLES: === ");
				for (String key : stats.keySet()) stats.get(key).output();
			}

		}
		System.out.println("=== AFTER " + shuffles + " SHUFFLES: === ");
		for (String key : stats.keySet()) stats.get(key).output();
	}
	
	
	public static void testCount(int minLength, int maxLength) {
		Date d1 = new Date();
		Map<String, Long> result = MapUtil.sortByValue(countSequences(Ciphers.Z408, 3, null, minLength, maxLength), true);
		for (String key : result.keySet()) {
			System.out.println(result.get(key) + "	" + key);
		}
		System.out.println("Size: " + result.size());
		Date d2 = new Date();
		System.out.println(d2.getTime() - d1.getTime());
		
	}
	
	public static void testIso() {
		System.out.println(isomorphism("XYZXYZTUUUVVUT"));
	}
	
	/** process shuffle result.  compare given cipher against result. */
	public static void process(String file, String cipher, int k) {
		List<String> lines = FileUtil.loadFrom(file);

		Map<String, double[]> map = new HashMap<String, double[]>(); 
		double sum = 0;
		long total = 0;
		for (String line : lines) {
			total++;
			String[] split = line.split("	");
			String key = split[0].split(" ")[2];
			double[] val = new double[] { Double.valueOf(split[4]), Double.valueOf(split[6]) };
			sum += Double.valueOf(split[8]);
			map.put(key, val);
		}
		System.out.println("Mean sigma: " + sum/total);
		
		Map<String, Long> counts = countSequences(cipher, 3, null, 6, 15);
		sum = 0;
		total = 0;
		for (String key : counts.keySet()) {
			double val = counts.get(key);
			double[] val2 = map.get(key);
			if (val2 == null) continue;
			double sigma = (val-val2[0])/val2[1];
			sum += sigma;
			total++;
			System.out.println(key + "	" + sigma);
		}
		System.out.println("Mean sigma: " + sum/total);
	}
	
	public static void main(String[] args) {
		//testCount(6, 15);
		//shuffleCounts("+E7'D*!$3*.)HSF1$M&^*%Y6[ZU=($VQ,*$R+>)*'/KG$(*8V$BITC#X3>E14#]+H\\LX-3*=$)*@II^1HQ$[>!YP^<R\\U:T,<?$IO;#QK*5A\"$9I0&CE*^.+0D]M/JV$!K'*QR46)-4Z\"MI$8H1?9'?E&7^A?(!-B)\\V?Z+55A*Q_M9KX+G_/$2-4%E]R!;SFL)_<*8\"1I3&#N=#HL';Y$O0Z*F@[S!X.V\"':$*-^V\\:DK3UF,*E!8AB\"TQ$7K>[4=*[\"^\\H#$XME*T3C49+[]PG>-;%5#X*\\&N)T/RE1@?N3>2S\\IQ#]P$P'[JU*;O*:1@@K$*#$U:?RW2,Y*9E", 2, 1000, 7, 7);
		//String zJarlve = "+E7'D*!$3*.)HSF1$M&^*%Y6[ZU=($VQ,*$R+>)*'/KG$(*8V$BITC#X3>E14#]+H\\LX-3*=$)*@II^1HQ$[>!YP^<R\\U:T,<?$IO;#QK*5A\"$9I0&CE*^.+0D]M/JV$!K'*QR46)-4Z\"MI$8H1?9'?E&7^A?(!-B)\\V?Z+55A*Q_M9KX+G_/$2-4%E]R!;SFL)_<*8\"1I3&#N=#HL';Y$O0Z*F@[S!X.V\"':$*-^V\\:DK3UF,*E!8AB\"TQ$7K>[4=*[\"^\\H#$XME*T3C49+[]PG>-;%5#X*\\&N)T/RE1@?N3>2S\\IQ#]P$P'[JU*;O*:1@@K$*#$U:?RW2,Y*9E";
		shuffleCounts("HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKOBy:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()pp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|", 3, 1000, 7, 7);
//		countSequences(Ciphers.Z340, 3, "ABCABAB", 7, 15);
//		process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tests/homophones/isomorphisms/bb",
//				"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKOBy:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()pp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|",
//				3);
		//		countSequences(Ciphers.Z408, 2, "ABABABABABABABABABAA");
//		countSequences(Ciphers.Z408, 2, "ABBABBBBABBAAAABBBBBBB");
//		countSequences(Ciphers.Z408, 2, "ABAABAAAABAABBBBAAAAAAA");
//		countSequences(Ciphers.Z408, 2, "ABBBABBBBABBAAABBBBAB");
//		countSequences(Ciphers.Z408, 2, "ABABABABABABABABAAAA");
//		countSequences(Ciphers.Z408, 2, "ABAABAAAABAABBBBAAAAAA");
//		countSequences(Ciphers.Z408, 2, "ABABABABABABABABAAA");
//		countSequences(Ciphers.Z408, 2, "ABBABBBBABBAAAABBBBBB");
//		countSequences(Ciphers.Z408, 2, "ABABBBABABAABAAAAABBB");
//		countSequences(Ciphers.Z408, 2, "AABBBABBABAABAAAAABAB");
//		countSequences(Ciphers.Z408, 2, "ABBBABBBBABBAAABBBBABBB");
//		countSequences(Ciphers.Z408, 2, "ABBBABBBBABBAAABBBBABB");
//		countSequences(Ciphers.Z408, 2, "AABBABAAABABBBABABBA");
//		countSequences(Ciphers.Z408, 2, "ABABABBABBBABBBAAABAB");
//		countSequences(Ciphers.Z408, 2, "ABAAABABABBABBBBBAAAB");
//		countSequences(Ciphers.Z408, 2, "AABAAAABBAAABBBAABAABAB");
//		countSequences(Ciphers.Z408, 2, "ABBAAABAABAAABABBBABA");
//		countSequences(Ciphers.Z408, 2, "ABBABBBABBABBAAAAABAA");
//		countSequences(Ciphers.Z408, 2, "ABABABABABABABABABBBBA");
//		countSequences(Ciphers.Z408, 2, "ABABABABABABABABABBBB");
//		countSequences(Ciphers.Z408, 2, "ABABABABABABABABAAAAB");
//		countSequences(Ciphers.Z408, 2, "ABAAABAAAABBBBAABAAABA");
//		countSequences(Ciphers.Z408, 2, "AAABABAAABBAAAABBABAB");
//		countSequences(Ciphers.Z408, 2, "ABABBABBBBABBAAAABBBBBBB");
//		countSequences(Ciphers.Z408, 2, "ABABBABBBBABBAAAABBBBBB");
//		countSequences(Ciphers.Z408, 2, "ABABBABBBBABAAAABBAAB");
//		countSequences(Ciphers.Z408, 2, "AABABBAAABABBBABAAABA");
//		countSequences(Ciphers.Z408, 2, "AABAAABAAAABBAABAAABA");
//		countSequences(Ciphers.Z408, 2, "ABABBBBBABBAAAABBABBBBB");
//		countSequences(Ciphers.Z408, 2, "ABAAAAABAABBBBAABAAAAA");
//		countSequences(Ciphers.Z408, 2, "AAABAABAABAABBBAAAABAA");
//		countSequences(Ciphers.Z408, 2, "ABABABBBBBABBAAAABBABBBB");
//		countSequences(Ciphers.Z408, 2, "ABABABBBBBABBAAAABBABBB");
//		countSequences(Ciphers.Z408, 2, "ABABAAAAABAABBBBAABAAAA");
//		countSequences(Ciphers.Z408, 2, "ABABAAAAABAABBBBAABAAA");
//		countSequences(Ciphers.Z408, 2, "ABBABBBBBAAAAABBAABBA");
//		countSequences(Ciphers.Z408, 2, "AAABAABBAAAABBBAABAAAAB");
//		countSequences(Ciphers.Z408, 2, "ABBBABABAABAAAAABBBAA");
//		countSequences(Ciphers.Z408, 2, "AABBABBBBAAABBBAAABBA");
//		countSequences(Ciphers.Z408, 2, "AABAAABAABAABBBBBABBA");
//		countSequences(Ciphers.Z408, 2, "AABAABABABABABABABABB");
//		countSequences(Ciphers.Z408, 2, "AAAAABABAAABBAAAABBABA");
//		countSequences(Ciphers.Z408, 2, "ABBBABABBBABAAAABBBBBB");
//		countSequences(Ciphers.Z408, 2, "ABAAABABAAABABBBBAAAAAA");
//		countSequences(Ciphers.Z408, 2, "ABBBBBBBAAAAABBABBABAB");
//		countSequences(Ciphers.Z408, 2, "ABABABAABABAABABAABAABB");
//		countSequences(Ciphers.Z408, 2, "ABABABAABABAABABAABAAB");
//		countSequences(Ciphers.Z408, 2, "ABBBBABBBAAAAABBBBBABA");
//		countSequences(Ciphers.Z408, 2, "AABABBABAAABABBAAABABB");
//		countSequences(Ciphers.Z408, 2, "AABABBABAAABABBAAABAB");
		//countSequences(Ciphers.Z340, 2, "ABABAAAAAAAAAAAAAAAAAAABAABB");
		
		//testIso();
	}
}
