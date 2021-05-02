var dark = true;
function renderSymbols() {
	var inp=$("input").value;
	if (!inp || inp=="") {
		clear();
		return;
	}
	
	var split=inp.split("\n");
	
	var html = "";
	var forum = "";
	for (var i=0; i<split.length; i++) {
		for (var j=0; j<split[i].length; j++) {
			html += '<img src="http://oranchak.com/zodiac/webtoy/alphabet/' + (dark ? 'darker/' : '') + getName(split[i].charAt(j), true) + '.jpg">';
			forum += '[img]http://oranchak.com/zodiac/webtoy/alphabet/' + (dark ? 'darker/' : '') + getName(split[i].charAt(j), true) + '.jpg[/img]';
		}
		if (i<split.length-1) {
			html += "<br>";
			forum += "\n";
		}
	}
	$("symbols").innerHTML = html;
	
	$("source-html").value = html;
	$("source-forum").value = forum;
	   
	var linkFull = 'http://zodiackillerciphers.com/typewriter/index.html?cipher=' + escape(inp);
	var linkCompact = 'http://zodiackillerciphers.com/typewriter/index.html?cipher=' + escape(inp) + '&mode=compact';
	$("link-full").href=linkFull;
	$("link-full").innerHTML=linkFull;
	$("link-compact").href=linkCompact;
	$("link-compact").innerHTML=linkCompact;
}
	
function clear() {
	$("source-html").value = "";
	$("source-forum").value = "";
	$("symbols").innerHTML = "";
}	

function clearInput() {
	var val = $("input").value;
	if ("-z TYPE SOMETHING HERE z-" == val) {
		$("input").value = "";
		$("symbols").innerHTML = "";
	}
}

function toggle() {
	
	var elem = $("toggle");
	elem.innerHTML = "<a href=\"javascript:toggle()\" class=\"button1\">" + (dark ? "darker" : "lighter") + "</a>";
	dark = !dark;
	renderSymbols();
}

function show(which) {
	hideAll();
	$(which).style.display="";
	$(which).focus();
	$(which).select();
}             

function hideAll() {
	$("source-html").style.display="none";
	$("source-forum").style.display="none";
	$("links").style.display="none";
}

function done() {
	$("loading").innerHTML = "";
}

function hideavail() {
	$("palette").style.display="none";
	$("avail").innerHTML = '<a href="javascript:showavail()" class="button1" style="margin-bottom: 0">show</a>';
	$("spacer").style.marginTop="1em";
}

function showavail() {
	$("palette").style.display="";
	$("avail").innerHTML = '<a href="javascript:hideavail()" class="button1" style="margin-bottom: 0">hide</a>';
	$("spacer").style.marginTop="";
}

function type(letter) {
	clearInput();
	var inp = $("input");
	var newValue = "";
	var start = getCaret(inp);
	var move = start == inp.value.length;
	newValue = inp.value.substring(0, start) + letter + inp.value.substring(start);
	inp.value = newValue;
	renderSymbols();
	var val = inp.value;
	inp.value = val;
	inp.focus();
	if (move) moveCaretToEnd(inp);
}	
 
// from http://stackoverflow.com/questions/263743/how-to-get-caret-position-in-textarea
function getCaret(el) { 
  if (el.selectionStart) { 
    return el.selectionStart; 
  } else if (document.selection) { 
    el.focus(); 

    var r = document.selection.createRange(); 
    if (r == null) { 
      return 0; 
    } 

    var re = el.createTextRange(), 
        rc = re.duplicate(); 
    re.moveToBookmark(r.getBookmark()); 
    rc.setEndPoint('EndToStart', re); 

    return rc.text.length; 
  }  
  return 0; 
}

function moveCaretToEnd(el) {
    if (typeof el.selectionStart == "number") {
        	el.selectionStart = el.selectionEnd = el.value.length;
    } else if (typeof el.createTextRange != "undefined") {
        el.focus();
        var range = el.createTextRange();
        range.collapse(false);
        range.select();
    }
}

function textAreaFocus(textarea) {
	textarea.style.backgroundColor="#efefef";
	textarea.style.color="#000";
//	moveCaretToEnd(textarea);
    // Work around Chrome's little problem
//    window.setTimeout(function() {
//        moveCaretToEnd(textarea);
//    }, 1);
}

function links() {
	hideAll();
	$("links").style.display="";
}
function hover(cell) {
	cell.style.border="thin solid #0f0";
}
function unhover(cell) {
	cell.style.border="thin solid #ccc";
}

function gup( name )
{
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}

google.load("prototype", "1.7.0.0");
google.load("scriptaculous", "1.8.3");

	var _gaq = _gaq || [];
	_gaq.push(['_setAccount','UA-31527148-1']);
	_gaq.push(['_trackPageview']);
	(function() {
		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	})();

