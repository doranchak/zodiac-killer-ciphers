package com.zodiackillerciphers.rest.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.HomophonesResultBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotUtils;
import com.zodiackillerciphers.rest.beans.Cipher;
import com.zodiackillerciphers.rest.beans.HomophonesSummary;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.CipherTransformations;

@RestController
@CrossOrigin
public class HomophonesService {
//	/** return cipher with the given name */
//	@GetMapping("/cipher")
//	public Cipher cipher(@RequestParam(value = "name", defaultValue = "z340") String cipherName) {
//		return new Cipher(Ciphers.Z340, "z340", "Zodiac's 340-character cipher");
//	};
//
	static int MIN_REPS = 3;
	@PostMapping("/hom")
	public HomophonesSummary homophones(@RequestParam(value="cipher") String cipher, @RequestParam(value="length") int length) {
		cipher = URLDecoder.decode(cipher);
		System.out.println("CIPHER: " + cipher);
		HomophonesSummary hom = new HomophonesSummary();
		hom.setPcs(HomophonesNew.perfectCycleScoreFor(length, cipher, MIN_REPS, false));
		hom.setJarlveHomophoneScore(JarlveMeasurements.homScore(JarlveMeasurements.cipherToShort(cipher), 5));
		String a = Ciphers.alphabet(cipher);
		String[] alphabet = new String[a.length()];
		for (int i = 0; i < a.length(); i++)
			alphabet[i] = "" + a.charAt(i);
		List<HomophonesResultBean> beans = HomophonesNew.search(cipher, alphabet, length, true, true, 2, null, false);
		hom.setResults(beans);
		return hom;
	}
	
	public static void main(String[] args) {
		String cipher = Ciphers.Z408;
		System.out.println(HomophonesNew.perfectCycleScoreFor(2, cipher, MIN_REPS, false));
		System.out.println(JarlveMeasurements.homScore(JarlveMeasurements.cipherToShort(cipher), 5));
	}
}
