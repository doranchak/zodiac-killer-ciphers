import React from 'react';
import {setState} from 'react';
import {create, rand} from 'random-seed';
import './Matrix.css'
import CipherWindow from './CipherWindow';

var W;
var H;
const centerX = 400;
const centerY = 200;
const squareWidth = 26.2; // [26 drifts right,27 drifts left]
const squareHeight = 25; // [24.5 drifts down, 25.5 drifts up]

export class Matrix extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            matrix: props.matrix,
            height: props.matrix.length,
            width: props.matrix[0].length,
            current_row: 0,
            current_col: 0,
            rows_cols: '',
            currentUnit: 0,
            phase3map: [], // map linear position to phase3 grid location
            phase3numbers: [], // grid numbers in phase3 order
            phase4map: [], // map linear position to phase4 grid location
            phase4numbers: [], // grid numbers in phase4 order
            ms: 200
        }
        H = this.state.matrix.length;
        W = this.state.matrix[0].length;
        this.handleClick = this.handleClick.bind(this);
        this.phase1 = this.phase1.bind(this);
        this.phase2 = this.phase2.bind(this);
        this.phase3 = this.phase3.bind(this);
        this.phase4 = this.phase4.bind(this);
        this.phase5 = this.phase5.bind(this);
        this.phase6 = this.phase6.bind(this);
        this.direction1cipher = this.direction1cipher.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.oneDigit = this.oneDigit.bind(this);
        this.gridCountOnes = this.gridCountOnes.bind(this);
        this.gridCountOnesSub = this.gridCountOnesSub.bind(this);
        this.findAdjacent = this.findAdjacent.bind(this);
        this.makeVisited = this.makeVisited.bind(this);
        this.number = this.number.bind(this);
        this.clearGrid = this.clearGrid.bind(this);
        this.moveViewport = this.moveViewport.bind(this);
        this.makePhase3Map();
        this.makePhase4Map();
    }

    handleClick(e) {
        // this.toggle(e.target);
        //console.log(this.state.highlights);
    }

    moveViewport(row, col) {
        var x = centerX - col * squareWidth;
        var y = centerY - row * squareHeight;
        var elem = document.getElementById("root");
        elem.style.left = x + "px";
        elem.style.top = y + "px";
    }

    phase1() {
        this.hideButtons();
        this.state.current_row = 4;
        this.state.current_col = 0;
        this.moveViewport(this.state.current_row, this.state.current_col);
        setTimeout(() => this.phase1Loop(), 1000);
    }
    phase1Loop() {
        this.toggle(document.getElementById(this.state.current_row + "_" + this.state.current_col));
        this.moveViewport(this.state.current_row, this.state.current_col);
        this.state.current_col++;
        if (this.state.current_col == W) {
            this.state.current_row++;
            this.state.current_col = 0;
        }
        if (this.state.current_row == H) return;
        setTimeout(() => this.phase1Loop(), this.state.ms);
    }
    phase2() {
        this.hideButtons();
        this.state.current_row = 0;
        this.state.current_col = W-1;
        this.moveViewport(this.state.current_row, this.state.current_col);
        setTimeout(() => this.phase2Loop(), 1000);
    }
    phase2Loop() {
        this.toggle(document.getElementById(this.state.current_row + "_" + this.state.current_col));
        this.moveViewport(this.state.current_row, this.state.current_col);
        this.state.current_row++;
        if (this.state.current_row == H) {
            this.state.current_col--;
            this.state.current_row = 0;
        }
        if (this.state.current_col < 0) return;
        setTimeout(() => this.phase2Loop(), this.state.ms);
    }
    phase3() {
        this.hideButtons();
        this.state.current_row = 0;
        this.state.current_col = 0;
        const [row, col] = this.state.phase3map[0];
        this.moveViewport(row, col);
        setTimeout(() => this.phase3Loop(), 1000);
    }
    phase3Loop() {
        const pos = this.state.current_row * W + this.state.current_col;
        const [row, col] = this.state.phase3map[pos];
        this.moveViewport(row, col);
        this.toggle(document.getElementById(row + "_" + col));
        this.moveViewport();
        this.state.current_col++;
        if (this.state.current_col == W) {
            this.state.current_row++;
            this.state.current_col = 0;
        }
        if (this.state.current_row == H) return;
        setTimeout(() => this.phase3Loop(), this.state.ms);
    }
    phase4() {
        this.hideButtons();
        this.state.current_row = 0;
        this.state.current_col = 0;
        const [row, col] = this.state.phase4map[0];
        this.moveViewport(row, col);
        setTimeout(() => this.phase4Loop(), 1000);
    }
    phase4Loop() {
        const pos = this.state.current_row * W + this.state.current_col;
        const [row, col] = this.state.phase4map[pos];
        // console.log(pos + " " + row + " " + col);
        this.moveViewport(row, col);
        this.toggle(document.getElementById(row + "_" + col));
        this.moveViewport();
        this.state.current_col++;
        if (this.state.current_col == W) {
            this.state.current_row++;
            this.state.current_col = 0;
        }
        if (this.state.current_row == H) return;
        setTimeout(() => this.phase4Loop(), this.state.ms);
    }
    phase5() {
        this.hideButtons();
        this.state.current_row = 0;
        this.state.current_col = 0;
        this.moveViewport(this.state.current_row, this.state.current_col);
        setTimeout(() => this.phase5Loop(), 1000);
    }
    phase5Loop() {
        this.toggle(document.getElementById(this.state.current_row + "_" + this.state.current_col));
        this.moveViewport(this.state.current_row, this.state.current_col);
        this.state.current_col++;
        if (this.state.current_col == W) {
            this.state.current_row++;
            this.state.current_col = 0;
        }
        if (this.state.current_row == H) return;
        setTimeout(() => this.phase5Loop(), this.state.ms);
    }

    phase6() {
        this.hideButtons();
        this.state.current_row = 9;
        this.state.current_col = 4;
        this.moveViewport(this.state.current_row, this.state.current_col);
        setTimeout(() => this.phase6Loop(), 1000);
    }
    phase6Loop() {
        this.toggle6(this.state.current_row, this.state.current_col, 0);
        this.toggle6(this.state.current_row, this.state.current_col, 1);
        this.toggle6(this.state.current_row, this.state.current_col, 2);
        this.toggle6(this.state.current_row, this.state.current_col, 3);
        this.toggle6(this.state.current_row, this.state.current_col, 4);
        // this.toggle(document.getElementById(this.state.current_row + "_" + this.state.current_col));
        if (this.state.current_row == 9 || this.state.current_col < 13)
            this.moveViewport(this.state.current_row, this.state.current_col);
        this.state.current_col++;
        if (this.state.current_col == W) {
            this.state.current_row++;
            this.state.current_col = 0;
        }
        if (this.state.current_row == 10 && this.state.current_col == 17) return;
        setTimeout(() => this.phase6Loop(), this.state.ms);
    }

    toggle6(row, col, offset) { // [382, 432]
        const pos = row*W + col - offset;
        // if (offset == 0) console.log(pos, row, col);
        if (pos < 382 || pos > 432) return;
        const row2 = parseInt(pos/W);
        const col2 = pos % W;
        document.getElementById(row2 + '_' + col2).className = 'phase6-' + offset;
    }

    direction1cipher() {
        if (this.state.current_row == this.state.height) return;
        var pos = this.state.current_row * this.state.width + this.state.current_col;
        this.jumpToCipherPosition(pos, true, this.state.matrix[this.state.current_row][this.state.current_col]);
        this.state.current_col++;
        if (this.state.current_col == this.state.width) {
            this.state.current_row++;
            this.state.current_col = 0;
        }
        setTimeout(() => this.direction1cipher(), 70);
    }

    direction2() {
        this.toggle(document.getElementById(this.state.current_row + "_" + this.state.current_col));
        //console.log(this.state.current_col, this.state.current_row, this.state.width, this.state.height);
        this.state.current_col++;
        if (this.state.current_col == this.state.width) {
            this.state.current_row++;
            this.state.current_col = 0;
        }
        if (this.state.current_row == this.state.height) return;
        setTimeout(() => this.direction1(), 10);
    }

    handleSubmit(event) {
        try {
            var lines = this.state.rows_cols.trim().split("\n");
            for (var i=0; i<lines.length; i++) {
                var line = lines[i];
                let colors = this.randomColor();
                var s = line.split(" ");
                for (const ss in s) {
                    var rc = s[ss].split(",");
                    var r = rc[0];
                    const c = rc[1];
                    this.markColor(r,c, colors[0], colors[1]);
                }
            }
        }
            catch(err) {
            console.log(err);
        }
        event.preventDefault();
    }

    randomColor() {
		var bg = this.randomRGB();
		var fg = this.textColorFor(bg);
		var currentBG = this.rgbToHex(bg);
		var currentFG = this.rgbToHex(fg);
        return [currentBG, currentFG];
	}
	randomRGB() {
		var r = Math.floor(Math.random()*256);
		var g = Math.floor(Math.random()*256);
		var b = Math.floor(Math.random()*256);
		return [r, g, b];
	}
	
	/* from https://trendct.org/2016/01/22/how-to-choose-a-label-color-to-contrast-with-background/ */
	brightness(rgb) {
		var r = rgb[0];
		var g = rgb[1];
		var b = rgb[2];
		return (r * 299 + g * 587 + b * 114) / 1000;
	}
	textColorFor(rgb) {
		var b = this.brightness(rgb);
		return b > 123 ? [0,0,0] : [255,255,255];
	}
    componentToHex(c) {
        var hex = c.toString(16);
        return hex.length == 1 ? "0" + hex : hex;
    }
    
    rgbToHex(rgb) {
        return "#" + this.componentToHex(rgb[0]) + this.componentToHex(rgb[1]) + this.componentToHex(rgb[2]);
    }
        

    handleChange(event) {
        this.setState({rows_cols: event.target.value});
        //alert(event.target.value);
    }

    oneDigit() {
        const colors = [];
        for (var i=0; i<10; i++) {
            colors[i] = this.randomColor();
        }
        const m = this.state.matrix;
        for (var row=0; row<m.length; row++) {
            for (var col=0; col<m[row].length; col++) {
                const val = m[row][col];
                if (val < 10) {
                    this.markColor(row,col,colors[val][0], colors[val][1]);
                    // this.markColor(row,col,"#5ad2da","black");
                }
            }
        }
    }

    number(num) {
        const m = this.state.matrix;
        for (var row=0; row<m.length; row++) {
            for (var col=0; col<m[row].length; col++) {
                const val = m[row][col];
                if (val == num)
                    this.mark(row,col);
                else 
                    this.markClear(row,col);
            }
        }
    }

    toggle(e) {
        e.className = "off";
        // setTimeout(() => e.className = "on", 10);
        e.className = "on"; // e.className == "on" ? "off" : "on";
    }
    mark(row, col) {
        const e = document.getElementById(row+"_"+col);
        if (e) e.className = "mark";
    }
    markClear(row, col) {
        const e = document.getElementById(row+"_"+col);
        if (e) e.className = "";
    }
    markColor(row, col, colorBG, colorFG) {
        const e = document.getElementById(row+"_"+col);
        if (e) {
            e.style.color = colorFG;
            e.style.backgroundColor = colorBG;
        }
    }

    makeVisited(height, width) {
        var visited = [];
        for (var row=0; row<height; row++) {
            visited[row] = [];
            for (var col=0; col<width; col++) {
                visited[row][col] = 0;
            }
        }
        return visited;
    }

    /* for each position in the grid, count the number of one digit numbers at or immediately adjacent (horizontally, vertically, and diagonally) to this position */
    gridCountOnes() {
        var m = this.state.matrix;
        var counts = []; 
        for (var row=0; row<m.length; row++) {
            counts[row] = [];
            for (var col=0; col<m[row].length; col++) {
                counts[row][col] = -1;
            }
        }

        for (var row=0; row<m.length; row++) {
            for (var col=0; col<m[row].length; col++) {
                var found = [];
                this.findAdjacent(this.makeVisited(H, W), row, col, found);
                counts[row][col] = found.length;
            }
        }

        console.log(counts);

        for (var row=0; row<H; row++) {
            for (var col=0; col<W; col++) {
                if (counts[row][col] > 9) {
                    this.mark(row,col);
                }
            }
        }
    }

    findAdjacent(visited, row, col, found) {
        //console.log("wha",row,col);
        if (visited[row][col]) return; // already here
        visited[row][col] = true;
        var val = this.state.matrix[row][col];
        if (val > 9) return; // 2-digit number here, so exit

        // this is one digit number, so add it to results
        found.push([row, col]);

        for (var r=Math.max(row-1, 0); r<=row+1 && r<H; r++) {
            for (var c=Math.max(col-1, 0); c<=col+1 && c<W; c++) {
                if (r==row && c==col) continue; // ignore current position
                this.findAdjacent(visited, r, c, found);
            }
        }
        
    }

    gridCountOnesSub(counts, row, col, level) {
        //console.log(row,col,level, counts[0]);
        var count = counts[row][col];

        // return count if we already have it
        if (count > -1) return count;
        if (count == -2) return 0; // ignore this position which is involved in recursion

        // mark so we don't visit it again prematurely
        counts[row][col] = -2;

        
        var val = this.state.matrix[row][col];

        // two-digit number, so return 0
        if (val > 9) {
            counts[row][col] = 0;
            return 0;
        }

        // check all adjacent non-visited positions.
        // if any have a count, then it will be the same at current position, so return that.
        // if none have a count, then return the (level+1) for the current position.

        for (var r=Math.max(row-1, 0); r<=row+1 && r<H; r++) {
            for (var c=Math.max(col-1, 0); c<=col+1 && c<W; c++) {
                if (r==row && c==col) continue; // ignore current position
                count = this.gridCountOnesSub(counts, r, c, level+1);
                if (count > 0) {
                    // adjacent position was already computed so current position result will be identical.
                    counts[row][col] = count;
                    return count;
                }
            }
        }
        // if we made it here, no more adjacent single digits can be found.  so max size is level+1.
        counts[row][col] = level+1;
        return level+1;
    }

    clearGrid() {
        document.getElementById("matrix").innerHTML = "";
    }

    makePhase3MapOBSOLETE() {
        var row = 0,col = 0;
        var rowStart = 0;
        var colStart = 0;
        console.log(this.state.height + " " + this.state.width);
        for (var pos=0; pos<this.state.height*this.state.width; pos++) {
            console.log(row + " " + col + " " + this.state.matrix[row][col]);
            this.state.phase3numbers.push(this.state.matrix[row][col]);
            this.state.phase3map.push([row,col]);
            col++;
            row--;
            if (row < 0) {
                if (rowStart < this.state.height - 1) {
                    rowStart++;
                } else  {
                    colStart++;
                }
                row = rowStart;
                col = colStart;
            } else if (col == this.state.width) {
                if (rowStart < this.state.height - 1) {
                    rowStart++;
                } else  {
                    colStart++;
                }
                row = rowStart;
                col = colStart;
            }
        }
        console.log('phase3 map ', this.state.phase3map);
        console.log('phase3 nums ', this.state.phase3numbers);
    }

    makePhase3Map() {
        for (var rowStart = 0; rowStart < this.state.height; rowStart++) {
            for (var col=0; col<this.state.width; col++) {
                var row = rowStart - col;
                if (row < 0) break;
                this.state.phase3map.push([row, col]);
                this.state.phase3numbers.push(this.state.matrix[row][col]);
            }
        }
        for (var colStart = 1; colStart < this.state.width; colStart++) {
            for (var rowStart = 0; rowStart < this.state.height; rowStart++) {
                var col = colStart + rowStart;
                var row = this.state.height - 1 - rowStart;
                if (col == this.state.width) break;
                if (row < 0) break;
                this.state.phase3map.push([row, col]);
                this.state.phase3numbers.push(this.state.matrix[row][col]);
            }
        }
    }

    makePhase4Map() {
        for (var colStart = this.state.width - 1; colStart >= 0; colStart--) {
            for (var row=0; row<this.state.height; row++) {
                var col = colStart + row;
                if (col >= this.state.width) break;
                this.state.phase4map.push([row, col]);
                this.state.phase4numbers.push(this.state.matrix[row][col]);
            }
        }
        for (var rowStart = 1; rowStart < this.state.height; rowStart++) {
            for (var col=0; col<this.state.width; col++) {
                var row = rowStart + col;
                if (row >= this.state.height) break;
                this.state.phase4map.push([row, col]);
                this.state.phase4numbers.push(this.state.matrix[row][col]);
            }
        }
    }

    hideButtons() {
        document.getElementById("buttons").style.display="none";
        document.getElementById("cipherwrapper").style.display="none";
    }

    render() {
        const m = this.state.matrix;
        var rows = [];
        for (var i=0; i<m.length; i++) {
            var cols = [];
            for (var j=0; j<m[i].length; j++) {
                var key = i + "_" + j;
                //var cl = this.state.highlights[i][j] ? "on" : "off";
                cols.push(<td onMouseOver={this.handleClick} id={key} key={key}>{m[i][j]}</td>);
            }
            var key = i;
            rows.push(<tr key={key}>{cols}</tr>);
        }

        return (
            <div className="wrapper">

                <table className="table-wrapper">
                    <thead></thead>
                    <tbody>
                        <tr>
                            <td>
                                <table id="matrix" className="matrix"><thead></thead><tbody>{rows}</tbody></table>
                            </td>
                            <td className="buttons" id="buttons">
                                <button onClick={this.phase1}>Phase I</button><br/>
                                <button onClick={this.phase2}>Phase II</button><br/>
                                <button onClick={this.phase3}>Phase III</button><br/>
                                <button onClick={this.phase4}>Phase IV</button><br/>
                                <button onClick={this.phase5}>Phase V</button>
                                <button onClick={this.phase6}>Phase VI</button>
                                <br></br>
                                <form onSubmit={this.handleSubmit}>
                                    <textarea value={this.state.rows_cols} onChange={this.handleChange}></textarea><br></br>
                                    <input type="submit" value="Mark"></input>
                                </form>
                                <button onClick={this.oneDigit}>One digit</button><br></br>
                                <button onClick={this.gridCountOnes}>Count ones</button><br></br>
                                <button onClick={() => this.number(0)}>0</button>
                                <button onClick={() => this.number(1)}>1</button>
                                <button onClick={() => this.number(2)}>2</button>
                                <button onClick={() => this.number(3)}>3</button><br/>
                                <button onClick={() => this.number(4)}>4</button>
                                <button onClick={() => this.number(5)}>5</button>
                                <button onClick={() => this.number(6)}>6</button>
                                <button onClick={() => this.number(7)}>7</button><br/>
                                <button onClick={() => this.number(8)}>8</button>
                                <button onClick={() => this.number(9)}>9</button>
                                <button onClick={() => this.number(10)}>10</button>
                                <button onClick={() => this.number(11)}>11</button><br/>
                                <button onClick={this.clearGrid}>Clear grid</button>
                                <button onClick={this.renderCipher}>Cipher</button>
                                <button onClick={this.direction1cipher}>Direction 1 cipher</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <CipherWindow matrix={this.state.matrix} phase3map={this.state.phase3map} phase3numbers={this.state.phase3numbers} phase4map={this.state.phase4map} phase4numbers={this.state.phase4numbers}>
                </CipherWindow>
            </div>
        );
    }

}

export default Matrix;