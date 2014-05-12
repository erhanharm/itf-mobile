<?php

$response = array();
 
if (isset($_POST['questionId']) && isset($_POST['userId'])) {
 
    $questionId = $_POST['questionId'];
    $userId = $_POST['userId'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
 
    $result = mysql_query("SELECT * FROM question WHERE id='".$questionId."'");

    if($result && mysql_num_rows($result)>0){    
        $result = mysql_fetch_array($result);
        $result = mysql_query("UPDATE question SET  isAnswered= 1 WHERE id = $questionId");

        if ($result) {
            $response["success"] = 1;
            $response["message"] = "This question is answered.";

            echo json_encode($response);
        } else {
            $response["success"] = 0;
            $response["message"] = "Oops! An error occurred.";
        }
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>