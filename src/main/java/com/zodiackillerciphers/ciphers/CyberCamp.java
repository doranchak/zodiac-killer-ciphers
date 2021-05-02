package com.zodiackillerciphers.ciphers;

public class CyberCamp {
	/** output baconian */
	public static void makeBaconian() {
		String binary = "ABAABBABAABAABABAABBABABAABAAAAAABBAAABBABAAAABBABAABBABAABBAABBBAABAABAAABAABAABAABAABBABABBBAAAAABABBBAABBAAAAAAB";
		String message = "WehaveplantedabombinapublicplaceYoumusttaketenmilliondollarsinunmarkedbillstotheintersectionofaberdeenandwilliamson";
		for (int i=0; i<message.length(); i++) {
			char b = binary.charAt(i);
			System.out.println("<span class=\"" + b + "\">" + message.charAt(i) + "</span>");
		}
	}
	public static void main(String[] args) {
		makeBaconian();
	}
}
