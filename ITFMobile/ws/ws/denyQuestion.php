<?php

$response = array();

if (isset($_POST['userId']) && isset($_POST['questionId'])) {
    
    $userId = $_POST['userId'];
    $questionId = $_POST['questionId'];
 
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("DELETE FROM question WHERE forUserId = ".$userId. " AND id =".$questionId);

    if (mysql_affected_rows() > 0) {
        $response["success"] = 1;
        $response["message"] = "You denied this question " ;

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