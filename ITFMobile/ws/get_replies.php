<?php

$response = array();
 
 if (isset($_POST['thread_id'])) {
     
    $thread_id = $_POST['thread_id'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("SELECT reply.id as id, content, time, thread_id, reply.user_id as user_id, user.username as username FROM reply, user WHERE thread_id = ".$thread_id." AND user_id = user.id ORDER BY time DESC") or die(mysql_error());

    if ($result && mysql_num_rows($result) > 0) {
        $response["replies"] = array();
		//$count=0;
        while ($row = mysql_fetch_array($result)) {
            //$count++;
            //if($count>= $from && $count <= $to){
                $reply = array();
                $reply["id"] = $row["id"];
                $reply["content"] = $row["content"];
                $reply["time"] = $row["time"];
                $reply["user_id"] = $row["user_id"];
                $reply["username"] = $row["username"];
                $reply["thread_id"] = $row["thread_id"];

                array_push($response["replies"], $reply);
            //}
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No reply found";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>