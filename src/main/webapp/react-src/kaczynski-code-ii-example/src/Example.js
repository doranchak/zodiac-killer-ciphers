import React from 'react';
import {setState} from 'react';
import {create, rand} from 'random-seed';
import './Example.css'

const cipherLength = 51;
export class Example extends React.Component {
    constructor(props) {
        super(props);
        this.state = { // -1 represents '?'
            cipher: [36, 86, 49, 22, 64, 84, 67, 11, 44, 85, 91, 62, 96, 22, 
                37, 44, 55, 89, 96, 17, 25, 25, 77, 72, 38, 14, 0, 35, 90, 
                61, 11, 92, 65, 38, 59, 53, 21, 8, 36, 96, 25, 73],
            showCipher: new Array(cipherLength).fill(true),
            gridNumbers: [38, 58, 39, 88, 36, 42, 55, 84, 26, 63, 77, 20, 83, 
                98, 23, 16, 32, 60, 54, 92, 15, 91, 39, 30, 20, 72, 85, 21, 
                76, 40, 69, 61, 10, 4, 17, 37, 97, 84, 23, 54, 82, 84],
            showGrid: new Array(cipherLength).fill(4),
            series: [98, 28, 10, 34, 28, 42, 12, 27, 18, 22, 14, 42, 13, 24, 
                14, 28, 23, 29, 42, 25, 10, 34, 38, 42, 18, 42, 15, 14, 14, 
                21, 42, 31, 55, 34, 42, 16, 24, 24, 13, 42, 43, 89],
            // with 3 corrections to the intentional errors
            seriesFinal: [29, 8, 48, 60, 66, 50, 63, 64, 66, -1, 42, 53, 32, 45,
                67, 61, 70, 32, 55, 57, 60, 56, 11, 35, 17, 29, 32, 45, 56,
                52, 57, 70, 10, 31, 34, 32, 29, 33, 43, 51, 49, 47, 62, 14,
                33, 1, 49, 57, 57, 44, 34],
            showSeries: new Array(cipherLength).fill(true),
            seriesMarkings: [26, 42, 46],
            seriesMarking: -1,
            plaintext: [
                'WHO', 'S', 'A', 'Y', 'S', '/', 'C', 'R', 'I', 'M', 'E', '/', 'D', 'O', 'E', 'S', 'N', 'T', '/', 'P', 'A', 'Y', '?', 
                '/', 'I', '/', 'F', 'E', 'E', 'L', '/', 'V', 'ER', 'Y', '/', 'G', 'O', 'O', 'D', '/', 'ABOUT', 'THIS'
            ],
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
            markModuloWhich: -1, // 0 = cipher, 1 = cipher and pad, 2 = cipher + 100 (if needed), 3 = cipher, pad, and subtraction result, 4 = none
            // width: 13,
            // height: 4
            width: 14,
            height: 3,
            // ms: 80,
            ms: 80,
            // msModulo: 150,
            msModulo: 250,
            // msDecode: 150,
            msDecode: 250,
            msSlower: 250,
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
		this.modulo100 = this.modulo100.bind(this);
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
        this.modulo100Sub = this.modulo100Sub.bind(this);
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
        // this.setState({plaintextFinalMode: true});
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

    modulo100() {
        this.state.markModuloPos = 0;
        this.state.markModuloWhich = -1;
        this.modulo100Frame();
    }

    modulo100Frame() {
        if (this.state.markModuloPos == cipherLength) {
            // this.setState({
            //     markModuloPos: -1,
            //     markModuloWhich: -1,
            //     showSeries: new Array(cipherLength).fill(true)
            // });
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

        setTimeout(() => this.modulo100Frame(), this.state.msModulo);
    }

    cipherClassName(pos) {
        if (this.state.markModuloPos == -1) {
            return "content cipher-on";
        }
        // markModuloWhich: -1, // 0 = cipher, 1 = cipher and pad, 2 = cipher + 100 (if needed), 3 = cipher, pad, and subtraction result
        if (this.state.markModuloPos == pos) {
            if (this.state.markModuloWhich < 2)
                return "content cipher-on-modulo";
            if (this.state.markModuloWhich < 4)
                if (this.state.cipher[pos] < this.state.gridNumbers[pos])
                    return "content cipher-on-modulo-plus-100";
                else
                    return "content cipher-on-modulo";
        }
        return "content cipher-on-static";
    }
    gridClassName(pos) {
        if (this.state.markModuloPos != pos)
            return 'content grid-' + this.state.showGrid[pos]
        if (this.state.markModuloWhich == 1 || this.state.markModuloWhich == 2 || this.state.markModuloWhich == 3)
            return "content grid-on-modulo";
        return 'content grid-' + this.state.showGrid[pos]
    }
    seriesClassName(pos) {
        // console.log(pos, this.state.seriesMarking, this.state.showPlaintext[pos], this.state.markModuloPos, this.state.markModuloWhich);
        if (this.state.seriesMarking > -1 && this.state.seriesMarkings[this.state.seriesMarking] == pos)
            return 'content series-on-modulo';
        if (this.state.showPlaintext[pos] == 1 || this.state.showPlaintext[pos] == 2)
            return 'content series-on-modulo';
        if (this.state.markModuloPos != pos)
            return 'content';
        if (this.state.markModuloWhich == 3)
            return 'content series-on-modulo';
        return 'content';
    }

    decode() {
        this.decodeFrame(0);
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
            if (this.state.markModuloWhich < 3)
                return ' ';
            if (this.state.markModuloWhich == 3) {
                // if (this.state.series[pos] == -1) return '?';
                return (100 + this.state.cipher[this.state.markModuloPos] - this.state.gridNumbers[this.state.markModuloPos]) % 100;
            }
            return this.tr(this.state.series[this.state.markModuloPos]);
        }
    }

    cipherValue(pos) {
        let val = this.state.cipher[pos];
        // console.log(val, this.state.gridNumbers[pos], this.state.markModuloWhich);
        if ((this.state.markModuloWhich == 2 || this.state.markModuloWhich == 3) && pos == this.state.markModuloPos && val < this.state.gridNumbers[pos])
            return val + 100;
        return this.tr(val);
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
            size = 15;
        else if (val.length == 4) 
            size = 15;
        else if (val.length == 5) 
            size = 11;
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
        var idP = `p-cell-${pos}`;
        var idS = `s-cell-${pos}`;
        if (which < 2) 
            // if (pos == 26 || pos == 42 || pos == 46)
            //     document.getElementById(idP).className = "content plaintext-on-highlight wavy";
            // else
            document.getElementById(idP).className = "content plaintext-on-highlight";
        else
            document.getElementById(idP).className = "content plaintext-on";

        // if (which == 0 && pos == 32) {  // FUTURE TENSE                
        //     var idP = "p-cell-1-15";
        //     var idS = "s-cell-32";
        //     // highlight "future tense"
        //     document.getElementById(idP).innerHTML = "FUTURE TENSE";
        //     document.getElementById(idP).className = "content plaintext-on-highlight";
        //     document.getElementById(idP).style = "font-size: 9pt; letter-spacing: normal;";
        //     setTimeout(() => {
        //         // highlight the series number
        //         document.getElementById(idS).innerHTML = "10";
        //         document.getElementById(idS).className = "content series-on-modulo";
        //     }, this.state.msDecode);
        //     setTimeout(() => {
        //         // un highlight both
        //         this.substitutionsFrame(pos, 2);
        //     }, 2*this.state.msDecode);
        //     return;
        // }
        if (which == 1) {
            document.getElementById(idS).innerHTML = this.tr(this.state.series[pos]);
            document.getElementById(idS).className = "content series-on-modulo";
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

    modulo100Sub() {
       this.modulo100SubFrame(0, 0);
    }

    modulo100SubFrame(pos, which) {
        // 0: highlight series
        // 1: highlight grid number
        // 2: add to produce cipher number
        // 3: subtract 100 if needed
        // 4: highlight none

        if (which > 4) {
            which = 0;
            pos++;
        }

        if (pos == cipherLength) return;

        var idS = `s-cell-${pos}`;
        var s = this.state.series[pos];
        var idG = `g-cell-${pos}`;
        var g = this.state.gridNumbers[pos];

        // if (s < g) {
        //     s += 90;
        // }

        var idC = `c-cell-${pos}`;
        var c = this.state.cipher[pos];

        var elemS = document.getElementById(idS);
        var elemG = document.getElementById(idG);
        var elemC = document.getElementById(idC);

        if (which == 0) {
            elemS.className = "content series-on-modulo";
        } else if (which == 1) {
            elemG.className = "content grid-on-modulo";
        } else if (which == 2) {
            elemC.className = "content cipher-on-modulo";
            elemC.innerHTML = g + s;
            // elemG.className = "content grid-on-modulo";
        } else if (which == 3) {
            if (g + s >= 100) {
                elemC.innerHTML = (g + s) % 100;
                elemC.className = "content cipher-on-modulo-plus-100";
            } else
                elemC.className = "content cipher-on-modulo";
        } else if (which == 4) {
            elemC.className = "content cipher-on-static";
            elemG.className = "content grid-on";
            elemS.className = "content series-on";
        }
        setTimeout(() => this.modulo100SubFrame(pos, which+1), this.state.msDecode);
    }

    render() {
        let html = [];
        let rows = [];
        for (let row=0; row<this.state.height; row++) {
            // ciphertext:
            let cols = [];
            for (let col=0; col<this.state.width; col++) {
                let pos = row * this.state.width + col;
                const id = `c-cell-${pos}`;
                if (this.state.showCipher[pos] && pos < this.state.cipher.length) {
                    cols.push(<td key={id} id={id} className={this.cipherClassName(pos)}>{this.cipherValue(pos)}</td>);
                } else cols.push(<td key={id} id={id} className="content cipher-off">&nbsp;</td>);
            }
            rows.push(<tr key={'c-' + row} className="row-cipher"><td className="row-cipher-prefix">A (Cipher):</td>{cols}</tr>);
            // grid numbers:
            cols = [];
            for (let col=0; col<this.state.width; col++) {
                let pos = row * this.state.width + col;
                const id = `g-cell-${pos}`;
                // if (this.state.showGrid[pos] && pos < this.state.gridNumbers.length) {
                //     cols.push(<td class="content grid-on">{this.state.gridNumbers[pos]}</td>);
                // } else cols.push(<td class="content grid-off">&nbsp;</td>);
                cols.push(<td key={id} id={id} className={this.gridClassName(pos)}>{this.tr(this.state.gridNumbers[pos])}</td>);
            }
            rows.push(<tr key={'g-' + row} className="row-grid"><td className="row-grid-prefix">B (Pad):</td>{cols}</tr>);
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
                <button onClick={this.modulo100}>Modulo 100</button>

                {/* <button onClick={this.decode}>Decode</button>
                <button onClick={this.showAdjustments}>Show adjustments</button>
                <button onClick={this.togglePlaintextFinalMode}>Toggle final plaintext and series</button>
                <button onClick={this.showAdjustments2}>Show adjustments2</button>
                <button onClick={this.showSeriesCorrections}>Show series corrections</button> (do before and after)
                */}
                <br/>
                Scripted order:
                <br/>
                <button onClick={this.initToggle}>Init</button>
                <button onClick={this.toggleShowCipher}>Toggle cipher</button>
                <button onClick={this.toggleShowGrid}>Toggle grid</button>
                <button onClick={this.modulo100}>Modulo 100</button>
                <button onClick={this.decode}>Decode</button>
                {/* <button onClick={this.showAdjustments}>Show adjustments</button>
                <button onClick={this.showSeriesCorrections}>Show series corrections</button>
                <button onClick={this.togglePlaintextFinalMode}>Toggle final plaintext and series</button>
                <button onClick={this.showSeriesCorrections}>Show series corrections</button>
                <button onClick={this.showAdjustments2}>Show adjustments2</button>
                <button onClick={this.readPlaintext}>Read plaintext</button> */}
                Scripted order (plaintext to cipher): 
                <button onClick={this.initToggle2}>Init2</button>
                <button onClick={this.substitutions}>Substitutions</button>
                <button onClick={this.toggleShowGrid}>Toggle grid</button>
                <button onClick={this.modulo100Sub}>Modulo 100</button>
                {/* <button onClick={this.fruh}>Fruhstuck</button>
                <button onClick={this.question}>Question mark</button>
                <button onClick={this.every}>Every</button>
                <button onClick={this.mistakes}>Mistakes</button>
                <button onClick={this.toggleShowGrid}>Toggle grid</button>
                <button onClick={this.modulo90Sub}>Modulo 90</button> */}

            </div>
        );

        return html;
    }
}


export default Example;