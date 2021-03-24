package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Nothing extends TransformationBase {

	@Override
	public void setupParameters() {
	}

	public Nothing(List<StringBuffer> input) {
		this.input = input;
	}
	@Override
	public void executeMain(boolean showSteps) {
		output = copy(input);
	}
	
	public String toString() {
		return "Nothing()";  
	}

}
