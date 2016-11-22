package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Selector;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class LinearSelection extends TransformationBase implements Selector {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] {
				new Parameter("pos", 0, inputLength() - 1),
				new Parameter("length", 0, inputLength() - 1),
		};
	}

	public LinearSelection(List<StringBuffer> input, Integer pos, Integer length) {
		this.input = input;
		setParameterValue("pos", pos);
		setParameterValue("length", length);
	}

	public LinearSelection(List<StringBuffer> input, Float pos, Float length) {
		this.input = input;
		setParameterValue("pos", pos);
		setParameterValue("length", length);
	}
	@Override
	public void executeMain(boolean showSteps) {
		int pos = getParameterValue("pos");
		int length = getParameterValue("length");

		int row = rowFromPos(pos);
		int col = colFromPos(pos);
		
		int H = inputHeight();
		int W = inputWidth();
		
		String line = "";
		while (line.length() < length) {
			line += getCharAt(input, row, col);
			col++;
			if (col == W) {
				col = 0;
				row++;
				if (row == H) break;
			}
		}
		
		output = toList(line, W);

		if (showSteps) {
			say("Linear selection, position [" + pos + "] row [" + rowFromPos(pos) + "] col [" + colFromPos(pos) + "] length [" + length + "]:");
			dump(output);
		}
		
	}

	public String toString() {
		return "LinearSelection(" + getParameterValue("pos") + ", " + getParameterValue("length") + ")";  
	}
	
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation d = new LinearSelection(list, 10, 40);
		d.execute(true);
	}

	public static void main(String[] args) {
		test();
	}

	@Override
	public List<StringBuffer> replaceSelection(List<StringBuffer> input) {
		List<StringBuffer> result = copy(this.input); // the original input has the entire original cipher
		
		StringBuffer newInput = fromList(input); // a possibly manipulated version of the original linear selection.  keep spaces but remove trailing padding.
		
		int pos = getParameterValue("pos");
		int length = getParameterValue("length");
		int newLength = newInput.length();
		int pos2 = 0;

		for (int i = pos; i < pos + length; i++)
			setCharAt(result, i, ' '); // so we leave spaces behind if
										// replacement is shorter than original
										// selection.
		
		//System.out.println(pos+length);
		while (pos2 < newLength && pos < inputLength()) {
			//System.out.println(pos + " " + pos2 + " " + length + " " + (pos+length));
			//System.out.println(newInput.charAt(pos2));
			char c = newInput.charAt(pos2++);
			setCharAt(result, pos++, c);
		}
		
		return result;
		
	}

	@Override
	public boolean isValidSelection() {
		// TODO Do this right
		return true;
	}
}
