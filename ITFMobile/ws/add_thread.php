<?php
$response = array();
 
if (isset($_POST['title']) && isset($_POST['content']) && isset($_POST['folder_id']) && isset($_POST['user_id']) && isset($_POST['tags']) && isset($_POST['pictures'])) {
 
    $user_id = $_POST['user_id'];
    $content = $_POST['content'];
    $folder_id = $_POST['folder_id'];
    $title = $_POST['title'];
	$tags = $_POST['tags'];
	$pictures = $_POST['pictures'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();

    $status = 1;
	
    date_default_timezone_set('Australia/Melbourne');
    $time = date('m/d/y h:i:s', time());
	
	mysql_query("update folder set num_thread=num_thread+1 where id=".$folder_id);
    $result = mysql_query("INSERT INTO thread(title, content, time, folder_id, user_id, status, tags, pictures) VALUES('$title', '$content', NOW(), '$folder_id', '$user_id', '$status', '$tags', '$pictures')");
       if ($result) {
           // mysql_query("update folder set  INTO thread(title, content, time, folder_id, user_id, status) VALUES('$title', '$content', NOW(), '$folder_id', '$user_id', '$status')");
			
			$response["success"] = 1;
            $response["message"] = "Create new thread completed.";
     
            echo json_encode($response);
        } else {
            $response["success"] = 0;
            $response["message"] = "Oops! An error occurred.";

            echo json_encode($response);
        }
		
	//check and add new tags
	$tags=$tags . " ";
	$tags=str_replace(",  ","",$tags);
	$keys = split(", ", $tags);
	foreach ($keys as $key) {
		$key=trim($key);
		if($key!=""){
			$result = mysql_query("SELECT * FROM tag where name='".$key."'") or die(mysql_error());
			if (!($result && mysql_num_rows($result) > 0)) {
				mysql_query("INSERT INTO tag(name) VALUES('$key')");
			}
		}
	}
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>