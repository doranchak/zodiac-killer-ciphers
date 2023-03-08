var ciphers = new Array(
//z408 (full)
"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
//z408 (1st part)   	 
//"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRk",
//z408 (1st part) (with fixes)
//"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX75zS(RNt!YFlO9qGBTQS#BLd/P#B@XqEHMU^RRk",
//z408 solution
"ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGERTUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI",
// fixed "dangerous", "animal" version
//"ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGEROUSANIMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI",
//z340
"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+"
)

var wordMask = 
"011110000000111111000000011001100001110011000011100001111111000011110011100000001111111000110001111000000000111111001110011110000000001111100111000011111111100000000011001111000000111100000001111000001110000100001110000111100110011110000100010000110000001100000000111000111011110000001111000000110000001000011100001110011110000000111000011100111100001100001100000000001100000011100111111111000000000000000000";
            
var cipherblock = [];

function pt(row,col) {
	return ciphers[1][row*34+col].toLowerCase();
}
function ct(row,col) {
	return ciphers[2][row*34+col];
}
function draw() {
	var html = "";
	
	// first, pre-load the cipher grid
	
	for (var row=0; row<10; row++) {
		cipherblock[row] = [];
		for (var col=0; col<34; col++) {
			var cell = "<span id=\"r" + row + "c" + col + "\" class=\"c\"></span>";
			html += cell;
		}
	}
	document.getElementById("cipher").innerHTML = html;
	                          
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	
	// update plaintext
	for (var row=0; row<10; row++) {
		for (var col=0; col<34; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			elem.style.position = "absolute";
			elem.style.left = startx + col*dx + "px";
			elem.style.top = starty + row*dy + "px";
			
			var color = "black";
			elem.style.color = color;
			                                                           	
			elem.innerHTML = ct(row,col);
		}
	}
}
function updateCycle1() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<10; row++) {
		for (var col=0; col<34; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var color;
			if (elem.innerHTML == "l" || elem.innerHTML == "M")
				color = "black";
			else 
				color = "#ccc";
			elem.style.color = color;
		}
	}
	document.getElementById("sequence").innerHTML = "<center>lM&nbsp;&nbsp;lM&nbsp;&nbsp;lM&nbsp;&nbsp;lM&nbsp;&nbsp;lM&nbsp;&nbsp;lM&nbsp;&nbsp;lM</center>";
}
function updateCycle2() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<10; row++) {
		for (var col=0; col<34; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var color;
			if (elem.innerHTML == "^" || elem.innerHTML == "*")
				color = "black";
			else 
				color = "#ccc";
			elem.style.color = color;
		}
	}
	document.getElementById("sequence").innerHTML = "<center>^*&nbsp;&nbsp;^*&nbsp;&nbsp;^*&nbsp;&nbsp;^*&nbsp;&nbsp;^*&nbsp;&nbsp;^*</center>";
}                                    
function updateCycle3() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<10; row++) {
		for (var col=0; col<34; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var color;
			if (elem.innerHTML == "*" || elem.innerHTML == "9" || elem.innerHTML == "t")
				color = "black";
			else 
				color = "#ccc";
			elem.style.color = color;
		}
	}
	document.getElementById("sequence").innerHTML = "<center>*&nbsp;&nbsp;*9t&nbsp;&nbsp;*9t&nbsp;&nbsp;*9t&nbsp;&nbsp;*9t&nbsp;&nbsp;*&nbsp;</center>";
}                                    
function updateCycle4() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<10; row++) {
		for (var col=0; col<34; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var color;
			if (elem.innerHTML == "l" || elem.innerHTML == "V" || elem.innerHTML == "M")
				color = "black";
			else 
				color = "#ccc";
			elem.style.color = color;
		}
	}
	document.getElementById("sequence").innerHTML = "<center>lVM&nbsp;&nbsp;lVM&nbsp;&nbsp;lVM&nbsp;&nbsp;lVM&nbsp;&nbsp;l&nbsp;&nbsp;MlVVMlM</center>";
}                                    

function updateE() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<12; row++) {
		for (var col=0; col<34; col++) {
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

function updateCE() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<12; row++) {
		for (var col=0; col<34; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = pt(row,col) == "e";
			if (e) {
				elem.style.color="black";
				elem.innerHTML = ct(row,col);
				elem.className = "c";
				continue;
			}
			elem.style.color="#ddd";
		}
	}
}
function updateCRest() {
	var startx = 20;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	for (var row=0; row<12; row++) {
		for (var col=0; col<34; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
			var e = pt(row,col) == "e";
			if (e) {
				continue;
			}
			elem.innerHTML = ct(row,col);
			elem.className = "c";
			elem.color = "#ddd";
		}
	}
}

function sequence() {
	drawPlaintext();
	updateE();
	updateCE();
	updateCRest();
	
	var html = "";
 	for (var row=0; row<4; row++) {
		for (var col=0; col<70; col++) {
			var cell = "<span id=\"sr" + row + "c" + col + "\" class=\"c\"></span>";
			html += cell;
		}
	}
	document.getElementById("sequence").innerHTML = html;
	
	setTimeout(function() {sequenceTimer(0, "", 0, 20)}, 500);
}   

function sequenceTimer(pos, found, x, y) {
	const regex = /[ZpW+6NE]/g;	
	var ct;
	while (true) {
		if (pos == ciphers[0].length) return;
		ct = ciphers[0][pos];
		//console.log(pos + ", " + ct);
		if (ct.match(regex)) break;
		pos++;
	}
	found += ct;
	
	console.log("match: " + pos + "," + ct + ", found " + found);
	var row = parseInt(pos/34);
	var col = pos % 34;
	var elemTop = document.getElementById("r"+row+"c"+col);
	elemTop.style.color = "#090";
	elemTop.style.animation = "fade1 0.5s";
	
	var dx = 24;
	var dy = 32;

//	var x = startx + dx * found.length;
//	var y = starty;
	if (found.length == 35) {
		y += dy;
		x = 24;
	} else if (found.length == 18) {
		y += dy;
		x = 24;
	} else {
		x += dx;
	}
	
	var id = "sr0c" + (found.length-1);
	console.log("id " + id + ", len " + found.length + " x " + x + " y " + y);
	var elem = document.getElementById(id);
	elem.style.position = "absolute";
	elem.style.left = x + "px";
	elem.style.top = y + "px";
	elem.style.color = "#090";
	elem.style.animation = "fade1 0.5s"
	elem.innerHTML = ct;
	
	if (found == "ZpW+6NE") found += " ";
	else if (found == "ZpW+6NE ZpW+6NE") found += " ";
	else if (found == "ZpW+6NE ZpW+6NE EZpW+6NE") found += " ";
	else if (found == "ZpW+6NE ZpW+6NE EZpW+6NE ZpW+6NE") found += " ";
	else if (found == "ZpW+6NE ZpW+6NE EZpW+6NE ZpW+6NE NZpW+6NE") found += " ";
	else if (found == "ZpW+6NE ZpW+6NE EZpW+6NE ZpW+6NE NZpW+6NE ZpW+") found += " ";
	if (found.endsWith(" ")) x += dx;
   
	setTimeout(function() {sequenceTimer(pos+1, found, x, y)}, 125);
}