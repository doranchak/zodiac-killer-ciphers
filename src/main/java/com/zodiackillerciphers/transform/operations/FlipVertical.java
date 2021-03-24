package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class FlipVertical extends TransformationBase {

	@Override
	public void setupParameters() {
	}
	
	public FlipVertical(List<StringBuffer> input) {
		this.input = input; 
	}

	@Override
	public void executeMain(boolean showSteps) {
		List<StringBuffer> result = TransformationBase.copy(input);
		// equivalent to: rotate 180 degrees then flipHorizontal
		Rotate r = new Rotate(result, 2);
		r.execute(false); 
		FlipHorizontal f = new FlipHorizontal(r.getOutput());
		f.execute(false);
		output = f.getOutput();
		if (showSteps) {
			say("After flipping vertically:");
			dump(output);
		}
	}
	
	public String toString() {
		return "FlipVertical()";  
	}
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		Transformation t = new FlipVertical(list);
		t.execute(true);
	}
	
	
	public static void main(String[] args) {
		test();
	}
	

}
