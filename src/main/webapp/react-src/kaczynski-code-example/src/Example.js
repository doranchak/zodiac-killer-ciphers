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
            showSeries: new Array(cipherLength).fill(true),
            plaintext: [
                'I ME MINE MY', 'WILL HAVE', 'F', 'R', 'U', 'H', 'S', 'T', 'U', '_', 'C', 'K', '/', 'E', 'V', 'R', 'Y', '/', 
                'M', 'O', 'R', 'N', 'ING', ',', 'AND', 'I', 'C', 'E', 'N', 'J', 'O', 'Y', 'FUTURE TENSE', 'HE SHE IT HIM HER HIS HERS ITS', 
                '.', '/', 'MY', '/', 'D', 'I', 'G', 'E', 'J', 'TION', '/', 'IS', 'A', 'O', 'O', 'D', '.'],
            showPlaintext: new Array(cipherLength).fill(true),
            markModuloPos: -1, // which pair of numbers are we adding via modulo 90 addition?
            markModuloWhich: -1, // 0 = cipher, 1 = cipher and grid number, 2 = series before modulo 90, 3 = series after modulo 90
            // width: 13,
            // height: 4
            width: 17,
            height: 3,
            ms: 80,
            msModulo: 250,
            // showCipher: true,
            // showGrid: true,
            // showSeries: true,
            // showPlaintext: true
        }
        this.toggleShowCipher = this.toggleShowCipher.bind(this);
		this.toggleShowGrid = this.toggleShowGrid.bind(this);
		this.toggleShowSeries = this.toggleShowSeries.bind(this);
		this.toggleShowPlaintext = this.toggleShowPlaintext.bind(this);
		this.modulo90 = this.modulo90.bind(this);
        // console.log(this.state.cipher.length, this.state.gridNumbers.length, this.state.series.length, this.state.plaintext.length);
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
        const show = !this.state.showPlaintext[0];
        this.setState({showPlaintext: new Array(cipherLength).fill(show)});
	}

    modulo90() {
        this.state.markModuloPos = 0;
        this.state.markModuloWhich = -1;
        this.modulo90Frame();
    }

    modulo90Frame() {
        if (this.state.markModuloPos == cipherLength) return;
        console.log(this.state.markModuloPos, this.state.markModuloWhich);
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
        if (this.state.markModuloPos != pos)
            return 'content';
        if (this.state.markModuloWhich == 2)
            return 'content series-on-modulo';
        if (this.state.markModuloWhich == 3)
            return 'content series-on-modulo-2';
        return 'content';
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
                    cols.push(<td class={this.cipherClassName(pos)}>{this.tr(this.state.cipher[pos])}</td>);
                } else cols.push(<td class="content cipher-off">&nbsp;</td>);
            }
            rows.push(<tr class="row-cipher"><td class="row-cipher-prefix">Cipher:</td>{cols}</tr>);
            // grid numbers:
            cols = [];
            for (let col=0; col<this.state.width; col++) {
                let pos = row * this.state.width + col;
                // if (this.state.showGrid[pos] && pos < this.state.gridNumbers.length) {
                //     cols.push(<td class="content grid-on">{this.state.gridNumbers[pos]}</td>);
                // } else cols.push(<td class="content grid-off">&nbsp;</td>);
                cols.push(<td class={this.gridClassName(pos)}>{this.tr(this.state.gridNumbers[pos])}</td>);
            }
            rows.push(<tr class="row-grid"><td class="row-grid-prefix">Grid:</td>{cols}</tr>);
            // series:
            cols = [];
            for (let col=0; col<this.state.width; col++) {
                let pos = row * this.state.width + col;
                // if (this.evalSeries(pos)) {
                cols.push(<td class={this.seriesClassName(pos)}>{this.seriesValue(pos)}</td>);
                // } else cols.push(<td class="content">&nbsp;</td>);
            }
            rows.push(<tr class="row-series"><td class="row-series-prefix">Series:</td>{cols}</tr>);
            // plaintext:
            cols = [];
            for (let col=0; col<this.state.width; col++) {
                let pos = row * this.state.width + col;
                if (this.state.showPlaintext[pos] && pos < this.state.plaintext.length) {
                    cols.push(<td class="content" style={this.fontSizeFor(this.state.plaintext[pos])}>{this.state.plaintext[pos]}</td>);
                } else cols.push(<td class="content">&nbsp;</td>);
            }
            rows.push(<tr class="row-plaintext"><td class="row-plaintext-prefix">Decoded:</td>{cols}</tr>);
        }
        html.push(<table id="example-table">{rows}</table>);

        // buttons
        html.push(
            <div class="buttons">
                <button onClick={this.toggleShowCipher}>Toggle cipher</button>
                <button onClick={this.toggleShowGrid}>Toggle grid</button>
                <button onClick={this.toggleShowSeries}>Toggle series</button>
                <button onClick={this.toggleShowPlaintext}>Toggle plaintext</button>
                <button onClick={this.modulo90}>Modulo 90</button>
            </div>
        );

        return html;
    }

    seriesValue(pos) {
        console.log('seriesValue', pos, this.state.markModuloPos, this.state.markModuloWhich);
        if (this.state.markModuloPos == -1) {
            if (!this.state.showSeries[pos])
                return ' ';
            return this.tr(this.state.series[pos]);
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
        // if (pos < this.state.markModuloPos)
        // } else return this.tr(this.state.series[pos]);
    }

    tr(val) {
        return val == -1 ? '?' : val;
    }
    fontSizeFor(val) {
        var size;
        if (val.length < 3)
            size = 20;
        if (val.length == 3) 
            size = 17;
        if (val.length == 4) 
            size = 13;
        if (val.includes("HE SHE")) 
            size = 7;
        if (val.includes("FUTURE")) 
            size = 9;
        if (val.includes("I ME MINE")) 
            size = 9;
        if (val.includes("WILL HAVE")) 
            size = 13;   
        return {fontSize: size + "pt"};
        // min is 8pt for length 30
        // max is 20pt for length <= 3
        // so length range is [3,30] (interval of 27)
        // and font range is [8,20] (interval of 12)

        // const ratio = 1 - (val.length - 3) / 27;
        // const size = ratio * 12 + 8;

        // return {fontSize: size + "pt"};
    }
    
}


export default Example;