package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class PeriodColumn extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] { new Parameter("period", 1, inputWidth()) };
	}

	public PeriodColumn(List<StringBuffer> input, Integer period) {
		this.input = input;
		setParameterValue("period", period);
	}
	public PeriodColumn(List<StringBuffer> input, Float period) {
		this.input = input;
		setParameterValue("period", period);
	}

	@Override
	public void executeMain(boolean showSteps) {
		int period = getParameterValue("period");
		Rotate r = new Rotate(input, 1);
		r.execute(false);
		PeriodRow p = new PeriodRow(r.getOutput(), period);
		p.execute(false);
		r = new Rotate(p.getOutput(), 3);
		r.execute(false);
		
		output = r.getOutput();
		
		if (showSteps) {
			say("After applying column period " + period + ":");
			dump(output);
		}
		
	}
	
	public String toString() {
		return "PeriodColumn(" + getParameterValue("period") + ")";  
	}
	
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new PeriodColumn(list, 4);
		t.execute(true);
	}
	
	
	public static void main(String[] args) {
		test();
	}
	

}
