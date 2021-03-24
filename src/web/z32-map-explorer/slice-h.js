/* horizontal slices 

we are dividing the cipher text block into up to two pieces: [p1], and [p2]
[p1] and [p2] are separated by a line defined between [row1-1] and [row1].
	0 <= [row1] < H

[p1] consists of these rows from the cipher block: (0 ... row1-1).  (total of H1 = [row1] rows)
[p2] consists of these rows from the cipher block: (row1 ... H-1)  (total of H2 = [H-row1] rows)

select an operation from:  {none, flip, mirror, flip+mirror, delete}
perform selected operation on [p2].

select a placement from:  {bottom, right, up, left}
	this reduces to: select one of these options: {insert block, interleave}
	where insertions can happen before, after, or within the cipher block (need to enumerate all possible insertions)
	and interleaving can happen before, after, or within the cipher block (need to enumerate all possible interleavings)

select operation from: {concat, insert block, interleave}

if insert block, select [row2], where 0 <= [row2] <= [H1].  

if interleave, select [row3], where 0 <= [row3] <= [H] 

let block = {r0, r1, ... r8}  H = 9, row1 = 4, row2 = 7
let p1 = {r0, r1, r2, r3}  H1 = 4
let p2 = {r4, r5, r6}  H2 = 3
let p3 = {r7, r8}  H3 = 2

select p2

delete: 
	new = {r0, r1, r2, r3}

insert block:
	
	[row3] = 0: {r4, r5, r6,   r0, r1, r2, r3}
	[row3] = 1: {r0,   r4, r5, r6,    r1, r2, r3}
	[row3] = 2: {r0. r1,   r4, r5, r6,   r2, r3}
	[row3] = 3: {r0. r1, r2,   r4, r5, r6,   r3}
	[row3] = 4: {r0. r1, r2, r3,  r4, r5, r6}
	
interleave: 

	[row4] = -3: {r4, r5, r6, r0, r1, r2, r3}
	[row4] = -2: {r4, r5, r0, r6, r1, r2, r3}
	[row4] = -1: {r4, r0, r5, r1, r6, r2, r3}
	[row4] = 0: {r0, r4, r1, r5, r2, r6, r3}
	[row4] = 1: {r0, r1, r4, r2, r5, r3, r6}
	[row4] = 2: {r0, r1, r2, r4, r3, r5, r6}
	[row4] = 3: {r0, r1, r2, r3, r4, r5, r6}

*/

function piecesFor(c, row) {
	var p = [[],[]]; var index;
	for (var i=0; i<c.length; i++) {
		index = i < row ? 0 : 1;
		p[index].push(c[i]);
	}
	return p;
}

function sliceFlip(c) {
	var a = [];
	for (var i=0; i<c.length; i++) {
		var s = "";
		for (var j=c[i].length-1; j>=0; j--) s += c[i].charAt(j);
		a.push(s);
	}
	return a;
}

function sliceMirror(c) {
	var a = [];
	for (var i=c.length-1; i>=0; i--) a.push(c[i]);
	return a;
}

function sliceOperation(c, op) {
	if (op == 0) return c; // none
	if (op == 1) return sliceFlip(c); // flip horizontal
	if (op == 2) return sliceMirror(c); // flip vertical (mirror)
	if (op == 3) return sliceMirror(sliceFlip(c)); // flip and mirror
	return null;
}

function slice(c, row1, op1, op2, row2) {
	var p = piecesFor(c, row1);
	var p1 = p[0];
	var p2 = p[1];
	
	// 1st operation
	p2 = sliceOperation(p2, op1);
	if (p2 == null) return p1;

	// 2nd operation
	if (op2 == 0) return p1.concat(p2);
	if (op2 == 1) return insertBlock(p1, p2, row2);
	return interleaveBlock(p1, p2, row2);
}

function insertBlock(c1, c2, row) {
	if (row == 0) return c2.concat(c1);
	if (row >= c1.length) return c1.concat(c2);
	var a = [];
	for (var i=0; i<row; i++) a.push(c1[i]);
	for (var i=0; i<c2.length; i++) a.push(c2[i]);
	for (var i=row; i<c1.length; i++) a.push(c1[i]);
	return a;
}

function interleaveBlock(c1, c2, row) {
	
	var a = []; var n= 0-c2.length+row;
	var index1 = n;
	var index2 = 0;
	
	if (n > 0) {
		for (var i=0; i<n; i++) a.push(c1[i]);
	}
	
	for (var i=0; i<2*(c1.length+c2.length); i++) {
		if (i % 2 == 0) {
			if (index1 < c1.length && index1 >= 0) {
				a.push(c1[index1]);
			}
			index1++;
		} else {
			if (index2 < c2.length && index2 >= 0) {
				a.push(c2[index2]);
			}
			index2++;
		}
	}
	return a;
}


function testslices_h() {
	var d1 = new Date();
	var s = slices_h();
	var d2 = new Date();
	scoredisplay(s, d1, d2);
}

function testfour_sections() {
	var d1 = new Date();
	var s = four_sections();
	var d2 = new Date();
	scoredisplay(s, d1, d2);
}

function testfour_sections_two_layouts_run() {
	var d1 = new Date();
	var s = four_sections_two_layouts_run();
	var d2 = new Date();
	scoredisplay(s, d1, d2);
}




function slices_h() {
	var ciphers = [];
	
	var scores = []; // item 0: array of quadrant parameters.  item 1: array of ngram scores.  item 2: array of homophone scores.
	
	// rot 0 f N
	ciphers[ciphers.length] = $("input").value; // current ciphertext is the original
	
	// rot 0 f H
	fh(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();
	
	// rot 0 f V
	fv(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();

	// rot 90 f N
	r90cw(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();

	// rot 90 f H
	r90cw(); fh(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();

	// rot 90 f V
	r90cw(); fv(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();


	var d = new Date();
	
	
	var num = 0;
	var H = block.length;
	var W = block[0].length;
	
	var ops1 = [0,1,2,3,4]; // none, flip, mirror, flip+mirror, delete
	var ops2 = [0,1] // insert block, interleave
	
	var newcipher, line;
	var scores = [];

	for (var cip = 0; cip<ciphers.length; cip++) {
		$("input").value = ciphers[cip]; update();
		var cb = cipherblock;
		for (var row1=0; row1<H; row1++) {

			var H1 = row1;

			for (var op1=0; op1<ops1.length; op1++) {
				if (op1 == 4) {  // delete operation
					newcipher = slice(cb, row1, op1)
					line = toLine(newcipher); 
					scores[num] = [];
					scores[num][0] = [cip,row1,op1,0,0];
					scores[num][1] = scorengrams(line);
					scores[num][2] = scorehomophones(line);
					num++;
				} else {
					for (var op2=0; op2<ops2.length; op2++) { // insert or interleave
						if (op2 == 0) { // concat
							line = toLine(slice(cb, row1, op1, op2, 0));
							scores[num] = [];
							scores[num][0] = [cip,row1,op1,op2,0];
							scores[num][1] = scorengrams(line);
							scores[num][2] = scorehomophones(line);
							num++;
						} else if (op2 == 1) { // insert
							for (var row2=0; row2<=H1; row2++) {
								newcipher = slice(cb, row1, op1, op2, row2)
								line = toLine(newcipher); 
								scores[num] = [];
								scores[num][0] = [cip,row1,op1,op2,row2];
								scores[num][1] = scorengrams(line);
								scores[num][2] = scorehomophones(line);
								num++;
							}
						} else { // interleave
							for (var row3=0; row3<H; row3++) {
								newcipher = slice(cb, row1, op1, op2, row3)
								line = toLine(newcipher); 
								scores[num] = [];
								scores[num][0] = [cip,row1,op1,op2,row3];
								scores[num][1] = scorengrams(line);
								scores[num][2] = scorehomophones(line);
								num++;
							}
						}
					}
				}
			}
		}
	}
	return scores;
}

function toLine(c) {
	var line = ""; for (var i=0; i<c.length; i++) line += c[i];
	return line;
	
}

function four_sections() {
	var ciphers = [];
	
	var scores = []; // item 0: array of section parameters.  item 1: array of ngram scores.  item 2: array of homophone scores.
	
	// rot 0 f N
	ciphers[ciphers.length] = $("input").value; // current ciphertext is the original
	
	// rot 0 f H
	fh(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();
	
	// rot 0 f V
	fv(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();

	var d = new Date();

	var permutations = [[0, 1, 2, 3], [0, 1, 3, 2], [0, 2, 1, 3], [0, 2, 3, 1], [0, 3, 1, 2], [0, 3, 2, 1], [1, 0, 2, 3], [1, 0, 3, 2], [1, 2, 0, 3], [1, 2, 3, 0], [1, 3, 0, 2], [1, 3, 2, 0], [2, 0, 1, 3], [2, 0, 3, 1], [2, 1, 0, 3], [2, 1, 3, 0], [2, 3, 0, 1], [2, 3, 1, 0], [3, 0, 1, 2], [3, 0, 2, 1], [3, 1, 0, 2], [3, 1, 2, 0], [3, 2, 0, 1], [3, 2, 1, 0]];
	
	var num = 0;
	
	var c;
	for (var cip=0; cip<ciphers.length; cip++) {
		$("input").value = ciphers[cip]; update();
		
		var slices = [[],[],[],[]];
		for (var i=0; i<block.length; i++) {
			if (i<5) slices[0].push(cipherblock[i]);
			else if (i<10) slices[1].push(cipherblock[i]);
			else if (i<15) slices[2].push(cipherblock[i]);
			else slices[3].push(cipherblock[i]);
		}
		
		for (var i=0; i<permutations.length; i++) {
			var newslices = [];
			for (var op0=0; op0<4; op0++) {
				newslices[0] = sliceOperation(slices[permutations[i][0]], op0);
				for (var op1=0; op1<4; op1++) {
					newslices[1] = sliceOperation(slices[permutations[i][1]], op1);
					for (var op2=0; op2<4; op2++) {
						newslices[2] = sliceOperation(slices[permutations[i][2]], op2);
						for (var op3=0; op3<4; op3++) {
							newslices[3] = sliceOperation(slices[permutations[i][3]], op3);
							c = toLine(newslices[0].concat(newslices[1].concat(newslices[2].concat(newslices[3]))))
							scores[num] = [];
							scores[num][0] = [cip,i,op0,op1,op2,op3];
							scores[num][1] = scorengrams(c);
							scores[num][2] = scorehomophones(c);
							num++;
						}
					}
				}
			}
		}
	}
	
	return scores;
	
}

/* another four-sections test:

there are 24 permutations of the four 5-row segments.
select one of these permutations

let p1, p2, p3, and p4 be the segments from this permutation.

select p1

	select an operation from {none, flip, mirror, flip+mirror}

	perform operation on p1

	new cipher is p1 with height 5

select p2

	select an operation from {none, flip, mirror, flip+mirror}

	perform operation on p2

	select a placement from {top, right (row=0), bottom, left (row=0)}.  top and bottom are concatenations.  right and left can be done by interleaving p1 and p2.

	new cipher c has height 10

select p3

	select an operation from {none, flip, mirror, flip+mirror}

	perform operation on p3

	select a placement from {top, right (row=0 or row=5), bottom, left (row=0 or row=5)}.  top and bottom are concatenations.  right and left can be done by interleaving c and p3.
	
	new cipher c has height 15

select p4

	select an operation from {none, flip, mirror, flip+mirror}

	perform operation on p4

	select a placement from {top, right (row=0 or row=5 or row=10), bottom, left (row=0 or row=5 or row=10)}.  top and bottom are concatenations.  right and left can be done by interleaving c and p4.
	
	new cipher c has height 20
	
3*24*4*4*4*4*6*4*7 = 3096576 possibilities = 36 days at 1 test per second.  ouch.

maybe we can limit this to specific layouts.  original cipher (each letter represents 5 rows of cipher text):
	A
	B	===>    AB      or       ABCD
	C           CD
	D

so, let's try this:

select a permutation.
for each piece, select an operation and apply it to that piece.
create layout1 based on the four pieces.  layout1 is a square (top half of square: AB (5 rows, 34 columns).  bottom half: CD (5 rows, 34 columns))
create layout2 based on the four pieces.  layout2 is a "line" (ABCD.  5 rows, 68 columns.)

*/

// perform the following operations on the given 5-row pieces.  
//then stick them together in the given format (0=square, 1=long horizontal rectangle)
function four_sections_two_layouts(pieces, operations, layout) {
	var newpieces = [];
	for (var op=0; op<operations.length; op++) { // perform operation of pieces before assembling into layout
		newpieces[op] = sliceOperation(pieces[op], operations[op]);
	}
	if (layout == 0) return layout1(newpieces);
	return layout2(newpieces);
}

function layout1(pieces) {
	var newlayout = [];
	for (var row=0; row<5; row++) {
		newlayout[row] = pieces[0][row] + pieces[1][row];
	}
	for (var row=0; row<5; row++) {
		newlayout[row+5] = pieces[2][row] + pieces[3][row];
	}
	return newlayout;
}

function layout2(pieces) {
	var newlayout = [];
	for (var row=0; row<5; row++) {
		newlayout[row] = pieces[0][row] + pieces[1][row] + pieces[2][row] + pieces[3][row];
	}
	return newlayout;
	
}

function four_sections_two_layouts_run() {
	var ciphers = [];
	
	var scores = []; // item 0: array of section parameters.  item 1: array of ngram scores.  item 2: array of homophone scores.
	
	// rot 0 f N
	ciphers[ciphers.length] = $("input").value; // current ciphertext is the original
	
	// rot 0 f H
	fh(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();
	
	// rot 0 f V
	fv(); ciphers[ciphers.length] = $("input").value;
	$("input").value = ciphers[0]; update();

	var d = new Date();
	
	var operations = [0,1,2,3];

	var permutations = [[0, 1, 2, 3], [0, 1, 3, 2], [0, 2, 1, 3], [0, 2, 3, 1], [0, 3, 1, 2], [0, 3, 2, 1], [1, 0, 2, 3], [1, 0, 3, 2], [1, 2, 0, 3], [1, 2, 3, 0], [1, 3, 0, 2], [1, 3, 2, 0], [2, 0, 1, 3], [2, 0, 3, 1], [2, 1, 0, 3], [2, 1, 3, 0], [2, 3, 0, 1], [2, 3, 1, 0], [3, 0, 1, 2], [3, 0, 2, 1], [3, 1, 0, 2], [3, 1, 2, 0], [3, 2, 0, 1], [3, 2, 1, 0]];
	
	var num = 0;
	
	var c;
	for (var cip=0; cip<ciphers.length; cip++) { // 3*24*4*4*4*4*2 = 36864 (10.24 hours)
		$("input").value = ciphers[cip]; update();
		
		var slices = [[],[],[],[]];
		for (var i=0; i<block.length; i++) {
			if (i<5) slices[0].push(cipherblock[i]);
			else if (i<10) slices[1].push(cipherblock[i]);
			else if (i<15) slices[2].push(cipherblock[i]);
			else slices[3].push(cipherblock[i]);
		}
		
		for (var i=0; i<permutations.length; i++) {
			var newslices = [];
			for (var j=0; j<4; j++) newslices[j] = slices[permutations[i][j]]; // re-arrange slices based on selected permutation
			
			// permute all possible operations on individual slices
			for (var j=0; j<operations.length; j++) {
				for (var k=0; k<operations.length; k++) {
					for (var l=0; l<operations.length; l++) {
						for (var m=0; m<operations.length; m++) {
							var ops = [operations[j],operations[k],operations[l],operations[m]];
							for (var n=0; n<2; n++) { // the two possible layouts
								c = toLine(four_sections_two_layouts(newslices, ops, n));
								scores[num] = [];
								scores[num][0] = [cip,i,j,k,l,m,n];
								scores[num][1] = scorengrams(c);
								scores[num][2] = scorehomophones(c);
								num++;
							}
						}
					}
				}
			}
		}
	}

/*	var total = 0;  var hashm = 0;  var best;
	for (var key in hash) {
		if (hash[key] > hashm) {
			hashm = hash[key];
			best = key;
		}
		total++;
	}
	alert(total); alert(num); alert(best); alert(hashm);*/
	return scores;
	
	
}