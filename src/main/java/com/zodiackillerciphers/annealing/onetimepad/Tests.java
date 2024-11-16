package com.zodiackillerciphers.annealing.onetimepad;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.ngrams.AZDecrypt;

public class Tests {
	
	/** CIPHERS WITH KNOWN KEYS */
	
	public static int[] key_test_1 = {6, 4, 6, 7, 16, 24, 24, 13, 11, 13, 22, 14, 19, 17, 9, 11, 4, 16, 17, 14, 17, 17, 8, 6, 5, 15, 13, 25, 3, 10, 7, 25, 14, 4, 8, 10, 13, 10, 0, 1, 8, 2, 18, 6, 1, 4, 24, 14, 2, 6, 8, 1, 4, 25, 14, 20, 10, 12, 13, 2, 0, 7, 9, 7, 25, 18, 20, 9, 24, 12, 9, 18, 5, 25, 10, 8, 0, 15, 9, 12};
	//	INCRYPTOGRAPHYTHEONETIMEPADOTPISANENCRYPTIONTECHNIQUETHATCANNOTBECRACKEDBUTREQUI
	//	THEUSEOFASINGLEUSEPRESHAREDKEYTHATISLARGERTHANOREQUALTOTHESIZEOFTHEMESSAGEBEINGS
	//	ENTINTHISTECHNIQUEAPLAINTEXTISPAIREDWITHARANDOMSECRETKEYALSOREFERREDTOASAONETIME
	//	PADTHENEACHBITORCHARACTEROFTHEPLAINTEXTISENCRYPTEDBYCOMBININGITWITHTHECORRESPOND
	//	INGBITORCHARACTERFROMTHEPADUSINGMODULARADDITIONTHERESULTINGCIPHERTEXTWILLBEIMPOS	
	public static String[] ciphers_test_1 = {
		"ORIYONRBREWDAPCSIEESKZUKUPQNWZPRORMXPBYQBKGTUIAVPOYVISVUDONPNVCIDULJAWNVGTDZEFDU",
		"ZLKBICMSLFEBZCNFWUGFVJPGWTQJHIAGOXQCYKRHMTLNBRMFGWCBPSCNRQFKZLXMSZYVCEBSLDLMICPE",
		"KRZPDRFVDGAQAERBYURDCRQTYTKSLCWZWVMNJSTIITSTESKGGIZFXJSSKXFQRLOLQJYMRAJKFNXMTXVQ",
		"VEJAXCLRLPDPBKXCGXRFRTBKWDSSKOWKOMVDRHTJAGFISCNHGJJZGNAVSZVPGPCDHLBCFQLGWQOAPDWP",
		"ORMIYRMENUWFTTCPVVICDKPKUPQTVSUFASLEYKRBLFAZJSLHJKZFWTZNSZTEIWQLQLYGRIRDQAOQMEXE"			
	};
	public static String[] evolved1 = { // solver found these because they score higher
			"GOARREZOGRAPHYTHEORG", // azscore: 14023.49 (actual plaintext: 19102.33)  (6grams: 12033.02 20116.33)
			"RICULTUFASINGLEUSETT", // 15999.65 (20080.87)
			"CORIGINISTECHNIQUEER", // 19072.40 (19961.41) (6grams: 19211.29 20978.86)
			"NBBTATTEACHBITORCHET", // 13185.56 (17894.05)
			"GOEBBIURCHARACTERFVQ", // 13809.55 (19160.48)			
	};
	public static String[] evolved2 = { // solver found these because they score higher
			"GOARREZOGRAPHYTHEONETIMEPERVDEXKANENTRACTIONTECORP", // 17879.25 (18604.68)
				// via 6 grams:  16408.62  (19295.91)
			"RICULTUFASINGLEUSEPRESHARIRRONIZATISCATTERTHANOYIX",
			"CORIGINISTECHNIQUEAPLAINTILASHESIREDNIVUARANDOMZIJ",
			"NBBTATTEACHBITORCHARACTERSTARTEDAINTVXVVSENCRYPAIK",
			"GOEBBIURCHARACTERFROMTHEPERBCXCYMODUCATNDDITIONALL"
	};
	public static void test1() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/5-grams_english_jarlve_reddit_v1912.gz", 5);
		double sum = 0;
		for (String cipher : ciphers_test_1) {
			String pt = Shift.shift(cipher, key_test_1, true);
			double zks = OTPSolution.score(pt);
			sum += zks;
			System.out.println(zks + " " + AZDecrypt.score(pt) + " " + pt);
		}
		System.out.println("Total: " + sum);
		sum = 0;
		for (String cipher : ciphers_test_1) {
			cipher = cipher.substring(0, 20);
			String pt = Shift.shift(cipher, key_test_1, true);
			double zks = OTPSolution.score(pt);
			sum += zks;
			System.out.println(zks + " " + AZDecrypt.score(pt) + " " + pt);
		}
		System.out.println("Total: " + sum);
		sum = 0;
		for (String pt : evolved1) {
			double zks = OTPSolution.score(pt);
			sum += zks;
			System.out.println(zks + " " + AZDecrypt.score(pt) + " " + pt);
		}
		System.out.println("Total: " + sum);
		sum = 0;
		for (String cipher : ciphers_test_1) {
			cipher = cipher.substring(0, 50);
			String pt = Shift.shift(cipher, key_test_1, true);
			double zks = OTPSolution.score(pt);
			sum += zks;
			System.out.println(zks + " " + AZDecrypt.score(pt) + " " + pt);
		}
		System.out.println("Total: " + sum);
		sum = 0;
		for (String pt : evolved2) {
			double zks = OTPSolution.score(pt);
			sum += zks;
			System.out.println(zks + " " + AZDecrypt.score(pt) + " " + pt);
		}
		System.out.println("Total: " + sum);
	}
	
	public static int[] key_test_2 = {16, 8, 23, 24, 14, 21, 3, 19, 0, 15, 14, 5};
	public static String[] ciphers_test_2 = {
			"SPXKDDRGSWWU",
			"EZDYBDCTTXCS",
			"FIORWXXEAGZD",
			"HMIYHDRGSWWU",
			"DMFEVWRKHDCI"
	};

	
	/** CIPHERS WITH UNKNOWN KEYS */
	
	// unknown
	public static int[] key_unknown_len_12 = {0,0,0,0,0,0,0,0,0,0,0,0}; // force length of 12 
	public static int[] key_unknown_len_19 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; // force length of 19	
	
	public static String[] ciphers_unknown_1 = {
		"PWFXRAMXCIGM",
		"VURTGTDQXLWK",
		"NVHAGOSIACPQ"			
	};
	public static String[] ciphers_unknown_2 = { // include entire ciphers, since trying offsets method
		"PWFXRAMXCIGMYRWLTJFECKUQSNTYBKWPIYFQGYZSGRZMZCBTEXSLVMJHUXJNWQZUOIRNVZTEFWNVSTAHNEKFN",
		"VURTGTDQXLWKZAZOQIO",
		"NVHAGOSIACPQAZHIASRUWBLSKSKWS"			
	};
	// assorted combinations
	public static String[] ciphers_unknown_combo_1 = {
			ciphers_unknown_2[0], ciphers_unknown_2[1]
	};
	public static String[] ciphers_unknown_combo_2 = {
			ciphers_unknown_2[0], ciphers_unknown_2[2]
	};
	public static String[] ciphers_unknown_combo_3 = {
			ciphers_unknown_2[1], ciphers_unknown_2[2]
	};
	
	public static void main(String[] args) {
		test1();
	}

}
