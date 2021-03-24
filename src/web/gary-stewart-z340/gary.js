var translationsEVB = [];

translationsEVB['A'] = "A879^";
translationsEVB['B'] = "Bb";
translationsEVB['C'] = "Cc";
translationsEVB['D'] = "Dd";
translationsEVB['E'] = "Ee12";
translationsEVB['F'] = "Ff";
translationsEVB['G'] = "G";
translationsEVB['H'] = "H";
translationsEVB['I'] = "I!:;|/\\-";
translationsEVB['J'] = "Jj";
translationsEVB['K'] = "Kk=";
translationsEVB['L'] = "Ll|/\\";
translationsEVB['M'] = "M";
translationsEVB['N'] = "N?^";
translationsEVB['O'] = "O5601234()z#%*@_.";
translationsEVB['P'] = "P&p";
translationsEVB['Q'] = "Qqp";
translationsEVB['R'] = "Rr";
translationsEVB['S'] = "S";
translationsEVB['T'] = "T+[tj";
translationsEVB['U'] = "UV";
translationsEVB['V'] = "V";
translationsEVB['W'] = "W";
translationsEVB['X'] = "X";
translationsEVB['Y'] = "Yy";
translationsEVB['Z'] = "Zz";

var translationsEVBreverse = [];
for (x in translationsEVB) {
	var val = translationsEVB[x];
	for (var i=0; i<val.length; i++) {
		translationsEVBreverse[val[i]] = x;
	}
}

// get entire row or column of cipher text
function getRowOrColumn(index, row) {
	if (row) return cipher[which][index];
	var result = "";
	for (var row=0; row<cipher[which].length; row++) {
		result += cipher[which][row][index];
	}
	return result;
}

// return [row,col] of first cipher symbol encountered along the indicated row (column) that can be interpreted
// as the given plaintext letter.
function findTranslation(byCol, index, p) {
	var found = false;
	var ROWS = cipher[which].length;
	var COLS = cipher[which][0].length;
	var vals = translationsEVB[p];

	var current = 0;
	var end = byCol ? ROWS : COLS;
	
	var bestTranslation = 1000;
	var bestTranslationRc;
	
	while (true) {
		var col = byCol ? index : current;
		var row = byCol ? current : index;
		var c = cipher[which][row][col];
		//console.log("findTranslation index " + index + " current " + current + " end " + end + " row " + row + " col " + col + " c " + c + " vals " + vals);
		var vi = vals.indexOf(c);
		if (vi > -1) {
			if (vi < bestTranslation) {
				bestTranslation = vi;
				bestTranslationRc = [row, col];
			}
		}
		current++;
		if (current == end) break;
	}
	return bestTranslationRc;
}

// search all directions for the given text.
// if match found, returns array otherwise null.
// array contains list of positions that form the match.
// end of array has three elements:
// 1) Reading direction of match
// 2) Ciphertext that matches
// 3) Corresponding plaintext

function evbFindAll(text, name) {
	reset();
	var result;
	result = evbFind(text, false, false);
	if (!result) result = evbFind(text, false, true);
	if (!result) result = evbFind(text, true, false);
	if (!result) result = evbFind(text, true, true);
	if (result != null) 
		evbMark(result, name);
	return result;
}

function evbMark(result, name) {
	currentFG = "#fff";
	currentBG = "#900";
	for (var i = 0; i<result.length-3; i++) {
		darkenrc2(result[i][0], result[i][1]);
	}
	var html = "";
	var direction = result[result.length-3];
	var ciphertext = result[result.length-2];
	var plaintext = result[result.length-1];
	var withspaces = "";
	for (var i=0; i<name.length; i++) withspaces += name[i] + " ";
	html += "<div class='cipher'>" + ciphertext + "</div>";
	html += "<div class='p'>" + plaintext + "</div>";
	html += "<div class='p'>" + withspaces + "</div>";
	html += "<div class='direction'>" + direction + "</div>";
	document.getElementById("results").innerHTML = html;
}

// search for text.
// if byCol, then check for matches in each column.  otherwise, check in each row.
// if reverse, then start at the last row (or column if byCol is true).  otherwise, start at the first row (or column).
function evbFind(text, byCol, reverse) {
	
	var ROWS = cipher[which].length;
	var COLS = cipher[which][0].length;

	var offsetMax = 0;
	if (byCol && text.length < COLS)
		offsetMax = COLS - text.length;
	else if (text.length < ROWS)
		offsetMax = ROWS - text.length;
	
	var offset = 0;
	while (true) {
		var result = [];
		var current = 0;
		var end = 0;
		var increment = reverse ? -1 : 1;
		if (!byCol && !reverse) {
			current = offset;
			end = ROWS-1;
	    } else if (!byCol && reverse) {
			current = ROWS-1 - offset;
			end = 0;
	    } else if (byCol && !reverse) {
			current = offset;
			end = COLS-1;
	    } else if (byCol && reverse) {
			current = COLS-1-offset;
			end = 0;
		}
//		console.log("text " + text + " byCol " + byCol + " reverse " + reverse + " starting wwith current " + current + " end " + end + " inc " + increment + " offset " + offset + " offsetmax " + offsetMax);
		var found = "";
		var foundCipher = "";
		var textIndex = 0;
		while (true) {
			var p = text[textIndex];
			var rc = findTranslation(byCol, current, text[textIndex]);
			//console.log("p " + p + " current " + current + " found " + found + " rc " + rc);
			if (rc) {
				found += p;
				foundCipher += cipher[which][rc[0]][rc[1]];
				//darkenrc2(rc[0], rc[1]);
				result[result.length] = [rc[0], rc[1]];
			} else {
//				console.log(" - break: no match");
				break;
			}
			if (found.length == text.length) {
				result[result.length] = directionFor(byCol, reverse);
				result[result.length] = foundCipher;
				result[result.length] = found;
				return result;
			}
			if (current == end) {
//				console.log(" - break: reached end " + end);
				break;
			}
			current += increment;
			textIndex++;
			if (textIndex > text.length) {
//					console.log(" - break: past text");
					break;
			}
		}
		offset++;
		if (offset > offsetMax) {
			//console.log(" - break: offset " + offset + " " + offsetMax);
			break;
		}
	}
	return null;
}

function directionFor(byCol, reverse) {
	if (!byCol && !reverse) {
		return "Top to bottom";
    } else if (!byCol && reverse) {
		return "Bottom to top";
    } else if (byCol && !reverse) {
		return "Left to right";
    } else {
		return "Right to left";
	}		
}

function flatten(name) {
	var flattened = "";
	for (var i=0; i<name.length; i++) flattened += name[i];
	return flattened;
}

// generate random names until we find a match
function evbRandomName() {
	while (true) {
		var vertical = randBool();
		var length = 12 + Math.floor(Math.random()*9);
		var name = randomName(length);
		var result = evbFindAll(flatten(name), name);
		if (result) {
			console.log(name);
			document.getElementById("debug").innerHTML += name + "<br>";
			return result;
		}
	}
}
var shuffleCount = 0;
// shuffle the cipher until earl can be found
function shuffleEarl() {
	earls = [
		["EARL","VAN","BEST","JR"],
		["EARL","V","BEST","JR"],
		["EARL","VAN","BEST","JUNIOR"],
		["EARL","VAN","BEST","JR"]
	];
	earls = [
		["EARL","VAN","BEST","JUNIOR"]
	];
	var result = null;
	var name = null;
	shuffleCount = 0;
	while (true) {
		cipher[which] = shuffle(cipher[which]);
		ciphers[which] = cipher[which].join("");
		shuffleCount++;
		var found = false;
		for (var i=0; i<earls.length; i++) {
			name = earls[i];
			result = evbFindAll(flatten(name), name);
			if (result) {
				found = true;
				break;
			}
		}
		if (found) break;
	}
	init();
	evbFindAll(flatten(name), name);
	document.getElementById("results").innerHTML += "<br>Found after " + shuffleCount + " shuffles.";
}