// randomly shuffle the columns
function shuffleColumns() {
	var count = 0;
	
	var a = randomColumns();
	var newblock = [];
	for (var i=0; i<a.length; i++) {
		for (var row=0; row<block.length; row++) {
			if (i==0) newblock[row] = [];
			newblock[row][a[i][1]] = block[row][i];
		}
	}
	
	updateblock(newblock);
	
}

// random columnar transposition of the already-enciphered symbols
function shuffleTranspose() {
	var i=0;
	var newblock = [];
	for (var row=0; row<block.length; row++) newblock[row] = [];
	var a = randomColumns(); // 
	for (var col=0; col<block[0].length; col++) {
		for (var row=0; row<block.length; row++) {
			var ch = ciphertext[i++];
			newblock[row][a[col][1]] = ch;
		}
	}
	
	updateblock(newblock);
}

// random columnar transposition of the plaintext, then application of substitution
function shuffleTransposeSubstitution() {
}


// random selection of columns
function randomColumns() {
	var a = [];
	for (var i=0; i<block[0].length; i++) {
		a[i] = [Math.random(), i];
	}
	// random permutation of columns
	a.sort(function(a,b){return a[0]-b[0];})
	return a;
}	

// assuming that the 408's plaintext is loaded, apply the original symbol substitution sequence
function applyAssignments(a) {
	var pointers = {};
	for (var key in a) pointers[key] = 0;
	var newblock = [];
	for (var row=0; row<block.length; row++) {
		newblock[row] = [];
		for (var col=0; col<block[0].length; col++) {
			var ch = block[row][col];
			var symbol = a[ch][pointers[ch]++];
			newblock[row][col]=symbol;
		}
	}
	updateblock(newblock);
}
// derive the symbol assignment sequences from the plaintext solution of the 408
function deriveAssignments() {
	var z408 = cipherline[1];
	var z408sol = "ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGERTUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI";
	
	var map = {};
	for (var i=0; i<z408.length; i++) {
		var key = z408sol[i];
		if (!map[key]) map[key] = [];
		map[key].push(z408[i]);
	}
	return map;
	
}

