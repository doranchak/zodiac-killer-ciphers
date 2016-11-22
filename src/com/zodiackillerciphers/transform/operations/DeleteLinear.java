package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Selector;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class DeleteLinear extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] {
				new Parameter("pos", 0, inputLength() - 1),
				new Parameter("length", 0, inputLength() - 1),
		};
	}

	public DeleteLinear(List<StringBuffer> input, Integer pos, Integer length) {
		this.input = input;
		setParameterValue("pos", pos);
		setParameterValue("length", length);
	}

	public DeleteLinear(List<StringBuffer> input, Float pos, Float length) {
		this.input = input;
		setParameterValue("pos", pos);
		setParameterValue("length", length);
	}
	@Override
	public void executeMain(boolean showSteps) {
		int pos = getParameterValue("pos");
		int length = getParameterValue("length");

		output = copy(input);
		
		for (int p=pos; p<pos+length&&p<inputLength(); p++) {
			setCharAt(output, p, ' ');
		}

		if (showSteps) {
			say("Deleted linear selection, position [" + pos + "] length [" + length + "]:");
			dump(output);
		}
		
	}

	public String toString() {
		return "DeleteLinear(" + getParameterValue("pos") + ", " + getParameterValue("length") + ")";  
	}
	
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation d = new DeleteLinear(list, 10, 40);
		d.execute(true);
	}

	public static void main(String[] args) {
		test();
	}

}
