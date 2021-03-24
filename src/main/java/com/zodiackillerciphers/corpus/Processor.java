package com.zodiackillerciphers.corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.io.FileUtil;

public class Processor {
	/** process Zodiac correspondences.  count all repeated substrings of any length. 
	 * we use the resulting map of counts of all possible substrings as a basis for finding interesting substring
	 * matches from other corpora, such as project gutenberg,  
	 **/
	public static MapBean generateMap(String path, int maxSubstringLength, int minSubstringLength) {
		List<File> files = Reader.list(path);
		//Map<String, Integer> map = new HashMap<String, Integer>();
		MapBean bean = new MapBean();
		for (File file : files) {
			try {
				String text = FileUtil.loadSBFrom(file.getAbsolutePath()).toString();
				String textConverted = FileUtil.convert(text).toString();
				bean.mapFiles.put(file, text);
				System.out.println("File [" + file + "] Text [" + textConverted + "] Length [" + textConverted.length() + "]");
				for (int n=minSubstringLength; n<(Math.min(textConverted.length()+1, maxSubstringLength)); n++) {
					//if (n % 100 == 0) System.out.println(" - n = " + n);
					for (int i=0; i<textConverted.length()-n+1; i++) {
						String sub = textConverted.substring(i,i+n);
						
						/*String match = file.getName() + ": ";
						match += "..." + text.substring(Math.max(i-10,0), i);
						match += " [" + sub + "] ";
						match += text.substring(i+n, Math.min(i+n+10, text.length()));
						match += "...";*/
						
						Integer val = bean.mapCounts.get(sub);
						if (val == null) {
							val = 0;
						}
						val++;
						//info.matches.add(file.getName());
						bean.mapCounts.put(sub, val);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bean;
	}
	
	/** convert map into Info list, sorted by substring length, descending.  exclude non-repeating substrings. */
	public static List<InfoCount> toList(Map<String, Integer> map) {
		List<InfoCount> list = new ArrayList<InfoCount>();
		for (String key : map.keySet()) {
			Integer val = map.get(key);
			if (val > 1) {
				InfoCount info = new InfoCount();
				info.substring = key;
				info.count = val;
				list.add(info);
			}
		}
		Collections.sort(list);
		return list;
	}
	
	/** the map contains many substrings that are already contained by larger substrings.  only display the smaller ones if they have larger counts than the larger ones. */
	public static List<InfoCount> postProcess(MapBean bean) {
		List<InfoCount> list = toList(bean.mapCounts);
		System.out.println("Repeated substrings: " + list.size());
		
		list = postProcess(list);
		
		return list;
	}
	
	public static List<InfoCount> postProcess(List<InfoCount> list) {
		Map<String, InfoCount> seenMap = new HashMap<String, InfoCount>();
		
		int count = 0;
		for (InfoCount info : list) {
			// display info if:
			// 1) this substring is not part of a larger substring, or
			// 2) this substring is part of a larger substring and has a higher count than the larger substring
			seen(seenMap, info);
			count++;
			if (count % 1000 == 0) System.out.println(count + " of " + list.size() + "...");
		}
		
		List<InfoCount> sorted = new ArrayList<InfoCount>(seenMap.values());
		Collections.sort(sorted);
		return sorted;
	}
	
	public static void dump(List<InfoCount> list, MapBean bean) {
		for (InfoCount info : list) {
			System.out.println(info);
			System.out.println("{|");
			System.out.println("|-valign=\"top\"");
			System.out.println("! File");
			System.out.println("! Match");
			for (File file : bean.mapFiles.keySet()) {
				FileUtil.locate(info.substring, bean.mapFiles.get(file), file.getName(), true, null, null, 50, false, null);
			}
			System.out.println("|}");
		}
	}

	public static void dump2(List<InfoCount> list, String dirTmp, Map<String, String> zodiacCorpus, Map<String, IndexEntry> index, String gPrefix, boolean summaryOnly) {
		
		int count = 0; int total = 0;
		for (InfoCount info : list) {
			if (!summaryOnly) {
				System.out.println(info);
				
				System.out.println("<div class=\"ztable\">");
				System.out.println("{|");
				System.out.println("|-valign=\"top\"");
				System.out.println("! File");
				System.out.println("! Match");
			}
			for (String key : zodiacCorpus.keySet()) {
				boolean found = FileUtil.locate(info.substring, zodiacCorpus.get(key), key, true, null, null, 50, summaryOnly, info);
				if (summaryOnly && found) break;
			}
			if (!summaryOnly) {
				for (String file : info.fileNames) {
					try {
						File f = new File(file);
						String text = Search.read(f, dirTmp);
						String[] bib = infoFrom(file, index, gPrefix);
						FileUtil.locate(info.substring, text, f.getName(), false, bib[1], bib[0], 50, false, null);
					} catch (Exception e) {
						System.out.println(e);
						e.printStackTrace();
					}
				}
				System.out.println("|}");
				System.out.println("</div>");
			}
			
			count+=info.fileNames.size(); 
			total++;
			
			System.out.println("Processed " + total + " of " + list.size());
			if (count >= 1000) {
				System.out.println("PAGE BREAK");
				count = 0;
			}
		}
	}
	
	// returns {title, author}
	public static String[] infoFrom(String file, Map<String, IndexEntry> index, String gPrefix) {
		String[] result = new String[] { "N/A", "N/A" };
		
		String key = null;
		if (file.toLowerCase().startsWith(gPrefix.toLowerCase())) {
			key = file.toLowerCase().substring(gPrefix.length());
		}
		System.out.println("key: " + key);
		IndexEntry val = index.get(key);
		if (val == null) return result;
		result[0] = val.title;
		result[1] = val.author;
		return result;
	}
	
	
	/** update map of seen substrings.  keep only the best (non-spurious) substrings.
	 */
	public static void seen(Map<String, InfoCount> seenMap, InfoCount info) {
		
		// Each substring A in the map has these properties:
		// 1) No other substring B in the map is identical to A
		// 2) No other substring B in the map contains A and has an equal or higher count
		
		// Consider the given substring C.  We want to know whether or not to place it in the map.
		// 1) Do not place it in the map if some substring B in the map is identical to C
		// 2) Do not place it in the map if some substring B in the map contains C, and B's count is equal to or higher than C's count.
				// actually, it's impossible for B's count to be higher than C's count, since C is a substring of B.  
		        // so, we only have to check if the counts are equal.
		
		// If neither of those conditions are met, add C to the map.  then, remove all substrings that are contained by C
		// and have counts equal to B.

		if (seenMap.keySet().contains(info.substring)) return; // condition 1
		if (seenContains(seenMap, info)) return; // condition 2;
		
		// remove "dominated" substrings 
		seenRemove(seenMap, info);
		
		// add new substring
		seenMap.put(info.substring, info);
	}
	
	public static boolean seenContains(Map<String, InfoCount> seenMap, InfoCount info) {
		for (String s : seenMap.keySet()) {
			if (s.contains(info.substring)) {
				if (seenMap.get(s).count == info.count) return true;
			}
		}
		return false;
	}
	
	public static void seenRemove(Map<String, InfoCount> seenMap, InfoCount info) {
		List<String> removes = new ArrayList<String>();
		for (String s : seenMap.keySet()) {
			if (info.substring.contains(s)) {
				if (seenMap.get(s).count == info.count) removes.add(s);
			}
		}
		for (String s : removes) seenMap.remove(s);
	}

	/** generate and dump map of repeated substrings among all text files found at given path */
	public static void info(String path, int maxSubstringLength, int minSubstringLength) {
		MapBean bean = generateMap(path, maxSubstringLength, minSubstringLength);
		int countAll = bean.mapCounts.size();
		System.out.println("Total substrings: " + countAll);

		/*
		for (String key : map.keySet()) {
			countAll++;
			Integer val = map.get(key);
			if (val > 1) {
				countRepeats++;
				System.out.println(key.length() + ", " + key + ", " + val);
			}
		}*/
		
		List<InfoCount> list = postProcess(bean);
		dump(list, bean);
		
	}
	
	public static void main(String[] args) {
		if (args == null || args.length != 3 || args[0].equals("")) {
			System.err.println("Usage: com.zodiackillerciphers.corpus.Processor [path] [max substring length] [min substring length]");
			System.exit(-1);
		}
		info(args[0], Integer.valueOf(args[1]), Integer.valueOf(args[2]));
		
	}
}
