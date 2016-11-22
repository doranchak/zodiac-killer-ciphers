package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Width extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] { new Parameter("width", 1, inputLength()) };
	}
	
	public Width(List<StringBuffer> input, Integer width) {
		this.input = input;
		setParameterValue("width", width);
	}
	public Width(List<StringBuffer> input, Float width) {
		this.input = input;
		setParameterValue("width", width);
	}
	

	@Override
	public void executeMain(boolean showSteps) {
		int width = getParameterValue("width");
		output = toList(input, width, false);
		if (showSteps) {
			say("After applying new width [" + width + "]");
			dump(output);
		}
	}
	public String toString() {
		return "Width(" + getParameterValue("width") + ")";    
	}

	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new Width(list, 34);
		t.execute(true);
	}

	public static void main(String[] args) {
		test();
	}


}
