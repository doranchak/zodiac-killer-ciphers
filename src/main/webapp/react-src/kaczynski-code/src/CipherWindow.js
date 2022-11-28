import React from 'react';
import {setState} from 'react';
import {create, rand} from 'random-seed';
import './CipherWindow.css'


var cipherSeq = [5];
var r = create(12345);
for (var i=0; i<12000; i++) cipherSeq.push(r.intBetween(1, 89));

var p1count = 42*50;
var totalLength = 42*54;

class CipherWindow extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            matrix: props.matrix, // current dimensions: 42 W x 54 H  (total length: 2268)
            height: props.matrix.length, 
            width: props.matrix[0].length, 
            current_row: 4,
            current_col: 0,
            rows_cols: '',
            cipher: cipherSeq,
            currentUnit: 0,
            windowSize: 20,
            shift: 10,
            writingGridNumbers: true,
            ms: 70,
            phase3map: props.phase3map,
            phase3numbers: props.phase3numbers,
            phase4map: props.phase4map,
            phase4numbers: props.phase4numbers
        }
        // H = this.state.matrix.length;
        // W = this.state.matrix[0].length;
        this.phase1 = this.phase1.bind(this);
        this.phase2 = this.phase2.bind(this);
        this.phase3 = this.phase3.bind(this);
        this.phase4 = this.phase4.bind(this);
        this.phase5 = this.phase5.bind(this);
        this.nextUnit = this.nextUnit.bind(this);
        this.cipherBlock = this.cipherBlock.bind(this);
        this.gridNumber = this.gridNumber.bind(this);

        console.log("matrix height " + this.state.height + " width " + this.state.width + " total length " + this.state.height*this.state.width);
    }

    phase1() { // L-R, T-B, missing first 4 rows
        if (this.state.currentUnit == p1count) return;
        this.nextUnit();
        setTimeout(() => this.phase1(), this.state.ms);
    }

    phase2() { // T-B, R-L
        if (this.state.currentUnit == p1count + totalLength) return;
        this.nextUnit();
        setTimeout(() => this.phase2(), this.state.ms);
    }

    phase3() { // diagonally north east starting at top left corner
        if (this.state.currentUnit == p1count + 2*totalLength) return;
        this.nextUnit();
        setTimeout(() => this.phase3(), this.state.ms);
    }

    phase4() { // diagonally south east starting at top right corner
        if (this.state.currentUnit == p1count + 3*totalLength) return;
        this.nextUnit();
        setTimeout(() => this.phase4(), this.state.ms);
    }
    phase5() { // phase 1 again, starting at beginning
        if (this.state.currentUnit == p1count + 4*totalLength) return;
        this.nextUnit();
        setTimeout(() => this.phase5(), this.state.ms);
    }

    nextUnit() {
        this.setState({currentUnit: this.state.currentUnit+1});
    }

    cipherBlock() {
        var arr = [];

        // cipher sequence
        for (var i=0; i<=this.state.windowSize; i++) {
            var pos = this.state.currentUnit + i;
            var pos2 = pos - this.state.shift;


            if (pos2 < 0) {
                arr.push(<><span class="c" key={pos2}>&nbsp;&nbsp;</span>&nbsp;&nbsp;</>);
            } else {
                var c = this.state.cipher[pos2];
                var strNum = (c < 10 ? '0' : '') + c;
                var strComma = ', ';
                var id = "c" + i;
                var cl = pos2 == this.state.currentUnit ? "c-highlight" : "c";
                if (pos2 == 0) cl = "c-row";
                arr.push(<><span class={cl} id={id} key={id}>{strNum}</span>{strComma}</>);
            }
        }

        return arr;
    }

    gridNumberBlock() {
        var arr = [];
        // grid number sequence 
        if (this.state.writingGridNumbers) {
            for (var i=0; i<this.state.windowSize/2+1; i++) {
                var pos = this.state.currentUnit + i;
                var pos2 = pos - this.state.shift - 1;
    
                var str;
    
                if (pos2 < 0) {
                    arr.push(<><span class="g" key={pos2}>&nbsp;&nbsp;</span>&nbsp;&nbsp;</>);
                } else {
                    var g = this.gridNumber(pos2+this.state.matrix[0].length*4);
                    var strNum = (g < 10 ? '0' : '') + g;
                    var strComma = ', ';
                    var id = "g" + i;
                    var cl = pos2 == this.state.currentUnit - 1 ? "g-highlight" : "g";
                    arr.push(<><span class={cl} id={id} key={id}>{strNum}</span>{strComma}</>);
                }
            }
        }

        return arr;
    }

    gridNumber(pos) {
        var phase = this.phase(pos);
        var H = this.state.matrix.length;
        var W = this.state.matrix[0].length;
        var row, col;
        if (phase == 1) {
            row = parseInt(pos/W);
            col = pos % W;
            // console.log("phase1 " + pos + " currentUnit " + this.state.currentUnit + " " + row + " " + col + " " + this.state.matrix[row][col]);
        } else if (phase == 2) {
            pos = pos % totalLength;
            row = pos % H;
            col = W - 1 - parseInt(pos/H);
            // console.log("phase2 " + pos + " currentUnit " + this.state.currentUnit + " " + row + " " + col + " " + this.state.matrix[row][col]);
        } else if (phase == 3) {
            pos = pos % totalLength;
            row = this.state.phase3map[pos][0];
            col = this.state.phase3map[pos][1];
        } else if (phase == 4) {
            pos = pos % totalLength;
            row = this.state.phase4map[pos][0];
            col = this.state.phase4map[pos][1];
        } else if (phase == 5) {
            pos = pos % totalLength;
            row = parseInt(pos/W);
            col = pos % W;
        }
        return this.state.matrix[row][col];
    }

    phase(pos) {
        if (pos < totalLength) return 1;
        if (pos < 2*totalLength) return 2;
        if (pos < 3*totalLength) return 3;
        if (pos < 4*totalLength) return 4;
        return 5;
    }

    render() {
        return (
            <div id="cipherwrapper">
                <button onClick={this.nextUnit}>Next cipher unit</button>
                <button onClick={this.phase1}>Phase I</button>
                <button onClick={this.phase2}>Phase II</button>
                <button onClick={this.phase3}>Phase III</button>
                <button onClick={this.phase4}>Phase IV</button>
                <button onClick={this.phase5}>Phase V</button>
                <div id="cipher">{this.cipherBlock()}</div>
                <div id="grid-number-block">{this.gridNumberBlock()}</div>        
            </div>
        );
    }
}

export default CipherWindow;