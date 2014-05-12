<?php

$response = array();
 
if (isset($_POST['userId']) && isset($_POST['content']) && isset($_POST['questionId'])) {
 
    $userId = $_POST['userId'];
    $content = $_POST['content'];
    $questionId = $_POST['questionId'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
    $time = date('m/d/y h:i:s a', time());
    $result = mysql_query("INSERT INTO answer(questionId, content, userId, time) VALUES('$questionId', '$content', '$userId', NOW())");
       if ($result) {
            $response["success"] = 1;
            $response["message"] = "Create new question completed.";
     
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