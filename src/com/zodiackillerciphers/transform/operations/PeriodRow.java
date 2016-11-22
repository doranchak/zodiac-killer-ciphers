package com.zodiackillerciphers.transform.operations;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class PeriodRow extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] { new Parameter("period", 1, inputHeight()) };
	}

	public PeriodRow(List<StringBuffer> input, Integer period) {
		this.input = input;
		setParameterValue("period", period);
	}
	public PeriodRow(List<StringBuffer> input, Float period) {
		this.input = input;
		setParameterValue("period", period);
	}

	@Override
	public void executeMain(boolean showSteps) {
		int period = getParameterValue("period");
		List<StringBuffer> listNew = new ArrayList<StringBuffer>();
		for (int i = 0; i < input.size(); i++) {
			for (int j = i; j < input.size(); j += period) {
				if (listNew.size() == input.size()) {
					if (showSteps) {
						say("After applying row period " + period + ":");
						dump(listNew);
					}
					output = listNew;
					return;
				}
				listNew.add(new StringBuffer(input.get(j)));
			}
		}
		
		if (showSteps) {
			say("After applying row period " + period + ":");
			dump(listNew);
		}
		output = listNew;
	}
	public String toString() {
		return "PeriodRow(" + getParameterValue("period") + ")";  
	}
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new PeriodRow(list, 2);
		t.execute(true);
	}
	
	
	public static void main(String[] args) {
		test();
	}

}
