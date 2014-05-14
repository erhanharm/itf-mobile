<?php
$response = array();
 
if (isset($_POST['thread_id'])) {
 
    $thread_id = $_POST['thread_id'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
	
	$rs = mysql_query("SELECT thread.folder_id FROM thread WHERE id = ".$thread_id) or die(mysql_error());
    if ($rs && mysql_num_rows($rs) > 0) {
		while ($r = mysql_fetch_array($rs)) {
			mysql_query("update folder set num_thread=num_thread-1 where id=".$r["folder_id"]);
		}
	}
		
		$result = mysql_query("delete from thread where id=".$thread_id);
		if ($result) {
			$response["success"] = 1;
			$response["message"] = "Delete thread successful.";
		 
			echo json_encode($response);
		} else {
			$response["success"] = 0;
			$response["message"] = "Oops! An error occurred.";

			echo json_encode($response);
		}   
			
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>