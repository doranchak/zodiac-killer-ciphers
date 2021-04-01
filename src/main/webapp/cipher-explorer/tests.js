// http://zodiackillerciphers.com/wiki/index.php?title=Pivots
function symmetryPairs(row) {
	counts = [];
	rc = [];
	var offset = 1;
	while (true) {
		var r1 = row-offset;
		var r2 = row+offset;
		if (r1 < 0 || r2 >= cipher[which].length) {
			break;
		}
		for (var col=0; col<cipher[which][r1].length; col++) {
			var key = ""+cipher[which][r1][col]+cipher[which][r2][col];
//			console.log(key);
			key = key.split("").sort().join("");
			if (!counts[key]) counts[key] = 0;
			counts[key]++;
			if (!rc[key]) rc[key] = [];
			rc[key].push(r1);
			rc[key].push(col);
			rc[key].push(r2);
			rc[key].push(col);
		}
		offset++;
	}             
	
	var H = cipher[which].length;
	var W = cipher[which][0].length;
	var count = 0; 
	var a = [];
	for (var key in counts)
		if (counts[key] > 1) {
			count++;
			//console.log(key + ": " + counts[key]);
			var p = [];
			for (var i=0; i<rc[key].length; i+=2) {
				p.push(rc[key][i]*W+rc[key][i+1]);
				darkenrc2(rc[key][i],rc[key][i+1]);
			}
			randcolor(p);
		}
	return count;
}

// aka "repeated structure pattern"
function offsetRepeats(drow, dcol, allowWrap) {
	var result = 0;
	var H = cipher[which].length;
	var W = cipher[which][0].length
	var p1 = [];
	var p2 = [];
	for (var r1=0; r1<H; r1++) {
		for (var c1=0; c1<W; c1++) {
			var r2 = (r1 + drow + H) % H;
			var c2 = (c1 + dcol + W) % W;
			
			var r2 = r1 + drow;
			var c2 = c1 + dcol;
			if (!allowWrap) {
				if (r2 < 0 || r2 >= H) continue;
				if (c2 < 0 || c2 >= W) continue;
			} else {
				r2 = (r2 + H) % H;
				c2 = (c2 + W) % W;
			}
			
			if (cipher[which][r1][c1] == cipher[which][r2][c2]) {
				p1.push(r1*W+c1);
				p2.push(r2*W+c2);
				result++;
			}
		}
	}    
	randcolor(p1);
	randcolor(p2);
	return result;
}

String.prototype.replaceAt=function(index, character) {
    return this.substr(0, index) + character + this.substr(index+character.length);
}

/** randomly scramble the given text, using http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle */
function shuffle(cipher) {
	var result = shuffleLine(cipher.join(""));
	var a = [];
	var H = cipher.length;
	var W = cipher[0].length;
	for (var row = 0; row < H; row++) a.push(result.substring(row*W, row*W+W));
	return a;
}
function shuffleLine(cipher) {
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
	
