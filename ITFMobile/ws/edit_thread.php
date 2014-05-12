<?php
$response = array();
 
if (isset($_POST['title']) && isset($_POST['content']) && isset($_POST['status']) && isset($_POST['thread_id'])) {
 
    $thread_id = $_POST['thread_id'];
    $content = $_POST['content'];
    $title = $_POST['title'];
	$status = $_POST['status'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
	
    $result = mysql_query("update thread set title='$title', content='$content',status='$status' where id=".$thread_id);
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "Edit thread successful.";
     
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