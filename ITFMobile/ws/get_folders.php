<?php

$response = array();
 
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

	mysql_query('SET CHARACTER SET utf8');
    $result = mysql_query("SELECT * FROM folder ORDER BY folder_index ASC") or die(mysql_error());

    if ($result && mysql_num_rows($result) > 0) {
        $response["folders"] = array();
		//$count=0;
        while ($row = mysql_fetch_array($result)) {
            //$count++;
            //if($count>= $from && $count <= $to){
                $thread = array();
                $thread["id"] = $row["id"];
                $thread["name"] = $row["name"];
                $thread["note"] = $row["note"];
                $thread["parrent_id"] = $row["parrent_id"];
                $thread["folder_index"] = $row["folder_index"];
				$thread["num_thread"] = $row["num_thread"];

                array_push($response["folders"], $thread);
            //}
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No folder found";

        echo json_encode($response);
    }
?>