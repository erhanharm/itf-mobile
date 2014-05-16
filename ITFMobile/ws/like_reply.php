<?php
$response = array();

if (isset($_POST['user_id']) && isset($_POST['reply_id'])) {
    $user_id = $_POST['user_id'];
    $reply_id = $_POST['reply_id'];
 
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();
    $result = mysql_query("INSERT INTO _like(user_id, reply_id, thread_id) VALUES('$user_id', '$reply_id', -1)");
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "You liked this reply ";

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