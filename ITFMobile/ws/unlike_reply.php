<?php

$response = array();

if (isset($_POST['user_id']) && isset($_POST['reply_id'])) {
    $user_id = $_POST['user_id'];
    $reply_id = $_POST['reply_id'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();

    $result = mysql_query("DELETE FROM _like WHERE user_id = ".$user_id. " AND reply_id = ".$reply_id);

    if (mysql_affected_rows() > 0) {
        $response["success"] = 1;
        $response["message"] = "You unliked this reply ";

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