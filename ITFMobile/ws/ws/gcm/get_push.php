<?php
   $json = array();
   
   $pushID  = $_GET['pushID']; 
   if(isset($pushID)){
	$dbhandle = sqlite_open('data.db', 0666, $error);
		if (!$dbhandle) die ($error);
		$query = "SELECT *  from pushs WHERE pushID = $pushID";
		$result = sqlite_query($dbhandle, $query);
		if(!$result){
			$resultCode = 0;
			$json['data'] = array('resultCode' => $resultCode);
			$json = json_encode($json);
		}else
		{
		
			if ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
				$resultCode = 1;
				$json['data'] = array('resultCode' => $resultCode,'pushID' => $row['pushID'],'message' => $row['message'],'created_date' => $row['created_date']);
				$json = json_encode($json); 
			}else{
			
				$resultCode = 0;
				$json['data'] = array('resultCode' => $resultCode);
				$json = json_encode($json);
			}	
		}	
		
		/*
		
		if (!$result){
			echo "{\n\t\"data\": {";
			echo "\n\t\t\"resultCode\": 0" . ",";
			echo "\n\t}";
			echo "\n}";
		}

		if ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
			echo "{\n\t\"data\": {";
			echo "\n\t\t\"resultCode\": 1" . ",";
			echo "\n\t\t\"pushID\": " .  $row['pushID'] . ",";
			echo "\n\t\t\"message\": " .  $row['message'] . ",";
			echo "\n\t\t\"created\": " .  $row['created_date'] . ",";
			echo "\n\t}";
			echo "\n}";   
		}else{
			echo "{\n\t\"data\": {";
			echo "\n\t\t\"resultCode\": 0" . ",";
			echo "\n\t}";
			echo "\n}";
		}	
		*/

		sqlite_close($dbhandle);
   
   }else{
		$resultCode = 0;
		$json['data'] = array('resultCode' => $resultCode);
		$json = json_encode($json);
   }
   
   	echo $json;


?>