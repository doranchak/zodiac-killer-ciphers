var alignresults; var aligntexts;

var hightemp;

function aligncompute() {
	var a1tmp = $("align1").value.split("\n");
	var a2tmp = $("align2").value.split("\n");
	
	var a1 = []; var a2 = [];
	for (var i=0; i<a1tmp.length; i++) if (a1tmp[i]=="") continue; else a1[a1.length] = a1tmp[i];
	for (var i=0; i<a2tmp.length; i++) if (a2tmp[i]=="") continue; else a2[a2.length] = a2tmp[i];
	
	var a2width = 0; var result = []; var allresults = [];
	for (var row=0; row<a2.length; row++) a2width = Math.max(a2width, a2[row].length);

	aligntexts = [];
	for (var row=0; row<a2.length; row++) { // offset row
		allresults[row] = [];
		aligntexts[row] = [];
		for (var col=0; col<a2[row].length; col++) { // offset col
			// loop through first cipher, compare to second
			//shit += "<br>" + row + "," + col + "<br>";
			aligntexts[row][col] = [];
			result = [];
			for (var r1=0; r1<a1.length; r1++) {
				result[r1] = [];
				aligntexts[row][col][r1] = [];
				for (var c1=0; c1<a1[r1].length; c1++) {
					result[r1][c1] = false;
					var ch1 = a1[r1][c1];
					//shit += alignRowFor(r1,row,a2.length) + ", " + alignColFor(c1,col,a2width) + "; ";
					if (!a2[alignRowFor(r1,row,a2.length)]) continue;
					if (!a2[alignRowFor(r1,row,a2.length)][alignColFor(c1,col,a2width)]) continue;
					var ch2 = a2[alignRowFor(r1,row,a2.length)][alignColFor(c1,col,a2width)];
					aligntexts[row][col][r1][c1] = ch2;
					if (ch1 == ch2) result[r1][c1] = true;
				}
			}
			allresults[row][col] = result;
		}
	}
	
	
	
	alignresults = allresults;
	var html = "<table><tr valign=\"top\"><td><table class=\"align\"><tr><th colspan=\"" + (allresults[0].length + 1) + "\">Column Offset</th></tr><tr><th></th><th></th>";
	for (var col=0; col<allresults[0].length; col++) html += "<th>" + col + "</th>";
	html += "</tr><tr><th rowspan=\"" + (allresults.length + 1) + "\">Row Offset</th></tr>" ;
	

	var max = 0; var a = [];
	for (var row=0; row<allresults.length; row++) {
		a[row] = [];
		for (var col=0; col<allresults[row].length; col++) {
			a[row][col] = 0;
			if (!allresults[row][col]) continue;
			var count = 0;
			for (var r=0; r<allresults[row][col].length; r++) {
				for (var c=0; c<allresults[row][col][r].length; c++) {
					count += allresults[row][col][r][c] ? 1 : 0;
				}
			}
			a[row][col] = count;
			max = Math.max(max, count);
		}
		html += "</tr>";
	}
	for (var row=0; row<allresults.length; row++) {
		html += "<tr><th>" + row + "</th>";
		for (var col=0; col<allresults[row].length; col++) {
			var score = a[row][col]/max;
			var c = 255-Math.round(score*255/1.5);
			var color="rgb("+c+","+c+","+c+")";
			html += "<td style=\"background-color: " + color + "\">";
/*			if (!allresults[row][col]) continue;
			var count = 0;
			for (var r=0; r<allresults[row][col].length; r++) {
				for (var c=0; c<allresults[row][col][r].length; c++) {
					count += allresults[row][col][r][c] ? 1 : 0;
				}
			}
			if (count > 0) {*/
				html += "<a href=\"javascript:alignshow(" + row + "," + col + ")\">" + a[row][col] + "</a>";
//			}
			html += "</td>";
		}
		html += "</tr>";
	}
	html += "</table></td><td nowrap=\"yes\"><span id=\"as1\"></span></td><td nowrap=\"yes\"><span id=\"as2\"></span></td></tr></table>";
	
	$("align").innerHTML = html;
	return allresults;
}

function alignshow(row, col) {
	hightemp = "";
	var a1tmp = $("align1").value.split("\n");
	var a1 = [];
	for (var i=0; i<a1tmp.length; i++) if (a1tmp[i]=="") continue; else a1[a1.length] = a1tmp[i];

	var h1 = "<span style=\"margin: 1em 1em 1em 1em;\"><b><u>First Ciphertext</u></b><br><span class=\"fw2\">";
	var h2 = "<span style=\"margin: 1em 1em 1em 1em;\"><b><u>Second Ciphertext</u></b><br><span class=\"fw2\">";

	for (var r=0; r<alignresults[row][col].length; r++) {
		h1 += "<br>";
		h2 += "<br>";
		for (var c=0; c<alignresults[row][col][r].length; c++) {
			var ch1 = encodestr(a1[r][c]);
			var ch2 = aligntexts[row][col][r][c] ? encodestr(aligntexts[row][col][r][c]) : "&nbsp;"
			if (alignresults[row][col][r][c]) {
				hightemp += "hcellrgb(" + r + "," + c + ",null,r,g,b); ";
				h1 += "<span class='fw3'>" + ch1 + "</span>";
				h2 += "<span class='fw3'>" + ch2 + "</span>";
			} else {
				h1 += ch1;
				h2 += ch2;
			}
		}
	}

	h1 += "</span></span>";
	h2 += "</span><br><center><b><u>row offset [" + row + "]<br>column offset [" + col + "]</u></b></center></span>";
	
	$("as1").innerHTML = h1;
	$("as2").innerHTML = h2;
	
}

function alignRowFor(r, offset, height) {
	var row = r+offset;
	if (row < 0) row += height;
	row %= height;
	return row;
}

function alignColFor(c, offset, width) {
	var col = c+offset;
	if (col < 0) col += width;
	col %= width;
	return col;
}

function alignload() {
	$("align1").value = unescape($("avail").options[1].value);
	$("align2").value = unescape($("avail").options[5].value);
}

function alignswap() {
	var tmp = $("align1").value;
	$("align1").value = $("align2").value;
	$("align2").value = tmp;
	
}
