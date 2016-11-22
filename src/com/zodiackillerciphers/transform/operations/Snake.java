package com.zodiackillerciphers.transform.operations;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Snake extends TransformationBase {

	@Override
	public void setupParameters() {
	}
	
	public Snake(List<StringBuffer> input) {
		this.input = input;
	}

	@Override
	public void executeMain(boolean showSteps) {
		/**
		 * snake through the cipher text. start at upper left, read all the way
		 * to the right, then go down one line, and read to the left. continue
		 * alternating between left-to-right, and right-to-left directions.
		 */
		List<StringBuffer> result = new ArrayList<StringBuffer>();
		for (int i = 0; i < input.size(); i++) {
			StringBuffer sb = input.get(i);
			if (i % 2 == 0)
				result.add(new StringBuffer(sb));
			else
				result.add(new StringBuffer(sb).reverse());
		}
		if (showSteps) {
			say("After snaking:");
			dump(result);
		}
		output = result;
	}
	public String toString() {
		return "Snake()";    
	}
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new Snake(list);
		t.execute(true);
	}
	
	
	public static void main(String[] args) {
		test();
	}

}
