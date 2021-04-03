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

/** Generate stats on ciphers */
@RestController
public class StatsService {
}
