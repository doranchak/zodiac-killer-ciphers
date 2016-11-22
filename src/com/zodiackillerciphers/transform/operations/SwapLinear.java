package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class SwapLinear extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] {
				new Parameter("pos1", 0, inputLength() - 1),
				new Parameter("pos2", 0, inputLength() - 1),
				new Parameter("length", 1, inputLength())
		};
	}
	
	public SwapLinear(List<StringBuffer> input, Integer pos1, Integer pos2, Integer length) {
		this.input = input;
		setParameterValue("pos1", pos1);
		setParameterValue("pos2", pos2);
		setParameterValue("length", length);
	}
	public SwapLinear(List<StringBuffer> input, Float pos1, Float pos2, Float length) {
		this.input = input;
		setParameterValue("pos1", pos1);
		setParameterValue("pos2", pos2);
		setParameterValue("length", length);
	}

	@Override
	public void executeMain(boolean showSteps) {
		int pos1 = getParameterValue("pos1");
		int pos2 = getParameterValue("pos2");
		int length = getParameterValue("length");
		
		int pos3 = Math.min(pos1, pos2);
		int pos4 = Math.max(pos1, pos2);
		
		if (pos3 + length - 1 >= pos4) {
			if (showSteps) say(this.toString() + ": overlaps, so skipping.");
			output = copy(input);
			return; // overlap
		}
		
		if (pos4 + length - 1 >= inputLength()) {
			if (showSteps) say(this.toString() + ": goes past end, so skipping.");
			output = copy(input);
			return; // overlap
		}
		
		String s1 = extractLinear(input, pos1, length);
		String s2 = extractLinear(input, pos2, length);
		
		if (showSteps) {
			say("s1 len " + s1.length() + " s2 len " + s2.length() + " pos3 " + pos3 + " pos4 " + pos4 + " length " + length + " inputLength " + inputLength());
			say("Linear section 1: " + s1);
			say("Linear section 2: " + s2);
		}
		
		List<StringBuffer> result = copy(input);
		
		for (int i=0; i<s1.length(); i++) {
			setCharAt(result, pos1+i, getCharAt(input, pos2+i));
			setCharAt(result, pos2+i, getCharAt(input, pos1+i));
		}
		
		output = result;
		
		if (showSteps) {
			
			int r1 = rowFromPos(pos1);
			int c1 = colFromPos(pos1);
			int r2 = rowFromPos(pos2);
			int c2 = colFromPos(pos2);
			say("Linear swap pos1 [" + pos1 + "] (" + r1 + "," + c1 + ") pos2 [" + pos2 + "] (" + r2 + "," + c2 + ") length [" + length + "]:");
			dump(output);
		}
				
		
	}
	public String toString() {
		return "SwapLinear(" + getParameterValue("pos1") + ", " + getParameterValue("pos2") + ", " + getParameterValue("length") + ")";    
	}
	
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new SwapLinear(list, 0,5 , 5);
		t.execute(true);
	}

	public static void main(String[] args) {
		test();
	}
	

}
