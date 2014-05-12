<?php
$response = array();

require_once __DIR__ . '/db_connect.php';
 
$db = new DB_CONNECT();

if (isset($_GET["email"]) && isset($_GET["password"]) && isset($_GET['deviceId'])) {
    $email = $_GET['email'];
    $password = $_GET['password'];
    $deviceId = $_GET['deviceId'];
    $passwordMD5 = md5($password);

    $result = mysql_query("SELECT * FROM user WHERE email = '".$email."' AND password = '".$passwordMD5."'");
 
    if ($result && !empty($result)) {
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);

            $user = array();
            $user["id"] = $result["id"];
            $user["name"] = $result["name"];
            $user["email"] = $result["email"];
            $user["password"] = $password;

            $result_tmp = mysql_query("UPDATE user SET  deviceId = '$deviceId' WHERE id = ".$user["id"]);
            if($result_tmp){
                $user["deviceId"] = $deviceId;
            }else{
                 $user["deviceId"] = $result["deviceId"];
            }

            $user["isAnonymous"] = $result["isAnonymous"];

            $response["success"] = 1;
            $response["message"] = "success";
 
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
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>