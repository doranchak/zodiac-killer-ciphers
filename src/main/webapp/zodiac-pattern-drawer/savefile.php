<?php
	session_start();
	$Fileserver = $_SESSION['fileserver'];
	$Filenameuser = $_SESSION['filename'];
	header("Content-Type: text/plain");
	header("Content-Disposition: attachment; filename={$Filenameuser}");
	header("Pragma: no-cache");
	readfile("{$Fileserver}");
?>
