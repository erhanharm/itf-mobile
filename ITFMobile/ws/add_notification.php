<?php
require_once('gcm_send_push.php');

if (isset($_POST['content']) && isset($_POST['userId']) && isset($_POST['forUserId'])) {

	$content = $_POST['content']; 
	$userId = $_POST['userId']; 
	$forUserId = $_POST['forUserId']; 
   
	send_notification($content, $userId, $forUserId);
	
	$response["success"] = 1;
    $response["message"] = "Notification sent!";

} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>