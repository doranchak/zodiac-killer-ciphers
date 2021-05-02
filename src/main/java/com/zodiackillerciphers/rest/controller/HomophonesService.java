package com.zodiackillerciphers.rest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotUtils;
import com.zodiackillerciphers.rest.beans.Cipher;
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
	
}
