package com.zodiackillerciphers.homophones;

import java.util.Arrays;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

/** try to perform a progressive search, for better performance.
 * I think it only looks for cycles involving 2 symbols 
 **/
public class ProgressiveSearch {

	public static float[] z340scores;
	static {
		String cipher = Ciphers.cipher[0].cipher;
		String alphabet = Ciphers.alphabet(cipher);
		ActiveCycles cycles = new ActiveCycles(alphabet);
		cycles.process(cipher);
		z340scores = cycles.scores;
	}
	
	public static void search(String cipher, String alphabet, boolean summaryOnly) {
		ActiveCycles cycles = new ActiveCycles(alphabet);
		cycles.process(cipher);
		cycles.dump(summaryOnly);
	}
	
	public static float measureDifferences(String cipher, String alphabet) {
		ActiveCycles cycles = new ActiveCycles(alphabet);
		cycles.process(cipher);
		float diffs = 0;
		for (int i=0; i<z340scores.length; i++) {
			if (i == cycles.scores.length) break;
			diffs += Math.abs(z340scores[i] - cycles.scores[i]);
		}
		return diffs;
	}

	public static void testShuffles() {
		String cipher = Ciphers.cipher[1].cipher;
		int hits = 0;
		int total = 0;
		for (int i=0; i<100000; i++) {
			total++;
			cipher = CipherTransformations.shuffle(cipher);
			String alphabet = Ciphers.alphabet(cipher); 
			ActiveCycles cycles = new ActiveCycles(alphabet);
			cycles.process(cipher);
			//81.3715
			float sum = cycles.sum(cycles.scores);
			if (sum >= 123.574394) {
				hits++;
				System.out.println(hits + " / " + total);
				cycles.dump(true);
			}
		}
		
	}
	
	public static void test() {
		//String cipher = Ciphers.cipher[0].cipher;
		String cipher = "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)UF+BR&/+5.tME4|TD5Y*B|pJbRTlMcK<O2JzH6z9#SLy)#(+WNG|Z5UF+BMcc(:;y8BR";
		//String cipher = "9%(1UyKbjJ.#5BR+3+28@TSp1l-^NBtHER+B|JLY8OzFR(4>bl*VLk+FU2)^RJ/c5.DOzB(WH8MNR+|c+.cO6|5FU+<+RJ|*b.cVOL|5FBc)T(ZU+7XzR+k>+lpyV)D|(#kcNz):68Vp%CK-*<WqC2#pc-Ff2B9+>;ZlCP^BU-7tLRd|D5.p9O)*ZM6Bctz:&yVOp%<K+>^CFqNLPp*-WfzZ2d7;kl<S^+/|dT9f4YK+WGj4EyM+WAlH#+VB+L<z|4&+OkNpB1V2Ff/)z+Mp_*(;KSp2(TGO+FBcMSEG3dWKc.4_G5pDCE4GyTY+_BAdP2p|+tFMPHYGK+F6pX^2";
		//String cipher = ".pEk-lT<PF*9C+B^At^qUG|yVc+BKZl(RTH-*V<NF#pcC2L5WKBG@|tN)Oj|2J#+SBcK>4kpbM+<Mz9dVp*N2O1X6Y#5pOCLOdc)K+<_&*FBc5ORNF(UU%ZpBM+^^L>>;6ctYL2+OTyzZ)8A|K_H:WFB4YfC5Ob.cV^WlM2>9+-tyS;F/Bc<O#HERp-LlVGfUp(.fz4;W%8dz)kRKE7+b|5FS41P(O#|+T3GR&_/<.l9UCWzLHO+BdpD++J+++B+f++p8^%*.7RJ|*T2+b4qz|YjWG5D(F|)NzKMF+*-k.c+(P8JM+24l.zV6/y|5FBcRyd1NpR(:lk73XSGH+BM";
		String alphabet = Ciphers.alphabet(cipher); 
		//System.out.println("z340 reference: " + ActiveCycles.sum(z340scores) + " " + Arrays.toString(z340scores));
		search(cipher, alphabet, false);
		//System.out.println(measureDifferences(cipher, alphabet));
		//search("PUPUPUPUPUPUPUPUPUPPU");
	}
	
	public static void testFlipsRotations(String cipher) {
	}
	
	public static void main(String[] args) {
		test();
		//testShuffles();
	}
	
}
