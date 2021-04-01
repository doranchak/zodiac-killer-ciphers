function distancesearch() {
	var dateStart = new Date();
	
	/*every pair of letters in the ciphertext has distance measurements.  distance can be measured in several directions:
		- 1D non-wrapped distance 
		- 1D wrapped distance 
		- 2D horizontal non-wrapped distance
		- 2D horizontal wrapped distance
		- 2D vertical non-wrapped distance
		- 2D vertical wrapped distance
		- 2D diagonal non-wrapped distance
		- 2D diagonal wrapped distance
		in all of the above, boundaries are wrapped so we can find matches that overlap the boundaries.
		
		let's define a map whose key is a pair of letters A and B, such that A and B are arranged in alphabet order with respect to the ASCII transcription.
		the key maps to an array of distances.

		loop through all possible combinations of pairs in the cipher text.  for each pair, compute each distance measurement 
		and store in an array structure keyed to A and B.  The key (A,B) will have one or more entries.  Find all entries
		for (A,B) that match on any distance measurement.  These entries indicate all sequences that start with A and end with B, and 
		have the same distance. 
		
		we must also store the (row,col) for both A and B so we can locate the sequences.
		
		for each equidistant set of matches for (A,B), figure out the number of additionally matching characters.  this increases the value
		of the match.  the score can be the length multiplied by or added to the number of matching characters.
		
		here is the map structure:
		
			Key (A,B) => [[d1,d2,...],[r1,c1,r2,c2]]
		
		*/
		
	var a = {}; var key = ""; var ch1, ch2;  var r1, r2, c1, c2; var d; var val;
	tmpstrings = {};
	
	for (var i=0; i<ciphertext.length; i++) {
		for (var j=i+1; j<ciphertext.length; j++) {
			ch1 = ciphertext.charAt(i);
			ch2 = ciphertext.charAt(j);
			r1 = rowFromLine(i);
			r2 = rowFromLine(j);
			c1 = colFromLine(i);
			c2 = colFromLine(j);
			
			if (block[r1][c1] != ch1) alert("FATAL1: something ain't matching.");
			if (block[r2][c2] != ch2) alert("FATAL2: something ain't matching.");
			
			key = ch1 < ch2 ? ch1+ch2 : ch2 + ch1;
			
			d = distances(r1,c1,r2,c2);
						
			if (!a[key]) {
				a[key] = [];
			}
			a[key][a[key].length] = [d,[r1,c1,r2,c2]];
		}
	}

	tmpa = a;
	
	var seen; var result = [];
	var L;
	bylength = {};
	for (key in a) { // key = AB
		val = a[key];  // val is array of [distances,[pair of points]]
		for (var i=0; i<val.length; i++) {
			for (var j=0; j<val[i][0].length; j++) { // for each element of the distances array
				L = val[i][0][j];
				if (L > 0) {
					if (!bylength[L]) bylength[L] = {};
					if (!bylength[L][key]) bylength[L][key] = {};
					bylength[L][key][val[i][1]] = j; // the symbols A,B of key are connected by distance L measured by method j.  the symbols are defined by points val[i][1].
				}
			}
		}
	}

	var total; var sum = 0;
	for (var key1 in bylength) {  //for each Length
		for (var key2 in bylength[key1]) { // for each AB
			total = 0;
			for (var key3 in bylength[key1][key2]) { // for each pair of points
				total++;
			}
			if (total > 2) sum++;
			else delete bylength[key1][key2]; // get rid of non-matches
		}
	}
	
	
	html = "";
	
	var stats = {};
	for (var key1 in bylength) {
		if (key1 < 20) {
			var sorted = [];
			var statsrow = [0,0,0,null,0,0,null]; // count (all), count (unique symbol pairs), best count, best count (symbols), number of internal matches, best internal match, best internal match (symbols)
			for (var key2 in bylength[key1]) {
				sorted.push([dscore(bylength[key1][key2],key1,key2), dcount(bylength[key1][key2]), key2]);
			}
			sorted.sort().reverse();
			for (var i=0; i<sorted.length; i++) {
				html += 'Length [<b>' + key1 + '</b>]  Count [<b>' + sorted[i][1] + '</b>]  Score [<b>' + round((sorted[i][0]+1-1/sorted[i][1])) + '</b>] Symbols [<b><a href="javascript:hdsearch(' + key1 + ',\'' + sorted[i][2] + '\')">' + encodestr(sorted[i][2]) + '</a></b>] ';
				statsrow[0] += sorted[i][1];
				statsrow[1] ++;
				if (sorted[i][1] > statsrow[2]) {
					statsrow[2] = sorted[i][1];
					statsrow[3] = sorted[i][2];
				}
				statsrow[4] += sorted[i][0];
				if (sorted[i][0] > statsrow[5]) {
					statsrow[5] = sorted[i][0];
					statsrow[6] = sorted[i][2];
				}
				if (tmpstrings[key1]) {
					var m = tmpstrings[key1][sorted[i][2]];
					if (m) { 
						html += 'Internal matches [';
						for (var j=0; j<m.length; j++) { // for each internal match
							for (k=0; k<m[j][0].length; k++) { // for each character of the matched string
								if (k>0 && k<m[j][0].length-1 && m[j][2][k-1]) html+='<span style="background-color: #faa">'+encode(m[j][0].charAt(k))+'</span>' // if this position matches 
								else html+=encode(m[j][0].charAt(k));
							}
							html += ', ';
							for (k=0; k<m[j][1].length; k++) { // for each character of the matched string
								if (k>0 && k<m[j][1].length-1 && m[j][2][k-1]) html+='<span style="background-color: #faa">'+encode(m[j][1].charAt(k))+'</span>' // if this position matches 
								else html+=encode(m[j][1].charAt(k));
							}
							if (j != m.length-1) html += '&nbsp;&nbsp;&nbsp;&nbsp;'
						}
						html += ']';
					}
				}
				html += "<br>";
			}
			stats[key1] = statsrow;
		}
	}
	html += "<h2>Totals:</h2><table class=\"totals\"><tr><th>Distance</th><th>Count</th><th>Symbol pairs</th><th>Best count</th>";
	html += "<th>Internal matches</th><th>Best internal match</th></tr>";
	
	for (var k in stats) {
		html += "<tr><td align=\"right\">" + k + "</td>" +
		"<td align=\"right\">" + stats[k][0] + "</td>" + 
		"<td align=\"right\">" + stats[k][1] + "</td>" + 
		"<td align=\"right\">" + stats[k][2] + (stats[k][3] == null ? "" : ' - <a href="javascript:hdsearch(' + k + ',\'' + stats[k][3] + '\')"><tt>' + encodestr(stats[k][3]) + '</tt></a>') + 
		"<td align=\"right\">" + stats[k][4] + "</td>" + 
		"<td align=\"right\">" + stats[k][5] + (stats[k][6] == null ? "" : ' - <a href="javascript:hdsearch(' + k + ',\'' + stats[k][6] + '\')"><tt>' + encodestr(stats[k][6]) + '</tt></a>')
		"</tr>";
	}
	html += "</table>";
	
	$("dsearch").innerHTML = html + timing(dateStart, new Date());
	
	return bylength;
	
}

function dcount(a) {
	var count = 0;
	for (key in a) count++;
	return count;
}

// get strings representing the paths associated with the given key
function stringFrom(L, chars, key, patharray) {
	var k = dKeyToArray(key);
	var p = pathfor(k[0],k[1],k[2],k[3],parseInt(bylength[L][chars][key]));
	if (patharray) patharray.push(p);
	var s = "";
	for (var i=0; i<p.length; i++) s+= block[p[i][0]][p[i][1]];
	return s;
}

function dscore(a, L, chars) {
	var s = []; var p = []; var m;  
	for (var key in a) {
		s.push(stringFrom(L, chars, key, p));
	}
	
	var total = 0; var max = 0;
	for (var i=0; i<s.length; i++) {
		for (var j=i+1; j<s.length; j++) {
			if (i==j) continue;
			m = dmatches(s[i], s[j], p[i], p[j]); 
			total += m[3];
			max = Math.max(max, m[3]);
			if (m[3] > 0) {
				if (!tmpstrings[L]) tmpstrings[L] = {};
				if (!tmpstrings[L][chars]) tmpstrings[L][chars] = [];
				tmpstrings[L][chars].push(m);
			} 
		}
	}
//	return total;
	return max;
	
}

function dscore2(a, L, chars) {
	var positions = {};
	var symbols = {};
	for (key in a) {
		var k = dKeyToArray(key);
		var p = pathfor(k[0],k[1],k[2],k[3],parseInt(bylength[L][chars][key]));
		for (var i=0; i<p.length; i++) {
			positions[p[i]] = true;
			if (p[i][0] > block.length) return -1; 
			if (p[i][1] > block[0].length) return -1;
			if (!block[p[i][0]]) alert("huh [" + p[i] + "] " + L + " " + chars);
			symbols[block[p[i][0]][p[i][1]]] = true;
		}
	}
	
	var N = dcount(positions);
	var U = dcount(symbols);
	return (N-U)/N;
}

// compute number of equal positions in the two strings.  ignore the first and last symbols since we know they already match.
// path arrays are included so we can avoid counting anything that overlaps.
function dmatches(s1, s2, p1, p2) {
	var s3;
	if (s1.charAt(0) != s2.charAt(0)) {
		s3 = "";
		for (var i=s1.length-1; i>=0; i--) s3 += s1.charAt(i);
		p1.reverse();
	} else s3 = s1;
	
	if (s2.charAt(0) != s3.charAt(0)) {
		alert("PROBLEM - nobody should have called me with non-matching begin and end symbols");
		return -1;
	}
	
	var count = 0; var m = [];
	for (var i=1; i<s2.length-1; i++) {
		if (s2.charAt(i) == s3.charAt(i) && (p1[i][0] != p2[i][0] || p1[i][1] != p2[i][1])) {
			m.push(true);
			count++;
		} else m.push(false);
	}
	return [s2, s3, m, count];
}

function dscoreFEH(a, L, chars) {
	var p1, p2, s1, s2;
	
	var score; var max = 0; var r, c;
	var positions = {};
	var symbols = {};
	for (var key1 in a) {
		var k1 = dKeyToArray(key1);
		for (var key2 in a) {
			if (key1==key2) continue;
			score = 0;
			var k2 = dKeyToArray(key2);
			p1 = pathfor(k1[0],k1[1],k1[2],k1[3],parseInt(bylength[L][chars][key1]));
			p2 = pathfor(k2[0],k2[1],k2[2],k2[3],parseInt(bylength[L][chars][key2]));
			if (p1.length != p2.length) { return -1; }
			for (var i=0; i<p1.length; i++) {
				try {
//					if (seen[p1[i]]) continue; // we already checked this block
//					if (seen[p2[i]]) continue; // we already checked this block
//					seen[p1[i]] = true; seen[p2[i]] = true;
				if (block[p1[i][0]][p1[i][1]] == block[p2[i][0]][p2[i][1]]) score++;
			} catch (err) { return -1; }
			}	
			max = Math.max(max, score);
			score = 0;
			for (var i=0; i<p1.length; i++) {
//				if (seen[p1[i]]) continue; // we already checked this block
//				if (seen[p2[i]]) continue; // we already checked this block
//				if (seen[p2[p1.length-1-i]]) continue; // we already checked this block
//				seen[p1[i]] = true; seen[p2[i]] = true; seen[p2[p1.length-1-i]] = true;
				if (block[p1[i][0]][p1[i][1]] == block[p2[p1.length-1-i][0]][p2[p1.length-1-i][1]]) score++
			}
			max = Math.max(max, score);
			score = 0;
		}
	}
	
	return max;
}

// determine score based on number of matches

// highlight the given path (array of row,col tuples)
function hpath(path) {
	for (var i=0; i<path.length; i++) hcell(path[i][0], path[i][1], true);
}

// get list of positions that cover the entire path from (r1,c1) to (r2,c2) using method m
function pathfor(r1,c1,r2,c2,m) {
	var path = [];
	var L = ciphertext.length;
	var W = block[0].length;
	var H = block.length;
	
	var count = 0;
	
	// 1D non-wrapped distance 
	if (m == 0) {
		var p1 = posFromBlock(r1,c1);
		var p2 = posFromBlock(r2,c2);
		
		var p3 = Math.min(p1,p2);
		var p4 = Math.max(p1,p2);
		
		for (var i=p3; i<=p4; i++) {
			path.push(fromLine(i));
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}
		return path;
	}
	// 1D wrapped distance 
	if (m == 1) {
		var p1 = posFromBlock(r1,c1);
		var p2 = posFromBlock(r2,c2);
		
		var p3 = Math.min(p1,p2);
		var p4 = Math.max(p1,p2);
		
		for (var i=p3; i>=0; i--) {
			path.push(fromLine(i));
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}
		for (var i=ciphertext.length-1; i>=p4; i--) {
			path.push(fromLine(i));
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}
		return path;
	}
	
	// 2D horizontal non-wrapped distance
	if (m == 2) {
		for (var i=(c1<c2 ? c1 : c2); i<=(c1<c2 ? c2 : c1); i++) {
			path.push([r1,i]);
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}
		return path;
	}
	// 2D horizontal wrapped distance
	if (m == 3) {
		for (var i=(c1<c2 ? c1 : c2); i>=0; i--) {
			path.push([r1,i]);
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}
		for (var i=W-1; i>=(c1<c2 ? c2 : c1); i--) {
			path.push([r1,i]);
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}			
		return path;
	}
	// 2D vertical non-wrapped distance
	if (m == 4) {
		for (var i=(r1<r2 ? r1 : r2); i<=(r1<r2 ? r2 : r1); i++) {
			path.push([i,c1]);
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}
		return path;
	}
	// 2D vertical wrapped distance
	if (m == 5) {
		for (var i=(r1<r2 ? r1 : r2); i>=0; i--) {
			path.push([i,c1]);
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}					
		for (var i=H-1; i>=(r1<r2 ? r2 : r1); i--) {
			path.push([i,c1]);
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}
		return path;
	}
	// 
	if (m == 6) {
		var c3, c4, r3, r4, dx, dy;
		if (r1 < r2) { // pick the topmost row as the starting point
			c3 = c1; r3 = r1;
			c4 = c2; r4 = r2;
		} else {
			c3 = c2; r3 = r2;
			c4 = c1; r4 = r1;
		}
		
		// two possibilities:  from the top, the diagonal goes to the right, or the left.  in both cases, the direction is downward.
		dy = 1;
		dx = c3<c4 ? 1 : -1;
		
		var row = r3; var col = c3;
		while (row != r4 && col != c4) {
			path.push([row, col]);
			row += dy;
			col += dx;
			count++; if (count > 1000) { alert("RUNAWAY LOOP"); break; }
		}
		path.push([r4, c4]);
		return path;
	}
	// 2D diagonal non-wrapped distance
//	d[6] = Math.abs(r1-r2) == Math.abs(c1-c2) ? Math.abs(r1-r2) : -1;
	// 2D diagonal wrapped distance.  DONT CARE FOR NOW
	
}

function dKeyToArray(key) {
	var a = [];
	var k = key.split(",");
	a.push(parseInt(k[0]));
	a.push(parseInt(k[1]));
	a.push(parseInt(k[2]));
	a.push(parseInt(k[3]));
	return a;
}
function hdsearch(L, chars) {
	var a = {};
	for (var key in bylength[L][chars]) {
		var k = dKeyToArray(key);
		var p = pathfor(k[0],k[1],k[2],k[3],parseInt(bylength[L][chars][key]));
		for (var i=0; i<p.length; i++) a[p[i]] = true;
	}
	for (var key in a) {
		var k = key.split(",");
		hcell(k[0],k[1],true);
	}
	window.location.href="#top";
}


// assumes perfect block arrangement (all cols are same height, all rows are same width)
// -1 means N/A
function distances(r1,c1,r2,c2) {
	if (r1==r2 && c1==c2) return null;
	var d = [];
	
	var W = block[0].length;
	var H = block.length;
	var L = ciphertext.length;
	
	var pos1 = r1*W + c1;
	var pos2 = r2*W + c2;
	
	// 1D non-wrapped distance 
	d[0] = Math.abs(pos1-pos2);
	// 1D wrapped distance 
	d[1] = pos1 < pos2 ? pos1+L-pos2 : pos2+L-pos1;
	// 2D horizontal non-wrapped distance
	d[2] = r1 == r2 ? Math.abs(c2-c1) : -1; 
	// 2D horizontal wrapped distance
	d[3] = r1 == r2 ? ( 
		c1 < c2 ? c1 + W - c2 : c2 + W - c1
	) : -1;
	// 2D vertical non-wrapped distance
	d[4] = c1 == c2 ? Math.abs(r2-r1) : -1;
	// 2D vertical wrapped distance
	d[5] = c1 == c2 ? (
		r1 < r2 ? r1 + H - r2 : r2 + H - r1
	) : -1;
	// 2D diagonal non-wrapped distance
	d[6] = Math.abs(r1-r2) == Math.abs(c1-c2) ? Math.abs(r1-r2) : -1;
	// 2D diagonal wrapped distance.  DONT CARE FOR NOW
	return d;
}
