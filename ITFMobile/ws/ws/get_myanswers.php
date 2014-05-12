<?php

$response = array();
 
 if (isset($_POST['forUserId']) && isset($_POST['from']) && isset($_POST['to'])) {
     
    $forUserId = $_POST['forUserId'];
    $from = $_POST['from'];
    $to = $_POST['to'];

    require_once __DIR__ . '/db_connect.php';
     
    $db = new DB_CONNECT();
     
    $result = mysql_query("SELECT question.id as id, question.forUserId, content, time, categoryId, category.categoryName as categoryName, userId, user.name as userName, isAnswered FROM question, user, category WHERE question.userId = user.id AND question.categoryId = category.id AND question.forUserId = ".$forUserId." ORDER BY time DESC LIMIT ".$to) or die(mysql_error());

    if ($result && mysql_num_rows($result) > 0) {
        $response["questions"] = array();
        $count = 0;
        while ($row = mysql_fetch_array($result)) {
            $count++;
            if($count>= $from && $count <= $to){
                $question = array();
                $question["id"] = $row["id"];
                $question["forUserId"] = $row["forUserId"];
                $question["content"] = $row["content"];
                $question["time"] = $row["time"];
                $question["categoryId"] = $row["categoryId"];
                $question["categoryName"] = $row["categoryName"];
                $question["userId"] = $row["userId"];
                $question["userName"] = $row["userName"];
                $question["isAnswered"] = $row["isAnswered"];

                $result_tmp = mysql_query("SELECT name FROM user WHERE id = ".$question["forUserId"]) or die(mysql_error());
                if ($result_tmp && mysql_num_rows($result_tmp) > 0) {
                    $row = mysql_fetch_array($result_tmp);
                    $question["forUserName"] = $row["name"];
                }
                else{
                    $question["forUserName"] = "";
                }

                array_push($response["questions"], $question);
            }
        }
        $response["success"] = 1;
        $response["message"] = "success";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No question by id found";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>