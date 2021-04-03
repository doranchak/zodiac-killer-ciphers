package com.zodiackillerciphers.rest.controller;

import java.util.List;

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

/** Look for patterns in ciphers */
@RestController
public class PatternsService {
	/** find pivots in the given cipher */
	@PostMapping("/pivots")
	public List<Pivot> pivots(@RequestParam(value = "cipher") String cipher,
			@RequestParam(value = "minsize", defaultValue = "3") int minsize) {
		return PivotUtils.findPivots(cipher, minsize);
	};
	
}
