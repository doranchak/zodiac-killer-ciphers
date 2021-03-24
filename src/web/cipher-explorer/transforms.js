// convert 1D position in the one-line ciphertext to 2D position in the block ciphertext
function fromLine(pos) {
	return [rowFromLine(pos), colFromLine(pos)];
}
function rowFromLine(pos) {
	return Math.floor(pos/block[0].length);
}
function colFromLine(pos) {
	return pos % block[0].length;
}

// convert (row,col) position in block to position in 1D ciphertext 
function posFromBlock(row, col) {
	return row * block[0].length + col;
}

// split into 4 rectangular regions that intersect at a point between (i-1,j-1) and (i,j).
// if border is true, then ignore row i and col j (treat them as part of a border) 
// row and col are in terms of the new layout.  i and j refer to a point on the original layout.
// permutation is an array specifying the order in which the quadrants are rearranged.
function tr_quadrant(row, col, i, j, border, permutation) {
	if (row < 0 || col < 0) return [row, col];
	
	var H = block.length; var W = block[0].length;  // height and width in terms of original layout
	var pos; // linear position in cipher text, with respect to new layout
	
	var offset = border ? 1 : 0;
	
	var q = []; // quadrant array:  [UL corner, LR corner, height, width, linear start point]
	q[0] = [[0,0],               [i-1,j-1]];
	q[1] = [[0,j+offset],        [i-1,W-1]];
	q[2] = [[i+offset,0],        [H-1,j-1]];
	q[3] = [[i+offset,j+offset], [H-1,W-1]];
	
	// compute the remaining attributes of the quadrants (height, width, linear start point)
	var start = 0;
	for (var i=0; i<q.length; i++) {
		q[i][2] = q[i][1][0] - q[i][0][0] + 1; // height
		q[i][3] = q[i][1][1] - q[i][0][1] + 1; // width
		if (i==0) q[i][4] = 0;
		else q[i][4] = start;
		start += q[i][2] * q[i][3];
	}
	
	// map the permutations to new quadrants
	start = 0;
	var qp = [];
	for (var i=0; i<permutation.length; i++) {
		qp[i] = [];
		qp[i][0] = q[permutation[i]][0]; // UL corner
		qp[i][1] = q[permutation[i]][1]; // LR corner
		qp[i][2] = q[permutation[i]][2]; // height
		qp[i][3] = q[permutation[i]][3]; // width
		// linear position now depends on the permutation of quadrants
		if (i==0) qp[i][4] = 0;
		else qp[i][4] = start;
		start += qp[i][2] * qp[i][3];
	}
	
	/* without border, quadrants corners (UL and LR) are:  (W = original block width; H = original block height)
			upper left:  (0, 0) - (i-1, j-1)	dimensions: i x j
				new linear positions: [0, i*j-1]
			upper right: (0, j) - (i-1, W-1)	dimensions: i x (W-j)
				new linear positions: [i*j, i*W-1]
			lower left:  (i, 0) - (H-1, j-1)	dimensions: (H-i) x j
				new linear positions: [i*W, i*W+(H-i)*j - 1]
			lower right: (i, j) - (H-1, W-1)	dimensions: (H-i) x (W-j)
				new linear positions: [i*W+(H-i)*j, H*W-1]
				
	   with border, quadrants corners (UL and LR) are:  (W = block width; H = block height)
			upper left:  (0, 0) - (i-1, j-1)	dimensions: i x j
				new linear positions: [0, i*j-1]
			upper right: (0, j+1) - (i-1, W-1)	dimensions: i x (W-j-1)
				new linear positions: [i*j, i*W-i-1]
			lower left:  (i+1, 0) - (H-1, j-1)	dimensions: (H-i-1) x j
				new linear positions: [i*W-i, i*W-i+(H-i-1)*j-1]
			lower right: (i+1, j+1) - (H-1, W-1)	dimensions: (H-i-1) x (W-j-1)
				new linear positions: [i*W-i+(H-i-1)*j, H*W-H-W]
				
				
				HER> pl^VPk|1LTG2d
				Np+B (#O%DWY.<*Kf)
				By:c M+UZGW()L#zHJ

				Spp7 ^l8*V3pO++RK2
				_9M+ ztjd|5FP+&4k/
				p8R^ FlO-*dCkF>2D(
				#5+K q%;2UcXGV.zL|
				(G2J fj#O+_NYz+@L9
				d<M+ b+ZR2FBcyA64K
				-zlU V+^J+Op7<FBy-
				U+R/ 5tE|DYBpbTMKO
				2<cl RJ|*5T4M.+&BF
				z69S y#+N|5FBc(;8R
				lGFN ^f524b.cV4t++
				yBX1 *:49CE>VUZ5-+
				|c.3 zBK(Op^.fMqG2
				RcT+ L16C<+FlWB|)L
				++)W CzWcPOSHT/()p
				|Fkd W<7tB_YOB*-Cc
				>MDH NpkSzZO8A|K;+
			 	
				HER>  l^VPk|1LTG2d		
				Np+B  #O%DWY.<*Kf)
				By:c  +UZGW()L#zHJ
				
				_9M+  tjd|5FP+&4k/
				p8R^  lO-*dCkF>2D(
				#5+K  %;2UcXGV.zL|
				(G2J  j#O+_NYz+@L9
				d<M+  +ZR2FBcyA64K
				-zlU  +^J+Op7<FBy-
				U+R/  tE|DYBpbTMKO
				2<cl  J|*5T4M.+&BF
				z69S  #+N|5FBc(;8R
				lGFN  f524b.cV4t++
				yBX1  :49CE>VUZ5-+
				|c.3  BK(Op^.fMqG2
				RcT+  16C<+FlWB|)L
				++)W  zWcPOSHT/()p
				|Fkd  <7tB_YOB*-Cc
				>MDH  pkSzZO8A|K;+
				
				
				
				
				
	*/
	
	/* with border, quadrants corners (UL and LR) are:  (W = block width; H = block height)
			upper left:  (0, 0) - (i-1, j-1)
			upper right: (0, j+1) - (i-1, W-1)
			lower left:  (i+1, 0) - (H-1, j-1)
			lower right: (i+1, j+1) - (H-1, W-1)
	*/
	
	if (border) {
		pos = row*(W-1)+col; 
	} else {
		pos = posFromBlock(row, col);
	} 
	
	
	/* 
		the new layout's position pos lies in one of four possible quadrants.
		each quadrant's ciphertext apears in the new layout in sequence.  q1, q2, q3, then q4, in a linear fashion.
		q1 consists of h1 x w1 characters.  q2 consists of h2 x w2 characters, etc.
		W = w1 + w2 = w3 + w4
		H = h1 + h3 = h2 + h4
		pos is in q1 if it lies in range [0, h1 x w1 - 1]
		pos is in q2 if it lies in range [h1 x w1, h1 x w1 + h2 x w2 - 1]
		pos is in q3 if it lies in range [h1 x w1 + h2 x w2, h1 x w1 + h2 x w2 + h3 x w3 - 1]
		pos is in q4 if it lies in range [h1 x w1 + h2 x w2 + h3 x w3, h1 x w1 + h2 x w2 + h3 x w3 + h4 x w4 - 1]
		
		a permutation creates a new arrangement of quadrants, q'1, q'2, q'3, q'4.
		q'1 maps to one of { q1, q2, q3, q4 }
		q'2 maps to one of { q1, q2, q3, q4 }
		etc
		
		the default layout has an unchanged permutation, q'1 = q1, q'2 = q2, q'3 = q3, q'4 = q4.
		pos is in q'1 if it lies in range [0, h'1 x w'1 - 1]
		pos is in q'2 if it lies in range [h'1 x w'1, h'1 x w'1 + h'2 x w'2 - 1]
		etc
		
		h'i and w'i are determined by the assignment to the original quadrant.
		for example, if q'2 => q4, then h'2 = h4 and w'2 = w4
		let q'1 => q3.  then, h'1 = h3 and w'1 = w3.
		and pos is in q'2 if it lies in range [h'1 x w'1, h'1 x w'1 + h4 x w4 - 1]
		thus pos is in q'2 if it lies in range [h3 x w3, h3 x w3 + h4 x w4 - 1]

		if pos is in q'2 then select the appropriate symbol from the original layout.
		let qrow = the corresponding row of the original quadrant
		let qcol = the corresponding col of the original quadrant
		newpos = pos - n'1 where n'1 is the total number of symbols in q'1
		qrow = floor(newpos / w'2)
		qcol = newpos modulo w'2
		The original quadrant for q'2 is q4.
		Let row, col = the row, col of the original ciphertext corresponding to the correct symbol position
		row = ULrow4 + qrow
		col = ULcol4 + qcol

		if pos is in q'3 then select the appropriate symbol from the original layout.
		let qrow = the corresponding row of the original quadrant
		let qcol = the corresponding col of the original quadrant
		newpos = pos - n'1 - n'2 where n'1 is the total number of symbols in q'1 and n'2 is the total number of symbosl in q'2
		qrow = floor(newpos / w'2)
		qcol = newpos modulo w'2
		The original quadrant for q'2 is q4.
		Let row, col = the row, col of the original ciphertext corresponding to the correct symbol position
		row = ULrow4 + qrow
		col = ULcol4 + qcol
	*/
	
	var newpos; var n = 0; var qrow, ULrow, qcol, ULcol;
	for (var i=0; i<qp.length; i++) {
		if ((i!=qp.length-1 && pos < qp[i+1][4]) || i==q.length-1) {
			newpos = pos - n;
			qrow = Math.floor(newpos / qp[i][3]);
			qcol = newpos % qp[i][3];
			ULrow = qp[i][0][0]; ULcol = qp[i][0][1];
			break;
		}
		n += qp[i][2]*qp[i][3]; // running total of the number of symbols in each quadrant
	}
	
	return [ULrow + qrow, ULcol + qcol];
/*	
	
	var p, h, w;

	var offset = border ? 1 : 0;
	
	if (pos < i*j) { // Q1
		p = pos; h = i; w = j;
//		return [0,0];
		return [Math.floor(p/w), p % w];
	}
	if (pos < i*W - offset*i) {  // Q2
		p = pos-i*j; h = i; w = W-j-offset;
//		return [0,0];
		return [Math.floor(p/w), j + offset + p % w];
	} 
	if (pos < i*W-offset*i+(H-i-offset)*j) { // Q3
		p = pos-i*W+offset*i; h = H-i-offset; w = j;
//		return [0,0];
		return [i + offset + Math.floor(p/w), p % w];
	} 
	p = pos-(i*W-offset*i+(H-i-offset)*j);  //  Q4
	h = H-i-offset; w = W-j-offset;
	return [i + offset + Math.floor(p/w), j + offset + p % w];*/
}

// reverse the entire cipher text
function tr_reverse(row, col) {
	if (row < 0 || col < 0) return [row, col];
	return [block.length-row-1, block[0].length-col-1];
}

function tr_flip_horizontal(row, col) {
	if (row < 0 || col < 0) return [row, col];
	return [row, block[0].length-col-1];
}

function tr_flip_vertical(row, col) {
	if (row < 0 || col < 0) return [row, col];
	return [block.length-1-row, col];
}

function tr_rot_cw(row, col, amount) {
	if (row < 0 || col < 0) return [row, col];
	if (amount == 0) return [row, col];
	if (amount < 0) amount += 360;
	if (amount == 90) return [block.length-col-1, block[0].length-row-1];
	if (amount == 270) return [col, block[0].length-row-1];
	if (amount == 180) return [block.length-row-1, block[0].length-col-1];
	return [-1,-1];
}

function tr_test() {
	var b = []; 
	for (var row=0; row<block.length; row++) {
		b[row] = [];
		for (var col=0; col<block[0].length; col++) {
			var rc = tr_flip_vertical(row, col); b[row][col] = block[rc[0]][rc[1]];
		}
	}	
	updateblock(b);
}

function tr_test_rot_90() {
	var b = []; 
	for (var row=0; row<block[0].length; row++) {
		b[row] = [];
		for (var col=0; col<block.length; col++) {
			var rc = tr_rot_cw(row, col, 90); b[row][col] = block[rc[0]][rc[1]];
		}
	}	
	updateblock(b);
}

function tr_test_rot_180() {
	var b = []; 
	for (var row=0; row<block.length; row++) {
		b[row] = [];
		for (var col=0; col<block[0].length; col++) {
			var rc = tr_rot_cw(row, col, 180); b[row][col] = block[rc[0]][rc[1]];
		}
	}	
	updateblock(b);
}

function tr_test_rot_270() {
	var b = []; 
	for (var row=0; row<block[0].length; row++) {
		b[row] = [];
		for (var col=0; col<block.length; col++) {
			var rc = tr_rot_cw(row, col, 270); b[row][col] = block[rc[0]][rc[1]];
		}
	}	
	updateblock(b);
}

function tr_test_quad() {
	var b = []; 
	for (var row=0; row<block.length; row++) {
		b[row] = [];
		for (var col=0; col<block[0].length; col++) {
			var rc = tr_quadrant(row, col, 3, 4, false, [3,2,1,0]); b[row][col] = block[rc[0]][rc[1]];
		}
	}	
	updateblock(b);
}

function tr_test_quad2() {
	var b = []; var s = "";
	for (var row=0; row<block.length-1; row++) {
		b[row] = [];
		for (var col=0; col<block[0].length-1; col++) {
			var rc = tr_quadrant(row, col, 3, 4, true, [2,3,1,0]); 
			if (!block[rc[0]]) b[row][col] = "?";
			else {
				b[row][col] = block[rc[0]][rc[1]];
				s += row + "," + col + ":" + rc[0] + "," + rc[1] + "[" + block[rc[0]][rc[1]] + "]; ";
			}
		}
		s += "<br>";
	}	
	updateblock(b);
//	$("debug").innerHTML = s;
}

// return 1D string of ciphertext representing the given quadrant configuration
function tr_quadrant_string(i, j, border, permutation) {
	var offset = border ? 1 : 0;
	var c = ""; var rc;
	for (var row=0; row<block.length-offset; row++) {
		for (var col=0; col<block[0].length-offset; col++) {
			rc = tr_quadrant(row, col, i, j, border, permutation);
			c += block[rc[0]][rc[1]];
		}
	}
	return c;
}


// offset: start new ciphertext at pos (i,j), wrapping at boundaries

// permute rows
// permute columns