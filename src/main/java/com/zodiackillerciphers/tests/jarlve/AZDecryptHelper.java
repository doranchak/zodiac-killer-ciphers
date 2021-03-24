package com.zodiackillerciphers.tests.jarlve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;

public class AZDecryptHelper {
	
	/** process transposition experiment results into azdecrypt format */
	public static void processResults(String path, int generation) {
		System.out.println("results_sub_directory=with_meta_information");
		System.out.println();
		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith(generation + " archive size")) {
					
					int c = line.lastIndexOf(']') + 2;
					String cipher = line.substring(c);
					String cipherRev = new StringBuffer(cipher).reverse().toString();
					
					int a = line.indexOf("]")+2;
					int b = line.lastIndexOf('[')-1;
					
					String info = line.substring(a,b);
					System.out.println("cipher_information="+info);
					
					List<StringBuffer> list = TransformationBase.toList(cipher, 17);
					TransformationBase.dump(list);
					System.out.println();
					System.out.println("cipher_information="+info + " (reversed)");
					list = TransformationBase.toList(cipherRev, 17);
					TransformationBase.dump(list);
					System.out.println();
				}
			}
			//System.out.println("read " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/** output all words found in the azdecrypt results files in the given directory */
	public static void findWordsIn(String path) {
		WordFrequencies.init();
		File dir = new File(path);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".txt")) {
				if (file.getName().endsWith("group_list.txt")) continue;
				//System.out.println(file.getPath());
				List<String> lines = FileUtil.loadFrom(file.getPath());
				String plaintext = "";
				int i = 4;
				while (true) {
					String line = lines.get(i++);
					if (line.length() == 0) {
						break;
					}
					plaintext += line;
				}
				WordFrequencies.findAllWordsIn(plaintext, 4);
			}
		}
	}
	
	/** come up with shuffled ciphers that have the same bigram/trigram count as high-scoring
	 * ciphers in azdecrypt.
	 */
	public static void shuffleForAzDecrypt() {
		String cipher = Ciphers.cipher[0].cipher;
		cipher = CipherTransformations.shuffle(cipher);
		StringBuffer sb = new StringBuffer(cipher);
		
		int n2 = 0;
		int n3 = 0;
		
		int n2target = 48;
		int n3target = 6;
		
		StringBuffer sbNew = null;
		while (true) {
			if (n2 == n2target && n3 == n3target) break;
			int a =(int)(Math.random()*cipher.length());
			int b = a;
			while (a==b) b=(int)(Math.random()*cipher.length());
			sbNew = new StringBuffer(sb);
			char tmp = sbNew.charAt(b);
			sbNew.setCharAt(b, sbNew.charAt(a));
			sbNew.setCharAt(a, tmp);
			NGramsBean bean2 = new NGramsBean(2, sbNew.toString());
			NGramsBean bean3 = new NGramsBean(3, sbNew.toString());
			int r2 = bean2.numRepeats();
			int r3 = bean3.numRepeats();
			if (r2 > n2target || r3 > n3target) continue;
			if (r2 <= n2 && r3 <= n3) continue;
			sb = sbNew;
			n2 = r2;
			n3 = r3;
			//System.out.println("n2 " + n2 + " n3 " + n3);
		}
		System.out.println(sbNew);
		
	}
	
	/** make random shuffles for azdecrypt, to compute mean and std dev of scores.
	 * hoping this will help determine significance of any spikes in scores found during solves of z340.
	 **/
	public static void shuffleZ340() {
		String cipher = Ciphers.cipher[0].cipher;
		for (int i=0; i<10000; i++) {
			System.out.println("cipher_information=shuffle" + i);
			
			for (StringBuffer sb : TransformationBase.toList(CipherTransformations.shuffle(cipher), 17)) {
				System.out.println(sb);
			}
			System.out.println();
		}
	}
	
	public static void test() {
		processResults("/Users/doranchak/projects/zodiac/log-transform-12", 332500);
	}
	
	public static void main(String[] args) {
		//test();
		shuffleZ340();
		//for (int i=0; i<100; i++) shuffleForAzDecrypt();
		/*findWordsIn("/Users/doranchak/Downloads/az099/Results/with_meta_information_1");
		findWordsIn("/Users/doranchak/Downloads/az099/Results/with_meta_information_2");
		findWordsIn("/Users/doranchak/Downloads/az099/Results/with_meta_information");*/
	}
	
}
