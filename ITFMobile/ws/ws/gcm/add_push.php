<?php
require_once('GCM.php');

$message  = $_POST['message']; 

if (isset($message)  ) {
    
	// Store user details in db
	$dbhandle = sqlite_open('data.db', 0666, $error);
	if ($dbhandle){
		$date = strftime( "%d/%m/%Y", time());
		$pushID = strftime( "%s", time());
		echo $pushID . " - " . $message . " - " . $date;
		
		$vowels = array("'");
		$msg = str_replace($vowels, "''", $message);
		
		$ok = sqlite_exec($dbhandle, "INSERT INTO pushs VALUES('$pushID','$msg','$date')");
		
		if($ok){
			echo "OK. Your message was sent!";
		
			///////////////////////////////////////ANDROID///////////////////////////////////////////
			$msg = array('data'=>$message);
	
			$rs = sqlite_query($dbhandle,"select regID FROM devices WHERE type=0");  //0 is android, 1 is ios		
			while($row=sqlite_fetch_object($rs))
			{
				$keys[]=$row->regID;
			}
			foreach($keys as $data)
			{
				send_push_notification(array($data), $msg);
			}
			
			
			
			
			///////////////////////////////////////IOS///////////////////////////////////////////
			// Send Push Notification		
			$query = "SELECT regID, type from devices WHERE type=1";
			//echo $query;
			$result = sqlite_query($dbhandle, $query);
			if (!$result) die("Cannot execute query.");
			$deviceArray = array();
			while ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
				$deviceArray[] = $row['regID'];
				//echo $row['regID'] . "\n";
			}			

			$short_msg = $message;
			
			if(strlen($message)>100){
				$short_msg = substr($message,0,100);
				$short_msg .= "...";
				}
			echo "<br>" . $short_msg . "<br>";	
			$apns_msg = array('pushID' => $pushID, 'message' => $short_msg);
			
			//--------------------------------------------------
			/*
			$apns = new VAPNS();
			$apns_result = $apns->sendPushNotification($message,0,$deviceArray,TRUE);	

			printf("<br> apns_result = ");
			printf($apns_result);
			
			*/
			//----------------------------------------
			
			$passphrase = 'vicnj12345-';
			////////////////////////////////////////////////////////////////////////////////

			$ctx = stream_context_create();
			stream_context_set_option($ctx, 'ssl', 'local_cert', 'pushchat.pem');
			stream_context_set_option($ctx, 'ssl', 'passphrase', $passphrase);

			// Open a connection to the APNS server
			$fp = stream_socket_client(
				'ssl://gateway.sandbox.push.apple.com:2195', $err,
				$errstr, 60, STREAM_CLIENT_CONNECT|STREAM_CLIENT_PERSISTENT, $ctx);

			if (!$fp)
				exit("Failed to connect: $err $errstr" . PHP_EOL);

			//echo '<br>Connected to APNS' . PHP_EOL;

			// Create the payload body
			$body['aps'] = array(
				'alert' => $short_msg,
				'sound' => 'default',
				'content' => $pushID
				);

			// Encode the payload as JSON
			$payload = json_encode($body);
			
			echo "<br>" . $payload;
			
			foreach($deviceArray as $device_token){
				
				$msg = chr(0) . pack('n', 32) . pack('H*', $device_token) . pack('n', strlen($payload)) . $payload;
				$result = fwrite($fp, $msg, strlen($msg));
	
				if (!$result)
					echo '<br>Message not to ' . $device_token . 'delivered' . PHP_EOL;
				else
					echo '<br>Message to ' . $device_token . ' successfully delivered' . PHP_EOL;
		
			}
			
			// Close the connection to the server
			fclose($fp);
			
			//header('Location: ../index.php');
		}
	}
	sqlite_close($dbhandle);
	
} else {
	header('Location: ../index.php');
}


?>