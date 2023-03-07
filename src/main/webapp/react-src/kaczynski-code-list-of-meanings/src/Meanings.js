import React from 'react';
import {setState} from 'react';
import './Meanings.css'
import {doScrolling} from './Scroll';

const cipherLength = 51;
export class Meanings extends React.Component {
    constructor(props) {
        super(props);
        this.state = { 
            meanings: [[ // Code I
                "FOR",
                "BE (all present tense forms, including 'AM', 'IS', 'ARE', etc.)",
                "BE (all past tense forms)",
                "BE (future tense, i.e., 'WILL BE')",
                "THE",
                "A or AN",
                "HAVE (all present tense forms)",
                "HAVE (all past tense forms, i.e., 'HAD')",
                "HAVE (future tense)",
                "ED, or, when tagged onto the end of any verb, indicates the past tense, even if the past tense of that verb is not indicated by 'ED' in ordinary English.",
                "tagged onto the end of any verb indicates the future tense of that verb.",
                "ING",
                "ER",
                "LY",
                "TION",
                "THERE",
                "THEN",
                "AND",
                "BUT",
                "OR",
                "TO",
                "FROM",
                "TOWARD",
                "OF",
                "IN",
                "OUT",
                "NO",
                "BIG",
                "SMALL",
                "I, ME, MINE, MY",
                "YOU, YOUR, YOURS",
                "HE, SHE, IT, HIM, HER, HIS, HERS, ITS",
                "WORD-SPACER",
                "WORD-SPACER",
                "PERIOD",
                "COMMA",
                "QUESTION MARK",
                "PARENTHESIS (",
                "PARENTHESIS )",
                "A",
                "A",
                "B",
                "C",
                "D",
                "D",
                "E",
                "E",
                "E",
                "F",
                "G",
                "H",
                "I",
                "J",
                "K",
                "L",
                "M",
                "N",
                "O",
                "P",
                "Q",
                "R",
                "R",
                "S",
                "S",
                "T",
                "T",
                "U",
                "V",
                "W",
                "X",
                "Y",
                "Z",
                "delete",
                "delete",
                "CH",
                "SH",
                "TH (unvoiced)",
                "TH (voiced)",
                "delete",
                "OM",
                "PLOD",
                "ILL",
                "ETONA",
                "\" (quotation marks)",
                "WHEN",
                "WHERE",
                "WHAT",
                "ST",
                "THAT",
                "delete"],
                [ // Code II
                "0", 
                "1", 
                "2", 
                "3", 
                "4", 
                "5", 
                "6", 
                "7", 
                "8", 
                "9", 
                "A", 
                "B", 
                "C", 
                "D", 
                "E", 
                "F", 
                "G", 
                "H", 
                "I", 
                "J", 
                "K", 
                "L", 
                "M", 
                "N", 
                "O", 
                "P", 
                "Q", 
                "R", 
                "S", 
                "T", 
                "U", 
                "V", 
                "W", 
                "X", 
                "Y", 
                "Z", 
                "PERIOD", 
                "COMMA", 
                "QUESTION MARK", 
                "PARENTHESIS (", 
                "PARENTHESIS )", 
                "QUOTATION MARKS", 
                "WORD-SPACER", 
                "ABOUT", 
                "AN", 
                "AND", 
                "AT", 
                "BE, infinitive and all present tense forms (am, is, are)", 
                "BE, all past tense forms (was, were)", 
                "BUT", 
                "BY", 
                "CH", 
                "DE", 
                "DOWN", 
                "ED", 
                "ER", 
                "FOR", 
                "FROM", 
                "HAVE, infinitive and all presente tense forms (have, has)", 
                "HAVE, all past tense forms (had)", 
                "HE", 
                "IN", 
                "ING", 
                "ION", 
                "IT", 
                "LE", 
                "LESS", 
                "LY", 
                "MAKE", 
                "MORE", 
                "MUCH", 
                "NO", 
                "OF", 
                "OFF", 
                "ON", 
                "OR", 
                "OUT", 
                "OVER", 
                "PRE", 
                "RE", 
                "SH", 
                "SL", 
                "SO", 
                "ST", 
                "TH", 
                "THAT", 
                "THE", 
                "THERE", 
                "THEN", 
                "THIS", 
                "TO", 
                "TR", 
                "UN", 
                "UNDER", 
                "UP", 
                "WHAT", 
                "WHEN", 
                "WHERE", 
                "WHO", 
                "WILL"
            ]],
            meaningsWhich: 0,
            highlight: -1,
            series: [[29, 8, 48, 60, 66, 50, 63, 64, 66, 42, 53, 32, 45, 67, 61, 70, 32, 55, 
                57, 60, 56, 11, 35, 17, 29, 42, 45, 56, 52, 57, 70, 10, 31, 34, 32, 29, 33, 
                43, 51, 49, 47, 52, 14, 33, 1, 39, 57, 57, 44, 34],[98, 28, 10, 34, 28, 42, 12, 27, 18, 22, 14, 42, 13, 24, 14, 28, 23, 29, 42, 25, 10, 34, 38, 42, 18, 42, 15, 14, 14, 21, 42, 31, 55, 34, 42, 16, 24, 24, 13, 42, 43, 89]]
        }
        this.fn = this.fn.bind(this);
        // this.jump1 = this.jump1.bind(this);
    }

    fn() {
    }

    rowClassName(row) {
        // console.log('rowClass', row);
        if (row == this.state.highlight)
            return "row-on";
        return "row-off";
    }

    meaningsRows() {
        var html = [];
        var index = 0;
        this.state.meanings[this.state.meaningsWhich].forEach(meaning => {
            html.push(<tr key={index} className={this.rowClassName(index)} id={'meaning-' + index}><td className="meaning-number">{index++}</td><td className="meaning">{meaning}</td></tr>)
        });
        return html;
    }

    jump(which) {
        // console.log('jump', which);
        this.setState({highlight: -1});
        setTimeout(() => this.setState({highlight: which}), 1100);
        doScrolling('#meaning-' + (which-1), 1000);
    }
    jumpTop() {
        doScrolling('#top', 1000);
    }
    jumpBottom() {
        doScrolling('#bottom', 1000);
    }
    jumpBottomSlow() {
        doScrolling('#bottom', 10000);
    }

    jumpAll(which) {
        let series = this.state.series[this.state.meaningsWhich];
        if (which == series.length) return;
        this.jump(series[which]);
        setTimeout(() => this.jumpAll(which+1), 2500);
    }

    jumpButtons() {
        var buttons = [];
        for (const number of this.state.series[this.state.meaningsWhich].concat([62])) {
            // console.log(number);
            buttons.push(<button onClick={() => this.jump(number)}>{number}</button>);
        }
        return buttons;
    }

    render() {
        return <>
            <div id="top"></div>
            <table className="wrapper">
                <tbody>
                    <tr>
                        <td>
                            <table className="meanings">
                                <tbody>{this.meaningsRows()}</tbody>
                            </table>
                        </td>
                        <td className="buttons">
                            <button onClick={() => this.jumpTop()}>Top</button>
                            <br></br>Code sequence: 
                            {this.jumpButtons()}
                            <button onClick={() => this.jumpAll(0)}>Jump All</button>
                            <button onClick={() => this.jumpBottom()}>Bottom</button>
                            <button onClick={() => this.jumpBottomSlow()}>Bottom (slow)</button>
                            <button onClick={() => this.setState({ meaningsWhich: 0 })}>Code I</button>
                            <button onClick={() => this.setState({ meaningsWhich: 1 })}>Code II</button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div id="bottom"></div>
        </>
    }
}


export default Meanings;