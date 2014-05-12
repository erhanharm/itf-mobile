<?php
$response = array();

if (isset($_POST["username"]) && isset($_POST["password"]) && isset($_POST['device_id'])) {
    $username = $_POST['username'];
    $password = $_POST['password'];
    $device_id = $_POST['device_id'];

	require_once __DIR__ . '/db_connect.php';
 
	$db = new DB_CONNECT();
	
    $result = mysql_query("SELECT * FROM user WHERE username = '".$username."' AND password = '".$password."'");
 
    if ($result && !empty($result)) {
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);

            $user = array();
            $user["id"] = $result["id"];
            $user["username"] = $result["username"];
            $user["password"] = $result["password"];
			$user["name"] = $result["name"];
			$user["email"] = $result["email"];
			$user["user_type"] = $result["user_type"];
			$user["class"] = $result["class"];
			$user["birthday"] = $result["birthday"];
			$user["address"] = $result["address"];
			$user["interest"] = $result["interest"];
			$user["signature"] = $result["signature"];
			$user["join_date"] = $result["join_date"];

            $result_tmp = mysql_query("UPDATE user SET device_id = '$device_id' WHERE id = ".$user["id"]);
            if($result_tmp){
                $user["device_id"] = $device_id;
            }else{
                 $user["device_id"] = $result["device_id"];
            }

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