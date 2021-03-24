import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zodiackillerciphers.ciphers.Ciphers;

public class Presentation {
	/** generate html for bar graph of symbol frequencies */
	public static String html(char cipher, Character plain, int freq, int num) {
		String html = "";
		html += "<tr class=\"qtr\" id=\"q" + num + "\">\n";
		html += "<th scope=\"row\">" + (plain == null ? "" : plain.toUpperCase(plain)) + "</th>\n";
		html += "<td class=\"sent bar\" style=\"height: " + (20 * freq) + "px;\"><span class=\"cipher\">" + cipher
				+ "</span></td>\n";
		html += "</tr>";
		return html;
	}

	public static void generateHtml() {
		Map<Character, Character> decoder = Ciphers.decoderMapFor(Ciphers.Z408, Ciphers.Z408_SOLUTION);
		Map<Character, Integer> counts = Ciphers.countMap(Ciphers.Z408);
		counts = sortByValue(counts);
		int num = 1;
		for (Character cipher : counts.keySet()) {
			System.out.println(html(cipher, decoder.get(cipher), counts.get(cipher), num++));
		}
	}
	public static void generateHtml2() {
		Map<Character, Integer> counts = Ciphers.countMap(Ciphers.Z340);
		counts = sortByValue(counts);
		int num = 1;
		for (Character cipher : counts.keySet()) {
			System.out.println(html(cipher, null, counts.get(cipher), num++));
		}
	}
	
	public static void generateCss(int size) {
		for (int i=1; i<=size; i++) {
			System.out.println("#q-graph #q" + i + " {");
			System.out.println("  left: " + (i*16) + "px;");
			System.out.println("}");
		}
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
	
	
	public static void main(String[] args) {
//		generateHtml2();
		generateCss(63);
	}

}
