<?php 
	session_start();
	$history = json_decode($_POST['userpath']);
	foreach ($history as $path){
		if(strlen($finalhistory)!=0){
			$finalhistory .= "\r\n";
			$finalhistory .= implode("\r\n",$path);
		}else{
			$finalhistory .= implode("\r\n",$path);
		}
		foreach ($path as $i){
			echo $i;
		}
	}

	$finalhistory.= "\r\n";



	//$finalhistory = implode(", ",$history);

	$_SESSION['fileserver'] = session_id().".txt";
	$File = fopen($_SESSION['fileserver'],"w");
	file_put_contents($_SESSION['fileserver'],$finalhistory);
	fclose($File);

	$_SESSION['filename'] = "Cipher.txt";
?>
