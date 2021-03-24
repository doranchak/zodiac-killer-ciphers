package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.List;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;
import com.zodiackillerciphers.transform.operations.FlipHorizontal;
import com.zodiackillerciphers.transform.operations.FlipVertical;
import com.zodiackillerciphers.transform.operations.Rotate;

/** L-shaped route.  Start at a corner.
 * Select one of two directions.
 * Write out 
 */
public class LRoute extends CipherBase {
	static Random rand = new Random();
	
	public int width;
	public int rotateFlipOperation; // see Rotate.rotFlipOperations
	public int flip; // 0 (none), 1 (horizontal), 2 (vertical)
	
	public static int[] widths = new int[] {2, 4, 5, 10, 17, 20, 34, 68, 85, 170}; // divides evenly into 340
	
	@Override
	public String firstLayer(String plaintext) {
		width = widths[rand.nextInt(widths.length)];
		rotateFlipOperation = rand.nextInt(Rotate.rotFlipOperations.length); // all possible rotations/flips
		int[] rotflip = Rotate.rotFlipOperations[rotateFlipOperation];
		// L starts in upper left corner (first row), proceeds east, then south when we hit the edge.
		// Next L starts on row two, proceeds east, then south when we hit the 1st L
		// Etc.
		// But first, we can optionally flip or rotate the grid, which allows the L's to be read off in different directions
		// and starting corners.
		List<StringBuffer> list = TransformationBase.toList(plaintext, width);
//		System.out.println(width+","+rotateFlipOperation+","+list);
		
		int rotate = rotflip[0]/90;
		int flipH = rotflip[1];
		int flipV = rotflip[2];

		Rotate rot = new Rotate(list, rotate);
		rot.execute();
		list = rot.getOutput();

		if (flipH > 0) {
			FlipHorizontal flip = new FlipHorizontal(list);
			flip.execute();
			list = flip.getOutput();
		}
		if (flipV > 0) {
			FlipVertical flip = new FlipVertical(list);
			flip.execute();
			list = flip.getOutput();
		}
//		System.out.println(list);
		int H = list.size();
		int W = list.get(0).length();
		
		StringBuilder transposed = new StringBuilder();
		int currentEdge = W-1;
		for (int row=0; row<H; row++) {
			for (int col=0; col<=currentEdge; col++) {
				char ch = TransformationBase.getCharAt(list, row, col);
				transposed.append(ch);
			}
			for (int row2=row+1; row2<H; row2++) {
				char ch = TransformationBase.getCharAt(list, row2, currentEdge);
				transposed.append(ch);
			}
			if (currentEdge == 0) {
				break;
			}
			currentEdge--;
		}
		
		return transposed.toString();
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		LRoute sp = new LRoute();
		String xform = sp.firstLayer(pt);
		System.out.println(xform);
		if (xform.length() != 340) {
			throw new RuntimeException("Length is " + xform.length());
		}
//		String result = sp.makeCipher(pt);
//		System.out.println("Result: " + result);
	}

	public static void main(String[] args) {
		 test();
	}

}
