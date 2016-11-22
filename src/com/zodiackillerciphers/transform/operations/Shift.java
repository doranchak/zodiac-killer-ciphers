package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Shift extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] { new Parameter("direction", 0, 7),
				new Parameter("amount", 0, inputLength())		
		};
	}

	public Shift(List<StringBuffer> input, Integer direction, Integer amount) {
		this.input = input;
		setParameterValue("direction", direction);
		setParameterValue("amount", amount);
	}
	public Shift(List<StringBuffer> input, Float direction, Float amount) {
		this.input = input;
		setParameterValue("direction", direction);
		setParameterValue("amount", amount);
	}

	@Override
	public void executeMain(boolean showSteps) {
		/**
		 * shift the selected rectangular region in the given direction by the
		 * given number of positions. text is allowed to wrap around boundaries
		 * directions: 0 N, 1 NE, 2 E, 3 SE, 4 S, 5 SW, 6 W, 7 NW
		 */
		int direction = getParameterValue("direction");
		int amount = getParameterValue("amount");
		List<StringBuffer> result = TransformationBase.copy(input);

		int drow = 0;
		int dcol = 0;

		if (direction == 0) {
			drow = -amount;
		} else if (direction == 1) {
			drow = -amount;
			dcol = amount;
		} else if (direction == 2) {
			dcol = amount;
		} else if (direction == 3) {
			drow = amount;
			dcol = amount;
		} else if (direction == 4) {
			drow = amount;
		} else if (direction == 5) {
			drow = amount;
			dcol = -amount;
		} else if (direction == 6) {
			dcol = -amount;
		} else if (direction == 7) {
			drow = -amount;
			dcol = -amount;
		}

		for (int r = 0; r < result.size(); r++) {
			for (int c = 0; c < result.get(0).length(); c++) {

				// apply wrapping logic
				int rr = r + drow;
				int cc = c + dcol;
				while (rr < 0)
					rr += inputHeight();
				while (cc < 0)
					cc += inputWidth();
				while (rr >= inputHeight())
					rr %= inputHeight();
				while (cc >= inputWidth())
					cc %= inputWidth();

				setCharAt(result, rr, cc, getCharAt(input, r, c));
			}
		}
		if (showSteps) {
			say("After shifting direction [" + direction + "] amount ["
					+ amount + "]:");
			dump(result);

		}
		output = result;
	}
	public String toString() {
		return "Shift(" + getParameterValue("direction") + ", " + getParameterValue("amount") + ")";    
	}

	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		Transformation t = new Shift(list, 3, 5);
		t.execute(true);
	}

	public static void main(String[] args) {
		test();
	}

}
