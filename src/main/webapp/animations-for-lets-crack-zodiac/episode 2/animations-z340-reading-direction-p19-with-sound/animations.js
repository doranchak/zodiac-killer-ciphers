var ciphers = new Array(
//z408		
"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
//z408 solution
"ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGERTUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI",
//z340
"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+",
//z340 p19
"H+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(c"
)

var cipherblock;
var cipherblock2;
var startx;
var starty;
var sizex;
var sizey;

/** render a cipher block, starting at the given screen position.  each cipher unit is rendered with the given size.
    example:  drawCipher(ciphers[2], 17, 25, 25, 10, 10, 2, 2, 'cipher', 'black')
*/
function drawCipher(cipher, width, szx, szy, sx, sy, gapx, gapy, id, color, drawCounter, reps, avg, count) {
	startx = sx;
	starty = sy;
	sizex = szx;
	sizey = szy;
	var cols = width;
	var rows = cipher.length/width;
	var html = ""; var html2 = "";
	var rc = "";
	var z = 0;
	for (var row=0; row<rows; row++) {
		for (var col=0; col<cols; col++) {
			rc = row+"_"+col;
			rc2 = "x"+row+"_"+col;
			z--;
			var left = (col+1)*gapx + col*sizex + startx; // gaps + symbols + starting offset
			var top = (row+1)*gapy + row*sizey + starty; // gaps + symbols + starting offset
			html += "<span id=\"" + rc + "\" class=\"c\" style=\"color: " + color + "; position: absolute; left: " + left + "px; top: " + top + "px; font-size: " + (sizex+10) + "px; padding: 1px 3px 1px 2px; z-index: " + z + "\">" + cipher[row*width+col] + "</span>";
			html2 += "<span id=\"x" + rc + "\" class=\"c\" style=\"color: " + color + "; position: absolute; left: " + (left+22*sizex) + "px; top: " + top + "px; font-size: " + (sizex+10) + "px; padding: 1px 3px 1px 2px; z-index: " + z + "\">&nbsp;</span>";
		}
	}
	document.getElementById(id).innerHTML = html;
	document.getElementById("cipher2").innerHTML = html2;
	// get all the HTML elements into a 2D array for easier manipulation
	cipherblock = [];
	cipherblock2 = [];
	for (var row=0; row<rows; row++) {
		cipherblock[row] = [];
		cipherblock2[row] = [];
		for (var col=0; col<cols; col++) {
			rc = row+"_"+col;
			rc2 = "x"+row+"_"+col;
			cipherblock[row][col] = document.getElementById(rc);
			cipherblock2[row][col] = document.getElementById(rc2);
		}
	}
	var counts = document.getElementById("counter");
	if (drawCounter) {
		counts.innerHTML = "REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + reps + "</span><br>";
		counts.innerHTML += "AVERAGE REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + avg.toFixed(2) + "</span><br>";
		counts.innerHTML += "TOTAL TEST CIPHERS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + numberWithCommas(count)  + "</span>"
		counts.style.fontFamily = "Work Sans";
		counts.style.position = "absolute";
		counts.style.top = "40px";
		counts.style.left = "550px";
		counts.style.fontSize = "40px";
		counts.style.color = "#6aa84f";
	}  else counts.innerHTML = "";
	/*
	counts.innerHTML = "TOTAL REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + 0 + "</span><br>";
	counts.style.fontFamily = "Work Sans";
	counts.style.position = "absolute";
	counts.style.top = "300px";
	counts.style.left = "750px";
	counts.style.fontSize = "40px";
	counts.style.color = "#6aa84f";*/
	
}

/** render a cipher block by putting all symbols in a pile, then animating moving them into position 
    example: animatePile(ciphers[2], 17, 25, 25, 200, 200, 10, 10, 2, 2, 'cipher', 200,1)
*/
function animatePile(cipher, width, sizex, sizey, startx, starty, endx, endy, gapx, gapy, id, steps, ms) {
	var cols = width;
	var rows = cipher.length/width;
	var html = "";
	var rc = "";
	var z = 0;
	
	var pos = [];
	var delta = [];
	var stepsArr = [];
	var stepsMax = 0;
	
	for (var row=0; row<rows; row++) {
		pos[row] = [];
		delta[row] = [];
		stepsArr[row] = [];
		for (var col=0; col<cols; col++) {
			rc = row+"_"+col;
			z--;
			var left = startx;
			var top = starty;
			pos[row][col] = new Array(top, left);
			stepsArr[row][col] = steps + 25*(1-2*Math.random());
			stepsMax = Math.max(stepsMax, stepsArr[row][col]);
			
			var endyRelative = (row+1)*gapy + row*sizey + endy;
			var endxRelative = (col+1)*gapx + col*sizex + endx;
			
			var dx = (endxRelative-startx)/stepsArr[row][col];
//			console.log(endxRelative + "," + startx + "," + steps + ":" + dx);
			
			var dy = (endyRelative-starty)/stepsArr[row][col];
			delta[row][col] = new Array(dy, dx);
			html += "<span id=\"" + rc + "\" class=\"c\" style=\"position: absolute; left: " + left + "px; top: " + top + "px; font-size: " + (sizex+10) + "px; z-index: " + z + "; color: white;\">" + cipher[row*width+col] + "</span>";
		}
	}
	console.log(pos); 
	console.log(delta);
	console.log(stepsArr);
	document.getElementById(id).innerHTML = html;
	
  var id = setInterval(frame, ms);
  var count = 0;
  function frame() {
		var color = parseInt(255 - count/stepsMax * 255);
//		console.log("color " + color);
		for (var row=0; row<rows; row++) {
			for (var col=0; col<cols; col++) {
				rc = row+"_"+col;
				var e = document.getElementById(rc);
				var top = parseFloat(e.style.top);
				var left = parseFloat(e.style.left);
				e.style.top = top + delta[row][col][0];
				e.style.left = left + delta[row][col][1];
				var rgb = "rgb(" + color + ", " + color + ", " + color + ")";
				e.style.color = rgb;
//				console.log(row+"_"+col + ": " + rgb);
			}
		}
		count++;
		if (count == stepsMax) clearInterval(id);
//		console.log(count);
	}
}

// randomly fade in cipher
function fadeIn(ms) {
	// array of random steps
	steps = [];
	var rows = cipherblock.length;
	var cols = cipherblock[0].length;
	for (var row=0; row<rows; row++) {
		steps[row] = [];
		for (var col=0; col<cols; col++) {
			steps[row][col] = Math.max(Math.random()/20, 0.02);
			cipherblock[row][col].style.opacity = 0;
		}
	}
	
	var total = cipherblock.length * cipherblock[0].length;
	var completed = 0;
	
	var id = setInterval(frame, ms);
	var count = 0;
	var opacity;
	function frame() {
		for (var row=0; row<rows; row++) {
			for (var col=0; col<cols; col++) {
				opacity = parseFloat(cipherblock[row][col].style.opacity);
				if (opacity >= 1) continue; // already finished this one
				opacity += steps[row][col];
				if (opacity >= 1) {
					opacity = 1;
					completed++;
				}
				cipherblock[row][col].style.opacity = opacity;
			}
		}
		if (completed == total) {
			clearInterval(id);
		}
	}
}

// randomly fade in cipher, with blur too
function fadeInWithBlur(ms, startBlur) {
	// array of random steps
	steps = [];
	var rows = cipherblock.length;
	var cols = cipherblock[0].length;
	for (var row=0; row<rows; row++) {
		steps[row] = [];
		for (var col=0; col<cols; col++) {
			steps[row][col] = Math.max(Math.random()/30, 0.01);
			cipherblock[row][col].style.opacity = 0;
			if (row<50){
				cipherblock[row][col].style.color = "transparent";
				cipherblock[row][col].style.textShadow = "0 0 " + startBlur + "px black";
			}
		}
	}
	var total = cipherblock.length * cipherblock[0].length;
	var completed = 0;
	console.log("got here");
	var id = setInterval(frame, ms);
	var count = 0;
	var opacity; var blur;
	function frame() {
		for (var row=0; row<rows; row++) {
			for (var col=0; col<cols; col++) {
				console.log(row+","+col);
				opacity = parseFloat(cipherblock[row][col].style.opacity);
				if (opacity >= 1) continue; // already finished this one
				opacity += steps[row][col];
				if (opacity >= 1) {
					opacity = 1;
					completed++;
				}
				cipherblock[row][col].style.opacity = opacity;
				// blur radius is in range [0,startBlur]
				if (row<50){
					cipherblock[row][col].style.textShadow = "0 0 " + (startBlur-opacity*startBlur) + "px black";
//					console.log(row+","+col+","+cipherblock[row][col].style.textShadow);
				}
			}
		}
		if (completed == total) {
			clearInterval(id);
			document.getElementById("b2").innerHTML = "Done";
		}
	}
}

// randomly fade out cipher in random directions
// startx, starty: upper left corner of cipher block.
function fadeOutRandom(ms) {
	hideButtons();
	// array of random steps (delta y, delta x)
	steps = [];
	positions = [];
	blurs = [];
	sizes = [];
	var rows = cipherblock.length;
	var cols = cipherblock[0].length;
	var slope;
	var centerx = cols*sizex/2 + startx;
	var centery = rows*sizey/2 + starty;
	var d;
	var dx, dy;
	for (var row=0; row<rows; row++) {
		steps[row] = [];
		positions[row] = [];
		blurs[row] = [];
		sizes[row] = [];
		for (var col=0; col<cols; col++) {
			x = parseInt(cipherblock[row][col].style.left);
			y = parseInt(cipherblock[row][col].style.top);
			if (centerx-x == 0) slope = 1;
			else slope = (centery-y)/(centerx-x);
			dx = 0;
			dy = 0;
			while (Math.abs(dx+dy) < 1) {
				dx = parseInt(Math.random()*9)-4;
				dy = parseInt(Math.random()*9)-4;
			}
			steps[row][col] = [dy, dx];
			positions[row][col] = [y, x];
			blurs[row][col] = 0;
//			sizes[row][col] = 1+parseInt(Math.random()*7);
			
			cipherblock[row][col].style.color = "transparent";
			cipherblock[row][col].style.textShadow = "0 0 0px black";
		}
	}
	console.log("got here");
	var id = setInterval(frame, ms);
	var count = 0;
	var opacity; var blur;
	var factor1 = 0.25;
	var factor2 = 1.11;
	var color = 0;
	function frame() {
		for (var row=0; row<rows; row++) {
			for (var col=0; col<cols; col++) {
				var y = positions[row][col][0];
				var x = positions[row][col][1];
				dy = steps[row][col][0]*factor1;
				dx = steps[row][col][1]*factor1;
				y += dy;
				x += dx;
				
//				blurs[row][col] = Math.min(30, blurs[row][col]+Math.random()/2);
				blurs[row][col] = Math.min(50, blurs[row][col]+0.75);
				//sizes[row][col]+=Math.random()*4;
				
//				console.log(y+","+x+","+row+","+col+","+steps[row][col]+","+factor1+","+factor2);
				cipherblock[row][col].style.top = y + "px";
				cipherblock[row][col].style.left = x + "px";
				positions[row][col] = [y, x];
				cipherblock[row][col].style.textShadow = "0 0 "+blurs[row][col]+"px rgb("+color+","+color+","+color+")";
//				cipherblock[row][col].style.fontSize = (parseInt(cipherblock[row][col].style.fontSize) + sizes[row][col])+"px";
			}
		}
		if (factor1 >= 1.0) {
			factor1 = 1.0;
		} else {
			factor1 *= factor2;
		}
		color+=1.25;
	}
}
function hideButtons() {
	document.getElementById("buttons").style.display="none";
}
function showButtons() {
	document.getElementById("buttons").style.display="";
}

function fadeOutRandomButton(ms) {
	hideButtons();
	setTimeout(function() {fadeOutRandom(ms)}, 1000);
}

function decodeAnimationButton(ms) {
	hideButtons();
	setTimeout(function() {decodeAnimation(ms)}, 1000);
}

function decodeAnimation(ms) {
	var state = []; // state[row][col] = [isDecoded (0,1), fontSize, blurSize]
	var rows = cipherblock.length;
	var cols = cipherblock[0].length;
	for (var row=0; row<24; row++) {
		state[row] = [];
		for (var col=0; col<17; col++) {
			state[row][col] = [0, 40, 30];
		}
	}
	var id = setInterval(frame, ms);
	var total = 0;
	function frame() {
		var count = 0;
		var s;
		for (var row=0; row<rows; row++) {
			for (var col=0; col<cols; col++) {
				s = state[row][col];
				if (!s[0] && !count) {
					decodeCell(row, col, s);
					count++;
//					console.log(count+","+s);
				} else if (s[0]){
					decodeCell(row, col, s);
					if (s[1] > 31) s[1]--;
					if (s[2] > 0) s[2]--;
//					console.log(count+","+s);
				}
			}
		}
		total++;
//		clearInterval(id);
//		if (ms > 10) ms = ms - 1;
//		id = setInterval(frame, ms)
	}

}

function decodeCell(row, col, s) {
	var pt = ciphers[1][row*17+col];
//	var html = "<span style=\"position: relative; top: -7px; color: white; background-color: black; font-size: 31px; font-weight: bold; font-family: 'courier new', monospace;\">" + pt + "</span>";
//	cipherblock[row][col].innerHTML = html;
	var e = cipherblock[row][col];
	e.style.fontFamily="courier new";
	e.style.fontWeight="bold";
	e.innerHTML = pt;
	e.style.fontSize = s[1] + "px";
//	e.style.color = "#090";
	if (!s[0]) {
		var top = parseInt(e.style.top);
		e.style.top = (top - 3) + "px";
		s[0] = 1;
	}
	cipherblock[row][col].style.color = "transparent";
	cipherblock[row][col].style.textShadow = "0 0 " + s[2] + "px #090";
	
//	e.style.position = "relative";
//	e.style.top = "-7px";
//	e.style.border = "thin solid black";
//	e.style.padding = "0 0 0 0px";
}

quote_block = [];
var quote = [
"ONE OF THE MOST SINGULAR CHARACTERISTICS",
"OF THE ART OF DECIPHERING IS THE STRONG",
"CONVICTION POSSESSED BY EVERY PERSON,",
"EVEN MODERATELY ACQUAINTED WITH IT, THAT",
"HE IS ABLE TO CONSTRUCT A CIPHER WHICH",
"NOBODY ELSE CAN DECIPHER. I HAVE ALSO",
"OBSERVED THAT THE CLEVERER THE PERSON,",
"THE MORE INTIMATE IS HIS CONVICTION."];
var quoteLine = "ONEOFTHEMOSTSINGULARCHARACTERISTICSOFTHEARTOFDECIPHERINGISTHESTRONGCONVICTIONPOSSESSEDBYEVERYPERSONEVENMODERATELYACQUAINTEDWITHITTHATHEISABLETOCONSTRUCTACIPHERWHICHNOBODYELSECANDECIPHERIHAVEALSOOBSERVEDTHATTHECLEVERERTHEPERSONTHEMOREINTIMATEISHISCONVICTION";
var quoteLine2 = "";
for (var i=0; i<quote.length; i++) quoteLine2 += quote[i];
var quoteShuffled = "O PIET SRO OIVHTEO CONSONRW TUROTISA C A INCET  NTNORLSTEWIHATE SO ES NIMHLS CLADUM SERNHBREAS .VE HEEMIQTHH LH  DCCSVTAACEMDEA E AHINBCEVRUEDCS CPIDO ENE RFTSOTTIOINSITO. NRV EYHAOPOIPTEIYEGTT  CDYLTHTTHIFIECNBTSC H PH IEEPO,IAORRAGI H RETRNHCHAALRT ESE ,ONRDG VC AO EETTI RH ISSEEBFN YESN IS VEERCETOE,O";
var quoteShuffledLine = "OPIETSROOIVHTEOCONSONRWTUROTISACAINCETNTNORLSTEWIHATESOESNIMHLSCLADUMSERNHBREASVEHEEMIQTHHLHDCCSVTAACEMDEAEAHINBCEVRUEDCSCPIDOENERFTSOTTIOINSITONRVEYHAOPOIPTEIYEGTTCDYLTHTTHIFIECNBTSCHPHIEEPOIAORRAGIHRETRNHCHAALRTESEONRDGVCAOEETTIRHISSEEBFNYESNISVEERCETOEO";

//"One of the most singular characteristics",
//"of the art of deciphering is the strong",
//"conviction possessed by every person,",
//"even moderately acquainted with it, that",
//"he is able to construct a cipher which",
//"nobody else can decipher. I have also",
//"observed that the cleverer the person,",
//"the more intimate is his conviction."];

function drawQuote(drawCounter, reps, avg, count) {
	var startX = 50;
	var startY = 40;
	var cellSizeX = 21;
	var cellSizeY = 31;
	var fontSize = 30;

	var html = "";
	for (var row=0; row<quote.length; row++) {
		for (var col=0; col<quote[row].length; col++) {
			var y = startY+row*cellSizeY;
			var x = startX+col*cellSizeX;
			var id = "q"+row+"_"+col;
			var q = quote[row][col];
			if (q == " ") q = "&nbsp;"
			html += "<span id=\"" + id + "\" style='position: absolute; left: " + x + "px; top: " + y + "px; font-family: courier; font-weight: bold; font-size: " + fontSize + "px; padding: 3px 2px 4px 2px; line-height: 0.8'>" + q + "</span>";
		}
	}
	document.getElementById("cipher").innerHTML = html;
	for (var row=0; row<quote.length; row++) {
		quote_block[row] = [];
		for (var col=0; col<quote[row].length; col++) {
			var id = "q"+row+"_"+col;
			quote_block[row][col] = document.getElementById(id);
		}
	}
	var counts = document.getElementById("counter");
	if (drawCounter) {
		counts.innerHTML = "REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + reps + "</span><br>";
		counts.innerHTML += "AVERAGE REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + avg.toFixed(2) + "</span><br>";
		counts.innerHTML += "TOTAL SHUFFLES: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + numberWithCommas(count)  + "</span>"
		counts.style.fontFamily = "Work Sans";
		counts.style.position = "absolute";
		counts.style.top = "300px";
		counts.style.left = "50px";
		counts.style.fontSize = "40px";
		counts.style.color = "#6aa84f";
	}  else counts.innerHTML = "";
}

function drawGraph(x, y) {
//	var x = [];
//	for (var i = 0; i < 500; i ++) {
//		x[i] = Math.random();
//	}

	var trace = {
	    x: x,
		y: y,
	    type: 'bar',
	  };
	var data = [trace];
	Plotly.newPlot('myDiv', data);
}

function qHighlight(index, color) {
	var e = quote_block[index[0]][index[1]];
	e.style.backgroundColor=color;
}
function qClearAll() {
	for (var row=0; row<quote_block.length; row++) {
		for (var col=0; col<quote_block[row].length; col++) {
			qHighlight([row, col], "white");
		}
	}
}

function qCountBigrams(bigram, ms) {
	var id = setInterval(frame, ms);
	index = [0,0];
	var reps = 0;
	function frame() {
		var next = nextBigram(bigram, index);
//		console.log("next " + next);
		if (next == null) {
			clearInterval(id);
		} else {
			var color = reps == 0 ? "#f90" : "#0ff";
			qHighlight(next[0], color);
			qHighlight(next[1], color);
			var counts = document.getElementById("counter");
			counts.innerHTML = "REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + reps + "</span>";
			reps++;
		}
	}
}

function qCountBigramsAll(ms) {
	var allCounts = [];
	for (var i=0; i<quoteLine.length-1; i++) {
		var bigram = quoteLine.substring(i,i+2);
		if (allCounts[bigram]) allCounts[bigram]++;
		else allCounts[bigram] = 1;
	}
	var bigramQueue = [];
	for (var key in allCounts) {
		if (allCounts[key] > 1) {
			bigramQueue[bigramQueue.length] = key;
			console.log(key + " " + (allCounts[key]-1));
		}
	}
	console.log(bigramQueue);
	var index = 0;
	var id = setTimeout(frame, ms);
	var totalReps = 0;
	function frame() {
		if (index == bigramQueue.length) {
			qClearAll();
			//var counts = document.getElementById("bigram");
			//counts.innerHTML = "";
			clearInterval(id);
		} else {
			// clear all highlights
			qClearAll();
			// find and highlight all occurrences of the bigram 
			var bg = bigramQueue[index++];
			var bigrams = allBigrams(bg);
			if (bigrams != null) {
				totalReps += bigrams.length-1;
				for (var i=0; i<bigrams.length; i++) {
					var color = i == 0 ? "#f90" : "#0ff";
					qHighlight(bigrams[i][0], color);
					qHighlight(bigrams[i][1], color);
				}
			}
			
			var counts = document.getElementById("counter");
			counts.innerHTML = "TOTAL REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + totalReps + "</span>";
			counts.style.fontFamily = "Work Sans";
			counts.style.position = "absolute";
			counts.style.top = "370px";
			counts.style.left = "360px";
			counts.style.fontSize = "40px";
			counts.style.color = "#6aa84f";
			
			var counts = document.getElementById("bigram");
			counts.innerHTML = "BIGRAM: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + bg + "</span>";
			counts.style.fontFamily = "Work Sans";
			counts.style.position = "absolute";
			counts.style.top = "370px";
			counts.style.left = "50px";
			counts.style.fontSize = "40px";
			counts.style.color = "#6aa84f";
			ms /= 1.2;
			if (ms < 75) ms = 75;
			setTimeout(frame, ms);
		}
	}
}


// return indices of next occurrence of the given bigram.
// null if it can't be found.
function nextBigram(bigram, index) {
	while (true) {
		var start1 = nextUnigram(bigram[0], index, true);
		if (start1 == null) return null;
		var inc = increment(index);
		if (!inc) return null;
		var start2 = nextUnigram(bigram[1], index, false)
		if (start2 != null) return [start1, start2];
	}
}

// return indices of all occurrences of the given bigram
function allBigrams(bigram) {
	var result = [];
	var index = [0,0];
	while (true) {
		var found = nextBigram(bigram, index);
		if (found == null) break;
		result[result.length] = found;
		if (!increment(index)) break;
	}
	return result;
}

// return index of start of next occurrence of the given unigram.
// null if it can't be found.
function nextUnigram(unigram, index, skip) {
	var inc = true;
	var row, col,q;
	while (inc) {
		row = index[0];
		col = index[1];
		q = quote[row][col];
		if (q == unigram) return [row, col];
		if (!skip) {
			var isAlpha = /[A-Z]/.test(q);
			if (isAlpha) return null;
		}
		inc = increment(index);
	}
	return null;
}

function increment(index) {
	index[1]++;
	if (index[1] >= quote[index[0]].length) {
		if (index[0] == quote.length-1) return false;
		index[0]++;
		index[1] = 0;
	}
	return true;
}

function qCountBigramsAllButton(ms) {
	hideButtons();
	var counts = document.getElementById("counter");
	counts.innerHTML = "TOTAL REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>0</span>";
	counts.style.fontFamily = "Work Sans";
	counts.style.position = "absolute";
	counts.style.top = "370px";
	counts.style.left = "360px";
	counts.style.fontSize = "40px";
	counts.style.color = "#6aa84f";
	
	setTimeout(function() {qCountBigramsAll(ms)}, 1000);
}

function qShuffle(ms) {
	var id = setTimeout(frame, ms);
	function frame() {
		// pick two random spots and swap them
		//var row1 = 0; 
		//var row2 = 0; 
		//var col1 = 0; 
		//var col2 = 0;
		//while (col1 == col2 && row1 == row2) {
		//	row1 = parseInt(Math.random()*quote.length);
		//	col1 = parseInt(Math.random()*quote[row1].length);
		//	row2 = parseInt(Math.random()*quote.length);
		//	col2 = parseInt(Math.random()*quote[row2].length);
		//}
		//var c1 = quote[row1][col1];
		//var c2 = quote[row2][col2];
		//console.log(row1+","+col1+","+row2+","+col2+","+c1+","+c2);
		//quote[row1] = quote[row1].replaceAt(col1, c2);
		//quote[row2] = quote[row2].replaceAt(col2, c1);
		//console.log(quote[row1]);
		//console.log(quote[row2]);
		ms = ms * 1.2;
		quoteLine2 = shuffle(quoteLine2);
		if (ms >= 1000) {
			quoteLine2 = quoteShuffled;
			quoteLine = quoteShuffledLine;
		}
		quoteConvert(quoteLine2);
		drawQuote(false);
		console.log(ms);
		if (ms < 1000) 
			setTimeout(frame, ms);
	}		
}

function qShuffleAverage(ms) {
	var id = setInterval(frame, ms);
	var counter = 0;
	var allCounts;
	var reps = 0;
	var total = 0;
	var min = 100000;
	var max = 0;
	var x = []; var y = [];
	for (var i=80; i<=115; i++) {
		x[i-80] = i;
		y[i-80] = 0;
	}
	function frame() {
		quoteLine2 = shuffle(quoteLine2);
		quoteConvert(quoteLine2);
		quoteLine3 = quoteLine2.replace(/[^A-Z]/g, "");
		
		allCounts = [];
		for (var i=0; i<quoteLine3.length-1; i++) {
			var bigram = quoteLine3.substring(i,i+2);
			if (allCounts[bigram]) allCounts[bigram]++;
			else allCounts[bigram] = 1;
		}
		reps = 0;
		for (var key in allCounts) {
			reps += allCounts[key] - 1;
//			console.log(key+" " + (allCounts[key]-1));
		}
		min = Math.min(min, reps);
		max = Math.max(max, reps);
		console.log(min + " " + max);
		total += reps;
		counter++;
		y[reps-80]++;
		console.log(x);
		if (counter % 100 == 0) {
			drawQuote(true, reps, parseInt(100*total/counter)/100, counter);
			drawGraph(x, y);
		}
		if (counter == 1000000)
			clearInterval(id);
	}		
}

function shuffle(str) {
	var a = Array.from(str);
	for (var i=str.length-1; i>= 1; i--) {
		var j = parseInt(Math.random()*(i+1));
		var tmp = a[j];
		a[j] = a[i];
		a[i] = tmp;
	}
	var result = "";
	for (var i=0; i<a.length; i++) result += a[i];
	return result;
}

function quoteConvert(str) {
	var index = 0;
	for (var i=0; i<quote.length; i++) {
		quote[i] = str.substring(index, index+quote[i].length);
		index += quote[i].length;
	}
}

String.prototype.replaceAt=function(index, replacement) {
    return this.substr(0, index) + replacement+ this.substr(index + replacement.length);
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function cipherHighlight(index, color) {
	var e = cipherblock[index[0]][index[1]];
	e.style.backgroundColor=color;
	e.style.color="black";
}
function cipherHighlight2(index, color) {
	var e = cipherblock[index[0]][index[1]];
	e.style.backgroundColor=color;
	e.style.color="#777";
}

function cipherClearAll() {
	for (var row=0; row<cipherblock.length; row++) {
		for (var col=0; col<cipherblock[row].length; col++) {
			cipherHighlight2([row, col], "white");
		}
	}
}

function cipherHighlightBigramsAll(bigram, which) {
	var index = 0;
	var found = false;
	var id = setInterval(frame, 1000);
	var reps = 0;
	function frame() {
		index = ciphers[which].indexOf(bigram, index);
		if (index == -1) {
			clearInterval(id);
			return;
		}
		var color = "#f90";
		if (found) color = "#0ff";
		else found = true;
		var row1 = parseInt(index / cipherblock[0].length);
		var col1 = index % cipherblock[0].length;
		var row2 = parseInt((index+1) / cipherblock[0].length);
		var col2 = (index+1) % cipherblock[0].length;
		cipherHighlight([row1, col1], color);
		cipherHighlight([row2, col2], color);
		index++;
		
		var counts = document.getElementById("counter");
		counts.innerHTML = "REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + reps + "</span><br>";
		counts.style.fontFamily = "Work Sans";
		counts.style.position = "absolute";
		counts.style.top = "300px";
		counts.style.left = "750px";
		counts.style.fontSize = "40px";
		counts.style.color = "#6aa84f";
		reps++;
	}
}

function cipherHighlightBigramsAllTotal(which) {
	
	var allCounts = [];
	for (var i=0; i<ciphers[which].length-1; i++) {
		var bigram = ciphers[which].substring(i,i+2);
		if (allCounts[bigram]) allCounts[bigram]++;
		else allCounts[bigram] = 1;
		console.log("bigram " + bigram + " " + allCounts[bigram]);
	}
	var bigramQueue = []; var seen = [];
	for (var pos=0; pos<ciphers[which].length-1; pos++) {
		var key = ciphers[which].substring(pos,pos+2);
		if (seen[key]) continue;
		if (allCounts[key] > 1) {
			bigramQueue[bigramQueue.length] = key;
			seen[key] = true;
			console.log(key + " " + (allCounts[key]-1));
		}
	}
	
	var index = 0;
	var ms = 1000;
	var id = setTimeout(frame, ms);
	var reps = 0;
	function frame() {
		cipherClearAll();
		if (index == bigramQueue.length) {
			return;
		}
		var bigram = bigramQueue[index];
		var pos = 0;
		var found = false;
		while (true) {
			pos = ciphers[which].indexOf(bigram, pos);
			if (pos == -1) break;
			var color = "#f90";
			if (found) color = "#0ff";
			else found = true;
			var row1 = parseInt(pos / cipherblock[0].length);
			var col1 = pos % cipherblock[0].length;
			var row2 = parseInt((pos+1) / cipherblock[0].length);
			var col2 = (pos+1) % cipherblock[0].length;
			cipherHighlight([row1, col1], color);
			cipherHighlight([row2, col2], color);
			pos++;
		}
		index++;
		reps += allCounts[bigram]-1;
		var counts = document.getElementById("counter");
		counts.innerHTML = "TOTAL REPEATS: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'>" + reps + "</span><br>";
		counts.style.fontFamily = "Work Sans";
		counts.style.position = "absolute";
		counts.style.top = "300px";
		counts.style.left = "750px";
		counts.style.fontSize = "40px";
		counts.style.color = "#6aa84f";
		
		var counts = document.getElementById("bigram");
		counts.innerHTML = "BIGRAM: <span style='color: red; font-family: \"Work Sans\"; font-size: 40px'><span id='bg'>" + bigram + "</span></span>";
		counts.style.fontFamily = "Work Sans";
		counts.style.position = "absolute";
		counts.style.top = "250px";
		counts.style.left = "750px";
		counts.style.fontSize = "40px";
		counts.style.color = "#6aa84f";

		
		ms = ms / 1.1;
		if (ms < 100) ms = 100;
		setTimeout(frame, ms);
	}
}
function cipherShuffleAverageButton(which, ms) {
	setTimeout(function() {
		cipherShuffleAverage(which, ms)	
	}, 2000);
}
function cipherShuffleAverage(which, ms) {
	drawCipher(ciphers[which], 17, 23, 23, 100, 30, 2, 2, 'cipher', '#777', true, 0, 0, 0);
	document.getElementById("myDiv").innerHTML = "";
	var id = setInterval(frame, ms);
	var counter = 0;
	var allCounts;
	var reps = 0;
	var total = 0;
	var min = 100000;
	var max = 0;
	var x = []; var y = [];
	for (var i=5; i<=(which==0 ? 50 : 40); i++) {
		x[i-5] = i;
		y[i-5] = 0;
	}
	function frame() {
		cipher = shuffle(ciphers[which]);
		
		allCounts = [];
		for (var i=0; i<cipher.length-1; i++) {
			var bigram = cipher.substring(i,i+2);
			if (allCounts[bigram]) allCounts[bigram]++;
			else allCounts[bigram] = 1;
		}
		reps = 0;
		for (var key in allCounts) {
			reps += allCounts[key] - 1;
//			console.log(key+" " + (allCounts[key]-1));
		}
		min = Math.min(min, reps);
		max = Math.max(max, reps);
		console.log(min + " " + max);
		total += reps;
		counter++;
		y[reps-5]++;
//		console.log(x);
		if (counter % 100 == 0) {
			//drawQuote(true, reps, parseInt(100*total/counter)/100, counter);
			ciphers[which] = cipher;
			drawCipher(ciphers[which], 17, 23, 23, 100, 30, 2, 2, 'cipher', '#777', true, reps, parseInt(100*total/counter)/100, counter)
			drawGraph(x, y);
		}
		if (counter >= 1000000) {
			clearInterval(id);
			if (which != 2) cipherShuffleAverageButton(2, 0);
			return;
		}
	}		
}

function testCiphersAverageButton(ms) {
	setTimeout(function() {
		testCiphersAverage(ms)	
	}, 2000);
}
function testCiphersAverage(ms) {
	which = 0;
	drawCipher(testCiphers[which], 17, 23, 23, 100, 30, 2, 2, 'cipher', '#777', true, 0, 0, 0);
	document.getElementById("myDiv").innerHTML = "";
	var id = setInterval(frame, ms);
	var counter = 0;
	var allCounts;
	var reps = 0;
	var total = 0;
	var min = 100000;
	var max = 0;
	var graphAdjust = 15;
	var x = []; var y = [];
	for (var i=graphAdjust; i<=65; i++) {
		x[i-graphAdjust] = i;
		y[i-graphAdjust] = 0;
	}
	function frame() {
		cipher = testCiphers[which];
		
		allCounts = [];
		for (var i=0; i<cipher.length-1; i++) {
			var bigram = cipher.substring(i,i+2);
			if (allCounts[bigram]) allCounts[bigram]++;
			else allCounts[bigram] = 1;
		}
		reps = 0;
		for (var key in allCounts) {
			reps += allCounts[key] - 1;
//			console.log(key+" " + (allCounts[key]-1));
		}
		min = Math.min(min, reps);
		max = Math.max(max, reps);
		console.log(min + " " + max);
		total += reps;
		counter++;
		y[reps-graphAdjust]++;
//		console.log(x);
		if (counter % 1 == 0) {
			drawCipher(testCiphers[which++], 17, 23, 23, 100, 30, 2, 2, 'cipher', '#777', true, reps, parseInt(100*total/counter)/100, counter)
			drawGraph(x, y);
		}
		if (which == testCiphers.length) {
			clearInterval(id);
			return;
		}
	}		
}

function readingButton(ms) {
	for (var i=0; i<340; i++) {
		sounds[i] = new sound("click sound2.mp3");
		sounds[i].play();
	}
	setTimeout(function() {
		reading(ms)
	}, 2000);
}

function reading(ms) {
	var W = cipherblock[0].length;
	var H = cipherblock.length;
	var alpha = []; // transparency values
	var alpha2 = []; // transparency values
	for (var r = 0; r<H; r++) {
		alpha[r] = [];
		alpha2[r] = [];
		for (var c = 0; c<W; c++) {
			alpha[r][c] = 0;
			alpha2[r][c] = 0;
		}
	}
	var row = 0; var col = 0;
	var row2 = 0; var col2 = 0;
	var fade = 0.04;
	var read = true;
	var id = setTimeout(frame, ms);
	var pos = 0; var offset = 0; var count = 0;
	var dec = 100;
	function frame() {
		row = parseInt(pos / W);
		col = pos % W;
		row2 = parseInt(count / W);
		col2 = count % W;
		if (read) {
			alpha[row][col] = 1.0;
			alpha2[row2][col2] = 1.0;
			cipherblock2[row2][col2].innerHTML = cipherblock[row][col].innerHTML;
			cipherblock2[row2][col2].style.color="#777";
			setTimeout(function() {
				sounds[count].play();
			}, 0);
		}
		for (var r = 0; r<H; r++) {
			for (var c = 0; c<W; c++) {
				
				// left cipher
				var a = alpha[r][c];
				if (a > 0.05) {
					cipherblock[r][c].style.color = "rgba(0, 0, 0, " + a + ")";
				}
				cipherblock[r][c].style.backgroundColor = "rgba(255, 144, 0, " + a + ")";
				if (a > 0 && (r != row || c != col)) alpha[r][c]-=fade;
				
				// right cipher
				a = alpha2[r][c];
				if (a > 0.05) {
					var color = 119*(1-a)/0.9;
					color = parseInt(color);
					cipherblock2[r][c].style.color = "rgb(" + color + ", " + color + ", " + color + ")";
				}
				cipherblock2[r][c].style.backgroundColor = "rgba(255, 144, 0, " + a + ")";
				if (a > 0 && (r != row2 || c != col2)) alpha2[r][c]-=fade;
				
			}
		}
		
		/*
		
		6 t^5 - 15 t^4 + 10 t^3 
		t [0,1] but we need to split into 50 intervals
		output [0,1] but scale to [1000, 20]
*/		
		
		if (ms > 100) {
			var t = Math.min(13, count);
			t = t / 13;
			var easing = 6*Math.pow(t,5)-15*Math.pow(t,4)+10*Math.pow(t,3);
			ms = 700 - 600*easing; // [700, 100]
		} else ms = Math.max(14, ms-1);
		
		
		
		count++;
		pos += 19;
		if (pos >= 340) {
			offset++;
			pos = offset;
		}
		if (count == 340) read = false;

//		if (ms < 120) ms = Math.max(20, ms-3);
//		else ms = Math.max(20, ms-dec);
//		dec /= 1.1;
		console.log(ms);
		setTimeout(frame, ms);
	}
}

function fromPos(pos) {
	return [parseInt(pos/17), pos % 17];
}
function toPos(r, c) {
	return r*17 + c;
}

function sound(src) {
  this.sound = document.createElement("audio");
  this.sound.src = src;
  this.sound.setAttribute("preload", "auto");
  this.sound.setAttribute("controls", "none");
  this.sound.style.display = "none";
  document.body.appendChild(this.sound);
  this.play = function(){
    this.sound.play();
  }
  this.stop = function(){
    this.sound.pause();
  }
}
var sounds = [];

