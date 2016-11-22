package com.zodiackillerciphers.transform.operations;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Merge extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] {
				new Parameter("symbol1", 0, alphabet.length() - 1),
				new Parameter("symbol2", 0, alphabet.length() - 1) };
	}

	public Merge(List<StringBuffer> input, Integer s1, Integer s2) {
		this.input = input;
		setParameterValue("symbol1", s1);
		setParameterValue("symbol2", s2);
	}
	public Merge(List<StringBuffer> input, Float s1, Float s2) {
		this.input = input;
		setParameterValue("symbol1", s1);
		setParameterValue("symbol2", s2);
	}

	@Override
	public void executeMain(boolean showSteps) {
		char s1 = fromInt(getParameterValue("symbol1"));
		char s2 = fromInt(getParameterValue("symbol2"));

		List<StringBuffer> result = new ArrayList<StringBuffer>();
		for (StringBuffer sb : input) {
			StringBuffer sbNew = new StringBuffer();
			for (int i = 0; i < sb.length(); i++) {
				char c = sb.charAt(i);
				if (c == s2)
					sbNew.append(s1);
				else
					sbNew.append(c);
			}
			result.add(sbNew);
		}
		if (showSteps) {
			say("After merging [" + s1 + "] with [" + s2 + "]:");
			dump(result);
		}
		output = result;
	}
	public String toString() {
		return "Merge(" + fromInt(getParameterValue("symbol1")) + ", " + fromInt(getParameterValue("symbol2")) + ")";  
	}

	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		Transformation t = new Merge(list, toInt('c'), toInt('+'));
		t.execute(true);
	}

	public static void main(String[] args) {
		test();
	}

}
