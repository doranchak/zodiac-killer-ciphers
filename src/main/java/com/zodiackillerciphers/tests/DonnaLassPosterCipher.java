package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;

public class DonnaLassPosterCipher {
	static void processKevinsResults(String path) {
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			line = line.toUpperCase();
			double iocDiff = Stats.iocDiff(line);
			float zk = NGrams.zkscore(new StringBuffer(line));
			System.out.println(zk + "	" + iocDiff + "	" + line);
		}
	}
	public static void main(String[] args) {
		processKevinsResults("/Users/doranchak/projects/zodiac/doit3.res.uniq");
	}
}
