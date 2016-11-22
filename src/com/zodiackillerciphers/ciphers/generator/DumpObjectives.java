package com.zodiackillerciphers.ciphers.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.ActiveCycles;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.ProgressiveSearch;
import com.zodiackillerciphers.homophones.SequenceMatcher;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.tests.EvenOddAndFactors;
import com.zodiackillerciphers.tests.PrimePhobia;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;

public class DumpObjectives {
	/** show cipher's objectives */
	public static void dump(String cipher) {
		// unigram stats
		Map<Character, Integer> counts = sortByValue(Ciphers.countMap(cipher));
		System.out.println("Unigrams: " + counts);
		System.out.println("Unigram distance: " + CandidateKey.measureUnigramDistance(cipher));
		System.out.println("Unigram differences: " + CandidateKey.dumpDifferences(cipher));
		System.out.println("Ngrams:");
		NGramsBean.dumpNGramsFor(cipher);
		//NGramsBean.dumpMissingGramsFor(cipher);
		System.out.println("Ngrams distance: " + NGramsBean.measureNgramDistanceCountsOnly(cipher, NGramsBean.referenceCipherBeans.get("z340")));
		//SequenceMatcher.test(cipher);
		System.out.println("Prime phobia: " + PrimePhobia.errors(cipher));
		EvenOddAndFactors.dumpEvenOddNGrams(cipher);
		System.out.println("Error: " + EvenOddAndFactors.measureEvenOddNgramsCountsOnly(cipher));
		System.out.println("Line repeats: " + CandidateKey.measureLineRepeats(cipher));

		String alphabet = Ciphers.alphabet(cipher); 
		ProgressiveSearch.search(cipher, alphabet, true);
		
		System.out.println("Cycle compare: " + HomophonesNew.measureCompare(cipher));
		System.out.println("Nonrepeat: " + JarlveMeasurements.nonrepeat(cipher));
		System.out.println("Nonrepeat altenate: " + JarlveMeasurements.nonrepeatAlternate(cipher));
		// TODO: fix this? System.out.println("Period 19 bigram repeats: " + Periods.applyPeriod(cipher, 2, 18, true).repeats());
	}

	/** http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static void test() {
		System.out.println();
		System.out.println("------------- Z340");
		System.out.println();
		dump(Ciphers.cipher[0].cipher);
		System.out.println();
		System.out.println("------------- EXPERIMENT ");
		System.out.println();
		//dump("C>XOpTS^2()LH1y+MkzZ39:7cB_OL|WyK_B^t)KlVVTf<B1LqOO*.C^G4dByk|+pkO-*+C/p14+Np()G+FBy|B<F;B8^bkVc.bWqc-lzCTd|5FBc(-t+RTzWf>((#Nd<.cCDj>6M2zcp6VzJ+b8Ul*UZzM8H|NWFlROFBc.p5@-KyPf|5F+UZ^5H6U-V3Fk+&D5BM4.RUp++EZA)DFSP+XG2AcG2OpDNpG2HER*|JRS7l#(c:+)49B%|j<K++^OWp<z5M+ylJ#+&d*TGR2L>LKM9+RKEYfZNz)(#O+4/J|tBt#8RY5W4<Y%l_2*-Fb++9|2;;p72+zFOYMPKLc++");
		//dump("TEHS7N_1>@z3XlP:_UbM++ |5F2%8RJ|*YO+++O+RzZ*8;8KW|Yq-.XN@L.zC STTAJFP4 HLS)//*H%p7<RqS;:Y>cdL%68UZ9LcG+&%YUVL: A1Cl&FB%<LT%|G2Pfc+&J&HWq1UC5*-2GyPL*:.Np<XR.<|b.cV%M)LU1 8-% 8R_FBc1K/DdFl-BbPG6Djq.DRS(#OBy|THERf*Cb&-*p y;.yL>DZcy%T6E|ByqH4 ()ZEFSYNp^F/R- ()594bF<-)LB3OpET@Fl@43Y|5FBc p7t(*E@S:H*cDM2XC*MD%Op;*WU.&X(#4FO|):#O@J _SM& Y3NYVX j");
		//dump("8y7T<:dG2U4Fl>^#OjJJVJD*-%3 G+RDj UZ^S8c|S(#jHER3WGdYJ(CF>|F6@D+/VPNp8R6T>-*GFZ+D^G_(4t5AU+qWCzFlFM+d+_k@@b<>fNpEVc.b4AG2D|5F^c:#RcCP <zZ187:K c <Y.(P^R+-;By|k/;zT bYM+c-9Op&@V+&.+b^ .l<Ud/k|5FBcD/*|JR4D1p7+GffKWF/|.-O++R()M+++p7UZJ D5<f>Jf8RB_FBy RC74f^AFBc^OpJ8.R8D)L +(X<JFK|(#>.U6dCD8:Sb# kY1JY2()L G2/YO+Oc;bc + #<_GYS-4H#Ob+&+<|RW<t;D");
		//dump("O2&#+NzO<V^T9DjyO*  ^ /Tz;)XW|5FBcNtK@tU+*MKMdRq+lEY  ( XZNSL9TY) j_E<t^z HER -9DX>klOBl:kKBDF_2MRJ|*TZ7&b.cVY^KVB2  |p<5M 6+c2UE*%FD;J+(4;G_d. *-Ofy|ZR -E6(+Rb<WXz23pTA->jC >pZdjC6y7/ &2OpKG*- dqf :2B:.L. W |W3|/ Y28X  3 XP2R<-*l >6C  MzH<GL#bT:Ef | Db>O 2+C > CEzZ478|KE 2F yB  Z  Z|5FBcO+bG@p7kflZ 3qMA  ++zkRH-YK> +XHO<Y H 91^Y 3ll K%Gl");
		dump("HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZO++RK2zHJSpp7^l8*V3pGW()L#_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+");
		
	}
	public static void main(String[] args) {
		test();
		//String c = Ciphers.cipher[0].cipher;
		//System.out.println(sortByValue(Ciphers.countMap(c)));
	}

}
