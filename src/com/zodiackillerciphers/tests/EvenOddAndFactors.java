package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.transform.CipherTransformations;

public class EvenOddAndFactors {
	
	public static String z340oddsRemoved = "E>lVk1T2N+(ODY<K)yc+ZW)#HSp^8Vp+R29+td5P&kpRFO*CF2(5K%2cG.L(2f#+Nz@9<++RFcA4-lV^+p<B-+/t|YpTK2cR|54.&F6S#N5B(8lF^54.Vt+B1:9EVZ-|.zKO^fq2c+1C+lB)+)CWPST(pFd<t_O*C>DNkzOAK+";
	public static String z340evensRemoved = "HRp^P|LGdpB#%W.*fB:MUG(LzJp7l*3O+K_Mzj|F+4/8^l-dk>D#+q;UXVz|GJjO_Y+LdMbZ2By6KzU+JO7FyUR5EDBbMO<lJ*TM+Bz9y+|Fc;RGNf2bc4+yX*4C>U5+c3B(p.MGRTL6<FW|L+WzcOH/)|kW7BYB-cMHpSZ8|;";

	/** from given cipher text, generate new cipher text that has all odd positions removed.
	 * generate another that has all even positions removed.
	 * then compare distribution of repeated ngrams to those of Z340 when the same
	 * procedure is applied.  
	 */
	
	public static float measureEvenOddNgrams(String cipher) {
		String[] removed = removeOddsEvens(cipher);
		float m1 = NGramsBean.measureNgramDistance(removed[0], NGramsBean.referenceCipherBeans.get("z340odds"));
		float m2 = NGramsBean.measureNgramDistance(removed[1], NGramsBean.referenceCipherBeans.get("z340evens"));
		
		return m1+m2;
		
	}

	public static float measureEvenOddNgramsCountsOnly(String cipher) {
		String[] removed = removeOddsEvens(cipher);
		float m1 = NGramsBean.measureNgramDistanceCountsOnly(removed[0], NGramsBean.referenceCipherBeans.get("z340odds"));
		float m2 = NGramsBean.measureNgramDistanceCountsOnly(removed[1], NGramsBean.referenceCipherBeans.get("z340evens"));
		return m1+m2;
		
	}
	
	public static String[] removeOddsEvens(String cipher) {
		String cipherOddsRemoved = "";
		String cipherEvensRemoved = "";
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			int n=i+1;
			if (n % 2 == 0) cipherOddsRemoved += ch;
			else cipherEvensRemoved += ch;
		}		
		return new String[] {cipherOddsRemoved, cipherEvensRemoved};
	}
	
	
	public static void dumpEvenOddNGrams(String cipher) {
		String[] removed = removeOddsEvens(cipher);
		System.out.println("=== ODDS REMOVED ===");
		int reps1 = NGramsBean.dumpNGramsFor(removed[0]);
		System.out.println("=== EVENS REMOVED ===");
		int reps2 = NGramsBean.dumpNGramsFor(removed[1]);
		System.out.println("Difference " + Math.abs(reps1-reps2) + " Reps1 " + reps1 + " Reps2 " + reps2);
		
		System.out.println("Odds symbol count: " + Ciphers.countMap(removed[0]).size());
		System.out.println("Evens symbol count: " + Ciphers.countMap(removed[1]).size());
	}
	
	/** test symbol positions for even, odd, and various other factors */
	public static void test(String cipher) {
		// array: {total, odds, evens, divisible by 3, divisible by 4, ... divisible by 20}
		Map<Character, int[]> counts = new HashMap<Character, int[]>();
		for (int i=0; i<cipher.length(); i++) {
			char key = cipher.charAt(i);
			int[] val = counts.get(key);
			if (val == null) val = new int[21];
			val[0]++;
			if ((i+1)%2 != 0) val[1]++;
			if ((i+1)%2 == 0) val[2]++;
			for (int n=3; n<=20; n++) {
				if ((i+1) % n == 0) val[n]++;
			}
			counts.put(key, val);
		}
		
		for (Character key : counts.keySet()) {
			String msg = key + "	";
			for (int i : counts.get(key)) msg += i + "	";
			System.out.println(msg);
		}
	}
	
	public static void testShuffles() {
		String cipher = Ciphers.cipher[0].cipher;
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		
		for (int i=0; i<1000000; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			String[] split = removeOddsEvens(cipher);
			NGramsBean bean1 = new NGramsBean(2, split[0]); 
			NGramsBean bean2 = new NGramsBean(2, split[1]);
			int diff = Math.abs(bean1.numRepeats() - bean2.numRepeats());
			Integer val = counts.get(diff);
			if (val == null) val = 0;
			val++;
			counts.put(diff, val);
			if (i % 10000 == 0) System.out.println(i+"...");
		}
		for (Integer key : counts.keySet())
			System.out.println(key + " " + counts.get(key));
	}
	
	public static void main(String[] args) {
		//test("041D,%(!9=:AN).-8\"1J@Q,T3KA8G?MO'2E/X3FU\\-GL_R!-%:#3W.&HBO8\"4G]<CX[+.E6IV8Z;H2=-K6()NU@+MFRS?\\Z+8EQ1E,_LJ9%TQN!EYHS$<^O3EX/IWE]-#C-E.>40P1$Y6B:-@VJD08^1MOEWQ=18IETGK5U1]3)FGS!3'@1,DHG:R0\\AQQX-_L%SD-=VKS0-UC(.PY?8[JLD4Q0N_VT9E!,AMB-E=8KZ<EOQ+X[6W&]G2$:8C1.)>(38PH5E'YG-\\\"N^I@B>\",/6MS'-E-WX<]F)3QCQPS1YI^[*4R3G?_%.$/&3@-ELKAD-UE\\JV0HEDNS(TE%7");
		//String cipher = "P%Z_fcy+8RX+4 >|-_W_ppk_5TG<jLqHcf6jdD94XC8WO3p;8E52|-_WbHERS_dVNpHSd2PcS;#t;/&RXAN+lb<Sj*SCPZLqJ(bt^|pf/:@5P;W1Z|SC T92HOW*y|WRJ|*y+|U(F5+KOCN|fM>-S32cS-C5-*@R*-OZEOG(d-fXz4|*L@|5F__ FYORWF+WZ_GPROy+-_NZCW;C).L%5*KbFBcK*K3NLMzZOD;7cdB+5(R1J3R6^*5Wyz<YE_5GqMA<|OL&%LUT@_by(<P2KCbH+KbZzfTRj_d_S8W.3.O#YN@AqZPj)XR^c5|5FBc596F;P4b.cVCby_NG&%OS";
		//String cipher = "N6b@<AFb<zX_R N * C JK^#M_T.fMftL+79#RS # + + R|RK4L4 . JL-EA^:;6d&@Vt&Y<pjC4 * * @4#N<3p2UVc.bjY|lUVPMb* 2 c 4SN;GPA6NzE4FBc.f-8R|U*|JRf49Sl6b)jjc3+|5FBcyC%Ez4-*CHz&Jc6_-zZ1D^;C|4@2lRV6-RFN#RG%dpK>#+b*6#jj+L)H.|%LW<l*f+bPRSUCY>k6+<B|lHERj^<R7 c O * 4 PV>ScW/%Ltt7O * V_dMk 5 4/5&HT. 4 L b(#-#Y;S+2>PZ1|5F+z&/N.VRGF&)+|YzP1<pKtE4k:%#&K*-SMb";
		//dumpEvenOddNGrams(Ciphers.cipher[0].cipher);
		
		/*for (int i=0; i<10000; i++) {
			dumpEvenOddNGrams(CipherTransformations.shuffle(Ciphers.cipher[0].cipher));
		}*/
		
		/*NGramsBean bean = new NGramsBean(2, z340oddsRemoved);
		System.out.println(bean);*/
		testShuffles();
		//dumpEvenOddNGrams(cipher);
		//dumpEvenOddNGrams(Ciphers.cipher[0].cipher);
		//System.out.println(measureEvenOddNgrams(Ciphers.cipher[0].cipher));
		//System.out.println(measureEvenOddNgrams(cipher));
	}
	
}
