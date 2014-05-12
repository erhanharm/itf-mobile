<?php
$response = array();
 
if (isset($_POST['content']) && isset($_POST['thread_id']) && isset($_POST['user_id'])) {
 
    $user_id = $_POST['user_id'];
    $content = $_POST['content'];
    $thread_id = $_POST['thread_id'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
	
    date_default_timezone_set('Australia/Melbourne');
    $time = date('m/d/y h:i:s', time());
	
    $result = mysql_query("INSERT INTO reply(content, time, user_id, thread_id) VALUES('$content', NOW(), '$user_id', '$thread_id')");
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "Create new reply completed.";
     
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