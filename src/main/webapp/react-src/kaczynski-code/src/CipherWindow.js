import React from 'react';
import {setState} from 'react';
import {create, rand} from 'random-seed';
import './CipherWindow.css'


var cipherSeq = [5];
var r = create(12345);
for (var i=0; i<12000; i++) cipherSeq.push(r.intBetween(1, 89));

class CipherWindow extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            matrix: props.matrix,
            height: props.matrix.length,
            width: props.matrix[0].length,
            current_row: 4,
            current_col: 0,
            rows_cols: '',
            cipher: cipherSeq,
            currentUnit: 0,
            windowSize: 20,
            shift: 10,
            writingGridNumbers: true
        }
        // H = this.state.matrix.length;
        // W = this.state.matrix[0].length;
        this.writeGridNumbers = this.writeGridNumbers.bind(this);
        this.nextUnit = this.nextUnit.bind(this);
        this.cipherBlock = this.cipherBlock.bind(this);
        this.gridNumber = this.gridNumber.bind(this);
    }

    writeGridNumbers() {
        // if (!this.state.writingGridNumbers) {
        //     this.state.writingGridNumbers = true;
        //     this.state.current_row = 4;
        //     this.state.current_col = 0;
        // }
        this.nextUnit();
        setTimeout(() => this.writeGridNumbers(), 250);
    }

    nextUnit() {
        this.setState({currentUnit: this.state.currentUnit+1});
        // this.jumpToCipherPosition(curr, true);
    }

    cipherBlock() {
        var arr = [];

        // cipher sequence
        for (var i=0; i<this.state.windowSize; i++) {
            var pos = this.state.currentUnit + i;
            var pos2 = pos - this.state.shift;


            if (pos2 < 0) {
                arr.push(<><span class="c">&nbsp;&nbsp;</span>&nbsp;&nbsp;</>);
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
                    arr.push(<><span class="g">&nbsp;&nbsp;</span>&nbsp;&nbsp;</>);
                } else {
                    var g = this.gridNumber(pos2+this.state.matrix[0].length*4);
                    var strNum = (g < 10 ? '0' : '') + g;
                    var strComma = ', ';
                    var id = "g" + i;
                    var cl = pos2 == this.state.currentUnit - 1 ? "g-highlight" : "g";
                    arr.push(<><span class={cl} id={id} key={id}>{strNum}</span>{strComma}</>);
                    console.log(g, strNum, cl, pos2, this.state.currentUnit);
                }
            }
        }

        return arr;
    }

    gridNumber(pos) {
        var W = this.state.matrix[0].length;
        var row = parseInt(pos/W);
        var col = pos % W;
        return this.state.matrix[row][col];
    }

    render() {
        return (
            <div id="cipherwrapper">
                <button onClick={this.nextUnit}>Next cipher unit</button>
                <button onClick={this.writeGridNumbers}>Write grid numbers</button>
                <div id="cipher">{this.cipherBlock()}</div>
                <div id="grid-number-block">{this.gridNumberBlock()}</div>        
            </div>
        );
    }
}

export default CipherWindow;