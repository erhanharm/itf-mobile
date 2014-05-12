<?php
$response = array();
 
if (isset($_POST['userId']) && isset($_POST['content']) && isset($_POST['categoryId']) && isset($_POST['forUserId'])) {
 
    $userId = $_POST['userId'];
    $content = $_POST['content'];
    $categoryId = $_POST['categoryId'];
    $forUserId = $_POST['forUserId'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();

    $isAnswered = 0;
    date_default_timezone_set('Australia/Melbourne');
    $time = date('m/d/y h:i:s', time());
    $result = mysql_query("INSERT INTO question(forUserId, content, time, categoryId, userId, isAnswered) VALUES('$forUserId', '$content', NOW(), '$categoryId', '$userId', '$isAnswered')");
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