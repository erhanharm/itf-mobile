<?php

$response = array();
 
if (isset($_POST['userId']) && isset($_POST['answerId'])) {
 
    $userId = $_POST['userId'];
    $answerId = $_POST['answerId'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();

    $result = mysql_query("INSERT INTO _dislike(userId, answerId) VALUES('$userId', '$answerId')");
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "You disliked this answer " . $answerId;

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