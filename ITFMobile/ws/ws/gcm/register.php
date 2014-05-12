<?php

require_once "define.php";
// return json response 
$json = array();
$errorCode = 0;

$regID  = $_GET['regID']; // GCM Registration ID got from device
$type = $_GET['type'];

//echo "\n Server :" . $regID;
/**
 * Registering a user device in database
 * Store reg id in users table
 */
if (isset($regID) && isset($type) ) {
    
	// Store user details in db
	$dbhandle = sqlite_open('data.db', 0666, $error);
	if($dbhandle){
		
		$query = "SELECT *  from devices WHERE regID = $regID AND type = $type";
		$result = sqlite_query($dbhandle, $query);
		$row = sqlite_fetch_array($result, SQLITE_ASSOC);
		if($row){
		
		// Device already exits.
			$errorCode = DeviceDidRegistered;
					
		}else{
			$ok1 = sqlite_exec($dbhandle, "INSERT INTO devices VALUES('$regID','$type')");
			if($ok1){
				$errorCode = RegisterOK;
			}else{
				$errorCode = RegisterError;
			}
		}
	}else{
		$errorCode = RegisterError;
	}
	
	
	sqlite_close($dbhandle);
	
} else {
    // user details not found
	$errorCode = RegisterError;
}

echo $errorCode;

?>