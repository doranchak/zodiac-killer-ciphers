package com.zodiackillerciphers.transform.operations;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class FlipHorizontal extends TransformationBase {

	@Override
	public void setupParameters() {
	}

	public FlipHorizontal(List<StringBuffer> input) {
		this.input = input;
	}

	@Override
	public void executeMain(boolean showSteps) {
		/** flip horizontally. */
		int h = TransformationBase.height(input);
		List<StringBuffer> result = new ArrayList<StringBuffer>(h);
		for (int i = 0; i < h; i++) {
			StringBuffer sb = new StringBuffer(input.get(i)).reverse();
			result.add(sb);
		}

		if (showSteps) {
			say("After flipping horizontally:");
			dump(result);
		}

		output = result;
	}
	public String toString() {
		return "FlipHorizontal()";  
	}

	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		Transformation t = new FlipHorizontal(list);
		t.execute(true);
	}

	public static void main(String[] args) {
		test();
	}
}