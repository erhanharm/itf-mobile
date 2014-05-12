<?php
$response = array();
 
if (isset($_POST['thread_id'])) {
 
    $thread_id = $_POST['thread_id'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
	
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