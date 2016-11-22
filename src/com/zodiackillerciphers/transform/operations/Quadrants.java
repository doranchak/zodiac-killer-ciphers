package com.zodiackillerciphers.transform.operations;

import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Quadrants extends TransformationBase {
	static String[] concats = new String[] {"((A concat-down B) concat-down C) concat-down D",
			"(A concat-right-top B) concat-down (C concat-right-top D)",
			"(A concat-right-bottom B) concat-down (C concat-right-bottom D)",
			"((A concat-right-top B) concat-right-top C) concat-right-top D",
			"((A concat-right-bottom B) concat-right-bottom C) concat-right-bottom D"};
	@Override
	public void setupParameters() { 
		this.parameters = new Parameter[] {
				new Parameter("i", 0, inputHeight() - 1),
				new Parameter("j", 0, inputWidth() - 1),
				new Parameter("p", 0, 23),
				new Parameter("k", 0, 1),
				new Parameter("r1", 0, 3),
				new Parameter("r2", 0, 3),
				new Parameter("r3", 0, 3),
				new Parameter("r4", 0, 3),
				new Parameter("f1", 0, 1),
				new Parameter("f2", 0, 1),
				new Parameter("f3", 0, 1),
				new Parameter("f4", 0, 1),
				new Parameter("rkf", 0, 8191),
				new Parameter("c", 0, 4) };

	}

	public Quadrants(List<StringBuffer> input, int i, int j, int k, int p,
			int r1, int r2, int r3, int r4, int f1, int f2, int f3, int f4,
			int c) {
		this.input = input;
		setParameterValue("i", i);
		setParameterValue("j", j);
		setParameterValue("k", k);
		setParameterValue("p", p);
		setParameterValue("r1", r1);
		setParameterValue("r2", r2);
		setParameterValue("r3", r3);
		setParameterValue("r4", r4);
		setParameterValue("f1", f1);
		setParameterValue("f2", f2);
		setParameterValue("f3", f3);
		setParameterValue("f4", f4);
		setParameterValue("c", c);
	}
	public Quadrants(List<StringBuffer> input, Integer i, Integer j, Integer p, Integer c, Integer rkf) {
		this.input = input;
		
		int[] array = rkf(rkf);
		//return new int[] {r0, r1, r2, r3, k, f0, f1, f2, f3};
		int r1 = array[0];
		int r2 = array[1];
		int r3 = array[2];
		int r4 = array[3];
		int k = array[4];
		int f1 = array[5];
		int f2 = array[6];
		int f3 = array[7];
		int f4 = array[8];
		
		setParameterValue("i", i);
		setParameterValue("j", j);
		setParameterValue("k", k);
		setParameterValue("p", p);
		setParameterValue("r1", r1);
		setParameterValue("r2", r2);
		setParameterValue("r3", r3);
		setParameterValue("r4", r4);
		setParameterValue("f1", f1);
		setParameterValue("f2", f2);
		setParameterValue("f3", f3);
		setParameterValue("f4", f4);
		setParameterValue("c", c);

	}
	
	public Quadrants(List<StringBuffer> input, Float i, Float j, Float p, Float c, Float rkf) {
		this.input = input;

		setParameterValue("rkf", rkf); // force conversion from float to int
		int[] array = rkf(getParameterValue("rkf"));
		//return new int[] {r0, r1, r2, r3, k, f0, f1, f2, f3};
		int r1 = array[0];
		int r2 = array[1];
		int r3 = array[2];
		int r4 = array[3];
		int k = array[4];
		int f1 = array[5];
		int f2 = array[6];
		int f3 = array[7];
		int f4 = array[8];
		
		setParameterValue("i", i);
		setParameterValue("j", j);
		setParameterValue("k", k);
		setParameterValue("p", p);
		setParameterValue("r1", r1);
		setParameterValue("r2", r2);
		setParameterValue("r3", r3);
		setParameterValue("r4", r4);
		setParameterValue("f1", f1);
		setParameterValue("f2", f2);
		setParameterValue("f3", f3);
		setParameterValue("f4", f4);
		setParameterValue("c", c);

	}
	

	@Override
	public void executeMain(boolean showSteps) {

		int i = getParameterValue("i");
		int j = getParameterValue("j");
		int k = getParameterValue("k");
		int p = getParameterValue("p");
		int r1 = getParameterValue("r1");
		int r2 = getParameterValue("r2");
		int r3 = getParameterValue("r3");
		int r4 = getParameterValue("r4");
		int f1 = getParameterValue("f1");
		int f2 = getParameterValue("f2");
		int f3 = getParameterValue("f3");
		int f4 = getParameterValue("f4");
		int c = getParameterValue("c");

		List<StringBuffer> result = TransformationBase.copy(input);
		com.zodiackillerciphers.old.Quadrants q = new com.zodiackillerciphers.old.Quadrants();
		result = toList(q.makeCipher(fromList(result, false).toString(), inputWidth(),
				i, j, k, p, r1, r2, r3, r4, f1, f2, f3, f4, c, showSteps),
				inputWidth());

		if (showSteps) {
			say("After concatenation operations [" + concats[c] + "]:");
			dump(result);
		}
		output = result;
	}
	
	/** get rotate, flip, and k values from the given binary-encoded integer */
	public static int[] rkf(int val) {
		// format:  r0 r0 r1 r1 r2 r2 r3 r3 k f0 f1 f2 f3
		
		int f3 = val & 1; val >>= 1;
		int f2 = val & 1; val >>= 1;
		int f1 = val & 1; val >>= 1;
		int f0 = val & 1; val >>= 1;
		int k = val & 1; val >>= 1;
		int r3 = val & 1 + (val & 2); val >>= 2;
		int r2 = val & 1 + (val & 2); val >>= 2;
		int r1 = val & 1 + (val & 2); val >>= 2;
		int r0 = val & 1 + (val & 2); val >>= 2;
		
		return new int[] {r0, r1, r2, r3, k, f0, f1, f2, f3};
	}

	/** get binary-encoded integer from given rotate, flip, and k values */
	public static int rkf(int[] val) {
		// format:  r0 r0 r1 r1 r2 r2 r3 r3 k f0 f1 f2 f3
		
		String binary = "";
		for (int i = 0; i<val.length; i++) {
			if (i < 4)
				binary += String.format("%2s", Integer.toBinaryString(val[i])).replace(" ", "0");
			else
				binary += Integer.toBinaryString(val[i]);
		}
		return Integer.parseInt(binary, 2);
		
	}
	
	public static void testRkf() {
		for (int val=0; val<8192; val++) {
			System.out.println(val + ": " + Integer.toBinaryString(val) + ": " + Arrays.toString(rkf(val)) + ": " + rkf(rkf(val)));
			if (val != rkf(rkf(val))) throw new RuntimeException("BROKEN!");
		}
	}
	public String toString() {
		return "Quadrants(" + getParameterValue("i") + ", " + getParameterValue("j") + ", " + getParameterValue("p") + ", " + getParameterValue("c") + ", " + getParameterValue("rkf") + " " + Arrays.toString(rkf(getParameterValue("rkf"))) + ")";    
	}
	
	public static void test() {
		//testResult(17,9,1,15,0,0,180,0,0,1,0,0,4); // best 340
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new Quadrants(list, 17,9,1,15,0,0,2,0,0,1,0,0,4);
		t.execute(true);
	}
	
	
	public static void main(String[] args) {
		test();
		//testRkf();
	}
}
