package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Period extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] { new Parameter("period", 1,
				Math.max(1, inputLength() / 2)) };
	}

	public Period(List<StringBuffer> input, Integer period) {
		this.input = input;
		setParameterValue("period",period);
	}
	public Period(List<StringBuffer> input, Float period) {
		this.input = input;
		setParameterValue("period",period);
	}
	/** rewrite the cipher text in the given box by applying the given period (to convert periodic ngrams to normal ngrams) */ 
	@Override
	public void executeMain(boolean showSteps) {
		int period = getParameterValue("period");
		String rewritten = Periods.rewrite3(fromList(input, false).toString(), period);
		List<StringBuffer> result = toList(rewritten, input.get(0).length());
		if (showSteps) {
			say("After applying period " + period + ": ");
			dump(result);
		}
		output = result;
	}
	public String toString() {
		return "Period(" + getParameterValue("period") + ")";  
	}
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		Transformation t = new Period(list, 2);
		t.execute(true);
		NGramsBean bean = new NGramsBean(2, fromList(t.getOutput()).toString());
		System.out.println(bean.numRepeats());
	}
	
	
	public static void main(String[] args) {
		test();
	}

}
