package com.zodiackillerciphers.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotUtils;

/** Look for patterns in ciphers */
@RestController
@CrossOrigin
public class PatternsService {
	/** find pivots in the given cipher */
	@PostMapping("/pivots")
	public List<Pivot> pivots(@RequestParam(value = "cipher") String cipher,
			@RequestParam(value = "minsize", defaultValue = "3") int minsize) {
		return PivotUtils.findPivots(cipher, minsize);
	};
	
}
