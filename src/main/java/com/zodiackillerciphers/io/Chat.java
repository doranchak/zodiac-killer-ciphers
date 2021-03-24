package com.zodiackillerciphers.io;

import java.util.List;

public class Chat {
	public static void process(String file) {
		String who = "";
		String time = "";
		String message = "";
		
		System.out.println("<table>");
		List<String> lines = FileUtil.loadFrom(file);		
		for (String line : lines) {
			if (line.startsWith("*** (")) {
				dump(who, time, message);
				who = "";
				time = "";
				message = "";
				System.out.println("<tr><td class='n' colspan='3'>" + line + "</td></tr>");
			}
			else if (line.contains("says to  (")) {
				dump(who, time, message); 
				who = line.substring(0, line.indexOf(" says to  ("));
				time = line.split("\\(")[1].split("\\)")[0];
				message = "";
				
			}
			else message += "<p>" + line + "</p>";
			
//			System.out.println(line.contains("says to  (") + ": " + line);
		}
		System.out.println("</table>");
	}
	
	static void dump(String who, String time, String message) {
		if ("".equals(who) && "".equals(time) && "".equals(message)) return;
		System.out.println("<tr><td class='w'>" + who + "</td><td class='t'>" + time + "</td><td class='m'>" + message + "</td></tr>");
	}
	
	public static void main (String[] args) {
		process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/chats/2013-10-11.txt");
	}
}
