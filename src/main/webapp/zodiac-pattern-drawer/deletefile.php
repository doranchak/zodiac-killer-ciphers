<?php
	session_start();
	$Filename = $_SESSION['fileserver'];
	unlink($Filename);
	echo $Filename;
?>
