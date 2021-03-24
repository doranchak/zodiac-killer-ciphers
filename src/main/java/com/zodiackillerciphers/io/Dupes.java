package com.zodiackillerciphers.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Dupes {
	/** scan the output of fdupes -r -S, and redisplay in descending order of filesize */
	public static void dupeScan(String filePath, int limit) {
		List<DupesBean> beans = new ArrayList<DupesBean>(); 
		List<String> lines = FileUtil.loadFrom(filePath);
		DupesBean bean = null;
		for (String line : lines) {
			if (line.contains("bytes each")) {
				if (bean != null) {
					beans.add(bean);
					//if (beans.size() > limit) break;
				}
				bean = new DupesBean();
				bean.fileSize = Long.valueOf(line.split(" ")[0]);
			} else if (line.length() == 0) continue;
			else if (bean != null) {
				bean.files.add(line);
			}
		}
		if (bean != null) beans.add(bean);
		beans.sort(new Comparator<DupesBean>() {
			@Override
			public int compare(DupesBean o1, DupesBean o2) {
				// TODO Auto-generated method stub
				return Long.compare(o2.fileSize, o1.fileSize);
			}
		});
		int count = 0;
		for (DupesBean b : beans) {
			System.out.println(b.fileSize);
			for (String file : b.files) {
				System.out.println("   " + file);
			}
			count++;
			if (count >= limit) break;
		}
		folderAnalysis(beans, limit);
	}
	
	/** try to figure out which folders have the most dupes, by tracking total size of duped files within them */
	public static void folderAnalysis(List<DupesBean> beans, int limit) {
		Map<String, Long> folders = new HashMap<String, Long>();
		for (DupesBean bean : beans) {
			for (String file : bean.files) {
				List<String> paths = foldersFrom(file);
				for (String path : paths) {
					Long size = folders.get(path);
					if (size == null) size = 0l;
					size += bean.fileSize;
					folders.put(path, size);
				}
			}
		}
		folders = sortByValue(folders);
		int count = 0;
		for (String key : folders.keySet()) {
			System.out.println(folders.get(key) + "	" + key);
			count++;
			if (count == limit) break;
		}
	}
	
	static Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static List<String> foldersFrom(String path) {
		List<String> list = new ArrayList<String>();
		String[] split = path.split("/");
		String fullPath = "";
		for (int i=0; i<split.length; i++) {
			if (fullPath.length() > 0) 
				fullPath += "/";
			fullPath += split[i];
			list.add(fullPath);
		}
		return list;
	}
	
	public static void main(String[] args) {
		dupeScan("/Volumes/BackupsSmegPort/dupes", 100);
	}
}
