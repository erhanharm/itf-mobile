<?php

$response = array();

if (isset($_POST['user_id']) && isset($_POST['thread_id'])) {
    
    $user_id = $_POST['user_id'];
    $thread_id = $_POST['thread_id'];
 
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("DELETE FROM _dislike WHERE user_id = ".$user_id. " AND thread_id = " .$thread_id);

    if (mysql_affected_rows() > 0) {
        $response["success"] = 1;
        $response["message"] = "You undisliked this thread";

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