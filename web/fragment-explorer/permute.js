// re-arrange the cipher block using the given scheme.
//	 rows: an array specifying the new ordering of rows
//   cols: an array specifying the new ordering of columns
//   rowflips: array of booleans: if true, the given row is reversed.
//   colflips: array of booleans: if true, the given col is reversed.
//   offsets:  determins the "origin" of the cipher text.  original cipher's origin is (0,0).  
// permute_row_col([0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19],[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0])
function permute_row_col(rows, cols, rowflips, colflips, offsets) {
	var newblock = [];
	var row, col; var H = block.length; var W = block[0].length;  var line;
	for (var i=0; i<rows.length; i++) newblock[i] = [];
	
	for (var i=0; i<rows.length; i++) {
		line = "";
		for (var j=0; j<cols.length; j++) {
			row = rows[i] + offsets[0]; if (row < 0) row += H; row %= H;
			col = cols[j] + offsets[1]; if (col < 0) col += W; col %= W;
			if (rowflips[i]) col = W-1-col;
			if (colflips[j]) row = H-1-row;
			line += block[row][col];
		}
		newblock[i] = line;
	}
	return newblock;
}



var pop = [];  var newpop = []; var pop_size = 3;
var mutrate = 0.2; var go_evolve = true; var gen; var fitness_function = 0;
function evolve(fit) {
	fitness_function = fit;
	gen = 0;
	var H = block.length; var W = block[0].length;
	for (var i=0; i<pop_size; i++) {
		pop[i] = [[],[],[],[]];
		for (var j=0; j<H; j++) { // init rows
			pop[i][0][j] = j; 
			pop[i][2][j] = 0;
		}
		for (var j=0; j<W; j++) { // init cols
			pop[i][1][j] = j; 
			pop[i][3][j] = 0;
		}
		pop[i][4] = [0,0]; // offset
		evaluate(pop[i]);
	}
	evolve_loop();
}

function evolve_loop() {
	d1 = new Date();
	d2 = new Date();
	while ((d2.getTime() - d1.getTime()) < 5000) {
		newpop = [];
		for (var i=0; i<pop_size; i++) { 
			newpop[i] = [];
			for (var j=0; j<pop[i].length; j++) if (j==5) newpop[i][j] = pop[i][j]; else newpop[i][j] = pop[i][j].clone(); // new offspring
			mutate(i); // mutate it
			evaluate(newpop[i]);
		}
		select(); // replace pop with better offspring
		d2 = new Date();
		gen++;
	}
	displayPop();
	if (go_evolve) setTimeout("evolve_loop()", 500);
}

function indToString(ind) {
	var s = "permute_row_col(";
	
	s += "[" + ind[0] + "],";
	s += "[" + ind[1] + "],";
	s += "[" + ind[2] + "],";
	s += "[" + ind[3] + "],";
	s += "[" + ind[4] + "]";
	
	s += ");  // fitness " + ind[5];

	return s;
}

function displayPop() {
	
	var max = 0; var top;
	var pops = "";
	for (var i=0; i<pop_size; i++) {
		pops += indToString(pop[i]) + "<br>";
		if (pop[i][5] > max) {
			max = pop[i][5];
			top = indToString(pop[i]);
		}
	}
	best = "<hr>best: " + top + "<hr>";
	
	var html = "gen [" + gen + "]<br>" + best + pops;
	$("debug").innerHTML = html;
}

function doMutate() {
	if (Math.random() <= mutrate) return true;
}

function mutate(i) {
	for (var j=0; j<newpop[i][0].length; j++) { // mutate rows
		if (doMutate()) {
			swap(newpop[i][0], j, Math.floor(Math.random()*newpop[i][0].length));
		}
	}
	for (var j=0; j<newpop[i][1].length; j++) { // mutate cols
		if (doMutate()) {
			swap(newpop[i][1], j, Math.floor(Math.random()*newpop[i][1].length));
		}
	}
	for (var j=0; j<newpop[i][2].length; j++) { // mutate rowflips
		if (doMutate()) {
			newpop[i][2][j] = !newpop[i][2][j];
		}
	}
	for (var j=0; j<newpop[i][3].length; j++) { // mutate colflips
		if (doMutate()) {
			newpop[i][3][j] = !newpop[i][3][j];
		}
	}
	// mutate offsets
	if (doMutate()) newpop[i][4][0] = Math.floor(Math.random()*newpop[i][0].length);
	if (doMutate()) newpop[i][4][1] = Math.floor(Math.random()*newpop[i][1].length);
}

function evaluate(ind) {
	for (var k=0; k<ind[0].length; k++) if (isNaN(ind[0][k])) alert(k+": " + ind[0][0]);
	var c = toLine(permute_row_col(ind[0], ind[1], ind[2], ind[3], ind[4]));
	
	if (fitness_function == 0) {
		var ng = ngrams(c, 3);
		ind[5] = 0;
		for (var key in ng) if (ng[key] > 1) ind[5] += ng[key];
	}
	else ind[5] = scorehomophones(c)[3];
	
}

function doReplace() {
	return Math.random() > 0.5;
}
function select() {
	for (var i=0; i<pop_size; i++) {
		if (newpop[i][5] > pop[i][5]) pop[i] = newpop[i];
		else if (newpop[i][5] > pop[i][5]) {
			if (doReplace()) {
				pop[i] = newpop[i];
			}
		}
	}
}

function swap(a, i, j) {
	var tmp = a[j];
	a[j] = a[i];
	a[i] = tmp;
}