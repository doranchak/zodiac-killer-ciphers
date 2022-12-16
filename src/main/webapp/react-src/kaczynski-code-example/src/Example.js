import React from 'react';
import {setState} from 'react';
import {create, rand} from 'random-seed';
import './Example.css'

const cipherLength = 51;
export class Example extends React.Component {
    constructor(props) {
        super(props);
        this.state = { // -1 represents '?'
            cipher: [72, 24, 74, 82, 75, 60, 58, 19, 14, -1, 64, 73, 12, 34, 51, 47, 
                68, 7, 84, 76, 19, 32, 70, 83, 59, 73, 78, 16, 49, 43, 49, 4, 46, 
                76, 80, 50, 1, 30, 12, 17, 46, 10, 12, 10, 63, 29, 67, 48, 49, 
                44, 19],
            showCipher: new Array(cipherLength).fill(true),
            gridNumbers: [47, 74, 64, 68, 81, 80, 5, 45, 52, 67, 68, 70, 20, 11,
                16, 14, 2, 25, 61, 71, 41, 24, 31, 42, 48, 46, 54, 29, 7,
                9, 8, 66, 54, 45, 44, 72, 28, 3, 31, 34, 3, 37, 40, 4,
                60, 62, 62, 9, 8, 0, 15],
            showGrid: new Array(cipherLength).fill(4),
            series: [29, 8, 48, 60, 66, 50, 63, 64, 66, -1, 42, 53, 32, 45,
                67, 61, 70, 32, 55, 57, 60, 56, 11, 35, 17, 29, 42, 45, 56,
                52, 57, 70, 10, 31, 34, 32, 29, 33, 43, 51, 49, 47, 52, 14,
                33, 1, 39, 57, 57, 44, 34],
            // with 3 corrections to the intentional errors
            seriesFinal: [29, 8, 48, 60, 66, 50, 63, 64, 66, -1, 42, 53, 32, 45,
                67, 61, 70, 32, 55, 57, 60, 56, 11, 35, 17, 29, 32, 45, 56,
                52, 57, 70, 10, 31, 34, 32, 29, 33, 43, 51, 49, 47, 62, 14,
                33, 1, 49, 57, 57, 44, 34],
            showSeries: new Array(cipherLength).fill(true),
            seriesMarkings: [26, 42, 46],
            seriesMarking: -1,
            plaintext: [
                'I ME MINE MY', 'WILL HAVE', 'F', 'R', 'U', 'H', 'S', 'T', 'U', ' ', 'C', 'K', '/', 'E', 'V', 'R', 'Y', '/', 
                'M', 'O', 'R', 'N', 'ING', ',', 'AND', 'I ME MINE MY', 'C', 'E', 'N', 'J', 'O', 'Y', 'FUTURE TENSE', 'HE SHE IT HIM HER HIS HERS ITS', 
                '.', '/', 'MY', '/', 'D', 'I', 'G', 'E', 'J', 'TION', '/', 'IS', 'A', 'O', 'O', 'D', '.'],
            // positions to highlight before replacing with final plaintext snippets.
            plaintextFinalMarkings: [
                [0], // I
                [2, 3, 4, 5, 6, 7, 8, 9, 10, 11], // BREAKFAST
                [13, 14, 15, 16], // EVERY
                [25], // I
                [26], // word spacer
                [27, 28, 29, 30, 31, 32], // WILL ENJOY
                [33], // IT
                [42], // J
                [46], // A
            ],
            plaintextFinalMarking: -1,
            plaintextFinal: [
                ['I', 'WILL HAVE', 'BREAKFAST', '/', 'EVERY'], 
                ['/', 'M', 'O', 'R', 'N', 'ING', ',', 'AND', 'I', '/', 'WILL ENJOY', 'IT'], 
                ['.', '/', 'MY', '/', 'D', 'I', 'G', 'E', 'S', 'TION', '/', 'IS', 'G', 'O', 'O', 'D', '.']],
            plaintextFinalWidths: [
                [1, 1, 10, 1, 4], 
                [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 6, 1], 
                [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
            ],
            plaintextFinalMarking2: -1,
            plaintextFinalMarkings2: [
                [0, 0], // I
                [0, 2], // BREAKFAST
                [0, 4], // EVERY
                [1, 8], // I
                [1, 9], // word spacer
                [1, 10], // WILL ENJOY
                [1, 11], // IT
                [2, 8], // J
                [2, 12], // A
            ],
            plaintextFinalMode: false, 
            // sections in the final plaintext to highlight when reading it out in full
            plaintextReading: -1,
            plaintextReadings: [
                [[0, 0]], [[0, 1]], [[0, 2]], [[0, 4]],
                [[1, 1], [1, 2], [1, 3], [1, 4], [1, 5]], [[1, 7]], [[1, 8]], [[1, 10]], [[1, 11]],
                [[2, 2]], [[2, 4], [2, 5], [2, 6], [2, 7], [2, 8], [2, 9]], [[2, 11]], [[2, 12], [2, 13], [2, 14], [2, 15]]
            ],
            showPlaintext: new Array(cipherLength).fill(3), // 0: off, 1: highlight series, 2: highlight series + decoding, 3: highlight decoding only
            markModuloPos: -1, // which pair of numbers are we adding via modulo 90 addition?
            markModuloWhich: -1, // 0 = cipher, 1 = cipher and grid number, 2 = series before modulo 90, 3 = series after modulo 90
            // width: 13,
            // height: 4
            width: 17,
            height: 3,
            // ms: 80,
            ms: 80,
            // msModulo: 150,
            msModulo: 10,
            // msDecode: 150,
            msDecode: 250,
            msSlower: 100,
            // showCipher: true,
            // showGrid: true,
            // showSeries: true,
            // showPlaintext: true
        }
        this.initToggle = this.initToggle.bind(this);
        this.initToggle2 = this.initToggle2.bind(this);
        this.toggleShowCipher = this.toggleShowCipher.bind(this);
		this.toggleShowGrid = this.toggleShowGrid.bind(this);
		this.toggleShowSeries = this.toggleShowSeries.bind(this);
		this.toggleShowPlaintext = this.toggleShowPlaintext.bind(this);
		this.togglePlaintextFinalMode = this.togglePlaintextFinalMode.bind(this);
		this.modulo90 = this.modulo90.bind(this);
		this.decode = this.decode.bind(this);
		this.showAdjustments = this.showAdjustments.bind(this);
		this.showAdjustments2 = this.showAdjustments2.bind(this);
		this.showSeriesCorrections = this.showSeriesCorrections.bind(this);
		this.readPlaintext = this.readPlaintext.bind(this);
        this.fruh = this.fruh.bind(this);
        this.question = this.question.bind(this);
        this.every = this.every.bind(this);
        this.mistakes = this.mistakes.bind(this);
        this.substitutions = this.substitutions.bind(this);
    }

    initToggle() {
        this.toggleShowCipher();
		this.toggleShowGrid();
		this.toggleShowSeries();
		this.toggleShowPlaintext();
    }

    initToggle2() {
        this.toggleShowCipher();
		this.toggleShowGrid();
		this.toggleShowSeries();
        this.setState({plaintextFinalMode: true});
    }

    toggleShowCipher() {
        const show = !this.state.showCipher[0];
        // this.setState({showCipher: new Array(cipherLength).fill(show)});
        this.toggleShowCipherFrame(show, 0);
    }

    toggleShowCipherFrame(show, pos) {
        const s = this.state.showCipher;
        s[pos] = show;
        this.setState({showCipher: s});
        pos++;
        if (pos < cipherLength)
            setTimeout(() => this.toggleShowCipherFrame(show, pos), this.state.ms);
    }
	
	toggleShowGrid() {
        const show = !this.state.showGrid[0];
        if (!show)
            this.setState({showGrid: new Array(cipherLength).fill(0)});
        else
            this.toggleShowGridFrame(0);
	}
	
    toggleShowGridFrame(pos) {
        const s = this.state.showGrid;
        s[pos] = 1;
        s[pos-1] = 2;
        s[pos-2] = 3;
        s[pos-3] = 4;

        this.setState({showGrid: s});
        pos++;
        if (pos < cipherLength + 3)
            setTimeout(() => this.toggleShowGridFrame(pos), this.state.ms);
    }
	toggleShowSeries() {
        const show = !this.state.showSeries[0];
        this.setState({showSeries: new Array(cipherLength).fill(show)});
	}
	
	toggleShowPlaintext() {
        const show = this.state.showPlaintext[0] ? 0 : 3;
        this.setState({showPlaintext: new Array(cipherLength).fill(show)});
	}
	togglePlaintextFinalMode() {
        this.setState({plaintextFinalMode: !this.state.plaintextFinalMode});
	}

    modulo90() {
        this.state.markModuloPos = 0;
        this.state.markModuloWhich = -1;
        this.modulo90Frame();
    }

    modulo90Frame() {
        if (this.state.markModuloPos == cipherLength) {
            this.setState({
                markModuloPos: -1,
                markModuloWhich: -1,
                showSeries: new Array(cipherLength).fill(true)
            });
            return;
        }
        var pos = this.state.markModuloPos;
        var which = this.state.markModuloWhich;
        which++;
        if (which == 5) {
            which = 0;
            pos++;
        }
        this.setState({markModuloPos: pos, markModuloWhich: which});

        setTimeout(() => this.modulo90Frame(), this.state.msModulo);
    }

    cipherClassName(pos) {
        if (this.state.markModuloPos == -1) {
            return "content cipher-on";
        }
        if (this.state.markModuloWhich < 3 && this.state.markModuloPos == pos)
            return "content cipher-on-modulo";
        return "content cipher-on-static";
    }
    gridClassName(pos) {
        if (this.state.markModuloPos != pos)
            return 'content grid-' + this.state.showGrid[pos]
        if (this.state.markModuloWhich == 1 || this.state.markModuloWhich == 2)
            return "content grid-on-modulo";
        return 'content grid-' + this.state.showGrid[pos]
    }
    seriesClassName(pos) {
        if (this.state.seriesMarking > -1 && this.state.seriesMarkings[this.state.seriesMarking] == pos)
            return 'content series-on-modulo';
        if (this.state.showPlaintext[pos] == 1 || this.state.showPlaintext[pos] == 2)
            return 'content series-on-modulo';
        if (this.state.markModuloPos != pos)
            return 'content';
        if (this.state.markModuloWhich == 2)
            return 'content series-on-modulo';
        if (this.state.markModuloWhich == 3)
            return 'content series-on-modulo-2';
        return 'content';
    }

    decode() {
        this.decodeFrame(0, 0);
    }

    decodeFrame(pos) {
        if (pos == cipherLength) return;
        var val = this.state.showPlaintext[pos];
        val++;
        if (val == 4) {
            pos++;
            val = 0;
        }
        this.state.showPlaintext[pos] = val;
        this.setState({showPlaintext: this.state.showPlaintext});
        setTimeout(() => this.decodeFrame(pos), this.state.msDecode);
    }

    plaintextClassName(pos) {
        if (this.state.plaintextFinalMarking != -1) {
            const match = this.state.plaintextFinalMarkings[this.state.plaintextFinalMarking].filter(e => e == pos);
            if (match.length > 0) return "content plaintext-on-highlight";
        }
        if (pos >= this.state.plaintext.length || this.state.showPlaintext[pos] < 2) 
            return "content plaintext-off";
        if (this.state.showPlaintext[pos] == 2)
            return "content plaintext-on-highlight";
        return "content plaintext-on";
    }

    plaintextClassNameFinal(row, col) {
        if (this.state.plaintextFinalMarking2 > -1) {
            const rc = this.state.plaintextFinalMarkings2[this.state.plaintextFinalMarking2];
            if (rc[0] == row && rc[1] == col) return "content plaintext-on-highlight";
        }
        if (this.state.plaintextReading != -1) {
            const match = this.state.plaintextReadings[this.state.plaintextReading].filter(e => e[0] == row && e[1] == col);
            if (match.length > 0) return "content plaintext-on-highlight";
        }

        return "content plaintext-on";
    }

    plaintextValue(pos) {
        if (pos >= this.state.plaintext.length || this.state.showPlaintext[pos] < 2) 
            return " ";
        return this.state.plaintext[pos];
    }

    seriesValue(pos) {
        if (this.state.markModuloPos == -1) {
            if (!this.state.showSeries[pos])
                return ' ';
            return this.tr(this.state.plaintextFinalMode ? this.state.seriesFinal[pos] : this.state.series[pos]);
        } 
        
        if (pos < this.state.markModuloPos)
            return this.tr(this.state.series[pos]);

        if (pos == this.state.markModuloPos) {
            // if (this.state.cipher[this.state.markModuloPos] == -1) return '?';
            if (this.state.markModuloWhich < 2)
                return ' ';
            if (this.state.markModuloWhich == 2) {
                if (this.state.series[pos] == -1) return '?';
                return this.state.cipher[this.state.markModuloPos] + this.state.gridNumbers[this.state.markModuloPos];
            }
            return this.tr(this.state.series[this.state.markModuloPos]);
        }
    }

    tr(val) {
        return val == -1 ? '?' : val;
    }
    fontSizeFor(val) {
        var size;
        var spacing = "normal";
        var padding = "";
        if (val.length < 3)
            size = 20;
        else if (val.length == 3) 
            size = 17;
        else if (val.length == 4) 
            size = 13;
        else if (val.includes("HE SHE")) 
            size = 7;
        else if (val.includes("FUTURE")) 
            size = 9;
        else if (val.includes("I ME MINE")) 
            size = 9;
        else if (val.includes("WILL HAVE")) 
            size = 13;   
        else if (val.includes("BREAKFAST") || val.includes("FRUHSTUCK")) {
            size = 28;
            spacing = "45px";
            padding = "0.6em";
        }
        else if (val.includes("EVERY")) {
            size = 28;
            spacing = "25px";
            padding = "0.35em";
        }
        else if (val.includes("WILL ENJOY")) {
            size = 27;
            spacing = "11px";
            padding = "0.4em";
        }
        return {fontSize: size + "pt", letterSpacing: spacing, paddingLeft: padding};
        // min is 8pt for length 30
        // max is 20pt for length <= 3
        // so length range is [3,30] (interval of 27)
        // and font range is [8,20] (interval of 12)

        // const ratio = 1 - (val.length - 3) / 27;
        // const size = ratio * 12 + 8;

        // return {fontSize: size + "pt"};
    }

    // fontSizeForFinal(row, col) {
    //     var size = this.fontSizeFor(this.state.plaintextFinal[row][col]);
    //     return size;
    // }

    plaintextValueFinal(row, col) {
        return this.state.plaintextFinal[row][col];
    }

    showAdjustments() {
        this.showAdjustmentsFrame();
    }

    showAdjustmentsFrame() {        
        if (this.state.plaintextFinalMarking == this.state.plaintextFinalMarkings.length - 1) {
            this.setState({plaintextFinalMarking: -1});
            return;
        }
        this.setState({plaintextFinalMarking: this.state.plaintextFinalMarking + 1});
        setTimeout(() => this.showAdjustmentsFrame(), this.state.msSlower);
    }

    showAdjustments2() {
        this.showAdjustments2Frame();
    }

    showAdjustments2Frame() {        
        if (this.state.plaintextFinalMarking2 == this.state.plaintextFinalMarkings2.length - 1) {
            this.setState({plaintextFinalMarking2: -1});
            return;
        }
        this.setState({plaintextFinalMarking2: this.state.plaintextFinalMarking2 + 1});
        setTimeout(() => this.showAdjustments2Frame(), this.state.msSlower);
    }

    showSeriesCorrections() {
        this.showSeriesCorrectionsFrame();
    }

    showSeriesCorrectionsFrame() {        
        if (this.state.seriesMarking == this.state.seriesMarkings.length - 1) {
            this.setState({seriesMarking: -1});
            return;
        }
        this.setState({seriesMarking: this.state.seriesMarking + 1});
        setTimeout(() => this.showSeriesCorrectionsFrame(), this.state.msSlower);
    }

    readPlaintext() {
        this.readPlaintextFrame();
    }

    readPlaintextFrame() {        
        if (this.state.plaintextReading == this.state.plaintextReadings.length - 1) {
            this.setState({plaintextReading: -1});
            return;
        }
        this.setState({plaintextReading: this.state.plaintextReading + 1});
        setTimeout(() => this.readPlaintextFrame(), this.state.msSlower);
    }

    fruh() {
        this.setState({plaintextReading: 2});
        setTimeout(() => this.fruh2(), this.state.msSlower);
    }

    fruh2() {
        this.state.plaintextFinal[0][2] = 'FRUHSTUCK';
        this.setState({plaintextFinal: this.state.plaintextFinal});
        setTimeout(() => this.fruh3(), this.state.msSlower);
    }

    fruh3() {
        this.setState({plaintextReading: -1});
    }
    
    question() {
        this.setState({plaintextReading: 2});
        setTimeout(() => this.question2(), this.state.msSlower);
    }
    question2() {
        this.state.plaintextFinal[0] = ['I', 'WILL HAVE', 'F', 'R', 'U', 'H', 'S', 'T', 'U', ' ', 'C', 'K', '/', 'EVERY'];
        this.state.plaintextFinalWidths[0] = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4];
        this.setState({plaintextFinal: this.state.plaintextFinal, plaintextFinalWidths: this.state.plaintextFinalWidths});
        setTimeout(() => this.question3(), 100);
    }

    question3() {
        for (var col=2; col<12; col++) {
            const id = 'p-cell-0-' + col;
            document.getElementById(id).className = "content plaintext-on-highlight";
        }
        setTimeout(() => this.question4(), this.state.msSlower);
    }

    question4() {
        for (var col=2; col<12; col++) {
            const id = 'p-cell-0-' + col;
            document.getElementById(id).className = "content plaintext-on";
        }
    }

    every() {
        document.getElementById('p-cell-0-13').className = "content plaintext-on-highlight";
        setTimeout(() => this.every2(), this.state.msSlower);
    }

    every2() {
        // document.getElementById('p-cell-0-13').innerHTML = "EVRY";
        // do this properly so cells render
        this.state.plaintextFinal[0] = ['I', 'WILL HAVE', 'F', 'R', 'U', 'H', 'S', 'T', 'U', ' ', 'C', 'K', '/', 'E', 'V', 'R', 'Y'];
        this.state.plaintextFinalMarkings[2] = [13, 14, 15, 16];
        this.state.plaintextFinalWidths[0] = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1];
        document.getElementById('p-cell-0-13').className = "content plaintext-on";
        this.setState({
            plaintextFinal: this.state.plaintextFinal,
            plaintextFinalMarkings: this.state.plaintextFinalMarkings,
            plaintextFinalWidths: this.state.plaintextFinalWidths
        });
        setTimeout(() => {
            for (var col=13; col<17; col++) {
                document.getElementById('p-cell-0-' + col).className = "content plaintext-on-highlight";
            }
        }, 100);
        setTimeout(() => this.every3(), this.state.msSlower);
    }

    every3() {
        for (var col=13; col<17; col++) {
            document.getElementById('p-cell-0-' + col).className = "content plaintext-on";
        }
    }

    mistakes() {
        document.getElementById('p-cell-1-9').className = "content plaintext-on-highlight";
        setTimeout(() => this.mistakes2(), this.state.msSlower);
    }

    mistakes2() {
        document.getElementById('p-cell-1-9').innerHTML = "〰";
        document.getElementById('p-cell-1-9').className += " wavy";
        setTimeout(() => this.mistakes3(), this.state.msSlower);
    }
    
    mistakes3() {
        document.getElementById('p-cell-1-9').className = "content plaintext-on-red wavy";
        setTimeout(() => this.mistakes4(), this.state.msSlower);
    }

    mistakes4() {
        document.getElementById('p-cell-2-8').className = "content plaintext-on-highlight";
        setTimeout(() => this.mistakes5(), this.state.msSlower);
    }
    
    mistakes5() {
        document.getElementById('p-cell-2-8').innerHTML = "〰";
        document.getElementById('p-cell-2-8').className += " wavy";
        setTimeout(() => this.mistakes6(), this.state.msSlower);
    }

    mistakes6() {
        document.getElementById('p-cell-2-8').className = "content plaintext-on-red wavy";
        setTimeout(() => this.mistakes7(), this.state.msSlower);
    }

    mistakes7() {
        document.getElementById('p-cell-2-12').className = "content plaintext-on-highlight";
        setTimeout(() => this.mistakes8(), this.state.msSlower);
    }
    
    mistakes8() {
        document.getElementById('p-cell-2-12').innerHTML = "〰";
        document.getElementById('p-cell-2-12').className += " wavy";
        setTimeout(() => this.mistakes9(), this.state.msSlower);
    }

    mistakes9() {
        document.getElementById('p-cell-2-12').className = "content plaintext-on-red wavy";
    }

    substitutions() {
        this.substitutionsFrame(0, 0);
    }

    substitutionsFrame(pos, which) {
        // which: 
        //   0: highlight plaintext
        //   1: highlight plaintext + series
        //   2: highlight neither
        if (which == 3) {
            pos++;
            which = 0;
        }
        if (pos == cipherLength) return;
        // if (pos == 27) return;
        var row = parseInt(pos / this.state.width);
        var col = pos % this.state.width;

        var cl;
        var idP = `p-cell-${row}-${col}`;
        var idS = `s-cell-${pos}`;
        if (which < 2) 
            if (pos == 26 || pos == 42 || pos == 46)
                document.getElementById(idP).className = "content plaintext-on-highlight wavy";
            else
                document.getElementById(idP).className = "content plaintext-on-highlight";
        else
            document.getElementById(idP).className = "content plaintext-on";

        if (which == 0 && pos == 32) {  // FUTURE TENSE                
            var idP = "p-cell-1-15";
            var idS = "s-cell-32";
            // highlight "future tense"
            document.getElementById(idP).innerHTML = "FUTURE TENSE";
            document.getElementById(idP).className = "content plaintext-on-highlight";
            document.getElementById(idP).style = "font-size: 9pt; letter-spacing: normal;";
            setTimeout(() => {
                // highlight the series number
                document.getElementById(idS).innerHTML = "10";
                document.getElementById(idS).className = "content series-on-modulo";
            }, this.state.msDecode);
            setTimeout(() => {
                // un highlight both
                this.substitutionsFrame(pos, 2);
            }, 2*this.state.msDecode);
            return;
        }
        if (which == 1) {
            if (pos == 26) { // first intentional error
                document.getElementById(idS).innerHTML = 32; // set to correct number
                document.getElementById(idS).className = "content series-on-modulo";
                setTimeout(() => {
                    // set to incorrect number
                    document.getElementById(idS).innerHTML = 42;
                    document.getElementById(idS).className = "content series-on-modulo";
                    setTimeout(() => {
                        // set the incorrect plaintext
                        document.getElementById(idP).innerHTML = "C";
                        document.getElementById(idP).className = "content plaintext-on-highlight";
                        setTimeout(() => {
                            // resume normal processing
                            which = 2;
                            this.substitutionsFrame(pos, which);
                        }, this.state.msDecode);
                    }, this.state.msDecode);
                }, this.state.msDecode);
                return;
            } else if (pos == 27) { // replace WILL ENJOY with ENJOY future tense
                const parent = document.getElementById("p-cell-1-10").parentElement;
                // document.getElementById("p-cell-1-11").remove();
                // document.getElementById("p-cell-1-10").remove();
                // parent.appendChild(<><td>E</td><td>N</td><td>J</td><td>O</td><td>Y</td><td>FUTURE TENSE</td></>);
                parent.innerHTML = `
                    <td class="row-plaintext-prefix">Decoded:</td>
                    <td id="p-cell-1-0" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">/</td>
                    <td id="p-cell-1-1" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">M</td>
                    <td id="p-cell-1-2" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">O</td>
                    <td id="p-cell-1-3" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">R</td>
                    <td id="p-cell-1-4" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">N</td>
                    <td id="p-cell-1-5" class="content plaintext-on" colspan="1" style="font-size: 17pt; letter-spacing: normal;">ING</td>
                    <td id="p-cell-1-6" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">,</td>
                    <td id="p-cell-1-7" class="content plaintext-on" colspan="1" style="font-size: 17pt; letter-spacing: normal;">AND</td>
                    <td id="p-cell-1-8" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">I</td>
                    <td id="p-cell-1-9" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">C</td>
                    <td id="p-cell-1-10" class="content plaintext-on-highlight" colspan="1" style="font-size: 20pt; letter-spacing: normal;">E</td>
                    <td id="p-cell-1-11" class="content plaintext-on-highlight" colspan="1" style="font-size: 20pt; letter-spacing: normal;">N</td>
                    <td id="p-cell-1-12" class="content plaintext-on-highlight" colspan="1" style="font-size: 20pt; letter-spacing: normal;">J</td>
                    <td id="p-cell-1-13" class="content plaintext-on-highlight" colspan="1" style="font-size: 20pt; letter-spacing: normal;">O</td>
                    <td id="p-cell-1-14" class="content plaintext-on-highlight" colspan="1" style="font-size: 20pt; letter-spacing: normal;">Y</td>
                    <td id="p-cell-1-15" class="content plaintext-on" colspan="1" style="font-size: 9pt; letter-spacing: normal;"> </td>
                    <td id="p-cell-1-16" class="content plaintext-on" colspan="1" style="font-size: 20pt; letter-spacing: normal;">IT</td>
                    `;
                for (var i=0; i<5; i++) {
                    // 1: highlight series 1
                    // 2: unhighlight series 1
                    // 3: highlight series 2
                    // 4: unhighlight series 2
                    // etc

                    var delayOn = (2*i + 1) * this.state.msDecode;
                    var delayOff = (2*i + 2) * this.state.msDecode;
                    var id = `s-cell-${pos+i}`;
                    this.seriesOnOff(id, pos+i, delayOn, true);
                    this.seriesOnOff(id, pos+i, delayOff, false);
                }
                setTimeout(() => {
                    for (var i=0; i<5; i++) {
                        document.getElementById(`p-cell-1-${10+i}`).className = "content plaintext-on";        
                    }
                }, 11 * this.state.msDecode);
                setTimeout(() => {
                    this.substitutionsFrame(32, 0);
                }, 12 * this.state.msDecode);
                return;
            } else if (pos == 42) { // S in DIGESTION
                var idS = "s-cell-42";
                var idP = "p-cell-2-8";
                document.getElementById(idS).innerHTML = 62; // set to correct number
                document.getElementById(idS).className = "content series-on-modulo";
                setTimeout(() => {
                    // set to incorrect number
                    document.getElementById(idS).innerHTML = 52;
                    document.getElementById(idS).className = "content series-on-modulo";
                    setTimeout(() => {
                        // set the incorrect plaintext
                        document.getElementById(idP).innerHTML = "J";
                        document.getElementById(idP).className = "content plaintext-on-highlight";
                        setTimeout(() => {
                            // resume normal processing
                            which = 2;
                            this.substitutionsFrame(pos, which);
                        }, this.state.msDecode);
                    }, this.state.msDecode);
                }, this.state.msDecode);
                return;
            } else if (pos == 46) {
                var idS = "s-cell-46";
                var idP = "p-cell-2-12";
                document.getElementById(idS).innerHTML = 49; // set to correct number
                document.getElementById(idS).className = "content series-on-modulo";
                setTimeout(() => {
                    // set to incorrect number
                    document.getElementById(idS).innerHTML = 39;
                    document.getElementById(idS).className = "content series-on-modulo";
                    setTimeout(() => {
                        // set the incorrect plaintext
                        document.getElementById(idP).innerHTML = "A";
                        document.getElementById(idP).className = "content plaintext-on-highlight";
                        setTimeout(() => {
                            // resume normal processing
                            which = 2;
                            this.substitutionsFrame(pos, which);
                        }, this.state.msDecode);
                    }, this.state.msDecode);
                }, this.state.msDecode);
                return;
            } else {
                document.getElementById(idS).innerHTML = this.tr(this.state.series[pos]);
                document.getElementById(idS).className = "content series-on-modulo";
            }
        } else {
            document.getElementById(idS).className = "content";
        }
            
        setTimeout(() => this.substitutionsFrame(pos, which+1), this.state.msDecode);

    }

    seriesOnOff(id, pos, delay, on) {
        setTimeout(() => {
            const elem = document.getElementById(id);
            if (on) elem.innerHTML = this.state.series[pos];
            elem.className = "content series-on" + (on ? "-modulo" : "");
        }, delay);
    }

    render() {
        let html = [];
        let rows = [];
        for (let row=0; row<this.state.height; row++) {
            // ciphertext:
            let cols = [];
            for (let col=0; col<this.state.width; col++) {
                let pos = row * this.state.width + col;
                if (this.state.showCipher[pos] && pos < this.state.cipher.length) {
                    cols.push(<td key={'c-cell-' + pos} className={this.cipherClassName(pos)}>{this.tr(this.state.cipher[pos])}</td>);
                } else cols.push(<td key={'c-cell-' + pos} className="content cipher-off">&nbsp;</td>);
            }
            rows.push(<tr key={'c-' + row} className="row-cipher"><td className="row-cipher-prefix">Cipher:</td>{cols}</tr>);
            // grid numbers:
            cols = [];
            for (let col=0; col<this.state.width; col++) {
                let pos = row * this.state.width + col;
                // if (this.state.showGrid[pos] && pos < this.state.gridNumbers.length) {
                //     cols.push(<td class="content grid-on">{this.state.gridNumbers[pos]}</td>);
                // } else cols.push(<td class="content grid-off">&nbsp;</td>);
                cols.push(<td key={'g-cell-' + pos} className={this.gridClassName(pos)}>{this.tr(this.state.gridNumbers[pos])}</td>);
            }
            rows.push(<tr key={'g-' + row} className="row-grid"><td className="row-grid-prefix">Grid:</td>{cols}</tr>);
            // series:
            cols = [];
            for (let col=0; col<this.state.width; col++) {
                let pos = row * this.state.width + col;
                const id = `s-cell-${pos}`;
                cols.push(<td id={id} key={id} className={this.seriesClassName(pos)}>{this.seriesValue(pos)}</td>);
            }
            rows.push(<tr key={'s-' + row} className="row-series"><td className="row-series-prefix">Series:</td>{cols}</tr>);
            // plaintext:
            cols = [];
            if (this.state.plaintextFinalMode) {
                for (let col=0; col<this.state.plaintextFinal[row].length; col++) {
                    const key = 'p-cell-' + row + "-" + col;
                    cols.push(<td id={key} key={key} className={this.plaintextClassNameFinal(row, col)} colSpan={this.state.plaintextFinalWidths[row][col]} style={this.fontSizeFor(this.state.plaintextFinal[row][col])}>{this.plaintextValueFinal(row, col)}</td>);
                }
            } else {
                for (let col=0; col<this.state.width; col++) {
                    let pos = row * this.state.width + col;
                    const key = 'p-cell-' + pos;
                    cols.push(<td id={key} key={key} className={this.plaintextClassName(pos)} style={this.fontSizeFor(this.state.plaintext[pos])}>{this.plaintextValue(pos)}</td>);

                }
            }
            rows.push(<tr key={'p-' + row} className="row-plaintext"><td className="row-plaintext-prefix">Decoded:</td>{cols}</tr>);
        }
        html.push(<table id="example-table"><tbody>{rows}</tbody></table>);

        // buttons
        html.push(
            <div className="buttons">
                <button onClick={this.initToggle}>Init</button>
                <button onClick={this.toggleShowCipher}>Toggle cipher</button>
                <button onClick={this.toggleShowGrid}>Toggle grid</button>
                <button onClick={this.toggleShowSeries}>Toggle series</button>
                <button onClick={this.toggleShowPlaintext}>Toggle plaintext</button>
                <button onClick={this.modulo90}>Modulo 90</button>
                <button onClick={this.decode}>Decode</button>
                <button onClick={this.showAdjustments}>Show adjustments</button>
                <button onClick={this.togglePlaintextFinalMode}>Toggle final plaintext and series</button>
                <button onClick={this.showAdjustments2}>Show adjustments2</button>
                <button onClick={this.showSeriesCorrections}>Show series corrections</button> (do before and after)
                <br/>
                Scripted order:
                <br/>
                <button onClick={this.initToggle}>Init</button>
                <button onClick={this.toggleShowCipher}>Toggle cipher</button>
                <button onClick={this.toggleShowGrid}>Toggle grid</button>
                <button onClick={this.modulo90}>Modulo 90</button>
                <button onClick={this.decode}>Decode</button>
                <button onClick={this.showAdjustments}>Show adjustments</button>
                <button onClick={this.showSeriesCorrections}>Show series corrections</button>
                <button onClick={this.togglePlaintextFinalMode}>Toggle final plaintext and series</button>
                <button onClick={this.showSeriesCorrections}>Show series corrections</button>
                <button onClick={this.showAdjustments2}>Show adjustments2</button>
                <button onClick={this.readPlaintext}>Read plaintext</button><br/>
                Scripted order (plaintext to cipher):<br/>
                <button onClick={this.initToggle2}>Init2</button>
                <button onClick={this.fruh}>Fruhstuck</button>
                <button onClick={this.question}>Question mark</button>
                <button onClick={this.every}>Every</button>
                <button onClick={this.mistakes}>Mistakes</button>
                <button onClick={this.substitutions}>Substitutions</button>
                <button onClick={this.toggleShowGrid}>Toggle grid</button>
            </div>
        );

        return html;
    }
}


export default Example;