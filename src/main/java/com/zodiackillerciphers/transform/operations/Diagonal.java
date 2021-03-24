package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Diagonal extends TransformationBase {

	/**
	 * start at upper left corner. stream text diagonally, in given direction.
	 * direction = 0: lower left to upper right direction = 1: upper right to
	 * lower left direction = 2: snake: first go lower left to upper right, then
	 * upper right to lower left. direction = 3: snake: first go upper right to
	 * lower left, then lower left to upper right.
	 * 
	 **/

	public Diagonal(List<StringBuffer> input, Integer direction) {
		this.input = input;
		setParameterValue("direction", direction);
	}
	public Diagonal(List<StringBuffer> input, Float direction) {
		this.input = input;
		setParameterValue("direction", direction);
	}

	public String directionAsString() {
		int d = getParameterValue("direction");
		switch (d) {
		case 0:
			return "lower left to upper right";
		case 1:
			return "upper right to lower left";
		case 2:
			return "snake: first go lower left to upper right, then upper right to lower left";
		case 3:
			return "snake: first go upper right to lower left, then lower left to upper right";
		default:
			return "unknown direction";
		}
	}

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] { new Parameter("direction", 0, 3) };
	}

	@Override
	public void executeMain(boolean showSteps) {
		
		StringBuffer sb = new StringBuffer();
		int direction = getParameterValue("direction");

		int h = height(input);
		int w = width(input);

		int startRow = 0;
		int startCol = 0;

		int drow = 0, dcol = 0;
		if (direction == 0 || direction == 2) {
			drow = -1;
			dcol = 1;
		} else if (direction == 1 || direction == 3) {
			drow = 1;
			dcol = -1;
		}

		int count = 0;
		while (sb.length() < w * h) {
			// System.out.println("len " + sb.length() + " startRow " + startRow
			// + " startCol " + startCol);
			int row = startRow;
			int col = startCol;

			StringBuffer line = new StringBuffer();
			while (true) {
				// System.out.println(row + ", " + col);
				line.append(input.get(row).charAt(col));
				row += drow;
				col += dcol;

				if (row < 0 || row >= h || col < 0 || col >= w)
					break;
			}
			if (direction > 1 && count % 2 == 1)
				sb.append(new StringBuffer(line).reverse());
			else
				sb.append(line);
			count++;

			if (direction == 0 || direction == 2) {
				if (startRow < h - 1) {
					startRow++;
				} else {
					startCol++;
				}
			} else if (direction == 1 || direction == 3) {
				if (startCol < w - 1) {
					startCol++;
				} else {
					startRow++;
				}
			}

		}

		List<StringBuffer> result = toList(sb.toString(), width(input));
		if (showSteps) {
			say("Applied diagonal, direction " + direction + " ("
					+ directionAsString() + ")");
			dump(result);
		}

		output = result;
	}
	
	public String toString() {
		return "Diagonal(" + getParameterValue("direction") + ")";  
	}
	
	public static String transform(String cipher, int width, int which) {
		List<StringBuffer> list = TransformationBase.toList(
				cipher, 17);
		Transformation t = new Diagonal(list, which);
		t.execute(false);
		list = t.getOutput();
		return TransformationBase.fromList(list).toString();
	}

	public static void testDiagonal() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		for (int d = 0; d <= 3; d++) {
			// System.out.println("= " + d + " =");

			Transformation t = new Diagonal(list, d);
			t.execute(true);
		}
	}

	public static void main(String[] args) {
		testDiagonal();
	}

}
