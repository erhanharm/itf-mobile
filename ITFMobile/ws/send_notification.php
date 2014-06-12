<?php
require_once('gcm_send_push.php');

function send_notification($content, $user_id, $for_user_id) {
   
		$vowels = array("'");
		$msg = str_replace($vowels, "''", $content);
		
		require_once __DIR__ . '/db_connect.php';
 
		$db = new DB_CONNECT();
		$time = date('m/d/y h:i:s a', time());
		$result = mysql_query("INSERT INTO notification(user_id, for_user_id, content, time) VALUES('$user_id','$for_user_id', '$msg', NOW())");
		
		if($result){
			$content=str_replace("\\", "", $content);
		
			$deviceIds = mysql_query("SELECT * FROM user WHERE id=".$for_user_id) or die(mysql_error());
			if ($deviceIds && mysql_num_rows($deviceIds) > 0) {
				while ($row = mysql_fetch_array($deviceIds)) {
					$keys[] = $row["device_id"];
					//echo $row["device_id"];
				}
			}
	
			///////////////////////////////////////ANDROID///////////////////////////////////////////
			$msg2 = array('data'=>$content);
			foreach($keys as $data)
			{
				send_push_notification(array($data), $msg2);
			}
		} 
}
?>