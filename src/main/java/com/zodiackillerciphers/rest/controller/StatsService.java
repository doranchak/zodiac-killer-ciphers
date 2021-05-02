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

/** Generate stats on ciphers */
@RestController
@CrossOrigin
public class StatsService {
	

	/** TODO: https://ciphermysteries.com/2008/05/11/the-dagapeyeff-cipher
	 * Along a row, the domain of digits alternates between [67890] and [12345]
	 * 
	 * Is there a way to generalize this observation?  I..e, detect 
	 * positions cipher symbols tend to appear in or avoid?
	 */
	
	/** TODO: "Suffixity":  http://ciphermysteries.com/2021/04/18/introducing-the-suffixity-metric-perhaps */
}


