var WIDTH=17;

/* many thanks to http://mspeight.blogspot.com/2007/05/how-to-disable-backspace-in-ie-and.html for this delete/backspace trapper */
if (typeof window.event != 'undefined') // IE
  document.onkeydown = function() // IE
    {
    var t=event.srcElement.type;
    var kc=event.keyCode;
    return ((kc != 8) || ( t == 'text') ||
             (t == 'textarea') || ( t == 'submit'))
    }
else
  document.onkeypress = function(e)  // FireFox/Others 
    {
    var t=e.target.type;
    var kc=e.keyCode;
    if ((kc != 8) || ( t == 'text') ||
        (t == 'textarea') || ( t == 'submit'))
        return true
    else {
        return false
    }
   }

	var doStats = false;
	var stats = new Array(2);
	var frequencies = new Array();
	var statsSortedKeys = new Array(2);
	var cipherLength;

	var cipherReset;

	var letterFrequencies = new Array(
	);
	letterFrequencies["A"] = 0.08167;
	letterFrequencies["B"] = 0.01492;	
	letterFrequencies["C"] = 0.02782;
	letterFrequencies["D"] = 0.04253; 	
	letterFrequencies["E"] = 0.12702; 	
	letterFrequencies["F"] = 0.02228; 	
	letterFrequencies["G"] = 0.02015; 	
	letterFrequencies["H"] = 0.06094; 	
	letterFrequencies["I"] = 0.06966; 	
	letterFrequencies["J"] = 0.00153; 	
	letterFrequencies["K"] = 0.00772; 	
	letterFrequencies["L"] = 0.04025; 	
	letterFrequencies["M"] = 0.02406; 	
	letterFrequencies["N"] = 0.06749;
	letterFrequencies["O"] = 0.07507;
	letterFrequencies["P"] = 0.01929;
	letterFrequencies["Q"] = 0.00095;
	letterFrequencies["R"] = 0.05987;
	letterFrequencies["S"] = 0.06327;
	letterFrequencies["T"] = 0.09056;
	letterFrequencies["U"] = 0.02758;
	letterFrequencies["V"] = 0.00978;
	letterFrequencies["W"] = 0.02360;
	letterFrequencies["X"] = 0.00150;
	letterFrequencies["Y"] = 0.01974;
	letterFrequencies["Z"] = 0.00074;
	
	
	var images = new Array( "alphabet/a.jpg","alphabet/b.jpg","alphabet/bb.jpg","alphabet/bc.jpg","alphabet/bd.jpg","alphabet/bf.jpg","alphabet/bj.jpg","alphabet/bk.jpg","alphabet/bl.jpg",
	"alphabet/bp.jpg","alphabet/bq.jpg","alphabet/by.jpg","alphabet/c.jpg","alphabet/caret.jpg","alphabet/d.jpg","alphabet/dash.jpg","alphabet/dot.jpg","alphabet/e.jpg",
	"alphabet/f.jpg","alphabet/g.jpg","alphabet/gt.jpg","alphabet/h.jpg","alphabet/i.jpg","alphabet/idl.jpg","alphabet/idr.jpg","alphabet/j.jpg","alphabet/k.jpg",
	"alphabet/l.jpg","alphabet/lt.jpg","alphabet/m.jpg","alphabet/n.jpg","alphabet/n1.jpg","alphabet/n2.jpg","alphabet/n3.jpg","alphabet/n4.jpg","alphabet/n5.jpg",
	"alphabet/n6.jpg","alphabet/n7.jpg","alphabet/n8.jpg","alphabet/n9.jpg","alphabet/o.jpg","alphabet/p.jpg","alphabet/perp.jpg","alphabet/pf.jpg","alphabet/phi.jpg",
	"alphabet/plus.jpg","alphabet/r.jpg","alphabet/s.jpg","alphabet/slash.jpg","alphabet/sq.jpg","alphabet/sqd.jpg","alphabet/sqe.jpg","alphabet/sql.jpg","alphabet/sqr.jpg",
	"alphabet/t.jpg","alphabet/theta.jpg","alphabet/u.jpg","alphabet/v.jpg","alphabet/w.jpg","alphabet/x.jpg","alphabet/y.jpg","alphabet/z.jpg","alphabet/zodiac.jpg");

	function preload() {
		var objImage = new Image();
		for	(i=0; i<images.length; i++)
		{
			objImage.src = images[i];
		}
	}

	var which = 0;

	var ciphers = new Array(
	"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+"
//	"NKzj4<Oc%qDb%57/(:8>V<)MWyGN^zz.O*:F_%4.KJzzRHK&ZbPY<L<K8q7jKl.|2DtzC6^zZz9-*G5D6>1HERVMD<.>y|zMYFtc4tO_f.V5z-Vc.bZkM^E6dy1FP.McO*lH|z|5Ftc&F+4f.9YzD*-.RTJp@:k)%bCRDc:WcP-D:PX8%z+cNzODGL-<pN;Z%<Pz%(NSfCzHD3EYzZ%d|^c#cR)tl+zHG2FP4N;kT_lUXb;E61MY@FW#1z|_RBz@A-5*%pP8NWPt|/kT:6SCZ(%AdR9|JR46GzBz()X%NdA|YWG*|5Fp4.-3F2zkJ|^D;dHC%#K6S12NJR:ARz+l"
	);


	var cipher;
	
	var alphabet = new Array("ABCDEFGH|JKLMNOPRSTUVWXYZ123456789plkdfycjqbtz()>^+.<-/#_@*%&;:", "ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefjklpqrtz!#%()=+/@\\^_"
	);



	var decoder = new Array();

	var getKey = false;
	var getKeyFor;
	var getKeyId;

	var newLoad = false;          
	
	var sequences;
	                  
	/** highlight the given sequence #*/
	function hSeq(seq) {
		/** unhighlight all others */
		for (var i=0; i<sequences.length; i++)
			hmatch(sequences[i], false);
		/** highlight selected sequence */
		hmatch(sequences[seq], true);	
	}

	function resetCipher() {
		cipher = new Array();
		for (var i=0; i<ciphers.length; i++) {
			var c = ciphers[i];
			cipher[i] = new Array();
			var j = 0;
			while (j<c.length) {
				cipher[i][cipher[i].length] = c.substring(j,j+WIDTH);
				j+=WIDTH;
			}
		}
		cipherLength = new Array(cipher.length);

		cipherReset = new Array();
		for (i=0; i<cipher.length; i++) {
			cipherReset[i] = cipher[i].slice();
		}
	}
	
	function d(name) {
		return document.getElementById(name);
	}
	
	function init() {
		resetCipher();
		preload();
		cipherLength[which] = 0;
		for (var i=0; i<cipher[which].length; i++) {
			for (var j=0; j<cipher[which][i].length; j++) {
				decoder[cipher[which][i].substring(j,j+1)] = "";
				cipherLength[which]++;
			}
		}
		render();

//		computeStatistics();
//		setTimeout("render()", 250);
//		renderInteresting();
	}

	function renderGridAll() {
		var d = ""; 
				var ctable = document.getElementById("cipher");
				if (ctable.childNodes[0]) ctable.removeChild(ctable.childNodes[0]);

				var newChild = document.createElement("table");
				newChild.className = "cipher";
				var tbody = document.createElement("tbody");
				var cell; var trow;

/*
				trow = document.createElement("tr");
								for (var col=0; col<=cipher[which][0].length; col++) {
									cell = document.createElement("td");
									if (col>0) cell.innerHTML = "<span class='index'>" + (col-1) + "</span>";
									trow.appendChild(cell);
								}
								tbody.appendChild(trow);
*/				
				
				for (var row=0; row<cipher[which].length; row++) {
					trow = document.createElement("tr");
					trow.setAttribute("style","height: 20px");
					
					/*
					cell = document.createElement("td");
					cell.className = "index";
					//cell.innerHTML = "<span class='index'>" + (row*cipher[which][row].length) + "</span>";
					cell.innerHTML = "<span class='index'>" + row + "</span>";
					trow.appendChild(cell);*/
					for (var col=0; col<cipher[which][row].length; col++) {
						letter = cipher[which][row].substring(col,col+1);
						id = row + "_" + col;
						d += (getDecoded(letter) == "" ? " " : getDecoded(letter));
				    cell = document.createElement( "td" );
						cell.setAttribute("id",id);
						

//						if (isPrime1(row*17+col+1)) cell.setAttribute("class","prime");
						if ((row*WIDTH+col+1) % 2 == 0) cell.setAttribute("class","even"); 
						else cell.setAttribute("class","odd"); 
						
//						cell.onmouseover = new Function("h('" + id + "')");
//						cell.onmouseout = new Function("u('" + id + "')");
//			 		  cell.setAttribute("onmouseout","u('" + id + "')");
						cell.setAttribute("onclick","tog(event, " + row + "," + col + ")");
						cell.setAttribute("title","row " + row + " col " + col + " pos " + (cipher[which][0].length * row + col));
//						cell.setAttribute("ondblclick","dbl(" + row + "," + col + ")");
//						cell.onclick = function() { var temp=new Function("g('" + letter + "','" + id + "')"); temp(); };
//						cell.onclick = new Function("g(\"" + (letter == "\\" ? "\\\\" : letter) + "\",\"" + id + "\")");
//						eval("cell.onClick = g('" + letter + "','" + id + "')");
//						cell.className = getName(letter, false);
						if (getName(letter, false)=="blank") {
//							cell.style.paddingLeft = "1px";
//						 	cell.style.paddingRight = "1px";
					  }
						else {
//							cell.style.paddingLeft = "10px";
//						 	cell.style.paddingRight = "10px";
						}
						
		//				cell.setAttribute("style",(getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : ""));
//						cell.innerHTML = "<center>"+getDecoded(letter) + "</center>&nbsp;" 
						cell.innerHTML = getImg(letter, row);
						trow.appendChild(cell);
					  //html += "<td id=\"" + id + "\" onmouseover=\"h('" + id + "')\" onmouseout=\"u('" + id + "')\" onclick=\"g('" + letter + "','" + id + "')\" class=\"" + getName(letter, false) + "\" style=\"" + (getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : "") + "\">" + getDecoded(letter) + "</td>";
						//html += "</td>";
					}


					tbody.appendChild(trow);
					//html += "</tr>";
				}
				thead = document.createElement("thead");
				tfoot = document.createElement("tfoot");
				newChild.appendChild(thead);
				newChild.appendChild(tfoot);
				newChild.appendChild(tbody);
				ctable.appendChild(newChild);
				return d;
	}

	function renderCellsFor(symbol, plaintext) {
		var cell;
		var d = "";
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				letter = cipher[which][row].substring(col,col+1);
				d += (getDecoded(letter) == "" ? " " : getDecoded(letter));
				if (letter == symbol) {
					id = row + "_" + col;
				 	cell = document.getElementById(id);
					//			cell.setAttribute("onmouseover","h('" + id + "')");
					//			cell.setAttribute("onmouseout","u('" + id + "')");
//								cell.setAttribute("onclick","g('" + letter + "','" + id + "')");
					//			cell.onClick = "g('" + letter + "','" + id + "')";
								cell.className = getName(letter, false);
								if (getName(letter, false)=="blank") {
//									cell.style.paddingLeft = "1px";
//								 	cell.style.paddingRight = "1px";
							  }
								else {
//									cell.style.paddingLeft = "10px";
//								 	cell.style.paddingRight = "10px";
								}
//					if (cell.childNodes[0]) cell.removeChild(cell.childNodes[0]);
//					cell.appendChild(document.createTextNode(getDecoded(letter)));
					cell.innerHTML = getDecoded(letter);
				}
			}
		}
		document.getElementById("s"+getName(symbol,true)).className = getName(letter, false);
		document.getElementById("s"+getName(symbol,true)).innerHTML = plaintext;
		renderCipherInfo(d);
	}
	
	function renderCipherInfo(d) {
		var decoded = "<p><u>Decoded ciphertext</u>: <b>";
		var plaintext = "";
		
		if (doStats) {
			frequencies = new Array();
			for (var i=0; i<d.length; i++) {
				decoded += d.charAt(i).toLowerCase() + " ";
				plaintext += d.charAt(i);
				if (!frequencies[d.charAt(i)]) frequencies[""+d.charAt(i)] = 0;
				frequencies[""+d.charAt(i)]++;
			}
			decoded += "</b></p>";

			var keys = new Array();
			for (x in frequencies) {
				keys[keys.length] = x;
			}
			var frequenciesSortedKeys = null;
			frequenciesSortedKeys = sortByValue(keys, frequencies);
			decoded += "<p id=\"stats2\" style=\"display:" + document.getElementById("stats").style.display + "\"><u>Letter frequencies</u>: ";
		
			decoded += "<table><tr valign=\"top\"><td style=\"border-right: thin solid #999\"><center><b>PLAINTEXT:</b></center>";
			decoded += "<table class=\"lettertable\" style=\"padding: 5px 5px 5px 5px\">";
			var max = 0; var scale;
			for (x in frequenciesSortedKeys) {
				letter = frequenciesSortedKeys[x];
				if (letter != " ") {
					if (max == 0) {
						max = frequencies[frequenciesSortedKeys[x]]; 
					}
					scale = Math.round(100*frequencies[frequenciesSortedKeys[x]]/max);
					decoded += "<tr>";
					decoded += "<td class=\"letter\">" + letter + "</td>";
					//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
					decoded += "<td nowrap=\"yes\" class=\"normal\">" + frequencies[frequenciesSortedKeys[x]] + " (" + Math.round(100*(frequencies[frequenciesSortedKeys[x]]/cipherLength[which])) + "%)</td>";
					decoded += "<td><img style=\"border-right: thick solid #009; border-left: 2px solid #ccf\" \" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				  decoded += "</tr>";
				}
			}
			decoded += "</table></td><td><center><b>EXPECTED:</b></center>";
			decoded += "<table class=\"lettertable\" style=\"padding: 5px 5px 5px 5px\">";
		
			keys = new Array();
			for (x in letterFrequencies) {
				keys[keys.length] = x;
			}
			var letterFreqSortedKeys = null;
			letterFreqSortedKeys = sortByValue(keys, letterFrequencies);
			max = 0;
			for (x in letterFreqSortedKeys) {
				letter = letterFreqSortedKeys[x];
				if (letter != " ") {
					if (max == 0) {
						max = letterFrequencies[letterFreqSortedKeys[x]]; 
					}
					scale = Math.round(100*letterFrequencies[letterFreqSortedKeys[x]]/max);
					decoded += "<tr>";
					decoded += "<td class=\"letter\">" + letter + "</td>";
					//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
					decoded += "<td nowrap=\"yes\" class=\"normal\">" + Math.round(letterFrequencies[letterFreqSortedKeys[x]]*cipherLength[which]) + " (" + Math.round(100*(letterFrequencies[letterFreqSortedKeys[x]])) + "%)</td>";
					decoded += "<td><img style=\"border-right: thick solid #009; border-left: 2px solid #ccf\" \" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				  decoded += "</tr>";
				}
			}
			decoded += "</table><a href=\"http://en.wikipedia.org/wiki/Letter_frequencies#Relative_frequencies_of_letters_in_the_English_language\">(source)</a>";
		
			decoded += "</td></tr></table>";
		}
		//for (x in frequenciesSortedKeys) if (frequenciesSortedKeys[x] != " ") decoded += "(" + frequenciesSortedKeys[x] + ", " + frequencies[frequenciesSortedKeys[x]] + ") ";


		if (doStats) {
			html = "<u>Symbol frequencies</u>: <table class=\"lettertable\">";
		
			var max = 0;
			for (x in statsSortedKeys[which]) {
				letter = statsSortedKeys[which][x];
				if (max == 0) {
					max = stats[which][statsSortedKeys[which][x]]; 
				}
				scale = Math.round(60*stats[which][statsSortedKeys[which][x]]/max);
			
			
				html += "<tr valign=\"middle\">";
				html += "<td align=\"right\"><img style=\"border-left: thick solid #009; border-right: 2px solid #ccf\" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				html += "<td align=\"middle\" id=\"s" + getName(letter, true) + "\" onclick=\"g('" + letter + "','bogus')\" class=\"" + getName(letter, false) + "\" style=\"" + (getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : "") + "\"><div class=\"letter\">" + getDecoded(letter) + "</div></td>";
				//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
				html += "<td nowrap=\"yes\" class=\"normal\">" + stats[which][statsSortedKeys[which][x]] + " (" + Math.round(100*(stats[which][statsSortedKeys[which][x]]/cipherLength[which])) + "%)</td>";
				html += "</tr>";
			}
			html += "</table>";
			document.getElementById("stats").innerHTML = html;
		}
		
		
	}
	
	/* break cipher into rows */
	function split(c) {
		var a = new Array();
		var w = cipher[which][0].length;
		for (var i=0; i<c.length/w; i++) {
			a[i] = c.substring(i*w, i*w+w);
		}
		return a;
	}
	
	function render() {
		//var html = "<table border=\"1\" class=\"cipher\">";
		var html = "";
		var id;
		var d1 = new Date();
		var d = renderGridAll();
		renderCipherInfo(d);
	}
	
	function renderInteresting() {
		var html;
		
		html = "Interesting decoders: ";
		
		for (var i=0; i<interestingHash[which].length; i++) {
			html += "<a href=\"javascript:getInteresting(" + i + ")\">" + interestingHash[which][i].name + "</a> | ";
		}
		
		var elem = document.getElementById("interesting");
		if (elem)
			elem.innerHTML = html;
		
	}

	function reset() {
		init();
	}

	function h(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thin solid gray";
	}
	function h2(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thick solid red";
	}
	
	function hall(letter) {
		for (var j=0; j<cipher[which].length; j++)
			for (var i=0; i<cipher[which][j].length; i++)
				if (cipher[which][j].charAt(i) == letter) h2(j+"_"+i);
	}
	
	function u(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thin solid white";
	}

	function getDecoded(letter) {
		if (decoder[letter]=="") return "";
		if (!decoder[letter]) return "";
		return (decoder[letter]);
	}

	function getDecoder() {
		var d = "";
		for (i=0; i<alphabet[which].length; i++) d+= (!decoder[alphabet[which][i]] || decoder[alphabet[which][i]] == "" ? "?" : decoder[alphabet[which][i]]) + " ";
		return d;
	}


	function g(letter, id) {
		getKey = true;
		getKeyFor = letter;
		document.getElementById("key").innerHTML = "Type the letter you want for <img src=\"alphabet/" + getName(letter, true) + ".jpg\">.  " + 
			"Or, <a href=\"#\" onclick=\"clearletter()\">[reset this letter]</a>.";

		var elem = document.getElementById(id);
		/*if (elem) {
			elem.style.border = "thin solid #0f0";
	  }*/
		getKeyId = id;
	}

     function chr(c) {
     var h = c . toString (16);
     h = unescape ('%'+h);
     return h;
     }

	function getName(letter, ignoreDecoder) {
		if (!ignoreDecoder && decoder[letter] != "") return "blank";
		switch (letter) {
			case "1" : return("n1");
			case "2" : return("n2");
			case "3" : return("n3");
			case "4" : return("n4");
			case "5" : return("n5");
			case "6" : return("n6");
			case "7" : return("n7");
			case "8" : return("n8");
			case "9" : return("n9");
			case "^" : return("caret");
			case "#" : return("sq");
			case "_" : return("sqe");
			case "@" : return("sqd");
			case "*" : return("sql");
			case "%" : return("sqr");
			case "(" : return("theta");
			case ")" : return("phi");
			case "z" : return("zodiac");
			case "t" : return("perp");
			case "&" : return("pf");
			case ";" : return("idl");
			case ":" : return("idr");
			case ">" : return("gt");
			case "." : return("dot");
			case "<" : return("lt");
			case "+" : return("plus");
			case "/" : return("slash");
			case "\\" : return("backslash");
			case "-" : return("dash");
			case "!" : return("funnyi");
			case "=" : return("sidek");
			case "|" : return("bar");

/*			case "a" : return("blank");
			case "g" : return("blank");
			case "h" : return("blank");
			case "i" : return("blank");
			case "m" : return("blank");
			case "n" : return("blank");
			case "o" : return("blank");
			case "s" : return("blank");
			case "t" : return("blank");
			case "u" : return("blank");
			case "v" : return("blank");
			case "w" : return("blank");
			case "x" : return("blank");
			case "z" : return("blank");*/

            case "b" : return("bb");
            case "c" : return("bc");
            case "d" : return("bd");
            case "e" : return("be");
            case "f" : return("bf");
            case "j" : return("bj");
            case "k" : return("bk");
            case "l" : return("bl");
            case "p" : return("bp");
            case "q" : return("bq");
            case "r" : return("br");
            case "y" : return("by");

			case "A" : return("a");
			case "B" : return("b");
			case "C" : return("c");
			case "D" : return("d");
			case "E" : return("e");
			case "F" : return("f");
			case "G" : return("g");
			case "H" : return("h");
			case "I" : return("i");
			case "J" : return("j");
			case "K" : return("k");
			case "L" : return("l");
			case "M" : return("m");
			case "N" : return("n");
			case "O" : return("o");
			case "P" : return("p");
			case "Q" : return("q");
			case "R" : return("r");
			case "S" : return("s");
			case "T" : return("t");
			case "U" : return("u");
			case "V" : return("v");
			case "W" : return("w");
			case "X" : return("x");
			case "Y" : return("y");
			case "Z" : return("z"); 

			// symbols unique to 13- and 32-character ciphers
			case "?" : return("omega");    
			case "0" : return("taurus");
			case "[" : return("t2");

			case " " : return("blank");
			//case "$" : return("tao");
			default : return("unknown");
		}
		//if (letter == letter.toLowerCase()) return "b" + letter.toLowerCase();
		//else return letter.toLowerCase();
	}

	function keypress(event) {
		var key = window.event ? event.keyCode : event.which;
		if (getKey) {
      //alert("key: " + key);
      if (key < 32 || key > 126)
				letter = "";
			else
				letter = chr(key).toUpperCase();

			decoder[getKeyFor] = letter;

			getKey = false;
			renderCellsFor(getKeyFor, letter);
			getKeyFor = null;
			document.getElementById("key").innerHTML = "";
			return false;
		}
	}
	
	function clearletter() {
		decoder[getKeyFor] = ""; getKey = false;
		renderCellsFor(getKeyFor, "");
		getKeyFor = null;
		document.getElementById("key").innerHTML = "";
	}

	function randomize() {
		for (var i=0; i<cipher[which].length; i++) {
			for (var j=0; j<cipher[which][i].length; j++) {
				decoder[cipher[which][i].substring(j,j+1)] = chr(Math.floor(Math.random()*26+65));
			}
		}
		render();
	}

	function getInteresting(w) {
		for (var i=0; i<interestingHash[which][w].decoder.length; i++) {
			letter = interestingHash[which][w].decoder.substring(i,i+1).toUpperCase();
			if (letter != "?") decoder[alphabet[which].substring(i,i+1)] = letter;
			else decoder[alphabet[which].substring(i,i+1)] = "";
		}
		render();
	}
	
	function switchCipher(w) {
		which = w;
		init();
	}
	
	/* highlight all occurrences of the given cipher letters */
	function highlightLetters(letters) {
		unhighlightAll();
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				id = row + "_" + col;
				letter = cipher[which][row].substring(col,col+1);
				for (var i=0; i<letters.length; i++) {
					if (letter == letters.substring(i,i+1)) {
						h(id);
						break;
					}
				}
			}
		}
	}
	
	/* clear all letter highlighting */
	function unhighlightAll() {
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				u(row+"_"+col);
			}
		}
	}
	
	/* compute frequency statistics */
	function computeStatistics() {
		if (doStats) {
			stats[0] = new Array();
			stats[1] = new Array();
			for (var i=0; i<cipher[which].length; i++) {
				for (var j=0; j<cipher[which][i].length; j++) {
					c = cipher[which][i].charAt(j);
					if (stats[which][""+c] == null) stats[which][""+c] = 0;
					stats[which][""+c]++;
				}
			}
		
			var keys = new Array();
			for (x in stats[which]) {
				keys[keys.length] = x;
			}
			statsSortedKeys[which] = sortByValue(keys, stats[which]);
		}
		
	}
	
	function sortByValue(keyArray, valueMap) {
		return keyArray.sort(function(a,b){return valueMap[a]-valueMap[b];}).reverse();
	}
	
	function toggleStats() {
		var elem = document.getElementById("stats").style;
		if (elem.display == "none") {
			elem.display = ""; 
		}
		else {
			elem.display = "none";
		}
		elem = document.getElementById("stats2").style;
		if (elem.display == "none") {
			elem.display = ""; 
		}
		else {
			elem.display = "none";
		}
	}
	
	function flipH(c) {
		var newLine;
		for (i=0; i<c.length; i++) {
			newLine = "";
			for (j=c[i].length-1; j>=0; j--) {
				newLine += c[i].charAt(j);
			}
			c[i] = newLine;
		}
		return c;
	}
	
	function flipV(c) {
		var newArray = new Array();
		for (i=c.length-1; i>=0; i--) {
			newArray[c.length-1 - i] = c[i];
		}
		c = newArray;
		return c;
	}

	function rotateCW(c) {
		var newArray = new Array();
		var newLine;
		for (i=0; i<c[0].length; i++) {
			newLine = "";
			for (j=c.length-1; j>=0; j--) {
				newLine += c[j].charAt(i);
			}
			newArray[newArray.length] = newLine;
		}
		c = newArray;
		return c;
	}
	
	function rotateCCW(c) {
		var newArray = new Array();
		var newLine;
		for (i=c[0].length-1; i>=0; i--) {
			newLine = "";
			for (j=0; j<c.length; j++) {
				newLine += c[j].charAt(i);
			}
			newArray[newArray.length] = newLine;
		}
		c = newArray;
		return c;
	}                           
	
	/* for each letter, map it to list of positions of all its appearances in the cipher. */
	function makeIndex() {
//		var s = "";
		var index = {};
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				var key = cipher[which][row].charAt(col);
//				s += "("+row+","+col+","+key+") ";
				if (index[key]) {
					index[key][index[key].length] = [row, col];
				} else {
					index[key] = [[row, col]];
				}
			}
		}
//    	alert(s);

		return index;
	}

	/** find all repeated sequences */
	function repeatsfind() {repeats(cipher[which])}
	
	function makeFrom(cipherblock) {
		var a = []; var s = "";
		for (var row=0; row<cipherblock.length; row++) {
			a[row] = "";
			for (var col=0; col<cipherblock[row].length; col++) {
				a[row] += cipherblock[row][col];
				s+= cipherblock[row][col];
			}
		}
		cipher[which] = a;
	}
	
	function repeats(cipherblock) {
		makeFrom(cipherblock);
		var index = makeIndex();
		var count1 = 0; var count2 = 0;
		var r = []; // each entry is three arrays: 1) the sequence, 2) the positions of the first sequence, 3) the positions of the second sequence.  then the two directions.
		for (var row=0; row<cipherblock.length; row++) {
			for (var col=0; col<cipherblock[row].length; col++) {
				var found = repeatsFor(index, row, col);
				if (found) {
					count1++;
					for (var i=0; i<found.length; i++) {
						if (found[i][0].length > 1) {
							count2++;
							r[r.length] = new Array(
								found[i][0],
								found[i][1],
								found[i][2],
								found[i][3], found[i][4] // the two directions.
							);
						}
					}
				}
				
			}
		}
		           
		var rNew = [];
		var hash = {};

		/* discard if:
			- sequence length is less than 3
			- sequence is palindromic and matches itself
			- duplicate			
		 */
		for (var i=0; i<r.length; i++) {
			
			/* sequence length is less than 2 */
			var len = r[i][0].length;
			if (len < 2) continue;

			/* check for palindrome.  the two sets of positions will be equal. */
			var check = new Array();
			var count = 0;
			for (var j=0; j<r[i][1].length; j++) {
				var key = ""+r[i][1][j];
				if (!check[key]) {
					count++;
					check[key] = true;
				}
				key = ""+r[i][2][j];
				if (!check[key]) {
					count++;
					check[key] = true;
				}
			}
			if (count == len) continue;
			
			/* is this a dupe? */
			var union = r[i][1].concat(r[i][2]); // combine the two sets positions
			union.sort(); // sort so dupes will look the same in a hash
			var s = "";
			for (var j=0; j<union.length; j++) s+= union[j]+";";
			if (hash[s]) continue;			
			hash[s] = true;
			rNew[rNew.length] = new Array( r[i][0], r[i][1], r[i][2], r[i][3], r[i][4]);
		}
		
		sequences = rNew;
		    
		var lengths = {};
		if (sequences.length == 0) { html += "None found"; return; }
		for (var i=0; i<sequences.length; i++) { // make distinct set of all lengths
			lengths[rNew[i][0].length] = true;
		}
		var l2 = [];
		for (var l in lengths) l2[l2.length] = l;
		l2.sort(function(a,b) { if (a<b) return -1; if (a>=b) return 1; else return 0});
		    
		var html = "Repeated sequences.  Click to highlight.  ";
		
		for (var i=l2.length-1; i>=0; i--) {
			html += "<b>Length: " + l2[i] + "</b>: "
			for (var j=0; j<sequences.length; j++) {
				if (rNew[j][0].length == l2[i]) {
					html += "<a href=\"javascript:hSeq(" + j + ")\">" + rNew[j][0] + "</a> | ";
				}
			}
		}

		html += "<br>Number of sequences by direction: <table>";
		for (var i=0; i<8; i++) {
			html += "<tr valign='top'>";
			html += "<td>";
			if (i==0) html += "right";
			else if (i==1) html += "right-down";
			else if (i==2) html += "down";
			else if (i==3) html += "left-down";
			else if (i==4) html += "left";
			else if (i==5) html += "left-up";
			else if (i==6) html += "up";
			else if (i==7) html += "right-up";
			else html += "???";
			html += "</td><td>";
			html += "</td></tr>";
		}
		html += "</table>";


		document.getElementById("seq").innerHTML = html;
	}

	/** find all repeated sequences for strings starting at [row, col] */
	function repeatsFor(index, row, col) {
		var r = new Array();
		
		var directions = new Array(
			new Array(0, 1), // right
			new Array(1, 1), // right-down
			new Array(1, 0), // down
			new Array(1, -1), // left-down
			new Array(0, -1), // left
			new Array(-1, -1), // left-up
			new Array(-1, 0), // up
			new Array(-1, 1) // right-up
		);
		
		/* inspect each direction */
		var key = get(row, col);
		var candidates = index[key];
		if (candidates) {
			for (var c=0; c<candidates.length; c++) {
				for (var i=0; i<directions.length; i++) {
					for (var j=0; j<directions.length; j++) {
						var result = new Array();
						result[0] = "";
						result[1] = new Array();
						result[2] = new Array();       

						if (row == candidates[c][0] && col == candidates[c][1] && i==j ) {
							; // do nothing, because we don't want to match the sequence at (row,col) with itself. 
						} else {
							matches(result, row, col, directions[i][0], directions[i][1], candidates[c][0], candidates[c][1], directions[j][0], directions[j][1]);
							r[r.length] = new Array(result[0], result[1], result[2], i, j);
						}
					}
				}
			}
		}     

		                            
		return r;
	}               
	
	/** look for string match for sequences beginning at [r0,c0] and [r1,c1].  direction of sequences is determined by 
	    (dr0, dc0) and (dr1, dc1).  "result" is an array that tracks the maximum matched sequence and occurrence positions. */
	function matches(result, r0, c0, dr0, dc0, r1, c1, dr1, dc1) {
		var ch1 = get(r0, c0);
		var ch2 = get(r1, c1);
//		alert("r0 " + r0 + " c0 " + c0 + " dr0 " + dr0 + " dc0 " + dc0 + " r1 " + r1 + " c1 " + c1 + " dr1 " + dr1 + " dc1 "+ dc1 + " ch1 " + ch1 + " ch2 " + ch2);
		
		if (ch1 == ch2) {
			result[0] += ch1;
			result[1][result[1].length] = new Array(getR(r0), getC(c0));
			result[2][result[2].length] = new Array(getR(r1), getC(c1));
			matches(result, (r0+dr0), (c0+dc0), dr0, dc0, (r1+dr1), (c1+dc1), dr1, dc1);
		}
	}          
	
	function ngrams(line, n) {
		var map = {};
		var ngram; var count;
		for (var i=0; i<line.length-n+1; i++) {
			ngram = line.substring(i,i+n);
			count = map[ngram];
			if (count) count++;
			else count=1;
			map[ngram] = count;
		}
		return map;
	}
	
	// transposed ngrams
	function ngramstrans(line, n) {
		var map1 = {}; var map2 = {};
		var ngram; var val; var list;
		
		var positions = {}; var pos;
		var ignore;
		
		for (var i=0; i<line.length-n+1; i++) {
			ngram = line.substring(i,i+n);
			ngramSorted = sortByChar(ngram);
			
			// do not count overlapping matches.  For example, the string ABCA can be split into two 3-grams: ABC and BCA, whose transpositions match each other.
			ignore=false;
			if (positions[ngramSorted]) {
				pos = positions[ngramSorted];
				for (var j=0; j<pos.length; j++) {
					if (i-pos[j] < n) {
						ignore = true;
						break;
					}
				}
			} else pos = [];
			
			if (ignore) continue;
			
			pos[pos.length] = i;
			positions[ngramSorted] = pos;
			
			// first map tracks the count of total occurrences
			if (!map1[ngramSorted]) val = 0;
			else val = map1[ngramSorted];
			val++;
			map1[ngramSorted] = val;
			
			// second map tracks the string sequences
			if (!map2[ngramSorted]) list = {};
			else list = map2[ngramSorted];
			list[ngram] = true;
			map2[ngramSorted] = list;
			
		}
		
		// remove all strictly non-transposed sequences, and remove anything with count of 1.
		var found = false;
		for (var key1 in map2) {
			if (map1[key1] <= 1) {
				map1[key1] = false;
				map2[key1] = false;
				continue;
			}
			var u = {};
			var count = 0;
			for (var key2 in map2[key1]) {
				if (!u[key2]) {
					count++;
					u[key2] = true;
				}
				if (count > 1) break;
			}
			if (count == 1) {
				map1[key1] = false;
				map2[key1] = false;
			} else {
				found = true;
			}
		}
		
		var r = [];
		r.push(map1);
		r.push(map2);
		r.push(found);
		return r;
	}
	
	function sortByChar(s) {
		var r = [];
		for (var i=0; i<s.length; i++) r.push(s.charAt(i));
		r.sort();
		var sorted = "";
		for (var i=0; i<r.length; i++) sorted += r[i];
		return sorted;
	}
	
	function dumpNGrams(all) {
		var line = "";
		for (var i=0; i<cipher[which].length; i++) line += cipher[which][i];
		var html = "";
		
		var go = true;
		var n = 1; var repeats = 0; var uniqueRepeats=0;
		while (go) {
			go=false;
			var map = ngrams(line, n);
			
			html += "<h3>" + n + "-grams:</h3>";
			var keys = [];
			for (var key in map) keys[keys.length] = key;
			var sorted = sortByValue(keys, map);
			
			var result1 = "";
			var result2 = "";
			for (var i=0; i<sorted.length; i++) {
				count = map[sorted[i]];
				if (count > 1) go = true;
				if (all || count > 1) {
					result1 += getImg(sorted[i]) + " (" + count + ") ";
					result2 += sorted[i] + " (" + count + ") ";
				}
				if (count > 1) {
					repeats += count;
					uniqueRepeats++;
				}
			}
			html += result1 + "<br><br>ASCII version:<br>" + result2
		
			html += "<br><br><u>Total repeated " + n + "-grams</u>: <b>" + repeats + " (" + uniqueRepeats + " unique " + n + "-grams.)</b>";
			if (!go) html += "<br>No more repeats found."
			html += "<hr>"
			n++;
		}
		document.getElementById("ngrams").innerHTML = html;
		window.location="#ngrams";
	}
	
	function dumpAlphabetStats() {
		var html = "";
		var html2 = "<pre>";
		var counts = [];
		var symbols = [];
		for (var i=0; i<cipher.length; i++) {
			counts[i] = [];
			for (var row=0; row<cipher[i].length; row++) {
				for (var col=0; col<cipher[i][row].length; col++) {
					var symbol = cipher[i][row].charAt(col);
					symbols[symbol] = symbol;
					if (counts[i][symbol]) counts[i][symbol]++; else counts[i][symbol] = 1;
				}
			}
		}
		
		html += "<table border=1><tr><th>Symbol</th><th>ASCII</th><th>408 count</th><th>340 count</th><th>Total</th><th>Difference</th><th>408 Plaintext</th></tr>";

		var sorted = [];
		for (var symbol in symbols) sorted[sorted.length] = symbol;
		sorted.sort();
		
		var c3 =0; var c4=0;
		for (var i=0; i<sorted.length; i++) {
			var symbol = sorted[i];
			var count3 = (counts[0][symbol] ? counts[0][symbol] : 0);
			var count4 = (counts[1][symbol] ? counts[1][symbol] : 0);
			var decoded = (count4 > 0 ? decode408For(symbol) : "");
			html += "<tr>";
			html += "<td><img src=\"alphabet/" + getName(symbol, true) + ".jpg\"></td>";
			html += "<td>" + symbol + "</td>";
			c3+=count3;
			c4+=count4;
			html += "<td>" + count4 + "</td>";
			html += "<td>" + count3 + "</td>";
			html += "<td>" + (count4+count3) + "</td>";
			html += "<td>" + Math.abs(count4-count3) + "</td>";
			html += "<td>" + decoded + "</td>"
			html += "</tr>";
			
			var bg = (count3 > 0 && count4 > 0) ? "#ccc" :
				(count3 > 0 ? "#ccff99" : "#99ccff");
			
			html2 += "|-valign=\"top\" style=\"background-color: " + bg + "\"\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| [[File:" + getName(symbol, true) + ".jpg]]\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| &lt;tt>" + symbol + "&lt;/tt>\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + count3 + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + count4 + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + (count4+count3) + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + Math.abs(count4-count3) + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + decoded + "\n";
			
		}
//		alert(c3); alert(c4);
		html += "</tr></table>";
		
		
		document.getElementById("seq").innerHTML = html;// + html2;
	}
	
	/** get image corresponding to given string of cipher letters */
	function getImg(s, row) {
		var result = "";
		for (var i=0; i<s.length; i++) {
			var g = "";
			//if (row % 2 == 0) g = "green/lighter/"
			result += "<img src=\"alphabet2/" + g + getName(s.substring(i,i+1)) + ".jpg\">";
		}
		return result;
	}
	/** get image corresponding to given string of cipher letters */
	function getImgDarker(s) {
		var result = "";
		for (var i=0; i<s.length; i++) {
			result += "<img src=\"alphabet/darker/" + getName(s.substring(i,i+1)) + ".jpg\">";
		}
		return result;
	}
	
	/** get cipher character at position (r, c).  translate out of bounds values to within bounds values. */
	function get(r, c) {
		
		r=getR(r);
		c=getC(c);
		
		return cipher[which][r].substring(c,c+1);
	}     
	
	function getR(r) {
		r = r % cipher[which].length;
		if (r < 0) r = cipher[which].length + r;
		return r;
	}
	
	function getC(c) {
		c = c % cipher[which][0].length;
		if (c < 0) c = cipher[which][0].length + c;
		return c;
	}
	
	/** highlight/unhighlight the given matches */
	function hmatch(matches, highlight) {
		for (var j=1; j<3; j++) {
			for (var i=0; i<matches[j].length; i++) {
				var id = getR(matches[j][i][0])+"_"+getC(matches[j][i][1]);
//				alert(id);
				if (highlight) h2(id); else u(id);
			}
		}
	}

	// generate list of all homophones for each unique plaintext letter in the 408 solution
	function homophonesFor408() {
		var d = interestingHash[1][0]["decoder"];
		var u = {};
		for (i=0;i<alphabet[1].length; i++) {
			var symbol = alphabet[1][i];
			var plaintext = d[i];
			if (u[plaintext]) u[plaintext].push(symbol);
			else u[plaintext] = [symbol];
		}
		return u;
	}
	
	function decode408For(ch) {
		var d = interestingHash[1][0]["decoder"];
		var i;
		for (i=0;i<alphabet[1].length; i++) if (alphabet[1].charAt(i) == ch) break;
		if (d.charAt(i)) return d.charAt(i).toUpperCase();
		return "";
	}
	
	function decode408(str) {
		var s = "";
		for (var i=0; i<str.length; i++) s+=decode408For(str.charAt(i));
		return s;
	}	

	/* hide the given symbol */
	function hide(row, col) {
		document.getElementById(row+"_"+col).style.visibility="hidden";
	}
	/* show the given symbol */
	function show(row, col) {
		document.getElementById(row+"_"+col).style.visibility="";
	}

	/* darken the symbols at the given row,col */
	function darkenrc(row, col) {
		var e = document.getElementById(row+"_"+col);
		if (e == null) return;
		e.className = "dimmed";
		e.innerHTML = getImgDarker(cipher[which][row].charAt(col));
	}
	/* darken the symbols at the given row,col */
	function darkenrc2(row, col) {
		var e = document.getElementById(row+"_"+col);
		if (e == null) return;
		e.innerHTML = getImgDarker(cipher[which][row].charAt(col));
	}
	/* lighten the symbols at the given row,col */
	function lightenrc(row, col) {
		var e = document.getElementById(row+"_"+col);
		if (e == null) return;
		e.className = "";
		e.innerHTML = getImg(cipher[which][row].charAt(col), row);
	}
	
	function darkenpos(pos) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		darkenrc(parseInt(pos/W), pos%W);
	}
	function darkenposarray(a) {
		for (var i=0; i<a.length; i++) {
			var H=cipher[which].length;
			var W=cipher[which][0].length;
			darkenrc(parseInt(a[i]/W), a[i]%W);
		}
	}
	
	// assign a random color to the given positions
	function randcolor(a) {
		var r = Math.floor(Math.random()*192) + 64;
		var g = Math.floor(Math.random()*192) + 64;
		var b = Math.floor(Math.random()*192) + 64;
		for (var i=0; i<a.length; i++)
			rgb(a[i], r, g, b);
	}
	
	function rgb(pos, r, g, b) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		var row = parseInt(pos/W);
		var col = pos%W;

		darkenrc2(row,col);
		var elem = document.getElementById(row+"_"+col);
		if (elem) {
				elem.style.backgroundColor="rgb("+r+","+g+","+b+")";
				elem.childNodes[0].style.opacity="0.25";
				//elem.style.paddingBottom="2px";
		}
	}
	function hsl(pos, h, s, l) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		var row = parseInt(pos/W);
		var col = pos%W;
		darkenrc2(row,col);
		var elem = document.getElementById(row+"_"+col);
		if (elem) {
			    var rgb = hslToRgb(h,s,l);
				var rgbStr = "rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] +")";
				//elem.style.backgroundColor="hsl("+h+","+s+"%,"+l+"%)";
				elem.style.backgroundColor=rgbStr;
				elem.childNodes[0].style.opacity="0.25"
				elem.childNodes[0].style.width="100%"
				elem.childNodes[0].style.filter="alpha(opacity=25)"
				
				//elem.style.paddingBottom="2px";
		}
	}
	
	function hsl_clear(pos) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		var row = parseInt(pos/W);
		var col = pos%W;
		lightenrc(row,col);
		var elem = document.getElementById(row+"_"+col);
		if (elem) {
				elem.style.backgroundColor="";
				elem.childNodes[0].style.opacity="";
				//elem.style.paddingBottom="2px";
		}
	}
	function hsl_highlighted(pos) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		var row = parseInt(pos/W);
		var col = pos%W;
		var elem = document.getElementById(row+"_"+col);
		return elem.style.backgroundColor.indexOf("rgb") > -1;
	}
	
	function hsl_reset() {
		for (var pos=0; pos<ciphers[which].length;pos++) {
			if (hsl_highlighted(pos))
				hsl_clear(pos);
		}
	}
	
	
	// from http://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
	// assumes hue [0, 360), saturation [0, 100), lightness [0, 100)
	// takes array of arrays of positions as input
	function hsl_random(a) {
		var num_colors = a.length;
		var colors = [];
		for(var i = 0; i < 360; i += 360 / num_colors) {
		    var c = [];
			c[0] = i; // hue
		    c[1] = 90 + Math.random() * 10; // sat
		    c[2] = 50 + Math.random() * 10; // light
		    colors[colors.length] = c;
		}
		
		for (var i=0; i<a.length; i++) {
			var pos = a[i];
			var color = colors[i];
			for (var j=0; j<pos.length; j++) {
				var p = pos[j];
				hsl(p, color[0], color[1], color[2]);
			}
		}
	}


	function hslToRgb(h, s, l){
		h = h / 360;
		s = s / 100;
		l = l / 100;
	    var r, g, b;

	    if(s == 0){
	        r = g = b = l; // achromatic
	    }else{
	        var hue2rgb = function hue2rgb(p, q, t){
	            if(t < 0) t += 1;
	            if(t > 1) t -= 1;
	            if(t < 1/6) return p + (q - p) * 6 * t;
	            if(t < 1/2) return q;
	            if(t < 2/3) return p + (q - p) * (2/3 - t) * 6;
	            return p;
	        }

	        var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
	        var p = 2 * l - q;
	        r = hue2rgb(p, q, h + 1/3);
	        g = hue2rgb(p, q, h);
	        b = hue2rgb(p, q, h - 1/3);
	    }

	    return [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];
	}	
	
	function lightenpos(pos) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		lightenrc(parseInt(pos/W), pos%W);
	}
	/* darken all the symbols specified by the given array */
	function darken(rc) {
		var maxrow = -1;
		var minrow = 100000;
		var maxcol = -1;
		var mincol = 100000;
		for (var i=0; i<rc.length; i++) {
			var r = rc[i][0];
			var c = rc[i][1];
			maxrow = Math.max(maxrow, r);
			minrow = Math.min(minrow, r);
			maxcol = Math.max(maxcol, c);
			mincol = Math.min(mincol, c);
			darkenrc(r,c);
		}
		if (maxrow >= minrow && maxcol >= mincol) {
			for (var r=0; r<minrow-1; r++) {
				for (var c=0; c<cipher[which][r].length; c++) {
					hide(r, c);
				}
			}
			for (var r=(minrow==0 ? 0 : minrow-1); r<=maxrow+1 && r<cipher[which].length; r++) {
				for (var c=0; c<mincol-1; c++) {
					hide(r,c);
				}
				for (var c=maxcol+2; c<cipher[which][r].length; c++) {
					hide(r,c);
				}
			}
			for (var r=maxrow+2; r<cipher[which].length; r++) {
				for (var c=0; c<cipher[which][r].length; c++) {
					hide(r, c);
				}
			}
		}
	}
	/* highlight the path in the grid, then output the symbols, word, and the needed rotations/mirrors to make the word clearer */
	function showWord(rc, word) {
//		render();
//		darken(rc);
		var c=cipher[which];
		var html = "<table class=\"show\">";
		var row1 = "<tr style=\"visibility:visible\">";
		for (var i=0; i<rc.length; i++) {
			row1 += "<td>"+getImgDarker(c[rc[i][0]].charAt(rc[i][1])) + "</td>";
		}
		row1 += "</tr>"
		var row2 = "<tr style=\"visibility:visible\">";
		for (var i=0; i<rc.length; i++) {
			row2 += "<td><img src=\"" + translate(c[rc[i][0]].charAt(rc[i][1]), word.charAt(i)) + "\"></td>";
		}
		row2 += "</tr>"
		
		if (row1 == row2) {
			row1 = row1.replace("visible","hidden");
		}
		html += row1 + row2;
		
		
		html += "<tr>"
		for (var i=0; i<word.length; i++) {
			html += "<td>" + word.charAt(i) + "</td>";
		}
		html += "</tr>"
		html += "</table>";
		document.getElementById("word").innerHTML = html;
	}
	/* return the image that demonstrates the given symbol-to-interpretation mapping. */
	
	var translations = [];
	translations["!H"] = "funnyi-h";
	translations["#O"] = "sqe";
	translations["%O"] = "sqe";
	translations["&B"] = "pf-b";
	translations["&D"] = "pf-d";
	translations["&P"] = "pf";
	translations["&Q"] = "pf-q";
	translations[")I"] = "theta";
	translations["*O"] = "sqe";
	translations["+X"] = "x";
	translations["-I"] = "bar";
	translations[".O"] = "o";
	translations["/I"] = "bar";
	translations["0R"] = "r";
	translations["7A"] = "a";
	translations["7D"] = "n9-d";
	translations["8A"] = "a";
	translations["8D"] = "n9-d";
	translations["9A"] = "a";
	translations["9D"] = "n9-d";
	translations[":H"] = "idr-h";
	translations[";H"] = "idl-h";
	translations["<L"] = "lt-l";
	translations["<V"] = "v";
	translations["=K"] = "k";
	translations[">L"] = "lt-l";
	translations[">V"] = "v";
	translations["@O"] = "sqe";
	translations["CN"] = "c-n";
	translations["CU"] = "c-u";
	translations["CV"] = "c-v";
	translations["EM"] = "e-m";
	translations["EW"] = "e-w";
	translations["HI"] = "h-i";
	translations["IH"] = "i-h";
	translations["JY"] = "j-y";
	translations["ME"] = "m-e";
	translations["MW"] = "m-w";
	translations["NZ"] = "n-z";
	translations["PB"] = "p-b";
	translations["PD"] = "p-d";
	translations["PQ"] = "p-q";
	translations["UN"] = "u-n";
	translations["VL"] = "lt-l";
	translations["WE"] = "w-e";
	translations["WM"] = "w-m";
	translations["XT"] = "plus";
	translations["YT"] = "y-t";
	translations["ZN"] = "z-n";
	translations["\\I"] = "bar";
	translations["^L"] = "lt-l";
	translations["^A"] = "a";
	translations["^V"] = "v";
	translations["_O"] = "sqe";
	translations["bB"] = "b";
	translations["cC"] = "c";
	translations["cN"] = "c-n";
	translations["cU"] = "c-u";
	translations["cV"] = "c-v";
	translations["dD"] = "d";
	translations["eE"] = "e";
	translations["eM"] = "e-m";
	translations["eW"] = "e-w";
	translations["fF"] = "f";
	translations["jJ"] = "j";
	translations["jY"] = "j-y";
	translations["kK"] = "k";
	translations["lL"] = "l";
	translations["pB"] = "p-b";
	translations["pD"] = "p-d";
	translations["pP"] = "p";
	translations["pQ"] = "p-q";
	translations["qQ"] = "q";
	translations["rR"] = "r";
	translations["tT"] = "t";
	translations["yT"] = "y-t";
	translations["yY"] = "y";
	translations["zO"] = "o";
	translations["zT"] = "plus";
	translations["zZ"] = "z";
	
	function translate(symbol, plaintext) {
		var val = translations[symbol + plaintext];
		var tr;
		if (val == null) tr = getName(symbol);
		else tr = val;
		return "alphabet/darker/" + tr + ".jpg";
	}

function tog(event, row, col) {
	if(event.shiftKey) {
		dbl(row, col);
		return;
	}
	
	var elem = document.getElementById(row+"_"+col);
	var src = elem.childNodes[0].src;
	if (src.indexOf("darker") > -1) {
		lightenrc(row, col);
	} else darkenrc(row, col);
}	

function dbl(row, col) {
	var elem = document.getElementById(row+"_"+col);
	var src = elem.childNodes[0].src;
	var lighten = false;
	if (src.indexOf("darker") > -1) lighten = true;
	
	var ch = cipher[which][row][col];
	for (var r = 0; r<cipher[which].length; r++) {
		for (var c = 0; c<cipher[which][r].length; c++) {
			if (cipher[which][r][c] == ch)
				if (lighten) lightenrc(r,c);
				else darkenrc(r,c);
			
		}
	}
}	

function isPrime1(n) {
 if (isNaN(n) || !isFinite(n) || n%1 || n<2) return false; 
 var m=Math.sqrt(n);
 for (var i=2;i<=m;i++) if (n%i==0) return false;
 return true;
}
	
	
