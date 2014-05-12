<?php
   $json = array();
   
   if(TRUE){
	$dbhandle = sqlite_open('data.db', 0666, $error);
		if (!$dbhandle) die ($error);
		$query = "SELECT *  from pushs ORDER BY pushID DESC";
		$result = sqlite_query($dbhandle, $query);
		if(!$result){
			$resultCode = -1;
			$json['data'] = array('resultCode' => $resultCode);
			$json = json_encode($json);
		}else
		{
		
			if ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
				$resultCode = 1;
				$json['data'] = array('resultCode' => $resultCode,'pushID' => $row['pushID'],'message' => $row['message'],'created_date' => $row['created_date']);
				$json = json_encode($json); 
			}else{
			
				$resultCode = -2;
				$json['data'] = array('resultCode' => $resultCode);
				$json = json_encode($json);
			}	
		}	
		

		sqlite_close($dbhandle);
   
   }else{
		$resultCode = -3;
		$json['data'] = array('resultCode' => $resultCode);
		$json = json_encode($json);
   }
   
   	echo $json;


?>