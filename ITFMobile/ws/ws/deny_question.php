<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['forUserId']) && isset($_POST['questionId'])) {
 
    $forUserId = $_POST['forUserId'];
    $questionId = $_POST['questionId'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
	
    $result = mysql_query("DELETE FROM question WHERE questionId=$questionId AND forUserId=$forUserId");
        $db->close();
       if ($result) {
            // successfully delete from database
            $response["success"] = 1;
            $response["message"] = "You denied this question.";
     
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed to insert row
            $response["success"] = 0;
            $response["message"] = "Oops! An error occurred.";
     
            // echoing JSON response
            echo json_encode($response);
        }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>