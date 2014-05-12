<?php

$response = array();

if (isset($_POST['userId']) && isset($_POST['email']) && isset($_POST['password']) && isset($_POST['name']) && isset($_POST['isAnonymous'])) {
 
    $userId = $_POST['userId'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $name = $_POST['name'];
    $isAnonymous = $_POST['isAnonymous'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("SELECT * FROM user WHERE id = ".$userId."");

    if($result && mysql_num_rows($result)>0){    

        if($password == ""){
            $result = mysql_query("UPDATE user SET  email = '$email', name = '$name', isAnonymous=$isAnonymous  WHERE id = $userId");            
        }else{
            $passwordMD5 = md5($password);

            $result = mysql_query("UPDATE user SET  email = '$email', password = '$passwordMD5', name = '$name', isAnonymous=$isAnonymous  WHERE id = $userId");            
        }

        if ($result) {
            $response["success"] = 1;
            $response["message"] = "User successfully updated.";

            $result = mysql_query("SELECT * FROM user WHERE id = ".$userId."");

            if($result && mysql_num_rows($result)>0){

                $result = mysql_fetch_array($result);
                $user = array();
                $user["id"] = $result["id"];
                $user["name"] = $result["name"];
                $user["email"] = $result["email"];
                $user["password"] = $result["password"];
                $user["deviceId"] = $result["deviceId"];
                $user["isAnonymous"] = $result["isAnonymous"];

                $response["user"] = array();
                 
                array_push($response["user"], $user);
            }

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