package com.zodiackillerciphers.web;

import com.zodiackillerciphers.ciphers.Ciphers;

public class WebtoySymbols {
	/** map webtoy transcription to image names */
	public static String getName(char letter) {
		switch (letter) {
		case '1':
			return ("n1");
		case '2':
			return ("n2");
		case '3':
			return ("n3");
		case '4':
			return ("n4");
		case '5':
			return ("n5");
		case '6':
			return ("n6");
		case '7':
			return ("n7");
		case '8':
			return ("n8");
		case '9':
			return ("n9");
		case '^':
			return ("caret");
		case '#':
			return ("sq");
		case '_':
			return ("sqe");
		case '@':
			return ("sqd");
		case '*':
			return ("sql");
		case '%':
			return ("sqr");
		case '(':
			return ("theta");
		case ')':
			return ("phi");
		case 'z':
			return ("zodiac");
		case 't':
			return ("perp");
		case '&':
			return ("pf");
		case ';':
			return ("idl");
		case ':':
			return ("idr");
		case '>':
			return ("gt");
		case '.':
			return ("dot");
		case '<':
			return ("lt");
		case '+':
			return ("plus");
		case '/':
			return ("slash");
		case '\\':
			return ("backslash");
		case '-':
			return ("dash");
		case '!':
			return ("funnyi");
		case '=':
			return ("sidek");
		case '|':
			return ("bar");
		case 'b':
			return ("bb");
		case 'c':
			return ("bc");
		case 'd':
			return ("bd");
		case 'e':
			return ("be");
		case 'f':
			return ("bf");
		case 'j':
			return ("bj");
		case 'k':
			return ("bk");
		case 'l':
			return ("bl");
		case 'p':
			return ("bp");
		case 'q':
			return ("bq");
		case 'r':
			return ("br");
		case 'y':
			return ("by");
		case 'A':
			return ("a");
		case 'B':
			return ("b");
		case 'C':
			return ("c");
		case 'D':
			return ("d");
		case 'E':
			return ("e");
		case 'F':
			return ("f");
		case 'G':
			return ("g");
		case 'H':
			return ("h");
		case 'I':
			return ("i");
		case 'J':
			return ("j");
		case 'K':
			return ("k");
		case 'L':
			return ("l");
		case 'M':
			return ("m");
		case 'N':
			return ("n");
		case 'O':
			return ("o");
		case 'P':
			return ("p");
		case 'Q':
			return ("q");
		case 'R':
			return ("r");
		case 'S':
			return ("s");
		case 'T':
			return ("t");
		case 'U':
			return ("u");
		case 'V':
			return ("v");
		case 'W':
			return ("w");
		case 'X':
			return ("x");
		case 'Y':
			return ("y");
		case 'Z':
			return ("z");
			// symbols unique to 13- and 32-character ciphers
		case '?':
			return ("omega");
		case '0':
			return ("taurus");
		case '[':
			return ("t2");
		case ' ':
			return ("blank");
		default:
			return ("unknown");
		}
	}
	/** convert cipher transcription to HTML of images */
	public static String toHtml(String cipher, boolean darker) {
		String html = "";
		for (int i=0; i<cipher.length(); i++) {
			char c = cipher.charAt(i);
			html += toHtml(c, darker);
		}
		return html;
	}
	public static String toHtml(String cipher) {
		return toHtml(cipher, true);
	}
	public static String toHtml(char c, boolean darker) {
		String html = "";
		String n = getName(c);
		html += "<img src=\"http://www.oranchak.com/zodiac/webtoy/alphabet/";
		if (darker) html += "darker/";
		html += n+".jpg\">";
		return html;
		
	}
	public static void main(String[] args) {
		System.out.println(toHtml(Ciphers.cipher[0].cipher));
	}
	
}
