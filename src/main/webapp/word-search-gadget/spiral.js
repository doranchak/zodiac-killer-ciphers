// simple spiral: begin at position (i,j).  choose direction d1 (up, right, down, left).  advance n characters in this direction.
// choose 2nd direction d2.  advance one position.  begin spiral.  all out-of-bounds positions wrap around to the appropriate positions.
// stop when we encounter an already-seen position.
// d1 and d2 must be perpendicular.
function spiral(i, j, d1, n, d2) {
	// d1 and d2 must be perpendicular
	if (Math.abs(d1-d2) != 1) return ""; //NOT ENOUGH
	var c = "";
	var seen = [];
	var H = block.length; var W = block[0].length;
	for (var row=0; row<H; row++) {
		seen[row] = [];
		for (var col=0; col<W; col++) {
			seen[row][col] = false;
		}
	}
	
	c += block[i][j]; seen[i][j] = true;
	var d = [];
	spiraldirection(d, d1);

	for (var k=0; k<n; k++) { // start at i,j and advance n characters in direction d1
		i = swrow(i+d[0]);
		j = swcol(j+d[1]);
		if (seen[i][j]) return c;
		c += block[i][j];
		seen[i][j] = true;
	}
	
	// 2nd direction d2
	spiraldirection(d, d2);
	
	// begin spiraling 
	var dspiral1 = d2; var dspiral2 = opposite(d1); var inc1 = 1; var inc2 = n+1;
	while (true) {
		// go inc1 steps in direction dspiral1, then inc2 steps in direction dspiral2.
		// then, dspiral1 is flipped, and dspiral2 is flipped.  inc1 and inc2 both go up by one.
		// continue until we encounter an already-seen position.
		
		spiraldirection(d, dspiral1);
		for (var k=0; k<inc1; k++) {
			i = swrow(i+d[0]);
			j = swcol(j+d[1]);
			if (seen[i][j]) return c;
			c += block[i][j];
			seen[i][j] = true;
		}
		dspiral1 = opposite(dspiral1);
		
		spiraldirection(d, dspiral2);
		for (var k=0; k<inc2; k++) {
			i = swrow(i+d[0]);
			j = swcol(j+d[1]);
			if (seen[i][j]) return c;
			c += block[i][j];
			seen[i][j] = true;
		}
		dspiral2 = opposite(dspiral2);
		
		inc1++; inc2++;
	}
}

function swrow(row) {
	if (row < 0) return block.length + row;
	return row % block.length;
}
function swcol(col) {
	if (col < 0) return block[0].length + col;
	return col % block[0].length;
}

// update array of deltas for the given direction 
function spiraldirection(delta, direction) {
	if (direction == 0) { delta[0] = -1; delta[1] = 0; } // up
	else if (direction == 1) { delta[0] = 0; delta[1] = 1; } // right
	else if (direction == 2) { delta[0] = 1; delta[1] = 0; } // down
	else { delta[0] = 0; delta[1] = -1; } // left
}

// return the opposite direction
function opposite(direction) {
	return (direction + 2) % 4;
}

function testscorespirals() {
	var d1 = new Date();
	var s = scorespirals();
	var d2 = new Date();
	scoredisplay(s, d1, d2);
}

function scorespirals() {
	var directions = [[0,1],[1,0],[1,2],[2,1],[2,3],[3,2],[3,0],[0,3]]; // 8 possibilities
	

	// get ciphertexts for each combination of rotation and flips
	var ciphers = [];
	
	/* combinations:
		rot 0 f N
		rot 0 f H
		rot 0 f V
		rot 90 f N
		rot 90 f H
		rot 90 f V
	*/
	
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

	// how many for testing the 408?  6*24*17*8*5
	var numtested = 0; var c;
	for (var i=0; i<ciphers.length; i++) {
		$("input").value = ciphers[i]; update();
		var H = block.length;
		var W = block[0].length; 
		
		for (var row=0; row < H; row++) {
			for (var col=0; col < W; col++) {
				for (var d=0; d<directions.length; d++) {
					for (var n=0; n<5; n++) {
						c = spiral(row, col, directions[d][0], n, directions[d][1]);
						scores[numtested] = [];
						scores[numtested][0] = [i,row,col,directions[d][0],directions[d][1],n,c.length];
						scores[numtested][1] = scorengrams(c);
						scores[numtested][2] = scorehomophones(c);
						numtested++;
					}
				}
			}
		}	
		
	}
	return scores;
}








