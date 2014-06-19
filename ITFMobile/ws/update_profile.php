<?php

$response = array();

if (isset($_POST['user_id']) && isset($_POST['password']) && isset($_POST['name']) && isset($_POST['class']) && isset($_POST['address']) && isset($_POST['interest']) && isset($_POST['signature'])) {
 
    $user_id = $_POST['user_id'];
    $password = $_POST['password'];
    $name = $_POST['name'];
    $class = $_POST['class'];
	$address = $_POST['address'];
	$interest = $_POST['interest'];
	$signature = $_POST['signature'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("SELECT * FROM user WHERE id = ".$user_id."");

    if($result && mysql_num_rows($result)>0){    

        if($password == ""){
            $result = mysql_query("UPDATE user SET  class = '$class', name = '$name', address='$address', interest='$interest', signature='$signature'  WHERE id = $user_id");            
        }else{
            $result = mysql_query("UPDATE user SET  class = '$class', password = '$password', name = '$name', address='$address', interest='$interest', signature='$signature'  WHERE id = $user_id");            
        }

        if ($result) {
            $response["success"] = 1;
            $response["message"] = "User successfully updated.";

            echo json_encode($response);
        } else {
            $response["success"] = 0;
            $response["message"] = "User failed updated.";

            echo json_encode($response);
        }
    }
    else{
        $response["success"] = 0;
        $response["message"] = "User not found.";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>