<?php
$response = array();
 
if (isset($_POST['content']) && isset($_POST['reply_id'])) {
 
    $reply_id = $_POST['reply_id'];
    $content = $_POST['content'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
	
    $result = mysql_query("update reply set content='$content' where id=".$reply_id);
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "Edit reply successful.";
     
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