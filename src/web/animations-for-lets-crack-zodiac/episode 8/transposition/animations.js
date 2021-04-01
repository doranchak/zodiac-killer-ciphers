// pivot positions in the solved (untransposed) plaintext
var pivot1 = [97,80,216,199,182,173,164,155,293];
var pivot2 = [244,227,210,193,184,175,166];

var ciphers = new Array(
  "IHOPEYOUAREHAVINGLOTSOFFANINTRYINGTOCATCHMETHATWASNTMEONTHETVSHOWWHICHBRINGOUPAPOINTABOUTMEIAMNOTAFRAIDOFTHEGASCHAMBERBECAASEITWILLSENDMETOPARADLCEALLTHE",
  "SOOHERBECAUSEENOWHAVEENOUGHSLAVESTOWORVFORMEWHEREEVERYONEELSEHASNOTHINGWHENTHEYREACHPARADICESOTHEYAREAFRAIDOFDEATHIAMNOTAFRAIDBECAUSEIVNOWTHATMYNEW",
  "LIFEWILLBEANEASYONEINPARADICEDEATH",
  "MYNEWLIFEWILLBEANEASYONEINPARADICE.LIFEISDEATH",
  "MYNEWLIFEWILLBEANEASYONEINPARADICE.DEATHISLIFE",
  "MYNEWLIFEWILLBEANEASYONE.INPARADICE,LIFEISDEATH",
  "MYNEWLIFEWILLBEANEASYONE.INPARADICE,DEATHISLIFE",
  "MYNEWLIFEWILLBEANEASYONE.DEATHISLIFEINPARADICE"
);

var ciphersSpaces = new Array(
  "I HOPE YOU ARE HAVING LOTS OF FAN IN TRYING TO CATCH ME THAT WASNT ME ON THE TV SHOW WHICH BRINGO UP A POINT ABOUT ME I AM NOT AFRAID OF THE GAS CHAMBER BECAASE IT WILL SEND ME TO PARADLCE ALL THE",
  "SOOHER BECAUSE E NOW HAVE ENOUGH SLAVES TO WORV FOR ME WHERE EVERY ONE ELSE HAS NOTHING WHEN THEY REACH PARADICE SO THEY ARE AFRAID OF DEATH I AM NOT AFRAID BECAUSE I VNOW THAT MY NEW",
  "LIFE WILL BE AN EASY ONE IN PARADICE DEATH",
  "MY NEW LIFE WILL BE AN EASY ONE IN PARADICE. LIFE IS DEATH.",
  "MY NEW LIFE WILL BE AN EASY ONE IN PARADICE. DEATH IS LIFE.",
  "MY NEW LIFE WILL BE AN EASY ONE. IN PARADICE, LIFE IS DEATH.",
  "MY NEW LIFE WILL BE AN EASY ONE. IN PARADICE, DEATH IS LIFE.",
  "MY NEW LIFE WILL BE AN EASY ONE. DEATH IS LIFE IN PARADICE.",
  "LIFE WILL BE AN EASY ONE IN PARADICE LIFE IS DEATH"
);

var z340Plaintext = [
  "ironcaooiiergrtml",
  "echettatnwnniaabw",
  "eiteohsrtwtwgtais",
  "dccloapaoycahhoam",
  "bnohalplevfihseiu",
  "cpoofaasalyifnmnt",
  "tvhauttmsertonage",
  "ttmsbptahbenahugn",
  "liohehromfeeiddea",
  "saasohoshaclifeis",
  "shouvlrenneceroaa",
  "mieaoseavreonhsef",
  "adnditheevfeettpo",
  "atfeobvmeeneoelhh",
  "ierratenyrnosrvsh",
  "eenyaeataconboutm",
  "oerhgrdyihfaweewg",
  "hhwwyaweiadirutwc",
  "efilwillebnaeasye",
  "noniecidarapdeath"
];

var z340PlaintextTransposed = [
	"ihopeyouarehaving",
	"lotsoffanintrying",
	"tocatchmethatwasn",
	"tmeonthetvshowwhi",
	"chbringoupapointa",
	"boutmeiamnotafrai",
	"dofthegaschamberb",
	"ecaaseitwillsendm",
	"etoparadlceallthe",
	"sooherbecauseenow",
	"haveenoughslavest",
	"oworvformewhereev",
	"eryoneelsehasnoth",
	"ingwhentheyreachp",
	"aradicesotheyarea",
	"fraidofdeathiamno",
	"tafraidbecauseivn",
	"owthatmynewlifeis",
	"lifewillbeaneasyo",
	"neinparadicedeath"
];

var z340Ciphertext = [
  "HER>pl^VPk|1LTG2d",
  "Np+B(#O%DWY.<*Kf)",
  "By:cM+UZGW()L#zHJ",
  "Spp7^l8*V3pO++RK2",
  "_9M+ztjd|5FP+&4k/",
  "p8R^FlO-*dCkF>2D(",
  "#5+Kq%;2UcXGV.zL|",
  "(G2Jfj#O+_NYz+@L9",
  "d<M+b+ZR2FBcyA64K",
  "-zlUV+^J+Op7<FBy-",
  "U+R/5tE|DYBpbTMKO",
  "2<clRJ|*5T4M.+&BF",
  "z69Sy#+N|5FBc(;8R",
  "lGFN^f524b.cV4t++",
  "yBX1*:49CE>VUZ5-+",
  "|c.3zBK(Op^.fMqG2",
  "RcT+L16C<+FlWB|)L",
  "++)WCzWcPOSHT/()p",
  "|FkdW<7tB_YOB*-Cc",
  ">MDHNpkSzZO8A|K;+"
];

var z408Ciphertext = [
	"9%P/Z/UB%kOR=pX=B",
	"WV+eGYF69HP@K!qYe",
	"MJY^UIk7qTtNQYD5)",
	"S(/9#BPORAU%fRlqE",
	"k^LMZJdr\\pFHVWe8Y",
	"@+qGD9KI)6qX85zS(",
	"RNtIYElO8qGBTQS#B",
	"Ld/P#B@XqEHMU^RRk",
	"cZKqpI)Wq!85LMr9#",
	"BPDR+j=6\\N(eEUHkF",
	"ZcpOVWI5+tL)l^R6H",
	"I9DR_TYr\\de/@XJQA",
	"P5M8RUt%L)NVEKH=G",
	"rI!Jk598LMlNA)Z(P",
	"zUpkA9#BVW\\+VTtOP",
	"^=SrlfUe67DzG%%IM",
	"Nk)ScE/9%%ZfAP#BV",
	"peXqWq_F#8c+@9A9B",
	"%OT5RUc+_dYq_^SqW",
	"VZeGYKE_TYA9%#Lt_",
	"H!FBX9zXADd\\7L!=q",
	"_ed##6e5PORXQF%Gc",
	"Z@JTtq_8JI+rBPQW6",
	"VEXr9WI6qEHM)=UIk"]

var z408Plaintext = [
	"ILIKEKILLINGPEOPL",
	"EBECAUSEITISSOMUC",
	"HFUNITISMOREFUNTH",
	"ANKILLINGWILDGAME",
	"INTHEFORRESTBECAU",
	"SEMANISTHEMOATDAN",
	"GERTUEANAMALOFALL",
	"TOKILLSOMETHINGGI",
	"VESMETHEMOATTHRIL",
	"LINGEXPERENCEITIS",
	"EVENBETTERTHANGET",
	"TINGYOURROCKSOFFW",
	"ITHAGIRLTHEBESTPA",
	"RTOFITIATHAEWHENI",
	"DIEIWILLBEREBORNI",
	"NPARADICESNDALLTH",
	"EIHAVEKILLEDWILLB",
	"ECOMEMYSLAVESIWIL",
	"LNOTGIVEYOUMYNAME",
	"BECAUSEYOUWILLTRY",
	"TOSLOIDOWNORSTOPM",
	"YCOLLECTINGOFSLAV",
	"ESFORMYAFTERLIFEE",
	"BEORIETEMETHHPITI"
]


var ROWS=9;
var COLS=17;

var wordMask = new Array(
"011110001110000001111001110011111100111110011110000011001110011110000011111100100000111110010011100000011000111000000011111110011110000110011111111000111",
// IHOPEYOUAREHAVINGLOTSOFFANINTRYINGTOCATCHMETHATWASNTMEONTHETVSHOWWHICHBRINGOUPAPOINTABOUTMEIAMNOTAFRAIDOFTHEGASCHAMBERBECAASEITWILLSENDMETOPARADLCEALLTHE
  "000000111111101110000111111000000110000111001111100000000111100011111110000111100000111111110011110001111110011111011000111111000000010000111100111",
// SOOHERBECAUSEENOWHAVEENOUGHSLAVESTOWORVFORMEWHEREEVERYONEELSEHASNOTHINGWHENTHEYREACHPARADICESOTHEYAREAFRAIDOFDEATHIAMNOTAFRAIDBECAUSEIVNOWTHATMYNEW
  "0000111100110000111001111111100000"
// LIFEWILLBEANEASYONEINPARADICEDEATH
);

var cipherblock = [];

/** translate position from z340 to transposed (solved/readable) position */
var mapPositionFromZ340 = [0,19,38,57,76,95,114,133,152,1,20,39,58,77,96,115,134,136,2,21,40,59,78,97,116,135,137,3,22,41,60,79,98,117,119,138,4,23,42,61,80,99,118,120,139,5,24,43,62,81,100,102,121,140,6,25,44,63,82,101,103,122,141,7,26,45,64,83,85,104,123,142,8,27,46,65,84,86,105,124,143,9,28,47,66,68,87,106,125,144,10,29,48,67,69,88,107,126,145,11,30,49,51,70,89,108,127,146,12,31,50,52,71,90,109,128,147,13,32,34,53,72,91,110,129,148,14,33,35,54,73,92,111,130,149,15,17,36,55,74,93,112,131,150,16,18,37,56,75,94,113,132,151,153,172,191,210,229,247,267,286,305,154,173,192,211,230,248,268,287,289,155,174,193,212,231,249,269,288,290,156,175,194,213,232,250,270,272,291,157,176,195,214,233,251,271,273,292,158,177,196,215,234,252,255,274,293,159,178,197,216,235,253,256,275,294,160,179,198,217,236,238,257,276,295,161,180,199,218,237,239,258,277,296,162,181,200,219,221,240,259,278,297,163,182,201,220,222,254,260,279,298,183,202,204,223,241,261,280,299,184,203,205,224,242,262,281,300,185,187,206,225,243,263,282,301,186,188,207,226,244,264,283,302,170,189,208,227,245,265,284,303,171,190,209,228,246,266,285,304,164,165,166,167,168,169,309,308,307,306,310,311,312,313,315,314,317,316,318,319,320,321,324,323,322,326,325,334,333,332,331,330,329,328,327,335,336,337,338,339];
/** translate transposed (solved/readable) position back to original position in z340 */
var mapPositionToZ340 = [0,9,18,27,36,45,54,63,72,81,90,99,108,117,126,135,144,136,145,1,10,19,28,37,46,55,64,73,82,91,100,109,118,127,119,128,137,146,2,11,20,29,38,47,56,65,74,83,92,101,110,102,111,120,129,138,147,3,12,21,30,39,48,57,66,75,84,93,85,94,103,112,121,130,139,148,4,13,22,31,40,49,58,67,76,68,77,86,95,104,113,122,131,140,149,5,14,23,32,41,50,59,51,60,69,78,87,96,105,114,123,132,141,150,6,15,24,33,42,34,43,52,61,70,79,88,97,106,115,124,133,142,151,7,16,25,17,26,35,44,53,62,71,80,89,98,107,116,125,134,143,152,8,153,162,171,180,189,198,207,216,225,234,243,300,301,302,303,304,305,284,292,154,163,172,181,190,199,208,217,226,235,244,252,260,268,276,269,277,285,293,155,164,173,182,191,200,209,218,227,236,245,253,261,254,262,270,278,286,294,156,165,174,183,192,201,210,219,228,237,246,238,247,255,263,271,279,287,295,157,166,175,184,193,202,211,220,229,221,230,239,256,264,272,280,288,296,158,167,176,185,194,203,212,248,204,213,222,231,240,249,257,265,273,281,289,297,159,168,177,186,195,187,196,205,214,223,232,241,250,258,266,274,282,290,298,160,169,178,170,179,188,197,206,215,224,233,242,251,259,267,275,283,291,299,161,309,308,307,306,310,311,312,313,315,314,317,316,318,319,320,321,324,323,322,326,325,334,333,332,331,330,329,328,327,335,336,337,338,339];


function pt(row,col,which) {
  if (row*COLS+col >= ciphers[which].length) return "";
	return ciphers[which][row*COLS+col].toLowerCase();
}
function ct(row,col,which) {
	return ciphers[which][row*COLS+col];
}
function drawPlaintext(which) {
	console.log("drawplaintext");
	var html = "<span id=\"c1\">";

	// first, pre-load the cipher grid

	for (var row=0; row<ROWS; row++) {
		cipherblock[row] = [];
		for (var col=0; col<COLS; col++) {
			var cell = "<span id=\"r" + row + "c" + col + "\" class=\"p\"></span>";
			html += cell;
		}
	}
  html += "</span>"
	document.getElementById("cipher").innerHTML = html;

	var startx = 20;
	var starty = 20;
	var dx = 28;
	var dy = 36;

	// update plaintext
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var elem = document.getElementById("r"+row+"c"+col);
      if (!elem) return;
			elem.style.position = "absolute";
			elem.style.left = startx + col*dx + "px";
			elem.style.top = starty + row*dy + "px";

			var color = wordMask[which][row*COLS+col] == "0" ? "#060" : "#0c0";
			elem.style.color = color;

			elem.innerHTML = pt(row,col,which);
		}
	}
}

function transpose(row, col) {
  var pos = row*COLS + col;

  var row2 = pos % ROWS;
  var col2 = parseInt(pos / ROWS);
  return [row2, col2];

}
// highlight position in 1st cipher, draw it in 2nd cipher
function draw(row, col) {
  var id = "r" + row + "c" + col;
  highlight(row, col, "r", "orange");

  var rc = transpose(row, col);
  var row2 = rc[0];
  var col2 = rc[1];
  console.log(row2+","+col2);

  var startx = 550;
  var starty = 20;
  var dx = 28;
  var dy = 36;

  var left = "" + (startx + col2*dx) + "px";
  var top = "" + (starty + row2*dy) + "px";

  var color = wordMask[0][row*COLS+col] == "0" ? "#060" : "#0c0";
  var elem = document.getElementById("s"+row2+"c"+col2);
  elem.style.color = color;
  elem.style.backgroundColor = "orange";
  elem.innerHTML = pt(row, col, 0);

  //var cell2 = "<span id=\"s" + row2 + "c" + col2 + "\" class=\"p\" style=\"position: absolute; left: " +
  //  left + "; top: " + top + "; color: " + color +"; background-color: orange\">" + pt(row, col, 0) + "</span>";
  //var html = document.getElementById("cipher").innerHTML;
  //html = html + cell2;
  //document.getElementById("cipher").innerHTML = html;
}
// unhighlight the two positions
function drawun(row, col) {
    if (!document.getElementById("r"+row+"c"+col)) return;
    document.getElementById("r"+row+"c"+col).style.backgroundColor = "white";
    var pos = row*COLS + col;
    var row2 = pos % ROWS;
    var col2 = parseInt(pos / ROWS);
    console.log(row+","+col + " " + pos + " " + row2+","+col2);
    if (!document.getElementById("s"+row2+"c"+col2)) return;
    document.getElementById("s"+row2+"c"+col2).style.backgroundColor = "white";
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

function trianglesWTF() {
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

function makeHighlightLayer() {
	var startx = 18;
	var starty = 18;
	var dx = 24;
	var dy = 32;

	var html = "";
	for (var row=0; row<ROWS; row++) {
		for (var col=0; col<COLS; col++) {
			var id = "s" + row + "_" + col;
			var x = col*dx + startx;
			var y = row*dy + starty;
			html += "<span id='" + id + "' class=\"square\" style=\"display: none; position:absolute; top: " + y + "px; left: " + x + "px; z-index: -1\"></span>";
		}
	}
	document.getElementById("squares").innerHTML = html;

}
function highlight(row, col, prefix, color) {
	var id = prefix + row + "c" + col;
	var elem = document.getElementById(id);
	elem.style.backgroundColor = color;
}
function unhighlight(row, col, prefix, originalColor) {
	var id = prefix + row + "c" + col;
	var elem = document.getElementById(id);
	if (elem != null) { elem.style.backgroundColor = originalColor; }
}
var key;
var alpha = "ABCDEFGHIKLMNOPRSTUVWXY";

function drawKey() {
	key = [];
	for (var i=0; i<ciphers[0].length; i++) {
		var ct = ciphers[0][i];
		var pt = ciphers[1][i];
		var val = key[pt];
		if (val == null) val = "";
		if (val.indexOf(ct) == -1) val += ct;
		key[pt] = val;
	}
//	console.log(key);
	var startx = 630;
	var starty = 20;
	var dx = 24;
	var dy = 32;
	var html = "";
	var x = startx;
	var y = starty;
	for (var i=0; i<alpha.length; i++) {
		var id = "keyw_" + alpha[i].toLowerCase();
		var idC = "keyw_" + alpha[i].toLowerCase() + "_c";
		var idP = "keyw_" + alpha[i].toLowerCase() + "_p";
		html += "<span class=\"keyw\" id=\"" + id + "\">";
//		console.log(i);
		for (var j=0; j<key[alpha[i]].length; j++) {
			var ct = key[alpha[i]][j];
			html += "<span id=\"" + idC + "\" class=\"c\">" + ct + "</span>";
		}
	    // html += "<span class=\"keyp\" style=\"position: relative; top: " + y + "px; left: " + x + "px;\">" + alpha[i].toLowerCase() + "</span>";
 	    html += "<span id=\"" + idP + "\" class=\"keyp\">" + alpha[i].toLowerCase() + "&nbsp;</span>";
		html += "</span>";
		y += dy;
	}
	document.getElementById("key").innerHTML = html;
}
function makeHighlightLayerKey() {
	var html = "";
	for (var i=0; i<alpha.length; i++) {
		var al = alpha[i].toLowerCase();
		var idC = "keyw_" + al;
		var e = document.getElementById(idC);
		var rect = e.getBoundingClientRect();

		var startx = rect.x + 6;
		var starty = rect.y + 5;
		var dx = 20;
		var keyc = key[alpha[i]];
		for (var j=0; j<keyc.length; j++) {
			var id = "kh_" + al + "_" + j;
			var y = starty;
			var x = startx + dx*j;
			html += "<span id='" + id + "' class=\"square\" style=\"display: none; position:absolute; top: " + y + "px; left: " + x + "px; z-index: -1\"></span>";
		}
	}
	document.getElementById("keyhigh").innerHTML = html;
}

function highlightKey(row, col, on) {
	var p = pt(row, col);
	var c = ct(row, col);
	var val = key[p.toUpperCase()];
	for (var i=0; i<val.length; i++) {
		if (val[i] == c) {
			var id = "kh_" + p + "_" + i;
			console.log(id);
			document.getElementById(id).style.display = on ? "" : "none";
		}
	}
}

function decode(row, col, ms) {
	//  highlight ct in cipher & key
	highlight(row, col);
	highlightKey(row, col, true);
	// unhighlight all but one key element
	var p = pt(row, col);
//	for (var i=0; i<alpha.length; i++) {
//		var al = alpha[i].toLowerCase();
//		if (al == p) continue;
//		document.getElementById("keyw_" + al).className = "keyw_off";
//		document.getElementById("keyw_" + al + "_c").className = "c_off";
//		document.getElementById("keyw_" + al + "_p").className = "keyp_off";
//	}
	// highlight key wrapper
	document.getElementById("keyw_" + p).className = "keyw_off";

	setTimeout(function() {
		var id = "r" + row + "c" + col;
 		var elem = document.getElementById("r"+row+"c"+col);
		var color = wordMask[row*COLS+col] == "0" ? "#060" : "#0c0";
		elem.style.color = color;
    	elem.innerHTML = pt(row,col);
		elem.className = "p";
		setTimeout(function() {
			unhighlight(row, col, "r", "white");
			highlightKey(row, col, false);
			document.getElementById("keyw_" + p).className = "keyw";
			// rehighlight key elements
//			var p = pt(row, col);
//			for (var i=0; i<alpha.length; i++) {
//				var al = alpha[i].toLowerCase();
//				document.getElementById("keyw_" + al).className = "keyw";
//				document.getElementById("keyw_" + al + "_c").className = "c";
//				document.getElementById("keyw_" + al + "_p").className = "keyp";
//			}

		}, ms);

	}, ms);

}
function testDecode(ms) {
	var i = 0;

	setTimeout(timer, ms);
	function timer() {
		if (i==ciphers[1].length) return;
		var row = parseInt(i/COLS);
		var col = i % COLS;
		decode(row, col, ms);
		i++;
		setTimeout(timer, ms*3);
	}
}

function goDraw(ms) {
	setTimeout(timer, ms);
  var rowPrev=0; var colPrev=0;
  var row=0; var col=0;
	function timer() {
    drawun(rowPrev, colPrev);
    if (col == COLS) {
      col = 0;
      row++;
    }
    if (row == ROWS) {
      return;
    }
    draw(row, col);
    rowPrev = row;
    colPrev = col;
    col++;

		setTimeout(timer, ms);
	}
}

function goDraw2(ms) {
	setTimeout(timer, ms);
  var rowPrev=0; var colPrev=0;
  var row=0; var col=0;
	function timer() {
    unhighlight(rowPrev, colPrev, "s", "#a3b7f9");
    unhighlight(rowPrev, 2*rowPrev+colPrev, "t", "#a3b7f9");
    if (col == COLS - row*2) {
      col = 0;
      row++;
    }
    if (row == ROWS) {
      return;
    }
    highlight(row, col, "s", "orange");
    highlight(row, 2*row+col, "t", "orange");

    document.getElementById("t"+row+"c"+(2*row+col)).innerHTML = document.getElementById("s"+row+"c"+col).innerHTML;

    rowPrev = row;
    colPrev = col;
    col++;

		setTimeout(timer, ms);
	}
}

function goDraw3(ms) {
	setTimeout(timer, ms);
  var rowPrev=1; var colPrev=0;
  var row=1; var col=0;
	function timer() {
    unhighlight(rowPrev, COLS-2*rowPrev+colPrev, "s", "#c59434");
    unhighlight(rowPrev, colPrev, "t", "#c59434");
    if (col == row*2) {
      col = 0;
      row++;
    }
    if (row == ROWS) {
      return;
    }
    highlight(row, COLS-2*row+col, "s", "orange");
    highlight(row, col, "t", "orange");

    document.getElementById("t"+row+"c"+col).innerHTML = document.getElementById("s"+row+"c"+(COLS-2*row+col)).innerHTML;

    rowPrev = row;
    colPrev = col;
    col++;

		setTimeout(timer, ms);
	}
}

function oneColor(prefix) {
  for (var r=0; r<ROWS; r++) {
    for (var c=0; c<COLS; c++) {
      var elem = document.getElementById(prefix+r+"c"+c);
      if (elem) {
        elem.style.color = "#000";
        elem.style.backgroundColor = "white";
      }
    }
  }
}

function triangles() {
  var split = COLS;
  for (var r=0; r<ROWS; r++) {
    for (var c=0; c<COLS; c++) {
        var color = c >= split ? "#c59434" : "#a3b7f9";
        var elem = document.getElementById("s"+r+"c"+c);
        if (elem) elem.style.backgroundColor = color;
    }
    split-=2;
  }
}

function triangles2() {
  var startx = 20;
  var starty = 20;
  var dx = 28;
  var dy = 36;
  document.getElementById("c1").innerHTML = "";
  var html = "";
  var pos = 0; var posStart = 0;
  for (var r=0; r<ROWS; r++) {
    for (var c=0; c<COLS; c++) {
      var split = 2*r;
      var left = "" + (startx + c*dx) + "px";
      var top = "" + (starty + r*dy) + "px";
      var bgcolor = c < split ? "#c59434" : "#a3b7f9";
      var color = "black";
      var rc = transpose(r, c);
      console.log("rc: " + rc);
      // ciphers[0][pos].toLowerCase()
      var cell = "<span id=\"t" + r + "c" + c + "\" class=\"p\" style=\"position: absolute; left: " +
        left + "; top: " + top + "; color: " + color +"; background-color: " + bgcolor + "\">&nbsp;</span>";
      html += cell;
      pos += 9;
      if (pos >= ciphers[0].length) {
        posStart++;
        pos = posStart;
      }
    }
  }
  var h = document.getElementById("cipher").innerHTML + html;
  document.getElementById("cipher").innerHTML = h;
}

function section2() {
  document.getElementById("cipher").innerHTML = "";
  drawPlaintext(1);
}

function lifeIs() {
  var startx = 550;
  var starty = 20;
  var dx = 28;
  var dy = 36;
  var bgcolor = "white";
  var color = "#900";

    var str = "lifeis";
    for (var r = 0; r < ROWS; r++) {
      for (var c = 0; c < COLS; c++) {
        var left = "" + (startx + c*dx) + "px";
        var top = "" + (starty + r*dy) + "px";
        var cell;
        if (r == 0 && c >= 11 && c <= 16) {
          cell = "<span id=\"s" + r + "c" + c + "\" class=\"p\" style=\"position: absolute; left: " +
            left + "; top: " + top + "; color: " + color +"; background-color: " + bgcolor + "\">" + str[c-11] + "</span>";
        } else {
          cell = "<span id=\"s" + r + "c" + c + "\" class=\"p\" style=\"position: absolute; left: " +
            left + "; top: " + top + "; color: #eee; background-color: " + bgcolor + "\">x</span>";
        }
        document.getElementById("cipher").innerHTML += cell;
      }
    }
}

function goDraw4(ms) {
	setTimeout(timer, ms);

  var pos = 0;
  var row1=0; var col1=0; // normal order
  var row2=0; var col2=0; // column order

  var row1Prev=0; var col1Prev=0; // normal order
  var row2Prev=0; var col2Prev=0; // column order

  var startx = 550;
  var starty = 20;
  var dx = 28;
  var dy = 36;

	function timer() {
//    unhighlight(rowPrev, COLS-2*rowPrev+colPrev, "s", "#c59434");
    //unhighlight(rowPrev, colPrev, "t", "#c59434");
    unhighlight(row1Prev, col1Prev, "r", "white");
    unhighlight(row2Prev, col2Prev, "s", "white");
    if (pos == ciphers[1].length) {
      return;
    }
    highlight(row1, col1, "r", "orange");
    highlight(row2, col2, "s", "orange");

//    var left = "" + (startx + col2*dx) + "px";
    //var top = "" + (starty + row2*dy) + "px";
    var color = wordMask[1][row1*COLS+col1] == "0" ? "#060" : "#0c0";
    var elem = document.getElementById("s"+row2+"c"+col2);
    elem.style.color = color;
    elem.innerHTML = ciphers[1][pos].toLowerCase();
//    var cell = "<span id=\"s" + row2 + "c" + col2 + "\" class=\"p\" style=\"position: absolute; left: " +
//          left + "; top: " + top + "; color: " + color + "; background-color: orange\">" + ciphers[1][pos].toLowerCase() + "</span>";
//    document.getElementById("cipher").innerHTML += cell;

    row1Prev = row1;
    col1Prev = col1;
    row2Prev = row2;
    col2Prev = col2;

    pos++;

    col1++;
    if (col1 == COLS) {
      col1 = 0;
      row1++;
    }

    row2++;
    if (row2 == ROWS) {
      row2 = 0;
      col2++;
    }
    if (row2 == 0 && col2 >= 11 && col2 <= 16) {
      row2++;
    }
		setTimeout(timer, ms);
	}
}

function highlightMistake() {
  highlight(5, 3, "t", "yellow");
}

function unhighlightMistake() {
  unhighlight(5, 3, "t", "#c59434");
  document.getElementById("t5c3").innerHTML = "&nbsp;"
}

function highlightShift1() {
  var row=5;
  for (var col=4; col<10; col++) {
    highlight(row, col, "t", "yellow");
  }
}

function highlightShift2() {
  var row=5;
  for (var col=3; col<9; col++) {
    var elem1 = document.getElementById("t"+row+"c"+col);
    var elem2 = document.getElementById("t"+row+"c"+(col+1));
    elem1.innerHTML = elem2.innerHTML;
    elem1.style.backgroundColor = "yellow";
  }
  highlight(5, 9, "t", "#a3b7f9");
  document.getElementById("t5c9").innerHTML = "&nbsp;";
}

function highlightShift3() {
  var row=5;
  for (var col=3; col<9; col++) {
    unhighlight(row, col, "t", "#c59434");
  }
}

function highlightShift4() {
  var row=5;
  for (var col=10; col<17; col++) {
    highlight(row, col, "t", "yellow");
  }
}

function highlightShift5() {
  var row=5;
  for (var col=9; col<16; col++) {
    var elem1 = document.getElementById("t"+row+"c"+col);
    var elem2 = document.getElementById("t"+row+"c"+(col+1));
    elem1.innerHTML = elem2.innerHTML;
    elem1.style.backgroundColor = "yellow";
  }
  highlight(5, 16, "t", "#a3b7f9");
  document.getElementById("t5c16").innerHTML = "&nbsp;";
}

function highlightShift6() {
  var row=5;
  for (var col=9; col<16; col++) {
    unhighlight(row, col, "t", "#a3b7f9");
  }
}

function highlightShift7() {
  highlight(5, 16, "t", "yellow");
  document.getElementById("t5c16").innerHTML = "h";
}

function highlightShift8() {
  highlight(5, 16, "t", "#a3b7f9");
}

function section3() {
  document.getElementById("cipher").innerHTML = "";
  drawPlaintext(2);
}

function highlightChunk(pos1, pos2, color) {
  for (var pos=pos1; pos<=pos2; pos++) {
    var row = parseInt(pos/COLS);
    var col = pos % COLS;
    highlight(row, col, "r", color);
  }
}

function reverseChunk(pos1, pos2) {
  var i = 0;
  for (var pos=pos1; pos<=pos2; pos++) {
    var row = parseInt(pos/COLS);
    var col = pos % COLS;
    document.getElementById("r"+row+"c"+col).innerHTML = ciphers[2][pos2-i].toLowerCase();
    i++;
  }
}

function goReverse(ms) {

  var i=0;
  var j=0;
  var chunks = [
    [0, 3], // life
    [8, 9], // be
    [10, 11], // an
    [16, 18], // one
    [19, 20], // in
    [21, 28] // paradice
  ];
	setTimeout(timer, ms);

	function timer() {
    if (i == chunks.length) return;
    var chunk = chunks[i];
    if (j == 0) highlightChunk(chunk[0], chunk[1], "orange");
    else if (j == 1) reverseChunk(chunk[0], chunk[1]);
    else if (j == 2) highlightChunk(chunk[0], chunk[1], "white");
    j++;
    if (j==3) {
      j = 0;
      i++;
    }
		setTimeout(timer, ms);
	}
}

function grid() {
  var startx = 550;
  var starty = 20;
  var dx = 28;
  var dy = 36;

  var html = "";
  for (var row=0; row<ROWS; row++) {
    for (var col=0; col<COLS; col++) {
      //var color = wordMask[0][row*COLS+col] == "0" ? "#060" : "#0c0";
      var left = "" + (startx + col*dx) + "px";
      var top = "" + (starty + row*dy) + "px";
      var cell2 = "<span id=\"s" + row + "c" + col + "\" class=\"p\" style=\"position: absolute; left: " +
        left + "; top: " + top + "; color: #eee;\">x</span>";
      html = html + cell2;
    }
  }
  document.getElementById("cipher").innerHTML += html;
}

function withSpaces(which) {
    var elem = document.getElementById("cipher");
    elem.innerHTML = "";
    var html = "<span style=\"max-width: 400px;\">";
    var pt = ciphersSpaces[which];
    var colors = ["#060","#0c0"];
    var colorIndex = 0;
    for (var i=0; i<pt.length; i++) {
        if (pt[i] == ' ') {
          colorIndex++;
          colorIndex %= 2;
        }
        var cell = "<span class=\"p\" style=\"color: " + colors[colorIndex] + "\">" + pt[i].toLowerCase() + "</span>";
        html += cell;
    }
    html += "</span>";
    elem.innerHTML = html;
}

function withSpacesAll() {
    var elem = document.getElementById("cipher");
    elem.innerHTML = "";
    var html = "<span style=\"max-width: 400px;\">";

    for (var which = 0; which < 3; which++) {
	    if (which == 2) which = 8;
	    var pt = ciphersSpaces[which];
	    var colors = ["#060","#0c0"];
	    var colorIndex = 0;
	    for (var i=0; i<pt.length; i++) {
	        if (pt[i] == ' ') {
	          colorIndex++;
	          colorIndex %= 2;
	        }
	        var cell = "<span class=\"p\" style=\"color: " + colors[colorIndex] + "\">" + pt[i].toLowerCase() + "</span>";
	        html += cell;
	    }
	    html += "<br/>";
    }
    html += "</span>";
    elem.innerHTML = html;
}


function fullPlaintext(color) {
  var elem = document.getElementById("cipher");
  elem.innerHTML = "";

  var startx = 10;
  var starty = 10;
  var dx = 28;
  var dy = 36;

  var html = "";
  for (var row=0; row<20; row++) {
    for (var col=0; col<COLS; col++) {
      //var color = wordMask[0][row*COLS+col] == "0" ? "#060" : "#0c0";
      var left = "" + (startx + col*dx) + "px";
      var top = "" + (starty + row*dy) + "px";
      var cell2 = "<span id=\"r" + row + "c" + col + "\" class=\"p2\" style=\"position: absolute; left: " +
        left + "; top: " + top + "; color: " + color +";\">" + z340PlaintextTransposed[row][col] + "</span>";
      html = html + cell2;
    }
  }
  document.getElementById("cipher").innerHTML += html;
}


function ptHighlight(letter) {
  for (var row=0; row<20; row++) {
    for (var col=0; col<COLS; col++) {
      if (z340Plaintext[row][col] == letter) {
        document.getElementById("r"+row+"c"+col).style.color = "#060";
        document.getElementById("r"+row+"c"+col).style.backgroundColor = "yellow";
      }
    }
  }
}
function ctHighlight(symbol) {
  for (var row=0; row<20; row++) {
    for (var col=0; col<COLS; col++) {
      if (z340Ciphertext[row][col] == symbol) {
        var elem = document.getElementById("r"+row+"c"+col);
        elem.className = "c";
        elem.innerHTML = symbol;
        document.getElementById("r"+row+"c"+col).style.color = "#000";
        document.getElementById("r"+row+"c"+col).style.backgroundColor = "white";
      }
    }
  }
}
function dumpKey() {
  var map = {};
  for (var row=0; row<20; row++) {
    for (var col=0; col<COLS; col++) {
      var p = z340Plaintext[row][col];
      var c = z340Ciphertext[row][col];
      if (!map[p]) map[p] = "";
      if (map[p].indexOf(c) == -1)
        map[p] += c;
    }
  }
  console.log(map);
}

function goEncode(ms) {
  /* letter frequencies:

  46 e
  39 a
  27 o
  25 t
  24 h
  23 i
  22 n
  17 r
  16 s
  12 l
  11 w
  11 c
  10 f
   9 m
   9 d
   7 y
   7 v
   7 b
   6 u
   6 p
   6 g

  */
  var key = [
    "aKlOz*",
    "bf_",
    "cp",
    "dAS6",
    "eBbcN|4",
    "fF",
    "gL",
    "h+",
    "iHkPy<",
    "ldt7",
    "m2",
    "nDY.9>",
    "oMRV^",
    "pj8",
    "rETXZ1",
    "sJU&-",
    "tG#%(:;",
    "uq/@",
    "v5",
    "wW)",
    "yC3"
  ];

  var i=0; var j=1;
  var ct = false;

  setTimeout(timer, ms);

	function timer() {
    if (i == key.length) {
      document.getElementById("k1").style.backgroundColor = "white";
      return;
    }
    var p = key[i][0];
    var c = key[i][j];
    if (ct) {
      ctHighlight(c);
      if (j > 1)
        document.getElementById("k"+(j-2)).style.backgroundColor = "white";
      document.getElementById("k"+(j-1)).style.backgroundColor = "orange";
      j++;
    } else {
      renderKey(key[i]);
      ptHighlight(p);
      ct = true;
    }
    if (j==key[i].length) {
      i++;
      j = 1;
      ct = false;
    }
		setTimeout(timer, ms);
	}
}

function renderKey(key) {
  var p = key[0];
  var cs = key.substring(1);

  var startx = 550;
  var starty = 330;
  var dx = 28;
  var dy = 36;

  var html = "";

  html += "<span id=\"kp\" class=\"p\" style=\"center; position: absolute; left: " + (startx-20) + "px; top: " + (starty) + "px; color: #090;\"><b>" + p + ":</b></span>";
  for (var i=0; i<cs.length; i++) {
    var c = cs[i];
    var left = "" + (startx + (i+1)*dx) + "px";
    var top = "" + starty + "px";
    var cell = "<span id=\"k" + i + "\" class=\"c\" style=\"position: absolute; left: " +
      left + "; top: " + top + "; color: #black;\">" + c + "</span>";
    html = html + cell;
  }
  document.getElementById("key").innerHTML = html;

}

function randomWords(ms) {
  var elem = document.getElementById("cipher");
  elem.innerHTML = "";

  var startx = 10;
  var starty = 10;
  var dx = 28;
  var dy = 36;

  var html = "";
  var color = "black";

  var pt = [];
  for (var row=0; row<9; row++) {
  	pt[row] = [];
    for (var col=0; col<COLS; col++) {
      pt[row][col] = String.fromCharCode(97+Math.floor(Math.random() * 26));
      //var color = wordMask[0][row*COLS+col] == "0" ? "#060" : "#0c0";
      var left = "" + (startx + col*dx) + "px";
      var top = "" + (starty + row*dy) + "px";
      var cell2 = "<span id=\"r" + row + "c" + col + "\" class=\"p2\" style=\"position: absolute; left: " +
        left + "; top: " + top + "; color: " + color +";\">" + pt[row][col] + "</span>";
      html = html + cell2;
    }
  }
  document.getElementById("cipher").innerHTML += html;

  var rowPrev, colPrev;
  setTimeout(timer, 5000);

	function timer() {
		// select random word
		var word = words1000[Math.floor(Math.random()*words1000.length)];
		console.log(word);
		// pick start pos randomly from [0, 152-L+1]
		var start = Math.floor(Math.random()*(152-word.length+1));

		var r = Math.floor(Math.random()*127) + 127;
		var g = Math.floor(Math.random()*127) + 127;
		var b = Math.floor(Math.random()*127) + 127;
		var rgb = "rgb(" + r + "," + g + "," + b + ")";

		for (var i=0; i<word.length; i++) {
			var pos = start + i;
			var row = Math.floor(pos / 17);
			var col = pos % 17;
			document.getElementById("r" + row + "c" + col).innerHTML = word[i];
			highlight(row, col, "r", rgb);
		}

		setTimeout(timer, ms);
	}

}

function keyCompare() {
	var keys = [];
	for (var row=0; row<z408Ciphertext.length; row++) {
		var line = z408Ciphertext[row];
		for (var col=0; col<line.length; col++) {
			var ct = line[col];
			var a = keys[ct];
			if (!a) a = [];
			a[0] = z408Plaintext[row][col].toUpperCase();
			keys[ct] = a;
		}
	}
	for (var row=0; row<z340Ciphertext.length; row++) {
		var line = z340Ciphertext[row];
		for (var col=0; col<line.length; col++) {
			var ct = line[col];
			var a = keys[ct];
			if (!a) a = [];
			a[1] = z340Plaintext[row][col].toUpperCase();
			keys[ct] = a;
		}
	}
	var sorted = "ABbCcDdEeFfGHI!:;JjKk=LlMNOP&pQqRrSTtUV^WXYyZ.<>/|\\-+)(z642315_@*%#789";
	html = "";
	html += "<table><tr><td class='kc'>" + keyCompareTable(sorted, keys, 0, 14) + "</td>";
	html += "<td class='kc'>" + keyCompareTable(sorted, keys, 14, 14) + "</td>";
	html += "<td class='kc'>" + keyCompareTable(sorted, keys, 28, 14) + "</td>";
	html += "<td class='kc'>" + keyCompareTable(sorted, keys, 42, 14) + "</td>";
	html += "<td class='kc'>" + keyCompareTable(sorted, keys, 56, 14) + "</td></tr></table>";
/*	html = "<table><tr><td></td><td class='zhead'><center>408</center></td><td class='zhead'><center>340</center></td></tr>";
	for (var i=0; i<sorted.length; i++) {
		var k = sorted[i];
		console.log("k " + k);
		html += "<tr><td><span class='c'>" + k + "</span></td>";
		html += "<td><span class='p' style='color: #090'>";
		if (!keys[k][0]) html += "&nbsp;";
		else html += keys[k][0];
		html += "</span></td><td>";
		html += "<span class='p' style='color: #090'>";
		if (!keys[k][1]) html += "&nbsp;";
		else html += keys[k][1];
		html += "</span></td>";
		html += "</tr>";
	}
	html += "</table>"*/
	document.getElementById("cipher").innerHTML = html;

}

function keyCompareTable(sorted, keys, start, count) {
	var html = "<table><tr><td></td><td class='zhead'><center>408</center></td><td class='zhead'><center>340</center></td></tr>";
	for (var i=0; i<count; i++) {
		var k = sorted[start+i];
		html += "<tr><td><span class='c'>" + k + "</span></td>";
		html += "<td><span class='p' style='color: #090'>";
		if (!keys[k][0]) html += "&nbsp;";
		else html += keys[k][0];
		html += "</span></td><td>";
		html += "<span class='p' style='color: #090'>";
		if (!keys[k][1]) html += "&nbsp;";
		else html += keys[k][1];
		html += "</span></td>";
		html += "</tr>";
	}
	html += "</table>"
	return html;
}

function pivots() {
	for (i=0; i<pivot1.length; i++) {
		pos = pivot1[i];
		row = parseInt(pos / 17);
		col = pos % 17;
		highlight(row, col,"r","red");
	}
	for (i=0; i<pivot2.length; i++) {
		pos = pivot2[i];
		row = parseInt(pos / 17);
		col = pos % 17;
		highlight(row, col,"r","cyan");
	}
}

function whichPivot(pos) {
	if (pivot1.includes(pos)) return 1;
	if (pivot2.includes(pos)) return 2;
	return 0;
}

function animateTransposePivots(color, ms) {
	pivots();
  var elem = document.getElementById("cipher2");
  elem.innerHTML = "";

  var startx = 550;
  var starty = 10;
  var dx = 28;
  var dy = 36;

  var html = "";
  for (var row=0; row<20; row++) {
    for (var col=0; col<COLS; col++) {
      //var color = wordMask[0][row*COLS+col] == "0" ? "#060" : "#0c0";
      var left = "" + (startx + col*dx) + "px";
      var top = "" + (starty + row*dy) + "px";
      var cell2 = "<span id=\"s" + row + "c" + col + "\" class=\"p2\" style=\"position: absolute; left: " +
        left + "; top: " + top + "; color: " + color +";\"></span>";
      html = html + cell2;
    }
  }
  document.getElementById("cipher2").innerHTML += html;

  var pt = "";
  for (var i=0; i<z340Plaintext.length; i++) pt += z340Plaintext[i];
  	console.log(pt);
  var pos = 0;	

  var rowPrev1 = 0;
  var colPrev1 = 0;
  var rowPrev2 = 0;
  var colPrev2 = 0;

  setTimeout(timer, ms);
		function timer() {
			highlight(rowPrev1, colPrev1, "r", colorFor(pos-1, false));
			highlight(rowPrev2, colPrev2, "s", colorFor(pos-1, false));
		  	if (pos == 340) return;
		  	var pos2 = mapPositionFromZ340[pos];
		  	var row1 = parseInt(pos/17);
		  	var col1 = pos%17;
		  	var row2 = parseInt(pos2/17);
		  	var col2 = pos2%17;
		  	document.getElementById("s"+row2+"c"+col2).innerHTML = pt[pos2];
			highlight(row1, col1, "r", colorFor(pos, true));
			highlight(row2, col2, "s", colorFor(pos, true));
			rowPrev1 = row1;
			colPrev1 = col1;
			rowPrev2 = row2;
			colPrev2 = col2;
		  	if (whichPivot(pos) == 0)
				setTimeout(timer, ms);
			else 
				setTimeout(timer, 500);
		  	pos++;
	}

}

function colorFor(pos, on) {
	var pivot = whichPivot(pos);
	if (pivot == 0) return on ? "orange" : "";
	if (pivot == 1) return "red"
	return "cyan";
}

