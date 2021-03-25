package com.zodiackillerciphers.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.rest.beans.Cipher;

@RestController
public class CipherService {
	@GetMapping("/cipher")
	public Cipher cipher(@RequestParam(value = "name", defaultValue = "z340") String cipherName) {
		return new Cipher(Ciphers.Z340, "z340", "Zodiac's 340-character cipher");
	};

}
