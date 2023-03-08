package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/**
 * Another way to measure jarl's unigram distance window: For a given symbol,
 * count how many different lines it appears on. Compare to shuffles.
 */
public class LinesPerSymbol {
	public static void shuffle(String cipher, int width, int shuffles) {
		Map<Character, StatsWrapper> stats = new HashMap<Character, StatsWrapper>();
		Map<Character, Set<Integer>> rows = symbolRows(cipher, width);
		for (Character c : rows.keySet()) {
			StatsWrapper s = new StatsWrapper();
			s.name = "" + c;
			s.actual = rows.get(c).size();
			stats.put(c, s);
		}
		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			rows = symbolRows(cipher, width);
			for (Character c : rows.keySet()) {
				StatsWrapper s = stats.get(c);
				s.addValue(rows.get(c).size());
			}
		}
		System.out.println(StatsWrapper.header());
		for (Character c : rows.keySet()) {
			StatsWrapper s = stats.get(c);
			s.output();
		}
	}

	/** generate list of unique rows each character appears on */
	public static Map<Character, Set<Integer>> symbolRows(String cipher, int width) {
		Map<Character, Set<Integer>> map = new HashMap<Character, Set<Integer>>();
		for (int i = 0; i < cipher.length(); i++) {
			int row = i / width;
			// System.out.println("pos " + i + " row " + row);
			char ch = cipher.charAt(i);
			Set<Integer> val = map.get(ch);
			if (val == null) {
				val = new HashSet<Integer>();
				map.put(ch, val);
			}
			val.add(row);
		}
		return map;
	}

	public static void main(String[] args) {
		// System.out.println(symbolRows(Ciphers.Z340, 17));
		// shuffle(Ciphers.Z340, 17, 1000000);
		// shuffle(Ciphers.Z408, 17, 1000000);
		/** Z340 by columns: */
		// shuffle(">|+R|ylz2U-d(#p_SBNHMF+ccBG6<+z<G589pypEDk)T.XF9cRlM2+RMp:+RHdW+31NSl/U+JK^+7cB>NWCLz*^yR5VbfqFz^M(pp<z1B:f#Jt++j%ltl+#lk7W6K45+|E^Z#;Oj8UO^StcC(92N*|JRO2-d*Z%VzBP<OC4|5D+2+U*|VGDPZ_O+pEb5TYOF_cd53WWkOYSF^>.F4BpBNXCFp(Y|8OHl.VcBMp7cYGkPO).1ABTWfUVc.b<yzVF++L<L|*/BMZ4(+TFA+.>&+#*TK-(|q5t;&MB6@z24RzKG;C))G-+8BKy4LLDkKHf2+cpL2++RFO-K9|(/2J)d",
		// 20, 1000000);
		/** Z408 by columns: */
		shuffle("VZ_HV%pN^zrPIZBcLR@kSMW9E@e!ZOek=UI59cPZdN+^(JV%XJdFeTX)Sp!MDpDK/tqL/Y+PrT#BG5qSrkJ8RORqP!GM9^e/9t#XYRWclAkR_V+p#YDZ#UGZWq69KUqEf95UTWjIBE9JBIY/I_ezEc_/U#9tYI=)@lKdPkFU685X_+F9eB8%r56WXOIrO76BqJPAT_#%6VLL\\+\\qq8)\\Rq9%EIODYd8%7WM)dtN!Eq6pATHkH+RdAYcZD\\lNeL(8HGqFUtPOMrX\\9q+fz+NV/)e5MBXH%N@R)BQ7%_@AGVAE@lELUT8VfQK==PFL#^9P%T)KX^UM^Q5WRY!pUQ%!LSA#%tZHJRHrRSzelDqXIWG=tq9BIO(=Q6k9R#S8q5Y=k6cq_WBVMPPGAHF#kB(YE)eB",
				24, 1000000);
	}
}
