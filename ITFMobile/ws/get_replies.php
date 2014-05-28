<?php

$response = array();
 
 if (isset($_POST['thread_id']) && isset($_POST['user_id'])) {
     
    $thread_id = $_POST['thread_id'];
	$user_id = $_POST['user_id'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

	mysql_query('SET CHARACTER SET utf8');
    $result = mysql_query("SELECT reply.id as id, content, quote, quoteUserId, time, thread_id, reply.user_id as user_id, user.username as username FROM reply, user WHERE thread_id = ".$thread_id." AND user_id = user.id ORDER BY time DESC") or die(mysql_error());

    if ($result && mysql_num_rows($result) > 0) {
        $response["replies"] = array();
		//$count=0;
        while ($row = mysql_fetch_array($result)) {
            //$count++;
            //if($count>= $from && $count <= $to){
                $reply = array();
                $reply["id"] = $row["id"];
                $reply["content"] = $row["content"];
				$reply["quote"] = $row["quote"];
                $reply["time"] = $row["time"];
                $reply["user_id"] = $row["user_id"];
                $reply["username"] = $row["username"];
                $reply["thread_id"] = $row["thread_id"];
				
				$reply["quoteUserId"] = $row["quoteUserId"];
				$result_temp = mysql_query("SELECT username FROM user WHERE id = ".$row["quoteUserId"]) or die(mysql_error());
				$reply["quoteUsername"] = current($result_temp)["username"];
					
				$result_temp = mysql_query("SELECT count(id) as count_liked FROM _like WHERE reply_id = ".$reply["id"]." group by reply_id") or die(mysql_error());
				if ($result_temp && mysql_num_rows($result_temp) > 0) {
					while ($row = mysql_fetch_array($result_temp)) {
					$reply["count_liked"] = $row["count_liked"];
					}
				}else{
					$reply["count_liked"] = 0;
				}

				$result_temp = mysql_query("SELECT count(id) as count_disliked FROM _dislike WHERE reply_id = ".$reply["id"]." group by reply_id") or die(mysql_error());
				if ($result_temp && mysql_num_rows($result_temp) > 0) {
					while ($row = mysql_fetch_array($result_temp)) {
					$reply["count_disliked"] = $row["count_disliked"];
					}
				}else{
					$reply["count_disliked"] = 0;
				}

				$result_temp = mysql_query("SELECT id FROM _like WHERE reply_id = ".$reply["id"]." AND user_id = ".$user_id) or die(mysql_error());
				if ($result_temp && mysql_num_rows($result_temp) > 0) {
					while ($row = mysql_fetch_array($result_temp)) {
						$reply["is_liked"] = 1;
					}
				}else{
					$reply["is_liked"] = 0;
				}

				$result_temp = mysql_query("SELECT id FROM _dislike WHERE reply_id = ".$reply["id"]." AND user_id = ".$user_id) or die(mysql_error());
				if ($result_temp && mysql_num_rows($result_temp) > 0) {
					while ($row = mysql_fetch_array($result_temp)) {
						$reply["is_disliked"] = 1;
					}
				}else{
						$reply["is_disliked"] = 0;
				}
				
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