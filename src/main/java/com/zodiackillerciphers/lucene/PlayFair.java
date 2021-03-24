package com.zodiackillerciphers.lucene;

public class PlayFair {
	
	/** initial keysquare */
	public static String square = "abcdefghiklmnopqrstuvwxyz";
	public static String solution = "wearediscoveredsaveyourselfx";
	
	public static int MAX_GEN = 20000;
	
	
	
	/** decrypt the playfair ciphertext with the given keysquare. adapted from http://practicalcryptography.com/ciphers/playfair-cipher/ */
	public static String decrypt(String ciphertext, String keysquare) {
	    if(ciphertext.length() < 1){ throw new RuntimeException("please enter some ciphertext (letters only)"); }   
	    if(ciphertext.length() % 2 != 0){ throw new RuntimeException("ciphertext length must be even."); }    
	    if(keysquare.length() != 25){ throw new RuntimeException("keysquare must be 25 characters in length [" + keysquare.length() + "]"); }

	    String plaintext = "";
	    for(int i=0; i<ciphertext.length(); i+=2){
	        char a = ciphertext.charAt(i); char b = ciphertext.charAt(i+1);
	        char c, d;
	        int row1 = keysquare.indexOf(a) / 5;
	        int col1 = keysquare.indexOf(a) % 5;
	        int row2 = keysquare.indexOf(b) / 5;
	        int col2 = keysquare.indexOf(b) % 5;
	        if(row1 == row2){
	            if(col1 == 0) c = keysquare.charAt(row1*5 + 4);
	            else c = keysquare.charAt(row1*5 + col1 - 1);
	            if(col2 == 0) d = keysquare.charAt(row2*5 + 4);
	            else d = keysquare.charAt(row2*5 + col2 - 1);
	        }else if(col1 == col2){
	            if(row1 == 0) c = keysquare.charAt(20 + col1);
	            else c = keysquare.charAt((row1-1)*5 + col1);
	            if(row2 == 0) d = keysquare.charAt(20 + col2);
	            else d = keysquare.charAt((row2-1)*5 + col2);
	        }else{
	            c = keysquare.charAt(row1*5 + col2);
	            d = keysquare.charAt(row2*5 + col1);
	        }
	        plaintext += "" + c + d;
	    }
	    return plaintext;

	}
	
	public static void testDecrypt() {
		System.out.println(decrypt("ugrmkcsxhmufmkbtoxgcmvatluiv","monarchybdefgiklpqstuvwxz"));
		System.out.println(NGrams.zkscore(new StringBuffer(decrypt("ugrmkcsxhmufmkbtoxgcmvatluiv","monarchybdefgiklpqstuvwxz"))));
	}
	
	public static StringBuffer swap(StringBuffer sb) {
		int a = (int) (Math.random()*sb.length());
		int b = a;
		while (b == a) {
			b = (int) (Math.random()*sb.length());
		}
		
		char tmp = sb.charAt(a);
		sb.setCharAt(a, sb.charAt(b));
		sb.setCharAt(b, tmp);
		//System.out.println(a+","+b+","+sb);
		
		int n = 3+(int)(Math.random()*sb.length()-3); // random spot from [3, len-3)
		StringBuffer result = new StringBuffer(sb.substring(0,n));
		char[] sorted = sb.substring(n).toCharArray();
		java.util.Arrays.sort(sorted);
		result.append(new String(sorted));
		
		//System.out.println(n+"," + sb + "," + sb.substring(n) + ":" + result + ":" + new String(sorted));
		return result;
		
	}
	
	public static void testSwap() {
		StringBuffer sb = new StringBuffer("abcdef");
		while (true) swap(sb);
	}
	
	public static int same(StringBuffer sb) {
		int count=0;
		for (int i=0; i<sb.length(); i++) if (sb.charAt(i) == solution.charAt(i)) count++;
		return count;
	}
	
	public static StringBuffer randomize() {
		StringBuffer sb = new StringBuffer(square);
		for (int i=0; i<100; i++) swap(sb);
		return sb;
	}
	public static void crack(String cipher) {
		//StringBuffer keysquare = randomize();
		StringBuffer keysquare = new StringBuffer("nabcdmelvifghkopqrstuxwyz"); // plxesehelpmeiamdroxnizngatwthemomentwthechildrenarersafefromthebomlbbecauseitzswsomaswsivetoizginandthetrigxrmecqwbxuiresmuceqorktogetztadiustediustrightbutifiholdbacktowolongfroumonineixiwbwblowosealbwcontrowolofucselfadsetwthebombnpwpleasehelpueicanbnotremainincontrolformuchwongxwe   19920.375
		
		float score = 19920.375f; //Float.MIN_VALUE;
		float scoreBest = 19920.375f; //Float.MIN_VALUE;
		
		int gen = 0;
		
		String best = null;
		while (true) {
			// swap random letters in the keysquare
			StringBuffer old = new StringBuffer(keysquare.toString());
			
			int i=1+(int)(Math.random()*10);
			for (int j=0; j<i; j++) keysquare = swap(keysquare);
			
			String decoded = decrypt(cipher, keysquare.toString());
			// if score is the same or better, keep the new keysquare
			
			//int bad = 0;
			//for (int n=2; n<6; n++) bad += NGrams.badNgraphs(n, new StringBuffer(decoded));
			float zk = NGrams.zkscore(new StringBuffer(decoded));
			//float zk = same(new StringBuffer(decoded));
			//float zk = 1/(1+(float)bad);
			
			if (zk >= score) {
				score = zk;
				best = "gen " + gen + " score " + score + " keysquare " + keysquare + " decoded " + decoded;
				if (score > scoreBest) {
					System.out.println("NEW BEST " + best);
					scoreBest = score;
				}
			} else keysquare = old;
			
			if (zk > score) gen = 0;

			gen++;
			if (gen > MAX_GEN) {
				//keysquare = randomize();
				keysquare = new StringBuffer("nabcdmelvifghkopqrstuxwyz");
				gen = 0;
				score = Float.MIN_VALUE;
				System.out.println(best);
				best = "";
			}
		}
	}
	
	public static void main(String[] args) {
		//testDecrypt();
		//testSwap();
		crack("rmagqvglmreledinthuaodafdqzrglifeldpzrglbkmvbtmabqlqqcgmhpfirolafihlaldbyplmzdrytkenrytvilztodoeabanroiqtlqaplvarxawzmqlpvyngxhtosfhiqdzbnmztpiamztptlhkrdzpmolohincbdoshzhifdhgthnffdmdlmzeblblihzhqvbelbdkdpthzhihpnvylvgnctiqzrgldhlnmuurvlcqlglvunlmdbacdfpslededmadfdpshigfplynrbfdqaxl");
		//crack("ugrmkcsxhmufmkbtoxgcmvatluiv");
		
		//String cipher = 
		//StringBuffer sb = new StringBuffer(cipher, key);
//		System.out.println(NGrams.zkscore(new StringBuffer("plxesehelpmeiamdroxnizngatwthemomentwthechildrenarersafefromthebomlbbecauseitzswsomaswsivetoizginandthetrigxrmecqwbxuiresmuceqorktogetztadiustediustrightbutifiholdbacktowolongfroumonineixiwbwblowosealbwcontrowolofucselfadsetwthebombnpwpleasehelpueicanbnotremainincontrolformuchwongxwe")));
		
	}
}
