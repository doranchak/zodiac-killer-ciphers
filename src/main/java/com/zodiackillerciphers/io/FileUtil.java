package com.zodiackillerciphers.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.zodiackillerciphers.corpus.InfoCount;

public class FileUtil {
	
    public static Pattern deAccentPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    public static boolean DEACCENT = false;
    public static String ENCODING = "UTF-8";
	
	public static void rename(String from, String to) {
		File fromFile = new File(from);
		File toFile = new File(to);
		fromFile.renameTo(toFile);
	}
	public static File[] listFrom(String path) {
		try {
			File file = new File(path);
			return file.listFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** return array of tokens */
	public static String[] tokenize(String text) {
		if (text == null) return null;
		return text.replaceAll("'","").split("[\\p{P} \\t\\n\\r]");
	}
	public static String[] tokenize2(String text) {
		return text.split("[!\\\\_,@#%\\$<>\\-\\[\\]\\^&()\\*\\+/:;=\\?`\\{\\}\\|\\~\\. \\t\\n\\r]");
	}
	/** return array of tokens, with anything non-alphabetical removed and empty tokens ignored */
	public static String[] tokenizeAndConvert(String text) {
		return tokenizeAndConvert(text, false);
	}
	public static String[] tokenizeAndConvert(String text, boolean keepApostrophes) {
		String[] tokens = keepApostrophes ? tokenize2(text): tokenize(text);
		if (tokens == null) return null;
		List<String> results = new ArrayList<String>();
		for (String str : tokens) {
			str = str.toUpperCase();
			if (DEACCENT) str = deAccent(str);
			if (keepApostrophes)
				str = str.replaceAll("[^A-Z']", "");
			else str = str.replaceAll("[^A-Z]", "");
			if (str.length() < 1) continue;
			results.add(str);
		}
		return results.toArray(new String[0]);
	}
	/** single string version */
	public static String tokenizeAndConvertToString(String text) {
		String[] tokens = tokenizeAndConvert(text);
		StringBuffer result = new StringBuffer();
		for (String token : tokens) {
			if (result.length() > 0) result.append(" ");
			result.append(token);
		}
		return result.toString();
	}
	
	/** remove empty tokens */
	public static String[] removeEmptyTokens(String[] tokens) {
		List<String> results = new ArrayList<String>();
		for (String str : tokens) {
			if (str.length() < 1) continue;
			results.add(str);
		}
		return results.toArray(new String[0]);
	}
	
	/** return given list of Strings as stream of uppercase alphabetic letters only. */
	public static String convert(List<String> list) {
		StringBuffer sb = new StringBuffer();
		for (String s : list) {
			sb.append(convert(s));
		}
		return sb.toString();
	}
	
	public static String deAccent(String str) {
//		System.out.println("deAccent " + str);
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    return deAccentPattern.matcher(nfdNormalizedString).replaceAll("");
	}
	public static StringBuffer convert(String s) {
		return convert(s, true);
	}
	public static StringBuffer convert(String s, boolean deAccent) {
		StringBuffer sb = new StringBuffer();
//		if (deAccent) {
//			
//		} else 
			
		String upper = deAccent(s).toUpperCase();
		
		for (int i=0; i<upper.length(); i++) {
			char ch = upper.charAt(i);
			if (ch > 64 && ch < 91) sb.append(ch);
		}
		return sb;
	}
	
	public static List<String> loadFrom(String path) {
		return loadFrom(path, 0);
	}
	public static List<String> loadFrom(String path, long limit) {
		List<String> list = new ArrayList<String>();
		
		//System.out.println("loading from [" + path + "]...");

		BufferedReader input = null;
		int counter = 0;
		try {
//			input = new BufferedReader(new FileReader(new File(path)));
			input = new BufferedReader(
			           new InputStreamReader(new FileInputStream(path), "UTF-8"));
			
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				list.add(line);
				counter++;
				if (limit > 0 && counter == limit) break;
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
		
		return list;
	}

	public static TreeSet<String> loadSortedFrom(String folder, String filename) {
		System.out.println("loading " + folder + "/" + filename);
		TreeSet<String> list = new TreeSet<String>();
		
		BufferedReader input = null;
		long counter = 0;
		long suffix = 0;
		Date started = new Date();
		try {
			input = new BufferedReader(new FileReader(new File(folder + "/" + filename)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				list.add(line);
				counter++;
				if (counter % 1000000 == 0) {
					System.out.println("read " + counter + " lines.");
				}
				if (counter % 20000000 == 0) {
					Date ended = new Date();
					float rate = ended.getTime()-started.getTime();
					rate = list.size() / rate; // lines per ms
					rate = rate * 1000; // lines per second
					System.out.println("Read " + list.size() + " lines in " + (ended.getTime() - started.getTime())
							+ " ms (rate " + rate + " lines per sec)");
					// write out every 20 million sorted lines
					started = new Date();
					writeSortedTo(folder, "substitution-isomorphisms-index-sorted-" + (suffix++) + ".txt", list);
					ended = new Date();
					rate = ended.getTime()-started.getTime();
					rate = list.size() / rate; // lines per ms
					rate = rate * 1000; // lines per second
					System.out.println("Wrote " + list.size() + " lines in " + (ended.getTime() - started.getTime())
							+ " ms (rate " + rate + " lines per sec)");
					list.clear();
					
				}
			}
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
		Date ended = new Date();
		float rate = ended.getTime()-started.getTime();
		rate = list.size() / rate; // lines per ms
		rate = rate * 1000; // lines per second
		System.out.println("Read " + list.size() + " lines in " + (ended.getTime() - started.getTime())
				+ " ms (rate " + rate + " lines per sec)");
		// write out every 20 million sorted lines
		started = new Date();
		writeSortedTo(folder, "substitution-isomorphisms-index-sorted-" + (suffix++) + ".txt", list);
		ended = new Date();
		rate = ended.getTime()-started.getTime();
		rate = list.size() / rate; // lines per ms
		rate = rate * 1000; // lines per second
		System.out.println("Wrote " + list.size() + " lines in " + (ended.getTime() - started.getTime())
				+ " ms (rate " + rate + " lines per sec)");
		
		return list;
	}
	
	public static StringBuffer loadSBFrom(String path) { return loadSBFrom(path, true); }
	public static StringBuffer loadSBFrom(String path, boolean addSpaceForEachNewline) {
		return loadSBFrom(new File(path), addSpaceForEachNewline);
	}

	public static StringBuffer loadSBFrom(File file) {
		return loadSBFrom(file, true);
	}
	public static StringBuffer loadSBFrom(File file, boolean addSpaceForEachNewline) {
		StringBuffer sb = new StringBuffer();
		
		//System.out.println("loading from [" + file.getAbsolutePath() + "]...");

		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(
			           new InputStreamReader(new FileInputStream(file), ENCODING));
//			input = new BufferedReader(new FileReader(file));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				sb.append(line);
				if (addSpaceForEachNewline) sb.append(" ");
				counter++;
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
		
		return sb;
	}

	/*
	public static List<String> loadFrom(File file) {
		if (file.getName().toLowerCase().endsWith((".zip"))) {
			String dir = unpack(file);
			List<File> contents = Reader.list(dir);
			if (contents == null) return null;
			for (File f : contents)
				shit
		} else return loadFrom(file.getAbsolutePath());
	}*/
	
	public static void writeText(String path, String text) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(path));
			out.write(text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void testWriteText() {
		writeText("/Users/doranchak/feh.txt", "line 1");
		writeText("/Users/doranchak/feh.txt", "line 2");
		writeText("/Users/doranchak/feh.txt", "line 3");
		
		List<String> lines = new ArrayList<String>();
		lines.add("line 1");
		lines.add("line 2");	
		lines.add("line 3");
		writeText("/Users/doranchak/feh2.txt", lines);
	}
	
	public static void writeText(String path, Set<String> lines) {
		writeText(path, new ArrayList<String>(lines));
	}
	public static void writeText(String path, List<String> lines) {
		try {
			File fout = new File(path);
			FileOutputStream fos = new FileOutputStream(fout);
 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
 
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
 
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void writeSortedTo(String folder, String filename, TreeSet<String> lines) {
		System.out.println("writing " + folder + "/" + filename + ", " + lines.size() + " lines");
		long count = 0;
		try {
			File fout = new File(folder + "/" + filename);
			FileOutputStream fos = new FileOutputStream(fout);
 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
 
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
				count++;
				if (count % 1000000 == 0) 
					System.out.println("Wrote " + count + " lines");
			}
 
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/** find the "streamed" (all caps) text by looking for it in unstreamed form in the given text */
	public static boolean locate(String search, String text, String fileName, boolean linkLetters, String author, String title, int N, boolean summaryOnly, InfoCount summaryInfo) {
		String upper = text.toUpperCase();
		boolean found = false;
		int index = 0; int first = 0; 
		for (int i=0; i<upper.length(); i++) {
			char ch = upper.charAt(i);
			if (ch > 64 && ch < 91) {
				if (search.charAt(index) == ch) {
					//System.out.println("Match " + ch + " at i=" + i + " index=" + index);
					if (index == 0) first = i;
					index++;
				} else {
					//System.out.println("No match " + ch + " at i=" + i + " index=" + index);
					if (index > 0) i=first;
					index = 0; first = i;
				}
				if (index == search.length()) {
					String prefix = "";
					String suffix = "";
					// substring starts at position "first", ends at position "i"
					// show up to N chars before and after the substring
					
					// prefix starts at first-N, ends at first-1
					// suffix starts at i+1, ends at i+N
					
					if (first-1 > -1) {
						if (first-N > 0) prefix += "... "; 
						prefix += text.substring(Math.max(0, first-N), first);
					}
					if (i+1 < text.length()) {
						suffix += text.substring(i+1, Math.min(text.length(), i+N+1));
						if (i+N+1 < text.length()) suffix += "...";
					}

					found = true;
					if (summaryOnly) {
						System.out.println("Length: '''" + summaryInfo.substring.length() + "'''  Substring: '''[" + text.substring(first, i+1) + "]'''  Matches: '''" + summaryInfo.count + "'''");
						return found;
					}
					
					String match = prefix + "'''[" + text.substring(first, i+1) + "]'''" + suffix;
					String fnDisplay = linkLetters ? "[http://zodiac-killer-ciphers.googlecode.com/svn/trunk/letters/" + fileName + " " + fileName + "]" : fileName; 
					String css = "";
					if (author != null || title != null) {
						String line = "<span style=\"color: #2f4f4f;\">";
						if (title != null) line += " '''Title''': <u>" + title + "</u>";
						if (author != null) line += " '''Author''': <u>" + author + "</u>";
						line += "</span><br><br>" + match;
						System.out.println("|-valign=\"top\"");
						System.out.println("| " + fnDisplay);
						System.out.println("| " + line);
						
					} else {
						System.out.println("|-valign=\"top\"");
						css = "background-color: #ffdead";
						System.out.println("| style=\"white-space: nowrap; " + css + "\"| " + fnDisplay);
						System.out.println("| style=\"" + css + "\" | " + match + "");
					}
					index = 0; first = 0;
				}
			}
		}
		return found;
		
	}
	
	/** recursively fetch all files starting in the given path */
	public static List<File> allFiles(String path) {
		List<File> list = new ArrayList<File>();
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isDirectory())
					list.addAll(allFiles(f.getPath()));
				else 
					list.add(f);
			}
		}
		return list;
	}
	
	public static void testDeaccent() {
		//String test = "���������";
		String test = "François le Bossu, by Comtesse de Ségur";
		System.out.println(test);
		System.out.println(deAccent(test));
		System.out.println(deAccent(test.toUpperCase()));
		
		test = "";
		for (int i=32; i<=255; i++) test += (char) i;
		System.out.println(test);
		System.out.println(deAccent(test));
	}
	public static void main(String[] args) {
		//locate("PLEASEHELPMEICAN", "Dear Melvin This is the Zodiac speaking I wish you a happy Christmass. The one thing I ask  of you is this, please help me. I cannot reach out because of this thing in me  won't let me. I am finding it extreamly dificult to keep in check I am afraid I  will loose control again and take my nineth & posibly tenth victom. Please help  me I am drownding. At the moment the children are safe from the bomb because it  is so massive to dig in & the triger mech requires so much work to get it  adjusted just right. But if I hold back too long from no nine I will loose  complet all controol of my self & set the bomb up. Please help me I can not  remain in control for much longer.", "filename.txt", true, null, null, 50, false, null);
		//testWriteText();
//		testDeaccent();
//		System.out.println(Arrays.toString(tokenize(
//				"Roanoke County, on behalf of Roanoke County Public Schools, is evaluating sealed bids from qualified vendors to provide network equipment as specified.  Please download the Invitation for Bid document using the link below. Please download Addendum 1 using the link below. Please note: bidders must submit the revised price form included in Addendum 1.Bid Tabulation is available for download below.")));
//		loadSortedFrom("/Volumes/Biggie/projects/zodiac", "substitution-isomorphisms-index.txt");
		System.out.println(Arrays.toString(tokenizeAndConvert("THIS IS A TEST __OF___ !@#!@#TOKENIZATION%#$%#$@")));
		System.out.println(Arrays.toString(tokenizeAndConvert("THIS IS A TES'T __OF___ !@#!@#TOKENIZATION%#$%#$@")));
		System.out.println(Arrays.toString(tokenizeAndConvert("THIS IS A TES'T __OF___ !@#!@#TOKENIZATION%\\#$%#&'()*+/\\.:;=?`{|}~$@<>-_[]^^", true)));
//		System.out.println(Arrays.toString(tokenizeAndConvert("THIS IS A TEST __OF___ TOKEN$$$IZA'\"TION")));
	}
}
