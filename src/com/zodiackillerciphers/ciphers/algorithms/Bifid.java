package com.zodiackillerciphers.ciphers.algorithms;

/** http://practicalcryptography.com/ciphers/bifid-cipher */
public class Bifid {
	
	public static String encrypt(String plaintext, String keysquare, int period) {
		   // do some error checking
	    if(plaintext.length() < 1){ throw new RuntimeException("please enter some plaintext (letters only)"); }    
	    if(keysquare.length() != 25){ throw new RuntimeException("keysquare must be 25 characters in length"); }
	    if(keysquare.indexOf("j") >= 0){ throw new RuntimeException("key should not contain letter j (combine with i)."); }
	    plaintext = plaintext.toLowerCase();
	    keysquare = keysquare.toLowerCase();
	    if(period<=0){throw new RuntimeException("period should greater than 0"); }  
	    String ind = "12345";    String ct1 = ""; String ct2 = "";
	    for(int i=0; i<plaintext.length(); i++){
	        int index = keysquare.indexOf(plaintext.charAt(i));
	        ct1 += ind.charAt(index/5);
	        ct2 += ind.charAt(index%5);
	    }
	    //System.out.println("ct1 " + ct1);
	    //System.out.println("ct2 " + ct2);
	    int i = 0; String ct3 = "";
	    String bit=substring(ct1, i,period);
	    while(bit.length() > 0){
	        ct3 += bit + substring(ct2,i,period);
		    //System.out.println("ct3 " + ct3);
	        i+=period;
	        bit=substring(ct1,i,period);
	    }
	    //System.out.println("ct3 " + ct3);
	    String ciphertext = "";
	    for(i=0; i<ct3.length(); i+=2) { 
	    	int val = 5*(Integer.valueOf(""+ct3.charAt(i))-1);
	    	val += Integer.valueOf(""+ct3.charAt(i+1))-1;
	    	ciphertext += keysquare.charAt(val);
	    }
	    return ciphertext;
	}
	
	public static String decrypt(String ciphertext, String keysquare, int period) {
		   // do some error checking
	    if(ciphertext.length() < 1){ throw new RuntimeException("please enter some ciphertext (letters only)"); }    
	    if(keysquare.length() != 25){ throw new RuntimeException("keysquare must be 25 characters in length"); }
	    if(keysquare.indexOf("j") >= 0){ throw new RuntimeException("key should not contain letter j (combine with i)."); }
	    ciphertext = ciphertext.toLowerCase();
	    keysquare = keysquare.toLowerCase();
	    //period = ciphertext.length;
	    if(period<=0){throw new RuntimeException("period should greater than 0"); }  
	    String ind = "12345";   String pt1 = "";
	    for(int i=0; i<ciphertext.length(); i++){
	        int index = keysquare.indexOf(ciphertext.charAt(i));
	        pt1 += (ind.charAt(index/5)) + "" + (ind.charAt(index%5));
	        //d1 += index + " " + (index/5) + " " + ind.charAt(index/5) + " " + (index%5) + " " + ind.charAt(index%5) + "; ";
	    }
	    // pt1 32322312153512234211245522441222335441241242421224522441
	    int i = 0; String pt2 = ""; String pt3 = "";
	    while((pt1.length() - i) >= 2*period){
	        pt2 += substring(pt1,i,period);
	        pt3 += substring(pt1,i+period,period);
	        i+=2*period;
	    }
	    
	    int k = (pt1.length() - i)/2;
		if (k >= 1) {
			pt2 += substring(pt1, i, k);
			pt3 += substring(pt1, i+k, k);
		}
		
	    String plaintext = "";
		for (i = 0; i < pt2.length(); i++) { // i 0 pt2.charAt(i) 3 pt3.charAt(i) 3 (parseInt(pt2.charAt(i))-1)*5 + parseInt(pt3.charAt(i)-1) 102
			int val = ((Integer.valueOf(""+pt2.charAt(i)) - 1) * 5);
			val += Integer.valueOf(""+pt3.charAt(i)) - 1; 
			plaintext += keysquare.charAt(val);
		}
	    return plaintext;
	}

	/** equivalent to javascript substr() method */
	public static String substring(String str, int a, int b) {
		//System.out.println("a " + a + " b " + b + " str " + str + " len " + str.length());
		if (a >= str.length()) return "";
		if (a+b >= str.length()) return str.substring(a);
		return str.substring(a,a+b);
	}
	public static void test() {
		System.out.println(encrypt("defendtheeastwallofthecastle", "phqgmeaylnofdxkrcvszwbuti", 5));
		System.out.println(decrypt("ffyhmkhycpliashadtrlhcchlblr", "phqgmeaylnofdxkrcvszwbuti", 5));
		System.out.println(decrypt("oimzwtishdhwt", "oawntsldykhziefvrgpuxcqbm", 3));
	}
	
	public static void main(String[] args) {
		test();
	}
}
