package com.zodiackillerciphers.rest.controller;

import java.net.URLDecoder;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotSummary;
import com.zodiackillerciphers.pivots.PivotUtils;

/** Look for patterns in ciphers */
@RestController
@CrossOrigin
public class PatternsService {
	/** find pivots in the given cipher */
	@PostMapping("/pivots")
	public PivotSummary pivots(@RequestParam(value = "cipher") String cipher,
			@RequestParam(value = "minsize", defaultValue = "4") int minsize) {
		cipher = URLDecoder.decode(cipher);
		return PivotUtils.findPivotsSummary(cipher, minsize);
	};
	
}
