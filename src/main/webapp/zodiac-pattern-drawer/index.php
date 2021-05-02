<!DOCTYPE html>
<!--[if lt IE 7 ]><html class="ie ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]><html class="ie ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]><html class="ie ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--><html lang="en"> <!--<![endif]-->
    <head>
        <title>Zodiac Pattern Drawer</title>
        <?php include("include/head.php"); ?>
        <script>
            $(document).ready(function() {
                var folder = "Line_1";
                for (i = 0; i < 340; i++) {
                    if ((i % 17) == 0) {
                        folder = "Line_" + ((i / 17) + 1);
                    }
                    $('#cipher' + i).css({'background-image': 'url("images/character/' + folder + '/' + (i + 1) + '.png")',
											'background-repeat' : 'no-repeat'});
                }
            });
        </script>
        <script type="text/javascript" src="clickhandler.js"></script>
    </head>

    <body>
        <!-- Main Menu / Start
        ================================================== -->
        <header class="menu">
            <div class="container">
                <?php include("include/nav_logo_mobile.php"); ?>
            </div><!-- End of container -->
        </header>
        <?php
            session_start();
        ?>
        <!-- Main Menu / End
        ================================================== -->
        <div class="page-title myTitle">
            <div class="container">
                <button class="btn" id="undo-btn"><i class="icon-undo"></i>Undo</button>
                <button class="btn" id="redo-btn"><i class="icon-repeat"></i>Redo</button>
                <button class="btn" id="save-btn"><i class="icon-save"></i>Save</button>
                <button class="btn" id="clear-all">Clear all</button>
                <button class="btn presets" id="preset1">Forward</button>
                <button class="btn presets" id="preset2">Backwards</button>
                <button class="btn presets" id="preset3">Vertical Top-Down</button>
                <button class="btn presets" id="preset4">Zig Zag Top-Down</button>
                <button class="btn presets" id="preset5">Spiral</button>
            </div>
        </div>
        <div class="namesbar">
            <div id="name1">
                Original Cipher
            </div>
            <div id="name2">
                Your Arrangement
            </div>
        </div>

<section class="columns">
    <div class="container">
        <section class="eight columns">
            <div class="container cells originalCipher">
                <table class="default-table myTable">
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher0'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher1'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher2'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher3'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher4'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher5'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher6'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher7'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher8'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher9'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher10'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher11'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher12'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher13'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher14'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher15'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher16'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher17'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher18'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher19'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher20'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher21'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher22'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher23'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher24'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher25'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher26'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher27'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher28'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher29'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher30'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher31'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher32'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher33'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher34'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher35'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher36'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher37'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher38'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher39'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher40'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher41'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher42'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher43'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher44'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher45'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher46'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher47'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher48'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher49'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher50'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher51'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher52'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher53'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher54'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher55'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher56'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher57'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher58'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher59'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher60'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher61'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher62'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher63'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher64'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher65'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher66'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher67'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher68'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher69'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher70'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher71'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher72'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher73'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher74'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher75'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher76'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher77'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher78'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher79'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher80'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher81'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher82'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher83'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher84'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher85'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher86'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher87'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher88'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher89'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher90'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher91'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher92'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher93'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher94'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher95'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher96'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher97'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher98'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher99'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher100'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher101'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher102'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher103'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher104'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher105'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher106'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher107'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher108'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher109'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher110'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher111'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher112'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher113'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher114'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher115'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher116'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher117'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher118'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher119'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher120'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher121'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher122'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher123'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher124'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher125'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher126'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher127'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher128'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher129'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher130'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher131'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher132'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher133'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher134'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher135'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher136'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher137'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher138'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher139'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher140'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher141'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher142'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher143'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher144'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher145'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher146'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher147'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher148'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher149'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher150'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher151'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher152'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher153'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher154'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher155'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher156'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher157'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher158'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher159'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher160'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher161'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher162'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher163'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher164'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher165'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher166'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher167'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher168'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher169'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher170'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher171'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher172'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher173'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher174'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher175'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher176'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher177'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher178'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher179'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher180'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher181'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher182'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher183'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher184'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher185'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher186'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher187'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher188'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher189'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher190'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher191'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher192'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher193'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher194'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher195'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher196'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher197'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher198'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher199'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher200'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher201'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher202'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher203'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher204'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher205'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher206'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher207'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher208'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher209'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher210'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher211'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher212'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher213'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher214'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher215'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher216'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher217'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher218'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher219'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher220'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher221'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher222'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher223'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher224'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher225'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher226'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher227'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher228'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher229'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher230'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher231'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher232'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher233'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher234'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher235'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher236'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher237'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher238'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher239'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher240'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher241'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher242'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher243'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher244'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher245'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher246'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher247'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher248'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher249'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher250'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher251'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher252'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher253'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher254'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher255'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher256'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher257'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher258'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher259'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher260'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher261'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher262'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher263'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher264'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher265'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher266'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher267'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher268'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher269'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher270'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher271'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher272'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher273'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher274'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher275'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher276'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher277'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher278'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher279'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher280'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher281'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher282'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher283'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher284'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher285'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher286'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher287'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher288'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher289'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher290'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher291'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher292'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher293'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher294'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher295'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher296'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher297'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher298'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher299'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher300'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher301'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher302'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher303'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher304'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher305'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher306'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher307'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher308'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher309'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher310'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher311'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher312'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher313'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher314'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher315'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher316'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher317'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher318'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher319'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher320'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher321'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher322'><span class="text">0</span></div>
                    </div>
                    <div class="sixteen columns">
                        <div class='one column cell' id='cipher323'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher324'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher325'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher326'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher327'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher328'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher329'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher330'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher331'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher332'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher333'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher334'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher335'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher336'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher337'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher338'><span class="text">0</span></div>
                        <div class='one column cell' id='cipher339'><span class="text">0</span></div>
                    </div>
                </table>
            </div>
        </section>

        <section class="eight columns">
            <div class="container">
                <table class="default-table myTable" id="user_cipher">
                </table>
            </div>
        </section>

      </div>
    </section>



        <!-- Footer / Start
        ================================================== -->
        <?php include("include/footer.php"); ?>
        <!-- ==================================================
        Footer / End -->
        
</html>