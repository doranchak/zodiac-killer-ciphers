package com.zodiackillerciphers.old;

import java.text.DecimalFormat;

public class WikiUtils {
	
	/** fix special chars from the given string */
	public static String scrub(String str) {
		return str.replaceAll("<","&lt;").replaceAll("\\|","&#124;");
	}
}
