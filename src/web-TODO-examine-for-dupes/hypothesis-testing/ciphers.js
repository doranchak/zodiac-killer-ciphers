function hypothesisOnLoad() {
	updateCipher(); 
	makeDropdown();	
}

function $(id) {
	return document.getElementById(id);
}

var selected = 0;
function nextCipher() {
	var len = document.getElementById("drop").options.length;
	var sel = document.getElementById("drop").selectedIndex;
	if (sel < len-1) sel++;
	document.getElementById("drop").selectedIndex = sel;
	changeCipher();
}
function prevCipher() {
	var len = document.getElementById("drop").options.length;
	var sel = document.getElementById("drop").selectedIndex;
	if (sel > 0) sel--;
	document.getElementById("drop").selectedIndex = sel;
	changeCipher();
}
function changeCipher() {
	selected = document.getElementById("drop").selectedIndex;
	updateCipher();
}
function updateCipher() {
	ciphers[0] = ciphersNew[selected].ciphertext;
	ciphersNew[selected].render();
	ciphersNew[selected].highlightFeatures();
	buttons();
	buttonFeatures();
}

function makeDropdown() {
	var html = "<select onchange=\"changeCipher()\" id=\"drop\">";
	for (var i=0; i<ciphersNew.length; i++) {
		html += "<option>" + ciphersNew[i].name + "</option>";
	}
	html += "</select>";
	$("dropdown").innerHTML = html;
}

function getSortedKeys(obj) {
    var keys = []; for(var key in obj) keys.push(key);
    return keys.sort(function(a,b){return obj[a]-obj[b]});
}

var a;

function buttons() {
	a = [];
	var e = $("buttons");
	var html = "<div id=\"br1\"><span id=\"button-features\" class=\"button\" onmouseover=\"buttonFeatures()\">Pivots, box corners, <br>words, folds</span>";
	html += "<span id=\"button-bigrams\" class=\"button\" onmouseover=\"buttonBigrams()\">Bigrams (period 1)<br>(" + ciphersNew[selected].info["bigrams"] + " repeats)</span>";
	html += "<span id=\"button-bigrams19\" class=\"button\" onmouseover=\"buttonBigrams19()\">Bigrams (period 19)<br>(" + ciphersNew[selected].info["bigrams19"] + " repeats)</span>";
/*	html += "<span id=\"button-bigrams15\" class=\"button\" onmouseover=\"buttonBigrams15()\">Bigrams (flipped period 15)<br>(" + ciphersNew[selected].info["bigrams15"] + " repeats)</span>";*/
	html += "<span id=\"button-trigrams\" class=\"button\" onmouseover=\"buttonTrigrams()\">Trigrams (period 1)<br>(" + ciphersNew[selected].info["trigrams"] + " repeats)</span></div>";
	
	a = ["button-features","button-bigrams","button-bigrams19","button-trigrams"];	
	
	html += "<div id=\"br2\">Top 20 two-symbol cycles:</div>";
	for (var i=0; i<ciphersNew[selected].cycles.length; i++) {
		html += "<span id=\"button-cycle-" + i + "\" class=\"button\" onmouseover=\"buttonCycle(" + i + ")\">" + ciphersNew[selected].cycles[i].imgSymbol() + "</span>";
		a[a.length] = "button-cycle-" + i;
	}
	html += "</div>";
	html += "<div id=\"br3\">Unigrams:<br>";

    var map = ciphersNew[selected].unigrams();
	var keys = getSortedKeys(map).reverse();
	for (var i=0; i<keys.length; i++) {
		html += "<span id=\"button-unigram-" + i + "\" class=\"button\" onmouseover=\"buttonUnigram(" + i + ")\">" + ciphersNew[selected].imgSymbol(keys[i]) + " (" + map[keys[i]] + ")</span>";
		a[a.length] = "button-unigram-" + i;
	}
	html += "</div>";
	
	e.innerHTML = html;
}

function allOff() {

	for (var i=0; i<a.length; i++) {
		$(a[i]).className = "button";
	}
	
	$("sequence").innerHTML = "";
}

function buttonFeatures() {
	allOff();
	$("button-features").className = "button-on";
	ciphersNew[selected].highlightFeatures();
}
function buttonBigrams() {
	allOff();
	$("button-bigrams").className = "button-on";
	ciphersNew[selected].highlightBigrams();
}
function buttonBigrams19() {
	allOff();
	$("button-bigrams19").className = "button-on";
	ciphersNew[selected].highlightPeriod19Bigrams();
}
function buttonBigrams15() {
	allOff();
	$("button-bigrams15").className = "button-on";
	ciphersNew[selected].highlightPeriod15Bigrams();
}
function buttonTrigrams() {
	allOff();
	$("button-trigrams").className = "button-on";
	ciphersNew[selected].highlightTrigrams();
}
function buttonCycle(i) {
	allOff();
	$("button-cycle-" + i).className = "button-on";
	ciphersNew[selected].cycles[i].highlight();
	$("sequence").innerHTML = "Full sequence:<br>" + ciphersNew[selected].cycles[i].fullsequence(ciphersNew[selected].ciphertext);
}
function buttonUnigram(i) {
	allOff();
	
    var map = ciphersNew[selected].unigrams();
	var keys = getSortedKeys(map).reverse();
	$("button-unigram-" + i).className = "button-on";
    var map = ciphersNew[selected].highlightUnigrams(keys[i]);
}

function dumpAll() {
	var html = "<pre>";
	for (var i=0; i<ciphersNew.length; i++) {
		for (var row=0; row<20; row++) {
			html += ciphersNew[i].ciphertext.substring(row*17, row*17+17) + "<br>";
		}
		html += "<br><br>";
	}
 	html += "</pre>";

	$("dumpc").innerHTML = html;
	
}


function Cipher(name, ciphertext, pivots, boxCorners, words, foldMarks, bigrams, trigrams, period19Bigrams, period15Bigrams, cycles, info) {
	//name: Name/description of cipher
	this.name = name;
	//ciphertext: Cipher text in symbolic form
	this.ciphertext = ciphertext;

	this.pivots = pivots;
	this.boxCorners = boxCorners;
	this.words = words;
	this.foldMarks = foldMarks;
	this.bigrams = bigrams;
	this.trigrams = trigrams;
	this.period19Bigrams = period19Bigrams;
	this.period15Bigrams = period15Bigrams; // mirrored period 15
	this.cycles = cycles;
	this.info = info;

	this.highlightFeatures = function () {
		this.width(17);
		hsl_reset();
		hsl_random([pivots, boxCorners, words, foldMarks]);
	}
	this.highlightBigrams = function () {
		this.width(17);
		hsl_reset();
		hsl_random(bigrams);
	}
	this.highlightTrigrams = function () {
		this.width(17);
		hsl_reset();
		hsl_random(trigrams);
	}
	this.highlightPeriod19Bigrams = function () {
		this.width(19);
		ciphers[0] = ciphertext;
		init();
		hsl_reset();
		hsl_random(period19Bigrams);
	}
	this.highlightPeriod15Bigrams = function () {
		this.width(19);
		ciphers[0] = ciphertext;
		init();
		hsl_reset();
		hsl_random(period15Bigrams);
	}
	this.highlightUnigrams = function (u) {
		this.width(17);
		hsl_reset();
		var a = [];
		for (var i=0; i<ciphertext.length; i++) {
			if (ciphertext.charAt(i) == u) a[a.length] = i;
		}
		hsl_random([a]);
	}
	this.clearAll = function () {
		
	}
	this.render = function() {
		ciphers[0] = ciphertext;
		init();
	}
	this.width = function(w) {
		if (WIDTH == w) return;
		WIDTH = w;
		this.render();
	}
	this.unigrams = function() {
		var map = [];
		for (var i=0; i<ciphertext.length; i++) {
			var key = ciphertext.charAt(i);
			if (!map[key]) map[key] = 0;
			map[key]++;
		};
		return map;
	}
	this.imgSymbol = function(ch) {
//		return "ch " + ch + " getname " + getName(ch) + " ";
 		return "<img src=\"alphabet2/" + getName(ch) + ".jpg\">";
	}
}
function Cycle(sequence, posGood, posBad) {
	this.sequence = sequence;
	this.posGood = posGood;
	this.posBad = posBad;
	this.highlight = function () {
		hsl_reset();
		hsl_random([posGood, posBad]);
	}
	this.encode = function () {
		return sequence.replace(/[\u00A0-\u9999<>\&]/gim, function(i) {
	       return '&#'+i.charCodeAt(0)+';';
	    });		
	}
	this.imgSymbol = function() {
		var s = "";
		for (var i=0; i<this.sequence.length; i++) {
			s += "<img src=\"alphabet2/" + getName(sequence.charAt(i)) + ".jpg\">";
		}
		return s;
	}
	this.fullsequence = function(cipher) {
		var s = "";
		for (var j=0; j<cipher.length; j++) {
			for (var i=0; i<this.sequence.length; i++) {
				if (this.sequence.charAt(i) == cipher.charAt(j))
					s += "<img src=\"alphabet2/" + getName(sequence.charAt(i)) + ".jpg\">";
			}
		}
		return s;
	}
}

