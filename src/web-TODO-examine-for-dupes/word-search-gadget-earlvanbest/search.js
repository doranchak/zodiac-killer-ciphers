window.getWinSize= function(){
    if(window.innerWidth!= undefined){
        return [window.innerWidth, window.innerHeight];
    }
    else{
        var B= document.body, 
        D= document.documentElement;
        return [Math.max(D.clientWidth, B.clientWidth),
        Math.max(D.clientHeight, B.clientHeight)];
    }
}

var z340 = cipher[0].slice();
var z408 = cipher[1].slice();
var z408s = cipher[4].slice();

var translations = [];
translations["A"] = "A789^";
translations["B"] = "Bb";
translations["C"] = "Cc";
translations["D"] = "Dd";
translations["E"] = "Ee12";
translations["F"] = "Ff";
translations["G"] = "G";
translations["H"] = "H";
translations["I"] = "I!:;/\\|";
translations["J"] = "Jj";
translations["K"] = "Kk=";
translations["L"] = "Ll|/\\";
translations["M"] = "M";
translations["N"] = "N?^";
translations["O"] = "O()z0123456#%*@_.";
translations["P"] = "P&p";
translations["Q"] = "Qpq";
translations["R"] = "Rr";
translations["S"] = "S";
translations["T"] = "T+[tj";
translations["U"] = "UV";
translations["V"] = "V";
translations["W"] = "W";
translations["X"] = "X";
translations["Y"] = "Yy";
translations["Z"] = "Zz";


var directions = [];
directions[0] = [0,1];
directions[1] = [1,1];
directions[2] = [1,0];
directions[3] = [1,-1];
directions[4] = [0,-1];
directions[5] = [-1,-1];
directions[6] = [-1,0];
directions[7] = [-1,1];

var searchResults;

var searchResultSelected = 0;

var maxchanges = 3;
var shuffles = -1;

var maxresults = 500;

var uniqhash = [];
var uniqhashsize = 0;

var shuffled = false;
var visited;

var nearby = null;
var nearbyMode;

var imgsize;



String.prototype.replaceAt=function(index, character) {
    return this.substr(0, index) + character + this.substr(index+character.length);
}

function reset340() {
	shuffled = false;
	cipher[0] = z340.slice();
	which = 0;
	init();
	document.getElementById("search").value = "EARL VAN BEST JUNIOR";
	search("EARL VAN BEST JUNIOR", true);
}
function reset408() {
	shuffled = false;
	cipher[1] = z408.slice();
	which = 1;
	init();
	document.getElementById("search").value = "MUSTAFA";
	search("MUSTAFA", true);
}
function reset408s() {
	shuffled = false;
	cipher[1] = z408s.slice();
	which = 4;
	init();
	document.getElementById("search").value = "GARYSTEWART";
	search("GARYSTEWART", true);
}

/** randomly scramble the given text, using http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle */
function shuffle(cipher) {
	var sb = cipher;
	for (var i=sb.length-1; i>=1; i--) {
		var j = Math.floor(Math.random()*(i+1));
		if (i==j) continue;
		var tmp = sb.charAt(i);
		sb = sb.replaceAt(i, sb.charAt(j));
		sb = sb.replaceAt(j, tmp);
	}
	return sb;
}


function doShuffle(rend) {
	shuffled = true;
	var c = split(shuffle(cipher[which].join('')));
	cipher[which] = c;
	if (rend) {
		render();
		resize();
	}
}

function shufflesearch() {
	shuffles = 0;
	var word = document.getElementById('search').value;
	for (var i=0; i<1000; i++) {
		shuffles++;
		doShuffle(false);
		search(word, false);
		if (searchResults != null && searchResults.length > 0) {
			search(word, true);
			return;
		}
	}
	document.getElementById("results").innerHTML = "No matches in 1,000 shuffles.  Click button to try more shuffles."
}

function searchRender() {

	var html = "<i>Found [" + searchResults.length + "] matches</i> " + linkResults();
	html += "<center><div id=\"word\"></div></center>";
	if (shuffles > -1) {
		html += " after " + (shuffles+1) + " shuffles.";
		shuffles = -1;
	}
	
	html += "<select id=\"sr\" name=\"results\" onchange=\"hchange()\">";
	for (var i=0; i<searchResults.length; i++) {
		var result = searchResults[i];
		html += "<option value=\"" + i + "\">Match #" + (i+1) + "</option>";
	}
	html += "</select>";
	/*
	html += "<center><table id=\"rt\" class=\"results\">";
	html += "<tr><th>#</th></tr>"
	for (var i=0; i<searchResults.length; i++) {
		var result = searchResults[i];
		html += "<tr class=\"li_off\" id=\"li_" + i + "\" onmouseover=\"highlightresult(" + i + ", true)\"><td>Result " + (i+1) + "</td></tr>";
	}
	html += "</table></center>"*/
	html += "</center>";
	if (searchResults == null || searchResults.length == 0) html = "<i>Sorry, could not find it.  Try another search.</i>";
	document.getElementById("results").innerHTML = html;
	
	
}

function uniq(s) {
	var newS = new Array();
	var a = [];
	for (var i=0; i<s.length; i++) {
		var p = s[i].path.slice().sort().toString();
		if (a[p]) continue;
		newS[newS.length] = s[i];
		a[p] = true;
	}
	return newS;
}

function uniqadd(result) {
	var key = result.path.slice().sort().toString();
//	if (key == "7,10,7,11,7,8,7,9") alert(uniqhash[key]);
	var inc = 1;
	if (uniqhash[key]) {
		if (uniqhash[key].score() < result.score())
			return;
		inc = 0;	
	}
	uniqhash[key] = result;
	uniqhashsize += inc;
}

function btkword() {
	setStrict(0);
	
	var i = document.getElementById("wordselect").options.selectedIndex;
	
	var changes = 0;
	if (i==0) changes = 1;
	else if (btkwords[i] == "EMPTY") changes = 2;
	else if (btkwords[i] == "ADT") changes = 1;
	
	var jumps = 0;
	if (btkwords[i] == "SPOTVICTIM") jumps = 1;
	
	var word = btkwords[i];
	if (word == "ENTRY") word = "ENRTY";
	
	setChanges(changes);
	setJumps(jumps);
	
	setSearch(word);
	search(word, true);
	document.getElementById("wordselect").options.selectedIndex = i;
	
}
function z408word() {
	setStrict(0);
	
	var i = document.getElementById("wordselect").options.selectedIndex;
	
	var changes = 0;
	var jumps = 0;
	var word = z408words[i];
	
	setChanges(changes);
	setJumps(jumps);
	
	setSearch(word);
	search(word, true);
	document.getElementById("wordselect").options.selectedIndex = i;
}


function dump(list) {
	var s = "";
	for (var i=0; i<list.length; i++) {
		s += "(" + list[i] + ") ";
	}
	return s;
}

// word: word to search for
// index: current position within word
// path: current path under consideration
// row: current row
// col: current col
// drow: how many rows to change in each search step
// dcol: how many columns to change in each search step
// 
// returns true if the search is successful.  otherwise false.
function search2(searchword, index, path, row, col, drow, dcol) {
	if (searchword.length == index) {
		searchResults[searchResults.length] = new Result(searchword, path.slice());
//		alert("match " + searchResults.length);
		return true; // if we made it this far, a complete match was found.
	}
	
	// check for out of bounds.  
	if (row >= cipher[which].length) return false; 
	if (row < 0) return false;
	if (col >= cipher[which][0].length) return false;
	if (col < 0) return false;
	
	var letter = searchword.charAt(index);
	var symbols = translations[letter];
	if (symbols == null) return false; // there are no interpretations for this plaintext letter.  should not occur.
	
	var bycol = dcol != 0; // are we checking by row or column?
	
	var strip = "";
	if (bycol) 
		for (var r = 0; r < cipher[which].length; r++) 
			strip += cipher[which][r].charAt(col);
	else
		for (var c = 0; c < cipher[which][row].length; c++) 
			strip += cipher[which][row].charAt(c);
	// does the strip of cipher symbols contain any of the symbols that resemble the current letter?
	for (var i=0; i<symbols.length; i++) {
		var ch = symbols.charAt(i);
		var ind = strip.indexOf(ch);
		if (ind > -1) { // a match!
			// recurse to find more matches of the current word.
			var p = path.length;
			
			if (bycol) {
				path[p] = [ind, col];
			} 
			else {
				path[p] = [row, ind];
			}
			
//			alert("path [" + path.toString() + "] [" + path[p].toString() + "]");
//			alert(letter + " matches " + ch + " path " + path[p]);
			
//			alert (path[path.length-1][0]+","+path[path.length-1][1])
			if (typeof row == "undefined") {
				alert ("row undefined");
				return;
			}
			if (typeof col == "undefined") {
				alert ("col undefined");
				return;
			}
			if (typeof ind == "undefined") {
				alert ("ind undefined");
				return;
			}
			index++;
			var result = search2(searchword, index, path, row + drow, col + dcol, drow, dcol);
			index--;
			if (path.length > 0) {
				path.length--; // remove most recent path entry so we can backtrack
//				var newPath = path.slice(0, path.length-2); // remove most recent path entry so we can backtrack
//				path = newPath;
//				if (result) return true;
			}
		}
	}
			
}
function fix(word) {
	if (word == null) return null;
	var word2 = word.toUpperCase();
	var newWord = "";
	for (var i=0; i<word2.length; i++) {
		var ch = word2.charAt(i);
		if (ch >= 'A' && ch <= 'Z') newWord += ch;
	}
	return newWord;
}

function search(sword, rend) {
	var searchword = fix(sword);
	searchResults = [];
	// horizontal searches
	for (var col=0; col<cipher[which][0].length; col++) {
		search2(searchword, 0, [], 0, col, 0, 1); // start from the left
		search2(searchword, 0, [], 0, cipher[which][0].length-(col+1), 0, -1); // start from the right
	}
	// vertical searches
	for (var row=0; row<cipher[which].length; row++) {
		search2(searchword, 0, [], row, 0, 1, 0); // start from the top
		search2(searchword, 0, [], cipher[which].length-(row+1), 0, -1, 0); // start from the bottom
	}
	render();
	searchRender();
	if (searchResults.length > 0) highlightresult(0, true);
	
}

function debug(msg) {
	document.getElementById("debug").innerHTML += msg + "<br>";
}

function highlightresult(ind, on) {
	if (on) {
		showWord(searchResults[ind].path, searchResults[ind].word);
		highlightresult(searchResultSelected, false);
		searchResultSelected = ind;
	}
	var path = searchResults[ind].path;
	for (var i=0; i<path.length; i++) {
		if (on) darkenrc(path[i][0], path[i][1]);
		else lightenrc(path[i][0], path[i][1]);
	}
	
	var li = document.getElementById("li_" + ind);
	if (li) {
		if (on) li.className = "li_on";
		else li.className = "li_off";
	}
	
}

function Result(word, path) {
//	alert("new result " + word + ": " + path.toString());
	this.word = word;
	this.path = path;
}

function rget(name) {
	   if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
	      return decodeURIComponent(name[1]);
	}

function sv() {
	return document.getElementById("search").value;
}

function getElement(id) { return document.getElementById(id)}

function setSearch(word) {
	getElement("search").value = word;
}

function loadParams() {
	var word = rget("w");
	var strict = rget("s");
	var ciph = rget("c");
	var changes = rget("g");
	var jumps = rget("j");

	if (word) setSearch(word);
	if (ciph) {
		if (ciph.length == 1) {
			which = parseInt(ciph);
		} else {
			which = 0;
			cipher[0] = split(ciph);
		}
	}
}	
	
function linkResults() {
	var w = sv();
//	var s = ""+document.getElementById("strict").options.selectedIndex;
	var c = "" + (shuffled ? cipher[which].join('') : which);
//	var g = "" + document.getElementById("changes").value;
//	var j = "" + document.getElementById("maxj").options.selectedIndex;
	
	var uri = "?w=" + encodeURIComponent(w) + "&c=" + encodeURIComponent(c);
	var link = "http://zodiackillerciphers.com/word-search-gadget-earlvanbest/index.html" + uri;
	return "<a id=\"perm\" href=\"" + link + "\">(permalink)</a>";
	
}

function resize() {
	var size = window.getWinSize();
	var h = size[1];
	var w = size[0];
	var rows = cipher[which].length;
	var cols = cipher[which][0].length;
	var aspect = rows/cols;
	
	var hTarget = 0.9*h;
	var wTarget = hTarget / aspect;
	var wMax = w * 0.7;
	if (wTarget > wMax) hTarget = wMax * aspect;
	
	imgsize = hTarget/rows * 0.95;
	
	
	for (var row=0; row<rows; row++) {
		for (var col=0; col<cols; col++) {
			var img = document.getElementById(row+"_"+col).children[0];
			img.width = imgsize;
			img.height = imgsize;
			var td = document.getElementById(row+"_"+col);
//			td.style.lineHeight = imgsize;
//			td.style.height = imgsize;
//			td.style.width = imgsize;
			var tr = td.parentNode;
//			tr.style.height = imgsize;
//			tr.style.lineHeight = imgsize;
			
		}
	}
}

function hchange() {
	highlightresult(document.getElementById("sr").options.selectedIndex, true);
}

function onload() {
	search(document.getElementById('search').value, true)
}
