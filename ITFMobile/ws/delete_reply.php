<?php
$response = array();
 
if (isset($_POST['reply_id'])) {
 
    $reply_id = $_POST['reply_id'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
	
    $result = mysql_query("delete from reply where id=".$reply_id);
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "Delete reply successful.";
     
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