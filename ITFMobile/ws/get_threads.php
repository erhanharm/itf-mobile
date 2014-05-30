<?php

$response = array();
 
 if (isset($_POST['folder_id']) && isset($_POST['user_id'])) {
     
    $folder_id = $_POST['folder_id'];
	$user_id = $_POST['user_id'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

	mysql_query('SET CHARACTER SET utf8');
    $result = mysql_query("SELECT thread.id as id, title, content,tags, pictures, time, status, thread.folder_id as folder_id, thread.user_id as user_id,num_reply,num_view, user.username as username, folder.name as folder_name FROM thread, user, folder WHERE folder_id = ".$folder_id." AND user_id = user.id and folder_id = folder.id ORDER BY time DESC") or die(mysql_error());

    if ($result && mysql_num_rows($result) > 0) {
        $response["threads"] = array();
		//$count=0;
        while ($row = mysql_fetch_array($result)) {
            //$count++;
            //if($count>= $from && $count <= $to){
                $thread = array();
                $thread["id"] = $row["id"];
                $thread["title"] = $row["title"];
                $thread["content"] = $row["content"];
                $thread["time"] = $row["time"];
                $thread["folder_id"] = $row["folder_id"];
				$thread["folder_name"] = $row["folder_name"];
                $thread["user_id"] = $row["user_id"];
                $thread["username"] = $row["username"];
                $thread["status"] = $row["status"];
				$thread["num_reply"] = $row["num_reply"];
				$thread["num_view"] = $row["num_view"];
				$thread["tags"] = $row["tags"];
				$thread["pictures"] = $row["pictures"];
				
				$result_temp = mysql_query("SELECT count(id) as count_liked FROM _like WHERE thread_id = ".$thread["id"]." group by thread_id") or die(mysql_error());
				if ($result_temp && mysql_num_rows($result_temp) > 0) {
					while ($row = mysql_fetch_array($result_temp)) {
					$thread["count_liked"] = $row["count_liked"];
					}
				}else{
					$thread["count_liked"] = 0;
				}

				$result_temp = mysql_query("SELECT count(id) as count_disliked FROM _dislike WHERE thread_id = ".$thread["id"]." group by thread_id") or die(mysql_error());
				if ($result_temp && mysql_num_rows($result_temp) > 0) {
					while ($row = mysql_fetch_array($result_temp)) {
					$thread["count_disliked"] = $row["count_disliked"];
					}
				}else{
					$thread["count_disliked"] = 0;
				}

				$result_temp = mysql_query("SELECT id FROM _like WHERE thread_id = ".$thread["id"]." AND user_id = ".$user_id) or die(mysql_error());
				if ($result_temp && mysql_num_rows($result_temp) > 0) {
					while ($row = mysql_fetch_array($result_temp)) {
						$thread["is_liked"] = 1;
					}
				}else{
					$thread["is_liked"] = 0;
				}

				$result_temp = mysql_query("SELECT id FROM _dislike WHERE thread_id = ".$thread["id"]." AND user_id = ".$user_id) or die(mysql_error());
				if ($result_temp && mysql_num_rows($result_temp) > 0) {
					while ($row = mysql_fetch_array($result_temp)) {
						$thread["is_disliked"] = 1;
					}
				}else{
						$thread["is_disliked"] = 0;
				}

                array_push($response["threads"], $thread);
            //}
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No thread found";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>