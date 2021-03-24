package com.zodiackillerciphers.tests.jarlve;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.transform.Operations;
import com.zodiackillerciphers.transform.TransformationBase;

import ec.Individual;

/** http://www.zodiackillersite.com/viewtopic.php?p=43792#p43792 */
public class AZDecryptInterface {
	
	/** write the ciphers to the given path, then wait for the "finished.txt" file to appear in the output. */  
	public static List<AZResultBean> process(Individual[] inds, String inputPath, String outputPath) {
		//if (ciphers == null || ciphers.isEmpty() || inputPath == null || outputPath == null) return null;
		
		List<String> lines = new ArrayList<String>();
		lines.add("results_sub_directory=evolve");
		lines.add("");
		
		for (int i=0; i<inds.length; i++) {
			Operations op = (Operations) inds[i];
			//System.out.println("SMEG i=" + i + ", op hash " + op.hashCode());
			String cipher = op.outputString;
			lines.add("cipher_information=" + i);
			List<StringBuffer> grid = TransformationBase.toList(cipher, 17); 
			for (StringBuffer sb : grid) lines.add(sb.toString().replaceAll(" ", "")); // remove spaces to avoid confusing azdecrypt
			lines.add("");
		}
		
		FileUtil.writeText(inputPath + "/tmp.txt", lines);
		FileUtil.rename(inputPath + "/tmp.txt", inputPath + "/1.txt");

		File finished = new File(outputPath + "/finished_1.txt");
		while (!finished.exists()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		finished.delete();
		
		//System.out.println("Done!");
		return process(inds, outputPath + "/evolve");
	}
	public static List<AZResultBean> process(Individual[] inds, String outputPath) {
		if (outputPath == null) return null;
		List<AZResultBean> beans = new ArrayList<AZResultBean>();
		File dir = new File(outputPath);
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".txt")) {
				if (file.getName().equals("group_list.txt")) continue;
				
				AZResultBean bean = new AZResultBean();
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				bean.setNumber(Integer.valueOf(lines.get(0)));
				Operations op = ((Operations) inds[bean.getNumber()]);
				bean.setOperations(op.operationsActualSequenceFull);
				//System.out.println("SMEG after processing: i=" + bean.getNumber() + ", op hash " + op.hashCode());
				op.azBean = bean;
				String vals = lines.get(2);
				String[] split = vals.split(" ");
				bean.setScore(Float.valueOf(split[1]));
				//bean.setIoc(Integer.valueOf(split[3]));
				//bean.setM(Integer.valueOf(split[5]));
				
				boolean plain = true;
				String p = "";
				String c = "";
				for (int i=4; i<lines.size(); i++) {
					String line = lines.get(i);
					if (line.length() == 0) {
						plain = false;
						continue;
					}
					if (plain) p += line;
					else c += line;
				}
				bean.setPlaintext(p);
				bean.setCiphertext(c);
				if (!op.outputString.equals(c)) {
					System.out.println("DIFFERENT CIPHER " + bean + " vs original " + op.outputString);
				}
				beans.add(bean);
				file.delete();
			}
		}
		//System.exit(-1);
		dir.delete();
		return beans;
		
	}
	public static void main(String[] args) {
		List<String> ciphers = new ArrayList<String>();
		ciphers.add(Ciphers.cipher[0].cipher);
		ciphers.add(Ciphers.cipher[1].cipher);
		
		//List<AZResultBean> beans = process(ciphers, "/Users/doranchak/Downloads/AZdecrypt0991b/Ciphers", "/Users/doranchak/Downloads/AZdecrypt0991b/Results");
		//for (AZResultBean bean : beans) System.out.println(bean);
	}
}
