package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

/** W168-like "stencil" transposition.
 * A subgrid is applied to a stream of plaintext to produce enciphered text.
 * The subgrid is like a stencil applied to 
 *
 */

// TODO

public class Stencil extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] { new Parameter("reverse", 0, 1) };
	}

	public Stencil(List<StringBuffer> input, Integer reverse) {
		this.input = input;
		setParameterValue("reverse", reverse);
	}
	public Stencil(List<StringBuffer> input, Float reverse) {
		this.input = input;
		setParameterValue("reverse", reverse);
	}

	@Override
	public void executeMain(boolean showSteps) {
		/**
		 * convert text within given box to "spiral" ordering.
		 * 
		 * spiral type = combination of starting point, and direction 0: upper
		 * left, clockwise 1: upper left, counter clockwise 2: upper right,
		 * clockwise 3: upper right, counter clockwise 4: lower right, clockwise
		 * 5: lower right, counter clockwise 6: lower left, clockwise 7: lower
		 * left, counter clockwise
		 **/

		boolean reverse = getParameterValue("reverse") == 1;
		List<StringBuffer> result = TransformationBase.copy(input);
		int boxHeight = inputHeight();
		int boxWidth = inputWidth();

		// boundaries (inclusive) of the spiral as we're reading it. they get
		// pulled in as we change directions.
		int rmin = 0;
		int rmax = boxHeight - 1;
		int cmin = 0;
		int cmax = boxWidth - 1;

		// deltas. these change as we change directions.
		int dr = 0;
		int dc = 1;

		// initial position and spiral result
		int r = 0;
		int c = 0;
		StringBuffer spiral = new StringBuffer();
		// spiral.append(getCharAt(box1, r, c));
		while (spiral.length() < boxWidth * boxHeight) {
			mapTransform(r*boxWidth+c, spiral.length());
			spiral.append(getCharAt(result, r, c));
			r += dr;
			c += dc;
			// did we go over a boundary? if so, change direction, and pull in a
			// boundary
			if (r < rmin) { // was traveling north
				dr = 0;
				dc = 1;
				r = rmin;
				c += dc;
				cmin++;
			} else if (c > cmax) { // was traveling east
				dr = 1;
				dc = 0;
				c = cmax;
				r += dr;
				rmin++;
			} else if (r > rmax) { // was traveling south
				dr = 0;
				dc = -1;
				r = rmax;
				c += dc;
				cmax--;
			} else if (c < cmin) { // was traveling west
				dr = -1;
				dc = 0;
				c = cmin;
				r += dr;
				rmax--;
			}
		}

		if (reverse)
			spiral = spiral.reverse();

		result = toList(spiral.toString(), boxWidth);
		output = result;

		if (showSteps) {
			say("After spiral, reverse=" + reverse + ": ");
			dump(output);
		}

	}
	public String toString() {
		return "Spiral(" + getParameterValue("reverse") + ")";    
	}

	public static void test() {
		String Z340 = Ciphers.Z340;
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new Stencil(list, 0);
		t.execute(true);
		System.out.println("transformMap " + t.getTransformMap());
		String x1 = Transformation.transform(Z340, t.getTransformMap(), false);
		String x2 = Transformation.transform(x1, t.getTransformMap(), true);
		System.out.println("Via transform method: " + x1); 
		System.out.println("Via transform method (reversed): " + x2); 
		
		t = new Stencil(list, 1);
		t.execute(true);
		System.out.println("transformMap " + t.getTransformMap());
	}

	public static String transform(String cipher, int width, boolean reverse) {
		List<StringBuffer> list = TransformationBase.toList(
				cipher, width);
		Transformation t = new Stencil(list, reverse ? 1 : 0);
		t.execute(false);
		return fromList(t.getOutput()).toString();
	}
	
	public static void main(String[] args) {
		test();
		System.out.println(transform(Ciphers.Z340, 17, false));
		System.out.println(transform(Ciphers.Z340, 17, true));
	}

}
