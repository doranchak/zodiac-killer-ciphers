package com.zodiackillerciphers.old;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.cosine.CosineSimilarity;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.ngrams.NGramResultBean;
import com.zodiackillerciphers.ngrams.NGrams;

/** Measurements taken of cipher texts undergoing experimentation.  We hope that comparing measurements
 * of various cipher text hypotheses will shed light on which of them might be correct.  
 */
public class Measurements {

	static String[] test408shuffles = new String[] {
		"k%9R%BU==ZP/XOpB/HVW@9eFKYG+YqP!6eTJMNq)kQ5UYIDtY7^A(S%REPfq#/BlURO9p^kH\\YdV8ZLJeFWrM6+@X)(K8SDq9zq5IGqNRB8BlT#YtESGQOIEdLMqk@UR#/BRH^XP!Zc5q#)L9pKIr8MWqNPBe\\F=Ek+DjH(U6RtcZ)+HIl6VpWRL^5Od9I/\\AY@Q_DTJeXrR)5PVLGtE=RMUHNK%8MIrNLP9A(k!5Zl)8JWUz+VP#VOAp9t\\TBk7=^z6MUGIlSf%D%er%kNf%V/ABc)E#ZP9S8ep+#B_@9WXqAc9FqdO%q_Wc_qRTUSY^+5YZV9T_E%tYeKLA#_GD!H\\Aqz7=XF9!dLXBOe_XPceQG#d6%RF5#I@ZrJ6_BWtJqQ+P8TEEVMqkI)I9XWUH=6r",
		"Z/O/=kB=%UPRB9pX%GePYKH6Y9F+@eW!qVU^tIQT75qkYN)MYDJ#9UBfAOqRP/%ESRl(ZMFJVpr8\\dLHYkWe^DGq986IS)KqX(@5z+YIGETqO#8ltBBRQSN#PHBUEXRq@/MkL^Rdpq8IL!W9q)K5#cMrZ+R(jEN6k\\=DeFBUHPVOLWlt56+Ip)HZ^Rc_ReT@drQ\\YD/AIXJ9R8NUE)%=LtMVGPKH5kJl5AM8(L9!NPr)ZIAk\\9VWBOV#p+PzTtUlrDfG7eI6USzM^%%=cSZEA%9B%/)fVNP#kWqcq@8F9#_X+Bp9AeR5YU_d+q_cTqW%^SOYGAK%Y_tTEe9_V#LZXBd97DX=AzF\\qHL!!##R6QO5GPedXc_F%etT+qBI8WJ_Jr6ZPQ@9rHW)E6IqIXMkV=UE",
		"//P%BO9RpB=Z=k%UXYe+V6PW@!eYGKH9FqI^YJ7tMNY)5UQTqkDB9/(OUS%REq#fARPlJML^rFkHWY8ZVp\\de9Gq+Iq@X5(SD86)KzEItNOGRBQB#YTq8lSBP/dXHLM^kR#UEq@RIqKZW8c5M#9pL!q)rjRDP6(BeUFk+EN\\=HWOpc5LZ)^H6Vlt+IRTRD9reI/XAQ_@d\\YJU8M5%NPVKG=RE)LtH5J!I8lrN)P(kAML9Z9kpUB\\z+TPOAVWV#tfrS=eD^z%MIlG76U%ES)k9ZNfPVBcA%%/#qqXeFcp+9B9W@8#_AU5TO+Y%q^WqR_d_cSKGeZ_AV9#_tY%YTEL9BF!XdH\\Lq=X7DAz!6#de5R_XFcG#QOPe%qTJ@8+ZrP6WtBIJ_QWrXE6HVM=kI9)EqIU",
		"/P=/%9XZpBkBR%=UOe+YY9WqG!eH6@VKFP^Y5IqMDUY)T7NJQkt9/qBRSl#REAO%(fPUML8J\\keZWYprH^VdFGqS9)@zD5(6IX+8KqIt#E8RSYQBqOBNTlGP/RBqLR#^kEXMdU@HqK9IqcrpM#!W5ZL)8RDkj\\BH+UFN6ePE=(Op6W+ZRV^Ht5)clILRDQT\\IJ_XAdr/9@Ye8M=ULPHRKG)%V5EtNJ!(5LrZk)PM8NIA9lkpO9VztATPWB+UV#\\rSIf6^%l%M7ez=GUDS)BE%N#cPV%9fkA/ZqX9q#pAW9B8F+e@_c5TqU_%SR^Wd+qO_cYGetKTVLY#_Y_9Z%EABF=9AH!XLqDX\\!7zd#dG6P_%#FcO5XeQeRTJWqJZQtP6I8r@B_+rXIWqVU9=kE6ME)IH",
		"/9%=R%O=pP/XBZkBUeW9Y@VPK!+Yq6GHeF^Mq5NJtQYYID7UT)k9SRq%(UfR/BlO#AEPMk\\8H^FVWLJerZpYdG@)SX+q85q9zID6(KIR8#BNGTQtESOYqBlPLqRMdHU^/BRX#Ek@qcq95Z8LMKIrWp!#)RB\\keP(EUDjH6+NF=OZ+6)cLl^pWR5VtHIRI\\Q/9e@XDTJr_dAY8PL=V5NEKMUH%R)GtJrL(NIlA)!5Z8kMP9kzVO+U\\VTp9tBAWP#r^6Iz=DG%Sf%el7MUSN%BfkZAP)E#9c%V/qp#9+ec@9XqAFW8B_5%_qqOY_^TUS+RdWcGVTt9ZA%#eKL_YY_EBHA=\\!d7LF9!XXDqz#_PGXeRQFd6%5#OceTZJWr@+BPJqQ8tI6_rVqIMEH)=XWU69EkI",
		"RBU%/%P==p9/XOZkB@eF9eV+KY!WYqPGH6N)kq^JYQ5YMIDtUT7%EPR9(/fqRSBlU#AOHYd\\M^LV8WkJeFZprX(K)G+q8S5@9zqD6IBBl8INtT#QRESGYqOMk@qPd/UR^LBRH#EX5#)qqZKL9McIr8p!WeF=\\RPDEkUBjH(+N6)HI+Ocpl6^ZWRLVt5/AY\\R9D@QXITJe_drVGtL85ME=KPUHNR)%NP9LJI!A()r5ZlkM8+P#VkUpVOTz9t\\AWBzMU6r=SGI%^f%Dl7efV/%Sk)ABPNE#Zc%9+B_#qeX@99pqAcW8FqWc_5OT_q^%USYRd+9_ETGZe%t#VKLAYY_\\qzAB!F7=LH9!dXDXXceP#edQGF_6%R#O5r6_JT@JBWPZqQ+tI8MkIqrEX)I=VWUH9E6",
		"P=pX%/BO=%U9/RkZB+Y!qVe6PK9FWY@HGeY5YDJ^7tQqkMINTU)/qRl(9OUfRPSB%A#EL8We^MrFV\\dkJHpZYqS5z+GIq8)K@9X6D(t#QSNIOGT8lREBqYB/R^RdPXHUq@LBME#kK9MrZqW8Lq)cI5!p#DkUHPR6(E\\=BjeN+Fp6^RcO5Ll+IZW)tVHDQXJ9Rre@\\YIT/d_AM=KH58%NELtPUV)RG!()ZIJ8lAL9r5NMkPpOTtUkB\\VV#z9+WAPSI%%=reDG6U^fz7lM)BP#kS9ZA%/NEf%cVX99AeqFc@#_pq+8WBTq^SO5+Y__c%UqdRWet#LZG_A%TEVK9YY_F=L!!BXd7AzH9\\DXqdGF%e#5RQPe_6XO#cJWPQ@T8+BJ_ZqrIt6XI=UEr6H)qIVWME9k",
		"==U/RZ/%O9%XPpkBBKYFY@GeVPW9q+!He6Q5kINU^JtMqDYYT)7fqPB%#9(USRl/RAEOV8dJHZM^Fk\\eLWpYr8SK9XDG+q@)zq56(IT#lEBYINGR8StQqBOUR@BM#PdHLqR/^EkXL9)I5pqZ8cqrKM!#WEk=je+RP(B\\HDUNF6l6IW)VOcLZ+Rp^tH5@QYT/_R9eI\\JDXdArE=tUVR85NPLHMK)G%A(95NkJIlrLZ!)MP8VO#9+AkU\\zVtpTWPBGIUfzlr=D^6%S%7MeAB/EfcSkZN%#)P%V9@9_q+Wqecp#AX98BF_qcUqR5OY%_ST^dW+%tEK9YGZAVTLe#Y__7=z9\\XB!dHA!FLDqXQGe6X##eR_P%dFOc5BW_qrtT@+ZJQJPI68)IIWM9rEHVqUX=Ek6",
		"UZ%=/BROP9=p/%kXBFG9KYe@P+WY!eVHq6kUqQI)NtYM5Y^JTD7P#RfBE%U/SqR9(AlOdZ\\VJYHFLk8WM^perKD)89(Xqq@S5G+6zIlY8TEBBGtR#QINqSO@#qUBkMH/LR^PdERX)pqLI#58Kc9MqZ!rW=+\\EjFe(DBkURPNH6IV+lWH)LpZ6^OctR5Y_\\@TA/eDIQXR9dJrtRLEUGVNMP=K85)H%9kLA5PNl!r()JIMZ8#AVV9P+\\pzOTkUWtBUl6GfMzDS^I%r=7%e/c%AEVfZ)NBPSk%#9_W#@qB+cXp99qe8AFcR__UWqYT%q^5OdS+EYT%K_9AeVt#GZYL_zXA79q\\dFH=LB!D!Xe#PQ6cXRd_GF#eO%5_tJBq6r+JZWPT@IQ8I9q)WkMHXVI=rEEU6",
		"/Op=B9X%kZ/P=R%BUYP!Y6WqVHGe+K@9eFItY57MDJTU^YQNq)kBURqOSl(A#9/f%REPJFW8rke^pZMLVH\\Yd9q5SI@z+6DGq8X)(KEGQ#ORSNqYItTB8BlBH^RXLRdE#P/UMqk@I8M9WcrZ!pqKL5q#)j(Uk6BHPN+RDEe\\F=WL^65ZRctVOpl)+HITeXQrIJ9d_RD@/\\AYUNK=%PH5)R8MEVLGt5l)(8rZIMkJ!ANLP99\\TOBztUWAkpV+VP#fD%Ie^%=7lrSGz6MUEZPB9N#k%cS)Af%V/qc99FpAe8WqX@+#B_UY^q+%SOdR5T_q_WcKA#t_VLZYYGe%9T_E9dL=XH!!DXBF7\\Aqz6RFG5_%eO##dQXPceq+PW8ZQ@ItTJBrJ6_WH=I6VUEE9rX)MqkI"
	};
	
	/**
	 * Measure the given cipher text
	 * @param ciphertext cipher text to measure
	 * @param counts symbol counts.  passed in so we don't have to keep computing it.
	 * @return array of results: [ngrams, cosine distances, homophone score]
	 */
	public static Measurement measure(String ciphertext, Map<Character, Integer> counts) {
		Measurement m = new Measurement(ciphertext);
		//m.ngrams = NGrams.measure2(ciphertext);
		
		List<NGramResultBean> beans = NGrams.measure2(ciphertext, true);
		if (beans.size() < 20) {
			m.ngramTop20Mean = 1;
		} else {
			float mean = 0; int count = 0;
			for (int i=0; i<beans.size(); i++) {
				mean += beans.get(i).getProbability();
				count++;
			}
			mean /= count;
			m.ngramTop20Mean = mean;
		}
		
		m.cosineDistances = CosineSimilarity.measure(ciphertext);
		m.meanSimilarityPerSymbol = m.cosineDistances[3];
		m.homophones = Homophones.measure(ciphertext, counts);
		return m;
	}
	
	/** n-symbol merge search:  for each combination of n symbols selected from the cipher text alphabet,
	 * merge the symbols and calculate the measurements to see if selection of a real homophone set
	 * maximizes the measurements somehow. */
	public static void mergeSearch(String ciphertext, int n) {
		String alphabet = Ciphers.alphabet(ciphertext);
		int[] indices;
		CombinationGenerator x = new CombinationGenerator (alphabet.length(), n);
		StringBuffer combination;
		Map<Character, Integer> counts;
		Measurement m;
		
		
		// print out measurements of the original
		counts = HomophonesProblem.countsMapFor(ciphertext);
		m = measure(ciphertext, counts);
		System.out.println("??	????	" + mergeScores(m));  
		
		
		while (x.hasMore ()) {
		  combination = new StringBuffer ();
		  indices = x.getNext ();
		  for (int i = 0; i < indices.length; i++) {
		    combination.append (alphabet.charAt(indices[i]));
		  }
		  String comb = combination.toString();
		  //System.out.println(combination);
		  String merged = merge(ciphertext, comb);
		  counts = HomophonesProblem.countsMapFor(merged);
		  m = measure(merged, counts);
		  System.out.println(comb + "	" + Ciphers.realHomophone(comb) + "	" + mergeScores(m));  
		  
		}
	}
	
	public static String mergeScores(Measurement m) {
		return m.toString();
		
	}
	
	/** merge the given symbols of the ciphertext by replacing their occurrences with the first symbol in the given set. */
	public static String merge(String ciphertext, String symbols) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<symbols.length(); i++) set.add(symbols.charAt(i));
		StringBuffer sb = new StringBuffer(ciphertext);
		for (int i=0; i<sb.length(); i++) {
			if (set.contains(sb.charAt(i))) {
				sb.setCharAt(i, symbols.charAt(0));
			}
		}
		return sb.toString();
	}
	
	public static void testSpeed() {
		Map<Character, Integer> counts = HomophonesProblem.countsMapFor(Ciphers.cipher[1].cipher); 
		Measurement m;
		int count = 0;
		Date start = new Date();
		while (true) {
			for (int i=0; i<test408shuffles.length; i++) {
				m = measure(test408shuffles[i], counts);
				count++;
			}
			if (count % 100 == 0) {
				Date end = new Date();
				long diff = end.getTime()-start.getTime();
				System.out.println(count + ", rate: " + (1000*((float)count)/diff) + " per second");
			}
			
		}
	}
	
	public static void test(boolean forWiki) {
		Measurement m;
		for (int i=0; i<Ciphers.cipher.length; i++) {
			String cipher = Ciphers.cipher[i].cipher.replaceAll(" ", "").replaceAll("	", "");
			Map<Character, Integer> counts = HomophonesProblem.countsMapFor(cipher);
			m = null;
			try {
				m = measure(cipher, counts);
				m.ioc = Stats.ioc(new StringBuffer(cipher)).floatValue();
				m.chi2 = Stats.chi2(new StringBuffer(cipher));
				m.entropy = Stats.entropy(new StringBuffer(cipher));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (forWiki) {
				String style = Ciphers.cipher[i].solution == null ? "" : "style=\"background-color:#cfc\"";
				System.out.println("|-valign=\"top\" " + style);
				m.dumpLine(""+i, true);
				m.dumpLine(Ciphers.cipher[i].description, false);
				m.dumpForWiki();
				m.dumpLine("[http://oranchak.com/zodiac/wiki/cipher_"+i+".txt Transcription]", false);
			} else 
				System.out.println(i + "	" + Ciphers.cipher[i].description + "	" + m);
		}
	}
	
	
	public static void test408s() {
		Measurement m;
		int[] c = {1, 20, Ciphers.cipher.length-1};
		
		for (int i=0; i<c.length; i++) {
			String cipher = Ciphers.cipher[c[i]].cipher.replaceAll(" ", "").replaceAll("	", "");
			Map<Character, Integer> counts = HomophonesProblem.countsMapFor(cipher);
			m = null;
			try {
				m = measure(cipher, counts);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(c[i] + "	" + Ciphers.cipher[c[i]].description + "	" + m);
		}
	}
	

	
	public static void main(String[] args) {
		testSpeed();
		//test(false);
		//test408s();
		//mergeSearch(Ciphers.cipher[1].cipher, 5);
		//System.out.println(Ciphers.cipher[1].cipher);
		//System.out.println(merge(Ciphers.cipher[1].cipher, "ZPNOE+"));
		//System.out.println(new DecimalFormat("##0.000").format(918230912.09812039890123));
		
	}
}
