<?php

$response = array();

if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password']) && isset($_POST['deviceId'])) {
 
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $deviceId = $_POST['deviceId'];

    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();

    $result = mysql_query("SELECT * FROM user WHERE name='".$name."'");
    if($result && mysql_num_rows($result)==0){

        $result = mysql_query("SELECT * FROM user WHERE email = '".$email."'");

        if($result && mysql_num_rows($result)==0){
            $isAnonymous = 0;
            $passwordMD5 = md5($password);
            $result = mysql_query("INSERT INTO user(name, email, password, deviceId) VALUES('$name', '$email', '$passwordMD5', '$deviceId')");
               if ($result) {
                    $result = mysql_query("SELECT * FROM user WHERE email = '".$email."'") or die(mysql_error());
                 
                    if ($result && !empty($result)) {
                        if (mysql_num_rows($result) > 0) {
                 
                            $result = mysql_fetch_array($result);
                 
                            $user = array();
                            $user["id"] = $result["id"];
                            $user["name"] = $result["name"];
                            $user["email"] = $result["email"];
                            $user["password"] = $result["password"];
                            $user["deviceId"] = $result["deviceId"];
                            $user["isAnonymous"] = $result["isAnonymous"];
                        
                            $response["success"] = 1;
                            $response["message"] = "Register completed.";

                            $response["user"] = array();
                 
                            array_push($response["user"], $user);
                 
                            echo json_encode($response);
                        } else {
                            $response["success"] = 0;
                            $response["message"] = "No one!";
                 
                            echo json_encode($response);
                        }
                    } else {
                        $response["success"] = 0;
                        $response["message"] = "error!";

                        echo json_encode($response);
                    }
                } else {
                    $response["success"] = 0;
                    $response["message"] = "Oops! An error occurred.";

                    echo json_encode($response);
                }
        }else{
            $response["success"] = 0;
            $response["message"] = "This email registered.";

            echo json_encode($response);
        }
    }else{
        $response["success"] = 0;
        $response["message"] = "This name existed.";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
    
    echo json_encode($response);
}
?>