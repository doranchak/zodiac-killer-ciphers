package com.zodiackillerciphers.transform.operations;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Delete extends TransformationBase {

	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] {
				new Parameter("symbol", 0, alphabet.length() - 1),
				new Parameter("collapse", 0, 1) };
	}

	public Delete(List<StringBuffer> input, Integer symbol, Integer collapse) {
		this.input = input;
		setParameterValue("symbol", symbol);
		setParameterValue("collapse", collapse);
	}
	public Delete(List<StringBuffer> input, Float symbol, Float collapse) {
		this.input = input;
		setParameterValue("symbol", symbol);
		setParameterValue("collapse", collapse);
	}

	@Override
	public void executeMain(boolean showSteps) {

		char c = fromInt(getParameterValue("symbol"));
		boolean collapse = getParameterValue("collapse") == 1;

		StringBuffer sbNew = new StringBuffer();
		for (StringBuffer sb : input) {
			for (int i = 0; i < sb.length(); i++) {
				if (sb.charAt(i) == c) {
					if (!collapse)
						sbNew.append(' ');
				} else
					sbNew.append(sb.charAt(i));
			}
		}
		List<StringBuffer> result = toList(sbNew.toString(),
				input.get(0).length());

		if (showSteps) {
			String msg = "After removing symbol [" + c + "]";
			if (collapse)
				msg += " and removing spaces";
			else
				msg += " and keeping spaces.";
			say(msg);
			dump(result);
		}
		output = result;
	}
	
	public String toString() {
		return "Delete(" + fromInt(getParameterValue("symbol")) + ", " + getParameterValue("collapse") + ")";  
	}

	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		Transformation d = new Delete(list, toInt('+'), 0);
		d.execute(true);
		d = new Delete(list, toInt('+'), 1);
		d.execute(true);
	}

	public static void main(String[] args) {
		test();
	}
}
