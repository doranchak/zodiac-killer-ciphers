package com.zodiackillerciphers.tests;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;
import com.zodiackillerciphers.transform.operations.FlipHorizontal;
import com.zodiackillerciphers.transform.operations.FlipVertical;
import com.zodiackillerciphers.transform.operations.Rotate;

/** statistical tests to see how often specific features appear in random shuffles */
public class FeaturesOrPhantoms {
	
	public static double[] resultsFrom(DescriptiveStatistics stat) { 
		// samples, min, max, mean, median, stddev
		double[] d = new double[6];
		int i=0;
		d[i++] = stat.getN();
		d[i++] = stat.getMin();
		d[i++] = stat.getMax();
		d[i++] = stat.getMean();
		d[i++] = stat.getPercentile(0.5);
		d[i++] = stat.getStandardDeviation();
		return d;
	}
	public static void dump(double[] stats) {
		System.out.println(Arrays.toString(stats));
	}
	public static void dump(DescriptiveStatistics stats) {
		dump(resultsFrom(stats));
	}
	
	public static void ngramRepeats(int n, String cipher) {
		DescriptiveStatistics stat = new DescriptiveStatistics(); 
		
		for (int i=0; i<1000000; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			NGramsBean bean = new NGramsBean(n, cipher);
			int reps = bean.numRepeats();
			stat.addValue(reps);
			if (i%100000 == 0) System.out.println(i+"...");
		}
		dump(stat);
	}
	public static void maxPeriodicBigramRepeats(String cipher) {
		DescriptiveStatistics stat = new DescriptiveStatistics(); 
		
		for (int i=0; i<100000; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			int max = 0;
			for (int period=1; period<=cipher.length()/2; period++) {
				String re = Periods.rewrite3(cipher, period);
				NGramsBean bean = new NGramsBean(2, re);
				int reps = bean.numRepeats();
				max = Math.max(max, reps);
			}
			if (i%1000 == 0) System.out.println(i+"...");
			stat.addValue(max);
		}
		dump(stat);
	}

	public static void maxPeriodicBigramRepeatsWithRotFlip(String cipher) {
		DescriptiveStatistics stat = new DescriptiveStatistics(); 
		
		for (int i=0; i<100000; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			
			String[] ciphers = new String[8];
			
			int j=0;
			for (int[] rotflip1 : Rotate.rotFlipOperations) {
				int rotate1 = rotflip1[0]/90;
				int flipH1 = rotflip1[1];
				int flipV1 = rotflip1[2];

				List<StringBuffer> list = TransformationBase.toList(cipher, 17);
				Rotate rot = new Rotate(list, rotate1);
				rot.execute();
				list = rot.getOutput();

				if (flipH1 > 0) {
					FlipHorizontal flip = new FlipHorizontal(list);
					flip.execute();
					list = flip.getOutput();
				}
				if (flipV1 > 0) {
					FlipVertical flip = new FlipVertical(list);
					flip.execute();
					list = flip.getOutput();
				}
				ciphers[j] = TransformationBase.fromList(list).toString();
				j++;
			}
			
			int max = 0;
			
			for (String cipher2 : ciphers) {
				for (int period=1; period<=cipher2.length()/2; period++) {
					String re = Periods.rewrite3(cipher2, period);
					NGramsBean bean = new NGramsBean(2, re);
					int reps = bean.numRepeats();
					max = Math.max(max, reps);
				}
			}
			//System.out.println(max);
			if (i%1000 == 0) System.out.println(i+"...");
			stat.addValue(max);
		}
		dump(stat);
	}
	
	/** non-zodiac cipher */
	public static void berengerSauniere() {
		// for resulting text lengths 
		DescriptiveStatistics statL = new DescriptiveStatistics(); 
		// for number of perforations 
		DescriptiveStatistics statP = new DescriptiveStatistics(); 
		String cipher = "";
		for (String line : BerengerSauniere.cipherInnerOnly)
			cipher += line;
		
		for (int i=0; i<1000000; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			String[] grid = new String[10];
			for (int row=0; row<10; row++) grid[row] = cipher.substring(row*20,row*20+20);
			//System.out.println(Arrays.toString(grid));
			String[] results = BerengerSauniere.perforate(grid);
			System.out.println(results[0].length() + " " + results[1]);
			
			if (results[0].length() == 64 && "25".equals(results[1])) {
				System.out.println("hit: " + Arrays.toString(grid));
			}
			statL.addValue(results[0].length());
			statP.addValue(Integer.valueOf(results[1]));
			//if (i%100000 == 0) System.out.println(i+"...");
		}
		dump(statL);
		dump(statP);
	}
	
	
	public static void main(String[] args) {
		//System.out.println(new NGramsBean(4, Ciphers.cipher[1].cipher).numRepeats());
//		ngramRepeats(3, Ciphers.cipher[1].cipher);
//		ngramRepeats(3, Ciphers.cipher[0].cipher);
//		ngramRepeats(4, Ciphers.cipher[1].cipher);
//		ngramRepeats(4, Ciphers.cipher[0].cipher);
		//maxPeriodicBigramRepeatsWithRotFlip(Ciphers.cipher[0].cipher);
//		maxPeriodicBigramRepeatsWithRotFlip(Ciphers.cipher[1].cipher);
		berengerSauniere();
//		String cipher = Ciphers.cipher[1].cipher;
//		int max = 0;
//		for (int period=1; period<=cipher.length()/2; period++) {
//			String re = Periods.rewrite3(cipher, period);
//			NGramsBean bean = new NGramsBean(2, re);
//			int reps = bean.numRepeats();
//			max = Math.max(max, reps);
//		}
//		System.out.println(max);
		
	}
	
}
