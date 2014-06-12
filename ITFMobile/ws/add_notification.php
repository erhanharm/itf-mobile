<?php
require_once('send_notification.php');

if (isset($_POST['content']) && isset($_POST['user_id']) && isset($_POST['for_user_id'])) {

	$content = $_POST['content']; 
	$user_id = $_POST['user_id']; 
	$for_user_id = $_POST['for_user_id']; 
   
	send_notification($content, $user_id, $for_user_id);
	
	$response["success"] = 1;
    $response["message"] = "Notification sent!";
	echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>