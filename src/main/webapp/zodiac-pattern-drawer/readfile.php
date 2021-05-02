<?php
	
	/* Get name of file */
	$filename = $_GET['name'];
	$fp = fopen($filename, "r");
	$numarray = array();
	$i = 0;

	/* Put number in array */
	while($num = fscanf($fp,"%d\n")){
		$numarray[$i] = implode("",$num);
		$i++;
	}

	fclose($fp);
	/* Return array as comma separated string to javascript */
	echo implode(", ",$numarray);
 ?>