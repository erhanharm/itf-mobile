<?php
$response = array();
 
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();
     
    $result = mysql_query("SELECT * FROM tag ORDER BY name ASC") or die(mysql_error());
     
    if ($result && mysql_num_rows($result) > 0) {
        $response["tags"] = array();
        while ($row = mysql_fetch_array($result)) {
            $tag = array();
            $tag["id"] = $row["id"];
            $tag["name"] = $row["name"];

            $result_tmp = mysql_query("SELECT * FROM thread WHERE tags like '%".$row["name"].",%'") or die(mysql_error());
            if ($result_tmp){
				$tag["num_refer"] = mysql_num_rows($result_tmp);
			}
			else $tag["num_refer"] = 0;

            array_push($response["tags"], $tag);
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No tag found";

        echo json_encode($response);
    }

?>