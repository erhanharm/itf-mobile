<?php

$response = array();
 
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

	mysql_query('SET CHARACTER SET utf8');
    $result = mysql_query("SELECT * FROM user") or die(mysql_error());

    if ($result && mysql_num_rows($result) > 0) {
        $response["users"] = array();
		//$count=0;
        while ($row = mysql_fetch_array($result)) {
            //$count++;
            //if($count>= $from && $count <= $to){
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

                array_push($response["users"], $user);
            //}
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No user found";

        echo json_encode($response);
    }
?>