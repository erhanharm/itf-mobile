<?php

$response = array();
 
if (isset($_POST['user_id']) && isset($_POST['thread_id'])) {
 
    $user_id = $_POST['user_id'];
    $thread_id = $_POST['thread_id'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();

    $result = mysql_query("INSERT INTO _dislike(user_id, reply_id, thread_id) VALUES('$user_id', -1, '$thread_id')");
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "You disliked this thread ";

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