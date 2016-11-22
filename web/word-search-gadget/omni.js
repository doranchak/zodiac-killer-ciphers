function omnisearch() {
	var html = "";
	var diagonal = $("omnid").checked;
	var exclude = $("omnie").checked;
	// make index of positions for each symbol
	var hash = {};
	for (var row=0; row<block.length; row++) {
		for (var col=0; col<block[row].length; col++) {
			var ch = block[row][col];
			if (!hash[ch]) hash[ch] = [];
			hash[ch].push([row,col]);
		}
	}
	
	// matrix to track visited and unvisited cells
	var visited = [];
	for (var row=0; row<block.length; row++) visited[row] = [];
	
	var matches = []; // track list of matches
	var counts = {}; // track counts by sequence
	
	var a = alphabetGet();
	
	for (var i=0; i<a.length; i++) {
		var ch = a[i];  
		var p = hash[ch];
		
		for (var p1=0; p1<p.length; p1++) {
			for (var p2=p1+1; p2<p.length; p2++) {
				omnimatches(p[p1], p[p2], [], [], matches, counts, visited, [], diagonal);
			}
		}
	}
	
	// remove dupes from matches
	matches = uniquematches(matches);
	
	// sort in descending order of sequence length
	matches.sort(function(a,b){
		if (a[0].length < b[0].length) return 1;
		if (a[0].length > b[0].length) return -1;
		return 0;
	});

	html += "<table><tr><th>Sequence</th><th># of occurrences</th></tr>";
	for (var i=0; i<matches.length; i++) {
		if (exclude && excludematch(matches[i])) continue;
		var m1 = matches[i][0];
		var m2 = matches[i][1];
		var s = matches[i][2].join("");
		
		var onclick="";
		for (var j=0; j<m1.length; j++) {
			onclick += "hcell(" + m1[j][0]+","+m1[j][1]+"); ";
		}
		for (var j=0; j<m2.length; j++) {
			onclick += "hcellrgb(" + m2[j][0]+","+m2[j][1]+",null,100,200,100); ";
		}
		
		html += "<td><a href=\"#\" onclick=\"" + onclick + "\">" + encodestr(s) + "</a></td>";
		html += "<td>" + counts[s] + "</td>";
		
		html += "<tr>";
		html += "</tr>";
	}
	html += "</table>";
	
	$("omni").innerHTML = html;
}

// returns true if the given match is a normal repeated linear n-gram
function excludematch(m) {
	// one sequence must cover at least two rows and two columns
	var hr={}; var hc={}; var cr = 0; var cc = 0;
	for (var i=0; i<m[0].length; i++) {
		var r = m[0][i][0];
		var c = m[0][i][1];
		if (!hr[r]) {
			cr++;
			hr[r] = true;
		}
		if (!hc[c]) {
			cc++;
			hc[c] = true;
		}
	}
	if (cr > 1 && cc > 1) return false;
	hr = {}; hc = {}; cr = 0; cc = 0;
	
	for (var i=0; i<m[1].length; i++) {
		var r = m[1][i][0];
		var c = m[1][i][1];
		if (!hr[r]) {
			cr++;
			hr[r] = true;
		}
		if (!hc[c]) {
			cc++;
			hc[c] = true;
		}
	}
	if (cr > 1 && cc > 1) return false;
	
	return true;
	
}


// find all matches starting at the two given positions
// p1, p2: the two positions to search from
// m1: list of positions comprising the match so far for the first position
// m2: list of positions comprising the match so far for the second position
// matches: results array
// counts: counts by sequence
// visited: tracks which cells we've already visited
// seq: char array to track matching sequence
// diagonal: if true, include diaogonal adjacencies
// returns true if at least one match was found here
function omnimatches(p1, p2, m1, m2, matches, counts, visited, seq, diagonal) {
	var r1 = p1[0]; var c1 = p1[1];
	var r2 = p2[0]; var c2 = p2[1];

	if (r1 == r2 && c1 == c2) return false; // positions are the same
	
	if (visited[r1][c1]) return false; // already visited this position
	if (visited[r2][c2]) return false; // already visited this position

	if (block[r1][c1] != block[r2][c2]) return false; // not a match

	visited[r1][c1] = true;
	visited[r2][c2] = true;
	
	m1.push([r1,c1]); m2.push([r2,c2]);

	var ch = block[r1][c1];
	seq.push(ch);
	
	// compare all adjacent cells of p1 and p2
	var a1 = adjacent(p1, diagonal);
	var a2 = adjacent(p2, diagonal);
	
	var found = false;
	
	for (var i=0; i<a1.length; i++) {
		for (var j=0; j<a2.length; j++) {
			found |= omnimatches(a1[i], a2[j], m1.slice(0), m2.slice(0), matches, counts, visited, seq.slice(0), diagonal);
		}
	}

	// done searching for matches here.  time to backtrack.
	// if we found no matches at all, then track the current match, because it was the best we found along this path.
	if (!found && seq.length > 1) {
		matches.push([m1.slice(0), m2.slice(0), seq.slice(0)]);
		var key = seq.join("");
		var val = counts[key];
		if (!val) val = 1; else val++;
		counts[key] = val;
	}
	
	visited[r1][c1] = false;
	visited[r2][c2] = false;
	
	return true; // if we got this far, at least one match was found here.
	
}

// return list of adjacent positions to p.
// if diagonal is true, diagonal adjacencies are also included
function adjacent(p, diagonal) {
	var list = [];
	
	var r=p[0]; var c=p[1];
	list.push([r, (c+1)%block[0].length]); // right
	list.push([r, (c-1+block[0].length)%block[0].length]); // left
	list.push([(r+1)%block.length, c]); // down
	list.push([(r-1+block.length)%block.length, c]); // up
	
	if (diagonal) {
		list.push([(r+1)%block.length, (c+1)%block[0].length]); // bottom right
		list.push([(r+1)%block.length, (c-1+block[0].length)%block[0].length]); // bottom left
		list.push([(r-1+block.length)%block.length, (c-1+block[0].length)%block[0].length]); // top left
		list.push([(r-1+block.length)%block.length, (c+1)%block[0].length]); // top right
	}
	
	return list;
}

function uniquematches(matches) {
	var m = [];
	var hash = {};
	for (var i=0; i<matches.length; i++) {
		var tmp = matches[i][0].slice(0);
		tmp = tmp.concat(matches[i][1].slice(0));
		var key = ""+tmp.sort();
		if (hash[key]) continue;
		m.push(matches[i]);
		hash[key] = true;
	}
	return m;
}

