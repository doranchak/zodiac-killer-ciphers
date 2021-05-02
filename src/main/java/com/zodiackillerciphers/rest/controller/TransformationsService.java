package com.zodiackillerciphers.rest.controller;

import java.util.List;

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

/** Perform transformations on ciphers */
@RestController
@CrossOrigin
public class TransformationsService {
	/** shuffle the given cipher */
	@PostMapping("/shuffle")
	public Cipher shuffle(@RequestParam(value = "cipher") String cipher) {
		return new Cipher(CipherTransformations.shuffle(cipher), "Shuffled", "Shuffled cipher", 17);
	};

	/** convert given string into z340-like transposition */
	@PostMapping("/z340transposition")
	public String z340transposition(@RequestParam(value = "cipher") String cipher) {
		return Periods.matrixUndo(cipher, Periods.transpositionMatrixZ340Solution());
	};

	/** convert given string from z340-like transposition */
	@PostMapping("/z340untransposition")
	public String z340untransposition(@RequestParam(value = "cipher") String cipher) {
		return Periods.matrixApply(cipher, Periods.transpositionMatrixZ340Solution());
	};
	
}
