package com.zodiackillerciphers.transform.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.old.Quadrants;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Rotate extends TransformationBase {

	/** this list of rotation, flipH and flipV values covers all possible ways of rotating and flipping.
	 * redundant symmetries have been removed.
	 */
	public static int[][] rotFlipOperations = new int[][] {
		{0,0,0}, {0,0,1}, {0,1,0}, {0,1,1}, {90,0,0}, {90,0,1}, {90,1,0}, {90,1,1}
	};
	
	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] { new Parameter("degrees", 0, 3) };
	}

	public Rotate(List<StringBuffer> input, Integer degrees) {
		this.input = input;
		setParameterValue("degrees", degrees);
	}
	public Rotate(List<StringBuffer> input, Float degrees) {
		this.input = input;
		setParameterValue("degrees", degrees);
	}

	@Override
	public void executeMain(boolean showSteps) {
		int deg = getParameterValue("degrees");
		if (deg == 0) {
			output = copy(input);
			if (showSteps) say(this.toString() + ": Nothing need be done.");
			return;
		}
		
		if (deg == 1) deg = 90;
		else if (deg == 2) deg = 180;
		else if (deg == 3) deg = 270;
		
		List<StringBuffer> result = null;

		int h = TransformationBase.height(input);
		int w = TransformationBase.width(input);

		if (deg == 90) {
			result = new ArrayList<StringBuffer>(width(input));
			for (int i = 0; i < w; i++) {
				StringBuffer sb = new StringBuffer();
				for (int j = h - 1; j >= 0; j--) {
					sb.append(input.get(j).charAt(i));
				}
				result.add(sb);
			}
		} else if (deg == 180) {
			result = new ArrayList<StringBuffer>(TransformationBase.width(input));
			for (int i = h - 1; i >= 0; i--) {
				StringBuffer sb = new StringBuffer();
				for (int j = w - 1; j >= 0; j--) {
					sb.append(input.get(i).charAt(j));
				}
				result.add(sb);
			}
		} else if (deg == 270) {
			result = new ArrayList<StringBuffer>(TransformationBase.width(input));
			for (int i = w - 1; i >= 0; i--) {
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < h; j++) {
					sb.append(input.get(j).charAt(i));
				}
				result.add(sb);
			}
		}
		if (showSteps) {
			say("After rotating " + deg + " degrees:");
			dump(result);
		}
		output = result;
	}

	public String toString() {
		return "Rotate(" + getParameterValue("degrees") + ")";    
	}
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		
		Integer deg = 1;
		Transformation t = new Rotate(list, deg);
		t.execute(true);
		deg = 2;
		t = new Rotate(list, deg);
		t.execute(true);
		deg = 3;
		t = new Rotate(list, deg);
		t.execute(true);
		deg = 0;
		t = new Rotate(list, deg);
		t.execute(true);
	}
	
	/** which rotate/flip combinations are equivalent? */
	public static void findEquivalencies() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> list = TransformationBase.toList(cipher,  17);
		
		Map<String, String> map = new HashMap<String, String>();
		
		
		for (int rotate=0; rotate<=3; rotate++) {
			Rotate rot = new Rotate(list, rotate);
			rot.execute();
			list = rot.getOutput();
			for (int flipH=0; flipH<=1; flipH++) {
				if (flipH>0) {
					FlipHorizontal flip = new FlipHorizontal(list);
					flip.execute();
					list = flip.getOutput();
				}
				for (int flipV=0; flipV<=1; flipV++) {
					if (flipV>0) {
						FlipVertical flip = new FlipVertical(list);
						flip.execute();
						list = flip.getOutput();
					}

					String val = (rotate*90) + " " + flipH + " " + flipV;
					String key = TransformationBase.fromList(list).toString();
					System.out.println(val + " " + key);
					if (map.containsKey(key)) {
						System.out.println(val + " is equivalent to " + map.get(key) + ".  " + key);
					} else map.put(key, val);
					
				}
			}
		}
	}

	public static void main(String[] args) {
		//test();
		findEquivalencies();
	}

}
