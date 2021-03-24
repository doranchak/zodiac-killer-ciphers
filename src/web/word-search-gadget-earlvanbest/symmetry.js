// find all matching positions with respect to the given vertical axis at column (row) [k] .  if [between] is false,
// the axis is defined at column (row) [k].  if [between] is true, the axis is defined as between column (row) [k-1] and [k].
// [symtype]:  if 0, then look for reflected mirrored positions.  if 1, then look for positional repetitions (kind of like copy and paste).
// "off-board" positions wrap around to the other side of the cipher block.  if 2, then look for "jazzerman" style repetitions 
// (see http://www.zodiackillerfacts.com/forum/viewtopic.php?p=22662#p22662)
// [type]: if 0, then k is a column (horizontal symmetry), otherwise k is a row (vertical symmetry).

var symhash; var symhashloc;
function sym(type, k, between, symtype) {
	// horizontal symmetry:
	// between = false
	// cipher width: W columns  [0, W-1]
	// column k
	// number of columns on each side: N=floor((W-1)/2).
	//		left set:  [k-N,k-1]
	//      right set: [k+1, K+N]


	// horizontal symmetry:
	// between = true
	// cipher width: W columns  [0, W-1]
	// column k
	// number of columns on each side: N=floor(W/2).
	//		left set:  [k-N,k-1]
	//      right set: [k, K+N-1]
	
	var result = [];
	var W = block[0].length; var H = block.length;
	
	symhash = {};
	symhashloc = {};

	if (type == 0) {
		var N = between ? parseInt(W/2) : parseInt((W-1)/2);

		var k1; // left or top side 
		if (symtype == 0) k1 = k-1; 
		else if (symtype == 1) k1 = k-N;
		
		var k2 = between ? k : k+1; // right or bottom side
		
		var ak1, ak2; // adjusted for wrapping
		
		for (var i=0; i<N; i++) {
			for (var row=0; row<H; row++) {
				ak1 = (k1+W)%W;
				ak2 = k2%W;
				
				var key = symkeyfor(block[row][ak1],block[row][ak2]);
				if (symhash[key]) symhash[key]++;
				else symhash[key] = 1;
				
				if (!symhashloc[key]) symhashloc[key] = [];
				symhashloc[key].push([[row,ak1],[row,ak2]]);
				
				if (block[row][ak1] == block[row][ak2]) {
					result.push([[row,ak1],[row,ak2]]);
				}
			}
			if (symtype == 1) k1++;
			else k1--;
			k2++;
		}
	} else if (type == 1) {
		var N = between ? parseInt(H/2) : parseInt((H-1)/2);

		var k1; // left or top side 
		if (symtype == 0) k1 = k-1; 
		else if (symtype == 1) k1 = k-N;

		var k2 = between ? k : k+1; // right or bottom side
		
		var ak1, ak2; // adjusted for wrapping
		
		for (var i=0; i<N; i++) {
			for (var col=0; col<W; col++) {
				ak1 = (k1+H)%H;
				ak2 = k2%H;

				var key = symkeyfor(block[ak1][col],block[ak2][col]);
				if (symhash[key]) symhash[key]++;
				else symhash[key] = 1;

				if (!symhashloc[key]) symhashloc[key] = [];
				symhashloc[key].push([[ak1,col],[ak2,col]]);
				
				if (block[ak1][col] == block[ak2][col]) {
					result.push([[ak1,col],[ak2,col]]);
				}
			}
			if (symtype == 1) k1++;
			else k1--;
			k2++;
		}
	} 
	
	for (var key in symhash) if (symhash[key] < 2) { delete symhash[key]; delete symhashloc[key]; }
	return result;
	
}

function symkeyfor(a,b) {
	return a+b;
}

function symmetry() {
	var html = "";

	html += "<table><tr><th>Type</th><th>Row/Col</th><th>Between?</th><th>#Matches</th><th>Symbols</th><th>Jazzerman pairs</th></tr>";
	var a;
	// horizontal mirrored symmetries
	for (var col=0; col<block[0].length; col++) {
		a = sym(0, col, false, 0);
		if (a.length > 0) 
			html += "<tr><td>Horizontal mirror</td><td>" + col + "</td><td>No</td><td>" + a.length + "</td><td>" + encodematches(a) + "</td><td>" + encodejp() + "</td></tr>";
		a = sym(0, col, true, 0);
		if (a.length > 0)
			html += "<tr><td>Horizontal mirror</td><td>" + col + "</td><td>Yes</td><td>" + a.length + "</td><td>" + encodematches(a) + "</td><td>" + encodejp() + "</td></tr>";
			
	}
	// vertical mirrored symmetries
	for (var row=0; row<block.length; row++) {
		a = sym(1, row, false, 0);
		if (a.length > 0)
			html += "<tr><td>Vertical mirror</td><td>" + row + "</td><td>No</td><td>" + a.length + "</td><td>" + encodematches(a) + "</td><td>" + encodejp() + "</td></tr>";
		a = sym(1, row, true, 0);
		if (a.length > 0)
			html += "<tr><td>Vertical mirror</td><td>" + row + "</td><td>Yes</td><td>" + a.length + "</td><td>" + encodematches(a) + "</td><td>" + encodejp() + "</td></tr>";
			
	}
	// the copy symmetries aren't quite right because they don't account for varying distances between copies
	// horizontal copy symmetries
	for (var col=0; col<block[0].length; col++) {
		a = sym(0, col, false, 1);
		if (a.length > 0)
			html += "<tr><td>Horizontal copy</td><td>" + col + "</td><td>No</td><td>" + a.length + "</td><td>" + encodematches(a) + "</td><td>" + encodejp() + "</td></tr>";
		a = sym(0, col, true, 1);
		if (a.length > 0)
			html += "<tr><td>Horizontal copy</td><td>" + col + "</td><td>Yes</td><td>" + a.length + "</td><td>" + encodematches(a) + "</td><td>" + encodejp() + "</td></tr>";
			
	}
	// vertical copy symmetries
	for (var row=0; row<block.length; row++) {
		a = sym(1, row, false, 1);
		if (a.length > 0)
			html += "<tr><td>Vertical copy</td><td>" + row + "</td><td>No</td><td>" + a.length + "</td><td>" + encodematches(a) + "</td><td>" + encodejp() + "</td></tr>";
		a = sym(1, row, true, 1);
		if (a.length > 0)
			html += "<tr><td>Vertical copy</td><td>" + row + "</td><td>Yes</td><td>" + a.length + "</td><td>" + encodematches(a) + "</td><td>" + encodejp() + "</td></tr>";
			
	}
	
	
	html += "</table>";
	$("symmetry").innerHTML = html;
}

function encodematches(matches) {
	var h = "<a class=\"fw\" href=\"#\" onclick=\"";
	for (var i=0; i<matches.length; i++) {
		for (var j=0; j<matches[i].length; j++) {
			h += "hcell(" + matches[i][j][0]+","+matches[i][j][1]+"); ";
		}
	}
	h += "\">";
	
	for (var i=0; i<matches.length; i++) {
		h += encodestr(block[matches[i][0][0]][matches[i][0][1]]);
	}
	h += "</a>";
	return h;
	
}

function encodejp() {
	var h = "<a class=\"fw\" href=\"#\" onclick=\"";
	var enc = "";
	
	for (var key in symhash) {
		
		var r = Math.floor(Math.random()*192) + 64;
		var g = Math.floor(Math.random()*192) + 64;
		var b = Math.floor(Math.random()*192) + 64;
//		hcellrgb(a[i][0], a[i][1], true, r, g, b);
		
		
		enc += encodestr(key) + " ";
		var a = symhashloc[key];  // each element is a pair
		for (var i=0; i<a.length; i++) {
			for (var j=0; j<a[i].length; j++) {
				h+="hcellrgb("+a[i][j][0]+","+a[i][j][1]+", true, " + r + ", " + g + ", " + b + "); ";
			}
		}
	}
	h += "\">";
	h += enc + "</a>";
	return h;
}

// what about offsets and rotations?