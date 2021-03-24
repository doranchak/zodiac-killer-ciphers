// ciphertext in various orientations
// TODO: "wrapped" matches (gapped ngram that starts on one line and extends into the next line) occur in a single direction.  we might be missing potential matches that occur in the other direction.
var gapciphers = [];

var gapgrid = [];

var NUM_ORIENTATIONS = 2;
function initgapciphers() {
	if (gapciphers.length == 0) {
		var copy;

		// orientations:
		// left to right, top to bottom: original
		gapciphers.push(ciphertext); 	
		// left to right, bottom to top: flip vertical
	//	copy = cipherblock.slice(0);
	//	gapciphers.push(toLine(flipV(copy)));
		// right to left, top to bottom: flip horizontal
//		copy = cipherblock.slice(0);
//		gapciphers.push(toLine(flipH(copy)));
		// right to left, bottom to top: flip horizontal, flip vertical
//		copy = cipherblock.slice(0);
//		gapciphers.push(toLine(flipV(flipH(copy))));
		// top to bottom, left to right: rotate 90 deg ccw + flip vertical
		copy = cipherblock.slice(0);
		gapciphers.push(toLine(flipV(rotateCCW(copy))));
		// top to bottom, right to left: rotate 90 deg ccw
//		copy = cipherblock.slice(0);
//		gapciphers.push(toLine(rotateCCW(copy)));
	}
}

var raritymin = 10000000;
var raritymax = -10000000;
// sort keys in descending order of score
function gapsort(g) {
	var sorted = [];
	
	var s = 0; 

	for (var key in g) {
		var rar = gaprarity(key);
		raritymin = Math.min(raritymin, rar);
		raritymax = Math.max(raritymax, rar);
	}

	for (var key in g) {
		var score = gapscore(key, g[key]);
		sorted[s++] = [key, score];

		// update gap grid while we're here
		for (var i=0; i<g[key].length; i++) {
			var row = g[key][i][0];
			var col = g[key][i][1];
			var n = g[key][i][3];
			var o = g[key][i][2];
			gapgrid[row][col][n][o][0] += "[" + key + "] ";;
			gapgrid[row][col][n][o][1] += score;
		}

	}
	
	
	return sorted.sort(function(a,b){return b[1]-a[1];});
	
}

// combined score: key length, repeats, orientations, rarity
function gapscore(key, matches) {
	var score = 1/key.length; // total key length (smaller is better)
	score *= matches.length / key.length; // extent of coverage of repeats

	/*
	var o = 0;  var hash = {};
	for (var i=0; i<matches.length; i++) {
		if (!hash[matches[i][2]]) o++;
		hash[matches[i][2]] = true;
	}
	score *= o; // number of distinct orientations
	*/
	
	score *= (gaprarity(key)-raritymin)/(raritymax-raritymin); // measurement of how rare these symbols are.  higher rarity = higher score.
	return score;
}

function initgapgrid() {
	for (var row=0; row<block.length; row++) {
		gapgrid[row] = [];
		for (var col=0; col<block[0].length; col++) {
			gapgrid[row][col] = [];
			for (var n=0; n<5; n++) {
				gapgrid[row][col][n] = [];
				for (var a=0; a<NUM_ORIENTATIONS; a++) gapgrid[row][col][n][a] = ["", 0];
			}
		}
	}
}

function onamefor(o) {
	if (o==0) return "Left to right, up to down";
//	if (o==1) return "Left to right, bottom to top";
//	if (o==1) return "Right to left, top to bottom";
//	if (o==3) return "Right to left, bottom to top";
	if (o==1) return "Top to bottom, left to right";
//	if (o==5) return "Top to bottom, right to left";
	return "Huh?";
	
}
function gapgridhtml() {
	var html = "<p>";
	for (var o=0; o<NUM_ORIENTATIONS; o++) {
		html += "<table><tr valign='top'>";
		for (var n=0; n<3; n++) {
			
			html += "<td>Orientation [" + onamefor(o) + "] N [" + (n+2) + "]<br>";
			html += "<table class=\"rendertable\">";
			
			var max = 0;
			for (var row=0; row<block.length; row++) {
				for (var col=0; col<block[0].length; col++) {
					max = Math.max(max, gapgrid[row][col][n][o][1]);
				}
			}
			
			for (var row=0; row<block.length; row++) {
				html += "<tr>";
				for (var col=0; col<block[0].length; col++) {
					var key = gapgrid[row][col][n][o][0];
					var value = gapgrid[row][col][n][o][1];
					var rank = max == 0 ? 1 : (1-value/max);
					var color = Math.round(rank*255);
					var font = (color > 127 ? "black" : "white");
						
					html += "<td title=\"" + encodestr(key) + ":" + value + "\" style=\"background-color: rgb(" + color + "," + color + "," + color + "); color: " + font + "\">";
					html += encodestr(block[row][col]);
					html += "</td>";
				}
				html += "</tr>";
			}
			
			html += "</table></td>";
		}
		html += "</tr></table>";
		
	}
	
	// all orientations combined
	html += "<table><tr valign='top'>";
	for (var n=0; n<3; n++) {
		html += "<td><table><tr valign=\"top\">";
		
		html += "<td>Orientation [All] N [" + (n+2) + "]<br>";
		html += "<table class=\"rendertable\">";
		
		var max = 0;
		for (var row=0; row<block.length; row++) {
			for (var col=0; col<block[0].length; col++) {
				for (var o=0; o<NUM_ORIENTATIONS; o++) {
					max = Math.max(max, gapgrid[row][col][n][o][1]);
				}
			}
		}
		
		for (var row=0; row<block.length; row++) {
			html += "<tr>";
			for (var col=0; col<block[0].length; col++) {
				var value = 0;
				for (var o=0; o<NUM_ORIENTATIONS; o++) {
					var key = gapgrid[row][col][n][o][0];
					value += gapgrid[row][col][n][o][1];
				}
				var rank = max == 0 ? 1 : (1-value/max);
				var color = Math.round(rank*255);
				var font = (color > 127 ? "black" : "white");
					
				html += "<td title=\"" + encodestr(key) + ":" + value + "\" style=\"background-color: rgb(" + color + "," + color + "," + color + "); color: " + font + "\">";
				html += encodestr(block[row][col]);
				html += "</td>";
			}
			html += "</tr>";
		}
		
		html += "</table></td></tr></table></td>";
	}	
	html += "</tr></table>";
	
	html += "</p>";
	return html;
}

function gaps() {
	
	initgapciphers();
	initgapgrid();
	var html = "";
	var L = 5;
	
	for (var i=1; i<L; i++) {
		var header = false;
		
		var g = gapsFor([i]);
		var sorted = gapsort(g);
		for (var z=0; z<sorted.length; z++) { var key = sorted[z][0];
				if (!header) html += "Repeated pairs of symbols that are [" + i + "] positions apart:<br>";
				header = true;
				html += gapsHtml(key, g) + " Score: [<b>" + Math.round(1000*sorted[z][1])/1000 + "</b>]<br>" ;
		}
	}
	for (var i=1; i<L; i++) {
		for (var j=i; j<L; j++) {
			var header = false;
			var g = gapsFor([i,j]);
			var sorted = gapsort(g);
			for (var z=0; z<sorted.length; z++) { var key = sorted[z][0];
					if (!header) html += "Repeated triplets of symbols, where first and second are [" + i + "] positions apart, and second and third are [" + j + "] positions apart:<br>";
					header = true;
					html += gapsHtml(key, g) + " Score: [<b>" + Math.round(1000*sorted[z][1])/1000 + "</b>]<br>" ;
			}
		}
	}

	for (var i=1; i<L; i++) {
		for (var j=i; j<L; j++) {
			for (var k=j; k<L; k++) {
				var header = false;
				var g = gapsFor([i,j,k]);
				var sorted = gapsort(g);
				for (var z=0; z<sorted.length; z++) { var key = sorted[z][0];
						if (!header) html += "Repeated quads of symbols, where first and second are [" + i + "] positions apart, second and third are [" + j + "] positions apart, and third and fourth are [" + k + "] positions apart:<br>";
						header = true;
						html += gapsHtml(key, g) + " Score: [<b>" + Math.round(1000*sorted[z][1])/1000 + "</b>]<br>" ;
				}
			}
		}
	}
	
	for (var i=1; i<L; i++) {
		for (var j=i; j<L; j++) {
			for (var k=j; k<L; k++) {
				for (var m=k; m<L; m++) {
					var header = false;
					var g = gapsFor([i,j,k,m]);
					var sorted = gapsort(g);
					for (var z=0; z<sorted.length; z++) { var key = sorted[z][0];
							if (!header) html += "Repeated quints of symbols, where first and second are [" + i + "] positions apart, second and third are [" + j + "] positions apart, third and fourth are [" + k + "] positions apart, and fourth and fifth are [" + m + "] positions apart:<br>";
							header = true;
							html += gapsHtml(key, g) + " Score: [<b>" + Math.round(1000*sorted[z][1])/1000 + "</b>]<br>" ;
					}
				}
			}
		}
	}
	for (var i=1; i<L; i++) {
		for (var j=i; j<L; j++) {
			for (var k=j; k<L; k++) {
				for (var m=k; m<L; m++) {
					for (var n=m; n<L; n++) {
						var header = false;
						var g = gapsFor([i,j,k,m,n]);
						var sorted = gapsort(g);
						for (var z=0; z<sorted.length; z++) { var key = sorted[z][0];
								if (!header) html += "Repeated quints of symbols, where first and second are [" + i + "] positions apart, second and third are [" + j + "] positions apart, third and fourth are [" + k + "] positions apart, fourth and fifth are [" + m + "] positions apart, and fifth and sixth are [" + n + "] positions apart:<br>";
								header = true;
								html += gapsHtml(key, g) + " Score: [<b>" + Math.round(1000*sorted[z][1])/1000 + "</b>]<br>" ;
						}
					}
				}
			}
		}
	}
	
	
	$("gaps").innerHTML = html + gapgridhtml();
}

function gapscells(a) {
	var s = "";
	for (var i=0; i<a.length; i++) s += "hcell(" + a[i][0]+","+a[i][1] + "); ";
	return s;
}

function gaprarity(key) {
	
	var total = 0;
	for (var i=0; i<key.length; i++) {
		if (key[i] == " ") continue;
		total += symbolcounts[key[i]];
	}
	return 1 - total/ciphertext.length;
}
function gapsHtml(key, g) {
	var html = "";
	var click="";
	
	//for (var i=0; i<g[key][1].length; i++) click += "hrange(" + g[key][1][i][0] + "," + g[key][1][i][1] + "); ";
	click = gapscells(g[key]);
	
	var o = 0;  var hash = {};
	for (var i=0; i<g[key].length; i++) {
		if (!hash[g[key][i][2]]) o++;
		hash[g[key][i][2]] = true;
	}
	var rarity = Math.round(1000*(gaprarity(key)-raritymin) / (raritymax-raritymin))/1000;
	
	html += "<a class=\"ga\" href=\"#\" onclick=\"" + click + "\"><span class=\"fw\">" + encodestr(key).replace(/ /g,"<span class=\"fw2\">?</span>") + "</span></a>: Repeats: [<b>" + Math.round(100 * g[key].length / key.length)/100 + "</b>], Orientations: [<b>" + o + "</b>], Rarity score: [<b>" + rarity + "]</b>"
	return html;
}

/* 
	return hash of gap results.
	key: gapped ngram
*/	
function gapsFor(offsets) {
	initgapciphers();
	var r = {};
	
	for (var a=0; a<gapciphers.length; a++) {
	
		var seg; var stop = false;

	for (var i=0; i<gapciphers[a].length; i++) {
		
		seg = gapciphers[a][i]; var sum = 0;
		var pos = i;
		for (var j=0; j<offsets.length; j++) {
			sum += offsets[j];
			pos += offsets[j];
			if (pos>=gapciphers[a].length) {
				stop = true;
				break;
			}
			
			for (var k=1; k<offsets[j]; k++) seg += " ";
			seg += gapciphers[a][pos];
		}

		
		if (!r[seg]) {
			r[seg] = [];
		}

		if (seg.length != sum + 1) continue;
/*		
		if (seg == "1 (") {
			alert(i+","+a+" : " + gapciphers[a]);
		}
*/		
		
//		r[seg][1].push([i,i+sum]);
		for (var jj=0; jj<=sum; jj++) {
			var b = gapTr(i+jj, a);
			var skip = false;
			for (var kk=0; kk<r[seg].length; kk++) { // look for an existing row,col pair
				if (r[seg][kk][0] == b[0] && r[seg][kk][1] == b[1]) {
					skip = true;
					break;
				}
			}
			if (!skip) 
			r[seg].push([b[0],b[1],a,offsets.length-1]); // row, column, orientation, n index in gap grid
		}
		
		if (stop) break;
	}
	}
	for (var key in r) if (r[key].length <= key.length) { delete r[key]; } 
	return r;
}

function gapToBlock(line, orientation) {
	var r = [];
	for (var i=0; i<line.length; i++) {
		var b = gapTr(i, orientation);
		if (!r[b[0]]) r[b[0]] = [];
		r[b[0]][b[1]] = block[b[0]][b[1]];
	}
	return r;
/*	var l2 = "";
	for (var row=0; row<r.length; row++) 
		for (var col=0; col<r[row].length; col++) l2 += r[row][col];
	return l2;*/
}

// return original cipher text's [row,col] pair for the given 1D line position of the given orientation
function gapTr(pos, orientation) {
	// compute transformed block's height and width
	var H, W;
	//if (orientation == 0 || orientation == 1 || orientation == 2 || orientation == 3) {
	if (orientation < 1) {
		H = block.length; W = block[0].length;
	} else {
		W = block.length; H = block[0].length;
	}

	// row and col of the transformed block
	var row = Math.floor(pos/W);
	var col = pos % W;
	
	H--; W--;

	if (orientation == 0) return [row, col]; // untransformed
	// left to right, bottom to top: flip vertical
//	if (orientation == 1) return [H-row, col];
	// right to left, top to bottom: flip horizontal
//	if (orientation == 1) return [row, W-col];
	// right to left, bottom to top: flip horizontal, flip vertical
//	if (orientation == 3) return [H-row, W-col];
	// top to bottom, left to right: rotate 90 deg ccw + flip vertical
	if (orientation == 1) return [col, row];
	// top to bottom, right to left: rotate 90 deg ccw
//	if (orientation == 5) return [col, H-row];
	
	alert("bad orientation");
	
}