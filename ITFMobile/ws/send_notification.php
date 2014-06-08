<?php
require_once('gcm_send_push.php');

function send_notification($content, $userId, $forUserId) {
   
		$vowels = array("'");
		$msg = str_replace($vowels, "''", $content);
		
		require_once __DIR__ . '/db_connect.php';
 
		$db = new DB_CONNECT();
		$time = date('m/d/y h:i:s a', time());
		$result = mysql_query("INSERT INTO notification(userId, forUserId, content, time) VALUES('$userId','$forUserId', '$msg', NOW())");
		
		if($result){
			$content=str_replace("\\", "", $content);
		
			$deviceIds = mysql_query("SELECT * FROM user WHERE id=".$forUserId) or die(mysql_error());
			if ($deviceIds && mysql_num_rows($deviceIds) > 0) {
				while ($row = mysql_fetch_array($deviceIds)) {
					$keys[] = $row["deviceId"];
					//echo $row["deviceId"];
				}
			}
	
			///////////////////////////////////////ANDROID///////////////////////////////////////////
			$msg = array('data'=>$content);
			foreach($keys as $data)
			{
				send_push_notification(array($data), $msg);
			}
			
			echo json_encode($response);
		} 
}
?>