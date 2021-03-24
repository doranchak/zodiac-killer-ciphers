package com.zodiackillerciphers.tests.flippedsymbols;

import java.util.HashSet;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

/**
 * he found a pattern of flipped symbols in Z340 (see Aug 8, 2018 email from
 * morf, subject "Fwd: Interesting Pattern found in 340 Cipher")
 */
public class JamesSara {
	/** all symbols that are horizontal and vertical flips */
	public static String flipped1 = "pl^kdfyctjqb";
	/** all symbols that are horizontal flips (vertical flips not included) */
	public static String flipped2 = "plkdfycjqb";
	public static Set<Character> flipped;

	public static String alphabet = Ciphers.alphabet(Ciphers.Z340);
	public static String subset;

	static void initFlipped(String str) {
		flipped = new HashSet<Character>();
		for (int i = 0; i < str.length(); i++)
			flipped.add(str.charAt(i));
	}

	/** return the number of flipped symbols in the given portion of cipher */
	static int countFlipped(String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++)
			if (flipped.contains(str.charAt(i)))
				count++;
		return count;
	}

	/** return the given row of the cipher */
	public static String row(String cipher, int row) {
		return cipher.substring(row * 17, row * 17 + 17);
	}

	/** instead of counting flipped symbols, count from a set of random ones */
	static void randomSymbols() {
		alphabet = CipherTransformations.shuffle(alphabet);
		subset = "";
		for (int i = 0; i < 12; i++)
			subset += alphabet.charAt(i);
		initFlipped(subset);
	}

	/**
	 * how many times do we see the same pattern when the cipher text is randomized?
	 */
	public static void shuffleTest1(int shuffles, boolean randomSymbols) {
		int hits = 0;
		initFlipped(flipped1);
		String cipher = Ciphers.Z340;
		for (int i = 0; i < shuffles; i++) {
			if (randomSymbols) {
				randomSymbols();
			}
			// for (int n = 2; n <= 10; n++) {
			for (int n : new int[] { 5 }) {
				int total = 0;
				String result = "";
				String rowNums = "";
				Set<Integer> counts = new HashSet<Integer>();
				if (!randomSymbols) // don't shuffle cipher if we are only picking random symbols
					cipher = CipherTransformations.shuffle(cipher);
				int reps = 0;
				for (int j = 0; j < n; j++) {
					int count = -1;
					int repsInner = 0;
					boolean found = true;
					for (int row = j; row < cipher.length() / 17; row += n) {
						repsInner++;
						String sub = row(cipher, row);
						if (row == j)
							count = countFlipped(sub);
						else {
							int count2 = countFlipped(sub);
							if (count != count2) {
								found = false;
								break;
							}
						}
					}
					if (found) {
						total++;
						result += count + " ";
						counts.add(count);
						rowNums += (j + 1) + " ";
						reps = repsInner;
					}
				}
				if (total > 0) {
					// shuffle#, period, matches, counts, reps, all same?, cipher
					System.out.println(i + "	" + n + "	" + total + "	{" + result + "}	{" + rowNums + "}	"
							+ (total > 1 && counts.size() == 1) + "	" + reps + "	" + flipCounts(cipher) + "	"
							+ (randomSymbols ? subset + "	" : "") + cipher);
					hits++;
				}
			}
		}
		System.out.println("Hits: " + hits);
	}

	/**
	 * how times is the same phenomenon observed for random selections of 12
	 * symbols?
	 */
	public static void shuffleTest2(int shuffles) {

	}

	public static void dumpRows(String cipher) {
		for (int row = 0; row < cipher.length() / 17; row++) {
			String r = row(cipher, row);
			System.out.println(countFlipped(r) + " " + r);
		}
	}

	public static String flipCounts(String cipher) {
		String result = "{";
		for (int row = 0; row < cipher.length() / 17; row++) {
			String r = row(cipher, row);
			result += countFlipped(r) + " ";
		}
		result += "}";
		return result;
	}

	public static void test1() {
		initFlipped(flipped1);
		for (int row = 0; row < 20; row++) {
			String r = row(Ciphers.Z340, row);
			System.out.println(r + " " + countFlipped(r));
		}
	}

	// TODO: select different set of symbols, of the same size as the set of flipped
	// symbols. do repeats occur?

	public static void main(String[] args) {
		initFlipped(flipped1);
		// test1();
		shuffleTest1(1000000, false);
		// dumpRows(
		// "+B1UP5+(+*f+<+48*Z_W(2q19pPXRUyE+OJG7BcM+lClc+k:yK|b|H-@lS;^y|zWOcJ2R)yM5(<<z9|zVzN8jMtD2+BZOJ+-f-G+ZJ*|cD^/pNF5W|A;D|FBppBKfFVpOUKlTqyT&*%_>O#MBR+p2*+M|HPckOAtzlzz66FV+N8FD2^.^22EBBWL<pY.O)+Yd2<1z(Vb:|<++p5SBVC/p+)5Cd>4kRV.6RBp+OR^T(F3G#ckG)4UH%BK42S/#LW._5F++Y.c+>8Zb4E*N9G5NGl.;3OBKcFcT(Lz#d+Sc7ljp-MtRLT)4KH&F|tW(UY#Rk7K+M9CFdLO-f^dXC>L");
	}

}
