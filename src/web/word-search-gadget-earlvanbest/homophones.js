// cache of dumped sequences
//var sequenceCache = {};

// obtain the max repeating substring of the given string.  from http://stackoverflow.com/questions/3898083/find-longest-repeating-substring-in-javascript-using-regular-expressions
function maxRepeat(input) {
 var reg = /(?=((.+)(?:.*?\2)+))/g;
 var sub = ""; //somewhere to stick temp results
 var maxstr = ""; // our maximum length repeated string
 reg.lastIndex = 0; // because reg previously existed, we may need to reset this
 sub = reg.exec(input); // find the first repeated string
 while (!(sub == null)){
  if ((!(sub == null)) && (sub[2].length > maxstr.length)){
   maxstr = sub[2];
  }
  sub = reg.exec(input);
  reg.lastIndex++; // start searching from the next position
 }
 return maxstr;
}

function repeatScore(input, n) {
	var counts = {}; var sub; var max = 0; var maxPattern;
	for (var i=0; i<input.length-n+1; i++) {
		sub = input.substring(i, i+n);
		var c = 0; var u = {};
		for (var j=0; j<sub.length; j++) { 
			if (!u[sub.charAt(j)]) c++; 
			u[sub.charAt(j)] = true;
		}			
		if (c == n) {
			if (counts[sub]) counts[sub]++;
			else counts[sub] = 1;
			if (counts[sub] > max) {
				max = counts[sub];
				maxPattern = sub;
			}
		}
	}
	return [max, maxPattern];
}

// returns maximum repeating substrings within the given string.  returns array of [number of repeats, repeated substring].
// ignore any substring less than 2 characters in length.
function repeatingSubstrings(input) {
	// get max repeat of the current input
	var m = maxRepeat(input);
	if (m.length < 2) return null;

	var a = [];
	
	if (m.split(m.charAt(0)).length > m.length) return null; // ignore trivial one-char repeats (example: ++++)
	a[1] = m;

	var s = input.split(m);
	if (s) {
		if (s.length > 0) 
			a[0] = s.length-1;
	}
	return a;
	/*
		for (var i=0; i<s.length; i++) {
			if (s[i].length > 3) {
				var r = repeatingSubstrings(s[i]);
				if (r) {
					a[0] += r[0];
					a[1] = a[1].concat(r[1]);
				}
			}
		}
	}
	return a;
*/	
}

//function resetSequenceCache() {
//	sequenceCache = {};
//}
// return ciphertext with all symbols removed except for the ones in the given hash set
// also return positions of the returned symbols
function dumpSequence(symbols) {
	
//	var key = "";
//	for (var i in symbols) key += i;
//	if (sequenceCache[key]) return sequenceCache[key];
	
	var s = ""; var pos = [];
	for (var i=0; i<ciphertext.length; i++) if (symbols[ciphertext.charAt(i)]) {
		s+= ciphertext.charAt(i);
		pos[pos.length] = i;
	}
//	sequenceCache[key] = [s, pos];
	return [s, pos];
}

// run dumpSequence with the given symbols (as string)
function dumpSequenceFor(symbols) {
	var s = {}; for (var i=0; i<symbols.length; i++) s[symbols[i]] = true; return dumpSequence(s);
}

// brute force search for homophones by finding patterns in sequences of symbol usage
function homophoneSearchN2OLD() {
	var a = alphabetGet();
	var ch; var s; var c; var r; var dump;
	var result = [];
	
	for (var i=0; i<a.length; i++) {
		for (var j=i+1; j<a.length; j++) {
			s = {};
			s[a[i]] = true;
			s[a[j]] = true;
			dump = dumpSequence(s);
			c = dump[0];
			r = repeatingSubstrings(c);
				if (r) 
					result.push([0, c.length, r[1].length, r[0], contig(c, r[1]), r[0]*r[1].length/c.length, cyclerate(r[1], 2), r[1], s, c]);
		}
	}
	
	var max = [];
	for (var i=0; i<6; i++) max[i] = 0;
	for (var i=0; i<result.length; i++) {
		for (var j=0; j<6; j++) max[j] = Math.max(max[j], result[i][j+1]);
	}
	for (var i=0; i<result.length; i++) {
		var sum = 0;
		for (var j=0; j<6; j++) sum += result[i][j+1]/max[j];
		result[i][0] = sum;
	}
	
	return result;
}

// combine all symbols in the given list.  replace the ciphertext with a version with merged symbols.
function mergesymbols(symbols) {
	var u = {};
	for (var i=1; i<symbols.length; i++) u[symbols[i]] = true;
	
	var r = symbols[0]; // we are going to replace the other symbols with the first symbol.
	
	var inp = $("input").value;
	
	var newinp = "";
	for (var i=0; i<inp.length; i++) {
		var ch = inp[i];
		if (u[ch]) newinp += r;
		else newinp += ch;
	}
	
	$("input").value = newinp;
	update();
	new Effect.Highlight("render");
	window.location.href="#top";
}

function homophonesearch(N) {
	var dateStart = new Date();
	var h = N == 2 ? homophoneSearchN2() : N == 3 ? homophoneSearchN3() : homophoneSearchN4();
	tmphomophones = h;
	h.sort().reverse();

	var sums = [0, 0, 0, 0];  // score, repeats, longest run, ratio
	var min = [1e99, 1e99, 1e99, 1e99];
	var max = [0, 0, 0, 0];
	var html = "";
	
	var byrun = [];
	
	var symbolcounts = ngrams(ciphertext, 1);
	
	html += 'Display homophone candidate score map <a href="javascript:homophonemap(tmphomophones, false)">(using sum)</a> | <a href="javascript:homophonemap(tmphomophones, true)">(using max)</a> <br>';

	var include408 = ciphertext == cipherline[1];
	if (include408) {
		var h408 = homophonesFor408();
		html += "Homophones of the 408: "
		for (key in h408) {
			html += "<b>" + key + "</b> [<tt style=\"color: #999\">" + encodestr((""+h408[key]).replace(/,/g,"")) + "</tt>] ";
		}
	}
	
	html += "<table><tr><th>Score</th><th>Sequence</th><th>Pattern</th><th>Repeats</th><th>Longest run (% of pattern)</th><th>Weighted score</th>" + (include408 ? "<th>Actual homophone of the 408?</th>" : "") + "</tr>";
	var nulls = "";
	var tophalf = 0;
	for (var i=0; i<h.length; i++) {
		if (h[i] != null) {
			var html2 = "";
		html2 += "<tr>";
		html2 += "<td>" + round(h[i][0]) + "</td>";
		html2 += "<td><tt>" + encodestr(h[i][3]) + "</tt>&nbsp;&nbsp;<i><a style=\"color: #999; font-size: 8pt\" href=\"javascript:mergesymbols('" + h[i][3] + "')\">[Merge]</a></i></td>";
		html2 += '<td><tt><a href="javascript:hsearcheach(\'' + h[i][3] + '\')">' + encodestr(h[i][4]) + "</a></tt></td>";
		html2 += "<td>" + h[i][1] + "</td>";
		var perc = 100*N*(h[i][2]+1)/h[i][4].length;
		html2 += "<td>" + h[i][2] + " (" + Math.round(100*perc)/100 + "%)</td>"; // 2 * (runCount+1) / patternLength = ratio of run length to entire pattern
		
		//html2 += "<td>" + homprob(N, h[i][1], h[i][4].length - N*h[i][1], h[i][3], h[i][4].length ) + " args " + (N + ", " + h[i][1] + ", " + (h[i][4].length - N*h[i][1]) + ", " + h[i][3] + ", " + h[i][4].length) + "</td>";
		// weighted score = longest run / sum of symbol counts 
		var sum = 0;
		if (h[i][3]) for (var j=0; j<h[i][3].length; j++) sum += symbolcounts[h[i][3][j]];
		var weighted = h[i][2] / sum;
		html2 += "<td>" + weighted + "</td>";
		
		if (include408) {
			html2 += "<td>";
			var u = {};
			if (h[i][3])
				for (var j=0; j<h[i][3].length; j++)
					u[decode408For(h[i][3][j])] = true;
			if (dcount(u) == 1) html2 += '<span style="color: #0a0">Yes</span>'; else html2 += '<span style="color: #a00">No</span>';
			html2 += "</td>"
		}
		html2 += "</tr>";
		
		sums[0] += h[i][0]; min[0] = Math.min(min[0], h[i][0]); max[0] = Math.max(max[0], h[i][0]);
		sums[1] += h[i][1]; min[1] = Math.min(min[1], h[i][1]); max[1] = Math.max(max[1], h[i][1]);
		sums[2] += h[i][2]; min[2] = Math.min(min[2], h[i][2]); max[2] = Math.max(max[2], h[i][2]);
		sums[3] += perc; min[3] = Math.min(min[3], perc); max[3] = Math.max(max[3], perc);
		
		if (byrun[h[i][2]]) byrun[h[i][2]]++; else byrun[h[i][2]] = 1; 
		if (h[i][0] >= 0.5 && i < 1000) html += html2;
		if (h[i][0] >= 0.5) tophalf++;
	} else nulls += i + " ";
	}
	html += "</table>";
	
	if (nulls != "") alert("Unexpected nulls: " + nulls);
	
	// summaries
	
	html += "Tested <b>" + h.length + "</b> combinations of symbols.  <b>" + tophalf + "<b> (" + Math.round(1000*100*tophalf/h.length)/1000 + "%) of them scored 0.5 or higher.<br>"
	html += "<table><tr><th>Which</th><th>Min</th><th>Max</th><th>Sum</th><th>Average</th></tr>";
	for (var i=0; i<4; i++) {
		html += "<tr><td>";
		if (i==0) html += "Score";
		else if (i==1) html += "Repeats";
		else if (i==2) html += "Longest Run";
		else if (i==3) html += "Run Ratio";
		else html += "ERROR";
		html += "</td>";
		html += "<td>" + round(min[i]) + "</td>";
		html += "<td>" + round(max[i]) + "</td>";
		html += "<td>" + round(sums[i]) + "</td>";
		html += "<td>" + round(sums[i]/h.length) + "</td>";
		html += "</tr>";
	}
	html += "</table>";

	html += "<table><tr valign='top'><td>"
	html += "<table><tr><th>Longest Run Length</th><th>Number of Occurrences</th></tr>";
	var maxnum = 0; var ds = "";
	for (var i=0; i<byrun.length; i++) {
		html += "<tr><td>" + i + "</td><td>";
		if (byrun[i]) {
			html += byrun[i]; 
			maxnum = Math.max(maxnum, byrun[i]);
			ds += byrun[i];
		} else {
			html += "0";
			ds += "0";
		}
		if (i<byrun.length-1) ds += ",";
		html == "</td></tr>"
	}
	html += "</table></td><td>"
	html += '<img src="http://chart.apis.google.com/chart?chxr=0,0,' + maxnum + '|1,0,' + max[2] + '&chxt=y,x&chs=300x225&cht=lc&chco=3D7930&chds=0,' + maxnum + '&chd=t:' + ds + '&chg=14.3,-1,1,1&chls=2,4,0&chm=B,C5D4B5BB,0,0,0&chtt=Longest%20Run%20Lengths"/>';
	html += "</td></tr></table>"
	
	var which = "homo" + (N == 2 ? "" : N == 3 ? "2" : "3");
	$(which).innerHTML = html + timing(dateStart, new Date());
}

/** each of m homophone cycles are distributed, in order, to the k slots between (k-1) extra symbols.  this routine enumerates all possible distributions. */
function homcycle(m, k) {
	var a = []; // assignments of cycles to slots
//	var html = "";
	for (var i=0; i<m; i++) a[i] = 0;
	var go = true;
//	html += a + "<br>";
	var count = 1;
	while (go) {
		var pos=a.length-1;
		a[pos]++;
		while (a[pos]==k) {
			if (pos == 0) { go=false; break; }
			a[pos-1]++;
			for (var i=pos; i<m; i++) a[i]=a[i-1];
			pos--;
		}
		if (!go) break;
		count++;
//		html += a + "<br>";
	}
//	document.write(count + "<hr>" +html);
	return count;
}

/** THIS IS BROKEN.  count number of distinct ways to assign the given letters to k slots */
function homslots(k, letters) {
	var hash = {};

	var a = []; // assignment of i'th letter to slot [0,k)
	for (var i=0; i<letters.length; i++) a[i] = 0;

	var i=letters.length-1; var go=true;
	while (go) {
		
		var r = [];
		for (var j=0; j<k; j++) r[j]="";
		for (var j=0; j<a.length; j++) r[a[j]] += letters[j];
		var s = "";
		for (var j=0; j<r.length; j++) s += r[j] + ",";
		hash[s]= true;

		a[i]++;
		while (a[i]==k) {
			a[i--] = 0;
			if (i<0) {
				go=false;
				break;
			}
			a[i]++;
		}
		i=letters.length-1;


	}
	var c = 0;
	var html = "";
	for (var key in hash) {
		c++;
		html += key + "<br>";
	}
	document.write(html);
	return c;
}

/* compute prob of this kind of hom pattern occurring by chance.  uses S = c! (m+1)^k ∏(f(i)!) / L!, 1 <= i <= c */
function homprob(c, m, k, cycle, L) {
	var result = factorial(c) * Math.pow(m+1, k);
	for (var i=0; i<cycle.length; i++) result *= factorial(symbolcounts[cycle[i]]);
	result /= factorial(L);
	return result;
}

function homophoneSearchN2() {
	N = 2;
	var a = alphabetGet();
	var ch; var s; var c; var r; var dump;
	var result = [];
	
	for (var i=0; i<a.length; i++) {
		for (var j=i+1; j<a.length; j++) {
			s = {};
			s[a[i]] = true;
			s[a[j]] = true;
			dump = dumpSequence(s);
			c = dump[0];
			r = repeatScore(c,N);
			result.push([0, r[0], contig(c, r[1], dump[1]), r[1], c, dump[1]]);
		}
	}

	var max = [0, 0];
	for (var i=0; i<result.length; i++) {
		max[0] = Math.max(max[0], result[i][1]);
		max[1] = Math.max(max[1], result[i][2]);
	}
	for (var i=0; i<result.length; i++) {
		//combined rank of num of repeats, and longest run length
		//result[i][0] = (result[i][1]/max[0] + result[i][2]/max[1])/2;
		//combined rank of longest run length, and ratio of longest run length to pattern length
		result[i][0] = (result[i][2]/max[1] + N*(result[i][2]+1)/result[i][4].length)/2;
	}
	
	return result;
}

function homophoneSearchN3() {
	N = 3;
	var a = alphabetGet();
	var ch; var s; var c; var r; var dump;
	var result = [];
	
	for (var i=0; i<a.length; i++) {
		for (var j=i+1; j<a.length; j++) {
			for (var k=j+1; k<a.length; k++) {
				s = {};
				s[a[i]] = true;
				s[a[j]] = true;
				s[a[k]] = true;
				dump = dumpSequence(s);
				c = dump[0];
				r = repeatScore(c,N);
				result.push([0, r[0], contig(c, r[1], dump[1]), r[1], c, dump[1]]);
			}
		}
	}

	var max = [0, 0];
	for (var i=0; i<result.length; i++) {
		max[0] = Math.max(max[0], result[i][1]);
		max[1] = Math.max(max[1], result[i][2]);
	}
	
	for (var i=0; i<result.length; i++) {
		//combined rank of num of repeats, and longest run length
		//result[i][0] = (result[i][1]/max[0] + result[i][2]/max[1])/2;
		//combined rank of longest run length, and ratio of longest run length to pattern length
		result[i][0] = (result[i][2]/max[1] + N*(result[i][2]+1)/result[i][4].length)/2;
	}
	
	return result;
}

function homophoneSearchN4() {
	N = 4;
	var a = alphabetGet();
	var ch; var s; var c; var r; var dump;
	var result = [];
	
	for (var i=0; i<a.length; i++) {
		for (var j=i+1; j<a.length; j++) {
			for (var k=j+1; k<a.length; k++) {
				for (var l=k+1; l<a.length; l++) {
					s = {};
					s[a[i]] = true;
					s[a[j]] = true;
					s[a[k]] = true;
					s[a[l]] = true;
					dump = dumpSequence(s);
					c = dump[0];
					r = repeatScore(c,N);
					result.push([0, r[0], contig(c, r[1], dump[1]), r[1], c, dump[1]]);
				}
			}
		}
	}

	var max = [0, 0];
	for (var i=0; i<result.length; i++) {
		max[0] = Math.max(max[0], result[i][1]);
		max[1] = Math.max(max[1], result[i][2]);
	}
	
	for (var i=0; i<result.length; i++) {
		//combined rank of num of repeats, and longest run length
		//result[i][0] = (result[i][1]/max[0] + result[i][2]/max[1])/2;
		//combined rank of longest run length, and ratio of longest run length to pattern length
		result[i][0] = (result[i][2]/max[1] + N*(result[i][2]+1)/result[i][4].length)/2;
	}
	
	return result;
}

// count max number of repeating contiguous sequences
function contig(str, pattern, positions) {
	var sp = str.split(pattern);
	
	var found = false; var max = 0;
	var count = 0;
	for (var i=1; i<sp.length-1; i++) {
		if ("" == sp[i]) { // this indicates adjacent patterns
			if (found) count++; // keep counting
			else { // begin counting
				count = 1;
				found = true;
			}
		}
		else if (found) { // no longer finding adjacent patterns
			found = false;
			max = Math.max(max, count);
			count = 0;
		}
		if (found && i==sp.length-2) { // kept finding adjacent patterns at the end
			max = Math.max(max, count);
		}
	}
	
	// update array of positions to only count the ones that are part of the max contiguous repeats
	var s = "";
	for (var i=0; i<max+1; i++) s += pattern;
	var index = str.indexOf(s);
	for (var i=0; i<positions.length; i++) {
		if (i<index || i>=index+s.length) positions[i] = -1;
	}
	
	return max;
}

// measure average uniqueness rate per n characters
function cyclerate(str, n) {
	var sum = 0; var count = 0;
	for (var i=0; i<str.length-n+1; i++) {
		count++;
		var u = {};
		for (var j=i; j<i+n; j++) {
			u[str.charAt(j)] = true;
		}
		sum += dcount(u)/n;
	}
	
	return sum/count;
	
}

// compute homophone scores for each position of the cipher text.
// the score of a position is the sum of all longest-runs found for all homophone candidates that share the position
// or, if max is true, the score is the max longest-run found for this position.
function hscorecipher(h, max) {
	var scores = [];
	for (var i=0; i<ciphertext.length; i++) scores[i] = 0;
	for (var i=0; i<h.length; i++) {
		for (var j=0; j<h[i][5].length; j++) {
			if (h[i][5][j] > -1) {
				if (max) scores[h[i][5][j]] = Math.max(scores[h[i][5][j]], h[i][2]);
				else scores[h[i][5][j]] += h[i][2];
			}
		}
	}
	return scores;
}

// render a map of the homophone candidate scores
function homophonemap(h, usemax) {
	var scores = hscorecipher(h, usemax);
	var max = scores.max();

	for (var i=0; i<ciphertext.length; i++) {
		var rc = fromLine(i);
		var rank = (1-scores[i]/max);
		var color = Math.round(rank*255);
		setbg(rc[0], rc[1], color);
	}
	
	jumptop();
}

// return the length of the continuous repeated subsequence the given letter belongs to, for the given homophone candidate length
// sequence: the entire cipher text sequence for a given combination of symbols
// pos: the position of the sequence we are inspecting
// len: the homophone candidate length
// returns [length, subsequence]
// i gave up on this 
function homlengthUNUSED(sequence, pos, len) {
	// for each possible repeated subsequence
	var bestCount = 0;
	var bestSubsequence;
	for (var i=0; i<len; i++) {
		var start=pos-i;
		var end=pos-i+len-1;
		if (start >= 0 && end < sequence.length) {
			var H = sequence.substring(start,end+1);
			var j = i; // position in H
			var k = pos; // position in sequence
			var sub = ""; // subsequence
			var count = 0; // length of repeated subsequences
			
			while (k < sequence.length) {
				if (sequence.charAt(k) == H.charAt(j)) {
					count++;
				}
				k++;
				j = (j+1)%len;
			}
		}
	}	
}

/* grid method for finding sequential homophones */
function homogrid() {
	var alpha = alphabetGet();
	
	var pairs = {};
	var num = alpha.length;
	
	for (var i=0; i<num; i++) { 
		pairs[alpha[i]] = {}; 
		for (var j=0; j<num; j++) 
			pairs[alpha[i]][alpha[j]] = 0;
	}
	
	/* sequences for each symbol */
	var sequences = {};
	for (var i=0; i<num; i++) {
		sequences[alpha[i]] = [];
		var s = ciphertext.split(alpha[i]);
		for (var j=0; j<s.length; j++) {
			if (j==0) continue;
			sequences[alpha[i]].push(alpha[i]+s[j]);
		}
	}
	
	for (var i=0; i<num; i++) {
		for (var j=0; j<sequences[alpha[i]].length-1; j++) {
			for (var k=1; k<sequences[alpha[i]][j].length; k++) pairs[alpha[i]][sequences[alpha[i]][j][k]]++;
		}
	}
//	return pairs;
	var html;
	html += "<br>Pair counts:";
	html += "<table><tr><td></td>";
	for (var i=0; i<alpha.length; i++) html += "<td>" + encodestr(alpha[i]) + "</td>";
	html += "</tr>";
	for (var i=0; i<alpha.length; i++) {
		html += "<tr><td>" + encodestr(alpha[i]) + "</td>";
		for (var j=0; j<alpha.length; j++) {
			html += "<td>" + pairs[alpha[i]][alpha[j]] + "</td>";
		}
		html += "</tr>";
	}
	html += "</table>";
	
	
	$("homogrid").innerHTML = html;
}	



