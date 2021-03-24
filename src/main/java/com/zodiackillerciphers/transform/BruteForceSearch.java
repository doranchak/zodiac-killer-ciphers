package com.zodiackillerciphers.transform;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.transform.operations.Period;
import com.zodiackillerciphers.transform.operations.PeriodColumn;
import com.zodiackillerciphers.transform.operations.Shift;

/** simple brute force generation of a limited list of operations. */
public class BruteForceSearch {
	public static void search(String cipher) {
		List<StringBuffer> list = TransformationBase.toList(cipher, 17);
		search(0, list, "");
	}
	
	public static void search(int which, List<StringBuffer> cipher, String description) {
		if (which == 3) {
			StringBuffer from = TransformationBase.fromList(cipher);
			NGramsBean ng = new NGramsBean(2, from.toString());
			System.out.println(ng.numRepeats() + "	" + description + "	"  + from);
			
			return;
		}
		// loop through all transformations of the given cipher
		
		// Period:
		for (int p=1; p<35; p++) {
			Transformation t = new Period(cipher, p);
			t.execute(false);
			search(which+1, t.getOutput(), description + "Period(" + p + ") ");
		}
		
		// PeriodColumn:
		for (int p=2; p<16; p++) {
			Transformation t = new PeriodColumn(cipher, p);
			t.execute(false);
			search(which+1, t.getOutput(), description + "PeriodColumn(" + p + ") ");
		}

		// Shift:
		for (int direction : new int[] {0,2}) {
			int min = 1;
			int max = direction == 0 ? 19 : 17;
			for (int amount=min; amount<=max; amount++) {
				Transformation t = new Shift(cipher, direction, amount);
				t.execute(false);
				search(which+1, t.getOutput(), description + "Shift(" + direction + ", " + amount + ") ");
			}
		}
		
	}
	
	public static void main(String[] args) {
//		search(Ciphers.Z408);
//		search(Ciphers.Z340);
		
		// first 340 of 408's solution, encoded as a 340:
		search("+z+.4./OO+B-k49kOp<pD;NFL+S+>%t^&8y7Nb+5+FlRZPJNH_E(B.+Oz+AfM|KGfdlc+B5EP7RZ)cF5<28Y6%2^(B+FSypl9(1GdB:pWV&cTHTldzt7YKz59.+OK>R^pSEXbf-+Cp>^4#*p^Rd55yW+zO|b-4@kcW2BDc+V+>pCpH3c5_cWVyYB-4##+BfUtN))RD.FqJJM+S*(-|)K1*2<cF#k(W1RJ|_/(#*(pM*pBXG|L+M+OO<2)c<tWA+BkYZ;G|DLFHGTOjVy2|*TC4.|jzLGM+Kz<P8RlclUFKdC2F|M/zOB9V:+CLUR6lUBT^23L8;6F2UqNM+KzVZU");
	}
}
