var ciphers = new Array(
//z408 (full)
"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
//z408 (1st part)   	 
//"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRk",
//z408 (1st part) (with fixes)
//"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX75zS(RNt!YFlO9qGBTQS#BLd/P#B@XqEHMU^RRk",
//z408 solution
"ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGERTUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI",

// z408 with one fix
"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNt!YElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
// z408 sol with one fix
"ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGEROUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI",

// z408 with sloi fix
"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBXAzXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
"ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGEROUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOWDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI",


// fixed "dangerous", "animal" version
//"ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGEROUSANIMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI",
//z340
//"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+"
)

var ROWS=17;
var COLS=24;

var wordMask = 
"011110000000111111000000011001100001110011000011100001111111000011110011100000001111111000110001111000000000111111001110011110000000001111100111000011111111100000000011001111000000111100000001111000001110000100001110000111100110011110000100010000110000001100000000111000111011110000001111000000110000001000011100001110011110000000111000011100111100001100001100000000001100000011100111111111000000000000000000";
            
var cipherblock = [];

function pt(row,col) {
	return ciphers[1][row*24+col].toLowerCase();
}
function ct(row,col) {
	return ciphers[0][row*24+col];
}
function drawPlaintext() {
	var html = "";
	
	// first, pre-load the cipher grid
	
	for (var row=0; row<ROWS; row++) {
		cipherblock[row] = [];
		for (var col=0; col<COLS; col++) {
			var cell = "<span id=\"r" + row + "c" + col + "\" class=\"p\"></span>";
			html += cell;
		}
	}
	document.getElementById("cipher").innerHTML = html;
	                          
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	
	// update plaintext
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			elem.style.position = "absolute";
			elem.style.left = startx + col*dx + "px";
			elem.style.top = starty + row*dy + "px";
			
			var color = wordMask[row*COLS+col] == "0" ? "#060" : "#0c0";
			elem.style.color = color;
			                                                           	
			elem.innerHTML = pt(row,col);
		}
	}
}
function updateBlack() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var color = "black";
			elem.style.color = color;
		}
	}
}
function updateIUNUSED() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = pt(row,col) == "e";
			if (e) {
				elem.style.fontWeight="bold";
				elem.style.color="black";
				continue;
			}
			elem.style.color="#ddd";
		}
	}
}

function updateC() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			elem.innerHTML = ct(row,col);
			elem.style.color="#ccc";
			elem.className = "c";
		}
	}
}
function updateCDark() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			elem.innerHTML = ct(row,col);
			elem.style.color="#000";
			elem.className = "c";
		}
	}
}
function updateSloiC() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;        
	var row = 14;
	for (var col=6; col<10; col++) {
		var elem = document.getElementById("r"+row+"c"+col);
		elem.style.color="black";
		elem.style.fontWeight="bold";
	}
}
function updateSloiP() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;        
	var row = 14;
	for (var col=6; col<10; col++) {
		var elem = document.getElementById("r"+row+"c"+col);
		elem.style.color="#090";
		elem.style.fontWeight="bold";
		elem.className = "p";
		elem.innerHTML = pt(row, col);
	}
}

function updateDownC() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;        
	var row = 14;
	for (var col=10; col<14; col++) {
		var elem = document.getElementById("r"+row+"c"+col);
		elem.style.color="black";
		elem.style.fontWeight="bold";
	}
}
function updateDownP() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;        
	var row = 14;
	for (var col=10; col<14; col++) {
		var elem = document.getElementById("r"+row+"c"+col);
		elem.style.color="#090";
		elem.style.fontWeight="bold";
		elem.className = "p";
		elem.innerHTML = pt(row, col);
	}
}


function updateO() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = ct(row,col) == "!";
			if (e) {
				elem.style.fontWeight="bold";
				elem.style.color="#090";
//				elem.className = "c";
//				elem.innerHTML = "!";
				continue;
			}
			elem.style.color="#ddd";
		}
	}
}

function isSloi(row, col) {
	return row == 14 && col > 5 && col < 10;
}

function updateI() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = ct(row,col) == "!";
			if (e) {
				elem.style.fontWeight="bold";
				elem.style.color="black";
				elem.className = "c";
				elem.innerHTML = "!";
				continue;
			}
			elem.style.color="#ddd";
		}
	}
}

function updateIDangertue() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = ct(row,col) == "!";
			if (e) {
				elem.style.fontWeight="bold";
				elem.style.color="black";
				elem.className = "c";
				elem.innerHTML = "!";
				continue;
			}
			if (isDangertue(row, col))
				elem.style.color="#000";
			else 
				elem.style.color="#ddd";
		}
	}
}
function updateIDangertueFix1() {
	var elem = document.getElementById("r4c9");
	elem.innerHTML = "!";
}
function updateIDangertueFix1P() {
	updateDangertueP();
	var elem = document.getElementById("r4c9");
	elem.innerHTML = "o";
}

function drawPlaintextFix1() {
	ciphers[1] = ciphers[3];
	ciphers[0] = ciphers[2];
	drawPlaintext();
}
function drawPlaintextFix2() {
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			elem.style.color="#ddd";
		}
	}
}

function drawPlaintextRest() {
	// update plaintext
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var color = wordMask[row*COLS+col] == "0" ? "#060" : "#0c0";
			elem.style.color = color;
			elem.className = "p";
//			if (row == 4 && (col > 2 && col < 12)) 
//				elem.style.backgroundColor = "yellow";
			                                                           	
			elem.innerHTML = pt(row,col);
		}
	}
}

function updateS() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = ct(row,col) == "F";
			if (e) {
				elem.style.fontWeight="bold";
				elem.style.color="#090";
//				elem.className = "c";
//				elem.innerHTML = "!";
				continue;
			}
			elem.style.color="#ddd";
		}
	}
}
function updateF() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = ct(row,col) == "F";
			if (e) {
				elem.style.fontWeight="bold";
				elem.style.color="black";
				elem.className = "c";
				continue;
			}
			elem.style.color="#ddd";
		}
	}
}
function fixC() {
	drawPlaintext();
	ciphers[0] = ciphers[4];
	ciphers[1] = ciphers[5];
	updateC();
	updateSloiC();
}
function fixP() {
	updateSloiP();
}

function triangles() {
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = isTriangle(ct(row,col));
			if (e) {
				elem.style.fontWeight="bold";
				elem.style.color="black";
				elem.className = "c";
				continue;
			}
			elem.style.color="#ddd";
		}
	}
}
function isTriangle(ch) {
	return ch == "7" || ch == "8" || ch == "9";
}
function fadeC() {
	drawPlaintext();
	updateC();
}

function updateFDangertue() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = ct(row,col) == "F";
			if (e) {
				elem.style.fontWeight="bold";
				elem.style.color="black";
				elem.className = "c";
				continue;
			}
			if (isDangertue(row, col))
				elem.style.color="#000";
			else 
				elem.style.color="#ddd";
		}
	}
}
function updateFDangertueFix1() {
	var elem = document.getElementById("r4c11");
	elem.innerHTML = "F";
}
function updateFDangertueFix1P() {
	updateDangertueP();
	var elem = document.getElementById("r4c11");
	elem.innerHTML = "s";
}

