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
				$user["id"] = $row["id"];
				$user["username"] = $row["username"];
				$user["password"] = $row["password"];
				$user["name"] = $row["name"];
				$user["email"] = $row["email"];
				$user["user_type"] = $row["user_type"];
				$user["class"] = $row["class"];
				$user["birthday"] = $row["birthday"];
				$user["address"] = $row["address"];
				$user["interest"] = $row["interest"];
				$user["signature"] = $row["signature"];
				$user["join_date"] = $row["join_date"];
				$user["device_id"] = $row["device_id"];

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