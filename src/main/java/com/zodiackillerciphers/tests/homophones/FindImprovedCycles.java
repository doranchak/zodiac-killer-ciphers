package com.zodiackillerciphers.tests.homophones;

import java.util.Date;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.old.HomophonesKingBahler;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;
import com.zodiackillerciphers.transform.operations.Period;

/**
 * Jarlve discovered removing row 14 drastically improves hom cycles.
 * 
 * http://www.zodiackillersite.com/viewtopic.php?p=54990#p54990
 * 
 * It prompted me to try to find the best possible substring removal.
 * 
 */
public class FindImprovedCycles {
	static String Z340 = Ciphers.cipher[0].cipher;
	static float PCS_BASE = (float) HomophonesNew.perfectCycleScoreFor(2, Z340,
			3, false);
	static int count = 0;

	public static void search() {
		System.out.println("Reference PCS: " + PCS_BASE);
		search(0, 1);
		System.out.println("Total tests: " + count);
	}

	public static void search(int start, int end) {
		if (start == Z340.length())
			return;

		for (int end2 = end; end2 <= Z340.length(); end2++) {
			if (end2 - start > Z340.length() / 4)
				break; // don't delete more than a quarter of the cipher text
			count++;
			// compute cycle score resulting from deleting [start, end)
			StringBuilder sb = new StringBuilder(Z340).delete(start, end2);
			float pcs2 = (float) HomophonesNew.perfectCycleScoreFor(2,
					sb.toString(), 3, false);
			// if (pcs2 > PCS_BASE)
			String deleted = Z340.substring(start, end2);
			System.out.println(pcs2 + "	" + start + "	" + end2 + "	" + sb + "	"
					+ deleted + "	" + deleted.length());
		}

		// now search from new start position
		search(start + 1, start + 2);
	}

	/** reproducing Jarlve's row-based test */
	public static void testRows(String cipher) {
		for (int L = 2; L < 8; L++) {
			System.out.println(L + "	"
					+ HomophonesNew.perfectCycleScoreFor(L, cipher, 3, false));
			for (int row = 0; row < 20; row++) {
				int start = row * 17;
				int end = start + 17;
				StringBuilder sb = new StringBuilder(cipher).delete(start, end);
				String sub = cipher.substring(start, end);
				System.out.println(L
						+ "	"
						+ HomophonesNew.perfectCycleScoreFor(L, sb.toString(),
								3, false) + "	" + row + "	" + sb + "	" + sub);
			}
		}
	}

	/** reproducing Jarlve's row-based test - REMOVE_HOMOPHONES method */
	public static void testRows3(String cipher) {
		for (int row = 0; row < 20; row++) {
			int start = row * 17;
			int end = start + 17;
			StringBuilder sb = new StringBuilder(cipher).delete(start, end);
			String sub = cipher.substring(start, end);
			System.out.println(
					+ HomophonesKingBahler.scoreAppearances(sb.toString())
							 + "	" + row + "	" + sb + "	" + sub);
		}
	}
	
	/** use other measurement */
	public static void testRows2(String cipher) {
		System.out.println(JarlveMeasurements.homScore(cipher));
		for (int row = 0; row < 20; row++) {
			int start = row * 17;
			int end = start + 17;
			StringBuilder sb = new StringBuilder(cipher).delete(start, end);
			String sub = cipher.substring(start, end);
			System.out.println(JarlveMeasurements.homScore(sb.toString()) + "	" + row + "	" + sb + "	" + sub);
		}
	}

	/**
	 * removing row14 improves L=3 cycles. test adjustments to start/end point
	 * to see if it exactly row 14 or not.
	 */
	public static void testRow14() {
		int L = 3;
		int count = 0;
		Date d1 = new Date();
		// row 14 begins at pos 221, ends at pos 237.
		for (int start = 221-42; start <= (221+42); start++) {
			for (int end = 238-42; end <= (238+42); end++) {
				if (end <= start)
					continue;
				StringBuilder sb = new StringBuilder(Z340).delete(start, end);
				String sub = Z340.substring(start, end);
				double pcs = HomophonesNew.perfectCycleScoreFor(L,
						sb.toString(), 3, false);
				Date d2 = new Date();
				count++;
				System.out.println(L + "	" + pcs + "	" + start + "	" + end
						+ "	" + sb + "	" + sub + "	"
						+ (d2.getTime() - d1.getTime()) + "	" + count);
			}
		}
	}
	
	/** test removal of all possible pairs of rows */
	public static void testRowsPairs(String cipher) {
		for (int r1 = 0; r1 < 20; r1++) {
			for (int r2 = 0; r2 < 20; r2++) {
				String newCipher = "";
				for (int i = 0; i < cipher.length(); i++) {
					int row = i / 17;
					if (row == r1 || row == r2)
						continue;
					newCipher += cipher.charAt(i);
				}
				System.out.println(HomophonesKingBahler.scoreAppearances(newCipher) + "	" + r1 + "	" + r2 + "	"
						+ newCipher);
			}
		}
	}

	/** test removal of all possible triples of rows */
	public static void testRowsTriples(String cipher) {
		for (int r1 = 0; r1 < 20; r1++) {
			for (int r2 = 0; r2 < 20; r2++) {
				for (int r3 = 0; r3 < 20; r3++) {
					String newCipher = "";
					for (int i = 0; i < cipher.length(); i++) {
						int row = i / 17;
						if (row == r1 || row == r2 || row == r3)
							continue;
						newCipher += cipher.charAt(i);
					}
					System.out.println(HomophonesKingBahler.scoreAppearances(newCipher) + "	" + r1 + "	" + r2 + "	" + r3 + "	" 
							+ newCipher);
				}
			}
		}
	}
	/** test removal of all possible quads of rows */
	public static void testRowsQuads(String cipher) {
		System.out.println("Cipher: " + cipher);
		System.out.println("Base score: " + HomophonesKingBahler.scorePerfectCycles(cipher));
		
		int rows = cipher.length()/17;
		for (int r1 = 0; r1 < rows; r1++) {
			for (int r2 = 0; r2 < rows; r2++) {
				for (int r3 = 0; r3 < rows; r3++) {
					for (int r4 = 0; r4 < rows; r4++) {
					String newCipher = "";
					for (int i = 0; i < cipher.length(); i++) {
						int row = i / 17;
						if (row == r1 || row == r2 || row == r3 || row == r4)
							continue;
						newCipher += cipher.charAt(i);
					}
//					long score = HomophonesKingBahler.scoreAppearances(newCipher);
					double score = HomophonesKingBahler.scorePerfectCycles(newCipher);
					//if (score < 2509691) continue;
					System.out.println(score + "	" + r1 + "	" + r2 + "	" + r3 + "	" + r4 + "	" 
							+ newCipher);
				}
			}}
		}
	}
	
	public static void shuffleTest() {
		String cipher = "+E7'D*!$3*.)HSF1$M&^*%Y6[ZU=($VQ,*$R+>)*'/KG$(*8V$BITC#X3>E14#]+H\\LX-3*=$)*@II^1HQ$[>!YP^<R\\U:T,<?$IO;#QK*5A\"$9I0&CE*^.+0D]M/JV$!K'*QR46)-4Z\"MI$8H1?9'?E&7^A?(!-B)\\V?Z+55A*Q_M9KX+G_/$2-4%E]R!;SFL)_<*8\"1I3&#N=#HL';Y$O0Z*F@[S!X.V\"':$*-^V\\:DK3UF,*E!8AB\"TQ$7K>[4=*[\"^\\H#$XME*T3C49+[]PG>-;%5#X*\\&N)T/RE1@?N3>2S\\IQ#]P$P'[JU*;O*:1@@K$*#$U:?RW2,Y*9E";
		StatsWrapper stats = new StatsWrapper();
		stats.actual = JarlveMeasurements.homScore(cipher);
		int count = 0;
		double max = stats.actual;
		System.out.println("actual score: " + max);
		while (true) {
			count++;
			if (count % 100000 == 0) {
				System.out.println("After " + count + " shuffles:");
				stats.output();
			}
			cipher = CipherTransformations.shuffle(cipher);
			double score = JarlveMeasurements.homScore(cipher);
			stats.addValue(score);
			if (score > max) {
				System.out.println("New max: " + max + " " + cipher);
			}
		}
		
	}
	
	public static void testOperations(String cipher) {
		
		double scoreOriginal = HomophonesKingBahler.scorePerfectCycles(cipher);
		System.out.println("cipher: " + cipher);
		System.out.println("score: " + scoreOriginal);

		List<StringBuffer> list = TransformationBase.toList(cipher, 17);
		for (int p=2; p<cipher.length()/2; p++) {
			Transformation t = new Period(list, p);
			t.execute(false);
			String newCipher = TransformationBase.fromList(t.getOutput()).toString();
			double score = HomophonesKingBahler
					.scorePerfectCycles(newCipher);
			System.out.println(score + "	" + p + "	" + newCipher);
		}
	}

	public static void main(String[] args) {
		// search();
		//testRows3(Ciphers.Z340);
//		testRowsQuads(Ciphers.Z340);
//		testRowsQuads(CipherTransformations.shuffle(Ciphers.Z340));
		testOperations(Ciphers.Z340);
		//testRows2("+E7'D*!$3*.)HSF1$M&^*%Y6[ZU=($VQ,*$R+>)*'/KG$(*8V$BITC#X3>E14#]+H\\LX-3*=$)*@II^1HQ$[>!YP^<R\\U:T,<?$IO;#QK*5A\"$9I0&CE*^.+0D]M/JV$!K'*QR46)-4Z\"MI$8H1?9'?E&7^A?(!-B)\\V?Z+55A*Q_M9KX+G_/$2-4%E]R!;SFL)_<*8\"1I3&#N=#HL';Y$O0Z*F@[S!X.V\"':$*-^V\\:DK3UF,*E!8AB\"TQ$7K>[4=*[\"^\\H#$XME*T3C49+[]PG>-;%5#X*\\&N)T/RE1@?N3>2S\\IQ#]P$P'[JU*;O*:1@@K$*#$U:?RW2,Y*9E");
		//testRow14();
//		shuffleTest();
	}
}
