package com.zodiackillerciphers.ciphers;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RicinBomber {
	static String[] cipher = new String[] {
		"ACMRX", 
		"OhT[oNi\\jPSBkQ*tEeFI+vxlo]BXYsUE0", 
		"CkZoO|ATG1J\\4,iKStMjbNpvx|2RXBr*Q\\,T/", 
		"USfI5|4Gc\\/ziKSICq.", 
		"0hvTp|[A+Nx\\ymSEkQRtoPF|XL\\jBkYeKGZbO", 
		"*tM/JyuEiSU,vp|X0qo[\\TxSl|BkZhFOEtL", 
		"soi\\ACXBjS-UR+|AEvIjgGUT1/YoV4.", 
		"NiABk\\Ijz*JvSUEtyn|\\0TKSqCD+RL.", 
		"QoibABv|Tp\\F*whCYuRixSZ4GXNEJvQoky", 
		"q|BW\\/rSjt*6KT.", 
		"ip|UCeRXs*FJMIqF\\[vGkTxSm/ivI2,4CfOJ=", 
		"0G+A/jNHZItG-gc/QT1Ei|UoXLq\\vTKS", 
		"A|4i\\5BOIjEvSGz-."
	};
	
	static String[] solution = new String[] {
		"Ricin",
		"Put caster (sic) beans in a clouth (sic) bag and wrap",
		"in paper towles (sic) then crush them in a vice, to",
		"remove some of the oil.",
		"Put the crushed beans in a blender and chop up",
		"in cold water, then place the bean pulp and",
		"water in a refriuerator (sic) for two days.", 
		"Strane (sic) or filter and keep the liquid.",
		"Saturate the liquid with epson (sic) salts and",
		"leave overnight.",
		"The ricin will collect on the bottom, simply",
		"pour or sipon (sic) off most water and let the",
		"rest evaporate off"
	};

	static Map<Character, Character> decoder; 
	static {
		decoder = new HashMap<Character, Character>();
		decoder.put('.', '.');
		decoder.put(',', ',');
		decoder.put('*','I');
		decoder.put('+','U');
		decoder.put('-','F');
		decoder.put('/','O');
		decoder.put('0','P');
		decoder.put('1','W');
		decoder.put('2','M');
		decoder.put('4','S');
		decoder.put('5','V');
		decoder.put('6','G');
		decoder.put('=','Y');
		decoder.put('A','R');
		decoder.put('B','A');
		decoder.put('C','I');
		decoder.put('D','Q');
		decoder.put('E','A');
		decoder.put('F','L');
		decoder.put('G','O');
		decoder.put('H','I');
		decoder.put('I','O');
		decoder.put('J','L');
		decoder.put('K','H');
		decoder.put('L','D');
		decoder.put('M','C');
		decoder.put('N','S');
		decoder.put('O','P');
		decoder.put('P','B');
		decoder.put('Q','S');
		decoder.put('R','I');
		decoder.put('S','E');
		decoder.put('T','T');
		decoder.put('U','R');
		decoder.put('V','Y');
		decoder.put('W','V');
		decoder.put('X','N');
		decoder.put('Y','D');
		decoder.put('Z','P');
		decoder.put('[','C');
		decoder.put('\\','E');
		decoder.put(']','G');
		decoder.put('b','U');
		decoder.put('c','M');
		decoder.put('e','C');
		decoder.put('f','M');
		decoder.put('g','F');
		decoder.put('h','U');
		decoder.put('i','T');
		decoder.put('j','R');
		decoder.put('k','N');
		decoder.put('l','B');
		decoder.put('m','B');
		decoder.put('n','K');
		decoder.put('o','A');
		decoder.put('p','H');
		decoder.put('q','L');
		decoder.put('r','V');
		decoder.put('s','W');
		decoder.put('t','N');
		decoder.put('u','W');
		decoder.put('v','T');
		decoder.put('w','Q');
		decoder.put('x','H');
		decoder.put('y','D');
		decoder.put('z','F');
		decoder.put('|','E');
	}
	
	static Map<Character, String> translator; 
	static {
		translator = new HashMap<Character, String>();
		translator.put('\\', "backslash");
		translator.put('[', "bracket-left");
		translator.put(']', "bracket-right");
		translator.put('=', "equals");
		translator.put('-', "hyphen");
		translator.put('.', "period");
		translator.put('|', "pipe");
		translator.put('+', "plus");
		translator.put('/', "slash");
		translator.put('*', "star");
		translator.put(',', "comma");
	}
	public static String imageFor(char ch) {
		String suffix = ".png";
		if (ch > 96 && ch < 123) return "l" + ch + suffix;
		String val = translator.get(ch);
		if (val != null) return val + suffix;
		return (ch + suffix).toLowerCase();
	}

	public static void dumpCipher() {
		
		Map<Character, Integer> counts = new HashMap<Character, Integer>();
		ValueComparator bvc =  new ValueComparator(counts);
		TreeMap<Character, Integer> sorted_map = new TreeMap<Character, Integer>(bvc);

		String html = "<style>td.a {text-align: center; color: #090; font-weight: bold; font-size: 14pt; font-family: courier;} td {font-size: 14pt;} </style>";
		String msg = "";
		
		html += "<a name=\"solution\"><h2>Solution</h2>";
		for (int row=0; row<cipher.length; row++) {
			
			String row1 = "";
			String row2 = "";
			//String shit = "";
			html += "<table>";
			for (int col=0; col<cipher[row].length(); col++) {
				char symbol = cipher[row].charAt(col);
				//shit += symbol;
				Character decoded = decoder.get(symbol);

				Integer val = counts.get(symbol);
				if (val == null) val = 0;
				val++;
				counts.put(symbol, val);
				
				msg += (decoded == null ? symbol : decoded);
				String cell;
				if (symbol == ' ') cell = "";
				else cell = imageHTMLFor(symbol);
				//shit += " " + decoded + " " + cell + "; ";
				row1 += "<td class='a'>" + cell + "</td>";
				row2 += "<td class='a'>" + (decoded == null ? symbol : decoded) + "</td>";
			}
			msg += System.getProperty("line.separator");
			
			html += "<tr>";
			html += row1;
			html += "</tr>";
			html += "<tr>";
			html += row2;
			html += "</tr>";
			html += "<tr>";
			html += "<td style=\"text-align: left; padding-left: 2em; color: #009; \" colspan=\"" + (cipher[row].length()) + "\">" + solution[row] + "</td>";
			html += "</tr>";
			
			html += "</table>";
			//System.out.println(shit);
		}

		html += "<hr><table><tr valign='top'><td>";
		html += "<a name=\"key\"><h2>Key</h2>";
		String a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i=0; i<a.length(); i++) {
			html += "<table><tr>";
			char letter = a.charAt(i);
			html += "<td class='a'>" + letter + "</td>";
			for (Character key : decoder.keySet())
				if (decoder.get(key) == letter) 
					html += "<td class='a'>" + imageHTMLFor(key) + "</td>";
			html += "</tr></table>";
		}
		html += "</td><td>";
		html += "<a name=\"cycles\"><h2>Homophone Cycles</h2>";
		for (int i=0; i<a.length(); i++) {
			html += "<table><tr>";
			char letter = a.charAt(i);
			html += "<td class='a'>" + letter + "</td>";
			html += "<td class='a'>";
			String symbols = p2c(letter);
			for (int row=0; row<cipher.length; row++) {
				for (int col=0; col<cipher[row].length(); col++) {
					Character ch = cipher[row].charAt(col);
					if (symbols.contains(""+ch)) {
						html += imageHTMLFor(ch);
					}
				}
			}
			html += "</td>";
			html += "</tr></table>";
		}
		
		html += "</td></tr></table><hr>";
		
		html += "<a name=\"counts\"><h2>Symbol Counts</h2>";
		Map<Character, Integer> sorted = sortByValue(counts);//sorted_map.putAll(counts);
		for (Character symbol : sorted.keySet()) {
			html += imageHTMLFor(symbol) + " " + sorted.get(symbol) + " ";
		}
		html += "<br>";
		
		
		/*html += "<pre>" + msg;
		html += "</pre>";*/
		System.out.println(html);
				
	}
	public static String p2c(char p) {
		String result = "";
		for (Character key : decoder.keySet()) {
			Character val = decoder.get(key);
			if (val == p) result += key;
		}
		return result;
	}
	public static String imageHTMLFor(Character symbol) {
		if (decoder.get(symbol) == null) return "" + symbol;
		return "<img alt=\"" + symbol + "\" title=\"" + symbol + "\" src=\"alphabet/" + imageFor(symbol) + "\">";		
	}
	static Map sortByValue(Map map) {
	     List list = new LinkedList(map.entrySet());
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o2)).getValue())
	              .compareTo(((Map.Entry) (o1)).getValue());
	          }
	     });

	    Map result = new LinkedHashMap();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	        Map.Entry entry = (Map.Entry)it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	} 	public static void main(String[] args) {
		dumpCipher();
		//System.out.println(p2c('Y'));
	}
	
	
	
}
class ValueComparator implements Comparator<Character> {

    Map<Character, Integer> base;
    public ValueComparator(Map<Character, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(Character a, Character b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}