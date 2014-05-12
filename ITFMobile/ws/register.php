<?php

$response = array();

if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['email']) && isset($_POST['device_id'])) {
	$username = $_POST['username'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $device_id = $_POST['device_id'];

    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();

    $result = mysql_query("SELECT * FROM user WHERE username='".$username."'");
    if($result && mysql_num_rows($result)==0){
        $result = mysql_query("SELECT * FROM user WHERE email = '".$email."'");
        if($result && mysql_num_rows($result)==0){
		
			date_default_timezone_set('Australia/Melbourne');
			$time = date('m/d/y h:i:s', time());
			
            $result = mysql_query("INSERT INTO user(username, email, password, device_id, join_date) VALUES('$username', '$email', '$password', '$device_id',NOW())");
               if ($result) {
                    $result = mysql_query("SELECT * FROM user WHERE username = '".$username."'") or die(mysql_error());
                 
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
							$user["device_id"] = $result["device_id"];
                        
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
        $response["message"] = "This username existed.";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
    
    echo json_encode($response);
}
?>