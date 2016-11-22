var cells = [];
var flag = 0;
var cells_counter = 0; // Keeps track of the number of cells selected by the user. It is used to draw the client cipher view on the right side
var lines_counter = 0; // Keeps track of the number of lines in the client cipher view
var isLoading = false;

var redo = [];
var sequence = [];

// This class stores a path selected by the user (Obs.: A path can be just one cell. In other words, it can be just a single click)
function Path(){
    this.cells = [];
}

// This variable stores all the paths done by the user
var hist = [];

/*

 Check which class is active, highlight the next one and remove the current one.
 Then, increment the counter of how many times the cell has been clicked,
 then update the css parameters related to the text.

 */

var highlight = function(cell) {

    if (cell.hasClass("active1")) {
        cell.addClass("active2");
        cell.removeClass("active1");
        var text = cell.text();
        var c = parseInt(text);
        c += 1;
        cell.find("span").text(c);
        //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');

    } else if (cell.hasClass("active2")) {
        cell.addClass("active3");
        cell.removeClass("active2");
        var text = cell.text();
        var c = parseInt(text);
        c += 1;
        cell.find("span").text(c);
        //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');

    } else if (cell.hasClass("active3")) {
        cell.addClass("active4");
        cell.removeClass("active3");
        var text = cell.text();
        var c = parseInt(text);
        c += 1;
        cell.find("span").text(c);
        //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');

    } else if (cell.hasClass("active4")) {
        cell.addClass("active5");
        cell.removeClass("active4");
        var text = cell.text();
        var c = parseInt(text);
        c += 1;
        cell.find("span").text(c);
        //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');

    } else if (cell.hasClass("active5")) {
        var text = cell.text();
        var c = parseInt(text);
        c += 1;
        cell.find("span").text(c);
        //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');
    }

    else {
        cell.addClass("active1");
        var text = cell.text();
        var c = parseInt(text);
        c += 1;
        cell.find("span").text(c);
        //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');
    }
}

var unhighlight = function(cell) {

    if (cell != undefined) {

        if (cell.hasClass("active1")) {
            cell.removeClass("active1");
            var text = cell.text();
            var c = parseInt(text);
            c -= 1;
            cell.find("span").text(c);
           // cell.find("span").attr('style', 'color:white', 'position: absolute', 'bottom: 0', 'right: 0');

        } else if (cell.hasClass("active2")) {
            cell.addClass("active1");
            cell.removeClass("active2");
            var text = cell.text();
            var c = parseInt(text);
            c -= 1;
            cell.find("span").text(c);
            //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');

        } else if (cell.hasClass("active3")) {
            cell.addClass("active2");
            cell.removeClass("active3");
            var text = cell.text();
            var c = parseInt(text);
            c -= 1;
            cell.find("span").text(c);
            //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');

        } else if (cell.hasClass("active4")) {
            cell.addClass("active3");
            cell.removeClass("active4");
            var text = cell.text();
            var c = parseInt(text);
            c -= 1;
            cell.find("span").text(c);
            //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');

        } else if (cell.hasClass("active5")){

            var text = cell.text();
            var c = parseInt(text);
            c -= 1;

            if(c<5){
                cell.addClass("active4");
                cell.removeClass("active5");
                cell.find("span").text(c);
                //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');
            }else{
                cell.find("span").text(c);
                //cell.find("span").attr('style', 'color:black', 'position: absolute', 'bottom: 0', 'right: 0');
            }
        }

        else {
            var text = cell.text();
            var c = parseInt(text);
            c -= 1;
            cell.find("span").text(c);
            //cell.find("span").attr('style', 'color:white', 'position: absolute', 'bottom: 0', 'right: 0');
        }
    }
}

/* Function to evaluate if two cells are diagonally one from another */
var isDiagonal = function(v, line1, line2, column1, column2) {
    /* Subtract the number and get their absolute values */
    num1 = line1 - line2;
    num2 = column1 - column2;
    a1 = Math.abs(num1);
    a2 = Math.abs(num2);
    /*	Get the sign of the column subtraction.
     If column 1 is greater than column 2, the signal is positive.
     If column 1 is less than column 2, the signal is negative.
     */
    if (num2 > 0) {
        sign = 1;
    } else {
        sign = -1;
    }

    /* If the absolute values are the same, the cells are diagonal from one another */
    if (a1 == a2) {
        var v = [true, sign];
        return v;
        /* If the absolute values are not the same, the cells are not diagonal from one another */
    } else {
        var v = [false, sign];
        return v;
    }
}

/* Main function to listen to all the buttons and keys */
var main = function() {

    $('#undo-btn').click(function()
    {
        if(!isLoading){
            console.log(isLoading);
            if(hist.length > 0) // If there is some element in hist
            {
                var path = hist.pop();
                redo.push(path);
                //console.log(redo);
                for (i = 0; i < path.cells.length; i++) {
                    unhighlight(path.cells[i]);
                    removeFromClientCipher();
                }
            }
        }
    });

    $('#redo-btn').click(function() {

        if(!isLoading){

            if(redo.length > 0) // If there is element in redo
            {
                var path = redo.pop();
                hist.push(path);
                //console.log(path);
                for (i = 0; i < path.cells.length; i++) {
                    highlight(path.cells[i]);
                    addToClientCipher(path.cells[i]);
                }
            }
        }
    });

    $('#save-btn').click(function(){

        if(!isLoading){
            var history = convertHistory(hist);
            var getfile = $.ajax({
                url: "createfile.php",
                type: "POST",
                dataType: "json",
                data: {userpath:JSON.stringify(history)},
                success: function(data){
                    console.log("Success!");
                }
            });
            window.setTimeout(function(){
                location.href = "savefile.php";
                var deleteTimeout = setTimeout(function(){
                    var deletefile = $.ajax({
                        url: "deletefile.php",
                        type: "POST",
                        dataType: "json",
                        success: function(data){
                            console.log("File deleted in the server!");
                        }
                    });
                },10000);
            },1000);
        }
    });

    /* Handler to clear everything */
    $("#clear-all").click(function(){

        sure = confirm("Are you sure you want to clear the cipher?");
        if(sure==true){

            redo = [];
            hist = [];
            cells = [];
            cells_counter = 0;
            lines_counter = 0;
            var cellsclear = $(".cell").toArray();

            for(var i=0;i<cellsclear.length;i++){
                if($(cellsclear[i]).hasClass("active1")){
                    $(cellsclear[i]).removeClass("active1");
                    $(cellsclear[i]).find("span").text(0);
                }
                if ($(cellsclear[i]).hasClass("active2")){
                    $(cellsclear[i]).removeClass("active2");
                    $(cellsclear[i]).find("span").text(0);
                }
                if ($(cellsclear[i]).hasClass("active3")){
                    $(cellsclear[i]).removeClass("active3");
                    $(cellsclear[i]).find("span").text(0);
                }
                if ($(cellsclear[i]).hasClass("active4")){
                    $(cellsclear[i]).removeClass("active4");
                    $(cellsclear[i]).find("span").text(0);
                }
                if ($(cellsclear[i]).hasClass("active5")){
                    $(cellsclear[i]).removeClass("active5");
                    $(cellsclear[i]).find("span").text(0);
                }
            }

            $("#user_cipher").empty();
        }

    });

    /* Handle click of any button for the preset */
    $(".presets").click(function(){
        if(!isLoading){
            isLoading = true;
            var filename = $(this).text()+".txt";
            $.ajax({url: "readfile.php",
                    type: "GET",
                    data: {name:filename},
                    success: function(data){
                        var numarray = data.split(", ");
                        //console.log(numarray);
                        (function highlightPattern(i){
                          setTimeout(function(){
                            if (i<numarray.length){
                                $("#cipher"+numarray[i]).click();
                                i++;
                                highlightPattern(i);
                            }else{
                                isLoading = false;
                            }
                          },5);
                        })(0);
                    }
            });
        }
    });

    /* If a cell is clicked, highlight it and put it in the array */
    $(".cell").click(function(event) {

        //Clear redo
        redo = [];

        clicked = $(this);
        highlight(clicked);
        cells[cells.length] = clicked;
        var path = new Path();

        //Push the first cell clicked
        path.cells.push(clicked);   //Add the cell to the path
        var temp_cell = new Path();
        temp_cell.cells.push(clicked);
        hist.push(temp_cell);       //Add the path to the history

        // If the key to make a path is pressed
        if(flag == 1)
        {
            // If two cells were clicked to make a path
            if(cells.length == 2) // If the user has already clicked in two cells, so make the path between these cells if exist a path
            {

                var c1 = cells[0].attr('id');
                var c2 = cells[1].attr('id');
                var s1 = [];
                var s2 = [];
                /* Get the number that comes after the "cipher" word in the id attribute */
                s1 = c1.substring(6, c1.length);
                s2 = c2.substring(6, c2.length);
                /* Convert these numbers to integers */
                var n1 = parseInt(s1);
                var n2 = parseInt(s2);
                v = [n1, n2];
                /* Sort the array in the ascending order */
                v.sort(function(a, b) {
                    return a - b
                });

                path.cells = [];  // Clean the path variable
                // Add the first cell of the path
                path.cells.push($(document.getElementById("cipher" + v[0].toString())));

                /* Get the lines of the clicked cells */
                line1 = Math.floor(v[0] / 17);
                line2 = Math.floor(v[1] / 17);
                /* Get the columns of the clicked cells */
                column1 = v[0] % 17;
                column2 = v[1] % 17;

                /* If the path is vertical */
                if (line1 != line2 && column1 == column2) {
                    for (i = line1 + 1; i < line2; i++) {
                        var mark = i * 17 + column1;
                        highlight($(document.getElementById("cipher" + mark.toString())));
                        addToClientCipher($(document.getElementById("cipher" + mark.toString()))); // Add to the user cipher view
                        path.cells.push($(document.getElementById("cipher" + mark.toString())));
                    }
                }

                /* If the path is horizontal */
                else if (line1 == line2 && column1 != column2) {
                    for (i = column1 + 1; i < column2; i++) {
                        var mark = line1 * 17 + i;
                        highlight($(document.getElementById("cipher" + mark.toString())));
                        addToClientCipher($(document.getElementById("cipher" + mark.toString()))); // Add to the user cipher view
                        path.cells.push($(document.getElementById("cipher" + mark.toString())));
                    }
                }

                /* If the path is diagonal */
                else if (line1 != line2 && column1 != column2) {
                    var ans = isDiagonal(v, line1, line2, column1, column2);
                    /* If the cells are diagonal from one another and the signal is negative, column 1 is less than column 2. This means to "go right" */
                    var aux = n1;
                    if (ans[0] == true && ans[1] == -1) {
                        /* 	If "going right", this means that the column will be incremented.
                         If aux is the second cell clicked, which has been highlighted already,
                         the last element that has been highlighted is this cell minus 18.
                         If aux equals this last element, the while stops.
                         The path will have been highlighted.
                         */
                        if(n1>n2){
                            aux = n1;
                            while (aux != (v[0] + 18)) {
                                aux = aux - 18;
                                highlight($(document.getElementById("cipher" + aux.toString())));
                                addToClientCipher($(document.getElementById("cipher" + aux.toString()))); // Add to the user cipher view
                                path.cells.push($(document.getElementById("cipher" + aux.toString())));
                            }
                        }else{
                            while (aux != (v[1] - 18)) {
                                aux = aux + 18;
                                highlight($(document.getElementById("cipher" + aux.toString())));
                                addToClientCipher($(document.getElementById("cipher" + aux.toString()))); // Add to the user cipher view
                                path.cells.push($(document.getElementById("cipher" + aux.toString())));
                            }
                        }
                        /* If the cells are diagonal from one another and the signal is positive, column 1 is greater than column 2. This means to "go left" */
                    } else if (ans[0] == true && ans[1] == 1) {
                        /* 	If "going left", this means that the column will be decremented.
                         If aux is the second cell clicked, which has been highlighted already,
                         the last element that has been highlighted is this cell minus 16.
                         If aux equals this last element, the while stops.
                         The path will have been highlighted.
                         */

                         if(n1>n2){
                            aux = n1;
                            while (aux != (v[0] + 16)) {
                                aux = aux - 16;
                                highlight($(document.getElementById("cipher" + aux.toString())));
                                addToClientCipher($(document.getElementById("cipher" + aux.toString()))); // Add to the user cipher view
                                path.cells.push($(document.getElementById("cipher" + aux.toString())));
                            }
                        }else{
                            while (aux != (v[1] - 16)) {
                                aux = aux + 16;
                                highlight($(document.getElementById("cipher" + aux.toString())));
                                addToClientCipher($(document.getElementById("cipher" + aux.toString()))); // Add to the user cipher view
                                path.cells.push($(document.getElementById("cipher" + aux.toString())));
                            }
                        }
                    } else if (ans[0] == false) {
                        alert("This path is not valid.");
                        path.cells.push($(document.getElementById("cipher" + v[1].toString())));
                        addToClientCipher(cells[1]);
                        /*console.log(path);
                        console.log(hist);*/
                        cells = [];
                        flag = 0;
                        return;
                    }
                }

                //Pop the last two elements from the History because instead of saving 2 cells, we are going to save just the path formed by these cells
                hist.pop();
                hist.pop();

                // Add the last cell of the path
                path.cells.push($(document.getElementById("cipher" + v[1].toString())));
                addToClientCipher(cells[1]); // Add the first cell clicked when holding the key

                // If the number of the last clicked cell is less the first clicked cell, it's necessary to sort the array in a descending order
                if(n2 < n1)
                {

                    path.cells.sort(function(a,b){

                        /* Encapsulate in function */
                        var c1 = a.attr('id');
                        var c2 = b.attr('id');
                        var s1 = [];
                        var s2 = [];
                        /* Get the number that comes after the "cipher" word in the id attribute */
                        s1 = c1.substring(6, c1.length);
                        s2 = c2.substring(6, c2.length);
                        /* Convert these numbers to integers */
                        var n1 = parseInt(s1);
                        var n2 = parseInt(s2);
                        return n2-n1
                    });
                    //console.log(path.cells);
                }

                // Then add the path to the history
                hist.push(path);

                cells = []; // Clean cells

            }else{
                addToClientCipher(cells[0]); // Add the first cell clicked when holding the key
            }
        }else{ // If the key was not pressed, it's not necessary to save the last click, so clean cells
             addToClientCipher(clicked); // Add to the user cipher view
            cells = [];
        }

    });

    /*
    This function hadles the event when a keydown event happens
    */
    function KeyPress(e)
    {

        console.log(typeof(e));

        // Undo Shortcut Key  Ctrl + Z
        if ((e.which == 90 || e.keyCode == 90))
        {
            if(!isLoading){
                if(hist.length > 0) // If there is some element in hist
                {
                    var path = hist.pop();
                    redo.push(path);
                    //console.log(path.cells.length);
                    for (i = 0; i < path.cells.length; i++) {
                        unhighlight(path.cells[i]);
                        removeFromClientCipher();
                    }
                }
            }
        }

        // Redo Shortcut Key  Ctrl + Y
        if ((e.which == 89 || e.keyCode == 89))
        {
            if(!isLoading){
                if(redo.length > 0) // If there is element in redo
                {
                    var path = redo.pop();
                    hist.push(path);
                    for (i = 0; i < path.cells.length; i++) {
                        highlight(path.cells[i]);
                        addToClientCipher(path.cells[i]);
                    }
                }
            }
        }

        // Key for making paths
        if(e.which == 18){
            if(!isLoading){
                if (flag == 0) {
                    flag = 1;
                }
            }
        }
    }


    $(this).keydown(function(e) {
        KeyPress(e);
    });

    /*
     When the key is released (in this case it's the key k),
     calculate the path between the 2 cells and highlight all the cells in the path.
     */

    $(this).keyup(function(e){
        if(e.which==18){
            cells = [];
            flag = 0;
        }
    });

}


/*
    Gets all the cells in the history
*/
var convertHistory = function(pattern){

    var path=[];
    var finalpath = [];

    for(i=0;i<pattern.length;i++){
        for(j=0;j<pattern[i].cells.length;j++){
            var c = pattern[i].cells[j].attr('id');
            var s = [];
            /* Get the number that comes after the "cipher" word in the id attribute */
            s = c.substring(6, c.length);
            path[j] = parseInt(s)+1;
        }

        finalpath[i] = path;
        path = [];
    }

    // console.log(finalpath);
    return finalpath;
}

// Add a single cell to the client cipher view. It inserts a cell based on the
// current number of cells that are already in the client cipher view in order to
// add at most 17 cells per each line.
var addToClientCipher = function(cell)
{

    // Get the image in the cell
    var image_letter = cell.css('background-image'); // Gets the image of the cell that was selected
    var indeximage = image_letter.search("images");
    var imagesliced = image_letter.slice(indeximage);
    var imagenoquotes = imagesliced.slice(0,imagesliced.indexOf('"'));
    //console.log(imagenoquotes);
        
    // If the number of cells on the client cipher view is divided by 16 OR there is no line yet, it means that 
    // a new line must be added
    if((cells_counter % 17) == 0 || lines_counter == 0){
        lines_counter++; // Add a line to the client cipher view
        $("#user_cipher").append("<div class='sixteen columns' id='line" + lines_counter+"'></div>"); // Add the new line
    }
    
    cells_counter++; // Update the number of cells
    // Add the cell into the current line. It uses the cells_counter as the identifier of each cell according the order it was add.
    $("#line"+lines_counter).append("<div class='one column cell' id='userCipher" + cells_counter + "'></div>");
    $("#userCipher"+cells_counter).css({'background-image': 'url('+'"'+imagenoquotes+'")','background-repeat':'no-repeat'});
    //$("#line"+lines_counter).children(":last-child").fadeIn(300);
}


// Remove remove a specific number of cell (numberOfCellsToRemove) from the client cipher view. 
// It removes the cells based on the current number of cells that are already in the client cipher view.
// It removes from the last added cells.
var removeFromClientCipher = function()
{
    cells_counter--; // Update the number of cells on the client cipher view
    //$("#userCipher"+(cells_counter+1)).fadeOut(300, function () {$(this).remove()});
    $("#userCipher"+(cells_counter+1)).remove();
    
    if((cells_counter % 17) == 0){
        var line = document.getElementById("line"+lines_counter);
        line.parentNode.removeChild(line); // Remove the cell from the client cipher view
        lines_counter--; // Add a line to the client cipher view
    }
}

/* Function to highlight the elements in a pattern with an interval of 1 millisecond */
var highlightPattern = function(i,nums){
    nums[i] = parseInt(nums[i]);
    console.log(nums[i]);
    $("#cipher"+nums[i]).click();    
}        

$(document).ready(main);