package com.zodiackillerciphers.corpus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapBean {
	public Map<String, Integer> mapCounts;
	public Map<File, String> mapFiles;
	
	public MapBean() {
		mapCounts = new HashMap<String, Integer>();
		mapFiles = new HashMap<File, String>();
	}
}
