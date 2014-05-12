<?php
require_once('GCM.php');

// return json response 
$json = array();

$regID  = $_GET['regID']; // GCM Registration ID got from device
$type = $_GET['type'];

/**
 * Registering a user device in database
 * Store reg id in users table
 */
if (isset($regID) && isset($type) ) {
    
	// Store user details in db
	$dbhandle = sqlite_open('data.db', 0666, $error);
	if ($dbhandle){
		$ok1 = sqlite_exec($dbhandle, "delete from devices where regID='$regID' and type=$type");
	}
	sqlite_close($dbhandle);
} else {
    // user details not found
}
?>