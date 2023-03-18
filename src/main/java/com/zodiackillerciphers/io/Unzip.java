package com.zodiackillerciphers.io;

import java.io.*;
import java.util.Arrays;
import java.util.zip.*;

import com.zodiackillerciphers.tests.cribbing.TextBean;

public class Unzip {
	static final int BUFFER = 2048;
	public static boolean SHOW_INFO = true;
	public static String read(String file) {
		return read(new File(file));
	}
	
	/** read text files from within given zip file.  file is uncompressed in memory. */
	public static String read(File file) {
		StringBuffer sb = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(file);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			while((entry = zis.getNextEntry()) != null) {
			    if (SHOW_INFO) System.out.println("Extracting: " + entry);
			    int count;
			    byte data[] = new byte[BUFFER];
			    String filename = entry.getName();
			    if (SHOW_INFO) System.out.println("Filename: " + filename);
			    while ((count = zis.read(data, 0, BUFFER)) != -1) {
			       // Do whatever you want with the data variable
			       sb.append(new String(data));
			    }
			}
			zis.close();
		} catch (Exception e) {
			System.err.println("Error unzipping file: " + file);
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static TextBean readConverted(String file) {
		return readConverted(new File(file));
	}
	/** read text file from within given zip file.  file is uncompressed in memory.
	 * NOTE: it is assumed that the zip file contains only ONE text file.
	 **/
	public static TextBean readConverted(File file) {
		StringBuilder sbOriginal = new StringBuilder();
		StringBuilder sbConverted = new StringBuilder();
		int posOriginal = 0;
		int posConverted = 0;
		TextBean bean = new TextBean();
		bean.setFileName(file.getPath());
		try {
			FileInputStream fis = new FileInputStream(file);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			while((entry = zis.getNextEntry()) != null) {
			    System.out.println("Extracting: " + entry + ", size: " + entry.getSize());
			    if (entry.getSize() > 20000000l) {
				    System.out.println("IGNORING too big: " + entry + ", " + file.getName());
			    	return null;
			    }
			    int count;
//			    long total = 0;
			    byte data[] = new byte[BUFFER];
			    String filename = entry.getName();
			    //System.out.println("Filename: " + filename);

			    StringBuffer sb1 = new StringBuffer();
			    
			    while ((count = zis.read(data, 0, BUFFER)) != -1) {
//			    	total += count;
//			    	System.out.println(total+"...");
			    	sb1.append(new String(data));
			    }

		    	String chunkOriginal = sb1.toString().replaceAll("\0", "");
		    	sbOriginal.append(chunkOriginal);
		    	String chunkUpper = FileUtil.deAccent(chunkOriginal).toUpperCase();
		    	
		    	for (int i=0; i<chunkUpper.length(); i++) {
//		    		if (i%10000 == 0) {
//		    			System.out.println(i + " of " + chunkUpper.length());
//		    			System.out.println("Free memory (bytes): " + 
//		    					  Runtime.getRuntime().freeMemory());
//		    			/* This will return Long.MAX_VALUE if there is no preset limit */
//		    			  long maxMemory = Runtime.getRuntime().maxMemory();
//		    			  /* Maximum amount of memory the JVM will attempt to use */
//		    			  System.out.println("Maximum memory (bytes): " + 
//		    			  (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));
//
//		    			  /* Total memory currently in use by the JVM */
//		    			  System.out.println("Total memory (bytes): " + 
//		    			  Runtime.getRuntime().totalMemory());
//				    		System.out.println(bean.getMapOC().size());
//				    		System.out.println(bean.getMapCO().size());
//		    		}
		    		bean.getMapOC().put(posOriginal, posConverted);
		    		bean.getMapCO().put(posConverted, posOriginal);
					char ch = chunkUpper.charAt(i);
					if (ch > 64 && ch < 91) {
						sbConverted.append(ch);
						posConverted++;
					} 
//					if (posOriginal <= 5000) {
//						System.out.println("posOriginal " + posOriginal + " posConverted " + posConverted + " ch " + (int) ch);
//					}
					posOriginal++;
		    	}
			    
			    
			    break; // ignore any other files
			}
			zis.close();
		} catch (Exception e) {
			System.err.println("Error unzipping file: " + file);
			e.printStackTrace();
		}
		bean.setConverted(sbConverted);
		bean.setOriginal(sbOriginal);
		return bean;
	}
	
//	/** convert raw text into converted streamed uppercase text with info on context */
//	public static TextBean process(String inp) {
//		return new TextBean(inp);
//	}
	
	public static void testUnzip() {

		String input = read("/Volumes/Biggie/projects/zodiac/docs/for-cribbing-experiment/gutenberg-wget/www.gutenberg.org/robot/part-27/4201.zip");
		TextBean bean = readConverted("/Volumes/Biggie/projects/zodiac/docs/for-cribbing-experiment/gutenberg-wget/www.gutenberg.org/robot/part-27/4201.zip");
		bean.dump();
		
		
		for (int i=0; i<10000; i++) {
			int pos = (int) (Math.random()*(bean.getConverted().length()-1000));
			int len = 10+(int)(Math.random()*70);
			String compare;
			System.out.println("pos " + pos + " len " + len);
			String subOriginal = bean.removeNewlines(bean.getOriginal().substring(pos, pos+len));
			String subConverted = bean.removeNewlines(bean.convertedFor(pos, len));
			System.out.println("subO: " + subOriginal);
			System.out.println("subC: " + subConverted);
			compare = FileUtil.convert(subOriginal).toString();
			if (!compare.equals(subConverted)) {
				System.err.println("NOT EQUAL " + subConverted + ", " + compare);
				System.exit(-1);
			}
			
			subConverted = bean.getConverted().substring(pos, pos+len);
			subOriginal = bean.removeNewlines(bean.originalFor(pos, len, true, 0));
			System.out.println("subO: " + subOriginal);
			System.out.println("subC: " + subConverted);
			compare = FileUtil.convert(subOriginal).toString();
			if (!compare.equals(subConverted)) {
				System.err.println("NOT EQUAL " + subConverted + ", " + compare);
				System.exit(-1);
			}
		}
		bean = readConverted("/Volumes/Biggie/projects/zodiac/docs/for-cribbing-experiment/gutenberg-wget/www.gutenberg.org/robot/part-51/2206.zip");
		bean.dump();
	}
	
	public static void main(String[] args) {
//		testUnzip();
		String input = read("/Volumes/Biggie/projects/zodiac/docs/for-cribbing-experiment/gutenberg-wget/www.gutenberg.org/robot/part-27/4201.zip").toUpperCase();
		System.out.println(Arrays.toString(FileUtil.tokenize(input)));
	}
}
