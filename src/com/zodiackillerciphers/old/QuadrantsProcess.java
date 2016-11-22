package com.zodiackillerciphers.old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QuadrantsProcess {
	/** process one of the quadrant results files to extract maximum h-mean for all combinations of 1 or 2 input variables */
	public static void processResultsFile(String fileName) {
		
		int NUM = 3;
		
		Map<String, Float[]> maxxes = new HashMap<String, Float[]>(); // for hmean
		//Map<String, Float> maxxesNgrams = new HashMap<String, Float>(); // for ngrams
		
	    //...checks on aFile are elided
	    int counter = 0;
	    //declared here only to make visible to finally clause
	    BufferedReader input = null;
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      input = new BufferedReader( new FileReader(new File(fileName)) );
	      String line = null; //not declared within while loop
	      /*
	      * readLine is a bit quirky :
	      * it returns the content of a line MINUS the newline.
	      * it returns null only for the END of the stream.
	      * it returns an empty String if two newlines appear in a row.
	      */
	      while (( line = input.readLine()) != null){
	    	if (line.startsWith("MAX")) break;  
	    	if (line.startsWith("Tested")) break;
	    	String[] split = line.split(",");
	    	Float hsum = Float.valueOf(split[21]); // h-mean is in field 22
	    	Float hmean = Float.valueOf(split[22]); // h-mean is in field 22
	    	Float ngrams = Float.valueOf(split[95]); // n-grams sum is in field 95
	    	
	    	
	    	// consider each variable independently 
	    	for (int i=0; i<13; i++) {
	    		String key = "one," + i+","+Integer.valueOf(split[i]);
	    		Float[] vals = maxxes.get(key);
	    		if (vals == null) {
	    			vals = new Float[NUM];
	    			for (int z=0; z<vals.length; z++) vals[z] = 0f;
	    		}
	    		vals[0] = Math.max(vals[0], hsum);
	    		vals[1] = Math.max(vals[1], hmean);
	    		vals[2] = Math.max(vals[2], ngrams);
	    		maxxes.put(key, vals);
	    	}
	    	
	    	// consider pairs of variables
	    	for (int i=0; i<12; i++) {
		    	for (int j=i+1; j<13; j++) {
		    		String key = "two," + i + "," + j + "," + Integer.valueOf(split[i]) + "," + Integer.valueOf(split[j]);

		    		Float[] vals = maxxes.get(key);
		    		if (vals == null) {
		    			vals = new Float[NUM];
		    			for (int z=0; z<vals.length; z++) vals[z] = 0f;
		    		}
		    		vals[0] = Math.max(vals[0], hsum);
		    		vals[1] = Math.max(vals[1], hmean);
		    		vals[2] = Math.max(vals[2], ngrams);
		    		maxxes.put(key, vals);
		    	
		    	}
	    	}
	    	
	    	
	    	
	        counter++;
	       // contents.append(System.getProperty("line.separator"));
	      }
	    }
	    catch (FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    finally {
	      try {
	        if (input!= null) {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	      }
	      catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }

	    for (String key : maxxes.keySet()) {
	    	Float[] vals = maxxes.get(key);
	    	System.out.println(key + "," + vals[0] + "," + vals[1] + "," + vals[2]);
	    }
	}

	/** process the processed results files to extract maximums for all combinations of variables */
	public static void processResultsFileAll(String fileName) {
		
		int NUM = 3;
		
		Map<String, Float[]> maxxes = new HashMap<String, Float[]>(); // for hmean
		//Map<String, Float> maxxesNgrams = new HashMap<String, Float>(); // for ngrams
		
	    //...checks on aFile are elided
	    int counter = 0;
	    //declared here only to make visible to finally clause
	    BufferedReader input = null;
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      input = new BufferedReader( new FileReader(new File(fileName)) );
	      String line = null; //not declared within while loop
	      /*
	      * readLine is a bit quirky :
	      * it returns the content of a line MINUS the newline.
	      * it returns null only for the END of the stream.
	      * it returns an empty String if two newlines appear in a row.
	      */
	      boolean two = false;
	      while (( line = input.readLine()) != null){
	    	two = line.startsWith("two");
	    	//System.out.println(line);
	    	int offset = two ? 2 : 0;
	    	String[] split = line.split(",");
	    	Float hsum = Float.valueOf(split[3+offset]); 
	    	Float hmean = Float.valueOf(split[4+offset]);
	    	Float ngrams = Float.valueOf(split[5+offset]);
	    	
	    	String key;
	    	if (two) {
	    		key = split[0] + "," + Quadrants.nameFrom(Integer.valueOf(split[1])) + "," + Quadrants.nameFrom(Integer.valueOf(split[2]))
	    			+ "," + split[3] + "," + split[4];  
	    	}
	    	else {
	    		key = split[0] + "," + Quadrants.nameFrom(Integer.valueOf(split[1])) + "," + split[2];
	    	}
	    	
    		Float[] vals = maxxes.get(key);
    		if (vals == null) {
    			vals = new Float[NUM];
    			for (int z=0; z<vals.length; z++) vals[z] = 0f;
    		}
    		vals[0] = Math.max(vals[0], hsum);
    		vals[1] = Math.max(vals[1], hmean);
    		vals[2] = Math.max(vals[2], ngrams);
    		maxxes.put(key, vals);
	    	
	        counter++;
	       // contents.append(System.getProperty("line.separator"));
	      }
	    }
	    catch (FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    finally {
	      try {
	        if (input!= null) {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	      }
	      catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }

	    for (String key : maxxes.keySet()) {
	    	Float[] vals = maxxes.get(key);
	    	System.out.println(key + "," + vals[0] + "," + vals[1] + "," + vals[2]);
	    }
	}
	
	/** count how many of the tests in the given file have h sum that exceeds that of the unaltered cipher text.  also prints only the lines that have higher h sum. */
	public static void count(String fileName, float min) {

		int hits = 0;
		int total = 0;
	    BufferedReader input = null;
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      input = new BufferedReader( new FileReader(new File(fileName)) );
	      String line = null; //not declared within while loop
	      /*
	      * readLine is a bit quirky :
	      * it returns the content of a line MINUS the newline.
	      * it returns null only for the END of the stream.
	      * it returns an empty String if two newlines appear in a row.
	      */
	      boolean two = false;
	      while (( line = input.readLine()) != null){
	    	if (line.startsWith("MAX") || line.startsWith("Tested")) continue;
	    	String[] split = line.split(",");
	    	Float val = Float.valueOf(split[13]);
	    	total++;
	    	if (val > min) { 
	    		hits++;
	    		System.out.println(line);
	    	}
	      }
	    }
	    catch (FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    finally {
	      try {
	        if (input!= null) {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	      }
	      catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }

	    System.out.println("hits " + hits + " total " + total);
	}

	/** print out only the results that have the given score as a minimum. */
	public static void filter(String fileName, float min) {

	    BufferedReader input = null;
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      input = new BufferedReader( new FileReader(new File(fileName)) );
	      String line = null; //not declared within while loop
	      /*
	      * readLine is a bit quirky :
	      * it returns the content of a line MINUS the newline.
	      * it returns null only for the END of the stream.
	      * it returns an empty String if two newlines appear in a row.
	      */
	      boolean two = false;
	      while (( line = input.readLine()) != null){
	    	if (line.startsWith("quad") || line.startsWith("hits")) continue;
	    	String[] split = line.split(",");
	    	Float val = Float.valueOf(split[13]);
	    	if (val >= min)	System.out.println(line);
	    	}
	    		
	      
	    }
	    catch (FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    finally {
	      try {
	        if (input!= null) {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	      }
	      catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }
	}
	
	/** look for high-scoring homophone candidates, and directly inspect the actual sequence counts
	 * to determine if there is truly an improvement over the original cipher
	 */
	public static void processResultsFile(String fileName, int threshold) {
		
	    //...checks on aFile are elided
	    int counter = 0;
	    //declared here only to make visible to finally clause
	    BufferedReader input = null;
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      input = new BufferedReader( new FileReader(new File(fileName)) );
	      String line = null; //not declared within while loop
	      /*
	      * readLine is a bit quirky :
	      * it returns the content of a line MINUS the newline.
	      * it returns null only for the END of the stream.
	      * it returns an empty String if two newlines appear in a row.
	      */
	      while (( line = input.readLine()) != null){
	    	if (line.startsWith("MAX")) break;  
	    	if (line.startsWith("Tested")) break;
	    	String[] split = line.split(",");

	    	int[] args = new int[14];
	    	for (int i=0; i<14; i++) {
	    		args[i] = Integer.valueOf(split[i]);
	    	}
	    	
	    	if (args[13] <= threshold) continue; // ignore anything that scores less than the threshold
	    	
	    	
	    	
	        counter++;
	      }
	    }
	    catch (FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    finally {
	      try {
	        if (input!= null) {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	      }
	      catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }
	}
	
	public static void main(String[] args) {
		//processResultsFile(args[0]);
		//processResultsFileAll("/Users/doranchak/projects/work/java/zodiac/variable-max-quad3-results-all.txt");
		filter(args[0], Float.valueOf(args[1]));
	}
}
