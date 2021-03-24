//https://stackoverflow.com/questions/951021/what-is-the-javascript-version-of-sleep
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function demo() {
  console.log('Taking a break...');
  await sleep(2000);
  console.log('Two second later');
}

// visualization of periodic untransposition
async function animatePeriod(n, delay) {
	darkenAll();
	await sleep(1000);
	makeTargetTable();
	var count = 0;   
	var c = cipher[which].join("");
	var prev = -1;
	var prev2 = -1;
	
	for (var i=0; i<n; i++) {
		for (var j=i; j<c.length; j+=n) {
			//var delay2 = 0;
			//if (count < c.length/3) delay2 = delay*(c.length/3-count)/(c.length/3);
			rgb(j, 100, 255, 100);
			if (prev > -1) {
				lightenpos(prev);
				rgbClear(prev);
			}
			writeCell(count, prev2, c.charAt(j));
			prev = j;
			prev2 = count;
			count++;            
			//console.log(delay2);
			await sleep(delay);
			delay /= 1.04;
			if (delay < 15) delay = 15;
		}
	}
	lightenpos(prev);
	rgbClear(prev);
	rgb2Clear(prev2);
}

function makeTargetTable() {
	var e = document.getElementById("results");
	var html = "<table class=\"cipher\">";
	for (var row=0; row<HEIGHT; row++) {
		html += "<tr>";
		for (var col=0; col<WIDTH; col++) {
			html += "<td id=\"a" + row + "_" + col + "\"></td>";
		}
		html += "</tr>";
	}
	html += "</table>";
	e.innerHTML = html;
}
function makeTargetTable2() {
	var e = document.getElementById("results");
	var html = "<table class=\"cipher\">";
	for (var row=0; row<HEIGHT; row++) {
		html += "<tr>";
		for (var col=0; col<7; col++) {
			html += "<td id=\"a" + row + "_" + col + "\"></td>";
		}
		html += "</tr>";
	}
	html += "</table>";
	e.innerHTML = html;
}

function writeCell(pos, prev, s) {
	var row = parseInt(pos / WIDTH);
	var col = pos % WIDTH;
	var e = document.getElementById("a" + row + "_" + col);
    e.innerHTML = getImgDarker(s);
	rgb2(pos, 100, 255, 100);
	if (prev > -1) {
		rgb2Clear(prev);
	}

}
function writeCell2(pos, prev, s) {
	var row = parseInt(pos / 7);
	var col = pos % 7;
	console.log("pos " + pos + " row " + row + " col " + col);
	var e = document.getElementById("a" + row + "_" + col);
    e.innerHTML = getImgDarker(s);
	rgb3(pos, 100, 255, 100);
	if (prev > -1) {
		rgb3Clear(prev);
	}

}

function rgb2(pos, r, g, b) {
	var H=cipher[which].length;
	var W=cipher[which][0].length;
	var row = parseInt(pos/W);
	var col = pos%W;

	var elem = document.getElementById("a"+row+"_"+col);
	if (elem) {
			elem.style.backgroundColor="rgb("+r+","+g+","+b+")";
			elem.childNodes[0].style.opacity="0.25";
			//elem.style.paddingBottom="2px";
	}
}
function rgb3(pos, r, g, b) {
	var H=cipher[which].length;
	var W=7;
	var row = parseInt(pos/W);
	var col = pos%W;

	var elem = document.getElementById("a"+row+"_"+col);
	if (elem) {
			elem.style.backgroundColor="rgb("+r+","+g+","+b+")";
			elem.childNodes[0].style.opacity="0.25";
			//elem.style.paddingBottom="2px";
	}
}
function rgb2Clear(pos) {
	var H=cipher[which].length;
	var W=17;
	var row = parseInt(pos/W);
	var col = pos%W;
	
	var elem = document.getElementById("a"+row+"_"+col);
	if (elem) {
			elem.style.backgroundColor="";
			elem.childNodes[0].style.opacity="";
			//elem.style.paddingBottom="2px";
	}
}
function rgb3Clear(pos) {
	var H=cipher[which].length;
	var W=7;
	var row = parseInt(pos/W);
	var col = pos%W;
	
	var elem = document.getElementById("a"+row+"_"+col);
	if (elem) {
			elem.style.backgroundColor="";
			elem.childNodes[0].style.opacity="";
			//elem.style.paddingBottom="2px";
	}
}

// visualization of ngrams
async function animateNgrams(n, delay) {
	var c = cipher[which].join("");
	
	var map = ngrams(c, n);
	var positions = ngramRepeatPositions(c, map);
	var psize = Object.keys(positions).length
	
	await sleep(2000);
	
	var coverage = {};
	                       
	var keycount = 0;
	for (var key in positions) {
		var delay2 = 100;
		if (keycount < psize/2) delay2 = 100+delay*(psize/2-keycount)/psize/2;
		keycount++;
		for (var k=0; k<positions[key].length; k++) {
			var pos = positions[key][k];
			for (var i=0; i<n; i++) {
				rgbClear(pos+i);
				darkenpos2(pos+i);
				coverage[pos+i] = true;
			}
		}
		var e = document.getElementById("results");
		var html = "<div class=\"coverage\">";  
		var size = Object.keys(coverage).length;
		var percent = 100*size/c.length;        
		percent = Math.round(100*percent)/100;
		html += "Coverage: <br><b>" + percent + "%</b>";
		html += "</div>";
		e.innerHTML = html;
		await sleep(delay2);
		
		
		for (var k=0; k<positions[key].length; k++) {
			var pos = positions[key][k];
			for (var i=0; i<n; i++) {
				rgb(pos+i, 100, 255, 100);
			}
		}
		
	}

}
 
// for each position p, measure length of longest sequence starting at p and containing no repeated symbols
function animateMaxNonrepeatUnigrams() {
	var c = cipher[which].join("");
	var counts = {};
	var sum = 0;
	var total = 0;
   	for (var i=0; i<c.length; i++) {
		var seen = {};
		var len = 0;
		for (var j=i; j<c.length; j++) {
			var ch = c[j];
			if (seen[ch]) break;
			seen[ch] = true;
			len++;
		}
		console.log(i+" " + j + " " + len);
		if (!counts[len]) counts[len] = 0;
		counts[len] = counts[len]+1;
		sum += len;
		total++;
	}
	console.log("Sum " + sum + " total " + total + " avg " + (sum/total));
	return counts;
}

// animate a route.  input is a list of positions.
// garlick cipher:  186-
function animateRoute(route) {
}

function isE(c) {
	var homs = "ZpW+6NE";
	for (var i=0; i<homs.length; i++) if (homs[i] == c) return true;
	return false;
}    
// visualization of Z408's homophones for E
async function animateCycles(delay) {
	await sleep(2000);
	makeTargetTable2();
	var count = 0;   
	var c = cipher[which].join("");
	var prev = -1;
	var prev2 = -1;
 
   	for (var pos=0; pos<c.length; pos++) {
		if (pos == 107 || pos == 232) continue;
		if (isE(c[pos])) {
			rgb(pos, 100, 255, 100);
			if (prev > -1) {
				darkenpos2(prev);
				rgbClear(prev);
			}
			writeCell2(count, prev2, c[pos]);
			prev = pos;
			prev2 = count;
			count++;            
			await sleep(delay);
    	}
	}
	lightenpos(prev);
	rgbClear(prev);
	rgb3Clear(prev2);
	
}