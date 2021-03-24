package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Selector;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class RectangularSelection extends TransformationBase implements Selector {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] {
				new Parameter("row", 0, inputHeight() - 1),
				new Parameter("col", 0, inputWidth() - 1),
				new Parameter("height", 1, inputHeight()),
				new Parameter("width", 1, inputWidth())
		};
	}

	public RectangularSelection(List<StringBuffer> input, Integer row, Integer col, Integer height, Integer width) {
		this.input = input;
		setParameterValue("row", row);
		setParameterValue("col", col);
		setParameterValue("height", height);
		setParameterValue("width", width);
	}
	public RectangularSelection(List<StringBuffer> input, Float row, Float col, Float height, Float width) {
		this.input = input;
		setParameterValue("row", row);
		setParameterValue("col", col);
		setParameterValue("height", height);
		setParameterValue("width", width);
	}

	public boolean isValidSelection() {
		int row = getParameterValue("row");
		int col = getParameterValue("col");
		int height = getParameterValue("height");
		int width = getParameterValue("width");
		if (row < 0 || col < 0) return false;
		if (row >= input.size() || col >= input.get(0).length()) return false;
		if (row+height-1 >= input.size() || col+width-1 >= input.get(0).length()) return false;
		return true;
	}
	
	@Override
	public void executeMain(boolean showSteps) {
		int row = getParameterValue("row");
		int col = getParameterValue("col");
		int height = getParameterValue("height");
		int width = getParameterValue("width");
		if (isValidSelection()) {
			output = extractBox(input, row, col, height, width, showSteps);
			if (showSteps) {
				List<StringBuffer> show = copy(input);
				List<StringBuffer> showBox = copy(output);
				fill(showBox,'_');
				show = replaceBox(show, showBox, row, col);
				
				say("Selected box, shown in grid:");
				dump(show);
				say("Extracted box at (" + row + ", " + col + ") height [" + height + "] width [" + width + "]:");
				dump(output);
			}
		}
		else {
			if (showSteps) say("Invalid selection; skipping");
			output = copy(input);
		}
	}

	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.Z340, 17);
		dump(list);
		Transformation d = new RectangularSelection(list, 8,10,5,4);
		d.execute(true);
		
		String cipher = TransformationBase.fromList(d.getOutput()).toString();
		System.out.println(NGramsBean.numRepeats(2, cipher));
	}
	public static void test2() {
		
		String cipher = Ciphers.Z340;
		for (int i=0; i<1000000; i++) {
			String shuf = CipherTransformations.shuffle(cipher);
			List<StringBuffer> list = TransformationBase.toList(
					shuf, 17);
			Transformation d = new RectangularSelection(list, 10,0,10,16);
			d.execute();
			String result = TransformationBase.fromList(d.getOutput()).toString();
			if (NGramsBean.numRepeats(2, result) == 0)
			System.out.println(i + " Hit " + result + ", " + shuf);	
		}
	}

	public String toString() {
		return "RectangularSelection(" + getParameterValue("row") + ", " + getParameterValue("col") + ", " + getParameterValue("height") + ", " + getParameterValue("width") + ")";    
	}
	public static void main(String[] args) {
		test();
	}

	@Override
	public List<StringBuffer> replaceSelection(List<StringBuffer> input) {
		List<StringBuffer> result = copy(this.input); // the original input has the entire original cipher
		if (!isValidSelection()) {
			return result;
		}
		int width = getParameterValue("width");
		int height = getParameterValue("height");
		int row = getParameterValue("row");
		int col = getParameterValue("col");
		
		// first, reset the original selection in case some operation as left behind some gaps
		for (int r = row; r < row+height; r++) {
			for (int c = col; c < col+width; c++) {
				setCharAt(result, r, c, ' ');
			}
		}
		
		List<StringBuffer> box = truncateBox(toList(input, width, false), width, height); // ensures the width matches the original selection
		return replaceBox(result, box, row, col);
	}
}
