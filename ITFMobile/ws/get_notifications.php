<?php
$response = array();
 
 if (isset($_POST['userId']) && isset($_POST['from']) && isset($_POST['to'])) {
     
    $userId = $_POST['userId'];
    $from = $_POST['from'];
    $to = $_POST['to'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("SELECT * FROM notification WHERE forUserId = ".$userId." ORDER BY time DESC LIMIT ".$to) or die(mysql_error());

    if ($result && mysql_num_rows($result) > 0) {

        $response["notifications"] = array();
        $count = 0;
        while ($row = mysql_fetch_array($result)) {
            $count++;
            if($count>= $from && $count <= $to){
                $question = array();
                $question["id"] = $row["id"];
                $question["userId"] = $row["userId"];
                $question["content"] = $row["content"];
                $question["time"] = $row["time"];
				$question["forUserId"] = $row["forUserId"];

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