// return array of ngram counts (repetitions only)
function scorengrams(cipher) {
	var go; var result = []; var count; var num;
	var ng; var n = 2;
	do {
		go = false;
		count = 0; num = 0;
		ng = ngrams(cipher, n);
		
		for (var key in ng) {
			if (ng[key] > 1) {
				count += ng[key];
				num ++;
				go = true;
			}
		}
		
		if (go) {
			result[result.length] = count;
			result[result.length] = num;
		}
		n++;
	} while (go);
	
	return result;
}

// return array of homophone longest-run counts by length
function scorehomophones(cipher) {
	ciphertext = cipher;
	var h = homophoneSearchN2();


	var byrun = [];

	for (var i=0; i<h.length; i++) {
		if (byrun[h[i][2]]) byrun[h[i][2]]++; else byrun[h[i][2]] = 1; 
	}
	
	return byrun;
	
}

function scoredisplay(s, d1, d2) {
	
	var line;
	
	var timing = "Started " + d1 + " Ended " + d2 + " Duration " + (d2.getTime() - d1.getTime()) + " ms.\n";
	document.write(timing);
	
	// figure out how wide each array gets
	var widths = [0,0,0];
	for (var i=0; i<s.length; i++) {
		for (var j=0; j<s[i].length; j++) {
			widths[j] = Math.max(widths[j], s[i][j].length);
		}
	}
	
	var min = [];
	var max = [];
	var sum = [];
	for (var j=0; j<s[0].length; j++) {
		min[j] = []; max[j] = []; sum[j] = [];
		for (var k=0; k<widths[j]; k++) {
			min[j][k] = 1e90; max[j][k] = -1e90; sum[j][k] = 0;
		}
	}
	
	
	var val;
	for (var i=0; i<s.length; i++) {
		line = "";
		for (var j=0; j<s[i].length; j++) {
			for (var k=0; k<widths[j]; k++) {
				val = s[i][j][k];
				if (!val) val = 0;
				line += val+",";
				
				min[j][k] = Math.min(min[j][k], val);
				max[j][k] = Math.max(max[j][k], val);
				sum[j][k] += val;
			}
		}
		document.write(line + "\n");
	}
	document.write("\n\nTotal: " + s.length + "\n");
	for (var i=0; i<min.length; i++) {
		for (var j=0; j<min[i].length; j++) {
			document.write(i+","+j + ": Min [" + min[i][j] + "] Max [" + max[i][j] + "] Sum [" + sum[i][j] + "] Avg [" + (sum[i][j]/s.length) + "] \n");
		}
	}
}


function testscorequadrants() {
	var d1 = new Date();
	var s = scorequadrants();
	var d2 = new Date();
	scoredisplay(s, d1, d2);
}

// score every possible quadrant configuration
function scorequadrants() {
	
	/* configurations:
	
		clockwise rotation:  {0, 90}
		flips:  {none, horizontal, vertical}
		quadrants: 	rows {0 < i < H}  cols {0 < i < W}  border {true, false} permutations {24 possible selections of (0,1,2,3)}
		
	*/	
	
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


	var permutations = [[0, 1, 2, 3], [0, 1, 3, 2], [0, 2, 1, 3], [0, 2, 3, 1], [0, 3, 1, 2], [0, 3, 2, 1], [1, 0, 2, 3], [1, 0, 3, 2], [1, 2, 0, 3], [1, 2, 3, 0], [1, 3, 0, 2], [1, 3, 2, 0], [2, 0, 1, 3], [2, 0, 3, 1], [2, 1, 0, 3], [2, 1, 3, 0], [2, 3, 0, 1], [2, 3, 1, 0], [3, 0, 1, 2], [3, 0, 2, 1], [3, 1, 0, 2], [3, 1, 2, 0], [3, 2, 0, 1], [3, 2, 1, 0]];
	var d = new Date();
	// 6 ciphers * H rows * W cols * 2 borders * 24 permutations = 6*17*20*2*24 = 97,920 (for the 340)    6*17*24*2*24 = 117,504 (for the 408)
	var numtested = 0; var c;
	for (var i=0; i<ciphers.length; i++) {
		$("input").value = ciphers[i]; update();
		var H = block.length;
		var W = block[0].length; 
		for (var row=0; row < H; row++) {
			for (var col=0; col < W; col++) {
				for (var b=0; b<2; b++) {
					for (var p=0; p<permutations.length; p++) {
						c = tr_quadrant_string(row, col, b==1, permutations[p]);
						scores[numtested] = [];
						scores[numtested][0] = [i,row,col,b,p];
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
