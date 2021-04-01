var translations = [];
translations['A'] = "A";
translations['B'] = "B";
translations['b'] = "B";
translations['C'] = "CUVN";
translations['c'] = "CUVN";
translations['D'] = "D";
translations['d'] = "D";
translations['E'] = "EMW";
translations['e'] = "EMW";
translations['F'] = "F";
translations['f'] = "F";
translations['G'] = "G";
translations['H'] = "HI";
translations['I'] = "IH";
translations['!'] = "IH";
translations[':'] = "IH";
translations[';'] = "IH";
translations['J'] = "JY";
translations['j'] = "JY";
translations['K'] = "K";
translations['k'] = "K";
translations['='] = "K";
translations['L'] = "L";
translations['l'] = "L";
translations['M'] = "MWE";
translations['N'] = "NZ";
translations['O'] = "O";
translations['P'] = "PDBQ";
translations['p'] = "PDBQ";
translations['&'] = "PDBQ";
translations['Q'] = "Q";
translations['q'] = "Q";
translations['R'] = "R";
translations['r'] = "R";
translations['S'] = "S";
translations['T'] = "T";
translations['t'] = "T";
translations['U'] = "UN";
translations['V'] = "VL";
translations['W'] = "WEM";
translations['X'] = "XT";
translations['Y'] = "YT";
translations['y'] = "YT";
translations['Z'] = "ZN";
translations['z'] = "OTZ";
translations['('] = "OI";
translations[')'] = "OI";
translations['1'] = "O";
translations['2'] = "O";
translations['3'] = "O";
translations['4'] = "O";
translations['5'] = "O";
translations['6'] = "O";
translations['_'] = "O";
translations['#'] = "O";
translations['%'] = "O";
translations['*'] = "O";
translations['@'] = "O";
translations['7'] = "AD";
translations['8'] = "AD";
translations['9'] = "AD";
translations['+'] = "TX";
translations['-'] = "I";
translations['.'] = "O";
translations['/'] = "I";
translations['<'] = "VL";
translations['>'] = "VL";
translations['\\'] =  "I";
translations['^'] = "VLA";
translations['|'] = "I";
translations['0'] = "R";
translations['['] = "RTY";
translations['?'] = "NUV";

function switchPos(row, col) {
	var pos = row*WIDTH+col;
	showWords(pos);
}

function showWords(pos) {
	var elem = document.getElementById("results");
	var html = "";
	                
	var count = 0;
	for (var i=results[pos].length-1; i>=0; i--) {
		html += "<span onmouseover=\"hw(" + pos + ",this)\" onmouseout=\"hw2(this)\">" + results[pos][i].toLowerCase() + "</span> ";
		count++;
		if (count == 100) break;
	}       
	
	elem.innerHTML = html;    
	
	cw();
	
}

function ambiguous(p, c) {
	if (translations[c] == null) return false;
	var tr = translations[c];
	for (var i=0; i<tr.length; i++) {
		if (tr[i] == c) continue;
		if (tr[i] == p) return true;
	}                               
	return false;
}

function hw(pos, elem) {
	lightenAll();
	var word = elem.innerHTML.toUpperCase();
	elem.style.color="#090";
	for (var i=0; i<word.length; i++) {
		var r = Math.trunc((pos+i)/WIDTH);
		var c = (pos+i)%WIDTH;
		darkenrc(r,c);
	}
	
	var elem = document.getElementById("word");
	
	var html = "";
	for (var i=0; i<word.length; i++) {
		if (pos+i < ciphers[0].length)
			html += getImgDarker(ciphers[0][pos+i]);
	}                       
	html += "<br>";
	for (var i=0; i<word.length; i++) {
		var cl = "letter";
		if (word[i] == ciphers[0][pos+i]) cl = "letter2";
		else if (ambiguous(word[i], ciphers[0][pos+i])) cl = "letter3";
		html += "<span class=\"" + cl + "\">" + word[i] + "</span>";
	}                       
	elem.innerHTML = html;
}
function hw2(elem) {
	lightenAll();
    elem.style.color="#000";
	cw();
}
function cw() {
 document.getElementById("word").innerHTML = "";   
}