package com.zodiackillerciphers.transform.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.RepeatingFragments;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.Box;
import com.zodiackillerciphers.transform.Operations;
import com.zodiackillerciphers.transform.Parameter;
import com.zodiackillerciphers.transform.Point;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

import ec.util.MersenneTwisterFast;

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

		if (height > H || height < 0) {
			if (showSteps) say(this.toString() + ": height " + height + " > " + H + " or " + height + " < 0");
			return;
		}
		if (width > W || width < 0) {
			if (showSteps) say(this.toString() + ": width " + width + " > " + W + " or " + height + " < 0");
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
	
	/** try to find swap operation that maximizes mean sigma.  limit to full rows. */
	public static void meanSigmaTest() {
		String cipher = Ciphers.Z340;

		int total = 0;
		int width = 17;
		for (int row1=0; row1<19; row1++) {
			for (int row2=row1+1; row2<20; row2++) {
				for (int height=1; height<11; height++) {
					total++;
					List<StringBuffer> list = TransformationBase.toList(cipher, 17);
					Transformation swap = new Swap(list, row1*width, row2*width, height, width);
					swap.execute(false);
					String result = TransformationBase.fromList(swap.getOutput()).toString();
					System.out.println(HomophonesNew.meanSigma(result, false, false) + "	" + swap);
				};
			}
		}
		System.out.println(total);
	}
	/**
	 * try out random Swap operations to see if any of them cause spikes
	 * in mean L2 sigma
	 */
	public static void randomOperationsMeanSigma() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		//String cipher = Ciphers.Z340;
		// start with Swap(306, 85, 2, 17) Swap(187, 34, 3, 17) because it doubles mean L2 sigma 
		String cipher = "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKOBy:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()pp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|";
		List<StringBuffer> list = TransformationBase.toList(cipher, 17);
		while (true) {

			List<Transformation> ops = new ArrayList<Transformation>();
			for (int i = 0; i < rand.nextInt(4) + 1; i++) {
				int row1 = 1+rand.nextInt(19);
				int row2 = 1+rand.nextInt(19);
				int height = 1+rand.nextInt(10);
				int width = 17;
				Transformation t = new Swap(list, row1*width, row2*width, height, width);
				ops.add(t);
			}
			
			String desc = "";
			list = TransformationBase.toList(cipher, 17);
			for (Transformation op : ops) {
				op.setInput(list);
				op.execute();
				list = op.getOutput();
				desc += op.toString() + " ";
			}

			String result = TransformationBase.fromList(list).toString();
			double meansigma = HomophonesNew.meanSigma(result, false, false);
			if (meansigma > 0.42876895529129533)
				System.out.println(meansigma + "	" + desc);
		}
	}
	
	/** mean l2 sigma 0.5473754018875291 */
	public static void testBestSwap() {
		String cipher = Ciphers.Z340;
		List<StringBuffer> list = TransformationBase.toList(cipher, 17);
		Transformation t = new Swap(list, 306, 85, 2, 17);
		t.execute(true);
		t = new Swap(t.getOutput(), 187, 34, 3, 17);
		t.execute(true);
		t = new Swap(t.getOutput(), 34, 306, 1, 17);
		t.execute(true);
		t = new Swap(t.getOutput(), 238, 272, 1, 17);
		t.execute(true);
		t = new Swap(t.getOutput(), 0, 153, 1, 17);
		t.execute(true);
		t = new Swap(t.getOutput(), 68, 85, 1, 17);
		t.execute(true);
		cipher = TransformationBase.fromList(t.getOutput()).toString();
		System.out.println(HomophonesNew.meanSigma(cipher, false, true));
	}

	// start with cipher that has high mean l2 sigma score.  brute force row swaps to look for improvements.
	public static void bruteForceRowSwaps(int rowSize, boolean includeCoverage, boolean includeRepFragMeanProb) {
		System.out.println("rowSize " + rowSize);
		// mean sigma 0.5869620666091453 
		//String cipher = "++)WCzWcPOSHT/()p2<clRJ|*5T4M.+&BF>MDHNpkSzZO8A|K;+lGFN^f524b.cV4t++By:cM+UZGW()L#zHJp8R^FlO-*dCkF>2D(_9M+ztjd|5FP+&4k/d<M+b+ZR2FBcyA64KSpp7^l8*V3pO++RK2(G2Jfj#O+_NYz+@L9U+R/5tE|DYBpbTMKO|FkdW<7tB_YOB*-CcHER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L#5+Kq%;2UcXGV.zL|z69Sy#+N|5FBc(;8R-zlUV+^J+Op7<FBy-";
		String cipher = Ciphers.Z340;
		
		boolean hit = true;
		double bestMean = 0;
		String bestCipher = cipher;
		String bestOperations = "";
		while (hit) {
			System.out.println("Looking for new best...");
			hit = false;
			cipher = bestCipher;
			String currentBestOperation = null;
			for (int row1=0; row1<19; row1++) {
				for (int row2=row1+1; row2<20; row2++) {
					List<StringBuffer> list = TransformationBase.toList(cipher, 17);
					Transformation t = new Swap(list, row1*17, row2*17, rowSize, 17);
					t.execute(false);
					String result = TransformationBase.fromList(t.getOutput()).toString();
					//System.out.println(HomophonesNew.meanSigma(result, false, false) + "	" + row1 + "	" + row2 + "	" + t);
					double mean = HomophonesNew.meanSigma(result, false, false);
					mean = 1;
					double meanOriginal = mean;
					double coverage = 0;
					double prob = 0;
					if (includeCoverage) {
						// reward scores that have higher coverage
						NGramsBean ng = new NGramsBean(2, result);
						coverage = ng.coverage.size();
						coverage /= cipher.length();
						mean *= coverage;
					}
					if (includeRepFragMeanProb) {
						prob = RepeatingFragments.meanProbability(result);
						mean /= prob;
					}
					if (mean > bestMean) {
						String extra = "";
						if (includeCoverage) extra += "	" + meanOriginal+ "	" + coverage;
						if (includeRepFragMeanProb) extra += "	" + meanOriginal + "	" + prob;
						System.out.println("new best: " + mean + "	" + row1 + "	" + row2 + "	" + t +"	" + result + extra);
						hit = true;
						bestMean = mean;
						bestCipher = result;
						currentBestOperation = t.toString();
					}
				}
				
			}
			if (hit) bestOperations += currentBestOperation + " ";
			System.out.println("bestOperations " + bestOperations);
		}
		System.out.println("No more improvements found.");
	}
	/** trying to find row swap to improve cycles in jarvle's test cipher */
	public static void bruteForceRowSwaps2(int rowSize) {
		System.out.println("rowSize " + rowSize);
		// mean sigma 0.5869620666091453 
		//String cipher = "++)WCzWcPOSHT/()p2<clRJ|*5T4M.+&BF>MDHNpkSzZO8A|K;+lGFN^f524b.cV4t++By:cM+UZGW()L#zHJp8R^FlO-*dCkF>2D(_9M+ztjd|5FP+&4k/d<M+b+ZR2FBcyA64KSpp7^l8*V3pO++RK2(G2Jfj#O+_NYz+@L9U+R/5tE|DYBpbTMKO|FkdW<7tB_YOB*-CcHER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L#5+Kq%;2UcXGV.zL|z69Sy#+N|5FBc(;8R-zlUV+^J+Op7<FBy-";
		//String cipher = "+E7'D*!$3*.)HSF1$M&^*%Y6[ZU=($VQ,*$R+>)*'/KG$(*8V$BITC#X3>E14#]+H\\LX-3*=$)*@II^1HQ$[>!YP^<R\\U:T,<?$IO;#QK*5A\"$9I0&CE*^.+0D]M/JV$!K'*QR46)-4Z\"MI$8H1?9'?E&7^A?(!-B)\\V?Z+55A*Q_M9KX+G_/$2-4%E]R!;SFL)_<*8\"1I3&#N=#HL';Y$O0Z*F@[S!X.V\"':$*-^V\\:DK3UF,*E!8AB\"TQ$7K>[4=*[\"^\\H#$XME*T3C49+[]PG>-;%5#X*\\&N)T/RE1@?N3>2S\\IQ#]P$P'[JU*;O*:1@@K$*#$U:?RW2,Y*9E";
		String cipher = Ciphers.Z408;
		
		boolean hit = true;
		String bestCipher = cipher;
		String bestOperations = "";
		double bestScore = JarlveMeasurements.homScore(cipher);
		
		System.out.println("Starting score: " + bestScore);
		while (hit) {
			System.out.println("Looking for new best...");
			hit = false;
			cipher = bestCipher;
			String currentBestOperation = null;
			for (int row1=0; row1<19; row1++) {
				for (int row2=row1+1; row2<20; row2++) {
					List<StringBuffer> list = TransformationBase.toList(cipher, 17);
					Transformation t = new Swap(list, row1*17, row2*17, rowSize, 17);
					t.execute(false);
					String result = TransformationBase.fromList(t.getOutput()).toString();
					//System.out.println(HomophonesNew.meanSigma(result, false, false) + "	" + row1 + "	" + row2 + "	" + t);
					double score = JarlveMeasurements.homScore(result);
					if (score > bestScore) {
						System.out.println("new best: " + score + "	" + row1 + "	" + row2 + "	" + t +"	" + result);
						hit = true;
						bestScore = score;
						bestCipher = result;
						currentBestOperation = t.toString();
					}
				}
				
			}
			if (hit) bestOperations += currentBestOperation + " ";
			System.out.println("bestOperations " + bestOperations);
		}
		System.out.println("No more improvements found.");
	}
	/** trying to find column swaps to improve cycles in the given cipher */
	public static void bruteForceColumnSwaps(String cipher, int colSize, int width) {
		System.out.println("colSize " + colSize);
		
		boolean hit = true;
		String bestCipher = cipher;
		String bestOperations = "";
		double bestScore = JarlveMeasurements.homScore(cipher);
		int H = cipher.length()/width;
		
		System.out.println("Starting score: " + bestScore);
		while (hit) {
			System.out.println("Looking for new best...");
			hit = false;
			cipher = bestCipher;
			String currentBestOperation = null;
			for (int col1=0; col1<width; col1++) {
				for (int col2=col1+1; col2<20; col2++) {
					List<StringBuffer> list = TransformationBase.toList(cipher, width);
					Transformation t = new Swap(list, col1, col2, H, colSize);
					t.execute(false);
					String result = TransformationBase.fromList(t.getOutput()).toString();
					//System.out.println(HomophonesNew.meanSigma(result, false, false) + "	" + row1 + "	" + row2 + "	" + t);
					double score = JarlveMeasurements.homScore(result);
					if (score > bestScore) {
						System.out.println("new best: " + score + "	" + col1 + "	" + col2 + "	" + t +"	" + result);
						hit = true;
						bestScore = score;
						bestCipher = result;
						currentBestOperation = t.toString();
					}
				}
				
			}
			if (hit) bestOperations += currentBestOperation + " ";
			System.out.println("bestOperations " + bestOperations);
		}
		System.out.println("No more improvements found.");
	}
	/** jarlve's idea: reverse substrings until no more cycle improvements possible. */
	public static void bruteForceReversals(String cipher, int width) {
		
		boolean hit = true;
		String bestCipher = cipher;
		String bestOperations = "";
		double bestScore = JarlveMeasurements.homScore(cipher);
		
		System.out.println("Starting score: " + bestScore);
		while (hit) {
			System.out.println("Looking for new best...");
			hit = false;
			cipher = bestCipher;
			String currentBestOperation = null;
			for (int pos=0; pos<cipher.length()-2; pos++) {
				for (int len=17; pos+len<cipher.length(); len++) {
					List<StringBuffer> list = TransformationBase.toList(cipher, width);
					LinearSelection t1 = new LinearSelection(list, pos, len); 
					t1.execute(false);
					Reverse t2 = new Reverse(t1.getOutput());
					t2.execute(false);
					t2.setOutput(t1.replaceSelection(t2.getOutput()));
					
					//System.out.println(t2.getOutput());
					
					String result = TransformationBase.fromList(t2.getOutput()).toString();
					if (result.length() != cipher.length()) {
						System.err.println("Something went wrong: " + result);
						System.exit(-1);
					}
					//System.out.println(HomophonesNew.meanSigma(result, false, false) + "	" + row1 + "	" + row2 + "	" + t);
					double score = JarlveMeasurements.homScore(result);
					if (score > bestScore) {
						System.out.println("new best: " + score + "	" + pos + "	" + len + "	" + t1 +"	" + result);
						hit = true;
						bestScore = score;
						bestCipher = result;
						currentBestOperation = t1.toString();
					}
				}
				
			}
			if (hit) bestOperations += currentBestOperation + " ";
			System.out.println("bestOperations " + bestOperations);
		}
		System.out.println("No more improvements found.");
	}
	
	/**
	 * the given cipher is a re-arrangement of Z340 by row. print out the row
	 * ordering compared to the original.
	 */
	public static void printRowNumbers(String cipher) {
		String numbers = "";
		for (int row1=0; row1<20; row1++) {
			String sub1 = cipher.substring(row1*17,row1*17+17);
			for (int row2=0; row2<20; row2++) {
				String sub2 = Ciphers.Z340.substring(row2*17,row2*17+17);
				if (sub1.equals(sub2)) {
					numbers += row2 + " ";
				}
			}
		}
		System.out.println(numbers);
	}

	public static void main(String[] args) {
		//test();
		//countValidSwaps();
		//testSwap();
		//meanSigmaTest();
		//randomOperationsMeanSigma();
		//testBestSwap();
		//bruteForceRowSwaps2(1);
		//bruteForceReversals(Ciphers.Z408, 17);
		bruteForceReversals("+E7'D*!$3*.)HSF1$M&^*%Y6[ZU=($VQ,*$R+>)*'/KG$(*8V$BITC#X3>E14#]+H\\LX-3*=$)*@II^1HQ$[>!YP^<R\\U:T,<?$IO;#QK*5A\"$9I0&CE*^.+0D]M/JV$!K'*QR46)-4Z\"MI$8H1?9'?E&7^A?(!-B)\\V?Z+55A*Q_M9KX+G_/$2-4%E]R!;SFL)_<*8\"1I3&#N=#HL';Y$O0Z*F@[S!X.V\"':$*-^V\\:DK3UF,*E!8AB\"TQ$7K>[4=*[\"^\\H#$XME*T3C49+[]PG>-;%5#X*\\&N)T/RE1@?N3>2S\\IQ#]P$P'[JU*;O*:1@@K$*#$U:?RW2,Y*9E", 17);
		//bruteForceColumnSwaps("+E7'D*!$3*.)HSF1$M&^*%Y6[ZU=($VQ,*$R+>)*'/KG$(*8V$BITC#X3>E14#]+H\\LX-3*=$)*@II^1HQ$[>!YP^<R\\U:T,<?$IO;#QK*5A\"$9I0&CE*^.+0D]M/JV$!K'*QR46)-4Z\"MI$8H1?9'?E&7^A?(!-B)\\V?Z+55A*Q_M9KX+G_/$2-4%E]R!;SFL)_<*8\"1I3&#N=#HL';Y$O0Z*F@[S!X.V\"':$*-^V\\:DK3UF,*E!8AB\"TQ$7K>[4=*[\"^\\H#$XME*T3C49+[]PG>-;%5#X*\\&N)T/RE1@?N3>2S\\IQ#]P$P'[JU*;O*:1@@K$*#$U:?RW2,Y*9E", 1, 17);
		//printRowNumbers("RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p2<clRJ|*5T4M.+&BF(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64KHER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)z69Sy#+N|5FBc(;8R_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL||FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+lGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKOBy:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2");
	}

}
