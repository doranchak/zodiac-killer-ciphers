package com.zodiackillerciphers.transform.operations;

import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.Box;
import com.zodiackillerciphers.transform.Operations;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Point;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Swap extends TransformationBase {

	public boolean success = false;
	
	@Override
	public void setupParameters() {
		this.parameters = new Parameter[] {
				new Parameter("pos1", 0, inputLength() - 1),
				new Parameter("pos2", 0, inputLength() - 1),
				new Parameter("height", 1, inputHeight()),
				new Parameter("width", 1, inputWidth()) };
	}

	public Swap(List<StringBuffer> input, Integer pos1, Integer pos2, Integer height,
			Integer width) {
		this.input = input;
		setParameterValue("pos1", pos1);
		setParameterValue("pos2", pos2);
		setParameterValue("height", height);
		setParameterValue("width", width);
	}
	public Swap(List<StringBuffer> input, Float pos1, Float pos2, Float height,
			float width) {
		this.input = input;
		setParameterValue("pos1", pos1);
		setParameterValue("pos2", pos2);
		setParameterValue("height", height);
		setParameterValue("width", width);
	}

	@Override
	public void executeMain(boolean showSteps) {
		/**
		 * exchanges the text within the two given rectangular regions. each
		 * rectangular region is specified by their upper left corners, and the
		 * given width and height.
		 **/
		
		try {
		List<StringBuffer> result = TransformationBase.copy(input);
		int H = inputHeight();
		int W = inputWidth();

		int pos1 = getParameterValue("pos1");
		int pos2 = getParameterValue("pos2");
		int height = getParameterValue("height");
		int width = getParameterValue("width");

		int row1 = rowFromPos(pos1);
		int col1 = colFromPos(pos1);
		int row2 = rowFromPos(pos2);
		int col2 = colFromPos(pos2);

		if (row1 < 0 || row2 < 0) {
			if (showSteps) say(this.toString() + ": rows: one is less than 0: " + row1 + " or " + row2);
			return;
		}
		if (row1 >= H || row2 >= H) {
			if (showSteps) say(this.toString() + ": rows: one is >= " + H + ": " + row1 + " or " + row2);
			return;
		}

		if (col1 < 0 || col1 < 0) {
			if (showSteps) say(this.toString() + ": cols: one is less than 0: " + col1 + " or " + col2);
			return;
		}
		if (col1 >= W || col2 >= W) {
			if (showSteps) say(this.toString() + ": cols: one is >= " + W + ": " + col1 + " or " + col2);
			return;
		}

		if (height >= H || height < 0) {
			if (showSteps) say(this.toString() + ": height " + height + " >= " + H + " or " + height + " < 0");
			return;
		}
		if (width >= W || width < 0) {
			if (showSteps) say(this.toString() + ": width " + width + " >= " + W + " or " + height + " < 0");
			return;
		}
		if (row1 + height > H) {
			if (showSteps) say(this.toString() + ": row1 " + row1 + " + height " + height + " > H " + H);
			return;
		}
		if (row2 + height > H) {
			if (showSteps) say(this.toString() + ": row2 " + row2 + " + height " + height + " > H " + H);
			return;
		}
		if (col1 + width > W) {
			if (showSteps) say(this.toString() + ": col1 " + col1 + " + width " + width + " > W " + W);
			return;
		}
		if (col2 + width > W) {
			if (showSteps) say(this.toString() + ": col2 " + col2 + " + width " + width + " > W " + W);
			return;
		}

		Box box1 = new Box(new Point(row1, col1), new Point(row1 + height - 1,
				col1 + width - 1));
		Box box2 = new Box(new Point(row2, col2), new Point(row2 + height - 1,
				col2 + width - 1));
		if (overlap(box1, box2)) {
			if (showSteps) say(this.toString() + ": boxes overlap.");
			
			return;
		}

		List<StringBuffer> extract1 = TransformationBase.extractBox(input,
				row1, col1, height, width, showSteps);
		List<StringBuffer> extract2 = TransformationBase.extractBox(input,
				row2, col2, height, width, showSteps);
		
		if (showSteps) {
			say("First box:");
			dump(extract1);
			say("Second box:");
			dump(extract2);
			
			List<StringBuffer> showBoxes = copy(input);
			List<StringBuffer> showBox1 = copy(extract1);
			List<StringBuffer> showBox2 = copy(extract2);
			fill(showBox1, '_');
			fill(showBox2, '=');
			showBoxes = replaceBox(showBoxes, showBox1, row1, col1);
			showBoxes = replaceBox(showBoxes, showBox2, row2, col2);
			say("Both boxes in grid:");
			dump(showBoxes);
			
		}

		List<StringBuffer> result1 = replaceBox(result, extract1, row2, col2);
		List<StringBuffer> result2 = replaceBox(result1, extract2, row1, col1);

		output = result2;

		if (showSteps) {
			say("Swap pos1 [" + pos1 + "] row1 [" + row1 + "] col1 [" + col1 + "] pos2 [" + pos2 + "] row2 [" + row2 + "] col2 [" + col2 + "] height [" + height + "] width [" + width + "]:");
			dump(output);
		}
		
		success = true;
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output == null) output = copy(input); // in case of early return, pass the input along as output.
			
		}
	}
	public String toString() {
		return "Swap(" + getParameterValue("pos1") + ", " + getParameterValue("pos2") + ", " + getParameterValue("height") + ", " + getParameterValue("width") + ")";    
	}
	
	/** count all possible valid swaps.
	 * 
	 * all possible swaps, including invalid, equals: 340*340*20*17 = 39,304,000
	 *  
	 *  */
	public static void countValidSwaps() {
		int hits = 0;
		int total = 0;
		
		/** experiment 1 */
		List<StringBuffer> list = TransformationBase.toList(
				"H+M8|CV@Kz/JNbVM)|DR(UVFFz9<Ut*5cZG+kNpOGp+2|G++|TB4-R)Wk^D(+4(5J+JYM(+|TC7zPYLR/8KjROp+8y.LWBO|<z29^%OF7TBlXz6PYALKJp+l2_cFKzF*K<SBKG)y7t-cYAy29^4OFT-+dpclddG+4Ucy5C^W(cMEB+*5k.L-RR+4>f|pFH>#Z3P>Ldl5||.UqL+dpl%WO&D(MVE5FV52cW<SVW)+k#2b^D4ct+c+ztZk.#Kp+fZ+B.;+B31c_81*H_Rq#2pb&RG1BCOO|TfSMF;+B<MF6N:(+H*;2BpzOUNyBO<Sf9pl/CN:^j*Xz6-+l#2E.B)>", 17);
		
		/** experiment 2 */
		list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);

		for (int pos1 = 0; pos1<340; pos1++) {
			for (int pos2 = 0; pos2<340; pos2++) {
				for (int H=1; H<=20; H++) {
					for (int W=1; W<=17; W++) {
						Swap d = new Swap(list, pos1, pos2, H, W);
						d.execute(false);
						total++;
						if (d.success) {
							hits++;
						}
						String xform = TransformationBase.fromList(d.output).toString();
						/** first experiment 
						NGramsBean bean = new NGramsBean(8, xform);
						int num = bean.numRepeats();
						if (num > 0) System.out.println(num + " " + pos1 + " " + pos2 + " " + H + " " + W);
						if (total % 10000 == 0) System.out.println(hits + " out of " + total);
						*/
						
						/** second experiment */
						float non = JarlveMeasurements.nonrepeatAlternate(xform);
						if (non < 1500)
							System.out.println(non + " " + pos1 + " " + pos2 + " " + H + " " + W);
						
					}
					
				}
				
			}
			
		}
		
		
	}
	
	public static void testSwap() {
		//List<StringBuffer> list = TransformationBase.toList(
			//	"H+M8|CV@Kz/JNbVM)|DR(UVFFz9<Ut*5cZG+kNpOGp+2|G++|TB4-R)Wk^D(+4(5J+JYM(+|TC7zPYLR/8KjROp+8y.LWBO|<z29^%OF7TBlXz6PYALKJp+l2_cFKzF*K<SBKG)y7t-cYAy29^4OFT-+dpclddG+4Ucy5C^W(cMEB+*5k.L-RR+4>f|pFH>#Z3P>Ldl5||.UqL+dpl%WO&D(MVE5FV52cW<SVW)+k#2b^D4ct+c+ztZk.#Kp+fZ+B.;+B31c_81*H_Rq#2pb&RG1BCOO|TfSMF;+B<MF6N:(+H*;2BpzOUNyBO<Sf9pl/CN:^j*Xz6-+l#2E.B)>", 17);
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation d = new Swap(list, 215, 85, 5, 6);
		d.execute(true);
		Operations o = new Operations();
		System.out.println(Arrays.toString(o.measure(TransformationBase.fromList(d.getOutput()).toString())));
		
	}
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(
				Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation d = new Swap(list, 35, 200, 5, 4);
		d.execute(true);
	}

	public static void main(String[] args) {
		//test();
		//countValidSwaps();
		testSwap();
	}

}
