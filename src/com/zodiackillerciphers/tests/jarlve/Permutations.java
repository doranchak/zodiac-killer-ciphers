package com.zodiackillerciphers.tests.jarlve;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;

public class Permutations {
	/* http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041 */
	
	public static int WIDTH = 17;
	/*
	public static void ResultBean jarlveDiagonalTranspositions(int rows, int cols, int startCorner,
			int advanceByRowOrCol, boolean snake)*/
	
	public static void permute(String cipher) {
		//	{noflip, flip}
		//		{rotate0, rotate90, rotate180, rotate270}
		//			{grid cols from 1 to 340}
		//				{rows = ceiling(L/cols)}
		//					{diag from 0 to 3}
		//					
		// total: 2 * 4 * 340 * 4 = 10,880

		int count = 0;
		
		//for (int period=1; period<=170; period++) {
		//	cipher = Periods.rewrite3(cipher, period);

		List<StringBuffer> grid = TransformationBase.toList(cipher, WIDTH);
		for (int flip = 0; flip < 2; flip++) {
			List<StringBuffer> step1 = CipherTransformations.flipHorizontal(grid, flip);
			for (int rotate = 0; rotate < 271; rotate += 90) {
				List<StringBuffer> step2 = CipherTransformations.rotate(step1,
						rotate);
				for (int width = 1; width <= 340; width++) {
					List<StringBuffer> step3 = TransformationBase.toList(
							step2, width);

					for (int period = 1; period < step3.size()/2; period++) {
							List<StringBuffer> step0 = null; //CipherTransformations
									//.periodRows(step3, period);

							for (int flip2 = 0; flip2 < 2; flip2++) {
								List<StringBuffer> step4 = CipherTransformations
										.flipHorizontal(step0, flip2);
								for (int rotate2 = 0; rotate2 < 271; rotate2 += 90) {
									List<StringBuffer> step5 = CipherTransformations
											.rotate(step4, rotate2);

									for (int diag = -1; diag < 4; diag++) {
										List<StringBuffer> step6;
										if (diag == -1)
											step6 = TransformationBase
													.copy(step5);
										else {
											//step6 = CipherTransformations
													//.diagonal(step5, diag);
											step6 = null;
										}

										String oneliner = TransformationBase
												.fromList(step6).toString();
										NGramsBean bean = new NGramsBean(2,
												oneliner);
										int num2 = bean.numRepeats();
										bean = new NGramsBean(3, oneliner);
										int num3 = bean.numRepeats();
										if (num2 > 40 || num3 > 4) {
											String intermediates = " ";
											intermediates += TransformationBase
													.fromList(step1) + " ";
											intermediates += TransformationBase
													.fromList(step2) + " ";
											intermediates += TransformationBase
													.fromList(step3);
											System.out.println(num2 + " "
													+ num3 + " " + flip + " " + rotate + " "
													+ width + " " + period
													+ " " +
													+ flip2 + " "
													+ rotate2 + " " + diag
													+ " " + oneliner
													+ intermediates);
										}

										/*
										 * if (oneliner.startsWith(">OpW%l+"))
										 * System.out.println("SMEG! " + num2 +
										 * " " + num3 + " " + flip + " " +
										 * rotate + " " +width + " " + diag +
										 * " " + flip2 + " " + rotate2 + " " +
										 * oneliner);
										 */

										count++;

									}
								}
							}
						}
					}
				
				// }
			}			
			
		}
		System.out.println("Tested " + count + " permutations.");
		
	}

	
	public static void test2() {
		String cipher = Ciphers.cipher[0].cipher;
		
		//List<StringBuffer> grid = CipherTransformations.olson(TransformationBase.toList(cipher, 17), 10, false);
		//for (StringBuffer sb : grid) System.out.println(sb);
		System.out.println();
	}
	
	public static void test() {
		String cipher = Ciphers.cipher[0].cipher;
		
		List<StringBuffer> grid = CipherTransformations.flipHorizontal(TransformationBase.toList(cipher, 17), 1);
		grid = TransformationBase.toList(grid, 14);
		grid = CipherTransformations.flipHorizontal(grid, 1);
		
		for (StringBuffer sb : grid) System.out.println(sb);
		System.out.println();

		//grid = CipherTransformations.diagonal(grid, 0);
		for (StringBuffer sb : grid) System.out.println(sb);
		System.out.println();
	}
	
	public static void main(String[] args) {
		permute(Ciphers.cipher[0].cipher);
		//test2();
	}
}
