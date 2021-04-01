var block; // integer-indexed 2d array of characters
var highlights = []; // 2d array of highlight locations
var ciphertext; // 1d string of ciphertext
var ciphertextRow, ciphertextCol;
var cipherblock; // 2d strings of ciphertext
var faded; // which symbols are hidden/faded
var symbolcounts;
var symbolcountsmax;

var repeatrow;
var repeatcol;
var maxrepeatrow;
var maxrepeatcol;

var tmprrow;
var tmprcol;
var tmphomophones;

var only10 = true;
var only10b = true;
var only10c = true;
//var only10d = true;

var bylength;
var tmpa;

var tmpstrings;

var LOG2 = .693147;

var nrbests;

// hover modes:  0 = info, 1 = quad
var hovermode = 0;
var quadI, quadJ;

var repsn = 20;

var symboltotals;

var threshMost = 9;
var threshLeast = 5;

var ngramsRepeats;

function keyup() {
resizeIt();
update()	
}

function initStats() {
	var g = gup("cipher");
	if (g == null || g == "")
		getcipher(cipher[0]);
	else {
		d("input").value = unescape(g);
		update();
	}
		
}

// what to do when the user clicks the cipher text
function clickaction() {
	if (hovermode == 0) {
		showhide("col1");
		showhide("col2");
	} else if (hovermode == 1) { // update cipher text with new quadrants
		var b = []; 
		for (var row=0; row<block.length; row++) {
			b[row] = [];
			for (var col=0; col<block[0].length; col++) {
				var rc = tr_quadrant(row, col, quadI, quadJ, false, [0,1,2,3]); b[row][col] = block[rc[0]][rc[1]];
			}
		}	
		updateblock(b);
		hovermode = 0;
	}
}

function flipeditdone() {
	update();
	showhide("col1");
	showhide("col2");
	if (hovermode == 1) {
		showhide("quad1");
		showhide("quad2");
	}
}

// fill out a cipher with spaces to make it fill the block perfectly
function padinput() {
	var inp = $("input").value.rtrim().ltrim();
	var split = inp.split("\n");
	var max = 0;
	for (var i=0; i<split.length; i++) max = Math.max(max, split[i].length);
	
	var inpnew = "";
	for (var i=0; i<split.length; i++) {
		inpnew += split[i];
		for (var j=0; j<max-split[i].length; j++) inpnew += " ";
		inpnew += "\n"
	};
	$("input").value = inpnew;
}

function updateblock(newBlock) {
	var inp = "";
	for (var row=0; row<newBlock.length; row++) {
		for (var col=0; col<newBlock[0].length; col++) {
			inp += newBlock[row][col];
		}
		inp += "\n";
	}
	$("input").value = inp;
	update();
}

function update() {
	sequenceCache = {};
	padinput();
	var s = "";
	var c = d("input").value.rtrim().ltrim();
	cipherblock = c.split("\n"); 
	ciphertext = "";
	
	block = []; block[0] = [];
	var row = 0; var col = 0;
	for (var i=0; i<c.length; i++) {
		var ch = c.charAt(i);
		if (ch == '\n') { 
			s += ch;
			row++; col=0;
			block[row] = [];
			continue;
		}
		if (escape(ch) == "%0D") continue; // stupid IE7
		block[row][col] = ch;
		ciphertext += ch;
		col++;
		
	}
	
	resetHighlights();
	renderBlock(block);
	renderAlphabet();
	renderPermalink();
	renderNGrams();
	renderRepsChart();
	renderNonRepeats();
	renderTrans();
	renderIOC();
//	renderContacts();
	
	resetcontrol('repeats');
	resetcontrol('seq');
	resetcontrol('dsearch');
	resetcontrol('homo');
	resetcontrol('homo2');
	resetcontrol('homo3');
	resetcontrol('contacts');
}

function density() {
	for (var row=0; row<block.length; row++) {
		for (var col=0; col<block[row].length; col++) {
			var rank = (1-symbolcounts[block[row][col]]/symbolcountsmax);
			var color = Math.round(rank*255);
			setbg(row, col, color);
		}
	}
}

function rowrepeatmap() {
	for (var row=0; row<block.length; row++) {
		var rank = 1-repeatrow[row]/maxrepeatrow;
		var color = Math.round(rank*255);
		for (var col=0; col<block[row].length; col++) setbg(row, col, color);
	}
}

function colrepeatmap() {
	for (var col=0; col<block.length; col++) {
		var rank = 1-repeatcol[col]/maxrepeatcol;
		var color = Math.round(rank*255);
		for (var row=0; row<block.length; row++) setbg(row, col, color);
	}
}

function firsts() {
	hclear();
	var a = {};
	for (var row=0; row<block.length; row++) {
		for (var col=0; col<block[row].length; col++) {
			if (!a[block[row][col]]) {
				hcell(row, col);
				a[block[row][col]] = true;
			}
		}
	}
}

function setbg(row, col, color, ignorefont) {
	var font = (color > 127 ? "black" : "white");
	$("r"+row+"c"+col).style.backgroundColor = "rgb("+color+","+color+","+color+")";
	if (!ignorefont) $("r"+row+"c"+col).style.color = font;
}

function hsearchall(n) {
	hclear();
	var map = ngrammaps[n];
	if (!map) return;
	for (var key in map) if (map[key] > 1) hsearchrand(key);
}

var ngrammaps;
function renderNGrams() {
	ngrammaps = {};
	ngramRepeats = [];
	var dateStart = new Date();
	symbolcountsmax = 0;
	var n=1; var go=false;
	var html = '<table><tr valign="top">';
	
	var maxxes = []; var ds = []; var lab = []; 
	do {
		var allcount = 0; var othercount = 0;
		go = false;
		var map = ngrams(ciphertext, n);
		ngrammaps[n] = map;
		for (var key in map) {
			allcount += map[key];
			if (map[key] > 1) othercount += map[key];
		}
		if (n==1) symbolcounts = map;
		var keys = [];
		for (var key in map) keys[keys.length] = key;
		var sorted = sortByValue(keys, map);
		if (n==1) symbolcountsmax = map[sorted[0]];
		

		var max, repeats=0, uniqueRepeats=0;
		
		var html3 = ""; ds[n-1] = ""; lab[n-1] = "";
		for (var i=0; i<sorted.length; i++) {
			var show = only10 ? i<10 : true;
			count = map[sorted[i]];
			if (i<10 && count > 1) {
				othercount -= count;
				ds[n-1] += "" + count + (othercount > 0 ? "," : "");
				lab[n-1] += cencodestr(sorted[i]); lab[n-1] += (othercount > 0 ? "|" : "");
			}
			var class1, class2;
			if (i==0) {
				max = count;
				maxxes[n-1] = max;
				class1=' class="first"';
				class2=' class="value first"';
			} else {
				class1=''; class2=' class="value"';
			}
			
			if (n==1 || count > 1) {
				go = true;
				if (show) html3 += '<tr><td' + class1 + ' style="width: 1em" align="right"><a href="javascript:hsearch(\'' + sorted[i] + '\')">' + encode(sorted[i]) + '</a></td><td' + class2 + '><img src="bar.png" alt="" width="' + Math.round(100*count/max) + '" height="16"/>' + count + '</td></tr>'
			}
			if (count > 1) {
				repeats += count;
				uniqueRepeats++;
			}
		}
		if (othercount > 0) {
			ds[n-1] += othercount; maxxes[n-1] = Math.max(maxxes[n-1], othercount);
			lab[n-1] += "other";
		}

		ngramRepeats.push(uniqueRepeats);

		var html2 = "";
		html2 += "<td><table class=\"chart\" cellspacing=\"0\" cellpadding=\"0\">"; 
		html2 += "<caption align=\"top\"><b>" + allcount + "</b> total " + n + "-grams.  <b>" + keys.length + "</b> are unique.  ";
		html2 += "<b>" + uniqueRepeats + "</b> of them repeat, for a total of <b>" + repeats + "</b> repeats, which cover <b>" + Math.round(10000*(n*repeats/ciphertext.length))/100 + "%</b> of the ciphertext.<br>";
		html2 += "<a href=\"#\" onclick=\"hsearchall(" + n + ")\">(Highlight all)</a>";
		html2 += "</caption>";
		html2 += html3;
		html2 += '</table><div style="width: 100px" id="chart' + n + '"/><div id="legend' + n + '"/></td>';
		
		n++;
		if (go) html += html2;
	} while (go);
	if (only10) html += '<tr><td colspan=3><i><a href="javascript:only10=false;renderNGrams();">(Show All)</a></i></td></tr>';
	else html += '<tr><td colspan=3><i><a href="javascript:only10=true;renderNGrams();">(Show top 10 only)</a></i></td></tr>';
	html += "</tr></table>"
	
	
	html += '<table><tr>';
	for (var i=0; i<maxxes.length-1; i++) { //alert(ds[i]);
		var chart = '<td><img src="http://chart.apis.google.com/chart?chs=300x200&cht=p&chds=0,' + maxxes[i] + '&chd=t:' + ds[i] + '&chl=' + lab[i] + '&chtt=Top%20Repeated%20' + (i+1) + '-grams" width="300" height="200" alt="ngrams chart" /></td>';
		html += chart;
	}
	html += '</tr></table>';
	
	d("ngrams").innerHTML = html + timing(dateStart, new Date());
}

// fix up string to pass it in google charts URLs
function cencodestr(str) {
	var s = str;
	while (s.indexOf("|") > -1) s = s.replace("|","!");
	return encodeURIComponent(s);
}

function renderTrans() {
	var dateStart = new Date();
	var n=2; var go=false;
	var html = '<table><tr valign="top">';
	
	do {
		go = false;
		var maps = ngramstrans(ciphertext, n);
		if (!maps[2]) {
			n++;
			go = n<11;
			continue;
		}
		var map = maps[0];
		var keys = [];
		for (var key in map) keys[keys.length] = key;
		var sorted = sortByValue(keys, map);
		

		var max, repeats=0, uniqueRepeats=0;
		var html3 = "";

		var foundone = false;
		for (var i=0; i<sorted.length; i++) {
			var show = only10c ? i<10 : true;
			count = map[sorted[i]];
			var class1, class2;
			if (i==0) {
				max = count;
				class1=' class="first"';
				class2=' class="value first"';
			} else {
				class1=''; class2=' class="value"';
			}

			if (n==1 || count > 1) {
				foundone = true;
				if (show) {
					html3 += '<tr valign="top"><td' + class1 + ' style="width: 1em" align="right"><span style="white-space: nowrap">';
					var matches = [];
					for (var key in maps[1][sorted[i]]) {
						matches[matches.length] = key;
					}
					for (var j=0; j<matches.length; j++) {
						html3 += '<a href="javascript:hsearch(\'' + matches[j] + '\')">' + encodestr(matches[j]) + '</a>';
						if (j<matches.length-1) html3 += ", ";
					}
					html3 += '</span></td><td' + class2 + '><img src="bar.png" alt="" width="' + Math.round(100*count/max) + '" height="16"/>' + count + '</td></tr>';
				}
			}
			if (count > 1) {
				repeats += count;
				uniqueRepeats++;
			}
		}

		var html2 = "";
		if (foundone) {
					html2 += "<td><table class=\"chart\" cellspacing=\"0\" cellpadding=\"0\">"; 
			//		html2 += "<caption align=\"top\">There are <b>" + keys.length + "</b> unique " + n + "-grams.  ";
			//		html2 += "<b>" + uniqueRepeats + "</b> of them repeat, for a total of <b>" + repeats + "</b> repeats, which cover <b>" + Math.round(100*(n*repeats/ciphertext.length)) + "%</b> of the ciphertext.";
			//		html2 += "</caption>";
					html2 += html3;
					html2 += '</table><div style="width: 100px" id="chart' + n + '"/><div id="legend' + n + '"/></td>';
		}
		
		html += html2;
		n++;
		go = n < 11;
	} while (go);
	if (only10c) html += '<tr><td colspan=3><i><a href="javascript:only10c=false;renderTrans();">(Show All)</a></i></td></tr>';
	else html += '<tr><td colspan=3><i><a href="javascript:only10c=true;renderTrans();">(Show top 10 only)</a></i></td></tr>';
	html += "</tr></table>"
	
	d("trans").innerHTML = html + timing(dateStart, new Date());
	
}

function renderPermalink() {
	d("perma").innerHTML = "<a href=\"http://oranchak.com/zodiac/webtoy/stats.html?cipher=" + escape(d("input").value) + "\">(Permalink)</a>";
}

function resetHighlights() {
	for (var row=0; row<block.length; row++) {
		highlights[row] = [];
		for (var col=0; col<block[row].length; col++) 
			highlights[row][col] = false;
		}
}

function infocount(symbol) {
	var count = 0;
	for (var i=0; i<ciphertext.length; i++) if (ciphertext.charAt(i) == symbol) count++;
	return count;
}

function hoveron(row, col) {
	
	if (hovermode == 0) { // info mode
		var html = "<center>Row <b>" + row + "</b> Col <b>" + col + "</b> Pos <b>" + (row*block[0].length+col) + "</b> Symbol <b>" + encodestr(block[row][col]) + "</b> Count <b>" + infocount(block[row][col]) + "</b></center>";
		$("info").innerHTML = html;
	} else if (hovermode == 1) { // quad mode
		quadrantHighlight(row, col);
		quadrantDimensions(row, col);
		quadI = row; quadJ = col;
	}
}

function hoveroff(row, col) {
	
	if (hovermode == 0) { // info mode
		$("info").innerHTML = "";
	} else if (hovermode == 1) { // quad mode
		
	}
	
}

function quadrants() {
	showhide("quad1");
	showhide("quad2");
	hovermode = 1;
}

function quadrantDimensions(i, j) {
	var H = block.length; var W = block[0].length;
	
	var html = '<center><span style="color: #f00">' + i + " X " + j + '</span>&nbsp;&nbsp;';
	html += '<span style="color: #0f0">' + i + " X " + (W-j) + '</span>&nbsp;&nbsp;';
	html += '<span style="color: #00f">' + (H-i) + " X " + j + '</span>&nbsp;&nbsp;';
	html += '<span style="color: #0ff">' + (H-i) + " X " + (W-j) + '</span></center>';
	$("info").innerHTML = html;
	
}

function quadrantHighlight(i, j) {
	for (var row=0; row<block.length; row++) {
		for (var col=0; col<block[0].length; col++) {
			var e = d("r"+row+"c"+col);
			var btop = "none"; var bleft="none";
			if (row == i) btop = "thin solid #900";
			if (col == j) bleft = "thin solid #900"
			e.style.borderTop = btop;
			e.style.borderLeft = bleft;
			var bg;
			if (row < i && col < j) bg = "#fcc";
			else if (row < i && col >= j) bg = "#cfc";
			else if (row >= i && col < j) bg = "#ccf";
			else bg = "#aff";
			e.style.backgroundColor = bg;
		}
	}
}


function renderBlock(block) {
	ciphertextRow = []; ciphertextCol = [];
	s = '<span onclick="clickaction()">';
	s += '<table class="rendertable">'
	for (var row=0; row<block.length; row++) {
		ciphertextRow[row] = "";
		s += '<tr>';
		var cl;
		for (var col=0; col<block[row].length; col++) {
			if (row == 0) ciphertextCol[col] = "";
			if (col == -1 && row == -1) {
				s += "<span class=\"celln\"/>";
			} else if (col == -1) {
				s += "<span class=\"celln\">" + row + "</span>";
			} else if (row == -1) {
				s += "<span class=\"celln\">" + col + "</span>";
			} else {
				var style = "";
				if (faded && faded[row] && faded[row][col]) style="style=\"color: #eee\"";
//				s += '<span onmouseover="hoveron(' + row+','+col+')" onmouseout="hoveroff(' + row+','+col+')" class="cell" id="r' + row + 'c' + col + '">';
				s += '<td onmouseover="hoveron(' + row+','+col+')" onmouseout="hoveroff(' + row+','+col+')" class="cell" id="r' + row + 'c' + col + '" ' + style + '>';
				var ch = block[row][col];
				s += encode(ch);
//				s += "</span>";
				s += '</td>';
				
				ciphertextRow[row] += ch;
				ciphertextCol[col] += ch;
			}
		}
		//s+="<br>";
		s+='</tr>';
	}
	s += '</table></span>';
	s += '<span id="info">&nbsp;</span>';
	s += '<br><center style="white-space: nowrap"><a title="Rotate 90 degrees clockwise" href="javascript:r90cw()"><img border="0" src="r90cw-20.gif"></a>';
	s += '<a title="Rotate 90 degrees counter-clockwise" href="javascript:r90ccw()"><img border="0" src="r90ccw-20.gif"></a>';
	s += '<a title="Flip vertically" href="javascript:fv()"><img border="0" src="fv-20.gif"></a>';
	s += '<a title="Flip horizontally" href="javascript:fh()"><img border="0" src="fh-20.gif"></a>';
	s += '<a title="Quadrant mode" href="javascript:quadrants()"><img id="quad1" border="0" src="quad.gif"><img id="quad2" style="display:none" border="0" src="quad2.gif"></a><br/>';
	s += '<a href="javascript:hclear()">(clear highlights)</a></center>';
	
	
	d("render").innerHTML = s;
	resizeIt();
}

function space(num) {
	var r = 1;
	for (var i=0; i<num; i++) r*=26;
	return r;
}

function renderAlphabet() {
	var dateStart = new Date();
	
	var a = alphabetGet();
	repeatrow = [];
	repeatcol = [];
	maxrepeatrow = 0;
	maxrepeatcol = 0;
	
	var rrow;
	var rcol;
	
	var max = 0;
	for (var i=0; i<block.length; i++) max = Math.max(max, block[i].length);
	
	var s = "<p><i>Click cipher text to edit it.</i></p><p>Characters: <b>" + ciphertext.length + "</b>, Rows: <b>" + block.length + "</b>, Columns: <b>" + max + "</b>.</p><p><b>" + a.length + "</b> unique symbols in this cipher's alphabet: ";
	for (var i=0; i<a.length; i++) {
		s += "<a href=\"javascript:halpha(" + i +")\">" + encode(a[i]) + "</a> ";
	}
	s += "  Multiplicity (symbols divided by length): <b>" + Math.round(1000 * a.length / ciphertext.length)/1000 + "</b> &middot; ";
	var sp = space(a.length);
	s += " Key search space (assuming 26 plaintext letters): <b><a target=\"_new\" href=\"http://www.wolframalpha.com/input/?i=" + (""+sp).replace("+","%2B") + "\">"+ sp + "</a></b>."
	s += "</p><p>";

	s += "Repeated symbols by row: ";
	var repeatrowsymbols = [];
	for (var row=0; row<block.length; row++) {
		var u = {}; var repeats = 0;
		rrow = {};
		for (col=0; col<block[row].length; col++) {
			if (u[block[row][col]]) {
				repeats++;
				rrow[block[row][col]] = true;
			}
			u[block[row][col]] = true;
		}
		repeatrow.push(repeats);
		repeatrowsymbols.push(rrow);
		maxrepeatrow = Math.max(maxrepeatrow, repeats);
		s += '<a href="#" onmouseover="hrow(' + row + ')" onmouseout="crow(' + row + ')">' + repeats + "</a> ";
		for (var ch in rrow) s+= '<a class="rs" href="#" onmouseover="hsearchrow(' + row + ',\'' + ch.replace("\\","\\\\") + '\')" onmouseout="crow(' + row + ')">' + ch + "</a>";
		s += " &middot; ";
	}
	
	var chart3 = '<img src="http://chart.apis.google.com/chart?chxr=0,0,' + maxrepeatrow + '|1,0,' + (block.length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=0,'+ maxrepeatrow + '&chd=t:' + (""+repeatrow) + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Reps%20by%20Row" width="150" height="112" alt="" />';
	
	s += "<br>Repeated symbols by column: ";
	var repeatcolsymbols = [];
	for (var col=0; col<block[0].length; col++) {
		var u = {}; var repeats = 0;
		rcol = {};
		for (row=0; row<block.length; row++) {
			if (u[block[row][col]]) {
				repeats++;
				rcol[block[row][col]] = true;
			}
			u[block[row][col]] = true;
		}
		repeatcol.push(repeats);
		repeatcolsymbols.push(rcol);
		maxrepeatcol = Math.max(maxrepeatcol, repeats);
		s += '<a href="#" onmouseover="hcol(' + col + ')" onmouseout="ccol(' + col + ')">' + repeats + "</a> ";
		for (var ch in rcol) s+= '<a class="rs" href="#" onmouseover="hsearchcol(' + col + ',\'' + ch.replace("\\","\\\\") + '\')" onmouseout="ccol(' + col + ')">' + ch + "</a>";
		s += " &middot; ";
	}
	
	var chart4 = '<img src="http://chart.apis.google.com/chart?chxr=0,0,' + maxrepeatcol + '|1,0,' + (block[0].length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=0,'+ maxrepeatcol + '&chd=t:' + (""+repeatcol) + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Reps%20by%20Col" width="150" height="112" alt="" />';

	tmprrow = [];
	tmprcol = [];
	
	s += "<br>Column repeats by row: ";
	var crrmax = 0; var ds = "";
	for (var row=0; row<block.length; row++) {
		tmprrow[row] = [];
		var count = 0;
		for (var col=0; col<block[row].length; col++) {
			var ch = block[row][col];
			if (repeatcolsymbols[col][ch]) { // this row's character at this column position is repeated somewhere else in the column
				count++;
				tmprrow[row].push([col, ch]);
			}
		}
		crrmax = Math.max(crrmax, count);
		ds += count + (row != block.length-1 ? "," : "");
		s += '<a href="#" onmouseover="hrepeatrow(' + row + ')" onmouseout="hclear()">' + count + "</a> ";
	}

	var chart1 = '<img src="http://chart.apis.google.com/chart?chxr=0,0,' + crrmax + '|1,0,' + (block.length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=0,'+ crrmax + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Col%20Reps%20by%20Row" width="150" height="112" alt="" />';

	s += "<br>Row repeats by column: ";
	var rrcmax = 0; ds = "";
	for (var col=0; col<block[0].length; col++) {
		tmprcol[col] = [];
		var count = 0;
		for (var row=0; row<block.length; row++) {
			if (!block[row]) alert(row);
			var ch = block[row][col];
			if (repeatrowsymbols[row][ch]) { // this col's character at this row position is repeated somewhere else in the row
				count++;
				tmprcol[col].push([row, ch]);
			}
		}
		rrcmax = Math.max(rrcmax, count);
		ds += count + (col != block[0].length-1 ? "," : "");
		s += '<a href="#" onmouseover="hrepeatcol(' + col + ')" onmouseout="hclear()">' + count + "</a> ";
	}
	s += "<br><input type=\"button\" onclick=\"hideLeast()\" value=\"Hide symbols\"> that occur fewer than <input id=\"threshLeast\" size=\"3\" value=\"" + threshLeast + "\"> times. <input type=\"button\" onclick=\"threshReset();\" value=\"Reset\">";
	s += "<br><input type=\"button\" onclick=\"hideMost()\" value=\"Hide symbols\"> that occur more than <input id=\"threshMost\" size=\"3\" value=\"" + threshMost + "\"> times. <input type=\"button\" onclick=\"threshReset();\" value=\"Reset\">";

	var chart2 = '<img src="http://chart.apis.google.com/chart?chxr=0,0,' + rrcmax + '|1,0,' + (block[0].length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=0,'+rrcmax + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Row%20Reps%20by%20Col" width="150" height="112" alt="" />';
	
	// generate chart data for appearance of new symbols
	ds = "";
	var armax = 0;
	var u = {};
	for (var i=0; i<ciphertext.length; i++) {
		var ch = ciphertext.charAt(i);
		if (!u[ch]) armax++;
		ds += ""+armax; if (i<ciphertext.length-1) ds += ",";
		u[ch] = true;
	}
	var chart5 = '<img src="http://chart.apis.google.com/chart?chxr=0,0,' + armax + '|1,0,' + (ciphertext.length-1) + '&chxt=y,x&chs=300x225&cht=lc&chco=3D7930&chds=0,'+armax + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Appearance%20of%20new%20symbols" width="300" height="225" alt="" />';
	
	s += '<table style="float:right"><tr><td>' + chart3 + '</td><td>' + chart4 + '</td></tr><tr><td>' + chart1 + '</td><td>' + chart2 + '</td></tr>';
	s += '<tr><td colspan="2">' + chart5 + '</td></tr>';
	s += '</table>';
	
	s += "</p><p>";
	s += '<a href="javascript:density()">Symbol density map</a> | <a href="javascript:rowrepeatmap()">Row repeat map</a> | <a href="javascript:colrepeatmap()">Column repeat map</a> | <a href="javascript:firsts()">Highlight first occurrences of symbols</a>';	
	//s += ' | <a href="javascript:accumulation()">Repetition accumulation map</a>';
	s += "</p>";
	s += "</p><p>";
	
	s += "Other block layouts (row x col): ";
	var f = factors();
	for (var i=0; i<f.length; i++) {
		var a = f[i];
		var b = ciphertext.length / a;
		s += '<a href="javascript:layout(' + a + ',' + b + ')">' + a + 'x' + b + '</a>';
		if (i < f.length-1) s += ' &middot; ';
	}
	
	s += "</p>";
	d("alphabet").innerHTML = s + timing(dateStart, new Date());
}

function timing(dateStart, dateEnd) {
	return "<em style='color:#999'>Computation time: [" + (dateEnd-dateStart) + "] ms.</em><br>";
}

// for a given row, highlight any of its symbols that also occur in columns (the columnar repeats are also highlighted)
function hrepeatrow(row) {
	hrow2(row);
	for (var i=0; i<tmprrow[row].length; i++) {
		hsearchcol(tmprrow[row][i][0], tmprrow[row][i][1]);
	}
}

// for a given col, highlight any of its symbols that also occur in rows (the row repeats are also highlighted)
function hrepeatcol(col) {
	hcol2(col);
	for (var i=0; i<tmprcol[col].length; i++) {
		hsearchrow(tmprcol[col][i][0], tmprcol[col][i][1]);
	}
}

function halpha(i) {
	var a = alphabetGet();
	var ch = a[i];
	for (var row=0; row<block.length; row++) {
		for (var col=0; col<block[row].length; col++) {
			if (ch == block[row][col]) togglecell(row, col, true);
//			else ccell(row, col);
		}
	}
}

function halphargb(ch, r, g, b) {
	for (var row=0; row<block.length; row++) {
		for (var col=0; col<block[row].length; col++) {
			if (ch == block[row][col]) hcellrgb(row, col, true, r, g, b);
//			else ccell(row, col);
		}
	}
}


function encode(ch) {
	if (ch == "<") return "&lt;"
	if (ch == "&") return "&amp;";
	return ch;
}

function encodestr(str) {
	if (!str) return null;
	var s1 = str.replace(/[&]/g,"&amp;");
	return s1.replace(/[<]/g,"&lt;");
}

function cell(row, col) {
	return d("r"+row+"c"+col);
}

function togglecell(row, col, effect) {
	if (highlights[row][col]) ccell(row, col); else hcell(row, col, effect);
}

function hcell(row, col, effect) {
	highlights[row][col] = true;
	var elem = cell(row, col);
	if (elem) {
			elem.style.backgroundColor="#faa";
			elem.style.paddingBottom="2px";
		//	if (effect) new Effect.Highlight("r"+row+"c"+col, {startcolor: "#99999", endcolor: "#ffaaaa"})	
		//	elem.style.color="#fff";
		//	elem.style.fontWeight="bold";
	}
}
function hcellrgb(row, col, effect, r, g, b) {
	if (highlights && highlights[row]) highlights[row][col] = true;
	var elem = cell(row, col);
	if (elem) {
			elem.style.backgroundColor="rgb("+r+","+g+","+b+")";
			elem.style.paddingBottom="2px";
	}
}

// highlight cipher from pos1 to pos2
function hrange(pos1, pos2) {
	for (var i=pos1; i<=pos2; i++) {
		yx = fromLine(i);
		hcell(yx[0], yx[1], true);
	}
	
}

function hclear() {
	for (var row=0; row<block.length; row++)
		for (var col=0; col<block[row].length; col++)
			ccell(row, col);
	
}
function ccell(row, col) {
	highlights[row][col] = false;
	var elem = cell(row, col);
	if (elem) {
		elem.style.backgroundColor="#fff";
		elem.style.color="#000";
		//	elem.style.fontWeight="";
	}
}

function hrow(row) {
	for (var col=0; col<block[row].length; col++) hcell(row, col);
}

function hrow2(row) {
	for (var col=0; col<block[row].length; col++) {
		var elem = cell(row, col);
		elem.style.backgroundColor="#aaa";
		elem.style.paddingBottom="2px";
	}
}

function hcol(col) {
	for (var row=0; row<block.length; row++) hcell(row, col);
}

function hcol2(col) {
	for (var row=0; row<block.length; row++) {
		var elem = cell(row, col);
		elem.style.backgroundColor="#ddd";
		elem.style.paddingBottom="2px";
	}
}

function crow(row) {
	for (var col=0; col<block[row].length; col++) ccell(row, col);
}

function ccol(col) {
	for (var row=0; row<block.length; row++) ccell(row, col);
}

function getcipher(cipher) {
	var s = "";
	for (var row=0; row<cipher.length; row++) {
		for (var col=0; col<cipher[row].length; col++) {
			s += cipher[row].charAt(col);
		}
		s += "\n";
	}
		
	d("input").value = s;
//	new Effect.Highlight("col2");
	update();
}

Array.prototype.getUniqueValues = function () {
var hash = new Object();
for (j = 0; j < this.length; j++) {hash[this[j]] = true}
var array = new Array();
for (value in hash) {array.push(value)};
return array;
}

function alphabetGet() {
	var alpha = [];
	for (var row=0; row<block.length; row++) {
		for (var col=0; col<block[row].length; col++) {
			alpha.push(block[row][col]);
		}
	}
	return alpha.getUniqueValues().sort();
}

function gup( name )
{
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}

function showhide(which) {
	var d = $(which).style.display;
	$(which).style.display = (d == "none" ? "" : "none");
}

function resetcontrol(which) {
	$(which).innerHTML = "";
	$(which+'c').style.display = "";
}

// return array of all block positions that match the given string
function search(str) {
	var results = [];
	var i = ciphertext.indexOf(str);
	while (i>-1) {
		for (var j=i; j<i+str.length; j++) {
			results.push(fromLine(j));
		}
		i = ciphertext.indexOf(str, i+1);
	}
	return results;
}

// highlight all cells matching the search
function hsearch(str) {
	var a = search(str);
	for (var i=0; i<a.length; i++)
		hcell(a[i][0], a[i][1], true);
	jumptop();
}
// randomized color version
function hsearchrand(str) {
	var a = search(str);
	var r = Math.floor(Math.random()*192) + 64;
	var g = Math.floor(Math.random()*192) + 64;
	var b = Math.floor(Math.random()*192) + 64;
	for (var i=0; i<a.length; i++)
		hcellrgb(a[i][0], a[i][1], true, r, g, b);
	jumptop();
}


// highlight all cells matching each letter of the given string
function hsearcheach(str) {
	for (var i=0; i<str.length; i++) hsearch(str.charAt(i));
}

function hsearchrow(row, str) { // only works with one-letter searches
	var a = search(str);
	for (var i=0; i<a.length; i++)
		if (a[i][0] == row)
			hcell(a[i][0], a[i][1], false);
}
function hsearchcol(col, str) { // only works with one-letter searches
	var a = search(str);
	for (var i=0; i<a.length; i++)
		if (a[i][1] == col)
			hcell(a[i][0], a[i][1], false);
}

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
}
String.prototype.ltrim = function() {
	return this.replace(/^\s+/,"");
}
String.prototype.rtrim = function() {
	return this.replace(/\s+$/,"");
}

function toInput(c) {
	var s = "";
	for (var i=0; i<c.length; i++) {
		s += c[i] + "\n";
	}
	$('input').value = s;
	update();
//	new Effect.Highlight("col2");
}
function r90cw() {
	cipherblock=rotateCW(cipherblock);
	toInput(cipherblock);
}
function r90ccw() {
	cipherblock=rotateCCW(cipherblock);
	toInput(cipherblock);
}
function fv() {
	cipherblock=flipV(cipherblock);
	toInput(cipherblock);
}
function fh() {
	cipherblock=flipH(cipherblock);
	toInput(cipherblock);
}

// find all sequences that contain only non-repeating symbols.  returns map of L -> [pos, str], where L is string length, pos is location of string, and str is string sequence.
function nonrepeats() {
	var i = 0;
	
	var map = {};
	var strings = [];

	var d = "";
	
//	var bests = [];
//	for (i=0; i<ciphertext.length; i++) bests[i] = 0;
	
	while (i<ciphertext.length) {
		var len = 0;
		var seen = {};
		var str = "";
		var j = i;
		while (j<ciphertext.length) {
			var ch = ciphertext.charAt(j);
			if (seen[ch]) break;
			seen[ch] = true;
			str += ch;
			len++;
			j++;
		}
		var val = [i, str];
		
		var add = true;
		
		/* only add if this sequence is not within another larger sequence */
		for (var k=0; k<strings.length; k++) {
			if (strings[k].indexOf(str) > -1) {
				add = false;
				break;
			}
		}
		//add=true;
		
		if (add) {
			if (!map[len])
				map[len] = [];
			map[len].push(val);
			strings.push(str);
		}
		i++;
	}
	return map;
}

function renderNonRepeats() {
	var dateStart = new Date();
	var html = "";
	var map = nonrepeats();
	var k = [];
	var count = 0;
	
	/* for each position of cipher text, track length of longest non-repeating sequence it appears in */
	nrbests = [];
	for (var i=0; i<block.length*block[0].length; i++) nrbests[i] = 0;
	
	/* counts for pairwise occurrences of symbols */
	/*var pairs = {};
	var alpha = alphabetGet();
	var num = alpha.length;
	for (var i=0; i<num; i++) { 
		pairs[alpha[i]] = {}; 
		for (var j=0; j<num; j++) 
			pairs[alpha[i]][alpha[j]] = 0;
	}
	var seenpos = {};*/
	
	for (var key in map) k[k.length] = key;
	for (var i=k.length-1; i>0; i--) {
		for (var j=0; j<map[k[i]].length; j++) {
			html += "Length [<b>" + k[i] + "</b>] [<b>" + Math.round(100*100*k[i]/ciphertext.length)/100 + "%</b>] Position [<b>" + map[k[i]][j][0] + "</b>]: <tt><a href='javascript:hrange(" + map[k[i]][j][0] + "," + (parseInt(map[k[i]][j][0])+parseInt(k[i])-1) + ")'>" + encodestr(map[k[i]][j][1]) + "</a></tt><br>";
			for (var z=map[k[i]][j][0]; z<parseInt(map[k[i]][j][0])+parseInt(k[i]); z++) {
				nrbests[z] = Math.max(nrbests[z], k[i]);
			}
			
//			var pos = parseInt(map[k[i]][j][0]);
//			var len = parseInt(k[i]);
//			var sequence = map[k[i]][j][1];
//			html += " [seq: " + pos+","+len+","+sequence + "] ";
//			for (var z=pos; z<pos+len-1; z++) {
//				for (var y=pos; y<pos+len; y++) {
//				var y=pos+1;
//					if (y==z) continue;
//					var score = 0;
//					if (!seenpos[z]) score++;
//					if (!seenpos[y]) score++;
//					score=2;
//					
//					var b=z-pos;
//					var a=y-pos;
//					
//					if (!pairs[sequence[z]]) alert(sequence + ", " + z + ", " + y + ", " + sequence[z] + ", " + sequence[y]);
//					if (sequence[b] == "B" && sequence[a] == "P") alert(sequence);
//					pairs[sequence[b]][sequence[a]] += score;
//				}
//			}
//			for (var z=pos; z<pos+len; z++) seenpos[z] = true;
			count++;
			if (only10b && count == 10) break;
		}
		if (only10b && count == 10) break;
	}
	if (only10b) html += '<i><a href="javascript:only10b=false;renderNonRepeats();">(Show All and Chart)</a></i>';
	else html += '<a href="javascript:only10b=true;renderNonRepeats();">(Show first 10 only)</a></i></td></tr>';
	if (!only10b) html += "<br>Chart of max non-repeating sequence lengths by position (X axis: <a href='javascript:drawVisualization(nrbests, true)'>row #</a> | <a href='javascript:drawVisualization(nrbests, false)'>position #</a>): ";
	//for (var i=0; i<bests.length; i++) html += bests[i] + ", ";
	
//	html += "<br>Pair counts:";
//	html += "<table><tr><td></td>";
//	for (var i=0; i<alpha.length; i++) html += "<td>" + encodestr(alpha[i]) + "</td>";
//	html += "</tr>";
//	for (var i=0; i<alpha.length; i++) {
//		html += "<tr><td>" + encodestr(alpha[i]) + "</td>";
//		for (var j=0; j<alpha.length; j++) {
//			html += "<td>" + pairs[alpha[i]][alpha[j]] + "</td>";
//		}
//		html += "</tr>";
//	}
//	html += "</table>";
	
	
	
	$("nonrepeats").innerHTML = html + "<br>" + timing(dateStart, new Date());
	if (!only10b) drawVisualization(nrbests, false);
	
}

function getrepeats(n) {
	var ngram; var u;
	var result = []; var count;
	for (var i=0; i<ciphertext.length-n+1; i++) {
		u = {};
		ngram = ciphertext.substring(i,i+n);
		count = 0;
		for (var j=0; j<ngram.length; j++) {
			if (u[ngram.charAt(j)]) count++;
			u[ngram.charAt(j)] = true;
		}
		result[i] = [count/(n-1), i];
	}
	result.sort().reverse(); // highest scores on top
	
	// extract only the top scores
	var result2 = [];
	result2[0] = result[0];
	var i=1;
	while (result[i][0] == result2[0][0]) {
		result2[i] = result[i];
		i++;
	}
	return result2;
}

function yield(text) {
	u = {};
	var total = 0;
	for (var i=0; i<text.length; i++) {
		var c = text[i];
		if (u[c]) continue;
		u[c] = true;
		total += symbolcounts[c];
	}
	return "" + (Math.round(10000*total/ciphertext.length)/100) + "%";
}

function renderRepeats() {
	var dateStart = new Date();
	
	var html = "<i>Showing segment lengths up to one third of the ciphertext length:</i><br>";
	var map = nonrepeats(); var a; var count = 0;
	for (var n=Math.floor(ciphertext.length/3); n>2; n--) {
		a = getrepeats(n);
		for (var i=0; i<10 && i<a.length; i++) {
			html += 'Length [<b>' + n + '</b>]  Repeats [<b>' + (Math.round(a[i][0]*(n+1))-1) + '</b>]  Score [<b>' + Math.round(100*100*a[i][0])/100 + '%</b>] Yield [<b>' + yield(ciphertext.substring(a[i][1], a[i][1]+n)) + ']</b>:  <tt><a href="#" onclick="javascript:hrange(' + a[i][1] + ',' + (a[i][1] + n - 1) + ')">' + encodestr(ciphertext.substring(a[i][1], a[i][1]+n)) + '</a></tt><br>';
		}
		count++;
	}
	$("repeats").innerHTML = html + timing(dateStart, new Date());
}

function renderSeqs() {
	var dateStart = new Date();
	var html = "";
	
	repeats(block);
	
	var lengths = {};
	
	for (var i=0; i<sequences.length; i++) { // make distinct set of all lengths
		lengths[sequences[i][0].length] = true;
	}
	var l2 = [];
	for (var l in lengths) l2[l2.length] = l;
	l2.sort(function(a,b){return a-b});
	    
	var totals = [0,0,0,0,0,0,0,0]
	var byDirection = [{}, {}, {}, {}, {}, {}, {}, {}];
	for (var i=l2.length-1; i>=0; i--) {
		html += "<b>Length: " + l2[i] + "</b>: "
		for (var j=0; j<sequences.length; j++) {
			if (sequences[j][0].length == l2[i]) {
				html += "<a href=\"javascript:hSeq2(" + j + ")\">" + sequences[j][0] + "</a> | ";
				totals[sequences[j][3]]++;
				totals[sequences[j][4]]++;
				byDirection[sequences[j][3]][sequences[j][0]] = true;
				byDirection[sequences[j][4]][sequences[j][0]] = true;
			}
		}
	}

	html += "<br>Number of sequences by direction: <table><tr><th>Direction</th><th>Total</th><th>Sequences</th></tr>";
	for (var i=0; i<8; i++) {
		html += "<tr valign='top'>";
		html += "<td align='right'>";
		if (i==0) html += "right";
		else if (i==1) html += "right-down";
		else if (i==2) html += "down";
		else if (i==3) html += "left-down";
		else if (i==4) html += "left";
		else if (i==5) html += "left-up";
		else if (i==6) html += "up";
		else if (i==7) html += "right-up";
		else html += "???";
		html += "</td><td align='right'><b>" + totals[i]+"</b></td><td>";
		for (var s in byDirection[i]) html += "[<tt>" + s + "</tt>] ";		
		html += "</td></tr>";
	}
	html += "</table>";
	
	document.getElementById("seq").innerHTML = html + timing(dateStart, new Date());
}

function hSeq2(seq) {
	for (var i=0; i<sequences[seq][1].length; i++) 
		hcell(sequences[seq][1][i][0], sequences[seq][1][i][1], true);
	for (var i=0; i<sequences[seq][2].length; i++) 
		hcell(sequences[seq][2][i][0], sequences[seq][2][i][1], true);
	jumptop();
}

function iocAll() {
	return ioc(ciphertext);
}

function iocCol(col) {
	return ioc(ciphertextCol[col]);
}

function iocRow(row) {
	return ioc(ciphertextRow[row]);
}

function ioc(str) {
	var sum = 0; var n; var L = str.length;
	var u = {};
	for (var i=0; i<L; i++) {
		if (!u[str.charAt(i)]) u[str.charAt(i)] = 1;
		else u[str.charAt(i)]++;
	}
	for (var key in u) {
		n = u[key];
		sum += n*(n-1);
	}
	sum /= L*(L-1);
	return sum;
}

function entropyall() {
	return entropy(ciphertext);
}

function entropyCol(col) {
	return entropy(ciphertextCol[col]);
}

function entropyRow(row) {
	return entropy(ciphertextRow[row]);
}

function entropy(str) {
	var sum = 0; var pm; // probability mass
	var L = ciphertext.length;
	var u = {};
	for (var i=0; i<L; i++) {
		if (!u[str.charAt(i)]) u[str.charAt(i)] = 1;
		else u[str.charAt(i)]++;
	}
	for (var key in u) {
		pm = u[key]/L;
		sum += pm*Math.log(pm)/LOG2;
	}
	return -1.0*sum;
}

function chi2All() { return chi2(ciphertext);}
function chi2Row(row) { return chi2(ciphertextRow[row]);}
function chi2Col(col) { return chi2(ciphertextCol[col]);}

function chi2(str) {
	var pm; // probability mass
	var uniq = 0; var chi2 = 0; var curr;
	var L = ciphertext.length;
	var u = {};
	for (var i=0; i<L; i++) {
		var ch = str.charAt(i);
		if (!u[ch]) { uniq++; u[ch] = 1; }
		else u[ch]++;
	}
	pm = L/uniq;
	for (var key in u) {
		curr = u[key]-pm;
		curr *= curr;
		curr /= pm;
		chi2 += curr;
	}
	return chi2/L;
}

function renderIOC() {
	var dateStart = new Date();
	var html = "";
	var i = iocAll(); var e = entropyall(); var sum = 0;
	
	var max = 0; min = 1e50; var ds = "";

	///////////////
	// IOC
	///////////////
	
	html += "IoC: <b>" + (Math.round(10000*i)/10000) + "</b>. ";
	html += "By row: ";
	for (var row=0; row<block.length; row++) {
		i = iocRow(row); sum += i; max = Math.max(max, i); min = Math.min(min, i); ds += ""+i; if (row<block.length-1) ds += ",";
		html += '<a href="javascript:hrow(' + row + ')">' + round(i) + '</a>' + (row<block.length-1 ? " &middot; " : "");
	}
	var chart1 = '<img src="http://chart.apis.google.com/chart?chxr=0,' + min + ',' + max + '|1,0,' + (block.length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=' + min + ','+ max + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=IoC%20By%20Row" width="150" height="112" alt="" />';
	
	html += ' (Average: <b>' + round(sum/block.length) + '</b>).  ';
	sum = 0; max = 0; min = 1e50; ds = "";
	html += "By col: ";
	for (var col=0; col<block[0].length; col++) {
		i = iocCol(col); sum += i; max = Math.max(max, i); min = Math.min(min, i); ds += ""+i; if (col<block[0].length-1) ds += ",";
		html += '<a href="javascript:hcol(' + col + ')">' + round(i) + '</a>' + (col<block[0].length-1 ? " &middot; " : "");
	}
	var chart2 = '<img src="http://chart.apis.google.com/chart?chxr=0,' + min + ',' + max + '|1,0,' + (block[0].length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=' + min + ','+ max + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=IoC%20By%20Col" width="150" height="112" alt="" />';
	
	html += ' (Average: <b>' + round(sum/block.length) + '</b>).  ';
	html += "Ratio to IoC of random letters: <b>" + (Math.round(10000*i/0.0385)/10000) + "</b>.<br>";
	html += '<span style="color: #aaa"><i>(English plaintext: 0.0667; Russian: 0.0529; German: 0.0762; Spanish: 0.0775; Random letters: 0.0385)</i></span><br>'

	///////////////
	// ENTROPY
	///////////////
	
	html += "Entropy: <b>" + (Math.round(10000*e)/10000) + "</b>.";
	
	sum = 0; max = 0; min = 1e50; ds = "";
	html += "By row: ";
	for (var row=0; row<block.length; row++) {
		i = entropyRow(row); sum += i; max = Math.max(max, i); min = Math.min(min, i); ds += ""+i; if (row<block.length-1) ds += ",";
		html += '<a href="javascript:hrow(' + row + ')">' + round(i) + '</a>' + (row<block.length-1 ? " &middot; " : "");
	}
	var chart3 = '<img src="http://chart.apis.google.com/chart?chxr=0,' + min + ',' + max + '|1,0,' + (block.length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=' + min + ','+ max + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Entropy%20By%20Row" width="150" height="112" alt="" />';
	
	html += ' (Average: <b>' + round(sum/block.length) + '</b>).  ';
	
	sum = 0; max = 0; min = 1e50; ds = "";
	html += "By col: ";
	for (var col=0; col<block[0].length; col++) {
		i = entropyCol(col); sum += i; max = Math.max(max, i); min = Math.min(min, i); ds += ""+i; if (col<block[0].length-1) ds += ",";
		html += '<a href="javascript:hcol(' + col + ')">' + round(i) + '</a>' + (col<block[0].length-1 ? " &middot; " : "");
	}
	var chart4 = '<img src="http://chart.apis.google.com/chart?chxr=0,' + min + ',' + max + '|1,0,' + (block[0].length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=' + min + ','+ max + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Entropy%20By%20Col" width="150" height="112" alt="" />';
	
	html += ' (Average: <b>' + round(sum/block.length) + '</b>).<br>';

	///////////////
	// CHI SQUARED
	///////////////
	i = chi2All();
	html += "Chi<sup>2</sup>: <b>" + (Math.round(10000*i)/10000) + "</b>.";
	
	sum = 0; max = 0; min = 1e50; ds = "";
	html += "By row: ";
	for (var row=0; row<block.length; row++) {
		i = chi2Row(row); sum += i; max = Math.max(max, i); min = Math.min(min, i); ds += ""+i; if (row<block.length-1) ds += ",";
		html += '<a href="javascript:hrow(' + row + ')">' + round(i) + '</a>' + (row<block.length-1 ? " &middot; " : "");
	}
	var chart5 = '<img src="http://chart.apis.google.com/chart?chxr=0,' + min + ',' + max + '|1,0,' + (block.length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=' + min + ','+ max + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Chi^2%20By%20Row" width="150" height="112" alt="" />';
	
	html += ' (Average: <b>' + round(sum/block.length) + '</b>).  ';
	
	sum = 0; max = 0; min = 1e50; ds = "";
	html += "By col: ";
	for (var col=0; col<block[0].length; col++) {
		i = chi2Col(col); sum += i; max = Math.max(max, i); min = Math.min(min, i); ds += ""+i; if (col<block[0].length-1) ds += ",";
		html += '<a href="javascript:hcol(' + col + ')">' + round(i) + '</a>' + (col<block[0].length-1 ? " &middot; " : "");
	}
	var chart6 = '<img src="http://chart.apis.google.com/chart?chxr=0,' + min + ',' + max + '|1,0,' + (block[0].length-1) + '&chxt=y,x&chs=150x112&cht=lc&chco=3D7930&chds=' + min + ','+ max + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Chi^2%20By%20Col" width="150" height="112" alt="" />';
	
	html += ' (Average: <b>' + round(sum/block.length) + '</b>).  ';


	
	html += '<table><tr><td>' + chart1 + "</td><td>" + chart2 + '</td><td>' + chart3 + "</td><td>" + chart4 + '</td><td>' + chart5 + "</td><td>" + chart6 + "</td></tr></table>";
	$("ioc").innerHTML = html + timing(dateStart, new Date());
}

function accumulation() {
	var u = {};
	var total = 0;
	var ch;
	var a = [];
	for (var row=0; row<block.length; row++) {
		a[row] = [];
		for (var col=0; col<block[row].length; col++) {
			ch = block[row][col];
			if (u[ch]) total++;
			u[ch] = true;
			a[row][col] = total;
		}
	}
	
	for (var row=0; row<block.length; row++) {
		for (var col=0; col<block[row].length; col++) {
			var rank = (1-a[row][col]/total);
			var color = Math.round(rank*255);
			setbg(row, col, color, true);
		}
	}
}

function pad(str) {
	if (str.length == 1) return " " + str;
	return str;
}

function factors() {
	var L = ciphertext.length;
	var f = [];
	for (var i=1; i<L; i++) {
		if (L/i == Math.round(L/i)) f[f.length] = i;
	}
	return f;
}

function layout(a, b) {
	var r = "";
	for (var i=0; i<ciphertext.length; i++) {
		if (i > 0 && i % b == 0) r += "\n";
		r += ciphertext.charAt(i);
	}
	$('input').value = r;
//	new Effect.Highlight("col2");
	update();
}

function encodeToHex(str){
    var r="";
    var e=str.length;
    var c=0;
    var h;
    while(c<e){
        h=str.charCodeAt(c++).toString(16);
        while(h.length<3) h="0"+h + " ";
        r+=h;
    }
    return r;
}






// round given number to 3 places
function round(num) {
	return Math.round(1000*num)/1000;
}

//user picked a different cipher from the picklist
function selectcipher() {
	var cipher = $("avail").options[$("avail").selectedIndex].value;
	if (cipher == "b1") {
		$('input').value = encodebeale(b1);
		update();
		layout(20,26);
	} else if (cipher == "b2") {
		$('input').value = encodebeale(b2);
		update();
		layout(20,26);
	} else if (cipher == "b3") {
		$('input').value = encodebeale(b3);
		update();
		layout(20,26);
	}
	else window.location.href="?cipher=" + cipher;
}

// make the browser window jump to the top
function jumptop() {
	window.location.href="#top";	
}


function drawVisualization(bests, showRows) {
  $("visualization").style.display="";
  // Create and populate the data table.
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'pos');
  data.addColumn('number', 'Max');


  var max = 0;
  for (var i=0; i<bests.length; i++) {
	if (showRows) data.addRow([""+Math.floor(i/block[0].length), bests[i]]);
	else data.addRow([""+i, bests[i]]);
	max = Math.max(bests[i], max);
  }

  max++;

  new google.visualization.LineChart(document.getElementById('visualization')).
      draw(data, {curveType: "line",
                  width: 800, height: 300,
                  vAxis: {maxValue: max}}
          );
}

function repsinc() {
	$("repsn").selectedIndex++;
	renderRepsChart();
}	

function repsdec() {
	$("repsn").selectedIndex--;
	renderRepsChart();
}	

function drawRepsByPosChart(reps, showRows) {
  // Create and populate the data table.
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'pos');
  data.addColumn('number', 'Repeats');


  var max = 0;
  for (var i=0; i<reps.length; i++) {
	if (showRows) data.addRow([""+Math.floor(i/block[0].length), reps[i]]);
	else data.addRow([""+i, reps[i]]);
	max = Math.max(reps[i], max);
  }

  max++;

  new google.visualization.LineChart(document.getElementById('repschart')).
      draw(data, {curveType: "line",
                  width: 800, height: 300,
                  vAxis: {maxValue: max}}
          );
}


/* for each position of cipher text, compute total number of repeated n-grams between repsn steps to the left of the position and repsn steps to its right. */
function computeRepeatsByPos(n) {
	var repeats = [];
	for (var i=0; i<ciphertext.length; i++) {
		repeats[i] = 0;
		var a = Math.max(i-repsn, 0);
		var b = Math.min(i+repsn, ciphertext.length-1);
		var sub = ciphertext.substring(a,b+1);
		
		var symbols = {};
		for (var j=0; j<sub.length-n+1; j++) {
			var gram = sub.substring(j,j+n);
			if (symbols[gram]) repeats[i]++;
			else symbols[gram] = true;
		}
	}
	return repeats;	
}

function renderRepsChart() {
	repsn = parseInt($("repsn").options[$("repsn").selectedIndex].value);
	var ngram = parseInt($("repsngram").options[$("repsngram").selectedIndex].value);
	var showRows = $("repsx").selectedIndex == 0;
	drawRepsByPosChart(computeRepeatsByPos(ngram), showRows);
}	

/* find cipher text segment of length n that, when decoded, reveals the largest amount of cipher text. */
function bestYield(n) {
	var sub;
	var count;
	var bestCount = 0;
	var bestSub;
	var bestSymCount = 0;
	var bestPos = 0;
	var bestSymc = 0;
	var bySym = [];

	for (var i=0; i<ciphertext.length-n+1; i++) {
		count = 0;
		sub = ciphertext.substring(i, i+n);
		var u = {};
		var symc = 0;
		for (var j=0; j<sub.length; j++) {
			if (!u[sub[j]]) {
				symc++;
				count += symbolcounts[sub[j]];
				u[sub[j]] = true;
			}
		}
		if (count > bestCount) {
			bestSub = sub;
			bestCount = count;
			bestSymCount = symc;
			bestPos = i;
			bestSymc = symc;
		}
		
		bySym.push([symc, i, count, sub]);
	}	
	return [bestSub, bestCount, bestSymc, bestPos, bySym];
}	

function renderyield() {
	var html = "";
	var n = 1;
	
	var sym = {};
	
	html += "By length:<br>";
	while(true) {
		var best = bestYield(n);
		var hr1 = best[3];
		var hr2 = hr1+best[0].length-1;
		html += "Length [<b>" + best[0].length + "</b>] [<b>" + (Math.round(100*best[0].length/ciphertext.length)) + "%</b>] Yield [<b>" + best[1] + "</b>] [<b>" + (Math.round(100*best[1]/ciphertext.length)) + "%</b>] Symbols [<b>" + best[2] + "</b>] Position [<b>" + best[3] + "</b>] Ciphertext [<b><a href='javascript:hrange(" + hr1 + "," + hr2 + "); window.location=\"#top\"'>" + encodestr(best[0]) + "</a></b>]<br>";

		/* track best counts per # of unique symbols */
		if (!sym[best[2]]) sym[best[2]] = [0, null, 0];
		if (best[1] > sym[best[2]][0]) {
			sym[best[2]] = [best[1], best];
		}
		
		if (best[1] == ciphertext.length) break;
		n++;
	}

	html += "By # of unique symbols: <br>";
	for (var key in sym) {
		var best = sym[key][1];
		html += "Length [<b>" + best[0].length + "</b>] [<b>" + (Math.round(100*best[0].length/ciphertext.length)) + "%</b>] Yield [<b>" + best[1] + "</b>] [<b>" + (Math.round(100*best[1]/ciphertext.length)) + "%</b>] Symbols [<b>" + best[2] + "</b>] Position [<b>" + best[3] + "</b>] Ciphertext [<b><a href='javascript:hrange(" + hr1 + "," + hr2 + "); window.location=\"#top\"'>" + encodestr(best[0]) + "</a></b>]<br>";
	}
	
	$("yield").innerHTML = html;
}	

function threshReset() {
	faded=[];
	update();
}

function hideLeast() {
	var val=$("threshLeast").value;
	if (!val) return;
	threshLeast = val;
	faded=[];
	for (var row=0; row<block.length; row++) {
		faded[row] = [];
		for (var col=0; col<block[row].length; col++) {
			if (symbolcounts[block[row][col]] < val) faded[row][col] = true; else faded[row][col] = false;
		}
	}
	update();
}

function hideMost() {
	var val=$("threshMost").value;
	if (!val) return;
	threshMost = val;
	faded=[];
	for (var row=0; row<block.length; row++) {
		faded[row] = [];
		for (var col=0; col<block[row].length; col++) {
			if (symbolcounts[block[row][col]] > val) faded[row][col] = true; else faded[row][col] = false;
		}
	}
	update();
}

function intersections() {
	var html = "";
	for (var L=12; L>1; L--) {
		for (var row=0; row<block.length; row++) {
			for (var col=0; col<block[row].length; col++) {
				// compute strings of length L in all 8 directions, centered upon (row, col)
				var segments = []; var seen = {}; var matches = {}; var found = false; var p = [];
				var db = "";
				for (var i=0; i<8; i++) {
					var a = segmentFor(row, col, L, i);
					segments[i] = a[0];
					p[i] = a[1];
					var key = ""+segments[i].sort();
					db += "<br>row " + row + " col " + col + " L " + L + " i " + i + " seg " + segments[i] + " key " + key;
					
					if (seen[key]) {
						found = true;
						matches[i] = true; matches[seen[key]-1] = true;
					}
					seen[key] = i+1;
				}
				if (found) {
//					html += db;
					html += "Length [" + L + "] Row [" + row + "] Col [" + col + "] Segments ";
					var link = "<a href=\"#\" onclick=\""; var html2 = "";
					for (var key in matches) {
						var hash = {};
						for (var i=0; i<p[key].length; i++) {
							hash[""+p[key][i]] = true;
						}
						for (var i in hash) link += "hcell(" + i + "); ";
						html2 += "[" + encodestr(segformat(segments[key])) + "]";
					}
					html += link + "\">" + html2 + "</a><br>";
				}
			}
		}
	}
	$("intersections").innerHTML = html;
}

/*function segmentshighlight(p) {
	var r = "";
	for (var i=0; i<p.length; i++) r += "hcell(" + p[i][0] + ","+p[i][1] + "); ";
	return r;
}*/

function segformat(a) {
	var r = "";
	for (var i=0; i<a.length; i++) r += a[i];
	return r;
}

function segmentFor(row, col, L, dir) {
	var drow, dcol;
	if (dir == 0) { drow = -1; dcol = -1; }
	else if (dir == 1) { drow = -1; dcol = 0; }
	else if (dir == 2) { drow = -1; dcol = 1; }
	else if (dir == 3) { drow = 0; dcol = 1; }
	else if (dir == 4) { drow = 1; dcol = 1; }
	else if (dir == 5) { drow = 1; dcol = 0; }
	else if (dir == 6) { drow = 1; dcol = -1; }
	else if (dir == 7) { drow = 0; dcol = -1; }
	
	var a = []; var r = row; var c = col; var p = [];
	for (var i=0; i<L; i++) {
		a[a.length] = block[r][c];
		p[p.length] = [r,c];
		var m = move(r, c, drow, dcol);
		r = m[0]; c = m[1];
	}
	return [a,p];
}


function move(row, col, drow, dcol) {
	var r = row+drow;
	var c = col+dcol;
	
	if (r<0) r+=block.length;
	if (c<0) c+=block[0].length;
	
	r%=block.length;
	c%=block[0].length;
	
	return [r,c];
}	

