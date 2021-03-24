function env() {
	var L = parseInt($("envl").value);
	html = "";
	
	var alpha = alphabetGet();
	var alphacounts = [];
	for (var i=0; i<alpha.length; i++) {
		alphacounts[i] = 0;
		var grid = []; var hits = [];
		for (var j=0; j<ciphertext.length; j++) {
			if (ciphertext[j] == alpha[i]) {
				alphacounts[i]++;
				grid[grid.length] = envsegment(j, L);
				var h = hits.length; hits[h] = [];
				for (var k=0; k<2*L+1; k++) hits[h][k] = 0;
			}
		}
		if (grid && grid.length > 1) {
			for (var col=0; col<grid[0].length; col++) {
				if (col == (grid[0].length-1)/2) continue;
				for (var row1=0; row1<grid.length; row1++) { // match in same column
					for (var row2=row1+1; row2<grid.length; row2++) {
						if (grid[row1][col] == grid[row2][col]) { hits[row1][col] = 1; hits[row2][col] = 1; }
					}
				}
				for (var row1=0; row1<grid.length; row1++) { // match in reflected column
					for (var row2=0; row2<grid.length; row2++) {
						if (grid[row1][col] == grid[row2][grid[0].length-1-col]) { 
							if (hits[row1][col] == 0) hits[row1][col] = 2;
							if (hits[row2][grid[0].length-1-col] == 0) hits[row2][grid[0].length-1-col] = 2;
						}
					}
				}
			}
		}
		if (grid && grid.length > 1) {
			var m1 = 0; var m2 = 0; var s1 = {}; var s2 = {};
//			var numerator = 0;
			for (var row=0; row<grid.length; row++) {
				var r = "";
				for (var col=0; col<grid[0].length; col++) {
					var c;
					if (col==(grid[0].length-1)/2) c = "envx";
					else if (hits[row][col]==0) c = "envnone";
					else if (hits[row][col]==1) c = "envin";
					else if (hits[row][col]==2) c = "envsym";
					var ch = encodestr(grid[row][col]);
					r += "<span class=\"" + c + "\">" + ch + "</span>";
					
					if (hits[row][col] == 1) {
						m1++;
//						numerator += alphacounts[i];
						if (s1[grid[row][col]] == null) s1[grid[row][col]] = 1;
						else s1[grid[row][col]]++;
					}
					else if (hits[row][col] == 2) {
						m2++;
//						numerator += alphacounts[i];
						if (s2[grid[row][col]] == null) s2[grid[row][col]] = 1;
						else s2[grid[row][col]]++;
					}
				}
				html += r + "<br>";
			}
//			var norm = (m1+m2) / (numerator/Math.pow(340, (m1+m2)));
			var counts = ngrams(ciphertext, 1);
			
			html += "<br>Results for symbol [<b>" + encodestr(alpha[i]) + "</b>] (which repeats [<b>" + alphacounts[i] + "</b>] times): In-column sum: [<b>" + m1 + "</b>] Symmetry column sum: [<b>" + m2 + "</b>], total: [<b>" + (m1+m2)+ "</b>]<br>";
			html += "In-column symbol counts: ";
			for (var key in s1) html += "[<span class=\"fw\">" + encodestr(key) + "</span>, <b>" + s1[key] + "</b>] ";
//			html += "<br>In-column symbol probabilities: ";
//			for (var key in s1) html += "[<span class=\"fw\">" + encodestr(key) + "</span>, <b>" + Math.pow(counts[key]/340, s1[key]) + "</b>] ";
//			for (var key in s1) html += "[<span class=\"fw\">" + encodestr(key) + "</span>, <b>" + envcount(340, L, alphacounts[i], counts[key], s1[key]) + "</b>] ";
			html += "<br>Symmetry column symbol counts: ";
			for (var key in s2) html += "[<span class=\"fw\">" + encodestr(key) + "</span>, <b>" + s2[key] + "</b>] ";
//			html += "<br>Symmetry column probabilities: ";
//			for (var key in s2) html += "[<span class=\"fw\">" + encodestr(key) + "</span>, <b>" + Math.pow(counts[key]/340, s2[key]) + "</b>] ";
//			for (var key in s2) html += "[<span class=\"fw\">" + encodestr(key) + "</span>, <b>" + envcount(340, L, alphacounts[i], counts[key], s2[key]) + "</b>] ";			
			html += "<br><br>";
		}
		
	}
	
	$("env").innerHTML = html;
}

function envsegment(i, L) {
	var s = "";
	for (var j=i-L; j<i+L+1; j++) {
		if (j>=0) s += ciphertext[j];
		else s += ciphertext[ciphertext.length + j];
	}
	return s;
}

//[340!/(340 - Yn)!] * [2*L] * [Xn!/(Xn-Cn)!] * [Yn! / (Yn - Cn)!]
function envcount(M, L, Yn, Xn, Cn) {
	var term1 = p(M,Yn);
	var term2 = p(Xn,Cn);
	var term3 = p(Yn,Cn);
	return  2*L*term1*term2*term3;
}

function p(n, k) {
	var r = 1;
	for (var i=n; i>n-k; i--) r*=i;
	return r;
}

function factorial(n) {
	if (n==0) return 1;
	if (n==1) return 1;
	return n*factorial(n-1);
}

function symboldistances() {
	var html = "Hover mouse over a cell to see an explanation.";
	var grid = []; // (i,j): symbol i's max min distance to occurrences of symbol j
	var alpha = alphabetGet();
	
	for (var i=0; i<alpha.length; i++) {
		grid[i] = [];
		for (var j=0; j<alpha.length; j++) {
			grid[i][j] = 0;
			if (i==j) continue;
			
			// all occurrences of symbol i
			for (var a=0; a<ciphertext.length; a++) {
				if (ciphertext[a] == alpha[i]) { // found symbol i.  look for nearest occurence of symbol j (in both directions)
					var min = ciphertext.length;
					for (var b=a+1; b<ciphertext.length; b++) { // to the right
						if (ciphertext[b] == alpha[j]) {
							min = Math.min(min, b-a);
							break;
						}
					}
					for (var b=a-1; b>=0; b--) { // to the left
						if (ciphertext[b] == alpha[j]) {
							min = Math.min(min, a-b);
							break;
						}
					}
					grid[i][j] = Math.max(grid[i][j], min);
				}
			}
		}
	}
	
	html += "<table class=\"distances-grid\">";
	html += "<tr><th></th>";
	for (var i=0; i<alpha.length; i++) {
		html += "<th>" + encodestr(alpha[i]) + " <span>(" + symbolcounts[alpha[i]] + ")</span></th>";
	}
	html += "</tr>";
	
	for (var i=0; i<grid.length; i++) {
		html += "<tr><th>" + encodestr(alpha[i]) + " <span>(" + symbolcounts[alpha[i]] + ")</span></th>";
		for (var j=0; j<grid.length; j++) {
			var title = "For all " + symbolcounts[alpha[i]] + " times symbol [" + encodestr(alpha[i]) + "] appears in the cipher text, symbol [" + encodestr(alpha[j]) + "], which appears " + symbolcounts[alpha[j]] + " times, can be found at most [" + grid[i][j] + "] positions away.";
			var td = i!=j && grid[i][j] <= 10 ? "<td style=\"background-color: #ddd\" title=\"" + title + "\">" : "<td title=\"" + title + "\">";
			html += td + (i==j ? "" : grid[i][j]) + "</td>";
		}
		html += "</tr>";
	}
	html += "</table>";
	$("distances").innerHTML = html;
}
