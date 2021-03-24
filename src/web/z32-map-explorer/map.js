var rendered = false;
var currentRadians;
var currentInches;
var selected=0;

var diablo=[477, 216];
var quentin=[111, 136]; // another reference point, so we can estimate latitudes and longitudes
// horiz. coords for scale: 29 to 133 = 104 pixels per 8 miles
// 1 inch = 6.4 miles
var milesPerInch = 6.4;
// (6.4 miles / 1 inch) * (104 pixels / 8 miles) = 83.2 pixels / inch
var pixelsPerInch = milesPerInch*104/8;

// magnetic declination as of June 27, 1970
var declination = 16.88 * Math.PI/180; // degrees to radians

// calculate end point of magnetic north indicator (which starts at mt diablo)
var indicatorLength = 125; // pixels
var magNorth = [diablo[0] + indicatorLength * Math.sin(declination), diablo[1] - indicatorLength * Math.cos(declination)];


var selectedLocation = [];

// latitude/longitude of mt diablo
var longLatDiablo = [121.914321, 37.88168];
// latitude/longitude of san quentin prison
var longLatSanQuentin = [122.489715, 37.938606];

//var longPerPixel=(longLatSanQuentin[0]-longLatDiablo[0])/(diablo[0]-quentin[0]);
//var latPerPixel=(longLatSanQuentin[1]-longLatDiablo[1])/(diablo[1]-quentin[1]);

var doTrack = true;

// lat/long range covered by map
var mapLat = [38.137925, 37.285749]; // north to south
var mapLong = [122.657933, 121.709321]; // west to east
var mapWidth = 571;
var mapHeight = 688;
//var longPerPixel=(longLatSanQuentin[0]-longLatDiablo[0])/(diablo[0]-quentin[0]);
var latPerPixel=(mapLat[0]-mapLat[1]) / mapHeight;
var longPerPixel=(mapLong[0]-mapLong[1]) / mapWidth;


function canvas(which) {
	return document.getElementById("canvas" + which);
}
function context(which) {
	return canvas(which).getContext("2d");
}

/*function plot() {
	var ctx = context();
	ctx.strokeStyle="#FF0000";
	ctx.moveTo(0,0);
	ctx.lineTo(200,100);
	ctx.lineWidth=5;
	ctx.stroke();
}*/

function clear(which) {
	var ctx = context(which);
	ctx.clearRect(0, 0, canvas(which).width, canvas(which).height);
}

function getMousePos(c, evt) {
        var rect = c.getBoundingClientRect();
        return {
          x: evt.clientX - rect.left,
          y: evt.clientY - rect.top
        };
      }

function writeMessage(canvas, message, x, y) {
        var context = canvas.getContext('2d');
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.font = '18pt Calibri';
        context.fillStyle = 'black';
        context.fillText(message, 10, 25);
}
function drawTarget(x, y) {
	clear(2);
	var img = new Image();
	img.src = "crosshair2.png"
	context(2).drawImage(img,x-30,y-30);
}
function onload() {
	listener();
	drawMagneticNorth();
	init();
	render();
	highlightZ32();
}	               

function highlightZ32() {
	rgb(0, 255, 200, 200);
	rgb(25, 255, 200, 200);
	rgb(1, 200, 255, 200);
	rgb(31, 200, 255, 200);
	rgb(5, 200, 200, 255);
	rgb(13, 200, 200, 255);
}
function listener() {
	canvas(3).addEventListener('mousemove', function(evt) {
	        var mousePos = getMousePos(canvas(3), evt);
	        //var message = 'Mouse position: ' + mousePos.x + ',' + mousePos.y;
	        //writeMessage(canvas(2), message, mousePos.x, mousePos.y);
			doUpdate(mousePos.x, mousePos.y);
	      }, false);	
	
	canvas(3).addEventListener("click", function(evt) {
        var mousePos = getMousePos(canvas(3), evt);
		doClick(mousePos.x, mousePos.y);
	}, false);
}	
function doClick(x, y) {
	cursor(doTrack);
	doTrack = !doTrack;         
	if (doTrack) {
		clear(3);
		doUpdate(x, y);
	}
	else doSolutions(x, y);
}                          

function doSolutions(x, y) {
	document.getElementById("navigate").innerHTML = "Computing...";
	distanceSort(currentRadians, currentInches);
	clear(3);
	solutions[0].render();
	selected=0;
	
	renderNavigation();
	renderMarkers();
	
	document.getElementById("cipher").style.display="";
	document.getElementById("header").style.display="";
}

function cursor(on) {
	var cur;
	cur = on ? "auto" : "none";
	document.getElementById("canvas1").style.cursor = cur;
	document.getElementById("canvas2").style.cursor = cur;
	document.getElementById("canvas3").style.cursor = cur;
}


function doUpdate(x, y) {
	if (!doTrack) return;
	updatePos(x, y);
	drawTarget(x, y);
	clear(2); 
	var ctx = context(2);
	
	var radius = distance(x, y, diablo[0], diablo[1]);
	var radEnd = radians(x, y) - Math.PI/2 + declination;
	var radStart = declination - Math.PI/2;             

	ctx.globalAlpha=0.5;
	
	ctx.beginPath();
    ctx.strokeStyle = '#000000';
    ctx.lineWidth = 8;
	ctx.arc(diablo[0], diablo[1], radius, radStart, radEnd);
	ctx.stroke();	
	
	ctx.beginPath();
    ctx.strokeStyle = '#00cc00';
    ctx.lineWidth = 6;
	ctx.arc(diablo[0], diablo[1], radius, radStart, radEnd);
	ctx.stroke();	
	ctx.lineTo(diablo[0], diablo[1]);
	ctx.closePath();
	ctx.fillStyle = "#00cc00";
	ctx.fill();
	
	var img = new Image();
	img.src = "crosshair2.png"
	ctx.drawImage(img,x-30,y-30);
}

function updatePos(x, y) {                
	var html = "<div class='header' style='margin-bottom: 1em;'>Current position:</div>";
	html += "<table id='current'><tr>";
	html += "<td align='right'>Location (pixels): </td><td><b>" + x + ", " + y + "</b></td></tr>";
	currentInches = inches(x, y);
	currentRadians = radians(x, y);
	var rad = round100(currentRadians);
	html += "<tr><td align='right'>Radians: </td><td><b>" + rad + " (" + toPi(rad) + "&pi;) (" + toMils(rad) + " mils) (" + toDegrees(rad) + "&deg;)</b></td></tr>";
	html += "<tr><td align='right'>Distance from Mt Diablo: </td><td><b>" + round100(currentInches) + " inches (" +round100(toMiles(currentInches)) + " miles)</b></td></tr>";
	//var ll = toLatLong([x, y]);
	//html += "<tr><td align='right'>Estimated coordinates: </td><td><b>" + ll[0] + "&deg; N, " + ll[1] + "&deg; W</b></td></tr>";
	html += "</table>";
	document.getElementById("pos").innerHTML = html;
}

function drawStroked(ctx, text, x, y) {
    ctx.font = "20px Sans-serif"
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 4;
    ctx.lineJoin="miter"; //Experiment with "bevel" & "round" for the effect you want!
	ctx.miterLimit=2;
    ctx.strokeText(text, x, y);
    ctx.fillStyle = '#ff0000';
    ctx.fillText(text, x, y);
}

function drawMagneticNorth() {
	var ctx = context(1);
	//ctx.strokeStyle="#000000";
	//ctx.lineWidth=8;
	//canvas_arrow(ctx, diablo[0], diablo[1], magNorth[0], magNorth[1], 10);
	//ctx.stroke();
	//ctx.strokeStyle="#cc0000";
	//ctx.lineWidth=5;
	//canvas_arrow(ctx, diablo[0], diablo[1], magNorth[0], magNorth[1], 7);
	//ctx.stroke();
                   
	drawArrow(ctx, diablo[0], diablo[1], magNorth[0], magNorth[1], 11, "#000000");
	drawArrow(ctx, diablo[0], diablo[1], magNorth[0], magNorth[1], 8, "#cc0000");
	//ctx.font = "20px Arial bold";
	//ctx.fillStyle = "#cc0000";
	//ctx.fillText("Mag. N.", magNorth[0]-20, magNorth[1]-20);
	drawStroked(ctx, "MAG. N.", magNorth[0]-20, magNorth[1]-20);
	
}

function canvas_arrow(context, fromx, fromy, tox, toy, headlen){
    //var headlen = 10;   // length of head in pixels
    var angle = Math.atan2(toy-fromy,tox-fromx);
    context.moveTo(fromx, fromy);
    context.lineTo(tox, toy);
	context.moveTo(tox, toy);
    context.lineTo(tox-headlen*Math.cos(angle-Math.PI/6),toy-headlen*Math.sin(angle-Math.PI/6));
    context.moveTo(tox, toy);
    context.lineTo(tox-headlen*Math.cos(angle+Math.PI/6),toy-headlen*Math.sin(angle+Math.PI/6));
}

function drawArrow(ctx, fromx, fromy, tox, toy, linewidth, color){
                var headlen = linewidth/2;

                var angle = Math.atan2(toy-fromy,tox-fromx);

                //starting path of the arrow from the start square to the end square and drawing the stroke
                ctx.beginPath();
                ctx.moveTo(fromx, fromy);
                ctx.lineTo(tox, toy);
                ctx.strokeStyle = color;
                ctx.lineWidth = linewidth;
                ctx.stroke();

                //starting a new path from the head of the arrow to one of the sides of the point
                ctx.beginPath();
                ctx.moveTo(tox, toy);
                ctx.lineTo(tox-headlen*Math.cos(angle-Math.PI/7),toy-headlen*Math.sin(angle-Math.PI/7));

                //path from the side point of the arrow, to the other side point
                ctx.lineTo(tox-headlen*Math.cos(angle+Math.PI/7),toy-headlen*Math.sin(angle+Math.PI/7));

                //path from the side point back to the tip of the arrow, and then again to the opposite side point
                ctx.lineTo(tox, toy);
                ctx.lineTo(tox-headlen*Math.cos(angle-Math.PI/7),toy-headlen*Math.sin(angle-Math.PI/7));

                //draws the paths created above
                ctx.strokeStyle = color;
                ctx.lineWidth = linewidth;
                ctx.stroke();
                ctx.fillStyle = color;
                ctx.fill();
}
                


/** calculate distance between given points (in pixels) */
function distance(x1, y1, x2, y2) {
	return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
}
function inches(x, y) {
	var d = distance(x, y, diablo[0], diablo[1]);
	return d/pixelsPerInch;
}

/** calculate radians between mag n at mt diablo and vector from mt. diablo to current position */
function radians(x, y) {
	var angle;
//	if (x<=(diablo[0]) && y<=(diablo[1]))
//		angle = Math.atan((diablo[1] - y) / (diablo[0] - x)) + Math.PI*3/2;
	//else
	 angle = Math.atan2(y - diablo[1], x - diablo[0]) + Math.PI/2;
	// adjust for mag n
	angle -= declination;
	
	if (angle < 0) angle = 2*Math.PI + angle;
	return angle;
}

function toPi(rad) {
	return round100(rad/Math.PI);
}

function toDegrees(rad) {
	return round100(180*rad/Math.PI);
}

function toMils(rad) {
	return round100(rad*3200/Math.PI);
}

function toMiles(inches) {
	return round100(milesPerInch*inches);
}

function toLatLong(xy) {
//	return [longLatDiablo[1]+latPerPixel*(diablo[1]-xy[1]), longLatDiablo[0]+longPerPixel*(diablo[0]-xy[0])];
//	return [mapLat[0]-latPerPixel*]

  var latVal = longLatDiablo[1]+latPerPixel*(diablo[1]-xy[1]);
  var longVal = longLatDiablo[0]+longPerPixel*(diablo[0]-xy[0]);
  latVal = Math.round(100000*latVal)/100000;
  longVal = Math.round(100000*longVal)/100000;
  return [latVal, longVal];
  

}
              
/** convert from radians and inches to cartesian coordinates */
function toXY(radians, inches) {
	var adjustedRadians = Math.PI/2-declination-radians;
	return [inches*pixelsPerInch*Math.cos(adjustedRadians)+diablo[0], diablo[1]-inches*pixelsPerInch*Math.sin(adjustedRadians)];
}

function polarDistance(r1, theta1, r2, theta2) {
	var xy1 = toXY(r1, theta1);
	var xy2 = toXY(r2, theta2);
	var d1 = xy1[0]-xy2[0];
	var d2 = xy1[1]-xy2[1];
	return Math.sqrt(d1*d1 + d2*d2);
	
}
function polarDistance2(r1, theta1, r2, theta2) {
	return Math.sqrt(r1*r1 + r2*r2 - 2*r1*r2*Math.cos(theta1-theta2));
}

function renderNavigation() {
	var htmlPrev;
	var htmlNext;
	var htmlSelect;
	if (selected == 0) htmlPrev = "Prev";
	else htmlPrev = "<a href='javascript:prevSolution()'>Prev</a>";
	if (selected == solutions.length-1) htmlNext = "Next";
	else htmlNext = "<a href='javascript:nextSolution()'>Next</a>";
	
	htmlSelect = "<select onchange='select(this)'>";
	for (var i=0; i<100&&i<solutions.length; i++) {
		htmlSelect += "<option value='" + i + "'";
		if (i==selected) htmlSelect += " selected=\"yes\"";
		htmlSelect += ">" + solutions[i].radians + ", " + solutions[i].inches + ": " + solutions[i].plaintext + "</option>";
	}                                                              
	htmlSelect += "</select>";
	
	document.getElementById("navigate").innerHTML = htmlPrev + " " + htmlSelect + " " + htmlNext;
}

function nextSolution() {
	if (selected == solutions.length-1) return;
	selected++;
	solutions[selected].render();
	renderMarkers();
	renderNavigation();
}   

function prevSolution() {
	if (selected == 0) return;
	selected--;
	solutions[selected].render();
	renderMarkers();
	renderNavigation();
}

function renderMarkers() {
	clear(3);
	var num = 9;
	var count = 0;
	var i = selected - 4;
	if (i < 0) i = 0;
	
	var prevRadians = 0;
	var prevInches = 0;
	
	while (count < num) {
		solutions[i].marker(true);
		if (solutions[i].radians != prevRadians || solutions[i].inches != prevInches) {
			count++;
		}
		prevRadians = solutions[i].radians;
		prevInches = solutions[i].inches;
		i++;
	}
	solutions[selected].marker(false);
	
}

function select(elem) {
	selected = elem.value;
	solutions[selected].render();
	renderNavigation();
	renderMarkers();
}

function round100(val) {
	return Math.round(100*val)/100;
}