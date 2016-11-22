package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.Vectors;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.MeasurementsBean;
import com.zodiackillerciphers.transform.Operations;

/** rewrite cipher text using different periods.  jarlve observed that "period 19" ngrams
 * have the highest counts (that is, symbols with 19 positions between them).
 */
public class Periods {

	/** generate count by ngram of the given cipher, where each ngram has the given period */
	public static PeriodBean applyPeriodOBSOLETE(String cipher, int n, int period, boolean countOnly) {
		// made it obsolete, because i think the rewrite method is more consistent with what jarlve and smokie are doing 
		PeriodBean bean = new PeriodBean();
		bean.period = period;
		bean.n = n;
		bean.rewritten = "";
		for (int i=0; i<cipher.length(); i++) {
			String key = "";
			Set<Integer> seen = null;
			if (!countOnly) seen = new HashSet<Integer>(); // track positions to avoid duplicate locations within an ngram
			for (int j=0; j<n; j++) {
				if (j == 0) { 
					if (!countOnly && seen.contains(i)) break;
					key += cipher.charAt(i);
					if (!countOnly) seen.add(i);
				}
				else {
					int pos = (i+j*(period+1)) % cipher.length();
					if (!countOnly && seen.contains(pos)) break;
					key += cipher.charAt(pos);
					if (!countOnly) seen.add(pos);
				}
			}
			if (key.length() == n) {
				Integer val = bean.counts.get(key);
				if (val == null) val = 0;
				val++;
				bean.counts.put(key, val);
				
				if (val > 1) {
					// track positions involved with repeats
					if (!countOnly) {
						bean.positions.add(i);
						for (int j=1; j<n; j++) bean.positions.add((i+j*(period+1)) % cipher.length());
					}
				}
			}
		}
		return bean;
	}
	
	public static double score(Map<Character, Integer> counts, int reps, String ngram) {
		double prob = 1;
		for (int i=0; i<ngram.length(); i++) {
			char key = ngram.charAt(i);
			prob *= ((double)counts.get(key))/340;
		}
		prob = Math.pow(prob, reps-1);
		System.out.println(prob + " " + ngram + " " + reps);
		return prob;
	}
	
	public static void testPeaks() {
		int sum = 0;
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		
		for (int i=0; i<1000000; i++) {
			String cipher = CipherTransformations.shuffle(Ciphers.cipher[0].cipher);
			int n = 2;
			int max = 0;
			for (int p=1; p<=170; p++) {
				String rewritten = rewrite3(cipher, p);
				NGramsBean bean = new NGramsBean(n, rewritten);
				//System.out.println(bean.numRepeats() + "	" + i + "	" + bean.repeatsInfo());
				max = Math.max(max, bean.numRepeats());
			}

			Integer val = counts.get(max);
			if (val == null) val = 0;
			val++;
			counts.put(max, val);
			sum += max;
			if (i % 1000 == 0) {
				System.out.println(new Date() + ": " + i+"...");
				System.out.println(counts);
				//System.out.println(sum/(i+1));
			}
			
		}
		
		System.out.println(sum/1000000);

		System.out.println("Final counts:");
		System.out.println(counts);
		
	}
	
	/** Measure fragFaster for all periods of Z340  
	 * 
	 * http://www.zodiackillersite.com/viewtopic.php?p=44818#p44818 */
	public static void testForJarlve() {
		String cipher = Ciphers.cipher[1].cipher.substring(0,340);
		for (int i=1; i<=cipher.length()/2; i++) {
			String rewritten = rewrite3(cipher, i);
			float frag = RepeatingFragmentsFaster.measure(rewritten);
			System.out.println(i + "	" + rewritten + "	" + frag);
		}
	}
	
	public static void test(String cipher, boolean countOnly) {
		//String cipher = Ciphers.cipher[0].cipher;
		//String cipher = "HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+";
		//String cipher = "d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>"; // z340 flipped horizontally
		//Operations o = new Operations();
		int n = 2;
		for (int i=1; i<=cipher.length()/2; i++) {
			String rewritten = rewrite3(cipher, i);
			//PeriodBean bean = Periods.applyPeriod(cipher, n, i, countOnly);
			//System.out.println(bean.toString(true));
			NGramsBean bean = new NGramsBean(n, rewritten);
			
			String line = i + "	" + bean.numRepeats() + "	" + bean.count() + "	";
			//line += RepeatingFragmentsFaster.measure(rewritten, false, false) + "	" + RepeatingFragmentsFaster.measure(rewritten, true, false) + "	";
			line += "=(\"" + bean.repeatsInfo() + "\")	=(\"" + rewritten + "\")";
			System.out.println(line);
			//System.out.println(bean.numRepeats() + "	" + i + "	" + bean.repeatsInfo());
			//System.out.println(RepeatingFragmentsFaster.measure(rewritten, true, false) + " frag");
			/*if (bean.numRepeats() > 0) {
				System.out.println(Arrays.toString(o.measure(rewritten)) + " " + rewritten);
			}*/
		}
		Map<Character, Integer> counts = Ciphers.countMap(Ciphers.cipher[0].cipher);
		
		/*
		score(counts, 4, "p+");
		score(counts, 3, "G+");
		score(counts, 3, "<S");
		score(counts, 3, "+4");
		score(counts, 3, "(+");
		score(counts, 3, "#2");
		score(counts, 2, "|T");
		score(counts, 2, "|<");
		score(counts, 2, "z6");
		score(counts, 2, "k.");
		score(counts, 2, "^D");
		score(counts, 2, "YA");
		score(counts, 2, "Xz");
		score(counts, 2, "TB");
		score(counts, 2, "PY");
		score(counts, 2, "OF");
		score(counts, 2, "N:");
		score(counts, 2, "MF");
		score(counts, 2, "D(");
		score(counts, 2, "BO");
		score(counts, 2, ";+");
		score(counts, 2, "9^");
		score(counts, 2, ".L");
		score(counts, 2, "-R");
		score(counts, 2, "+|");
		score(counts, 2, "+l");
		score(counts, 2, "+k");
		score(counts, 2, "+c");
		score(counts, 2, "+B");
		score(counts, 2, "*5");*/
	}
	
	/** untranspose, using modulo 340 method */
	public static String rewrite2(String cipher, int n) {
		//String cipher = Ciphers.cipher[0].cipher;
		
		int length = cipher.length();
		Set<Integer> seen = new HashSet<Integer>();
		String result = "";
		
		int pos = 0;
		int count = 0;
		while (true) {
			if (count == length) break;
			pos %= 340;
			if (seen.contains(pos)) {
				pos++;
				continue;
			}
			result += cipher.charAt(pos);
			seen.add(pos);
			pos += n;
			count++;
		}
		
		return result;
		
		
	}
	/** untranspose, using a "grid movement" method */
	public static void rewrite(String cipher) {
		//String cipher = Ciphers.cipher[0].cipher;
		String[] grid = Ciphers.grid(cipher, 17);
		
		boolean[][] visited = new boolean[20][17];
		for (int row=0; row<20; row++) { 
			visited[row] = new boolean[17];
			for (int col=0; col<17; col++) {
				visited[row][col] = false;
			}
		}
		
		int row = 0;
		int col = 0;
		String result = "";
		while (true) {
			if (row < 0) row += 20;
			if (col < 0) col += 17;
			row = row % 20;
			col = col % 17;
			if (visited[row][col]) break;
			result += grid[row].charAt(col);
			visited[row][col] = true;
			row++; col-=3; // period 14
		}
		
		System.out.println(result);
		System.out.println(result.length());
		
		
	}
	
	public static boolean showMapping(int pos) {
		
		// pivots in horizontally flipped z340
		int[] pivots = new int[] {
				174,
				191,
				208,
				225,
				226,
				227,
				228,
				145,
				162,
				179,
				196,
				197,
				198,
				199				
		};
		for (int pivot : pivots) if (pivot == pos) return true;
		return false;
	}
	
	/** untranspose, using a non-wrapping method. */
	public static String rewrite3(String cipher, int n) {
		String result = "";
		for (int i=0; i<n; i++) {
			for (int j=i; j<cipher.length(); j+=n) {
				result += cipher.charAt(j);
				//if (showMapping(j))
				//	System.out.println("from " + j + " to " + (result.length()-1));
			}
		}
		return result;
	}
	
	/** return map of new positions to old positions */
	public static Map<Integer, Integer> rewrite3Map(String cipher, int n) {
		String result = "";
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int count = 0;
		for (int i=0; i<n; i++) {
			for (int j=i; j<cipher.length(); j+=n) {
				//result += cipher.charAt(j);
				map.put(count++, j);
				//if (showMapping(j))
				//	System.out.println("from " + j + " to " + (result.length()-1));
			}
		}
		return map;
	}
	
	/** return set of positions covered by all repeating bigrams found in the untransposition of the given period */
	public static Set<Integer> positions(String cipher, int period) {
		String result = "";
		Set<Integer> set = new HashSet<Integer>();
		/** map new (untransposed) position back to original position */
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		int positionNew = 0;
		for (int i=0; i<period; i++) {
			for (int j=i; j<cipher.length(); j+=period) {
				result += cipher.charAt(j);
				map.put(positionNew++, j);
			}
		}
		
		NGramsBean bean = new NGramsBean(2, result);
		for (String ngram : bean.repeats) {
			for (int pos : bean.positions.get(ngram)) {
				for (int i=0; i<ngram.length(); i++) {
					set.add(map.get(pos+i));
				}
			}
		}
		
		//System.out.println(map);
		return set;
	}
	
	/** return set of positions covered by all repeating bigrams found in the untransposition of the given period,
	 * in the form of highlight function calls for the cipher-highlighter */
	public static String positionsJS(String cipher, int period) {
		String result = "";
		/** map new (untransposed) position back to original position */
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		int positionNew = 0;
		for (int i=0; i<period; i++) {
			for (int j=i; j<cipher.length(); j+=period) {
				result += cipher.charAt(j);
				map.put(positionNew++, j);
			}
		}
		
		String js = "hsl_random([";
		NGramsBean bean = new NGramsBean(2, result);
		for (String ngram : bean.repeats) {
			js += "[";
			String csv = "";
			for (int pos : bean.positions.get(ngram)) {
				for (int i=0; i<ngram.length(); i++) {
					if (!csv.isEmpty()) csv += ",";
					csv += map.get(pos+i);
				}
			}
			js += csv + "],";
		}
		js += "])";
		//System.out.println(map);
		return js;
	}
	
	
	/** return rewritten cipher and map to the corresponding positions in the original cipher */
	public static PeriodMap rewrite4(String cipher, int n) {
		PeriodMap pm = new PeriodMap();
		String result = "";
		int k=0;
		for (int i=0; i<n; i++) {
			for (int j=i; j<cipher.length(); j+=n) {
				result += cipher.charAt(j);
				pm.map.put(k++, j);
				//System.out.println((k++) + " " + j);
			}
		}
		pm.rewritten = result;
		return pm;
	}
	
	public static void visualize() {

		String cipher = Ciphers.cipher[0].cipher;
		PeriodMap pm = rewrite4(cipher, 18);
		String re = pm.rewritten;
		
		System.out.println(re);
		System.out.println(pm.map);
		int n = 2;
		NGramsBean bean = new NGramsBean(n, re);
		//bean.dump();
		for (String key : bean.positions.keySet()) {
			if (bean.counts.get(key) > 1) {
				String darken = "";
				String lighten = "";
				for (Integer pos : bean.positions.get(key)) {
					for (int i=pos; i<pos+n; i++) {
						int pos2 = pm.map.get(i);
						int r = pos2/17;
						int c = pos2 % 17;
						darken += "darkenrc(" + r + "," + c + "); ";
						lighten += "lightenrc(" + r + "," + c + "); ";
					}
				}
				System.out.println(bean.counts.get(key) + " <button onmouseover=\"" + darken + "\" onmouseout=\"" + lighten + "\">" + key + "</button>");
			}
		}
	}
	
	/** determine peak bigrams and its period.
	 * returned array: {period, number of bigram repeats}
	 **/  
	public static int[] peakPeriodicBigrams(String cipher, boolean showSteps) {
		int[] best = new int[2];
		best[0] = 1; // 
		for (int period = 1; period < cipher.length()/2; period++) {
			String re = rewrite3(cipher, period);
			NGramsBean bean = new NGramsBean(2, re);
			int reps = bean.numRepeats();
			if (reps > best[1]) {
				best[0] = period;
				best[1] = reps;
			}
			if (showSteps) {
				System.out.println(period + " " + reps + " " + re);
			}
		}
		return best;
	}
	
	/** return a list showing distribution of repeating bigram counts across
	 * all periods.  list is sorted in descending order.  
	 **/  
	public static List<Integer> periodicBigramDistribution(String cipher) {
		List<Integer> list = new ArrayList<Integer>();
		for (int period = 1; period < cipher.length()/2; period++) {
			String re = rewrite3(cipher, period);
			NGramsBean bean = new NGramsBean(2, re);
			list.add(bean.numRepeats());
		}
		Collections.sort(list);
		Collections.reverse(list);
		return list;
	}
	
	/** measure distance between this cipher's periodic bigram distribution and that of the Z340 */ 
	public static double periodicBigramDistributionDistance(String cipher) {
		List<Integer> list1 = periodicBigramDistribution(cipher);
		List<Integer> list2 = MeasurementsBean.referenceBean.periodicBigramDistribution;
		return Vectors.distance(list1, list2, true);
	}
	
	
	public static void main(String[] args) {
		//testScytale();
		//String cipher = Ciphers.cipher[0].cipher;
		
		/*String[] ciphers = new String[] {
				Ciphers.cipher[0].cipher,
				"16OjYU_ykp+BB+^WU(KX<Mbpc^4|5FBcfjkkqlHpbY5p^1GG(dHERJ|*8YzZ)d;7Clj9@WqB|z+WUB;-2U<J/T25+J8D(+cc31l*F2T9W^RLN<pYDN)p5M6zklF%+b.cV2lX|OWB4<F3+lkBFcFP-*8yp-f+(WMDJ_.U1V((T-42>SkFB;qbN+%+P25&NZ^+^/FBctO4#++EJD2A/+&LO*cGC7+Z+CC...py3#pD+j_DZBF2S:DzM<p8|5FUlS_LtVT)pGzKM<*-12MALc1bDd(|+B1|pqYNz+6&jKDPA|Vyf++|SV#)5V2z+1K)VTft1LG+D<:#G>1f464*%+Tz",
				"&7t/Vc.bP4()TOM+p/^#Cc(yOBKf#8d5-G>|5F.kO8_F&9KzctGzO-6bk+fX;cZ+^N5k2+MzUX2+d|71y;d44.|5FBcj)+21y/2+9y(KPHER+*8++WB9MV&:t+UNFdTllt^.%&NqyO7zZ3D|^C;PF#+VEAK:EGGEEppR+FC9YK-5*<Fcc3+5C|*-Ek-AU+lO+U<zS-*|H&Oc)qMT^L*S+LMCD*|JR(l|_4Z4DY+JR_|52).)z2zZ&Rj&VNLJ#BS@W+@*+5cFBcR+RB>WBUC5&G*TbPBbUJFBNF+SB2G(dO>5(#3TK8DlB>&*M+V<W&B#kz64f|<G.@+8(lL<&O1l",
				"%7zJL.b|5FBcfW&7Dp62+)p.cMzz+)52WkJ^q-Bpcyz1pycOTD_+Yqb.cVSZLZF4|+%f4UOBz7PY&XR249%#9^3%E#&%_TyHt|2Ok)8+A+pCz|5Fp*>j2&+c8zT^6FBc2+pO7|FB7O+RKO8LULtMTdy+3RPO+dKRFP<+.;8TkM-pkyG@DL/l8B5:4<-FK(lzZ#d;9C8&V:pCN.lR>cySD6+1+7U4+tB<JkpZ5<NjTG+XdN2+G|B)MYRl(WUUfHERJ|*V<(pd1F+524+|+._/c<W(F<+c1PN-*P>O&O+>Cz5l3p)YGKkB/VJWA*-+CNF+P5B.BS|LWj|MO>-j2yLf",
				"#)k>-O|Mb^M)b)cdAqKUWb)*-)+<HTB)j3):Kf.L^3SyO)2pb6)YE(pcOALS+)zR4zV;^b.cVkq)Uy%T9CN+CyC^ZS#7yqVq#GDGjDZDjjHG+G*DN<BK&9dlR%zY;pb)>S1qMpF(Nc&pL|qFkzT)KlcXc)<|5F|8N_9WU^8^T:-C<E#HER66M%FUB2-B8%@1*7J_#/pUBz*6O)-lNq2|lt+7(4q9^FBckRJ|*OBYfL%)-7kt^/-*W;p)()l6pTMJB2^/|l)VRc6()fS4l9|5FBcO%y3Wz^)R|bKd9Y7zZ#d8|c/pz)lcPTLpFlWfLBFKNqKSLBRP1)tFBf)dqXtP",
				"#&c*_Zb.cVPRJ|*9l&Xt6)ES7Kc&3Ly|F&Dq%U&TbXU.j_1&J#B5<&T<plcpbp6HERM%G2DLRYEGjFBcdl413RjVUWMNzX)-*|5FPt^^NzB>ztckdFY)j+cVyRX&(|6V&<BOUFyz#CT&CU8R@X6|c/#&f-N6&HqN:&J>b:8*--RDYplqHZ<|&6G<|/Fd6UB3F1zX42B|5FBcRf9kY|-&(J2M8FNR;&lW@-*&4B/+5&(fZ/4q6)CNBZ.22Sq<..DDE2yqD7ySE(&8W9YKfWfzb/:&6WWzZ3DA/c|M;Fp8zLz6#b&3WB33GA2lL3&6#9&3tXPp7B4(N)3:)^X6&kpk",
				"#l1>_UC*ttFlyR_VJp2+z6|K:zZ%7|8kZKjf+zqPddk77^+kdXl2+L%1B|5FBc<pCA)c45M*+T*-BpPR+5+6+P^Y<FzWO-*BfU5.XEFBcyU;fO/|#9^lN+zK5K_+O>MN6-V^4C+l2%Ay#)(p4SU@py+b/-t(/L++W.pyCP<pL-B+*4%R|5FRzDK/&G3D+l-TMc&BAORz|b.N63+9_fVXb.cVyYl>^4;RJ|*3Nc|.c^)+dc_t9M+|+DKc6.+29pWzNOO/J%;WHpb|Hc%6LpOTLRDM1dRlRB+<PFBDpZUO8pqF2SMF)+Gz2TMA6jFL>WKF)OcHEROY%<:d12S#S+C/",
				"|#P-z4_Xl>(HjlDXk+lj;ScLMGM:z;MVTFX2CRqMEEjEd9EM7.%)&X(kWWK8@V8M&|6GH^Syq-76./t1T-*bFU(|5FBc12fG3l7p2;Oc|5F%;kt2&(%#z2bV^M;t)WXUKGYD#A<|M)L8MOMl)MHz^STql7c./b.cV6tF;M<5GX-jM+6FBcyzpN42#&-*-;&.qy.Mz;CY+3YX|A(4C|Lb;8*LMU(z1f5RJ|*^cfqtXMBt<|5Y(N7|1V*WP/St3)MBzMXDJX;MFM:L#HERTN^P(RFz*>W(#MbWt4ZpJBDH)MMcG%2jAl;86|FG4dA.D+yBH5fcJEMFzZ_9|7cB5B^t",
				"zFT-+p)OZk*+MNCLtV-X_W+<<yNBR)p+fMp-*+3cGKkcM/.9(BCWA|2:jl6LU+.TB>WR7L+FBzZOD8|K_FBc9l#)H^zUG&PS1zc5RdfH^*G>c91OWNJ;;#MG3DdB+z77O4ct^K5N1P<tM5l2+OHp+(F>+^_)/lb.cV+(pSj74F-V<BzGOBcOU4p|OF-zp<c;q6%.q2L4(#B485+OR+YGb|5FCL#6+lz5dPYyR^HERSp+q)Y2zpDTCKG8K%VE%+V%J+EFC|#BDYRJ|*MUF*|Xt|^L(:/l+A|9yUlSTG*-&KyT+k>J4k2+p.2WHO<|5FBcR<K#ydf++@+(YMBWpGfd"
		};
		for (String cipher : ciphers) {
			String re = rewrite3(cipher, 19);
			NGramsBean ng = new NGramsBean(2, re);
			String line = ng.count() + "	" + ng.numRepeats(); 
			//System.out.println("period19 " + ng.count() + " " + ng.numRepeats());
			ng = new NGramsBean(2, cipher);
			//System.out.println("period1 " + ng.count() + " " + ng.numRepeats());
			line = ng.count() + "	" + ng.numRepeats() + "	" + line;
			System.out.println(line);
			
			System.out.println(cipher);
			int[] num = Ciphers.toNumeric(cipher, false);
			String n = "";
			for (int i : num) n += (i<10 ? "0"+i : i) + " ";
			System.out.println(n);
		}*/
		//System.out.println(positions(Ciphers.cipher[0].cipher, 39));
		
		//String cipher = "C;XO^z-7Y(SG5DYk>AUFcK2ypW<TXf847yWc<p>2pEVXT9#6LjW9OpHER;b.2-*Xc)l%FTB<:O3.dcK|5FBcMpK/fU)1c>X(zLcT/X28pOb.cVO;X5N/2L_2JfY/<XXk1MX9T&t|TBO>P9#dFNzd4zK<)-EX+ZBpB|C6kWKOW-YRL#@7G*^UXRcBXO.MSXz7)f:@jyPz>%lfXNOX3G2&L>X2|X+|q3()zk_NRO8t|5FC*-NJ6qU4BJz)W1lf<X8|Hp&//L|*^dzX3VX)XPAyRJ|*(dzZ_77|kqXO4MCY+/jM&5G2GKGFPB9l5(-O#FXWl#FWpU:FBcdTBBdKq*2+";
		/*String cipher = Ciphers.cipher[0].cipher;
		String flip = "";
		for (int row=0; row<20; row++) {
			for (int col=16; col>=0; col--) {
				flip += cipher.charAt(row*17+col);
			}
		}
		System.out.println(new NGramsBean(2, rewrite3(cipher, 19)).numRepeats());
		System.out.println(new NGramsBean(2, rewrite3(cipher, 19)).count());
		System.out.println(new NGramsBean(2, rewrite3(flip, 15)).numRepeats());
		System.out.println(new NGramsBean(2, rewrite3(flip, 15)).count());
		
		System.out.println(rewrite3(cipher, 19));
		System.out.println(rewrite3(flip, 15));
		*/
		//cipher = " HER>pl^VPk|1LTG2 Np+B(#O%DWY.<*Kf By:cM+UZGW()L#zH Spp7^l8*V3pO++RK _9M+ztjd|5FP+&4k p8R^FlO-*dCkF>2D #5+Kq%;2UcXGV.zL (G2Jfj#O+_NYz+@L d<M+b+ZR2FBcyA64 -zlUV+^J+Op7<FBy U+R/5tE|DYBpbTMK 2<clRJ|*5T4M.+&B z69Sy#+N|5FBc(;8 lGFN^f524b.cV4t+ yBX1*:49CE>VUZ5- |c.3zBK(Op^.fMqG RcT+L16C<+FlWB|) ++)WCzWcPOSHT/() |FkdW<7tB_YOB*-C >MDHNpkSzZO8A|K;";
		//String cipher = "THECONFESSIONBYSHEWASYOUNGANDBEAUTIFULBUTNOWSHEISBATTEREDANDDEADSHEISNOTTHEFIRSTANDSHEWILLNOTBETHELASTILAYAWAKENIGHTSTHINKINGABOUTMYNEXTVICTIMMAYBESHEWILLBETHEBEAUTIFULBLONDTHATBABYSITSNEARTHELITTLESTOREANDWALKSDOWNTHEDARKALLEYEACHEVENINGABOUTSEVENORMAYBESHEWILLBETHESHAPELYBRUNETTTHATSAIDNOWHENIASKEDHERFORADATEINHIGHSCHOOLBUTMAYBEITWILLNO";
		//test(cipher, false);
		//System.out.println(rewrite3(cipher, 15));
		//test ("HER>pl^VPk|1LTG2d Np+B(#O%DWY.<*Kf) By:cM+UZGW()L#zHJ Spp7^l8*V3pO++RK2 _9M+ztjd|5FP+&4k/ p8R^FlO-*dCkF>2D( #5+Kq%;2UcXGV.zL| (G2Jfj#O+_NYz+@L9 d<M+b+ZR2FBcyA64K -zlUV+^J+Op7<FBy- U+R/5tE|DYBpbTMKO 2<clRJ|*5T4M.+&BF z69Sy#+N|5FBc(;8R lGFN^f524b.cV4t++ yBX1*:49CE>VUZ5-+ |c.3zBK(Op^.fMqG2 RcT+L16C<+FlWB|)L ++)WCzWcPOSHT/()p |FkdW<7tB_YOB*-Cc >MDHNpkSzZO8A|K;+ ", false);
		/*String[] ciphers = new String[] {
				Ciphers.cipher[6].cipher,
				Ciphers.cipher[0].cipher,
				Ciphers.cipher[1].cipher.substring(0,340)
		};*/
		test(Ciphers.cipher[0].cipher, false);
		//test(ciphers[1], false);
		//test(ciphers[2], false);
		//test(Ciphers.cipher[0].cipher, false);
		//testScytale();
		
		//System.out.println(positionsJS(Ciphers.cipher[0].cipher, 19));
		
		
		//System.out.println(rewrite3(Ciphers.cipher[6].cipher, 15));
		//System.out.println(rewrite3("0123456789", 4));
		//System.out.println(rewrite3("HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+", 57));
		//System.out.println(new NGramsBean(2, rewrite3(Ciphers.cipher[0].cipher, 19)).jsHsl());

		//System.out.println(rewrite3("04815926X37X", 4));
		//testPeaks();
		//System.out.println(re);
		
		//System.out.println(Arrays.toString(peakPeriodicBigrams(Ciphers.cipher[1].cipher, true)));
		//test(re, false);
		//testForJarlve();
		//NGramsBean bean = new NGramsBean(2, re);
		//System.out.println(bean);
		//bean.dump();
		
		//visualize();
		//rewrite(cipher);
	}
}
