package com.zodiackillerciphers.ciphers;

/**
 * https://www.facebook.com/postsecret/photos/a.10157040884090240/10158971184135240/
 */
public class PostSecretCipher {
	public static String[] cipher = new String[] { 
			"00010110110101111110110100111101010111100101010010010000",
			"00010101100101111110110101010011011111001101111100110000",
			"00010100100101011010010111111011011011001101111110110000",
			"0001000100010101111001011?001001010010100101111110110000",
			"00010001000101111010010101011001011011000101010110010000",
			"00011111101101001100010111101001010101100101111110110000",
			"00010001000101011110010100110001011111101101011110010000",
			"00011111101101011000010101011001010111100101101100010000",
			"00011111101101011110010100011001010000100101100010110000" };

	public static String toHtml() {
		String html = "";
		for (String c : cipher) {
			for (int i = 0; i < c.length(); i++) {
				char ch = c.charAt(i);
				String cl = ch == '0' ? "white" : "black";
				if (ch == '?')
					cl = "red"; // special case of missing value
				html += "<div class=\"" + cl + "\">&nbsp;</div>";
			}
			html += "<br>";

		}
		return html;
	}
	public static void main(String[] args) {
		System.out.println(toHtml());
	}
}
