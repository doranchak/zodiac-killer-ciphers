package com.zodiackillerciphers.transform.operations;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Reverse extends TransformationBase {

	@Override
	public void setupParameters() {
		// TODO Auto-generated method stub

	}

	public Reverse(List<StringBuffer> input) {
		this.input = input;
	}

	@Override
	public void executeMain(boolean showSteps) {
		
		List<StringBuffer> result = new ArrayList<StringBuffer>(); 
		for (int r1 = 0; r1 < inputHeight(); r1++) {
			StringBuffer row = new StringBuffer();
			for (int c1 = 0; c1 < inputWidth(); c1++) {

				int r2 = inputHeight() - 1 - r1;
				int c2 = inputWidth() - 1 - c1;

				row.append(getCharAt(input, r2, c2));
			}
			result.add(row);
		}
		
		if (showSteps) {
			say("After reverse:");
			dump(result);
		}
		output = result;
	}
	
	public String toString() {
		return "Reverse()";    
	}
	
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		Transformation t = new Reverse(list);
		t.execute(true);
	}
	
	
	public static void main(String[] args) {
		test();
	}


}
