package com.zodiackillerciphers.tests.cribbing;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.io.FileUtil;

public class TextBean extends Base {
	/** converted stream of text (all uppercase, no whitespace, alphabet only (A-Z)) */
	StringBuilder converted;
	/** original unconverted text */
	StringBuilder original;
	/** file name */
	String fileName;
	/** map positions from original to converted */
	Map<Integer, Integer> mapOC;
	/** map positions from converted to original */
	Map<Integer, Integer> mapCO;

	/** ignore conversion between original/converted */
	boolean ignoreConversion = false;
	
	public TextBean() {
		mapOC = new HashMap<Integer, Integer>();
		mapCO = new HashMap<Integer, Integer>();
	}
	/** create bean and perform conversion */
	public TextBean(StringBuilder original) {
		this.original = original;
		if (!ignoreConversion) convert();
	}
	
	public void convert() {
		mapOC = new HashMap<Integer, Integer>();
		mapCO = new HashMap<Integer, Integer>();
	
		if (original == null) return;
		
		int posC = 0;
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<original.length(); i++) {
			char ch = original.charAt(i);
			/** replace accented character with unaccented equivalent */
			String deaccent = FileUtil.deAccent(""+ch).toUpperCase();
			if (deaccent.length() != 1) {
				fatal("FATAL: Did not expect deaccent of [" + ch + "] to not be length 1.  [" + deaccent + "]");
			}
			mapOC.put(i, posC);
			mapCO.put(posC, i);
			
			ch = deaccent.charAt(0);
			if (ch > 64 && ch < 91) {
				sb.append(ch);
				posC++;
			}
		}
		converted = sb;
		
	}
	
	/**
	 * return converted substring corresponding to original text starting at
	 * given pos and having given length
	 */
	public String convertedFor(int posOriginal, int length) {
		return converted.substring(mapOC.get(posOriginal), mapOC.get(posOriginal+length));
	}
	/**
	 * return original substring corresponding to converted text starting at
	 * given pos and having given length
	 * 
	 * if additional > 0, add that much extra to the result on each side (to show context) 
	 * 
	 */
	public String originalFor(int posConverted, int length, boolean removeNewlines, int additional) {
		try {
			Integer a = mapCO.get(posConverted);
			Integer b = mapCO.get(posConverted+length);
			if (a == null) {
				fatal("originalFor: mapCO null for key " + posConverted);
			}
			if (b == null) {
//			System.out.println("posConverted " + posConverted);
//			System.out.println("length " + length);
//			System.out.println("converted length " + converted.length());
//			
//			for (int i=0; i<591800; i++) {
//				if (mapCO.get(i) == null) System.out.println(" null for " + i);
//			}
				//fatal("originalFor: mapCO null for key " + (posConverted+length));
				b = mapCO.get(posConverted+length-1);
			}
			String result = original.substring(a, Math.min(b, original.length()));
			if (removeNewlines) result = result.replace("\n", " ").replace("\r", " ");
			
			if (additional > 0) {
				int newStart = Math.max(0, posConverted - additional);
				int chunkLength = posConverted - newStart;
//			System.out.println("posConverted " + posConverted + " newStart " + newStart + " len " + chunkLength);
				if (chunkLength > 0) {
					result = originalFor(newStart, chunkLength, true, 0) + "[" + result;
				}
				newStart = Math.min(converted.length()-1, posConverted + length);
				int newEnd = Math.min(converted.length()-1, posConverted + length + additional);
				chunkLength = newEnd - newStart + 1;
//			System.out.println("posConverted " + posConverted + " newStart " + newStart + " newEnd " + newEnd + " len " + chunkLength);
				if (chunkLength > 0) {
					result = result + "]" + originalFor(newStart, chunkLength, true, 0);
				}
			}
			
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public String removeNewlines(String inp) {
		return inp.replaceAll("\n", " ").replaceAll("\r", " ");
	}
	
	public Integer nextMatchFor(Map<Integer, Integer> map, int position) {
		Integer result = null;
		while (result == null) {
			result = map.get(position);
			position++;
		}
		return result;
	}
	
	public void dump() {
		System.out.println("original:");
		System.out.println(original.substring(0,1000));
		System.out.println("converted:");
		System.out.println(converted.substring(0,1000));
		System.out.println("mapOC: " + mapOC.size());
		System.out.println("mapCO: " + mapCO.size());
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the mapOC
	 */
	public Map<Integer, Integer> getMapOC() {
		return mapOC;
	}
	/**
	 * @param mapOC the mapOC to set
	 */
	public void setMapOC(Map<Integer, Integer> mapOC) {
		this.mapOC = mapOC;
	}
	/**
	 * @return the mapCO
	 */
	public Map<Integer, Integer> getMapCO() {
		return mapCO;
	}
	/**
	 * @param mapCO the mapCO to set
	 */
	public void setMapCO(Map<Integer, Integer> mapCO) {
		this.mapCO = mapCO;
	}
	/**
	 * @return the converted
	 */
	public StringBuilder getConverted() {
		return converted;
	}
	/**
	 * @param converted the converted to set
	 */
	public void setConverted(StringBuilder converted) {
		this.converted = converted;
	}
	/**
	 * @return the original
	 */
	public StringBuilder getOriginal() {
		return original;
	}
	/**
	 * @param original the original to set
	 */
	public void setOriginal(StringBuilder original) {
		this.original = original;
	}
	

	public static void main(String[] args) {
		String test = "abc";
		System.out.println(test.substring(2,Math.min(100, test.length())));
	}
	/**
	 * @return the ignoreConversion
	 */
	public boolean isIgnoreConversion() {
		return ignoreConversion;
	}
	/**
	 * @param ignoreConversion the ignoreConversion to set
	 */
	public void setIgnoreConversion(boolean ignoreConversion) {
		this.ignoreConversion = ignoreConversion;
	}
}
