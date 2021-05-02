<!DOCTYPE html>
<!--[if lt IE 7 ]><html class="ie ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]><html class="ie ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]><html class="ie ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<html lang="en"> <!--<![endif]-->
    <head>
        <title>User Guide :: Zodiac Pattern Drawer</title>
        <?php include("include/head.php"); ?>
    </head>
    <body>

        <!-- Main Menu / Start
        ================================================== -->
        <header class="menu">
            <div class="container">
                <?php include("include/nav_logo_mobile.php"); ?>
            </div><!-- End of container -->
        </header>
        <!-- Main Menu / End
        ================================================== -->

        <!-- Title Bar / Start
        ================================================== -->
        <div class="page-title">
            <div class="container">

                <div class="twelve columns">
                    <p id="userguidep">User Guide</p>
                </div>

                <nav class="four columns">
                    <ul class="breadcrumbs">
                        <li><a href="index.php">Home</a></li>
                        <li><i class="icon-angle-right"></i></li>
                        <li>User Guide </li>
                    </ul>
                </nav>

            </div>
        </div>
        <!-- Title Bar / End
        ================================================== -->

        <!-- User Guide / Start
        ================================================== -->
        <div class="container">
            <div class="sixteen columns">
                <div class="userguidepage">
            <p id="userguideh"> Hello! Please, read through this user guide carefully. </p>
            <br>
            This web tool is used to generate new configurations for the Zodiac-340 cipher.
            <br>
            This user guide will explain the usage and functionality of this tool.
            <br>
            Any character of the cipher can be clicked. When a character is clicked, it appears on the right side of the page.
            <br>
            The number that is in the lower right corner of every character in the cipher simply tells you how many times you have selected that character.
            <br>
            In order to select multiple sequential characters at once, the alt key can be pressed. This is detailed below:
            <ul class="userguideul">
                <li class="userguideli"> Step 1: Hold the alt key. </li>
                <li class="userguideli"> Step 2: Click any character in the cipher. </li>
                <li class="userguideli"> Step 3: Click another character in the cipher that is in the horizontal, vertical or diagonal direction, relative to the first character clicked. </li>
                <li class="userguideli"> Step 4: Release the alt key if you wish. You may keep the alt key pressed, but keep in mind that once all the characters between the two clicked characters are highlighted, if you click a new character, the previous selection is not continued. </li>
            </ul>
    
            The created cipher can be saved as a text file. This file will contain the numbers (from 1 to 340) of each clicked character, each one in a different line.
            <br>
            There are some preset configurations that can be loaded. Just click the button corresponding to the configuration you would like to view and this configuration will be formed.
            <br>
            Be careful. If you click multiple preset buttons at the same time, they will conflict and you might not get the desired result.
            <br>
            If you feel that you made a mistake while creating your new cipher, use the undo button, or press the CTRL and the Z keys in your keyboard. Press the redo button or the CTRL and the Y keys in your keyboard to redo.
            <br>
            Please, feel free to click on the other sections of the page to find out more about the software and its creators.
            <br>
            It is encouraged to click on the Tutorial section in the page to watch a video showing examples on how to use the software.
        </div>
            </div>
        </div>
        <!-- User Guide / End
        ================================================== -->

        <!-- Footer / Start
        ================================================== -->
        <?php include("include/footer.php"); ?>
        <!-- ==================================================
        Footer / End -->

    </body>
</html>