package com.zodiackillerciphers.ciphers.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.tests.Kasiski;
import com.zodiackillerciphers.tests.LetterFrequencies;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;
import com.zodiackillerciphers.transform.operations.FlipHorizontal;
import com.zodiackillerciphers.transform.operations.Reverse;
import com.zodiackillerciphers.transform.operations.Rotate;

import ec.util.MersenneTwisterFast;

public class Scytale {
	
	/** the period corresponds to the diameter of the scytale.
	 * the input is written left to right on a long strip.
	 * the strip is wound around the scytale with the given diameter.
	 * the output is produced by reading rows horizontally from the scytale. 
	 **/
	public static String encode(String input, int period) {
		return Periods.rewrite3(input, period);
	}
	/** the period corresponds to the diameter of the scytale */
	public static String decode(String input, int period) {
		int regular = input.length() % period;
		if (regular == 0) regular = period;
		int irregular = period - regular;
		
		int rows = input.length()/period;
		if (input.length() % period > 0) rows++;
		
		String[] grid = new String[rows];
		for (int i=0; i<grid.length; i++) grid[i] = "";
		
		int currentRow = 0;
		for (int i=0; i<input.length(); i++) {
			grid[currentRow] += input.charAt(i);
			//System.out.println("i " + i+" irregular " + irregular + " regular "+regular + " char " +input.charAt(i) + " rows " + rows + " currentRow " + currentRow);
			//for (String row : grid) System.out.println(row);
			currentRow++;
			if (currentRow >= rows) {
				currentRow = 0;
				regular--;
			}
			if (regular == 0) {
				rows--;
				regular--;
			}
		}
		
		String result = "";
		for (String row : grid) result += row;
		return result;
	}
	
	public static void test() {
		//System.out.println(encode("0123456789", 4));
		//System.out.println(decode(encode("0123456789", 4),4));
		
		String inp = "thisisatestoftheemergencyscytalesystemyoutwattz";
		String en = encode(inp, 12);
		String de = decode(en, 12);
		System.out.println("input " + inp);
		System.out.println("encoded " + en);
		System.out.println("decoded " + de);
		
	}
	
	public static void testRandom() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		for (int i=0; i<100000; i++) {
			String s = "";
			int len = rand.nextInt(100) + 5;
			int period = rand.nextInt(100) + 1;
			
			for (int j=0; j<len; j++) s += LetterFrequencies.randomLetter();
			String en = encode(s, period);
			String de = decode(en, period);
			if (!de.equals(s)) {
				System.out.println(period);
				System.out.println(s);
				System.out.println(en);
				System.out.println(de);
			}
			
		}
	}
	
	public static void forAzdecrypto() {
		/*List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		
		Set<String> seen = new HashSet<String>();
		for (boolean reverse : new boolean[] {false, true}) {
			for (int rotate : new int[] {0,1,2,3}) {
				for (boolean flip : new boolean[] {false, true}) {
					if (reverse) {
						Transformation t = new Reverse(list);
						t.execute(false);
						list = t.getOutput();
					}
					Transformation t = new Rotate(list, rotate);
					t.execute(false);
					list = t.getOutput();
					System.out.println("after rev " + reverse + " rot " + rotate + " width " + list.get(0).length());
					
					for (StringBuffer sb : list) System.out.println("line " + sb);
					
					
					if (flip) {
						t = new FlipHorizontal(list);
						t.execute(false);
						list = t.getOutput();
					}
					String cipher = TransformationBase.fromList(list).toString();
					//if (seen.contains(cipher)) continue;
					//seen.add(cipher);
					
					for (int period=1;period<=1; period++) {
						String decoded = decode(cipher, period);
						System.out.println("cipher_information=z340_period_" + period + "_reverse_" + reverse + "_rotate_"+rotate+"_flip_"+flip);
						List<StringBuffer> lines = TransformationBase.toList(
								decoded, 17);
						for (StringBuffer line : lines) System.out.println(line);
						System.out.println();
						
					}
					
				}
			}
		}*/
		String[] ciphers = new String[] {
		"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+", // rot0flip0		
		"d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>", // rot0flip1
		">|+R|ylz2U-d(#p_SBNHMF+ccBG6<+z<G589pypEDk)T.XF9cRlM2+RMp:+RHdW+31NSl/U+JK^+7cB>NWCLz*^yR5VbfqFz^M(pp<z1B:f#Jt++j%ltl+#lk7W6K45+|E^Z#;Oj8UO^StcC(92N*|JRO2-d*Z%VzBP<OC4|5D+2+U*|VGDPZ_O+pEb5TYOF_cd53WWkOYSF^>.F4BpBNXCFp(Y|8OHl.VcBMp7cYGkPO).1ABTWfUVc.b<yzVF++L<L|*/BMZ4(+TFA+.>&+#*TK-(|q5t;&MB6@z24RzKG;C))G-+8BKy4LLDkKHf2+cpL2++RFO-K9|(/2J)d", // rot1flip0
		"HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+", // rot1flip1
		"+;K|A8OZzSkpNHDM>cC-*BOY_Bt7<WdkF|p)(/THSOPcWzCW)++L)|BWlF+<C61L+TcR2GqMf.^pO(KBz3.c|+-5ZUV>EC94:*1XBy++t4Vc.b425f^NFGlR8;(cBF5|N+#yS96zFB&+.M4T5*|JRlc<2OKMTbpBYD|Et5/R+U-yBF<7pO+J^+VUlz-K46AycBF2RZ+b+M<d9L@+zYN_+O#jfJ2G(|Lz.VGXcU2;%qK+5#(D2>FkCd*-OlF^R8p/k4&+PF5|djtz+M9_2KR++Op3V*8l^7ppSJHz#L)(WGZU+Mc:yB)fK*<.YWD%O#(B+pNd2GTL1|kPV^lp>REH", // rot2flip0
		">MDHNpkSzZO8A|K;+|FkdW<7tB_YOB*-Cc++)WCzWcPOSHT/()pRcT+L16C<+FlWB|)L|c.3zBK(Op^.fMqG2yBX1*:49CE>VUZ5-+lGFN^f524b.cV4t++z69Sy#+N|5FBc(;8R2<clRJ|*5T4M.+&BFU+R/5tE|DYBpbTMKO-zlUV+^J+Op7<FBy-d<M+b+ZR2FBcyA64K(G2Jfj#O+_NYz+@L9#5+Kq%;2UcXGV.zL|p8R^FlO-*dCkF>2D(_9M+ztjd|5FP+&4k/Spp7^l8*V3pO++RK2By:cM+UZGW()L#zHJNp+B(#O%DWY.<*Kf)HER>pl^VPk|1LTG2d", // rot2flip1
		"d)J2/(|9K-OFR++2Lpc+2fHKkDLL4yKB8+-G))C;GKzR42z@6BM&;t5q|(-KT*#+&>.+AFT+(4ZMB/*|L<L++FVzy<b.cVUfWTBA1.)OPkGYc7pMBcV.lHO8|Y(pFCXNBpB4F.>^FSYOkWW35dc_FOYT5bEp+O_ZPDGV|*U+2+D5|4CO<PBzV%Z*d-2ORJ|*N29(CctS^OU8jO;#Z^E|+54K6W7kl#+ltl%j++tJ#f:B1z<pp(M^zFqfbV5Ry^*zLCWN>Bc7+^KJ+U/lSN13+WdHR+:pMR+2MlRc9FX.T)kDEpyp985G<z+<6GBcc+FMHNBS_p#(d-U2zly|R+|>", // rot3flip0
		"+cpL2++RFO-K9|(/2J)d;C))G-+8BKy4LLDkKHf2K-(|q5t;&MB6@z24RzKG|*/BMZ4(+TFA+.>&+#*TABTWfUVc.b<yzVF++L<L8OHl.VcBMp7cYGkPO).1OYSF^>.F4BpBNXCFp(Y|Z_O+pEb5TYOF_cd53WWkzBP<OC4|5D+2+U*|VGDPStcC(92N*|JRO2-d*Z%Vk7W6K45+|E^Z#;Oj8UO^p<z1B:f#Jt++j%ltl+#lNWCLz*^yR5VbfqFz^M(pHdW+31NSl/U+JK^+7cB>Dk)T.XF9cRlM2+RMp:+RMF+ccBG6<+z<G589pypE>|+R|ylz2U-d(#p_SBNH"}; // rot3flip1
		
		for (int i=0; i<ciphers.length; i++) {
			for (int period=1;period<340; period++) {
				String decoded = decode(ciphers[i], period);
				//List<StringBuffer> lines = TransformationBase.toList(
				//		decoded, 17);
				//for (StringBuffer line : lines) System.out.println(line);
				//System.out.println();
				NGramsBean bean = new NGramsBean(2, decoded);
				System.out.println(bean.numRepeats() + " cipher_information=z340_" + i + "_scytale_decode_period_" + period);
			}
		}
		
	}
	/** test if rewrite3 is equivalent to scytale.  compare to http://www.cryptool-online.org/index.php?option=com_cto&view=tool&Itemid=73&lang=en 
	 * conclusion: encode is the same, but decode is not due to irregular modulo when cipher length is not a multiple of period.
	 * 
	 * */
	public static void testScytale() {
		/*String pt = "THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG";
		for (int i=1; i<=20; i++) System.out.println(i + " " + rewrite3(pt, i));*/
		
		String pt = "ANNGLNFITHRTSLMD_LIAMITEAT_LSBYNIMNH_AFIAUTENE_GITUEOREY_EEATSRCRS_DDNHIDHTE_BWONEAHL_YIRTRNEL_STIHITSS_OHTESIUT_LTICNSTO_DAEIOCLL_IKSTTAEE_EIWYYRRN_RNEDERSGMSGRETYAOYOFESTILOWFRRPRNODATOEIEGNSRHMPTSOETDETOETNVHCRHRTOTEEODETHRRNNNCUIEEATETONNDDDUIIRFGEAERGNPOLCNIEHUSRORDNTBEWTOENAOOSHUTEOITRTONISTLRHONANAAAAOBOTGGSWDOETECAIFEDESIOINUAOPAN";
		for (int i=1; i<=170; i++) {
			String re = decode(pt, i);
			System.out.println(ZKDecrypto.calcscore(new StringBuffer(re)) + " " + i + " " + re);
		}
	}
	
	/** compare Periods.rewrite3 and Scytale.decode.  How many untranspositions are the same? */
	public static void testCompare() {
		
		String cipher = Ciphers.cipher[0].cipher;
		// map cipher to untransposition types
		Map<String, String> map = new HashMap<String, String>();
		for (int period=1; period<340; period++) {
			String c1 = decode(cipher, period);
			String val = map.get(c1);
			if (val == null) val = "";
			val += "scytale" + period + " ";
			map.put(c1, val);
			
			String c2 = Periods.rewrite3(cipher, period);
			val = map.get(c2);
			if (val == null) val = "";
			val += "period" + period + " ";
			map.put(c2, val);
		}
		for (String key : map.keySet()) {
			System.out.println(map.get(key) + ": " + key);
		}
	}
	
	/** look for common factors in the spacing of bigrams */
	public static void testFactors() {
		String cipher = Ciphers.cipher[0].cipher;
		cipher = CipherTransformations.shuffle(cipher);
		/** track overall count stats for each factor */
		Map<Integer, DescriptiveStatistics> allCounts = new HashMap<Integer, DescriptiveStatistics>(); 
		for (int period=1; period<=cipher.length()/2; period++) {
			String re = /* decode(cipher, period);*/ Periods.rewrite3(cipher, period);
			
			// key: new position.  val: old position
			Map<Integer, Integer> map = Periods.rewrite3Map(cipher, period);
			//if (period == 39) System.out.println(re);
			NGramsBean bean = new NGramsBean(2, re);
			Map<Integer, Integer> counts = new HashMap<Integer, Integer>(); 
			for (String ngram : bean.repeats) {
				List<Integer> positions = bean.positions.get(ngram);
				for (int i=0; i<positions.size()-1; i++) {
					for (int j=i+1; j<positions.size(); j++) {
						int a = map.get(positions.get(i));
						int b = map.get(positions.get(j));
						//int a = positions.get(i);
						//int b = positions.get(j);
						int pos1 = Math.min(a, b);
						int pos2 = Math.max(a, b);
						int diff = pos2-pos1;
						System.out.println("period " + period + " ngram " + ngram + " diff " + diff);
						List<Integer> factors = Kasiski.factors(diff);
						for (Integer factor : factors) {
							Integer val = counts.get(factor);
							if (val == null) val= 0;
							val++;
							counts.put(factor, val);
							//if (factor == 17 && period==1) {
							//	System.out.println("hsl_random([[" + pos1 + "," + (pos1+1) + "," +pos2 + "," + (pos2+1) + "]]);");
						//	}
						}
					}
				}
			}
			Map<Integer, Integer> counts2 = new HashMap<Integer, Integer>(); 
			for (Integer key : counts.keySet()) {
				//if (counts.get(key) > 1) counts2.put(key, counts.get(key));
				counts2.put(key, counts.get(key));
				
			}
			for (Integer key : counts2.keySet()) {
				// count, factor, period
				System.out.println(counts2.get(key) + " " + key + " " + period);
			}
			
			// track overall count statistics
			for (int factor = 2; factor < 100; factor++) {
				DescriptiveStatistics val = allCounts.get(factor);
				if (val == null) val = new DescriptiveStatistics();
				allCounts.put(factor, val);
				Integer count = counts2.get(factor);
				if (count == null) count = 0;
				val.addValue(count);
			}
		}
		for (int factor = 2; factor < 100; factor++) {
			DescriptiveStatistics val = allCounts.get(factor);
			System.out.println(factor + " mean " + val.getMean() + " stddev " + val.getStandardDeviation());
		}
	}
	
	/** look for Scytale and periodic untranspositions that maximize repeating ngrams for several values of n*/
	public static void testNgrams() {
		String c = Ciphers.cipher[0].cipher;
		for (int period=1; period<=170; period++) {
			String[] ciphers = new String[] {
					 Periods.rewrite3(c, period), decode(c, period)	
				};
			for (int i=0; i<ciphers.length; i++) {
				String cipher = ciphers[i];
				String type = i==0 ? "period" : "scytale";
				for (int n=2; n<=10; n++) {
					NGramsBean bean = new NGramsBean(n, cipher);
					System.out.println(bean.numRepeats() + " " + n + " " + type + " " + period);
				}
			}
			
		}
	}
	
	public static void testHslWithMap() {
		String re = Periods.rewrite3(Ciphers.cipher[0].cipher, 135);
		Map<Integer, Integer> map = Periods.rewrite3Map(Ciphers.cipher[0].cipher, 135);
		NGramsBean bean = new NGramsBean(2, re);
		System.out.println(bean.jsHsl(map));
		
	}
	
	/*static boolean hasMrLowePattern(List<StringBuffer> list) {
		
	}*/
	
	static void findMrLowePatterns() {
		String cipher = Ciphers.cipher[0].cipher;
		
	}
	
	public static void main(String[] args) {
		
		//forAzdecrypto();
		//testScytale();
		//testCompare();
		//testFactors();
		//testNgrams();
		//testHslWithMap();
		System.out.println(encode("1....................................6.....................................................X..4................X...................X................XXX2....................................7....Y...................Y...................Y...........5....YYYY................................................3....................................8", 1));
		//System.out.println(Periods.rewrite3(Ciphers.cipher[0].cipher, 101));
		
		/*String re = Periods.rewrite3(Ciphers.cipher[0].cipher, 26);
		Map<Integer, Integer> map = Periods.rewrite3Map(Ciphers.cipher[0].cipher, 26);
		System.out.println(map);
		NGramsBean bean = new NGramsBean(2, re);
		for (String ngram : bean.repeats) {
			String s = "";
			for (Integer pos : bean.positions.get(ngram)) {
				s += (map.get(pos)) + ", " + map.get(pos+1) + ", ";
				//System.out.println("pos " + pos + " map " + map.get(pos));
			}
			System.out.println(s);
		}*/
		//System.out.println(re);
		
		
		//System.out.println(bean.jsHsl());
		
		
		
		//testRandom();
		//test();
		
		// normal
		/*String cipher = Ciphers.cipher[0].cipher;
		for (int period=1;period<=340; period++) {
			String decoded = decode(cipher, period);
			System.out.println(decoded);
		}
		// horizontal mirror
		cipher = Ciphers.cipher[6].cipher;
		for (int period=1;period<=340; period++) {
			String decoded = decode(cipher, period);
			System.out.println(decoded);
		}*/
		// rot90
		// rot90 + flipped
		
	}
}
