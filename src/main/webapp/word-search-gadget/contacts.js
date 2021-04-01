var after;
var before;
var contactvector;
var sims;
var simssorted;
var simssorted2;

var resultmax;
var cossum; 
var cossum25;

function updateContactVector(d) {
	// update the contact vector.
	contactvector = {};
	for (var i=0; i<ciphertext.length; i++) {
		var x, xp, xf;
		x = ciphertext.charAt(i);
		if (i-d>=0) xp = ciphertext.charAt(i-d);
		if (i<ciphertext.length-d) xf = ciphertext.charAt(i+d);
		if (!contactvector[x]) {contactvector[x] = []; contactvector[x][0] = {}; contactvector[x][1] = {};}
		
		var val;
		// xp precedes x
		val = contactvector[x][0][xp];
		if (!val || val == 0) val = 1; else val++; contactvector[x][0][xp] = val;
		// xf follows x
		val = contactvector[x][1][xf];
		if (!val || val == 0) val = 1; else val++; contactvector[x][1][xf] = val;
	}	
	

}	

function renderContacts() {
	var dateStart = new Date();
	before = {};
	after = {};
	
	var ch1, ch2;
	
	var maxbefore = 0;
	var maxafter = 0;
	
	for (var i=0; i<ciphertext.length-1; i++) {
		ch1 = ciphertext.charAt(i);
		ch2 = ciphertext.charAt(i+1);
		if (!before[ch1]) before[ch1] = {};
		if (before[ch1][ch2]) {
			before[ch1][ch2]++;
		} else before[ch1][ch2] = 1;
		maxbefore = Math.max(maxbefore, before[ch1][ch2]);

		if (!after[ch2]) after[ch2] = {};
		if (after[ch2][ch1]) {
			after[ch2][ch1]++;
		} else after[ch2][ch1] = 1;
		maxafter = Math.max(maxafter, after[ch2][ch1]);
		
	}
	
	updateContactVector(1);
	
	var a = alphabetGet();
	var html = "";
	var count;

	html += "Each grid entry is a count of how many times the row's character appears before the column's character.<br>";
	for (var i=-1; i<a.length; i++) {
		for (var j=-1; j<a.length; j++) {
			if (i==-1 && j==-1) html += "<span class=\"cell2\">&nbsp;</span>";
			else if (i==-1) html += "<span class=\"cell2\">" + pad(a[j]) + "</span>";
			else if (j==-1) html += "<span class=\"cell2\">" + pad(a[i]) + "</span>";
			else {
				ch1 = a[i]; ch2 = a[j];
				if (before[ch1] && before[ch1][ch2]) count = before[ch1][ch2];
				else count = 0;

				var rank = (1-count/maxbefore);
				var color = Math.round(rank*255);
				var font = (color > 127 ? "black" : "white");
				
				html += '<span class="cell2" style="background-color: rgb(' + color + ',' + color + ',' + color + '); color: ' + font + '">' + (count > 0 ? '<a style="color: ' + font + '" href="javascript:hsearch(\'' + ch1+ch2 + '\')">' + pad(""+count) + '</a>' : pad(""+count)) + '</span>';
			}
		}
		html+="<br>";
	}
	
/*	
	for (var i=-1; i<a.length; i++) {
		for (var j=-1; j<a.length; j++) {
			if (i==-1 && j==-1) html += "<span class=\"cell2\">&nbsp;</span>";
			else if (i==-1) html += "<span class=\"cell2\">" + pad(a[j]) + "</span>";
			else if (j==-1) html += "<span class=\"cell2\">" + pad(a[i]) + "</span>";
			else {
				ch1 = a[i]; ch2 = a[j];
				if (after[ch1] && after[ch1][ch2]) count = after[ch1][ch2];
				else count = 0;

				var rank = (1-count/maxafter);
				var color = Math.round(rank*255);
				var font = (color > 127 ? "black" : "white");

				html += '<span class="cell2" style="background-color: rgb(' + color + ',' + color + ',' + color + '); color: ' + font + '"><a style="color: ' + font + '" href="javascript:hsearch(\'' + ch2+ch2 + '\')">' + pad(""+count) + "</a></span>";
			}
		}
		html+="<br>";
	}*/
	
	html += "<span id='cosine'></span>";

	$('contacts').innerHTML = html + timing(dateStart, new Date());
	renderCosine(1);
}

function renderCosine(distance) {
	var a = alphabetGet();
	var html = "Cosine similarities [d=" + distance + "]: (<a href=\"javascript:renderCosine(1)\">d=1</a> | <a href=\"javascript:renderCosine(2)\">d=2</a> | <a href=\"javascript:renderCosine(3)\">d=3</a> | <a href=\"javascript:renderCosine(4)\">d=4</a> | <a href=\"javascript:renderCosine(5)\">d=5</a>)<br><div id=\"cos\">";
	
	convec(distance);
	
	var z408 = "";
	var is408 = ciphertext == cipherline[1];
	cossum = 0; cossum25 = 0;
	
	resultmax = simssorted[0][1];
	
	for (var i=0; i<simssorted.length; i++) {
		if (is408) {
			z408 = "Actual homophone? [";
			if (decode408For(simssorted[i][0][0]) == decode408For(simssorted[i][0][1])) z408 += "<span style=\"color: red\">Yes</span>"; else z408 += "No";
			z408 += "]";
		}
		html += "Symbols: [" + encodestr(simssorted[i][0]) + "] Cosine similarity: [" + simssorted[i][1] + "] " + z408 + "<br>";
		cossum += simssorted[i][1];
		if (i<25) cossum25 += simssorted[i][1];
	}
	html += "</div>";
	html += "Sum: [" + cossum + "] Mean [" + (cossum/simssorted.length) + "] Median [" + simssorted[Math.floor(simssorted.length/2)][1] + "]<br>";
	html += "Top 25 Sum: [" + cossum25 + "] Mean [" + (cossum25/25) + "]<br>";

	// now, perform the merges of similar letters
	simssorted2 = simssorted.slice();
	var oldcipher = ciphertext;
	var merges = [];
	var count = 0; var total = a.length-1;
	do {
		var key = simssorted[0][0];
		var keys = {}; // track the merged keys
		var ch1 = key[0]; var ch2 = key[1];
		if (is408) {
			z408 = "Actual homophone? [";
			if (decode408For(ch1) == decode408For(ch2)) z408 += "<span style=\"color: red\">Yes</span>"; else z408 += "No";
			z408 += "]";
		}
		
		html += "Merging symbols " + encodestr(ch1) + " and " + encodestr(ch2) + ", which have similarity " + simssorted[0][1] + ". " + z408 + "<br>";

		var ciphertext2 = "";
		for (var i=0; i<ciphertext.length; i++) if (ciphertext[i] == ch2) ciphertext2 += ch1; else ciphertext2 += ciphertext[i];
		ciphertext = ciphertext2;
		convec();
		
		//track sets of symbols that were merged together
		var found = [-1, -1]; var fi = 0;
		for (var i=0; i<merges.length; i++) {
			if (!merges[i]) continue;
			if (merges[i][ch1] || merges[i][ch2]) {
				merges[i][ch1] = true;
				merges[i][ch2] = true;
				found[fi++] = i;
			}
		}
		// if a set wasn't found, create a new one
		if (fi == 0) {
			var m = {}; m[ch1] = true; m[ch2] = true;
			merges.push(m);
		} 
		// if two sets matched, then we need to merge them
		else if (fi == 2) {
			var m = {};
			for (ch in merges[found[0]]) m[ch] = true;
			for (ch in merges[found[1]]) m[ch] = true;
			delete merges[found[0]];
			delete merges[found[1]];
			merges.push(m);
		}
		
		html += "Merges: ";
		for (var i=0; i<merges.length; i++) {
			if (!merges[i]) continue;
			html += "[";
			for (var ch in merges[i]) html += encodestr(ch);
			html += "] ";
			
		}
		html += "<br>";
		count++;
	} while (count < total); //(sims.length > 1);
	$("cosine").innerHTML = html;
	ciphertext = oldcipher;
}	

function convec(d) {
	// update the contact vector.
	contactvector = {};
	for (var i=0; i<ciphertext.length; i++) {
		var x, xp, xf;
		x = ciphertext.charAt(i);
		if (i-d>=0) xp = ciphertext.charAt(i-d);
		if (i<ciphertext.length-d) xf = ciphertext.charAt(i+d);
		if (!contactvector[x]) {contactvector[x] = []; contactvector[x][0] = {}; contactvector[x][1] = {}; } //contactvector[x][2] = {}; contactvector[x][3] = {};}
		
		var val;
		// xp precedes x
		if (xp != null) {
			val = contactvector[x][0][xp];
			if (!val || val == 0) val = 1; else val++; contactvector[x][0][xp] = val;
		}
		// xf follows x
		if (xf != null) {
			val = contactvector[x][1][xf];
			if (!val || val == 0) val = 1; else val++; contactvector[x][1][xf] = val;
		}


		// 2nd symbol before and after
		/*
		if (i>1) xp = ciphertext.charAt(i-2);
		if (i<ciphertext.length-2) xf = ciphertext.charAt(i+2);
		if (xp != null) {
			val = contactvector[x][2][xp];
			if (!val || val == 0) val = 1; else val++; contactvector[x][2][xp] = val;
		}
		// xf follows x
		if (xf != null) {
			val = contactvector[x][3][xf];
			if (!val || val == 0) val = 1; else val++; contactvector[x][3][xf] = val;
		}
		*/
		
	}	
	
	sims = [];
	var a = alphabetGet();
	for (var i=0; i<a.length; i++) {
		var ch1 = a[i]; 
		for (var j=i+1; j<a.length; j++) {
			//if (j==i) continue;
			var ch2 = a[j];
			var key = ""+ch1+ch2;
			sims.push([key, cosineSimilarity(ch1, ch2)]);
		}
	}
	
	simssorted = sims.sort(function(a,b){return b[1]-a[1];});
	
}

// determine cosine similarity of two letters, based on contactvector
function cosineSimilarity(ch1, ch2) {
	var cv1 = contactvector[ch1];
	var cv2 = contactvector[ch2];
	if (cv1 == null || cv2 == null) return 0;
	var a = {};
	for (var ch in cv1[0]) a[ch] = true;
	for (var ch in cv1[1]) a[ch] = true;
	for (var ch in cv2[0]) a[ch] = true;
	for (var ch in cv2[1]) a[ch] = true;

	/*
	for (var ch in cv1[2]) a[ch] = true;
	for (var ch in cv1[3]) a[ch] = true;
	for (var ch in cv2[2]) a[ch] = true;
	for (var ch in cv2[3]) a[ch] = true;
	*/
	
	var x = [];
	var y = [];
	for (var ch in a) {
		if (!cv1[0][ch]) x.push(0);
		else x.push(cv1[0][ch]);
		if (!cv2[0][ch]) y.push(0);
		else y.push(cv2[0][ch]);
	}
	
	for (var ch in a) {
		if (!cv1[1][ch]) x.push(0);
		else x.push(cv1[1][ch]);
		if (!cv2[1][ch]) y.push(0);
		else y.push(cv2[1][ch]);
	}

	/*
	for (var ch in a) {
		if (!cv1[2][ch]) x.push(0);
		else x.push(cv1[2][ch]);
		if (!cv2[2][ch]) y.push(0);
		else y.push(cv2[2][ch]);
	}

	for (var ch in a) {
		if (!cv1[3][ch]) x.push(0);
		else x.push(cv1[3][ch]);
		if (!cv2[3][ch]) y.push(0);
		else y.push(cv2[3][ch]);
	}*/

	return cosineDistance(x, y);
}

// determine cosine distance between two vectors of symbol counts (note: this expects the vectors to be hashes of symbol counts)
function cosineDistance(x, y) {
	var dot = 0; // dot product
	var magX = 0; // magnitude of vector x
	var magY = 0; // magnitude of vector y
	for (var i=0; i<x.length; i++) {
		var xi = x[i];
		var yi = y[i];
		dot += xi*yi;
		magX += xi*xi;
		magY += yi*yi;
	}
	magX = Math.sqrt(magX);
	magY = Math.sqrt(magY);
	return dot/(magX*magY);
}
