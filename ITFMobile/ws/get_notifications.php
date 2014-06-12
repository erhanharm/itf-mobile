<?php
$response = array();
 
 if (isset($_POST['user_id']) && isset($_POST['from']) && isset($_POST['to'])) {
     
    $user_id = $_POST['user_id'];
    $from = $_POST['from'];
    $to = $_POST['to'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("SELECT * FROM notification WHERE for_user_id = ".$user_id." ORDER BY time DESC LIMIT ".$to) or die(mysql_error());

    if ($result && mysql_num_rows($result) > 0) {

        $response["notifications"] = array();
        $count = 0;
        while ($row = mysql_fetch_array($result)) {
            $count++;
            if($count>= $from && $count <= $to){
                $question = array();
                $question["id"] = $row["id"];
                $question["user_id"] = $row["user_id"];
                $question["content"] = $row["content"];
                $question["time"] = $row["time"];
				$question["for_user_id"] = $row["for_user_id"];

                array_push($response["notifications"], $question);
            }
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No notifications found";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>