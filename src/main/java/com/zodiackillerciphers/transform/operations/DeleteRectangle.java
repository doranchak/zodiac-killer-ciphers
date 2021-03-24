package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Selector;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

/** delete a rectangular selection */
public class DeleteRectangle extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] {
				new Parameter("row", 0, inputHeight() - 1),
				new Parameter("col", 0, inputWidth() - 1),
				new Parameter("height", 1, inputHeight()),
				new Parameter("width", 1, inputWidth())
		};
	}

	public DeleteRectangle(List<StringBuffer> input, Integer row, Integer col, Integer height, Integer width) {
		this.input = input;
		setParameterValue("row", row);
		setParameterValue("col", col);
		setParameterValue("height", height);
		setParameterValue("width", width);
	}
	public DeleteRectangle(List<StringBuffer> input, Float row, Float col, Float height, Float width) {
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
		output = copy(input);
		if (isValidSelection()) {
			for (int r=row; r<row+height; r++) {
				for (int c=col; c<col+width; c++) {
					setCharAt(output, r, c, ' ');
				}
			}
			if (showSteps) {
				say("After deleting rectangle at row " + row + " col " + col + " height " + height + " width " + width + ":");
				dump(output);
			}
		} else {
			if (showSteps) say("Not a valid selection");
		}
	}

	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation d = new DeleteRectangle(list, 5, 5, 3, 10);
		d.execute(true);
	}

	public String toString() {
		return "DeleteRectangle(" + getParameterValue("row") + ", " + getParameterValue("col") + ", " + getParameterValue("height") + ", " + getParameterValue("width") + ")";    
	}
	public static void main(String[] args) {
		test();
	}

}
