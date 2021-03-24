package com.zodiackillerciphers.transform.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
				new Parameter("rkf", 0, 8191), // not used
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
		return "Quadrants(" + getParameterValue("i") + ", "
				+ getParameterValue("j") + ", " + getParameterValue("p") + ", "
				+ getParameterValue("c") + ", " + getParameterValue("rkf")
				+ " " + Arrays.toString(rkf(getParameterValue("rkf"))) + ")";
	}
	
	public static void test() {
		//testResult(17,9,1,15,0,0,180,0,0,1,0,0,4); // best 340
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new Quadrants(list, 17,9,1,15,0,0,2,0,0,1,0,0,4);
		t.execute(true);
	}
	
	/** this one makes 49 bigram repeats */
	public static void test49Bigrams() {
		//testResult(17,9,1,15,0,0,180,0,0,1,0,0,4); // best 340
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new Period(list, 19);
		t.execute(true);
		t = new Quadrants(t.getOutput(), 4, 4, 0, 4, 1, 2, 1, 2, 1, 0, 1, 0, 4);
		t.execute(true);
	}
	
	/** another simplified standalone version:
	 * a point (r,c) indicates the quadrant intersection.
	 * quadrants are then defined with upper left and lower right corners.
	 * coordinates are inclusive.
	 * 
	 * Q0: (0,0) - (R,C)
	 * Q1: (R,C+1) - (R,16)
	 * Q2: (R+1,0) - (19,C)
	 * Q3: (R+1,C+1) - (19,16)
	 * 
	 * input is streamed into quadrants in the specified order.
	 * 
	 * if reverse is true, then undo the transformation
	 * 
	 */
	public static String transform(String input, int r, int c, int[] order, int width, boolean reverse) {
		Map<Integer, Integer> xform = new HashMap<Integer, Integer>(); 
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<input.length(); i++) {
			sb.append(" ");
		}
		int i=0;
		for (int o : order) {
			int[] corners = cornersFor(o, r, c);
//			System.out.println(Arrays.toString(corners));
			for (int row=corners[0]; row<=corners[2]; row++) {
				for (int col=corners[1]; col<=corners[3]; col++) {
					int pos = row*width+col;
					if (reverse) {
						sb.setCharAt(i++, input.charAt(pos));
						
					} else {
						sb.setCharAt(pos, input.charAt(i++));
					}
//					System.out.println(row + "," + col + "," + pos + ": " + sb);
				}
			}
		}
		return sb.toString();
		
	}
	public static int[] cornersFor(int quadrant, int r, int c) {
		if (quadrant == 0) return new int[] {0,0,r,c};
		if (quadrant == 1) return new int[] {0,c+1,r,16};
		if (quadrant == 2) return new int[] {r+1,0,19,c};
		return new int[] {r+1,c+1,19,16};
	}
	
	public static void testTransform() {
		String input = Ciphers.Z408_SOLUTION.substring(0, 340);
		String q1 = transform(input, 10, 8, new int[] {0,1,2,3}, 17, false);
		String q2 = transform(q1, 10, 8, new int[] {0,1,2,3}, 17, true);
		System.out.println("transformed: " + q1);
		System.out.println("untransformed: " + q2);
	}
	/** make files of all possible quadrant transformations.  very large, so split based on prefixes, 
	 * then can post process the files to remove dupes. */
	public static void makeAll(String folder) {
		try {
			
			int fileNumber = 0;
			String filePrefix = "z340_quadrants_";
			String fileSuffix = ".txt";
			/** map cipher prefixes to files */
			Map<String, BufferedWriter> writers = new HashMap<String, BufferedWriter>(); 

			long total = 0;
			for (int i=0; i<20; i++) {
				for (int j=0; j<17; j++) {
					for (int k=0; k<2; k++) {
						for (int p=0; p<24; p++) {
							for (int r1=0; r1<4; r1++) {
								for (int r2=0; r2<4; r2++) {
									for (int r3=0; r3<4; r3++) {
										for (int r4=0; r4<4; r4++) {
											for (int f1=0; f1<2; f1++) {
												for (int f2=0; f2<2; f2++) {
													for (int f3=0; f3<2; f3++) {
														for (int f4=0; f4<2; f4++) {
															for (int c=0; c<5; c++) {
																List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
																String key = i + " " + j + " " + k + " " + p + " " + r1 + " " + r2 + " " + r3 + " " + r4 + " " + f1 + " " + f2 + " " + f3 + " " + f4 + " " + c;
																Transformation t = new Quadrants(list, i, j, k, p, r1,
																		r2, r3, r4, f1, f2, f3, f4, c);
																t.execute(false);
																StringBuffer cipher = TransformationBase.fromList(t.getOutput());
																String prefix = cipher.substring(0,2);
																BufferedWriter writer = writers.get(prefix);
																if (writer == null) {
																	File fout = new File(folder + "/" + filePrefix + (fileNumber++) + fileSuffix);
																	FileOutputStream fos = new FileOutputStream(fout);
																	writer = new BufferedWriter(new OutputStreamWriter(fos));
																	writers.put(prefix, writer);
																	System.out.println("Files open: " + fileNumber);
																}
																writer.write(key + " " + cipher);
																writer.newLine();
																total++;
																if (total % 10000 == 0) 
																	System.out.println(total + " ciphers written.");
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				
			}
			for (BufferedWriter writer : writers.values())
				writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
	}
	
	public static void makeRandom(int n) {
		
//					new Parameter("i", 0, inputHeight() - 1),
//					new Parameter("j", 0, inputWidth() - 1),
//		new Parameter("k", 0, 1),
//					new Parameter("p", 0, 23),
//					new Parameter("r1", 0, 3),
//					new Parameter("r2", 0, 3),
//					new Parameter("r3", 0, 3),
//					new Parameter("r4", 0, 3),
//					new Parameter("f1", 0, 1),
//					new Parameter("f2", 0, 1),
//					new Parameter("f3", 0, 1),
//					new Parameter("f4", 0, 1),
//					new Parameter("c", 0, 4) };
		
//		public Quadrants(List<StringBuffer> input, int i, int j, int k, int p,
//				int r1, int r2, int r3, int r4, int f1, int f2, int f3, int f4,
//				int c) {
		
		Set<String> seen = new HashSet<String>();
		Random rand = new Random();
		int x = 0;
		while (true) {
			List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
			int i = rand.nextInt(20);
			int j = rand.nextInt(17);
			int k = rand.nextInt(2);
			int p = rand.nextInt(24);
			int r1 = rand.nextInt(4);
			int r2 = rand.nextInt(4);
			int r3 = rand.nextInt(4);
			int r4 = rand.nextInt(4);
			int f1 = rand.nextInt(2);
			int f2 = rand.nextInt(2);
			int f3 = rand.nextInt(2);
			int f4 = rand.nextInt(2);
			int c = rand.nextInt(5);
			String key = i + " " + j + " " + k + " " + p + " " + r1 + " " + r2 + " " + r3 + " " + r4 + " " + f1 + " " + f2 + " " + f3 + " " + f4 + " " + c;
			Transformation t = new Quadrants(list, i,j,k,p,r1,r2,r3,r4,f1,f2,f3,f4,c);
			t.execute(false);
			StringBuffer cipher = TransformationBase.fromList(t.getOutput());
			if (seen.contains(cipher.toString())) continue;
			seen.add(cipher.toString());
			System.out.println(key+" "+cipher);
			x++;
			if (x==n) return;
		}
	}
	
	public static void main(String[] args) {
		test();
//		test49Bigrams();
//		testTransform();
		//testRkf();
//		makeRandom(1000000);
//		makeAll("/Volumes/Smeggabytes/projects/zodiac/quadrants-all-permutations");
	}
}
